/*
 * Copyright 2017 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkItemResponse.Failure;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.deletebyquery.DeleteByQueryAction;
import org.elasticsearch.action.deletebyquery.DeleteByQueryRequestBuilder;
import org.elasticsearch.action.deletebyquery.DeleteByQueryResponse;
import org.elasticsearch.action.index.IndexRequest.OpType;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkIndexByScrollResponse;
import org.elasticsearch.index.reindex.UpdateByQueryAction;
import org.elasticsearch.index.reindex.UpdateByQueryRequestBuilder;
import org.elasticsearch.script.ScriptService.ScriptType;

import com.b2international.index.admin.EsIndexAdmin;
import com.b2international.index.mapping.DocumentMapping;
import com.b2international.index.query.EsQueryBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;

/**
 * @since 5.10 
 */
public class EsDocumentWriter implements Writer {

	private final EsIndexAdmin admin;
	private final Searcher searcher;

	private final Map<String, Object> indexOperations = newHashMap();
	private final Multimap<Class<?>, String> deleteOperations = HashMultimap.create();
	private final ObjectMapper mapper;
	private List<BulkUpdate<?>> updateOperations = newArrayList();
 	
	public EsDocumentWriter(EsIndexAdmin admin, Searcher searcher, ObjectMapper mapper) {
		this.admin = admin;
		this.searcher = searcher;
		this.mapper = mapper;
	}
	
	@Override
	public void put(String key, Object object) throws IOException {
		indexOperations.put(key, object);
	}

	@Override
	public <T> void putAll(Map<String, T> objectsByKey) throws IOException {
		indexOperations.putAll(objectsByKey);
	}

	@Override
	public <T> void bulkUpdate(BulkUpdate<T> update) throws IOException {
		updateOperations.add(update);
	}

	@Override
	public void remove(Class<?> type, String key) throws IOException {
		removeAll(Collections.singletonMap(type, ImmutableSet.of(key)));
	}

	@Override
	public void removeAll(Map<Class<?>, Set<String>> keysByType) throws IOException {
		for (Class<?> type : keysByType.keySet()) {
			deleteOperations.putAll(type, keysByType.get(type));
		}
	}

	@Override
	public void commit() throws IOException {
		if (indexOperations.isEmpty() && deleteOperations.isEmpty() && updateOperations.isEmpty()) {
			return;
		}
		
		final Client client = admin.client();
		// apply bulk updates first
		for (BulkUpdate<?> update : updateOperations) {
			final DocumentMapping mapping = admin.mappings().getMapping(update.getType());
			final QueryBuilder query = new EsQueryBuilder(mapping).build(update.getFilter());
			UpdateByQueryRequestBuilder ubqrb = UpdateByQueryAction.INSTANCE.newRequestBuilder(client);

			final String rawScript = mapping.getScript(update.getScript());
			org.elasticsearch.script.Script script = new org.elasticsearch.script.Script(rawScript, ScriptType.INLINE, "groovy", ImmutableMap.of("params", update.getParams()));

			ubqrb.source().setIndices(admin.name()).setTypes(mapping.typeAsString()).setRouting(mapping.typeAsString());
			BulkIndexByScrollResponse r = ubqrb
			    .script(script)
			    .filter(query)
			    .get();
			checkState(r.getVersionConflicts() == 0, "There were unknown version conflicts during bulk updates");
			admin.log().info("Updated {} {} documents with script '{}', params({})", r.getUpdated(), mapping.typeAsString(), update.getScript(), update.getParams());
			checkState(r.getSearchFailures().isEmpty(), "There were search failure during bulk updates");
			if (!r.getIndexingFailures().isEmpty()) {
				Throwable t = null;
				for (Failure failure : r.getIndexingFailures()) {
					if (t == null) {
						t = failure.getCause();
					}
					admin.log().error("Index failure during bulk update", failure.getCause());
				}
				throw new IllegalStateException("There were indexing failures during bulk updates. See logs for all failures.", t);
			}
		}
		
		// then bulk indexes
		if (!indexOperations.isEmpty()) {
			final BulkRequestBuilder bulk = client.prepareBulk();
			for (Entry<String, Object> entry : indexOperations.entrySet()) {
				final String id = entry.getKey();
				if (!deleteOperations.containsValue(id)) {
					final Object obj = entry.getValue();
					final DocumentMapping mapping = admin.mappings().getMapping(obj.getClass());
					final byte[] _source = mapper.writeValueAsBytes(obj);
					bulk.add(client
							.prepareIndex(admin.name(), mapping.typeAsString(), id)
							.setOpType(OpType.INDEX)
							.setSource(_source)
							.setRouting(mapping.typeAsString()));
				}
			}
			BulkResponse response = bulk.setRefresh(true).get();
			if (response.hasFailures()) {
				for (BulkItemResponse itemResponse : response.getItems()) {
					checkState(!itemResponse.isFailed(), "Failed to commit tx to index '%s', %s", admin.name(), itemResponse.getFailureMessage());
				}
			}
		}
	
		// and deletes last
		for (Class<?> type : deleteOperations.keySet()) {
			final DocumentMapping mapping = admin.mappings().getMapping(type);
			final String typeString = mapping.typeAsString();
			final DeleteByQueryRequestBuilder dbqrb = DeleteByQueryAction.INSTANCE.newRequestBuilder(client);
			final Collection<String> values = deleteOperations.get(type);
			final DeleteByQueryResponse response = dbqrb
					.setIndices(admin.name())
					.setTypes(typeString)
					.setRouting(typeString)
					.setQuery(QueryBuilders.idsQuery(mapping.typeAsString()).ids(values))
					.get();
			checkState(!response.isTimedOut(), "Delete by query request timed out. %s -> %s", mapping.typeAsString(), values);
			checkState(response.getTotalFailed() == 0, "There were failures executing delete docs by query requests");
			checkState(response.getShardFailures().length == 0, "There were shard failure when executing delete by query request.");
		}
		
		// refresh the index, so all changes picked up properly
		admin.refresh();
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
		for (BulkUpdate<?> update : updateOperations) {
			System.err.format("\t%s -> %s, %s, %s\n", admin.mappings().getMapping(update.getType()).typeAsString(), update.getFilter(), update.getScript(), update.getParams());
		}
	}

	@Override
	public Searcher searcher() {
		return searcher;
	}

	@Override
	public void close() throws Exception {
	}
	
}
