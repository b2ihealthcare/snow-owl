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

import java.util.List;
import java.util.stream.Collectors;

import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.domain.Concept;
import com.b2international.snowowl.fhir.core.model.Designation;
import com.b2international.snowowl.fhir.core.model.codesystem.CodeSystem;
import com.b2international.snowowl.fhir.core.model.codesystem.LookupRequest;
import com.b2international.snowowl.fhir.core.model.codesystem.Property;
import com.b2international.snowowl.fhir.core.model.codesystem.SupportedCodeSystemRequestProperties;

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
	 * @param request
	 * @return
	 */
	default String configureConceptExpand(LookupRequest request) {
		return null;
	}

	/**
	 * Implementers may offer custom designation list based on the loaded concept's details or based on the information available in their tooling
	 * repository. The default implementation returns the alternative terms listed in the generic {@link Concept} representation of the code.
	 * 
	 * @param context
	 * @param codeSystem
	 * @param concept
	 * @param request
	 * @param acceptLanguage
	 * @return
	 */
	default List<Designation> expandDesignations(ServiceProvider context, CodeSystem codeSystem, Concept concept, LookupRequest request, String acceptLanguage) {
		if (request.isPropertyRequested(SupportedCodeSystemRequestProperties.DESIGNATION)) {
			return concept.getAlternativeTerms().stream().map(term -> Designation.builder().value(term).build()).collect(Collectors.toList());
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
	 * @param request
	 * @return
	 */
	default List<Property> expandProperties(ServiceProvider context, CodeSystem codeSystem, Concept concept, LookupRequest request) {
		return null;
	}

}
