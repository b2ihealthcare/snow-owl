/*
 * Copyright 2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.fhir.core.request.codesystem;

import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.events.BaseRequestBuilder;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.request.SystemRequestBuilder;
import com.b2international.snowowl.fhir.core.model.codesystem.LookupRequest;
import com.b2international.snowowl.fhir.core.model.codesystem.LookupResult;

/**
 * @since 8.0
 */
public final class FhirLookupRequestBuilder 
		extends BaseRequestBuilder<FhirLookupRequestBuilder, ServiceProvider, LookupResult>
		implements SystemRequestBuilder<LookupResult> {

	private LookupRequest request;
	
	public FhirLookupRequestBuilder setRequest(LookupRequest request) {
		this.request = request;
		return getSelf();
	}
	
	@Override
	protected Request<ServiceProvider, LookupResult> doBuild() {
		return new FhirLookupRequest(request);
	}

}
