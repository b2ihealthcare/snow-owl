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
package com.b2international.snowowl.snomed.datastore.index.change;

import static com.b2international.snowowl.snomed.datastore.id.RandomSnomedIdentiferGenerator.generateConceptId;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.b2international.index.revision.Revision;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.request.SnomedOWLExpressionConverter;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage;
import com.b2international.snowowl.test.commons.snomed.TestBranchContext;
import com.google.common.collect.Iterables;

/**
 * @since 4.7
 */
public class RefSetMemberChangeProcessorTest extends BaseChangeProcessorTest {
	
	private RefSetMemberChangeProcessor processor = new RefSetMemberChangeProcessor(new SnomedOWLExpressionConverter(TestBranchContext.on(MAIN).build()));

	@Test
	public void newMember() throws Exception {
		final SnomedRefSetMember member = createSimpleMember(generateConceptId(), generateConceptId());
		registerNew(member);
		
		process(processor);
		
		final SnomedRefSetMemberIndexEntry expected = SnomedRefSetMemberIndexEntry.builder(member).build();
		final Revision actual = Iterables.getOnlyElement(processor.getNewMappings().values());
		assertDocEquals(expected, actual);
		assertEquals(0, processor.getChangedMappings().size());
		assertEquals(0, processor.getDeletions().size());
	}
	
	@Test
	public void changedMember() throws Exception {
		final SnomedRefSetMember member = createSimpleMember(generateConceptId(), generateConceptId());
		registerDirty(member);
		
		process(processor);
		
		final SnomedRefSetMemberIndexEntry expected = SnomedRefSetMemberIndexEntry.builder(member).build();
		final Revision actual = Iterables.getOnlyElement(processor.getChangedMappings().values());
		assertDocEquals(expected, actual);
		assertEquals(0, processor.getNewMappings().size());
		assertEquals(0, processor.getDeletions().size());
	}
	
	@Test
	public void detachedMember() throws Exception {
		registerDetached(nextStorageKeyAsCDOID(), SnomedRefSetPackage.Literals.SNOMED_REF_SET_MEMBER);
		
		process(processor);
		
		assertEquals(0, processor.getNewMappings().size());
		assertEquals(0, processor.getChangedMappings().size());
		assertEquals(1, processor.getDeletions().size());
	}
	
	@Test
	public void detachedMultipleMembersWithDifferentType() throws Exception {
		registerDetached(nextStorageKeyAsCDOID(), SnomedRefSetPackage.Literals.SNOMED_SIMPLE_MAP_REF_SET_MEMBER);
		registerDetached(nextStorageKeyAsCDOID(), SnomedRefSetPackage.Literals.SNOMED_LANGUAGE_REF_SET_MEMBER);
		
		process(processor);
		
		assertEquals(0, processor.getNewMappings().size());
		assertEquals(0, processor.getChangedMappings().size());
		assertEquals(2, processor.getDeletions().size());
	}

}
