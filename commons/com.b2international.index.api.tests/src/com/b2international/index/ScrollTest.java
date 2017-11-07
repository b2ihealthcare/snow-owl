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

import static com.google.common.collect.Lists.newArrayListWithExpectedSize;
import static com.google.common.collect.Maps.newHashMap;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang.RandomStringUtils;
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

	private final Random rnd = new Random();

	@Override
	protected Collection<Class<?>> getTypes() {
		return ImmutableList.of(Data.class);
	}
	
	@Test
	public void searchAndReturnTopNHitsWithLimit() throws Exception {
		indexDocs(100_000);
		// return top 100 hits
		Hits<Data> hits = search(Query.select(Data.class)
				.where(Expressions.matchAll())
				.limit(100)
				.build());
		assertThat(hits).hasSize(100);
	}
	
	@Test
	public void searchAndReturnAllHitsWithLimit() throws Exception {
		indexDocs(100_000);
		// return all hits within the first page
		Stopwatch w = Stopwatch.createStarted();
		Hits<Data> hits = search(Query.select(Data.class)
				.where(Expressions.matchAll())
				.limit(100_000)
				.build());
		System.err.println("ReturnAllHitsWithLimit took " + w);
		assertThat(hits).hasSize(100_000);
	}
	
	@Test
	public void searchAndReturnAllHitsWithScroll() throws Exception {
		indexDocs(100_000);
		// return all hits within the first page
		List<Data> hits = newArrayListWithExpectedSize(100_000);
		
		Stopwatch w = Stopwatch.createStarted();
		Iterable<Hits<Data>> scroll = scroll(Query.select(Data.class)
				.where(Expressions.matchAll())
				.limit(1000)
				.build());
 		
 		for (Hits<Data> scrollHits : scroll) {
			hits.addAll(scrollHits.getHits());
		}
 		
 		System.err.println("ReturnAllHitsWithScroll took " + w);
		assertThat(hits).hasSize(100_000);
	}
	
	private void indexDocs(int numberOfDocs) {
		final Map<String, Data> docsToIndex = newHashMap();
		for (int i = 0; i < numberOfDocs; i++) {
			Data doc = new Data();
			doc.setAnalyzedField(RandomStringUtils.randomAlphabetic(20));
			doc.setFloatField(rnd.nextFloat());
			doc.setIntField(rnd.nextInt());
			docsToIndex.put("key"+i, doc);
		}
		indexDocuments(docsToIndex);
	}

}
