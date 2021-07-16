/*
 * Copyright 2020-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
import com.b2international.commons.options.Options;
import com.b2international.commons.options.OptionsBuilder;
import com.b2international.snowowl.core.ServiceProvider;
import com.google.common.collect.Lists;

/**
 * @since 7.5
 */
public abstract class IndexResourceRequestBuilder<B extends IndexResourceRequestBuilder<B, C, R>, C extends ServiceProvider, R> extends ResourceRequestBuilder<B, C, R> {

	private Options expand = OptionsBuilder.newBuilder().build();
	private List<String> fields = Collections.emptyList();

	protected IndexResourceRequestBuilder() {}
	
	public final B setExpand(String...expand) {
		if (!CompareUtils.isEmpty(expand)) {
			return setExpand(String.join(",", expand));
		}
		return getSelf();
	}
	
	public final B setExpand(List<String> expand) {
		if (!CompareUtils.isEmpty(expand)) {
			return setExpand(String.join(",", expand));
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
			this.fields = List.copyOf(fields);
		}
		return getSelf();
	}
	
	@Override
	protected void init(ResourceRequest<C, R> req) {
		super.init(req);
		((IndexResourceRequest<C, R>) req).setExpand(expand);
		((IndexResourceRequest<C, R>) req).setFields(fields);
	}

	@Override
	protected abstract IndexResourceRequest<C, R> create();

}
