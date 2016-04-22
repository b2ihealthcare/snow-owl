/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import com.b2international.index.admin.IndexAdmin;
import com.b2international.index.mapping.MappingStrategy;
import com.b2international.index.query.Query;
import com.b2international.index.query.Query.AfterWhereBuilder;
import com.b2international.index.query.Query.QueryBuilder;
import com.b2international.index.query.Query.SearchContextBuilder;
import com.b2international.index.request.DeleteRequestBuilder;
import com.b2international.index.request.GetResponse;
import com.b2international.index.request.IndexRequestBuilder;
import com.b2international.index.request.SearchRequestBuilder;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * General purpose index service implementation on top of Elasticsearch library.
 * 
 * @since 4.7
 */
public class DefaultIndex implements Index {

	private final IndexAdmin admin;
	private final IndexClient client;

	protected DefaultIndex(IndexClient client, IndexAdmin admin) {
		this.client = checkNotNull(client, "client");
		this.admin = checkNotNull(admin, "admin");
	}
	
	@Override
	public <T> T get(Class<T> type, String key) {
		final MappingStrategy<T> mapping = mapping(type);
		return mapping.convert(get(mapping.getType(), key));
	}
	
	@Override
	public Map<String, Object> get(String type, String key) {
		final GetResponse getResponse = this.client.prepareGet(type, key).get();
		if (getResponse.isExists()) {
			return getResponse.getSource();
		} else {
			return null;
		}
	}
	
	@Override
	public <T> void put(T object) {
		prepareIndex(getType(object.getClass()), null, object).withRefresh().get();
	}
	
	@Override
	public <T> void put(String key, T object) {
		put(getType(object.getClass()), key, object);
	}
	
	@Override
	public void put(String type, String key, Object object) {
		prepareIndex(type, key, object).withRefresh().get();
	}

	private byte[] toJSON(Object object) {
		try {
			return admin().mappings().mapper().writeValueAsBytes(object);
		} catch (JsonProcessingException e) {
			throw new IndexException("Failed to serialized object", e);
		}
	}

	@Override
	public <T> boolean remove(Class<T> type, String key) {
		return remove(getType(type), key);
	}
	
	@Override
	public boolean remove(String type, String key) {
		return prepareDelete(type, key).withRefresh().get().isFound();
	}
	
	private IndexRequestBuilder prepareIndex(String type, String key, Object object) {
		if (key != null) {
			return this.client.prepareIndex(type, key).setSource(toJSON(object));
		} else {
			return this.client.prepareIndex(type).setSource(toJSON(object));
		}
	}

	private DeleteRequestBuilder prepareDelete(String type, String key) {
		return this.client.prepareDelete(type, key);
	}

	private <T> String getType(Class<T> type) {
		return mapping(type).getType();
	}

	@Override
	public String name() {
		return admin().name();
	}
	
	@Override
	public <T> MappingStrategy<T> mapping(Class<T> type) {
		return admin().mappings().getMapping(type);
	}

	@Override
	public IndexAdmin admin() {
		return admin;
	}
	
	@Override
	public QueryBuilder query() {
		return Query.builder();
	}
	
	@Override
	public <T> Iterable<T> search(AfterWhereBuilder query, Class<T> type) {
		final SearchContextBuilder context = (SearchContextBuilder) query;
		final MappingStrategy<T> mapping = mapping(type);
		final String typeName = mapping.getType();
		final SearchRequestBuilder req = this.client.prepareSearch(typeName);
		final SearchExecutor executor = getExecutor(context);
		return executor.execute(req, type);
	}

	protected SearchExecutor getExecutor(final SearchContextBuilder context) {
		SearchExecutor executor = context.executor();
		if (executor == null) {
			executor = client.getDefaultExecutor(admin().mappings().mapper());
		}
		return executor;
	}

//	@Override
//	public Iterator<SearchHit> scan(org.elasticsearch.index.query.QueryBuilder queryBuilder) {
//		return new ScanningSearchHitIterator(client, queryBuilder, index, 15000);
//	}
	
}