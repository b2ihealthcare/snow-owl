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
package com.b2international.snowowl.snomed.datastore.index.change;

import static com.b2international.snowowl.snomed.datastore.id.RandomSnomedIdentiferGenerator.generateConceptId;
import static com.b2international.snowowl.snomed.datastore.id.RandomSnomedIdentiferGenerator.generateDescriptionId;
import static com.google.common.collect.Sets.newHashSet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.junit.Test;

import com.b2international.collections.PrimitiveSets;
import com.b2international.collections.longs.LongSet;
import com.b2international.index.revision.Revision;
import com.b2international.index.revision.RevisionIndexRead;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.core.domain.IComponent;
import com.b2international.snowowl.datastore.ICDOCommitChangeSet;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.Description;
import com.b2international.snowowl.snomed.Relationship;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.SnomedFactory;
import com.b2international.snowowl.snomed.SnomedPackage;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.domain.Acceptability;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument.Builder;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionFragment;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;
import com.b2international.snowowl.snomed.datastore.taxonomy.Taxonomies;
import com.b2international.snowowl.snomed.datastore.taxonomy.Taxonomy;
import com.b2international.snowowl.snomed.snomedrefset.SnomedLanguageRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

/**
 * @since 4.7
 */
public class ConceptChangeProcessorTest extends BaseChangeProcessorTest {

	private Collection<String> availableImages = newHashSet(Concepts.ROOT_CONCEPT, Concepts.MODULE_ROOT, Concepts.NAMESPACE_ROOT);
	private LongSet statedChangedConceptIds = PrimitiveSets.newLongOpenHashSet();
	private LongSet inferredChangedConceptIds = PrimitiveSets.newLongOpenHashSet();
	
	private ConceptChangeProcessor process() {
		return index().read(MAIN, new RevisionIndexRead<ConceptChangeProcessor>() {
			@Override
			public ConceptChangeProcessor execute(RevisionSearcher searcher) throws IOException {
				final ICDOCommitChangeSet commitChangeSet = createChangeSet();
				final Taxonomy inferredTaxonomy = Taxonomies.inferred(searcher, commitChangeSet, inferredChangedConceptIds, true);
				final Taxonomy statedTaxonomy = Taxonomies.stated(searcher, commitChangeSet, statedChangedConceptIds, true);
				final ConceptChangeProcessor processor = new ConceptChangeProcessor(DoiData.DEFAULT_SCORE, availableImages, statedTaxonomy, inferredTaxonomy);
				processor.process(commitChangeSet, searcher);
				return processor;
			}
		});
	}
	
	@Test
	public void indexSingleConcept() throws Exception {
		final Concept concept = createConcept(generateConceptId());
		registerNew(concept);
		
		final ConceptChangeProcessor processor = process();
		
		final SnomedConceptDocument expected = doc(concept).build();
		final Revision actual = Iterables.getOnlyElement(processor.getNewMappings());
		assertDocEquals(expected, actual);
		assertEquals(0, processor.getChangedMappings().size());
		assertEquals(0, processor.getDeletions().size());
	}
	
	@Test
	public void indexConceptWithSingleFsn() throws Exception {
		final Concept concept = createConcept(generateConceptId());
		registerNew(concept);
		final Description description = createFsnWithTwoAcceptabilityMembers();
		concept.getDescriptions().add(description);
		
		final ConceptChangeProcessor processor = process();
		
		final SnomedConceptDocument expected = doc(concept)
				.preferredDescriptions(ImmutableList.of(
					new SnomedDescriptionFragment(description.getId(), CDOIDUtil.getLong(description.cdoID()), Concepts.FULLY_SPECIFIED_NAME, description.getTerm(), Concepts.REFSET_LANGUAGE_TYPE_UK)
				))
				.build();
		final Revision actual = Iterables.getOnlyElement(processor.getNewMappings());
		assertDocEquals(expected, actual);
		assertEquals(0, processor.getChangedMappings().size());
		assertEquals(0, processor.getDeletions().size());
	}
	
	@Test
	public void indexConceptWithFsnAndSynonym() throws Exception {
		final Concept concept = createConcept(generateConceptId());
		registerNew(concept);
		final Description fsn = createFsnWithTwoAcceptabilityMembers();
		concept.getDescriptions().add(fsn);
		final Description synonym = createFsnWithTwoAcceptabilityMembers();
		synonym.setType(getConcept(Concepts.SYNONYM));
		concept.getDescriptions().add(synonym);
		
		final ConceptChangeProcessor processor = process();
		
		final SnomedConceptDocument expected = doc(concept)
				.preferredDescriptions(ImmutableList.of(
					new SnomedDescriptionFragment(fsn.getId(), CDOIDUtil.getLong(fsn.cdoID()), Concepts.FULLY_SPECIFIED_NAME, fsn.getTerm(), Concepts.REFSET_LANGUAGE_TYPE_UK),
					new SnomedDescriptionFragment(synonym.getId(), CDOIDUtil.getLong(synonym.cdoID()), Concepts.SYNONYM, synonym.getTerm(), Concepts.REFSET_LANGUAGE_TYPE_UK)
				))
				.build();
		final Revision actual = Iterables.getOnlyElement(processor.getNewMappings());
		assertDocEquals(expected, actual);
		assertEquals(0, processor.getChangedMappings().size());
		assertEquals(0, processor.getDeletions().size());
	}
	
	@Test
	public void indexConceptWithFsnAndDefinition() throws Exception {
		final Concept concept = createConcept(generateConceptId());
		registerNew(concept);
		final Description fsn = createFsnWithTwoAcceptabilityMembers();
		concept.getDescriptions().add(fsn);
		final Description textDefinition = createFsnWithTwoAcceptabilityMembers();
		textDefinition.setType(getConcept(Concepts.TEXT_DEFINITION));
		concept.getDescriptions().add(textDefinition);
		
		final ConceptChangeProcessor processor = process();
		
		final SnomedConceptDocument expected = doc(concept)
				.preferredDescriptions(ImmutableList.of(
					new SnomedDescriptionFragment(fsn.getId(), CDOIDUtil.getLong(fsn.cdoID()), Concepts.FULLY_SPECIFIED_NAME, fsn.getTerm(), Concepts.REFSET_LANGUAGE_TYPE_UK)
				))
				.build();
		final Revision actual = Iterables.getOnlyElement(processor.getNewMappings());
		assertDocEquals(expected, actual);
		assertEquals(0, processor.getChangedMappings().size());
		assertEquals(0, processor.getDeletions().size());
	}
	
	@Test
	public void indexNewFsnForExistingConcept() throws Exception {
		final Concept concept = createConcept(generateConceptId());
		registerExistingObject(concept);
		final long conceptStorageKey = CDOIDUtil.getLong(concept.cdoID());
		indexRevision(MAIN, doc(concept).storageKey(conceptStorageKey).build());
		
		final Concept changedConcept = createConcept(concept.getId());
		withCDOID(changedConcept, conceptStorageKey);
		final Description fsn = createFsnWithTwoAcceptabilityMembers();
		changedConcept.getDescriptions().add(fsn);
		registerNew(fsn);
		registerDirty(changedConcept);
		registerAddRevisionDelta(changedConcept, SnomedPackage.Literals.CONCEPT__DESCRIPTIONS, 0, fsn);
		
		final ConceptChangeProcessor processor = process();
		
		final SnomedConceptDocument expected = doc(concept)
				.preferredDescriptions(ImmutableList.of(
					new SnomedDescriptionFragment(fsn.getId(), CDOIDUtil.getLong(fsn.cdoID()), Concepts.FULLY_SPECIFIED_NAME, fsn.getTerm(), Concepts.REFSET_LANGUAGE_TYPE_UK)
				))
				.build();
		final Revision actual = Iterables.getOnlyElement(processor.getChangedMappings());
		assertDocEquals(expected, actual);
		assertEquals(0, processor.getNewMappings().size());
		assertEquals(0, processor.getDeletions().size());
	}
	
	@Test
	public void indexNewSynonymForExistingConcept() throws Exception {
		final Concept concept = createConcept(generateConceptId());
		registerExistingObject(concept);
		final long conceptStorageKey = CDOIDUtil.getLong(concept.cdoID());
		indexRevision(MAIN, doc(concept).storageKey(conceptStorageKey).build());
		
		final Concept changedConcept = createConcept(concept.getId());
		withCDOID(changedConcept, conceptStorageKey);
		final Description synonym = createFsnWithTwoAcceptabilityMembers();
		synonym.setType(getConcept(Concepts.SYNONYM));
		changedConcept.getDescriptions().add(synonym);
		registerNew(synonym);
		registerDirty(changedConcept);
		registerAddRevisionDelta(changedConcept, SnomedPackage.Literals.CONCEPT__DESCRIPTIONS, 0, synonym);
		
		final ConceptChangeProcessor processor = process();
		
		final SnomedConceptDocument expected = doc(concept)
				.preferredDescriptions(ImmutableList.of(
					new SnomedDescriptionFragment(synonym.getId(), CDOIDUtil.getLong(synonym.cdoID()), Concepts.SYNONYM, synonym.getTerm(), Concepts.REFSET_LANGUAGE_TYPE_UK)
				))
				.build();
		final Revision actual = Iterables.getOnlyElement(processor.getChangedMappings());
		assertDocEquals(expected, actual);
		assertEquals(0, processor.getNewMappings().size());
		assertEquals(0, processor.getDeletions().size());
	}
	
	@Test
	public void indexNewTextDefinitionForExistingConcept() throws Exception {
		final Concept concept = createConcept(generateConceptId());
		registerExistingObject(concept);
		final long conceptStorageKey = CDOIDUtil.getLong(concept.cdoID());
		indexRevision(MAIN, doc(concept).storageKey(conceptStorageKey).build());
		
		final Concept changedConcept = createConcept(concept.getId());
		withCDOID(changedConcept, conceptStorageKey);
		final Description textDefinition = createFsnWithTwoAcceptabilityMembers();
		textDefinition.setType(getConcept(Concepts.TEXT_DEFINITION));
		changedConcept.getDescriptions().add(textDefinition);
		registerNew(textDefinition);
		registerDirty(changedConcept);
		registerAddRevisionDelta(changedConcept, SnomedPackage.Literals.CONCEPT__DESCRIPTIONS, 0, textDefinition);
		
		final ConceptChangeProcessor processor = process();
		
		final SnomedConceptDocument expected = doc(concept).build();
		final Revision actual = Iterables.getOnlyElement(processor.getChangedMappings());
		assertDocEquals(expected, actual);
		assertEquals(0, processor.getNewMappings().size());
		assertEquals(0, processor.getDeletions().size());
	}
	
	@Test
	public void removeFsnFromExistingConcept() throws Exception {
		final Concept concept = createConcept(generateConceptId());
		final long conceptStorageKey = CDOIDUtil.getLong(concept.cdoID());
		registerExistingObject(concept);
		indexRevision(MAIN, doc(concept)
				.storageKey(conceptStorageKey)
				.preferredDescriptions(ImmutableList.of(
					new SnomedDescriptionFragment(generateDescriptionId(), nextStorageKey(), Concepts.FULLY_SPECIFIED_NAME, "Term", Concepts.REFSET_LANGUAGE_TYPE_UK)
				))
				.build());
		
		registerDirty(concept);
		registerRemoveRevisionDelta(concept, SnomedPackage.Literals.CONCEPT__DESCRIPTIONS, 0);
		
		final ConceptChangeProcessor processor = process();
		
		final SnomedConceptDocument expected = doc(concept).build();
		final Revision actual = Iterables.getOnlyElement(processor.getChangedMappings());
		assertDocEquals(expected, actual);
		assertEquals(0, processor.getNewMappings().size());
		assertEquals(0, processor.getDeletions().size());
	}
	
	@Test
	public void removeSynonymFromExistingConcept() throws Exception {
		final Concept concept = createConcept(generateConceptId());
		final long conceptStorageKey = CDOIDUtil.getLong(concept.cdoID());
		registerExistingObject(concept);
		indexRevision(MAIN, doc(concept)
				.storageKey(conceptStorageKey)
				.preferredDescriptions(ImmutableList.of(
					new SnomedDescriptionFragment(generateDescriptionId(), nextStorageKey(), Concepts.SYNONYM, "Hello", Concepts.REFSET_LANGUAGE_TYPE_UK)
				))
				.build());
		
		registerDirty(concept);
		registerRemoveRevisionDelta(concept, SnomedPackage.Literals.CONCEPT__DESCRIPTIONS, 0);
		
		final ConceptChangeProcessor processor = process();
		
		final SnomedConceptDocument expected = doc(concept).build();
		final Revision actual = Iterables.getOnlyElement(processor.getChangedMappings());
		assertDocEquals(expected, actual);
		assertEquals(0, processor.getNewMappings().size());
		assertEquals(0, processor.getDeletions().size());
	}
	
	@Test
	public void updateTermOfConceptFsn() throws Exception {
		final Concept concept = createConcept(generateConceptId());
		final long conceptStorageKey = CDOIDUtil.getLong(concept.cdoID());
		Description fsn = createFsnWithTwoAcceptabilityMembers();
		fsn.setConcept(concept);
		registerExistingObject(concept);
		registerExistingObject(fsn);
		
		indexRevision(MAIN, doc(concept)
				.storageKey(conceptStorageKey)
				.preferredDescriptions(ImmutableList.of(
					new SnomedDescriptionFragment(fsn.getId(), CDOIDUtil.getLong(fsn.cdoID()), Concepts.FULLY_SPECIFIED_NAME, fsn.getTerm(), Concepts.REFSET_LANGUAGE_TYPE_UK)
				))
				.build());
		
		fsn.setTerm("Term (changed)");
		registerDirty(fsn);
		registerSetRevisionDelta(fsn, SnomedPackage.Literals.DESCRIPTION__TERM, "Term", fsn.getTerm());
		
		final ConceptChangeProcessor processor = process();
		
		final SnomedConceptDocument expected = doc(concept)
				.preferredDescriptions(ImmutableList.of(
					new SnomedDescriptionFragment(fsn.getId(), CDOIDUtil.getLong(fsn.cdoID()), Concepts.FULLY_SPECIFIED_NAME, fsn.getTerm(), Concepts.REFSET_LANGUAGE_TYPE_UK)
				))
				.build();
		final Revision actual = Iterables.getOnlyElement(processor.getChangedMappings());
		assertDocEquals(expected, actual);
		assertEquals(0, processor.getNewMappings().size());
		assertEquals(0, processor.getDeletions().size());
	}
	
	@Test
	public void inactivateFsnOfConcept() throws Exception {
		final Concept concept = createConcept(generateConceptId());
		final long conceptStorageKey = CDOIDUtil.getLong(concept.cdoID());
		Description fsn = createFsnWithTwoAcceptabilityMembers();
		fsn.setConcept(concept);
		registerExistingObject(concept);
		registerExistingObject(fsn);
		
		indexRevision(MAIN, doc(concept)
				.storageKey(conceptStorageKey)
				.preferredDescriptions(ImmutableList.of(
					new SnomedDescriptionFragment(fsn.getId(), CDOIDUtil.getLong(fsn.cdoID()), Concepts.FULLY_SPECIFIED_NAME, fsn.getTerm(), Concepts.REFSET_LANGUAGE_TYPE_UK)
				))
				.build());
		
		fsn.setActive(false);
		registerDirty(fsn);
		registerSetRevisionDelta(fsn, SnomedPackage.Literals.COMPONENT__ACTIVE, true, false);
		
		final ConceptChangeProcessor processor = process();
		
		final SnomedConceptDocument expected = doc(concept).build();
		final Revision actual = Iterables.getOnlyElement(processor.getChangedMappings());
		assertDocEquals(expected, actual);
		assertEquals(0, processor.getNewMappings().size());
		assertEquals(0, processor.getDeletions().size());
	}
	
	@Test
	public void updateCaseSignificanceOfConceptDescription() throws Exception {
		final Concept concept = createConcept(generateConceptId());
		final long conceptStorageKey = CDOIDUtil.getLong(concept.cdoID());
		Description fsn = createFsnWithTwoAcceptabilityMembers();
		fsn.setConcept(concept);
		registerExistingObject(concept);
		registerExistingObject(fsn);
		
		indexRevision(MAIN, doc(concept)
				.storageKey(conceptStorageKey)
				.preferredDescriptions(ImmutableList.of(
					new SnomedDescriptionFragment(fsn.getId(), CDOIDUtil.getLong(fsn.cdoID()), Concepts.FULLY_SPECIFIED_NAME, fsn.getTerm(), Concepts.REFSET_LANGUAGE_TYPE_UK)
				))
				.build());
		
		registerDirty(fsn);
		registerSetRevisionDelta(fsn, SnomedPackage.Literals.DESCRIPTION__CASE_SIGNIFICANCE, null /*unused*/, null /*unused*/);
		
		final ConceptChangeProcessor processor = process();
		
		assertEquals(0, processor.getNewMappings().size());
		assertEquals(0, processor.getChangedMappings().size());
		assertEquals(0, processor.getDeletions().size());
	}
	
	@Test
	public void updateTypeOfConceptDescription() throws Exception {
		final Concept concept = createConcept(generateConceptId());
		final long conceptStorageKey = CDOIDUtil.getLong(concept.cdoID());
		Description description = createFsnWithTwoAcceptabilityMembers();
		description.setConcept(concept);
		registerExistingObject(concept);
		registerExistingObject(description);
		
		indexRevision(MAIN, doc(concept)
				.storageKey(conceptStorageKey)
				.preferredDescriptions(ImmutableList.of(
					new SnomedDescriptionFragment(description.getId(), CDOIDUtil.getLong(description.cdoID()), Concepts.FULLY_SPECIFIED_NAME, description.getTerm(), Concepts.REFSET_LANGUAGE_TYPE_UK)
				))
				.build());
		
		description.setType(getConcept(Concepts.SYNONYM));
		registerDirty(description);
		registerSetRevisionDelta(description, SnomedPackage.Literals.DESCRIPTION__TYPE, null /*unused*/, null /*unused*/);
		
		final ConceptChangeProcessor processor = process();
		
		final SnomedConceptDocument expected = doc(concept)
				.preferredDescriptions(ImmutableList.of(
					new SnomedDescriptionFragment(description.getId(), CDOIDUtil.getLong(description.cdoID()), Concepts.SYNONYM, description.getTerm(), Concepts.REFSET_LANGUAGE_TYPE_UK)
				))
				.build();
		final Revision actual = Iterables.getOnlyElement(processor.getChangedMappings());
		assertDocEquals(expected, actual);
		assertEquals(0, processor.getNewMappings().size());
		assertEquals(0, processor.getDeletions().size());
	}
	
	@Test
	public void addAcceptabilityMemberToConceptDescription() throws Exception {
		final Concept concept = createConcept(generateConceptId());
		final long conceptStorageKey = CDOIDUtil.getLong(concept.cdoID());
		Description description = createFsnWithTwoAcceptabilityMembers();
		description.setConcept(concept);
		registerExistingObject(concept);
		registerExistingObject(description);
		
		indexRevision(MAIN, doc(concept)
				.storageKey(conceptStorageKey)
				.preferredDescriptions(ImmutableList.of(
					new SnomedDescriptionFragment(description.getId(), CDOIDUtil.getLong(description.cdoID()), Concepts.FULLY_SPECIFIED_NAME, description.getTerm(), Concepts.REFSET_LANGUAGE_TYPE_UK)
				))
				.build());
		
		final SnomedLanguageRefSetMember newMember = createLangMember(description.getId(), Acceptability.PREFERRED, Concepts.REFSET_LANGUAGE_TYPE_US);
		description.getLanguageRefSetMembers().add(newMember);
		registerNew(newMember);
		registerDirty(description);
		registerAddRevisionDelta(description, SnomedPackage.Literals.DESCRIPTION__LANGUAGE_REF_SET_MEMBERS, 1, newMember);
		
		final ConceptChangeProcessor processor = process();
		
		final SnomedConceptDocument expected = doc(concept)
				.preferredDescriptions(ImmutableList.of(
					new SnomedDescriptionFragment(description.getId(), CDOIDUtil.getLong(description.cdoID()), Concepts.FULLY_SPECIFIED_NAME, description.getTerm(), ImmutableList.of(Concepts.REFSET_LANGUAGE_TYPE_UK, Concepts.REFSET_LANGUAGE_TYPE_US))
				))
				.build();
		final Revision actual = Iterables.getOnlyElement(processor.getChangedMappings());
		assertDocEquals(expected, actual);
		assertEquals(0, processor.getNewMappings().size());
		assertEquals(0, processor.getDeletions().size());
	}
	
	@Test
	public void inactivateAcceptabilityMemberOfConceptDescription() throws Exception {
		final Concept concept = createConcept(generateConceptId());
		final long conceptStorageKey = CDOIDUtil.getLong(concept.cdoID());
		Description description = createFsnWithTwoAcceptabilityMembers();
		SnomedLanguageRefSetMember memberToInactivate = createLangMember(description.getId(), Acceptability.PREFERRED, Concepts.REFSET_LANGUAGE_TYPE_US);
		description.getLanguageRefSetMembers().add(memberToInactivate);
		description.setConcept(concept);
		registerExistingObject(concept);
		registerExistingObject(description);
		registerExistingObject(memberToInactivate);
		
		indexRevision(MAIN, doc(concept)
				.storageKey(conceptStorageKey)
				.preferredDescriptions(ImmutableList.of(
					new SnomedDescriptionFragment(description.getId(), CDOIDUtil.getLong(description.cdoID()), Concepts.FULLY_SPECIFIED_NAME, description.getTerm(), ImmutableList.of(Concepts.REFSET_LANGUAGE_TYPE_UK, Concepts.REFSET_LANGUAGE_TYPE_US))
				))
				.build());
		
		memberToInactivate.setActive(false);
		registerDirty(memberToInactivate);
		registerSetRevisionDelta(memberToInactivate, SnomedRefSetPackage.Literals.SNOMED_REF_SET_MEMBER__ACTIVE, true, false);
		
		final ConceptChangeProcessor processor = process();
		
		final SnomedConceptDocument expected = doc(concept)
				.preferredDescriptions(ImmutableList.of(
					new SnomedDescriptionFragment(description.getId(), CDOIDUtil.getLong(description.cdoID()), Concepts.FULLY_SPECIFIED_NAME, description.getTerm(), Concepts.REFSET_LANGUAGE_TYPE_UK)
				))
				.build();
		final Revision actual = Iterables.getOnlyElement(processor.getChangedMappings());
		assertDocEquals(expected, actual);
		assertEquals(0, processor.getNewMappings().size());
		assertEquals(0, processor.getDeletions().size());
	}
	
	@Test
	public void changeAcceptabilityOfConceptDescription() throws Exception {
		final Concept concept = createConcept(generateConceptId());
		final long conceptStorageKey = CDOIDUtil.getLong(concept.cdoID());
		Description description = createFsnWithTwoAcceptabilityMembers();
		SnomedLanguageRefSetMember memberToChange = createLangMember(description.getId(), Acceptability.PREFERRED, Concepts.REFSET_LANGUAGE_TYPE_US);
		description.getLanguageRefSetMembers().add(memberToChange);
		description.setConcept(concept);
		registerExistingObject(concept);
		registerExistingObject(description);
		registerExistingObject(memberToChange);
		
		indexRevision(MAIN, doc(concept)
				.storageKey(conceptStorageKey)
				.preferredDescriptions(ImmutableList.of(
					new SnomedDescriptionFragment(description.getId(), CDOIDUtil.getLong(description.cdoID()), Concepts.FULLY_SPECIFIED_NAME, description.getTerm(), ImmutableList.of(Concepts.REFSET_LANGUAGE_TYPE_UK, Concepts.REFSET_LANGUAGE_TYPE_US))
				))
				.build());
		
		memberToChange.setAcceptabilityId(Concepts.REFSET_DESCRIPTION_ACCEPTABILITY_ACCEPTABLE);
		registerDirty(memberToChange);
		registerSetRevisionDelta(memberToChange, SnomedRefSetPackage.Literals.SNOMED_LANGUAGE_REF_SET_MEMBER__ACCEPTABILITY_ID, null /*unused*/, null /*unused*/);
		
		final ConceptChangeProcessor processor = process();
		
		final SnomedConceptDocument expected = doc(concept)
				.preferredDescriptions(ImmutableList.of(
					new SnomedDescriptionFragment(description.getId(), CDOIDUtil.getLong(description.cdoID()), Concepts.FULLY_SPECIFIED_NAME, description.getTerm(), Concepts.REFSET_LANGUAGE_TYPE_UK)
				))
				.build();
		final Revision actual = Iterables.getOnlyElement(processor.getChangedMappings());
		assertDocEquals(expected, actual);
		assertEquals(0, processor.getNewMappings().size());
		assertEquals(0, processor.getDeletions().size());
	}
	
	@Test
	public void indexNewStatedChildConceptOfRoot() throws Exception {
		// index the ROOT concept as existing concept
		final long rootConceptId = Long.parseLong(Concepts.ROOT_CONCEPT);
		statedChangedConceptIds.add(rootConceptId);
		
		final Concept concept = createConcept(generateConceptId());
		availableImages.add(concept.getId());
		registerNew(concept);
		
		final Relationship relationship = createStatedRelationship(concept.getId(), Concepts.IS_A, Concepts.ROOT_CONCEPT);
		concept.getOutboundRelationships().add(relationship);
		registerNew(relationship);
		
		final ConceptChangeProcessor processor = process();
		
		final SnomedConceptDocument expected = doc(concept)
				.iconId(concept.getId())
				.statedParents(PrimitiveSets.newLongOpenHashSet(rootConceptId))
				.statedAncestors(PrimitiveSets.newLongOpenHashSet(IComponent.ROOT_IDL))
				.build();
		final Revision actual = Iterables.getOnlyElement(processor.getNewMappings());
		assertDocEquals(expected, actual);
		assertEquals(0, processor.getChangedMappings().size());
		assertEquals(0, processor.getDeletions().size());
	}
	
	@Test
	public void indexNewStatedAndInferredChildConceptOfRoot() throws Exception {
		// index the ROOT concept as existing concept
		final long rootConceptId = Long.parseLong(Concepts.ROOT_CONCEPT);
		statedChangedConceptIds.add(rootConceptId);
		inferredChangedConceptIds.add(rootConceptId);
		
		final Concept concept = createConcept(generateConceptId());
		registerNew(concept);
		
		final Relationship statedRelationship = createStatedRelationship(concept.getId(), Concepts.IS_A, Concepts.ROOT_CONCEPT);
		concept.getOutboundRelationships().add(statedRelationship);
		registerNew(statedRelationship);
		
		final Relationship inferredRelationship = createInferredRelationship(concept.getId(), Concepts.IS_A, Concepts.ROOT_CONCEPT);
		concept.getOutboundRelationships().add(inferredRelationship);
		registerNew(inferredRelationship);
		
		final ConceptChangeProcessor processor = process();
		
		final SnomedConceptDocument expected = doc(concept)
				.parents(PrimitiveSets.newLongOpenHashSet(rootConceptId))
				.ancestors(PrimitiveSets.newLongOpenHashSet(IComponent.ROOT_IDL))
				.statedParents(PrimitiveSets.newLongOpenHashSet(rootConceptId))
				.statedAncestors(PrimitiveSets.newLongOpenHashSet(IComponent.ROOT_IDL))
				.build();
		final Revision actual = Iterables.getOnlyElement(processor.getNewMappings());
		assertDocEquals(expected, actual);
		assertEquals(0, processor.getChangedMappings().size());
		assertEquals(0, processor.getDeletions().size());
	}
	
	@Test
	public void updateStatedRelationshipDestination() throws Exception {
		// index the ROOT concept as existing concept
		final long rootConceptId = Long.parseLong(Concepts.ROOT_CONCEPT);
		final Concept concept1 = createConcept(generateConceptId());
		final Concept concept2 = createConcept(Concepts.NAMESPACE_ROOT);
		
		final long concept1Id = Long.parseLong(concept1.getId());
		final long concept2Id = Long.parseLong(concept2.getId());
		
		statedChangedConceptIds.add(rootConceptId);
		statedChangedConceptIds.add(concept1Id);
		statedChangedConceptIds.add(concept2Id);
		
		final Relationship statedRelationship = createStatedRelationship(concept1.getId(), Concepts.IS_A, Concepts.ROOT_CONCEPT);
		concept1.getOutboundRelationships().add(statedRelationship);
		
		indexRevision(MAIN, SnomedRelationshipIndexEntry.builder(statedRelationship).storageKey(nextStorageKey()).build());
		indexRevision(MAIN, doc(concept1)
				.storageKey(nextStorageKey())
				.statedParents(PrimitiveSets.newLongOpenHashSet(rootConceptId))
				.build());
		indexRevision(MAIN, doc(concept2)
				.storageKey(nextStorageKey())
				.build());
		
		// change destination from ROOT to concept 2
		statedRelationship.setDestination(concept2);
		registerDirty(statedRelationship);

		final ConceptChangeProcessor processor = process();
		
		final SnomedConceptDocument expected = doc(concept1)
				.iconId(Concepts.NAMESPACE_ROOT)
				.statedParents(PrimitiveSets.newLongOpenHashSet(concept2Id))
				.statedAncestors(PrimitiveSets.newLongOpenHashSet(IComponent.ROOT_IDL))
				.build();
		assertEquals(1, processor.getChangedMappings().size());
		final Revision actual = Iterables.getOnlyElement(processor.getChangedMappings());
		assertDocEquals(expected, actual);
		assertEquals(0, processor.getNewMappings().size());
		assertEquals(0, processor.getDeletions().size());
	}
	
	@Test
	public void deleteLeafConcept() throws Exception {
		final String conceptId = generateConceptId();
		createConcept(conceptId);
		
		registerDetached(SnomedPackage.Literals.CONCEPT, conceptId);
		
		final ConceptChangeProcessor processor = process();
		
		assertEquals(0, processor.getNewMappings().size());
		assertEquals(0, processor.getChangedMappings().size());
		assertEquals(1, processor.getDeletions().size());
	}
	
	@Test
	public void deleteConceptWithOneStatedChild() throws Exception {
		final long namespaceRootLong = Long.parseLong(Concepts.NAMESPACE_ROOT);
		// given a parent concept and child concept
		final String parentId = generateConceptId();
		availableImages.add(parentId);
		final String childId = generateConceptId();
		final Concept parentConcept = createConcept(parentId);
		final Concept childConcept = createConcept(childId);
		// and a stated relationship between the two
		final Relationship childToParentIsa = createStatedRelationship(childId, Concepts.IS_A, parentId);
		final Relationship childToAnotherConceptIsa = createStatedRelationship(childId, Concepts.IS_A, Concepts.NAMESPACE_ROOT);
		
		final long parentIdLong = Long.parseLong(parentId);
		final long childIdLong = Long.parseLong(childId);
		
		// index the child and parent concept documents as current state
		indexRevision(MAIN, doc(parentConcept).storageKey(CDOIDUtil.getLong(parentConcept.cdoID())).build());
		indexRevision(MAIN, doc(childConcept)
				.storageKey(CDOIDUtil.getLong(childConcept.cdoID()))
				// child concept has stated parent and ROOT ancestor
				.statedParents(PrimitiveSets.newLongOpenHashSet(parentIdLong))
				.statedAncestors(PrimitiveSets.newLongOpenHashSet(IComponent.ROOT_IDL))
				.build());
		
		// index existing stated relationships
		indexRevision(MAIN, SnomedRelationshipIndexEntry.builder(childToParentIsa).storageKey(CDOIDUtil.getLong(childToParentIsa.cdoID())).build());
		indexRevision(MAIN, SnomedRelationshipIndexEntry.builder(childToAnotherConceptIsa).storageKey(CDOIDUtil.getLong(childToAnotherConceptIsa.cdoID())).build());
		
		// register child concept as existing concept in view, so it can be loaded via CDO
		registerExistingObject(childConcept);
		
		// delete parent concept and its single relationship
		registerDetached(SnomedPackage.Literals.CONCEPT, parentId);
		registerDetached(SnomedPackage.Literals.RELATIONSHIP, childToParentIsa.getId());
		
		statedChangedConceptIds.add(parentIdLong);
		statedChangedConceptIds.add(childIdLong);
		statedChangedConceptIds.add(namespaceRootLong);
		
		final ConceptChangeProcessor processor = process();
		
		// the parent concept should be deleted
		assertEquals(1, processor.getDeletions().size());
		
		// and the child concept needs to be reindexed as child of the invisible ROOT ID
		assertEquals(1, processor.getChangedMappings().size());
		final Revision newChildRevision = Iterables.getOnlyElement(processor.getChangedMappings());
		final SnomedConceptDocument expectedChildRevision = doc(childConcept)
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
		final String parentId = generateConceptId();
		final String childId = generateConceptId();
		final Concept parentConcept = createConcept(parentId);
		final Concept childConcept = createConcept(childId);
		
		final long parentIdLong = Long.parseLong(parentId);
		final long childIdLong = Long.parseLong(childId);
		statedChangedConceptIds.add(parentIdLong);
		statedChangedConceptIds.add(childIdLong);

		// index the child and parent concept documents as current state
		indexRevision(MAIN, doc(parentConcept).storageKey(CDOIDUtil.getLong(parentConcept.cdoID())).build());
		indexRevision(MAIN, doc(childConcept).storageKey(CDOIDUtil.getLong(childConcept.cdoID())).build());
		
		registerExistingObject(childConcept);
		registerExistingObject(parentConcept);
		
		// add a new stated relationship between the two
		final Relationship childToParentIsa = createStatedRelationship(childId, Concepts.IS_A, parentId);
		registerNew(childToParentIsa);
		
		final ConceptChangeProcessor processor = process();
		
		// the child document should be reindexed with new parent information 
		assertEquals(1, processor.getChangedMappings().size());
		final SnomedConceptDocument expectedDoc = doc(childConcept)
				.statedParents(PrimitiveSets.newLongOpenHashSet(parentIdLong))
				.statedAncestors(PrimitiveSets.newLongOpenHashSet(IComponent.ROOT_IDL))
				.build();
		final Revision changedDoc = Iterables.getOnlyElement(processor.getChangedMappings());
		assertDocEquals(expectedDoc, changedDoc);
		assertEquals(0, processor.getNewMappings().size());
		assertEquals(0, processor.getDeletions().size());
	}
	
	@Test
	public void createNewRefSetWithIdentifierConcept() throws Exception {
		final String identifierId = generateConceptId();
		final Concept identifierConcept = createConcept(identifierId);
		registerNew(identifierConcept);
		final SnomedRefSet refSet = getRegularRefSet(identifierId, SnomedTerminologyComponentConstants.CONCEPT_NUMBER);
		registerNew(refSet);
		
		final ConceptChangeProcessor processor = process();
		
		final SnomedConceptDocument expected = doc(identifierConcept).refSet(refSet).build();
		assertEquals(1, processor.getNewMappings().size());
		final Revision actual = Iterables.getOnlyElement(processor.getNewMappings());
		assertDocEquals(expected, actual);
		assertEquals(0, processor.getChangedMappings().size());
		assertEquals(0, processor.getDeletions().size());
	}
	
	@Test
	public void createRefSetForExistingConcept() throws Exception {
		final String identifierId = generateConceptId();
		final Concept identifierConcept = createConcept(identifierId);
		final long conceptStorageKey = CDOIDUtil.getLong(identifierConcept.cdoID());
		indexRevision(MAIN, doc(identifierConcept).storageKey(conceptStorageKey).build());

		SnomedConceptDocument before = getRevision(MAIN, SnomedConceptDocument.class, identifierId);
		assertNull(before.getRefSetType());
		assertEquals(0, before.getReferencedComponentType());
		assertEquals(-1L, before.getRefSetStorageKey());
		
		final SnomedRefSet refSet = getRegularRefSet(identifierId, SnomedTerminologyComponentConstants.CONCEPT_NUMBER);
		final long refsetStorageKey = CDOIDUtil.getLong(refSet.cdoID());
		registerNew(refSet);
		
		final ConceptChangeProcessor processor = process();
		
		final SnomedConceptDocument expected = doc(identifierConcept).refSet(refSet).build();
		assertEquals(0, processor.getNewMappings().size());
		assertEquals(0, processor.getDeletions().size());
		
		assertEquals(1, processor.getChangedMappings().size());
		final SnomedConceptDocument actual = (SnomedConceptDocument) processor.getChangedMappings().get(conceptStorageKey);
		assertDocEquals(expected, actual);
		assertEquals(SnomedRefSetType.SIMPLE, actual.getRefSetType());
		assertEquals(100, actual.getReferencedComponentType());
		assertEquals(refsetStorageKey, actual.getRefSetStorageKey());
	}
	
	@Test
	public void deleteRefSetButKeepIdentifierConcept() throws Exception {
		final String refSetConceptId = generateConceptId();
		final SnomedRefSet refSet = getRegularRefSet(refSetConceptId, SnomedTerminologyComponentConstants.CONCEPT_NUMBER);
		indexRevision(MAIN, doc(createConcept(refSetConceptId)).storageKey(nextStorageKey()).refSet(refSet).build());
		registerDetached(SnomedRefSetPackage.Literals.SNOMED_REF_SET, refSetConceptId);
		
		final ConceptChangeProcessor processor = process();
		
		assertEquals(0, processor.getNewMappings().size());
		assertEquals(1, processor.getChangedMappings().size());
		assertEquals(0, processor.getDeletions().size());
	}
	
	@Test
	public void updateRefSetIdentifierEffectiveTime() throws Exception {
		final String conceptId = generateConceptId();
		final long conceptStorageKey = nextStorageKey();
		final SnomedRefSet refSet = getRegularRefSet(conceptId, SnomedTerminologyComponentConstants.CONCEPT_NUMBER);
		final long refSetStorageKey = CDOIDUtil.getLong(refSet.cdoID());
		indexRevision(MAIN, doc(createConcept(conceptId)).storageKey(conceptStorageKey).refSet(refSet).build());
		
		// change set
		// XXX intentionally not registering this object to the concept map
		final Date newEffectiveTime = new Date();
		final Concept dirtyConcept = SnomedFactory.eINSTANCE.createConcept();
		withCDOID(dirtyConcept, conceptStorageKey);
		dirtyConcept.setId(conceptId);
		dirtyConcept.setEffectiveTime(newEffectiveTime);
		dirtyConcept.setReleased(true);
		dirtyConcept.setDefinitionStatus(getConcept(Concepts.FULLY_DEFINED));
		dirtyConcept.setModule(module());
		dirtyConcept.setExhaustive(false);
		registerDirty(dirtyConcept);
		registerSetRevisionDelta(dirtyConcept, SnomedPackage.Literals.COMPONENT__RELEASED, false, true);
		registerSetRevisionDelta(dirtyConcept, SnomedPackage.Literals.COMPONENT__EFFECTIVE_TIME, null, newEffectiveTime);
		
		final ConceptChangeProcessor processor = process();
		
		assertEquals(0, processor.getNewMappings().size());
		assertEquals(1, processor.getChangedMappings().size());
		assertEquals(0, processor.getDeletions().size());
		
		// assert that refset props are not going to be removed from the concept doc
		final SnomedConceptDocument newRevision = (SnomedConceptDocument) processor.getChangedMappings().get(conceptStorageKey);
		assertEquals(refSetStorageKey, newRevision.getRefSetStorageKey());
		assertEquals(newEffectiveTime.getTime(), newRevision.getEffectiveTime());
		assertEquals(true, newRevision.isReleased());
	}
	
	@Test
	public void addNewSimpleMemberToExistingConcept() throws Exception {
		final String conceptId = generateConceptId();
		final String referringReferenceSetId = generateConceptId();
		
		final Concept concept = createConcept(conceptId);
		final SnomedRefSetMember member = createSimpleMember(conceptId, referringReferenceSetId);
		
		// set current state
		registerExistingObject(concept);
		indexRevision(MAIN, doc(concept).storageKey(CDOIDUtil.getLong(concept.cdoID())).build());
		
		registerNew(member);
		
		final ConceptChangeProcessor processor = process();
		
		// the concept needs to be reindexed with the referring member value
		final SnomedConceptDocument expected = doc(concept)
				.memberOf(ImmutableSet.of(referringReferenceSetId))
				.activeMemberOf(ImmutableSet.of(referringReferenceSetId))
				.build();
		assertEquals(1, processor.getChangedMappings().size());
		final Revision actual = Iterables.getOnlyElement(processor.getChangedMappings());
		assertDocEquals(expected, actual);
		assertEquals(0, processor.getNewMappings().size());
		assertEquals(0, processor.getDeletions().size());
	}
	
	@Test
	public void addInactiveMemberToExistingConcept() throws Exception {
		final String conceptId = generateConceptId();
		final String referringReferenceSetId = generateConceptId();
		
		final Concept concept = createConcept(conceptId);
		final SnomedRefSetMember member = createSimpleMember(conceptId, referringReferenceSetId);
		member.setActive(false);
		
		// set current state
		registerExistingObject(concept);
		indexRevision(MAIN, doc(concept).storageKey(CDOIDUtil.getLong(concept.cdoID())).build());
		
		registerNew(member);
		
		final ConceptChangeProcessor processor = process();
		
		// the concept needs to be reindexed with the referring member value
		final SnomedConceptDocument expected = doc(concept)
				.memberOf(ImmutableSet.of(referringReferenceSetId))
				.build();
		assertEquals(1, processor.getChangedMappings().size());
		final Revision actual = Iterables.getOnlyElement(processor.getChangedMappings());
		assertDocEquals(expected, actual);
		assertEquals(0, processor.getNewMappings().size());
		assertEquals(0, processor.getDeletions().size());
	}
	
	@Test
	public void addNewSimpleMapMemberToExistingConcept() throws Exception {
		final String conceptId = generateConceptId();
		final String referringMappingReferenceSetId = generateConceptId();
		
		final Concept concept = createConcept(conceptId);
		final SnomedRefSetMember member = createSimpleMapMember(conceptId, "A00", referringMappingReferenceSetId);
		
		// set current state
		registerExistingObject(concept);
		indexRevision(MAIN, doc(concept).storageKey(CDOIDUtil.getLong(concept.cdoID())).build());
		
		registerNew(member);
		
		final ConceptChangeProcessor processor = process();
		
		// the concept needs to be reindexed with new memberOf array fields
		final SnomedConceptDocument expected = doc(concept)
				.memberOf(ImmutableSet.of(referringMappingReferenceSetId))
				.activeMemberOf(ImmutableSet.of(referringMappingReferenceSetId))
				.build();
		assertEquals(1, processor.getChangedMappings().size());
		final Revision actual = Iterables.getOnlyElement(processor.getChangedMappings());
		assertDocEquals(expected, actual);
		assertEquals(0, processor.getNewMappings().size());
		assertEquals(0, processor.getDeletions().size());
	}
	
	@Test
	public void inactivateSimpleMemberOfConcept() throws Exception {
		final String conceptId = generateConceptId();
		final String referringReferenceSetId = generateConceptId();
		
		final Concept concept = createConcept(conceptId);
		final SnomedRefSetMember member = createSimpleMember(conceptId, referringReferenceSetId);
		
		// set current state
		registerExistingObject(concept);
		registerExistingObject(member);
		indexRevision(MAIN, doc(concept)
				.storageKey(CDOIDUtil.getLong(concept.cdoID()))
				.memberOf(Collections.singleton(referringReferenceSetId))
				.activeMemberOf(Collections.singleton(referringReferenceSetId))
				.build());
		indexRevision(MAIN, SnomedRefSetMemberIndexEntry.builder(member)
				.storageKey(CDOIDUtil.getLong(member.cdoID()))
				.build());
		
		member.setActive(false);
		registerDirty(member);
		registerSetRevisionDelta(member, SnomedRefSetPackage.Literals.SNOMED_REF_SET_MEMBER__ACTIVE, true, false);
		
		final ConceptChangeProcessor processor = process();
		
		// the concept needs to be reindexed with the referring member value
		final SnomedConceptDocument expected = doc(concept)
				.memberOf(ImmutableSet.of(referringReferenceSetId))
				.build();
		assertEquals(1, processor.getChangedMappings().size());
		final Revision actual = Iterables.getOnlyElement(processor.getChangedMappings());
		assertDocEquals(expected, actual);
		assertEquals(0, processor.getNewMappings().size());
		assertEquals(0, processor.getDeletions().size());
	}
	
	@Test
	public void reactivateSimpleMemberOfConcept() throws Exception {
		final String conceptId = generateConceptId();
		final String referringReferenceSetId = generateConceptId();
		
		final Concept concept = createConcept(conceptId);
		final SnomedRefSetMember member = createSimpleMember(conceptId, referringReferenceSetId);
		member.setActive(false);
		
		// set current state
		registerExistingObject(concept);
		registerExistingObject(member);
		indexRevision(MAIN, doc(concept)
				.storageKey(CDOIDUtil.getLong(concept.cdoID()))
				.memberOf(Collections.singleton(referringReferenceSetId))
				.build());
		indexRevision(MAIN, SnomedRefSetMemberIndexEntry.builder(member)
				.storageKey(CDOIDUtil.getLong(member.cdoID()))
				.build());
		
		member.setActive(true);
		registerDirty(member);
		registerSetRevisionDelta(member, SnomedRefSetPackage.Literals.SNOMED_REF_SET_MEMBER__ACTIVE, false, true);
		
		final ConceptChangeProcessor processor = process();
		
		// the concept needs to be reindexed with the referring member value
		final SnomedConceptDocument expected = doc(concept)
				.memberOf(ImmutableSet.of(referringReferenceSetId))
				.activeMemberOf(ImmutableSet.of(referringReferenceSetId))
				.build();
		assertEquals(1, processor.getChangedMappings().size());
		final Revision actual = Iterables.getOnlyElement(processor.getChangedMappings());
		assertDocEquals(expected, actual);
		assertEquals(0, processor.getNewMappings().size());
		assertEquals(0, processor.getDeletions().size());
	}
	
	@Test
	public void deleteSimpleMemberOfConcept() throws Exception {
		final String conceptId = generateConceptId();
		final String referringReferenceSetId = generateConceptId();
		
		final Concept concept = createConcept(conceptId);
		final SnomedRefSetMember member = createSimpleMember(conceptId, referringReferenceSetId);
		
		// set current state
		registerExistingObject(concept);
		registerExistingObject(member);
		indexRevision(MAIN, doc(concept)
				.storageKey(CDOIDUtil.getLong(concept.cdoID()))
				.memberOf(ImmutableSet.of(referringReferenceSetId))
				.activeMemberOf(ImmutableSet.of(referringReferenceSetId))
				.build());
		indexRevision(MAIN, SnomedRefSetMemberIndexEntry.builder(member)
				.storageKey(CDOIDUtil.getLong(member.cdoID()))
				.build());
		
		registerDetached(SnomedRefSetPackage.Literals.SNOMED_REF_SET_MEMBER, member.getUuid());
		
		final ConceptChangeProcessor processor = process();
		
		// the concept needs to be reindexed with the referring member value
		final SnomedConceptDocument expected = doc(concept).build();
		assertEquals(1, processor.getChangedMappings().size());
		final Revision actual = Iterables.getOnlyElement(processor.getChangedMappings());
		assertDocEquals(expected, actual);
		assertEquals(0, processor.getNewMappings().size());
		assertEquals(0, processor.getDeletions().size());
	}
	
	@Test
	public void deleteOneMemberFromMultipleMembersOfConcept() {
		final String conceptId = generateConceptId();
		final String referringReferenceSetId = generateConceptId();
		
		final Concept concept = createConcept(conceptId);
		final SnomedRefSetMember member1 = createSimpleMember(conceptId, referringReferenceSetId);
		final SnomedRefSetMember member2 = createSimpleMember(conceptId, referringReferenceSetId);
		
		registerExistingObject(concept);
		registerExistingObject(member1);
		registerExistingObject(member2);
		
		indexRevision(MAIN, doc(concept)
				.storageKey(CDOIDUtil.getLong(concept.cdoID()))
				.memberOf(ImmutableList.of(referringReferenceSetId, referringReferenceSetId))
				.activeMemberOf(ImmutableList.of(referringReferenceSetId, referringReferenceSetId))
				.build());
		indexRevision(MAIN, SnomedRefSetMemberIndexEntry.builder(member1)
				.storageKey(CDOIDUtil.getLong(member1.cdoID()))
				.build());
		indexRevision(MAIN, SnomedRefSetMemberIndexEntry.builder(member2)
				.storageKey(CDOIDUtil.getLong(member2.cdoID()))
				.build());
		
		registerDetached(member1.eClass(), member1.getUuid());
		
		final ConceptChangeProcessor processor = process();
		
		// the concept needs to be reindexed with the new memberOf and activeMemberOf array field values
		final SnomedConceptDocument expected = doc(concept)
				.memberOf(Collections.singleton(referringReferenceSetId))
				.activeMemberOf(Collections.singleton(referringReferenceSetId))
				.build();
		assertEquals(1, processor.getChangedMappings().size());
		final Revision actual = Iterables.getOnlyElement(processor.getChangedMappings());
		assertDocEquals(expected, actual);
		assertEquals(0, processor.getNewMappings().size());
		assertEquals(0, processor.getDeletions().size());
	}
	
	@Test
	public void deleteSimpleMapMemberOfConcept() throws Exception {
		final String conceptId = generateConceptId();
		final String referringMappingReferenceSetId = generateConceptId();
		
		final Concept concept = createConcept(conceptId);
		final SnomedRefSetMember member = createSimpleMapMember(conceptId, "A00", referringMappingReferenceSetId);
		
		// set current state
		registerExistingObject(concept);
		registerExistingObject(member);
		indexRevision(MAIN, doc(concept)
				.storageKey(CDOIDUtil.getLong(concept.cdoID()))
				.memberOf(ImmutableSet.of(referringMappingReferenceSetId))
				.activeMemberOf(ImmutableSet.of(referringMappingReferenceSetId))
				.build());
		indexRevision(MAIN, SnomedRefSetMemberIndexEntry.builder(member)
				.storageKey(CDOIDUtil.getLong(member.cdoID()))
				.build());
		
		registerDetached(SnomedRefSetPackage.Literals.SNOMED_REF_SET_MEMBER, member.getUuid());
		
		final ConceptChangeProcessor processor = process();
		
		// the concept needs to be reindexed with the referring member value
		final SnomedConceptDocument expected = doc(concept).build();
		assertEquals(1, processor.getChangedMappings().size());
		final Revision actual = Iterables.getOnlyElement(processor.getChangedMappings());
		assertDocEquals(expected, actual);
		assertEquals(0, processor.getNewMappings().size());
		assertEquals(0, processor.getDeletions().size());
	}
	
	@Test
	public void inactivateConcept() throws Exception {
		// current state
		final String conceptId = generateConceptId();
		final Concept concept = createConcept(conceptId);
		final long conceptStorageKey = CDOIDUtil.getLong(concept.cdoID());
		registerExistingObject(concept);
		indexRevision(MAIN, doc(concept).storageKey(conceptStorageKey).build());
		
		// change set
		// XXX intentionally not registering this object to the concept map
		final Concept dirtyConcept = SnomedFactory.eINSTANCE.createConcept();
		withCDOID(dirtyConcept, conceptStorageKey);
		dirtyConcept.setId(conceptId);
		dirtyConcept.setActive(false);
		dirtyConcept.setDefinitionStatus(getConcept(Concepts.FULLY_DEFINED));
		dirtyConcept.setModule(module());
		dirtyConcept.setExhaustive(false);
		registerDirty(dirtyConcept);
		registerSetRevisionDelta(dirtyConcept, SnomedPackage.Literals.COMPONENT__ACTIVE, true, false);
		
		final ConceptChangeProcessor processor = process();
		
		// expected index changes, concept should be inactive now
		assertEquals(1, processor.getChangedMappings().size());
		final SnomedConceptDocument expected = doc(concept).active(false).build();
		final Revision actual = Iterables.getOnlyElement(processor.getChangedMappings());
		assertDocEquals(expected, actual);
		assertEquals(0, processor.getNewMappings().size());
		assertEquals(0, processor.getDeletions().size());
	}
	
	@Test
	public void newInferredIsaRelationshipDoesNotChangeStatedTaxonomy() throws Exception {
		// given a parent concept and child concept
		final String parentId = generateConceptId();
		final String childId = generateConceptId();
		final Concept parentConcept = createConcept(parentId);
		final Concept childConcept = createConcept(childId);
		final Relationship statedIsa = createStatedRelationship(childId, Concepts.IS_A, parentId);
		registerExistingObject(statedIsa);
		
		final long parentIdLong = Long.parseLong(parentId);
		final long childIdLong = Long.parseLong(childId);

		// index the child and parent concept documents as current state
		indexRevision(MAIN, doc(parentConcept).storageKey(CDOIDUtil.getLong(parentConcept.cdoID())).build());
		indexRevision(MAIN, doc(childConcept)
				.storageKey(CDOIDUtil.getLong(childConcept.cdoID()))
				.statedParents(PrimitiveSets.newLongOpenHashSet(parentIdLong))
				.statedAncestors(PrimitiveSets.newLongOpenHashSet(IComponent.ROOT_IDL))
				.build());
		indexRevision(MAIN, SnomedRelationshipIndexEntry.builder(statedIsa)
				.storageKey(CDOIDUtil.getLong(statedIsa.cdoID()))
				.build());
		
		registerExistingObject(childConcept);
		registerExistingObject(parentConcept);
		
		// add a new stated relationship between the two
		inferredChangedConceptIds.add(parentIdLong);
		inferredChangedConceptIds.add(childIdLong);
		final Relationship childToParentIsa = createInferredRelationship(childId, Concepts.IS_A, parentId);
		registerNew(childToParentIsa);
		registerDirty(childConcept);
		
		final ConceptChangeProcessor processor = process();
		
		assertEquals(1, processor.getChangedMappings().size());
		final SnomedConceptDocument expected = doc(childConcept)
				.statedParents(PrimitiveSets.newLongOpenHashSet(parentIdLong))
				.statedAncestors(PrimitiveSets.newLongOpenHashSet(IComponent.ROOT_IDL))
				.parents(PrimitiveSets.newLongOpenHashSet(parentIdLong))
				.ancestors(PrimitiveSets.newLongOpenHashSet(IComponent.ROOT_IDL))
				.build();
		final Revision actual = Iterables.getOnlyElement(processor.getChangedMappings());
		assertDocEquals(expected, actual);
		assertEquals(0, processor.getNewMappings().size());
		assertEquals(0, processor.getDeletions().size());
	}
	
	private Builder doc(final Concept concept) {
		return SnomedConceptDocument.builder()
				.id(concept.getId())
				.iconId(Concepts.ROOT_CONCEPT)
				.active(concept.isActive())
				.released(concept.isReleased())
				.exhaustive(concept.isExhaustive())
				.moduleId(concept.getModule().getId())
				.effectiveTime(EffectiveTimes.getEffectiveTime(concept.getEffectiveTime()))
				.primitive(Concepts.PRIMITIVE.equals(concept.getDefinitionStatus().getId()))
				.parents(PrimitiveSets.newLongOpenHashSet(IComponent.ROOT_IDL))
				.ancestors(PrimitiveSets.newLongOpenHashSet())
				.statedParents(PrimitiveSets.newLongOpenHashSet(IComponent.ROOT_IDL))
				.statedAncestors(PrimitiveSets.newLongOpenHashSet());
	}

	private Concept createConcept(final String id) {
		final Concept concept = getConcept(id);
		withCDOID(concept, nextStorageKey());
		concept.setActive(true);
		concept.setDefinitionStatus(getConcept(Concepts.FULLY_DEFINED));
		concept.setModule(module());
		concept.setExhaustive(false);
		return concept;
	}

}
