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
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

import java.util.Collections;

import org.junit.Test;

import com.b2international.index.revision.Revision;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.Acceptability;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;

/**
 * @since 4.7
 */
public class DescriptionChangeProcessorTest extends BaseChangeProcessorTest {

	// test subject
	private DescriptionChangeProcessor processor = new DescriptionChangeProcessor();
	
	@Test
	public void addNewDescriptionWithoutLanguageMembers() throws Exception {
		SnomedDescriptionIndexEntry fsn = fsn(generateConceptId(), Collections.emptyMap());
		stageNew(fsn);
		
		process(processor);
		
		assertEquals(1, processor.getNewMappings().size());
		Revision actual = Iterables.getOnlyElement(processor.getNewMappings().values());
		assertDocEquals(fsn, actual);
		assertEquals(0, processor.getChangedMappings().size());
		assertEquals(0, processor.getDeletions().size());
	}
	
	@Test
	public void addNewDescriptionWithAcceptableLanguageMember() throws Exception {
		SnomedDescriptionIndexEntry fsn = fsn(generateConceptId(), Collections.emptyMap());
		SnomedRefSetMemberIndexEntry acceptableInUk = langMember(fsn.getId(), Acceptability.ACCEPTABLE, Concepts.REFSET_LANGUAGE_TYPE_UK);
		stageNew(fsn);
		stageNew(acceptableInUk);
		
		process(processor);
		
		final SnomedDescriptionIndexEntry expectedDoc = SnomedDescriptionIndexEntry.builder(fsn)
				.memberOf(ImmutableList.of(Concepts.REFSET_LANGUAGE_TYPE_UK)) // TODO fix me
				.activeMemberOf(ImmutableList.of(Concepts.REFSET_LANGUAGE_TYPE_UK))
				.acceptability(Concepts.REFSET_LANGUAGE_TYPE_UK, Acceptability.ACCEPTABLE)
				.build();
		final Revision currentDoc = Iterables.getOnlyElement(processor.getNewMappings().values());
		assertDocEquals(expectedDoc, currentDoc);
		assertEquals(0, processor.getChangedMappings().size());
		assertEquals(0, processor.getDeletions().size());
	}
	
	@Test
	public void addNewDescriptionWithPreferredLanguageMember() throws Exception {
		SnomedDescriptionIndexEntry fsn = fsn(generateConceptId(), Collections.emptyMap());
		SnomedRefSetMemberIndexEntry preferredInUk = langMember(fsn.getId(), Acceptability.PREFERRED, Concepts.REFSET_LANGUAGE_TYPE_UK);
		stageNew(fsn);
		stageNew(preferredInUk);
		
		process(processor);
		
		final SnomedDescriptionIndexEntry expectedDoc = SnomedDescriptionIndexEntry.builder(fsn)
				.memberOf(ImmutableList.of(Concepts.REFSET_LANGUAGE_TYPE_UK))
				.activeMemberOf(ImmutableList.of(Concepts.REFSET_LANGUAGE_TYPE_UK))
				.acceptability(Concepts.REFSET_LANGUAGE_TYPE_UK, Acceptability.PREFERRED)
				.build();
		final Revision currentDoc = Iterables.getOnlyElement(processor.getNewMappings().values());
		assertDocEquals(expectedDoc, currentDoc);
		assertEquals(0, processor.getChangedMappings().size());
		assertEquals(0, processor.getDeletions().size());
	}
	
	@Test
	public void deleteAcceptableLanguageMember() throws Exception {
		// create description as dirty
		final SnomedDescriptionIndexEntry fsn = fsn(generateConceptId(), ImmutableMap.of(
			Concepts.REFSET_LANGUAGE_TYPE_UK, Acceptability.PREFERRED,
			Concepts.REFSET_LANGUAGE_TYPE_US, Acceptability.ACCEPTABLE
		));
		final SnomedRefSetMemberIndexEntry acceptableInUs = langMember(fsn.getId(), Acceptability.ACCEPTABLE, Concepts.REFSET_LANGUAGE_TYPE_US);
		final SnomedRefSetMemberIndexEntry preferredInUk = langMember(fsn.getId(), Acceptability.PREFERRED, Concepts.REFSET_LANGUAGE_TYPE_UK);
		
		// index current revisions, so change processor can find them (both the description and the members)
		initRevisions(fsn, acceptableInUs, preferredInUk);

		stageRemove(acceptableInUs);
		
		process(processor);
		
		// expected that the new doc will have only the preferred acceptability
		final SnomedDescriptionIndexEntry expectedDoc = SnomedDescriptionIndexEntry.builder(fsn)
				.acceptabilityMap(Collections.singletonMap(Concepts.REFSET_LANGUAGE_TYPE_UK, Acceptability.PREFERRED))
				.build();
		final Revision currentDoc = Iterables.getOnlyElement(processor.getChangedMappings().values()).getNewRevision();
		assertDocEquals(expectedDoc, currentDoc);
		// XXX the deleted member handled by another processor
		assertEquals(0, processor.getNewMappings().size());
		assertEquals(0, processor.getDeletions().size());
	}
	
	@Test
	public void deletePreferredLanguageMember() throws Exception {
		// create description as dirty
		final SnomedDescriptionIndexEntry fsn = fsn(generateConceptId(), ImmutableMap.of(
			Concepts.REFSET_LANGUAGE_TYPE_UK, Acceptability.PREFERRED,
			Concepts.REFSET_LANGUAGE_TYPE_US, Acceptability.ACCEPTABLE
		));
		final SnomedRefSetMemberIndexEntry acceptableInUs = langMember(fsn.getId(), Acceptability.ACCEPTABLE, Concepts.REFSET_LANGUAGE_TYPE_US);
		final SnomedRefSetMemberIndexEntry preferredInUk = langMember(fsn.getId(), Acceptability.PREFERRED, Concepts.REFSET_LANGUAGE_TYPE_UK);
		
		// index current revisions, so change processor can find them (both the description and the members)
		initRevisions(fsn, acceptableInUs, preferredInUk);

		stageRemove(preferredInUk);
		
		process(processor);
		
		// expected that the new doc will have only the preferred acceptability
		final SnomedDescriptionIndexEntry expectedDoc = SnomedDescriptionIndexEntry.builder(fsn)
				.acceptabilityMap(Collections.singletonMap(Concepts.REFSET_LANGUAGE_TYPE_US, Acceptability.ACCEPTABLE))
				.build();
		final Revision currentDoc = Iterables.getOnlyElement(processor.getChangedMappings().values()).getNewRevision();
		assertDocEquals(expectedDoc, currentDoc);
		// XXX the deleted member handled by another processor
		assertEquals(0, processor.getNewMappings().size());
		assertEquals(0, processor.getDeletions().size());
	}
	
	@Test
	public void changeDescriptionCaseSignificance() throws Exception {
		final SnomedDescriptionIndexEntry fsn = fsn(generateConceptId(), Collections.emptyMap());
		initRevisions(fsn);
		
		stageChange(fsn, SnomedDescriptionIndexEntry.builder(fsn).caseSignificanceId(Concepts.ENTIRE_TERM_CASE_INSENSITIVE).build());
		
		process(processor);
		
		final SnomedDescriptionIndexEntry expectedDoc = SnomedDescriptionIndexEntry.builder(fsn)
				.caseSignificanceId(Concepts.ENTIRE_TERM_CASE_INSENSITIVE)
				.build();
		final Revision currentDoc = Iterables.getOnlyElement(processor.getChangedMappings().values()).getNewRevision();
		assertDocEquals(expectedDoc, currentDoc);
		assertEquals(0, processor.getNewMappings().size());
		assertEquals(0, processor.getDeletions().size());
	}
	
	@Test
	public void changeLanguageMemberAcceptability() throws Exception {
		final SnomedDescriptionIndexEntry fsn = fsn(generateConceptId(), ImmutableMap.of(
			Concepts.REFSET_LANGUAGE_TYPE_UK, Acceptability.ACCEPTABLE
		));
		final SnomedRefSetMemberIndexEntry acceptableInUk = langMember(fsn.getId(), Acceptability.ACCEPTABLE, Concepts.REFSET_LANGUAGE_TYPE_UK);
		
		initRevisions(fsn, acceptableInUk);
		
		// make the change
		stageChange(acceptableInUk, SnomedRefSetMemberIndexEntry.builder(acceptableInUk).field(SnomedRf2Headers.FIELD_ACCEPTABILITY_ID, Acceptability.PREFERRED.getConceptId()).build());
		
		process(processor);
		
		
		assertEquals(1, processor.getChangedMappings().size());
		
		// description doc must be reindexed with change acceptabilityMap
		final SnomedDescriptionIndexEntry expectedDoc = SnomedDescriptionIndexEntry.builder(fsn).
				acceptabilityMap(Collections.singletonMap(Concepts.REFSET_LANGUAGE_TYPE_UK, Acceptability.PREFERRED))
				.build();
		final Revision currentDoc = Iterables.getOnlyElement(processor.getChangedMappings().values()).getNewRevision();
		assertDocEquals(expectedDoc, currentDoc);
		assertEquals(0, processor.getNewMappings().size());
		assertEquals(0, processor.getDeletions().size());
	}

	@Test
	public void deleteDescription() throws Exception {
		final SnomedDescriptionIndexEntry fsn = fsn(generateConceptId(), Collections.emptyMap());
		initRevisions(fsn);
		
		stageRemove(fsn);
		
		process(processor);
		
		assertThat(processor.getNewMappings()).isEmpty();
		assertThat(processor.getChangedMappings()).isEmpty();
		assertThat(processor.getDeletions().asMap()).isEmpty();
	}
	
	@Test
	public void addNewMemberToNewDescription() {
		final SnomedDescriptionIndexEntry fsn = fsn(generateConceptId(), Collections.emptyMap());
		final String referringRefSetId = generateConceptId();
		final SnomedRefSetMemberIndexEntry member = simpleMember(fsn.getId(), referringRefSetId);
		
		stageNew(fsn);
		stageNew(member);
		
		process(processor);
		
		final SnomedDescriptionIndexEntry expectedDoc = SnomedDescriptionIndexEntry
				.builder(fsn)
				.memberOf(Collections.singleton(referringRefSetId))
				.activeMemberOf(Collections.singleton(referringRefSetId))
				.build();
		
		final Revision currentDoc = Iterables.getOnlyElement(processor.getNewMappings().values());
		assertDocEquals(expectedDoc, currentDoc);
		assertEquals(0, processor.getChangedMappings().size());
		assertEquals(0, processor.getDeletions().size());
	}
	
	@Test
	public void addNewMemberToExistingDescription() {
		final SnomedDescriptionIndexEntry fsn = fsn(generateConceptId(), Collections.emptyMap());
		initRevisions(fsn);
		
		final String referringRefSetId = generateConceptId();
		final SnomedRefSetMemberIndexEntry member = simpleMember(fsn.getId(), referringRefSetId);
		
		stageNew(member);
		
		process(processor);
		
		final SnomedDescriptionIndexEntry expectedDoc = SnomedDescriptionIndexEntry
				.builder(fsn)
				.memberOf(Collections.singleton(referringRefSetId))
				.activeMemberOf(Collections.singleton(referringRefSetId))
				.build();
		
		final Revision currentDoc = Iterables.getOnlyElement(processor.getChangedMappings().values()).getNewRevision();
		assertDocEquals(expectedDoc, currentDoc);
		assertEquals(0, processor.getNewMappings().size());
		assertEquals(0, processor.getDeletions().size());
	}
	
	@Test
	public void deleteMemberOfDescription() {
		final SnomedDescriptionIndexEntry fsn = fsn(generateConceptId(), Collections.emptyMap());
		final String referringRefSetId = generateConceptId();
		final SnomedRefSetMemberIndexEntry member = simpleMember(fsn.getId(), referringRefSetId);
		
		initRevisions(
			SnomedDescriptionIndexEntry
				.builder(fsn)
				.memberOf(Collections.singleton(referringRefSetId))
				.activeMemberOf(Collections.singleton(referringRefSetId))
				.build(), 
			member
		);

		stageRemove(member);
		
		process(processor);
		
		final SnomedDescriptionIndexEntry expectedDoc = SnomedDescriptionIndexEntry
				.builder(fsn)
				.build();
		
		final Revision currentDoc = Iterables.getOnlyElement(processor.getChangedMappings().values()).getNewRevision();
		assertDocEquals(expectedDoc, currentDoc);
		assertEquals(0, processor.getNewMappings().size());
		assertEquals(0, processor.getDeletions().size());
	}
	
	@Test
	public void deleteOneMemberFromMultipleMembersOfDescription() {
		final SnomedDescriptionIndexEntry fsn = fsn(generateConceptId(), Collections.emptyMap());
		final String referringRefSetId = generateConceptId();
		final SnomedRefSetMemberIndexEntry member1 = simpleMember(fsn.getId(), referringRefSetId);
		final SnomedRefSetMemberIndexEntry member2 = simpleMember(fsn.getId(), referringRefSetId);
		
		initRevisions(
			SnomedDescriptionIndexEntry
				.builder(fsn)
				.memberOf(ImmutableList.of(referringRefSetId, referringRefSetId))
				.activeMemberOf(ImmutableList.of(referringRefSetId, referringRefSetId))
				.build(), 
			member1,
			member2
		);
		
		stageRemove(member1);
		
		process(processor);
		
		final SnomedDescriptionIndexEntry expectedDoc = SnomedDescriptionIndexEntry
				.builder(fsn)
				.memberOf(Collections.singleton(referringRefSetId))
				.activeMemberOf(Collections.singleton(referringRefSetId))
				.build();
		
		final Revision currentDoc = Iterables.getOnlyElement(processor.getChangedMappings().values()).getNewRevision();
		assertDocEquals(expectedDoc, currentDoc);
		assertEquals(0, processor.getNewMappings().size());
		assertEquals(0, processor.getDeletions().size());
	}
	
}
