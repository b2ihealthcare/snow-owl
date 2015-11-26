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

import java.util.Collections;
import java.util.List;

import com.b2international.commons.CompareUtils;
import com.b2international.commons.http.ExtendedLocale;
import com.b2international.commons.options.Options;
import com.b2international.commons.options.OptionsBuilder;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.events.Request;

/**
 * @since 4.5
 */
public abstract class GetRequestBuilder<B extends GetRequestBuilder<B, R>, R> extends BaseBranchRequestBuilder<B, R> {

	private String componentId;
	private Options expand = OptionsBuilder.newBuilder().build();
	private List<ExtendedLocale> locales = Collections.emptyList();

	protected GetRequestBuilder(String repositoryId) {
		super(repositoryId);
	}
	
	public final B setComponentId(String componentId) {
		this.componentId = componentId;
		return getSelf();
	}
	
	public final B setExpand(String expand) {
		if (!CompareUtils.isEmpty(expand)) {
			this.expand = ExpandParser.parse(expand);
		}
		return getSelf();
	}
	
	public final B setLocales(List<ExtendedLocale> locales) {
		if (!CompareUtils.isEmpty(locales)) {
			this.locales = locales;
		}
		return getSelf();
	}
	
	@Override
	protected final Request<BranchContext, R> wrap(Request<BranchContext, R> req) {
		return new IndexReadRequest<>(req);
	}
	
	@Override
	protected final Request<BranchContext, R> doBuild() {
		final GetRequest<R> req = create();
		req.setComponentId(componentId);
		req.setExpand(expand);
		req.setLocales(locales);
		return req;
	}
	
	protected abstract GetRequest<R> create();
	
}
