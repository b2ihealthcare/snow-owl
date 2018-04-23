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
package com.b2international.snowowl.snomed.datastore.index.entry;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

import org.junit.Test;

import com.b2international.index.revision.BaseRevisionIndexTest;
import com.b2international.index.revision.RevisionBranch;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry.Fields;
import com.b2international.snowowl.snomed.snomedrefset.DataType;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @since 4.7
 */
public class SnomedRefSetMemberDocumentSerializationTest extends BaseRevisionIndexTest {

	@Override
	protected Collection<Class<?>> getTypes() {
		return Collections.<Class<?>>singleton(SnomedRefSetMemberIndexEntry.class);
	}
	
	@Override
	protected void configureMapper(ObjectMapper mapper) {
		super.configureMapper(mapper);
		mapper.setSerializationInclusion(Include.NON_NULL);
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
			.referencedComponentType(SnomedTerminologyComponentConstants.CONCEPT_NUMBER)
			.referenceSetId(Concepts.REFSET_B2I_EXAMPLE)
			.referenceSetType(SnomedRefSetType.ASSOCIATION)
			.build();
		
		indexRevision(RevisionBranch.MAIN_PATH, STORAGE_KEY1, member);
		final SnomedRefSetMemberIndexEntry actual = getRevision(RevisionBranch.MAIN_PATH, SnomedRefSetMemberIndexEntry.class, STORAGE_KEY1);
		assertEquals(STORAGE_KEY1, actual.getStorageKey());
		assertEquals(SnomedTerminologyComponentConstants.CONCEPT_NUMBER, actual.getReferencedComponentType());
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
				.referencedComponentType(SnomedTerminologyComponentConstants.CONCEPT_NUMBER)
				.referenceSetId(Concepts.REFSET_B2I_EXAMPLE)
				.referenceSetType(SnomedRefSetType.SIMPLE_MAP)
				.field(Fields.MAP_TARGET, "A01")
				.build();
			
		indexRevision(RevisionBranch.MAIN_PATH, STORAGE_KEY1, member);
		final SnomedRefSetMemberIndexEntry actual = getRevision(RevisionBranch.MAIN_PATH, SnomedRefSetMemberIndexEntry.class, STORAGE_KEY1);
		assertEquals(STORAGE_KEY1, actual.getStorageKey());
		assertEquals(SnomedTerminologyComponentConstants.CONCEPT_NUMBER, actual.getReferencedComponentType());
		assertDocEquals(member, actual);
	}
	
	@Test
	public void indexLanguageMember() throws Exception {
		final SnomedRefSetMemberIndexEntry member = SnomedRefSetMemberIndexEntry.builder()
				.id(UUID.randomUUID().toString())
				.active(true)
				.effectiveTime(EffectiveTimes.UNSET_EFFECTIVE_TIME)
				.released(false)
				.moduleId(Concepts.MODULE_SCT_CORE)
				// TODO use description ID in test case
				.referencedComponentId(Concepts.ROOT_CONCEPT)
				.referencedComponentType(SnomedTerminologyComponentConstants.DESCRIPTION_NUMBER)
				.referenceSetId(Concepts.REFSET_B2I_EXAMPLE)
				.referenceSetType(SnomedRefSetType.LANGUAGE)
				.field(Fields.ACCEPTABILITY_ID, Concepts.REFSET_DESCRIPTION_ACCEPTABILITY_PREFERRED)
				.build();
			
		indexRevision(RevisionBranch.MAIN_PATH, STORAGE_KEY1, member);
		final SnomedRefSetMemberIndexEntry actual = getRevision(RevisionBranch.MAIN_PATH, SnomedRefSetMemberIndexEntry.class, STORAGE_KEY1);
		assertEquals(STORAGE_KEY1, actual.getStorageKey());
		assertEquals(SnomedTerminologyComponentConstants.DESCRIPTION_NUMBER, actual.getReferencedComponentType());
		assertDocEquals(member, actual);
	}
	
	@Test
	public void indexStringConcreteDomainMember() throws Exception {
		final SnomedRefSetMemberIndexEntry member = SnomedRefSetMemberIndexEntry.builder()
				.id(UUID.randomUUID().toString())
				.active(true)
				.effectiveTime(EffectiveTimes.UNSET_EFFECTIVE_TIME)
				.released(false)
				.moduleId(Concepts.MODULE_SCT_CORE)
				.referencedComponentId(Concepts.ROOT_CONCEPT)
				.referencedComponentType(SnomedTerminologyComponentConstants.CONCEPT_NUMBER)
				.referenceSetId(Concepts.REFSET_B2I_EXAMPLE)
				.referenceSetType(SnomedRefSetType.CONCRETE_DATA_TYPE)
				.field(Fields.DATA_TYPE, DataType.STRING)
				.field(SnomedRf2Headers.FIELD_VALUE, "TEST")
				.build();
			
		indexRevision(RevisionBranch.MAIN_PATH, STORAGE_KEY1, member);
		final SnomedRefSetMemberIndexEntry actual = getRevision(RevisionBranch.MAIN_PATH, SnomedRefSetMemberIndexEntry.class, STORAGE_KEY1);
		assertEquals(STORAGE_KEY1, actual.getStorageKey());
		assertEquals("TEST", actual.getValue());
		assertDocEquals(member, actual);
		
		// verify that concrete domain members have only a single value field indexed
		final JsonNode json = getMapper().convertValue(member, JsonNode.class);
		assertNull(json.get(Fields.BOOLEAN_VALUE));
		assertNull(json.get(Fields.INTEGER_VALUE));
		assertNull(json.get(Fields.DECIMAL_VALUE));
	}
	
}
