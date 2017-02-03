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

import java.io.IOException;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.KeywordAttribute;

/**
 * A {@link TokenFilter} implementation that uses two Unicode Private Use Area
 * characters to mark the beginning and end of a tokenized sequence, for exact
 * matching support.
 * 
 * @since 4.4
 */
public class BookendTokenFilter extends TokenFilter {

	/**
	 * Leading marker character: U+EC00 (PRIVATE USE AREA: EC00) 
	 */
	public static final char LEADING_MARKER = '\uEC00';

	/**
	 * Trailing marker character: U+EC01 (PRIVATE USE AREA: EC01) 
	 */
	public static final char TRAILING_MARKER = '\uEC01';

	private enum BookendState {
		START,
		PASS_THROUGH,
		EOS;
	}

	private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
	private final KeywordAttribute keywordAtt = addAttribute(KeywordAttribute.class);
	private BookendState bookendState;

	private final boolean includeLead;
	private final boolean includeTrail;

	public BookendTokenFilter(final TokenStream input, final boolean includeLead, final boolean includeTrail) {
		super(input);
		this.includeLead = includeLead;
		this.includeTrail = includeTrail;
		resetBookendState();
	}

	private void resetBookendState() {
		bookendState = includeLead ? BookendState.START : BookendState.PASS_THROUGH;
	}

	@Override
	public void reset() throws IOException {
		super.reset();
		resetBookendState();
	}

	@Override
	public boolean incrementToken() throws IOException {
		switch (bookendState) {
		case START:
			clearAttributes();
			termAtt.setEmpty().append(LEADING_MARKER);
			keywordAtt.setKeyword(true);
			bookendState = BookendState.PASS_THROUGH;
			return true;

		case PASS_THROUGH:
			if (input.incrementToken()) {
				return true;
			}

			// If more input could not be gathered, the next state will be end-of-stream 
			bookendState = BookendState.EOS;

			if (includeTrail) {
				clearAttributes();
				termAtt.setEmpty().append(TRAILING_MARKER);
				keywordAtt.setKeyword(true);
				return true;
			} else {
				return false;
			}

		case EOS:
			return false;

		default:
			throw new IllegalStateException("Unhandled state '" + bookendState + "'.");
		}
	}
}
