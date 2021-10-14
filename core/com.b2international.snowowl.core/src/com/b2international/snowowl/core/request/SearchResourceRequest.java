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
package com.b2international.snowowl.core.request;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.b2international.commons.CompareUtils;
import com.b2international.commons.options.Options;
import com.b2international.commons.options.OptionsBuilder;
import com.b2international.index.ID;
import com.b2international.index.query.Expression;
import com.b2international.index.query.Expressions.ExpressionBuilder;
import com.b2international.index.query.SortBy;
import com.b2international.index.query.SortBy.Builder;
import com.b2international.index.query.SortBy.Order;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.annotations.VisibleForTesting;

/**
 * @since 5.2
 */
public abstract class SearchResourceRequest<C extends ServiceProvider, B> extends IndexResourceRequest<C, B> {
	
	private static final long serialVersionUID = 1L;

	/**
	 * Special option character that can be used for special search expressions in filters where usually a user enters a text, like a term filter.
	 * @see #getSpecialOptionKey
	 */
	public static final String SPECIAL_OPTION_CHARACTER = "@";
	
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
		 * Sort result by the specified sort fields.
		 */
		SORT_BY,
	}

	public static abstract class Sort implements Serializable {
		
		private static final long serialVersionUID = 1L;
		
		private final boolean ascending;

		public Sort(final boolean ascending) {
			this.ascending = ascending;
		}

		public boolean isAscending() {
			return ascending;
		}

		public static SortField fieldAsc(String field) {
			return SortField.of(field, true);
		}
		
		public static SortField fieldDesc(String field) {
			return SortField.of(field, false);
		}
		
		public static SortScript scriptAsc(String script, final Map<String, Object> arguments) {
			return SortScript.of(script, arguments, true);
		}
		
		public static SortScript scriptDesc(String script, final Map<String, Object> arguments) {
			return SortScript.of(script, arguments, false);
		}
		
	}
	
	public static final class SortField extends Sort {
		
		private static final long serialVersionUID = 2L;
		
		private final String field;
		
		private SortField(String field, boolean ascending) {
			super(ascending);
			this.field = field;
		}
		
		public String getField() {
			return field;
		}
		
		public static SortField of(String field, boolean ascending) {
			return new SortField(field, ascending);
		}
		
		@Override
		public int hashCode() {
			return Objects.hash(field, isAscending());
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) { return true; }
			if (obj == null) { return false; }
			if (getClass() != obj.getClass()) { return false; }
			final SortField other = (SortField) obj;
			if (isAscending() != other.isAscending()) { return false; }
			if (!Objects.equals(field, other.field)) { return false; }
			return true;
		}
		
		@Override
		public String toString() {
			return String.format("%s:%s", field, isAscending() ? "asc" : "desc");
		}
	}
	
	public static final class SortScript extends Sort {
		
		private static final long serialVersionUID = 2L;
		
		private final String script;
		private final Map<String, Object> arguments;
		
		private SortScript(final String script,
				final Map<String, Object> arguments,
				final boolean ascending) {
			super(ascending);
			this.script = script;
			this.arguments = arguments;
		}

		public String getScript() {
			return script;
		}
		
		public Map<String, Object> getArguments() {
			return arguments;
		}
		
		public static SortScript of(String script, final Map<String, Object> arguments, boolean ascending) {
			return new SortScript(script, arguments, ascending);
		}
		
		@Override
		public int hashCode() {
			return Objects.hash(script, arguments, isAscending());
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) { return true; }
			if (obj == null) { return false; }
			if (getClass() != obj.getClass()) { return false; }
			final SortScript other = (SortScript) obj;
			if (isAscending() != other.isAscending()) { return false; }
			if (!Objects.equals(script, other.script)) { return false; }
			if (!Objects.equals(arguments, other.arguments)) { return false; }
			return true;
		}
		
		@Override
		public String toString() {
			return String.format("%s:%s", script, isAscending() ? "asc" : "desc");
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
	
	private String searchAfter;

	/**
	 * Restrict search to the given collection of component identifiers.
	 */
	private Set<String> componentIds;
	
	@Min(0)
	private int limit;

	@NotNull
	private Options options;
	
	protected SearchResourceRequest() {}
	
	/**
	 * Subclasses may override this to provide the OptionKey where special search expressions can be supported and processed. By default this returns
	 * <code>null</code> which disables the feature.
	 * 
	 * @return
	 */
	protected Enum<?> getSpecialOptionKey() {
		return null;
	}
	
	void setSearchAfter(String searchAfter) {
		this.searchAfter = searchAfter;
	}
	
	void setComponentIds(Set<String> componentIds) {
		this.componentIds = componentIds;
	}

	@JsonProperty
	protected final String searchAfter() {
		return searchAfter;
	}
	
	void setLimit(int limit) {
		this.limit = limit;
	}
	
	void setOptions(Options options) {
		this.options = options;
	}
	
	@JsonProperty
	protected final int limit() {
		return limit;
	}
	
	@JsonProperty
	protected final Options options() {
		return options;
	}
	
	@JsonProperty
	protected final Set<String> componentIds() {
		return componentIds;
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
	
	protected final ExpressionBuilder addIdFilter(ExpressionBuilder queryBuilder, Function<Collection<String>, Expression> expressionFactory) {
		return applyIdFilter(queryBuilder, (qb, ids) -> qb.filter(expressionFactory.apply(ids)));
	}
	
	/**
	 * Modifies the specified input using the given reducer {@link BiFunction} if a component ID filter 
	 * list is present.
	 * 
	 * @param input
	 * @param reducer
	 */
	protected final <T> T applyIdFilter(T input, BiFunction<T, Collection<String>, T> reducer) {
		if (componentIds != null) {
			return reducer.apply(input, componentIds);
		} else {
			return input;
		}
	}
	
	/**
	 * Applies a filter clause to the given queryBuilder if the specified optionKey holds any value, otherwise this method does nothing.
	 *  
	 * @param <T> - the expected type of the filter values
	 * @param queryBuilder - the query builder to apply the filter to
	 * @param optionKey - the search option key
	 * @param filterType - the expected type of the filter values
	 * @param expressionFactory - the factory that creates the index clause based on the filter values
	 */
	protected final <T> void addFilter(ExpressionBuilder queryBuilder, Enum<?> optionKey, Class<T> filterType, Function<Collection<T>, Expression> expressionFactory) {
		if (containsKey(optionKey)) {
			Collection<T> filterValues = getCollection(optionKey, filterType);
			queryBuilder.filter(expressionFactory.apply(filterValues));
		}
	}
	
	@Override
	public final B execute(C context) {
		try {
			// process the options for special option expressions and map them as options on their own
			setOptions(processSpecialOptionKey(options, getSpecialOptionKey(), this::extractSpecialOptionValue));
			return doExecute(context);
		} catch (NoResultException e) {
			return createEmptyResult(limit);
		} catch (IOException e) {
			throw new SnowowlRuntimeException("Caught exception while executing search request.", e);
		}
	}
	
	/**
	 * Subclasses may override this method to extract the special option key value if it is not stored as a String in the {@link Options}.
	 * 
	 * @param options
	 * @param specialOptionKey
	 * @return
	 */
	protected String extractSpecialOptionValue(final Options options, final Enum<?> specialOptionKey) {
		return options.getString(specialOptionKey);
	}
	
	@VisibleForTesting
	static Options processSpecialOptionKey(Options options, Enum<?> specialOptionKey) {
		return processSpecialOptionKey(options, specialOptionKey, Options::getString);
	}
	
	@VisibleForTesting
	static Options processSpecialOptionKey(Options options, Enum<?> specialOptionKey, BiFunction<Options, Enum<?>, String> extractSpecialOption) {
		if (specialOptionKey != null && options.containsKey(specialOptionKey)) {
			// this will throw a CCE if non-String value is encountered in the option key and that is okay
			String specialOption = extractSpecialOption.apply(options, specialOptionKey);
			if (specialOption.startsWith(SPECIAL_OPTION_CHARACTER) && specialOption.endsWith(")")) {
				// strip of the leading and trailing characters so we end up with a field(value expression that can be split on the first occurence of
				// the ( character
				String fieldAndValueWithParenSeparator = specialOption.substring(1, specialOption.length() - 1);
				int separatorIdx = fieldAndValueWithParenSeparator.indexOf("(");
				if (separatorIdx != -1) {
					String field = fieldAndValueWithParenSeparator.substring(0, separatorIdx).toUpperCase();
					String value = fieldAndValueWithParenSeparator.substring(separatorIdx + 1);
					
					if (!CompareUtils.isEmpty(field) && !CompareUtils.isEmpty(value)) {
						OptionsBuilder newOptions = Options.builder().put(field, value);
						
						for (String key : options.keySet()) {
							if (!specialOptionKey.name().equals(key)) {
								newOptions.put(key, options.get(key));
							}
						}
						
						return newOptions.build();
					}
				}
			}
		}
		return options;
	}

	protected final List<Sort> sortBy() {
		return containsKey(SearchResourceRequest.OptionKey.SORT_BY) ? getList(SearchResourceRequest.OptionKey.SORT_BY, Sort.class) : null;
	}
	
	/**
	 * @return the currently set {@link SortBy} search option or if sort is not present in the request, the default sort which is by the configured {@link ID} document field.
	 */
	protected final SortBy querySortBy(C context) {
		List<Sort> sortBy = sortBy();
		if (!CompareUtils.isEmpty(sortBy)) {
			SortBy.Builder sortBuilder = SortBy.builder();
			for (Sort sort : sortBy) {
				toQuerySortBy(context, sortBuilder, sort);
			}
			return sortBuilder.build();
		}		
		return SortBy.DEFAULT;
	}
	
	/**
	 * Search requests may alter the default sortBy construction. By default it creates field and script sorts, but special sorts can be constructed using special sort keys.
	 * @param context - the context to access if needed
	 * @param sortBuilder - the builder to append to the query sort
	 * @param sort - the sort to convert to low-level query sort
	 */
	@OverridingMethodsMustInvokeSuper
	protected void toQuerySortBy(C context, Builder sortBuilder, Sort sort) {
		final Order order = sort.isAscending() ? Order.ASC : Order.DESC;
		if (sort instanceof SortField) {
			SortField sortField = (SortField) sort;
			sortBuilder.sortByField(sortField.getField(), order);
		} else if (sort instanceof SortScript) {
			SortScript sortScript = (SortScript) sort;
			sortBuilder.sortByScript(sortScript.getScript(), sortScript.getArguments(), order);
		} else {
			throw new UnsupportedOperationException("Cannot handle sort type " + sort);
		}
	}
	
	/**
	 * Creates a new empty result object with the specified offset and limit parameter.

	 * @param limit
	 * @return
	 */
	protected abstract B createEmptyResult(int limit);

	/**
	 * Executes this search request.
	 * 
	 * @param context
	 * @return
	 * @throws IOException
	 */
	protected abstract B doExecute(C context) throws IOException;
	
	/**
	 * Constructs the operator property name for the given property name.
	 * 
	 * @param property
	 * @return
	 */
	public static String operator(String property) {
		return String.format("%sOperator", property);
	}
	
}
