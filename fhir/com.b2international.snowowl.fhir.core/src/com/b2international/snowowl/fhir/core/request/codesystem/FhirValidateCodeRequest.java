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

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.hl7.fhir.r5.model.CodeSystem;
import org.hl7.fhir.r5.model.CodeableConcept;
import org.hl7.fhir.r5.model.Coding;

import com.b2international.fhir.r5.operations.CodeSystemValidateCodeParameters;
import com.b2international.fhir.r5.operations.CodeSystemValidateCodeResultParameters;
import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.codesystem.CodeSystemRequests;
import com.b2international.snowowl.core.domain.Concept;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Sets;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

/**
 * @since 8.0
 */
final class FhirValidateCodeRequest extends FhirRequest<CodeSystemValidateCodeResultParameters> {

	private static final long serialVersionUID = 1L;

	@NotNull
	@Valid
	@JsonProperty
	@JsonUnwrapped
	private final CodeSystemValidateCodeParameters parameters;
	
	FhirValidateCodeRequest(CodeSystemValidateCodeParameters parameters) {
		super(parameters.getUrl() != null ? parameters.getUrl().getValue() : parameters.getCoding().getSystem(), parameters.getVersion().getValue());
		this.parameters = parameters;
	}

	@Override
	public CodeSystemValidateCodeResultParameters doExecute(ServiceProvider context, CodeSystem codeSystem) {
		Set<Coding> codings = collectCodingsToValidate(parameters);
		Map<String, Coding> codingsById = codings.stream().collect(Collectors.toMap(Coding::getCode, c -> c));
		
		// extract locales from the request
		Map<String, Concept> conceptsById = CodeSystemRequests.prepareSearchConcepts()
				.setLimit(codingsById.keySet().size())
				.filterByCodeSystemUri(new ResourceURI(codeSystem.getId()))
				.filterByIds(codingsById.keySet())
				.setLocales(extractLocales(parameters.getDisplayLanguage()))
				.buildAsync()
				.execute(context)
				.stream()
				.collect(Collectors.toMap(Concept::getId, c -> c));
		
		// check if both Maps have the same keys and report if not
		
		Set<String> missingConceptIds = Sets.difference(codingsById.keySet(), conceptsById.keySet());
		
		if (!missingConceptIds.isEmpty()) {
			return new CodeSystemValidateCodeResultParameters()
					.setResult(false)
					.setMessage(String.format("Could not find code%s '%s'.", missingConceptIds.size() == 1 ? "" : "s", ImmutableSortedSet.copyOf(missingConceptIds)));
		}
		
		// iterate over requested IDs to detect if one does not have any concept returned
		// XXX it would be great to have support for multiple messages/validation results in a single request
		for (String id : codingsById.keySet()) {
			// check display if provided
			Coding providedCoding = codingsById.get(id);
			if (providedCoding.getDisplay() != null) {
				
				Concept concept = conceptsById.get(id);
				
				// TODO what about alternative terms?
				
				if (!providedCoding.getDisplay().equals(concept.getTerm())) {
					return new CodeSystemValidateCodeResultParameters()
							.setResult(false)
							.setDisplay(concept.getTerm())
							.setMessage(String.format("Incorrect display '%s' for code '%s'.", providedCoding.getDisplay(), providedCoding.getCode())); 
				}
			}
		}
		
		return new CodeSystemValidateCodeResultParameters().setResult(true);
	}
	
	private Set<Coding> collectCodingsToValidate(CodeSystemValidateCodeParameters parameters) {
		Set<Coding> codings = new HashSet<>(3);
				
		if (parameters.getCode() != null) {
			Coding coding = new Coding()
					.setCode(parameters.getCode().getValue())
					.setDisplay(parameters.getDisplay().getValue());
			
			codings.add(coding);
		}
				
		if (parameters.getCoding() != null) {
			codings.add(parameters.getCoding());
		}
			
		CodeableConcept codeableConcept = parameters.getCodeableConcept();
		if (codeableConcept != null && codeableConcept.getCoding() != null) {
			codeableConcept.getCoding().forEach(codings::add);
		}
		return codings;
	}
	
}
