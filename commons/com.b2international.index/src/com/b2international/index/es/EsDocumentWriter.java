/*
 * Copyright 2017-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.elasticsearch.action.DocWriteRequest.OpType;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.xcontent.XContentType;

import com.b2international.index.BulkDelete;
import com.b2international.index.BulkUpdate;
import com.b2international.index.IndexClientFactory;
import com.b2international.index.IndexException;
import com.b2international.index.Searcher;
import com.b2international.index.Writer;
import com.b2international.index.es.admin.EsIndexAdmin;
import com.b2international.index.mapping.DocumentMapping;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import com.google.common.collect.Table;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
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
	public void put(String key, Object object) {
		indexOperations.put(object.getClass(), key, object);
	}

	@Override
	public <T> void putAll(Map<String, T> objectsByKey) {
		objectsByKey.forEach(this::put);
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
		if (indexOperations.isEmpty() && deleteOperations.isEmpty() && bulkUpdateOperations.isEmpty() && bulkDeleteOperations.isEmpty()) {
			return;
		}
		
		final Set<DocumentMapping> mappingsToRefresh = Collections.synchronizedSet(newHashSet());
		final EsClient client = admin.client();
		// apply bulk updates first
		final ListeningExecutorService executor;
		if (bulkUpdateOperations.size() > 1 || bulkDeleteOperations.size() > 1) {
			final int threads = Math.min(4, Math.max(bulkUpdateOperations.size(), bulkDeleteOperations.size()));
			executor = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(threads));
		} else {
			executor = MoreExecutors.newDirectExecutorService();
		}
		final List<ListenableFuture<?>> updateFutures = newArrayList();
		for (BulkUpdate<?> update : bulkUpdateOperations) {
			updateFutures.add(executor.submit(() -> admin.bulkUpdate(update, mappingsToRefresh)));
		}
		for (BulkDelete<?> delete: bulkDeleteOperations) {
			updateFutures.add(executor.submit(() -> admin.bulkDelete(delete, mappingsToRefresh)));
		}
		try {
			executor.shutdown();
			Futures.allAsList(updateFutures).get();
			executor.awaitTermination(10, TimeUnit.SECONDS);
		} catch (InterruptedException | ExecutionException e) {
			throw new IndexException("Couldn't execute bulk updates", e);
		}
		
		// then bulk indexes/deletes
		if (!indexOperations.isEmpty() || !deleteOperations.isEmpty()) {
			final BulkProcessor processor = BulkProcessor.builder((req, listener) -> client.bulkAsync(req, RequestOptions.DEFAULT, listener), new BulkProcessor.Listener() {
				@Override
				public void beforeBulk(long executionId, BulkRequest request) {
					admin.log().debug("Sending bulk request {}", request.numberOfActions());
				}
				
				@Override
				public void afterBulk(long executionId, BulkRequest request, Throwable failure) {
					admin.log().error("Failed bulk request", failure);
				}
				
				@Override
				public void afterBulk(long executionId, BulkRequest request, BulkResponse response) {
					admin.log().debug("Successfully processed bulk request ({}) in {}.", request.numberOfActions(), response.getTook());
					if (response.hasFailures()) {
						for (BulkItemResponse itemResponse : response.getItems()) {
							checkState(!itemResponse.isFailed(), "Failed to commit bulk request in index '%s', %s", admin.name(), itemResponse.getFailureMessage());
						}
					}
				}
			})
			.setConcurrentRequests(getConcurrencyLevel())
			.setBulkActions(10_000)
			.setBulkSize(new ByteSizeValue(10L, ByteSizeUnit.MB))
			.build();

			for (Class<?> type : ImmutableSet.copyOf(indexOperations.rowKeySet())) {
				final Map<String, Object> indexOperationsForType = indexOperations.row(type);
				
				final DocumentMapping mapping = admin.mappings().getMapping(type);
				final String typeString = mapping.typeAsString();
				final String typeIndex = admin.getTypeIndex(mapping);
				
				mappingsToRefresh.add(mapping);
				
				for (Entry<String, Object> entry : Iterables.consumingIterable(indexOperationsForType.entrySet())) {
					final String id = entry.getKey();
					if (!deleteOperations.containsValue(id)) {
						final Object obj = entry.getValue();
						final Set<String> hashedFields = mapping.getHashedFields();
						final byte[] _source;
						
						if (!hashedFields.isEmpty()) {
							final ObjectNode objNode = mapper.valueToTree(obj);
							final ObjectNode hashedNode = mapper.createObjectNode();
						
							// Preserve property order, share references with objNode
							for (String hashedField : hashedFields) {
								JsonNode value = objNode.get(hashedField);
								if (value != null && !value.isNull()) {
									hashedNode.set(hashedField, value);
								}
							}
						
							final byte[] hashedBytes = mapper.writeValueAsBytes(hashedNode);
							final HashCode hashCode = Hashing.sha1().hashBytes(hashedBytes);
							
							// Inject the result as an extra field into the to-be-indexed JSON content
							objNode.put(DocumentMapping._HASH, hashCode.toString());
							_source = mapper.writeValueAsBytes(objNode);
							
						} else {
							_source = mapper.writeValueAsBytes(obj);
						}
						
						processor.add(new IndexRequest(typeIndex, typeString, id)
								.opType(OpType.INDEX)
								.source(_source, XContentType.JSON));
					}
				}
	
				for (String id : deleteOperations.removeAll(type)) {
					processor.add(new DeleteRequest(typeIndex, typeString, id));
				}
				
				// Flush processor between index boundaries
				processor.flush();
			}
			
			// Remaining delete operations can be executed on their own
			for (Class<?> type : ImmutableSet.copyOf(deleteOperations.keySet())) {
				final DocumentMapping mapping = admin.mappings().getMapping(type);
				final String typeString = mapping.typeAsString();
				final String typeIndex = admin.getTypeIndex(mapping);
				
				mappingsToRefresh.add(mapping);
				
				for (String id : deleteOperations.removeAll(type)) {
					processor.add(new DeleteRequest(typeIndex, typeString, id));
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

		// refresh the index if there were only updates
		admin.refresh(mappingsToRefresh);
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
