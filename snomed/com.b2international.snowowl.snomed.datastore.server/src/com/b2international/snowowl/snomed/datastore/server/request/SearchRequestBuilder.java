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

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.b2international.commons.CompareUtils;
import com.b2international.commons.http.ExtendedLocale;
import com.b2international.commons.options.OptionsBuilder;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.events.RequestBuilder;
import com.b2international.snowowl.datastore.request.RepositoryRequests;

/**
 * @since 4.5
 */
public abstract class SearchRequestBuilder<B extends SearchRequestBuilder<B, R>, R> implements RequestBuilder<BranchContext, R> {

	private final String repositoryId;
	
	private int offset = 0;
	private int limit = 50;
	private Collection<String> componentIds = Collections.emptyList();
	private List<String> expand = Collections.emptyList();
	private List<ExtendedLocale> locales = Collections.emptyList();
	private final OptionsBuilder optionsBuilder = OptionsBuilder.newBuilder();
	
	protected SearchRequestBuilder(String repositoryId) {
		this.repositoryId = repositoryId;
	}
	
	public final B setOffset(int offset) {
		this.offset = offset;
		return getSelf();
	}
	
	public final B setLimit(int limit) {
		this.limit = limit;
		return getSelf();
	}
	
	public final B setExpand(List<String> expand) {
		this.expand = expand;
		return getSelf();
	}
	
	public final B setLocales(List<ExtendedLocale> locales) {
		this.locales = locales;
		return getSelf();
	}
	
	public final B setComponentIds(Collection<String> componentIds) {
		this.componentIds = componentIds;
		return getSelf();
	}
	
	public final B all() {
		return setOffset(0).setLimit(Integer.MAX_VALUE);
	}
	
	public final B one() {
		return setOffset(0).setLimit(1);
	}
	
	// XXX: Does not allow empty-ish values
	protected final B addOption(String key, Object value) {
		if (!CompareUtils.isEmpty(value)) {
			optionsBuilder.put(key, value);
		}
		return getSelf();
	}
	
	protected final B addOption(Enum<?> key, Object value) {
		return addOption(key.name(), value);
	}
	
	public final Request<ServiceProvider, R> build(String branch) {
		return RepositoryRequests.wrap(repositoryId, branch, RepositoryRequests.toIndexReadRequest(build()));
	}
	
	@Override
	public final Request<BranchContext, R> build() {
		final SearchRequest<R> req = create();
		req.setLimit(limit);
		req.setOffset(offset);
		req.setExpand(expand);
		req.setLocales(locales);
		req.setComponentIds(componentIds);
		req.setOptions(optionsBuilder.build());
		return req;
	}
	
	protected abstract SearchRequest<R> create();

	protected final B getSelf() {
		return (B) this;
	}
}
