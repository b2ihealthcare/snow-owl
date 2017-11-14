/*
 * Copyright 2017 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newTreeSet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;

import com.b2international.index.Fixtures.Data;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Query;
import com.b2international.index.query.SortBy;
import com.b2international.index.query.SortBy.Order;
import com.google.common.collect.ImmutableList;

/**
 * @since 6.0.0
 */
public class SearchAfterTest extends BaseIndexTest {

	private static final int NUM_DOCS = 1000;
	
	@Override
	protected Collection<Class<?>> getTypes() {
		return ImmutableList.of(Data.class);
	}
	
	@Before
	public void setup() {
		super.setup();
		
		final Map<String, Data> docs = newHashMap(); 

		for (int i = 0; i < NUM_DOCS; i++) {
			final Data data = new Data();
			data.setField1("field" + i);
			docs.put(Integer.toString(i), data);
		}
		
		indexDocuments(docs);
	}
	
	@Test
	public void searchAfterImplicitDocIdSort() throws Exception {
		final Hits<Data> allDocsSortedByDocId = search(Query.select(Data.class)
				.where(Expressions.matchAll())
				.limit(NUM_DOCS)
				.build());
		
		final List<Data> expectedHitsInOrder = allDocsSortedByDocId.stream().limit(10).collect(Collectors.toList());
		
		final Hits<Data> first5ActualHits = search(Query.select(Data.class)
				.where(Expressions.matchAll())
				.limit(5)
				.build());
				
		final List<Data> first5Actual = first5ActualHits
				.stream()
				.limit(5)
				.collect(Collectors.toList());
		
		assertThat(first5Actual).startsWith(expectedHitsInOrder.stream().limit(5).toArray(size -> new Data[size]));
		
		final Hits<Data> second5ActualHits = search(Query.select(Data.class)
				.where(Expressions.matchAll())
				.searchAfter(first5ActualHits.getSearchAfter())
				.limit(5)
				.build());

		final List<Data> second5Actual = second5ActualHits
				.stream()
				.limit(5)
				.collect(Collectors.toList());
		
		assertThat(second5Actual).startsWith(expectedHitsInOrder.stream().skip(5).limit(5).toArray(size -> new Data[size]));
	}
	
	@Test
	public void searchAfterSortOnField() throws Exception {
		final Hits<Data> allDocsSortedByDocId = search(Query.select(Data.class)
				.where(Expressions.matchAll())
				.sortBy(SortBy.field("field1", Order.DESC))
				.limit(NUM_DOCS)
				.build());
		
		final List<Data> expectedHitsInOrder = allDocsSortedByDocId.stream().limit(10).collect(Collectors.toList());
		
		final Hits<Data> first5ActualHits = search(Query.select(Data.class)
				.where(Expressions.matchAll())
				.sortBy(SortBy.field("field1", Order.DESC))
				.limit(5)
				.build());
				
		final List<Data> first5Actual = first5ActualHits
				.stream()
				.limit(5)
				.collect(Collectors.toList());
		
		assertThat(first5Actual).startsWith(expectedHitsInOrder.stream().limit(5).toArray(size -> new Data[size]));
		
		final Hits<Data> second5ActualHits = search(Query.select(Data.class)
				.where(Expressions.matchAll())
				.sortBy(SortBy.field("field1", Order.DESC))
				.searchAfter(first5ActualHits.getSearchAfter())
				.limit(5)
				.build());

		final List<Data> second5Actual = second5ActualHits
				.stream()
				.limit(5)
				.collect(Collectors.toList());
		
		assertThat(second5Actual).startsWith(expectedHitsInOrder.stream().skip(5).limit(5).toArray(size -> new Data[size]));
	}
	
}
