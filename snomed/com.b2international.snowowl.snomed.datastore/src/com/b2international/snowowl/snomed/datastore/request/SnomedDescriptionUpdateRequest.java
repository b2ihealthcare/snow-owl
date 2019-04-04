/*
 * Copyright 2011-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.Description;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.Acceptability;
import com.b2international.snowowl.snomed.core.domain.AssociationType;
import com.b2international.snowowl.snomed.core.domain.CaseSignificance;
import com.b2international.snowowl.snomed.core.domain.DescriptionInactivationIndicator;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
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
	
	private String term;
	private String typeId;
	private String languageCode;
	
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
	
	void setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
	}
	
	void setTerm(String term) {
		this.term = term;
	}
	
	void setTypeId(String typeId) {
		this.typeId = typeId;
	}
	
	@Override
	public Boolean execute(TransactionContext context) {
		final Description description = context.lookup(getComponentId(), Description.class);

		boolean changed = false;
		changed |= updateModule(context, description);
		changed |= updateCaseSignificance(context, description, caseSignificance);
		changed |= updateTypeId(context, description);
		changed |= updateTerm(context, description);
		changed |= updateLanguageCode(context, description);
		changed |= processInactivation(context, description);

		// XXX: acceptability and association changes do not push the effective time forward on the description
		updateAcceptability(context, description);

		if (changed && description.isSetEffectiveTime()) {
			description.unsetEffectiveTime();
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
	
	private boolean updateTypeId(final TransactionContext context, final Description description) {
		if (null == typeId) {
			return false;
		}
		
		if (!description.getType().getId().equals(typeId)) {
			checkUpdateOnReleased(description, SnomedRf2Headers.FIELD_TYPE_ID, typeId);
			description.setType(context.lookup(typeId, Concept.class));
			return true;
		}
		
		return false;
	}
	
	private boolean updateTerm(final TransactionContext context, final Description description) {
		if (null == term) {
			return false;
		}
		
		if (!description.getTerm().equals(term)) {
			description.setTerm(term);
			return true;
		}
		
		return false;
	}
	
	private boolean updateLanguageCode(final TransactionContext context, final Description description) {
		if (null == languageCode) {
			return false;
		}
		
		if (!description.getLanguageCode().equals(languageCode)) {
			checkUpdateOnReleased(description, SnomedRf2Headers.FIELD_LANGUAGE_CODE, languageCode);
			description.setLanguageCode(languageCode);
			return true;
		}
		return false;
	}
	
	@Override
	public Set<String> getRequiredComponentIds(TransactionContext context) {
		final Builder<String> ids = ImmutableSet.<String>builder();
		ids.add(getComponentId());
		if (inactivationIndicator != null) {
			ids.add(inactivationIndicator.getConceptId());
		}
		if (caseSignificance != null) {
			ids.add(caseSignificance.getConceptId());
		}
		if (associationTargets != null && !associationTargets.isEmpty()) {
			associationTargets.entries().forEach(entry -> {
				ids.add(entry.getKey().getConceptId());
				ids.add(entry.getValue());
			});
		}
		if (typeId != null) {
			ids.add(typeId);
		}
		return ids.build();
	}
	
}
