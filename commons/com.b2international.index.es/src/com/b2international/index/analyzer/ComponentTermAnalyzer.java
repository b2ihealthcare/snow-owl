/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.index.analyzer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilter;

import com.b2international.index.query.TextConstants;

/**
 * A variant of {@link Analyzer} that uses {@link CharMatcherTokenizer} to tokenize incoming text and convert it to lower case,
 * then passes tokens through {@link ASCIIFoldingFilter} to convert any non-ASCII characters to their closest ASCII alternative,
 * and optionally {@link BookendTokenFilter} to add start and end markers.
 */
public class ComponentTermAnalyzer extends Analyzer {

	/*
	 * Flag to add artificial marker terms for text start and end -- should be set 
	 * at indexing time and when looking for exact matches using PhraseQuery.
	 */
	private final boolean includeLead;
	private final boolean includeTrail;

	public ComponentTermAnalyzer() {
		this(false, false);
	}

	public ComponentTermAnalyzer(final boolean includeLead, final boolean includeTrail) {
		this.includeLead = includeLead;
		this.includeTrail = includeTrail;
	}

	@Override
	protected TokenStreamComponents createComponents(final String fieldName) {
		final Tokenizer source = createTokenizer(fieldName);
		final TokenFilter result = createFilterChain(source);
		return new TokenStreamComponents(source, result);
	}

	private Tokenizer createTokenizer(final String fieldName) {
		return new CharMatcherTokenizer(TextConstants.WHITESPACE_OR_DELIMITER_MATCHER);
	}

	private TokenFilter createFilterChain(final Tokenizer source) {
		return new BookendTokenFilter(new ASCIIFoldingFilter(source), includeLead, includeTrail);
	}
}
