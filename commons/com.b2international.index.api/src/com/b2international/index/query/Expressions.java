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
package com.b2international.index.query;

import static com.google.common.collect.Lists.newArrayList;

import java.util.Collection;
import java.util.List;

import com.b2international.index.Analyzers;
import com.b2international.index.query.TextPredicate.MatchType;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

/**
 * @since 4.7
 */
public class Expressions {
	
	public static final class ExpressionBuilder {
		
		private final List<Expression> mustClauses = newArrayList();
		private final List<Expression> mustNotClauses = newArrayList();
		private final List<Expression> shouldClauses = newArrayList();
		private final List<Expression> filterClauses = newArrayList();
		private int minShouldMatch = 1;
		
		private ExpressionBuilder() {
		}
		
		public ExpressionBuilder must(Expression e) {
			this.mustClauses.add(e);
			return this;
		}
		
		public ExpressionBuilder mustNot(Expression e) {
			this.mustNotClauses.add(e);
			return this;
		}
		
		public ExpressionBuilder should(Expression e) {
			this.shouldClauses.add(e);
			return this;
		}
		
		public ExpressionBuilder filter(Expression e) {
			this.filterClauses.add(e);
			return this;
		}
		
		public ExpressionBuilder setMinimumNumberShouldMatch(int minShouldMatch) {
			this.minShouldMatch = minShouldMatch;
			return this;
		}

		public Expression build() {
			if (mustClauses.isEmpty() && mustNotClauses.isEmpty() && shouldClauses.isEmpty() && filterClauses.isEmpty()) {
				return matchAll();
			} else {
				final BoolExpression be = new BoolExpression(mustClauses, mustNotClauses, shouldClauses, filterClauses);
				be.setMinShouldMatch(minShouldMatch);
				return be;
			}
		}

	}
	
	public static Expression nestedMatch(final String path, Expression expression) {
		final List<String> pathSegments = Lists.reverse(Splitter.on(".").splitToList(path));
		Expression previous = expression;
		for (String segment : pathSegments) {
			previous = new NestedPredicate(segment, previous);
		}
		return previous;
	}
	
	public static Expression prefixMatch(final String field, final String prefix) {
		return new PrefixPredicate(field, prefix);
	}
	
	public static Expression exactMatch(String field, String value) {
		return new StringPredicate(field, value);
	}
	
	public static Expression exactMatch(String field, Long value) {
		return new LongPredicate(field, value);
	}
	
	public static Expression match(String field, Boolean value) {
		return new BooleanPredicate(field, value);
	}
	
	public static Expression match(String field, Integer value) {
		return new IntPredicate(field, value);
	}

	public static Expression matchAll() {
		return new MatchAll();
	}
	
	public static Expression matchNone() {
		return new MatchNone();
	}

	public static Expression matchRange(String fieldName, long from, long to) {
		return new LongRangePredicate(fieldName, from, to);
	}
	
	public static Expression matchRange(String fieldName, int from, int to) {
		return new IntRangePredicate(fieldName, from, to);
	}

	public static Expression hasParent(Class<?> parentType, Expression expression) {
		return new HasParentPredicate(parentType, expression);
	}

	public static Expression matchRange(String field, String from, String to) {
		return new StringRangePredicate(field, from, to);
	}

	public static Expression matchAnyInt(String field, Iterable<Integer> values) {
		return new IntSetPredicate(field, ImmutableSet.copyOf(values));
	}
	
	public static Expression matchAny(String field, Iterable<String> values) {
		return new StringSetPredicate(field, ImmutableSet.copyOf(values));
	}
	
	public static Expression matchAnyLong(String field, Iterable<Long> storageKeys) {
		return new LongSetPredicate(field, storageKeys);
	}

	public static ExpressionBuilder builder() {
		return new ExpressionBuilder();
	}

	public static Expression boost(Expression expression, float boost) {
		return new BoostPredicate(expression, boost);
	}
	
	public static Expression matchTextAll(String field, String term) {
		return new TextPredicate(field, term, MatchType.ALL);
	}
	
	public static Expression matchTextAll(String field, String term, Analyzers analyzer) {
		return new TextPredicate(field, term, MatchType.ALL, analyzer);
	}
	
	public static Expression matchTextAny(String field, String term) {
		return new TextPredicate(field, term, MatchType.ANY);
	}
	
	public static Expression matchTextAny(String field, String term, Analyzers analyzer) {
		return new TextPredicate(field, term, MatchType.ANY, analyzer);
	}
	
	public static Expression matchTextPhrase(String field, String term) {
		return new TextPredicate(field, term, MatchType.PHRASE);
	}
	
	public static Expression matchTextPhrase(String field, String term, Analyzers analyzer) {
		return new TextPredicate(field, term, MatchType.PHRASE, analyzer);
	}
	
	public static Expression matchTextAllPrefix(String field, String term) {
		return new TextPredicate(field, term, MatchType.ALL_PREFIX);
	}
	
	public static Expression matchTextAllPrefix(String field, String term, Analyzers analyzer) {
		return new TextPredicate(field, term, MatchType.ALL_PREFIX, analyzer);
	}
	
	public static Expression matchTextFuzzy(String field, String term) {
		return new TextPredicate(field, term, MatchType.FUZZY);
	}
	
	public static Expression matchTextFuzzy(String field, String term, Analyzers analyzer) {
		return new TextPredicate(field, term, MatchType.FUZZY, analyzer);
	}
	
	public static Expression matchTextParsed(String field, String term) {
		return new TextPredicate(field, term, MatchType.PARSED);
	}
	
	public static Expression dismax(Collection<Expression> disjuncts) {
		return new DisMaxPredicate(disjuncts, 0.0f);
	}

	public static Expression customScore(Expression query, ScoreFunction func) {
		return customScore(query, func, false);
	}
	
	public static Expression customScore(Expression query, ScoreFunction func, boolean strict) {
		return new CustomScoreExpression(query, func, strict);
	}

}