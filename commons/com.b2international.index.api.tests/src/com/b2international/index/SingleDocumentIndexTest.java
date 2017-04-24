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

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.b2international.index.Fixtures.Data;
import com.b2international.index.Fixtures.DataWithMap;
import com.b2international.index.mapping.DocumentMapping;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Query;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * @since 4.7
 */
public class SingleDocumentIndexTest extends BaseIndexTest {

	private static final int NUM_DOCS = 123;

	@Override
	protected Collection<Class<?>> getTypes() {
		return ImmutableList.<Class<?>>of(Data.class, DataWithMap.class);
	}
	
	@Test
	public void searchEmptyIndexShouldReturnNullDocument() throws Exception {
		assertNull(getDocument(Data.class, KEY1));
	}
	
	@Test
	public void indexDocument() throws Exception {
		final Data doc = new Data();
		indexDocument(KEY1, doc);
		assertEquals(doc, getDocument(Data.class, KEY1));
	}
	
	@Test
	public void indexTwoDocumentWithDifferentTypeButWithSameId() throws Exception {
		final Data doc = new Data();
		final DataWithMap doc2 = new DataWithMap(ImmutableMap.<String, Object>of("prop1", "value1"));
		indexDocument(KEY1, doc);
		indexDocument(KEY1, doc2);
		assertEquals(doc, getDocument(Data.class, KEY1));
		assertEquals(doc2, getDocument(DataWithMap.class, KEY1));
	}
	
	@Test
	public void indexMultipleDocuments() throws Exception {
		final Map<String, Data> documents = newHashMap();
		final List<String> keys = newArrayList();
		
		for (int i = 0; i < NUM_DOCS; i++) {
			final Data doc = new Data();
			doc.setIntField(i);
			
			final String key = Integer.toString(i);
			keys.add(key);
			documents.put(key, doc);
		}
		
		indexDocuments(documents);
		
		// Change sequential keys to a random permutation of the original
		Collections.shuffle(keys);
		
		for (int i = 0; i < NUM_DOCS; i++) {
			String key = keys.get(i);
			assertEquals(documents.get(key), getDocument(Data.class, key));
		}
	}
	
	@Test
	public void updateDocument() throws Exception {
		indexDocument();
		
		final Data updatedDoc = new Data();
		updatedDoc.setField1("field1_updated");
		updatedDoc.setField2("field2_updated");
		indexDocument(KEY1, updatedDoc);
		
		Query<Data> query = Query.select(Data.class)
				.where(DocumentMapping.matchId(KEY1))
				.build();
		
		// execute search so we can see that it really updated the doc and did not create a duplicate
		final Iterable<Data> matches = search(query);
		assertThat(matches).hasSize(1);
		assertThat(matches).containsOnly(updatedDoc);
	}
	
	@Test
	public void indexDocumentWithSearchDuringTransaction() throws Exception {
		final Data data = new Data();
		index().write(new IndexWrite<Void>() {
			@Override
			public Void execute(Writer index) throws IOException {
				index.put(KEY1, data);
				assertNull(index.searcher().get(Data.class, KEY1));
				index.commit();
				return null;
			}
		});
	}
	
	@Test
	public void deleteDocument() throws Exception {
		indexDocument();
		deleteDocument(Data.class, KEY1);
		assertNull(getDocument(Data.class, KEY1));
	}
	
	@Test
	public void searchDocuments() throws Exception {
		final Data data1 = new Data();
		data1.setField1("field1_1");
		data1.setField2("field2_1");
		indexDocument(KEY1, data1);
		
		final Data data2 = new Data();
		data2.setField1("field1_2");
		data2.setField2("field2_2");
		indexDocument(KEY2, data2);
		
		// search for field1_1 value, it should return a single doc
		final Query<Data> query = Query.select(Data.class)
				.where(Expressions.exactMatch("field1", "field1_1"))
				.build();
		
		final Iterable<Data> matches = search(query);
		assertThat(matches).hasSize(1);
		assertThat(matches).containsOnly(data1);
	}
	
	@Test
	public void indexDocumentWithMapType() throws Exception {
		final DataWithMap data = new DataWithMap(ImmutableMap.<String, Object>of("field1", "field1Value", "field2", "field2Value"));
		indexDocument(KEY1, data);
		assertEquals(data, getDocument(DataWithMap.class, KEY1));
		
		final Query<DataWithMap> query = Query.select(DataWithMap.class)
				.where(Expressions.exactMatch("field1", "field1Value"))
				.build();
		
		final Iterable<DataWithMap> matches = search(query);
		assertThat(matches).hasSize(1);
		assertThat(matches).containsOnly(data);
	}
	
}
