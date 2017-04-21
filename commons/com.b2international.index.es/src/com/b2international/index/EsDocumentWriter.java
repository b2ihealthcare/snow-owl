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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.deletebyquery.DeleteByQueryAction;
import org.elasticsearch.action.deletebyquery.DeleteByQueryRequestBuilder;
import org.elasticsearch.action.deletebyquery.DeleteByQueryResponse;
import org.elasticsearch.action.index.IndexRequest.OpType;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.reindex.BulkIndexByScrollResponse;
import org.elasticsearch.index.reindex.UpdateByQueryAction;
import org.elasticsearch.index.reindex.UpdateByQueryRequestBuilder;
import org.elasticsearch.script.ScriptService.ScriptType;

import com.b2international.index.admin.EsIndexAdmin;
import com.b2international.index.mapping.DocumentMapping;
import com.b2international.index.query.EsQueryBuilder;
import com.b2international.index.query.Expressions;
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
			BulkResponse response = bulk.get();
			for (BulkItemResponse itemResponse : response.getItems()) {
				checkState(itemResponse.getFailure() == null, "Failed to commit tx to index '%s', %s", admin.name(), itemResponse.getFailureMessage());
			}
		}
	
		for (Class<?> type : deleteOperations.keySet()) {
			final DocumentMapping mapping = admin.mappings().getMapping(type);
			final String typeString = mapping.typeAsString();
			DeleteByQueryRequestBuilder dbqrb = DeleteByQueryAction.INSTANCE.newRequestBuilder(client);
			final DeleteByQueryResponse response = dbqrb
					.setIndices(admin.name())
					.setTypes(typeString)
					.setRouting(typeString)
					.setQuery(new EsQueryBuilder(mapping).build(Expressions.matchAny(DocumentMapping._ID, deleteOperations.get(type))))
					.get();
			checkState(response.getTotalFailed() == 0, "There were failures executing delete docs by query requests");
		}
		
		// apply bulk updates
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
			checkState(r.getIndexingFailures().isEmpty(), "There were indexing failures during bulk update");
		}
		
		// refresh the index, so all changes picked up properly
		admin.client().admin().indices().prepareRefresh(admin.name()).get();
	}

	@Override
	public Searcher searcher() {
		return searcher;
	}

	@Override
	public void close() throws Exception {
	}
	
}
