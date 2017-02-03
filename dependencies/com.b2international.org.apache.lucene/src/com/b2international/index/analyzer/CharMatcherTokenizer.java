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

import org.apache.lucene.analysis.util.CharTokenizer;

import com.google.common.base.CharMatcher;

/**
 * A variant of {@link CharTokenizer} which splits tokens according to the specified {@link CharMatcher},
 * converting characters to lower case in the normalization step.
 */
public class CharMatcherTokenizer extends CharTokenizer {

	private final CharMatcher tokenBoundaryMatcher;

	public CharMatcherTokenizer(final CharMatcher tokenBoundaryMatcher) {
		super();
		this.tokenBoundaryMatcher = tokenBoundaryMatcher;
	}

	@Override
	protected int normalize(final int c) {
		return Character.toLowerCase(c);
	}

	@Override
	protected boolean isTokenChar(final int c) {
		// CharMatcher can't be applied to the supplementary code point range
		return Character.isSupplementaryCodePoint(c) || !tokenBoundaryMatcher.matches((char) c);
	}
}
