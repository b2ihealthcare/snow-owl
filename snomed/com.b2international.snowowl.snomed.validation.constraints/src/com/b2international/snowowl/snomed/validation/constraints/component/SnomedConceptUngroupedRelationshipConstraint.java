/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.validation.constraints.component;

import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.HAS_ACTIVE_INGREDIENT;
import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.HAS_DOSE_FORM;
import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.IS_A;
import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.LATERALITY;
import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.PART_OF;
import static com.google.common.collect.Lists.newArrayList;

import java.util.List;
import java.util.Set;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.validation.ComponentValidationConstraint;
import com.b2international.snowowl.core.validation.ComponentValidationDiagnostic;
import com.b2international.snowowl.core.validation.ComponentValidationDiagnosticImpl;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.domain.ISnomedRelationship;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationships;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.datastore.services.ISnomedConceptNameProvider;
import com.google.common.collect.ImmutableSet;

/**
 * Active relationships of the following types must be ungrouped:
 * <ul>
 * <li>Is a (116680003)</li>
 * <li>Part of (123005000)</li>
 * <li>Laterality (272741003)</li>
 * <li>Has active ingredient (127489000)</li>
 * <li>Has dose form (411116001)</li>
 * </ul>
 */
public class SnomedConceptUngroupedRelationshipConstraint extends ComponentValidationConstraint<SnomedConceptDocument> {

	public static final String ID = "com.b2international.snowowl.snomed.validation.constraints.component.SnomedConceptUngroupedRelationshipConstraint";
	
	private static final Set<String> UNGROUPED_RELATIONSHIP_TYPES = ImmutableSet.of(IS_A, PART_OF, LATERALITY, HAS_ACTIVE_INGREDIENT, HAS_DOSE_FORM);
	
	@Override
	public ComponentValidationDiagnostic validate(final IBranchPath branchPath, final SnomedConceptDocument component) {
		
		final List<ComponentValidationDiagnostic> diagnostics = newArrayList();
		final SnomedRelationships relationships = SnomedRequests.prepareSearchRelationship()
			.all()
			.filterByActive(true)
			.filterBySource(component.getId())
			.filterByGroup(0)
			.filterByType(UNGROUPED_RELATIONSHIP_TYPES)
			.build(branchPath.getPath())
			.execute(getBus())
			.getSync();
		
		for (ISnomedRelationship relationship : relationships) {
			final String relationshipTypeId = relationship.getTypeId();
			final String errorMessage = createErrorMessage(relationshipTypeId, component, branchPath);
			diagnostics.add(new ComponentValidationDiagnosticImpl(component.getId(), errorMessage, ID, SnomedTerminologyComponentConstants.CONCEPT_NUMBER, error()));
		}
		
		if (diagnostics.isEmpty()) {
			return createOk(component.getId(), ID, SnomedTerminologyComponentConstants.CONCEPT_NUMBER);
		} else {
			return new ComponentValidationDiagnosticImpl(component.getId(), ID, SnomedTerminologyComponentConstants.CONCEPT_NUMBER, diagnostics);
		}
	}

	private String createErrorMessage(final String relationshipTypeId, final SnomedConceptDocument component, final IBranchPath branchPath) {
		final String relationshipTypeLabel = getConceptLabel(relationshipTypeId, branchPath);
		return String.format("'%s' has a grouped relationship of the type '%s' that must always be ungrouped.", component.getLabel(), relationshipTypeLabel);
	}

	private String getConceptLabel(final String conceptId, final IBranchPath branchPath) {
		return ApplicationContext.getServiceForClass(ISnomedConceptNameProvider.class).getComponentLabel(branchPath, conceptId);
	}

}