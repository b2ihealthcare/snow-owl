/*
 * Copyright 2019 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.Collections;

import org.junit.Test;

import com.b2international.collections.PrimitiveSets;
import com.b2international.index.revision.Revision;
import com.b2international.snowowl.core.domain.IComponent;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedOWLRelationshipDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

/**
 * @since 6.14
 */
public class ConceptChangeProcessorAxiomTest extends BaseConceptPreCommitHookTest {

	@Test
	public void newEmptyAxiom() throws Exception {
		SnomedConceptDocument concept = concept().build();
		indexRevision(MAIN, concept);
		
		SnomedRefSetMemberIndexEntry member = createOwlAxiom(concept.getId(), "").build();
		stageNew(member);
		
		final ConceptChangeProcessor processor = process();
		
		assertEquals(1, processor.getChangedMappings().size());
		final SnomedConceptDocument expected = docWithDefaults(concept)
				.activeMemberOf(Collections.singleton(Concepts.REFSET_OWL_AXIOM))
				.memberOf(Collections.singleton(Concepts.REFSET_OWL_AXIOM))
				.build();
		final Revision actual = Iterables.getOnlyElement(processor.getChangedMappings().values()).getNewRevision();
		assertDocEquals(expected, actual);
		assertEquals(0, processor.getNewMappings().size());
		assertEquals(0, processor.getDeletions().size());
	}
	
	@Test
	public void newSubClassOfAxiom() throws Exception {
		SnomedConceptDocument concept = concept().build();
		SnomedConceptDocument parentConcept = concept().build();
		
		indexRevision(MAIN, concept, parentConcept);
		
		statedChangedConceptIds.add(Long.parseLong(concept.getId()));
		statedChangedConceptIds.add(Long.parseLong(parentConcept.getId()));
		
		SnomedRefSetMemberIndexEntry member = createOwlAxiom(concept.getId(), String.format("SubClassOf(:%s :%s)", concept.getId(), parentConcept.getId())).build();
		stageNew(member);
		
		final ConceptChangeProcessor processor = process();
		
		assertEquals(1, processor.getChangedMappings().size());
		final SnomedConceptDocument expected = docWithDefaults(concept)
				.statedParents(Long.parseLong(parentConcept.getId()))
				.statedAncestors(PrimitiveSets.newLongOpenHashSet(IComponent.ROOT_IDL))
				.activeMemberOf(Collections.singleton(Concepts.REFSET_OWL_AXIOM))
				.memberOf(Collections.singleton(Concepts.REFSET_OWL_AXIOM))
				.build();
		final Revision actual = Iterables.getOnlyElement(processor.getChangedMappings().values()).getNewRevision();
		assertDocEquals(expected, actual);
		assertEquals(0, processor.getNewMappings().size());
		assertEquals(0, processor.getDeletions().size());
	}
	
	@Test
	public void newSubClassOfAxiom_TwoTargets() throws Exception {
		SnomedConceptDocument concept = concept().build();
		SnomedConceptDocument parentConcept = concept().build();
		SnomedConceptDocument parentConcept2 = concept().build();
		indexRevision(MAIN, concept, parentConcept, parentConcept2);
		
		statedChangedConceptIds.add(Long.parseLong(concept.getId()));
		statedChangedConceptIds.add(Long.parseLong(parentConcept.getId()));
		statedChangedConceptIds.add(Long.parseLong(parentConcept2.getId()));
		
		SnomedRefSetMemberIndexEntry member = createOwlAxiom(concept.getId(), String.format("SubClassOf(:%s ObjectIntersectionOf(:%s :%s))", concept.getId(), parentConcept.getId(), parentConcept2.getId())).build();
		stageNew(member);
		
		final ConceptChangeProcessor processor = process();
		
		assertEquals(1, processor.getChangedMappings().size());
		final SnomedConceptDocument expected = docWithDefaults(concept)
				.statedParents(Long.parseLong(parentConcept.getId()), Long.parseLong(parentConcept2.getId()))
				.statedAncestors(PrimitiveSets.newLongOpenHashSet(IComponent.ROOT_IDL))
				.activeMemberOf(Collections.singleton(Concepts.REFSET_OWL_AXIOM))
				.memberOf(Collections.singleton(Concepts.REFSET_OWL_AXIOM))
				.build();
		final Revision actual = Iterables.getOnlyElement(processor.getChangedMappings().values()).getNewRevision();
		assertDocEquals(expected, actual);
		assertEquals(0, processor.getNewMappings().size());
		assertEquals(0, processor.getDeletions().size());
	}
	
	@Test
	public void updateEmptyAxiom() throws Exception {
		SnomedConceptDocument concept = concept()
				.activeMemberOf(Collections.singleton(Concepts.REFSET_OWL_AXIOM))
				.memberOf(Collections.singleton(Concepts.REFSET_OWL_AXIOM))
				.build();
		SnomedConceptDocument parentConcept = concept().build();
		SnomedRefSetMemberIndexEntry member = createOwlAxiom(concept.getId(), "").build();
		
		indexRevision(MAIN, concept, parentConcept, member);
		
		statedChangedConceptIds.add(Long.parseLong(concept.getId()));
		statedChangedConceptIds.add(Long.parseLong(parentConcept.getId()));
		
		// update axiom
		stageChange(member, SnomedRefSetMemberIndexEntry.builder(member).owlExpression(String.format("SubClassOf(:%s :%s)", concept.getId(), parentConcept.getId())).build());
		
		final ConceptChangeProcessor processor = process();
		
		assertEquals(1, processor.getChangedMappings().size());
		final SnomedConceptDocument expected = docWithDefaults(concept)
				.statedParents(Long.parseLong(parentConcept.getId()))
				.statedAncestors(PrimitiveSets.newLongOpenHashSet(IComponent.ROOT_IDL))
				.activeMemberOf(Collections.singleton(Concepts.REFSET_OWL_AXIOM))
				.memberOf(Collections.singleton(Concepts.REFSET_OWL_AXIOM))
				.build();
		final Revision actual = Iterables.getOnlyElement(processor.getChangedMappings().values()).getNewRevision();
		assertDocEquals(expected, actual);
		assertEquals(0, processor.getNewMappings().size());
		assertEquals(0, processor.getDeletions().size());
	}
	
	@Test
	public void updateAxiomParent() throws Exception {
		SnomedConceptDocument parentConcept = concept().build();
		SnomedConceptDocument concept = concept()
				.statedParents(Long.parseLong(parentConcept.getId()))
				.statedAncestors(PrimitiveSets.newLongOpenHashSet(IComponent.ROOT_IDL))
				.activeMemberOf(Collections.singleton(Concepts.REFSET_OWL_AXIOM))
				.memberOf(Collections.singleton(Concepts.REFSET_OWL_AXIOM))
				.build();
		SnomedConceptDocument parentConcept2 = concept().build();
		SnomedRefSetMemberIndexEntry member = createOwlAxiom(concept.getId(), String.format("SubClassOf(:%s :%s)", concept.getId(), parentConcept.getId()))
				.classAxiomRelationships(ImmutableList.of(new SnomedOWLRelationshipDocument(Concepts.IS_A, parentConcept.getId(), 0)))
				.build();
		
		indexRevision(MAIN, concept, parentConcept, parentConcept2, member);
		
		statedChangedConceptIds.add(Long.parseLong(concept.getId()));
		statedChangedConceptIds.add(Long.parseLong(parentConcept.getId()));
		statedChangedConceptIds.add(Long.parseLong(parentConcept2.getId()));
		
		// update axiom
		stageChange(member, SnomedRefSetMemberIndexEntry.builder(member).owlExpression(String.format("SubClassOf(:%s :%s)", concept.getId(), parentConcept2.getId())).build());
		
		final ConceptChangeProcessor processor = process();
		
		assertEquals(1, processor.getChangedMappings().size());
		final SnomedConceptDocument expected = docWithDefaults(concept)
				.statedParents(Long.parseLong(parentConcept2.getId()))
				.statedAncestors(PrimitiveSets.newLongOpenHashSet(IComponent.ROOT_IDL))
				.activeMemberOf(Collections.singleton(Concepts.REFSET_OWL_AXIOM))
				.memberOf(Collections.singleton(Concepts.REFSET_OWL_AXIOM))
				.build();
		final Revision actual = Iterables.getOnlyElement(processor.getChangedMappings().values()).getNewRevision();
		assertDocEquals(expected, actual);
		assertEquals(0, processor.getNewMappings().size());
		assertEquals(0, processor.getDeletions().size());
	}
	
	@Test
	public void deleteEmpty() throws Exception {
		final SnomedConceptDocument concept = concept()
				.activeMemberOf(Collections.singleton(Concepts.REFSET_OWL_AXIOM))
				.memberOf(Collections.singleton(Concepts.REFSET_OWL_AXIOM))
				.build();
		final SnomedRefSetMemberIndexEntry member = createOwlAxiom(concept.getId(), "").build();

		indexRevision(MAIN, concept, member);
		
		stageRemove(member);
		
		final ConceptChangeProcessor processor = process();
		
		assertEquals(1, processor.getChangedMappings().size());
		final SnomedConceptDocument expected = docWithDefaults(concept).build();
		final Revision actual = Iterables.getOnlyElement(processor.getChangedMappings().values()).getNewRevision();
		assertDocEquals(expected, actual);
		assertEquals(0, processor.getNewMappings().size());
		assertEquals(0, processor.getDeletions().size());
	}
	
	@Test
	public void deleteSubClassOfAxiom() throws Exception {
		final SnomedConceptDocument parentConcept = concept().build();
		final SnomedConceptDocument concept = concept()
				.statedParents(Long.parseLong(parentConcept.getId()))
				.statedAncestors(PrimitiveSets.newLongOpenHashSet(IComponent.ROOT_IDL))
				.build();
		SnomedRefSetMemberIndexEntry member = createOwlAxiom(concept.getId(), String.format("SubClassOf(:%s :%s)", concept.getId(), parentConcept.getId()))
				.classAxiomRelationships(ImmutableList.of(new SnomedOWLRelationshipDocument(Concepts.IS_A, parentConcept.getId(), 0)))
				.build();
		
		indexRevision(MAIN, concept, parentConcept, member);
		
		statedChangedConceptIds.add(Long.parseLong(concept.getId()));
		statedChangedConceptIds.add(Long.parseLong(parentConcept.getId()));
		
		stageRemove(member);
		
		final ConceptChangeProcessor processor = process();
		
		assertEquals(1, processor.getChangedMappings().size());
		final SnomedConceptDocument expected = docWithDefaults(concept)
				.statedParents(PrimitiveSets.newLongOpenHashSet(IComponent.ROOT_IDL))
				.build();
		final Revision actual = Iterables.getOnlyElement(processor.getChangedMappings().values()).getNewRevision();
		assertDocEquals(expected, actual);
		assertEquals(0, processor.getNewMappings().size());
		assertEquals(0, processor.getDeletions().size());
	}
	
	@Test
	public void deleteSubClassOfAxiom_TwoTargets() throws Exception {
		final SnomedConceptDocument parentConcept = concept().build();
		final SnomedConceptDocument parentConcept2 = concept().build();
		final SnomedConceptDocument concept = concept()
				.statedParents(Long.parseLong(parentConcept.getId()))
				.statedAncestors(PrimitiveSets.newLongOpenHashSet(IComponent.ROOT_IDL))
				.build();
		final SnomedRefSetMemberIndexEntry member = createOwlAxiom(concept.getId(), String.format("SubClassOf(:%s ObjectIntersectionOf(:%s :%s))", concept.getId(), parentConcept.getId(), parentConcept2.getId()))
				.classAxiomRelationships(ImmutableList.of(new SnomedOWLRelationshipDocument(Concepts.IS_A, parentConcept.getId(), 0)))
				.build();
		
		indexRevision(MAIN, concept, parentConcept, parentConcept2, member);
		
		statedChangedConceptIds.add(Long.parseLong(concept.getId()));
		statedChangedConceptIds.add(Long.parseLong(parentConcept.getId()));
		
		stageRemove(member);
		
		final ConceptChangeProcessor processor = process();
		
		assertEquals(1, processor.getChangedMappings().size());
		final SnomedConceptDocument expected = docWithDefaults(concept)
				.statedParents(PrimitiveSets.newLongOpenHashSet(IComponent.ROOT_IDL))
				.build();
		final Revision actual = Iterables.getOnlyElement(processor.getChangedMappings().values()).getNewRevision();
		assertDocEquals(expected, actual);
		assertEquals(0, processor.getNewMappings().size());
		assertEquals(0, processor.getDeletions().size());
	}
	
	@Test
	public void inactivateIsa_AddSubClassOf() throws Exception {
		final SnomedConceptDocument parentConcept = concept().build();
		final SnomedConceptDocument concept = concept()
				.statedParents(Long.parseLong(parentConcept.getId()))
				.statedAncestors(PrimitiveSets.newLongOpenHashSet(IComponent.ROOT_IDL))
				.build();
		SnomedRelationshipIndexEntry isaRelationship = createStatedRelationship(concept.getId(), Concepts.IS_A, parentConcept.getId());

		indexRevision(MAIN, concept, parentConcept, isaRelationship);
		
		statedChangedConceptIds.add(Long.parseLong(concept.getId()));
		statedChangedConceptIds.add(Long.parseLong(parentConcept.getId()));
		
		stageChange(isaRelationship, SnomedRelationshipIndexEntry.builder(isaRelationship).active(false).build());
		
		SnomedRefSetMemberIndexEntry member = createOwlAxiom(concept.getId(), String.format("SubClassOf(:%s :%s)", concept.getId(), parentConcept.getId())).build();
		stageNew(member);
		
		final ConceptChangeProcessor processor = process();
		
		assertEquals(1, processor.getChangedMappings().size());
		final SnomedConceptDocument expected = docWithDefaults(concept)
				.statedParents(Long.parseLong(parentConcept.getId()))
				.statedAncestors(PrimitiveSets.newLongOpenHashSet(IComponent.ROOT_IDL))
				.activeMemberOf(Collections.singleton(Concepts.REFSET_OWL_AXIOM))
				.memberOf(Collections.singleton(Concepts.REFSET_OWL_AXIOM))
				.build();
		final Revision actual = Iterables.getOnlyElement(processor.getChangedMappings().values()).getNewRevision();
		assertDocEquals(expected, actual);
		
		assertEquals(0, processor.getNewMappings().size());
		assertEquals(0, processor.getDeletions().size());
	}
	
	@Test
	public void activateIsa_InactivateOwlMember() throws Exception {
		final SnomedConceptDocument parentConcept = concept().build();
		final SnomedConceptDocument concept = concept()
				.statedParents(Long.parseLong(parentConcept.getId()))
				.statedAncestors(PrimitiveSets.newLongOpenHashSet(IComponent.ROOT_IDL))
				.activeMemberOf(Collections.singleton(Concepts.REFSET_OWL_AXIOM))
				.memberOf(Collections.singleton(Concepts.REFSET_OWL_AXIOM))
				.build();
		SnomedRelationshipIndexEntry isaRelationship = SnomedRelationshipIndexEntry.builder(createStatedRelationship(concept.getId(), Concepts.IS_A, parentConcept.getId())).active(false).build();
		SnomedRefSetMemberIndexEntry member = createOwlAxiom(concept.getId(), String.format("SubClassOf(:%s :%s)", concept.getId(), parentConcept.getId()))
				.classAxiomRelationships(ImmutableList.of(new SnomedOWLRelationshipDocument(Concepts.IS_A, parentConcept.getId(), 0)))
				.build();
		
		indexRevision(MAIN, concept, parentConcept, isaRelationship, member);
		
		statedChangedConceptIds.add(Long.parseLong(concept.getId()));
		statedChangedConceptIds.add(Long.parseLong(parentConcept.getId()));

		stageChange(isaRelationship, SnomedRelationshipIndexEntry.builder(isaRelationship).active(true).build());
		stageChange(member, SnomedRefSetMemberIndexEntry.builder(member).active(false).build());
		
		final ConceptChangeProcessor processor = process();
		
		assertEquals(1, processor.getChangedMappings().size());
		final SnomedConceptDocument expected = docWithDefaults(concept)
				.statedParents(Long.parseLong(parentConcept.getId()))
				.statedAncestors(PrimitiveSets.newLongOpenHashSet(IComponent.ROOT_IDL))
				.memberOf(Collections.singleton(Concepts.REFSET_OWL_AXIOM))
				.build();
		final Revision actual = Iterables.getOnlyElement(processor.getChangedMappings().values()).getNewRevision();
		assertDocEquals(expected, actual);
		
		assertEquals(0, processor.getNewMappings().size());
		assertEquals(0, processor.getDeletions().size());
	}
	
}
