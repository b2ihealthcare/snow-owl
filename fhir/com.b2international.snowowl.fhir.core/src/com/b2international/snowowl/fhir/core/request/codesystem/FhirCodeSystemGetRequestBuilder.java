/*
 * Copyright 2021-2024 B2i Healthcare, https://b2ihealthcare.com
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

import com.b2international.snowowl.fhir.core.request.FhirResourceGetRequestBuilder;

/**
 * @since 8.0
 */
public final class FhirCodeSystemGetRequestBuilder 
		extends FhirResourceGetRequestBuilder<FhirCodeSystemGetRequestBuilder, FhirCodeSystemSearchRequestBuilder, CodeSystem> {

	public FhirCodeSystemGetRequestBuilder(String idOrUrl) {
		super(new FhirCodeSystemGetRequest(idOrUrl));
	}
	
}
