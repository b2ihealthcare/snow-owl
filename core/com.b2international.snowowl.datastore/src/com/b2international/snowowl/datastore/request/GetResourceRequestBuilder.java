/*
 * Copyright 2016-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.function.Supplier;

import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.domain.CollectionResource;

/**
 * @since 5.2
 */
public abstract class GetResourceRequestBuilder<
	B extends GetResourceRequestBuilder<B, SB, C, R>, 
	SB extends SearchResourceRequestBuilder<SB, C, ? extends CollectionResource<R>>, 
	C extends ServiceProvider, 
	R> extends ResourceRequestBuilder<B, C, R> {
	
	private final String id;
	private final Class<R> type;
	private final Supplier<SB> searchRequestFactory;
	
	protected GetResourceRequestBuilder(final Class<R> type, final String id, Supplier<SB> searchRequestFactory) {
		super();
		this.type = type;
		this.id = id;
		this.searchRequestFactory = searchRequestFactory;
	}
	
	@Override
	protected final ResourceRequest<C, R> create() {
		return new GetResourceRequest<>(type, id, searchRequestFactory);
	}

}
