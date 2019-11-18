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
import com.b2international.snowowl.core.events.BaseRequestBuilder;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.request.SystemRequestBuilder;
import com.b2international.snowowl.fhir.core.model.codesystem.CodeSystem;

/**
 * @since 7.2
 */
public final class FhirCodeSystemGetRequestBuilder extends BaseRequestBuilder<FhirCodeSystemGetRequestBuilder, ServiceProvider, CodeSystem> implements SystemRequestBuilder<CodeSystem> {

	private final String logicalId;
	
	FhirCodeSystemGetRequestBuilder(String logicalId) {
		this.logicalId = logicalId;
	}
	
	@Override
	protected Request<ServiceProvider, CodeSystem> doBuild() {
		return new FhirCodeSystemGetRequest(logicalId);
	}

}
