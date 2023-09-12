/*
 * Copyright 2021-2023 B2i Healthcare Pte Ltd, http://b2i.sg
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
import com.b2international.snowowl.fhir.core.model.codesystem.LookupDesignation;
import com.b2international.snowowl.fhir.core.model.codesystem.LookupProperty;
import com.b2international.snowowl.fhir.core.model.codesystem.LookupRequest;
import com.b2international.snowowl.fhir.core.model.codesystem.LookupRequestProperties;

/**
 * @since 8.0
 */
public interface FhirCodeSystemLookupConverter {

	FhirCodeSystemLookupConverter DEFAULT = new FhirCodeSystemLookupConverter() { };

	/**
	 * Implementers may need to load additional data from the underlying
	 * CodeSystem's tooling repository to offer the best possible lookup result and
	 * it can be done by requested expansion of additional data via Snow Owl's
	 * Expand API. This method by default does not request load of any additional
	 * data.
	 * 
	 * @param request
	 * @return
	 */
	default String configureConceptExpand(LookupRequest request) {
		return null;
	}

	/**
	 * Implementers may offer custom designation list based on the loaded concept's
	 * details or based on the information available in their tooling repository.
	 * <p>
	 * The default implementation returns the alternative terms listed in the
	 * generic {@link Concept} representation of the code, if it has been requested.
	 * 
	 * @param context
	 * @param codeSystem
	 * @param concept
	 * @param request
	 * @param acceptLanguage
	 * @return
	 */
	default List<LookupDesignation> expandDesignations(ServiceProvider context, CodeSystem codeSystem, Concept concept, LookupRequest request, String acceptLanguage) {
		if (!request.containsProperty(LookupRequestProperties.DESIGNATION.getCode())) {
			return null;
		}
		
		return concept.getAlternativeTerms()
			.stream()
			.map(term -> {
				final LookupDesignation designation = new LookupDesignation();
				// designation.setLanguage(...) can not be set, we don't have enough information
				designation.setValue(term);
				// designation.setUse(...) can not be set either
				return designation;
			})
			.collect(Collectors.toList());
	}

	/**
	 * Implementers may offer custom property list based on the loaded concept's
	 * details or based on the information available in their tooling repository.
	 * The default implementation returns no properties at all.
	 * 
	 * @param context
	 * @param codeSystem
	 * @param concept
	 * @param request
	 * @return
	 */
	default List<LookupProperty> expandProperties(ServiceProvider context, CodeSystem codeSystem, Concept concept, LookupRequest request) {
		return null;
	}
}
