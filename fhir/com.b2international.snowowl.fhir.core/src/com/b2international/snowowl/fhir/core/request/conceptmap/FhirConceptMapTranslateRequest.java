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
package com.b2international.snowowl.fhir.core.request.conceptmap;

import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.fhir.core.model.conceptmap.ConceptMap;
import com.b2international.snowowl.fhir.core.model.conceptmap.TranslateRequest;
import com.b2international.snowowl.fhir.core.model.conceptmap.TranslateResult;
import com.b2international.snowowl.fhir.core.request.FhirRequests;

/**
 * @since 8.0
 */
final class FhirConceptMapTranslateRequest implements Request<ServiceProvider, TranslateResult> {

	private static final long serialVersionUID = 1L;
	
	private TranslateRequest request;

	public FhirConceptMapTranslateRequest(TranslateRequest request) {
		this.request = request;
	}
	
	@Override
	public TranslateResult execute(ServiceProvider context) {
		ConceptMap conceptMap = FhirRequests.conceptMaps().prepareGet(request.getUrlValue()).buildAsync().execute(context);
		return context.optionalService(FhirConceptMapTranslator.class).orElse(FhirConceptMapTranslator.NOOP).translate(context, conceptMap, request);
	}

}
