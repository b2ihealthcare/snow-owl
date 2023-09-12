/*
 * Copyright 2018-2023 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.fhir.core.model.codesystem;

import org.hl7.fhir.r5.model.CodeSystem.PropertyComponent;
import org.hl7.fhir.r5.model.CodeSystem.PropertyType;
import org.hl7.fhir.r5.model.CodeType;
import org.hl7.fhir.utilities.StandardsStatus;

/**
 * Enumerates defined concept properties. Values are based on FHIR's "Concept properties" code system.
 * 
 * @since 6.4
 */
public enum CommonConceptProperties {

	INACTIVE(new PropertyComponent("inactive", PropertyType.BOOLEAN)
		.setDescription("True if the concept is not considered active - e.g. not a valid concept any more. Property type is boolean, default value is false")),

	DEPRECATED(new PropertyComponent("deprecated", PropertyType.DATETIME)
		.setDescription(
			"The date at which a concept was deprecated. Concepts that are deprecated but not inactive can still be used, " + 
			"but their use is discouraged, and they should be expected to be made inactive in a future release. Property type is dateTime")),

	DEPRECATION_DATE(new PropertyComponent("deprecationDate", PropertyType.DATETIME)
		.setDescription(
			"The date at which a concept was deprecated. Concepts that are deprecated but not inactive can still be used, " + 
			"but their use is discouraged, and they should be expected to be made inactive in a future release. Property type is dateTime")),

	NOT_SELECTABLE(new PropertyComponent("notSelectable", PropertyType.BOOLEAN)
		.setDescription(
			"The concept is not intended to be chosen by the user - only intended to be used as a selector for other concepts. " + 
			"Note, though, that the interpretation of this is highly contextual; all concepts are selectable in some context. Property type is boolean, default value is false")),

	PARENT(new PropertyComponent("parent", PropertyType.CODE)
		.setDescription(
			"The concept identified in this property is a parent of the concept on which it is a property. " + 
			"The property type will be 'code'. The meaning of 'parent' is defined by the hierarchyMeaning attribute")),

	CHILD(new PropertyComponent("child", PropertyType.CODE)
		.setDescription(
			"The concept identified in this property is a child of the concept on which it is a property. " + 
			"The property type will be 'code'. The meaning of 'child' is defined by the hierarchyMeaning attribute"));

	static {
		DEPRECATED.setStandardsStatus(StandardsStatus.DEPRECATED);
	}

	public final static String CODE_SYSTEM_BASE = "http://hl7.org/fhir/concept-properties";

	private final PropertyComponent component;

	private CommonConceptProperties(final PropertyComponent component) {
		this.component = component.setUri(CODE_SYSTEM_BASE + "#" + component.getCode());
	}

	private void setStandardsStatus(final StandardsStatus status) {
		component.setStandardsStatus(status);
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

	public LookupProperty withValue(CodeType value) {
		return createProperty().setValueCode(value);
	}
}
