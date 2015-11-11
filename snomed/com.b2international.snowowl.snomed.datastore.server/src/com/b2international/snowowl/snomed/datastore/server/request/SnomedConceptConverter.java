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
package com.b2international.snowowl.snomed.datastore.server.request;

import java.util.Collection;
import java.util.List;

import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.domain.DefinitionStatus;
import com.b2international.snowowl.snomed.core.domain.ISnomedConcept;
import com.b2international.snowowl.snomed.core.domain.InactivationIndicator;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SubclassDefinitionStatus;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.services.AbstractSnomedRefSetMembershipLookupService;
import com.google.common.collect.FluentIterable;
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
	
	public ISnomedConcept apply(final Concept concept) {
		final SnomedConcept result = new SnomedConcept();
		result.setId(concept.getId());
		result.setEffectiveTime(concept.getEffectiveTime());
		result.setActive(concept.isActive());
		result.setDefinitionStatus(toDefinitionStatus(concept.isPrimitive()));
		result.setModuleId(concept.getModule().getId());
		result.setReleased(concept.isReleased());
		result.setSubclassDefinitionStatus(toSubclassDefinitionStatus(concept.isExhaustive()));
		result.setInactivationIndicator(toInactivationIndicator(concept.getId()));
		result.setAssociationTargets(toAssociationTargets(SnomedTerminologyComponentConstants.CONCEPT, concept.getId()));
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
				return member.getInactivationIndicator();
			}
		}

		return null;
	}

	public List<ISnomedConcept> convert(Collection<SnomedConceptIndexEntry> entries) {
		return FluentIterable.from(entries).transform(this).toList();
	}

}