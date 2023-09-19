/*
 * Copyright 2023 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.fhir.core.model.r5;

import java.util.Collection;

import com.b2international.snowowl.fhir.core.model.conceptmap.Match;
import com.b2international.snowowl.fhir.core.model.conceptmap.TranslateResult;
import com.b2international.snowowl.fhir.core.model.dt.Code;
import com.b2international.snowowl.fhir.core.model.dt.Coding;
import com.b2international.snowowl.fhir.core.model.dt.Uri;

/**
 * @since 9.0
 */
public class FhirTranslateConverter {

	public static org.hl7.fhir.r5.model.Parameters toParameters(final TranslateResult translateResult) {
		final var parameters = new org.hl7.fhir.r5.model.Parameters();

		parameters.addParameter("result", translateResult.getResult());
		parameters.addParameter("message", translateResult.getMessage());
		
		final Collection<Match> matches = translateResult.getMatches();
		if (matches != null) {
			for (final Match match : matches) {
				final var matchParameter = parameters.addParameter().setName("match");
				
				final Code equivalence = match.getEquivalence();
				if (equivalence != null) {
					addPart(matchParameter, "relationship", new org.hl7.fhir.r5.model.Enumeration<>(
						new org.hl7.fhir.r5.model.Enumerations.ConceptMapRelationshipEnumFactory(), 
						org.hl7.fhir.r5.model.Enumerations.ConceptMapRelationship.fromCode(equivalence.getCodeValue())));
				}
				
				final Coding concept = match.getConcept();
				if (concept != null) {
					final var fhirConcept = new org.hl7.fhir.r5.model.Coding(
						concept.getSystemValue(), 
						concept.getVersion(),
						concept.getCodeValue(),
						concept.getDisplay());
					
					addPart(matchParameter, "concept", fhirConcept);
				}
				
				// TODO: support returning mapping properties (priority, provenance, hints,
				// experimental flag, etc.)
				
				// TODO: support returning products (there seems to be a mismatch between the R5
				// spec and Snow Owl's internal representation)
				
				final Uri source = match.getSource();
				if (source != null) {
					addPart(matchParameter, "originMap", new org.hl7.fhir.r5.model.UriType(source.getUriValue()));
				}
			}
		}
		
		return parameters;
	}
	
	private static void addPart(
		final org.hl7.fhir.r5.model.Parameters.ParametersParameterComponent parameterComponent, 
		final String name, 
		final org.hl7.fhir.r5.model.DataType value
	) {
		if (value != null) {
			parameterComponent.addPart().setName(name).setValue(value);
		}
	}
}
