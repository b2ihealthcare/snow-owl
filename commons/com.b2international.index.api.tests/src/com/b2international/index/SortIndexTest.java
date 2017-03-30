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
import static com.google.common.collect.Sets.newTreeSet;
import static org.junit.Assert.assertArrayEquals;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.PrimitiveIterator;
import java.util.Random;
import java.util.SortedSet;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;

import com.b2international.index.Fixtures.Data;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.FieldScoreFunction;
import com.b2international.index.query.Query;
import com.b2international.index.query.SortBy;
import com.b2international.index.query.SortBy.Order;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * @since 5.4
 */
public class SortIndexTest extends BaseIndexTest {

	@Override
	protected Collection<Class<?>> getTypes() {
		return ImmutableList.<Class<?>>of(Data.class);
	}

	@Test
	public void sortStringField() throws Exception {
		final SortedSet<String> ordered = newTreeSet(); 

		for (int i = 0; i < 20; i++) {
			String item = null;
			while (item == null || ordered.contains(item)) {
				item = RandomStringUtils.randomAlphabetic(10);
			}
			ordered.add(item);
			final Data data = new Data();
			data.setField1(item); 
			indexDocument(Integer.toString(i), data);
		}

		final Query<Data> ascendingQuery = Query.select(Data.class)
				.where(Expressions.matchAll())
				.sortBy(SortBy.field("field1", Order.ASC))
				.build();

		final Hits<Data> ascendingHits = search(ascendingQuery);
		final List<String> ascendingFields = FluentIterable.from(ascendingHits).transform(data -> data.getField1()).toList();
		assertArrayEquals(Iterables.toArray(ordered, String.class), Iterables.toArray(ascendingFields, String.class));

		final Query<Data> descendingQuery = Query.select(Data.class)
				.where(Expressions.matchAll())
				.sortBy(SortBy.field("field1", Order.DESC))
				.build();

		final Hits<Data> descendingHits = search(descendingQuery);
		final SortedSet<String> descendingOrdered = ImmutableSortedSet.copyOf(ordered).descendingSet();
		final List<String> descendingFields = FluentIterable.from(descendingHits).transform(data -> data.getField1()).toList();
		assertArrayEquals(Iterables.toArray(descendingOrdered, String.class), Iterables.toArray(descendingFields, String.class));
	}

	@Test
	public void sortAnalyzedField() throws Exception {
		final SortedSet<String> ordered = newTreeSet(); 

		for (int i = 0; i < 20; i++) {
			String item = null;
			while (item == null || ordered.contains(item)) {
				item = RandomStringUtils.randomAlphabetic(10);
			}
			ordered.add(item);
			final Data data = new Data();
			data.setAnalyzedField(item); 
			indexDocument(Integer.toString(i), data);
		}

		final Query<Data> ascendingQuery = Query.select(Data.class)
				.where(Expressions.matchAll())
				.sortBy(SortBy.field("analyzedField", Order.ASC))
				.build();

		final Hits<Data> ascendingHits = search(ascendingQuery);
		final List<String> ascendingFields = FluentIterable.from(ascendingHits).transform(data -> data.getAnalyzedField()).toList();
		assertArrayEquals(Iterables.toArray(ordered, String.class), Iterables.toArray(ascendingFields, String.class));

		final Query<Data> descendingQuery = Query.select(Data.class)
				.where(Expressions.matchAll())
				.sortBy(SortBy.field("analyzedField", Order.DESC))
				.build();

		final Hits<Data> descendingHits = search(descendingQuery);
		final SortedSet<String> descendingOrdered = ImmutableSortedSet.copyOf(ordered).descendingSet();
		final List<String> descendingFields = FluentIterable.from(descendingHits).transform(data -> data.getAnalyzedField()).toList();
		assertArrayEquals(Iterables.toArray(descendingOrdered, String.class), Iterables.toArray(descendingFields, String.class));
	}

	@Test
	public void sortBigDecimalField() throws Exception {
		final PrimitiveIterator.OfDouble doubleIterator = new Random().doubles().iterator();
		final SortedSet<BigDecimal> ordered = newTreeSet(); 

		for (int i = 0; i < 20; i++) {
			BigDecimal item = null;
			while (item == null || ordered.contains(item)) {
				item = BigDecimal.valueOf(doubleIterator.nextDouble()); 
			}
			ordered.add(item);
			final Data data = new Data();
			data.setBigDecimalField(item); 
			indexDocument(Integer.toString(i), data);
		}

		final Query<Data> ascendingQuery = Query.select(Data.class)
				.where(Expressions.matchAll())
				.sortBy(SortBy.field("bigDecimalField", Order.ASC))
				.build();

		final Hits<Data> ascendingHits = search(ascendingQuery);
		final List<BigDecimal> ascendingFields = FluentIterable.from(ascendingHits).transform(data -> data.getBigDecimalField()).toList();
		assertArrayEquals(Iterables.toArray(ordered, BigDecimal.class), Iterables.toArray(ascendingFields, BigDecimal.class));

		final Query<Data> descendingQuery = Query.select(Data.class)
				.where(Expressions.matchAll())
				.sortBy(SortBy.field("bigDecimalField", Order.DESC))
				.build();

		final Hits<Data> descendingHits = search(descendingQuery);
		final SortedSet<BigDecimal> descendingOrdered = ImmutableSortedSet.copyOf(ordered).descendingSet();
		final List<BigDecimal> descendingFields = FluentIterable.from(descendingHits).transform(data -> data.getBigDecimalField()).toList();
		assertArrayEquals(Iterables.toArray(descendingOrdered, BigDecimal.class), Iterables.toArray(descendingFields, BigDecimal.class));
	}

	@Test
	public void sortFloatField() throws Exception {
		final PrimitiveIterator.OfDouble doubleIterator = new Random().doubles().iterator();
		final SortedSet<Float> ordered = newTreeSet(); 

		for (int i = 0; i < 20; i++) {
			float item = 0.0f;
			while (item == 0.0f || ordered.contains(item)) {
				item = (float) doubleIterator.nextDouble(); 
			}
			ordered.add(item);
			final Data data = new Data();
			data.setFloatField(item); 
			indexDocument(Integer.toString(i), data);
		}

		final Query<Data> ascendingQuery = Query.select(Data.class)
				.where(Expressions.matchAll())
				.sortBy(SortBy.field("floatField", Order.ASC))
				.build();

		final Hits<Data> ascendingHits = search(ascendingQuery);
		final List<Float> ascendingFields = FluentIterable.from(ascendingHits).transform(data -> data.getFloatField()).toList();
		assertArrayEquals(Iterables.toArray(ordered, Float.class), Iterables.toArray(ascendingFields, Float.class));

		final Query<Data> descendingQuery = Query.select(Data.class)
				.where(Expressions.matchAll())
				.sortBy(SortBy.field("floatField", Order.DESC))
				.build();

		final Hits<Data> descendingHits = search(descendingQuery);
		final SortedSet<Float> descendingOrdered = ImmutableSortedSet.copyOf(ordered).descendingSet();
		final List<Float> descendingFields = FluentIterable.from(descendingHits).transform(data -> data.getFloatField()).toList();
		assertArrayEquals(Iterables.toArray(descendingOrdered, Float.class), Iterables.toArray(descendingFields, Float.class));
	}

	@Test
	public void sortLongField() throws Exception {
		final PrimitiveIterator.OfLong longIterator = new Random().longs().iterator();
		final SortedSet<Long> ordered = newTreeSet(); 

		for (int i = 0; i < 20; i++) {
			long item = 0L;
			while (item == 0L || ordered.contains(item)) {
				item = longIterator.nextLong(); 
			}
			ordered.add(item);
			final Data data = new Data();
			data.setLongField(item); 
			indexDocument(Integer.toString(i), data);
		}

		final Query<Data> ascendingQuery = Query.select(Data.class)
				.where(Expressions.matchAll())
				.sortBy(SortBy.field("longField", Order.ASC))
				.build();

		final Hits<Data> ascendingHits = search(ascendingQuery);
		final List<Long> ascendingFields = FluentIterable.from(ascendingHits).transform(data -> data.getLongField()).toList();
		assertArrayEquals(Iterables.toArray(ordered, Long.class), Iterables.toArray(ascendingFields, Long.class));

		final Query<Data> descendingQuery = Query.select(Data.class)
				.where(Expressions.matchAll())
				.sortBy(SortBy.field("longField", Order.DESC))
				.build();

		final Hits<Data> descendingHits = search(descendingQuery);
		final SortedSet<Long> descendingOrdered = ImmutableSortedSet.copyOf(ordered).descendingSet();
		final List<Long> descendingFields = FluentIterable.from(descendingHits).transform(data -> data.getLongField()).toList();
		assertArrayEquals(Iterables.toArray(descendingOrdered, Long.class), Iterables.toArray(descendingFields, Long.class));
	}

	@Test
	public void sortIntField() throws Exception {
		final PrimitiveIterator.OfInt intIterator = new Random().ints().iterator();
		final SortedSet<Integer> ordered = newTreeSet(); 

		for (int i = 0; i < 20; i++) {
			int item = 0;
			while (item == 0 || ordered.contains(item)) {
				item = intIterator.nextInt(); 
			}
			ordered.add(item);
			final Data data = new Data();
			data.setIntField(item); 
			indexDocument(Integer.toString(i), data);
		}

		final Query<Data> ascendingQuery = Query.select(Data.class)
				.where(Expressions.matchAll())
				.sortBy(SortBy.field("intField", Order.ASC))
				.build();

		final Hits<Data> ascendingHits = search(ascendingQuery);
		final List<Integer> ascendingFields = FluentIterable.from(ascendingHits).transform(data -> data.getIntField()).toList();
		assertArrayEquals(Iterables.toArray(ordered, Integer.class), Iterables.toArray(ascendingFields, Integer.class));

		final Query<Data> descendingQuery = Query.select(Data.class)
				.where(Expressions.matchAll())
				.sortBy(SortBy.field("intField", Order.DESC))
				.build();

		final Hits<Data> descendingHits = search(descendingQuery);
		final SortedSet<Integer> descendingOrdered = ImmutableSortedSet.copyOf(ordered).descendingSet();
		final List<Integer> descendingFields = FluentIterable.from(descendingHits).transform(data -> data.getIntField()).toList();
		assertArrayEquals(Iterables.toArray(descendingOrdered, Integer.class), Iterables.toArray(descendingFields, Integer.class));
	}

	@Test
	public void sortShortField() throws Exception {
		final PrimitiveIterator.OfInt intIterator = new Random().ints().iterator();
		final SortedSet<Short> ordered = newTreeSet(); 

		for (short i = 0; i < 20; i++) {
			short item = 0;
			while (item == 0 || ordered.contains(item)) {
				item = (short) intIterator.nextInt(); 
			}
			ordered.add(item);
			final Data data = new Data();
			data.setShortField(item); 
			indexDocument(Integer.toString(i), data);
		}

		final Query<Data> ascendingQuery = Query.select(Data.class)
				.where(Expressions.matchAll())
				.sortBy(SortBy.field("shortField", Order.ASC))
				.build();

		final Hits<Data> ascendingHits = search(ascendingQuery);
		final List<Short> ascendingFields = FluentIterable.from(ascendingHits).transform(data -> data.getShortField()).toList();
		assertArrayEquals(Iterables.toArray(ordered, Short.class), Iterables.toArray(ascendingFields, Short.class));

		final Query<Data> descendingQuery = Query.select(Data.class)
				.where(Expressions.matchAll())
				.sortBy(SortBy.field("shortField", Order.DESC))
				.build();

		final Hits<Data> descendingHits = search(descendingQuery);
		final SortedSet<Short> descendingOrdered = ImmutableSortedSet.copyOf(ordered).descendingSet();
		final List<Short> descendingFields = FluentIterable.from(descendingHits).transform(data -> data.getShortField()).toList();
		assertArrayEquals(Iterables.toArray(descendingOrdered, Short.class), Iterables.toArray(descendingFields, Short.class));
	}

	@Test
	public void sortDocOrder() throws Exception {
		final List<String> ordered = newArrayList(); 
		
		for (int i = 0; i < 20; i++) {
			String item = null;
			while (item == null || ordered.contains(item)) {
				item = RandomStringUtils.randomAlphabetic(10);
			}
			ordered.add(item);
			final Data data = new Data();
			data.setField1(item); 
			indexDocument(Integer.toString(i), data);
		}
		
		final Query<Data> ascendingQuery = Query.select(Data.class)
				.where(Expressions.matchAll())
				.sortBy(SortBy.DOC)
				.build();
		
		final Hits<Data> ascendingHits = search(ascendingQuery);
		final List<String> ascendingFields = FluentIterable.from(ascendingHits).transform(data -> data.getField1()).toList();
		assertArrayEquals(Iterables.toArray(ordered, String.class), Iterables.toArray(ascendingFields, String.class));
		
		final Query<Data> descendingQuery = Query.select(Data.class)
				.where(Expressions.matchAll())
				.sortBy(SortBy.field(SortBy.FIELD_DOC, Order.DESC))
				.build();
		
		final Hits<Data> descendingHits = search(descendingQuery);
		final List<String> descendingOrdered = Lists.reverse(ordered);
		final List<String> descendingFields = FluentIterable.from(descendingHits).transform(data -> data.getField1()).toList();
		assertArrayEquals(Iterables.toArray(descendingOrdered, String.class), Iterables.toArray(descendingFields, String.class));
	}
	
	@Test
	public void sortScore() throws Exception {
		final List<String> ordered = newArrayList(); 
		
		for (int i = 0; i < 20; i++) {
			String item = null;
			while (item == null || ordered.contains(item)) {
				item = RandomStringUtils.randomAlphabetic(10);
			}
			ordered.add(item);
			final Data data = new Data();
			data.setField1(item); 
			data.setFloatField(100.0f - i);
			indexDocument(Integer.toString(i), data);
		}
		
		final Query<Data> descendingQuery = Query.select(Data.class)
				.where(Expressions.customScore(
						Expressions.matchAll(), 
						new FieldScoreFunction("floatField")))
				.withScores(true)
				.sortBy(SortBy.SCORE)
				.build();
		
		final Hits<Data> descendingHits = search(descendingQuery);
		final List<String> descendingFields = FluentIterable.from(descendingHits).transform(data -> data.getField1()).toList();
		assertArrayEquals(Iterables.toArray(ordered, String.class), Iterables.toArray(descendingFields, String.class));
		
		final Query<Data> ascendingQuery = Query.select(Data.class)
				.where(Expressions.customScore(
						Expressions.matchAll(),  
						new FieldScoreFunction("floatField")))
				.sortBy(SortBy.field(SortBy.FIELD_SCORE, Order.ASC))
				.build();
		
		final Hits<Data> ascendingHits = search(ascendingQuery);
		final List<String> ascendingOrdered = Lists.reverse(ordered);
		final List<String> ascendingFields = FluentIterable.from(ascendingHits).transform(data -> data.getField1()).toList();
		assertArrayEquals(Iterables.toArray(ascendingOrdered, String.class), Iterables.toArray(ascendingFields, String.class));
	}	
}
