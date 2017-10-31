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

import java.io.IOException;
import java.util.Collection;

import org.junit.Ignore;
import org.junit.Test;

import com.b2international.index.Fixtures.Data;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Query;
import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableSet;

/**
 * @since 4.7
 */
public class DocLoadPerformanceTest extends BaseIndexTest {

	protected static final int NUM_INDEXED_DOCUMENTS = 1_000_000;

	@Override
	protected Collection<Class<?>> getTypes() {
		return ImmutableSet.<Class<?>>of(Data.class);
	}
	
	@Ignore
	@Test
	public void docLoadTest() throws Exception {
		Stopwatch w = Stopwatch.createStarted();
		index().write(new IndexWrite<Void>() {
			@Override
			public Void execute(Writer index) throws IOException {
				for (int i = 0; i < NUM_INDEXED_DOCUMENTS; i++) {
					final Data data = new Data();
					data.setField1("field1_" + i);
					data.setField2("field2_" + i);
					index.put(String.valueOf(i), data);
				}
				index.commit();
				return null;
			}
		});
		System.err.println("Index took: " + w);
		
		for (int i = 0; i < 10; i++) {
			w.reset().start();
			final Hits<Data> hits = index().read(new IndexRead<Hits<Data>>() {
				@Override
				public Hits<Data> execute(DocSearcher index) throws IOException {
					return index.search(Query.select(Data.class)
							.where(Expressions.matchAll())
							.limit(Integer.MAX_VALUE)
							.build());
				}
			});
			System.err.println("Docload took: " + w);
			System.err.println("---------------");
			assertEquals(NUM_INDEXED_DOCUMENTS, hits.getHits().size());
		}
		
	}

}
