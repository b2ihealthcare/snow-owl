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

import java.util.List;
import java.util.stream.Stream;

import org.hl7.fhir.r5.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.r5.model.ConceptMap;

import com.b2international.fhir.r5.operations.ConceptMapTranslateParameters;
import com.b2international.fhir.r5.operations.ConceptMapTranslateResultParameters;
import com.b2international.snowowl.core.RepositoryManager;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.fhir.core.R5ObjectFields;
import com.b2international.snowowl.fhir.core.exceptions.BadRequestException;
import com.b2international.snowowl.fhir.core.request.FhirRequests;

/**
 * @since 8.0
 */
final class FhirConceptMapTranslateRequest implements Request<ServiceProvider, ConceptMapTranslateResultParameters> {

	private static final long serialVersionUID = 1L;
	
	private final ConceptMapTranslateParameters parameters;

	public FhirConceptMapTranslateRequest(ConceptMapTranslateParameters parameters) {
		this.parameters = parameters;
		
		// One (and only one) of the in parameters (sourceCode, sourceCoding, sourceCodeableConcept, targetCode, targetCoding, or targetCodeableConcept) SHALL be provided, to identify the code that is to be translated.
		final long nonNullInputs = Stream.of(
			parameters.getSourceCode(), 
			parameters.getSourceCoding(), 
			parameters.getSourceCodeableConcept(),
			parameters.getTargetCode(),
			parameters.getTargetCoding(),
			parameters.getTargetCodeableConcept()
		)
		.filter(p -> p != null)
		.count();
		
		if (nonNullInputs != 1L) {
			throw new BadRequestException("One (and only one) of the 'in' parameters (sourceCode, sourceCoding, sourceCodeableConcept, targetCode, targetCoding, targetCodeableConcept) must be provided to identify the code that is to be translated.");
		}
	}
	
	@Override
	public ConceptMapTranslateResultParameters execute(ServiceProvider context) {
		ConceptMap conceptMap = lookupConceptMaps(context);
		return context.service(RepositoryManager.class)
				.get(conceptMap.getUserString("toolingId"))
				.optionalService(FhirConceptMapTranslator.class)
				.orElse(FhirConceptMapTranslator.NOOP)
				.translate(context, conceptMap, parameters);
	}

	// TODO make this consider source/target scopes to find appropriate ConceptMaps when URL is not defined, for now we basically need the URL parameter to be able to translate using a dedicated map
	private ConceptMap lookupConceptMaps(ServiceProvider context) {
		if (parameters.getUrl() == null) {
			throw new BadRequestException("'url' is required to reduce the scope of the translate operation to a single ConceptMap");
		}
		return FhirRequests.conceptMaps().prepareSearch()
			.filterByUrl(parameters.getUrl().getValue())
			.setElements(List.copyOf(R5ObjectFields.ConceptMap.MANDATORY))
			.setCount(1)
			.buildAsync()
			.execute(context)
			.getEntry()
			.stream()
			.map(BundleEntryComponent::getResource)
			.filter(ConceptMap.class::isInstance)
			.map(ConceptMap.class::cast)
			.findFirst()
			.orElse(null);
	}

}
