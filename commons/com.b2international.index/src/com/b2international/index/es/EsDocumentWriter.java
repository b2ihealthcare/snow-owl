/*
 * Copyright 2017-2022 B2i Healthcare Pte Ltd, http://b2i.sg
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.b2international.index.es;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;

import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.elasticsearch.action.DocWriteRequest.OpType;
import org.elasticsearch.action.bulk.*;
import org.elasticsearch.action.bulk.BulkItemResponse.Failure;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.common.UUIDs;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.core.TimeValue;
import org.elasticsearch.xcontent.XContentType;

import com.b2international.commons.StringUtils;
import com.b2international.index.*;
import com.b2international.index.es.admin.EsIndexAdmin;
import com.b2international.index.es.client.EsClient;
import com.b2international.index.mapping.DocumentMapping;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.*;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

/**
 * @since 5.10 
 */
public class EsDocumentWriter implements Writer {

	private final EsIndexAdmin admin;
	private final Searcher searcher;

	private final Table<Class<?>, String, Object> indexOperations = HashBasedTable.create();
	private final Multimap<Class<?>, String> deleteOperations = HashMultimap.create();
	private final ObjectMapper mapper;
	private List<BulkUpdate<?>> bulkUpdateOperations = newArrayList();
	private List<BulkDelete<?>> bulkDeleteOperations = newArrayList();
 	
	public EsDocumentWriter(EsIndexAdmin admin, Searcher searcher, ObjectMapper mapper) {
		this.admin = admin;
		this.searcher = searcher;
		this.mapper = mapper;
	}
	
	@Override
	public void put(Object object) {
		DocumentMapping mapping = admin.mappings().getMapping(object.getClass());
		final String id = mapping.isAutoGeneratedId() ? UUIDs.randomBase64UUID() : mapping.getIdFieldValue(object);
		indexOperations.put(object.getClass(), id, object);
	}

	@Override
	public <T> void putAll(Collection<T> objects) {
		objects.forEach(this::put);
	}

	@Override
	public <T> void bulkUpdate(BulkUpdate<T> update) {
		bulkUpdateOperations.add(update);
	}
	
	@Override
	public <T> void bulkDelete(BulkDelete<T> delete) {
		bulkDeleteOperations.add(delete);
	}
	
	@Override
	public <T> T updateImmediately(Update<T> update) {
		return admin.updateImmediately(update, mapper);
	}

	@Override
	public void remove(Class<?> type, String key) {
		remove(type, ImmutableSet.of(key));
	}
	
	@Override
	public void remove(Class<?> type, Set<String> keysToRemove) {
		removeAll(Collections.singletonMap(type, keysToRemove));
	}

	@Override
	public void removeAll(Map<Class<?>, Set<String>> keysByType) {
		for (Class<?> type : keysByType.keySet()) {
			deleteOperations.putAll(type, keysByType.get(type));
		}
	}

	@Override
	public void commit() throws IOException {
		if (isEmpty()) {
			return;
		}
		
		final Set<DocumentMapping> mappingsToRefresh = Collections.synchronizedSet(newHashSet());
		final EsClient client = admin.client();
		// apply bulk updates first
		final ListeningExecutorService executor;
		admin.log().trace("Applying bulk updates ({}) and deletes ({})...", bulkUpdateOperations.size(), bulkDeleteOperations.size());
		if (bulkUpdateOperations.size() > 1 || bulkDeleteOperations.size() > 1) {
			final int threads = Math.min(4, Math.max(bulkUpdateOperations.size(), bulkDeleteOperations.size()));
			executor = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(threads));
		} else {
			executor = MoreExecutors.newDirectExecutorService();
		}
		final List<ListenableFuture<?>> updateFutures = newArrayList();
		for (BulkUpdate<?> update : bulkUpdateOperations) {
			updateFutures.add(executor.submit(() -> {
				if (admin.bulkUpdate(update)) {
					mappingsToRefresh.add(admin.mappings().getMapping(update.getType()));
				}
			}));
		}
		for (BulkDelete<?> delete: bulkDeleteOperations) {
			updateFutures.add(executor.submit(() -> {
				if (admin.bulkDelete(delete)) {
					mappingsToRefresh.add(admin.mappings().getMapping(delete.getType()));
				}
			}));
		}
		try {
			executor.shutdown();
			Futures.allAsList(updateFutures).get();
			executor.awaitTermination(10, TimeUnit.SECONDS);
		} catch (InterruptedException | ExecutionException e) {
			admin.log().error("Couldn't execute bulk updates", e);
			throw new IndexException("Couldn't execute bulk updates", e);
		}
		
		// then bulk indexes/deletes
		final List<Failure> bulkIndexFailures = new ArrayList<>();
		if (!indexOperations.isEmpty() || !deleteOperations.isEmpty()) {
			admin.log().trace("Applying writes ({}) and deletes ({})...", indexOperations.size(), deleteOperations.size());
			final BulkProcessor processor = client.bulk(new BulkProcessor.Listener() {
				@Override
				public void beforeBulk(long executionId, BulkRequest request) {
					admin.log().trace("Sending bulk request to cluster '{}', batch '{}', index '{}'", request.getDescription(), request.numberOfActions(), request.getIndices());
				}
				
				@Override
				public void afterBulk(long executionId, BulkRequest request, Throwable failure) {
					admin.log().error("Failed bulk request '{}'", request.getDescription(), failure);
				}
				
				@Override
				public void afterBulk(long executionId, BulkRequest request, BulkResponse response) {
					admin.log().trace("Successfully sent bulk request to cluster '{}' ({}) in {}, index '{}'.", request.getDescription(), request.numberOfActions(), response.getTook(), request.getIndices());
					if (response.hasFailures()) {
						for (BulkItemResponse itemResponse : response.getItems()) {
							if (itemResponse.isFailed()) {
								bulkIndexFailures.add(itemResponse.getFailure());
							}
						}
					}
				}
			})
			.setConcurrentRequests(getConcurrencyLevel())
			.setBulkActions((int) admin.settings().get(IndexClientFactory.BULK_ACTIONS_SIZE))
			.setBulkSize(new ByteSizeValue((int) admin.settings().get(IndexClientFactory.BULK_ACTIONS_SIZE_IN_MB), ByteSizeUnit.MB))
			.setBackoffPolicy(BackoffPolicy.constantBackoff(TimeValue.timeValueSeconds(15), 4))
			.build();
			
			for (Class<?> type : ImmutableSet.copyOf(indexOperations.rowKeySet())) {
				final Map<String, Object> indexOperationsForType = indexOperations.row(type);
				
				final DocumentMapping mapping = admin.mappings().getMapping(type);
				final String typeIndex = admin.getTypeIndex(mapping);
				
				mappingsToRefresh.add(mapping);
				
				for (Entry<String, Object> entry : Iterables.consumingIterable(indexOperationsForType.entrySet())) {
					final String id = entry.getKey();
					if (!deleteOperations.containsValue(id)) {
						final Object obj = entry.getValue();
						final byte[] _source = mapper.writeValueAsBytes(obj);
						IndexRequest indexRequest = new IndexRequest()
								.index(typeIndex)
								.opType(OpType.INDEX)
								.source(_source, XContentType.JSON);
						// XXX revisions has their special local ID, but that's not needed when sending them to ES, ES will autogenerate a non-conflicting ID for them 
						if (!mapping.isAutoGeneratedId()) {
							indexRequest.id(id);
						}
						processor.add(indexRequest);
					}
				}
	
				for (String id : deleteOperations.removeAll(type)) {
					processor.add(new DeleteRequest(typeIndex, id));
				}
				
				// Flush processor between index boundaries
				processor.flush();
			}
			
			// Remaining delete operations can be executed on their own
			for (Class<?> type : ImmutableSet.copyOf(deleteOperations.keySet())) {
				final DocumentMapping mapping = admin.mappings().getMapping(type);
				final String typeIndex = admin.getTypeIndex(mapping);
				
				mappingsToRefresh.add(mapping);
				
				for (String id : deleteOperations.removeAll(type)) {
					processor.add(new DeleteRequest(typeIndex, id));
				}

				// Flush processor between index boundaries
				processor.flush();
			}

			try {
				processor.awaitClose(5, TimeUnit.MINUTES);
			} catch (InterruptedException e) {
				throw new IndexException("Interrupted bulk processing part of the commit", e);
			}
		}

		// refresh the indexes after successfuly commit
		admin.refresh(mappingsToRefresh);
		
		// if there were failures, fail the process here, as we are unable to rollback at any point due to append-only nature, the client has to mitigate the error either by restoring a backup or
		if (!bulkIndexFailures.isEmpty()) {
			bulkIndexFailures.forEach(failure -> {
				admin.log().error("Failed to commit item in bulk request in index '{}' due to error: {}", failure.getIndex(), StringUtils.truncate(failure.getMessage(), 1000));
			});
			throw new IndexException("There were bulk indexing failures and certain documents did not get persisted into the index. Check the logs for further details.", null);
		}
	}

	@Override
	public boolean isEmpty() {
		return indexOperations.isEmpty() && deleteOperations.isEmpty() && bulkUpdateOperations.isEmpty() && bulkDeleteOperations.isEmpty();
	}

	private int getConcurrencyLevel() {
		return (int) admin.settings().get(IndexClientFactory.COMMIT_CONCURRENCY_LEVEL);
	}

	/*
	 * Testing only, dumps a text representation of all operations to the console
	 */
	private void dumpOps() throws IOException {
		System.err.println("Added documents:");
		for (Entry<Class<?>, Map<String, Object>> indexOperationsByType  : indexOperations.rowMap().entrySet()) {
			for (Entry<String, Object> entry : indexOperationsByType.getValue().entrySet()) {
				System.err.format("\t%s -> %s\n", entry.getKey(), mapper.writeValueAsString(entry.getValue()));
			}
		}
		System.err.println("Deleted documents: ");
		for (Class<?> type : deleteOperations.keySet()) {
			System.err.format("\t%s -> %s\n", admin.mappings().getMapping(type).typeAsString(), deleteOperations.get(type));
		}
		System.err.println("Bulk updates: ");
		for (BulkUpdate<?> update : bulkUpdateOperations) {
			System.err.format("\t%s -> %s, %s, %s\n", admin.mappings().getMapping(update.getType()).typeAsString(), update.getFilter(), update.getScript(), update.getParams());
		}
		System.err.println("Bulk deletes: ");
		for (BulkDelete<?> delete : bulkDeleteOperations) {
			System.err.format("\t%s -> %s\n", admin.mappings().getMapping(delete.getType()).typeAsString(), delete.getFilter());
		}
	}

	@Override
	public Searcher searcher() {
		return searcher;
	}
}
