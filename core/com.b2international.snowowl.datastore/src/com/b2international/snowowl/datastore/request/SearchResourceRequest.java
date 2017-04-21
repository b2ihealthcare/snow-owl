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
package com.b2international.snowowl.datastore.request;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.b2international.commons.options.Options;
import com.b2international.index.query.Expression;
import com.b2international.index.query.Expressions.ExpressionBuilder;
import com.b2international.index.query.Query.QueryBuilder;
import com.b2international.index.query.Query;
import com.b2international.index.query.SortBy;
import com.b2international.index.query.SortBy.Order;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 5.2
 */
public abstract class SearchResourceRequest<C extends ServiceProvider, B> extends ResourceRequest<C, B> {
	
	/**
	 * Exception that indicates that the search request will not have any matching items therefore can immediately respond back with an empty result.
	 * @since 5.4
	 */
	public static class NoResultException extends RuntimeException {
		private static final long serialVersionUID = 5581643581423131046L;
	}
	
	/**
	 * @since 5.8
	 */
	public enum OptionKey {
		/**
		 * Restrict search to the given collection of component identifiers.
		 */
		COMPONENT_IDS,
		
		/**
		 * Sort result by the specified sort fields.
		 */
		SORT_BY;
	}

	/**
	 * Special field name for sorting based on the document's natural occurrence (document order). 
	 */
	public static final SortField DOC = new SortField(SortBy.FIELD_DOC, true);
	
	/**
	 * Special field name for sorting based on the document score (relevance).
	 */
	public static final SortField SCORE = new SortField(SortBy.FIELD_SCORE, false);
	
	public static class SortField implements Serializable {
		private final String field;
		private final boolean ascending;
		
		public SortField(String field, boolean ascending) {
			this.field = field;
			this.ascending = ascending;
		}
		
		public String getField() {
			return field;
		}
		
		public boolean isAscending() {
			return ascending;
		}
	}
	
	/**
	 * Operator that can be used to specify more fine-grained value filtering.
	 * 
	 * @since 5.4
	 */
	public enum Operator {
		EQUALS,
		NOT_EQUALS,
		GREATER_THAN,
		GREATER_THAN_EQUALS,
		LESS_THAN,
		LESS_THAN_EQUALS,
	}
	
	@Min(0)
	private int offset;

	@Min(0)
	private int limit;

	@NotNull
	private Options options;
	
	protected SearchResourceRequest() {}
	
	void setLimit(int limit) {
		this.limit = limit;
	}
	
	void setOffset(int offset) {
		this.offset = offset;
	}
	
	void setOptions(Options options) {
		this.options = options;
	}
	
	@JsonProperty
	protected final int offset() {
		return offset;
	}
	
	@JsonProperty
	protected final int limit() {
		return limit;
	}
	
	@JsonProperty
	protected final Options options() {
		return options;
	}
	
	protected final boolean containsKey(Enum<?> key) {
		return options.containsKey(key.name());
	}
	
	protected final Object get(Enum<?> key) {
		return options.get(key.name());
	}

	protected final <T> T get(Enum<?> key, Class<T> expectedType) {
		return options.get(key.name(), expectedType);
	}

	protected final boolean getBoolean(Enum<?> key) {
		return options.getBoolean(key.name());
	}

	protected final String getString(Enum<?> key) {
		return options.getString(key.name());
	}

	protected final <T> Collection<T> getCollection(Enum<?> key, Class<T> type) {
		return options.getCollection(key.name(), type);
	}
	
	protected final <T> List<T> getList(Enum<?> key, Class<T> type) {
		return options.getList(key.name(), type);
	}
	
	protected final Options getOptions(Enum<?> key) {
		return options.getOptions(key.name());
	}
	
	/**
	 * Modifies the specified input using the given reducer {@link BiFunction} if a component ID filter 
	 * list is present.
	 * 
	 * @param input
	 * @param reducer
	 */
	protected final <T> T applyIdFilter(T input, BiFunction<T, Collection<String>, T> reducer) {
		if (containsKey(OptionKey.COMPONENT_IDS)) {
			return reducer.apply(input, getCollection(OptionKey.COMPONENT_IDS, String.class));
		} else {
			return input;
		}
	}
	
	protected final ExpressionBuilder addIdFilter(ExpressionBuilder queryBuilder, Function<Collection<String>, Expression> expressionFactory) {
		return applyIdFilter(queryBuilder, (qb, ids) -> qb.filter(expressionFactory.apply(ids)));
	}
	
	protected final SortBy sortBy() {
		if (containsKey(OptionKey.SORT_BY)) {
			List<SortField> fields = getList(OptionKey.SORT_BY, SortField.class);
			SortBy.Builder builder = SortBy.builder();
			for (SortField sortField : fields) {
				builder.add(sortField.getField(), sortField.isAscending() ? Order.ASC : Order.DESC);
			}
			return builder.build();
		} else {
			return SortBy.DOC;
		}		
	}
	
	protected final <T> QueryBuilder<T> select(Class<T> select) {
		return Query.selectPartial(select, fields());
	}
	
	@Override
	public final B execute(C context) {
		try {
			return doExecute(context);
		} catch (NoResultException e) {
			return createEmptyResult(offset, limit);
		} catch (IOException e) {
			throw new SnowowlRuntimeException("Caught exception while executing search request.", e);
		}
	}
	
	/**
	 * Creates a new empty result object with the specified offset and limit parameter.
	 * @param offset
	 * @param limit
	 * @return
	 */
	protected abstract B createEmptyResult(int offset, int limit);

	/**
	 * Executes this search request.
	 * @param context
	 * @return
	 * @throws IOException
	 */
	protected abstract B doExecute(C context) throws IOException;
	
	/**
	 * Constructs the operator property name for the given property name.
	 * @param property
	 * @return
	 */
	public static String operator(String property) {
		return String.format("%sOperator", property);
	}
	
}
