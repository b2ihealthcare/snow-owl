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

import com.b2international.index.admin.IndexAdmin;

/**
 * @since 4.7
 */
public final class DefaultIndex implements Index {

	private final IndexClient client;

	public DefaultIndex(IndexClient client) {
		this.client = checkNotNull(client, "client");
	}
	
	@Override
	public <T> T read(IndexRead<T> read) {
		try (Searcher searcher = client.searcher()) {
			return read.execute(searcher);
		} catch (Exception e) {
			throw new IndexException("Failed to execute index read", e);
		}
	}
	
	@Override
	public <T> T write(IndexWrite<T> write) {
		try (Writer writer = client.writer()) {
			return write.execute(writer);
		} catch (Exception e) {
			throw new IndexException("Failed to execute index write", e);
		}
	}
	
//	@Override
//	public <T> T get(Class<T> type, String key) {
//		final MappingStrategy<T> mapping = mapping(type);
//		return mapping.convert(get(mapping.getType(), key));
//	}
//	
//	@Override
//	public Map<String, Object> get(final String type, final String key) {
//		return client.read(new IndexRead<Map<String, Object>>() {
//			@Override
//			public Map<String, Object> execute(Searcher index) throws IOException {
//				final GetResponse res = index.prepareGet(type, key).get();
//				return res.isExists() ? res.getSource() : null;
//			}
//		});
//	}
//	
//	@Override
//	public <T> void put(T object) {
//		prepareIndex(getType(object.getClass()), null, object).withRefresh().get();
//	}
//	
//	@Override
//	public <T> void put(String key, T object) {
//		put(getType(object.getClass()), key, object);
//	}
	
//	@Override
//	public void put(final String type, final String key, final Object object) {
//		this.client.write(new IndexWrite<Void>() {
//			@Override
//			public void execute(Writer index) throws IOException {
//				index.prepareIndex(type, key).setSource(object).get();
//			}
//		});
//	}
//
//	@Override
//	public <T> boolean remove(Class<T> type, String key) {
//		return remove(getType(type), key);
//	}
//	
//	@Override
//	public boolean remove(String type, String key) {
//		return prepareDelete(type, key).withRefresh().get().isFound();
//	}
//	
//	private IndexRequestBuilder prepareIndex(String type, String key, Object object) {
//		if (key != null) {
//			return this.client.prepareIndex(type, key).setSource(object);
//		} else {
//			return this.client.prepareIndex(type).setSource(object);
//		}
//	}
//
//	private DeleteRequestBuilder prepareDelete(String type, String key) {
//		return this.client.prepareDelete(type, key);
//	}

//	private <T> String getType(Class<T> type) {
//		return mapping(type).getType();
//	}

	@Override
	public String name() {
		return admin().name();
	}
	
//	@Override
//	public <T> MappingStrategy<T> mapping(Class<T> type) {
//		return admin().mappings().getMapping(type);
//	}

	@Override
	public IndexAdmin admin() {
		return client.admin();
	}
	
//	@Override
//	public QueryBuilder query() {
//		return Query.builder();
//	}
	
//	@Override
//	public <T> Iterable<T> search(final AfterWhereBuilder query, final Class<T> type) {
//		final SearchContextBuilder context = (SearchContextBuilder) query;
//		final MappingStrategy<T> mapping = mapping(type);
//		final String typeName = mapping.getType();
//		return this.client.read(new IndexRead<Iterable<T>>() {
//			@Override
//			public Iterable<T> execute(Searcher index) throws IOException {
//				final SearchRequestBuilder req = index.prepareSearch(typeName);
//				final SearchExecutor executor = getExecutor(context);
//				return executor.execute(req, type);
//			}
//		});
//	}

//	protected SearchExecutor getExecutor(final SearchContextBuilder context) {
//		SearchExecutor executor = context.executor();
//		if (executor == null) {
//			executor = client.getDefaultExecutor(admin().mappings().mapper());
//		}
//		return executor;
//	}

//	@Override
//	public Iterator<SearchHit> scan(org.elasticsearch.index.query.QueryBuilder queryBuilder) {
//		return new ScanningSearchHitIterator(client, queryBuilder, index, 15000);
//	}
	
}