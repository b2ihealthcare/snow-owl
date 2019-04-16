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

import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.Acceptability;
import com.b2international.snowowl.snomed.core.domain.AssociationType;
import com.b2international.snowowl.snomed.core.domain.CaseSignificance;
import com.b2international.snowowl.snomed.core.domain.DescriptionInactivationIndicator;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry;
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
		final SnomedDescriptionIndexEntry description = context.lookup(getComponentId(), SnomedDescriptionIndexEntry.class);
		final SnomedDescriptionIndexEntry.Builder updatedDescription = SnomedDescriptionIndexEntry.builder(description); 

		boolean changed = false;
		changed |= updateModule(context, description, updatedDescription);
		changed |= updateCaseSignificance(context, description, updatedDescription, caseSignificance);
		changed |= updateTypeId(context, description, updatedDescription);
		changed |= updateTerm(context, description, updatedDescription);
		changed |= updateLanguageCode(context, description, updatedDescription);
		changed |= processInactivation(context, description, updatedDescription);

		// XXX: acceptability and association changes do not push the effective time forward on the description
		updateAcceptability(context, description, updatedDescription);

		if (changed) {
			if (description.getEffectiveTime() != EffectiveTimes.UNSET_EFFECTIVE_TIME) {
				updatedDescription.effectiveTime(EffectiveTimes.UNSET_EFFECTIVE_TIME);
			}
			context.update(description, updatedDescription.build());
		}

		return changed;
	}

	private void updateAcceptability(TransactionContext context, final SnomedDescriptionIndexEntry description, final SnomedDescriptionIndexEntry.Builder updatedDescription) {
		new SnomedDescriptionAcceptabilityUpdateRequest(description.getId(), description.getModuleId(), acceptability, false).execute(context);
	}

	private void updateAssociationTargets(TransactionContext context, final SnomedDescriptionIndexEntry description, final Multimap<AssociationType, String> associationTargets) {
		final SnomedAssociationTargetUpdateRequest associationUpdateRequest = new SnomedAssociationTargetUpdateRequest(description.getId(), description.getModuleId());
		associationUpdateRequest.setNewAssociationTargets(associationTargets);
		associationUpdateRequest.execute(context);
	}
	
	private boolean processInactivation(final TransactionContext context, final SnomedDescriptionIndexEntry description, final SnomedDescriptionIndexEntry.Builder updatedDescription) {
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

			updatedDescription.active(false);
			updateInactivationIndicator(context, description, newIndicator);
			updateAssociationTargets(context, description, newAssociationTargets);
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
			
			updatedDescription.active(true);
			updateInactivationIndicator(context, description, newIndicator);
			updateAssociationTargets(context, description, newAssociationTargets);
			return true;
			
		} else if (!currentStatus && !newStatus) {
			
			// Inactive --> Inactive: update indicator and/or association targets if required
			// (using original values that can be null)

			updateInactivationIndicator(context, description, inactivationIndicator);
			updateAssociationTargets(context, description, associationTargets);
			return false;
			
		} else /* if (currentStatus && newStatus) */ {
			return false;
		}
	}

	private void updateInactivationIndicator(final TransactionContext context, final SnomedDescriptionIndexEntry description, final DescriptionInactivationIndicator inactivationIndicator) {
		if (inactivationIndicator == null) {
			return;
		}
		
		final SnomedInactivationReasonUpdateRequest inactivationUpdateRequest = new SnomedInactivationReasonUpdateRequest(
				description.getId(), 
				Concepts.REFSET_DESCRIPTION_INACTIVITY_INDICATOR,
				description.getModuleId());
		
		inactivationUpdateRequest.setInactivationValueId(inactivationIndicator.getConceptId());
		inactivationUpdateRequest.execute(context);
	}

	private boolean updateCaseSignificance(final TransactionContext context, final SnomedDescriptionIndexEntry original, final SnomedDescriptionIndexEntry.Builder description, final CaseSignificance newCaseSignificance) {
		if (null == newCaseSignificance) {
			return false;
		}

		final String existingCaseSignificanceId = original.getCaseSignificanceId();
		final String newCaseSignificanceId = newCaseSignificance.getConceptId();
		if (!existingCaseSignificanceId.equals(newCaseSignificanceId)) {
			description.caseSignificanceId(context.lookup(newCaseSignificanceId, SnomedConceptDocument.class).getId());
			return true;
		} else {
			return false;
		}
	}
	
	private boolean updateTypeId(final TransactionContext context, final SnomedDescriptionIndexEntry original, final SnomedDescriptionIndexEntry.Builder description) {
		if (null == typeId) {
			return false;
		}
		
		if (!original.getTypeId().equals(typeId)) {
			checkUpdateOnReleased(original, SnomedRf2Headers.FIELD_TYPE_ID, typeId);
			description.typeId(context.lookup(typeId, SnomedConceptDocument.class).getId());
			return true;
		}
		
		return false;
	}
	
	private boolean updateTerm(final TransactionContext context, final SnomedDescriptionIndexEntry original, final SnomedDescriptionIndexEntry.Builder description) {
		if (null == term) {
			return false;
		}
		
		if (!original.getTerm().equals(term)) {
			description.term(term);
			return true;
		}
		
		return false;
	}
	
	private boolean updateLanguageCode(final TransactionContext context, final SnomedDescriptionIndexEntry original, final SnomedDescriptionIndexEntry.Builder description) {
		if (null == languageCode) {
			return false;
		}
		
		if (!original.getLanguageCode().equals(languageCode)) {
			checkUpdateOnReleased(original, SnomedRf2Headers.FIELD_LANGUAGE_CODE, languageCode);
			description.languageCode(languageCode);
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
