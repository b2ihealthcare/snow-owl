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

import static com.google.common.base.Preconditions.checkState;

import org.hibernate.validator.constraints.NotEmpty;

import com.b2international.commons.StringUtils;
import com.b2international.commons.exceptions.NotFoundException;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.domain.CollectionResource;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 5.2
 */
public abstract class GetResourceRequest<SB extends SearchResourceRequestBuilder<SB, C, ? extends CollectionResource<R>>, C extends ServiceProvider, R> extends ResourceRequest<C, R> {
	
	private static final long serialVersionUID = 1L;
	
	@NotEmpty
	@JsonProperty
	private final String id;
	
	protected GetResourceRequest(final String id) {
		this.id = id;
	}
	
	/**
	 * Creates a new {@link SearchResourceRequestBuilder} to search for the resource by its identifier.
	 * @return
	 */
	protected abstract SB createSearchRequestBuilder();
	
	@Override
	public R execute(final C context) {
		CollectionResource<R> items = createSearchRequestBuilder()
			.setLimit(2)
			.setFields(fields())
			.setLocales(locales())
			.setExpand(expand())
			.filterById(id)
			.build()
			.execute(context);
		checkState(items.getItems().size() <= 1, "Multiple documents found for '%s'.", id);
		return items
			.first()
			.orElseThrow(() -> new NotFoundException(StringUtils.splitCamelCaseAndCapitalize(getReturnType().getSimpleName()), id));
	}

}
