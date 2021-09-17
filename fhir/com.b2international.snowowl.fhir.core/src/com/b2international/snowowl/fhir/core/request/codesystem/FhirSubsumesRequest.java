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

import java.util.Objects;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.codesystem.CodeSystemRequests;
import com.b2international.snowowl.fhir.core.model.codesystem.CodeSystem;
import com.b2international.snowowl.fhir.core.model.codesystem.SubsumptionRequest;
import com.b2international.snowowl.fhir.core.model.codesystem.SubsumptionResult;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

/**
 * Test the subsumption relationship between code/Coding A and code/Coding B given the semantics of subsumption in the underlying code system (see hierarchyMeaning).
 * 
 * @see <a href="http://hl7.org/fhir/codesystem-operation-subsumes.html">offical FHIR $subsumes operation docs</a> for more details.
 * @since 8.0
 */
final class FhirSubsumesRequest extends FhirRequest<SubsumptionResult> {

	private static final long serialVersionUID = 1L;
	
	@NotNull
	@Valid
	@JsonProperty
	@JsonUnwrapped
	private final SubsumptionRequest request;

	public FhirSubsumesRequest(SubsumptionRequest request) {
		super(request.getSystem(), request.getVersion());
		this.request = request;
	}

	@Override
	public SubsumptionResult doExecute(ServiceProvider context, CodeSystem codeSystem) {
		
		final String codeA = request.getCodeA() != null ? request.getCodeA() : request.getCodingA().getCodeValue();
		final String codeB = request.getCodeB() != null ? request.getCodeB() : request.getCodingB().getCodeValue();
		
		if (Objects.equals(codeA, codeB)) {
			return SubsumptionResult.equivalent();
		} else if (isSubsumedBy(context, codeSystem, codeA, codeB)) {
			return SubsumptionResult.subsumedBy(); 
		} else if (isSubsumedBy(context, codeSystem, codeB, codeA)) {
			return SubsumptionResult.subsumes();	
		} else {
			return SubsumptionResult.notSubsumed();				
		}
	}

	private boolean isSubsumedBy(ServiceProvider context, CodeSystem codeSystem, final String subType, final String superType) {
		return CodeSystemRequests.prepareSearchConcepts()
			.setLimit(0)
			.filterByCodeSystemUri(codeSystem.getResourceURI())
			.filterById(subType)
			.filterByAncestor(superType)
			.buildAsync()
			.execute(context)
			.getTotal() > 0;
	}

}
