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
 * @since 9.3 
 */
public class ConceptMapTranslateParameters extends BaseParameters {

	public ConceptMapTranslateParameters(Parameters parameters) {
		super(parameters);
	}
	
	public UriType getUrl() {
		return getParameterValue("url", Parameters.ParametersParameterComponent::getValueUriType);
	}
	
	public ConceptMap getConceptMap() {
		throw new FHIRException("Inline input parameter 'conceptMap' is not supported.");
	}
	
	public StringType getConceptMapVersion() {
		throw new FHIRException("Inline input parameter 'conceptMapVersion' is not supported.");
	}
	
	public UriType getSystem() {
		return getParameterValue("system", Parameters.ParametersParameterComponent::getValueUriType);
	}
	
	public StringType getVersion() {
		return getParameterValue("version", Parameters.ParametersParameterComponent::getValueStringType);
	}
	
	public CodeType getSourceCode() {
		return getParameterValue("sourceCode", Parameters.ParametersParameterComponent::getValueCodeType);
	}

	public Coding getSourceCoding() {
		return getParameterValue("sourceCoding", Parameters.ParametersParameterComponent::getValueCoding);
	}
	
	public CodeableConcept getSourceCodeableConcept() {
		return getParameterValue("sourceCodeableConcept", Parameters.ParametersParameterComponent::getValueCodeableConcept);
	}
	
	public UriType getSourceScope() {
		return getParameterValue("sourceScope", Parameters.ParametersParameterComponent::getValueUriType);
	}
	
	public CodeType getTargetCode() {
		return getParameterValue("targetCode", Parameters.ParametersParameterComponent::getValueCodeType);
	}

	public Coding getTargetCoding() {
		return getParameterValue("targetCoding", Parameters.ParametersParameterComponent::getValueCoding);
	}
	
	public CodeableConcept getTargetCodeableConcept() {
		return getParameterValue("targetCodeableConcept", Parameters.ParametersParameterComponent::getValueCodeableConcept);
	}
	
	public UriType getTargetScope() {
		return getParameterValue("targetScope", Parameters.ParametersParameterComponent::getValueUriType);
	}
	
	// TODO add Dependency params
	
}
