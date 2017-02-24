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

import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.b2international.commons.CompareUtils;
import com.b2international.commons.http.ExtendedLocale;
import com.b2international.commons.options.Options;
import com.b2international.commons.options.OptionsBuilder;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.events.Request;

/**
 * @since 4.6
 */
public abstract class BaseRevisionResourceRequestBuilder<B extends BaseRevisionResourceRequestBuilder<B, R>, R> extends BaseRevisionIndexReadRequestBuilder<B, R> {

	private Options expand = OptionsBuilder.newBuilder().build();
	private List<ExtendedLocale> locales = Collections.emptyList();
	private Set<String> fields = Collections.emptySet();
	
	public final B setLocales(List<ExtendedLocale> locales) {
		if (!CompareUtils.isEmpty(locales)) {
			this.locales = locales;
		}
		return getSelf();
	}
	
	/**
	 * Instructs the request to expand the result set returned to include certain fields.
	 * @param expand fields to be included in the returned components
	 * @return BaseRevisionResourceRequestBuilder
	 * @see SnomedConceptConverter
	 */
	public final B setExpand(String expand) {
		if (!CompareUtils.isEmpty(expand)) {
			this.expand = ExpandParser.parse(expand);
		}
		return getSelf();
	}
	
	/**
	 * Instructs the request to expand the result set returned to include certain optional fields.
	 * @param expand fields to be included in the returned components
	 * @return BaseRevisionResourceRequestBuilder
	 * @see SnomedConceptConverter
	 */
	public final B setExpand(Options expand) {
		if (!CompareUtils.isEmpty(expand)) {
			this.expand = expand;
		}
		return getSelf();
	}
	
	public final B setFields(Set<String> fields) {
		if (!CompareUtils.isEmpty(fields)) {
			this.fields = fields;
		}
		return getSelf();
	}
	
	@Override
	protected final Request<BranchContext, R> doBuild() {
		final BaseResourceRequest<BranchContext, R> req = create();
		req.setLocales(locales);
		req.setExpand(expand);
		req.setFields(fields);
		return req;
	}

	protected abstract BaseResourceRequest<BranchContext, R> create();
	
}
