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

import java.util.Objects;

import org.hl7.fhir.r5.model.CodeSystem;

import com.b2international.fhir.r5.operations.CodeSystemSubsumptionParameters;
import com.b2international.fhir.r5.operations.CodeSystemSubsumptionResultParameters;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.TerminologyResource;
import com.b2international.snowowl.core.codesystem.CodeSystemRequests;

/**
 * Test the subsumption relationship between code/Coding A and code/Coding B given the semantics of subsumption in the underlying code system (see hierarchyMeaning).
 * 
 * @see <a href="http://hl7.org/fhir/codesystem-operation-subsumes.html">offical FHIR $subsumes operation docs</a> for more details.
 * @since 8.0
 */
final class FhirCodeSystemSubsumesRequest extends FhirRequest<CodeSystemSubsumptionResultParameters> {

	private static final long serialVersionUID = 1L;
	
	private final CodeSystemSubsumptionParameters parameters;

	public FhirCodeSystemSubsumesRequest(CodeSystemSubsumptionParameters parameters) {
		super(parameters.getSystem().getValue(), parameters.getVersion().getValue());
		this.parameters = parameters;
	}

	@Override
	public CodeSystemSubsumptionResultParameters doExecute(ServiceProvider context, CodeSystem codeSystem) {
		
		final String codeA = parameters.getCodeA() != null ? parameters.getCodeA().getValue() : parameters.getCodingA().getCode();
		final String codeB = parameters.getCodeB() != null ? parameters.getCodeB().getValue() : parameters.getCodingB().getCode();
		
		if (Objects.equals(codeA, codeB)) {
			return CodeSystemSubsumptionResultParameters.equivalent();
		} else if (isSubsumedBy(context, codeSystem, codeA, codeB)) {
			return CodeSystemSubsumptionResultParameters.subsumedBy(); 
		} else if (isSubsumedBy(context, codeSystem, codeB, codeA)) {
			return CodeSystemSubsumptionResultParameters.subsumes();	
		} else {
			return CodeSystemSubsumptionResultParameters.notSubsumed();				
		}
	}

	private boolean isSubsumedBy(ServiceProvider context, CodeSystem codeSystem, final String subType, final String superType) {
		return CodeSystemRequests.prepareSearchConcepts()
			.setLimit(0)
			.filterByCodeSystem(codeSystem.getUserString(TerminologyResource.Fields.RESOURCE_URI))
			.filterById(subType)
			.filterByAncestor(superType)
			.buildAsync()
			.execute(context)
			.getTotal() > 0;
	}

}
