/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.server.request;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.apache.lucene.search.Filter;

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.commons.options.Options;
import com.b2international.snowowl.core.api.index.IndexException;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.events.BaseRequest;
import com.b2international.snowowl.datastore.index.mapping.Mappings;

/**
 * @since 4.5
 */
public abstract class SearchRequest<B> extends BaseRequest<BranchContext, B> {

	@Min(0)
	private int offset;
	
	@Min(0)
	private int limit;
	
	@NotNull
	private Options options;
	
	@NotNull
	private List<String> expand;
	
	@NotNull
	private List<ExtendedLocale> locales;
	
	@NotNull
	private Collection<String> componentIds;
	
	protected SearchRequest() {}
	
	void setLimit(int limit) {
		this.limit = limit;
	}
	
	void setOffset(int offset) {
		this.offset = offset;
	}
	
	void setOptions(Options options) {
		this.options = options;
	}
	
	void setExpand(List<String> expand) {
		this.expand = expand;
	}
	
	void setLocales(List<ExtendedLocale> locales) {
		this.locales = locales;
	}
	
	void setComponentIds(Collection<String> componentIds) {
		this.componentIds = componentIds;
	}
	
	protected final int offset() {
		return offset;
	}
	
	protected final int limit() {
		return limit;
	}
	
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
	
	protected final List<String> expand() {
		return expand;
	}
	
	protected final Collection<String> componentIds() {
		return componentIds;
	}
	
	protected final List<ExtendedLocale> locales() {
		return locales;
	}
	
	protected Filter createComponentIdFilter() {
		return Mappings.id().createTermsFilter(componentIds);
	}
	
	@Override
	public final B execute(BranchContext context) {
		try {
			return doExecute(context);
		} catch (IOException e) {
			throw new IndexException("Caught exception while executing search request.", e);
		}
	}

	protected abstract B doExecute(BranchContext context) throws IOException;

}
