/*
 * Copyright 2011-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.b2international.index.query.TextPredicate.MatchType;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * @since 4.7
 */
public class Expressions {
	
	public static final class ExpressionBuilder extends AbstractExpressionBuilder<ExpressionBuilder> {
		
		@Override
		protected ExpressionBuilder getSelf() {
			return this;
		}

	}
	
	public static ExpressionBuilder builder() {
		return new ExpressionBuilder();
	}
	
	public static Expression exists(final String field) {
		return matchRange(field, (String) null, (String) null);
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
	
	public static Expression match(String field, BigDecimal value) {
		return new DecimalPredicate(field, value);
	}
	
	public static Expression match(String field, Double value) {
		return new DoublePredicate(field, value);
	}

	public static Expression matchAll() {
		return MatchAll.INSTANCE;
	}
	
	public static Expression matchNone() {
		return MatchNone.INSTANCE;
	}

	public static Expression hasParent(Class<?> parentType, Expression expression) {
		return new HasParentPredicate(parentType, expression);
	}

	public static Expression matchRange(String fieldName, Long from, Long to) {
		return matchRange(fieldName, from, to, true, true);
	}
	
	public static Expression matchRange(String fieldName, Long from, Long to, boolean includeFrom, boolean includeTo) {
		return new LongRangePredicate(fieldName, from, to, includeFrom, includeTo);
	}
	
	public static Expression matchRange(String fieldName, Integer from, Integer to) {
		return matchRange(fieldName, from, to, true, true);
	}
	
	public static Expression matchRange(String fieldName, Integer from, Integer to, boolean includeFrom, boolean includeTo) {
		return new IntRangePredicate(fieldName, from, to, includeFrom, includeTo);
	}

	public static Expression matchRange(String field, String from, String to) {
		return matchRange(field, from, to, true, true);
	}
	
	public static Expression matchRange(String field, String from, String to, boolean includeFrom, boolean includeTo) {
		return new StringRangePredicate(field, from, to, includeFrom, includeTo);
	}
	
	public static Expression matchRange(String field, BigDecimal from, BigDecimal to) {
		return matchRange(field, from, to, true, true);
	}
	
	public static Expression matchRange(String field, BigDecimal from, BigDecimal to, boolean includeFrom, boolean includeTo) {
		return new DecimalRangePredicate(field, from, to, includeFrom, includeTo);
	}
	
	public static Expression matchRange(String field, Double from, Double to) {
		return matchRange(field, from, to, true, true);
	}
	
	public static Expression matchRange(String field, Double from, Double to, boolean includeFrom, boolean includeTo) {
		return new DoubleRangePredicate(field, from, to, includeFrom, includeTo);
	}

	public static Expression matchAnyInt(String field, Iterable<Integer> values) {
		return new IntSetPredicate(field, values);
	}
	
	public static Expression matchAnyEnum(String field, Iterable<? extends Enum<?>> values) {
		final Iterable<String> names = Iterables.transform(values, Enum::name);
		return matchAny(field, names);
	}
	
	public static Expression matchAny(String field, Iterable<String> values) {
		return new StringSetPredicate(field, values);
	}
	
	public static Expression matchAnyLong(String field, Iterable<Long> values) {
		return new LongSetPredicate(field, values);
	}
	
	public static Expression matchAnyDecimal(String field, Iterable<BigDecimal> values) {
		return new DecimalSetPredicate(field, values);
	}
	
	public static Expression matchAnyDouble(String field, Iterable<Double> values) {
		return new DoubleSetPredicate(field, values);
	}

	public static Expression boost(Expression expression, float boost) {
		return new BoostPredicate(expression, boost);
	}
	
	public static TextPredicate matchTextAll(String field, String term) {
		return new TextPredicate(field, term, MatchType.ALL);
	}
	
	public static TextPredicate matchTextAny(String field, String term) {
		return new TextPredicate(field, term, MatchType.ANY);
	}
	
	public static TextPredicate matchTextAny(String field, String term, int minShouldMatch) {
		return new TextPredicate(field, term, MatchType.ANY, minShouldMatch);
	}
	
	/**
	 * <p>
	 * Returns a text predicate that uses <a href="https://www.elastic.co/guide/en/elasticsearch/reference/7.10/query-dsl-match-bool-prefix-query.html">match boolean prefix query</a>.
	 * It will match results by constructing a bool query with an AND operator from the analyzed terms,
	 * using the last term in a prefix query and every term preceding it in a term query.
	 * </p>
	 * 
	 * @param field	the text field to query
	 * @param term the string query
	 * @return new match boolean prefix type Text Predicate
	 */
	public static TextPredicate matchBooleanPrefix(String field, String term) {
		return new TextPredicate(field, term, MatchType.BOOLEAN_PREFIX);
	}
	
	public static TextPredicate matchTextPhrase(String field, String term) {
		return new TextPredicate(field, term, MatchType.PHRASE);
	}
	
	public static TextPredicate matchTextFuzzy(String field, String term) {
		return new TextPredicate(field, term, MatchType.FUZZY);
	}
	
	public static TextPredicate matchTextParsed(String field, String term) {
		return new TextPredicate(field, term, MatchType.PARSED);
	}
	
	public static RegexpPredicate regexp(String field, String regexp) {
		return new RegexpPredicate(field, regexp);
	}
	
	public static Expression dismaxWithScoreCategories(Expression...disjuncts) {
		return dismaxWithScoreCategories(List.of(disjuncts));
	}
	
	public static Expression dismaxWithScoreCategories(List<Expression> disjuncts) {
		return new DisMaxPredicate(
			IntStream.range(0, disjuncts.size())
				.mapToObj(i -> scriptScore(disjuncts.get(i), "normalizeWithOffset", Map.of("offset", disjuncts.size() - 1 - i)))
				.collect(Collectors.toList()),
			0.0f
		);
	}
	
	public static Expression dismax(Expression...disjuncts) {
		return dismax(List.of(disjuncts));
	}
	
	public static Expression dismax(Collection<Expression> disjuncts) {
		return new DisMaxPredicate(disjuncts, 0.0f);
	}

	public static Expression scriptScore(Expression query, String scriptName) {
		return new ScriptScoreExpression(query, scriptName, Collections.emptyMap());
	}
	
	public static Expression scriptScore(Expression query, String scriptName, Map<String, Object> params) {
		return new ScriptScoreExpression(query, scriptName, params);
	}
	
	public static Expression scriptQuery(String script) {
		return new ScriptQueryExpression(script, Collections.emptyMap());
	}
	
	public static Expression scriptQuery(String script, Map<String, Object> params) {
		return new ScriptQueryExpression(script, params);
	}

}