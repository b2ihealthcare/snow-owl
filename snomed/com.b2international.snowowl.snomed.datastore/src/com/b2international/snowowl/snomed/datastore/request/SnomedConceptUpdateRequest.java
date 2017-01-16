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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.b2international.snowowl.core.exceptions.ComponentStatusConflictException;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.Description;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.domain.AssociationType;
import com.b2international.snowowl.snomed.core.domain.DefinitionStatus;
import com.b2international.snowowl.snomed.core.domain.DescriptionInactivationIndicator;
import com.b2international.snowowl.snomed.core.domain.ISnomedConcept;
import com.b2international.snowowl.snomed.core.domain.ISnomedRelationship;
import com.b2international.snowowl.snomed.core.domain.InactivationIndicator;
import com.b2international.snowowl.snomed.core.domain.SnomedComponent;
import com.b2international.snowowl.snomed.core.domain.SnomedDescription;
import com.b2international.snowowl.snomed.core.domain.SubclassDefinitionStatus;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSet;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.SnomedEditingContext;
import com.b2international.snowowl.snomed.datastore.SnomedInactivationPlan;
import com.b2international.snowowl.snomed.datastore.SnomedInactivationPlan.InactivationReason;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

/**
 * @since 4.5
 */
public final class SnomedConceptUpdateRequest extends BaseSnomedComponentUpdateRequest {

	private static final Logger LOGGER = LoggerFactory.getLogger(SnomedConceptUpdateRequest.class);

	private DefinitionStatus definitionStatus;
	private SubclassDefinitionStatus subclassDefinitionStatus;
	private InactivationIndicator inactivationIndicator;
	private Multimap<AssociationType, String> associationTargets;
	private List<SnomedDescription> descriptions;
	private List<ISnomedRelationship> relationships;
	private List<SnomedReferenceSetMember> members;
	
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
	
	void setDescriptions(List<SnomedDescription> descriptions) {
		this.descriptions = descriptions;
	}
	
	void setRelationships(List<ISnomedRelationship> relationships) {
		this.relationships = relationships;
	}
	
	void setMembers(List<SnomedReferenceSetMember> members) {
		this.members = members;
	}
	
	@Override
	public Boolean execute(TransactionContext context) {
		final Concept concept = context.lookup(getComponentId(), Concept.class);

		boolean changed = false;
		changed |= updateModule(context, concept);
		changed |= updateDefinitionStatus(context, concept);
		changed |= updateSubclassDefinitionStatus(context, concept);
		changed |= updateComponents(context, concept, concept.getDescriptions(), descriptions, description -> description.getId(), id -> SnomedRequests.prepareDeleteDescription().setComponentId(id).build());
		changed |= updateComponents(context, concept, concept.getOutboundRelationships(), relationships, relationship -> relationship.getId(), id -> SnomedRequests.prepareDeleteRelationship().setComponentId(id).build());
		// TODO load all members referencing this concept except inactivation related ones
		// XXX currently we support only concrete domain members to be updated
		changed |= updateComponents(context, concept, concept.getConcreteDomainRefSetMembers(), filterMembers(context, members), member -> member.getUuid(), id -> SnomedRequests.prepareDeleteMember().setComponentId(id).build());
		changed |= processInactivation(context, concept);

		if (changed) {
			if (concept.isSetEffectiveTime()) {
				concept.unsetEffectiveTime();
			} else {
				if (concept.isReleased()) {
					long start = new Date().getTime();
					final String branchPath = getLatestReleaseBranch(context);
					final ISnomedConcept releasedConcept = SnomedRequests.prepareGetConcept()
							.setComponentId(getComponentId())
							.build(SnomedDatastoreActivator.REPOSITORY_UUID, branchPath)
							.execute(context.service(IEventBus.class))
							.getSync();
					if (!isDifferentToPreviousRelease(concept, releasedConcept)) {
						concept.setEffectiveTime(releasedConcept.getEffectiveTime());
					}
					LOGGER.info("Previous version comparison took {}", new Date().getTime() - start);
				}
			}
		}
		
		return changed;
	}

	private Iterable<SnomedReferenceSetMember> filterMembers(TransactionContext context, List<SnomedReferenceSetMember> members) {
		if (members == null) return null;
		final Set<String> referenceSets = members.stream().map(member -> member.getReferenceSetId()).collect(Collectors.toSet());
		final Map<String, SnomedReferenceSet> refSetsById = SnomedRequests.prepareSearchRefSet()
				.setLimit(referenceSets.size())
				.setComponentIds(referenceSets)
				.build()
				.execute(context)
				.getItems()
				.stream()
				.collect(Collectors.toMap(SnomedReferenceSet::getId, Function.identity()));
		
		return members.stream()
				.filter(member -> refSetsById.get(member.getReferenceSetId()).getType() == SnomedRefSetType.CONCRETE_DATA_TYPE)
				.collect(Collectors.toSet());
	}

	private boolean isDifferentToPreviousRelease(Concept concept, ISnomedConcept releasedConcept) {
		if (releasedConcept.isActive() != concept.isActive()) return true;
		if (!releasedConcept.getModuleId().equals(concept.getModule().getId())) return true;
		if (!releasedConcept.getDefinitionStatus().getConceptId().equals(concept.getDefinitionStatus().getId())) return true;
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
		
		// The inactivation plan places new inactivation reason members on descriptions, even if one is already present. Fix this by running the update on the descriptions again.
		for (final Description description : concept.getDescriptions()) {
			// Add "Concept non-current" reason to active descriptions
			if (description.isActive()) {
				SnomedInactivationReasonUpdateRequest<Description> descriptionUpdateRequest = new SnomedInactivationReasonUpdateRequest<>(
						description.getId(), 
						Description.class, 
						Concepts.REFSET_DESCRIPTION_INACTIVITY_INDICATOR);
				
				// XXX: The only other inactivation reason an active description can have is "Pending move"; not sure what the implications are
				descriptionUpdateRequest.setInactivationValueId(DescriptionInactivationIndicator.CONCEPT_NON_CURRENT.getConceptId());
				descriptionUpdateRequest.execute(context);
			}
		}
	}

	private void reactivateConcept(final TransactionContext context, final Concept concept) {
		if (concept.isActive()) {
			throw new ComponentStatusConflictException(concept.getId(), concept.isActive());
		}
		
		concept.setActive(true);
		
		for (final Description description : concept.getDescriptions()) {
			// Remove "Concept non-current" reason from active descriptions by changing to "no reason given"
			if (description.isActive()) {
				SnomedInactivationReasonUpdateRequest<Description> descriptionUpdateRequest = new SnomedInactivationReasonUpdateRequest<>(
						description.getId(), 
						Description.class, 
						Concepts.REFSET_DESCRIPTION_INACTIVITY_INDICATOR);
				
				descriptionUpdateRequest.setInactivationValueId(DescriptionInactivationIndicator.RETIRED.getConceptId());
				descriptionUpdateRequest.execute(context);
			}
		}
	}
	
	private <T extends EObject, U extends SnomedComponent> boolean updateComponents(final TransactionContext context, 
			final Concept concept, 
			final Iterable<T> previousComponents,
			final Iterable<U> currentComponents, 
			final com.google.common.base.Function<T, String> idProvider,
			final Function<String, Request<TransactionContext, Void>> toDeleteRequest) {
		boolean changed = false;
		if (currentComponents == null) {
			return changed;
		}
		
		
		// collect new/changed/deleted components and process them
		final Map<String, T> previousComponentsById = Maps.uniqueIndex(previousComponents, idProvider);
		final Map<String, U> currentComponentsById = Maps.uniqueIndex(currentComponents, component -> component.getId());
		
		return Sets.union(previousComponentsById.keySet(), currentComponentsById.keySet())
			.stream()
			.map(componentId -> {
				if (!previousComponentsById.containsKey(componentId) && currentComponentsById.containsKey(componentId)) {
					// new component
					return currentComponentsById.get(componentId).toCreateRequest(concept.getId());
				} else if (previousComponentsById.containsKey(componentId) && currentComponentsById.containsKey(componentId)) {
					// changed component
					return currentComponentsById.get(componentId).toUpdateRequest();
				} else if (previousComponentsById.containsKey(componentId) && !currentComponentsById.containsKey(componentId)) {
					// deleted component
					return toDeleteRequest.apply(componentId);
				} else {
					throw new IllegalStateException("Invalid case, should not happen");
				}
			})
			.map(req -> req.execute(context))
			.filter(Boolean.class::isInstance)
			.map(Boolean.class::cast)
			.reduce(Boolean.FALSE, (r1, r2) -> r1 || r2);
	}
	
}
