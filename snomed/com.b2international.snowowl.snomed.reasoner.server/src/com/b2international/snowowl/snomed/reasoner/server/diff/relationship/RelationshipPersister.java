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

import java.util.Collection;
import java.util.Map;

import org.eclipse.emf.cdo.transaction.CDOTransaction;

import com.b2international.snowowl.core.ComponentIdentifierPair;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.Relationship;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.datastore.SnomedConceptLookupService;
import com.b2international.snowowl.snomed.datastore.SnomedEditingContext;
import com.b2international.snowowl.snomed.datastore.SnomedRelationshipLookupService;
import com.b2international.snowowl.snomed.datastore.StatementFragment;
import com.b2international.snowowl.snomed.datastore.model.SnomedModelExtensions;
import com.b2international.snowowl.snomed.reasoner.server.NamespaceAndMolduleAssigner;
import com.b2international.snowowl.snomed.reasoner.server.diff.OntologyChange.Nature;
import com.b2international.snowowl.snomed.reasoner.server.diff.OntologyChangeProcessor;
import com.b2international.snowowl.snomed.snomedrefset.SnomedConcreteDataTypeRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedConcreteDataTypeRefSetMember;
import com.google.common.base.Objects;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

/**
 * Applies changes related to relationships using the specified SNOMED CT editing context.
 */
public class RelationshipPersister extends OntologyChangeProcessor<StatementFragment> {

	private final Concept inferredRelationshipConcept;
	private final Concept existentialRelationshipConcept;
	private final Concept universalRelationshipConcept;
	private final SnomedConceptLookupService conceptLookupService;
	private final SnomedRelationshipLookupService relationshipLookupService;
	private final CDOTransaction transaction;
	private final SnomedEditingContext context;
	private final Map<Long, Concept> relationshipTypeConcepts;
	private final Nature nature;
	
	private final Collection<String> relationshipIds = Sets.newHashSet();
	
	public RelationshipPersister(final SnomedEditingContext context, final Nature nature, final NamespaceAndMolduleAssigner relationshipNamespaceAllocator, Multimap<String, StatementFragment> newStatementsMultimap) {
		
		super(relationshipNamespaceAllocator, newStatementsMultimap);
		this.context = context;
		this.nature = nature;
		
		conceptLookupService = new SnomedConceptLookupService();
		relationshipLookupService = new SnomedRelationshipLookupService();
		
		transaction = context.getTransaction();
		
		inferredRelationshipConcept = conceptLookupService.getComponent(Concepts.INFERRED_RELATIONSHIP, transaction);
		existentialRelationshipConcept = conceptLookupService.getComponent(Concepts.EXISTENTIAL_RESTRICTION_MODIFIER, transaction);
		universalRelationshipConcept = conceptLookupService.getComponent(Concepts.UNIVERSAL_RESTRICTION_MODIFIER, transaction);
		
		relationshipTypeConcepts = Maps.newHashMap();
	}
	
	@Override
	protected void handleRemovedSubject(final String conceptId, final StatementFragment removedEntry) {

		if (!Nature.REMOVE.equals(nature)) {
			return;
		}
		
		final Relationship relationship = (Relationship) context.lookup(removedEntry.getStorageKey());
		SnomedModelExtensions.removeOrDeactivate(relationship);
	}
	
	@Override
	protected void beforeHandleAddedSubjects() {
		//pre-allocate namespaces for the new relationships per each concept
		getRelationshipNamespaceAssigner().allocateRelationshipNamespacesAndModules(newPropertiesMultiMap.keySet(), context);
	}
	
	@Override
	protected void handleAddedSubject(final String sourceConceptId, final StatementFragment addedEntry) {

		if (!Nature.ADD.equals(nature)) {
			return;
		}

		final String namespace = getRelationshipNamespaceAssigner().getRelationshipNamespace(sourceConceptId, context.getBranchPath());
		
		
		final Concept sourceConcept = conceptLookupService.getComponent(sourceConceptId, transaction);
		final Concept module = getRelationshipNamespaceAssigner().getRelationshipModule(sourceConceptId, context.getBranchPath());
		
		final Concept typeConcept;
		final long typeId = addedEntry.getTypeId();
		
		if (relationshipTypeConcepts.containsKey(typeId)) {
			
			typeConcept = relationshipTypeConcepts.get(typeId);
			
		} else {
			
			typeConcept = conceptLookupService.getComponent(Long.toString(typeId), transaction);
			relationshipTypeConcepts.put(typeId, typeConcept);
			
		}
		
		final Concept destinationConcept = conceptLookupService.getComponent(Long.toString(addedEntry.getDestinationId()), transaction);
		
		final Relationship newRel = context.buildEmptyRelationship(namespace);
		
		relationshipIds.add(newRel.getId());

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
						getCharacteristicTypeId(originalMember, Concepts.INFERRED_RELATIONSHIP), 
						originalMember.getLabel(), 
						module.getId(), 
						concreteDataTypeRefSet);
				
				newRel.getConcreteDomainRefSetMembers().add(refSetMember);
			}
		}
	}
	
	public Collection<String> getRelationshipIds() {
		return relationshipIds;
	}

	private String getCharacteristicTypeId(final SnomedConcreteDataTypeRefSetMember originalMember, final String defaultCharacteristicTypeId) {
		return Objects.firstNonNull(originalMember.getCharacteristicTypeId(), defaultCharacteristicTypeId);
	}
}
