/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.semanticengine.simpleast.normalform.test;

import static com.b2international.snowowl.semanticengine.simpleast.test.utils.TestUtils.assertConceptDefinitionsEqual;
import static com.b2international.snowowl.semanticengine.simpleast.test.utils.TestUtils.buildAttribute;
import static com.b2international.snowowl.semanticengine.simpleast.test.utils.TestUtils.buildAttributeClauseList;
import static com.b2international.snowowl.semanticengine.simpleast.test.utils.TestUtils.buildConceptDefinition;
import static com.b2international.snowowl.semanticengine.simpleast.test.utils.TestUtils.buildExpression;
import static com.b2international.snowowl.semanticengine.simpleast.test.utils.TestUtils.buildUnconstrainedConceptGroup;
import static org.junit.Assert.assertEquals;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.semanticengine.simpleast.normalform.AttributeClauseList;
import com.b2international.snowowl.semanticengine.simpleast.normalform.ConceptDefinition;
import com.b2international.snowowl.semanticengine.simpleast.normalform.FocusConceptNormalizationResult;
import com.b2international.snowowl.semanticengine.simpleast.normalform.FocusConceptNormalizer;
import com.b2international.snowowl.semanticengine.simpleast.test.SnomedConcepts;
import com.b2international.snowowl.semanticengine.simpleast.utils.QueryAstUtils;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.dsl.query.queryast.AttributeClause;
import com.b2international.snowowl.snomed.dsl.query.queryast.RValue;
import com.google.common.base.Function;

public class FocusConceptNormalizerTest {

	private FocusConceptNormalizer focusConceptNormalizer;
	
	@Before
	public void beforeTest() {
		focusConceptNormalizer = new FocusConceptNormalizer(Branch.MAIN_PATH);
	}
	
	@Test
	public void testFractureOfFemur() {
		RValue expression = buildExpression(buildUnconstrainedConceptGroup(SnomedConcepts.FRACTURE_OF_FEMUR));
		FocusConceptNormalizationResult normalizedFocusConcepts = focusConceptNormalizer.normalizeFocusConcepts(QueryAstUtils.getFocusConcepts(expression));
		
		// fracture of bone
		SnomedConceptDocument expectedFocusConcept = getConcept(SnomedConcepts.FRACTURE_OF_BONE);
		assertEquals(Collections.singletonList(expectedFocusConcept), normalizedFocusConcepts.filteredPrimitiveSuperTypes);

		AttributeClause associatedMorphologyAttribute = buildAttribute(SnomedConcepts.ASSOCIATED_MORPHOLOGY, SnomedConcepts.FRACTURE);
		AttributeClause findingSiteAttribute = buildAttribute(SnomedConcepts.FINDING_SITE, SnomedConcepts.BONE_STRUCTURE_OF_FEMUR);
		AttributeClauseList expectedAttributeGroup = buildAttributeClauseList(associatedMorphologyAttribute, findingSiteAttribute);
		ConceptDefinition expectedConceptDefinition = buildConceptDefinition(expectedAttributeGroup);
		assertConceptDefinitionsEqual(expectedConceptDefinition, normalizedFocusConcepts.mergedConceptDefinition);
	}
	
	@Test
	public void testFootPain() {
		RValue expression = buildExpression(buildUnconstrainedConceptGroup(SnomedConcepts.FOOT_PAIN));
		FocusConceptNormalizationResult normalizedFocusConcepts = focusConceptNormalizer.normalizeFocusConcepts(QueryAstUtils.getFocusConcepts(expression));
		
		SnomedConceptDocument expectedFocusConcept = getConcept(SnomedConcepts.PAIN);
		assertEquals(Collections.singletonList(expectedFocusConcept), normalizedFocusConcepts.filteredPrimitiveSuperTypes);
		
		AttributeClause findingSiteFootStructure = buildAttribute(SnomedConcepts.FINDING_SITE, SnomedConcepts.FOOT_STRUCTURE);
		ConceptDefinition expectedConceptDefinition = buildConceptDefinition(findingSiteFootStructure);
		assertConceptDefinitionsEqual(expectedConceptDefinition, normalizedFocusConcepts.mergedConceptDefinition);
	}
	
	@Test
	public void testSalpingoOophorectomy() {
		RValue expression = buildExpression(buildUnconstrainedConceptGroup(SnomedConcepts.SALPINGO_OOPHORECTOMY));
		FocusConceptNormalizationResult normalizedFocusConcepts = focusConceptNormalizer.normalizeFocusConcepts(QueryAstUtils.getFocusConcepts(expression));
		
		SnomedConceptDocument expectedFocusConcept = getConcept(SnomedConcepts.EXCISION_OF_PELVIS);
		assertEquals(Collections.singletonList(expectedFocusConcept), normalizedFocusConcepts.filteredPrimitiveSuperTypes);
		
		AttributeClause methodExcisionAction1 = buildAttribute(SnomedConcepts.METHOD, SnomedConcepts.EXCISION_ACTION);
		AttributeClause procedureSiteFallopianTubeStructure = buildAttribute(SnomedConcepts.PROCEDURE_SITE_DIRECT, 
				SnomedConcepts.FALLOPIAN_TUBE_STRUCTURE);
		AttributeClause methodExcisionAction2 = buildAttribute(SnomedConcepts.METHOD, SnomedConcepts.EXCISION_ACTION);
		AttributeClause procedureSiteOvarianStructure = buildAttribute(SnomedConcepts.PROCEDURE_SITE_DIRECT, 
				SnomedConcepts.OVARIAN_STRUCTURE);
		
		ConceptDefinition expectedConceptDefinition = buildConceptDefinition(buildAttributeClauseList(methodExcisionAction1, procedureSiteFallopianTubeStructure), 
				buildAttributeClauseList(methodExcisionAction2, procedureSiteOvarianStructure));
		assertConceptDefinitionsEqual(expectedConceptDefinition, normalizedFocusConcepts.mergedConceptDefinition);
	}
	
	@Test
	public void testAuscultation() {
		RValue expression = buildExpression(buildUnconstrainedConceptGroup(SnomedConcepts.AUSCULTATION));
		FocusConceptNormalizationResult normalizedFocusConcepts = focusConceptNormalizer.normalizeFocusConcepts(QueryAstUtils.getFocusConcepts(expression));
		
		SnomedConceptDocument expectedFocusConcept = getConcept(SnomedConcepts.AUSCULTATION);
		assertEquals(Collections.singletonList(expectedFocusConcept), normalizedFocusConcepts.filteredPrimitiveSuperTypes);
		
		AttributeClause methodAuscultationAction = buildAttribute(SnomedConcepts.METHOD, SnomedConcepts.AUSCULTATION_ACTION);
		ConceptDefinition expectedConceptDefinition = buildConceptDefinition(methodAuscultationAction);
		assertConceptDefinitionsEqual(expectedConceptDefinition, normalizedFocusConcepts.mergedConceptDefinition);
	}

	private SnomedConceptDocument getConcept(String conceptId) {
		return SnomedRequests.prepareGetConcept(conceptId)
				.build(SnomedDatastoreActivator.REPOSITORY_UUID, Branch.MAIN_PATH)
				.execute(ApplicationContext.getServiceForClass(IEventBus.class))
				.then(new Function<SnomedConcept, SnomedConceptDocument>() {
					@Override
					public SnomedConceptDocument apply(SnomedConcept input) {
						return SnomedConceptDocument.builder(input).build();
					}
				})
				.getSync();
	}
}