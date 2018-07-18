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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

import java.util.Collections;

import org.junit.Test;

import com.b2international.index.revision.Revision;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.domain.Acceptability;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionFragment;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

/**
 * @since 7.0
 */
public class PreferredDescriptionPreCommitHookTest extends BaseConceptPreCommitHookTest {

	@Test
	public void indexConceptWithSingleFsn() throws Exception {
		final SnomedConceptDocument concept = concept().build();
		final SnomedDescriptionIndexEntry fsn = fsn(concept.getId(), Collections.singletonMap(Concepts.REFSET_LANGUAGE_TYPE_UK, Acceptability.PREFERRED));
		
		stageNew(concept);
		stageNew(fsn);
		
		final ConceptChangeProcessor processor = process();
		
		final SnomedConceptDocument expected = docWithDefaults(concept)
				.preferredDescriptions(ImmutableList.of(
					new SnomedDescriptionFragment(fsn.getId(), fsn.getTypeId(), fsn.getTerm(), Concepts.REFSET_LANGUAGE_TYPE_UK)
				))
				.build();
		final Revision actual = Iterables.getOnlyElement(processor.getNewMappings().values());
		assertDocEquals(expected, actual);
		assertEquals(0, processor.getChangedMappings().size());
		assertEquals(0, processor.getDeletions().size());
	}
	
	@Test
	public void indexConceptWithFsnAndSynonym() throws Exception {
		final SnomedConceptDocument concept = concept().build();
		final SnomedDescriptionIndexEntry fsn = fsn(concept.getId(), Collections.singletonMap(Concepts.REFSET_LANGUAGE_TYPE_UK, Acceptability.PREFERRED));
		final SnomedDescriptionIndexEntry synonym = synonym(concept.getId(), Collections.singletonMap(Concepts.REFSET_LANGUAGE_TYPE_UK, Acceptability.PREFERRED));
		
		stageNew(concept);
		stageNew(fsn);
		stageNew(synonym);
		
		final ConceptChangeProcessor processor = process();
		
		final SnomedConceptDocument expected = docWithDefaults(concept)
				.preferredDescriptions(ImmutableList.of(
					new SnomedDescriptionFragment(fsn.getId(), fsn.getTypeId(), fsn.getTerm(), Concepts.REFSET_LANGUAGE_TYPE_UK),
					new SnomedDescriptionFragment(synonym.getId(), synonym.getTypeId(), synonym.getTerm(), Concepts.REFSET_LANGUAGE_TYPE_UK)
				))
				.build();
		final Revision actual = Iterables.getOnlyElement(processor.getNewMappings().values());
		assertDocEquals(expected, actual);
		assertEquals(0, processor.getChangedMappings().size());
		assertEquals(0, processor.getDeletions().size());
	}
	
	@Test
	public void indexConceptWithFsnAndDefinition() throws Exception {
		final SnomedConceptDocument concept = concept().build();
		final SnomedDescriptionIndexEntry fsn = fsn(concept.getId(), Collections.singletonMap(Concepts.REFSET_LANGUAGE_TYPE_UK, Acceptability.PREFERRED));
		final SnomedDescriptionIndexEntry definition = definition(concept.getId(), Collections.singletonMap(Concepts.REFSET_LANGUAGE_TYPE_UK, Acceptability.PREFERRED));

		stageNew(concept);
		stageNew(fsn);
		stageNew(definition);
		
		final ConceptChangeProcessor processor = process();
		
		final SnomedConceptDocument expected = docWithDefaults(concept)
				.preferredDescriptions(ImmutableList.of(
					new SnomedDescriptionFragment(fsn.getId(), fsn.getTypeId(), fsn.getTerm(), Concepts.REFSET_LANGUAGE_TYPE_UK)
				))
				.build();
		final Revision actual = Iterables.getOnlyElement(processor.getNewMappings().values());
		assertDocEquals(expected, actual);
		assertEquals(0, processor.getChangedMappings().size());
		assertEquals(0, processor.getDeletions().size());
	}
	
	@Test
	public void indexNewFsnForExistingConcept() throws Exception {
		final SnomedConceptDocument concept = concept().build();
		initRevisions(docWithDefaults(concept).build());
		
		final SnomedDescriptionIndexEntry fsn = fsn(concept.getId(), Collections.singletonMap(Concepts.REFSET_LANGUAGE_TYPE_UK, Acceptability.PREFERRED));
		stageNew(fsn);
		
		final ConceptChangeProcessor processor = process();
		
		final SnomedConceptDocument expected = docWithDefaults(concept)
				.preferredDescriptions(ImmutableList.of(
					new SnomedDescriptionFragment(fsn.getId(), fsn.getTypeId(), fsn.getTerm(), Concepts.REFSET_LANGUAGE_TYPE_UK)
				))
				.build();
		assertEquals(1, processor.getChangedMappings().size());
		final Revision actual = Iterables.getOnlyElement(processor.getChangedMappings().values()).getNewRevision();
		assertDocEquals(expected, actual);
		assertEquals(0, processor.getNewMappings().size());
		assertEquals(0, processor.getDeletions().size());
	}
	
	@Test
	public void indexNewSynonymForExistingConcept() throws Exception {
		final SnomedConceptDocument concept = concept().build();
		initRevisions(docWithDefaults(concept).build());
		
		final SnomedDescriptionIndexEntry synonym = synonym(concept.getId(), Collections.singletonMap(Concepts.REFSET_LANGUAGE_TYPE_UK, Acceptability.PREFERRED));
		stageNew(synonym);
		
		final ConceptChangeProcessor processor = process();
		
		final SnomedConceptDocument expected = docWithDefaults(concept)
				.preferredDescriptions(ImmutableList.of(
					new SnomedDescriptionFragment(synonym.getId(), synonym.getTypeId(), synonym.getTerm(), Concepts.REFSET_LANGUAGE_TYPE_UK)
				))
				.build();
		final Revision actual = Iterables.getOnlyElement(processor.getChangedMappings().values()).getNewRevision();
		assertDocEquals(expected, actual);
		assertEquals(0, processor.getNewMappings().size());
		assertEquals(0, processor.getDeletions().size());
	}
	
	@Test
	public void indexNewTextDefinitionForExistingConcept() throws Exception {
		final SnomedConceptDocument concept = concept().build();
		initRevisions(docWithDefaults(concept).build());
		
		final SnomedDescriptionIndexEntry textDefinition = definition(concept.getId(), Collections.singletonMap(Concepts.REFSET_LANGUAGE_TYPE_UK, Acceptability.PREFERRED));
		stageNew(textDefinition);
		
		final ConceptChangeProcessor processor = process();
		
		assertEquals(0, processor.getChangedMappings().size());
		assertEquals(0, processor.getNewMappings().size());
		assertEquals(0, processor.getDeletions().size());
	}
	
	@Test
	public void removeFsnFromExistingConcept() throws Exception {
		final SnomedConceptDocument concept = concept().build();
		
		final SnomedDescriptionIndexEntry fsn = fsn(concept.getId(), Collections.singletonMap(Concepts.REFSET_LANGUAGE_TYPE_UK, Acceptability.PREFERRED));
		initRevisions(
			docWithDefaults(concept)
				.preferredDescriptions(ImmutableList.of(
					new SnomedDescriptionFragment(fsn.getId(), fsn.getTypeId(), fsn.getTerm(), Concepts.REFSET_LANGUAGE_TYPE_UK)
				))
				.build(),
			fsn
		);
		
		stageRemove(fsn);
		
		final ConceptChangeProcessor processor = process();
		
		final SnomedConceptDocument expected = docWithDefaults(concept).build();
		final Revision actual = Iterables.getOnlyElement(processor.getChangedMappings().values()).getNewRevision();
		assertDocEquals(expected, actual);
		assertEquals(0, processor.getNewMappings().size());
		assertEquals(0, processor.getDeletions().size());
	}
	
	@Test
	public void removeSynonymFromExistingConcept() throws Exception {
		final SnomedConceptDocument concept = concept().build();
		
		final SnomedDescriptionIndexEntry synonym = synonym(concept.getId(), Collections.singletonMap(Concepts.REFSET_LANGUAGE_TYPE_UK, Acceptability.PREFERRED));
		initRevisions(
			docWithDefaults(concept)
				.preferredDescriptions(ImmutableList.of(
					new SnomedDescriptionFragment(synonym.getId(), synonym.getTypeId(), synonym.getTerm(), Concepts.REFSET_LANGUAGE_TYPE_UK)
				))
				.build(),
			synonym
		);
		
		stageRemove(synonym);
		
		final ConceptChangeProcessor processor = process();
		
		final SnomedConceptDocument expected = docWithDefaults(concept).build();
		final Revision actual = Iterables.getOnlyElement(processor.getChangedMappings().values()).getNewRevision();
		assertDocEquals(expected, actual);
		assertEquals(0, processor.getNewMappings().size());
		assertEquals(0, processor.getDeletions().size());
	}
	
	@Test
	public void updateTermOfConceptFsn() throws Exception {
		final SnomedConceptDocument concept = concept().build();
		final SnomedDescriptionIndexEntry fsn = fsn(concept.getId(), Collections.singletonMap(Concepts.REFSET_LANGUAGE_TYPE_UK, Acceptability.PREFERRED));

		initRevisions(
			docWithDefaults(concept)
				.preferredDescriptions(ImmutableList.of(
					new SnomedDescriptionFragment(fsn.getId(), fsn.getTypeId(), fsn.getTerm(), Concepts.REFSET_LANGUAGE_TYPE_UK)
				))
				.build(), 
			fsn
		);
		
		stageChange(fsn, SnomedDescriptionIndexEntry.builder(fsn).term("Term (changed)").build());
		
		final ConceptChangeProcessor processor = process();
		
		final SnomedConceptDocument expected = docWithDefaults(concept)
				.preferredDescriptions(ImmutableList.of(
					new SnomedDescriptionFragment(fsn.getId(), fsn.getTypeId(), "Term (changed)", Concepts.REFSET_LANGUAGE_TYPE_UK)
				))
				.build();
		final Revision actual = Iterables.getOnlyElement(processor.getChangedMappings().values()).getNewRevision();
		assertDocEquals(expected, actual);
		assertEquals(0, processor.getNewMappings().size());
		assertEquals(0, processor.getDeletions().size());
	}
	
	@Test
	public void inactivateFsnOfConcept() throws Exception {
		final SnomedConceptDocument concept = concept().build();
		final SnomedDescriptionIndexEntry fsn = fsn(concept.getId(), Collections.singletonMap(Concepts.REFSET_LANGUAGE_TYPE_UK, Acceptability.PREFERRED));
		
		initRevisions(
			docWithDefaults(concept)
				.preferredDescriptions(ImmutableList.of(
					new SnomedDescriptionFragment(fsn.getId(), fsn.getTypeId(), fsn.getTerm(), Concepts.REFSET_LANGUAGE_TYPE_UK)
				))
				.build(),
			fsn
		);
		
		stageChange(fsn, SnomedDescriptionIndexEntry.builder(fsn).active(false).build());
		
		final ConceptChangeProcessor processor = process();
		
		final SnomedConceptDocument expected = docWithDefaults(concept).build();
		final Revision actual = Iterables.getOnlyElement(processor.getChangedMappings().values()).getNewRevision();
		assertDocEquals(expected, actual);
		assertEquals(0, processor.getNewMappings().size());
		assertEquals(0, processor.getDeletions().size());
	}
	
	@Test
	public void updateCaseSignificanceOfConceptDescription() throws Exception {
		final SnomedConceptDocument concept = concept().build();
		final SnomedDescriptionIndexEntry fsn = fsn(concept.getId(), Collections.singletonMap(Concepts.REFSET_LANGUAGE_TYPE_UK, Acceptability.PREFERRED));
		
		initRevisions(
			docWithDefaults(concept)
				.preferredDescriptions(ImmutableList.of(
					new SnomedDescriptionFragment(fsn.getId(), fsn.getTypeId(), fsn.getTerm(), Concepts.REFSET_LANGUAGE_TYPE_UK)
				))
				.build(),
			fsn
		);

		stageChange(fsn, SnomedDescriptionIndexEntry.builder(fsn).caseSignificanceId(Concepts.ENTIRE_TERM_CASE_INSENSITIVE).build());
		
		final ConceptChangeProcessor processor = process();
		
		assertEquals(0, processor.getNewMappings().size());
		assertEquals(0, processor.getChangedMappings().size());
		assertEquals(0, processor.getDeletions().size());
	}
	
	@Test
	public void updateTypeOfConceptDescription() throws Exception {
		final SnomedConceptDocument concept = concept().build();
		final SnomedDescriptionIndexEntry fsn = fsn(concept.getId(), Collections.singletonMap(Concepts.REFSET_LANGUAGE_TYPE_UK, Acceptability.PREFERRED));
		
		initRevisions(
			docWithDefaults(concept)
				.preferredDescriptions(ImmutableList.of(
					new SnomedDescriptionFragment(fsn.getId(), fsn.getTypeId(), fsn.getTerm(), Concepts.REFSET_LANGUAGE_TYPE_UK)
				))
				.build(), 
			fsn
		);
		
		SnomedDescriptionIndexEntry synonym = SnomedDescriptionIndexEntry.builder(fsn).typeId(Concepts.SYNONYM).build();
		stageChange(fsn, synonym);
		
		final ConceptChangeProcessor processor = process();
		
		final SnomedConceptDocument expected = docWithDefaults(concept)
				.preferredDescriptions(ImmutableList.of(
					new SnomedDescriptionFragment(synonym.getId(), synonym.getTypeId(), synonym.getTerm(), Concepts.REFSET_LANGUAGE_TYPE_UK)
				))
				.build();
		final Revision actual = Iterables.getOnlyElement(processor.getChangedMappings().values()).getNewRevision();
		assertDocEquals(expected, actual);
		assertEquals(0, processor.getNewMappings().size());
		assertEquals(0, processor.getDeletions().size());
	}
	
	@Test
	public void addTwoConceptsWithNewDescriptions() throws Exception {
		final SnomedConceptDocument concept1 = concept().build();
		final SnomedDescriptionIndexEntry fsnOfConcept1 = fsn(concept1.getId(), Collections.singletonMap(Concepts.REFSET_LANGUAGE_TYPE_UK, Acceptability.PREFERRED));
		
		final SnomedConceptDocument concept2 = concept().build();
		final SnomedDescriptionIndexEntry fsnOfConcept2 = fsn(concept2.getId(), Collections.singletonMap(Concepts.REFSET_LANGUAGE_TYPE_UK, Acceptability.PREFERRED));
		
		stageNew(concept1);
		stageNew(concept2);
		stageNew(fsnOfConcept1);
		stageNew(fsnOfConcept2);
		
		ConceptChangeProcessor processor = process();
		
		assertEquals(2, processor.getNewMappings().size());
		
		processor.getNewMappings().values().forEach(newConcept -> {
			if (newConcept instanceof SnomedConceptDocument) {
				assertThat(((SnomedConceptDocument) newConcept).getPreferredDescriptions()).hasSize(1);
			}
		});
		
		assertEquals(0, processor.getChangedMappings().size());
		assertEquals(0, processor.getDeletions().size());
	}
	
//	@Test
//	public void addAcceptabilityMemberToConceptDescription() throws Exception {
//		final SnomedConceptDocument concept = concept().build();
//		final SnomedDescriptionIndexEntry fsn = fsn(concept.getId(), ImmutableMap.of(
//			Concepts.REFSET_LANGUAGE_TYPE_UK, Acceptability.PREFERRED,
//			Concepts.REFSET_LANGUAGE_TYPE_US, Acceptability.ACCEPTABLE
//		));
//		final SnomedRefSetMemberIndexEntry preferredInUk = langMember(fsn.getId(), Acceptability.PREFERRED, Concepts.REFSET_LANGUAGE_TYPE_UK);
//		
//		initRevisions(
//			docWithDefaults(concept)
//				.preferredDescriptions(ImmutableList.of(
//					new SnomedDescriptionFragment(fsn.getId(), fsn.getTypeId(), fsn.getTerm(), Concepts.REFSET_LANGUAGE_TYPE_UK)
//				))
//				.build(),
//			fsn,
//			preferredInUk
//		);
//		
//		final SnomedRefSetMemberIndexEntry preferredInUs = langMember(fsn.getId(), Acceptability.PREFERRED, Concepts.REFSET_LANGUAGE_TYPE_US);
//		stageNew(preferredInUs);
//		
//		final ConceptChangeProcessor processor = process();
//		
//		final SnomedConceptDocument expected = docWithDefaults(concept)
//				.preferredDescriptions(ImmutableList.of(
//					new SnomedDescriptionFragment(fsn.getId(), fsn.getTypeId(), fsn.getTerm(), ImmutableList.of(Concepts.REFSET_LANGUAGE_TYPE_UK, Concepts.REFSET_LANGUAGE_TYPE_US))
//				))
//				.build();
//		assertEquals(1, processor.getChangedMappings().size());
//		final Revision actual = Iterables.getOnlyElement(processor.getChangedMappings().values()).getNewRevision();
//		assertDocEquals(expected, actual);
//		assertEquals(0, processor.getNewMappings().size());
//		assertEquals(0, processor.getDeletions().size());
//	}
//	
//	@Test
//	public void inactivateAcceptabilityMemberOfConceptDescription() throws Exception {
//		final Concept concept = createConcept(generateConceptId());
//		final long conceptStorageKey = CDOIDUtil.getLong(concept.cdoID());
//		Description description = createFsnWithTwoAcceptabilityMembers();
//		SnomedLanguageRefSetMember memberToInactivate = createLangMember(description.getId(), Acceptability.PREFERRED, Concepts.REFSET_LANGUAGE_TYPE_US);
//		description.getLanguageRefSetMembers().add(memberToInactivate);
//		description.setConcept(concept);
//		registerExistingObject(concept);
//		registerExistingObject(description);
//		registerExistingObject(memberToInactivate);
//		
//		indexRevision(MAIN, doc(concept)
//				.storageKey(conceptStorageKey)
//				.preferredDescriptions(ImmutableList.of(
//					new SnomedDescriptionFragment(description.getId(), CDOIDUtil.getLong(description.cdoID()), Concepts.FULLY_SPECIFIED_NAME, description.getTerm(), ImmutableList.of(Concepts.REFSET_LANGUAGE_TYPE_UK, Concepts.REFSET_LANGUAGE_TYPE_US))
//				))
//				.build());
//		
//		memberToInactivate.setActive(false);
//		registerDirty(memberToInactivate);
//		registerSetRevisionDelta(memberToInactivate, SnomedRefSetPackage.Literals.SNOMED_REF_SET_MEMBER__ACTIVE, true, false);
//		
//		final ConceptChangeProcessor processor = process();
//		
//		final SnomedConceptDocument expected = doc(concept)
//				.preferredDescriptions(ImmutableList.of(
//					new SnomedDescriptionFragment(description.getId(), CDOIDUtil.getLong(description.cdoID()), Concepts.FULLY_SPECIFIED_NAME, description.getTerm(), Concepts.REFSET_LANGUAGE_TYPE_UK)
//				))
//				.build();
//		final Revision actual = Iterables.getOnlyElement(processor.getChangedMappings().values()).getNewRevision();
//		assertDocEquals(expected, actual);
//		assertEquals(0, processor.getNewMappings().size());
//		assertEquals(0, processor.getDeletions().size());
//	}
//	
//	@Test
//	public void changeAcceptabilityOfConceptDescription() throws Exception {
//		final Concept concept = createConcept(generateConceptId());
//		final long conceptStorageKey = CDOIDUtil.getLong(concept.cdoID());
//		Description description = createFsnWithTwoAcceptabilityMembers();
//		SnomedLanguageRefSetMember memberToChange = createLangMember(description.getId(), Acceptability.PREFERRED, Concepts.REFSET_LANGUAGE_TYPE_US);
//		description.getLanguageRefSetMembers().add(memberToChange);
//		description.setConcept(concept);
//		registerExistingObject(concept);
//		registerExistingObject(description);
//		registerExistingObject(memberToChange);
//		
//		indexRevision(MAIN, doc(concept)
//				.storageKey(conceptStorageKey)
//				.preferredDescriptions(ImmutableList.of(
//					new SnomedDescriptionFragment(description.getId(), CDOIDUtil.getLong(description.cdoID()), Concepts.FULLY_SPECIFIED_NAME, description.getTerm(), ImmutableList.of(Concepts.REFSET_LANGUAGE_TYPE_UK, Concepts.REFSET_LANGUAGE_TYPE_US))
//				))
//				.build());
//		
//		memberToChange.setAcceptabilityId(Concepts.REFSET_DESCRIPTION_ACCEPTABILITY_ACCEPTABLE);
//		registerDirty(memberToChange);
//		registerSetRevisionDelta(memberToChange, SnomedRefSetPackage.Literals.SNOMED_LANGUAGE_REF_SET_MEMBER__ACCEPTABILITY_ID, null /*unused*/, null /*unused*/);
//		
//		final ConceptChangeProcessor processor = process();
//		
//		final SnomedConceptDocument expected = doc(concept)
//				.preferredDescriptions(ImmutableList.of(
//					new SnomedDescriptionFragment(description.getId(), CDOIDUtil.getLong(description.cdoID()), Concepts.FULLY_SPECIFIED_NAME, description.getTerm(), Concepts.REFSET_LANGUAGE_TYPE_UK)
//				))
//				.build();
//		final Revision actual = Iterables.getOnlyElement(processor.getChangedMappings().values()).getNewRevision();
//		assertDocEquals(expected, actual);
//		assertEquals(0, processor.getNewMappings().size());
//		assertEquals(0, processor.getDeletions().size());
//	}
	
}
