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
package com.b2international.snowowl.snomed.datastore.taxonomy;

import java.io.IOException;
import java.util.Map;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOSetFeatureDelta;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.ecore.EObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.CompareUtils;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.datastore.CDOCommitChangeSet;
import com.b2international.snowowl.datastore.ICDOCommitChangeSet;
import com.b2international.snowowl.datastore.cdo.CDOIDUtils;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.Relationship;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.SnomedPackage;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedOWLRelationshipDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;
import com.b2international.snowowl.snomed.datastore.request.SnomedOWLExpressionConverter;
import com.b2international.snowowl.snomed.datastore.request.SnomedOWLExpressionConverterResult;
import com.b2international.snowowl.snomed.snomedrefset.SnomedOWLExpressionRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;

/**
 * @since 4.7
 */
public class TaxonomyGraphUpdater {
	
	private static final Logger LOGGER = LoggerFactory.getLogger("repository");

	private final ICDOCommitChangeSet commitChangeSet;
	private final String characteristicTypeId;
	private final RevisionSearcher searcher;
	private final SnomedOWLExpressionConverter expressionConverter;
	
	public TaxonomyGraphUpdater(RevisionSearcher searcher,
			SnomedOWLExpressionConverter expressionConverter,
			CDOTransaction transaction,
			String characteristicTypeId) {
		
		this(searcher, expressionConverter, new CDOCommitChangeSet(transaction, 
						transaction.getSession().getUserID(), 
						transaction.getCommitComment(), 
						transaction.getNewObjects().values(), 
						transaction.getDirtyObjects().values(), 
						Maps.transformValues(transaction.getDetachedObjects(), EObject::eClass), 
						transaction.getRevisionDeltas(), 
						-1L),
				characteristicTypeId);
	}
			
	public TaxonomyGraphUpdater(RevisionSearcher searcher,
			SnomedOWLExpressionConverter expressionConverter,
			ICDOCommitChangeSet commitChangeSet, 
			String characteristicTypeId) {

		this.searcher = searcher;
		this.expressionConverter = expressionConverter;
		this.commitChangeSet = commitChangeSet;
		this.characteristicTypeId = characteristicTypeId;
	}
	
	public TaxonomyGraphStatus update(final TaxonomyGraph graphToUpdate) {
		LOGGER.trace("Processing changes taxonomic information.");
		
		//here we have to consider changes triggered by repository state revert
		//this point the following might happen:
		//SNOMED CT concept and/or relationship will be contained by both deleted and new collections
		//with same business (SCT ID) but different primary ID (CDO ID) [this is the way how we handle object resurrection]
		//we decided, to order changes by primary keys. as primary IDs are provided in sequence, one could assume
		//that the larger primary ID happens later, and that is the truth
		
		//but as deletion always happens later than addition, we only have to take care of deletion
		//so if the deletion is about to erase something that has the same SCT ID but more recent (larger) 
		//primary key, we just ignore it when building the taxonomy.
		
		final Iterable<Concept> newConcepts = commitChangeSet.getNewComponents(Concept.class);
		final Iterable<Concept> dirtyConcepts = commitChangeSet.getDirtyComponents(Concept.class);
		final Iterable<CDOID> deletedConceptStorageKeys = commitChangeSet.getDetachedComponents(SnomedPackage.Literals.CONCEPT);
		final Iterable<Relationship> newRelationships = commitChangeSet.getNewComponents(Relationship.class);
		final Iterable<Relationship> dirtyRelationships = commitChangeSet.getDirtyComponents(Relationship.class);
		final Iterable<CDOID> deletedRelationships = commitChangeSet.getDetachedComponents(SnomedPackage.Literals.RELATIONSHIP);
		
		//SCT ID - relationships
		final Map<String, Relationship> _newRelationships = Maps.newHashMap(Maps.uniqueIndex(newRelationships, Relationship::getId));
		
		//SCT ID - concepts
		final Map<String, Concept> _newConcepts = Maps.newHashMap(Maps.uniqueIndex(newConcepts, Concept::getId));
		
		for (final Relationship newRelationship : newRelationships) {
			updateEdge(newRelationship, graphToUpdate);
		}
		
		for (final Relationship dirtyRelationship : dirtyRelationships) {
			updateEdge(dirtyRelationship, graphToUpdate);
		}
		
		// lookup all deleted relationship documents
		final Iterable<SnomedRelationshipIndexEntry> deletedRelationshipEntries;
		try {
			deletedRelationshipEntries = searcher.get(SnomedRelationshipIndexEntry.class, CDOIDUtils.createCdoIdToLong(deletedRelationships));
		} catch (IOException e) {
			throw new SnowowlRuntimeException(e);
		}
		
		for (final SnomedRelationshipIndexEntry relationship : deletedRelationshipEntries) {
			final String relationshipId = relationship.getId();
			//same relationship as new and detached
			if (_newRelationships.containsKey(relationshipId)) {
				final Relationship newRelationship = _newRelationships.get(relationshipId);
				final String typeId = newRelationship.getType().getId();
				//ignore everything but IS_As
				if (Concepts.IS_A.equals(typeId)) {
					//check source and destination as well
					if (relationship.getSourceId().equals(newRelationship.getSource().getId())
							&& relationship.getDestinationId().equals(newRelationship.getDestination().getId())) {
						
						//and if the new relationship has more recent (larger CDO ID), ignore deletion
						if (CDOIDUtils.asLong(newRelationship.cdoID()) > relationship.getStorageKey()) {
							continue;
						}
					}
				}
			}
			graphToUpdate.removeEdge(relationship.getId());
		}
		
		if (Concepts.STATED_RELATIONSHIP.equals(characteristicTypeId)) {
			final Iterable<SnomedOWLExpressionRefSetMember> newOwlMembers = commitChangeSet.getNewComponents(SnomedOWLExpressionRefSetMember.class);
			final Iterable<SnomedOWLExpressionRefSetMember> dirtyOwlMembers = commitChangeSet.getDirtyComponents(SnomedOWLExpressionRefSetMember.class);
			for (SnomedOWLExpressionRefSetMember owlMember : Iterables.concat(newOwlMembers, dirtyOwlMembers)) {
				updateEdge(owlMember, graphToUpdate);
			}
			
			final Iterable<CDOID> deletedOwlAxioms = commitChangeSet.getDetachedComponents(SnomedRefSetPackage.Literals.SNOMED_OWL_EXPRESSION_REF_SET_MEMBER);
			// look up all deleted owl axioms
			final Iterable<SnomedRefSetMemberIndexEntry> deletedAxiomEntries;
			try {
				deletedAxiomEntries = searcher.get(SnomedRefSetMemberIndexEntry.class, CDOIDUtils.createCdoIdToLong(deletedOwlAxioms));
			} catch (IOException e) {
				throw new SnowowlRuntimeException(e);
			}
			for (final SnomedRefSetMemberIndexEntry detachedOwlMember : deletedAxiomEntries) {
				graphToUpdate.removeEdge(detachedOwlMember.getId());
			}
		}
		
		for (final Concept newConcept : newConcepts) {
			updateConcept(newConcept, graphToUpdate);
		}
		
		try {
			final Iterable<SnomedConceptDocument> deletedConcepts = searcher.get(SnomedConceptDocument.class, CDOIDUtils.createCdoIdToLong(deletedConceptStorageKeys));
			for (final SnomedConceptDocument concept : deletedConcepts) {
				//consider the same as for relationship
				//we have to decide if deletion is the 'stronger' modification or not
				final String conceptId = concept.getId();
				
				//same concept as addition and deletion
				if (_newConcepts.containsKey(conceptId)) {
					final Concept newConcept = _newConcepts.get(conceptId);
					//check whether new concept has more recent (larger CDO ID) or not, ignore deletion
					if (CDOIDUtils.asLong(newConcept.cdoID()) > concept.getStorageKey()) {
						continue;
					}
				}
				//else delete it
				graphToUpdate.removeNode(conceptId);
			}
		} catch (IOException e) {
			throw new SnowowlRuntimeException(e);
		}
		
		for (final Concept dirtyConcept : dirtyConcepts) {
			final CDORevisionDelta revisionDelta = commitChangeSet.getRevisionDeltas().get(dirtyConcept.cdoID());
			if (revisionDelta == null) {
				continue;
			}
			final CDOFeatureDelta changeStatusDelta = revisionDelta.getFeatureDelta(SnomedPackage.Literals.COMPONENT__ACTIVE);
			if (changeStatusDelta instanceof CDOSetFeatureDelta) {
				CDOSetFeatureDelta delta = (CDOSetFeatureDelta) changeStatusDelta;
				final Boolean oldValue;
				if (delta.getOldValue() instanceof Boolean) {
					oldValue = (Boolean) delta.getOldValue();
				} else if (CDOSetFeatureDelta.UNSPECIFIED == delta.getOldValue()) {
					oldValue = false;
				} else {
					throw new RuntimeException("Unknown old value type: " + delta.getOldValue());
				}
				final Boolean newValue = (Boolean) delta.getValue();
				if (Boolean.FALSE == oldValue && Boolean.TRUE == newValue) {
					// make sure the node is part of the new tree
					graphToUpdate.addNode(dirtyConcept.getId());
				}
			}
		}
		LOGGER.trace("Rebuilding taxonomic information based on the changes.");
		return graphToUpdate.update();
	}
	
	private void updateEdge(SnomedOWLExpressionRefSetMember owlMember, TaxonomyGraph graphToUpdate) {
		if (owlMember.isActive()) {
			SnomedOWLExpressionConverterResult result = expressionConverter.toSnomedOWLRelationships(owlMember.getReferencedComponentId(), owlMember.getOwlExpression());
			if (!CompareUtils.isEmpty(result.getClassAxiomRelationships())) {
				final long[] destinationIds = result.getClassAxiomRelationships().stream()
					.filter(r -> Concepts.IS_A.equals(r.getTypeId()))
					.map(SnomedOWLRelationshipDocument::getDestinationId)
					.mapToLong(Long::parseLong)
					.toArray();
				graphToUpdate.addEdge(owlMember.getUuid(), Long.parseLong(owlMember.getReferencedComponentId()), destinationIds);
			} else {
				graphToUpdate.removeEdge(owlMember.getUuid());
			}
		} else {
			graphToUpdate.removeEdge(owlMember.getUuid());
		}
	}

	private void updateConcept(Concept concept, TaxonomyGraph graphToUpdate) {
		if (concept.isActive()) {
			graphToUpdate.addNode(concept.getId());
		}
	}

	private void updateEdge(final Relationship relationship, TaxonomyGraph graphToUpdate) {
		if (!relationship.isActive()) {
			graphToUpdate.removeEdge(relationship.getId());
		} else if (Concepts.IS_A.equals(relationship.getType().getId()) && characteristicTypeId.equals(relationship.getCharacteristicType().getId())) {
			graphToUpdate.addEdge(
				relationship.getId(),
				Long.parseLong(relationship.getSource().getId()),
				new long[] { Long.parseLong(relationship.getDestination().getId()) }
			);
		}
	}
	
}