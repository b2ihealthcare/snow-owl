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
package com.b2international.index;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import org.elasticsearch.action.DocWriteRequest.OpType;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkItemResponse.Failure;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.reindex.AbstractBulkByScrollRequestBuilder;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.elasticsearch.index.reindex.UpdateByQueryAction;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.script.ScriptType;

import com.b2international.index.admin.EsIndexAdmin;
import com.b2international.index.mapping.DocumentMapping;
import com.b2international.index.query.EsQueryBuilder;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.common.primitives.Ints;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

/**
 * @since 5.10 
 */
public class EsDocumentWriter implements DocWriter {

	private static final int DEFAULT_MAX_NUMBER_OF_VERSION_CONFLICT_RETRIES = 5;

	private static final int BATCHS_SIZE = 10_000;
	
	private final EsIndexAdmin admin;
	private final DocSearcher searcher;

	private final Random random = new Random();
	private final Map<String, Object> indexOperations = newHashMap();
	private final Multimap<Class<?>, String> deleteOperations = HashMultimap.create();
	private final ObjectMapper mapper;
	private List<BulkUpdate<?>> bulkUpdateOperations = newArrayList();
	private List<BulkDelete<?>> bulkDeleteOperations = newArrayList();
 	
	public EsDocumentWriter(EsIndexAdmin admin, DocSearcher searcher, ObjectMapper mapper) {
		this.admin = admin;
		this.searcher = searcher;
		this.mapper = mapper;
	}
	
	@Override
	public void put(String key, Object object) {
		indexOperations.put(key, object);
	}

	@Override
	public <T> void putAll(Map<String, T> objectsByKey) {
		indexOperations.putAll(objectsByKey);
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
		
		final Set<DocumentMapping> mappingsToRefresh = newHashSet();
		final Client client = admin.client();
		// apply bulk updates first
		final ListeningExecutorService executor;
		if (bulkUpdateOperations.size() > 1 || bulkDeleteOperations.size() > 1) {
			final int threads = Ints.min(4, bulkUpdateOperations.size(), bulkDeleteOperations.size());
			executor = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(threads));
		} else {
			executor = MoreExecutors.newDirectExecutorService();
		}
		final List<ListenableFuture<?>> updateFutures = newArrayList();
		for (BulkUpdate<?> update : bulkUpdateOperations) {
			updateFutures.add(executor.submit(() -> bulkUpdate(client, update, mappingsToRefresh)));
		}
		for (BulkDelete<?> delete: bulkDeleteOperations) {
			updateFutures.add(executor.submit(() -> bulkDelete(client, delete, mappingsToRefresh)));
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
			final BulkProcessor processor = BulkProcessor.builder(client, new BulkProcessor.Listener() {
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
			.build();

			for (Entry<String, Object> entry : indexOperations.entrySet()) {
				final String id = entry.getKey();
				if (!deleteOperations.containsValue(id)) {
					final Object obj = entry.getValue();
					final DocumentMapping mapping = admin.mappings().getMapping(obj.getClass());
					mappingsToRefresh.add(mapping);

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
					
					processor.add(client
							.prepareIndex(admin.getTypeIndex(mapping), mapping.typeAsString(), id)
							.setOpType(OpType.INDEX)
							.setSource(_source, XContentType.JSON)
							.request());
				}
			}

			for (Class<?> type : deleteOperations.keySet()) {
				final DocumentMapping mapping = admin.mappings().getMapping(type);
				mappingsToRefresh.add(mapping);
				final String typeString = mapping.typeAsString();
				for (String id : deleteOperations.get(type)) {
					processor.add(client.prepareDelete(admin.getTypeIndex(mapping), typeString, id).request());
				}
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

	private void bulkUpdate(final Client client, final BulkUpdate<?> update, Set<DocumentMapping> mappingsToRefresh) {
		final DocumentMapping mapping = admin.mappings().getMapping(update.getType());
		final String rawScript = mapping.getScript(update.getScript()).script();
		org.elasticsearch.script.Script script = new org.elasticsearch.script.Script(ScriptType.INLINE, "painless", rawScript, ImmutableMap.copyOf(update.getParams()));

		bulkIndexByScroll(update, () -> UpdateByQueryAction.INSTANCE.newRequestBuilder(client).script(script), mappingsToRefresh);
	}

	private void bulkDelete(final Client client, final BulkDelete<?> delete, Set<DocumentMapping> mappingsToRefresh) {
		bulkIndexByScroll(delete, () -> DeleteByQueryAction.INSTANCE.newRequestBuilder(client), mappingsToRefresh);
	}

	private void bulkIndexByScroll(final BulkOperation<?> op, final Supplier<AbstractBulkByScrollRequestBuilder<?, ?>> requestBuilderSupplier, Set<DocumentMapping> mappingsToRefresh) {
		final DocumentMapping mapping = admin.mappings().getMapping(op.getType());
		final QueryBuilder query = new EsQueryBuilder(mapping).build(op.getFilter());
		
		long versionConflicts = 0;
		int attempts = DEFAULT_MAX_NUMBER_OF_VERSION_CONFLICT_RETRIES;
		do {
			AbstractBulkByScrollRequestBuilder<?, ?> requestBuilder = requestBuilderSupplier.get();
			
			requestBuilder.source()
				.setSize(BATCHS_SIZE)
				.setIndices(admin.getTypeIndex(mapping))
				.setTypes(mapping.typeAsString());
			
			BulkByScrollResponse r = requestBuilder
				.setSlices(getConcurrencyLevel())
				.filter(query)
				.get();
			
			boolean created = r.getCreated() > 0;
			if (created) {
				mappingsToRefresh.add(mapping);
				admin.log().info("Created {} {} documents with bulk {}", r.getCreated(), mapping.typeAsString(), op);
			}
			
			boolean updated = r.getUpdated() > 0;
			if (updated) {
				mappingsToRefresh.add(mapping);
				admin.log().info("Updated {} {} documents with bulk {}", r.getUpdated(), mapping.typeAsString(), op);
			}
			
			boolean deleted = r.getDeleted() > 0;
			if (deleted) {
				mappingsToRefresh.add(mapping);
				admin.log().info("Deleted {} {} documents with bulk {}", r.getDeleted(), mapping.typeAsString(), op);
			}
			
			if (!created && !updated && !deleted) {
				admin.log().warn("No {} document changed as a result of bulk {}, no-ops ({}), conflicts ({})", 
						mapping.typeAsString(), 
						op, 
						r.getNoops(), 
						r.getVersionConflicts());
			}
			
			checkState(r.getSearchFailures().isEmpty(), "There were search failures during a bulk operation");
			if (!r.getBulkFailures().isEmpty()) {
				boolean versionConflictsOnly = true;
				Throwable t = null;
				for (Failure failure : r.getBulkFailures()) {
					if (failure.getStatus() != RestStatus.CONFLICT) {
						versionConflictsOnly = false;
						if (t == null) {
							t = failure.getCause();
						}
						admin.log().error("Index failure during bulk operation", failure.getCause());
					} else {
						admin.log().warn("Version conflict reason: {}", failure.getMessage());
					}
				}
				if (!versionConflictsOnly) {
					throw new IllegalStateException("There were indexing failures during a bulk operation. See logs for all failures.", t);
				}
			}
			
			if (attempts <= 0) {
				throw new IndexException("There were indexing failures during a bulk operation. See logs for all failures.", null);
			}
			
			versionConflicts = r.getVersionConflicts();
			if (versionConflicts > 0) {
				--attempts;
				try {
					Thread.sleep(100 + random.nextInt(900));
					admin.refresh(Collections.singleton(mapping));
				} catch (InterruptedException e) {
					throw new IndexException("Interrupted", e);
				}
			}
		} while (versionConflicts > 0);
	}

	private int getConcurrencyLevel() {
		return (int) admin.settings().get(IndexClientFactory.COMMIT_CONCURRENCY_LEVEL);
	}

	/*
	 * Testing only, dumps a text representation of all operations to the console
	 */
	private void dumpOps() throws IOException {
		System.err.println("Added documents:");
		for (Entry<String, Object> entry : indexOperations.entrySet()) {
			System.err.format("\t%s -> %s\n", entry.getKey(), mapper.writeValueAsString(entry.getValue()));
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
	public DocSearcher searcher() {
		return searcher;
	}
}
