/*
 * Copyright 2021 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.elasticsearch.common.UUIDs;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.b2international.index.Fixtures.Data;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Query;
import com.google.common.base.Stopwatch;

/**
 * It is possible to manually monitor cache usage via GET localhost:9200/_stats/request_cache?human
 * 
 * @since 8.0
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CacheTest extends BaseIndexTest {

	private static final int NUMBER_OF_DOCS = 10_000;
	private static final int NUMBER_OF_RUNS = 10;

	@Override
	protected Collection<Class<?>> getTypes() {
		return List.of(Fixtures.Data.class);
	}

	@Before
	public void before() {
		List<Data> docs = new ArrayList<>();
		for (int i = 0; i < NUMBER_OF_DOCS; i++) {
			Data data = new Data(UUIDs.randomBase64UUID());
			data.setAnalyzedField("hello-" + i);
			data.setIntField(i);
			docs.add(data);
		}
		indexDocuments(docs);
	}
	
	@Test
	public void _01_uncached_hits() throws Exception {
		Stopwatch w = Stopwatch.createUnstarted();
		for (int i = 0; i < NUMBER_OF_RUNS; i++) {
			w.start();
			Hits<Data> hits = search(Query.select(Data.class)
					.where(Expressions.matchAll())
					.limit(NUMBER_OF_DOCS)
					.build());
			w.stop();
			assertThat(hits).hasSize(NUMBER_OF_DOCS);
		}
		System.err.println("Avg uncached_hits resp time: " + w.elapsed(TimeUnit.MILLISECONDS) / NUMBER_OF_RUNS + "ms");
	}
	
	@Test
	public void _02_cached_hits() throws Exception {
		Stopwatch w = Stopwatch.createUnstarted();
		for (int i = 0; i < 10; i++) {
			w.start();
			Hits<Data> hits = search(Query.select(Data.class)
					.where(Expressions.matchAll())
					.limit(NUMBER_OF_DOCS)
					.cached(true)
					.build());
			w.stop();
			assertThat(hits).hasSize(NUMBER_OF_DOCS);
		}
		System.err.println("Avg cached_hits resp time: " + w.elapsed(TimeUnit.MILLISECONDS) / NUMBER_OF_RUNS + "ms");
	}
	
	@Test
	public void _03_uncached_docvalues() throws Exception {
		Stopwatch w = Stopwatch.createUnstarted();
		for (int i = 0; i < NUMBER_OF_RUNS; i++) {
			w.start();
			Hits<String[]> hits = search(Query.select(String[].class)
					.from(Data.class)
					.fields("id", "intField")
					.where(Expressions.matchAll())
					.limit(NUMBER_OF_DOCS)
					.build());
			w.stop();
			assertThat(hits).hasSize(NUMBER_OF_DOCS);
		}
		System.err.println("Avg uncached_docvalues resp time: " + w.elapsed(TimeUnit.MILLISECONDS) / NUMBER_OF_RUNS + "ms");
	}
	
	@Test
	public void _04_cached_docvalues() throws Exception {
		Stopwatch w = Stopwatch.createUnstarted();
		for (int i = 0; i < NUMBER_OF_RUNS; i++) {
			w.start();
			Hits<String[]> hits = search(Query.select(String[].class)
					.from(Data.class)
					.fields("id", "intField")
					.where(Expressions.matchAll())
					.limit(NUMBER_OF_DOCS)
					.cached(true)
					.build());
			w.stop();
			assertThat(hits).hasSize(NUMBER_OF_DOCS);
		}
		System.err.println("Avg cached_docvalues resp time: " + w.elapsed(TimeUnit.MILLISECONDS) / NUMBER_OF_RUNS + "ms");
	}
	
	@Test
	public void _05_uncached_filtered_range() throws Exception {
		Stopwatch w = Stopwatch.createUnstarted();
		for (int i = 0; i < NUMBER_OF_RUNS; i++) {
			w.start();
			Hits<Data> hits = search(Query.select(Data.class)
					.where(Expressions.matchRange("intField", 1001, 7000))
					.limit(Integer.MAX_VALUE)
					.build());
			w.stop();
			assertThat(hits).hasSize(6000);
		}
		System.err.println("Avg uncached_filtered_range resp time: " + w.elapsed(TimeUnit.MILLISECONDS) / NUMBER_OF_RUNS + "ms");
	}
	
	@Test
	public void _06_cached_filtered_range() throws Exception {
		Stopwatch w = Stopwatch.createUnstarted();
		for (int i = 0; i < NUMBER_OF_RUNS; i++) {
			w.start();
			Hits<Data> hits = search(Query.select(Data.class)
					.where(Expressions.matchRange("intField", 1001, 7000))
					.limit(Integer.MAX_VALUE)
					.cached(true)
					.build());
			w.stop();
			assertThat(hits).hasSize(6000);
		}
		System.err.println("Avg cached_filtered_range resp time: " + w.elapsed(TimeUnit.MILLISECONDS) / NUMBER_OF_RUNS + "ms");
	}
	
}
