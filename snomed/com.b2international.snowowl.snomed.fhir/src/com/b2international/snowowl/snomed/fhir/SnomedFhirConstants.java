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
package com.b2international.snowowl.snomed.fhir;

import java.util.List;

import org.hl7.fhir.r5.model.CodeSystem;
import org.hl7.fhir.r5.model.CodeSystem.CodeSystemFilterComponent;
import org.hl7.fhir.r5.model.CodeSystem.PropertyType;
import org.hl7.fhir.r5.model.Enumerations.FilterOperator;

import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;

/**
 * @since 9.4.0
 */
public class SnomedFhirConstants {

	/**
	 * See <a href="https://terminology.hl7.org/SNOMEDCT.html#snomed-ct-filters">https://terminology.hl7.org/SNOMEDCT.html#snomed-ct-filters</a>
	 */
	public static final List<CodeSystemFilterComponent> SUPPORTED_SNOMED_CODESYSTEM_FILTERS = List.of(
		new CodeSystem.CodeSystemFilterComponent("concept", FilterOperator.ISA, "[concept id]")
			.setDescription("Includes all concept ids that have a transitive is-a relationship with the concept id provided as the value (including the concept itself)"),
		
		new CodeSystem.CodeSystemFilterComponent("concept", FilterOperator.DESCENDENTOF, "[concept id]")
			.setDescription("Includes all concept ids that have a transitive is-a relationship with the concept id provided as the value (excluding the concept itself)"),
			
		new CodeSystem.CodeSystemFilterComponent("concept", FilterOperator.IN, "[concept id]")
			.setDescription("Includes all concept ids that are active members of the reference set identified by the concept id provided as the value"),
			
		new CodeSystem.CodeSystemFilterComponent("constraint", FilterOperator.EQUAL, "[expression constraint]")
			.setDescription("The result of the filter is the result of executing the given SNOMED CT Expression Constraint (https://snomed.org/ecl).")

		// post-coordinated expressions are not supported yet
//		new CodeSystem.CodeSystemFilterComponent("expressions", FilterOperator.EQUAL, "true or false")
//			.setDescription("Whether post-coordinated expressions are included in the value set.")
			
	);
	
	public static final CodeSystem.PropertyComponent SNOMED_PROPERTY_EFFECTIVE_TIME = new CodeSystem.PropertyComponent("effectiveTime", PropertyType.DATETIME)
			.setUri(toConceptFieldUri("effectiveTime"))
			.setDescription("This is the effectiveTime value from the RF2 concepts file (from the snapshot).");
	
	public static final CodeSystem.PropertyComponent SNOMED_PROPERTY_INACTIVE = new CodeSystem.PropertyComponent("inactive", PropertyType.BOOLEAN)
			.setUri(toConceptFieldUri("inactive"))
			.setDescription("Whether the code is active or not (defaults to false). This is derived from the active column in the Concept file of the RF2 Distribution (by inverting the value).");
	
	public static final CodeSystem.PropertyComponent SNOMED_PROPERTY_MODULE_ID = new CodeSystem.PropertyComponent("moduleId", PropertyType.CODE)
			.setUri(toConceptFieldUri("moduleId"))
			.setDescription("The SNOMED CT concept id of the module that the concept belongs to.");
	
	public static final CodeSystem.PropertyComponent SNOMED_PROPERTY_NORMAL_FORM = new CodeSystem.PropertyComponent("normalForm", PropertyType.STRING)
			.setUri(toConceptFieldUri("normalForm"))
			.setDescription("Generated Necessary Normal Form expression for the provided code or expression, with terms. The normal form expressions are not suitable for use in subsumption testing.");
	
	public static final CodeSystem.PropertyComponent SNOMED_PROPERTY_NORMAL_FORM_TERSE = new CodeSystem.PropertyComponent("normalFormTerse", PropertyType.STRING)
			.setUri(toConceptFieldUri("normalFormTerse"))
			.setDescription("Generated Necessary Normal form expression for the provided code or expression, concept ids only. The normal form expressions are not suitable for use in subsumption testing.");
	
	public static final CodeSystem.PropertyComponent SNOMED_PROPERTY_SEMANTIC_TAG = new CodeSystem.PropertyComponent("semanticTag", PropertyType.STRING)
			.setUri(toConceptFieldUri("semanticTag"))
			.setDescription("The phrase inside the last set of brackets of its Fully Specified Name.");
	
	public static final CodeSystem.PropertyComponent SNOMED_PROPERTY_SUFFICIENTLY_DEFINED = new CodeSystem.PropertyComponent("sufficientlyDefined", PropertyType.BOOLEAN)
			.setUri(toConceptFieldUri("sufficientlyDefined"))
			.setDescription("True if the description logic definition of the concept includes sufficient conditions. This is derived from the definitionStatusId value in the Concept file of the RF2 distribution (i.e. If 900000000000073002 |Sufficiently defined concept definition status| then true).");
	
	private static String toConceptFieldUri(String propertyName) {
		return String.join("/field/Concept.", SnomedTerminologyComponentConstants.SNOMED_URI_BASE, propertyName);
	}
	
}
