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

import org.hl7.fhir.r5.model.CodeSystem;

import com.b2international.snowowl.fhir.core.request.FhirResourceGetRequest;

/**
 * @since 8.0
 */
final class FhirCodeSystemGetRequest extends FhirResourceGetRequest<FhirCodeSystemSearchRequestBuilder, CodeSystem> {

	private static final long serialVersionUID = 1L;
	
	public FhirCodeSystemGetRequest(String idOrUrl) {
		super(idOrUrl);
	}

	@Override
	protected FhirCodeSystemSearchRequestBuilder prepareSearch() {
		return new FhirCodeSystemSearchRequestBuilder();
	}
}
