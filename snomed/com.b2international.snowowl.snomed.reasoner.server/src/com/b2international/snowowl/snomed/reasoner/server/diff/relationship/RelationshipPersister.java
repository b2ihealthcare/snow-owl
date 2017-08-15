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

import java.util.Map;

import org.eclipse.emf.cdo.transaction.CDOTransaction;

import com.b2international.snowowl.core.ComponentIdentifierPair;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.Relationship;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.SnomedFactory;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.datastore.SnomedConceptLookupService;
import com.b2international.snowowl.snomed.datastore.SnomedEditingContext;
import com.b2international.snowowl.snomed.datastore.SnomedRelationshipLookupService;
import com.b2international.snowowl.snomed.datastore.StatementFragment;
import com.b2international.snowowl.snomed.datastore.model.SnomedModelExtensions;
import com.b2international.snowowl.snomed.reasoner.server.NamespaceAndModuleAssigner;
import com.b2international.snowowl.snomed.snomedrefset.SnomedConcreteDataTypeRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedConcreteDataTypeRefSetMember;
import com.google.common.collect.Maps;

/**
 * Applies changes related to relationships using the specified SNOMED CT editing context.
 */
public class RelationshipPersister {

	private final Concept inferredRelationshipConcept;
	private final Concept existentialRelationshipConcept;
	private final Concept universalRelationshipConcept;
	private final SnomedConceptLookupService conceptLookupService;
	private final SnomedRelationshipLookupService relationshipLookupService;
	private final CDOTransaction transaction;
	private final Map<Long, Concept> relationshipTypeConcepts;
	
	private final SnomedEditingContext context;
	private final NamespaceAndModuleAssigner namespaceAndModuleAssigner;
	
	public RelationshipPersister(final SnomedEditingContext context, final NamespaceAndModuleAssigner namespaceAndModuleAssigner) {
		this.context = context;
		this.namespaceAndModuleAssigner = namespaceAndModuleAssigner;
		
		conceptLookupService = new SnomedConceptLookupService();
		relationshipLookupService = new SnomedRelationshipLookupService();
		
		transaction = context.getTransaction();
		
		inferredRelationshipConcept = conceptLookupService.getComponent(Concepts.INFERRED_RELATIONSHIP, transaction);
		existentialRelationshipConcept = conceptLookupService.getComponent(Concepts.EXISTENTIAL_RESTRICTION_MODIFIER, transaction);
		universalRelationshipConcept = conceptLookupService.getComponent(Concepts.UNIVERSAL_RESTRICTION_MODIFIER, transaction);
		
		relationshipTypeConcepts = Maps.newHashMap();
	}
	
	public void handleRemovedSubject(final String conceptId, final StatementFragment removedEntry) {
		final Relationship relationship = (Relationship) context.lookup(removedEntry.getStorageKey());
		SnomedModelExtensions.removeOrDeactivate(relationship);
	}
	
	public void handleAddedSubject(final String sourceConceptId, final StatementFragment addedEntry) {
		final String relationshipId = namespaceAndModuleAssigner.getRelationshipId(sourceConceptId);
		final Concept sourceConcept = conceptLookupService.getComponent(sourceConceptId, transaction);
		final Concept module = namespaceAndModuleAssigner.getRelationshipModule(sourceConceptId);
		
		final Concept typeConcept;
		final long typeId = addedEntry.getTypeId();
		
		if (relationshipTypeConcepts.containsKey(typeId)) {
			typeConcept = relationshipTypeConcepts.get(typeId);
		} else {
			typeConcept = conceptLookupService.getComponent(Long.toString(typeId), transaction);
			relationshipTypeConcepts.put(typeId, typeConcept);
		}
		
		final Concept destinationConcept = conceptLookupService.getComponent(Long.toString(addedEntry.getDestinationId()), transaction);
		
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
		newRel.setModule(module);
		
		if (addedEntry.getStatementId() != -1L) {
			final Relationship originalRel = relationshipLookupService.getComponent(Long.toString(addedEntry.getStatementId()), transaction);
			
			for (final SnomedConcreteDataTypeRefSetMember originalMember : originalRel.getConcreteDomainRefSetMembers()) {
				final SnomedConcreteDataTypeRefSet concreteDataTypeRefSet = (SnomedConcreteDataTypeRefSet) originalMember.getRefSet();
				final SnomedConcreteDataTypeRefSetMember refSetMember = context.getRefSetEditingContext().createConcreteDataTypeRefSetMember(
						ComponentIdentifierPair.create(SnomedTerminologyComponentConstants.RELATIONSHIP, newRel.getId()),
						originalMember.getUomComponentId(),
						originalMember.getOperatorComponentId(),
						originalMember.getSerializedValue(), 
						getCharacteristicTypeId(originalMember), 
						originalMember.getLabel(), 
						module.getId(), 
						concreteDataTypeRefSet);
				
				newRel.getConcreteDomainRefSetMembers().add(refSetMember);
			}
		}
	}

	private String getCharacteristicTypeId(final SnomedConcreteDataTypeRefSetMember originalMember) {
		if (Concepts.ADDITIONAL_RELATIONSHIP.equals(originalMember.getCharacteristicTypeId())) {
			return Concepts.ADDITIONAL_RELATIONSHIP;
		} else {
			return Concepts.INFERRED_RELATIONSHIP;
		}
	}
}
