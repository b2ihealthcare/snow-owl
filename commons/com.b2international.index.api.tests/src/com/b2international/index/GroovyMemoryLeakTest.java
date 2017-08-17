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

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;

import com.b2international.index.Fixtures.Data;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Query;
import com.b2international.index.query.SortBy;
import com.google.common.collect.ImmutableList;

/**
 * @since 5.10.11
 */
public class GroovyMemoryLeakTest extends BaseIndexTest {

	private static final int NUM_DOCS = 10_000;
	
	@Test
	public void tryToGenerateMemoryLeak() throws Exception {
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

		ExecutorService executor = Executors.newFixedThreadPool(2);
		
		final Runnable theQuery = () -> {
			for (int i = 0; i < 10_000; i++) {
				final Query<Data> query = Query.select(Data.class)
						.where(Expressions.scriptScore(Expressions.matchAll(), "floatField"))
						.limit(NUM_DOCS)
						.sortBy(SortBy.SCORE)
						.build();
				search(query);
			}
		};

		executor.submit(theQuery, null);
		executor.submit(theQuery, null);
		executor.submit(theQuery, null);
		executor.submit(theQuery, null);
		
		executor.shutdown();
		assertTrue(executor.awaitTermination(5, TimeUnit.MINUTES));
	}

	@Override
	protected Collection<Class<?>> getTypes() {
		return ImmutableList.<Class<?>>of(Data.class);
	}
	
}
