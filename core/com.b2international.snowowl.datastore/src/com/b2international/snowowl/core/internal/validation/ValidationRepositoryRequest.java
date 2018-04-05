/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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

import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.events.DelegatingRequest;
import com.b2international.snowowl.core.events.Request;

/**
 * @since 6.3
 */
public final class ValidationRepositoryRequest<B> extends DelegatingRequest<ServiceProvider, ValidationRepositoryContext, B>{

	ValidationRepositoryRequest(Request<ValidationRepositoryContext, B> next) {
		super(next);
	}

	@Override
	public B execute(ServiceProvider context) {
		ValidationRepositoryContext validationContext = new ValidationRepositoryContext(context);
		B response = next(validationContext);
		validationContext.commit();
		return response;
	}

}