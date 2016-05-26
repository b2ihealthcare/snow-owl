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

import static com.google.common.collect.Lists.newArrayList;

import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.validation.ComponentValidationConstraint;
import com.b2international.snowowl.core.validation.ComponentValidationDiagnostic;
import com.b2international.snowowl.core.validation.ComponentValidationDiagnosticImpl;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.domain.Acceptability;
import com.b2international.snowowl.snomed.core.domain.ISnomedDescription;
import com.b2international.snowowl.snomed.core.domain.SnomedDescriptions;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.datastore.services.ISnomedConceptNameProvider;
import com.google.common.base.Joiner;
import com.google.common.base.Predicates;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

/**
 * From the SNOMED CT TIG:
 * There may be more than one active description with the typeId 900000000000003001 | Fully specified name |. 
 * However, only one fully specified name should be marked as preferred for use in a given language or dialect in the relevant Language reference set
 * 
 */
public class SnomedConceptOneActiveFsnPerLanguageConstraint extends ComponentValidationConstraint<SnomedConceptDocument> {

	public static final String ID = "com.b2international.snowowl.snomed.validation.constraints.component.SnomedConceptOneActiveFsnPerLanguageConstraint";
	
	@Override
	public ComponentValidationDiagnostic validate(final IBranchPath branchPath, final SnomedConceptDocument concept) {

		final SnomedDescriptions descriptions = SnomedRequests.prepareSearchDescription()
			.filterByActive(true)
			.filterByType(Concepts.FULLY_SPECIFIED_NAME)
			.filterByConceptId(concept.getId())
			.build(branchPath.getPath())
			.executeSync(getBus());
		
		if (descriptions.getTotal() > 1) {
			final Multimap<String, String> languageRefsetIdToDescriptionIdMap = HashMultimap.create(); 
			
			for (final ISnomedDescription description : descriptions) {
				final Set<String> languageRefsetIdsWithPreferredMember = Maps.filterValues(description.getAcceptabilityMap(), Predicates.equalTo(Acceptability.PREFERRED)).keySet();
				for (final String id : languageRefsetIdsWithPreferredMember) {
					languageRefsetIdToDescriptionIdMap.put(id, description.getId());
				}
			}
			
			final List<ComponentValidationDiagnostic> diagnostics = newArrayList();
			
			for (final Entry<String, Collection<String>> entry : languageRefsetIdToDescriptionIdMap.asMap().entrySet()) {
				if (entry.getValue().size() > 1) {
					diagnostics.add(new ComponentValidationDiagnosticImpl(concept.getId(), createErrorMessage(concept, entry, branchPath), ID, SnomedTerminologyComponentConstants.CONCEPT_NUMBER, error()));
				}
			}
			
			if (!diagnostics.isEmpty()) {
				return new ComponentValidationDiagnosticImpl(concept.getId(), ID, SnomedTerminologyComponentConstants.CONCEPT_NUMBER, diagnostics);
			}
		}
		
		return createOk(concept.getId(), ID, SnomedTerminologyComponentConstants.CONCEPT_NUMBER);
	}
	
	private String createErrorMessage(final SnomedConceptDocument concept, final Entry<String, Collection<String>> entry, final IBranchPath branchPath) {
		return String.format(
				"%s has multiple active fully specified name marked as preferred in language reference set %s | %s (description ids: %s).",
				concept.getId(), entry.getKey(), getConceptLabel(entry.getKey(), branchPath), Joiner.on(", ").join(entry.getValue()));
	}

	private String getConceptLabel(final String conceptId, final IBranchPath branchPath) {
		return ApplicationContext.getServiceForClass(ISnomedConceptNameProvider.class).getComponentLabel(branchPath, conceptId);
	}

}