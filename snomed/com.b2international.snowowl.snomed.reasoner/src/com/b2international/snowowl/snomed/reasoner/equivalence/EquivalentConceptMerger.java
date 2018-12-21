/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.reasoner.equivalence;

import static com.google.common.collect.Maps.newHashMap;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.bulk.BulkRequestBuilder;
import com.b2international.snowowl.core.exceptions.ConflictException;
import com.b2international.snowowl.datastore.request.DeleteRequestBuilder;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.CharacteristicType;
import com.b2international.snowowl.snomed.core.domain.InactivationIndicator;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationships;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMembers;
import com.b2international.snowowl.snomed.datastore.id.SnomedIdentifiers;
import com.b2international.snowowl.snomed.datastore.request.SnomedConceptUpdateRequestBuilder;
import com.b2international.snowowl.snomed.datastore.request.SnomedRefSetMemberCreateRequestBuilder;
import com.b2international.snowowl.snomed.datastore.request.SnomedRelationshipCreateRequestBuilder;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;

/**
 *
 */
public class EquivalentConceptMerger {

	private static final Set<CharacteristicType> INBOUND_CHARACTERISTIC_TYPES = ImmutableSet.of(
			CharacteristicType.STATED_RELATIONSHIP,
			CharacteristicType.ADDITIONAL_RELATIONSHIP);

	private static final Set<CharacteristicType> OUTBOUND_CHARACTERISTIC_TYPES = ImmutableSet.of(
			CharacteristicType.ADDITIONAL_RELATIONSHIP);
	
	private final BulkRequestBuilder<TransactionContext> bulkRequestBuilder;
	private final Multimap<SnomedConcept, SnomedConcept> equivalentConcepts;

	public EquivalentConceptMerger(
			final BulkRequestBuilder<TransactionContext> bulkRequestBuilder, 
			final Multimap<SnomedConcept, SnomedConcept> equivalentConcepts) {
		
		this.bulkRequestBuilder = bulkRequestBuilder;
		this.equivalentConcepts = equivalentConcepts;
	}

	public void merge() {
		if (equivalentConcepts.isEmpty()) {
			return;
		}
		
		try {
			
			final Map<String, String> suggestedReplacements = newHashMap();
			equivalentConcepts.entries().forEach(e -> {
				final String idToRemove = e.getValue().getId();
				final String idToKeep = e.getKey().getId();
				final String existingIdToKeep = suggestedReplacements.put(idToRemove, idToKeep);
				if (existingIdToKeep != null) {
					throw new IllegalStateException(String.format("Concept '%s' should be replaced by two other concepts: '%s' and '%s'.", idToRemove, idToKeep, existingIdToKeep));
				}
			});
			
			for (final SnomedConcept conceptToKeep : equivalentConcepts.keySet()) {
				final Collection<SnomedConcept> conceptsToRemove = equivalentConcepts.get(conceptToKeep);
				mergeEquivalentConcept(conceptToKeep, conceptsToRemove, suggestedReplacements);
				removeOrDeactivate(conceptsToRemove);
			}
			
		} catch (final ConflictException e) {
			throw new SnowowlRuntimeException(e);
		}
	}

	private void mergeEquivalentConcept(final SnomedConcept conceptToKeep, 
			final Collection<SnomedConcept> conceptsToRemove, 
			final Map<String, String> suggestedReplacements) {
		
		for (final SnomedConcept conceptToRemove : conceptsToRemove) {
			mergeInboundRelationships(conceptToKeep, conceptToRemove, suggestedReplacements);
			mergeOutboundRelationships(conceptToKeep, conceptToRemove, suggestedReplacements);
			mergeRefSetMembers(conceptToKeep, conceptToRemove);
		}
	}

	private void mergeInboundRelationships(final SnomedConcept conceptToKeep, 
			 final SnomedConcept conceptToRemove,
			 final Map<String, String> suggestedReplacements) {

		 final SnomedRelationships currentRelationships = conceptToKeep.getInboundRelationships();
		 final SnomedRelationships candidateRelationships = conceptToRemove.getInboundRelationships();
		 
		 candidateRelationships.forEach(candidate -> {
			 if (!INBOUND_CHARACTERISTIC_TYPES.contains(candidate.getCharacteristicType())) {
				 return;
			 }
			 
			 final String candidateSourceId = candidate.getSourceId();
			 final String candidateReplacementId = suggestedReplacements.getOrDefault(candidateSourceId, candidateSourceId);
			 
			 // Check if the re-mapped source would be the same concept, creating a loop
			 if (candidateReplacementId.equals(conceptToKeep.getId())) {
				 return;
			 }
			 
			final boolean alreadyExists = currentRelationships.stream().anyMatch(current -> current.getSourceId().equals(candidateReplacementId)
				&& current.isDestinationNegated() == candidate.isDestinationNegated()
				&& current.getTypeId().equals(candidate.getTypeId())
				&& current.getGroup().equals(candidate.getGroup())
				&& current.getUnionGroup().equals(candidate.getUnionGroup())
				&& current.getModifier().equals(candidate.getModifier()));
			 
			 if (!alreadyExists) {
				 final String namespace = Strings.emptyToNull(SnomedIdentifiers.getNamespace(candidate.getId()));
				 final SnomedRelationshipCreateRequestBuilder createRequestBuilder = SnomedRequests.prepareNewRelationship()
						 .setActive(true)
						 .setCharacteristicType(candidate.getCharacteristicType())
						 .setDestinationId(conceptToKeep.getId()) // point to merged concept
						 .setDestinationNegated(candidate.isDestinationNegated())
						 .setGroup(candidate.getGroup())
						 .setIdFromNamespace(namespace) // use same namespace as the original relationship
						 .setModifier(candidate.getModifier())
						 .setModuleId(candidate.getModuleId())
						 .setSourceId(candidateReplacementId) // use remapped source
						 .setTypeId(candidate.getTypeId())
						 .setUnionGroup(candidate.getUnionGroup());

				 bulkRequestBuilder.add(createRequestBuilder);
			 }
		 });
	}

	private void mergeOutboundRelationships(final SnomedConcept conceptToKeep, 
			final SnomedConcept conceptToRemove,
			final Map<String, String> suggestedReplacements) {
		
		final SnomedRelationships currentRelationships = conceptToKeep.getRelationships();
		final SnomedRelationships candidateRelationships = conceptToRemove.getRelationships();
		
		candidateRelationships.forEach(candidate -> {
			if (!OUTBOUND_CHARACTERISTIC_TYPES.contains(candidate.getCharacteristicType())) {
				return;
			}
			
			final String candidateDestinationId = candidate.getDestinationId();
			final String candidateReplacementId = suggestedReplacements.getOrDefault(candidateDestinationId, candidateDestinationId);
			
			// Check if the re-mapped destination would be the same concept, creating a loop
			if (candidateReplacementId.equals(conceptToKeep.getId())) {
				return;
			}
			
			final boolean alreadyExists = currentRelationships.stream().anyMatch(current -> current.getDestinationId().equals(candidateReplacementId)
					&& current.isDestinationNegated() == candidate.isDestinationNegated()
					&& current.getTypeId().equals(candidate.getTypeId())
					&& current.getGroup().equals(candidate.getGroup())
					&& current.getUnionGroup().equals(candidate.getUnionGroup())
					&& current.getModifier().equals(candidate.getModifier()));
			
			if (!alreadyExists) {
				final String namespace = Strings.emptyToNull(SnomedIdentifiers.getNamespace(candidate.getId()));
				final SnomedRelationshipCreateRequestBuilder createRequestBuilder = SnomedRequests.prepareNewRelationship()
						.setActive(true)
						.setCharacteristicType(candidate.getCharacteristicType())
						.setDestinationId(candidateReplacementId) // use remapped destination
						.setDestinationNegated(candidate.isDestinationNegated())
						.setGroup(candidate.getGroup())
						.setIdFromNamespace(namespace) // use same namespace as the original relationship
						.setModifier(candidate.getModifier())
						.setModuleId(candidate.getModuleId())
						.setSourceId(conceptToKeep.getId()) // point to merged concept
						.setTypeId(candidate.getTypeId())
						.setUnionGroup(candidate.getUnionGroup());
				
				bulkRequestBuilder.add(createRequestBuilder);
			}
		});
	}
	
	private void mergeRefSetMembers(final SnomedConcept conceptToKeep, final SnomedConcept conceptToRemove) {
		final SnomedReferenceSetMembers currentMembers = conceptToKeep.getMembers();
		final SnomedReferenceSetMembers candidateMembers = conceptToRemove.getMembers();
		
		candidateMembers.forEach(candidate -> {
			if (SnomedRefSetType.CONCRETE_DATA_TYPE.equals(candidate.type())) {
				final String characteristicTypeId = (String) candidate.getProperties().get(SnomedRf2Headers.FIELD_CHARACTERISTIC_TYPE_ID);
				final CharacteristicType characteristicType = CharacteristicType.getByConceptId(characteristicTypeId);
				if (!OUTBOUND_CHARACTERISTIC_TYPES.contains(characteristicType)) {
					return;
				}
			}
			
			// FIXME: support re-mapping of extra properties which contain a concept ID?
			
			final boolean alreadyExists = currentMembers.stream().anyMatch(current -> current.type().equals(candidate.type())
					&& current.getReferenceSetId().equals(candidate.getReferenceSetId())
					&& current.getProperties().equals(candidate.getProperties()));
			
			if (!alreadyExists) {
				final SnomedRefSetMemberCreateRequestBuilder createRequestBuilder = SnomedRequests.prepareNewMember()
						.setActive(true)
						.setModuleId(candidate.getModuleId())
						.setProperties(newHashMap(candidate.getProperties()))
						.setReferencedComponentId(conceptToKeep.getId()) // point to merged concept
						.setReferenceSetId(candidate.getReferenceSetId());
				
				bulkRequestBuilder.add(createRequestBuilder);
			}
		});
	}

	private void removeOrDeactivate(final Collection<SnomedConcept> conceptsToRemove) {
		conceptsToRemove.stream()
			.map(c -> c.isReleased() ? createInactivationRequest(c) : createDeleteRequest(c))
			.forEachOrdered(bulkRequestBuilder::add);
	}

	private SnomedConceptUpdateRequestBuilder createInactivationRequest(final SnomedConcept concept) {
		return SnomedRequests.prepareUpdateConcept(concept.getId())
				.setActive(false)
				.setInactivationIndicator(InactivationIndicator.RETIRED);
	}

	private DeleteRequestBuilder createDeleteRequest(final SnomedConcept concept) {
		return SnomedRequests.prepareDeleteConcept(concept.getId());
	}
}
