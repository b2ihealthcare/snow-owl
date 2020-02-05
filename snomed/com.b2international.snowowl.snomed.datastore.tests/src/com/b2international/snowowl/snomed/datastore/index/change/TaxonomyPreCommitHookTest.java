/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.index.change;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.b2international.collections.PrimitiveSets;
import com.b2international.index.revision.Revision;
import com.b2international.snowowl.core.domain.IComponent;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;
import com.google.common.collect.Iterables;

/**
 * @since 7.0
 */
public class TaxonomyPreCommitHookTest extends BaseConceptPreCommitHookTest {

	@Test
	public void indexOrphanConcept_ShouldFillDefaultsForDerivedFields() throws Exception {
		final SnomedConceptDocument concept = concept().build();
		stageNew(concept);
		
		final ConceptChangeProcessor processor = process();
		
		final SnomedConceptDocument expected = docWithDefaults(concept).build();
		final Revision actual = Iterables.getOnlyElement(processor.getNewMappings().values());
		assertDocEquals(expected, actual);
		assertEquals(0, processor.getChangedMappings().size());
		assertEquals(0, processor.getDeletions().size());
	}
	
	@Test
	public void indexOrphanConceptWithIconId_ShouldGetItsIconId() throws Exception {
		final SnomedConceptDocument concept = concept().build();
		availableImages.add(concept.getId());
		stageNew(concept);
		
		final ConceptChangeProcessor processor = process();
		
		final SnomedConceptDocument expected = docWithDefaults(concept)
				.iconId(concept.getId())
				.build();
		final Revision actual = Iterables.getOnlyElement(processor.getNewMappings().values());
		assertDocEquals(expected, actual);
		assertEquals(0, processor.getChangedMappings().size());
		assertEquals(0, processor.getDeletions().size());
	}
	
	@Test
	public void indexStatedChild() throws Exception {
		// add the ROOT concept as existing concept in the stated tree
		statedChangedConceptIds.add(ROOT_CONCEPTL);
		
		final SnomedConceptDocument concept = concept().build();
		stageNew(concept);
		
		final SnomedRelationshipIndexEntry relationship = createStatedRelationship(concept.getId(), Concepts.IS_A, Concepts.ROOT_CONCEPT);
		stageNew(relationship);
		
		final ConceptChangeProcessor processor = process();
		
		final SnomedConceptDocument expected = docWithDefaults(concept)
				.statedParents(PrimitiveSets.newLongOpenHashSet(ROOT_CONCEPTL))
				.statedAncestors(PrimitiveSets.newLongOpenHashSet(IComponent.ROOT_IDL))
				.build();
		final Revision actual = Iterables.getOnlyElement(processor.getNewMappings().values());
		assertDocEquals(expected, actual);
		assertEquals(0, processor.getChangedMappings().size());
		assertEquals(0, processor.getDeletions().size());
	}
	
	@Test
	public void indexStatedInferredChild() throws Exception {
		// index the ROOT concept as existing concept
		final long rootConceptId = Long.parseLong(Concepts.ROOT_CONCEPT);
		statedChangedConceptIds.add(rootConceptId);
		inferredChangedConceptIds.add(rootConceptId);
		
		final SnomedConceptDocument concept = concept().build();
		stageNew(concept);
		
		final SnomedRelationshipIndexEntry statedRelationship = createStatedRelationship(concept.getId(), Concepts.IS_A, Concepts.ROOT_CONCEPT);
		stageNew(statedRelationship);
		
		final SnomedRelationshipIndexEntry inferredRelationship = createInferredRelationship(concept.getId(), Concepts.IS_A, Concepts.ROOT_CONCEPT);
		stageNew(inferredRelationship);
		
		final ConceptChangeProcessor processor = process();
		
		final SnomedConceptDocument expected = docWithDefaults(concept)
				.parents(PrimitiveSets.newLongOpenHashSet(rootConceptId))
				.ancestors(PrimitiveSets.newLongOpenHashSet(IComponent.ROOT_IDL))
				.statedParents(PrimitiveSets.newLongOpenHashSet(rootConceptId))
				.statedAncestors(PrimitiveSets.newLongOpenHashSet(IComponent.ROOT_IDL))
				.build();
		final Revision actual = Iterables.getOnlyElement(processor.getNewMappings().values());
		assertDocEquals(expected, actual);
		assertEquals(0, processor.getChangedMappings().size());
		assertEquals(0, processor.getDeletions().size());
	}
	
	@Test
	public void updateStatedRelationshipDestination() throws Exception {
		final SnomedConceptDocument sourceConcept = concept().build();
		final SnomedConceptDocument newDestinationConcept = concept(Concepts.NAMESPACE_ROOT).build();
		
		final long concept1Id = Long.parseLong(sourceConcept.getId());
		final long concept2Id = Long.parseLong(newDestinationConcept.getId());
		
		// register IDs for stated changes
		statedChangedConceptIds.add(ROOT_CONCEPTL);
		statedChangedConceptIds.add(concept1Id);
		statedChangedConceptIds.add(concept2Id);
		
		// prepare index repository state
		final SnomedRelationshipIndexEntry statedRelationship = createStatedRelationship(sourceConcept.getId(), Concepts.IS_A, Concepts.ROOT_CONCEPT);

		initRevisions(
			statedRelationship, 
			docWithDefaults(sourceConcept)
				.statedParents(ROOT_CONCEPTL)
				.build(),
			docWithDefaults(newDestinationConcept).build()
		);
		
		// change destination from ROOT to NEWDST
		stageChange(statedRelationship, SnomedRelationshipIndexEntry.builder(statedRelationship).destinationId(newDestinationConcept.getId()).build());

		final ConceptChangeProcessor processor = process();
		
		final SnomedConceptDocument expected = docWithDefaults(sourceConcept)
				.iconId(Concepts.NAMESPACE_ROOT)
				.statedParents(PrimitiveSets.newLongOpenHashSet(concept2Id))
				.statedAncestors(PrimitiveSets.newLongOpenHashSet(IComponent.ROOT_IDL))
				.build();
		assertEquals(1, processor.getChangedMappings().size());
		final Revision actual = Iterables.getOnlyElement(processor.getChangedMappings().values()).getNewRevision();
		assertDocEquals(expected, actual);
		assertEquals(0, processor.getNewMappings().size());
		assertEquals(0, processor.getDeletions().size());
	}
	
	// Sanity checks
	
	@Test
	public void deleteLeafConcept() throws Exception {
		SnomedConceptDocument concept = concept().build();
		indexRevision(MAIN, docWithDefaults(concept).build());
		stageRemove(concept);
		
		final ConceptChangeProcessor processor = process();
		
		assertEquals(0, processor.getNewMappings().size());
		assertEquals(0, processor.getChangedMappings().size());
		assertEquals(0, processor.getDeletions().size());
	}
	
	@Test
	public void deleteConceptWithOneStatedChild() throws Exception {
		final long namespaceRootLong = Long.parseLong(Concepts.NAMESPACE_ROOT);
		// given a parent concept and child concept
		final SnomedConceptDocument parent = concept().build();
		final SnomedConceptDocument child = concept().build();
		
		availableImages.add(parent.getId());
		// and a stated relationship between the two
		final SnomedRelationshipIndexEntry childToParentIsa = createStatedRelationship(child.getId(), Concepts.IS_A, parent.getId());
		final SnomedRelationshipIndexEntry childToAnotherConceptIsa = createStatedRelationship(child.getId(), Concepts.IS_A, Concepts.NAMESPACE_ROOT);
		
		final long parentIdLong = Long.parseLong(parent.getId());
		final long childIdLong = Long.parseLong(child.getId());
		
		// index the child and parent concept documents as current state
		initRevisions(
			docWithDefaults(parent).build(),
			// child concept has stated parent and ROOT ancestor
			docWithDefaults(child)
				.statedParents(PrimitiveSets.newLongOpenHashSet(parentIdLong))
				.statedAncestors(PrimitiveSets.newLongOpenHashSet(IComponent.ROOT_IDL))
				.build(),
			// index existing stated relationships
			childToParentIsa,
			childToAnotherConceptIsa
		);
		
		// delete parent concept and its single relationship
		stageRemove(parent);
		stageRemove(childToParentIsa);
		
		statedChangedConceptIds.add(parentIdLong);
		statedChangedConceptIds.add(childIdLong);
		statedChangedConceptIds.add(namespaceRootLong);
		
		final ConceptChangeProcessor processor = process();
		
		// the parent concept should be deleted, but handled by stageRemove 
		assertEquals(0, processor.getDeletions().size());
		
		// and the child concept needs to be reindexed as child of the invisible ROOT ID
		assertEquals(1, processor.getChangedMappings().size());
		final Revision newChildRevision = Iterables.getOnlyElement(processor.getChangedMappings().values()).getNewRevision();
		final SnomedConceptDocument expectedChildRevision = docWithDefaults(child)
				.iconId(Concepts.NAMESPACE_ROOT)
				.statedParents(PrimitiveSets.newLongOpenHashSet(namespaceRootLong))
				.statedAncestors(PrimitiveSets.newLongOpenHashSet(IComponent.ROOT_IDL))
				.build();
		assertDocEquals(expectedChildRevision, newChildRevision);
		
		// no new mappings were registered
		assertEquals(0, processor.getNewMappings().size());
	}

	@Test
	public void addIsaRelationshipToExistingConcepts() throws Exception {
		// given a parent concept and child concept
		final SnomedConceptDocument parent = concept().build();
		final SnomedConceptDocument child = concept().build();
		
		final long parentIdLong = Long.parseLong(parent.getId());
		final long childIdLong = Long.parseLong(child.getId());
		statedChangedConceptIds.add(parentIdLong);
		statedChangedConceptIds.add(childIdLong);

		// index the child and parent concept documents as current state
		initRevisions(
			docWithDefaults(parent).build(),
			docWithDefaults(child).build()
		);
		
		// add a new stated relationship between the two
		final SnomedRelationshipIndexEntry childToParentIsa = createStatedRelationship(child.getId(), Concepts.IS_A, parent.getId());
		stageNew(childToParentIsa);
		
		final ConceptChangeProcessor processor = process();
		
		// the child document should be reindexed with new parent information 
		assertEquals(1, processor.getChangedMappings().size());
		final SnomedConceptDocument expectedDoc = docWithDefaults(child)
				.statedParents(PrimitiveSets.newLongOpenHashSet(parentIdLong))
				.statedAncestors(PrimitiveSets.newLongOpenHashSet(IComponent.ROOT_IDL))
				.build();
		final Revision changedDoc = Iterables.getOnlyElement(processor.getChangedMappings().values()).getNewRevision();
		assertDocEquals(expectedDoc, changedDoc);
		assertEquals(0, processor.getNewMappings().size());
		assertEquals(0, processor.getDeletions().size());
	}
	
	@Test
	public void newInferredIsaRelationshipDoesNotChangeStatedTaxonomy() throws Exception {
		// given a parent concept and child concept
		final SnomedConceptDocument parent = concept().build();
		final SnomedConceptDocument child = concept().build();
		final SnomedRelationshipIndexEntry statedIsa = createStatedRelationship(child.getId(), Concepts.IS_A, parent.getId());
		
		
		final long parentIdLong = Long.parseLong(parent.getId());
		final long childIdLong = Long.parseLong(child.getId());

		// index the child and parent concept documents as current state
		initRevisions(
			docWithDefaults(parent).build(), 
			docWithDefaults(child)
				.statedParents(PrimitiveSets.newLongOpenHashSet(parentIdLong))
				.statedAncestors(PrimitiveSets.newLongOpenHashSet(IComponent.ROOT_IDL))
				.build(), 
			statedIsa
		);
		
		// add a new stated relationship between the two
		inferredChangedConceptIds.add(parentIdLong);
		inferredChangedConceptIds.add(childIdLong);
		
		final SnomedRelationshipIndexEntry childToParentIsa = createInferredRelationship(child.getId(), Concepts.IS_A, parent.getId());
		stageNew(childToParentIsa);
		
		final ConceptChangeProcessor processor = process();
		
		assertEquals(1, processor.getChangedMappings().size());
		final SnomedConceptDocument expected = docWithDefaults(child)
				.statedParents(PrimitiveSets.newLongOpenHashSet(parentIdLong))
				.statedAncestors(PrimitiveSets.newLongOpenHashSet(IComponent.ROOT_IDL))
				.parents(PrimitiveSets.newLongOpenHashSet(parentIdLong))
				.ancestors(PrimitiveSets.newLongOpenHashSet(IComponent.ROOT_IDL))
				.build();
		final Revision actual = Iterables.getOnlyElement(processor.getChangedMappings().values()).getNewRevision();
		assertDocEquals(expected, actual);
		assertEquals(0, processor.getNewMappings().size());
		assertEquals(0, processor.getDeletions().size());
	}
	
}
