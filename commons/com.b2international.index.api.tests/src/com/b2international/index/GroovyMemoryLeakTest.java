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
import org.junit.Ignore;
import org.junit.Test;

import com.b2international.index.Fixtures.Data;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Query;
import com.b2international.index.query.SortBy;
import com.google.common.collect.ImmutableList;

/**
 * Test to verify that Groovy script execution in the Lucene implementation of the Index API does not produce huge amount of compiled script classes and constant GC allocation failures (even Metaspace) and eventually unresponsiveness of the entire server.
 * 
 * To detect the memory leak:
 * 1. Use 5.10.10 version of Snow Owl
 * 2. Run this test via the provided launch configuration
 * 3. Use the GC log output to detect the mem leak (or use an external JVM monitoring app, like jvisualvm)
 * 4. After the first/second GC in the Metaspace area the JVM will enter into a state, where it constantly tries to GC the Metaspace area, but it can't free up space at all.  
 * 
 * To verify the memory leak:
 * 1. Use 5.10.11 version of Snow Owl (or apply the fix from the corresponding commit)
 * 2. Run this test via the provided launch configuration
 * 3. Use the GC log output to detect that there is no Metaspace GC at all (or use an external JVM monitoring app, like jvisualvm)
 * 4. The fix reduces the amount of runtime generated Script classes, so Metaspace won't fill up and the JVM won't enter into the above observed state
 * 
 * @since 5.10.11
 */
public class GroovyMemoryLeakTest extends BaseIndexTest {

	private static final int NUM_DOCS = 10_000;
	
	@Ignore
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

		// run 4 threads to simulate a bit higher load on the index
		executor.submit(theQuery, null);
		executor.submit(theQuery, null);
		executor.submit(theQuery, null);
		executor.submit(theQuery, null);

		executor.shutdown();
		// this won't pass at all, even if the fix is applied
		// the purpose of this test to detect and verify the GC via external monitoring thus it cannot be automated properly
		assertTrue(executor.awaitTermination(5, TimeUnit.MINUTES));
	}

	@Override
	protected Collection<Class<?>> getTypes() {
		return ImmutableList.<Class<?>>of(Data.class);
	}
	
}
