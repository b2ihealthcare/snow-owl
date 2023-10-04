/*
 * Copyright 2021-2022 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.fhir.core.request.conceptmap;

import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.request.resource.ResourceDeleteRequestBuilder;

/**
 * @since 8.0
 */
public class FhirConceptMapRequests {

	// XXX: Constant needs to be repeated because we don't have access to the original
	private static final String RESOURCE_TYPE = "valuesets";

	public FhirConceptMapUpdateRequestBuilder prepareUpdate() {
		return new FhirConceptMapUpdateRequestBuilder();
	}

	public FhirConceptMapSearchRequestBuilder prepareSearch() {
		return new FhirConceptMapSearchRequestBuilder();
	}
	
	public FhirConceptMapGetRequestBuilder prepareGet(String idOrUrl) {
		return new FhirConceptMapGetRequestBuilder(idOrUrl);
	}

	public FhirConceptMapTranslateRequestBuilder prepareTranslate() {
		return new FhirConceptMapTranslateRequestBuilder();
	}
	
	public ResourceDeleteRequestBuilder prepareDelete(final String valueSetId) {
		return new ResourceDeleteRequestBuilder(ResourceURI.of(RESOURCE_TYPE, valueSetId));
	}
}
