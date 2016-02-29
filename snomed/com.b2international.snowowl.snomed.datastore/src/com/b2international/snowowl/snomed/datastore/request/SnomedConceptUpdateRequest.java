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

import static com.google.common.collect.Lists.newArrayList;

import org.eclipse.core.runtime.NullProgressMonitor;

import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.b2international.snowowl.core.exceptions.ComponentStatusConflictException;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.Description;
import com.b2international.snowowl.snomed.Inactivatable;
import com.b2international.snowowl.snomed.Relationship;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.domain.AssociationType;
import com.b2international.snowowl.snomed.core.domain.DefinitionStatus;
import com.b2international.snowowl.snomed.core.domain.InactivationIndicator;
import com.b2international.snowowl.snomed.core.domain.SubclassDefinitionStatus;
import com.b2international.snowowl.snomed.core.store.SnomedComponents;
import com.b2international.snowowl.snomed.datastore.SnomedEditingContext;
import com.b2international.snowowl.snomed.datastore.SnomedInactivationPlan;
import com.b2international.snowowl.snomed.datastore.model.SnomedModelExtensions;
import com.b2international.snowowl.snomed.snomedrefset.SnomedAssociationRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedAttributeValueRefSetMember;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;

/**
 * @since 4.5
 */
public final class SnomedConceptUpdateRequest extends BaseSnomedComponentUpdateRequest {

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
		changed |= updateModule(context, concept, getModuleId());
		changed |= updateDefinitionStatus(context, concept, definitionStatus);
		changed |= updateSubclassDefinitionStatus(context, concept, subclassDefinitionStatus);
		changed |= processInactivation(context, concept, isActive(), inactivationIndicator);

		updateAssociationTargets(context, concept, associationTargets);

		if (changed) {
			concept.unsetEffectiveTime();
		}
		return null;
	}

	private boolean updateDefinitionStatus(final TransactionContext context, final Concept concept, final DefinitionStatus newDefinitionStatus) {
		if (null == newDefinitionStatus) {
			return false;
		}

		final String existingDefinitionStatusId = concept.getDefinitionStatus().getId();
		final String newDefinitionStatusId = newDefinitionStatus.getConceptId();
		if (!existingDefinitionStatusId.equals(newDefinitionStatusId)) {
			concept.setDefinitionStatus(context.lookup(newDefinitionStatusId, Concept.class));
			return true;
		} else {
			return false;
		}
	}

	private boolean updateSubclassDefinitionStatus(final TransactionContext context, final Concept concept,
			final SubclassDefinitionStatus newSubclassDefinitionStatus) {

		if (null == newSubclassDefinitionStatus) {
			return false;
		}

		final boolean currentExhaustive = concept.isExhaustive();
		final boolean newExhaustive = newSubclassDefinitionStatus.isExhaustive();
		if (currentExhaustive != newExhaustive) {
			concept.setExhaustive(newExhaustive);
			return true;
		} else {
			return false;
		}
	}

	// TODO merge with SnomedDescriptionUpdateRequest.updateInactivationIndicator
	private boolean processInactivation(final TransactionContext context, final Concept concept, final Boolean inputStatus, final InactivationIndicator inputIndicator) {
		final boolean status = inputStatus == null ? concept.isActive() : inputStatus;
		final InactivationIndicator indicator = inputIndicator == null ? InactivationIndicator.RETIRED : inputIndicator; 
		
		if (!status && concept.isActive()) {
			inactivateConcept(context, concept, indicator);
			return true;
		} else if (status && !concept.isActive()) {
			if (inputIndicator != null) {
				throw new BadRequestException("Cannot reactivate concept and retain or change its inactivation indicators in the same time");
			}
			reactivateConcept(context, concept);
			return true;
		} else if (!status && !concept.isActive()) {
			// if the concept is already inactive, then check the current inactivation members
			boolean found = false;
			for (SnomedAttributeValueRefSetMember member : concept.getInactivationIndicatorRefSetMembers()) {
				if (member.isActive() && member.getValueId().equals(indicator.getConceptId())) {
					found = true;
					break;
				}
			}
			if (!found) {
				// remove or deactivate all currently active members
				for (SnomedAttributeValueRefSetMember member : newArrayList(concept.getInactivationIndicatorRefSetMembers())) {
					if (member.isActive()) {
						SnomedModelExtensions.removeOrDeactivate(member);
					}
				}
				// add the new member if not retired indicator
				if (!InactivationIndicator.RETIRED.equals(indicator)) {
					final SnomedAttributeValueRefSetMember member = SnomedComponents
							.newAttributeValueMember()
							.withReferencedComponent(concept.getId())
							.withRefSet(Concepts.REFSET_CONCEPT_INACTIVITY_INDICATOR)
							.withModule(concept.getModule().getId())
							.withValueId(indicator.getConceptId())
							.addTo(context);
					concept.getInactivationIndicatorRefSetMembers().add(member);
				}
			}
		}
		return false;
	}

	private void inactivateConcept(final TransactionContext context, final Concept concept, final InactivationIndicator newInactivationIndicator) {
		if (!concept.isActive()) {
			throw new ComponentStatusConflictException(concept.getId(), concept.isActive());
		}
		final SnomedEditingContext editingContext = context.service(SnomedEditingContext.class);
		final SnomedInactivationPlan inactivationPlan = editingContext.inactivateConcept(new NullProgressMonitor(), concept.getId());
		inactivationPlan.performInactivation(newInactivationIndicator.toInactivationReason(), null);
	}

	private void reactivateConcept(final TransactionContext context, final Concept concept) {
		if (concept.isActive()) {
			throw new ComponentStatusConflictException(concept.getId(), concept.isActive());
		}
		
		concept.setActive(true);
		
		for (final SnomedAssociationRefSetMember associationMember : ImmutableList.copyOf(concept.getAssociationRefSetMembers())) {
			SnomedModelExtensions.removeOrDeactivate(associationMember);
		}
		
		removeOrDeactivateInactivationIndicators(concept);
		
		for (final Description description : concept.getDescriptions()) {
			removeOrDeactivateInactivationIndicators(description);
		}
		
		reactivateRelationships(concept.getOutboundRelationships());
		reactivateRelationships(context.service(SnomedEditingContext.class).getInboundRelationships(concept.getId()));
	}

	private void reactivateRelationships(Iterable<Relationship> relationships) {
		for (final Relationship relationship : relationships) {
			if (!relationship.isActive()) {
				relationship.setActive(true);
				relationship.unsetEffectiveTime();
			}
		}
	}

	private void removeOrDeactivateInactivationIndicators(Inactivatable component) {
		for (final SnomedAttributeValueRefSetMember attributeValueMember : ImmutableList.copyOf(component.getInactivationIndicatorRefSetMembers())) {
			SnomedModelExtensions.removeOrDeactivate(attributeValueMember);
		}
	}

}
