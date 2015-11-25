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

import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.FULLY_SPECIFIED_NAME;
import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.CONCEPT_NUMBER;
import static com.google.common.base.Preconditions.checkNotNull;

import org.apache.lucene.search.Query;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.validation.ComponentValidationConstraint;
import com.b2international.snowowl.core.validation.ComponentValidationDiagnostic;
import com.b2international.snowowl.core.validation.ComponentValidationDiagnosticImpl;
import com.b2international.snowowl.datastore.server.snomed.index.SnomedIndexServerService;
import com.b2international.snowowl.snomed.datastore.index.SnomedIndexService;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedMappings;

/**
 * All concepts should have a fully-specified name of appropriate status.
 * <br/><br/><b>Note:</b> The SNOMED CT concept can be marked as valid if and only if the concept is <b>active</b> 
 * and has <b>exactly one active FSN</b>, but this constraint marks the concept invalid if it has not got any active 
 * FSN if the concept is active. The multiple active FSN validation is an other constraint's business.
 * 
 */
public class SnomedConceptFsnStatusConstraint extends ComponentValidationConstraint<SnomedConceptIndexEntry> {

	public static final String ID = "com.b2international.snowowl.snomed.validation.constraints.component.SnomedConceptFsnStatusConstraint";
	
	@Override
	public ComponentValidationDiagnostic validate(final IBranchPath branchPath, final SnomedConceptIndexEntry concept) {
		checkNotNull(branchPath, "branchPath");
		checkNotNull(concept, "component");
		
		//The SNOMED CT concept can be marked as valid only and if only is active and has exactly one active FSN 
		final String conceptId = concept.getId();
		if (concept.isActive()) {
			final Query query = createQuery(conceptId);
			if (getIndexService().getHitCount(branchPath, query, null) < 1) {
				final String errorMessage = concept.getLabel() + " has no active fully specified name.";
				return new ComponentValidationDiagnosticImpl(conceptId, errorMessage, ID, CONCEPT_NUMBER, error());
			}
		}
		return createOk(conceptId, ID, CONCEPT_NUMBER);
	}

	private SnomedIndexServerService getIndexService() {
		return (SnomedIndexServerService) ApplicationContext.getInstance().getService(SnomedIndexService.class);
	}

	private Query createQuery(final String conceptId) {
		return SnomedMappings.newQuery()
				.active()
				.descriptionConcept(conceptId)
				.descriptionType(FULLY_SPECIFIED_NAME)
				.matchAll();
	}

}