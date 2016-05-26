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

import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.CONCEPT_NUMBER;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.validation.ComponentValidationConstraint;
import com.b2international.snowowl.core.validation.ComponentValidationDiagnostic;
import com.b2international.snowowl.core.validation.ComponentValidationDiagnosticImpl;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.domain.SnomedDescriptions;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;

/**
 * All concepts should have a fully-specified name of appropriate status.
 * <br/><br/><b>Note:</b> The SNOMED CT concept can be marked as valid if and only if the concept is <b>active</b> 
 * and has <b>exactly one active FSN</b>, but this constraint marks the concept invalid if it has not got any active 
 * FSN if the concept is active. The multiple active FSN validation is an other constraint's business.
 * 
 */
public class SnomedConceptFsnStatusConstraint extends ComponentValidationConstraint<SnomedConceptDocument> {

	public static final String ID = "com.b2international.snowowl.snomed.validation.constraints.component.SnomedConceptFsnStatusConstraint";
	
	@Override
	public ComponentValidationDiagnostic validate(final IBranchPath branchPath, final SnomedConceptDocument concept) {
		if (concept.isActive()) {
			final SnomedDescriptions descriptions = SnomedRequests.prepareSearchDescription()
				.setLimit(0)
				.filterByActive(true)
				.filterByConceptId(concept.getId())
				.filterByType(Concepts.FULLY_SPECIFIED_NAME)
				.build(branchPath.getPath())
				.execute(getBus())
				.getSync();
			if (descriptions.getTotal() < 1) {
				final String errorMessage = String.format("%s has no active fully specified name.", concept.getLabel());
				return new ComponentValidationDiagnosticImpl(concept.getId(), errorMessage, ID, CONCEPT_NUMBER, error());
			}
		}
		return createOk(concept.getId(), ID, CONCEPT_NUMBER);
	}

}