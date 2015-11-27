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
package com.b2international.snowowl.datastore.request;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.b2international.commons.CompareUtils;
import com.b2international.commons.http.ExtendedLocale;
import com.b2international.commons.options.Options;
import com.b2international.commons.options.OptionsBuilder;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.datastore.request.SearchRequest.OptionKey;

/**
 * @since 4.5
 */
public abstract class SearchRequestBuilder<B extends SearchRequestBuilder<B, R>, R> extends BaseBranchRequestBuilder<B, R> {

	private static final int MAX_LIMIT = Integer.MAX_VALUE - 1;
	
	private int offset = 0;
	private int limit = 50;
	private Collection<String> componentIds = Collections.emptyList();
	private List<ExtendedLocale> locales = Collections.emptyList();
	private final OptionsBuilder optionsBuilder = OptionsBuilder.newBuilder();
	
	protected SearchRequestBuilder(String repositoryId) {
		super(repositoryId);
	}
	
	public final B setOffset(int offset) {
		this.offset = offset;
		return getSelf();
	}
	
	public final B setLimit(int limit) {
		this.limit = limit;
		return getSelf();
	}
	
	public final B setExpand(String expand) {
		if (!CompareUtils.isEmpty(expand)) {
			addOption(OptionKey.EXPAND, ExpandParser.parse(expand));
		}
		return getSelf();
	}
	
	public final B setExpand(Options expand) {
		addOption(OptionKey.EXPAND, expand);
		return getSelf();
	}
	
	public final B setLocales(List<ExtendedLocale> locales) {
		if (!CompareUtils.isEmpty(locales)) {
			this.locales = locales;
		}
		return getSelf();
	}
	
	public final B setComponentIds(Collection<String> componentIds) {
		this.componentIds = componentIds;
		return getSelf();
	}
	
	public final B all() {
		return setOffset(0).setLimit(MAX_LIMIT);
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
	
	@Override
	protected Request<BranchContext, R> wrap(Request<BranchContext, R> req) {
		return new IndexReadRequest<>(super.wrap(req));
	}
	
	@Override
	protected final Request<BranchContext, R> doBuild() {
		final SearchRequest<R> req = create();
		req.setOffset(offset);
		req.setLimit(Math.min(limit, MAX_LIMIT - offset));
		req.setLocales(locales);
		req.setComponentIds(componentIds);
		req.setOptions(optionsBuilder.build());
		return req;
	}
	
	protected abstract SearchRequest<R> create();

}
