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

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.dsl.scg.Attribute;
import com.b2international.snowowl.dsl.scg.Concept;
import com.b2international.snowowl.dsl.scg.Expression;
import com.b2international.snowowl.dsl.scg.Group;
import com.b2international.snowowl.semanticengine.normalform.ConceptDefinition;
import com.b2international.snowowl.semanticengine.normalform.ConceptDefinitionNormalizer;
import com.b2international.snowowl.semanticengine.test.utils.TestUtils;
import com.b2international.snowowl.semanticengine.utils.ScgBuilderUtils;
import com.b2international.snowowl.snomed.datastore.RecursiveTerminologyBrowser;
import com.b2international.snowowl.snomed.datastore.SnomedClientStatementBrowser;
import com.b2international.snowowl.snomed.datastore.SnomedClientTerminologyBrowser;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptIndexEntry;

public class ConceptDefinitionNormalizerTest {

	private ConceptDefinitionNormalizer conceptDefinitionNormalizer;

	@Before
	public void beforeTest() {
		SnomedClientTerminologyBrowser terminologyBrowser = ApplicationContext.getInstance().getService(SnomedClientTerminologyBrowser.class);
		RecursiveTerminologyBrowser<SnomedConceptIndexEntry, String> recursiveTerminologyBrowser = 
				new RecursiveTerminologyBrowser<SnomedConceptIndexEntry, String>(terminologyBrowser);
		SnomedClientStatementBrowser statementBrowser = ApplicationContext.getInstance().getService(SnomedClientStatementBrowser.class);
		conceptDefinitionNormalizer = new ConceptDefinitionNormalizer(recursiveTerminologyBrowser, statementBrowser);
	}
	
	/**
	 * The concept "fracture of femur" (Figure 4) is fully defined and its proximal primitive 
	 * supertype is a high-level primitive2. 
	 */
	@Test
	public void testFractureOfFemur() {
		String focusConceptId = "71620000";	// fracture of femur
		Concept focusConcept = ScgBuilderUtils.buildConcept(focusConceptId);
		Map<Concept, ConceptDefinition> normalizedConceptDefinitions = 
			conceptDefinitionNormalizer.getNormalizedConceptDefinitions(Collections.singleton(focusConcept));
		
		// associated morphology = fracture
		Attribute associatedMorphologyAttribute = ScgBuilderUtils.buildAttribute("116676008", "72704001");
		// finding site = bone structure of femur
		Attribute findingSiteAttribute = ScgBuilderUtils.buildAttribute("363698007", "71341001");
		Group expectedGroup = ScgBuilderUtils.buildGroup(Arrays.asList(
				new Attribute[] { associatedMorphologyAttribute, findingSiteAttribute }));
		ConceptDefinition expectedConceptDefinition = ScgBuilderUtils.buildConceptDefinition(expectedGroup);
		
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
		Concept focusConcept = ScgBuilderUtils.buildConcept(focusConceptId);
		Map<Concept, ConceptDefinition> normalizedConceptDefinitions = 
			conceptDefinitionNormalizer.getNormalizedConceptDefinitions(Collections.singleton(focusConcept));
		
		// associated morphology = neoplasm
		Attribute associatedMorphologyAttribute = ScgBuilderUtils.buildAttribute("116676008", "108369006");
		// finding site = (structure of lower lobe of lung : laterality = right)
		Concept structureOfLowerLobeOfLung = ScgBuilderUtils.buildConcept("90572001");
		Attribute lateralityRightAttribute = ScgBuilderUtils.buildAttribute("272741003", "24028007");
		Expression findingSiteValueExpression = ScgBuilderUtils.buildExpression(Collections.singletonList(structureOfLowerLobeOfLung), 
				Collections.<Group>emptyList(), 
				Collections.singletonList(lateralityRightAttribute));
		Attribute findingSiteAttribute = ScgBuilderUtils.buildAttribute("363698007", findingSiteValueExpression);
		Group expectedGroup = ScgBuilderUtils.buildGroup(Arrays.asList(
				new Attribute[] { associatedMorphologyAttribute, findingSiteAttribute }));
		ConceptDefinition expectedConceptDefinition = ScgBuilderUtils.buildConceptDefinition(expectedGroup);
		
		ConceptDefinition actualNormalizedConceptDefinition = normalizedConceptDefinitions.values().iterator().next();
		TestUtils.assertConceptDefinitionsEqual(expectedConceptDefinition, actualNormalizedConceptDefinition);
	}
	
	/**
	 * The concept "asthma" (Figure 5) is primitive so it is its own proximal primitive supertype. 
	 */
	@Test
	public void testAsthma() {
		String focusConceptId = "195967001";	// asthma
		Concept focusConcept = ScgBuilderUtils.buildConcept(focusConceptId);
		Map<Concept, ConceptDefinition> normalizedConceptDefinitions = 
			conceptDefinitionNormalizer.getNormalizedConceptDefinitions(Collections.singleton(focusConcept));
		
		// finding site = Structure of respiratory system (body structure)
		Attribute findingSiteAttribute = ScgBuilderUtils.buildAttribute("363698007", "20139000");
		ConceptDefinition expectedConceptDefinition = ScgBuilderUtils.buildConceptDefinition(findingSiteAttribute);
		
		ConceptDefinition actualNormalizedConceptDefinition = normalizedConceptDefinitions.values().iterator().next();
		TestUtils.assertConceptDefinitionsEqual(expectedConceptDefinition, actualNormalizedConceptDefinition);
	}
	
	/**
	 * Concept with more complex definition. Some concept definitions include 
	 * multiple instances of the same defining attribute. Usually these are grouped 
	 * separately for example to represent a procedure that examines one body structure and removes another.
	 */
	@Test
	public void testSalpingoOophporectomy() {
		String focusConceptId = "116028008";	// salpingo-oophorectomy
		Concept focusConcept = ScgBuilderUtils.buildConcept(focusConceptId);
		Map<Concept, ConceptDefinition> normalizedConceptDefinitions = 
			conceptDefinitionNormalizer.getNormalizedConceptDefinitions(Collections.singleton(focusConcept));
		
		// TODO: confirm that 'procedure site' and 'procedure site - direct' are both in the normalized definition
		// and will be filtered out at a later stage 
		
		// method = excision - action
		Attribute methodAttribute1 = ScgBuilderUtils.buildAttribute("260686004", "129304002");
		// procedure site - direct = Fallopian tube structure (body structure)
		Attribute procedureSiteDirectAttribute1 = ScgBuilderUtils.buildAttribute("405813007", "31435000");
		// method = excision - action
		Attribute methodAttribute2 = ScgBuilderUtils.buildAttribute("260686004", "129304002");
		// procedure site - direct = ovarian structure
		Attribute procedureSiteDirectAttribute2 = ScgBuilderUtils.buildAttribute("405813007", "15497006");
		
		Group expectedGroup1 = ScgBuilderUtils.buildGroup(Arrays.asList(
				new Attribute[] { methodAttribute1, procedureSiteDirectAttribute1 }));
		Group expectedGroup2 = ScgBuilderUtils.buildGroup(Arrays.asList(
				new Attribute[] { methodAttribute2, procedureSiteDirectAttribute2 }));
		
		ConceptDefinition expectedConceptDefinition = ScgBuilderUtils.buildConceptDefinition(Arrays.asList(expectedGroup1, expectedGroup2),
				Collections.<Attribute>emptySet());
		
		ConceptDefinition actualNormalizedConceptDefinition = normalizedConceptDefinitions.values().iterator().next();
		TestUtils.assertConceptDefinitionsEqual(expectedConceptDefinition, actualNormalizedConceptDefinition);
	}
	
}
