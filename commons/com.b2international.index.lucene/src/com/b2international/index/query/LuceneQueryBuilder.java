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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSetWithExpectedSize;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Deque;
import java.util.List;
import java.util.Map;

import org.apache.lucene.document.IntPoint;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.index.Term;
import org.apache.lucene.queries.CustomScoreQuery;
import org.apache.lucene.queries.function.FunctionQuery;
import org.apache.lucene.queries.function.ValueSource;
import org.apache.lucene.queries.function.valuesource.BytesRefFieldSource;
import org.apache.lucene.queries.function.valuesource.FloatFieldSource;
import org.apache.lucene.queries.function.valuesource.QueryValueSource;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.classic.QueryParser.Operator;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.BoostQuery;
import org.apache.lucene.search.ConstantScoreQuery;
import org.apache.lucene.search.DisjunctionMaxQuery;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.MatchNoDocsQuery;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.RegexpQuery;
import org.apache.lucene.search.TermInSetQuery;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.search.join.QueryBitSetProducer;
import org.apache.lucene.search.join.ScoreMode;
import org.apache.lucene.search.join.ToChildBlockJoinQuery;
import org.apache.lucene.search.join.ToParentBlockJoinQuery;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.QueryBuilder;
import org.apache.lucene.util.automaton.RegExp;

import com.b2international.commons.exceptions.FormattedRuntimeException;
import com.b2international.index.Script;
import com.b2international.index.ScriptEngine;
import com.b2international.index.compat.TextConstants;
import com.b2international.index.json.JsonDocumentMapping;
import com.b2international.index.lucene.Fields;
import com.b2international.index.mapping.DocumentMapping;
import com.b2international.index.query.TextPredicate.MatchType;
import com.b2international.index.util.DecimalUtils;
import com.b2international.index.util.NumericClassUtils;
import com.google.common.collect.Queues;

/**
 * @since 4.7
 */
public final class LuceneQueryBuilder {

	private static String ILLEGAL_STACK_STATE_MESSAGE = "Illegal internal stack state: %s";
	
	private final Deque<Query> deque = Queues.newLinkedBlockingDeque();
	private final DocumentMapping mapping;
	private final QueryBuilder luceneQueryBuilder;
	
	/**
	 * Set to {@code true} if at least one predicate is seen that affects query scoring.
	 */
	private boolean needsScoring;

	private final ScriptEngine scriptEngine;
	
	public LuceneQueryBuilder(DocumentMapping mapping, QueryBuilder luceneQueryBuilder, ScriptEngine scriptEngine) {
		this.luceneQueryBuilder = luceneQueryBuilder;
		this.scriptEngine = scriptEngine;
		this.mapping = checkNotNull(mapping, "mapping");
	}

	private FormattedRuntimeException newIllegalStateException() {
		return new FormattedRuntimeException(ILLEGAL_STACK_STATE_MESSAGE, deque);
	}
	
	public org.apache.lucene.search.Query build(Expression expression) {
		checkNotNull(expression, "expression");
		visit(expression);
		
		if (deque.size() == 1) {
			Query convertedQuery = deque.pop();
			BooleanQuery.Builder builder = new BooleanQuery.Builder();
			
			if (convertedQuery instanceof BooleanQuery) {
				BooleanQuery booleanQuery = (BooleanQuery) convertedQuery;
				
				for (BooleanClause clause : booleanQuery.clauses()) {
					builder.add(clause);
				}
				builder.setDisableCoord(booleanQuery.isCoordDisabled());
				builder.setMinimumNumberShouldMatch(booleanQuery.getMinimumNumberShouldMatch());
			} else {
				if (needsScoring) {
					builder.add(convertedQuery, Occur.MUST);
				} else {
					builder.add(new MatchAllDocsQuery(), Occur.MUST);
					builder.add(convertedQuery, Occur.FILTER);
				}
				builder.setDisableCoord(true);
			}
			builder.add(JsonDocumentMapping.matchType(mapping.typeAsString()), Occur.FILTER);
			return builder.build();
		} else {
			throw newIllegalStateException();
		}
	}

	private void visit(Expression expression) {
		if (expression instanceof MatchAll) {
			deque.push(new MatchAllDocsQuery());
		} else if (expression instanceof MatchNone) {
			deque.push(new MatchNoDocsQuery());
		} else if (expression instanceof StringPredicate) {
			StringPredicate predicate = (StringPredicate) expression;
			visit(predicate);
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
		final Script scoreScript = mapping.getScript(expression.scriptName());
		final Expression inner = expression.expression();
		visit(inner);
		final Query innerQuery = deque.pop();
		final CustomScoreQuery query = new CustomScoreQuery(
			new ConstantScoreQuery(innerQuery), 
			new FunctionQuery(visit(innerQuery, scoreScript, expression.getParams()))
		);
		query.setStrict(true);
		needsScoring = true;
		deque.push(query);
	}
	
	private ValueSource visit(final Query inner, final Script script, final Map<String, ? extends Object> scriptParams) {
		Map<String, ValueSource> valueSources = newHashMap();
		valueSources.put("_score", new QueryValueSource(inner, 0.0f));
		for (String field : script.fields()) {
			final Class<?> fieldType = mapping.getFieldType(field);
			if (String.class.isAssignableFrom(fieldType)) {
				valueSources.put(field, new BytesRefFieldSource(field));
			} else if (NumericClassUtils.isFloat(fieldType)) {
				valueSources.put(field, new FloatFieldSource(field));
			} else {
				throw new UnsupportedOperationException("Unsupported field type in custom score script " + script.name() + " - " + field);
			}
		}
		
		return new CustomScoreValueSource(script.script(), scriptParams, valueSources, scriptEngine);
	}
	
	private void visit(BooleanPredicate predicate) {
		deque.push(Fields.boolField(predicate.getField()).toQuery(predicate.getArgument()));
	}
	
	private void visit(BoolExpression bool) {
		final BooleanQuery.Builder query = new BooleanQuery.Builder();
		query.setDisableCoord(true);
		// first add the mustClauses, then the mustNotClauses, if there are no mustClauses but mustNot ones then add a match all before
		for (Expression must : bool.mustClauses()) {
			// visit the item and immediately pop the deque item back
			LuceneQueryBuilder innerQueryBuilder = new LuceneQueryBuilder(mapping, luceneQueryBuilder, scriptEngine);
			innerQueryBuilder.visit(must);
			
			if (innerQueryBuilder.needsScoring) {
				needsScoring = innerQueryBuilder.needsScoring;
				query.add(innerQueryBuilder.deque.pop(), Occur.MUST);
			} else {
				query.add(innerQueryBuilder.deque.pop(), Occur.FILTER);
			}
		}
		
		for (Expression mustNot : bool.mustNotClauses()) {
			visit(mustNot);
			query.add(deque.pop(), Occur.MUST_NOT);
		}
		
		for (Expression should : bool.shouldClauses()) {
			visit(should);
			query.add(deque.pop(), Occur.SHOULD);
		}
		
		for (Expression filter : bool.filterClauses()) {
			visit(filter);
			query.add(deque.pop(), Occur.FILTER);
		}
		
		if (!bool.shouldClauses().isEmpty()) {
			query.setMinimumNumberShouldMatch(bool.minShouldMatch());
		}
		
		deque.push(query.build());
	}
	
	private void visit(NestedPredicate predicate) {
		final Query parentFilter = JsonDocumentMapping.matchType(mapping.typeAsString());
		final DocumentMapping nestedMapping = mapping.getNestedMapping(predicate.getField());
		final Query childFilter = JsonDocumentMapping.matchType(nestedMapping.typeAsString());
		final Query innerQuery = new LuceneQueryBuilder(nestedMapping, luceneQueryBuilder, scriptEngine).build(predicate.getExpression());
		final Query childQuery = new BooleanQuery.Builder()
										.add(innerQuery, Occur.MUST)
										.add(childFilter, Occur.FILTER)
										.build();
		// TODO scoring???
		final Query nestedQuery = new ToParentBlockJoinQuery(childQuery, new QueryBitSetProducer(parentFilter), ScoreMode.None);
		deque.push(nestedQuery);
	}
	
	private void visit(HasParentPredicate predicate) {
		final Expression parentExpression = predicate.getExpression();
		final Class<?> parentType = predicate.getParentType();
		
		final DocumentMapping parentMapping = mapping.getParent();
		checkArgument(parentMapping.type() == parentType, "Unexpected parent type. %s vs. %s", parentMapping.type(), parentType);
		final Query parentQuery = new LuceneQueryBuilder(parentMapping, luceneQueryBuilder, scriptEngine).build(parentExpression);
		
		final Query parentFilter = JsonDocumentMapping.matchType(parentMapping.typeAsString());
		
		final Query toChildQuery = new ToChildBlockJoinQuery(parentQuery, new QueryBitSetProducer(parentFilter));
		deque.push(toChildQuery);
	}

	private void visit(TextPredicate predicate) {
		final String field = predicate.getField();
		final String term = predicate.term();
		final MatchType type = predicate.type();
		Query query;
		switch (type) {
		case PHRASE:
			{
				query = luceneQueryBuilder.createPhraseQuery(field, term);
			}
			break;
		case ALL:
			{
				query = luceneQueryBuilder.createBooleanQuery(field, term, Occur.MUST);
			}
			break;
		case ANY:
			{
				query = luceneQueryBuilder.createBooleanQuery(field, term, Occur.SHOULD);
			}
			break;
		case FUZZY:
			query = new FuzzyQuery(new Term(field, term), 1, 1);
			break;
		case PARSED:
			final QueryParser parser = new QueryParser(field, luceneQueryBuilder.getAnalyzer());
			parser.setDefaultOperator(Operator.AND);
			parser.setAllowLeadingWildcard(true);
			try {
				query = parser.parse(TextConstants.escape(term));
			} catch (ParseException e) {
				throw new QueryParseException(e.getMessage());
			}
			break;
		case REGEXP:
			query = new RegexpQuery(new Term(field, term), RegExp.ALL, 10_000); 
			break;
		default: throw new UnsupportedOperationException("Unexpected text match type: " + type);
		}
		
		if (query == null) {
			query = new MatchNoDocsQuery();
		} else {
			needsScoring = true;
		}
		
		deque.push(query);	
	}
	
	private void visit(StringPredicate predicate) {
		final Query filter = Fields.stringField(predicate.getField()).toQuery(predicate.getArgument());
		deque.push(filter);
	}
	
	private void visit(StringSetPredicate predicate) {
		final Query filter = Fields.stringField(predicate.getField()).toQuery(predicate.values());
		deque.push(filter);
	}
	
	private void visit(IntSetPredicate predicate) {
		final Query filter = Fields.intField(predicate.getField()).toQuery(predicate.values());
		deque.push(filter);
	}
	
	private void visit(LongSetPredicate predicate) {
		final Query filter = Fields.longField(predicate.getField()).toQuery(predicate.values());
		deque.push(filter);
	}
	
	private void visit(DecimalSetPredicate predicate) {
		final Collection<BytesRef> terms = newHashSetWithExpectedSize(predicate.values().size());
		for (BigDecimal decimal : predicate.values()) {
			terms.add(new BytesRef(DecimalUtils.encode(decimal)));
		}
		final Query filter = new TermInSetQuery(predicate.getField(), terms);
		deque.push(filter);
	}
	
	private void visit(PrefixPredicate predicate) {
		final Query filter = new PrefixQuery(new Term(predicate.getField(), predicate.getArgument()));
		deque.push(filter);
	}
	
	private void visit(IntPredicate predicate) {
		final Query filter = Fields.intField(predicate.getField()).toQuery(predicate.getArgument());
		deque.push(filter);
	}
	
	private void visit(LongPredicate predicate) {
		final Query filter = Fields.longField(predicate.getField()).toQuery(predicate.getArgument());
		deque.push(filter);
	}
	
	private void visit(DecimalPredicate predicate) {
		final Query filter = Fields.stringField(predicate.getField()).toQuery(DecimalUtils.encode(predicate.getArgument()));
		deque.push(filter);
	}

	private void visit(LongRangePredicate range) {
		final long lower;
		if (range.lower() == null) {
			lower = Long.MIN_VALUE;
		} else {
			lower = range.isIncludeLower() ? range.lower() : Math.addExact(range.lower(), 1L);
		}
		final long upper;
		if (range.upper() == null) {
			upper = Long.MAX_VALUE;
		} else {
			upper = range.isIncludeUpper() ? range.upper() : Math.subtractExact(range.upper(), 1L);
		}
		final Query filter = LongPoint.newRangeQuery(range.getField(), lower, upper);
		deque.push(filter);
	}
	
	private void visit(IntRangePredicate range) {
		final int lower;
		if (range.lower() == null) {
			lower = Integer.MIN_VALUE;
		} else {
			lower = range.isIncludeLower() ? range.lower() : Math.addExact(range.lower(), 1);
		}
		final int upper;
		if (range.upper() == null) {
			upper = Integer.MAX_VALUE;
		} else {
			upper = range.isIncludeUpper() ? range.upper() : Math.subtractExact(range.upper(), 1);
		}
		final Query filter = IntPoint.newRangeQuery(range.getField(), lower, upper);
		deque.push(filter);
	}
	
	private void visit(StringRangePredicate range) {
		final Query filter = TermRangeQuery.newStringRange(range.getField(), range.lower(), range.upper(), range.isIncludeLower(), range.isIncludeUpper());
		deque.push(filter);
	}
	
	private void visit(DecimalRangePredicate range) {
		final String lower = range.lower() == null ? null : DecimalUtils.encode(range.lower());
		final String upper = range.upper() == null ? null : DecimalUtils.encode(range.upper());
		final Query filter = TermRangeQuery.newStringRange(range.getField(), lower, upper, range.isIncludeLower(), range.isIncludeUpper());
		deque.push(filter);
	}
	
	private void visit(DisMaxPredicate dismax) {
		final List<Query> disjuncts = newArrayList();
		for (Expression disjunct : dismax.disjuncts()) {
			visit(disjunct);
			disjuncts.add(deque.pop());
		}
		deque.push(new DisjunctionMaxQuery(disjuncts, dismax.tieBreaker()));
	}
	
	private void visit(BoostPredicate boost) {
		visit(boost.expression());
		deque.push(new BoostQuery(deque.pop(), boost.boost()));
	}
	
}