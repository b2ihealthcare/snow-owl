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

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.CompareUtils;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.b2international.snowowl.core.exceptions.ComponentStatusConflictException;
import com.b2international.snowowl.snomed.Component;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.Description;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.Acceptability;
import com.b2international.snowowl.snomed.core.domain.AssociationType;
import com.b2international.snowowl.snomed.core.domain.DefinitionStatus;
import com.b2international.snowowl.snomed.core.domain.DescriptionInactivationIndicator;
import com.b2international.snowowl.snomed.core.domain.InactivationIndicator;
import com.b2international.snowowl.snomed.core.domain.SnomedComponent;
import com.b2international.snowowl.snomed.core.domain.SnomedDescription;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationship;
import com.b2international.snowowl.snomed.core.domain.SubclassDefinitionStatus;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMembers;
import com.b2international.snowowl.snomed.datastore.SnomedEditingContext;
import com.b2international.snowowl.snomed.datastore.SnomedInactivationPlan;
import com.b2international.snowowl.snomed.datastore.SnomedInactivationPlan.InactivationReason;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.google.common.base.Strings;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

/**
 * @since 4.5
 */
public final class SnomedConceptUpdateRequest extends SnomedComponentUpdateRequest {

	private static final Logger LOGGER = LoggerFactory.getLogger(SnomedConceptUpdateRequest.class);

	private static final Set<String> FILTERED_REFSET_IDS = ImmutableSet.of(Concepts.REFSET_CONCEPT_INACTIVITY_INDICATOR,
			Concepts.REFSET_ALTERNATIVE_ASSOCIATION,
			Concepts.REFSET_MOVED_FROM_ASSOCIATION,
			Concepts.REFSET_MOVED_TO_ASSOCIATION,
			Concepts.REFSET_POSSIBLY_EQUIVALENT_TO_ASSOCIATION,
			Concepts.REFSET_REFERS_TO_ASSOCIATION,
			Concepts.REFSET_REPLACED_BY_ASSOCIATION,
			Concepts.REFSET_SAME_AS_ASSOCIATION,
			Concepts.REFSET_SIMILAR_TO_ASSOCIATION,
			Concepts.REFSET_WAS_A_ASSOCIATION);

	private DefinitionStatus definitionStatus;
	private SubclassDefinitionStatus subclassDefinitionStatus;
	private InactivationIndicator inactivationIndicator;
	private Multimap<AssociationType, String> associationTargets;
	private List<SnomedDescription> descriptions;
	private List<SnomedRelationship> relationships;
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
	
	void setRelationships(List<SnomedRelationship> relationships) {
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
		
		if (descriptions != null) {
			updateComponents(context, concept, 
					getComponentIds(concept.getDescriptions()), descriptions, 
					id -> SnomedRequests.prepareDeleteDescription(id).build());
		}
		
		if (relationships != null) {
			updateComponents(context, concept, 
					getComponentIds(concept.getOutboundRelationships()), relationships, 
					id -> SnomedRequests.prepareDeleteRelationship(id).build());
		}
		
		if (members != null) {
			updateComponents(context, concept, 
					getPreviousMemberIds(concept, context), getUpdateableMembers(members).toSet(),
					id -> SnomedRequests.prepareDeleteMember(id).build());
		}
		
		changed |= processInactivation(context, concept);

		if (changed && concept.isSetEffectiveTime()) {
			concept.unsetEffectiveTime();
		}

		return changed;
	}

	private Set<String> getComponentIds(Iterable<? extends Component> components) {
		return FluentIterable.from(components).transform(c -> c.getId()).toSet();
	}

	private Set<String> getPreviousMemberIds(Concept concept, TransactionContext context) {
		SnomedReferenceSetMembers members = SnomedRequests.prepareSearchMember()
			.all()
			.filterByReferencedComponent(concept.getId())
			.build()
			.execute(context);
		
		return getUpdateableMembers(members)
				.transform(m -> m.getId())
				.toSet();
	}

	private FluentIterable<SnomedReferenceSetMember> getUpdateableMembers(Iterable<SnomedReferenceSetMember> members) {
		return FluentIterable.from(members)
				.filter(m -> !FILTERED_REFSET_IDS.contains(m.getReferenceSetId()));
	}

	private boolean updateDefinitionStatus(final TransactionContext context, final Concept concept) {
		final Set<String> newOwlAxiomExpressions = Optional.ofNullable(members)
				.map(Collection::stream)
				.orElseGet(Stream::empty)
					.filter(member -> SnomedRefSetType.OWL_AXIOM == member.type() || Concepts.REFSET_OWL_AXIOM.equals(member.getReferenceSetId()))
					.map(member -> (String) member.getProperties().get(SnomedRf2Headers.FIELD_OWL_EXPRESSION))
					.collect(Collectors.toSet());
		
		final String newDefinitionStatusId;
		if (!newOwlAxiomExpressions.isEmpty()) {
			// Calculate the definition status
			newDefinitionStatusId = SnomedOWLAxiomHelper.getDefinitionStatusFromExpressions(newOwlAxiomExpressions).getConceptId();
		} else {
			if (definitionStatus == null) return false;
			
			final String incomingDefinitionStatusId = definitionStatus.getConceptId();
			newDefinitionStatusId = incomingDefinitionStatusId;
		}
		
		final String existingDefinitionStatusId = concept.getDefinitionStatus().getId();
		if (!newDefinitionStatusId.equals(existingDefinitionStatusId)) {
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
			
			if (inactivationIndicator != null && InactivationIndicator.RETIRED != inactivationIndicator) {
				throw new BadRequestException("Cannot reactivate concept and retain or change its inactivation indicator at the same time.");
			}
			
			if (!CompareUtils.isEmpty(associationTargets)) {
				throw new BadRequestException("Cannot reactivate concept and retain or change its historical association targets at the same time.");
			}
			
			reactivateConcept(context, concept);
			updateInactivationIndicator(context, newIndicator);
			updateAssociationTargets(context, newAssociationTargets);
			return true;
			
		} else if (currentStatus == newStatus) {
			
			// Same status, allow indicator and/or association targets to be updated if required
			// (using original values that can be null)
			
			updateInactivationIndicator(context, inactivationIndicator);
			updateAssociationTargets(context, associationTargets);
			return false;
			
		} else {
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
			final Set<String> previousComponentIds,
			final Iterable<U> currentComponents, 
			final Function<String, Request<TransactionContext, ?>> toDeleteRequest) {

		// pre process all incoming components
		currentComponents.forEach(component -> {
			// all incoming components should define their ID in order to be processed
			if (Strings.isNullOrEmpty(component.getId())) {
				throw new BadRequestException("New components require their id to be set.");
			}
			// all components should have their module ID set
			if (Strings.isNullOrEmpty(component.getModuleId())) {
				throw new BadRequestException("It is required to specify the moduleId for the components.");
			}
		});
		
		// collect new/changed/deleted components and process them
		final Map<String, U> currentComponentsById = Maps.uniqueIndex(currentComponents, component -> component.getId());
		
		return Sets.union(previousComponentIds, currentComponentsById.keySet())
			.stream()
			.map(componentId -> {
				if (!previousComponentIds.contains(componentId) && currentComponentsById.containsKey(componentId)) {
					// new component
					return currentComponentsById.get(componentId).toCreateRequest(concept.getId());
				} else if (previousComponentIds.contains(componentId) && currentComponentsById.containsKey(componentId)) {
					// changed component
					return currentComponentsById.get(componentId).toUpdateRequest();
				} else if (previousComponentIds.contains(componentId) && !currentComponentsById.containsKey(componentId)) {
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
	
	@Override
	public Set<String> getRequiredComponentIds(TransactionContext context) {
		final Builder<String> ids = ImmutableSet.<String>builder();
		ids.add(getComponentId());
		if (getModuleId() != null) {
			ids.add(getModuleId());
		}

		ids.addAll(ImmutableList.of(Concepts.PRIMITIVE, Concepts.FULLY_DEFINED));
		
		if (inactivationIndicator != null) {
			ids.add(inactivationIndicator.getConceptId());
		}
		if (associationTargets != null && !associationTargets.isEmpty()) {
			associationTargets.entries().forEach(entry -> {
				ids.add(entry.getKey().getConceptId());
				ids.add(entry.getValue());
			});
		}
		if (!CompareUtils.isEmpty(descriptions)) {
			descriptions.forEach(description -> {
				ids.add(description.getModuleId());
				ids.add(description.getTypeId());
				ids.addAll(description.getAcceptabilityMap().keySet());
				ids.addAll(description.getAcceptabilityMap().values().stream().map(Acceptability::getConceptId).collect(Collectors.toSet()));
				ids.add(description.getCaseSignificance().getConceptId());
			});
		}
		if (!CompareUtils.isEmpty(relationships)) {
			relationships.forEach(relationship -> {
				ids.add(relationship.getModuleId());
				ids.add(relationship.getTypeId());
				ids.add(relationship.getDestinationId());
				ids.add(relationship.getCharacteristicType().getConceptId());
				ids.add(relationship.getModifier().getConceptId());
			});
		}
		if (!CompareUtils.isEmpty(members)) {
			members.forEach(member -> {
				ids.add(member.getModuleId());
				ids.add(member.getReferenceSetId());
				// TODO add specific props?
			});
		}
		return ids.build();
	}
	
}
