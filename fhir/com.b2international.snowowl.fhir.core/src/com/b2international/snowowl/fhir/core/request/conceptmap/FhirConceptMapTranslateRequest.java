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
package com.b2international.snowowl.fhir.core.request.conceptmap;

import org.elasticsearch.core.List;
import org.hl7.fhir.r5.model.ConceptMap;

import com.b2international.snowowl.core.RepositoryManager;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.fhir.core.R5ObjectFields;
import com.b2international.snowowl.fhir.core.operations.ConceptMapTranslateParameters;
import com.b2international.snowowl.fhir.core.operations.ConceptMapTranslateResultParameters;
import com.b2international.snowowl.fhir.core.request.FhirRequests;

/**
 * @since 8.0
 */
final class FhirConceptMapTranslateRequest implements Request<ServiceProvider, ConceptMapTranslateResultParameters> {

	private static final long serialVersionUID = 1L;
	
	private ConceptMapTranslateParameters parameters;

	public FhirConceptMapTranslateRequest(ConceptMapTranslateParameters parameters) {
		this.parameters = parameters;
	}
	
	@Override
	public ConceptMapTranslateResultParameters execute(ServiceProvider context) {
		ConceptMap conceptMap = FhirRequests.conceptMaps().prepareGet(parameters.getUrl().getValue())
				.setElements(List.copyOf(R5ObjectFields.ConceptMap.MANDATORY))
				.buildAsync().execute(context);
		return context.service(RepositoryManager.class)
				.get(conceptMap.getUserString("toolingId"))
				.optionalService(FhirConceptMapTranslator.class)
				.orElse(FhirConceptMapTranslator.NOOP)
				.translate(context, conceptMap, parameters);
	}

}
