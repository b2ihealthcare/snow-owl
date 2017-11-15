/*
 * Copyright 2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.internal.validation;

import com.b2international.index.DocSearcher;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.domain.DelegatingServiceProvider;
import com.b2international.snowowl.core.events.AsyncRequest;
import com.b2international.snowowl.core.events.DelegatingRequest;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.request.SystemRequestBuilder;

/**
 * @since 6.0
 * @param <R> - the return type
 */
public interface ValidationRepositoryReadRequestBuilder<R> extends SystemRequestBuilder<R> {

	@Override
	default AsyncRequest<R> buildAsync() {
		return new AsyncRequest<R>(
			new ValidationRepositoryReadRequest<>(build())
		);
	}
	
	final class ValidationRepositoryReadRequest<R> extends DelegatingRequest<ServiceProvider, ServiceProvider, R> {

		ValidationRepositoryReadRequest(Request<ServiceProvider, R> next) {
			super(next);
		}

		@Override
		public R execute(ServiceProvider context) {
			return context.service(ValidationRepository.class).read(searcher -> {
				return next().execute(DelegatingServiceProvider.basedOn(context)
						.bind(DocSearcher.class, searcher)
						.build());
			});
		}

	}
	
}
