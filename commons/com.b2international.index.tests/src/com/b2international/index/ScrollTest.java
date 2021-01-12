/*
 * Copyright 2017-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.google.common.collect.Lists.newArrayListWithExpectedSize;
import static com.google.common.collect.Maps.newHashMap;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Ignore;
import org.junit.Test;

import com.b2international.index.Fixtures.Data;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Query;
import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableList;

/**
 * @since 6.0.0
 */
public class ScrollTest extends BaseIndexTest {

	private static final int NUM_DOCS = 10_000;
	
	private final Random rnd = new Random();

	@Override
	protected Collection<Class<?>> getTypes() {
		return ImmutableList.of(Data.class);
	}

	@Test
	public void localScroll() throws Exception {
		indexDocs(20_000);

		Stopwatch w = Stopwatch.createStarted();
		Hits<Data> hits = search(Query.select(Data.class)
				.where(Expressions.matchAll())
				.limit(Integer.MAX_VALUE)
				.build());
		System.err.println("ReturnAllHitsWithLocalScroll took " + w);
		assertThat(hits).hasSize(20_000);
	}
	
	@Test
	public void searchAndReturnTopNHitsWithLimit() throws Exception {
		indexDocs(NUM_DOCS);
		// return top 100 hits
		Hits<Data> hits = search(Query.select(Data.class)
				.where(Expressions.matchAll())
				.limit(100)
				.build());
		assertThat(hits).hasSize(100);
	}
	
	@Test
	public void searchAndReturnAllHitsWithLimit() throws Exception {
		indexDocs(NUM_DOCS);
		// return all hits within the first page
		Stopwatch w = Stopwatch.createStarted();
		Hits<Data> hits = search(Query.select(Data.class)
				.where(Expressions.matchAll())
				.limit(NUM_DOCS)
				.build());
		System.err.println("ReturnAllHitsWithLimit took " + w);
		assertThat(hits).hasSize(NUM_DOCS);
	}
	
	@Test
	public void searchAndReturnAllPartialHits() throws Exception {
		indexDocs(NUM_DOCS);
		// return all hits within the first page
		Stopwatch w = Stopwatch.createStarted();
		Hits<String> hits = search(Query.select(String.class)
				.from(Data.class)
				.fields("field1")
				.where(Expressions.matchAll())
				.limit(NUM_DOCS)
				.build());
		System.err.println("ReturnAllPartialHitsWithLimit took " + w);
		assertThat(hits).hasSize(NUM_DOCS);
	}
	
	@Test
	public void searchAndReturnAllHitsWithScroll() throws Exception {
		indexDocs(NUM_DOCS);
		// return all hits within the first page
		List<Data> hits = newArrayListWithExpectedSize(NUM_DOCS);
		
		Stopwatch w = Stopwatch.createStarted();
		Iterable<Hits<Data>> scroll = scroll(Query.select(Data.class)
				.where(Expressions.matchAll())
				.limit(1000)
				.build());
 		
 		for (Hits<Data> scrollHits : scroll) {
			hits.addAll(scrollHits.getHits());
		}
 		
 		System.err.println("ReturnAllHitsWithScroll took " + w);
		assertThat(hits).hasSize(NUM_DOCS);
	}
	
	@Test(expected = SearchContextMissingException.class)
	@Ignore("slows down test suite; scroll context invalidation is non-deterministic")
	public void scrollTimeout() throws Exception {
		indexDocs(NUM_DOCS);
		
		Iterable<Hits<Data>> scroll = scroll(Query.select(Data.class)
				.where(Expressions.matchAll())
				.scroll("500ms")
				.limit(100)
				.build());
		
 		for (Hits<Data> scrollHits : scroll) {
			scrollHits.getHits();
			Thread.sleep(1000L);
			System.out.print(".");
		}
	}
	
	private void indexDocs(int numberOfDocs) {
		final Map<String, Data> docsToIndex = newHashMap();
		for (int i = 0; i < numberOfDocs; i++) {
			Data doc = new Data();
			doc.setAnalyzedField(RandomStringUtils.randomAlphabetic(20));
			doc.setField1("field1" + i);
			doc.setFloatField(rnd.nextFloat());
			doc.setIntField(rnd.nextInt());
			docsToIndex.put("key"+i, doc);
		}
		indexDocuments(docsToIndex);
	}

}
