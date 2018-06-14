/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.reasoner.model;

import static com.google.common.collect.Sets.newHashSet;

import java.util.List;
import java.util.Set;

import com.b2international.collections.longs.LongSet;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.Relationship;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.snomedrefset.SnomedConcreteDataTypeRefSetMember;
import com.google.common.primitives.Longs;

/**
 * Utility class for generating {@link ConceptDefinition} from a {@link Concept} instance.
 *
 */
public class ConceptDefinitionBuilder {

	public static final long MOCK_CONCEPT_ID = 1L;

	/**
	 *
	 * @param concept
	 * @param overrideId
	 * @param isGeneratedProduct
	 * @return
	 */
	public static ConceptDefinition createDefinition(final Concept concept, final boolean overrideId) {

		final boolean exhaustive = concept.isExhaustive();
		final LongSet disjointUnionIds;

		if (exhaustive) {
			throw new UnsupportedOperationException();
		} else {
			disjointUnionIds = null;
		}

		final Set<ConcreteDomainDefinition> conceptDomainDefinitions = newHashSet();
		final List<SnomedConcreteDataTypeRefSetMember> conceptDomainMembers = concept.getConcreteDomainRefSetMembers();
		collectConcreteDomainDefinitions(conceptDomainDefinitions, conceptDomainMembers);

		final boolean primitive = concept.isPrimitive();
		final long conceptId = overrideId ? MOCK_CONCEPT_ID : Long.parseLong(concept.getId());
		final ConceptDefinition result = new ConceptDefinition(conceptDomainDefinitions, conceptId, primitive, disjointUnionIds);

		// XXX: role inclusion is not handled

		for (final Relationship relationship : concept.getOutboundRelationships()) {

			if (!relationship.isActive()) {
				continue;
			}

			if (!isDefining(relationship)) {
				continue;
			}

			final Set<ConcreteDomainDefinition> relationshipDomainDefinitions = newHashSet();
			final List<SnomedConcreteDataTypeRefSetMember> relationshipDomainMembers = relationship.getConcreteDomainRefSetMembers();
			collectConcreteDomainDefinitions(relationshipDomainDefinitions, relationshipDomainMembers);

			final long typeId = Long.parseLong(relationship.getType().getId());
			final long destinationId = Long.parseLong(relationship.getDestination().getId());

			if (LongConcepts.IS_A_ID == typeId) {
				result.addIsaDefinition(new RelationshipDefinition(destinationId));
				continue;
			}

			final RelationshipDefinition relationshipDefinition = new RelationshipDefinition(relationshipDomainDefinitions, typeId,
					destinationId, relationship.isDestinationNegated(),
					Concepts.UNIVERSAL_RESTRICTION_MODIFIER.equals(relationship.getModifier().getId()));

			if (Longs.contains(LongConcepts.NEVER_GROUPED_ROLE_IDS, typeId) && 0 == relationship.getGroup()) {
				result.addNeverGroupedDefinition(relationshipDefinition, (byte) relationship.getGroup(), (byte) relationship.getUnionGroup());
			} else {
				result.addGroupDefinition(relationshipDefinition, (byte) relationship.getGroup(), (byte) relationship.getUnionGroup());
			}
		}

		return result;
	}

	private static boolean isDefining(final Relationship relationship) {
		return isDefining(relationship.getCharacteristicType().getId());
	}

	private static boolean isDefining(final String characteristicTypeId) {
		return Concepts.DEFINING_CHARACTERISTIC_TYPES.contains(characteristicTypeId);
	}

	private static void collectConcreteDomainDefinitions(final Set<ConcreteDomainDefinition> conceptDomainDefinitions,
			final List<SnomedConcreteDataTypeRefSetMember> concreteDomainRefSetMembers) {

		for (final SnomedConcreteDataTypeRefSetMember member : concreteDomainRefSetMembers) {

			if (!member.isActive()) {
				continue;
			}

			if (!isDefining(member.getCharacteristicTypeId())) {
				continue;
			}

			// XXX: operator is not considered
			conceptDomainDefinitions.add(new ConcreteDomainDefinition(member.getLabel(), null == member.getUomComponentId() ? -1L : Long
					.parseLong(member.getUomComponentId()), member.getSerializedValue(),
					ConcreteDomainDefinition.getOWL2Datatype(member.getDataType())));
		}
	}

	private ConceptDefinitionBuilder() {
		// Prevent instantiation
	}
}