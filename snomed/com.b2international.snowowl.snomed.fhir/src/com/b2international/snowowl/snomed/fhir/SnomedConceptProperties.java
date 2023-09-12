/*
 * Copyright 2018-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.fhir;

import org.hl7.fhir.r5.model.BooleanType;
import org.hl7.fhir.r5.model.CodeSystem.PropertyComponent;
import org.hl7.fhir.r5.model.CodeSystem.PropertyType;
import org.hl7.fhir.r5.model.CodeType;

import com.b2international.snowowl.fhir.core.model.codesystem.CommonConceptProperties;
import com.b2international.snowowl.fhir.core.model.codesystem.LookupProperty;

/**
 * FHIR additional SNOMED CT properties
 * <p> 
 * In addition to the properties listed below, any SNOMED CT relationship where
 * the relationship type is subsumed by Attribute (246061005) automatically
 * become properties.
 * 
 * @see CommonConceptProperties
 * @since 6.4
 */
public enum SnomedConceptProperties {

	EFFECTIVE_TIME(new PropertyComponent("effectiveTime", PropertyType.DATETIME)
		.setDescription("This is the effectiveTime value from the RF2 concepts file")),

	INACTIVE(new PropertyComponent("inactive", PropertyType.BOOLEAN)
		.setDescription(
			"Whether the code is active or not (defaults to false). " + 
			"This is derived from the active column in the Concept file of the RF2 Distribution (by inverting the value)")),

	MODULE_ID(new PropertyComponent("moduleId", PropertyType.CODE)
		.setDescription("The SNOMED CT concept id of the module that the concept belongs to")),

	@Deprecated
	NORMAL_FORM(new PropertyComponent("normalForm", PropertyType.STRING)
		.setDescription("Generated Normal form expression for the provided code or expression, with terms")),

	@Deprecated
	NORMAL_FORM_TERSE(new PropertyComponent("normalFormTerse", PropertyType.STRING)
		.setDescription("Generated Normal form expression for the provided code or expression, conceptIds only")),

	SUFFICIENTLY_DEFINED(new PropertyComponent("sufficientlyDefined", PropertyType.BOOLEAN)
		.setDescription(
			"True if the description logic definition of the concept includes sufficient conditions " + 
			"(i.e., if the concept is not primitive - found in the value of definitionStatusId in the concept file)"));

	private final PropertyComponent component;

	private SnomedConceptProperties(final PropertyComponent component) {
		// These codes do not receive an URI as none seems to be defined!
		this.component = component;
	}

	public PropertyComponent getComponent() {
		return component;
	}

	public CodeType getCode() {
		return component.getCodeElement();
	}

	private LookupProperty createProperty() {
		return new LookupProperty(component.getCodeElement(), component.getDescription());
	}

	public LookupProperty withValue(final boolean value) {
		return createProperty().setValueBoolean(new BooleanType(value));
	}

	public LookupProperty withValue(final String value) {
		return createProperty().setValueString(value);
	}
}
