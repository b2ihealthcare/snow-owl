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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;

import java.util.Collections;
import java.util.Deque;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.classic.QueryParser.Operator;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.ConstantScoreQuery;
import org.apache.lucene.search.DisjunctionMaxQuery;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.FilteredQuery;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.NumericRangeFilter;
import org.apache.lucene.search.PrefixFilter;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermRangeFilter;
import org.apache.lucene.search.join.ScoreMode;
import org.apache.lucene.search.join.ToChildBlockJoinQuery;
import org.apache.lucene.search.join.ToParentBlockJoinQuery;
import org.apache.lucene.util.QueryBuilder;
import org.apache.lucene.util.Version;
import org.apache.lucene.util.automaton.LevenshteinAutomata;

import com.b2international.commons.exceptions.FormattedRuntimeException;
import com.b2international.index.AnalyzerImpls;
import com.b2international.index.compat.Highlighting;
import com.b2international.index.compat.TextConstants;
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
	
	private final class DequeItem {
		private final Object query;
		
		DequeItem(org.apache.lucene.search.Query query) {
			this.query = query;
		}
		
		DequeItem(Filter filter) {
			this.query = filter;
		}

		@Override
		public String toString() {
			return query.toString();
		}

		public boolean isFilter() {
			return query instanceof Filter;
		}
		
		public boolean isQuery() {
			return query instanceof Query;
		}
		
		public Filter getFilter() {
			return (Filter) query;
		}
		
		public Query getQuery() {
			return (Query) query;
		}
		
		public Query toQuery() {
			if (isQuery()) {
				return getQuery();
			} else if (isFilter()) {
				return new ConstantScoreQuery(getFilter());
			} else {
				throw newIllegalStateException();
			}
		}

	}
	
	private final Deque<DequeItem> deque = Queues.newLinkedBlockingDeque();
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
			return deque.pop().toQuery();
		} else {
			throw newIllegalStateException();
		}
	}

	private void visit(Expression expression) {
		if (expression instanceof MatchAll) {
			deque.push(new DequeItem(new MatchAllDocsQuery()));
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
		} else {
			throw new IllegalArgumentException("Unexpected expression: " + expression);
		}
	}
	
	private void visit(BooleanPredicate predicate) {
		deque.push(new DequeItem(Fields.boolField(predicate.getField()).createTermsFilter(Collections.singleton(predicate.getArgument()))));
	}
	
	private void visit(BoolExpression bool) {
		final BooleanQuery query = new BooleanQuery();
		// first add the mustClauses, then the mustNotClauses, if there are no mustClauses but mustNot ones then add a match all before
		for (Expression must : bool.mustClauses()) {
			// visit the item and immediately pop the deque item back
			visit(must);
			final DequeItem item = deque.pop();
			query.add(item.toQuery(), Occur.MUST);
		}
		
		for (Expression mustNot : bool.mustNotClauses()) {
			visit(mustNot);
			final DequeItem item = deque.pop();
			query.add(item.toQuery(), Occur.MUST_NOT);
		}
		
		for (Expression should : bool.shouldClauses()) {
			visit(should);
			final DequeItem item = deque.pop();
			query.add(item.toQuery(), Occur.SHOULD);
		}
		
		if (!bool.shouldClauses().isEmpty()) {
			query.setMinimumNumberShouldMatch(query.getMinimumNumberShouldMatch());
		}
		
		deque.push(new DequeItem(query));
	}
	
	private void visit(NestedPredicate predicate) {
		final Filter parentFilter = JsonDocumentMapping.filterByType(mapping.typeAsString());
		final DocumentMapping nestedMapping = mapping.getNestedMapping(predicate.getField());
		final Filter childFilter = JsonDocumentMapping.filterByType(nestedMapping.typeAsString());
		final Query innerQuery = new LuceneQueryBuilder(nestedMapping).build(predicate.getExpression());
		final Query childQuery = new FilteredQuery(innerQuery, childFilter);
		// TODO scoring???
		final Query nestedQuery = new ToParentBlockJoinQuery(childQuery, parentFilter, ScoreMode.None);
		deque.push(new DequeItem(nestedQuery));
	}
	
	private void visit(HasParentPredicate predicate) {
		final Expression parentExpression = predicate.getExpression();
		final Class<?> parentType = predicate.getParentType();
		
		final DocumentMapping parentMapping = mapping.getParent();
		checkArgument(parentMapping.type() == parentType, "Unexpected parent type. %s vs. %s", parentMapping.type(), parentType);
		final Query parentQuery = new LuceneQueryBuilder(parentMapping).build(parentExpression);
		
		final Query toChildQuery = new ToChildBlockJoinQuery(parentQuery, JsonDocumentMapping.filterByType(parentMapping.typeAsString()), false);
		deque.push(new DequeItem(toChildQuery));
	}

	private void visit(TextPredicate predicate) {
		final String field = predicate.getField();
		final String term = predicate.term();
		final MatchType type = predicate.type();
		final Analyzer analyzer = AnalyzerImpls.getAnalyzer(predicate.analyzer());
		final Query query;
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
			final BooleanQuery q = new BooleanQuery(true);
			for (String prefix : prefixes) {
				q.add(new PrefixQuery(new Term(field, prefix)), Occur.MUST);
			}
			query = q;
			break;
		case PARSED:
			final QueryParser parser = new QueryParser(Version.LUCENE_4_9, field, analyzer);
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
		deque.push(new DequeItem(query));		
	}
	
	private void visit(StringPredicate predicate) {
		final Filter filter = Fields.stringField(predicate.getField()).createTermsFilter(Collections.singleton(predicate.getArgument()));
		deque.push(new DequeItem(filter));
	}
	
	private void visit(StringSetPredicate predicate) {
		final Filter filter = Fields.stringField(predicate.getField()).createTermsFilter(predicate.values());
		deque.push(new DequeItem(filter));
	}
	
	private void visit(IntSetPredicate predicate) {
		final Filter filter = Fields.intField(predicate.getField()).createTermsFilter(predicate.values());
		deque.push(new DequeItem(filter));
	}
	
	private void visit(LongSetPredicate predicate) {
		final Filter filter = Fields.longField(predicate.getField()).createTermsFilter(predicate.values());
		deque.push(new DequeItem(filter));
	}
	
	private void visit(PrefixPredicate predicate) {
		final Filter filter = new PrefixFilter(new Term(predicate.getField(), predicate.getArgument()));
		deque.push(new DequeItem(filter));
	}
	
	private void visit(IntPredicate predicate) {
		final Filter filter = Fields.intField(predicate.getField()).createTermsFilter(Collections.singleton(predicate.getArgument()));
		deque.push(new DequeItem(filter));
	}
	
	private void visit(LongPredicate predicate) {
		final Filter filter = Fields.longField(predicate.getField()).createTermsFilter(Collections.singleton(predicate.getArgument()));
		deque.push(new DequeItem(filter));
	}
	
	private void visit(LongRangePredicate range) {
		final Filter filter = NumericRangeFilter.newLongRange(range.getField(), range.from(), range.to(), true, true);
		deque.push(new DequeItem(filter));
	}
	
	private void visit(IntRangePredicate range) {
		final Filter filter = NumericRangeFilter.newIntRange(range.getField(), range.from(), range.to(), true, true);
		deque.push(new DequeItem(filter));
	}
	
	private void visit(StringRangePredicate range) {
		final Filter filter = TermRangeFilter.newStringRange(range.getField(), range.from(), range.to(), true, true);
		deque.push(new DequeItem(filter));
	}
	
	private void visit(DisMaxPredicate dismax) {
		final List<Query> disjuncts = newArrayList();
		for (Expression disjunct : dismax.disjuncts()) {
			visit(disjunct);
			disjuncts.add(deque.pop().toQuery());
		}
		deque.push(new DequeItem(new DisjunctionMaxQuery(disjuncts, dismax.tieBreaker())));
	}
	
	private void visit(BoostPredicate boost) {
		visit(boost.expression());
		final Query query = deque.pop().toQuery();
		query.setBoost(boost.boost());
		deque.push(new DequeItem(query));
	}
	
}