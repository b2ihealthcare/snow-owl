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
package com.b2international.index.query;

import java.util.Collections;
import java.util.List;

import com.b2international.commons.CompareUtils;
import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.index.query.Query.AfterWhereBuilder;
import com.b2international.index.query.Query.QueryBuilder;
import com.b2international.index.revision.Revision;

/**
 * @since 4.7
 */
class DefaultQueryBuilder<T> implements QueryBuilder<T>, AfterWhereBuilder<T> {

	private static final int DEFAULT_LIMIT = 50;

	private IndexSelection.Builder<T> selection;
	
	private String searchAfter;
	private int limit = DEFAULT_LIMIT;
	private Expression where;
	private SortBy sortBy = SortBy.DEFAULT;
	private boolean withScores = false;

	private List<String> fields = Collections.emptyList();

	DefaultQueryBuilder(Class<T> select) {
		this.selection = IndexSelection.builder(select);
	}
	
	@Override
	public QueryBuilder<T> from(Class<?> from, Class<?>...froms) {
		this.selection.from(from, froms);
		return this;
	}
	
	@Override
	public QueryBuilder<T> from(List<Class<?>> froms) {
		this.selection.from(froms);
		return this;
	}
	
	@Override
	public QueryBuilder<T> parent(Class<?> parentScope) {
		this.selection.withParentScope(parentScope);
		return this;
	}
	
	@Override
	public QueryBuilder<T> fields(List<String> fields) {
		this.fields = fields;
		return this;
	}
	
	@Override
	public AfterWhereBuilder<T> searchAfter(String searchAfter) {
		this.searchAfter = searchAfter;
		return this;
	}
	
	@Override
	public AfterWhereBuilder<T> limit(int limit) {
		this.limit = limit;
		return this;
	}

	@Override
	public AfterWhereBuilder<T> where(Expression expression) {
		this.where = expression;
		return this;
	}

	@Override
	public AfterWhereBuilder<T> sortBy(SortBy sortBy) {
		this.sortBy = sortBy;
		return this;
	}
	
	@Override
	public AfterWhereBuilder<T> withScores(boolean withScores) {
		this.withScores = withScores;
		return this;
	}

	@Override
	public Query<T> build() {
		IndexSelection<T> selection = this.selection.build();
		if (Revision.class.isAssignableFrom(selection.getSelect()) && !CompareUtils.isEmpty(fields) && !fields.contains(Revision.Fields.ID)) {
			throw new BadRequestException("'%s' field is required when loading objects partially.", Revision.Fields.ID);
		}
		Query<T> query = new Query<T>();
		query.setSelection(selection);
		query.setWhere(where);
		query.setSearchAfter(searchAfter);
		query.setLimit(limit);
		query.setSortBy(sortBy);
		query.setWithScores(withScores);
		query.setFields(fields);
		return query;
	}

}