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

import com.b2international.index.read.SearchExecutor;

/**
 * Represents a generic query on any kind of storage and model.
 * 
 * @since 4.7
 */
public class Query {

	/**
	 * @since 4.7
	 */
	public interface QueryBuilder {
		AfterSelectBuilder select(Select select);
		
		AfterSelectBuilder selectAll();
	}

	/**
	 * @since 4.7
	 */
	public interface AfterSelectBuilder {
		AfterWhereBuilder where(Expression expression);
	}

	/**
	 * @since 4.7
	 */
	public interface AfterWhereBuilder extends Buildable<Query> {
		AfterWhereBuilder offset(int offset);

		AfterWhereBuilder limit(int limit);

		AfterWhereBuilder sortBy(SortBy sortBy);
	}
	
	/**
	 * @since 4.7
	 */
	public interface SearchContextBuilder extends AfterWhereBuilder {
		SearchContextBuilder executeWith(SearchExecutor executor);
		
		SearchExecutor executor();
	}

	private int offset;
	private int limit;
	private Select select;
	private Expression where;
	private SortBy sortBy = SortBy.NONE;

	protected Query() {
	}

	public int getOffset() {
		return offset;
	}

	void setOffset(int offset) {
		this.offset = offset;
	}

	public int getLimit() {
		return limit;
	}

	void setLimit(int limit) {
		this.limit = limit;
	}

	public Select getSelect() {
		return select;
	}

	void setSelect(Select select) {
		this.select = select;
	}

	public Expression getWhere() {
		return where;
	}

	void setWhere(Expression where) {
		this.where = where;
	}

	public SortBy getSortBy() {
		return sortBy;
	}

	void setSortBy(SortBy sortBy) {
		this.sortBy = sortBy;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT " + select + " WHERE " + where);
		if (SortBy.NONE != sortBy) {
			sb.append(" SORT BY " + sortBy);
		}
		sb.append(" LIMIT " + limit);
		if (offset != 0) {
			sb.append(" OFFSET " + offset);
		}
		return sb.toString();
	}

	public static QueryBuilder builder() {
		return new DefaultQueryBuilder();
	}
}