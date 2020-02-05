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
import static org.junit.Assert.assertEquals;

import java.util.Collections;

import org.junit.Test;

import com.b2international.index.revision.Revision;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

/**
 * @since 4.7
 */
public class RelationshipChangeProcessorTest extends BaseChangeProcessorTest {
	
	private RelationshipChangeProcessor processor = new RelationshipChangeProcessor();

	@Test
	public void newRelationship() throws Exception {
		final SnomedRelationshipIndexEntry relationship = createRandomRelationship();
		stageNew(relationship);
		
		process(processor);
		
		assertEquals(0, processor.getNewMappings().size());
		assertEquals(0, processor.getChangedMappings().size());
		assertEquals(0, processor.getDeletions().size());
	}
	
	@Test
	public void changedRelationshipGroup() throws Exception {
		final SnomedRelationshipIndexEntry relationship = createRandomRelationship();
		initRevisions(relationship);
		
		stageChange(relationship, SnomedRelationshipIndexEntry.builder(relationship)
				.group(relationship.getGroup() + 1)
				.build());
		
		process(processor);
		
		final SnomedRelationshipIndexEntry expected = SnomedRelationshipIndexEntry
				.builder(relationship)
				.group(relationship.getGroup() + 1)
				.build();
		final Revision actual = Iterables.getOnlyElement(processor.getChangedMappings().values()).getNewRevision();
		assertDocEquals(expected, actual);
		assertEquals(0, processor.getNewMappings().size());
		assertEquals(0, processor.getDeletions().size());
	}
	
	@Test
	public void detachedRelationship() throws Exception {
		final SnomedRelationshipIndexEntry relationship = createRandomRelationship();
		indexRevision(MAIN, SnomedRelationshipIndexEntry.builder(relationship).build());
		
		stageRemove(relationship);
		
		process(processor);
		
		assertEquals(0, processor.getNewMappings().size());
		assertEquals(0, processor.getChangedMappings().size());
		assertEquals(0, processor.getDeletions().size());
	}
	
	@Test
	public void addNewMemberToExistingRelationship() {
		final SnomedRelationshipIndexEntry relationship = createRandomRelationship();
		final String referringRefSetId = generateConceptId();
		final SnomedRefSetMemberIndexEntry member = simpleMember(relationship.getId(), referringRefSetId);
		
		initRevisions(relationship);

		stageNew(member);
		
		process(processor);
		
		final SnomedRelationshipIndexEntry expectedDoc = SnomedRelationshipIndexEntry
				.builder(relationship)
				.memberOf(Collections.singleton(referringRefSetId))
				.activeMemberOf(Collections.singleton(referringRefSetId))
				.build();
		
		final Revision currentDoc = Iterables.getOnlyElement(processor.getChangedMappings().values()).getNewRevision();
		assertDocEquals(expectedDoc, currentDoc);
		assertEquals(0, processor.getNewMappings().size());
		assertEquals(0, processor.getDeletions().size());
	}
	
	@Test
	public void deleteMemberOfExistingRelationship() {
		final SnomedRelationshipIndexEntry relationship = createRandomRelationship();
		final String referringRefSetId = generateConceptId();
		final SnomedRefSetMemberIndexEntry member = simpleMember(relationship.getId(), referringRefSetId);

		initRevisions(
			SnomedRelationshipIndexEntry.builder(relationship)
				.memberOf(ImmutableList.of(referringRefSetId))
				.activeMemberOf(ImmutableList.of(referringRefSetId))
			.build(),
			member
		);

		stageRemove(member);
		
		process(processor);
		
		final SnomedRelationshipIndexEntry expectedDoc = SnomedRelationshipIndexEntry
				.builder(relationship)
				.build();
		
		final Revision currentDoc = Iterables.getOnlyElement(processor.getChangedMappings().values()).getNewRevision();
		assertDocEquals(expectedDoc, currentDoc);
		assertEquals(0, processor.getNewMappings().size());
		assertEquals(0, processor.getDeletions().size());
	}
	
	@Test
	public void deleteOneMemberFromMultipleMembersOfRelationship() {
		final SnomedRelationshipIndexEntry relationship = createRandomRelationship();
		final String referringRefSetId = generateConceptId();
		
		final SnomedRefSetMemberIndexEntry member1 = simpleMember(relationship.getId(), referringRefSetId);
		final SnomedRefSetMemberIndexEntry member2 = simpleMember(relationship.getId(), referringRefSetId);
		
		initRevisions(
			SnomedRelationshipIndexEntry.builder(relationship)
				.memberOf(ImmutableList.of(referringRefSetId, referringRefSetId))
				.activeMemberOf(ImmutableList.of(referringRefSetId, referringRefSetId))
			.build(),
			member1,
			member2
		);

		stageRemove(member1);
		
		process(processor);
		
		final SnomedRelationshipIndexEntry expectedDoc = SnomedRelationshipIndexEntry
				.builder(relationship)
				.memberOf(Collections.singleton(referringRefSetId))
				.activeMemberOf(Collections.singleton(referringRefSetId))
				.build();
		
		final Revision currentDoc = Iterables.getOnlyElement(processor.getChangedMappings().values()).getNewRevision();
		assertDocEquals(expectedDoc, currentDoc);
		assertEquals(0, processor.getNewMappings().size());
		assertEquals(0, processor.getDeletions().size());
	}

}
