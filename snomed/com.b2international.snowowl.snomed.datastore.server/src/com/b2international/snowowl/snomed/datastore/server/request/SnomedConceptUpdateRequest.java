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

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.util.EList;

import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.b2international.snowowl.core.exceptions.ComponentStatusConflictException;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.Description;
import com.b2international.snowowl.snomed.Inactivatable;
import com.b2international.snowowl.snomed.Relationship;
import com.b2international.snowowl.snomed.core.domain.AssociationType;
import com.b2international.snowowl.snomed.core.domain.DefinitionStatus;
import com.b2international.snowowl.snomed.core.domain.InactivationIndicator;
import com.b2international.snowowl.snomed.core.domain.SubclassDefinitionStatus;
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

	private boolean processInactivation(final TransactionContext context, final Concept concept, final Boolean newActive, final InactivationIndicator newInactivationIndicator) {

		if (null != newInactivationIndicator) {
			
			if (null == newActive || !newActive) {
				inactivateConcept(context, concept, newInactivationIndicator);
			} else {
				throw new BadRequestException("Bad");
			}
			
		} else {
			
			if (null == newActive) {
				return false;
			} else if (!newActive) {
				inactivateConcept(context, concept, InactivationIndicator.RETIRED);
			} else {
				reactivateConcept(context, concept);
			}
		}

		return true;
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
		reactivateRelationships(concept.getInboundRelationships());
	}

	private void reactivateRelationships(EList<Relationship> relationships) {
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
