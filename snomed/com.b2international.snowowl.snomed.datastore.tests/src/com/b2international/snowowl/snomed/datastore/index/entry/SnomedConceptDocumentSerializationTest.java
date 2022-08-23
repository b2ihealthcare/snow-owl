/*
 * Copyright 2011-2022 B2i Healthcare Pte Ltd, http://b2i.sg
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
import java.util.Map;

import org.junit.Test;

import com.b2international.collections.PrimitiveCollectionModule;
import com.b2international.collections.PrimitiveLists;
import com.b2international.collections.PrimitiveSets;
import com.b2international.index.IndexClientFactory;
import com.b2international.index.revision.BaseRevisionIndexTest;
import com.b2international.index.revision.RevisionBranch;
import com.b2international.snowowl.core.similarity.Similarity;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedDescription;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationship;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @since 4.7
 */
public class SnomedConceptDocumentSerializationTest extends BaseRevisionIndexTest {

	@Override
	protected void configureMapper(ObjectMapper mapper) {
		mapper.registerModule(new PrimitiveCollectionModule());
	}
	
	@Override
	protected Collection<Class<?>> getTypes() {
		return Collections.<Class<?>>singleton(SnomedConceptDocument.class);
	}
	
	@Override
	protected Map<String, Object> getIndexSettings() {
		// change field type via external mappings configuration for the SNOMED CT concept types
		return Map.of(SnomedConcept.TYPE, Map.of(
			IndexClientFactory.MAPPINGS, Map.of(
				"properties", Map.of(
					"similarity", Map.of(
						"type", "nested",
						"properties", Map.of(
							"predicted_value", Map.of(
								"type", "half_float"
							)
						)
					) 
				)
			) 
		));
	}
	
	@Test
	public void indexConcept() throws Exception {
		final SnomedConceptDocument concept = SnomedConceptDocument.builder()
				.id(Concepts.ROOT_CONCEPT)
				.iconId(Concepts.ROOT_CONCEPT)
				.active(true)
				.released(true)
				.effectiveTime(new Date().getTime())
				.moduleId(Concepts.MODULE_ROOT)
				.exhaustive(true)
				.definitionStatusId(Concepts.PRIMITIVE)
				.parents(PrimitiveSets.newLongSortedSet(-1L))
				.ancestors(PrimitiveSets.newLongSortedSet(-1L))
				.statedAncestors(PrimitiveSets.newLongSortedSet(-1L))
				.statedParents(PrimitiveSets.newLongSortedSet(-1L))
				.build();
		
		indexRevision(RevisionBranch.MAIN_PATH, concept);
		final SnomedConceptDocument actual = getRevision(RevisionBranch.MAIN_PATH, SnomedConceptDocument.class, Concepts.ROOT_CONCEPT);
		assertEquals("", actual.getNamespace());
		assertDocEquals(concept, actual);
	}
	
	@Test
	public void indexRefSetConcept() throws Exception {
		final SnomedConceptDocument concept = SnomedConceptDocument.builder()
				.id(Concepts.ROOT_CONCEPT)
				.iconId(Concepts.ROOT_CONCEPT)
				.active(true)
				.released(true)
				.effectiveTime(new Date().getTime())
				.moduleId(Concepts.MODULE_ROOT)
				.exhaustive(true)
				.definitionStatusId(Concepts.PRIMITIVE)
				.parents(PrimitiveSets.newLongSortedSet(-1L))
				.ancestors(PrimitiveSets.newLongSortedSet(-1L))
				.statedAncestors(PrimitiveSets.newLongSortedSet(-1L))
				.statedParents(PrimitiveSets.newLongSortedSet(-1L))
				.refSetType(SnomedRefSetType.ASSOCIATION)
				.referencedComponentType(SnomedConcept.TYPE)
				.mapTargetComponentType(SnomedRelationship.TYPE)
				.mapSourceComponentType(SnomedDescription.TYPE)
				.build();
		
		indexRevision(RevisionBranch.MAIN_PATH, concept);
		final SnomedConceptDocument actual = getRevision(RevisionBranch.MAIN_PATH, SnomedConceptDocument.class, Concepts.ROOT_CONCEPT);
		assertDocEquals(concept, actual);
	}
	
	@Test
	public void indexWithDifferentFloatFieldType() throws Exception {
		final SnomedConceptDocument concept = SnomedConceptDocument.builder()
				.id(Concepts.ROOT_CONCEPT)
				.iconId(Concepts.ROOT_CONCEPT)
				.active(true)
				.released(true)
				.effectiveTime(new Date().getTime())
				.moduleId(Concepts.MODULE_ROOT)
				.exhaustive(true)
				.definitionStatusId(Concepts.PRIMITIVE)
				.parents(PrimitiveSets.newLongSortedSet(-1L))
				.ancestors(PrimitiveSets.newLongSortedSet(-1L))
				.statedAncestors(PrimitiveSets.newLongSortedSet(-1L))
				.statedParents(PrimitiveSets.newLongSortedSet(-1L))
				.similarity(Similarity.of("", PrimitiveLists.newFloatArrayList(0.4f, -0.5f, 5.4f)))
				.build();
		
		indexRevision(RevisionBranch.MAIN_PATH, concept);
		final SnomedConceptDocument actual = getRevision(RevisionBranch.MAIN_PATH, SnomedConceptDocument.class, Concepts.ROOT_CONCEPT);
		assertDocEquals(concept, actual);
	}

}
