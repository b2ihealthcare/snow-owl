/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.fhir.core.model.subsumption;

import com.b2international.snowowl.fhir.core.model.dt.FhirDataType;
import com.b2international.snowowl.fhir.core.model.dt.FhirType;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * @since 6.4
 */
@JsonPropertyOrder({"outcome"})
public class SubsumptionResult {

	/**
	 * The subsumption relationship between code/Coding "A" and code/Coding "B". 
	 * There are 4 possible codes to be returned: equivalent, subsumes, subsumed-by, and not-subsumed. 
	 * If the server is unable to determine the relationship between the codes/Codings, then it returns an error (i.e. an OperationOutcome)
	 */
	@FhirType(FhirDataType.CODE)
	private final String outcome;
	
	private SubsumptionResult(String outcome) {
		this.outcome = outcome;
	}
	
	public String getOutcome() {
		return outcome;
	}
	
	public static SubsumptionResult equivalent() {
		return new SubsumptionResult("equivalent");
	}
	
	public static SubsumptionResult subsumes() {
		return new SubsumptionResult("subsumes");
	}
	
	public static SubsumptionResult subsumedBy() {
		return new SubsumptionResult("subsumed-by");
	}
	
	public static SubsumptionResult notSubsumed() {
		return new SubsumptionResult("not-subsumed");
	}
	
}
