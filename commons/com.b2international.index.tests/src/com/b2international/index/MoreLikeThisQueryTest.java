/*
 * Copyright 2022 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.Collection;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import com.b2international.index.Fixtures.Data;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Query;

/**
 * @since 8.5
 */
public class MoreLikeThisQueryTest extends BaseIndexTest {

	private static final String KEY3 = "key3";
	
	@Override
	protected Collection<Class<?>> getTypes() {
		return List.of(Data.class);
	}
	
	@Test
	public void like() throws Exception {
		Data data1 = new Data(KEY1);
		data1.setAnalyzedField("Some sample like text");
		Data data2 = new Data(KEY2);
		data2.setAnalyzedField("Another sample like text");
		Data data3 = new Data(KEY3);
		data3.setAnalyzedField("Totally irrelevant term");
		indexDocuments(data1, data2, data3);
		
		Hits<Data> hits = search(
			Query.select(Data.class)
				.where(Expressions.moreLikeThis(List.of("analyzedField.text"), List.of("sample like text"), null))
				.build()
		);
		assertThat(hits)
			.extracting(Data::getId)
			.containsOnly(KEY1, KEY2);
	}
	
	@Ignore("Just for education purposes only, MLT unlike parameter unfortunately does not work like this, just filters out unwanted terms from the like sentence and not generating word exclusions clauses, see next test for explicit mustNot query with unlike words")
	@Test
	public void likeAndUnlike() throws Exception {
		Data data1 = new Data(KEY1);
		data1.setAnalyzedField("Some sample like text");
		Data data2 = new Data(KEY2);
		data2.setAnalyzedField("Another sample like text");
		Data data3 = new Data(KEY3);
		data3.setAnalyzedField("Totally irrelevant term");
		indexDocuments(data1, data2, data3);
		
		Hits<Data> hits = search(
			Query.select(Data.class)
				.where(Expressions.moreLikeThis(List.of("analyzedField.text"), List.of("Another sample like text"), List.of("Another")))
				.build()
		);
		assertThat(hits)
			.extracting(Data::getId)
			.containsOnly(KEY1);
	}
	
	@Test
	public void likeAndUnlikeWithMustNotQuery() throws Exception {
		Data data1 = new Data(KEY1);
		data1.setAnalyzedField("Some sample like text");
		Data data2 = new Data(KEY2);
		data2.setAnalyzedField("Another sample battered baby");
		Data data3 = new Data(KEY3);
		data3.setAnalyzedField("Totally irrelevant term");
		indexDocuments(data1, data2, data3);
		
		Hits<Data> hits = search(
			Query.select(Data.class)
				.where(
					Expressions.bool()
						.must(Expressions.moreLikeThis(List.of("analyzedField.text"), List.of("Another sample like text"), null))
						.mustNot(Expressions.matchAny("analyzedField.text", List.of("Another")))
					.build()
				)
				.build()
		);
		assertThat(hits)
			.extracting(Data::getId)
			.containsOnly(KEY1);
	}

}
