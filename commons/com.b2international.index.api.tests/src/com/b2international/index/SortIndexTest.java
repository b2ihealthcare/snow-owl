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
import static com.google.common.collect.Maps.newLinkedHashMap;
import static com.google.common.collect.Sets.newTreeSet;
import static org.junit.Assert.assertArrayEquals;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.PrimitiveIterator;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Ignore;
import org.junit.Test;

import com.b2international.index.Fixtures.Data;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.FieldScoreFunction;
import com.b2international.index.query.Query;
import com.b2international.index.query.SortBy;
import com.b2international.index.query.SortBy.Order;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * @since 5.4
 */
public class SortIndexTest extends BaseIndexTest {

	private static final int NUM_DOCS = 1000;

	@Override
	protected Collection<Class<?>> getTypes() {
		return ImmutableList.<Class<?>>of(Data.class);
	}

	@Test
	public void sortStringField() throws Exception {
		final TreeSet<String> orderedItems = newTreeSet();
		final Map<String, Data> documents = newHashMap(); 

		for (int i = 0; i < NUM_DOCS; i++) {
			String item = null;
			while (item == null || orderedItems.contains(item)) {
				item = RandomStringUtils.randomAlphabetic(10);
			}
			orderedItems.add(item);
			
			final Data data = new Data();
			data.setField1(item); 
			documents.put(Integer.toString(i), data);
		}

		indexDocuments(documents);
		
		final Query<Data> ascendingQuery = Query.select(Data.class)
				.where(Expressions.matchAll())
				.limit(NUM_DOCS)
				.sortBy(SortBy.field("field1", Order.ASC))
				.build();

		checkDocumentOrder(ascendingQuery, data -> data.getField1(), orderedItems, String.class);
		
		final Query<Data> descendingQuery = Query.select(Data.class)
				.where(Expressions.matchAll())
				.limit(NUM_DOCS)
				.sortBy(SortBy.field("field1", Order.DESC))
				.build();

		checkDocumentOrder(descendingQuery, data -> data.getField1(), orderedItems.descendingSet(), String.class);
	}

	@Test
	public void sortAnalyzedField() throws Exception {
		final TreeSet<String> orderedItems = newTreeSet();
		final Map<String, Data> documents = newHashMap(); 

		for (int i = 0; i < NUM_DOCS; i++) {
			String item = null;
			while (item == null || orderedItems.contains(item)) {
				item = RandomStringUtils.randomAlphabetic(10);
			}
			orderedItems.add(item);
			
			final Data data = new Data();
			data.setAnalyzedField(item);
			documents.put(Integer.toString(i), data);
		}
		
		indexDocuments(documents);

		final Query<Data> ascendingQuery = Query.select(Data.class)
				.where(Expressions.matchAll())
				.limit(NUM_DOCS)
				.sortBy(SortBy.field("analyzedField", Order.ASC))
				.build();
		
		checkDocumentOrder(ascendingQuery, data -> data.getAnalyzedField(), orderedItems, String.class);

		final Query<Data> descendingQuery = Query.select(Data.class)
				.where(Expressions.matchAll())
				.limit(NUM_DOCS)
				.sortBy(SortBy.field("analyzedField", Order.DESC))
				.build();

		checkDocumentOrder(descendingQuery, data -> data.getAnalyzedField(), orderedItems.descendingSet(), String.class);
	}

	@Test
	public void sortBigDecimalField() throws Exception {
		final PrimitiveIterator.OfDouble doubleIterator = new Random().doubles().iterator();
		final TreeSet<BigDecimal> orderedItems = newTreeSet();
		final Map<String, Data> documents = newHashMap();

		for (int i = 0; i < NUM_DOCS; i++) {
			BigDecimal item = null;
			while (item == null || orderedItems.contains(item)) {
				item = BigDecimal.valueOf(doubleIterator.nextDouble()); 
			}
			orderedItems.add(item);
			
			final Data data = new Data();
			data.setBigDecimalField(item); 
			documents.put(Integer.toString(i), data);
		}
		
		indexDocuments(documents);

		final Query<Data> ascendingQuery = Query.select(Data.class)
				.where(Expressions.matchAll())
				.limit(NUM_DOCS)
				.sortBy(SortBy.field("bigDecimalField", Order.ASC))
				.build();

		checkDocumentOrder(ascendingQuery, data -> data.getBigDecimalField(), orderedItems, BigDecimal.class);

		final Query<Data> descendingQuery = Query.select(Data.class)
				.where(Expressions.matchAll())
				.limit(NUM_DOCS)
				.sortBy(SortBy.field("bigDecimalField", Order.DESC))
				.build();

		checkDocumentOrder(descendingQuery, data -> data.getBigDecimalField(), orderedItems.descendingSet(), BigDecimal.class);
	}

	@Test
	public void sortFloatField() throws Exception {
		final PrimitiveIterator.OfDouble doubleIterator = new Random().doubles().iterator();
		final TreeSet<Float> orderedItems = newTreeSet();
		final Map<String, Data> documents = newHashMap();

		for (int i = 0; i < NUM_DOCS; i++) {
			float item = 0.0f;
			while (item == 0.0f || orderedItems.contains(item)) {
				item = (float) doubleIterator.nextDouble(); 
			}
			orderedItems.add(item);
			
			final Data data = new Data();
			data.setFloatField(item); 
			documents.put(Integer.toString(i), data);
		}
		
		indexDocuments(documents);

		final Query<Data> ascendingQuery = Query.select(Data.class)
				.where(Expressions.matchAll())
				.limit(NUM_DOCS)
				.sortBy(SortBy.field("floatField", Order.ASC))
				.build();

		checkDocumentOrder(ascendingQuery, data -> data.getFloatField(), orderedItems, Float.class);

		final Query<Data> descendingQuery = Query.select(Data.class)
				.where(Expressions.matchAll())
				.limit(NUM_DOCS)
				.sortBy(SortBy.field("floatField", Order.DESC))
				.build();

		checkDocumentOrder(descendingQuery, data -> data.getFloatField(), orderedItems.descendingSet(), Float.class);
	}

	@Test
	public void sortLongField() throws Exception {
		final PrimitiveIterator.OfLong longIterator = new Random().longs().iterator();
		final TreeSet<Long> orderedItems = newTreeSet(); 
		final Map<String, Data> documents = newHashMap();
		
		for (int i = 0; i < NUM_DOCS; i++) {
			long item = 0L;
			while (item == 0L || orderedItems.contains(item)) {
				item = longIterator.nextLong(); 
			}
			orderedItems.add(item);
			
			final Data data = new Data();
			data.setLongField(item); 
			documents.put(Integer.toString(i), data);
		}
		
		indexDocuments(documents);

		final Query<Data> ascendingQuery = Query.select(Data.class)
				.where(Expressions.matchAll())
				.limit(NUM_DOCS)
				.sortBy(SortBy.field("longField", Order.ASC))
				.build();

		checkDocumentOrder(ascendingQuery, data -> data.getLongField(), orderedItems, Long.class);
		
		final Query<Data> descendingQuery = Query.select(Data.class)
				.where(Expressions.matchAll())
				.limit(NUM_DOCS)
				.sortBy(SortBy.field("longField", Order.DESC))
				.build();

		checkDocumentOrder(descendingQuery, data -> data.getLongField(), orderedItems.descendingSet(), Long.class);
	}

	@Test
	public void sortIntField() throws Exception {
		final PrimitiveIterator.OfInt intIterator = new Random().ints().iterator();
		final TreeSet<Integer> orderedItems = newTreeSet(); 
		final Map<String, Data> documents = newHashMap();
		
		for (int i = 0; i < NUM_DOCS; i++) {
			int item = 0;
			while (item == 0 || orderedItems.contains(item)) {
				item = intIterator.nextInt(); 
			}
			orderedItems.add(item);
			
			final Data data = new Data();
			data.setIntField(item); 
			documents.put(Integer.toString(i), data);
		}

		indexDocuments(documents);
		
		final Query<Data> ascendingQuery = Query.select(Data.class)
				.where(Expressions.matchAll())
				.limit(NUM_DOCS)
				.sortBy(SortBy.field("intField", Order.ASC))
				.build();
		
		checkDocumentOrder(ascendingQuery, data -> data.getIntField(), orderedItems, Integer.class);
		
		final Query<Data> descendingQuery = Query.select(Data.class)
				.where(Expressions.matchAll())
				.limit(NUM_DOCS)
				.sortBy(SortBy.field("intField", Order.DESC))
				.build();
		
		checkDocumentOrder(descendingQuery, data -> data.getIntField(), orderedItems.descendingSet(), Integer.class);
	}

	@Test
	public void sortShortField() throws Exception {
		final PrimitiveIterator.OfInt intIterator = new Random().ints().iterator();
		final TreeSet<Short> orderedItems = newTreeSet(); 
		final Map<String, Data> documents = newHashMap();
		
		for (short i = 0; i < NUM_DOCS; i++) {
			short item = 0;
			while (item == 0 || orderedItems.contains(item)) {
				item = (short) intIterator.nextInt(); 
			}
			orderedItems.add(item);
			
			final Data data = new Data();
			data.setShortField(item); 
			documents.put(Integer.toString(i), data);
		}
		
		indexDocuments(documents);

		final Query<Data> ascendingQuery = Query.select(Data.class)
				.where(Expressions.matchAll())
				.limit(NUM_DOCS)
				.sortBy(SortBy.field("shortField", Order.ASC))
				.build();

		checkDocumentOrder(ascendingQuery, data -> data.getShortField(), orderedItems, Short.class);

		final Query<Data> descendingQuery = Query.select(Data.class)
				.where(Expressions.matchAll())
				.limit(NUM_DOCS)
				.sortBy(SortBy.field("shortField", Order.DESC))
				.build();

		checkDocumentOrder(descendingQuery, data -> data.getShortField(), orderedItems.descendingSet(), Short.class);
	}

	@Ignore
	@Test
	public void sortDocOrder() throws Exception {
		final List<String> orderedItems = newArrayList();
		// XXX: Order of documents must be preserved, since we are testing document ordering
		final Map<String, Data> documents = newLinkedHashMap();
		
		for (int i = 0; i < NUM_DOCS; i++) {
			String item = null;
			while (item == null || orderedItems.contains(item)) {
				item = RandomStringUtils.randomAlphabetic(10);
			}
			orderedItems.add(item);

			final Data data = new Data();
			data.setField1(item);
			documents.put(item, data);
		}
		
		indexDocuments(documents);
		
		final Query<Data> ascendingQuery = Query.select(Data.class)
				.where(Expressions.matchAll())
				.limit(NUM_DOCS)
				.sortBy(SortBy.DOC)
				.build();
		
		checkDocumentOrder(ascendingQuery, data -> data.getField1(), ImmutableSet.copyOf(orderedItems), String.class);
		
		final Query<Data> descendingQuery = Query.select(Data.class)
				.where(Expressions.matchAll())
				.limit(NUM_DOCS)
				.sortBy(SortBy.field(SortBy.FIELD_DOC, Order.DESC))
				.build();
		
		checkDocumentOrder(descendingQuery, data -> data.getField1(), ImmutableSet.copyOf(Lists.reverse(orderedItems)), String.class);
	}
	
	@Test
	public void sortScore() throws Exception {
		final List<String> orderedItems = newArrayList(); 
		final Map<String, Data> documents = newHashMap();
		
		for (int i = 0; i < NUM_DOCS; i++) {
			String item = null;
			while (item == null || orderedItems.contains(item)) {
				item = RandomStringUtils.randomAlphabetic(10);
			}
			orderedItems.add(item);
			
			final Data data = new Data();
			data.setField1(item); 
			data.setFloatField(100.0f - i);
			documents.put(Integer.toString(i), data);
		}
		
		indexDocuments(documents);
		
		final Query<Data> descendingQuery = Query.select(Data.class)
				.where(Expressions.customScore(Expressions.matchAll(), new FieldScoreFunction("floatField")))
				.limit(NUM_DOCS)
				.sortBy(SortBy.SCORE)
				.build();
		
		checkDocumentOrder(descendingQuery, data -> data.getField1(), ImmutableSet.copyOf(orderedItems), String.class);
		
		final Query<Data> ascendingQuery = Query.select(Data.class)
				.where(Expressions.customScore(Expressions.matchAll(), new FieldScoreFunction("floatField")))
				.limit(NUM_DOCS)
				.sortBy(SortBy.field(SortBy.FIELD_SCORE, Order.ASC))
				.build();
		
		checkDocumentOrder(ascendingQuery, data -> data.getField1(), ImmutableSet.copyOf(Lists.reverse(orderedItems)), String.class);
	}

	private <T> void checkDocumentOrder(Query<Data> query, Function<? super Data, T> hitFunction, Set<T> keySet, Class<T> clazz) {
		final Hits<Data> hits = search(query);
		final T[] actual = FluentIterable.from(hits).transform(hitFunction).toArray(clazz);
		final T[] expected = Iterables.toArray(keySet, clazz);
		assertArrayEquals(expected, actual);
	}	
}
