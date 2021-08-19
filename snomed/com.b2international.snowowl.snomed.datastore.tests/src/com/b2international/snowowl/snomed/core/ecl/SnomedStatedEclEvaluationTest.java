/*
 * Copyright 2018-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.core.ecl;

import static com.b2international.snowowl.core.repository.RevisionDocument.Expressions.ids;
import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument.Expressions.statedAncestors;
import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument.Expressions.statedParents;
import static com.b2international.snowowl.test.commons.snomed.DocumentBuilders.concept;
import static com.b2international.snowowl.test.commons.snomed.DocumentBuilders.relationship;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.b2international.index.query.Expression;
import com.b2international.index.query.Expressions;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.tree.Trees;
import com.b2international.snowowl.test.commons.snomed.RandomSnomedIdentiferGenerator;
import com.google.common.collect.ImmutableSet;

/**
 * @since 5.15.1
 */
public class SnomedStatedEclEvaluationTest extends BaseSnomedEclEvaluationRequestTest {

	public SnomedStatedEclEvaluationTest() {
		super(Trees.STATED_FORM, false);
	}
	
	private static final String ROOT_CONCEPT = RandomSnomedIdentiferGenerator.generateConceptId();
	private static final String HAS_ACTIVE_INGREDIENT = Concepts.HAS_ACTIVE_INGREDIENT;
	private static final String SUBSTANCE = Concepts.SUBSTANCE;
	private static final String STATED_CONCEPT = RandomSnomedIdentiferGenerator.generateConceptId();
	
	@Test
	public void statedRefinementWithZeroToOneCardinalityInAttributeConjuction() throws Exception {
		generateTestHierarchy();
		final Expression actual = eval(String.format("<<%s:[1..*]{[0..1]%s=<<%s}", ROOT_CONCEPT, HAS_ACTIVE_INGREDIENT, SUBSTANCE));
		
		final Expression expected = and(
				descendantsOrSelfOf(ROOT_CONCEPT),
				ids(ImmutableSet.of(STATED_CONCEPT))
				);
		assertEquals(expected, actual);
	}
	
	private void generateTestHierarchy() {
		indexRevision(MAIN, 
			concept(STATED_CONCEPT).statedParents(Long.parseLong(ROOT_CONCEPT)).build(),
			relationship(STATED_CONCEPT, HAS_ACTIVE_INGREDIENT, SUBSTANCE, Concepts.STATED_RELATIONSHIP).relationshipGroup(1).build()
		);
	}
	
	private Expression descendantsOrSelfOf(String...conceptIds) {
		return Expressions.builder()
				.should(ids(ImmutableSet.copyOf(conceptIds)))
				.should(statedParents(ImmutableSet.copyOf(conceptIds)))
				.should(statedAncestors(ImmutableSet.copyOf(conceptIds))).build();
	}
	
	private static Expression and(Expression left, Expression right) {
		return Expressions.builder().filter(left).filter(right).build();
	}

}
