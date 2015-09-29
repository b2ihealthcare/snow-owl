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

import static com.b2international.snowowl.core.ApplicationContext.getServiceForClass;
import static com.b2international.snowowl.snomed.api.domain.DescriptionInactivationIndicator.getInactivationIndicatorByValueId;

import java.util.Collection;
import java.util.Map;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.snomed.api.domain.Acceptability;
import com.b2international.snowowl.snomed.api.domain.CaseSignificance;
import com.b2international.snowowl.snomed.api.domain.DescriptionInactivationIndicator;
import com.b2international.snowowl.snomed.api.domain.ISnomedDescription;
import com.b2international.snowowl.snomed.api.impl.domain.SnomedDescription;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.datastore.index.SnomedDescriptionIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.refset.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.services.AbstractSnomedRefSetMembershipLookupService;
import com.b2international.snowowl.snomed.datastore.services.ISnomedComponentService;
import com.google.common.collect.ImmutableMap;

public class SnomedDescriptionConverter extends AbstractSnomedComponentConverter<SnomedDescriptionIndexEntry, ISnomedDescription> {

	public SnomedDescriptionConverter(final AbstractSnomedRefSetMembershipLookupService refSetMembershipLookupService) {
		super(refSetMembershipLookupService);
	}

	@Override
	public ISnomedDescription apply(final SnomedDescriptionIndexEntry input) {
		final SnomedDescription result = new SnomedDescription();
		result.setAcceptabilityMap(toAcceptabilityMap(input.getId()));
		result.setActive(input.isActive());
		result.setCaseSignificance(toCaseSignificance(input.getCaseSignificance()));
		result.setConceptId(input.getConceptId());
		result.setEffectiveTime(toEffectiveTime(input.getEffectiveTimeAsLong()));
		result.setId(input.getId());
		result.setDescriptionInactivationIndicator(getDescriptionInactivationIndicator(input.getId()));
		result.setAssociationTargets(toAssociationTargets(SnomedTerminologyComponentConstants.DESCRIPTION, input.getId()));

		// TODO: index language code on SnomedDescriptionIndexEntries -- it's the only property which is not present.
		result.setLanguageCode("en");

		result.setModuleId(input.getModuleId());
		result.setReleased(input.isReleased());
		result.setTerm(input.getLabel());
		result.setTypeId(input.getType());

		return result;
	}

	private DescriptionInactivationIndicator getDescriptionInactivationIndicator(final String descriptionId) {
		final String inactivationId = getServiceForClass(ISnomedComponentService.class).getDescriptionInactivationId(getBranchPath(), descriptionId);
		return getInactivationIndicatorByValueId(inactivationId);
	}

	private IBranchPath getBranchPath() {
		return getRefSetMembershipLookupService().getBranchPath();
	}

	private Map<String, Acceptability> toAcceptabilityMap(final String descriptionId) {
		final Collection<SnomedRefSetMemberIndexEntry> languageMembers = getRefSetMembershipLookupService().getLanguageMembersForDescription(descriptionId);
		final ImmutableMap.Builder<String, Acceptability> resultsBuilder = ImmutableMap.builder();

		for (final SnomedRefSetMemberIndexEntry languageMember : languageMembers) {
			if (languageMember.isActive()) {
				resultsBuilder.put(languageMember.getRefSetIdentifierId(), toAcceptability(languageMember.getSpecialFieldId()));
			}
		}

		return resultsBuilder.build();
	}

	private Acceptability toAcceptability(final String acceptabilityId) {
		return Acceptability.getByConceptId(acceptabilityId);
	}

	private CaseSignificance toCaseSignificance(final String caseSignificanceId) {
		return CaseSignificance.getByConceptId(caseSignificanceId);
	}
}