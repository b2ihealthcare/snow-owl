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

import java.util.List;
import java.util.stream.Collectors;

import org.hl7.fhir.r5.model.CodeSystem;

import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.domain.Concept;
import com.b2international.snowowl.fhir.core.operations.CodeSystemLookupParameters;

/**
 * @since 8.0
 */
public interface FhirCodeSystemLookupConverter {

	FhirCodeSystemLookupConverter DEFAULT = new FhirCodeSystemLookupConverter() {
	};

	/**
	 * Implementers may need to load additional data from the underlying CodeSystem's tooling repository to offer the best possible lookup result and
	 * it can be done by requested expansion of additional data via Snow Owl's Expand API. This method by default does not request load of any
	 * additional data.
	 * 
	 * @param parameters
	 * @return
	 */
	default String configureConceptExpand(CodeSystemLookupParameters parameters) {
		return null;
	}

	/**
	 * Implementers may offer custom designation list based on the loaded concept's details or based on the information available in their tooling
	 * repository. The default implementation returns the alternative terms listed in the generic {@link Concept} representation of the code.
	 * 
	 * @param context
	 * @param codeSystem
	 * @param concept
	 * @param parameters
	 * @param acceptLanguage
	 * @return
	 */
	default List<CodeSystem.ConceptDefinitionDesignationComponent> expandDesignations(ServiceProvider context, CodeSystem codeSystem, Concept concept, CodeSystemLookupParameters parameters, String acceptLanguage) {
		if (parameters.isPropertyRequested(SupportedCodeSystemRequestProperties.DESIGNATION)) {
			return concept.getDescriptions()
				.stream()
				.map(description -> new CodeSystem.ConceptDefinitionDesignationComponent().setValue(description.getTerm()).setLanguage(description.getLanguage()))
				.collect(Collectors.toList());
		} else {
			return null;
		}
	}

	/**
	 * Implementers may offer custom property list based on the loaded concept's details or based on the information available in their tooling
	 * repository. The default implementation returns no properties at all.
	 * 
	 * @param context
	 * @param codeSystem
	 * @param concept
	 * @param parameters
	 * @return
	 */
	default List<CodeSystem.ConceptPropertyComponent> expandProperties(ServiceProvider context, CodeSystem codeSystem, Concept concept, CodeSystemLookupParameters parameters) {
		return null;
	}

}
