/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.request;

import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.Description;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.domain.Acceptability;
import com.b2international.snowowl.snomed.core.domain.AssociationType;
import com.b2international.snowowl.snomed.core.domain.CaseSignificance;
import com.b2international.snowowl.snomed.core.domain.DescriptionInactivationIndicator;
import com.b2international.snowowl.snomed.core.domain.SnomedDescription;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

/**
 * @since 4.5
 */
public final class SnomedDescriptionUpdateRequest extends SnomedComponentUpdateRequest {

	private static final Logger LOGGER = LoggerFactory.getLogger(SnomedDescriptionUpdateRequest.class);

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
	public Boolean execute(TransactionContext context) {
		final Description description = context.lookup(getComponentId(), Description.class);

		boolean changed = false;
		changed |= updateModule(context, description);
		changed |= updateCaseSignificance(context, description, caseSignificance);
		changed |= processInactivation(context, description);

		// XXX: acceptability and association changes do not push the effective time forward on the description
		updateAcceptability(context, description);

		if (changed) {
			if (description.isSetEffectiveTime()) {
				description.unsetEffectiveTime();
			} else {
				if (description.isReleased()) {
					long start = new Date().getTime();
					final String branchPath = getLatestReleaseBranch(context);
					final IEventBus bus = context.service(IEventBus.class);
					final SnomedDescription releasedDescription = SnomedRequests
						.prepareGetDescription(getComponentId())
						.build(SnomedDatastoreActivator.REPOSITORY_UUID, branchPath)
						.execute(bus)
						.getSync();
	
					if (!isDifferentToPreviousRelease(description, releasedDescription)) {
						description.setEffectiveTime(releasedDescription.getEffectiveTime());
					}
					LOGGER.info("Previous version comparison took {}", new Date().getTime() - start);
				}
			}
		}
		
		return changed;
	}

	private void updateAcceptability(TransactionContext context, final Description description) {
		final SnomedDescriptionAcceptabilityUpdateRequest acceptabilityUpdate = new SnomedDescriptionAcceptabilityUpdateRequest();
		acceptabilityUpdate.setAcceptability(acceptability);
		acceptabilityUpdate.setDescriptionId(description.getId());
		acceptabilityUpdate.execute(context);
	}

	private void updateAssociationTargets(TransactionContext context, final Multimap<AssociationType, String> associationTargets) {
		final SnomedAssociationTargetUpdateRequest<Description> associationUpdateRequest = new SnomedAssociationTargetUpdateRequest<>(getComponentId(), Description.class);
		associationUpdateRequest.setNewAssociationTargets(associationTargets);
		associationUpdateRequest.execute(context);
	}
	
	private boolean processInactivation(final TransactionContext context, final Description description) {
		if (null == isActive() && null == inactivationIndicator && null == associationTargets) {
			return false;
		}
		
		final boolean currentStatus = description.isActive();
		final boolean newStatus = isActive() == null ? currentStatus : isActive();
		final DescriptionInactivationIndicator newIndicator = inactivationIndicator == null ? DescriptionInactivationIndicator.RETIRED : inactivationIndicator;
		final Multimap<AssociationType, String> newAssociationTargets = associationTargets == null ? ImmutableMultimap.<AssociationType, String>of() : associationTargets;
		
		if (currentStatus && !newStatus) {
			
			// Active --> Inactive: description inactivation, update indicator and association targets
			// (using default values if not given)

			description.setActive(false);
			updateInactivationIndicator(context, newIndicator);
			updateAssociationTargets(context, newAssociationTargets);
			return true;
			
		} else if (!currentStatus && newStatus) {
			
			// Inactive --> Active: description reactivation, clear indicator and association targets
			// (using default values at all times)

			if (inactivationIndicator != null) {
				throw new BadRequestException("Cannot reactivate description and retain or change its inactivation indicator at the same time.");
			}
			
			if (associationTargets != null) {
				throw new BadRequestException("Cannot reactivate description and retain or change its historical association target(s) at the same time.");
			}
			
			description.setActive(true);
			updateInactivationIndicator(context, newIndicator);
			updateAssociationTargets(context, newAssociationTargets);
			return true;
			
		} else if (!currentStatus && !newStatus) {
			
			// Inactive --> Inactive: update indicator and/or association targets if required
			// (using original values that can be null)

			updateInactivationIndicator(context, inactivationIndicator);
			updateAssociationTargets(context, associationTargets);
			return false;
			
		} else /* if (currentStatus && newStatus) */ {
			return false;
		}
	}

	private void updateInactivationIndicator(final TransactionContext context, final DescriptionInactivationIndicator inactivationIndicator) {
		if (inactivationIndicator == null) {
			return;
		}
		
		final SnomedInactivationReasonUpdateRequest<Description> inactivationUpdateRequest = new SnomedInactivationReasonUpdateRequest<>(
				getComponentId(), 
				Description.class, 
				Concepts.REFSET_DESCRIPTION_INACTIVITY_INDICATOR);
		
		inactivationUpdateRequest.setInactivationValueId(inactivationIndicator.getConceptId());
		inactivationUpdateRequest.execute(context);
	}

	private boolean isDifferentToPreviousRelease(Description description, SnomedDescription releasedDescription) {
		if (releasedDescription.isActive() != description.isActive()) return true;
		if (!releasedDescription.getModuleId().equals(description.getModule().getId())) return true;
		if (!releasedDescription.getConceptId().equals(description.getConcept().getId())) return true;
		if (!releasedDescription.getLanguageCode().equals(description.getLanguageCode())) return true;
		if (!releasedDescription.getTypeId().equals(description.getType().getId())) return true;
		if (!releasedDescription.getTerm().equals(description.getTerm())) return true;
		if (!releasedDescription.getCaseSignificance().getConceptId().equals(description.getCaseSignificance().getId())) return true;

		return false;
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
