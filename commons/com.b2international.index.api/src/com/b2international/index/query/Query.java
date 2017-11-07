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

import java.util.Set;

import com.b2international.index.Searcher;
import com.b2international.index.mapping.DocumentMapping;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;

/**
 * Represents a generic query on any kind of storage and model.
 * 
 * @since 4.7
 */
public final class Query<T> {

	public static final String DEFAULT_SCROLL_KEEP_ALIVE = "60s";
	
	/**
	 * @since 4.7
	 */
	public interface QueryBuilder<T> {
		QueryBuilder<T> from(Class<?> from);
		
		QueryBuilder<T> parent(Class<?> parent);

		default QueryBuilder<T> fields(String...fields) {
			return fields(ImmutableSet.copyOf(fields));
		}
		
		QueryBuilder<T> fields(Set<String> fields);
		
		AfterWhereBuilder<T> where(Expression expression);
	}
	
	/**
	 * @since 4.7
	 */
	public interface AfterWhereBuilder<T> extends Buildable<Query<T>> {
		
		/**
		 * Keeps the context of the search alive with a default <code>60s</code> keep alive time value.
		 *  
		 * @param scrollKeepAlive
		 * @return
		 * @see #scroll(String)
		 */
		default AfterWhereBuilder<T> scroll() {
			return scroll(DEFAULT_SCROLL_KEEP_ALIVE);
		}
		
		/**
		 * A keep alive time value to pass to the query. This will keep the context of the search alive until the specified time value to scroll the
		 * results in subsequent {@link Searcher#scroll(String)} calls. 
		 * <br/ >
		 * Example values are <code>15s</code>, <code>1m</code>, <code>1h</code>.
		 * 
		 * @param scrollKeepAlive
		 * @return
		 */
		AfterWhereBuilder<T> scroll(String scrollKeepAlive);
		
		AfterWhereBuilder<T> limit(int limit);

		AfterWhereBuilder<T> sortBy(SortBy sortBy);
		
		AfterWhereBuilder<T> withScores(boolean withScores);
	}

	private String scrollKeepAlive;
	private int limit;
	private Class<T> select;
	private Class<?> from;
	private Expression where;
	private SortBy sortBy = SortBy.DOC_ID;
	private Class<?> parentType;
	private boolean withScores;
	private Set<String> fields;

	Query() {}

	public int getLimit() {
		return limit;
	}

	void setLimit(int limit) {
		this.limit = limit;
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
	
	public Class<T> getSelect() {
		return select;
	}
	
	void setSelect(Class<T> select) {
		this.select = select;
	}
	
	public Class<?> getFrom() {
		return from;
	}
	
	void setFrom(Class<?> from) {
		this.from = from;
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
	
	public Set<String> getFields() {
		return fields;
	}
	
	void setFields(Set<String> fields) {
		this.fields = fields;
	}
	
	public String getScrollKeepAlive() {
		return scrollKeepAlive;
	}
	
	public void setScrollKeepAlive(String scrollKeepAlive) {
		this.scrollKeepAlive = scrollKeepAlive;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT " + getSelectString());
		sb.append(" FROM " + DocumentMapping.getType(from));
		sb.append(" WHERE " + where);
		if (!SortBy.DOC_ID.equals(sortBy)) {
			sb.append(" SORT BY " + sortBy);
		}
		sb.append(" LIMIT " + limit);
		if (!Strings.isNullOrEmpty(scrollKeepAlive)) {
			sb.append(" SCROLL("+scrollKeepAlive+") ");
		}
		if (parentType != null) {
			sb.append(" HAS_PARENT(" + DocumentMapping.getType(parentType) + ")");
		}
		return sb.toString();
	}

	private String getSelectString() {
		return fields != null && !fields.isEmpty() ? Joiner.on(",").join(fields) : select == from ? "*" : select.toString();
	}

	public boolean isDocIdOnly() {
		return getFields().size() == 1 && getFields().contains(DocumentMapping._ID);
	}
	
	public static <T> QueryBuilder<T> select(Class<T> select) {
		return new DefaultQueryBuilder<>(select);
	}
	
}