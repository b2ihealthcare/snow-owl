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
package com.b2international.snowowl.snomed.datastore.server.converter;

import static com.b2international.snowowl.core.ApplicationContext.getServiceForClass;
import static com.b2international.snowowl.snomed.core.domain.DescriptionInactivationIndicator.getInactivationIndicatorByValueId;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.domain.CaseSignificance;
import com.b2international.snowowl.snomed.core.domain.DescriptionInactivationIndicator;
import com.b2international.snowowl.snomed.core.domain.ISnomedDescription;
import com.b2international.snowowl.snomed.core.domain.SnomedDescription;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry;
import com.b2international.snowowl.snomed.datastore.services.AbstractSnomedRefSetMembershipLookupService;
import com.b2international.snowowl.snomed.datastore.services.ISnomedComponentService;

public class SnomedDescriptionConverter extends AbstractSnomedComponentConverter<SnomedDescriptionIndexEntry, ISnomedDescription> {

	SnomedDescriptionConverter(final AbstractSnomedRefSetMembershipLookupService refSetMembershipLookupService) {
		super(refSetMembershipLookupService);
	}

	@Override
	public ISnomedDescription apply(final SnomedDescriptionIndexEntry input) {
		final SnomedDescription result = new SnomedDescription();
		result.setAcceptabilityMap(input.getAcceptabilityMap());
		result.setActive(input.isActive());
		result.setCaseSignificance(toCaseSignificance(input.getCaseSignificance()));
		result.setConceptId(input.getConceptId());
		result.setEffectiveTime(toEffectiveTime(input.getEffectiveTimeAsLong()));
		result.setId(input.getId());
		result.setDescriptionInactivationIndicator(getDescriptionInactivationIndicator(input.getId()));
		result.setAssociationTargets(toAssociationTargets(SnomedTerminologyComponentConstants.DESCRIPTION, input.getId()));
		result.setLanguageCode(input.getLanguageCode());
		result.setModuleId(input.getModuleId());
		result.setReleased(input.isReleased());
		result.setTerm(input.getTerm());
		result.setTypeId(input.getTypeId());
		return result;
	}
	
	private DescriptionInactivationIndicator getDescriptionInactivationIndicator(final String descriptionId) {
		final String inactivationId = getServiceForClass(ISnomedComponentService.class).getDescriptionInactivationId(getBranchPath(), descriptionId);
		return getInactivationIndicatorByValueId(inactivationId);
	}

	private IBranchPath getBranchPath() {
		return getRefSetMembershipLookupService().getBranchPath();
	}

	private CaseSignificance toCaseSignificance(final String caseSignificanceId) {
		return CaseSignificance.getByConceptId(caseSignificanceId);
	}
}
