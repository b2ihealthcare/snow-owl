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

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;

import org.junit.Test;

import com.b2international.index.Fixtures.Data;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Query;
import com.google.common.collect.ImmutableList;

/**
 * @since 5.12.0
 */
public class RegExpQueryTest extends BaseIndexTest {

	@Override
	protected Collection<Class<?>> getTypes() {
		return ImmutableList.<Class<?>>of(Data.class);
	}
	
	@Test
	public void containsText() throws Exception {
		Data match1 = new Data();
		match1.setAnalyzedField("Hello Regexp1!");
		indexDocument(KEY1, match1);
		
		Data match2 = new Data();
		match2.setAnalyzedField("Hello Regexp2!");
		indexDocument(KEY2, match2);
		
		Data notMatch = new Data();
		notMatch.setAnalyzedField("Hello World!");
		indexDocument("key3", notMatch);
		
		final Hits<Data> hits = search(
			Query.select(Data.class)
				.where(Expressions.matchTextRegexp("analyzedField.exact", ".*Regexp.*"))
				.build()
		);
		
		assertThat(hits).containsOnly(match1, match2);
	}
	
	@Test
	public void whitespaceRegexp() throws Exception {
		Data crlf = new Data();
		crlf.setAnalyzedField("Hello\\r\\nRegexp1!");
		indexDocument(KEY1, crlf);
		
		Data cr = new Data();
		cr.setAnalyzedField("Hello\\rRegexp2!");
		indexDocument(KEY2, cr);
		
		Data lf = new Data();
		lf.setAnalyzedField("Hello\\nRegexp2!");
		indexDocument("key3", lf);
		
		Data tab = new Data();
		tab.setAnalyzedField("Hello\\tRegexp2!");
		indexDocument("key4", tab);
		
		Data regular = new Data();
		regular.setAnalyzedField("Hello Regexp2!");
		indexDocument("key5", regular);
		
		final Hits<Data> hits = search(
			Query.select(Data.class)
				.where(Expressions.matchTextRegexp("analyzedField.exact", ".*[\\t\\r\\n]+.*"))
				.build()
		);
		
		assertThat(hits)
			.containsOnly(crlf, cr, lf, tab);
	}
	
	@Test
	public void doubleSpaces() throws Exception {
		Data doubleSpace = new Data();
		doubleSpace.setAnalyzedField("Hello  Regexp1!");
		indexDocument(KEY1, doubleSpace);
		
		Data singleSpace = new Data();
		singleSpace.setAnalyzedField("Hello Regexp2!");
		indexDocument(KEY2, singleSpace);
	
		final Hits<Data> hits = search(
			Query.select(Data.class)
				.where(Expressions.matchTextRegexp("analyzedField.exact", ".*[ ]{2,}.*"))
				.build()
		);
		
		assertThat(hits).containsOnly(doubleSpace);
	}
	
}
