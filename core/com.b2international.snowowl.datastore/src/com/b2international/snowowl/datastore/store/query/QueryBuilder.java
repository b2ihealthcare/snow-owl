/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore.store.query;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Sets.newHashSet;

import java.util.Collection;

import com.google.common.collect.ImmutableSet;

/**
 * @since 4.1
 */
public class QueryBuilder {

	private QueryImpl query;

	private QueryBuilder() {
		this.query = new QueryImpl();
	}
	
	public QueryBuilder match(String property, String value) {
		checkState(query != null, "Query already created, use another builder");
		this.query.addClause(new EqualsWhere(property, value));
		return this;
	}
	
	public QueryBuilder prefixMatch(String property, String value) {
		checkState(query != null, "Query already created, use another builder");
		this.query.addClause(new PrefixWhere(property, value));
		return this;
	}

	public QueryBuilder lessThan(String property, String value) {
		checkState(query != null, "Query already created, use another builder");
		this.query.addClause(new LessThanWhere(property, value));
		return this;
	}
	
	public Query build() {
		Query query = this.query;
		this.query = null;
		return query;
	}
	
	public static QueryBuilder newQuery() {
		return new QueryBuilder();
	}
	
	private static class QueryImpl implements Query {

		private Collection<Clause> clauses = newHashSet();
		
		@Override
		public Collection<Clause> clauses() {
			return ImmutableSet.copyOf(clauses);
		}
		
		public void addClause(Clause clause) {
			clauses.add(clause);
		}
		
	}
}
