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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.math.BigDecimal;
import java.util.Collection;

import org.junit.Test;

import com.b2international.index.Fixtures.Data;
import com.b2international.index.Fixtures.PartialData;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Query;
import com.b2international.index.query.SortBy;
import com.b2international.index.query.SortBy.Order;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

public class PartialDocumentLoadingTest extends BaseIndexTest {

	/**
	 * @see com.b2international.index.query.DefaultQueryBuilder
	 */
	private static final int DEFAULT_LIMIT = 50;

	@Override
	protected Collection<Class<?>> getTypes() {
		return ImmutableSet.<Class<?>>of(Data.class);
	}
	
	private void checkHits(final Hits<?> hits, int offset, int limit, int total, int returned) {
		assertEquals(offset, hits.getOffset());
		assertEquals(limit, hits.getLimit());
		assertEquals(total, hits.getTotal());
		assertEquals(returned, Iterables.size(hits));
		assertEquals(returned, hits.getHits().size());
	}

	@Test
	public void selectPartialWithClass() throws Exception {
		final Data data1 = new Data();
		data1.setField1("field1_1"); 
		data1.setField2("field2_1");
		indexDocument(KEY1, data1);
		
		final Data data2 = new Data();
		data2.setField2("field1_2"); 
		data2.setField2("field2_2");
		indexDocument(KEY2, data2);
		
		final Query<PartialData> query = Query.selectPartial(PartialData.class, Data.class)
				.where(Expressions.matchAll())
				.build();

		final Hits<PartialData> hits = search(query);
		
		checkHits(hits, 0, DEFAULT_LIMIT, 2, 2);
		assertEquals(data1.getField1(), hits.getHits().get(0).getField1());
		assertEquals(data2.getField1(), hits.getHits().get(1).getField1());
	}

	@Test
	public void selectPartialStringField() throws Exception {
		final Data data1 = new Data();
		data1.setField1("field1_1"); 
		data1.setField2("field2_1");
		indexDocument(KEY1, data1);
		
		final Data data2 = new Data();
		data2.setField1("field1_2"); 
		data2.setField2("field2_2");
		indexDocument(KEY2, data2);
		
		final Query<Data> query = Query.selectPartial(Data.class, "field1")
				.where(Expressions.matchAll())
				.build();
		
		final Hits<Data> hits = search(query);
		
		checkHits(hits, 0, DEFAULT_LIMIT, 2, 2);
		assertEquals(data1.getField1(), hits.getHits().get(0).getField1());
		assertEquals(data2.getField1(), hits.getHits().get(1).getField1());
		
		assertNull(hits.getHits().get(0).getField2());
		assertNull(hits.getHits().get(1).getField2());
	}
	
	@Test
	public void selectPartialAnalyzedField() throws Exception {
		final Data data1 = new Data();
		data1.setAnalyzedField("analyzedField_1"); 
		data1.setField1("field1_1");
		indexDocument(KEY1, data1);
		
		final Data data2 = new Data();
		data2.setAnalyzedField("analyzedField_2"); 
		data2.setField1("field1_2");
		indexDocument(KEY2, data2);
		
		final Query<Data> query = Query.selectPartial(Data.class, "analyzedField")
				.where(Expressions.matchAll())
				.build();
		
		final Hits<Data> hits = search(query);
		
		checkHits(hits, 0, DEFAULT_LIMIT, 2, 2);
		assertEquals(data1.getAnalyzedField(), hits.getHits().get(0).getAnalyzedField());
		assertEquals(data2.getAnalyzedField(), hits.getHits().get(1).getAnalyzedField());
		
		assertNull(hits.getHits().get(0).getField1());
		assertNull(hits.getHits().get(1).getField1());
	}
	
	@Test
	public void selectPartialBigDecimalField() throws Exception {
		final Data data1 = new Data();
		data1.setBigDecimalField(new BigDecimal("100.987654321")); 
		data1.setField1("field1_1");
		indexDocument(KEY1, data1);
		
		final Data data2 = new Data();
		data2.setBigDecimalField(new BigDecimal("200.123456789")); 
		data2.setField1("field1_2");
		indexDocument(KEY2, data2);
		
		final Query<Data> query = Query.selectPartial(Data.class, "bigDecimalField")
				.where(Expressions.matchAll())
				.build();
		
		final Hits<Data> hits = search(query);
		
		checkHits(hits, 0, DEFAULT_LIMIT, 2, 2);
		assertEquals(data1.getBigDecimalField(), hits.getHits().get(0).getBigDecimalField());
		assertEquals(data2.getBigDecimalField(), hits.getHits().get(1).getBigDecimalField());
		
		assertNull(hits.getHits().get(0).getField1());
		assertNull(hits.getHits().get(1).getField1());
	}
	
	@Test
	public void selectPartialFloatFields() throws Exception {
		final Data data1 = new Data();
		data1.setFloatField(64.0f); 
		data1.setFloatWrapper(32.0f); 
		data1.setField1("field1_1");
		indexDocument(KEY1, data1);
		
		final Data data2 = new Data();
		data2.setFloatField(16.0f); 
		data2.setFloatWrapper(8.0f); 
		data2.setField1("field1_2");
		indexDocument(KEY2, data2);
		
		final Query<Data> query = Query.selectPartial(Data.class, "floatField", "floatWrapper")
				.where(Expressions.matchAll())
				.build();
		
		final Hits<Data> hits = search(query);
		
		checkHits(hits, 0, DEFAULT_LIMIT, 2, 2);
		assertEquals(Float.floatToRawIntBits(data1.getFloatField()), Float.floatToRawIntBits(hits.getHits().get(0).getFloatField()));
		assertEquals(Float.floatToRawIntBits(data2.getFloatField()), Float.floatToRawIntBits(hits.getHits().get(1).getFloatField()));
		assertEquals(data1.getFloatWrapper(), hits.getHits().get(0).getFloatWrapper()); // Float is already doing bitwise comparison in equals
		assertEquals(data2.getFloatWrapper(), hits.getHits().get(1).getFloatWrapper());
		
		assertNull(hits.getHits().get(0).getField1());
		assertNull(hits.getHits().get(1).getField1());
	}
	
	@Test
	public void selectPartialLongFields() throws Exception {
		final Data data1 = new Data();
		data1.setLongField(64L); 
		data1.setLongWrapper(32L); 
		data1.setField1("field1_1");
		indexDocument(KEY1, data1);
		
		final Data data2 = new Data();
		data2.setLongField(16L); 
		data2.setLongWrapper(8L); 
		data2.setField1("field1_2");
		indexDocument(KEY2, data2);
		
		final Query<Data> query = Query.selectPartial(Data.class, "longField", "longWrapper")
				.where(Expressions.matchAll())
				.build();
		
		final Hits<Data> hits = search(query);
		
		checkHits(hits, 0, DEFAULT_LIMIT, 2, 2);
		assertEquals(data1.getLongField(), hits.getHits().get(0).getLongField());
		assertEquals(data2.getLongField(), hits.getHits().get(1).getLongField());
		assertEquals(data1.getLongWrapper(), hits.getHits().get(0).getLongWrapper());
		assertEquals(data2.getLongWrapper(), hits.getHits().get(1).getLongWrapper());
		
		assertNull(hits.getHits().get(0).getField1());
		assertNull(hits.getHits().get(1).getField1());
	}
	
	@Test
	public void selectPartialIntFields() throws Exception {
		final Data data1 = new Data();
		data1.setIntField(64); 
		data1.setIntWrapper(32); 
		data1.setField1("field1_1");
		indexDocument(KEY1, data1);
		
		final Data data2 = new Data();
		data2.setIntField(16); 
		data2.setIntWrapper(8); 
		data2.setField1("field1_2");
		indexDocument(KEY2, data2);
		
		final Query<Data> query = Query.selectPartial(Data.class, "intField", "intWrapper")
				.where(Expressions.matchAll())
				.build();
		
		final Hits<Data> hits = search(query);
		
		checkHits(hits, 0, DEFAULT_LIMIT, 2, 2);
		assertEquals(data1.getIntField(), hits.getHits().get(0).getIntField());
		assertEquals(data2.getIntField(), hits.getHits().get(1).getIntField());
		assertEquals(data1.getIntWrapper(), hits.getHits().get(0).getIntWrapper());
		assertEquals(data2.getIntWrapper(), hits.getHits().get(1).getIntWrapper());
		
		assertNull(hits.getHits().get(0).getField1());
		assertNull(hits.getHits().get(1).getField1());
	}
	
	@Test
	public void selectPartialShortFields() throws Exception {
		final Data data1 = new Data();
		data1.setShortField((short) 64); 
		data1.setShortWrapper((short) 32); 
		data1.setField1("field1_1");
		indexDocument(KEY1, data1);
		
		final Data data2 = new Data();
		data2.setShortField((short) 16); 
		data2.setShortWrapper((short) 8); 
		data2.setField1("field1_2");
		indexDocument(KEY2, data2);
		
		final Query<Data> query = Query.selectPartial(Data.class, "shortField", "shortWrapper")
				.where(Expressions.matchAll())
				.build();
		
		final Hits<Data> hits = search(query);
		
		checkHits(hits, 0, DEFAULT_LIMIT, 2, 2);
		assertEquals(data1.getShortField(), hits.getHits().get(0).getShortField());
		assertEquals(data2.getShortField(), hits.getHits().get(1).getShortField());
		assertEquals(data1.getShortWrapper(), hits.getHits().get(0).getShortWrapper());
		assertEquals(data2.getShortWrapper(), hits.getHits().get(1).getShortWrapper());
		
		assertNull(hits.getHits().get(0).getField1());
		assertNull(hits.getHits().get(1).getField1());
	}

	
	@Test
	public void selectPartialIntFieldsWithSort() throws Exception {
		final Data data1 = new Data();
		data1.setIntField(64); 
		data1.setIntWrapper(32); 
		data1.setField1("field1_1");
		indexDocument(KEY1, data1);
		
		final Data data2 = new Data();
		data2.setIntField(16); 
		data2.setIntWrapper(8); 
		data2.setField1("field1_2");
		indexDocument(KEY2, data2);
		
		final Query<Data> query = Query.selectPartial(Data.class, "intField", "intWrapper")
				.where(Expressions.matchAll())
				.sortBy(SortBy.field("field1", Order.DESC))
				.build();
		
		final Hits<Data> hits = search(query);
		
		checkHits(hits, 0, DEFAULT_LIMIT, 2, 2);
		
		// Results are now inverted: the second document should appear first
		assertEquals(data2.getIntField(), hits.getHits().get(0).getIntField());
		assertEquals(data1.getIntField(), hits.getHits().get(1).getIntField());
		assertEquals(data2.getIntWrapper(), hits.getHits().get(0).getIntWrapper());
		assertEquals(data1.getIntWrapper(), hits.getHits().get(1).getIntWrapper());
		
		assertNull(hits.getHits().get(0).getField1());
		assertNull(hits.getHits().get(1).getField1());
	}

}
