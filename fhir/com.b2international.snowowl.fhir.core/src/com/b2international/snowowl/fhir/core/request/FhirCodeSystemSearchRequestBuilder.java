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

import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.request.SearchResourceRequest;
import com.b2international.snowowl.core.request.SearchResourceRequestBuilder;
import com.b2international.snowowl.core.request.SystemRequestBuilder;
import com.b2international.snowowl.fhir.core.model.Bundle;
import com.b2international.snowowl.fhir.core.request.FhirCodeSystemSearchRequest.OptionKey;

/**
 * @since 7.2
 */
public final class FhirCodeSystemSearchRequestBuilder 
		extends SearchResourceRequestBuilder<FhirCodeSystemSearchRequestBuilder, ServiceProvider, Bundle>
		implements SystemRequestBuilder<Bundle> {

	private String uri = "unknown";
	
	public FhirCodeSystemSearchRequestBuilder setUri(String uri) {
		this.uri = uri;
		return getSelf();
	}

	public FhirCodeSystemSearchRequestBuilder filterBySystem(String system) {
		return addOption(OptionKey.SYSTEM, system);
	}
	
	public FhirCodeSystemSearchRequestBuilder filterBySystems(Iterable<String> systems) {
		return addOption(OptionKey.SYSTEM, systems);
	}
	
	@Override
	protected SearchResourceRequest<ServiceProvider, Bundle> createSearch() {
		final FhirCodeSystemSearchRequest req = new FhirCodeSystemSearchRequest();
		req.setUri(uri);
		return req;
	}

}
