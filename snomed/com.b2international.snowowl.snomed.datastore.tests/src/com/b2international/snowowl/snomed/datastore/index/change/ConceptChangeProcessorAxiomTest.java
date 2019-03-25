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

import static com.b2international.snowowl.snomed.datastore.id.RandomSnomedIdentiferGenerator.generateConceptId;
import static com.google.common.collect.Sets.newHashSet;
import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.junit.Test;

import com.b2international.collections.PrimitiveSets;
import com.b2international.collections.longs.LongSet;
import com.b2international.index.revision.Revision;
import com.b2international.index.revision.RevisionIndexRead;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.domain.IComponent;
import com.b2international.snowowl.datastore.ICDOCommitChangeSet;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.Relationship;
import com.b2international.snowowl.snomed.SnomedPackage;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.ecl.TestBranchContext;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedOWLRelationshipDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;
import com.b2international.snowowl.snomed.datastore.request.SnomedOWLExpressionConverter;
import com.b2international.snowowl.snomed.datastore.taxonomy.Taxonomies;
import com.b2international.snowowl.snomed.datastore.taxonomy.Taxonomy;
import com.b2international.snowowl.snomed.snomedrefset.SnomedOWLExpressionRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

/**
 * @since 6.14
 */
public class ConceptChangeProcessorAxiomTest extends BaseChangeProcessorTest {

	private static final BranchContext CONTEXT = TestBranchContext.on(MAIN).build();

	private Collection<String> availableImages = newHashSet(Concepts.ROOT_CONCEPT, Concepts.MODULE_ROOT, Concepts.NAMESPACE_ROOT);
	private LongSet statedChangedConceptIds = PrimitiveSets.newLongOpenHashSet();
	private LongSet inferredChangedConceptIds = PrimitiveSets.newLongOpenHashSet();
	
	
	private ConceptChangeProcessor process() {
		return index().read(MAIN, new RevisionIndexRead<ConceptChangeProcessor>() {
			@Override
			public ConceptChangeProcessor execute(RevisionSearcher searcher) throws IOException {
				final ICDOCommitChangeSet commitChangeSet = createChangeSet();
				final SnomedOWLExpressionConverter expressionConverter = new SnomedOWLExpressionConverter(CONTEXT.inject()
						.bind(RevisionSearcher.class, searcher)
						.build());
				final Taxonomy inferredTaxonomy = Taxonomies.inferred(searcher, expressionConverter, commitChangeSet, inferredChangedConceptIds, true);
				final Taxonomy statedTaxonomy = Taxonomies.stated(searcher, expressionConverter, commitChangeSet, statedChangedConceptIds, true);
				final ConceptChangeProcessor processor = new ConceptChangeProcessor(DoiData.DEFAULT_SCORE, availableImages, statedTaxonomy, inferredTaxonomy);
				processor.process(commitChangeSet, searcher);
				return processor;
			}
		});
	}
	
	@Test
	public void newEmptyAxiom() throws Exception {
		final Concept concept = createConcept(generateConceptId());
		registerExistingObject(concept);
		indexRevision(MAIN, CDOIDUtil.getLong(concept.cdoID()), doc(concept).build());
		
		SnomedOWLExpressionRefSetMember member = createOwlAxiom(concept.getId(), "");
		registerNew(member);
		
		final ConceptChangeProcessor processor = process();
		
		assertEquals(1, processor.getChangedMappings().size());
		final SnomedConceptDocument expected = doc(concept)
				.activeMemberOf(Collections.singleton(Concepts.REFSET_OWL_AXIOM))
				.memberOf(Collections.singleton(Concepts.REFSET_OWL_AXIOM))
				.build();
		final Revision actual = Iterables.getOnlyElement(processor.getChangedMappings().values());
		assertDocEquals(expected, actual);
		assertEquals(0, processor.getNewMappings().size());
		assertEquals(0, processor.getDeletions().size());
	}
	
	@Test
	public void newSubClassOfAxiom() throws Exception {
		final Concept concept = createConcept(generateConceptId());
		final Concept parentConcept = createConcept(generateConceptId());
		registerExistingObject(concept);
		registerExistingObject(parentConcept);
		indexRevision(MAIN, CDOIDUtil.getLong(concept.cdoID()), doc(concept).build());
		indexRevision(MAIN, CDOIDUtil.getLong(parentConcept.cdoID()), doc(parentConcept).build());
		
		statedChangedConceptIds.add(Long.parseLong(concept.getId()));
		statedChangedConceptIds.add(Long.parseLong(parentConcept.getId()));
		
		SnomedOWLExpressionRefSetMember member = createOwlAxiom(concept.getId(), String.format("SubClassOf(:%s :%s)", concept.getId(), parentConcept.getId()));
		registerNew(member);
		
		final ConceptChangeProcessor processor = process();
		
		assertEquals(1, processor.getChangedMappings().size());
		final SnomedConceptDocument expected = doc(concept)
				.statedParents(Long.parseLong(parentConcept.getId()))
				.statedAncestors(PrimitiveSets.newLongOpenHashSet(IComponent.ROOT_IDL))
				.activeMemberOf(Collections.singleton(Concepts.REFSET_OWL_AXIOM))
				.memberOf(Collections.singleton(Concepts.REFSET_OWL_AXIOM))
				.build();
		final Revision actual = Iterables.getOnlyElement(processor.getChangedMappings().values());
		assertDocEquals(expected, actual);
		assertEquals(0, processor.getNewMappings().size());
		assertEquals(0, processor.getDeletions().size());
	}
	
	@Test
	public void newSubClassOfAxiom_TwoTargets() throws Exception {
		final Concept concept = createConcept(generateConceptId());
		final Concept parentConcept = createConcept(generateConceptId());
		final Concept parentConcept2 = createConcept(generateConceptId());
		registerExistingObject(concept);
		registerExistingObject(parentConcept);
		registerExistingObject(parentConcept2);
		indexRevision(MAIN, CDOIDUtil.getLong(concept.cdoID()), doc(concept).build());
		indexRevision(MAIN, CDOIDUtil.getLong(parentConcept.cdoID()), doc(parentConcept).build());
		indexRevision(MAIN, CDOIDUtil.getLong(parentConcept2.cdoID()), doc(parentConcept2).build());
		
		statedChangedConceptIds.add(Long.parseLong(concept.getId()));
		statedChangedConceptIds.add(Long.parseLong(parentConcept.getId()));
		statedChangedConceptIds.add(Long.parseLong(parentConcept2.getId()));
		
		SnomedOWLExpressionRefSetMember member = createOwlAxiom(concept.getId(), String.format("SubClassOf(:%s ObjectIntersectionOf(:%s :%s))", concept.getId(), parentConcept.getId(), parentConcept2.getId()));
		registerNew(member);
		
		final ConceptChangeProcessor processor = process();
		
		assertEquals(1, processor.getChangedMappings().size());
		final SnomedConceptDocument expected = doc(concept)
				.statedParents(Long.parseLong(parentConcept.getId()), Long.parseLong(parentConcept2.getId()))
				.statedAncestors(PrimitiveSets.newLongOpenHashSet(IComponent.ROOT_IDL))
				.activeMemberOf(Collections.singleton(Concepts.REFSET_OWL_AXIOM))
				.memberOf(Collections.singleton(Concepts.REFSET_OWL_AXIOM))
				.build();
		final Revision actual = Iterables.getOnlyElement(processor.getChangedMappings().values());
		assertDocEquals(expected, actual);
		assertEquals(0, processor.getNewMappings().size());
		assertEquals(0, processor.getDeletions().size());
	}
	
	@Test
	public void updateEmptyAxiom() throws Exception {
		final Concept concept = createConcept(generateConceptId());
		final Concept parentConcept = createConcept(generateConceptId());
		SnomedOWLExpressionRefSetMember member = createOwlAxiom(concept.getId(), "");
		
		registerExistingObject(concept);
		registerExistingObject(parentConcept);
		indexRevision(MAIN, CDOIDUtil.getLong(concept.cdoID()), doc(concept)
				.activeMemberOf(Collections.singleton(Concepts.REFSET_OWL_AXIOM))
				.memberOf(Collections.singleton(Concepts.REFSET_OWL_AXIOM))
				.build());
		indexRevision(MAIN, CDOIDUtil.getLong(parentConcept.cdoID()), doc(parentConcept).build());
		indexRevision(MAIN, CDOIDUtil.getLong(member.cdoID()), doc(member).build());
		
		statedChangedConceptIds.add(Long.parseLong(concept.getId()));
		statedChangedConceptIds.add(Long.parseLong(parentConcept.getId()));
		
		// update axiom
		member.setOwlExpression(String.format("SubClassOf(:%s :%s)", concept.getId(), parentConcept.getId()));
		registerDirty(member);
		
		final ConceptChangeProcessor processor = process();
		
		assertEquals(1, processor.getChangedMappings().size());
		final SnomedConceptDocument expected = doc(concept)
				.statedParents(Long.parseLong(parentConcept.getId()))
				.statedAncestors(PrimitiveSets.newLongOpenHashSet(IComponent.ROOT_IDL))
				.activeMemberOf(Collections.singleton(Concepts.REFSET_OWL_AXIOM))
				.memberOf(Collections.singleton(Concepts.REFSET_OWL_AXIOM))
				.build();
		final Revision actual = Iterables.getOnlyElement(processor.getChangedMappings().values());
		assertDocEquals(expected, actual);
		assertEquals(0, processor.getNewMappings().size());
		assertEquals(0, processor.getDeletions().size());
	}
	
	@Test
	public void updateAxiomParent() throws Exception {
		final Concept concept = createConcept(generateConceptId());
		final Concept parentConcept = createConcept(generateConceptId());
		final Concept parentConcept2 = createConcept(generateConceptId());
		SnomedOWLExpressionRefSetMember member = createOwlAxiom(concept.getId(), String.format("SubClassOf(:%s :%s)", concept.getId(), parentConcept.getId()));
		
		registerExistingObject(concept);
		registerExistingObject(parentConcept);
		registerExistingObject(parentConcept2);
		
		indexRevision(MAIN, CDOIDUtil.getLong(concept.cdoID()), doc(concept)
				.statedParents(Long.parseLong(parentConcept.getId()))
				.statedAncestors(PrimitiveSets.newLongOpenHashSet(IComponent.ROOT_IDL))
				.activeMemberOf(Collections.singleton(Concepts.REFSET_OWL_AXIOM))
				.memberOf(Collections.singleton(Concepts.REFSET_OWL_AXIOM))
				.build());
		indexRevision(MAIN, CDOIDUtil.getLong(parentConcept.cdoID()), doc(parentConcept).build());
		indexRevision(MAIN, CDOIDUtil.getLong(parentConcept2.cdoID()), doc(parentConcept2).build());
		indexRevision(MAIN, CDOIDUtil.getLong(member.cdoID()), doc(member)
				.classAxiomRelationships(ImmutableList.of(new SnomedOWLRelationshipDocument(Concepts.IS_A, parentConcept.getId(), 0)))
				.build());
		
		statedChangedConceptIds.add(Long.parseLong(concept.getId()));
		statedChangedConceptIds.add(Long.parseLong(parentConcept.getId()));
		statedChangedConceptIds.add(Long.parseLong(parentConcept2.getId()));
		
		// update axiom
		member.setOwlExpression(String.format("SubClassOf(:%s :%s)", concept.getId(), parentConcept2.getId()));
		registerDirty(member);
		
		final ConceptChangeProcessor processor = process();
		
		assertEquals(1, processor.getChangedMappings().size());
		final SnomedConceptDocument expected = doc(concept)
				.statedParents(Long.parseLong(parentConcept2.getId()))
				.statedAncestors(PrimitiveSets.newLongOpenHashSet(IComponent.ROOT_IDL))
				.activeMemberOf(Collections.singleton(Concepts.REFSET_OWL_AXIOM))
				.memberOf(Collections.singleton(Concepts.REFSET_OWL_AXIOM))
				.build();
		final Revision actual = Iterables.getOnlyElement(processor.getChangedMappings().values());
		assertDocEquals(expected, actual);
		assertEquals(0, processor.getNewMappings().size());
		assertEquals(0, processor.getDeletions().size());
	}
	
	@Test
	public void deleteEmpty() throws Exception {
		final Concept concept = createConcept(generateConceptId());
		SnomedOWLExpressionRefSetMember member = createOwlAxiom(concept.getId(), "");
		registerExistingObject(concept);
		registerExistingObject(member);
		indexRevision(MAIN, CDOIDUtil.getLong(concept.cdoID()), doc(concept)
				.activeMemberOf(Collections.singleton(Concepts.REFSET_OWL_AXIOM))
				.memberOf(Collections.singleton(Concepts.REFSET_OWL_AXIOM))
				.build());
		indexRevision(MAIN, CDOIDUtil.getLong(member.cdoID()), doc(member).build());
		
		registerDetached(member.cdoID(), SnomedRefSetPackage.Literals.SNOMED_OWL_EXPRESSION_REF_SET_MEMBER);
		
		final ConceptChangeProcessor processor = process();
		
		assertEquals(1, processor.getChangedMappings().size());
		final SnomedConceptDocument expected = doc(concept).build();
		final Revision actual = Iterables.getOnlyElement(processor.getChangedMappings().values());
		assertDocEquals(expected, actual);
		assertEquals(0, processor.getNewMappings().size());
		assertEquals(0, processor.getDeletions().size());
	}
	
	@Test
	public void deleteSubClassOfAxiom() throws Exception {
		final Concept concept = createConcept(generateConceptId());
		final Concept parentConcept = createConcept(generateConceptId());
		SnomedOWLExpressionRefSetMember member = createOwlAxiom(concept.getId(), String.format("SubClassOf(:%s :%s)", concept.getId(), parentConcept.getId()));
		
		registerExistingObject(concept);
		registerExistingObject(parentConcept);
		registerExistingObject(member);
		
		indexRevision(MAIN, CDOIDUtil.getLong(concept.cdoID()), doc(concept)
				.statedParents(Long.parseLong(parentConcept.getId()))
				.statedAncestors(PrimitiveSets.newLongOpenHashSet(IComponent.ROOT_IDL))
				.build());
		indexRevision(MAIN, CDOIDUtil.getLong(parentConcept.cdoID()), doc(parentConcept).build());
		indexRevision(MAIN, CDOIDUtil.getLong(member.cdoID()), doc(member)
				.classAxiomRelationships(ImmutableList.of(new SnomedOWLRelationshipDocument(Concepts.IS_A, parentConcept.getId(), 0)))
				.build());
		
		statedChangedConceptIds.add(Long.parseLong(concept.getId()));
		statedChangedConceptIds.add(Long.parseLong(parentConcept.getId()));
		
		registerDetached(member.cdoID(), SnomedRefSetPackage.Literals.SNOMED_OWL_EXPRESSION_REF_SET_MEMBER);
		
		final ConceptChangeProcessor processor = process();
		
		assertEquals(1, processor.getChangedMappings().size());
		final SnomedConceptDocument expected = doc(concept)
				.statedParents(PrimitiveSets.newLongOpenHashSet(IComponent.ROOT_IDL))
				.build();
		final Revision actual = Iterables.getOnlyElement(processor.getChangedMappings().values());
		assertDocEquals(expected, actual);
		assertEquals(0, processor.getNewMappings().size());
		assertEquals(0, processor.getDeletions().size());
	}
	
	@Test
	public void deleteSubClassOfAxiom_TwoTargets() throws Exception {
		final Concept concept = createConcept(generateConceptId());
		final Concept parentConcept = createConcept(generateConceptId());
		final Concept parentConcept2 = createConcept(generateConceptId());
		SnomedOWLExpressionRefSetMember member = createOwlAxiom(concept.getId(), String.format("SubClassOf(:%s ObjectIntersectionOf(:%s :%s))", concept.getId(), parentConcept.getId(), parentConcept2.getId()));
		
		registerExistingObject(concept);
		registerExistingObject(parentConcept);
		registerExistingObject(parentConcept2);
		registerExistingObject(member);
		
		indexRevision(MAIN, CDOIDUtil.getLong(concept.cdoID()), doc(concept)
				.statedParents(Long.parseLong(parentConcept.getId()))
				.statedAncestors(PrimitiveSets.newLongOpenHashSet(IComponent.ROOT_IDL))
				.build());
		indexRevision(MAIN, CDOIDUtil.getLong(parentConcept.cdoID()), doc(parentConcept).build());
		indexRevision(MAIN, CDOIDUtil.getLong(parentConcept2.cdoID()), doc(parentConcept2).build());
		indexRevision(MAIN, CDOIDUtil.getLong(member.cdoID()), doc(member)
				.classAxiomRelationships(ImmutableList.of(new SnomedOWLRelationshipDocument(Concepts.IS_A, parentConcept.getId(), 0)))
				.build());
		
		statedChangedConceptIds.add(Long.parseLong(concept.getId()));
		statedChangedConceptIds.add(Long.parseLong(parentConcept.getId()));
		
		registerDetached(member.cdoID(), SnomedRefSetPackage.Literals.SNOMED_OWL_EXPRESSION_REF_SET_MEMBER);
		
		final ConceptChangeProcessor processor = process();
		
		assertEquals(1, processor.getChangedMappings().size());
		final SnomedConceptDocument expected = doc(concept)
				.statedParents(PrimitiveSets.newLongOpenHashSet(IComponent.ROOT_IDL))
				.build();
		final Revision actual = Iterables.getOnlyElement(processor.getChangedMappings().values());
		assertDocEquals(expected, actual);
		assertEquals(0, processor.getNewMappings().size());
		assertEquals(0, processor.getDeletions().size());
	}
	
	@Test
	public void inactivateIsa_AddSubClassOf() throws Exception {
		final Concept concept = createConcept(generateConceptId());
		final Concept parentConcept = createConcept(generateConceptId());
		Relationship isaRelationship = createStatedRelationship(concept.getId(), Concepts.IS_A, parentConcept.getId());
		SnomedOWLExpressionRefSetMember member = createOwlAxiom(concept.getId(), String.format("SubClassOf(:%s :%s)", concept.getId(), parentConcept.getId()));

		registerExistingObject(concept);
		indexRevision(MAIN, CDOIDUtil.getLong(concept.cdoID()), doc(concept)
				.statedParents(Long.parseLong(parentConcept.getId()))
				.statedAncestors(PrimitiveSets.newLongOpenHashSet(IComponent.ROOT_IDL))
				.build());
		
		registerExistingObject(parentConcept);
		indexRevision(MAIN, CDOIDUtil.getLong(parentConcept.cdoID()), doc(parentConcept).build());
		registerExistingObject(isaRelationship);
		indexRevision(MAIN, CDOIDUtil.getLong(isaRelationship.cdoID()), SnomedRelationshipIndexEntry.builder(isaRelationship).build());
		
		statedChangedConceptIds.add(Long.parseLong(concept.getId()));
		statedChangedConceptIds.add(Long.parseLong(parentConcept.getId()));
		
		isaRelationship.setActive(false);
		registerSetRevisionDelta(isaRelationship, SnomedPackage.Literals.COMPONENT__ACTIVE, true, false);
		registerDirty(isaRelationship);
		registerNew(member);
		
		final ConceptChangeProcessor processor = process();
		
		assertEquals(1, processor.getChangedMappings().size());
		final SnomedConceptDocument expected = doc(concept)
				.statedParents(Long.parseLong(parentConcept.getId()))
				.statedAncestors(PrimitiveSets.newLongOpenHashSet(IComponent.ROOT_IDL))
				.activeMemberOf(Collections.singleton(Concepts.REFSET_OWL_AXIOM))
				.memberOf(Collections.singleton(Concepts.REFSET_OWL_AXIOM))
				.build();
		final Revision actual = Iterables.getOnlyElement(processor.getChangedMappings().values());
		assertDocEquals(expected, actual);
		
		assertEquals(0, processor.getNewMappings().size());
		assertEquals(0, processor.getDeletions().size());
	}
	
	@Test
	public void activateIsa_InactivateOwlMember() throws Exception {
		final Concept concept = createConcept(generateConceptId());
		final Concept parentConcept = createConcept(generateConceptId());
		Relationship isaRelationship = createStatedRelationship(concept.getId(), Concepts.IS_A, parentConcept.getId());
		isaRelationship.setActive(false);
		
		SnomedOWLExpressionRefSetMember member = createOwlAxiom(concept.getId(), String.format("SubClassOf(:%s :%s)", concept.getId(), parentConcept.getId()));
		
		registerExistingObject(concept);
		indexRevision(MAIN, CDOIDUtil.getLong(concept.cdoID()), doc(concept)
				.statedParents(Long.parseLong(parentConcept.getId()))
				.statedAncestors(PrimitiveSets.newLongOpenHashSet(IComponent.ROOT_IDL))
				.activeMemberOf(Collections.singleton(Concepts.REFSET_OWL_AXIOM))
				.memberOf(Collections.singleton(Concepts.REFSET_OWL_AXIOM))
				.build());
		registerExistingObject(parentConcept);
		indexRevision(MAIN, CDOIDUtil.getLong(parentConcept.cdoID()), doc(parentConcept).build());
		registerExistingObject(isaRelationship);
		indexRevision(MAIN, CDOIDUtil.getLong(isaRelationship.cdoID()), SnomedRelationshipIndexEntry.builder(isaRelationship).build());
		registerExistingObject(member);
		indexRevision(MAIN, CDOIDUtil.getLong(member.cdoID()), doc(member)
				.classAxiomRelationships(ImmutableList.of(new SnomedOWLRelationshipDocument(Concepts.IS_A, parentConcept.getId(), 0)))
				.build());
		
		statedChangedConceptIds.add(Long.parseLong(concept.getId()));
		statedChangedConceptIds.add(Long.parseLong(parentConcept.getId()));
		
		isaRelationship.setActive(true);
		registerDirty(isaRelationship);
		registerSetRevisionDelta(isaRelationship, SnomedPackage.Literals.COMPONENT__ACTIVE, false, true);
		member.setActive(false);
		registerDirty(member);
		registerSetRevisionDelta(member, SnomedRefSetPackage.Literals.SNOMED_REF_SET_MEMBER__ACTIVE, true, false);
		
		final ConceptChangeProcessor processor = process();
		
		assertEquals(1, processor.getChangedMappings().size());
		final SnomedConceptDocument expected = doc(concept)
				.statedParents(Long.parseLong(parentConcept.getId()))
				.statedAncestors(PrimitiveSets.newLongOpenHashSet(IComponent.ROOT_IDL))
				.memberOf(Collections.singleton(Concepts.REFSET_OWL_AXIOM))
				.build();
		final Revision actual = Iterables.getOnlyElement(processor.getChangedMappings().values());
		assertDocEquals(expected, actual);
		
		assertEquals(0, processor.getNewMappings().size());
		assertEquals(0, processor.getDeletions().size());
	}
	
}
