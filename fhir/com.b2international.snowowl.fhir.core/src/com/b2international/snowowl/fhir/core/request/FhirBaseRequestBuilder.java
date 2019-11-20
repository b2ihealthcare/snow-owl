/*
 * Copyright 2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.fhir.core.request;

import java.util.List;

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.events.BaseRequestBuilder;
import com.b2international.snowowl.core.events.Request;

/**
 * @since 7.2
 * @param <B>
 * @param <C>
 * @param <R>
 */
public abstract class FhirBaseRequestBuilder<B extends FhirBaseRequestBuilder<B, C, R>, C extends ServiceProvider, R> extends BaseRequestBuilder<B, C, R> {

	private List<ExtendedLocale> locales;
	
	public final B setLocales(List<ExtendedLocale> locales) {
		this.locales = locales;
		return getSelf();
	}
	
	@Override
	protected final Request<C, R> doBuild() {
		final FhirBaseRequest<C, R> req = createFhirRequest();
		req.setLocales(locales);
		return req;
	}

	protected abstract FhirBaseRequest<C, R> createFhirRequest();
	
}
