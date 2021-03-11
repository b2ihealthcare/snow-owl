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
package com.b2international.index.es.query;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Deque;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.common.lucene.search.function.CombineFunction;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.*;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.slf4j.Logger;

import com.b2international.commons.exceptions.FormattedRuntimeException;
import com.b2international.index.IndexClientFactory;
import com.b2international.index.compat.TextConstants;
import com.b2international.index.mapping.DocumentMapping;
import com.b2international.index.query.*;
import com.b2international.index.query.TextPredicate.MatchType;
import com.b2international.index.util.DecimalUtils;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Queues;

/**
 * @since 4.7
 */
public final class EsQueryBuilder {

	private static final QueryBuilder MATCH_NONE = QueryBuilders.termQuery("match_none", "none");
	private static String ILLEGAL_STACK_STATE_MESSAGE = "Illegal internal stack state: %s";
	
	private final Deque<QueryBuilder> deque = Queues.newLinkedBlockingDeque();
	private final Map<String, Object> settings;
	private final DocumentMapping mapping;
	private final Logger log;
	private final String path;
	
	private boolean needsScoring;
	
	public EsQueryBuilder(DocumentMapping mapping, Map<String, Object> settings, Logger log) {
		this(mapping, settings, log, "");
	}
	
	private EsQueryBuilder(DocumentMapping mapping, Map<String, Object> settings, Logger log, String path) {
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
	
	public QueryBuilder build(Expression expression) {
		checkNotNull(expression, "expression");
		// always filter by type
		visit(expression);
		if (deque.size() == 1) {
			QueryBuilder queryBuilder = deque.pop();
			if (needsScoring) {
				return queryBuilder;
			} else {
				return QueryBuilders.boolQuery()
					.must(QueryBuilders.matchAllQuery())
					.filter(queryBuilder);
			}
		} else {
			throw newIllegalStateException();
		}
	}

	private void visit(Expression expression) {
		if (expression instanceof MatchAll) {
			deque.push(QueryBuilders.matchAllQuery());
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
		} else if (expression instanceof BoostPredicate) {
			visit((BoostPredicate) expression);
		} else if (expression instanceof ScriptScoreExpression) {
			visit((ScriptScoreExpression) expression);
		} else if (expression instanceof DecimalPredicate) {
			visit((DecimalPredicate) expression);
		} else if (expression instanceof DecimalRangePredicate) {
			visit((DecimalRangePredicate) expression);
		} else if (expression instanceof DecimalSetPredicate) {
			visit((DecimalSetPredicate) expression);
		} else if (expression instanceof ScriptQueryExpression){
			visit((ScriptQueryExpression)expression);
		} else {
			throw new IllegalArgumentException("Unexpected expression: " + expression);
		}
	}

	private void visit(ScriptQueryExpression expression) {
		deque.push(QueryBuilders.scriptQuery(expression.toEsScript(mapping)));
	}
	
	private void visit(ScriptScoreExpression expression) {
		final Expression inner = expression.expression();
		visit(inner);
		final QueryBuilder innerQuery = deque.pop();
		
		needsScoring = true;
		deque.push(QueryBuilders
				.functionScoreQuery(innerQuery, ScoreFunctionBuilders.scriptFunction(expression.toEsScript(mapping)))
				.boostMode(CombineFunction.REPLACE));
	}
	
	private void visit(BoolExpression bool) {
		final BoolQueryBuilder query = QueryBuilders.boolQuery();
		for (Expression must : bool.mustClauses()) {
			// visit the item and immediately pop the deque item back
			final EsQueryBuilder innerQueryBuilder = new EsQueryBuilder(mapping, settings, log);
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
			query.minimumShouldMatch(bool.minShouldMatch());
		}
		
		deque.push(query);
	}
	
	private void visit(NestedPredicate predicate) {
		final String nestedPath = toFieldPath(predicate);
		final DocumentMapping nestedMapping = mapping.getNestedMapping(predicate.getField());
		final EsQueryBuilder nestedQueryBuilder = new EsQueryBuilder(nestedMapping, settings, log, nestedPath);
		nestedQueryBuilder.visit(predicate.getExpression());
		needsScoring = nestedQueryBuilder.needsScoring;
		final QueryBuilder nestedQuery = nestedQueryBuilder.deque.pop();
		deque.push(QueryBuilders.nestedQuery(nestedPath, nestedQuery, ScoreMode.None));
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
		QueryBuilder query;
		switch (type) {
		case BOOLEAN_PREFIX:
			query = QueryBuilders.matchBoolPrefixQuery(field, term)
				.analyzer(predicate.analyzer())
				.operator(Operator.AND);
		case PHRASE:
			query = QueryBuilders.matchPhraseQuery(field, term)
						.analyzer(predicate.analyzer());
			break;
		case ALL:
			query = QueryBuilders.matchQuery(field, term)
						.analyzer(predicate.analyzer())
						.operator(Operator.AND);
			break;
		case ANY:
			query = QueryBuilders.matchQuery(field, term)
						.analyzer(predicate.analyzer())
						.operator(Operator.OR)
						.minimumShouldMatch(Integer.toString(minShouldMatch));
			break;
		case FUZZY:
			query = QueryBuilders.matchQuery(field, term)
						.analyzer(predicate.analyzer())
						.fuzziness(Fuzziness.ONE)
						.prefixLength(1)
						.operator(Operator.AND)
						.maxExpansions(10);
			break;
		case PARSED:
			query = QueryBuilders.queryStringQuery(TextConstants.escape(term))
						.analyzer(predicate.analyzer())
						.field(field)
						.escape(false)
						.allowLeadingWildcard(true)
						.defaultOperator(Operator.AND);
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
		deque.push(QueryBuilders.termQuery(toFieldPath(predicate), predicate.getArgument()));
	}
	
	private void visit(DecimalPredicate predicate) {
		deque.push(QueryBuilders.termQuery(toFieldPath(predicate), DecimalUtils.encode(predicate.getArgument())));
	}
	
	private <T> void visit(SetPredicate<T> predicate) {
		toTermsQuery(predicate, predicate.values(), null);
	}

	private void visit(DecimalSetPredicate predicate) {
		toTermsQuery(predicate, predicate.values(), DecimalUtils::encode);
	}
	
	// consider max terms count and break into multiple terms queries if number of terms are greater than that value
	private <T> void toTermsQuery(SetPredicate<T> predicate, final Set<T> terms, final Function<T, ?> valueConverter) {
		final int maxTermsCount = Integer.parseInt((String) settings.get(IndexClientFactory.MAX_TERMS_COUNT_KEY));
		if (terms.size() > maxTermsCount) {
			log.warn("More than currently configured max_terms_count ({}) filter values on field query: {}.{}", maxTermsCount, mapping.typeAsString(), toFieldPath(predicate));
			final BoolQueryBuilder bool = QueryBuilders.boolQuery().minimumShouldMatch(1);
			Iterables.partition(terms, maxTermsCount).forEach(partition -> {
				if (valueConverter != null) {
					bool.should(QueryBuilders.termsQuery(toFieldPath(predicate), partition.stream().map(valueConverter).collect(Collectors.toSet())));	
				} else {
					bool.should(QueryBuilders.termsQuery(toFieldPath(predicate), partition));
				}
			});
			deque.push(bool);
		} else {
			// push the terms query directly
			if (valueConverter != null) {
				deque.push(QueryBuilders.termsQuery(toFieldPath(predicate), terms.stream().map(valueConverter).collect(Collectors.toSet())));
			} else {
				deque.push(QueryBuilders.termsQuery(toFieldPath(predicate), terms));
			}
		}
	}
	
	private void visit(PrefixPredicate predicate) {
		deque.push(QueryBuilders.prefixQuery(toFieldPath(predicate), predicate.getArgument()));
	}
	
	private void visit(RegexpPredicate regexp) {
		deque.push(QueryBuilders.regexpQuery(toFieldPath(regexp), regexp.getArgument()));
	}
	
	private void visit(RangePredicate<?> range) {
		deque.push(QueryBuilders.rangeQuery(toFieldPath(range)).from(range.lower()).to(range.upper()).includeLower(range.isIncludeLower()).includeUpper(range.isIncludeUpper()));
	}
	
	private void visit(DecimalRangePredicate range) {
		final String lower = range.lower() == null ? null : DecimalUtils.encode(range.lower());
		final String upper = range.upper() == null ? null : DecimalUtils.encode(range.upper());
		deque.push(QueryBuilders.rangeQuery(toFieldPath(range)).from(lower).to(upper).includeLower(range.isIncludeLower()).includeUpper(range.isIncludeUpper()));
	}
	
	private void visit(DisMaxPredicate dismax) {
		DisMaxQueryBuilder dismaxBuilder = QueryBuilders.disMaxQuery();
		for (Expression disjunct : dismax.disjuncts()) {
			visit(disjunct);
			dismaxBuilder.add(deque.pop());
		}
		dismaxBuilder.tieBreaker(dismax.tieBreaker());
		deque.push(dismaxBuilder);
	}
	
	private void visit(BoostPredicate boost) {
		visit(boost.expression());
		QueryBuilder qb = deque.pop();
		qb.boost(boost.boost());
		deque.push(qb);
	}
	
}