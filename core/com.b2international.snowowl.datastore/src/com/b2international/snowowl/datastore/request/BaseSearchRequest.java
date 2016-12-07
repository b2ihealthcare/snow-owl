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
package com.b2international.snowowl.datastore.request;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.b2international.commons.options.Options;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 5.2
 */
public abstract class BaseSearchRequest<C extends ServiceProvider, B> extends BaseResourceRequest<C, B> {
	
	/**
	 * Exception that indicates that the search request will not have any matching items therefore can immediately respond back with an empty result.
	 * @since 5.4
	 */
	public static class NoResultException extends RuntimeException {
		private static final long serialVersionUID = 5581643581423131046L;
	}
	
	@Min(0)
	private int offset;

	@Min(0)
	private int limit;

	@NotNull
	private Options options;

	protected BaseSearchRequest() {}
	
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
	
}
