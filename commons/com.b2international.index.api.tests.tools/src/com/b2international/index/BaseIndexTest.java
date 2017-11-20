/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

import org.junit.After;
import org.junit.Before;

import com.b2international.index.aggregations.Aggregation;
import com.b2international.index.aggregations.AggregationBuilder;
import com.b2international.index.mapping.Mappings;
import com.b2international.index.query.Query;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;

/**
 * @since 4.7
 */
public abstract class BaseIndexTest {

	protected static final String KEY1 = "key1";
	protected static final String KEY2 = "key2";
	
	private Index index;
	private IndexClient client;
	private Mappings mappings;
	
	@Before
	public void setup() {
		final ObjectMapper mapper = new ObjectMapper();
		mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
		mappings = new Mappings(getTypes());
		client = createIndexClient(mapper, mappings);
		index = new DefaultIndex(client);
		index.admin().create();
	}
	
	@After
	public void teardown() {
		index.admin().delete();
	}
	
	/**
	 * @return the document types used by this test case
	 */
	protected abstract Collection<Class<?>> getTypes();

	/**
	 * @return the settings to use for the index client
	 */
	protected Map<String, Object> getSettings() {
		return ImmutableMap.of();
	}
	
	private final IndexClient createIndexClient(ObjectMapper mapper, Mappings mappings) {
		return Indexes.createIndexClient(UUID.randomUUID().toString(), mapper, mappings, getSettings());
	}
	
	protected final Index index() {
		return index;
	}
	
	protected final IndexClient client() {
		return client;
	}
	
	protected final <T> T getDocument(final Class<T> type, final String key) {
		return index().read(index -> index.get(type, key));
	}
	
	protected final void indexDocument(final String key, final Object doc) {
		index().write(index -> {
			index.put(key, doc);
			index.commit();
			return null;
		});
	}
	
	protected final <T> void indexDocuments(final Map<String, T> docs) {
		index().write(index -> {
			index.putAll(docs);
			index.commit();
			return null;
		});
	}
	
	protected final <T> Hits<T> search(final Query<T> query) {
		return index().read(index -> index.search(query));
	}
	
	protected final <T> Aggregation<T> aggregate(AggregationBuilder<T> aggregation) {
		return index().read(new IndexRead<Aggregation<T>>() {
			@Override
			public Aggregation<T> execute(Searcher index) throws IOException {
				return index.aggregate(aggregation);
			}
		});
	}
	
	protected final void deleteDocument(final Class<?> type, final String key) {
		index().write(index -> {
			index.remove(type, key);
			index.commit();
			return null;
		});
	}
	
	protected final <T> Iterable<Hits<T>> scroll(final Query<T> query) {
		return index().read(index -> index.scroll(query));
	}
	
}
