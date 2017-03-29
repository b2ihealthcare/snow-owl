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
import static com.google.common.collect.Sets.newHashSetWithExpectedSize;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Set;

import org.apache.hadoop.hbase.util.Order;
import org.apache.hadoop.hbase.util.OrderedBytes;
import org.apache.hadoop.hbase.util.SimplePositionedMutableByteRange;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.queries.CustomScoreQuery;
import org.apache.lucene.queries.TermsQuery;
import org.apache.lucene.queries.function.FunctionQuery;
import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.queries.function.ValueSource;
import org.apache.lucene.queries.function.valuesource.BytesRefFieldSource;
import org.apache.lucene.queries.function.valuesource.DualFloatFunction;
import org.apache.lucene.queries.function.valuesource.FloatFieldSource;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.classic.QueryParser.Operator;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.BoostQuery;
import org.apache.lucene.search.ConstantScoreQuery;
import org.apache.lucene.search.DisjunctionMaxQuery;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.MatchNoDocsQuery;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.search.join.QueryBitSetProducer;
import org.apache.lucene.search.join.ScoreMode;
import org.apache.lucene.search.join.ToChildBlockJoinQuery;
import org.apache.lucene.search.join.ToParentBlockJoinQuery;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.QueryBuilder;
import org.apache.lucene.util.automaton.LevenshteinAutomata;

import com.b2international.commons.exceptions.FormattedRuntimeException;
import com.b2international.index.AnalyzerImpls;
import com.b2international.index.compat.Highlighting;
import com.b2international.index.compat.TextConstants;
import com.b2international.index.json.Index;
import com.b2international.index.json.JsonDocumentMapping;
import com.b2international.index.lucene.Fields;
import com.b2international.index.mapping.DocumentMapping;
import com.b2international.index.query.TextPredicate.MatchType;
import com.google.common.collect.Queues;

/**
 * @since 4.7
 */
public final class LuceneQueryBuilder {

	private static String ILLEGAL_STACK_STATE_MESSAGE = "Illegal internal stack state: %s";
	
	private final Deque<Query> deque = Queues.newLinkedBlockingDeque();
	private final DocumentMapping mapping;
	
	public LuceneQueryBuilder(DocumentMapping mapping) {
		this.mapping = checkNotNull(mapping, "mapping");
	}

	private FormattedRuntimeException newIllegalStateException() {
		return new FormattedRuntimeException(ILLEGAL_STACK_STATE_MESSAGE, deque);
	}
	
	public org.apache.lucene.search.Query build(Expression expression) {
		checkNotNull(expression, "expression");
		// always filter by type
		visit(Expressions.builder().must(mapping.matchType()).must(expression).build());
		if (deque.size() == 1) {
			return deque.pop();
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
		} else if (expression instanceof CustomScoreExpression) {
			visit((CustomScoreExpression) expression);
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
	
	private void visit(CustomScoreExpression expression) {
		final Expression inner = expression.expression();
		visit(inner);
		final Query innerQuery = deque.pop();
		final CustomScoreQuery query = new CustomScoreQuery(new ConstantScoreQuery(innerQuery), new FunctionQuery(visit(expression.func())));
		query.setStrict(expression.isStrict());
		deque.push(query);
	}
	
	private ValueSource visit(final ScoreFunction func) {
		if (func instanceof DualScoreFunction) {
			final DualScoreFunction<?, ?> f = (DualScoreFunction<?, ?>) func;
			final String firstFieldName = f.getFirst();
			final Class<?> firstFieldType = mapping.getField(firstFieldName).getType();
			final String secondFieldName = f.getSecond();
			final Class<?> secondFieldType = mapping.getField(secondFieldName).getType();
			// only this combination is supported at the moment
			if (String.class == firstFieldType && float.class == secondFieldType) {
				final DualScoreFunction<String, Float> function = (DualScoreFunction<String, Float>) func;
				return new DualFloatFunction(new BytesRefFieldSource(firstFieldName), new FloatFieldSource(secondFieldName)) {
					@Override
					protected String name() {
						return f.name();
					}
					
					@Override
					protected float func(int doc, FunctionValues aVals, FunctionValues bVals) {
						final String firstValue = aVals.strVal(doc);
						final float secondValue = bVals.floatVal(doc);
						return function.compute(firstValue, secondValue);
					}
				};
			}
		} else if (func instanceof FieldScoreFunction) {
			final String field = ((FieldScoreFunction) func).getField();
			final Class<?> fieldType = mapping.getField(field).getType();
			if (fieldType == float.class || fieldType == Float.class) {
				return new FloatFieldSource(field); 
			}
		}
		throw new UnsupportedOperationException("Not supported score function: " + func);
	}
	
	private void visit(BooleanPredicate predicate) {
		deque.push(Fields.boolField(predicate.getField()).createTermsFilter(Collections.singleton(predicate.getArgument())));
	}
	
	private void visit(BoolExpression bool) {
		final BooleanQuery.Builder query = new BooleanQuery.Builder();
		query.setDisableCoord(true);
		// first add the mustClauses, then the mustNotClauses, if there are no mustClauses but mustNot ones then add a match all before
		for (Expression must : bool.mustClauses()) {
			// visit the item and immediately pop the deque item back
			visit(must);
			query.add(deque.pop(), Occur.MUST);
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
		final Query parentFilter = JsonDocumentMapping.filterByType(mapping.typeAsString());
		final DocumentMapping nestedMapping = mapping.getNestedMapping(predicate.getField());
		final Query childFilter = JsonDocumentMapping.filterByType(nestedMapping.typeAsString());
		final Query innerQuery = new LuceneQueryBuilder(nestedMapping).build(predicate.getExpression());
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
		final Query parentQuery = new LuceneQueryBuilder(parentMapping).build(parentExpression);
		
		final Query parentFilter = JsonDocumentMapping.filterByType(parentMapping.typeAsString());
		
		final Query toChildQuery = new ToChildBlockJoinQuery(parentQuery, new QueryBitSetProducer(parentFilter));
		deque.push(toChildQuery);
	}

	private void visit(TextPredicate predicate) {
		final String field = predicate.getField();
		final String term = predicate.term();
		final MatchType type = predicate.type();
		final Analyzer analyzer = AnalyzerImpls.getAnalyzer(predicate.analyzer());
		Query query;
		switch (type) {
		case PHRASE:
			{
				final QueryBuilder queryBuilder = new QueryBuilder(analyzer);
				query = queryBuilder.createPhraseQuery(field, term);
			}
			break;
		case ALL:
			{
				final QueryBuilder queryBuilder = new QueryBuilder(analyzer);
				query = queryBuilder.createBooleanQuery(field, term, Occur.MUST);
			}
			break;
		case ANY:
			{
				final QueryBuilder queryBuilder = new QueryBuilder(analyzer);
				query = queryBuilder.createBooleanQuery(field, term, Occur.SHOULD);
			}
			break;
		case FUZZY:
			query = new FuzzyQuery(new Term(field, term), LevenshteinAutomata.MAXIMUM_SUPPORTED_DISTANCE, 1);
			break;
		case ALL_PREFIX:
			final List<String> prefixes = Highlighting.split(analyzer, term);
			final BooleanQuery.Builder q = new BooleanQuery.Builder();
			q.setDisableCoord(true);
			for (String prefix : prefixes) {
				q.add(new PrefixQuery(new Term(field, prefix)), Occur.MUST);
			}
			query = q.build();
			break;
		case PARSED:
			final QueryParser parser = new QueryParser(field, analyzer);
			parser.setDefaultOperator(Operator.AND);
			parser.setAllowLeadingWildcard(true);
			try {
				query = parser.parse(TextConstants.escape(term));
			} catch (ParseException e) {
				throw new QueryParseException(e.getMessage());
			}
			break;
		default: throw new UnsupportedOperationException("Unexpected text match type: " + type);
		}
		if (query == null) {
			query = new MatchNoDocsQuery();
		}
		deque.push(query);	
	}
	
	private void visit(StringPredicate predicate) {
		final Query filter = Fields.stringField(predicate.getField()).createTermsFilter(Collections.singleton(predicate.getArgument()));
		deque.push(filter);
	}
	
	private void visit(StringSetPredicate predicate) {
		final Query filter = Fields.stringField(predicate.getField()).createTermsFilter(predicate.values());
		deque.push(filter);
	}
	
	private void visit(IntSetPredicate predicate) {
		final Query filter = Fields.intField(predicate.getField()).createTermsFilter(predicate.values());
		deque.push(filter);
	}
	
	private void visit(LongSetPredicate predicate) {
		final Query filter = Fields.longField(predicate.getField()).createTermsFilter(predicate.values());
		deque.push(filter);
	}
	
	private void visit(DecimalSetPredicate predicate) {
		final Collection<BytesRef> terms = newHashSetWithExpectedSize(predicate.values().size());
		for (BigDecimal decimal : predicate.values()) {
			terms.add(encode(decimal));
		}
		final Query filter = new TermsQuery(predicate.getField(), terms);
		deque.push(filter);
	}
	
	private void visit(PrefixPredicate predicate) {
		final Query filter = new PrefixQuery(new Term(predicate.getField(), predicate.getArgument()));
		deque.push(filter);
	}
	
	private void visit(IntPredicate predicate) {
		final Query filter = Fields.intField(predicate.getField()).createTermsFilter(Collections.singleton(predicate.getArgument()));
		deque.push(filter);
	}
	
	private void visit(LongPredicate predicate) {
		final Query filter = Fields.longField(predicate.getField()).createTermsFilter(Collections.singleton(predicate.getArgument()));
		deque.push(filter);
	}
	
	private void visit(DecimalPredicate predicate) {
		final Set<BigDecimal> vals = Collections.singleton(predicate.getArgument());
		final Collection<BytesRef> terms = newHashSetWithExpectedSize(vals.size());
		for (BigDecimal decimal : vals) {
			terms.add(encode(decimal));
		}
		final Query filter = new TermsQuery(predicate.getField(), terms);
		deque.push(filter);
	}

	private void visit(LongRangePredicate range) {
		final Query filter = NumericRangeQuery.newLongRange(range.getField(), range.lower(), range.upper(), range.isIncludeLower(), range.isIncludeUpper());
		deque.push(filter);
	}
	
	private void visit(IntRangePredicate range) {
		final Query filter = NumericRangeQuery.newIntRange(range.getField(), range.lower(), range.upper(), range.isIncludeLower(), range.isIncludeUpper());
		deque.push(filter);
	}
	
	private void visit(StringRangePredicate range) {
		final Query filter = TermRangeQuery.newStringRange(range.getField(), range.lower(), range.upper(), range.isIncludeLower(), range.isIncludeUpper());
		deque.push(filter);
	}
	
	private void visit(DecimalRangePredicate range) {
		final BytesRef lower = range.lower() == null ? null : encode(range.lower());
		final BytesRef upper = range.upper() == null ? null : encode(range.upper());
		final Query filter = new TermRangeQuery(range.getField(), lower, upper, range.isIncludeLower(), range.isIncludeUpper());
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
	
	private BytesRef encode(BigDecimal val) {
		final SimplePositionedMutableByteRange dst = new SimplePositionedMutableByteRange(Index.PRECISION);
		final int writtenBytes = OrderedBytes.encodeNumeric(dst, val, Order.ASCENDING);
		return new BytesRef(dst.getBytes(), 0, writtenBytes);
	}
	
}