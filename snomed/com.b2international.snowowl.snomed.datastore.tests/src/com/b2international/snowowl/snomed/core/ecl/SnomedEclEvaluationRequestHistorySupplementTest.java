/*
 * Copyright 2022 B2i Healthcare, https://b2ihealthcare.com
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

import static org.junit.Assert.assertEquals;

import java.util.UUID;

import org.elasticsearch.core.Set;
import org.junit.Before;
import org.junit.Test;

import com.b2international.index.query.Expression;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.test.commons.snomed.DocumentBuilders;
import com.b2international.snowowl.test.commons.snomed.RandomSnomedIdentiferGenerator;

/**
 * @since 8.1
 */
public class SnomedEclEvaluationRequestHistorySupplementTest extends BaseSnomedEclEvaluationRequestTest {

	private static final String SIMPLE_REFSET_ID = RandomSnomedIdentiferGenerator.generateConceptId();
	
	private static final String INACTIVE_CONCEPT_SAME_AS = RandomSnomedIdentiferGenerator.generateConceptId();
	private static final String INACTIVE_CONCEPT_WAS_A = RandomSnomedIdentiferGenerator.generateConceptId();
	private static final String INACTIVE_CONCEPT_REPLACED_BY = RandomSnomedIdentiferGenerator.generateConceptId();
	private static final String INACTIVE_CONCEPT_PARTIALLY_EQUIVALENT_TO = RandomSnomedIdentiferGenerator.generateConceptId();
	private static final String INACTIVE_CONCEPT_POSSIBLY_EQUIVALENT_TO = RandomSnomedIdentiferGenerator.generateConceptId();
	
	private static final String SUBSTANCE = Concepts.SUBSTANCE;
	private static final Long SUBSTANCE_L = Long.parseLong(SUBSTANCE);
	private static final String SUBSTANCE_CHILD_CONCEPT = RandomSnomedIdentiferGenerator.generateConceptId();

	private static final Long REFSET_HISTORICAL_ASSOCIATION_L = Long.parseLong(Concepts.REFSET_HISTORICAL_ASSOCIATION);
	
	@Before
	@Override
	public void setup() {
		super.setup();
		
		// generate historical association refset hierarchy
		indexRevision(MAIN, DocumentBuilders.concept(Concepts.REFSET_SAME_AS_ASSOCIATION)
				.parents(REFSET_HISTORICAL_ASSOCIATION_L)
				.build());
		indexRevision(MAIN, DocumentBuilders.concept(Concepts.REFSET_WAS_A_ASSOCIATION)
				.parents(REFSET_HISTORICAL_ASSOCIATION_L)
				.build());
		indexRevision(MAIN, DocumentBuilders.concept(Concepts.REFSET_REPLACED_BY_ASSOCIATION)
				.parents(REFSET_HISTORICAL_ASSOCIATION_L)
				.build());
		indexRevision(MAIN, DocumentBuilders.concept(Concepts.REFSET_PARTIALLY_EQUIVALENT_TO_ASSOCIATION)
				.parents(REFSET_HISTORICAL_ASSOCIATION_L)
				.build());
		indexRevision(MAIN, DocumentBuilders.concept(Concepts.REFSET_POSSIBLY_EQUIVALENT_TO_ASSOCIATION)
				.parents(REFSET_HISTORICAL_ASSOCIATION_L)
				.build());
		
		// generate a focus concept substance hierarchy
		indexRevision(MAIN, DocumentBuilders.concept(SUBSTANCE)
				.build());
		
		indexRevision(MAIN, DocumentBuilders.concept(SUBSTANCE_CHILD_CONCEPT)
				.parents(SUBSTANCE_L)
				.build());
		
		// generate association members
		indexRevision(MAIN, SnomedRefSetMemberIndexEntry.builder()
				.id(UUID.randomUUID().toString())
				.active(true)
				.moduleId(Concepts.MODULE_SCT_CORE)
				.referencedComponentId(INACTIVE_CONCEPT_SAME_AS)
				.referenceSetType(SnomedRefSetType.ASSOCIATION)
				.refsetId(Concepts.REFSET_SAME_AS_ASSOCIATION)
				.targetComponentId(SUBSTANCE)
				.build());
		
		indexRevision(MAIN, SnomedRefSetMemberIndexEntry.builder()
				.id(UUID.randomUUID().toString())
				.active(true)
				.moduleId(Concepts.MODULE_SCT_CORE)
				.referencedComponentId(INACTIVE_CONCEPT_WAS_A)
				.referenceSetType(SnomedRefSetType.ASSOCIATION)
				.refsetId(Concepts.REFSET_WAS_A_ASSOCIATION)
				.targetComponentId(SUBSTANCE_CHILD_CONCEPT)
				.build());
		
		indexRevision(MAIN, SnomedRefSetMemberIndexEntry.builder()
				.id(UUID.randomUUID().toString())
				.active(true)
				.moduleId(Concepts.MODULE_SCT_CORE)
				.referencedComponentId(INACTIVE_CONCEPT_REPLACED_BY)
				.referenceSetType(SnomedRefSetType.ASSOCIATION)
				.refsetId(Concepts.REFSET_REPLACED_BY_ASSOCIATION)
				.targetComponentId(SUBSTANCE)
				.build());
		
		indexRevision(MAIN, SnomedRefSetMemberIndexEntry.builder()
				.id(UUID.randomUUID().toString())
				.active(true)
				.moduleId(Concepts.MODULE_SCT_CORE)
				.referencedComponentId(INACTIVE_CONCEPT_PARTIALLY_EQUIVALENT_TO)
				.referenceSetType(SnomedRefSetType.ASSOCIATION)
				.refsetId(Concepts.REFSET_PARTIALLY_EQUIVALENT_TO_ASSOCIATION)
				.targetComponentId(SUBSTANCE_CHILD_CONCEPT)
				.build());
		
		indexRevision(MAIN, SnomedRefSetMemberIndexEntry.builder()
				.id(UUID.randomUUID().toString())
				.active(true)
				.moduleId(Concepts.MODULE_SCT_CORE)
				.referencedComponentId(INACTIVE_CONCEPT_POSSIBLY_EQUIVALENT_TO)
				.referenceSetType(SnomedRefSetType.ASSOCIATION)
				.refsetId(Concepts.REFSET_POSSIBLY_EQUIVALENT_TO_ASSOCIATION)
				.targetComponentId(SUBSTANCE)
				.build());
		
		indexRevision(MAIN, SnomedRefSetMemberIndexEntry.builder()
				.id(UUID.randomUUID().toString())
				.active(true)
				.moduleId(Concepts.MODULE_SCT_CORE)
				.referencedComponentId(INACTIVE_CONCEPT_POSSIBLY_EQUIVALENT_TO)
				.referenceSetType(SnomedRefSetType.SIMPLE)
				.refsetId(SIMPLE_REFSET_ID)
				.build());
	}
	
	@Test
	public void profile_default() throws Exception {
		Expression actual = eval("<< " + SUBSTANCE + "  {{ + HISTORY }}");
		Expression expected = SnomedConceptDocument.Expressions.ids(Set.of(SUBSTANCE, SUBSTANCE_CHILD_CONCEPT, INACTIVE_CONCEPT_SAME_AS, INACTIVE_CONCEPT_WAS_A, INACTIVE_CONCEPT_REPLACED_BY, INACTIVE_CONCEPT_PARTIALLY_EQUIVALENT_TO, INACTIVE_CONCEPT_POSSIBLY_EQUIVALENT_TO));
		assertEquals("Default profile should return all historical associations", expected, actual);
	}
	
	@Test
	public void profile_all_association_refsets() throws Exception {
		Expression actual = eval("<< " + SUBSTANCE + "  {{ + HISTORY (*) }}"); // this equals with MAX profile
		Expression expected = SnomedConceptDocument.Expressions.ids(Set.of(SUBSTANCE, SUBSTANCE_CHILD_CONCEPT, INACTIVE_CONCEPT_SAME_AS, INACTIVE_CONCEPT_WAS_A, INACTIVE_CONCEPT_REPLACED_BY, INACTIVE_CONCEPT_PARTIALLY_EQUIVALENT_TO, INACTIVE_CONCEPT_POSSIBLY_EQUIVALENT_TO));
		assertEquals("Explicitly selecting all historical associations should return all", expected, actual);
	}
	
	@Test
	public void profile_min() throws Exception {
		Expression actual = eval("<< " + SUBSTANCE + "  {{ + HISTORY-MIN }}");
		Expression expected = SnomedConceptDocument.Expressions.ids(Set.of(SUBSTANCE, SUBSTANCE_CHILD_CONCEPT, INACTIVE_CONCEPT_SAME_AS));
		assertEquals("Minimum profile should only return Same As associations", expected, actual);
	}
	
	@Test
	public void profile_mod() throws Exception {
		Expression actual = eval("<< " + SUBSTANCE + "  {{ + HISTORY-MOD }}");
		Expression expected = SnomedConceptDocument.Expressions.ids(Set.of(SUBSTANCE, SUBSTANCE_CHILD_CONCEPT, INACTIVE_CONCEPT_SAME_AS, INACTIVE_CONCEPT_WAS_A, INACTIVE_CONCEPT_REPLACED_BY, INACTIVE_CONCEPT_PARTIALLY_EQUIVALENT_TO));
		assertEquals("Moderate profile should return Same As, Was A, Replaced By and Partially Replaced By associations", expected, actual);
	}
	
	@Test
	public void profile_max() throws Exception {
		Expression actual = eval("<< " + SUBSTANCE + "  {{ + HISTORY-MAX }}");
		Expression expected = SnomedConceptDocument.Expressions.ids(Set.of(SUBSTANCE, SUBSTANCE_CHILD_CONCEPT, INACTIVE_CONCEPT_SAME_AS, INACTIVE_CONCEPT_WAS_A, INACTIVE_CONCEPT_REPLACED_BY, INACTIVE_CONCEPT_PARTIALLY_EQUIVALENT_TO, INACTIVE_CONCEPT_POSSIBLY_EQUIVALENT_TO));
		assertEquals("Maximum profile should return all historical associations", expected, actual);
	}
	
	@Test
	public void profile_explicit_refsets() throws Exception {
		Expression actual = eval("<< " + SUBSTANCE + "  {{ + HISTORY ( " + Concepts.REFSET_REPLACED_BY_ASSOCIATION + " OR " + Concepts.REFSET_PARTIALLY_EQUIVALENT_TO_ASSOCIATION + ") }}");
		Expression expected = SnomedConceptDocument.Expressions.ids(Set.of(SUBSTANCE, SUBSTANCE_CHILD_CONCEPT, INACTIVE_CONCEPT_REPLACED_BY, INACTIVE_CONCEPT_PARTIALLY_EQUIVALENT_TO));
		assertEquals("Explicitly selecting certain historical association reference sets should return associations from those only", expected, actual);
	}
	
}
