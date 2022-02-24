/*
 * Copyright 2011-2022 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

/**
 * Abstract superclass for building query {@link Expression}s.
 * 
 * @since 6.17
 */
public abstract class AbstractExpressionBuilder<B extends AbstractExpressionBuilder<B>>{

	protected final List<Expression> mustClauses = new ArrayList<>(1);
	protected final List<Expression> mustNotClauses = new ArrayList<>(1);
	protected final List<Expression> shouldClauses = new ArrayList<>(3);
	protected final List<Expression> filterClauses = new ArrayList<>(3);
	protected int minShouldMatch = 1;
	
	protected AbstractExpressionBuilder() {}
	
	public B must(Expression e) {
		this.mustClauses.add(e);
		return getSelf();
	}
	
	public B mustNot(Expression e) {
		this.mustNotClauses.add(e);
		return getSelf();
	}
	
	public B should(Expression e) {
		this.shouldClauses.add(e);
		return getSelf();
	}
	
	public B filter(Expression e) {
		this.filterClauses.add(e);
		return getSelf();
	}
	
	public B setMinimumNumberShouldMatch(int minShouldMatch) {
		this.minShouldMatch = minShouldMatch;
		return getSelf();
	}
	
	/**
	 * Return this builder
	 * @return
	 */
	protected abstract B getSelf();
	
	/**
	 * Return the built {@link Expression}
	 * @return
	 */
	public Expression build() {
		if (mustClauses.isEmpty() && mustNotClauses.isEmpty() && shouldClauses.isEmpty() && filterClauses.isEmpty()) {
			return Expressions.matchAll();
		} else if (isSingleFilter()) {
			// shortcut to reduce number of nested Boolean clauses
			return filterClauses.get(0);
		} else if (isSingleShould()) {
			// shortcut to reduce number of nested Boolean clauses
			return shouldClauses.get(0);
		} else {
			// before creating the boolean query make sure we flatten the query as much as possible
			
			// or(A=B, or(A=C, or(A=D))) - should only clauses deeply nested inside bool queries (and the minShouldMatch is 1 on all levels)
			flattenShoulds();
			
			// then optimize term filters that match the same field, field1=A or field1=B or field1=C should be converted to field1=any(A, B, C)
			// during EsQueryBuilder the system will optimize it again to fit into the max term count ES setting, see EsQueryBuilder
			mergeTermFilters();
			
			if (isSingleFilter()) {
				return filterClauses.get(0);
			}
			
			if (isSingleShould()) {
				return shouldClauses.get(0);
			}
			
			// if after the optimization the resulting bool clauses are empty, then return a MatchNone expression
			if (mustClauses.isEmpty() && mustNotClauses.isEmpty() && shouldClauses.isEmpty() && filterClauses.isEmpty()) {
				return Expressions.matchNone();
			} else {
				// otherwise create the bool expression as usual
				final BoolExpression be = new BoolExpression(mustClauses, mustNotClauses, shouldClauses, filterClauses);
				be.setMinShouldMatch(minShouldMatch);
				return be;
			}
		}
	}

	private boolean isSingleFilter() {
		return mustClauses.isEmpty() && mustNotClauses.isEmpty() && shouldClauses.isEmpty() && filterClauses.size() == 1;
	}
	
	private boolean isSingleShould() {
		return mustClauses.isEmpty() && mustNotClauses.isEmpty() && shouldClauses.size() == 1 && filterClauses.isEmpty();
	}

	protected final void flattenShoulds() {
		if (!mustClauses.isEmpty() || !mustNotClauses.isEmpty() || !filterClauses.isEmpty() || minShouldMatch != 1) {
			return;
		}
		for (Expression expression : List.copyOf(shouldClauses)) {
			if (expression instanceof BoolExpression) {
				BoolExpression bool = (BoolExpression) expression;
				if (bool.isShouldOnly() && bool.minShouldMatch() == 1) {
					shouldClauses.addAll(bool.shouldClauses());
					shouldClauses.remove(bool);
				}
			}
		}
	}

	protected final void mergeTermFilters() {
		// check each mustNot and should clause list and merge term/terms queries into a single terms query, targeting the same field
		mergeTermFilters(mustNotClauses);
		mergeTermFilters(shouldClauses);
		// XXX merging must/filter queries will change the boolean logic from AND to OR which leads to incorrect results
		// instead calculate the intersection of the values and use that for the expressions
		reduceTermFilters(mustClauses);
		reduceTermFilters(filterClauses);
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

	private void mergeTermFilters(List<Expression> clauses) {
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
				Set<Object> values = Sets.newHashSet();
				for (Expression expression : termExpressions) {
					if (expression instanceof SingleArgumentPredicate<?>) {
						values.add(((SingleArgumentPredicate<?>) expression).getArgument());
					} else if (expression instanceof SetPredicate<?>) {
						values.addAll(((SetPredicate<?>) expression).values());
					}
				}
				// remove all matching clauses first
				clauses.removeAll(termExpressions);
				// add the new merged expression
				clauses.add(Expressions.matchAnyObject(field, values));
			}
		}
	}
	
	private boolean shouldMergeSingleArgumentPredicate(Expression expression) {
		return expression instanceof SingleArgumentPredicate<?> && !(expression instanceof RegexpPredicate);
	}

	private boolean shouldMergeSetPredicate(Expression expression) {
		return expression instanceof SetPredicate<?> && !(expression instanceof PrefixPredicate);
	}
	
}
