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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;

import com.b2international.index.Fixtures.DeepData;
import com.b2international.index.Fixtures.MultipleNestedData;
import com.b2international.index.Fixtures.NestedData;
import com.b2international.index.Fixtures.ParentData;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Query;
import com.google.common.collect.ImmutableList;

/**
 * @since 4.7
 */
public class ComplexDocumentIndexTest extends BaseIndexTest {

	@Override
	protected final Collection<Class<?>> getTypes() {
		return ImmutableList.of(DeepData.class, MultipleNestedData.class);
	}
	
	@Test
	public void indexDeeplyNestedDocument() throws Exception {
		final DeepData data = new DeepData(new ParentData("field1", new NestedData("field2")));
		final DeepData data2 = new DeepData(new ParentData("field1", new NestedData("field2Changed")));
		try (Writer writer = client().writer()) {
			writer.put(KEY1, data);
			writer.put(KEY2, data2);
			writer.commit();
		}
		// try to get nested document as is first
		try (DocSearcher searcher = client().searcher()) {
			// get single data
			final DeepData actual = searcher.get(DeepData.class, KEY1);
			assertEquals(data, actual);
			// try nested query
			final Query<DeepData> query = Query.select(DeepData.class)
					.where(Expressions.nestedMatch("parentData.nestedData", 
							Expressions.exactMatch("field2", "field2"))
							).build();
			final Iterable<DeepData> matches = searcher.search(query);
			assertThat(matches).hasSize(1);
			assertThat(matches).containsOnly(data);
		}
	}
	
	@Test
	public void indexCollectionOfNestedDocs() throws Exception {
		final MultipleNestedData data = new MultipleNestedData(Arrays.asList(new NestedData("field2"), new NestedData("field2Another")));
		final MultipleNestedData data2 = new MultipleNestedData(Arrays.asList(new NestedData("field2Changed"), new NestedData("field2AnotherChanged")));
		// index multi nested data
		try (Writer writer = client().writer()) {
			writer.put(KEY1, data);
			writer.put(KEY2, data2);
			writer.commit();
		}
		
		try (DocSearcher searcher = client().searcher()) {
			// get data by key
			final MultipleNestedData actual = searcher.get(MultipleNestedData.class, KEY1);
			assertEquals(data, actual);
			// try nested query on collections
			final Query<MultipleNestedData> query = Query.select(MultipleNestedData.class)
					.where(Expressions.nestedMatch("nestedDatas", 
							Expressions.exactMatch("field2", "field2"))
							).build();
			final Iterable<MultipleNestedData> matches = searcher.search(query);
			assertThat(matches).hasSize(1);
			assertThat(matches).containsOnly(data);
		}
	}
	
}
