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

import java.util.Date;

import org.hl7.fhir.r5.model.*;

import com.b2international.snowowl.fhir.core.FhirDates;

/**
 * @since 9.3 
 */
public class CodeSystemValidateCodeParameters extends BaseParameters {

	public CodeSystemValidateCodeParameters() {
		super(new Parameters());
	}
	
	public CodeSystemValidateCodeParameters(Parameters parameters) {
		super(parameters);
	}
	
	public UriType getUrl() {
		return getParameterValue("url", Parameters.ParametersParameterComponent::getValueUriType);
	}
	
	public CodeSystem getCodeSystem() {
		return getParameterValue("codeSystem", parameter -> (CodeSystem) parameter.getResource());
	}
	
	public CodeType getCode() {
		return getParameterValue("code", Parameters.ParametersParameterComponent::getValueCodeType);
	}

	public Coding getCoding() {
		return getParameterValue("coding", Parameters.ParametersParameterComponent::getValueCoding);
	}
	
	public CodeableConcept getCodeableConcept() {
		return getParameterValue("codeableConcept", Parameters.ParametersParameterComponent::getValueCodeableConcept);
	}
	
	public StringType getVersion() {
		return getParameterValue("version", Parameters.ParametersParameterComponent::getValueStringType);
	}
	
	public StringType getDisplay() {
		return getParameterValue("display", Parameters.ParametersParameterComponent::getValueStringType);
	}
	
	public DateType getDate() {
		return getParameterValue("date", Parameters.ParametersParameterComponent::getValueDateType);
	}
	
	public BooleanType getAbstract() {
		return getParameterValue("abstract", Parameters.ParametersParameterComponent::getValueBooleanType);
	}
	
	public CodeType getDisplayLanguage() {
		return getParameterValue("displayLanguage", Parameters.ParametersParameterComponent::getValueCodeType);
	}
	
	public CodeSystemValidateCodeParameters setUrl(String url) {
		return setUrl(new UriType(url));
	}

	public CodeSystemValidateCodeParameters setUrl(UriType url) {
		getParameters().addParameter("url", url);
		return this;
	}
	
	public CodeSystemValidateCodeParameters setCodeSystem(CodeSystem codeSystem) {
		getParameters().addParameter(new Parameters.ParametersParameterComponent("codeSystem").setResource(codeSystem));
		return this;
	}
	
	public CodeSystemValidateCodeParameters setCode(String code) {
		return setCode(new CodeType(code));
	}

	public CodeSystemValidateCodeParameters setCode(CodeType code) {
		getParameters().addParameter("code", code);
		return this;
	}
	
	public CodeSystemValidateCodeParameters setCoding(Coding coding) {
		getParameters().addParameter("coding", coding);
		return this;
	}
	
	public CodeSystemValidateCodeParameters setCodeableConcept(CodeableConcept codeableConcept) {
		getParameters().addParameter("codeableConcept", codeableConcept);
		return this;
	}
	
	public CodeSystemValidateCodeParameters setVersion(String version) {
		return setVersion(new StringType(version));
	}

	public CodeSystemValidateCodeParameters setVersion(StringType version) {
		getParameters().addParameter("version", version);
		return this;
	}
	
	public CodeSystemValidateCodeParameters setDisplay(String display) {
		return setDisplay(new StringType(display));
	}

	public CodeSystemValidateCodeParameters setDisplay(StringType display) {
		getParameters().addParameter("display", display);
		return this;
	}
	
	public CodeSystemValidateCodeParameters setDate(String date) {
		return setDate(date == null ? null : FhirDates.parse(date));
	}
	
	public CodeSystemValidateCodeParameters setDate(Date date) {
		return setDate(new DateType(date));
	}
	
	public CodeSystemValidateCodeParameters setDate(DateType date) {
		getParameters().addParameter("date", date);
		return this;
	}
	
	public CodeSystemValidateCodeParameters setAbstract(Boolean isAbstract) {
		return isAbstract == null ? this : setAbstract(new BooleanType(isAbstract));
	}
	
	public CodeSystemValidateCodeParameters setAbstract(BooleanType isAbstract) {
		getParameters().addParameter("abstract", isAbstract);
		return this;
	}
	
	public CodeSystemValidateCodeParameters setDisplayLanguage(String displayLanguage) {
		return setDisplayLanguage(new CodeType(displayLanguage));
	}
	
	public CodeSystemValidateCodeParameters setDisplayLanguage(CodeType displayLanguage) {
		getParameters().addParameter("displayLanguage", displayLanguage);
		return this;
	}

}
