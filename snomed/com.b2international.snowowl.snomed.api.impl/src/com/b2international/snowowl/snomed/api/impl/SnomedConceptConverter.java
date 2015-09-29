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
package com.b2international.snowowl.snomed.api.impl;

import java.util.Collection;

import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.api.domain.DefinitionStatus;
import com.b2international.snowowl.snomed.api.domain.ISnomedConcept;
import com.b2international.snowowl.snomed.api.domain.InactivationIndicator;
import com.b2international.snowowl.snomed.api.domain.SubclassDefinitionStatus;
import com.b2international.snowowl.snomed.api.impl.domain.SnomedConcept;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.datastore.SnomedConceptIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.refset.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.services.AbstractSnomedRefSetMembershipLookupService;
import com.google.common.collect.ImmutableList;

public class SnomedConceptConverter extends AbstractSnomedComponentConverter<SnomedConceptIndexEntry, ISnomedConcept> {

	public SnomedConceptConverter(final AbstractSnomedRefSetMembershipLookupService refSetMembershipLookupService) {
		super(refSetMembershipLookupService);
	}

	@Override
	public ISnomedConcept apply(final SnomedConceptIndexEntry input) {
		final SnomedConcept result = new SnomedConcept();
		result.setActive(input.isActive());
		result.setDefinitionStatus(toDefinitionStatus(input.isPrimitive()));
		result.setEffectiveTime(toEffectiveTime(input.getEffectiveTimeAsLong()));
		result.setId(input.getId());
		result.setModuleId(input.getModuleId());
		result.setReleased(input.isReleased());
		result.setSubclassDefinitionStatus(toSubclassDefinitionStatus(input.isExhaustive()));
		result.setInactivationIndicator(toInactivationIndicator(input.getId()));
		result.setAssociationTargets(toAssociationTargets(SnomedTerminologyComponentConstants.CONCEPT, input.getId()));
		return result;
	}

	private DefinitionStatus toDefinitionStatus(final boolean primitive) {
		return primitive ? DefinitionStatus.PRIMITIVE : DefinitionStatus.FULLY_DEFINED;
	}

	private SubclassDefinitionStatus toSubclassDefinitionStatus(final boolean exhaustive) {
		return exhaustive ? SubclassDefinitionStatus.DISJOINT_SUBCLASSES : SubclassDefinitionStatus.NON_DISJOINT_SUBCLASSES;
	}

	private InactivationIndicator toInactivationIndicator(final String id) {
		final Collection<SnomedRefSetMemberIndexEntry> members = getRefSetMembershipLookupService().getMembers(
				SnomedTerminologyComponentConstants.CONCEPT,
				ImmutableList.of(Concepts.REFSET_CONCEPT_INACTIVITY_INDICATOR),
				id);

		for (final SnomedRefSetMemberIndexEntry member : members) {
			if (member.isActive()) {
				return InactivationIndicator.getByConceptId(member.getSpecialFieldId());
			}
		}

		return null;
	}

}