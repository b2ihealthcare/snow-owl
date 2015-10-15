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
	public static final char PUA_EC00_MARKER = '\uEC00';

	/**
	 * Trailing marker character: U+EC01 (PRIVATE USE AREA: EC01) 
	 */
	public static final char PUA_EC01_MARKER = '\uEC01';

	private enum State {
		LEAD_IN,
		FILTER,
		LEAD_OUT;
	}

	private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
	private final KeywordAttribute keywordAtt = addAttribute(KeywordAttribute.class);
	private State state = State.LEAD_IN;

	public BookendTokenFilter(final TokenStream input) {
		super(input);
	}

	@Override
	public void reset() throws IOException {
		super.reset();
		state = State.LEAD_IN;
	}

	@Override
	public boolean incrementToken() throws IOException {
		switch (state) {
		case LEAD_IN:
			termAtt.setLength(1);
			termAtt.setEmpty();
			termAtt.append(PUA_EC00_MARKER);
			keywordAtt.setKeyword(true);
			state = State.FILTER;
			return true;

		case FILTER:
			if (!input.incrementToken()) {
				termAtt.setLength(1);
				termAtt.setEmpty();
				termAtt.append(PUA_EC01_MARKER);
				keywordAtt.setKeyword(true);
				state = State.LEAD_OUT;
			}
			return true;

		case LEAD_OUT:
			return false;

		default:
			throw new IllegalStateException("Unhandled state '" + state + "'.");
		}
	}
}
