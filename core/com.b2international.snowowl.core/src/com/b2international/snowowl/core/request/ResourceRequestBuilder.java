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
package com.b2international.snowowl.core.request;

import java.util.Collections;
import java.util.List;

import com.b2international.commons.CompareUtils;
import com.b2international.commons.http.ExtendedLocale;
import com.b2international.commons.options.Options;
import com.b2international.commons.options.OptionsBuilder;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.events.BaseRequestBuilder;
import com.b2international.snowowl.core.events.Request;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

/**
 * @since 5.2
 */
public abstract class ResourceRequestBuilder<B extends ResourceRequestBuilder<B, C, R>, C extends ServiceProvider, R> extends BaseRequestBuilder<B, C, R> {

	private Options expand = OptionsBuilder.newBuilder().build();
	private List<ExtendedLocale> locales = Collections.emptyList();
	private List<String> fields = Collections.emptyList();

	protected ResourceRequestBuilder() {}
	
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
	
	public final B setFields(String first, String... rest) {
		return setFields(Lists.asList(first, rest));
	}
	
	public final B setFields(List<String> fields) {
		if (!CompareUtils.isEmpty(fields)) {
				this.fields = ImmutableList.copyOf(fields);
		}
		return getSelf();
	}

	@Override
	protected final Request<C, R> doBuild() {
		final ResourceRequest<C, R> req = create();
		req.setLocales(locales);
		req.setExpand(expand);
		req.setFields(fields);
		return req;
	}

	protected abstract ResourceRequest<C, R> create();

}
