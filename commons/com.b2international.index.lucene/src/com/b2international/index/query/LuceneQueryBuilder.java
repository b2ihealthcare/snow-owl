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

import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Deque;

import org.apache.lucene.queries.BooleanFilter;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.ConstantScoreQuery;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.FilteredQuery;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.join.ScoreMode;
import org.apache.lucene.search.join.ToParentBlockJoinQuery;

import com.b2international.commons.exceptions.FormattedRuntimeException;
import com.b2international.index.IndexException;
import com.b2international.index.json.JsonDocumentMapping;
import com.b2international.index.mapping.Mappings;
import com.google.common.collect.Queues;

/**
 * @since 4.7
 */
public final class LuceneQueryBuilder {

	private static String ILLEGAL_STACK_STATE_MESSAGE = "Illegal internal stack state: %s";
	
	private final class DequeItem {
		private final Object query;
		
		public DequeItem(org.apache.lucene.search.Query query) {
			this.query = query;
		}
		
		public DequeItem(Filter filter) {
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
	private final Class<?> type;
	
	public LuceneQueryBuilder(Class<?> type) {
		this.type = type;
	}

	private FormattedRuntimeException newIllegalStateException() {
		return new FormattedRuntimeException(ILLEGAL_STACK_STATE_MESSAGE, deque);
	}
	
	public org.apache.lucene.search.Query build(Expression expression) {
		checkNotNull(expression, "expression");
		traversePostOrder(expression);
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
			And and = (And) expression;
			visit(and);
		} else if (expression instanceof Or) {
			Or or = (Or) expression;
			visit(or);
		} else if (expression instanceof Not) {
			Not not = (Not) expression;
			visit(not);
//		} else if (expression instanceof Same) {
//			Same same = (Same) expression;
//			visit(same);
//		} else if (expression instanceof Group) {
//			Group group = (Group) expression;
//			visit(group);
		} else if (expression instanceof StringPredicate) {
			StringPredicate predicate = (StringPredicate) expression;
			visit(predicate);
//		} else if (expression instanceof BooleanPredicate) {
//			BooleanPredicate predicate = (BooleanPredicate) expression;
//			visit(predicate);
//		} else if (expression instanceof StringSetPredicate) {
//			StringSetPredicate predicate = (StringSetPredicate) expression;
//			visit(predicate);
		} else if (expression instanceof LongPredicate) {
			visit((LongPredicate) expression);
//		} else if (expression instanceof TextPredicate) {
//			TextPredicate predicate = (TextPredicate) expression;
//			visit(predicate);
//		} else if (expression instanceof DateRangePredicate) {
//			DateRangePredicate predicate = (DateRangePredicate) expression;
//			visit(predicate);
		} else if (expression instanceof NestedPredicate) {
			visit((NestedPredicate) expression);
		} else {
			throw new IllegalArgumentException("Unexpected expression: " + expression);
		}
	}
	
	private void visit(NestedPredicate predicate) {
		final Filter parentFilter = JsonDocumentMapping.filterByType(type);
		// TODO only one level deep nesting is supported right now
		final Class<?> childType = getField(type, predicate.getField()).getType();
		final Filter childFilter = JsonDocumentMapping.filterByType(childType);
		final Query innerQuery = new LuceneQueryBuilder(childType).build(predicate.getExpression());
		final Query childQuery = new FilteredQuery(innerQuery, childFilter);
		// TODO scoring???
		final Query nestedQuery = new ToParentBlockJoinQuery(childQuery, parentFilter, ScoreMode.None);
		System.out.println(nestedQuery);
		deque.push(new DequeItem(nestedQuery));
	}

	private static Field getField(Class<?> type, String field) {
		try {
			return type.getDeclaredField(field);
		} catch (NoSuchFieldException | SecurityException e) {
			throw new IndexException("Couldn't find field " + field, e);
		}
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
		final Filter filter = Mappings.stringField(predicate.getField()).createTermsFilter(Collections.singleton(predicate.getArgument()));
		deque.push(new DequeItem(filter));
	}
	
	private void visit(LongPredicate predicate) {
		final Filter filter = Mappings.longField(predicate.getField()).createTermsFilter(Collections.singleton(predicate.getArgument()));
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

	private void visit(And and) {
		if (and.getRight().isPresent() && deque.size() >= 2) {
			DequeItem right = deque.pop();
			DequeItem left = deque.pop();
			if (right.isFilter() && left.isFilter()) {
				final BooleanFilter filter = new BooleanFilter();
				filter.add(left.getFilter(), Occur.MUST);
				filter.add(right.getFilter(), Occur.MUST);
				deque.push(new DequeItem(filter));
			} else if (right.isFilter() && left.isQuery()) {
				final Query query = Mappings.newQuery().and(right.toQuery()).and(left.getQuery()).matchAll();
				deque.push(new DequeItem(query));
			} else if (right.isQuery() && left.isFilter()) {
				final Query query = Mappings.newQuery().and(right.getQuery()).and(left.toQuery()).matchAll();
				deque.push(new DequeItem(query));
			} else if (right.isQuery() && left.isQuery()) {
				final Query query = Mappings.newQuery().and(right.getQuery()).and(left.getQuery()).matchAll();
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
			} else if (right.isFilter() && left.isQuery()) {
				final Query query = Mappings.newQuery().and(right.toQuery()).and(left.getQuery()).matchAny();
				deque.push(new DequeItem(query));
			} else if (right.isQuery() && left.isFilter()) {
				final Query query = Mappings.newQuery().and(right.getQuery()).and(left.toQuery()).matchAny();
				deque.push(new DequeItem(query));
			} else if (right.isQuery() && left.isQuery()) {
				final Query query = Mappings.newQuery().and(right.getQuery()).and(left.getQuery()).matchAny();
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
	
//	private void visit(Not not) {
//		if (deque.size() >= 1) {
//			DequeItem item = deque.pop();
//			if (item.isFilter()) {
//				deque.push(new DequeItem(FilterBuilders.notFilter(item.getFilterBuilder())));
//			} else if (item.isQuery()) {
//				deque.push(new DequeItem(QueryBuilders.boolQuery().mustNot(item.getQueryBuilder())));
//			} else {
//				handleIllegalDequeState();
//			}
//		} else {
//			handleIllegalDequeState();
//		}
//	}

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