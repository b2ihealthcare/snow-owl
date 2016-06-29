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

import com.b2international.index.mapping.DocumentMapping;

/**
 * Represents a generic query on any kind of storage and model.
 * 
 * @since 4.7
 */
public final class Query<T> {

	/**
	 * @since 4.7
	 */
	public interface QueryBuilder<T> {
		AfterSelectBuilder<T> select(Select select);
		
		AfterSelectBuilder<T> selectAll();
	}

	/**
	 * @since 4.7
	 */
	public interface AfterSelectBuilder<T> {
		AfterWhereBuilder<T> where(Expression expression);
	}

	/**
	 * @since 4.7
	 */
	public interface AfterWhereBuilder<T> extends Buildable<Query<T>> {
		AfterWhereBuilder<T> offset(int offset);

		AfterWhereBuilder<T> limit(int limit);

		AfterWhereBuilder<T> sortBy(SortBy sortBy);
		
		AfterWhereBuilder<T> withScores(boolean withScores);
	}
	
	private int offset;
	private int limit;
	private Select select;
	private Expression where;
	private SortBy sortBy = SortBy.NONE;
	private Class<T> type;
	private Class<?> parentType;
	private boolean withScores;

	Query() {}

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
	
	public Class<T> getType() {
		return type;
	}
	
	void setType(Class<T> type) {
		this.type = type;
	}
	
	public Class<?> getParentType() {
		return parentType;
	}
	
	void setParentType(Class<?> parentType) {
		this.parentType = parentType;
	}
	
	public boolean isWithScores() {
		return withScores;
	}

	void setWithScores(boolean withScores) {
		this.withScores = withScores;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT " + select);
		sb.append(" FROM " + DocumentMapping.getType(type));
		sb.append(" WHERE " + where);
		if (SortBy.NONE != sortBy) {
			sb.append(" SORT BY " + sortBy);
		}
		sb.append(" LIMIT " + limit);
		if (offset != 0) {
			sb.append(" OFFSET " + offset);
		}
		if (parentType != null) {
			sb.append(" HAS_PARENT(" + DocumentMapping.getType(parentType) + ")");
		}
		return sb.toString();
	}

	public static <T> QueryBuilder<T> builder(Class<T> documentType) {
		return new DefaultQueryBuilder<T>(documentType, null);
	}
	
	public static <T> QueryBuilder<T> builder(Class<T> documentType, Class<?> parentScope) {
		return new DefaultQueryBuilder<T>(documentType, parentScope);
	}

}