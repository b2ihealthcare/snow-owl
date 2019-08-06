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

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

/**
 * Abstract superclass for building query {@link Expression}s.
 * 
 * @since 6.17
 */
public abstract class AbstractExpressionBuilder<B extends AbstractExpressionBuilder<B>>{

	protected final List<Expression> mustClauses = newArrayList();
	protected final List<Expression> mustNotClauses = newArrayList();
	protected final List<Expression> shouldClauses = newArrayList();
	protected final List<Expression> filterClauses = newArrayList();
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
		} else if (mustClauses.isEmpty() && mustNotClauses.isEmpty() && shouldClauses.isEmpty() && filterClauses.size() == 1) {
			// shortcut to reduce number of nested Boolean clauses
			return filterClauses.get(0);
		} else {
			final BoolExpression be = new BoolExpression(mustClauses, mustNotClauses, shouldClauses, filterClauses);
			be.setMinShouldMatch(minShouldMatch);
			return be;
		}
	}
}
