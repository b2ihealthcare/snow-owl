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

import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

import org.junit.Test;

import com.b2international.index.revision.BaseRevisionIndexTest;
import com.b2international.index.revision.RevisionBranch;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.domain.refset.DataType;
import com.b2international.snowowl.snomed.datastore.index.constraint.CardinalityPredicateFragment;
import com.b2international.snowowl.snomed.datastore.index.constraint.ConcreteDomainPredicateFragment;
import com.b2international.snowowl.snomed.datastore.index.constraint.DescriptionPredicateFragment;
import com.b2international.snowowl.snomed.datastore.index.constraint.EnumeratedDefinitionFragment;
import com.b2international.snowowl.snomed.datastore.index.constraint.HierarchyDefinitionFragment;
import com.b2international.snowowl.snomed.datastore.index.constraint.RelationshipPredicateFragment;
import com.b2international.snowowl.snomed.datastore.index.constraint.SnomedConstraintDocument;
import com.b2international.snowowl.snomed.mrcm.ConstraintForm;
import com.b2international.snowowl.snomed.mrcm.ConstraintStrength;
import com.b2international.snowowl.snomed.mrcm.GroupRule;
import com.b2international.snowowl.snomed.mrcm.HierarchyInclusionType;
import com.google.common.collect.ImmutableSet;

/**
 * @since 4.7
 */
public class SnomedConstraintDocumentSerializationTest extends BaseRevisionIndexTest {

	private static final boolean DEFAULT_STATUS = true;
	private static final String DEFAULT_AUTHOR = "author@company.xyz";
	private static final long DEFAULT_EFFECTIVE_TIME = -1L;

	private static String randomUUID() {
		return UUID.randomUUID().toString();
	}

	@Override
	protected Collection<Class<?>> getTypes() {
		return Collections.<Class<?>>singleton(SnomedConstraintDocument.class);
	}
	
	@Test
	public void indexDescriptionConstraint() throws Exception {
		final String id = randomUUID();
		final SnomedConstraintDocument constraint = SnomedConstraintDocument.descriptionBuilder()
				.active(DEFAULT_STATUS)
				.author(DEFAULT_AUTHOR)
				.domain(new HierarchyDefinitionFragment(id,
						DEFAULT_STATUS,
						DEFAULT_EFFECTIVE_TIME,
						DEFAULT_AUTHOR,
						Concepts.ROOT_CONCEPT,
						HierarchyInclusionType.SELF_OR_DESCENDANT))
				.effectiveTime(DEFAULT_EFFECTIVE_TIME)
				.form(ConstraintForm.ALL_FORMS)
				.id(id)
				.predicate(new CardinalityPredicateFragment(id,
						DEFAULT_STATUS,
						DEFAULT_EFFECTIVE_TIME,
						DEFAULT_AUTHOR,
						1,
						1,
						GroupRule.ALL_GROUPS,
						new DescriptionPredicateFragment(id,
								DEFAULT_STATUS,
								DEFAULT_EFFECTIVE_TIME,
								DEFAULT_AUTHOR,
								Concepts.FULLY_SPECIFIED_NAME)))
				.strength(ConstraintStrength.MANDATORY_CM)
				.validationMessage("All descendants of SNOMED CT Root, including the concept itself, must have exactly one FSN.")
				.storageKey(1L)
				.build();
		
		indexRevision(RevisionBranch.MAIN_PATH, constraint);
		final SnomedConstraintDocument actual = getRevision(RevisionBranch.MAIN_PATH, SnomedConstraintDocument.class, id);
		assertEquals(1L, actual.getStorageKey());
		assertDocEquals(constraint, actual);
	}

	@Test
	public void indexRelationshipConstraint() throws Exception {
		final String id = randomUUID();
		final SnomedConstraintDocument constraint = SnomedConstraintDocument.relationshipBuilder()
				.active(DEFAULT_STATUS)
				.author(DEFAULT_AUTHOR)
				.domain(new HierarchyDefinitionFragment(randomUUID(),
						DEFAULT_STATUS,
						DEFAULT_EFFECTIVE_TIME,
						DEFAULT_AUTHOR,
						Concepts.ROOT_CONCEPT,
						HierarchyInclusionType.DESCENDANT))
				.effectiveTime(DEFAULT_EFFECTIVE_TIME)
				.form(ConstraintForm.ALL_FORMS)
				.id(id)
				.predicate(new CardinalityPredicateFragment(randomUUID(),
						DEFAULT_STATUS,
						DEFAULT_EFFECTIVE_TIME,
						DEFAULT_AUTHOR,
						1,
						1,
						GroupRule.UNGROUPED,
						new RelationshipPredicateFragment(randomUUID(),
								DEFAULT_STATUS,
								DEFAULT_EFFECTIVE_TIME,
								DEFAULT_AUTHOR,
								new EnumeratedDefinitionFragment(randomUUID(), 
										DEFAULT_STATUS, 
										DEFAULT_EFFECTIVE_TIME, 
										DEFAULT_AUTHOR, 
										ImmutableSet.of(Concepts.IS_A)),
								new HierarchyDefinitionFragment(randomUUID(), 
										DEFAULT_STATUS, 
										DEFAULT_EFFECTIVE_TIME, 
										DEFAULT_AUTHOR, 
										Concepts.ROOT_CONCEPT, 
										HierarchyInclusionType.SELF_OR_DESCENDANT),
								Concepts.STATED_RELATIONSHIP)))
				.storageKey(1L)
				.strength(ConstraintStrength.MANDATORY_CM)
				.validationMessage("All descendants of SNOMED CT Root must have exactly one stated IS_A relationship.")
				.build();
		
		indexRevision(RevisionBranch.MAIN_PATH, constraint);
		final SnomedConstraintDocument actual = getRevision(RevisionBranch.MAIN_PATH, SnomedConstraintDocument.class, id);
		assertEquals(1L, actual.getStorageKey());
		assertDocEquals(constraint, actual);
	}

	@Test
	public void indexConcreteDomainConstraint() throws Exception {
		final String id = randomUUID();
		final SnomedConstraintDocument constraint = SnomedConstraintDocument.relationshipBuilder()
				.active(DEFAULT_STATUS)
				.author(DEFAULT_AUTHOR)
				.domain(new HierarchyDefinitionFragment(randomUUID(),
						DEFAULT_STATUS,
						DEFAULT_EFFECTIVE_TIME,
						DEFAULT_AUTHOR,
						Concepts.SUBSTANCE,
						HierarchyInclusionType.DESCENDANT))
				.effectiveTime(DEFAULT_EFFECTIVE_TIME)
				.form(ConstraintForm.ALL_FORMS)
				.id(id)
				.predicate(new CardinalityPredicateFragment(randomUUID(),
						DEFAULT_STATUS,
						DEFAULT_EFFECTIVE_TIME,
						DEFAULT_AUTHOR,
						1,
						1,
						GroupRule.UNGROUPED,
						new ConcreteDomainPredicateFragment(randomUUID(),
								DEFAULT_STATUS,
								DEFAULT_EFFECTIVE_TIME,
								DEFAULT_AUTHOR,
								"Vaccine",
								"canBeTaggedWithVaccine",
								DataType.BOOLEAN,
								Concepts.STATED_RELATIONSHIP)))
				.storageKey(1L)
				.strength(ConstraintStrength.MANDATORY_CM)
				.validationMessage("All descendants of Substance must have exactly one boolean concrete domain member called 'Vaccine'.")
				.build();
		
		indexRevision(RevisionBranch.MAIN_PATH, constraint);
		final SnomedConstraintDocument actual = getRevision(RevisionBranch.MAIN_PATH, SnomedConstraintDocument.class, id);
		assertEquals(1L, actual.getStorageKey());
		assertDocEquals(constraint, actual);
	}
}
