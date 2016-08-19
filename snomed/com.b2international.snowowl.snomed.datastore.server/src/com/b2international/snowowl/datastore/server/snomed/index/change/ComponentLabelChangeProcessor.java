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
package com.b2international.snowowl.datastore.server.snomed.index.change;

import org.eclipse.emf.cdo.common.id.CDOIDUtil;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.datastore.ICDOCommitChangeSet;
import com.b2international.snowowl.datastore.index.ChangeSetProcessorBase;
import com.b2international.snowowl.datastore.index.ComponentCompareFieldsUpdater;
import com.b2international.snowowl.datastore.server.snomed.index.SnomedIndexServerService;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.Description;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedDocumentBuilder;
import com.b2international.snowowl.snomed.snomedrefset.SnomedLanguageRefSetMember;

/**
 * Marks a concept as changed if any preferred language reference set member appears on a non-FSN description.
 * 
 * @since 4.3
 */
public class ComponentLabelChangeProcessor extends ChangeSetProcessorBase<SnomedDocumentBuilder> {

	public ComponentLabelChangeProcessor(IBranchPath branchPath, SnomedIndexServerService index) {
		super("label changes");
	}
	
	@Override
	public void process(ICDOCommitChangeSet commitChangeSet) {
		for (SnomedLanguageRefSetMember member : getNewComponents(commitChangeSet, SnomedLanguageRefSetMember.class)) {
			if (Concepts.REFSET_DESCRIPTION_ACCEPTABILITY_PREFERRED.equals(member.getAcceptabilityId()) && member.eContainer() instanceof Description) {
				final Description description = (Description) member.eContainer();
				if (!Concepts.FULLY_SPECIFIED_NAME.equals(description.getType().getId())) {
					final Concept relatedConcept = description.getConcept();
					final String conceptId = relatedConcept.getId();
					registerUpdate(conceptId, new ComponentCompareFieldsUpdater<SnomedDocumentBuilder>(conceptId, CDOIDUtil.getLong(relatedConcept.cdoID())));
				}
			}
		}
	}
}
