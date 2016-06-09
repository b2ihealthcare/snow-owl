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
package com.b2international.snowowl.semanticengine.simpleast.normalform.test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.semanticengine.simpleast.normalform.AttributeClauseList;
import com.b2international.snowowl.semanticengine.simpleast.normalform.ConceptDefinition;
import com.b2international.snowowl.semanticengine.simpleast.normalform.ConceptDefinitionNormalizer;
import com.b2international.snowowl.semanticengine.simpleast.test.utils.TestUtils;
import com.b2international.snowowl.snomed.datastore.SnomedClientTerminologyBrowser;
import com.b2international.snowowl.snomed.dsl.query.queryast.AttributeClause;
import com.b2international.snowowl.snomed.dsl.query.queryast.ConceptRef;
import com.b2international.snowowl.snomed.dsl.query.queryast.RValue;

public class ConceptDefinitionNormalizerTest {

	private ConceptDefinitionNormalizer conceptDefinitionNormalizer;

	@Before
	public void beforeTest() {
		SnomedClientTerminologyBrowser terminologyBrowser = ApplicationContext.getInstance().getService(SnomedClientTerminologyBrowser.class);
		conceptDefinitionNormalizer = new ConceptDefinitionNormalizer(terminologyBrowser);
	}
	
	/**
	 * The concept "fracture of femur" (Figure 4) is fully defined and its proximal primitive 
	 * supertype is a high-level primitive2. 
	 */
	@Test
	public void testFractureOfFemur() {
		String focusConceptId = "71620000";	// fracture of femur
		ConceptRef focusConceptRef = TestUtils.buildConcept(focusConceptId);
		Map<ConceptRef, ConceptDefinition> normalizedConceptDefinitions = 
			conceptDefinitionNormalizer.getNormalizedConceptDefinitions(Collections.singleton(focusConceptRef));
		
		// associated morphology = fracture
		AttributeClause associatedMorphologyAttributeClause = TestUtils.buildAttribute("116676008", "72704001");
		// finding site = bone structure of femur
		AttributeClause findingSiteAttributeClause = TestUtils.buildAttribute("363698007", "71341001");
		AttributeClauseList expectedAttributeClauseList = TestUtils.buildAttributeClauseList(Arrays.asList(
				new AttributeClause[] { associatedMorphologyAttributeClause, findingSiteAttributeClause }));
		ConceptDefinition expectedConceptDefinition = TestUtils.buildConceptDefinition(expectedAttributeClauseList);
		
		ConceptDefinition actualNormalizedConceptDefinition = normalizedConceptDefinitions.values().iterator().next();
		
		TestUtils.assertConceptDefinitionsEqual(expectedConceptDefinition, actualNormalizedConceptDefinition);
	}
	
	/**
	 * The concept "neoplasm of right lower lobe of lung" Figure 7) is fully-defined with a high-level proximal primitive ("disease").
	 * However, the value of the "finding site" attribute ("structure of right lower lobe of lung") is itself fully defined.
	 */
	@Test
	public void testNeoplasmOfRightLowerLobeOfLung() {
		String focusConceptId = "126716006";	// neoplasm of right lower lobe of lung
		ConceptRef focusConceptRef = TestUtils.buildConcept(focusConceptId);
		Map<ConceptRef, ConceptDefinition> normalizedConceptDefinitions = 
			conceptDefinitionNormalizer.getNormalizedConceptDefinitions(Collections.singleton(focusConceptRef));
		
		// associated morphology = neoplasm
		AttributeClause associatedMorphologyAttributeClause = TestUtils.buildAttribute("116676008", "108369006");
		// finding site = (structure of lower lobe of lung : laterality = right)
		ConceptRef structureOfLowerLobeOfLung = TestUtils.buildUnconstrainedConceptGroup("90572001");
		AttributeClause lateralityRightAttributeClause = TestUtils.buildAttribute("272741003", "24028007");
		RValue findingSiteValueExpression = TestUtils.buildExpression(Collections.singletonList(structureOfLowerLobeOfLung), 
				Collections.<AttributeClauseList>emptyList(), 
				Collections.singletonList(lateralityRightAttributeClause));
		AttributeClause findingSiteAttributeClause = TestUtils.buildAttribute("363698007", findingSiteValueExpression);
		AttributeClauseList expectedAttributeClauseList = TestUtils.buildAttributeClauseList(Arrays.asList(
				new AttributeClause[] { associatedMorphologyAttributeClause, findingSiteAttributeClause }));
		ConceptDefinition expectedConceptDefinition = TestUtils.buildConceptDefinition(expectedAttributeClauseList);
		
		ConceptDefinition actualNormalizedConceptDefinition = normalizedConceptDefinitions.values().iterator().next();
		TestUtils.assertConceptDefinitionsEqual(expectedConceptDefinition, actualNormalizedConceptDefinition);
	}
	
	/**
	 * The concept "asthma" (Figure 5) is primitive so it is its own proximal primitive supertype. 
	 */
	@Test
	public void testAsthma() {
		String focusConceptId = "195967001";	// asthma
		ConceptRef focusConceptRef = TestUtils.buildConcept(focusConceptId);
		Map<ConceptRef, ConceptDefinition> normalizedConceptDefinitions = 
			conceptDefinitionNormalizer.getNormalizedConceptDefinitions(Collections.singleton(focusConceptRef));
		
		// finding site = Structure of respiratory system (body structure)
		AttributeClause findingSiteAttributeClause = TestUtils.buildAttribute("363698007", "20139000");
		ConceptDefinition expectedConceptDefinition = TestUtils.buildConceptDefinition(findingSiteAttributeClause);
		
		ConceptDefinition actualNormalizedConceptDefinition = normalizedConceptDefinitions.values().iterator().next();
		TestUtils.assertConceptDefinitionsEqual(expectedConceptDefinition, actualNormalizedConceptDefinition);
	}
	
	/**
	 * ConceptRef with more complex definition. Some concept definitions include 
	 * multiple instances of the same defining attribute. Usually these are grouped 
	 * separately for example to represent a procedure that examines one body structure and removes another.
	 */
	@Test
	public void testSalpingoOophporectomy() {
		String focusConceptId = "116028008";	// salpingo-oophorectomy
		ConceptRef focusConceptRef = TestUtils.buildConcept(focusConceptId);
		Map<ConceptRef, ConceptDefinition> normalizedConceptDefinitions = 
			conceptDefinitionNormalizer.getNormalizedConceptDefinitions(Collections.singleton(focusConceptRef));
		
		// TODO: confirm that 'procedure site' and 'procedure site - direct' are both in the normalized definition
		// and will be filtered out at a later stage 
		
		// method = excision - action
		AttributeClause methodAttribute1 = TestUtils.buildAttribute("260686004", "129304002");
		// procedure site - direct = Fallopian tube structure (body structure)
		AttributeClause procedureSiteDirectAttribute1 = TestUtils.buildAttribute("405813007", "31435000");
		// method = excision - action
		AttributeClause methodAttribute2 = TestUtils.buildAttribute("260686004", "129304002");
		// procedure site - direct = ovarian structure
		AttributeClause procedureSiteDirectAttribute2 = TestUtils.buildAttribute("405813007", "15497006");
		
		AttributeClauseList expectedAttributeGroup1 = TestUtils.buildAttributeClauseList(Arrays.asList(
				new AttributeClause[] { methodAttribute1, procedureSiteDirectAttribute1 }));
		AttributeClauseList expectedAttributeGroup2 = TestUtils.buildAttributeClauseList(Arrays.asList(
				new AttributeClause[] { methodAttribute2, procedureSiteDirectAttribute2 }));
		
		ConceptDefinition expectedConceptDefinition = TestUtils.buildConceptDefinition(Arrays.asList(expectedAttributeGroup1, expectedAttributeGroup2),
				Collections.<AttributeClause>emptySet());
		
		ConceptDefinition actualNormalizedConceptDefinition = normalizedConceptDefinitions.values().iterator().next();
		TestUtils.assertConceptDefinitionsEqual(expectedConceptDefinition, actualNormalizedConceptDefinition);
	}
	
}
