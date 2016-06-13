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
package com.b2international.snowowl.snomed.datastore.index.entry;

import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

import org.junit.Test;

import com.b2international.index.revision.BaseRevisionIndexTest;
import com.b2international.index.revision.RevisionBranch;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry.Fields;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;

/**
 * @since 4.7
 */
public class SnomedRefSetMemberDocumentSerializationTest extends BaseRevisionIndexTest {

	@Override
	protected Collection<Class<?>> getTypes() {
		return Collections.<Class<?>>singleton(SnomedRefSetMemberIndexEntry.class);
	}
	
	@Test
	public void indexSimpleMember() throws Exception {
		final SnomedRefSetMemberIndexEntry member = SnomedRefSetMemberIndexEntry.builder()
			.id(UUID.randomUUID().toString())
			.active(true)
			.effectiveTime(EffectiveTimes.UNSET_EFFECTIVE_TIME)
			.released(false)
			.moduleId(Concepts.MODULE_SCT_CORE)
			.referencedComponentId(Concepts.ROOT_CONCEPT)
			.referenceSetId(Concepts.REFSET_B2I_EXAMPLE)
			.referenceSetType(SnomedRefSetType.ASSOCIATION)
			.build();
		
		indexRevision(RevisionBranch.MAIN_PATH, STORAGE_KEY1, member);
		final SnomedRefSetMemberIndexEntry actual = getRevision(RevisionBranch.MAIN_PATH, SnomedRefSetMemberIndexEntry.class, STORAGE_KEY1);
		assertEquals(STORAGE_KEY1, actual.getStorageKey());
		assertDocEquals(member, actual);
	}
	
	@Test
	public void indexSimpleMapMember() throws Exception {
		final SnomedRefSetMemberIndexEntry member = SnomedRefSetMemberIndexEntry.builder()
				.id(UUID.randomUUID().toString())
				.active(true)
				.effectiveTime(EffectiveTimes.UNSET_EFFECTIVE_TIME)
				.released(false)
				.moduleId(Concepts.MODULE_SCT_CORE)
				.referencedComponentId(Concepts.ROOT_CONCEPT)
				.referenceSetId(Concepts.REFSET_B2I_EXAMPLE)
				.referenceSetType(SnomedRefSetType.SIMPLE_MAP)
				.additionalField(Fields.MAP_TARGET, "A01")
				.build();
			
		indexRevision(RevisionBranch.MAIN_PATH, STORAGE_KEY1, member);
		final SnomedRefSetMemberIndexEntry actual = getRevision(RevisionBranch.MAIN_PATH, SnomedRefSetMemberIndexEntry.class, STORAGE_KEY1);
		assertEquals(STORAGE_KEY1, actual.getStorageKey());
		assertDocEquals(member, actual);
	}
	
}
