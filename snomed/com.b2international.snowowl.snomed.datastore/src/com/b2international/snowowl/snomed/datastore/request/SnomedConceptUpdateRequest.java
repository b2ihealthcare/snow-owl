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
package com.b2international.snowowl.snomed.datastore.request;

import java.util.Date;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.b2international.snowowl.core.exceptions.ComponentStatusConflictException;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.Description;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.domain.AssociationType;
import com.b2international.snowowl.snomed.core.domain.DefinitionStatus;
import com.b2international.snowowl.snomed.core.domain.DescriptionInactivationIndicator;
import com.b2international.snowowl.snomed.core.domain.InactivationIndicator;
import com.b2international.snowowl.snomed.core.domain.SubclassDefinitionStatus;
import com.b2international.snowowl.snomed.datastore.SnomedEditingContext;
import com.b2international.snowowl.snomed.datastore.SnomedInactivationPlan;
import com.b2international.snowowl.snomed.datastore.SnomedInactivationPlan.InactivationReason;
import com.b2international.snowowl.snomed.datastore.SnomedTerminologyBrowser;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptIndexEntry;
import com.b2international.snowowl.snomed.datastore.model.SnomedModelExtensions;
import com.b2international.snowowl.snomed.snomedrefset.SnomedAttributeValueRefSetMember;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

/**
 * @since 4.5
 */
public final class SnomedConceptUpdateRequest extends BaseSnomedComponentUpdateRequest {

	private static final Logger LOGGER = LoggerFactory.getLogger(SnomedConceptUpdateRequest.class);

	private DefinitionStatus definitionStatus;
	private SubclassDefinitionStatus subclassDefinitionStatus;
	private InactivationIndicator inactivationIndicator;
	private Multimap<AssociationType, String> associationTargets;
	
	SnomedConceptUpdateRequest(String componentId) {
		super(componentId);
	}
	
	void setDefinitionStatus(DefinitionStatus definitionStatus) {
		this.definitionStatus = definitionStatus;
	}
	
	void setSubclassDefinitionStatus(SubclassDefinitionStatus subclassDefinitionStatus) {
		this.subclassDefinitionStatus = subclassDefinitionStatus;
	}
	
	void setInactivationIndicator(InactivationIndicator inactivationIndicator) {
		this.inactivationIndicator = inactivationIndicator;
	}
	
	void setAssociationTargets(Multimap<AssociationType, String> associationTargets) {
		this.associationTargets = associationTargets;
	}
	
	@Override
	public Void execute(TransactionContext context) {
		final Concept concept = context.lookup(getComponentId(), Concept.class);

		boolean changed = false;
		changed |= updateModule(context, concept);
		changed |= updateDefinitionStatus(context, concept);
		changed |= updateSubclassDefinitionStatus(context, concept);
		changed |= processInactivation(context, concept);

		if (changed) {
			if (concept.isSetEffectiveTime()) {
				concept.unsetEffectiveTime();
			} else {
				if (concept.isReleased()) {
					long start = new Date().getTime();
					final IBranchPath branchPath = getLatestReleaseBranch(context);
					final SnomedTerminologyBrowser terminologyBrowser = context.service(SnomedTerminologyBrowser.class);
					final SnomedConceptIndexEntry releasedConcept = terminologyBrowser.getConcept(branchPath, getComponentId());	
					if (!isDifferentToPreviousRelease(concept, releasedConcept)) {
						concept.setEffectiveTime(EffectiveTimes.parse(releasedConcept.getEffectiveTime()));
					}
					LOGGER.info("Previous version comparison took {}", new Date().getTime() - start);
				}
			}
		}
		
		return null;
	}

	private boolean isDifferentToPreviousRelease(Concept concept, SnomedConceptIndexEntry releasedConcept) {
		if (releasedConcept.isActive() != concept.isActive()) return true;
		if (!releasedConcept.getModuleId().equals(concept.getModule().getId())) return true;
		if (releasedConcept.isPrimitive() != concept.isPrimitive()) return true;
		return false;
	}

	private boolean updateDefinitionStatus(final TransactionContext context, final Concept concept) {
		if (null == definitionStatus) {
			return false;
		}

		final String existingDefinitionStatusId = concept.getDefinitionStatus().getId();
		final String newDefinitionStatusId = definitionStatus.getConceptId();
		if (!existingDefinitionStatusId.equals(newDefinitionStatusId)) {
			concept.setDefinitionStatus(context.lookup(newDefinitionStatusId, Concept.class));
			return true;
		} else {
			return false;
		}
	}

	private boolean updateSubclassDefinitionStatus(final TransactionContext context, final Concept concept) {
		if (null == subclassDefinitionStatus) {
			return false;
		}

		final boolean currentExhaustive = concept.isExhaustive();
		final boolean newExhaustive = subclassDefinitionStatus.isExhaustive();
		if (currentExhaustive != newExhaustive) {
			concept.setExhaustive(newExhaustive);
			return true;
		} else {
			return false;
		}
	}

	private boolean processInactivation(final TransactionContext context, final Concept concept) {
		if (null == isActive() && null == inactivationIndicator && null == associationTargets) {
			return false;
		}
		
		final boolean currentStatus = concept.isActive();
		final boolean newStatus = isActive() == null ? currentStatus : isActive();
		final InactivationIndicator newIndicator = inactivationIndicator == null ? InactivationIndicator.RETIRED : inactivationIndicator; 
		final Multimap<AssociationType, String> newAssociationTargets = associationTargets == null ? ImmutableMultimap.<AssociationType, String>of() : associationTargets;
		
		if (currentStatus && !newStatus) {
			
			// Active --> Inactive: concept inactivation, update indicator and association targets
			// (using default values if not given)
			
			inactivateConcept(context, concept);
			updateInactivationIndicator(context, newIndicator);
			updateAssociationTargets(context, newAssociationTargets);
			return true;
			
		} else if (!currentStatus && newStatus) {
			
			// Inactive --> Active: concept reactivation, clear indicator and association targets
			// (using default values at all times)
			
			if (inactivationIndicator != null) {
				throw new BadRequestException("Cannot reactivate concept and retain or change its inactivation indicator at the same time.");
			}
			
			if (associationTargets != null) {
				throw new BadRequestException("Cannot reactivate concept and retain or change its historical association targets at the same time.");
			}
			
			reactivateConcept(context, concept);
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

	private void updateAssociationTargets(final TransactionContext context, Multimap<AssociationType, String> associationTargets) {
		if (associationTargets == null) {
			return;
		}
		
		SnomedAssociationTargetUpdateRequest<Concept> associationUpdateRequest = new SnomedAssociationTargetUpdateRequest<>(getComponentId(), Concept.class);
		associationUpdateRequest.setNewAssociationTargets(associationTargets);
		associationUpdateRequest.execute(context);
	}

	private void updateInactivationIndicator(final TransactionContext context, final InactivationIndicator indicator) {
		if (indicator == null) {
			return;
		}
		
		final SnomedInactivationReasonUpdateRequest<Concept> inactivationUpdateRequest = new SnomedInactivationReasonUpdateRequest<>(
				getComponentId(), 
				Concept.class, 
				Concepts.REFSET_CONCEPT_INACTIVITY_INDICATOR);
		
		inactivationUpdateRequest.setInactivationValueId(indicator.getConceptId());
		inactivationUpdateRequest.execute(context);
	}

	private void inactivateConcept(final TransactionContext context, final Concept concept) {
		if (!concept.isActive()) {
			throw new ComponentStatusConflictException(concept.getId(), concept.isActive());
		}
		
		// Run the basic inactivation plan without settings the inactivation reason or a historical association target; those will be handled separately
		final SnomedEditingContext editingContext = context.service(SnomedEditingContext.class);
		final SnomedInactivationPlan inactivationPlan = editingContext.inactivateConcept(new NullProgressMonitor(), concept.getId());
		inactivationPlan.performInactivation(InactivationReason.RETIRED, null);
	}

	private void reactivateConcept(final TransactionContext context, final Concept concept) {
		if (concept.isActive()) {
			throw new ComponentStatusConflictException(concept.getId(), concept.isActive());
		}
		
		concept.setActive(true);
		
		for (final Description description : concept.getDescriptions()) {
			// Remove "Concept non-current" reason from active descriptions
			if (description.isActive()) {
				for (SnomedAttributeValueRefSetMember attributeValueMember : ImmutableList.copyOf(description.getInactivationIndicatorRefSetMembers())) {
					if (DescriptionInactivationIndicator.CONCEPT_NON_CURRENT.getConceptId().equals(attributeValueMember.getValueId())) {
						SnomedModelExtensions.removeOrDeactivate(attributeValueMember);
					}
				}
			}
		}
	}
}
