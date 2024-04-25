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

import org.hl7.fhir.r5.model.CodeSystem.ConceptDefinitionDesignationComponent;
import org.hl7.fhir.r5.model.CodeSystem.ConceptPropertyComponent;
import org.hl7.fhir.r5.model.Parameters;

/**
 * @since 9.2
 */
public final class CodeSystemLookupResultParameters extends BaseParameters {

	public CodeSystemLookupResultParameters() {
		this(new Parameters());
	}
	
	public CodeSystemLookupResultParameters(Parameters parameters) {
		super(parameters);
	}

	public void setName(String name) {
		getParameters().addParameter("name", name);
	}

	public void setDisplay(String display) {
		getParameters().addParameter("display", display);
	}

	public void setVersion(String version) {
		getParameters().addParameter("version", version);
	}

	public void setDesignation(List<ConceptDefinitionDesignationComponent> designations) {
		if (designations == null) return;
		
		designations.stream()
			.map(designation -> {
				var designationParameter = new Parameters.ParametersParameterComponent().setName("designation");
				
				// add value part, which is the term of the designation
				designationParameter.addPart(
					new Parameters.ParametersParameterComponent()
						.setName("value")
						.setValue(designation.getValueElement())
				);
				// add language part
				designationParameter.addPart(
					new Parameters.ParametersParameterComponent()
						.setName("language")
						.setValue(designation.getLanguageElement())
				);
				// add use part
				designationParameter.addPart(
					new Parameters.ParametersParameterComponent()
						.setName("use")
						.setValue(designation.getUse())
				);
				// add language part
				designation.getAdditionalUse().forEach(additionalUse -> {
					designationParameter.addPart(
						new Parameters.ParametersParameterComponent()
						.setName("additionalUse")
						.setValue(additionalUse)
					);
				});
				
				
				return designationParameter; 
			})
			.forEach(getParameters()::addParameter);
	}

	public void setProperty(List<ConceptPropertyComponent> properties) {
		if (properties == null) return;
		
		properties.stream()
			.map(property -> {
				var propertyParameter = new Parameters.ParametersParameterComponent().setName("property");
				
				// property.code
				propertyParameter.addPart(
					new Parameters.ParametersParameterComponent()
						.setName("code")
						.setValue(property.getCodeElement())
				);
				
				// property.value
				propertyParameter.addPart(
					new Parameters.ParametersParameterComponent()
						.setName("value")
						.setValue(property.getValue())
				);
				
				return propertyParameter; 
			})
			.forEach(getParameters()::addParameter);
	}
	
}
