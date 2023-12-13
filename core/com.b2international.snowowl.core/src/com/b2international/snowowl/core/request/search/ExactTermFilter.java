/*
 * Copyright 2022 B2i Healthcare, https://b2ihealthcare.com
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

import org.elasticsearch.core.List;

import com.b2international.commons.collections.Collections3;
import com.b2international.index.query.Expression;
import com.b2international.index.query.Expressions;

/**
 * @since 8.5
 */
public final class ExactTermFilter extends TermFilter {

	private static final long serialVersionUID = 1L;
	
	private final Set<String> terms;
	private final boolean caseSensitive;
	
	ExactTermFilter(Iterable<String> terms, boolean caseSensitive) {
		this.terms = terms == null ? null : Collections3.toImmutableSet(terms);
		this.caseSensitive = caseSensitive;
	}
	
	@Override
	public Set<String> getTerms() {
		return terms;
	}
	
	public boolean isCaseSensitive() {
		return caseSensitive;
	}
	
	@Override
	public Expression toExpression(String field, String textFieldSuffix, String exactFieldSuffix, String prefixFieldSuffix) {
		return caseSensitive ? Expressions.matchAny(field, terms) : Expressions.matchAny(String.join(".", field, exactFieldSuffix), terms);
	}
	
	public static final class Builder {
		
		private Iterable<String> terms;
		private boolean caseSensitive;
		
		Builder() {
		}
		
		Builder(ExactTermFilter from) {
			this.terms = from.getTerms();
			this.caseSensitive = from.isCaseSensitive();
		}

		public Builder term(String term) {
			return terms(term == null ? null : List.of(term));
		}
		
		public Builder terms(Iterable<String> terms) {
			this.terms = terms;
			return this;
		}
		
		public Builder caseSensitive(boolean caseSensitive) {
			this.caseSensitive = caseSensitive;
			return this;
		}
		
		public ExactTermFilter build() {
			return new ExactTermFilter(terms, caseSensitive);
		}

	}
	
}
