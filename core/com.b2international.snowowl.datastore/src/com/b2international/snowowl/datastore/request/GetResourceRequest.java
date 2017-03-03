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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.function.Supplier;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.b2international.commons.StringUtils;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.domain.CollectionResource;
import com.b2international.snowowl.core.exceptions.NotFoundException;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 5.2
 */
public final class GetResourceRequest<B extends SearchResourceRequestBuilder<B, C, ? extends CollectionResource<R>>, C extends ServiceProvider, R> extends ResourceRequest<C, R> {
	
	@NotNull
	private final Supplier<B> searchRequestFactory;
	
	@NotNull
	private final Class<R> type;
	
	@NotEmpty
	@JsonProperty
	private final String id;
	
	GetResourceRequest(final Class<R> type, final String id, final Supplier<B> searchRequestFactory) {
		this.type = checkNotNull(type, "type");
		this.id = id;
		this.searchRequestFactory = searchRequestFactory;
	}
	
	@Override
	public Class<R> getReturnType() {
		return type;
	}

	@Override
	public R execute(final C context) {
		return searchRequestFactory.get()
			.setLimit(2)
			.setFields(fields())
			.setLocales(locales())
			.setExpand(expand())
			.filterById(id)
			.build()
			.execute(context)
			.first()
			.orElseThrow(() -> new NotFoundException(StringUtils.splitCamelCaseAndCapitalize(type.getSimpleName()), id));
	}

}
