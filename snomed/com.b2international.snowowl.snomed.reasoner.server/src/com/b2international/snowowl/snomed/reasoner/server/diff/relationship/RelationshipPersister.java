/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.reasoner.server.diff.relationship;

import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.Relationship;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.SnomedFactory;
import com.b2international.snowowl.snomed.datastore.SnomedEditingContext;
import com.b2international.snowowl.snomed.datastore.StatementFragment;
import com.b2international.snowowl.snomed.datastore.model.SnomedModelExtensions;
import com.b2international.snowowl.snomed.reasoner.server.NamespaceAndModuleAssigner;
import com.b2international.snowowl.snomed.snomedrefset.SnomedConcreteDataTypeRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedConcreteDataTypeRefSetMember;

/**
 * Applies changes related to relationships using the specified SNOMED CT editing context.
 */
public class RelationshipPersister {

	private final Concept inferredRelationshipConcept;
	private final Concept existentialRelationshipConcept;
	private final Concept universalRelationshipConcept;
	
	private final SnomedEditingContext context;
	private final NamespaceAndModuleAssigner namespaceAndModuleAssigner;
	
	public RelationshipPersister(final SnomedEditingContext context, final NamespaceAndModuleAssigner namespaceAndModuleAssigner) {
		this.context = context;
		this.namespaceAndModuleAssigner = namespaceAndModuleAssigner;

		this.inferredRelationshipConcept = context.lookup(Concepts.INFERRED_RELATIONSHIP, Concept.class);
		this.existentialRelationshipConcept = context.lookup(Concepts.EXISTENTIAL_RESTRICTION_MODIFIER, Concept.class);
		this.universalRelationshipConcept = context.lookup(Concepts.UNIVERSAL_RESTRICTION_MODIFIER, Concept.class);
	}
	
	public void handleRemovedSubject(final String conceptId, final StatementFragment removedEntry) {
		final Relationship relationship = (Relationship) context.lookup(removedEntry.getStorageKey());
		SnomedModelExtensions.removeOrDeactivate(relationship);
	}
	
	public void handleAddedSubject(final String conceptId, final StatementFragment addedEntry) {
		final Concept sourceConcept = context.lookup(conceptId, Concept.class);
		final Concept typeConcept = context.lookup(Long.toString(addedEntry.getTypeId()), Concept.class);
		final Concept destinationConcept = context.lookup(Long.toString(addedEntry.getDestinationId()), Concept.class);
		
		final String relationshipId = namespaceAndModuleAssigner.getRelationshipId(conceptId);
		final Concept moduleConcept = namespaceAndModuleAssigner.getRelationshipModule(conceptId);
		
		final Relationship newRel = SnomedFactory.eINSTANCE.createRelationship();
		newRel.setId(relationshipId);
		newRel.setType(typeConcept);
		newRel.setActive(true);
		newRel.setCharacteristicType(inferredRelationshipConcept);
		newRel.setSource(sourceConcept);
		newRel.setDestination(destinationConcept);
		newRel.setDestinationNegated(addedEntry.isDestinationNegated());
		newRel.setGroup(addedEntry.getGroup());
		newRel.setUnionGroup(addedEntry.getUnionGroup());
		newRel.setModifier(addedEntry.isUniversal() ? universalRelationshipConcept : existentialRelationshipConcept);
		newRel.setModule(moduleConcept);
		
		if (addedEntry.getStatementId() != -1L) {
			final Relationship originalRel = context.lookup(Long.toString(addedEntry.getStatementId()), Relationship.class);
			
			for (final SnomedConcreteDataTypeRefSetMember originalMember : originalRel.getConcreteDomainRefSetMembers()) {

				if (!Concepts.STATED_RELATIONSHIP.equals(originalMember.getCharacteristicTypeId())) {
					continue;
				}
				
				final SnomedConcreteDataTypeRefSet concreteDataTypeRefSet = (SnomedConcreteDataTypeRefSet) originalMember.getRefSet();
				final SnomedConcreteDataTypeRefSetMember refSetMember = context.getRefSetEditingContext().createConcreteDataTypeRefSetMember(
						newRel.getId(),
						originalMember.getUomComponentId(),
						originalMember.getOperatorComponentId(),
						originalMember.getSerializedValue(), 
						Concepts.INFERRED_RELATIONSHIP, 
						originalMember.getLabel(), 
						moduleConcept.getId(), 
						concreteDataTypeRefSet);
				
				newRel.getConcreteDomainRefSetMembers().add(refSetMember);
			}
		}
	}
}
