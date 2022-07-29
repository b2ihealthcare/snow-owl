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
package com.b2international.index.es.query;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.elasticsearch.script.Script;
import org.slf4j.Logger;

import com.b2international.commons.exceptions.FormattedRuntimeException;
import com.b2international.index.IndexClientFactory;
import com.b2international.index.compat.TextConstants;
import com.b2international.index.mapping.DocumentMapping;
import com.b2international.index.query.*;
import com.b2international.index.query.MoreLikeThisPredicate;
import com.b2international.index.query.TextPredicate.MatchType;
import com.b2international.index.util.DecimalUtils;
import com.google.common.collect.*;

import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.json.JsonData;

/**
 * @since 8.5
 */
public class Es8QueryBuilder {

	private static final Query MATCH_NONE = QueryBuilders.matchNone(m -> m);
	private static String ILLEGAL_STACK_STATE_MESSAGE = "Illegal internal stack state: %s";
	
	private final Deque<Query> deque = Queues.newLinkedBlockingDeque();
	private final Map<String, Object> settings;
	private final DocumentMapping mapping;
	private final Logger log;
	private final String path;
	
	private boolean needsScoring;
	private Float boost = null; // null means no boost
	
	public Es8QueryBuilder(DocumentMapping mapping, Map<String, Object> settings, Logger log) {
		this(mapping, settings, log, "");
	}
	
	private Es8QueryBuilder(DocumentMapping mapping, Map<String, Object> settings, Logger log, String path) {
		this.mapping = mapping;
		this.settings = settings;
		this.log = log;
		this.path = path;
	}
	
	private FormattedRuntimeException newIllegalStateException() {
		return new FormattedRuntimeException(ILLEGAL_STACK_STATE_MESSAGE, deque);
	}
	
	public boolean needsScoring() {
		return needsScoring;
	}
	
	public Query build(Expression expression) {
		checkNotNull(expression, "expression");
		visit(expression);
		if (deque.size() == 1) {
			return deque.pop();
		} else {
			throw newIllegalStateException();
		}
	}

	private void visit(Expression expression) {
		if (expression instanceof BoostPredicate) {
			// in case of boost predicate, store the boost value until visit of the inner expression happens, and let that set the boost value
			BoostPredicate boostPredicate = (BoostPredicate) expression;
			this.boost = boostPredicate.boost();
			try {
				visit(boostPredicate.expression());
			} finally {
				this.boost = null;
			}
		} else if (expression instanceof MatchAll) {
			deque.push(QueryBuilders.matchAll(m -> m));
		} else if (expression instanceof MatchNone) {
			// XXX executing a term query on a probably nonexistent field and value, should return zero docs
			deque.push(MATCH_NONE);
		} else if (expression instanceof StringPredicate) {
			visit((StringPredicate) expression);
		} else if (expression instanceof LongPredicate) {
			visit((LongPredicate) expression);
		} else if (expression instanceof LongRangePredicate) {
			visit((LongRangePredicate) expression);
		} else if (expression instanceof StringRangePredicate) {
			visit((StringRangePredicate) expression);
		} else if (expression instanceof NestedPredicate) {
			visit((NestedPredicate) expression);
		} else if (expression instanceof HasParentPredicate) {
			visit((HasParentPredicate) expression);
		} else if (expression instanceof PrefixPredicate) {
			visit((PrefixPredicate) expression);
		} else if (expression instanceof RegexpPredicate) {
			visit((RegexpPredicate) expression);
		} else if (expression instanceof StringSetPredicate) {
			visit((StringSetPredicate) expression);
		} else if (expression instanceof LongSetPredicate) {
			visit((LongSetPredicate) expression);
		} else if (expression instanceof IntPredicate) {
			visit((IntPredicate) expression);
		} else if (expression instanceof IntSetPredicate) {
			visit((IntSetPredicate) expression);
		} else if (expression instanceof BoolExpression) {
			visit((BoolExpression) expression);
		} else if (expression instanceof BooleanPredicate) {
			visit((BooleanPredicate) expression);
		} else if (expression instanceof IntRangePredicate) {
			visit((IntRangePredicate) expression);
		} else if (expression instanceof TextPredicate) {
			visit((TextPredicate) expression);
		} else if (expression instanceof DisMaxPredicate) {
			visit((DisMaxPredicate) expression);
		} else if (expression instanceof ScriptScoreExpression) {
			visit((ScriptScoreExpression) expression);
		} else if (expression instanceof DecimalPredicate) {
			visit((DecimalPredicate) expression);
		} else if (expression instanceof DecimalRangePredicate) {
			visit((DecimalRangePredicate) expression);
		} else if (expression instanceof DecimalSetPredicate) {
			visit((DecimalSetPredicate) expression);
		} else if (expression instanceof DoublePredicate) {
			visit((DoublePredicate) expression);
		} else if (expression instanceof DoubleRangePredicate) {
			visit((DoubleRangePredicate) expression);
		} else if (expression instanceof DoubleSetPredicate) {
			visit((DoubleSetPredicate) expression);
		} else if (expression instanceof ScriptQueryExpression){
			visit((ScriptQueryExpression) expression);
		} else if (expression instanceof MoreLikeThisPredicate){
			visit((MoreLikeThisPredicate) expression);
		} else {
			throw new IllegalArgumentException("Unexpected expression: " + expression);
		}
	}

	private void visit(ScriptQueryExpression expression) {
		Script esScript = expression.toEsScript(mapping);
		deque.push(QueryBuilders.script(script -> script.boost(this.boost).script(s -> s.inline(in -> in
			.lang(esScript.getLang())
			.source(esScript.getIdOrCode())
			.options(esScript.getOptions())
			.params(Maps.transformValues(esScript.getParams(), JsonData::of))
		))));
	}
	
	private void visit(ScriptScoreExpression expression) {
		final Expression inner = expression.expression();
		visit(inner);
		final Query innerQuery = deque.pop();
		
		Script esScript = expression.toEsScript(mapping);
		
		needsScoring = true;
		deque.push(QueryBuilders
			.functionScore(q -> q
				.boost(this.boost)
				.boostMode(FunctionBoostMode.Replace)
				.query(innerQuery)
				.functions(
					FunctionScoreBuilders.scriptScore(
						scriptScore -> scriptScore.script(
							s -> s.inline(in -> in
								.lang(esScript.getLang())
								.source(esScript.getIdOrCode())
								.options(esScript.getOptions())
								.params(Maps.transformValues(esScript.getParams(), JsonData::of))
							)
						)
					)
				)
			)
		);
	}
	
	private void visit(BoolExpression bool) {
		// Assumes that BoolExpression clauses are stored in writable array lists
		reduceTermFilters(bool.mustClauses());
		reduceTermFilters(bool.filterClauses());
		
		deque.push(QueryBuilders.bool(query -> {
			query.boost(this.boost);
			for (Expression must : bool.mustClauses()) {
				// visit the item and immediately pop the deque item back
				final Es8QueryBuilder innerQueryBuilder = new Es8QueryBuilder(mapping, settings, log, path);
				innerQueryBuilder.visit(must);
				if (innerQueryBuilder.needsScoring) {
					needsScoring = innerQueryBuilder.needsScoring;
					query.must(innerQueryBuilder.deque.pop());
				} else {
					query.filter(innerQueryBuilder.deque.pop());
				}
			}
			
			for (Expression mustNot : bool.mustNotClauses()) {
				visit(mustNot);
				query.mustNot(deque.pop());
			}
			
			for (Expression should : bool.shouldClauses()) {
				visit(should);
				query.should(deque.pop());
			}

			for (Expression filter : bool.filterClauses()) {
				visit(filter);
				query.filter(deque.pop());
			}
			
			if (!bool.shouldClauses().isEmpty()) {
				query.minimumShouldMatch(String.valueOf(bool.minShouldMatch()));
			}
			
			return query;
		}));
	}
	
	private void reduceTermFilters(List<Expression> clauses) {
		Multimap<String, Expression> termExpressionsByField = HashMultimap.create();
		for (Expression expression : List.copyOf(clauses)) {
			if (shouldMergeSingleArgumentPredicate(expression)) {
				termExpressionsByField.put(((SingleArgumentPredicate<?>) expression).getField(), expression);
			} else if (shouldMergeSetPredicate(expression)) {
				termExpressionsByField.put(((SetPredicate<?>) expression).getField(), expression);
			}
		}
		
		for (String field : Set.copyOf(termExpressionsByField.keySet())) {
			Collection<Expression> termExpressions = termExpressionsByField.removeAll(field);
			if (termExpressions.size() > 1) {
				Set<Object> values = null;
				for (Expression expression : termExpressions) {
					if (values != null && values.isEmpty()) {
						break;
					}
					Set<Object> expressionValues;
					if (expression instanceof SingleArgumentPredicate<?>) {
						expressionValues = Set.of(((SingleArgumentPredicate<?>) expression).getArgument());
					} else if (expression instanceof SetPredicate<?>) {
						expressionValues = Set.copyOf(((SetPredicate<?>) expression).values());
					} else {
						throw new IllegalStateException("Invalid clause detected when processing term/terms clauses: " + expression);
					}
					values = values == null ? expressionValues : Set.copyOf(Sets.intersection(values, expressionValues));
				}
				// remove all matching clauses first
				clauses.removeAll(termExpressions);
				// add the new merged expression
				clauses.add(Expressions.matchAnyObject(field, values));
			}
		}

	}
	
	private boolean shouldMergeSingleArgumentPredicate(Expression expression) {
		return AbstractExpressionBuilder.shouldMergeSingleArgumentPredicate(expression) && referencesScalarField(expression);
	}
	
	private boolean shouldMergeSetPredicate(Expression expression) {
		return AbstractExpressionBuilder.shouldMergeSetPredicate(expression) && referencesScalarField(expression);
	}

	// Predicates should not be eliminated if the field is a collection type
	private boolean referencesScalarField(Expression expression) {
		final String fieldName = ((Predicate) expression).getField();
		return mapping.getSelectableFields().contains(fieldName) && !mapping.isCollection(fieldName);
	}

	private void visit(NestedPredicate predicate) {
		final String nestedPath = toFieldPath(predicate);
		final DocumentMapping nestedMapping = mapping.getNestedMapping(predicate.getField());
		final Es8QueryBuilder nestedQueryBuilder = new Es8QueryBuilder(nestedMapping, settings, log, nestedPath);
		nestedQueryBuilder.visit(predicate.getExpression());
		needsScoring = nestedQueryBuilder.needsScoring;
		final Query nestedQuery = nestedQueryBuilder.deque.pop();
		deque.push(QueryBuilders.nested(n -> n
			.boost(this.boost)
			.path(nestedPath)
			.query(nestedQuery)
			.scoreMode(ChildScoreMode.None)
		));
	}

	private String toFieldPath(Predicate predicate) {
		return toFieldPath(predicate.getField());
	}

	private String toFieldPath(final String subPath) {
		return path.isEmpty() ? subPath : String.join(".", path, subPath);
	}
	
	private void visit(HasParentPredicate predicate) {
		throw new UnsupportedOperationException();
	}

	private void visit(TextPredicate predicate) {
		final String field = toFieldPath(predicate);
		final String term = predicate.term();
		final MatchType type = predicate.type();
		final int minShouldMatch = predicate.minShouldMatch();
		Query query;
		switch (type) {
		case BOOLEAN_PREFIX:
			query = QueryBuilders.matchBoolPrefix(mbp -> mbp
					.boost(this.boost)
					.field(field)
					.query(term)
					.analyzer(predicate.analyzer())
					.operator(Operator.And)
			);
			break;
		case PHRASE:
			query = QueryBuilders.matchPhrase(mp -> mp
					.boost(this.boost)
					.field(field)
					.query(term)
					.analyzer(predicate.analyzer())
			);
			break;
		case ALL:
			query = QueryBuilders.match(m -> m
					.boost(this.boost)
					.field(field)
					.query(term)
					.analyzer(predicate.analyzer())
					.operator(Operator.And)
					.fuzziness(predicate.fuzziness())
					.prefixLength(predicate.prefixLength())
					.maxExpansions(predicate.maxExpansions())
			);
			break;
		case ANY:
			query = QueryBuilders.match(m -> m
					.boost(this.boost)
					.field(field)
					.query(term)
					.analyzer(predicate.analyzer())
					.operator(Operator.Or)
					.minimumShouldMatch(Integer.toString(minShouldMatch))
					.fuzziness(predicate.fuzziness())
					.prefixLength(predicate.prefixLength())
					.maxExpansions(predicate.maxExpansions())
			);
			break;
		case PARSED:
			query = QueryBuilders.queryString(qs -> qs
					.boost(this.boost)
					.fields(field)
					.query(TextConstants.escape(term))
					.analyzer(predicate.analyzer())
					.escape(false)
					.allowLeadingWildcard(true)
					.defaultOperator(Operator.And)
			);
			break;
		default: throw new UnsupportedOperationException("Unexpected text match type: " + type);
		}
		if (query == null) {
			query = MATCH_NONE;
		} else {
			needsScoring = true;
		}
		deque.push(query);
	}
	
	private void visit(SingleArgumentPredicate<?> predicate) {
		deque.push(QueryBuilders.term(query -> query
			.boost(this.boost)
			.field(toFieldPath(predicate))
			.value(String.valueOf(predicate.getArgument()))
		));
	}
	
	private void visit(DecimalPredicate predicate) {
		deque.push(QueryBuilders.term(query -> query
			.boost(this.boost)
			.field(toFieldPath(predicate))
			.value(DecimalUtils.encode(predicate.getArgument()))
		));
	}
	
	private <T> void visit(SetPredicate<T> predicate) {
		toTermsQuery(predicate, predicate.values(), null);
	}

	private void visit(DecimalSetPredicate predicate) {
		toTermsQuery(predicate, predicate.values(), DecimalUtils::encode);
	}
	
	// consider max terms count and break into multiple terms queries if number of terms are greater than that value
	private <T> void toTermsQuery(SetPredicate<T> predicate, final Set<T> terms, final Function<T, ?> valueConverter) {
		
		final Function<T, ?> _valueConverter;
		if (valueConverter == null) {
			_valueConverter = a -> a;
		} else {
			_valueConverter = valueConverter;
		}
		
		final int maxTermsCount = Integer.parseInt((String) settings.get(IndexClientFactory.MAX_TERMS_COUNT_KEY));
		if (terms.size() > maxTermsCount) {
			log.warn("More ({}) than currently configured max_terms_count ({}) filter values on field query: {}.{}", terms.size(), maxTermsCount, mapping.typeAsString(), toFieldPath(predicate));
			final Query boolQuery = QueryBuilders.bool(bool -> {
				bool.boost(this.boost);
				
				Iterables.partition(terms, maxTermsCount).forEach(partition -> {
					bool.should(QueryBuilders.terms(t -> t 
						.field(toFieldPath(predicate))
						.terms(TermsQueryField.of(values -> values.value(partition.stream().map(_valueConverter).map(String::valueOf).map(FieldValue::of).collect(Collectors.toList()))))
					));
				});
				
				return bool
					.minimumShouldMatch("1");
			});
			deque.push(boolQuery);
		} else {
			// push the terms query directly
			deque.push(QueryBuilders.terms(t -> t 
				.boost(this.boost)
				.field(toFieldPath(predicate))
				.terms(TermsQueryField.of(values -> values.value(terms.stream().map(_valueConverter).map(String::valueOf).map(FieldValue::of).collect(Collectors.toList()))))
			));
		}
	}
	
	private void visit(PrefixPredicate predicate) {
		if (predicate.values().size() == 0) {
			deque.push(MATCH_NONE);
		} else if (predicate.values().size() == 1) {
			deque.push(QueryBuilders.prefix(p ->
				p
					.boost(this.boost)
					.field(toFieldPath(predicate))
					.value(Iterables.getOnlyElement(predicate.values()))
			));
		} else {
			deque.push(QueryBuilders.bool(bool -> {
				bool.boost(this.boost);
				for (String prefixMatch : predicate.values()) {
					bool.should(QueryBuilders.prefix(p ->
						p
							.field(toFieldPath(predicate))
							.value(prefixMatch)
					));
				}
				return bool;
			}));
		}
	}
	
	private void visit(RegexpPredicate regexp) {
		deque.push(QueryBuilders.regexp(r -> r.boost(this.boost).field(toFieldPath(regexp)).value(regexp.getArgument())));
	}
	
	private void visit(RangePredicate<?> range) {
		deque.push(QueryBuilders.range(r -> {
			r.boost(this.boost);
			if (range.lower() != null) {
				if (range.isIncludeLower()) {
					r.gte(JsonData.of(range.lower()));
				} else {
					r.gt(JsonData.of(range.lower()));
				}
			}
			if (range.upper() != null) {
				if (range.isIncludeUpper()) {
					r.lte(JsonData.of(range.upper()));
				} else {
					r.lt(JsonData.of(range.upper()));
				}
			}
			return r.field(toFieldPath(range));
		}));
	}
	
	private void visit(DecimalRangePredicate range) {
		deque.push(QueryBuilders.range(r -> {
			r.boost(this.boost);
			if (range.lower() != null) {
				final String lower = DecimalUtils.encode(range.lower());
				if (range.isIncludeLower()) {
					r.gte(JsonData.of(lower));
				} else {
					r.gt(JsonData.of(lower));
				}
			}
			if (range.upper() != null) {
				final String upper = DecimalUtils.encode(range.upper());
				if (range.isIncludeUpper()) {
					r.lte(JsonData.of(upper));
				} else {
					r.lt(JsonData.of(upper));
				}
			}
			return r.field(toFieldPath(range));
		}));
	}
	
	private void visit(DisMaxPredicate dismax) {
		deque.push(QueryBuilders.disMax(dm -> {
			dm.boost(this.boost);
			List<Query> disjunctQueries = new ArrayList<>();
			for (Expression disjunct : dismax.disjuncts()) {
				visit(disjunct);
				disjunctQueries.add(deque.pop());
			}
			return dm
					.queries(disjunctQueries)
					.tieBreaker((double) dismax.tieBreaker());
		}));
	}
	
	private void visit(MoreLikeThisPredicate mlt) {
		deque.push(
			QueryBuilders.moreLikeThis(m -> m
				.fields(List.copyOf(mlt.getFields()))
				.like(mlt.getLikeTexts().stream().map(likeText -> Like.of(l -> l.text(likeText))).collect(Collectors.toList()))
				.unlike(mlt.getUnlikeTexts().stream().map(unlikeText -> Like.of(l -> l.text(unlikeText))).collect(Collectors.toList()))
				.maxQueryTerms(mlt.getMaxQueryTerms())
				.minDocFreq(mlt.getMinDocFreq())
				.minTermFreq(mlt.getMinTermFreq())
				.minWordLength(mlt.getMinWordLength())
				.maxWordLength(mlt.getMaxWordLength())
				.minimumShouldMatch(mlt.getMinimumShouldMatch())
			)
		);
	}

}
