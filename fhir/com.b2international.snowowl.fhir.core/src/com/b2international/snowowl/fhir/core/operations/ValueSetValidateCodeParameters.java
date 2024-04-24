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

import org.hl7.fhir.exceptions.FHIRException;
import org.hl7.fhir.r5.model.*;

/**
 * @since 9.2 
 */
public class ValueSetValidateCodeParameters extends BaseParameters {

	public ValueSetValidateCodeParameters(Parameters parameters) {
		super(parameters);
	}
	
	public UriType getUrl() {
		return getParameterValue("url", Parameters.ParametersParameterComponent::getValueUriType);
	}
	
	public UriType getContext() {
		return getParameterValue("context", Parameters.ParametersParameterComponent::getValueUriType);
	}
	
	public ValueSet getValueSet() {
		throw new FHIRException("Inline input parameter 'valueSet' is not supported.");
	}
	
	public StringType getValueSetVersion() {
		return getParameterValue("valueSetVersion", Parameters.ParametersParameterComponent::getValueStringType);
	}
	
	public CodeType getCode() {
//		if (code != null) {
//			return code;
//		} else if (coding != null) {
//			return coding.getCode().getCodeValue();
//		}
//		return null;
		return getParameterValue("code", Parameters.ParametersParameterComponent::getValueCodeType);
	}

	public StringType getSystem() {
//		if (system != null) {
//			return system;
//		} else if (coding != null && coding.getSystem() != null) {
//			return coding.getSystem().getUriValue();
//		}
//		return null;
		return getParameterValue("system", Parameters.ParametersParameterComponent::getValueStringType); 
	}

	public StringType getSystemVersion() {
		return getParameterValue("systemVersion", Parameters.ParametersParameterComponent::getValueStringType);
	}
	
	public StringType getDisplay() {
		return getParameterValue("display", Parameters.ParametersParameterComponent::getValueStringType);
	}

	public Coding getCoding() {
		return getParameterValue("coding", Parameters.ParametersParameterComponent::getValueCoding);
	}
	
	public CodeableConcept getCodeableConcept() {
		return getParameterValue("codeableConcept", Parameters.ParametersParameterComponent::getValueCodeableConcept);
	}

	public DateType getDate() {
		return getParameterValue("date", Parameters.ParametersParameterComponent::getValueDateType);
	}
	
	public BooleanType getIsAbstract() {
		return getParameterValue("isAbstract", Parameters.ParametersParameterComponent::getValueBooleanType);
	}

	public CodeType getDisplayLanguage() {
		return getParameterValue("displayLanguage", Parameters.ParametersParameterComponent::getValueCodeType);
	}

}
