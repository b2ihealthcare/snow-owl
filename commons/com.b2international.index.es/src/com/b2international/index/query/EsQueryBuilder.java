/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Sets.newHashSetWithExpectedSize;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Deque;

import org.elasticsearch.common.lucene.search.function.CombineFunction;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.DisMaxQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder.Operator;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.elasticsearch.script.ScriptService.ScriptType;

import com.b2international.commons.exceptions.FormattedRuntimeException;
import com.b2international.index.compat.TextConstants;
import com.b2international.index.mapping.DocumentMapping;
import com.b2international.index.query.TextPredicate.MatchType;
import com.b2international.index.util.DecimalUtils;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Queues;

/**
 * @since 4.7
 */
public final class EsQueryBuilder {

	private static final QueryBuilder MATCH_NONE = QueryBuilders.termQuery("match_none", "none");
	private static String ILLEGAL_STACK_STATE_MESSAGE = "Illegal internal stack state: %s";
	
	private final Deque<QueryBuilder> deque = Queues.newLinkedBlockingDeque();
	private final DocumentMapping mapping;
	private final String path;
	
	private boolean needsScoring;
	
	public EsQueryBuilder(DocumentMapping mapping) {
		this(mapping, "");
	}
	
	private EsQueryBuilder(DocumentMapping mapping, String path) {
		this.mapping = mapping;
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
					.disableCoord(true)
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
		} else {
			throw new IllegalArgumentException("Unexpected expression: " + expression);
		}
	}
	
	private void visit(ScriptScoreExpression expression) {
		final Expression inner = expression.expression();
		visit(inner);
		final QueryBuilder innerQuery = deque.pop();
		
		final String rawScript = mapping.getScript(expression.scriptName()).script();
		org.elasticsearch.script.Script script = new org.elasticsearch.script.Script(rawScript, ScriptType.INLINE, "groovy", ImmutableMap.of("params", expression.getParams()));
		needsScoring = true;
		deque.push(QueryBuilders
				.functionScoreQuery(innerQuery, ScoreFunctionBuilders.scriptFunction(script))
				.boostMode(CombineFunction.REPLACE));
	}
	
	private void visit(BoolExpression bool) {
		final BoolQueryBuilder query = QueryBuilders.boolQuery();
		query.disableCoord(true);
		for (Expression must : bool.mustClauses()) {
			// visit the item and immediately pop the deque item back
			final EsQueryBuilder innerQueryBuilder = new EsQueryBuilder(mapping);
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
			query.minimumNumberShouldMatch(bool.minShouldMatch());
		}
		
		deque.push(query);
	}
	
	private void visit(NestedPredicate predicate) {
		final String nestedPath = toFieldPath(predicate);
		final DocumentMapping nestedMapping = mapping.getNestedMapping(predicate.getField());
		final EsQueryBuilder nestedQueryBuilder = new EsQueryBuilder(nestedMapping, nestedPath);
		nestedQueryBuilder.visit(predicate.getExpression());
		needsScoring = nestedQueryBuilder.needsScoring;
		final QueryBuilder nestedQuery = nestedQueryBuilder.deque.pop();
		deque.push(QueryBuilders.nestedQuery(nestedPath, nestedQuery));
	}

	private String toFieldPath(Predicate predicate) {
		return toFieldPath(predicate.getField());
	}

	private String toFieldPath(final String subPath) {
		return path.isEmpty() ? subPath : String.format("%s.%s", path, subPath);
	}
	
	private void visit(HasParentPredicate predicate) {
		throw new UnsupportedOperationException();
	}

	private void visit(TextPredicate predicate) {
		final String field = toFieldPath(predicate);
		final String term = predicate.term();
		final MatchType type = predicate.type();
		QueryBuilder query;
		switch (type) {
		case PHRASE:
			{
				query = QueryBuilders.matchPhraseQuery(field, term);
			}
			break;
		case ALL:
			{
				query = QueryBuilders.matchQuery(field, term).operator(org.elasticsearch.index.query.MatchQueryBuilder.Operator.AND);
			}
			break;
		case ANY:
			{
				query = QueryBuilders.matchQuery(field, term).operator(org.elasticsearch.index.query.MatchQueryBuilder.Operator.OR);
			}
			break;
		case FUZZY:
			query = QueryBuilders.fuzzyQuery(field, term).fuzziness(Fuzziness.ONE).prefixLength(1);
			break;
		case PARSED:
			query = QueryBuilders.queryStringQuery(TextConstants.escape(term)).allowLeadingWildcard(true).defaultOperator(Operator.AND);
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
	
	private void visit(SetPredicate<?> predicate) {
		deque.push(QueryBuilders.termsQuery(toFieldPath(predicate), predicate.values()));
	}
	
	private void visit(DecimalPredicate predicate) {
		deque.push(QueryBuilders.termQuery(toFieldPath(predicate), DecimalUtils.encode(predicate.getArgument())));
	}
	
	private void visit(DecimalSetPredicate predicate) {
		final Collection<String> terms = newHashSetWithExpectedSize(predicate.values().size());
		for (BigDecimal decimal : predicate.values()) {
			terms.add(DecimalUtils.encode(decimal));
		}
		deque.push(QueryBuilders.termsQuery(toFieldPath(predicate), terms));
	}
	
	private void visit(PrefixPredicate predicate) {
		deque.push(QueryBuilders.prefixQuery(toFieldPath(predicate), predicate.getArgument()));
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
		deque.push(QueryBuilders.boostingQuery().boost(boost.boost()).positive(deque.pop()));
	}
	
}