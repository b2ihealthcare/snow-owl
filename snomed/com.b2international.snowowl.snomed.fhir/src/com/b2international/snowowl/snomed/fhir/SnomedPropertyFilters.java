/*
 * Copyright 2023 B2i Healthcare Pte Ltd, http://b2i.sg
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

import org.hl7.fhir.r5.model.CodeSystem.CodeSystemFilterComponent;
import org.hl7.fhir.r5.model.CodeType;
import org.hl7.fhir.r5.model.Enumerations.FilterOperator;

/**
 * Enumerates property filters that can be used in value set composition
 * statements where the domain is a SNOMED CT code system.
 *
 * @since 9.0
 */
public enum SnomedPropertyFilters {

	IS_A(new CodeSystemFilterComponent("concept", FilterOperator.ISA, "[concept id]")
		.setDescription("Filter to include concepts that subsume the given concept (have a transitive inferred isA relationship to the concept given).")),

	REFSET_MEMBER_OF(new CodeSystemFilterComponent("concept", FilterOperator.IN, "[concept id]")
		.setDescription("Filter to include concepts that are active members of the reference set given.")),

	EXPRESSION(new CodeSystemFilterComponent("expression", FilterOperator.EQUAL, "[expression constraint]")
		.setDescription("Filter result of the given SNOMED CT expression constraint")),

	POST_COORDINATED_EXPRESSIONS(new CodeSystemFilterComponent("expressions", FilterOperator.EQUAL, "true or false")
		.setDescription("Whether post-coordinated expressions are included in the value set"));

	private final CodeSystemFilterComponent filterComponent;

	private SnomedPropertyFilters(final CodeSystemFilterComponent filterComponent) {
		this.filterComponent = filterComponent;
	}

	public CodeType getCode() {
		return filterComponent.getCodeElement();
	}

	public CodeSystemFilterComponent getFilterComponent() {
		return filterComponent;
	}
}
