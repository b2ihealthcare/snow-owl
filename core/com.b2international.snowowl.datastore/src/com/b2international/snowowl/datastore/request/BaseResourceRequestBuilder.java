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

import java.util.Collections;
import java.util.List;

import com.b2international.commons.CompareUtils;
import com.b2international.commons.http.ExtendedLocale;
import com.b2international.commons.options.Options;
import com.b2international.commons.options.OptionsBuilder;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.events.Request;

/**
 * @since 4.6
 */
public abstract class BaseResourceRequestBuilder<B extends BaseResourceRequestBuilder<B, R>, R> extends BaseIndexReadRequestBuilder<B, R> {

	private Options expand = OptionsBuilder.newBuilder().build();
	private List<ExtendedLocale> locales = Collections.emptyList();
	
	protected BaseResourceRequestBuilder(final String repositoryId) {
		super(repositoryId);
	}
	
	/**
	 * Sets the request to return the preferred locale for the returned display labels.
	 * Typical way to obtain the preferred locales: 
	 * <p>
	 * {@code private final List<ExtendedLocale> locales = ApplicationContext.getInstance().getService(LanguageSetting.class).getLanguagePreference()}
	 * 
	 * 
	 * @param locales for the labels returns by the request
	 * @return BaseResourceRequestBuilder   
	 */
	public final B setLocales(List<ExtendedLocale> locales) {
		if (!CompareUtils.isEmpty(locales)) {
			this.locales = locales;
		}
		return getSelf();
	}
	
	public final B setExpand(String expand) {
		if (!CompareUtils.isEmpty(expand)) {
			this.expand = ExpandParser.parse(expand);
		}
		return getSelf();
	}
	
	public final B setExpand(Options expand) {
		if (!CompareUtils.isEmpty(expand)) {
			this.expand = expand;
		}
		return getSelf();
	}
	
	@Override
	protected final Request<BranchContext, R> doBuild() {
		final BaseResourceRequest<BranchContext, R> req = create();
		req.setLocales(locales);
		req.setExpand(expand);
		return req;
	}

	protected abstract BaseResourceRequest<BranchContext, R> create();
	
}
