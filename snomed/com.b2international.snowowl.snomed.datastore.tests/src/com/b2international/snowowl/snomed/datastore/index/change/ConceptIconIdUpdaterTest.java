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
package com.b2international.snowowl.snomed.datastore.index.change;

import static java.util.Collections.singleton;
import static java.util.Collections.singletonMap;

import org.junit.Test;

import com.b2international.index.revision.Revision;
import com.b2international.snowowl.core.domain.IComponent;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.domain.Acceptability;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionFragment;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Iterables;

/**
 * @since 7.9.0
 */
public class ConceptIconIdUpdaterTest extends BaseConceptPreCommitHookTest {

	private static final String DISORDER_SEMANTIC_TAG = "disorder";
	private static final String FINDING_SEMANTIC_TAG = "finding";
	private static final String TEST_FSN = "Disorder characterized by edema (%s)";

	@Test
	public void indexTopLevelConceptWithUnknownSemanticTag() {

		final SnomedConceptDocument concept = concept()
				.statedParents(IComponent.ROOT_IDL)
				.parents(IComponent.ROOT_IDL)
				.iconId(null) // intentionally unset icon ID before change processing
				.build();

		final SnomedDescriptionIndexEntry fsn = description(
				concept.getId(),
				Concepts.FULLY_SPECIFIED_NAME,
				String.format(TEST_FSN, "apple"),
				singletonMap(Concepts.REFSET_LANGUAGE_TYPE_US, Acceptability.PREFERRED));

		stageNew(concept);
		stageNew(fsn);

		final ConceptChangeProcessor processor = process();

		final SnomedConceptDocument expected = docWithDefaults(concept)
				.semanticTags(ImmutableSortedSet.of("apple"))
				.preferredDescriptions(ImmutableList.of(
						new SnomedDescriptionFragment(fsn.getId(), fsn.getTypeId(), fsn.getTerm(), Concepts.REFSET_LANGUAGE_TYPE_US)
						))
				.iconId(Concepts.ROOT_CONCEPT) // icon ID must be the root concept's ID
				.build();

		final Revision actual = Iterables.getOnlyElement(processor.getNewMappings().values());
		assertDocEquals(expected, actual);

	}

	@Test
	public void indexTopLevelConceptWithKnownSemanticTag() {

		final SnomedConceptDocument concept = concept()
				.statedParents(IComponent.ROOT_IDL)
				.parents(IComponent.ROOT_IDL)
				.iconId(null) // intentionally unset icon ID before change processing
				.build();

		final SnomedDescriptionIndexEntry fsn = description(
				concept.getId(),
				Concepts.FULLY_SPECIFIED_NAME,
				String.format(TEST_FSN, FINDING_SEMANTIC_TAG),
				singletonMap(Concepts.REFSET_LANGUAGE_TYPE_US, Acceptability.PREFERRED));

		stageNew(concept);
		stageNew(fsn);

		availableImages.add(FINDING_SEMANTIC_TAG);

		final ConceptChangeProcessor processor = process();

		final SnomedConceptDocument expected = docWithDefaults(concept)
				.semanticTags(ImmutableSortedSet.of(FINDING_SEMANTIC_TAG))
				.preferredDescriptions(ImmutableList.of(
						new SnomedDescriptionFragment(fsn.getId(), fsn.getTypeId(), fsn.getTerm(), Concepts.REFSET_LANGUAGE_TYPE_US)
						))
				.iconId(FINDING_SEMANTIC_TAG) // icon ID must be "finding"
				.build();

		final Revision actual = Iterables.getOnlyElement(processor.getNewMappings().values());
		assertDocEquals(expected, actual);

	}

	@Test
	public void indexChildConceptWithUnknownSemanticTag() {

		final SnomedConceptDocument concept = concept()
				.iconId(null) // intentionally unset icon ID before change processing
				.build();

		final SnomedDescriptionIndexEntry fsn1 = description(
				concept.getId(),
				Concepts.FULLY_SPECIFIED_NAME,
				String.format(TEST_FSN, FINDING_SEMANTIC_TAG),
				singletonMap(Concepts.REFSET_LANGUAGE_TYPE_US, Acceptability.PREFERRED));

		indexRevision(MAIN, concept, fsn1);

		final SnomedConceptDocument childConcept = concept()
				.iconId(null) // intentionally unset icon ID before change processing
				.build();

		final SnomedDescriptionIndexEntry fsn2 = description(
				childConcept.getId(),
				Concepts.FULLY_SPECIFIED_NAME,
				String.format(TEST_FSN, "apple"),
				singletonMap(Concepts.REFSET_LANGUAGE_TYPE_US, Acceptability.PREFERRED));

		final SnomedRefSetMemberIndexEntry member = createOwlAxiom(childConcept.getId(), String.format("SubClassOf(:%s :%s)", childConcept.getId(), concept.getId())).build();

		stageNew(childConcept);
		stageNew(fsn2);
		stageNew(member);

		statedChangedConceptIds.add(Long.parseLong(concept.getId()));
		statedChangedConceptIds.add(Long.parseLong(childConcept.getId()));

		final ConceptChangeProcessor processor = process();

		final SnomedConceptDocument expectedChild = docWithDefaults(childConcept)
				.semanticTags(ImmutableSortedSet.of("apple"))
				.preferredDescriptions(ImmutableList.of(
						new SnomedDescriptionFragment(fsn2.getId(), fsn2.getTypeId(), fsn2.getTerm(), Concepts.REFSET_LANGUAGE_TYPE_US)
						))
				.iconId(Concepts.ROOT_CONCEPT) // icon ID must be the root concept's ID
				.statedParents(Long.parseLong(concept.getId()))
				.statedAncestors(IComponent.ROOT_IDL)
				.activeMemberOf(singleton(Concepts.REFSET_OWL_AXIOM))
				.memberOf(singleton(Concepts.REFSET_OWL_AXIOM))
				.build();

		final Revision actualChild = Iterables.getOnlyElement(processor.getNewMappings().values());
		assertDocEquals(expectedChild, actualChild);

	}

	@Test
	public void indexChildConceptWithKnownSemanticTag() {

		final SnomedConceptDocument concept = concept()
				.iconId(null) // intentionally unset icon ID before change processing
				.build();

		final SnomedDescriptionIndexEntry fsn1 = description(
				concept.getId(),
				Concepts.FULLY_SPECIFIED_NAME,
				String.format(TEST_FSN, FINDING_SEMANTIC_TAG),
				singletonMap(Concepts.REFSET_LANGUAGE_TYPE_US, Acceptability.PREFERRED));

		indexRevision(MAIN, concept, fsn1);

		final SnomedConceptDocument childConcept = concept()
				.iconId(null) // intentionally unset icon ID before change processing
				.build();

		final SnomedDescriptionIndexEntry fsn2 = description(
				childConcept.getId(),
				Concepts.FULLY_SPECIFIED_NAME,
				String.format(TEST_FSN, DISORDER_SEMANTIC_TAG),
				singletonMap(Concepts.REFSET_LANGUAGE_TYPE_US, Acceptability.PREFERRED));

		final SnomedRefSetMemberIndexEntry member = createOwlAxiom(childConcept.getId(), String.format("SubClassOf(:%s :%s)", childConcept.getId(), concept.getId())).build();

		stageNew(childConcept);
		stageNew(fsn2);
		stageNew(member);

		statedChangedConceptIds.add(Long.parseLong(concept.getId()));
		statedChangedConceptIds.add(Long.parseLong(childConcept.getId()));

		availableImages.add(DISORDER_SEMANTIC_TAG);

		final ConceptChangeProcessor processor = process();

		final SnomedConceptDocument expectedChild = docWithDefaults(childConcept)
				.semanticTags(ImmutableSortedSet.of(DISORDER_SEMANTIC_TAG))
				.preferredDescriptions(ImmutableList.of(
						new SnomedDescriptionFragment(fsn2.getId(), fsn2.getTypeId(), fsn2.getTerm(), Concepts.REFSET_LANGUAGE_TYPE_US)
						))
				.iconId(DISORDER_SEMANTIC_TAG) // icon ID must be "disorder"
				.statedParents(Long.parseLong(concept.getId()))
				.statedAncestors(IComponent.ROOT_IDL)
				.activeMemberOf(singleton(Concepts.REFSET_OWL_AXIOM))
				.memberOf(singleton(Concepts.REFSET_OWL_AXIOM))
				.build();

		final Revision actualChild = Iterables.getOnlyElement(processor.getNewMappings().values());
		assertDocEquals(expectedChild, actualChild);

	}

	@Test
	public void indexChildConceptWithKnownId() {

		final SnomedConceptDocument concept = concept()
				.iconId(null) // intentionally unset icon ID before change processing
				.build();

		final SnomedDescriptionIndexEntry fsn1 = description(
				concept.getId(),
				Concepts.FULLY_SPECIFIED_NAME,
				String.format(TEST_FSN, FINDING_SEMANTIC_TAG),
				singletonMap(Concepts.REFSET_LANGUAGE_TYPE_US, Acceptability.PREFERRED));

		indexRevision(MAIN, concept, fsn1);

		final SnomedConceptDocument childConcept = concept(Concepts.NAMESPACE_ROOT)
				.iconId(null) // intentionally unset icon ID before change processing
				.build();

		final SnomedDescriptionIndexEntry fsn2 = description(
				childConcept.getId(),
				Concepts.FULLY_SPECIFIED_NAME,
				String.format(TEST_FSN, "apple"),
				singletonMap(Concepts.REFSET_LANGUAGE_TYPE_US, Acceptability.PREFERRED));

		final SnomedRefSetMemberIndexEntry member = createOwlAxiom(childConcept.getId(), String.format("SubClassOf(:%s :%s)", childConcept.getId(), concept.getId())).build();

		stageNew(childConcept);
		stageNew(fsn2);
		stageNew(member);

		statedChangedConceptIds.add(Long.parseLong(concept.getId()));
		statedChangedConceptIds.add(Long.parseLong(childConcept.getId()));

		final ConceptChangeProcessor processor = process();

		final SnomedConceptDocument expectedChild = docWithDefaults(childConcept)
				.semanticTags(ImmutableSortedSet.of("apple"))
				.preferredDescriptions(ImmutableList.of(
						new SnomedDescriptionFragment(fsn2.getId(), fsn2.getTypeId(), fsn2.getTerm(), Concepts.REFSET_LANGUAGE_TYPE_US)
						))
				.iconId(Concepts.NAMESPACE_ROOT) // icon ID must be the known concept's ID
				.statedParents(Long.parseLong(concept.getId()))
				.statedAncestors(IComponent.ROOT_IDL)
				.activeMemberOf(singleton(Concepts.REFSET_OWL_AXIOM))
				.memberOf(singleton(Concepts.REFSET_OWL_AXIOM))
				.build();

		final Revision actualChild = Iterables.getOnlyElement(processor.getNewMappings().values());
		assertDocEquals(expectedChild, actualChild);

	}

	@Test
	public void indexChildConceptWithPreferredAndAcceptableFsn() {

		final SnomedConceptDocument concept = concept()
				.iconId(null) // intentionally unset icon ID before change processing
				.build();

		final SnomedDescriptionIndexEntry fsn1 = description(
				concept.getId(),
				Concepts.FULLY_SPECIFIED_NAME,
				String.format(TEST_FSN, FINDING_SEMANTIC_TAG),
				singletonMap(Concepts.REFSET_LANGUAGE_TYPE_US, Acceptability.PREFERRED));

		indexRevision(MAIN, concept, fsn1);

		final SnomedConceptDocument childConcept = concept()
				.iconId(null) // intentionally unset icon ID before change processing
				.build();

		final SnomedDescriptionIndexEntry fsn2 = description(
				childConcept.getId(),
				Concepts.FULLY_SPECIFIED_NAME,
				String.format(TEST_FSN, DISORDER_SEMANTIC_TAG),
				singletonMap(Concepts.REFSET_LANGUAGE_TYPE_US, Acceptability.PREFERRED));

		// another acceptable FSN must not cause any change in icon calculation
		final SnomedDescriptionIndexEntry fsn3 = description(
				childConcept.getId(),
				Concepts.FULLY_SPECIFIED_NAME,
				String.format(TEST_FSN, "trouble"),
				singletonMap(Concepts.REFSET_LANGUAGE_TYPE_US, Acceptability.ACCEPTABLE));

		final SnomedRefSetMemberIndexEntry member = createOwlAxiom(childConcept.getId(), String.format("SubClassOf(:%s :%s)", childConcept.getId(), concept.getId())).build();

		stageNew(childConcept);
		stageNew(fsn2);
		stageNew(fsn3);
		stageNew(member);

		statedChangedConceptIds.add(Long.parseLong(concept.getId()));
		statedChangedConceptIds.add(Long.parseLong(childConcept.getId()));

		availableImages.add(DISORDER_SEMANTIC_TAG);

		final ConceptChangeProcessor processor = process();

		final SnomedConceptDocument expectedChild = docWithDefaults(childConcept)
				.semanticTags(ImmutableSortedSet.of(DISORDER_SEMANTIC_TAG))
				.preferredDescriptions(ImmutableList.of(
						new SnomedDescriptionFragment(fsn2.getId(), fsn2.getTypeId(), fsn2.getTerm(), Concepts.REFSET_LANGUAGE_TYPE_US)
						))
				.iconId(DISORDER_SEMANTIC_TAG) // icon ID must be "disorder"
				.statedParents(Long.parseLong(concept.getId()))
				.statedAncestors(IComponent.ROOT_IDL)
				.activeMemberOf(singleton(Concepts.REFSET_OWL_AXIOM))
				.memberOf(singleton(Concepts.REFSET_OWL_AXIOM))
				.build();

		final Revision actualChild = Iterables.getOnlyElement(processor.getNewMappings().values());
		assertDocEquals(expectedChild, actualChild);

	}

	@Test
	public void indexChildConceptWithUSAndGBFsn() {

		final SnomedConceptDocument concept = concept()
				.iconId(null) // intentionally unset icon ID before change processing
				.build();

		final SnomedDescriptionIndexEntry fsn1 = description(
				concept.getId(),
				Concepts.FULLY_SPECIFIED_NAME,
				String.format(TEST_FSN, FINDING_SEMANTIC_TAG),
				singletonMap(Concepts.REFSET_LANGUAGE_TYPE_US, Acceptability.PREFERRED));

		indexRevision(MAIN, concept, fsn1);

		final SnomedConceptDocument childConcept = concept()
				.iconId(null) // intentionally unset icon ID before change processing
				.build();

		// the US preferred FSN has priority over the GB and the rest of the preferred FSNs
		final SnomedDescriptionIndexEntry fsn2 = description(
				childConcept.getId(),
				Concepts.FULLY_SPECIFIED_NAME,
				String.format(TEST_FSN, DISORDER_SEMANTIC_TAG),
				singletonMap(Concepts.REFSET_LANGUAGE_TYPE_US, Acceptability.PREFERRED));

		// the GB preferred FSN has priority over the other preferred FSNs (except the US one)
		final SnomedDescriptionIndexEntry fsn3 = description(
				childConcept.getId(),
				Concepts.FULLY_SPECIFIED_NAME,
				String.format(TEST_FSN, "trouble"),
				singletonMap(Concepts.REFSET_LANGUAGE_TYPE_UK, Acceptability.PREFERRED));

		final SnomedRefSetMemberIndexEntry member = createOwlAxiom(childConcept.getId(), String.format("SubClassOf(:%s :%s)", childConcept.getId(), concept.getId())).build();

		stageNew(childConcept);
		stageNew(fsn3);
		stageNew(fsn2);
		stageNew(member);

		statedChangedConceptIds.add(Long.parseLong(concept.getId()));
		statedChangedConceptIds.add(Long.parseLong(childConcept.getId()));

		availableImages.add(DISORDER_SEMANTIC_TAG);

		final ConceptChangeProcessor processor = process();

		final SnomedConceptDocument expectedChild = docWithDefaults(childConcept)
				.semanticTags(ImmutableSortedSet.of(DISORDER_SEMANTIC_TAG, "trouble")) // both semantic tags should be extracted
				.preferredDescriptions(ImmutableList.of(
						new SnomedDescriptionFragment(fsn2.getId(), fsn2.getTypeId(), fsn2.getTerm(), Concepts.REFSET_LANGUAGE_TYPE_US),
						new SnomedDescriptionFragment(fsn3.getId(), fsn3.getTypeId(), fsn3.getTerm(), Concepts.REFSET_LANGUAGE_TYPE_UK)
						))
				.iconId(DISORDER_SEMANTIC_TAG) // icon ID must be "disorder"
				.statedParents(Long.parseLong(concept.getId()))
				.statedAncestors(IComponent.ROOT_IDL)
				.activeMemberOf(singleton(Concepts.REFSET_OWL_AXIOM))
				.memberOf(singleton(Concepts.REFSET_OWL_AXIOM))
				.build();

		final Revision actualChild = Iterables.getOnlyElement(processor.getNewMappings().values());
		assertDocEquals(expectedChild, actualChild);

	}

	@Test
	public void indexChildConceptWithGBAndExtensionFsn() {

		final SnomedConceptDocument concept = concept()
				.iconId(null) // intentionally unset icon ID before change processing
				.build();

		final SnomedDescriptionIndexEntry fsn1 = description(
				concept.getId(),
				Concepts.FULLY_SPECIFIED_NAME,
				String.format(TEST_FSN, FINDING_SEMANTIC_TAG),
				singletonMap(Concepts.REFSET_LANGUAGE_TYPE_US, Acceptability.PREFERRED));

		indexRevision(MAIN, concept, fsn1);

		final SnomedConceptDocument childConcept = concept()
				.iconId(null) // intentionally unset icon ID before change processing
				.build();

		// the GB preferred FSN has priority over the other preferred FSNs (except the US one)
		final SnomedDescriptionIndexEntry fsn2 = description(
				childConcept.getId(),
				Concepts.FULLY_SPECIFIED_NAME,
				String.format(TEST_FSN, DISORDER_SEMANTIC_TAG),
				singletonMap(Concepts.REFSET_LANGUAGE_TYPE_UK, Acceptability.PREFERRED));

		final SnomedDescriptionIndexEntry fsn3 = description(
				childConcept.getId(),
				Concepts.FULLY_SPECIFIED_NAME,
				String.format(TEST_FSN, "trouble"),
				singletonMap(Concepts.REFSET_LANGUAGE_TYPE_SG, Acceptability.PREFERRED));

		final SnomedRefSetMemberIndexEntry member = createOwlAxiom(childConcept.getId(), String.format("SubClassOf(:%s :%s)", childConcept.getId(), concept.getId())).build();

		stageNew(childConcept);
		stageNew(fsn3);
		stageNew(fsn2);
		stageNew(member);

		statedChangedConceptIds.add(Long.parseLong(concept.getId()));
		statedChangedConceptIds.add(Long.parseLong(childConcept.getId()));

		availableImages.add(DISORDER_SEMANTIC_TAG);

		final ConceptChangeProcessor processor = process();

		final SnomedConceptDocument expectedChild = docWithDefaults(childConcept)
				.semanticTags(ImmutableSortedSet.of(DISORDER_SEMANTIC_TAG, "trouble")) // both semantic tags should be present
				.preferredDescriptions(ImmutableList.of(
						new SnomedDescriptionFragment(fsn2.getId(), fsn2.getTypeId(), fsn2.getTerm(), Concepts.REFSET_LANGUAGE_TYPE_UK),
						new SnomedDescriptionFragment(fsn3.getId(), fsn3.getTypeId(), fsn3.getTerm(), Concepts.REFSET_LANGUAGE_TYPE_SG)
						))
				.iconId(DISORDER_SEMANTIC_TAG) // icon ID must be "disorder"
				.statedParents(Long.parseLong(concept.getId()))
				.statedAncestors(IComponent.ROOT_IDL)
				.activeMemberOf(singleton(Concepts.REFSET_OWL_AXIOM))
				.memberOf(singleton(Concepts.REFSET_OWL_AXIOM))
				.build();

		final Revision actualChild = Iterables.getOnlyElement(processor.getNewMappings().values());
		assertDocEquals(expectedChild, actualChild);

	}

}
