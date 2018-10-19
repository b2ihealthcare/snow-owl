/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;

import com.b2international.collections.PrimitiveCollectionModule;
import com.b2international.collections.PrimitiveSets;
import com.b2international.index.Hits;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Query;
import com.b2international.index.query.SortBy;
import com.b2international.index.revision.BaseRevisionIndexTest;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.datastore.id.RandomSnomedIdentiferGenerator;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument.Builder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;

/**
 * @since 6.3
 */
public class SnomedConceptDocumentTermSortTest extends BaseRevisionIndexTest {

	private SnomedConceptDocument conceptA;
	private SnomedConceptDocument conceptB;
	private SnomedConceptDocument conceptC;
	private SnomedConceptDocument conceptD;
	
	@Override
	protected Collection<Class<?>> getTypes() {
		return ImmutableList.of(SnomedConceptDocument.class);
	}
	
	@Override
	protected void configureMapper(ObjectMapper mapper) {
		mapper.registerModule(new PrimitiveCollectionModule());
	}
	
	@Before
	@Override
	public void setup() {
		super.setup();
		conceptA = concept()
				.preferredDescriptions(
					ImmutableList.of(
						fsn("A (tag)", Concepts.REFSET_LANGUAGE_TYPE_UK), 
						synonym("A", Concepts.REFSET_LANGUAGE_TYPE_UK),
						synonym("Synonym A", Concepts.REFSET_LANGUAGE_TYPE_UK)
					)
				)
				.build();
		conceptB = concept()
				.preferredDescriptions(
					ImmutableList.of(
						fsn("B (tag)", Concepts.REFSET_LANGUAGE_TYPE_UK), 
						synonym("B", Concepts.REFSET_LANGUAGE_TYPE_UK),
						synonym("Term B", Concepts.REFSET_LANGUAGE_TYPE_UK)
					)
				)
				.build();
		conceptC = concept()
				.preferredDescriptions(
					ImmutableList.of(
						fsn("C (tag)", Concepts.REFSET_LANGUAGE_TYPE_UK), 
						synonym("C", Concepts.REFSET_LANGUAGE_TYPE_UK),
						synonym("Term C", Concepts.REFSET_LANGUAGE_TYPE_UK)
					)
				)
				.build();
		conceptD = concept()
				.preferredDescriptions(
					ImmutableList.of(
						fsn("Alternative Term (tag)", Concepts.REFSET_LANGUAGE_TYPE_UK), 
						synonym("Description with D start letter and syonym type", Concepts.REFSET_LANGUAGE_TYPE_UK),
						synonym("Term C", Concepts.REFSET_LANGUAGE_TYPE_UK)
					)
				)
				.build();
		indexRevision(MAIN, conceptA, conceptB, conceptC, conceptD);
	}
	
	@Test
	public void basicTermSort() throws Exception {
		final Hits<String> matches = search(MAIN, Query.select(String.class)
				.from(SnomedConceptDocument.class)
				.fields(SnomedConceptDocument.Fields.ID)
				.where(Expressions.matchAll())
				.sortBy(SnomedConceptDocument.sortByTerm(ImmutableList.of(Concepts.REFSET_LANGUAGE_TYPE_UK), Collections.singleton(Concepts.SYNONYM), SortBy.Order.ASC))
				.build());
		assertThat(matches).containsSequence(
			conceptA.getId(),
			conceptB.getId(),
			conceptC.getId(),
			conceptD.getId()
		);
	}

	private SnomedConceptDocument.Builder concept() {
		return concept(RandomSnomedIdentiferGenerator.generateConceptId());
	}

	private Builder concept(String conceptId) {
		return SnomedConceptDocument.builder()
				.id(conceptId)
				.iconId(Concepts.ROOT_CONCEPT)
				.active(true)
				.moduleId(Concepts.MODULE_ROOT)
				.exhaustive(true)
				.primitive(true)
				.parents(PrimitiveSets.newLongOpenHashSet(-1L))
				.ancestors(PrimitiveSets.newLongOpenHashSet(-1L))
				.statedAncestors(PrimitiveSets.newLongOpenHashSet(-1L))
				.statedParents(PrimitiveSets.newLongOpenHashSet(-1L));
	}
	
	private SnomedDescriptionFragment fsn(String term, String languageRefSetId) {
		return descriptionFragment(Concepts.FULLY_SPECIFIED_NAME, term, languageRefSetId);
	}
	
	private SnomedDescriptionFragment synonym(String term, String languageRefSetId) {
		return descriptionFragment(Concepts.SYNONYM, term, languageRefSetId);
	}

	private SnomedDescriptionFragment descriptionFragment(String typeId, String term, String languageRefSetId) {
		return new SnomedDescriptionFragment(RandomSnomedIdentiferGenerator.generateDescriptionId(), typeId, term, languageRefSetId);
	}

}
