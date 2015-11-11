/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.semanticengine.normalform.test;

import static com.b2international.snowowl.semanticengine.test.utils.TestUtils.assertConceptDefinitionsEqual;
import static com.b2international.snowowl.semanticengine.utils.ScgBuilderUtils.buildAttribute;
import static com.b2international.snowowl.semanticengine.utils.ScgBuilderUtils.buildConcept;
import static com.b2international.snowowl.semanticengine.utils.ScgBuilderUtils.buildConceptDefinition;
import static com.b2international.snowowl.semanticengine.utils.ScgBuilderUtils.buildExpression;
import static com.b2international.snowowl.semanticengine.utils.ScgBuilderUtils.buildGroup;
import static org.junit.Assert.assertEquals;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.dsl.scg.Attribute;
import com.b2international.snowowl.dsl.scg.Expression;
import com.b2international.snowowl.dsl.scg.Group;
import com.b2international.snowowl.semanticengine.normalform.ConceptDefinition;
import com.b2international.snowowl.semanticengine.normalform.FocusConceptNormalizationResult;
import com.b2international.snowowl.semanticengine.normalform.FocusConceptNormalizer;
import com.b2international.snowowl.semanticengine.test.SnomedConcepts;
import com.b2international.snowowl.snomed.datastore.RecursiveTerminologyBrowser;
import com.b2international.snowowl.snomed.datastore.SnomedClientStatementBrowser;
import com.b2international.snowowl.snomed.datastore.SnomedClientTerminologyBrowser;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptIndexEntry;

public class FocusConceptNormalizerTest {

	private FocusConceptNormalizer focusConceptNormalizer;
	private RecursiveTerminologyBrowser<SnomedConceptIndexEntry, String> terminologyBrowser;
	
	@Before
	public void beforeTest() {
		SnomedClientTerminologyBrowser snomedTerminologyBrowser = ApplicationContext.getInstance().getService(SnomedClientTerminologyBrowser.class);
		terminologyBrowser = new RecursiveTerminologyBrowser<SnomedConceptIndexEntry, String>(snomedTerminologyBrowser);
		focusConceptNormalizer = new FocusConceptNormalizer(terminologyBrowser,
				ApplicationContext.getInstance().getService(SnomedClientStatementBrowser.class));
	}
	
	@Test
	public void testFractureOfFemur() {
		Expression expression = buildExpression(buildConcept(SnomedConcepts.FRACTURE_OF_FEMUR));
		FocusConceptNormalizationResult normalizedFocusConcepts = focusConceptNormalizer.normalizeFocusConcepts(expression.getConcepts());
		
		// fracture of bone
		SnomedConceptIndexEntry expectedFocusConcept = terminologyBrowser.getConcept(SnomedConcepts.FRACTURE_OF_BONE);
		assertEquals(Collections.singletonList(expectedFocusConcept), normalizedFocusConcepts.filteredPrimitiveSuperTypes);

		Attribute associatedMorphologyAttribute = buildAttribute(SnomedConcepts.ASSOCIATED_MORPHOLOGY, SnomedConcepts.FRACTURE);
		Attribute findingSiteAttribute = buildAttribute(SnomedConcepts.FINDING_SITE, SnomedConcepts.BONE_STRUCTURE_OF_FEMUR);
		Group expectedGroup = buildGroup(associatedMorphologyAttribute, findingSiteAttribute);
		ConceptDefinition expectedConceptDefinition = buildConceptDefinition(expectedGroup);
		assertConceptDefinitionsEqual(expectedConceptDefinition, normalizedFocusConcepts.mergedConceptDefinition);
	}
	
	@Test
	public void testFootPain() {
		Expression expression = buildExpression(buildConcept(SnomedConcepts.FOOT_PAIN));
		FocusConceptNormalizationResult normalizedFocusConcepts = focusConceptNormalizer.normalizeFocusConcepts(expression.getConcepts());
		
		SnomedConceptIndexEntry expectedFocusConcept = terminologyBrowser.getConcept(SnomedConcepts.PAIN);
		assertEquals(Collections.singletonList(expectedFocusConcept), normalizedFocusConcepts.filteredPrimitiveSuperTypes);
		
		Attribute findingSiteFootStructure = buildAttribute(SnomedConcepts.FINDING_SITE, SnomedConcepts.FOOT_STRUCTURE);
		ConceptDefinition expectedConceptDefinition = buildConceptDefinition(findingSiteFootStructure);
		assertConceptDefinitionsEqual(expectedConceptDefinition, normalizedFocusConcepts.mergedConceptDefinition);
	}
	
	@Test
	public void testSalpingoOophorectomy() {
		Expression expression = buildExpression(buildConcept(SnomedConcepts.SALPINGO_OOPHORECTOMY));
		FocusConceptNormalizationResult normalizedFocusConcepts = focusConceptNormalizer.normalizeFocusConcepts(expression.getConcepts());
		
		SnomedConceptIndexEntry expectedFocusConcept = terminologyBrowser.getConcept(SnomedConcepts.EXCISION_OF_PELVIS);
		assertEquals(Collections.singletonList(expectedFocusConcept), normalizedFocusConcepts.filteredPrimitiveSuperTypes);
		
		Attribute methodExcisionAction1 = buildAttribute(SnomedConcepts.METHOD, SnomedConcepts.EXCISION_ACTION);
		Attribute procedureSiteFallopianTubeStructure = buildAttribute(SnomedConcepts.PROCEDURE_SITE_DIRECT, 
				SnomedConcepts.FALLOPIAN_TUBE_STRUCTURE);
		Attribute methodExcisionAction2 = buildAttribute(SnomedConcepts.METHOD, SnomedConcepts.EXCISION_ACTION);
		Attribute procedureSiteOvarianStructure = buildAttribute(SnomedConcepts.PROCEDURE_SITE_DIRECT, 
				SnomedConcepts.OVARIAN_STRUCTURE);
		
		ConceptDefinition expectedConceptDefinition = buildConceptDefinition(buildGroup(methodExcisionAction1, procedureSiteFallopianTubeStructure), 
				buildGroup(methodExcisionAction2, procedureSiteOvarianStructure));
		assertConceptDefinitionsEqual(expectedConceptDefinition, normalizedFocusConcepts.mergedConceptDefinition);
	}
	
	
	@Test
	public void testAuscultation() {
		Expression expression = buildExpression(buildConcept(SnomedConcepts.AUSCULTATION));
		FocusConceptNormalizationResult normalizedFocusConcepts = focusConceptNormalizer.normalizeFocusConcepts(expression.getConcepts());
		
		SnomedConceptIndexEntry expectedFocusConcept = terminologyBrowser.getConcept(SnomedConcepts.AUSCULTATION);
		assertEquals(Collections.singletonList(expectedFocusConcept), normalizedFocusConcepts.filteredPrimitiveSuperTypes);
		
		Attribute methodAuscultationAction = buildAttribute(SnomedConcepts.METHOD, SnomedConcepts.AUSCULTATION_ACTION);
		ConceptDefinition expectedConceptDefinition = buildConceptDefinition(methodAuscultationAction);
		assertConceptDefinitionsEqual(expectedConceptDefinition, normalizedFocusConcepts.mergedConceptDefinition);
	}
}