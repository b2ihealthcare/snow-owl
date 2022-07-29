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
package com.b2international.snowowl.core.request.search;

import java.util.Set;

import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.index.query.Expression;
import com.b2international.index.query.Expressions;

/**
 * @since 8.5
 */
public final class ParsedTermFilter extends TermFilter {

	private static final long serialVersionUID = 1L;
	
	private final String term;
	
	public ParsedTermFilter(String term) {
		if (term == null) {
			throw new BadRequestException("'term' filter parameter was null.");
		}
		this.term = term.trim();
	}
	
	public String getTerm() {
		return term;
	}
	
	@Override
	public Set<String> getTerms() {
		return Set.of(term);
	}
	
	@Override
	public Expression toExpression(String field, String textFieldSuffix, String exactFieldSuffix, String prefixFieldSuffix) {
		return Expressions.matchTextParsed(String.join(".", field, textFieldSuffix), term);
	}
	
	/**
	 * @since 8.5
	 */
	public static final class Builder {
		
		private String term;
		
		Builder() {
		}
		
		Builder(ParsedTermFilter from) {
			this.term = from.getTerm();
		}
		
		public Builder term(String term) {
			this.term = term;
			return this;
		}
		
		public ParsedTermFilter build() {
			return new ParsedTermFilter(term);
		}
		
	}
	
}
