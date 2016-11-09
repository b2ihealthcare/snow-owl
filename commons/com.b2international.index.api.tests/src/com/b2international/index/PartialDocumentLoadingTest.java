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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Collection;

import org.junit.Test;

import com.b2international.index.Fixtures.Data;
import com.b2international.index.Fixtures.PartialData;
import com.b2international.index.mapping.DocumentMapping;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Query;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

public class PartialDocumentLoadingTest extends BaseIndexTest {

	@Override
	protected Collection<Class<?>> getTypes() {
		return ImmutableSet.<Class<?>>of(Data.class);
	}
	
	@Test
	public void loadSingleFieldFromDocumentWithSelect() throws Exception {
		indexDocument(KEY, new Data("field1", "field2"));
		indexDocument(KEY2, new Data("field11", "field21"));
		
		final Iterable<PartialData> hits = search(Query
				.selectPartial(PartialData.class, Data.class)
				.where(Expressions.matchAny(DocumentMapping._ID, ImmutableSet.of(KEY, KEY2)))
				.build());
		
		assertEquals(2, Iterables.size(hits));
		assertEquals(PartialData.class, Iterables.get(hits, 0).getClass());
		assertEquals(PartialData.class, Iterables.get(hits, 1).getClass());
	}
	
	@Test
	public void selectPartialWithFields() throws Exception {
		final Data data1 = new Data("key1Field1", "key1Field2");
		indexDocument(KEY, data1);
		final Data data2 = new Data("key2Field1", "key2Field2");
		indexDocument(KEY2, data2);
		
		final Query<Data> query = Query.selectPartial(Data.class, "field1")
			.where(Expressions.matchAll())
			.build();
		final Hits<Data> hits = search(query);
		assertThat(hits).hasSize(2);
		
		Data partialData1 = Iterables.get(hits, 0);
		Data partialData2 = Iterables.get(hits, 1);
		assertEquals("key1Field1", partialData1.getField1());
		assertEquals("key2Field1", partialData2.getField1());
		assertNull(partialData1.getField2());
		assertNull(partialData2.getField2());
	}

}
