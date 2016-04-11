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

import java.util.List;

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.validation.ComponentValidationConstraint;
import com.b2international.snowowl.core.validation.ComponentValidationDiagnostic;
import com.b2international.snowowl.core.validation.ComponentValidationDiagnosticImpl;
import com.b2international.snowowl.snomed.core.domain.ISnomedRelationship;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationships;
import com.b2international.snowowl.snomed.core.lang.LanguageSetting;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptIndexEntry;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;

/**
 * A concept cannot be related to itself via an active relationship.
 * 
 */
public class SnomedConceptNotRelatedToItselfConstraint extends ComponentValidationConstraint<SnomedConceptIndexEntry> {

	public static final String ID = "com.b2international.snowowl.snomed.validation.constraints.component.SnomedConceptNotRelatedToItselfConstraint";
	
	@Override
	public ComponentValidationDiagnostic validate(final IBranchPath branchPath, final SnomedConceptIndexEntry component) {
		final String conceptId = component.getId();

		SnomedRelationships relationships = SnomedRequests.prepareSearchRelationship()
			.all()
			.filterByActive(true)
			.filterByDestination(conceptId)
			.filterBySource(conceptId)
			.setExpand("type(expand(pt()))")
			.setLocales(getLocales())
			.build(branchPath.getPath())
			.executeSync(getBus());
		
		if (!relationships.getItems().isEmpty()) {
			for (ISnomedRelationship relationship : relationships) {
				return new ComponentValidationDiagnosticImpl(conceptId, createErrorMessage(component, relationship, branchPath), ID, CONCEPT_NUMBER, error());
			}
		}
		
		return createOk(conceptId, ID, CONCEPT_NUMBER);
	}

	private String createErrorMessage(final SnomedConceptIndexEntry component, final ISnomedRelationship relationship, final IBranchPath branchPath) {
		return String.format("'%s' has an active relationship of type '%s' which points to itself.", component.getLabel(), relationship.getTypeConcept().getPt().getTerm());
	}

	private List<ExtendedLocale> getLocales() {
		return ApplicationContext.getServiceForClass(LanguageSetting.class).getLanguagePreference();
	}
}