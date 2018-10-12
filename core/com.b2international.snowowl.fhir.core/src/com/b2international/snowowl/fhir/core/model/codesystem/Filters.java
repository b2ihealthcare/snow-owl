/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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

import com.b2international.snowowl.fhir.core.codesystems.CommonConceptProperties;
import com.b2international.snowowl.fhir.core.codesystems.FilterOperator;

/**
 * FHIR Code system filter definitions.
 * @since 6.7
 */
public class Filters {
	
	private static final String SNOMED_CT_CONCEPT_ID = "SNOMED CT concept ID";
	
	public enum FilterPropertyCode {
		DESCENDANT,
		ANCESTOR,
		CONCEPT,
		EXPRESSION,
		EXPRESSIONS;
		
		public String getDisplayName() {
			return name().toLowerCase();
		}
	}
	
	/*
	 * Filters for common concept properties
	 * http://hl7.org/fhir/concept-properties
	 */
	public static final Filter COMMON_STATUS_FILTER = Filter.builder()
		.code(CommonConceptProperties.INACTIVE.getCodeValue())
		.description("Filter that includes concepts with the given status [true|false|TRUE|FALSE].")
		.addOperator(FilterOperator.EQUALS)
		.value("boolean status [true|false|TRUE|FALSE]")
		.build();
	
	public static final Filter COMMON_PARENT_FILTER = Filter.builder()
		.code(CommonConceptProperties.PARENT.getCodeValue())
		.description("Filter to return concepts that are direct children of the given parent specified by the concept ID.")
		.addOperator(FilterOperator.EQUALS)
		.value("concept id")
		.build();
	
	public static final Filter COMMON_CHILD_FILTER = Filter.builder()
		.code(CommonConceptProperties.CHILD.getCodeValue())
		.description("Filter to return concepts that are direct parent(s) of the given child specified by the concept ID.")
		.addOperator(FilterOperator.EQUALS)
		.value("concept id")
		.build();
	
	public static final Filter COMMON_DESCENDANT_FILTER = Filter.builder()
		.code("descendant")
		.description("Filter to return concepts that are descendants of the given concept specified by the concept ID.")
		.addOperator(FilterOperator.EQUALS)
		.value("concept id")
		.build();
	
	public static final Filter COMMON_ANCESTOR_FILTER = Filter.builder()
			.code("ancestor")
			.description("Filter to return concepts that are ancestors of the given concept specified by the concept ID.")
			.addOperator(FilterOperator.EQUALS)
			.value("concept id")
			.build();
			
	//SNOMED CT filters
	/**
	 * Generic is-a containment filter
	 * @see <a href="https://www.hl7.org/fhir/codesystem-snomedct.json.html">IS_A an IN filter</a>
	 */
	public static final Filter IS_A_FILTER = Filter.builder()
		.code(FilterPropertyCode.CONCEPT.getDisplayName())
		.description("Filter to include concepts that subsume the given concept (have a transitive inferred isA relationship to the concept given).")
		.addOperator(FilterOperator.IS_A)
		.value(SNOMED_CT_CONCEPT_ID)
		.build();
	
	/**
	 * Value set containment filter
	 * @see <a href="https://www.hl7.org/fhir/codesystem-snomedct.json.html">IS_A an IN filter</a>
	 */
	public static final Filter REFSET_MEMBER_OF = Filter.builder()
		.code(FilterPropertyCode.CONCEPT.getDisplayName())
		.description("Filter to include concepts that are active members of the reference set given.")
		.addOperator(FilterOperator.IN)
		.value(SNOMED_CT_CONCEPT_ID)
		.build();
	
	/**
	 * SNOMED CT filter
	 * @see <a href="https://www.hl7.org/fhir/codesystem-snomedct.json.html">SNOMED CT ECL filter</a>
	 */
	public static final Filter EXPRESSION_FILTER = Filter.builder()
		.code(FilterPropertyCode.EXPRESSION.getDisplayName())
		.description("Filter result of the given SNOMED CT Expression Constraint")
		.addOperator(FilterOperator.EQUALS)
		.value("SNOMED CT ECL Expression (http://snomed.org/ecl")
		.build();
	
	/**
	 * SNOMED CT filter
	 * @see <a href="https://www.hl7.org/fhir/codesystem-snomedct.json.html">SNOMED CT post cordinated expressions filter</a>
	 */
	public static final Filter EXPRESSIONS_FILTER = Filter.builder()
		.code(FilterPropertyCode.EXPRESSIONS.getDisplayName())
		.description("Whether post-coordinated expressions are included in the value set")
		.addOperator(FilterOperator.EQUALS)
		.value("true or false")
		.build();

}
