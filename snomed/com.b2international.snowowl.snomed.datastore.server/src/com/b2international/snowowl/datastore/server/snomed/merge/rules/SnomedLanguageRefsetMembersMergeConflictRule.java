/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore.server.snomed.merge.rules;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.ecore.util.EcoreUtil;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.merge.MergeConflict;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.server.cdo.IMergeConflictRule;
import com.b2international.snowowl.datastore.server.snomed.SnomedMergeConflict;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.Description;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.datastore.services.ISnomedComponentService;
import com.b2international.snowowl.snomed.snomedrefset.SnomedLanguageRefSetMember;

/**
 * @since 4.7
 */
public class SnomedLanguageRefsetMembersMergeConflictRule implements IMergeConflictRule {

	@Override
	public Collection<MergeConflict> validate(CDOTransaction transaction) {

		final IBranchPath branchPath = BranchPathUtils.createPath(transaction);
		final Set<String> synonymAndDescendantIds = ApplicationContext.getServiceForClass(ISnomedComponentService.class).getSynonymAndDescendantIds(
				branchPath);
		final Set<SnomedLanguageRefSetMember> membersToRemove = newHashSet();

		List<MergeConflict> conflicts = newArrayList();

		label: for (CDOObject newObject : transaction.getNewObjects().values()) {

			if (!(newObject instanceof SnomedLanguageRefSetMember)) {
				continue;
			}

			SnomedLanguageRefSetMember newLanguageRefSetMember = (SnomedLanguageRefSetMember) newObject;

			if (!newLanguageRefSetMember.isActive()) {
				continue;
			}

			Description description = (Description) newObject.eContainer();

			if (!description.isActive()) {
				continue;
			}

			String acceptabilityId = newLanguageRefSetMember.getAcceptabilityId();
			String typeId = description.getType().getId();
			String languageRefSetId = newLanguageRefSetMember.getRefSetIdentifierId();

			Concept concept = description.getConcept();

			for (Description conceptDescription : concept.getDescriptions()) {

				if (!conceptDescription.isActive()) {
					continue;
				}

				String conceptDescriptionTypeId = conceptDescription.getType().getId();

				if (!typeId.equals(conceptDescriptionTypeId)
						&& !(synonymAndDescendantIds.contains(typeId) && synonymAndDescendantIds.contains(conceptDescriptionTypeId))) {
					continue;
				}

				for (SnomedLanguageRefSetMember conceptDescriptionMember : conceptDescription.getLanguageRefSetMembers()) {

					if (!conceptDescriptionMember.isActive()) {
						continue;
					}

					if (!languageRefSetId.equals(conceptDescriptionMember.getRefSetIdentifierId())) {
						continue;
					}

					if (conceptDescriptionMember.equals(newLanguageRefSetMember)) {
						continue;
					}

					if (acceptabilityId.equals(conceptDescriptionMember.getAcceptabilityId())) {
						if (description.equals(conceptDescription)) {
							membersToRemove.add(newLanguageRefSetMember);
							continue label;
						} else if (Concepts.REFSET_DESCRIPTION_ACCEPTABILITY_PREFERRED.equals(acceptabilityId)) {
							conflicts.add(new SnomedMergeConflict(newLanguageRefSetMember.getUuid(), conceptDescriptionMember.getUuid(), String
									.format("Two SNOMED CT Descriptions selected as preferred terms. %s <-> %s", description.getId(),
											conceptDescription.getId())));
						}
					} else {
						if (description.equals(conceptDescription)) {
							conflicts.add(new SnomedMergeConflict(newLanguageRefSetMember.getUuid(), conceptDescriptionMember.getUuid(), String
									.format("Different acceptability selected for the same description, %s", description.getId())));
						}
					}
				}
			}
		}

		for (SnomedLanguageRefSetMember memberToRemove : membersToRemove) {
			EcoreUtil.remove(memberToRemove);
		}

		return conflicts;
	}

}
