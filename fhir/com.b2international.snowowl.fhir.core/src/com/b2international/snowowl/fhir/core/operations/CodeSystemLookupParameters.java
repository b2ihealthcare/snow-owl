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

import java.util.List;

import org.hl7.fhir.r5.model.*;

/**
 * @since 9.2
 */
public final class CodeSystemLookupParameters extends BaseParameters {

	public CodeSystemLookupParameters(Parameters parameters) {
		super(parameters);
	}
	
	public CodeType getCode() {
//		if (code != null) {
//			return code.getCodeValue();
//		} else if (coding != null) {
//			return coding.getCode().getCodeValue();
//		}
		return getParameterValue("code", Parameters.ParametersParameterComponent::getValueCodeType);
	}

	public UriType getSystem() {
//		if (system != null) {
//			return system.getUriValue();
//		} else if (coding != null && coding.getSystem() != null) {
//			return coding.getSystem().getUriValue();
//		}
		return getParameterValue("system", Parameters.ParametersParameterComponent::getValueUriType);
	}

	public StringType getVersion() {
		return getParameterValue("version", Parameters.ParametersParameterComponent::getValueStringType);
	}

	public Coding getCoding() {
		return getParameterValue("coding", Parameters.ParametersParameterComponent::getValueCoding);
	}

	public DateType getDate() {
		return getParameterValue("date", Parameters.ParametersParameterComponent::getValueDateType);
	}

	public CodeType getDisplayLanguage() {
		return getParameterValue("displayLanguage", Parameters.ParametersParameterComponent::getValueCodeType);
	}

	public List<CodeType> getProperty() {
		return getParameters("property").stream().map(Parameters.ParametersParameterComponent::getValueCodeType).toList();
	}

}
