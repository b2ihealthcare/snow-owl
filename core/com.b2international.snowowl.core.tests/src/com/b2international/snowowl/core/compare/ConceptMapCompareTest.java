/*
 * Copyright 2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.compare;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.b2international.snowowl.core.domain.SetMapping;
import com.b2international.snowowl.core.uri.ComponentURI;
import com.google.common.collect.ImmutableList;

/**
 * @since 7.8
 */
public class ConceptMapCompareTest implements CompareSets {
	private static final String CODE_SYSTEM_1 = "cs1";
	private static final String CODE_SYSTEM_2 = "cs2";
	public static final short CONCEPT_NUMBER = 100;


	SetMapping member1 = SetMapping.builder()
			.sourceComponentURI(ComponentURI.of(CODE_SYSTEM_1, CONCEPT_NUMBER, "1"))
			.targetComponentURI(ComponentURI.of(CODE_SYSTEM_2, CONCEPT_NUMBER, "1"))
			.build();

	SetMapping member2 = SetMapping.builder()
			.sourceComponentURI(ComponentURI.of(CODE_SYSTEM_1, CONCEPT_NUMBER, "2"))
			.targetComponentURI(ComponentURI.of(CODE_SYSTEM_2, CONCEPT_NUMBER, "2"))
			.build();

	SetMapping member3 = SetMapping.builder()
			.sourceComponentURI(ComponentURI.of(CODE_SYSTEM_1, CONCEPT_NUMBER, "3"))
			.targetComponentURI(ComponentURI.of(CODE_SYSTEM_2, CONCEPT_NUMBER, "3"))
			.build();

	SetMapping member4 = SetMapping.builder()
			.sourceComponentURI(ComponentURI.of(CODE_SYSTEM_1, CONCEPT_NUMBER, "4"))
			.targetComponentURI(ComponentURI.of(CODE_SYSTEM_2, CONCEPT_NUMBER, "4"))
			.build();

	SetMapping member5 = SetMapping.builder()
			.sourceComponentURI(ComponentURI.of(CODE_SYSTEM_2, CONCEPT_NUMBER, "1"))
			.targetComponentURI(ComponentURI.of(CODE_SYSTEM_2, CONCEPT_NUMBER, "1"))
			.build();

	SetMapping member6 = SetMapping.builder()
			.sourceComponentURI(ComponentURI.of(CODE_SYSTEM_1, CONCEPT_NUMBER, "1"))
			.targetComponentURI(ComponentURI.of(CODE_SYSTEM_2, CONCEPT_NUMBER, "3"))
			.build();

	@Test
	public void compareEqualSets() {
		ImmutableList<SetMapping> set = ImmutableList.of(member1,member2);

		ConceptMapCompareResult result = compareDifferents(set, set);

		assertEquals(0, result.getRemovedMembers().size());
		assertEquals(0, result.getAddedMembers().size());
		assertEquals(0, result.getChangedMembers().size());
	}

	@Test
	public void compareDifferentSets() {

		ImmutableList<SetMapping> set1 = ImmutableList.of(member1, member2, member3);
		ImmutableList<SetMapping> set2 = ImmutableList.of(member1, member4, member5, member6);

		ConceptMapCompareResult result = compareDifferents(set1, set2);

		assertEquals(2, result.getAddedMembers().size());
		assertEquals(2, result.getRemovedMembers().size());
		assertEquals(1, result.getChangedMembers().size());
		assertEquals(true, result.getChangedMembers().containsEntry(member1, member6));
		assertEquals(true, result.getAddedMembers().contains(member4));
		assertEquals(true, result.getAddedMembers().contains(member5));
		assertEquals(true, result.getRemovedMembers().contains(member2));
		assertEquals(true, result.getRemovedMembers().contains(member3));
	}

}
