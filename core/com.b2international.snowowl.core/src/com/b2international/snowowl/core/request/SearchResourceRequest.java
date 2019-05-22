/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.b2international.commons.StringUtils;
import com.b2international.commons.options.Options;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Strings;

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
		 * Sort result by the specified sort fields.
		 */
		SORT_BY;
	}

	public static interface Sort extends Serializable {
		
	}
	
	public static class SortField implements Sort {
		
		private static final long serialVersionUID = 1L;
		
		private final String field;
		private final boolean ascending;
		
		private SortField(String field, boolean ascending) {
			this.field = field;
			this.ascending = ascending;
		}
		
		public String getField() {
			return field;
		}
		
		public boolean isAscending() {
			return ascending;
		}
		
		public static SortField of(String field, boolean ascending) {
			return new SortField(field, ascending);
		}
		
		public static SortField ascending(String field) {
			return of(field, true);
		}
		
		public static SortField descending(String field) {
			return of(field, false);
		}
		
	}
	
	public static class SortScript implements Sort {
		
		private static final long serialVersionUID = 1L;
		
		private final String script;
		private final Map<String, Object> arguments;
		private final boolean ascending;
		
		private SortScript(final String script,
				final Map<String, Object> arguments,
				final boolean ascending) {
			this.script = script;
			this.arguments = arguments;
			this.ascending = ascending;
		}

		public String getScript() {
			return script;
		}
		
		public Map<String, Object> getArguments() {
			return arguments;
		}
		
		public boolean isAscending() {
			return ascending;
		}
		
		public static SortScript of(String script, final Map<String, Object> arguments, boolean ascending) {
			return new SortScript(script, arguments, ascending);
		}
		
		public static SortScript ascending(String script, final Map<String, Object> arguments) {
			return of(script, arguments, true);
		}
		
		public static SortScript descending(String script, final Map<String, Object> arguments) {
			return of(script, arguments, false);
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
	
	private String scrollId;
	private String scrollKeepAlive;
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
	
	@JsonIgnore
	protected final boolean isScrolled() {
		return !Strings.isNullOrEmpty(scrollId());
	}
	
	void setScrollId(String scrollId) {
		this.scrollId = scrollId;
	}
	
	void setScrollKeepAlive(String scrollKeepAlive) {
		this.scrollKeepAlive = scrollKeepAlive;
	}
	
	void setSearchAfter(String searchAfter) {
		this.searchAfter = searchAfter;
	}
	
	void setComponentIds(Set<String> componentIds) {
		this.componentIds = componentIds;
	}

	@JsonProperty
	protected final String scrollId() {
		return scrollId;
	}
	
	@JsonProperty
	protected final String scrollKeepAlive() {
		return scrollKeepAlive;
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
	
	protected final Set<String> componentIds() {
		return componentIds;
	}

	/**
	 * @return
	 */
	@JsonProperty("componentIds")
	String getTruncatedComponentIdValues() {
		return componentIds == null ? null : StringUtils.limitedToString(componentIds, 10);
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
		if (componentIds != null) {
			return reducer.apply(input, componentIds);
		} else {
			return input;
		}
	}
	
	@Override
	public final B execute(C context) {
		try {
			return doExecute(context);
		} catch (NoResultException e) {
			return createEmptyResult(limit);
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
	protected abstract B createEmptyResult(int limit);

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
