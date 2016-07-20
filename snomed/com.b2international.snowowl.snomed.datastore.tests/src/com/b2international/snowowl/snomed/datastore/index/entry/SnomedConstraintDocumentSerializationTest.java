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

import org.junit.Test;

import com.b2international.index.revision.BaseRevisionIndexTest;
import com.b2international.index.revision.RevisionBranch;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.datastore.snor.SnomedConstraintDocument;
import com.b2international.snowowl.snomed.mrcm.GroupRule;
import com.b2international.snowowl.snomed.snomedrefset.DataType;

/**
 * @since 4.7
 */
public class SnomedConstraintDocumentSerializationTest extends BaseRevisionIndexTest {

	@Override
	protected Collection<Class<?>> getTypes() {
		return Collections.<Class<?>>singleton(SnomedConstraintDocument.class);
	}
	
	@Test
	public void indexDescriptionPredicate() throws Exception {
		final SnomedConstraintDocument predicate = SnomedConstraintDocument.descriptionBuilder()
				.id(STORAGE_KEY1)
				.domain("queryExpressionTestField")
				.descriptionTypeId(Concepts.FULLY_SPECIFIED_NAME)
				.cardinality(1, 1)
				.build();
		
		indexRevision(RevisionBranch.MAIN_PATH, STORAGE_KEY1, predicate);
		final SnomedConstraintDocument actual = getRevision(RevisionBranch.MAIN_PATH, SnomedConstraintDocument.class, STORAGE_KEY1);
		assertEquals(STORAGE_KEY1, actual.getStorageKey());
		assertDocEquals(predicate, actual);
	}
	
	@Test
	public void indexRelationshipPredicate() throws Exception {
		final SnomedConstraintDocument predicate = SnomedConstraintDocument.relationshipBuilder()
				.id(STORAGE_KEY1)
				.domain("queryExpressionTestField")
				.cardinality(1, 1)
				.groupRule(GroupRule.UNGROUPED)
				.characteristicTypeExpression(Concepts.STATED_RELATIONSHIP)
				.relationshipTypeExpression(Concepts.IS_A)
				.relationshipValueExpression("<<"+Concepts.ROOT_CONCEPT)
				.build();
		
		indexRevision(RevisionBranch.MAIN_PATH, STORAGE_KEY1, predicate);
		final SnomedConstraintDocument actual = getRevision(RevisionBranch.MAIN_PATH, SnomedConstraintDocument.class, STORAGE_KEY1);
		assertEquals(STORAGE_KEY1, actual.getStorageKey());
		assertDocEquals(predicate, actual);
	}
	
	@Test
	public void indexDataTypePredicate() throws Exception {
		final SnomedConstraintDocument predicate = SnomedConstraintDocument.dataTypeBuilder()
				.id(STORAGE_KEY1)
				.domain("queryExpressionTestField")
				.dataTypeLabel("My Concrete Domain")
				.dataTypeName("myConcreteDomain")
				.dataType(DataType.STRING)
				.cardinality(1, 1)
				.build();
		
		indexRevision(RevisionBranch.MAIN_PATH, STORAGE_KEY1, predicate);
		final SnomedConstraintDocument actual = getRevision(RevisionBranch.MAIN_PATH, SnomedConstraintDocument.class, STORAGE_KEY1);
		assertEquals(STORAGE_KEY1, actual.getStorageKey());
		assertDocEquals(predicate, actual);
	}

}
