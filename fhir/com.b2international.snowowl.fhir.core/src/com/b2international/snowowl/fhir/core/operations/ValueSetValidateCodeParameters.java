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
 * @since 9.2 
 */
public class ValueSetValidateCodeParameters extends BaseParameters {

	public ValueSetValidateCodeParameters() {
		super(new Parameters());
	}
	
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
		return getParameterValue("valueSet", value -> (ValueSet) value.getResource());
	}
	
	public StringType getValueSetVersion() {
		return getParameterValue("valueSetVersion", Parameters.ParametersParameterComponent::getValueStringType);
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
	
	public StringType getSystem() {
		return getParameterValue("system", Parameters.ParametersParameterComponent::getValueStringType); 
	}

	public StringType getSystemVersion() {
		return getParameterValue("systemVersion", Parameters.ParametersParameterComponent::getValueStringType);
	}
	
	public StringType getDisplay() {
		return getParameterValue("display", Parameters.ParametersParameterComponent::getValueStringType);
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

	public ValueSetValidateCodeParameters setUrl(String url) {
		return setUrl(new UriType(url));
	}

	public ValueSetValidateCodeParameters setUrl(UriType url) {
		getParameters().addParameter("url", url);
		return this;
	}
	
	public ValueSetValidateCodeParameters setContext(String context) {
		return setContext(new UriType(context));
	}
	
	public ValueSetValidateCodeParameters setContext(UriType context) {
		getParameters().addParameter("context", context);
		return this;
	}
	
	public ValueSetValidateCodeParameters setValueSetVersion(String valueSetVersion) {
		return setValueSetVersion(new StringType(valueSetVersion));
	}
	
	public ValueSetValidateCodeParameters setValueSetVersion(StringType valueSetVersion) {
		getParameters().addParameter("valueSetVersion", valueSetVersion);
		return this;
	}
	
	public ValueSetValidateCodeParameters setCode(String code) {
		return setCode(new CodeType(code));
	}

	public ValueSetValidateCodeParameters setCode(CodeType code) {
		getParameters().addParameter("code", code);
		return this;
	}
	
	public ValueSetValidateCodeParameters setSystem(String system) {
		return setSystem(new UriType(system));
	}
	
	public ValueSetValidateCodeParameters setSystem(UriType system) {
		getParameters().addParameter("system", system);
		return this;
	}
	
	public ValueSetValidateCodeParameters setSystemVersion(String systemVersion) {
		return setSystemVersion(new StringType(systemVersion));
	}
	
	public ValueSetValidateCodeParameters setSystemVersion(StringType systemVersion) {
		getParameters().addParameter("systemVersion", systemVersion);
		return this;
	}
	
	public ValueSetValidateCodeParameters setDisplay(String display) {
		return setDisplay(new StringType(display));
	}

	public ValueSetValidateCodeParameters setDisplay(StringType display) {
		getParameters().addParameter("display", display);
		return this;
	}
	
	public ValueSetValidateCodeParameters setCoding(Coding coding) {
		getParameters().addParameter("coding", coding);
		return this;
	}
	
	public ValueSetValidateCodeParameters setCodeableConcept(CodeableConcept codeableConcept) {
		getParameters().addParameter("codeableConcept", codeableConcept);
		return this;
	}
	
	public ValueSetValidateCodeParameters setDate(String date) {
		return setDate(date == null ? null : FhirDates.parse(date));
	}
	
	public ValueSetValidateCodeParameters setDate(Date date) {
		return setDate(new DateType(date));
	}
	
	public ValueSetValidateCodeParameters setDate(DateType date) {
		getParameters().addParameter("date", date);
		return this;
	}
	
	public ValueSetValidateCodeParameters setAbstract(Boolean isAbstract) {
		return setAbstract(new BooleanType(isAbstract));
	}
	
	public ValueSetValidateCodeParameters setAbstract(BooleanType isAbstract) {
		getParameters().addParameter("abstract", isAbstract);
		return this;
	}
	
	public ValueSetValidateCodeParameters setDisplayLanguage(String displayLanguage) {
		return setDisplayLanguage(new CodeType(displayLanguage));
	}
	
	public ValueSetValidateCodeParameters setDisplayLanguage(CodeType displayLanguage) {
		getParameters().addParameter("displayLanguage", displayLanguage);
		return this;
	}

}
