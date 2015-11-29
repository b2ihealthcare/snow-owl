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

import static com.google.common.collect.Lists.newArrayList;

import java.util.Map;

import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.Description;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.domain.Acceptability;
import com.b2international.snowowl.snomed.core.domain.AssociationType;
import com.b2international.snowowl.snomed.core.domain.CaseSignificance;
import com.b2international.snowowl.snomed.core.domain.DescriptionInactivationIndicator;
import com.b2international.snowowl.snomed.core.store.SnomedComponents;
import com.b2international.snowowl.snomed.datastore.model.SnomedModelExtensions;
import com.b2international.snowowl.snomed.snomedrefset.SnomedAttributeValueRefSetMember;
import com.google.common.collect.Multimap;

/**
 * @since 4.5
 */
public final class SnomedDescriptionUpdateRequest extends BaseSnomedComponentUpdateRequest {

	private CaseSignificance caseSignificance;
	private Map<String, Acceptability> acceptability;
	private DescriptionInactivationIndicator inactivationIndicator;
	private Multimap<AssociationType, String> associationTargets;
	
	SnomedDescriptionUpdateRequest(String componentId) {
		super(componentId);
	}
	
	void setAcceptability(Map<String, Acceptability> acceptability) {
		this.acceptability = acceptability;
	}
	
	void setAssociationTargets(Multimap<AssociationType, String> associationTargets) {
		this.associationTargets = associationTargets;
	}
	
	void setCaseSignificance(CaseSignificance caseSignificance) {
		this.caseSignificance = caseSignificance;
	}
	
	void setInactivationIndicator(DescriptionInactivationIndicator inactivationIndicator) {
		this.inactivationIndicator = inactivationIndicator;
	}
	
	@Override
	public Void execute(TransactionContext context) {
		final Description description = context.lookup(getComponentId(), Description.class);

		boolean changed = false;
		changed |= updateModule(context, description, getModuleId());
		changed |= updateStatus(context, description, isActive());
		changed |= updateCaseSignificance(context, description, caseSignificance);
		
		updateInactivationIndicator(context, description, isActive(), inactivationIndicator);
		
		updateAssociationTargets(context, description, associationTargets);

		// XXX: acceptability changes do not push the effective time forward on the description
		final SnomedDescriptionAcceptabilityUpdateRequest acceptabilityUpdate = new SnomedDescriptionAcceptabilityUpdateRequest();
		acceptabilityUpdate.setAcceptability(acceptability);
		acceptabilityUpdate.setDescriptionId(description.getId());
		acceptabilityUpdate.execute(context);

		if (changed) {
			description.unsetEffectiveTime();
		}
		return null;
	}

	private void updateInactivationIndicator(TransactionContext context, Description description, Boolean active, DescriptionInactivationIndicator inactivationIndicator) {
		// the description should be inactive (indicated in the update) to be able to update the indicators
		if (Boolean.FALSE.equals(active) && inactivationIndicator != null) {
			boolean found = false;
			for (SnomedAttributeValueRefSetMember member : description.getInactivationIndicatorRefSetMembers()) {
				if (member.isActive()) {
					found = member.getValueId().equals(inactivationIndicator.getValueId());
				}
			}
			if (!found) {
				// inactivate or remove any active member(s) and add the new one
				for (SnomedAttributeValueRefSetMember member : newArrayList(description.getInactivationIndicatorRefSetMembers())) {
					SnomedModelExtensions.removeOrDeactivate(member);
				}
				final SnomedAttributeValueRefSetMember member = SnomedComponents
						.newAttributeValueMember()
						.withReferencedComponent(description.getId())
						.withRefSet(Concepts.REFSET_DESCRIPTION_INACTIVITY_INDICATOR)
						.withModule(description.getModule().getId())
						.withValueId(inactivationIndicator.getValueId())
						.addTo(context);
				description.getInactivationIndicatorRefSetMembers().add(member);
			}
		}
	}

	private boolean updateCaseSignificance(final TransactionContext context, final Description description, final CaseSignificance newCaseSignificance) {
		if (null == newCaseSignificance) {
			return false;
		}

		final String existingCaseSignificanceId = description.getCaseSignificance().getId();
		final String newCaseSignificanceId = newCaseSignificance.getConceptId();
		if (!existingCaseSignificanceId.equals(newCaseSignificanceId)) {
			description.setCaseSignificance(context.lookup(newCaseSignificanceId, Concept.class));
			return true;
		} else {
			return false;
		}
	}

}
