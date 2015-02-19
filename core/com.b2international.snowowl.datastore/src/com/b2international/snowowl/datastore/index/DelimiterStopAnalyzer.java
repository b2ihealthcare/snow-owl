/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore.index;

import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.util.Version;

import com.b2international.snowowl.core.TextConstants;

/**
 * A Lucene-based analyzer that uses {@link DelimiterTokenizer} to tokenize items, then runs {@link StopFilter} to remove stopwords.
 *
 */
public final class DelimiterStopAnalyzer extends Analyzer {

	@Override
	protected TokenStreamComponents createComponents(String fieldName, Reader reader) {
	    final Tokenizer source = new DelimiterTokenizer(reader);
	    return new TokenStreamComponents(source, new StopFilter(Version.LUCENE_4_9, source, new CharArraySet(Version.LUCENE_4_9, TextConstants.STOPWORDS, true)));
	}
}