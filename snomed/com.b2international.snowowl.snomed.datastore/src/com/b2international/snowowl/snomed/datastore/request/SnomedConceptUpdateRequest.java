/*
 * Copyright 2011-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.emf.ecore.EObject;

import com.b2international.commons.CompareUtils;
import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.index.Hits;
import com.b2international.index.query.Query;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.domain.IComponent;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.Acceptability;
import com.b2international.snowowl.snomed.core.domain.SnomedComponent;
import com.b2international.snowowl.snomed.core.domain.SnomedDescription;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationship;
import com.b2international.snowowl.snomed.core.domain.SubclassDefinitionStatus;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSet;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMembers;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedComponentDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.google.common.base.Strings;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * @since 4.5
 */
public final class SnomedConceptUpdateRequest extends SnomedComponentUpdateRequest {

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

	private String definitionStatusId;
	private SubclassDefinitionStatus subclassDefinitionStatus;
	private List<SnomedDescription> descriptions;
	private List<SnomedRelationship> relationships;
	private List<SnomedReferenceSetMember> members;

	private SnomedReferenceSet refSet;
	
	SnomedConceptUpdateRequest(String componentId) {
		super(componentId);
	}
	
	void setDefinitionStatusId(String definitionStatusId) {
		this.definitionStatusId = definitionStatusId;
	}
	
	void setSubclassDefinitionStatus(SubclassDefinitionStatus subclassDefinitionStatus) {
		this.subclassDefinitionStatus = subclassDefinitionStatus;
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
	
	void setRefSet(SnomedReferenceSet refSet) {
		this.refSet = refSet;
	}
	
	@Override
	public Boolean execute(TransactionContext context) {
		final SnomedConceptDocument concept = context.lookup(getComponentId(), SnomedConceptDocument.class);
		final SnomedConceptDocument.Builder updatedConcept = SnomedConceptDocument.builder(concept);

		boolean changed = false;
		changed |= updateModule(context, concept, updatedConcept);
		changed |= updateDefinitionStatus(context, concept, updatedConcept);
		changed |= updateSubclassDefinitionStatus(context, concept, updatedConcept);
		
		if (descriptions != null) {
			updateComponents(
				context, 
				concept.getId(), 
				getDescriptionIds(context, concept.getId()),
				descriptions, 
				id -> SnomedRequests.prepareDeleteDescription(id).build()
			);
		}
		
		if (relationships != null) {
			updateComponents(
				context, 
				concept.getId(), 
				getRelationshipIds(context, concept.getId()), 
				relationships, 
				id -> SnomedRequests.prepareDeleteRelationship(id).build());
		}
		
		if (members != null) {
			updateComponents(
				context, 
				concept.getId(), 
				getPreviousMemberIds(concept.getId(), context), 
				getUpdateableMembers(members).toSet(), 
				id -> SnomedRequests.prepareDeleteMember(id).build()
			);
		}
		
		changed |= processInactivation(context, concept, updatedConcept);

		if (changed && concept.getEffectiveTime() != EffectiveTimes.UNSET_EFFECTIVE_TIME) {
			updatedConcept.effectiveTime(EffectiveTimes.UNSET_EFFECTIVE_TIME);
		}
	
		// XXX the following updates won't and shouldn't trigger effective time unset or restoration logic
		changed |= updateRefSet(context, concept, updatedConcept);
		
		if (changed) {
			context.update(concept, updatedConcept.build());
		}
		
		return changed;
	}
	
	@Override
	protected <B extends SnomedComponentDocument.Builder<B, T>, T extends SnomedComponentDocument> void postInactivateComponent(TransactionContext context, T component, B updatedComponent) {
		// automatically set concept to primitive when inactivating it, unless the user decided to use a different definition status ID 
		// https://confluence.ihtsdotools.org/display/DOCEXTPG/5.4.2.3+Inactivate+Concept+in+an+Extension
		((SnomedConceptDocument.Builder) updatedComponent).primitive(definitionStatusId == null ? true : Concepts.PRIMITIVE.equals(definitionStatusId));
	}
	
	@Override
	protected String getInactivationIndicatorRefSetId() {
		return Concepts.REFSET_CONCEPT_INACTIVITY_INDICATOR;
	}

	private boolean updateRefSet(TransactionContext context, SnomedConceptDocument concept, SnomedConceptDocument.Builder updatedConcept) {
		final boolean force = refSet == SnomedReferenceSet.FORCE_DELETE;
		if (refSet == SnomedReferenceSet.DELETE || force) {
			for (Hits<SnomedRefSetMemberIndexEntry> hits : context.service(RevisionSearcher.class).scroll(Query
					.select(SnomedRefSetMemberIndexEntry.class)
					.where(SnomedRefSetMemberIndexEntry.Expressions.referenceSetId(getComponentId()))
					.limit(10_000)
					.build()))  {
				for (SnomedRefSetMemberIndexEntry member : hits) {
					context.delete(member, force);
				}
			}
			
			updatedConcept.clearRefSet();
			return true;
		}
		return false;
	}

	private Set<String> getDescriptionIds(BranchContext context, String conceptId) {
		return SnomedRequests.prepareSearchDescription()
				.all()
				.filterByConcept(conceptId)
				.setFields(SnomedDocument.Fields.ID)
				.build()
				.execute(context)
				.getItems()
				.stream()
				.map(IComponent::getId)
				.collect(Collectors.toSet());
	}
	
	private Set<String> getRelationshipIds(BranchContext context, String conceptId) {
		return SnomedRequests.prepareSearchRelationship()
				.all()
				.filterBySource(conceptId)
				.setFields(SnomedDocument.Fields.ID)
				.build()
				.execute(context)
				.getItems()
				.stream()
				.map(IComponent::getId)
				.collect(Collectors.toSet());
	}

	private Set<String> getPreviousMemberIds(final String conceptId, TransactionContext context) {
		SnomedReferenceSetMembers members = SnomedRequests.prepareSearchMember()
			.all()
			.filterByReferencedComponent(conceptId)
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

	private boolean updateDefinitionStatus(final TransactionContext context, final SnomedConceptDocument original, final SnomedConceptDocument.Builder concept) {
		final Set<String> newOwlAxiomExpressions = Optional.ofNullable(members)
				.map(Collection::stream)
				.orElseGet(Stream::empty)
					.filter(member -> SnomedRefSetType.OWL_AXIOM == member.type() || Concepts.REFSET_OWL_AXIOM.equals(member.getReferenceSetId()))
					.map(member -> (String) member.getProperties().get(SnomedRf2Headers.FIELD_OWL_EXPRESSION))
					.collect(Collectors.toSet());
		
		final String newDefinitionStatusId;
		if (!newOwlAxiomExpressions.isEmpty()) {
			// Calculate the definition status
			newDefinitionStatusId = SnomedOWLAxiomHelper.getDefinitionStatusFromExpressions(newOwlAxiomExpressions);
		} else {
			if (definitionStatusId == null) return false;
			
			final String incomingDefinitionStatusId = definitionStatusId;
			newDefinitionStatusId = incomingDefinitionStatusId;
		}
		
		final String existingDefinitionStatusId = original.isPrimitive() ? Concepts.PRIMITIVE : Concepts.FULLY_DEFINED;
		if (!newDefinitionStatusId.equals(existingDefinitionStatusId)) {
			context.lookup(newDefinitionStatusId, SnomedConceptDocument.class);
			concept.primitive(Concepts.PRIMITIVE.equals(newDefinitionStatusId));
			return true;
		} else {
			return false;
		}
		
	}

	private boolean updateSubclassDefinitionStatus(final TransactionContext context, final SnomedConceptDocument original, final SnomedConceptDocument.Builder concept) {
		if (null == subclassDefinitionStatus) {
			return false;
		}

		final boolean currentExhaustive = original.isExhaustive();
		final boolean newExhaustive = subclassDefinitionStatus.isExhaustive();
		if (currentExhaustive != newExhaustive) {
			concept.exhaustive(newExhaustive);
			return true;
		} else {
			return false;
		}
	}

	private <T extends EObject, U extends SnomedComponent> boolean updateComponents(final TransactionContext context, 
			final String conceptId, 
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
					return currentComponentsById.get(componentId).toCreateRequest(conceptId);
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
		
		if (getInactivationProperties() != null && getInactivationProperties().getInactivationIndicatorId() != null) {
			ids.add(getInactivationProperties().getInactivationIndicatorId());
		}
		if (getInactivationProperties() != null && !CompareUtils.isEmpty(getInactivationProperties().getAssociationTargets())) {
			getInactivationProperties().getAssociationTargets().forEach(associationTarget -> {
				ids.add(associationTarget.getReferenceSetId());
				ids.add(associationTarget.getTargetComponentId());
			});
		}
		if (!CompareUtils.isEmpty(descriptions)) {
			descriptions.forEach(description -> {
				ids.add(description.getModuleId());
				ids.add(description.getTypeId());
				ids.addAll(description.getAcceptabilityMap().keySet());
				ids.addAll(description.getAcceptabilityMap().values().stream().map(Acceptability::getConceptId).collect(Collectors.toSet()));
				ids.add(description.getCaseSignificanceId());
			});
		}
		if (!CompareUtils.isEmpty(relationships)) {
			relationships.forEach(relationship -> {
				ids.add(relationship.getModuleId());
				ids.add(relationship.getTypeId());
				ids.add(relationship.getDestinationId());
				ids.add(relationship.getCharacteristicTypeId());
				ids.add(relationship.getModifierId());
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
