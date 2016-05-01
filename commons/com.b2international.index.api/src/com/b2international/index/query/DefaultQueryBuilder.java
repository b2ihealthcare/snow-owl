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

import com.b2international.index.query.Query.AfterSelectBuilder;
import com.b2international.index.query.Query.AfterWhereBuilder;
import com.b2international.index.query.Query.QueryBuilder;

/**
 * @since 4.7
 */
class DefaultQueryBuilder<T> implements QueryBuilder<T>, AfterSelectBuilder<T>, AfterWhereBuilder<T> {

	private static final int DEFAULT_LIMIT = 50;

	private final Class<T> type;
	
	private int offset = 0;
	private int limit = DEFAULT_LIMIT;
	private Select select;
	private Expression where;
	private SortBy sortBy = SortBy.NONE;

	public DefaultQueryBuilder(Class<T> type) {
		this.type = type;
	}

	public int getLimit() {
		return limit;
	}
	
	public int getOffset() {
		return offset;
	}
	
	public Select getSelect() {
		return select;
	}
	
	public SortBy getSortBy() {
		return sortBy;
	}
	
	public Expression getWhere() {
		return where;
	}
	
	@Override
	public AfterWhereBuilder<T> offset(int offset) {
		this.offset = offset;
		return this;
	}

	@Override
	public AfterWhereBuilder<T> limit(int limit) {
		this.limit = limit;
		return this;
	}

	@Override
	public AfterSelectBuilder<T> select(Select select) {
		this.select = select;
		return this;
	}
	
	@Override
	public AfterSelectBuilder<T> selectAll() {
		return select(Select.all());
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
	public Query<T> build() {
		Query<T> query = new Query<T>();
		query.setType(type);
		query.setSelect(select);
		query.setWhere(where);
		query.setLimit(limit);
		query.setOffset(offset);
		query.setSortBy(sortBy);
		return query;
	}
}