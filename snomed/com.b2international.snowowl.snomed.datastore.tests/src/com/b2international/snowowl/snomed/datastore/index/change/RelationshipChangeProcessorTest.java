/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
import com.b2international.snowowl.snomed.Relationship;
import com.b2international.snowowl.snomed.SnomedPackage;
import com.b2international.snowowl.snomed.core.tests.util.DocumentBuilders;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

/**
 * @since 4.7
 */
public class RelationshipChangeProcessorTest extends BaseChangeProcessorTest {
	
	private RelationshipChangeProcessor processor = new RelationshipChangeProcessor();

	@Test
	public void newRelationship() throws Exception {
		final Relationship relationship = createRandomRelationship();
		registerNew(relationship);
		
		process(processor);
		
		final SnomedRelationshipIndexEntry expected = SnomedRelationshipIndexEntry.builder(relationship).build();
		final Revision actual = Iterables.getOnlyElement(processor.getNewMappings().values());
		assertDocEquals(expected, actual);
		assertEquals(0, processor.getChangedMappings().size());
		assertEquals(0, processor.getDeletions().size());
	}
	
	@Test
	public void changedRelationship() throws Exception {
		final Relationship relationship = createRandomRelationship();
		registerDirty(relationship);
		indexRevision(MAIN, SnomedRelationshipIndexEntry.builder(relationship).build());
		
		process(processor);
		
		final SnomedRelationshipIndexEntry expected = SnomedRelationshipIndexEntry.builder(relationship).build();
		final Revision actual = Iterables.getOnlyElement(processor.getChangedMappings().values()).getNewRevision();
		assertDocEquals(expected, actual);
		assertEquals(0, processor.getNewMappings().size());
		assertEquals(0, processor.getDeletions().size());
	}
	
	@Test
	public void detachedRelationship() throws Exception {
		final Relationship relationship = createRandomRelationship();
		
		indexRevision(MAIN, SnomedRelationshipIndexEntry.builder(relationship).build());
		registerDetached(relationship.cdoID(), SnomedPackage.Literals.RELATIONSHIP);
		
		process(processor);
		
		assertEquals(0, processor.getNewMappings().size());
		assertEquals(0, processor.getChangedMappings().size());
		assertEquals(1, processor.getDeletions().size());
	}
	
	@Test
	public void addNewMemberToExistingRelationship() {
		final Relationship relationship = createRandomRelationship();
		final String referringRefSetId = generateConceptId();
		final SnomedRefSetMember member = createSimpleMember(relationship.getId(), referringRefSetId);
		
		registerExistingObject(relationship);
		indexRevision(MAIN, SnomedRelationshipIndexEntry.builder(relationship).build());
		registerNew(member);
		
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
		final Relationship relationship = createRandomRelationship();
		final String referringRefSetId = generateConceptId();
		final SnomedRefSetMember member = createSimpleMember(relationship.getId(), referringRefSetId);
		
		registerExistingObject(relationship);
		registerExistingObject(member);
		indexRevision(MAIN, SnomedRelationshipIndexEntry.builder(relationship)
					.memberOf(ImmutableList.of(referringRefSetId))
					.activeMemberOf(ImmutableList.of(referringRefSetId))
					.build());
		indexRevision(MAIN, SnomedRefSetMemberIndexEntry.builder(member).build());
		registerDetached(member.cdoID(), member.eClass());
		
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
		final Relationship relationship = createRandomRelationship();
		final String referringRefSetId = generateConceptId();
		
		final SnomedRefSetMember member1 = createSimpleMember(relationship.getId(), referringRefSetId);
		final SnomedRefSetMember member2 = createSimpleMember(relationship.getId(), referringRefSetId);
		
		registerExistingObject(relationship);
		registerExistingObject(member1);
		registerExistingObject(member2);
		
		indexRevision(MAIN, SnomedRelationshipIndexEntry.builder(relationship)
					.memberOf(ImmutableList.of(referringRefSetId, referringRefSetId))
					.activeMemberOf(ImmutableList.of(referringRefSetId, referringRefSetId))
					.build());
		indexRevision(MAIN, SnomedRefSetMemberIndexEntry.builder(member1).build());
		indexRevision(MAIN, SnomedRefSetMemberIndexEntry.builder(member2).build());
		
		registerDetached(member1.cdoID(), member1.eClass());
		
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
