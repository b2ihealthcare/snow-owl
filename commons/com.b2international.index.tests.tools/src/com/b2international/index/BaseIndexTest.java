/*
 * Copyright 2011-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.junit.Rule;

import com.b2international.index.aggregations.Aggregation;
import com.b2international.index.aggregations.AggregationBuilder;
import com.b2international.index.query.Query;
import com.b2international.index.util.Reflections;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @since 4.7
 */
public abstract class BaseIndexTest {

	protected static final String KEY1 = "key1";
	protected static final String KEY2 = "key2";

	@Rule
	public final IndexResource index = IndexResource.create(getTypes(), this::configureMapper, this::getIndexSettings);

	/**
	 * @return the document types used by this test case
	 */
	protected abstract Collection<Class<?>> getTypes();

	
	protected void configureMapper(ObjectMapper mapper) {
		
	}
	
	protected Map<String, Object> getIndexSettings() {
		return Collections.emptyMap();
	}
	
	protected final ObjectMapper getMapper() {
		return index.getMapper();
	}
	
	protected final Index index() {
		return index.getIndex();
	}
	
	protected final IndexClient client() {
		return index.getClient();
	}
	
	protected final <T> T getDocument(final Class<T> type, final String key) {
		return index().read(index -> index.get(type, key));
	}
	
	protected final void indexDocument(final Object doc) {
		index().write(index -> {
			index.put(doc);
			index.commit();
			return null;
		});
	}
	
	@SafeVarargs
	protected final <T> void indexDocuments(final T...docs) {
		indexDocuments(Arrays.asList(docs));
	}
	
	protected final <T> void indexDocuments(final Collection<T> docs) {
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
	
	protected void assertDocEquals(Object expected, Object actual) {
		assertNotNull("Actual document is missing from index", actual);
		for (Field actualField : index.getIndex().admin().mappings().getMapping(actual.getClass()).getFields()) {
			Field existingField = null;
			try {
				existingField = Reflections.getField(expected.getClass(), actualField.getName());
			} catch (IndexException e) {
				// ignore fields that do not exist on the expected schema
				continue;
			}
			
			if (WithScore.SCORE.equals(existingField.getName())) {
				// skip known metadata fields from equality check
				continue;
			}
			assertEquals(String.format("Field '%s' should be equal", existingField.getName()), Reflections.getValueOrNull(expected, existingField), Reflections.getValueOrNull(actual, actualField));
		}
	}
	
}