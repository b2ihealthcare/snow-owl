/*
 * Copyright 2024 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.snowowl.fhir.core.operations;

import org.hl7.fhir.r5.model.Coding;
import org.hl7.fhir.r5.model.Parameters;
import org.hl7.fhir.r5.model.StringType;

/**
 * @since 9.2
 */
public class CodeSystemSubsumptionParameters extends BaseParameters {

	public CodeSystemSubsumptionParameters(Parameters parameters) {
		super(parameters);
	}
	
	public StringType getSystem() {
		return getParameterValue("system", Parameters.ParametersParameterComponent::getValueStringType);
	}
	
	public StringType getVersion() {
		return getParameterValue("version", Parameters.ParametersParameterComponent::getValueStringType);
	}
	
	public StringType getCodeA() {
		return getParameterValue("codeA", Parameters.ParametersParameterComponent::getValueStringType);
	}
	
	public StringType getCodeB() {
		return getParameterValue("codeB", Parameters.ParametersParameterComponent::getValueStringType);
	}
	
	public Coding getCodingA() {
		return getParameterValue("codingA", Parameters.ParametersParameterComponent::getValueCoding);
	}
	
	public Coding getCodingB() {
		return getParameterValue("codingB", Parameters.ParametersParameterComponent::getValueCoding);
	}

}
