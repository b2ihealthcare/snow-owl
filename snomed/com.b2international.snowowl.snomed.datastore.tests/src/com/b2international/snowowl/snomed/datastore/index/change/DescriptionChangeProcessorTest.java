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
import java.util.Map.Entry;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.junit.Test;

import com.b2international.index.revision.Revision;
import com.b2international.snowowl.snomed.Description;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.SnomedPackage;
import com.b2international.snowowl.snomed.core.domain.Acceptability;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.snomedrefset.SnomedLanguageRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

/**
 * @since 4.7
 */
public class DescriptionChangeProcessorTest extends BaseChangeProcessorTest {

	// test subject
	private DescriptionChangeProcessor processor = new DescriptionChangeProcessor();
	
	@Test
	public void addNewDescriptionWithoutLanguageMembers() throws Exception {
		final Description description = createDescription(Concepts.FULLY_SPECIFIED_NAME, "Example FSN");
		registerNew(description);
		
		process(processor);
		
		final SnomedDescriptionIndexEntry expectedDoc = SnomedDescriptionIndexEntry.builder(description).build();
		final Revision currentDoc = Iterables.getOnlyElement(processor.getNewMappings().values());
		assertDocEquals(expectedDoc, currentDoc);
		assertEquals(0, processor.getChangedMappings().size());
		assertEquals(0, processor.getDeletions().size());
	}
	
	@Test
	public void addNewDescriptionWithAcceptableLanguageMember() throws Exception {
		final Description description = createDescription(Concepts.FULLY_SPECIFIED_NAME, "Example FSN");
		final SnomedLanguageRefSetMember acceptableMember = createLangMember(description.getId(), Acceptability.ACCEPTABLE, Concepts.REFSET_LANGUAGE_TYPE_UK);
		description.getLanguageRefSetMembers().add(acceptableMember);
		registerNew(description);
		registerNew(acceptableMember);
		
		process(processor);
		
		final SnomedDescriptionIndexEntry expectedDoc = SnomedDescriptionIndexEntry.builder(description)
				.memberOf(ImmutableList.of(Concepts.REFSET_LANGUAGE_TYPE_UK))
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
		final Description description = createDescription(Concepts.FULLY_SPECIFIED_NAME, "Example FSN");
		final SnomedLanguageRefSetMember acceptableMember = createLangMember(description.getId(), Acceptability.PREFERRED, Concepts.REFSET_LANGUAGE_TYPE_UK);
		description.getLanguageRefSetMembers().add(acceptableMember);
		registerNew(description);
		registerNew(acceptableMember);
		
		process(processor);
		
		final SnomedDescriptionIndexEntry expectedDoc = SnomedDescriptionIndexEntry.builder(description)
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
		final Description description = createFsnWithTwoAcceptabilityMembers();
		final SnomedLanguageRefSetMember acceptableMember = getFirstMember(description, Acceptability.ACCEPTABLE);
		final SnomedLanguageRefSetMember preferredMember = getFirstMember(description, Acceptability.PREFERRED);
		
		// index current revisions, so change processor can find them (both the description and the members)
		indexRevision(MAIN, SnomedDescriptionIndexEntry.builder(description)
				.storageKey(CDOIDUtil.getLong(description.cdoID()))
				.acceptability(Concepts.REFSET_LANGUAGE_TYPE_UK, Acceptability.PREFERRED)
				.acceptability(Concepts.REFSET_LANGUAGE_TYPE_US, Acceptability.ACCEPTABLE)
				.build());
		indexRevision(MAIN, SnomedRefSetMemberIndexEntry.builder(acceptableMember)
				.storageKey(CDOIDUtil.getLong(acceptableMember.cdoID()))
				.build());
		indexRevision(MAIN, SnomedRefSetMemberIndexEntry.builder(preferredMember)
				.storageKey(CDOIDUtil.getLong(preferredMember.cdoID()))
				.build());
		
		// remove the acceptableMember and mark the description as dirty
		description.getLanguageRefSetMembers().remove(acceptableMember);
		registerDirty(description);
		// delete the acceptable member of the description
		registerDetached(acceptableMember.cdoID(), SnomedRefSetPackage.Literals.SNOMED_LANGUAGE_REF_SET_MEMBER);
		
		process(processor);
		
		// expected that the new doc will have only the preferred acceptability
		final SnomedDescriptionIndexEntry expectedDoc = SnomedDescriptionIndexEntry.builder(description)
				.acceptability(Concepts.REFSET_LANGUAGE_TYPE_UK, Acceptability.PREFERRED)
				.build();
		final Revision currentDoc = Iterables.getOnlyElement(processor.getChangedMappings().values());
		assertDocEquals(expectedDoc, currentDoc);
		// XXX the deleted member handled by another processor
		assertEquals(0, processor.getNewMappings().size());
		assertEquals(0, processor.getDeletions().size());
	}
	
	@Test
	public void deletePreferredLanguageMember() throws Exception {
		// create description as dirty
		final Description description = createFsnWithTwoAcceptabilityMembers();
		final SnomedLanguageRefSetMember acceptableMember = getFirstMember(description, Acceptability.ACCEPTABLE);
		final SnomedLanguageRefSetMember preferredMember = getFirstMember(description, Acceptability.PREFERRED);
		
		// index current revisions, so change processor can find them (both the description and the members)
		indexRevision(MAIN, SnomedDescriptionIndexEntry.builder(description)
				.storageKey(CDOIDUtil.getLong(description.cdoID()))
				.acceptability(Concepts.REFSET_LANGUAGE_TYPE_UK, Acceptability.PREFERRED)
				.acceptability(Concepts.REFSET_LANGUAGE_TYPE_US, Acceptability.ACCEPTABLE)
				.build());
		indexRevision(MAIN, SnomedRefSetMemberIndexEntry.builder(acceptableMember)
				.storageKey(CDOIDUtil.getLong(acceptableMember.cdoID()))
				.build());
		indexRevision(MAIN, SnomedRefSetMemberIndexEntry.builder(preferredMember)
				.storageKey(CDOIDUtil.getLong(preferredMember.cdoID()))
				.build());
		
		// remove the acceptableMember and mark the description as dirty
		description.getLanguageRefSetMembers().remove(preferredMember);
		registerDirty(description);
		// delete the acceptable member of the description
		registerDetached(preferredMember.cdoID(), SnomedRefSetPackage.Literals.SNOMED_LANGUAGE_REF_SET_MEMBER);
		
		process(processor);
		
		// expected that the new doc will have only the preferred acceptability
		final SnomedDescriptionIndexEntry expectedDoc = SnomedDescriptionIndexEntry.builder(description)
				.acceptability(Concepts.REFSET_LANGUAGE_TYPE_US, Acceptability.ACCEPTABLE)
				.build();
		final Revision currentDoc = Iterables.getOnlyElement(processor.getChangedMappings().values());
		assertDocEquals(expectedDoc, currentDoc);
		// XXX the deleted member handled by another processor
		assertEquals(0, processor.getNewMappings().size());
		assertEquals(0, processor.getDeletions().size());
	}
	
	@Test
	public void changeDescriptionCasesignificance() throws Exception {
		final Description description = createDescription(Concepts.FULLY_SPECIFIED_NAME, "Example FSN");
		indexRevision(MAIN, SnomedDescriptionIndexEntry.builder(description)
				.storageKey(CDOIDUtil.getLong(description.cdoID()))
				.build());
		description.setCaseSignificance(getConcept(Concepts.ENTIRE_TERM_CASE_INSENSITIVE));
		registerDirty(description);
		
		process(processor);
		
		final SnomedDescriptionIndexEntry expectedDoc = SnomedDescriptionIndexEntry.builder(description).build();
		final Revision currentDoc = Iterables.getOnlyElement(processor.getChangedMappings().values());
		assertDocEquals(expectedDoc, currentDoc);
		assertEquals(0, processor.getNewMappings().size());
		assertEquals(0, processor.getDeletions().size());
	}
	
	@Test
	public void changeLanguageMemberAcceptability() throws Exception {
		final Description description = createDescription(Concepts.FULLY_SPECIFIED_NAME, "Example FSN");
		final SnomedLanguageRefSetMember acceptableMember = createLangMember(description.getId(), Acceptability.ACCEPTABLE, Concepts.REFSET_LANGUAGE_TYPE_UK);
		description.getLanguageRefSetMembers().add(acceptableMember);
		// index revisions for previous state
		indexRevision(MAIN, SnomedDescriptionIndexEntry.builder(description).acceptability(Concepts.REFSET_LANGUAGE_TYPE_UK, Acceptability.ACCEPTABLE)
				.storageKey(CDOIDUtil.getLong(description.cdoID()))
				.build());
		indexRevision(MAIN, SnomedRefSetMemberIndexEntry.builder(acceptableMember)
				.storageKey(CDOIDUtil.getLong(acceptableMember.cdoID()))
				.build());
		
		// make the change
		acceptableMember.setAcceptabilityId(Acceptability.PREFERRED.getConceptId());
		registerDirty(acceptableMember);
		
		process(processor);
		
		// description doc must be reindexed with change acceptabilityMap
		final SnomedDescriptionIndexEntry expectedDoc = SnomedDescriptionIndexEntry.builder(description).
				acceptability(Concepts.REFSET_LANGUAGE_TYPE_UK, Acceptability.PREFERRED).build();
		final Revision currentDoc = Iterables.getOnlyElement(processor.getChangedMappings().values());
		assertDocEquals(expectedDoc, currentDoc);
		assertEquals(0, processor.getNewMappings().size());
		assertEquals(0, processor.getDeletions().size());
	}

	@Test
	public void deleteDescription() throws Exception {
		final CDOID storageKey = nextStorageKeyAsCDOID();
		registerDetached(storageKey, SnomedPackage.Literals.DESCRIPTION);
		
		process(processor);
		
		assertThat(processor.getNewMappings()).isEmpty();
		assertThat(processor.getChangedMappings()).isEmpty();
		assertEquals(1, processor.getDeletions().size());
		final Entry<Class<? extends Revision>, Long> deletionEntry = Iterables.getOnlyElement(processor.getDeletions().entries());
		assertEquals(SnomedDescriptionIndexEntry.class, deletionEntry.getKey());
		assertEquals(CDOIDUtil.getLong(storageKey), deletionEntry.getValue().longValue());
	}
	
	@Test
	public void addNewMemberToNewDescription() {
		final Description description = createDescription(Concepts.FULLY_SPECIFIED_NAME, "Example FSN");
		final String referringRefSetId = generateConceptId();
		final SnomedRefSetMember member = createSimpleMember(description.getId(), referringRefSetId);
		
		registerNew(description);
		registerNew(member);
		
		process(processor);
		
		final SnomedDescriptionIndexEntry expectedDoc = SnomedDescriptionIndexEntry
				.builder(description)
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
		final Description description = createDescription(Concepts.FULLY_SPECIFIED_NAME, "Example FSN");
		final String referringRefSetId = generateConceptId();
		final SnomedRefSetMember member = createSimpleMember(description.getId(), referringRefSetId);
		
		registerExistingObject(description);
		indexRevision(MAIN, SnomedDescriptionIndexEntry.builder(description)
				.storageKey(CDOIDUtil.getLong(description.cdoID()))
				.build());
		registerNew(member);
		
		process(processor);
		
		final SnomedDescriptionIndexEntry expectedDoc = SnomedDescriptionIndexEntry
				.builder(description)
				.memberOf(Collections.singleton(referringRefSetId))
				.activeMemberOf(Collections.singleton(referringRefSetId))
				.build();
		
		final Revision currentDoc = Iterables.getOnlyElement(processor.getChangedMappings().values());
		assertDocEquals(expectedDoc, currentDoc);
		assertEquals(0, processor.getNewMappings().size());
		assertEquals(0, processor.getDeletions().size());
	}
	
	@Test
	public void deleteMemberOfDescription() {
		final Description description = createDescription(Concepts.FULLY_SPECIFIED_NAME, "Example FSN");
		final String referringRefSetId = generateConceptId();
		final SnomedRefSetMember member = createSimpleMember(description.getId(), referringRefSetId);
		
		registerExistingObject(description);
		registerExistingObject(member);
		
		indexRevision(MAIN, SnomedDescriptionIndexEntry.builder(description).storageKey(CDOIDUtil.getLong(description.cdoID())).build());
		indexRevision(MAIN, SnomedRefSetMemberIndexEntry.builder(member).storageKey(CDOIDUtil.getLong(member.cdoID())).build());
		
		registerDetached(member.cdoID(), SnomedRefSetPackage.Literals.SNOMED_REF_SET_MEMBER);
		
		process(processor);
		
		final SnomedDescriptionIndexEntry expectedDoc = SnomedDescriptionIndexEntry
				.builder(description)
				.build();
		
		final Revision currentDoc = Iterables.getOnlyElement(processor.getChangedMappings().values());
		assertDocEquals(expectedDoc, currentDoc);
		assertEquals(0, processor.getNewMappings().size());
		assertEquals(0, processor.getDeletions().size());
	}
	
	@Test
	public void deleteOneMemberFromMultipleMembersOfDescription() {
		final Description description = createDescription(Concepts.FULLY_SPECIFIED_NAME, "Example FSN");
		final String referringRefSetId = generateConceptId();
		final SnomedRefSetMember member1 = createSimpleMember(description.getId(), referringRefSetId);
		final SnomedRefSetMember member2 = createSimpleMember(description.getId(), referringRefSetId);
		
		registerExistingObject(description);
		registerExistingObject(member1);
		registerExistingObject(member2);
		
		indexRevision(MAIN, SnomedDescriptionIndexEntry.builder(description)
					.storageKey(CDOIDUtil.getLong(description.cdoID()))
					.memberOf(ImmutableList.of(referringRefSetId, referringRefSetId))
					.activeMemberOf(ImmutableList.of(referringRefSetId, referringRefSetId))
					.build());
		indexRevision(MAIN, SnomedRefSetMemberIndexEntry.builder(member1).storageKey(CDOIDUtil.getLong(member1.cdoID())).build());
		indexRevision(MAIN, SnomedRefSetMemberIndexEntry.builder(member2).storageKey(CDOIDUtil.getLong(member2.cdoID())).build());
		
		registerDetached(member1.cdoID(), member1.eClass());
		
		process(processor);
		
		final SnomedDescriptionIndexEntry expectedDoc = SnomedDescriptionIndexEntry
				.builder(description)
				.memberOf(Collections.singleton(referringRefSetId))
				.activeMemberOf(Collections.singleton(referringRefSetId))
				.build();
		
		final Revision currentDoc = Iterables.getOnlyElement(processor.getChangedMappings().values());
		assertDocEquals(expectedDoc, currentDoc);
		assertEquals(0, processor.getNewMappings().size());
		assertEquals(0, processor.getDeletions().size());
	}
	
	// Fixture helpers
	
	private SnomedLanguageRefSetMember getFirstMember(Description description, Acceptability acceptability) {
		for (SnomedLanguageRefSetMember member : description.getLanguageRefSetMembers()) {
			if (acceptability.getConceptId().equals(member.getAcceptabilityId())) {
				return member;
			}
		}
		return null;
	}

}
