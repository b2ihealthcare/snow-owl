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

import java.util.List;
import java.util.stream.Stream;

import com.b2international.commons.CompareUtils;
import com.b2international.index.Hits;
import com.b2international.index.Searcher;
import com.b2international.index.revision.Revision;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;

/**
 * Represents a generic query on any kind of storage and model.
 * 
 * @since 4.7
 */
public final class Query<T> {

	private static final Joiner COMMA_JOINER = Joiner.on(",");
	
	/**
	 * @since 4.7
	 */
	public interface QueryBuilder<T> {
		
		QueryBuilder<T> from(Class<?> from, Class<?>...additionalFroms);
		
		QueryBuilder<T> from(List<Class<?>> froms);
		
		QueryBuilder<T> parent(Class<?> parent);

		default QueryBuilder<T> fields(String...fields) {
			return fields(ImmutableList.copyOf(fields));
		}
		
		QueryBuilder<T> fields(List<String> fields);
		
		AfterWhereBuilder<T> where(Expression expression);

	}
	
	/**
	 * @since 4.7
	 */
	public interface AfterWhereBuilder<T> extends Buildable<Query<T>> {
		
		/**
		 * Return matches after the specified sort values. This can be used for live scrolling on large result sets. If you would like to iterate over
		 * the entire result set, use the {@link #stream(String) scroll API} instead.
		 * 
		 * @param sortValues - the last sort values in sort order 
		 * @return
		 */
		AfterWhereBuilder<T> searchAfter(String sortValues);
		
		/**
		 * The maximum number of hits to return.
		 * @param limit
		 * @return
		 */
		AfterWhereBuilder<T> limit(int limit);

		/**
		 * Sort the results by the specified {@link SortBy} construct.
		 * 
		 * @param sortBy
		 * @return
		 * @see SortBy
		 */
		AfterWhereBuilder<T> sortBy(SortBy sortBy);
		
		/**
		 * Whether to compute scores for the hits or not. By default it is disabled.
		 * @param withScores
		 * @return
		 */
		AfterWhereBuilder<T> withScores(boolean withScores);
	}

	private String searchAfter;
	private int limit;
	private IndexSelection<T> selection;
	private Expression where;
	private SortBy sortBy = SortBy.DEFAULT;
	private boolean withScores;
	private List<String> fields;

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

	public IndexSelection<T> getSelection() {
		return selection;
	}
	
	void setSelection(IndexSelection<T> selection) {
		this.selection = selection;
	}
	
	public boolean isWithScores() {
		return withScores;
	}

	void setWithScores(boolean withScores) {
		this.withScores = withScores;
	}
	
	public List<String> getFields() {
		return fields;
	}
	
	void setFields(List<String> fields) {
		this.fields = fields;
	}
	
	public String getSearchAfter() {
		return searchAfter;
	}
	
	void setSearchAfter(String searchAfter) {
		this.searchAfter = searchAfter;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT " + getSelectString());
		sb.append(" FROM " + COMMA_JOINER.join(selection.getFromDocumentTypes()));
		sb.append(" WHERE " + where);
		if (!SortBy.DEFAULT.equals(sortBy)) {
			sb.append(" SORT BY " + sortBy);
		}
		sb.append(" LIMIT " + limit);
		if (selection.getParentScope() != null) {
			sb.append(" HAS_PARENT(" + selection.getParentScopeDocumentType() + ")");
		}
		return sb.toString();
	}

	private String getSelectString() {
		return !CompareUtils.isEmpty(fields) ? COMMA_JOINER.join(fields) : selection.toSelectString();
	}

	public static <T> QueryBuilder<T> select(Class<T> select) {
		return new DefaultQueryBuilder<>(select);
	}
	
	public static <T> QueryBuilder<T> select(IndexSelection<T> select) {
		return new DefaultQueryBuilder<>(select.getSelect())
				.from(select.getFrom())
				.parent(select.getParentScope());
	}

	public boolean isRevisionQuery() {
		return getSelection().getFrom().stream().anyMatch(Revision.class::isAssignableFrom);
	}
	
	public AfterWhereBuilder<T> withWhere(Expression where) {
		return Query.select(getSelection())
			.fields(getFields())
			.where(where)
			.sortBy(getSortBy())
			.limit(getLimit())
			.searchAfter(getSearchAfter())
			.withScores(isWithScores());
	}
	
	public AfterWhereBuilder<T> withSearchAfter(String searchAfter) {
		return Query.select(getSelection())
				.fields(getFields())
				.where(getWhere())
				.sortBy(getSortBy())
				.limit(getLimit())
				.searchAfter(searchAfter)
				.withScores(isWithScores());
	}

	/**
	 * Convenience method for returning all search results in batches.
	 * <p>
	 * If the index does not change in the meantime, or the query is idempotent, the
	 * total number of returned documents should match the {@link Hits#getTotal()
	 * total hit count} property included in any of the returned elements of the
	 * Stream. Batch size is controlled by the query's {@code limit} parameter.
	 * <p>
	 * If you would like to process individual documents instead of larger batches,
	 * keep the limit setting at a reasonably high value, eg. {@code 1000}, and
	 * consider using {@code flatMap}:
	 * <pre>
	 * Query.select(...)
	 *     [...]
	 *     .limit(1000)
	 *     .build()
	 *     .stream(searcher)
	 *     .flatMap(Hits::stream)
	 *     .forEachOrdered(doc -> { ... });
	 * </pre>
	 * 
	 * @param searcher the searcher to use for streaming results
	 * @return
	 */
	public final Stream<Hits<T>> stream(Searcher searcher) {
		return searcher.stream(this);
	}
	
}
