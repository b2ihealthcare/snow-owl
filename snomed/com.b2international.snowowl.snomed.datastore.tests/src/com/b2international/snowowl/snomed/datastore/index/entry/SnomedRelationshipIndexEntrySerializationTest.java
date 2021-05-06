/*
 * Copyright 2011-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
import java.util.Date;

import org.junit.Test;

import com.b2international.collections.PrimitiveCollectionModule;
import com.b2international.index.revision.BaseRevisionIndexTest;
import com.b2international.index.revision.RevisionBranch;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.domain.RelationshipValue;
import com.b2international.snowowl.snomed.core.domain.RelationshipValueType;
import com.b2international.snowowl.test.commons.snomed.RandomSnomedIdentiferGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @since 4.7
 */
public class SnomedRelationshipIndexEntrySerializationTest extends BaseRevisionIndexTest {

	@Override
	protected Collection<Class<?>> getTypes() {
		return Collections.<Class<?>>singleton(SnomedRelationshipIndexEntry.class);
	}
	
	@Override
	protected void configureMapper(ObjectMapper mapper) {
		mapper.registerModule(new PrimitiveCollectionModule());
	}
	
	private static SnomedRelationshipIndexEntry.Builder createBuilder() {
		return SnomedRelationshipIndexEntry.builder()
			.id(RandomSnomedIdentiferGenerator.generateRelationshipId())
			.active(true)
			.released(true)
			.effectiveTime(new Date().getTime())
			.moduleId(Concepts.MODULE_ROOT)
			.sourceId(Concepts.ROOT_CONCEPT)
			.destinationId(Concepts.ROOT_CONCEPT)
			.typeId(Concepts.IS_A)
			.characteristicTypeId(Concepts.STATED_RELATIONSHIP)
			.modifierId(Concepts.EXISTENTIAL_RESTRICTION_MODIFIER)
			.destinationNegated(true)
			.group(1)
			.unionGroup(1);
	}

	private SnomedRelationshipIndexEntry assertDocEquals(final SnomedRelationshipIndexEntry expected) {
		final SnomedRelationshipIndexEntry actual = getRevision(RevisionBranch.MAIN_PATH, SnomedRelationshipIndexEntry.class, expected.getId());
		assertDocEquals(expected, actual);
		return actual;
	}

	@Test
	public void indexRelationship() throws Exception {
		final SnomedRelationshipIndexEntry relationship = createBuilder().build();
		indexRevision(RevisionBranch.MAIN_PATH, relationship);
		
		final SnomedRelationshipIndexEntry actual = assertDocEquals(relationship);
		assertEquals("", actual.getNamespace());
	}

	@Test
	public void indexInteger() throws Exception {
		final SnomedRelationshipIndexEntry integerValue = createBuilder()
			.value(new RelationshipValue(5))
			.build();
		
		indexRevision(RevisionBranch.MAIN_PATH, integerValue);
		final SnomedRelationshipIndexEntry actual = assertDocEquals(integerValue);
		assertEquals(RelationshipValueType.INTEGER, actual.getValueType());
	}
	
	@Test
	public void indexDecimal() throws Exception {
		final SnomedRelationshipIndexEntry decimalValue = createBuilder()
			.value(new RelationshipValue(3.333d))
			.build();
		
		indexRevision(RevisionBranch.MAIN_PATH, decimalValue);
		final SnomedRelationshipIndexEntry actual = assertDocEquals(decimalValue);
		assertEquals(RelationshipValueType.DECIMAL, actual.getValueType());
	}
	
	@Test
	public void indexString() throws Exception {
		final SnomedRelationshipIndexEntry stringValue = createBuilder()
			.value(new RelationshipValue("Hello, world!"))
			.build();
		
		indexRevision(RevisionBranch.MAIN_PATH, stringValue);
		final SnomedRelationshipIndexEntry actual = assertDocEquals(stringValue);
		assertEquals(RelationshipValueType.STRING, actual.getValueType());
	}
	
	@Test
	public void indexBoolean() throws Exception {
		final SnomedRelationshipIndexEntry booleanValue = createBuilder()
			.value(new RelationshipValue(false))
			.build();
		
		indexRevision(RevisionBranch.MAIN_PATH, booleanValue);
		final SnomedRelationshipIndexEntry actual = assertDocEquals(booleanValue);
		assertEquals(RelationshipValueType.BOOLEAN, actual.getValueType());
	}
}
