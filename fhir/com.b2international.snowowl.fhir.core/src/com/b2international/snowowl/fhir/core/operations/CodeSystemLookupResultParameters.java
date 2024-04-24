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
//		getParameters().addParameter("designation", designations);
	}

	public void setProperty(List<ConceptPropertyComponent> properties) {
//		getParameters().addParameter("property", properties);
	}
	
	
	
	
	
}
