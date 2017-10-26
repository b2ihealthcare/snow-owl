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

import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Maps.newTreeMap;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.b2international.index.Fixtures.Data;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Query;
import com.b2international.index.query.SortBy;
import com.b2international.index.query.SortBy.Order;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * @since 5.10
 */
@RunWith(Parameterized.class)
public class OffsetLimitTest extends BaseIndexTest {

    @Parameters(name= "{index}: {3} ({0}, {1})")
    public static Iterable<Object[]> data() {
        return Arrays.asList(new Object[][] {
        	{ 0, 0, 0, "hit count only" },  
        	{ 0, 10, 10, "start from beginning" },
        	{ 10, 10, 10, "small offset within the first result window" }, 
        	{ 90, 10, 10, "at the edge of the first result window" }, 
        	{ 95, 10, 10, "crossing the first result window" },
        	{ 100, 10, 10, "at the beginning of the second result window" }, 
        	{ 110, 10, 10, "within the second result window" },
        	{ 190, 10, 10, "at the edge of the second result window" }, 
        	{ 195, 10, 10, "crossing the second result window" },
        	{ 200, 10, 5, "at the beginning of the third result window (but only 5 documents remain in total)" }, 
        	{ 202, 10, 3, "small offset within the third result window" },
        	{ 205, 10, 0, "reaching the end of indexed documents" },
        	{ 300, 10, 0, "offset over total number of documents" },
        	{ 0, 100, 100, "read entire window" },
        	{ 10, 100, 100, "cross two result windows" },
        	{ 5, 200, 200, "cross three windows" },
        	{ 0, 205, 205, "read all documents" },
        	{ 10, 200, 195, "read all documents with offset" }
        });
    }

	private static final int NUM_DOCS = 205;
	private static final int SMALL_WINDOW = 100;

	private final int offset;
	private final int limit;
	private final int expectedDocs;

	public OffsetLimitTest(int offset, int limit, int expectedDocs, String description) {
		this.offset = offset;
		this.limit = limit;
		this.expectedDocs = expectedDocs;
	}
	
	@Override
	protected Collection<Class<?>> getTypes() {
		return ImmutableList.<Class<?>>of(Data.class);
	}
	
	@Override
	protected Map<String, Object> getSettings() {
		Map<String, Object> settings = newHashMap(super.getSettings());
		settings.put(IndexClientFactory.RESULT_WINDOW_KEY, ""+SMALL_WINDOW);
		return ImmutableMap.copyOf(settings);
	}
	
	@Test
	public void testOffsetLimit() throws Exception {
		Map<String, Data> documents = newTreeMap();
		
		for (int i = 0; i < NUM_DOCS; i++) {
			String item = null;
			while (item == null || documents.keySet().contains(item)) {
				item = RandomStringUtils.randomAlphabetic(10);
			}

			final Data data = new Data();
			data.setField1(item);
			documents.put(item, data);
		}
		
		indexDocuments(documents);

		final Query<Data> ascendingQuery = Query.select(Data.class)
				.where(Expressions.matchAll())
				.offset(offset)
				.limit(limit)
				.sortBy(SortBy.field("field1", Order.ASC))
				.build();

		final Hits<Data> ascendingHits = search(ascendingQuery);

		final String[] ascendingFields = FluentIterable.from(ascendingHits)
				.transform(data -> data.getField1())
				.toArray(String.class);
		
		assertEquals(expectedDocs, ascendingFields.length);
		
		final String[] expectedFields = FluentIterable.from(documents.keySet())
				.skip(offset)
				.limit(limit)
				.toArray(String.class);
		
		assertArrayEquals(expectedFields, ascendingFields);
	}
}
