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

import static com.b2international.snowowl.core.ApplicationContext.getServiceForClass;
import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.CONCEPT_NUMBER;
import static com.google.common.base.Preconditions.checkNotNull;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.validation.ComponentValidationConstraint;
import com.b2international.snowowl.core.validation.ComponentValidationDiagnostic;
import com.b2international.snowowl.core.validation.ComponentValidationDiagnosticImpl;
import com.b2international.snowowl.snomed.datastore.SnomedStatementBrowser;
import com.b2international.snowowl.snomed.datastore.SnomedTaxonomyService;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;
import com.b2international.snowowl.snomed.datastore.services.ISnomedRelationshipNameProvider;

/**
 * A concept cannot be related to itself via an active relationship.
 * 
 */
public class SnomedConceptNotRelatedToItselfConstraint extends ComponentValidationConstraint<SnomedConceptIndexEntry> {

	public static final String ID = "com.b2international.snowowl.snomed.validation.constraints.component.SnomedConceptNotRelatedToItselfConstraint";
	
	@Override
	public ComponentValidationDiagnostic validate(final IBranchPath branchPath, final SnomedConceptIndexEntry component) {
		checkNotNull(branchPath, "branchPath");
		checkNotNull(component, "component");
		final SnomedTaxonomyService taxonomyService = getServiceForClass(SnomedTaxonomyService.class);
		final String conceptId = component.getId();

		if (isConceptReferenceItself(branchPath, taxonomyService, conceptId)) {
			final SnomedStatementBrowser statementBrowser = getServiceForClass(SnomedStatementBrowser.class);
			for (final SnomedRelationshipIndexEntry statement : statementBrowser.getOutboundStatements(branchPath, component)) {
				if (statement.isActive() && statement.getValueId().equals(conceptId)) {
					final String relationshipLabel = ApplicationContext.getServiceForClass(ISnomedRelationshipNameProvider.class).getComponentLabel(branchPath, statement.getId());
					final String message = "'" + component.getLabel() + "' has an active relationship '" + relationshipLabel + "' which points to itself.";
					return new ComponentValidationDiagnosticImpl(conceptId, message, ID, CONCEPT_NUMBER, error());
				}
			}
		}
		
			
		return createOk(conceptId, ID, CONCEPT_NUMBER);
	}

	private boolean isConceptReferenceItself(final IBranchPath branchPath, final SnomedTaxonomyService taxonomyService, final String conceptId) {
		return taxonomyService.getSupertypes(branchPath, conceptId).contains(conceptId) //IS_A referencing 
				|| taxonomyService.getOutboundConcepts(branchPath, conceptId).contains(conceptId); //other active relationships
	}

}