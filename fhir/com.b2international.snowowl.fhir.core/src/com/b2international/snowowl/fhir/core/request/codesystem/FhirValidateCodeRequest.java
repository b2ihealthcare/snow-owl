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

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.codesystem.CodeSystemRequests;
import com.b2international.snowowl.core.domain.Concept;
import com.b2international.snowowl.fhir.core.model.ValidateCodeResult;
import com.b2international.snowowl.fhir.core.model.codesystem.CodeSystem;
import com.b2international.snowowl.fhir.core.model.codesystem.ValidateCodeRequest;
import com.b2international.snowowl.fhir.core.model.dt.CodeableConcept;
import com.b2international.snowowl.fhir.core.model.dt.Coding;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Sets;

/**
 * @since 8.0
 */
final class FhirValidateCodeRequest extends FhirRequest<ValidateCodeResult> {

	private static final long serialVersionUID = 1L;

	@NotNull
	@Valid
	@JsonProperty
	@JsonUnwrapped
	private final ValidateCodeRequest request;
	
	FhirValidateCodeRequest(ValidateCodeRequest request) {
		super(request.getUrl() != null ? request.getUrl().getUriValue() : request.getCoding().getSystemValue(), request.getVersion());
		this.request = request;
	}

	@Override
	public ValidateCodeResult doExecute(ServiceProvider context, CodeSystem codeSystem) {
		Set<Coding> codings = collectCodingsToValidate(request);
		Map<String, Coding> codingsById = codings.stream().collect(Collectors.toMap(Coding::getCodeValue, c -> c));
		
		// extract locales from the request
		Map<String, Concept> conceptsById = CodeSystemRequests.prepareSearchConcepts()
				.setLimit(codingsById.keySet().size())
				.filterByIds(codingsById.keySet())
				.setLocales(extractLocales(request.getDisplayLanguage()))
				.build(codeSystem.getResourceURI())
				.getRequest()
				.execute(context)
				.stream()
				.collect(Collectors.toMap(Concept::getId, c -> c));
		
		// check if both Maps have the same keys and report if not
		
		Set<String> missingConceptIds = Sets.difference(codingsById.keySet(), conceptsById.keySet());
		
		if (!missingConceptIds.isEmpty()) {
			return ValidateCodeResult.builder()
					.result(false)
					.message(String.format("Could not find code%s '%s'.", missingConceptIds.size() == 1 ? "" : "s", ImmutableSortedSet.copyOf(missingConceptIds)))
					.build();
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
					return ValidateCodeResult.builder()
							.result(false)
							.display(concept.getTerm())
							.message(String.format("Incorrect display '%s' for code '%s'.", providedCoding.getDisplay(), providedCoding.getCodeValue()))
							.build(); 
				}
			}
		}
		
		return ValidateCodeResult.builder().result(true).build();
	}
	
	private Set<Coding> collectCodingsToValidate(ValidateCodeRequest request) {
		Set<Coding> codings = new HashSet<>(3);
				
		if (request.getCode() != null) {
			Coding coding = Coding.builder()
					.code(request.getCode())
					.display(request.getDisplay())
					.build();
			
			codings.add(coding);
		}
				
		if (request.getCoding() != null) {
			codings.add(request.getCoding());
		}
			
		CodeableConcept codeableConcept = request.getCodeableConcept();
		if (codeableConcept != null) {
			if (codeableConcept.getCodings() != null) { 
				codeableConcept.getCodings().forEach(c -> codings.add(c));
			}
		}
		return codings;
	}
	
}
