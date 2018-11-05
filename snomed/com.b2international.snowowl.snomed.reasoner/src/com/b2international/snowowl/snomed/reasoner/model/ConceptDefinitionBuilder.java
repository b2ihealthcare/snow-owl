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
package com.b2international.snowowl.snomed.reasoner.model;

import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.google.common.primitives.Longs;

/**
 * Utility class for generating {@link ConceptDefinition} from a {@link Concept} instance.
 */
public class ConceptDefinitionBuilder {

	public static final long MOCK_CONCEPT_ID = 1L;

	/**
	 * @param concept
	 * @param overrideId
	 */
	public static ConceptDefinition createDefinition(final Concept concept, final boolean overrideId) {

		/*
		 * XXX: Role inclusion and disjoint unions are not supported for unpersisted
		 * concepts; the former is not checked as a pre-condition because the
		 * concept is not yet part of the hierarchy.
		 */
		if (concept.isExhaustive()) {
			throw new UnsupportedOperationException("Concept definition can not be created for unpersisted exhaustive concepts.");
		}

		final boolean primitive = concept.isPrimitive();
		final long conceptId = overrideId ? MOCK_CONCEPT_ID : Long.parseLong(concept.getId());
		final ConceptDefinition definition = new ConceptDefinition(conceptId, primitive);

		concept.getConcreteDomainRefSetMembers()
			.stream()
			.filter(m -> m.isActive())
			.filter(m -> Concepts.STATED_RELATIONSHIP.equals(m.getCharacteristicTypeId()))
			.forEachOrdered(m -> {
				final ConcreteDomainDefinition cdDefinition = new ConcreteDomainDefinition(m.getTypeId(), m.getDataType(), m.getSerializedValue());
	
				// CD members are always "never-grouped" in group 0, but never part of any union group
				if (m.getGroup() == 0) {
					definition.addNeverGroupedDefinition(cdDefinition, m.getGroup(), 0);
				} else {
					definition.addGroupDefinition(cdDefinition, m.getGroup(), 0);
				}
			});

		concept.getOutboundRelationships()
			.stream()
			.filter(r -> r.isActive())
			.filter(r -> Concepts.STATED_RELATIONSHIP.equals(r.getCharacteristicType().getId()))
			.forEachOrdered(r -> {
				final long typeId = Long.parseLong(r.getType().getId());
				final long destinationId = Long.parseLong(r.getDestination().getId());
				final boolean universal = Concepts.UNIVERSAL_RESTRICTION_MODIFIER.equals(r.getModifier().getId());
	
				if (LongConcepts.IS_A_ID == typeId) {
					definition.addClassParentId(destinationId);
				} else {
					final RelationshipDefinition relationshipDefinition = new RelationshipDefinition(typeId,
							destinationId, 
							r.isDestinationNegated(),
							universal);
	
					if (Longs.contains(LongConcepts.NEVER_GROUPED_ROLE_IDS, typeId) && r.getGroup() == 0) {
						definition.addNeverGroupedDefinition(relationshipDefinition, r.getGroup(), r.getUnionGroup());
					} else {
						definition.addGroupDefinition(relationshipDefinition, r.getGroup(), r.getUnionGroup());
					}
				}
			});

		return definition;
	}

	private ConceptDefinitionBuilder() {
		// Prevent instantiation
	}
}
