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

import java.util.Collections;
import java.util.Deque;

import org.apache.lucene.index.Term;
import org.apache.lucene.queries.BooleanFilter;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.ConstantScoreQuery;
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
import org.apache.lucene.util.automaton.LevenshteinAutomata;

import com.b2international.commons.exceptions.FormattedRuntimeException;
import com.b2international.index.json.JsonDocumentMapping;
import com.b2international.index.lucene.Fields;
import com.b2international.index.mapping.DocumentMapping;
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
		traversePostOrder(Expressions.and(mapping.matchType(), expression));
		if (deque.size() == 1) {
			return deque.pop().toQuery();
		} else {
			throw newIllegalStateException();
		}
	}

	private void visit(Expression expression) {
		if (expression instanceof MatchAll) {
			deque.push(new DequeItem(new MatchAllDocsQuery()));
		} else if (expression instanceof And) {
			visit((And) expression);
		} else if (expression instanceof Or) {
			visit((Or) expression);
		} else if (expression instanceof AndNot) {
			visit((AndNot) expression);
		} else if (expression instanceof StringPredicate) {
			StringPredicate predicate = (StringPredicate) expression;
			visit(predicate);
		} else if (expression instanceof LongPredicate) {
			visit((LongPredicate) expression);
		} else if (expression instanceof RangePredicate) {
			visit((RangePredicate) expression);
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
		} else if (expression instanceof IntSetPredicate) {
			visit((IntSetPredicate) expression);
		} else if (expression instanceof PrefixTextPredicate) {
			visit((PrefixTextPredicate) expression);
		} else if (expression instanceof FuzzyTextPredicate) {
			visit((FuzzyTextPredicate) expression);
		} else if (expression instanceof BoolExpression) {
			visit((BoolExpression) expression);
		} else if (expression instanceof BooleanPredicate) {
			visit((BooleanPredicate) expression);
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

	private void visit(FuzzyTextPredicate predicate) {
		final FuzzyQuery query = new FuzzyQuery(new Term(predicate.getField(), predicate.term()), LevenshteinAutomata.MAXIMUM_SUPPORTED_DISTANCE, 1);
		deque.push(new DequeItem(query));
	}
	
	private void visit(PrefixTextPredicate predicate) {
		final Query query = new PrefixQuery(new Term(predicate.getField(), predicate.prefix()));
		deque.push(new DequeItem(query));
	}
	
//	private void visit(TextPredicate predicate) {
//		Feature feature = predicate.getFeature();
//		Operator operator = predicate.getOperator();
//		switch (operator) {
//		case ALL:
//			QueryBuilder queryBuilder = QueryBuilders.matchQuery(feature.getField(), predicate.getText()).operator(MatchQueryBuilder.Operator.AND);
//			deque.push(new DequeItem(queryBuilder));
//			break;
//		case ANY:
//			queryBuilder = QueryBuilders.matchQuery(feature.getField(), predicate.getText()).operator(MatchQueryBuilder.Operator.OR);
//			deque.push(new DequeItem(queryBuilder));
//			break;
//		case EXACT:
//			queryBuilder = QueryBuilders.matchQuery(feature.getField(), predicate.getText()).type(MatchQueryBuilder.Type.PHRASE);
//			deque.push(new DequeItem(queryBuilder));
//			break;
//		case NONE:
//			queryBuilder = QueryBuilders.boolQuery().mustNot(
//					QueryBuilders.matchQuery(feature.getField(), predicate.getText()).operator(MatchQueryBuilder.Operator.OR));
//			deque.push(new DequeItem(queryBuilder));
//			break;
//		default:
//			throw new IllegalArgumentException("Unexpected operator: " + operator);
//		}
//	}
	
//	private void visit(StringSetPredicate predicate) {
//		Feature feature = predicate.getFeature();
//		FilterBuilder filter = FilterBuilders.termsFilter(feature.getField(), predicate.getArgument());
//		deque.push(new DequeItem(filter));
//	}
	
//	private void visit(BooleanPredicate predicate) {
//		Feature feature = predicate.getFeature();
//		FilterBuilder filter = FilterBuilders.termFilter(feature.getField(), predicate.getArgument());
//		deque.push(new DequeItem(filter));
//	}

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
	
	private void visit(LongPredicate predicate) {
		final Filter filter = Fields.longField(predicate.getField()).createTermsFilter(Collections.singleton(predicate.getArgument()));
		deque.push(new DequeItem(filter));
	}
	
//	private void visit(DateRangePredicate predicate) {
//		Feature feature = predicate.getFeature();
//		RangeFilterBuilder filter = FilterBuilders.rangeFilter(feature.getField());
//		if (predicate.getStart().isPresent()) {
//			filter.from(predicate.getStart().get()).includeLower(predicate.isStartInclusive());
//		}
//		if (predicate.getEnd().isPresent()) {
//			filter.to(predicate.getEnd().get()).includeUpper(predicate.isEndInclusive());
//		}
//		deque.push(new DequeItem(filter));
//	}
	
	private void visit(RangePredicate range) {
		final Filter filter = NumericRangeFilter.newLongRange(range.getField(), range.from(), range.to(), true, true);
		deque.push(new DequeItem(filter));
	}
	
	private void visit(StringRangePredicate range) {
		final Filter filter = TermRangeFilter.newStringRange(range.getField(), range.from(), range.to(), false, false);
		deque.push(new DequeItem(filter));
	}
	
	private void visit(And and) {
		if (and.getRight().isPresent() && deque.size() >= 2) {
			DequeItem right = deque.pop();
			DequeItem left = deque.pop();
			if (right.isFilter() && left.isFilter()) {
				final BooleanFilter filter = new BooleanFilter();
				filter.add(left.getFilter(), Occur.MUST);
				filter.add(right.getFilter(), Occur.MUST);
				deque.push(new DequeItem(filter));
			} else {
				final Query query = Fields.newQuery().and(left.toQuery()).and(right.toQuery()).matchAll();
				deque.push(new DequeItem(query));
			}
		} else if (deque.size() >= 1) {
			DequeItem item = deque.pop();
			if (item.isFilter()) {
				deque.push(new DequeItem(item.getFilter()));
			} else if (item.isQuery()) {
				deque.push(new DequeItem(item.getQuery()));
			} else {
				throw newIllegalStateException();
			}
		} else {
			throw newIllegalStateException();
		}
	}
	
	private void visit(AndNot and) {
		if (and.getRight().isPresent() && deque.size() >= 2) {
			DequeItem right = deque.pop();
			DequeItem left = deque.pop();
			if (right.isFilter() && left.isFilter()) {
				final BooleanFilter filter = new BooleanFilter();
				filter.add(left.getFilter(), Occur.MUST);
				filter.add(right.getFilter(), Occur.MUST_NOT);
				deque.push(new DequeItem(filter));
			} else {
				final BooleanQuery query = new BooleanQuery();
				query.add(left.toQuery(), Occur.MUST);
				query.add(right.toQuery(), Occur.MUST_NOT);
				deque.push(new DequeItem(query));
			}
		} else if (deque.size() >= 1) {
			DequeItem item = deque.pop();
			if (item.isFilter()) {
				deque.push(new DequeItem(item.getFilter()));
			} else if (item.isQuery()) {
				deque.push(new DequeItem(item.getQuery()));
			} else {
				throw newIllegalStateException();
			}
		} else {
			throw newIllegalStateException();
		}
	}

	private void visit(Or or) {
		if (or.getRight().isPresent() && deque.size() >= 2) {
			DequeItem right = deque.pop();
			DequeItem left = deque.pop();
			if (right.isFilter() && left.isFilter()) {
				final BooleanFilter filter = new BooleanFilter();
				filter.add(left.getFilter(), Occur.SHOULD);
				filter.add(right.getFilter(), Occur.SHOULD);
				deque.push(new DequeItem(filter));
			} else {
				final Query query = Fields.newQuery().and(left.toQuery()).and(right.toQuery()).matchAny();
				deque.push(new DequeItem(query));
			}
		} else if (deque.size() >= 1) {
			DequeItem item = deque.pop();
			if (item.isFilter()) {
				final BooleanFilter filter = new BooleanFilter();
				filter.add(item.getFilter(), Occur.SHOULD);
				deque.push(new DequeItem(filter));
			} else if (item.isQuery()) {
				final BooleanQuery query = new BooleanQuery(true);
				query.add(item.getQuery(), Occur.SHOULD);
				deque.push(new DequeItem(query));
			} else {
				throw newIllegalStateException();
			}
		} else {
			throw newIllegalStateException();
		}
	}
	
//	private void visit(Same same) {
//		if (deque.size() >= 1) {
//			DequeItem item = deque.pop();
//			if (item.isFilter()) {
//				deque.push(new DequeItem(FilterBuilders.nestedFilter(same.getPath().getPath(), item.getFilterBuilder())));
//			} else if (item.isQuery()) {
//				deque.push(new DequeItem(QueryBuilders.nestedQuery(same.getPath().getPath(), item.getQueryBuilder())));
//			} else {
//				handleIllegalDequeState();
//			}
//		} else {
//			handleIllegalDequeState();
//		}
//	}
	
//	private void visit(Group parenthesis) {
//		// carries no real meaning, skip it
//	}

	private void traversePostOrder(Expression node) {
		if (node instanceof BinaryOperator) {
			BinaryOperator binaryOperator = (BinaryOperator) node;
			Expression left = binaryOperator.getLeft();
			traversePostOrder(left);
			if (binaryOperator.getRight().isPresent()) {
				Expression right = binaryOperator.getRight().get();
				traversePostOrder(right);
			}
		} else if (node instanceof UnaryOperator) {
			UnaryOperator unaryOperator = (UnaryOperator) node;
			Expression right = unaryOperator.getRight();
			traversePostOrder(right);
		}
		visit(node);
	}

}