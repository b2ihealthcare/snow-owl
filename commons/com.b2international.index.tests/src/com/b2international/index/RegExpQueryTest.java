/*
 * Copyright 2017-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
		Data match1 = new Data(KEY1);
		match1.setAnalyzedField("Hello Regexp1!");
		
		Data match2 = new Data(KEY2);
		match2.setAnalyzedField("Hello Regexp2!");
		
		Data notMatch = new Data("key3");
		notMatch.setAnalyzedField("Hello World!");
		indexDocuments(match1, match2, notMatch);
		
		final Hits<Data> hits = search(
			Query.select(Data.class)
				.where(Expressions.regexp("analyzedField.exact", ".*Regexp.*"))
				.build()
		);
		
		assertThat(hits).containsOnly(match1, match2);
	}
	
	@Test
	public void whitespaceRegexp() throws Exception {
		Data crlf = new Data(KEY1);
		crlf.setAnalyzedField("Hello\\r\\nRegexp1!");
		
		Data cr = new Data(KEY2);
		cr.setAnalyzedField("Hello\\rRegexp2!");
		
		Data lf = new Data("key3");
		lf.setAnalyzedField("Hello\\nRegexp2!");
		
		Data tab = new Data("key4");
		tab.setAnalyzedField("Hello\\tRegexp2!");
		
		Data regular = new Data("key5");
		regular.setAnalyzedField("Hello Regexp2!");
		
		indexDocuments(crlf, cr, lf, tab, regular);
		
		final Hits<Data> hits = search(
			Query.select(Data.class)
				.where(Expressions.regexp("analyzedField.exact", ".*[\\t\\r\\n]+.*"))
				.build()
		);
		
		assertThat(hits)
			.containsOnly(crlf, cr, lf, tab);
	}
	
	@Test
	public void doubleSpaces() throws Exception {
		Data doubleSpace = new Data(KEY1);
		doubleSpace.setAnalyzedField("Hello  Regexp1!");
		
		Data singleSpace = new Data(KEY2);
		singleSpace.setAnalyzedField("Hello Regexp2!");
		indexDocuments(doubleSpace, singleSpace);
	
		final Hits<Data> hits = search(
			Query.select(Data.class)
				.where(Expressions.regexp("analyzedField.exact", ".*[ ]{2,}.*"))
				.build()
		);
		
		assertThat(hits).containsOnly(doubleSpace);
	}
	
}
