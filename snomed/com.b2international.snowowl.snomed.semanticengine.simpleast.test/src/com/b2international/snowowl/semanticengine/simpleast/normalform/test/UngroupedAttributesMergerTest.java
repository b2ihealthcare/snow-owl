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
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.junit.Test;

import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.semanticengine.simpleast.normalform.AttributeClauseList;
import com.b2international.snowowl.semanticengine.simpleast.normalform.ConceptDefinition;
import com.b2international.snowowl.semanticengine.simpleast.normalform.UngroupedAttributesMerger;
import com.b2international.snowowl.semanticengine.simpleast.subsumption.SubsumptionTester;
import com.b2international.snowowl.semanticengine.simpleast.test.SnomedConcepts;
import com.b2international.snowowl.semanticengine.simpleast.test.utils.TestUtils;
import com.b2international.snowowl.snomed.dsl.query.queryast.AttributeClause;
import com.b2international.snowowl.snomed.dsl.query.queryast.ConceptRef;

public class UngroupedAttributesMergerTest {
	
	@Test
	public void testMergeSingleConceptDefinition() {
		
		UngroupedAttributesMerger merger = new UngroupedAttributesMerger(new SubsumptionTester(Branch.MAIN_PATH));
		
		AttributeClause findingSiteLungStructure = TestUtils.buildAttribute(SnomedConcepts.FINDING_SITE, SnomedConcepts.LUNG_STRUCTURE);
		AttributeClause causativeAgentDustAllergen = TestUtils.buildAttribute(SnomedConcepts.CAUSATIVE_AGENT, SnomedConcepts.DUST_ALLERGEN);
		ConceptDefinition conceptDefinition1 = TestUtils.buildConceptDefinition(findingSiteLungStructure, causativeAgentDustAllergen);
		
		Map<ConceptRef, ConceptDefinition> conceptDefinitionMap = new HashMap<ConceptRef, ConceptDefinition>();
		ConceptRef concept1 = TestUtils.buildConcept("CONCEPT_1");
		conceptDefinitionMap.put(concept1, conceptDefinition1);
		
		ConceptDefinition expectedConceptDefinition = TestUtils.buildConceptDefinition(EcoreUtil.copy(findingSiteLungStructure), 
				EcoreUtil.copy(causativeAgentDustAllergen));
		
		ConceptDefinition mergedConceptDefinition = new ConceptDefinition();
		merger.mergeUngroupedAttributes(conceptDefinitionMap, mergedConceptDefinition);
		
		TestUtils.assertConceptDefinitionsEqual(expectedConceptDefinition, mergedConceptDefinition);
	}
	
	@Test
	public void testUngroupedAttributeNameMatch1() {
		/* 
		 * If the value of one of the name-matched attributes subsumes the other value
		 * 		include the attribute with the most specific value (not grouped)
		 */
		
		UngroupedAttributesMerger merger = new UngroupedAttributesMerger(new SubsumptionTester(Branch.MAIN_PATH));
		Map<ConceptRef, ConceptDefinition> conceptDefinitionMap = new HashMap<ConceptRef, ConceptDefinition>();
		
		AttributeClause findingSiteLungStructure = TestUtils.buildAttribute(SnomedConcepts.FINDING_SITE, SnomedConcepts.LUNG_STRUCTURE);
		AttributeClause causativeAgentDustAllergen = TestUtils.buildAttribute(SnomedConcepts.CAUSATIVE_AGENT, SnomedConcepts.DUST_ALLERGEN);
		AttributeClause severitySevere = TestUtils.buildAttribute(SnomedConcepts.SEVERITY, SnomedConcepts.SEVERE);
		AttributeClause causativeAgentSubstance = TestUtils.buildAttribute(SnomedConcepts.CAUSATIVE_AGENT, SnomedConcepts.SUBSTANCE);

		// build test concept definitions
		ConceptRef concept1 = TestUtils.buildConcept("CONCEPT_1");
		ConceptDefinition conceptDefinition1 = TestUtils.buildConceptDefinition(findingSiteLungStructure, causativeAgentDustAllergen);
		conceptDefinitionMap.put(concept1, conceptDefinition1);
		ConceptRef concept2 = TestUtils.buildConcept("CONCEPT_2");
		ConceptDefinition conceptDefinition2 = TestUtils.buildConceptDefinition(severitySevere, causativeAgentSubstance);
		conceptDefinitionMap.put(concept2, conceptDefinition2);
		
		// build expected results
		ConceptDefinition expectedConceptDefinition = TestUtils.buildConceptDefinition(findingSiteLungStructure, 
				severitySevere, causativeAgentDustAllergen);
		
		ConceptDefinition mergedConceptDefinition = new ConceptDefinition();
		merger.mergeUngroupedAttributes(conceptDefinitionMap, mergedConceptDefinition);
		
		TestUtils.assertConceptDefinitionsEqual(expectedConceptDefinition, mergedConceptDefinition);
	}

	@Test
	public void testUngroupedAttributeNameMatch1Reverse() {
		/* 
		 * If the value of one of the name-matched attributes subsumes the other value
		 * 		include the attribute with the most specific value (not grouped)
		 */
		
		UngroupedAttributesMerger merger = new UngroupedAttributesMerger(new SubsumptionTester(Branch.MAIN_PATH));
		Map<ConceptRef, ConceptDefinition> conceptDefinitionMap = new HashMap<ConceptRef, ConceptDefinition>();
		
		AttributeClause findingSiteLungStructure = TestUtils.buildAttribute(SnomedConcepts.FINDING_SITE, SnomedConcepts.LUNG_STRUCTURE);
		AttributeClause causativeAgentDustAllergen = TestUtils.buildAttribute(SnomedConcepts.CAUSATIVE_AGENT, SnomedConcepts.DUST_ALLERGEN);
		AttributeClause severitySevere = TestUtils.buildAttribute(SnomedConcepts.SEVERITY, SnomedConcepts.SEVERE);
		AttributeClause causativeAgentSubstance = TestUtils.buildAttribute(SnomedConcepts.CAUSATIVE_AGENT, SnomedConcepts.SUBSTANCE);

		// build test concept definitions
		ConceptRef concept2 = TestUtils.buildConcept("CONCEPT_2");
		ConceptDefinition conceptDefinition2 = TestUtils.buildConceptDefinition(severitySevere, causativeAgentSubstance);
		conceptDefinitionMap.put(concept2, conceptDefinition2);
		ConceptRef concept1 = TestUtils.buildConcept("CONCEPT_1");
		ConceptDefinition conceptDefinition1 = TestUtils.buildConceptDefinition(findingSiteLungStructure, causativeAgentDustAllergen);
		conceptDefinitionMap.put(concept1, conceptDefinition1);
		
		// build expected results
		ConceptDefinition expectedConceptDefinition = TestUtils.buildConceptDefinition(findingSiteLungStructure, 
				severitySevere, causativeAgentDustAllergen);
		
		ConceptDefinition mergedConceptDefinition = new ConceptDefinition();
		merger.mergeUngroupedAttributes(conceptDefinitionMap, mergedConceptDefinition);
		
		TestUtils.assertConceptDefinitionsEqual(expectedConceptDefinition, mergedConceptDefinition);
	}
	
	@Test
	public void testUngroupedAttributeNameMatch2() {
		/* 
		 * If the value of the name-matched attributes are identical
		 * 		include one and omit the other
		 */
		
		UngroupedAttributesMerger merger = new UngroupedAttributesMerger(new SubsumptionTester(Branch.MAIN_PATH));
		Map<ConceptRef, ConceptDefinition> conceptDefinitionMap = new HashMap<ConceptRef, ConceptDefinition>();
		
		AttributeClause findingSiteLungStructure = TestUtils.buildAttribute(SnomedConcepts.FINDING_SITE, SnomedConcepts.LUNG_STRUCTURE);
		AttributeClause causativeAgentDustAllergen = TestUtils.buildAttribute(SnomedConcepts.CAUSATIVE_AGENT, SnomedConcepts.DUST_ALLERGEN);
		AttributeClause severitySevere = TestUtils.buildAttribute(SnomedConcepts.SEVERITY, SnomedConcepts.SEVERE);
		AttributeClause causativeAgentDustAllergen2 = TestUtils.buildAttribute(SnomedConcepts.CAUSATIVE_AGENT, SnomedConcepts.DUST_ALLERGEN);
		
		// build test concept definitions
		ConceptRef concept1 = TestUtils.buildConcept("CONCEPT_1");
		ConceptDefinition conceptDefinition1 = TestUtils.buildConceptDefinition(findingSiteLungStructure, causativeAgentDustAllergen);
		conceptDefinitionMap.put(concept1, conceptDefinition1);
		ConceptRef concept2 = TestUtils.buildConcept("CONCEPT_2");
		ConceptDefinition conceptDefinition2 = TestUtils.buildConceptDefinition(severitySevere, causativeAgentDustAllergen2);
		conceptDefinitionMap.put(concept2, conceptDefinition2);
		
		// build expected results
		ConceptDefinition expectedConceptDefinition = TestUtils.buildConceptDefinition(findingSiteLungStructure, severitySevere, 
				causativeAgentDustAllergen);
		
		ConceptDefinition mergedConceptDefinition = new ConceptDefinition();
		merger.mergeUngroupedAttributes(conceptDefinitionMap, mergedConceptDefinition);
		
		TestUtils.assertConceptDefinitionsEqual(expectedConceptDefinition, mergedConceptDefinition);
	}
	
	@Test
	public void testUngroupedAttributeNameMatch2Reverse() {
		/* 
		 * If the value of the name-matched attributes are identical
		 * 		include one and omit the other
		 */
		
		UngroupedAttributesMerger merger = new UngroupedAttributesMerger(new SubsumptionTester(Branch.MAIN_PATH));
		Map<ConceptRef, ConceptDefinition> conceptDefinitionMap = new HashMap<ConceptRef, ConceptDefinition>();
		
		AttributeClause findingSiteLungStructure = TestUtils.buildAttribute(SnomedConcepts.FINDING_SITE, SnomedConcepts.LUNG_STRUCTURE);
		AttributeClause causativeAgentDustAllergen = TestUtils.buildAttribute(SnomedConcepts.CAUSATIVE_AGENT, SnomedConcepts.DUST_ALLERGEN);
		
		AttributeClause severitySevere = TestUtils.buildAttribute(SnomedConcepts.SEVERITY, SnomedConcepts.SEVERE);
		AttributeClause causativeAgentDustAllergen2 = TestUtils.buildAttribute(SnomedConcepts.CAUSATIVE_AGENT, SnomedConcepts.DUST_ALLERGEN);
		
		// build test concept definitions
		ConceptRef concept2 = TestUtils.buildConcept("CONCEPT_2");
		ConceptDefinition conceptDefinition2 = TestUtils.buildConceptDefinition(severitySevere, causativeAgentDustAllergen2);
		conceptDefinitionMap.put(concept2, conceptDefinition2);
		ConceptRef concept1 = TestUtils.buildConcept("CONCEPT_1");
		ConceptDefinition conceptDefinition1 = TestUtils.buildConceptDefinition(findingSiteLungStructure, causativeAgentDustAllergen);
		conceptDefinitionMap.put(concept1, conceptDefinition1);
		
		// build expected results
		ConceptDefinition expectedConceptDefinition = TestUtils.buildConceptDefinition(findingSiteLungStructure, severitySevere, 
				causativeAgentDustAllergen);
		
		ConceptDefinition mergedConceptDefinition = new ConceptDefinition();
		merger.mergeUngroupedAttributes(conceptDefinitionMap, mergedConceptDefinition);
		
		TestUtils.assertConceptDefinitionsEqual(expectedConceptDefinition, mergedConceptDefinition);
	}

	@Test
	public void testUngroupedAttributeNameMatch3() {
		/*
		 * If neither of the two preceding conditions apply
		 *		include both attributes (not grouped) 
		 */
		
		UngroupedAttributesMerger merger = new UngroupedAttributesMerger(new SubsumptionTester(Branch.MAIN_PATH));
		Map<ConceptRef, ConceptDefinition> conceptDefinitionMap = new HashMap<ConceptRef, ConceptDefinition>();
		
		AttributeClause findingSiteLungStructure = TestUtils.buildAttribute(SnomedConcepts.FINDING_SITE, 
				SnomedConcepts.LUNG_STRUCTURE);
		AttributeClause severitySevere = TestUtils.buildAttribute(SnomedConcepts.SEVERITY, 
				SnomedConcepts.SEVERE);
		AttributeClause findingSiteHeadStructure = TestUtils.buildAttribute(SnomedConcepts.FINDING_SITE, 
				SnomedConcepts.HEAD_STRUCTURE);

		// build test concept definitions
		ConceptRef concept1 = TestUtils.buildConcept("CONCEPT_1");
		ConceptDefinition conceptDefinition1 = TestUtils.buildConceptDefinition(findingSiteLungStructure);
		conceptDefinitionMap.put(concept1, conceptDefinition1);
		ConceptRef concept2 = TestUtils.buildConcept("CONCEPT_2");
		ConceptDefinition conceptDefinition2 = TestUtils.buildConceptDefinition(severitySevere, findingSiteHeadStructure);
		conceptDefinitionMap.put(concept2, conceptDefinition2);
		
		// build expected results
		ConceptDefinition expectedConceptDefinition = TestUtils.buildConceptDefinition(findingSiteLungStructure, severitySevere, 
				findingSiteHeadStructure);
		
		ConceptDefinition mergedConceptDefinition = new ConceptDefinition();
		merger.mergeUngroupedAttributes(conceptDefinitionMap, mergedConceptDefinition);
		
		TestUtils.assertConceptDefinitionsEqual(expectedConceptDefinition, mergedConceptDefinition);
	}
	
	@Test
	public void testUngroupedAttributeNameMatch3Reverse() {
		/*
		 * If neither of the two preceding conditions apply
		 *		include both attributes (not grouped) 
		 */
		
		UngroupedAttributesMerger merger = new UngroupedAttributesMerger(new SubsumptionTester(Branch.MAIN_PATH));
		Map<ConceptRef, ConceptDefinition> conceptDefinitionMap = new HashMap<ConceptRef, ConceptDefinition>();
		
		AttributeClause findingSiteLungStructure = TestUtils.buildAttribute(SnomedConcepts.FINDING_SITE, 
				SnomedConcepts.LUNG_STRUCTURE);
		AttributeClause severitySevere = TestUtils.buildAttribute(SnomedConcepts.SEVERITY, 
				SnomedConcepts.SEVERE);
		AttributeClause findingSiteHeadStructure = TestUtils.buildAttribute(SnomedConcepts.FINDING_SITE, 
				SnomedConcepts.HEAD_STRUCTURE);

		// build test concept definitions
		ConceptRef concept2 = TestUtils.buildConcept("CONCEPT_2");
		ConceptDefinition conceptDefinition2 = TestUtils.buildConceptDefinition(severitySevere, findingSiteHeadStructure);
		conceptDefinitionMap.put(concept2, conceptDefinition2);
		ConceptRef concept1 = TestUtils.buildConcept("CONCEPT_1");
		ConceptDefinition conceptDefinition1 = TestUtils.buildConceptDefinition(findingSiteLungStructure);
		conceptDefinitionMap.put(concept1, conceptDefinition1);
		
		// build expected results
		ConceptDefinition expectedConceptDefinition = TestUtils.buildConceptDefinition(findingSiteLungStructure, severitySevere, 
				findingSiteHeadStructure);
		
		ConceptDefinition mergedConceptDefinition = new ConceptDefinition();
		merger.mergeUngroupedAttributes(conceptDefinitionMap, mergedConceptDefinition);
		
		TestUtils.assertConceptDefinitionsEqual(expectedConceptDefinition, mergedConceptDefinition);
	}
	
	@Test
	public void testGroupedAttributeNameMatch1() {
		/* 
		 * If the value of the ungrouped attribute subsumes value of the name-matched grouped attribute
		 *		omit the ungrouped attribute from the target definition
		 */
		
		UngroupedAttributesMerger merger = new UngroupedAttributesMerger(new SubsumptionTester(Branch.MAIN_PATH));
		Map<ConceptRef, ConceptDefinition> conceptDefinitionMap = new HashMap<ConceptRef, ConceptDefinition>();
		
		AttributeClause findingSiteLungStructure = TestUtils.buildAttribute(SnomedConcepts.FINDING_SITE, SnomedConcepts.LUNG_STRUCTURE);
		AttributeClause causativeAgentDustAllergen = TestUtils.buildAttribute(SnomedConcepts.CAUSATIVE_AGENT, SnomedConcepts.DUST_ALLERGEN);
		AttributeClauseList group = TestUtils.buildAttributeClauseList(Arrays.asList(findingSiteLungStructure, causativeAgentDustAllergen));
		
		AttributeClause severitySevere = TestUtils.buildAttribute(SnomedConcepts.SEVERITY, SnomedConcepts.SEVERE);
		AttributeClause causativeAgentSubstance = TestUtils.buildAttribute(SnomedConcepts.CAUSATIVE_AGENT, SnomedConcepts.SUBSTANCE);

		// build test concept definitions
		ConceptRef concept1 = TestUtils.buildConcept("CONCEPT_1");
		ConceptDefinition conceptDefinition1 = TestUtils.buildConceptDefinition(group);
		conceptDefinitionMap.put(concept1, conceptDefinition1);
		ConceptRef concept2 = TestUtils.buildConcept("CONCEPT_2");
		ConceptDefinition conceptDefinition2 = TestUtils.buildConceptDefinition(severitySevere, causativeAgentSubstance);
		conceptDefinitionMap.put(concept2, conceptDefinition2);
		
		// build expected results
		AttributeClauseList expectedAttributeClauseList = TestUtils.buildAttributeClauseList(Arrays.asList((AttributeClause)EcoreUtil.copy(findingSiteLungStructure), 
				(AttributeClause)EcoreUtil.copy(causativeAgentDustAllergen)));
		ConceptDefinition expectedConceptDefinition = TestUtils.buildConceptDefinition(Collections.singleton(expectedAttributeClauseList), 
				Arrays.asList(EcoreUtil.copy(severitySevere)));
		
		ConceptDefinition mergedConceptDefinition = TestUtils.buildConceptDefinition(group);
		merger.mergeUngroupedAttributes(conceptDefinitionMap, mergedConceptDefinition);
		
		TestUtils.assertConceptDefinitionsEqual(expectedConceptDefinition, mergedConceptDefinition);
	}
	
	@Test
	public void testGroupedAttributeNameMatch2() {
		/* 
		 * If the value of the grouped attribute is subsumed by the value of the name-matched grouped attribute
		 *		add the ungrouped attribute to the group containing the matching grouped attribute in the target definition
		 *		if this condition is met by multiple groups
		 *			add the ungrouped attribute to all groups that meet this condition
		 */
		
		UngroupedAttributesMerger merger = new UngroupedAttributesMerger(new SubsumptionTester(Branch.MAIN_PATH));
		Map<ConceptRef, ConceptDefinition> conceptDefinitionMap = new HashMap<ConceptRef, ConceptDefinition>();
		
		AttributeClause findingSiteLungStructure = TestUtils.buildAttribute(SnomedConcepts.FINDING_SITE, 
				SnomedConcepts.LUNG_STRUCTURE);
		AttributeClause causativeAgentSubstance = TestUtils.buildAttribute(SnomedConcepts.CAUSATIVE_AGENT, 
				SnomedConcepts.SUBSTANCE);
		AttributeClauseList group = TestUtils.buildAttributeClauseList(Arrays.asList(findingSiteLungStructure, causativeAgentSubstance));
		
		AttributeClause severitySevere = TestUtils.buildAttribute(SnomedConcepts.SEVERITY, 
				SnomedConcepts.SEVERE);
		AttributeClause causativeAgentDustAllergen = TestUtils.buildAttribute(SnomedConcepts.CAUSATIVE_AGENT, 
				SnomedConcepts.DUST_ALLERGEN);
		
		// build test concept definitions
		ConceptRef concept1 = TestUtils.buildConcept("CONCEPT_1");
		ConceptDefinition conceptDefinition1 = TestUtils.buildConceptDefinition(group);
		conceptDefinitionMap.put(concept1, conceptDefinition1);
		ConceptRef concept2 = TestUtils.buildConcept("CONCEPT_2");
		ConceptDefinition conceptDefinition2 = TestUtils.buildConceptDefinition(severitySevere, causativeAgentDustAllergen);
		conceptDefinitionMap.put(concept2, conceptDefinition2);
		
		// build expected results
		AttributeClauseList expectedAttributeClauseList = TestUtils.buildAttributeClauseList(Arrays.asList((AttributeClause)EcoreUtil.copy(findingSiteLungStructure), 
				(AttributeClause)EcoreUtil.copy(causativeAgentDustAllergen), (AttributeClause)EcoreUtil.copy(causativeAgentSubstance)));
		ConceptDefinition expectedConceptDefinition = TestUtils.buildConceptDefinition(Collections.singleton(expectedAttributeClauseList), 
				Collections.singleton(EcoreUtil.copy(severitySevere)));
		
		ConceptDefinition mergedConceptDefinition = TestUtils.buildConceptDefinition(group);
		merger.mergeUngroupedAttributes(conceptDefinitionMap, mergedConceptDefinition);
		
		TestUtils.assertConceptDefinitionsEqual(expectedConceptDefinition, mergedConceptDefinition);
	}
	
	@Test
	public void testGroupedAttributeNameMatch3() {
		/* 
		 * If the value of the name-matched grouped and ungrouped attributes are disjoint
		 * 		add the ungrouped attribute as an ungrouped attribute in the target expression.
		 */
		
		UngroupedAttributesMerger merger = new UngroupedAttributesMerger(new SubsumptionTester(Branch.MAIN_PATH));
		Map<ConceptRef, ConceptDefinition> conceptDefinitionMap = new HashMap<ConceptRef, ConceptDefinition>();
		
		AttributeClause findingSiteLungStructure = TestUtils.buildAttribute(SnomedConcepts.FINDING_SITE, 
				SnomedConcepts.LUNG_STRUCTURE);
		AttributeClause causativeAgentDustAllergen = TestUtils.buildAttribute(SnomedConcepts.CAUSATIVE_AGENT, 
				SnomedConcepts.DUST_ALLERGEN);
		AttributeClauseList group = TestUtils.buildAttributeClauseList(Arrays.asList(findingSiteLungStructure, causativeAgentDustAllergen));
		
		AttributeClause severitySevere = TestUtils.buildAttribute(SnomedConcepts.SEVERITY, SnomedConcepts.SEVERE);
		AttributeClause causativeAgentContactAllergen = TestUtils.buildAttribute(SnomedConcepts.CAUSATIVE_AGENT, 
				SnomedConcepts.CONTACT_ALLERGEN);
		
		// build test concept definitions
		ConceptRef concept1 = TestUtils.buildConcept("CONCEPT_1");
		ConceptDefinition conceptDefinition1 = TestUtils.buildConceptDefinition(group);
		conceptDefinitionMap.put(concept1, conceptDefinition1);
		
		ConceptRef concept2 = TestUtils.buildConcept("CONCEPT_2");
		ConceptDefinition conceptDefinition2 = TestUtils.buildConceptDefinition(severitySevere, causativeAgentContactAllergen);
		conceptDefinitionMap.put(concept2, conceptDefinition2);
		
		// build expected results
		AttributeClauseList expectedAttributeClauseList = TestUtils.buildAttributeClauseList(Arrays.asList(new AttributeClause[] { (AttributeClause)EcoreUtil.copy(findingSiteLungStructure), 
				(AttributeClause)EcoreUtil.copy(causativeAgentDustAllergen) }));
		ConceptDefinition expectedConceptDefinition = TestUtils.buildConceptDefinition(Collections.singleton(expectedAttributeClauseList), 
				Arrays.asList(EcoreUtil.copy(severitySevere), EcoreUtil.copy(causativeAgentContactAllergen)));
		
		ConceptDefinition mergedConceptDefinition = TestUtils.buildConceptDefinition(group);
		merger.mergeUngroupedAttributes(conceptDefinitionMap, mergedConceptDefinition);
		
		TestUtils.assertConceptDefinitionsEqual(expectedConceptDefinition, mergedConceptDefinition);
	}

	@Test
	public void testGroupedAttributeNameMatch1Reverse() {
		/* 
		 * If the value of the ungrouped attribute subsumes value of the name-matched grouped attribute
		 *		omit the ungrouped attribute from the target definition
		 */
		
		UngroupedAttributesMerger merger = new UngroupedAttributesMerger(new SubsumptionTester(Branch.MAIN_PATH));
		Map<ConceptRef, ConceptDefinition> conceptDefinitionMap = new HashMap<ConceptRef, ConceptDefinition>();
		
		AttributeClause findingSiteLungStructure = TestUtils.buildAttribute(SnomedConcepts.FINDING_SITE, SnomedConcepts.LUNG_STRUCTURE);
		AttributeClause causativeAgentDustAllergen = TestUtils.buildAttribute(SnomedConcepts.CAUSATIVE_AGENT, SnomedConcepts.DUST_ALLERGEN);
		AttributeClauseList group = TestUtils.buildAttributeClauseList(Arrays.asList(findingSiteLungStructure, causativeAgentDustAllergen));
		
		AttributeClause severitySevere = TestUtils.buildAttribute(SnomedConcepts.SEVERITY, SnomedConcepts.SEVERE);
		AttributeClause causativeAgentSubstance = TestUtils.buildAttribute(SnomedConcepts.CAUSATIVE_AGENT, SnomedConcepts.SUBSTANCE);

		// build test concept definitions
		ConceptRef concept2 = TestUtils.buildConcept("CONCEPT_2");
		ConceptDefinition conceptDefinition2 = TestUtils.buildConceptDefinition(severitySevere, causativeAgentSubstance);
		conceptDefinitionMap.put(concept2, conceptDefinition2);
		ConceptRef concept1 = TestUtils.buildConcept("CONCEPT_1");
		ConceptDefinition conceptDefinition1 = TestUtils.buildConceptDefinition(group);
		conceptDefinitionMap.put(concept1, conceptDefinition1);
		
		// build expected results
		AttributeClauseList expectedAttributeClauseList = TestUtils.buildAttributeClauseList(Arrays.asList((AttributeClause)EcoreUtil.copy(findingSiteLungStructure), 
				(AttributeClause)EcoreUtil.copy(causativeAgentDustAllergen)));
		ConceptDefinition expectedConceptDefinition = TestUtils.buildConceptDefinition(Collections.singleton(expectedAttributeClauseList), 
				Arrays.asList(EcoreUtil.copy(severitySevere)));
		
		ConceptDefinition mergedConceptDefinition = TestUtils.buildConceptDefinition(group);
		merger.mergeUngroupedAttributes(conceptDefinitionMap, mergedConceptDefinition);
		
		TestUtils.assertConceptDefinitionsEqual(expectedConceptDefinition, mergedConceptDefinition);
	}

	@Test
	public void testGroupedAttributeNameMatch2Reverse() {
		/* 
		 * If the value of the grouped attribute is subsumed by the value of the name-matched grouped attribute
		 *		add the ungrouped attribute to the group containing the matching grouped attribute in the target definition
		 *		if this condition is met by multiple groups
		 *			add the ungrouped attribute to all groups that meet this condition
		 */
		
		UngroupedAttributesMerger merger = new UngroupedAttributesMerger(new SubsumptionTester(Branch.MAIN_PATH));
		Map<ConceptRef, ConceptDefinition> conceptDefinitionMap = new HashMap<ConceptRef, ConceptDefinition>();
		
		AttributeClause findingSiteLungStructure = TestUtils.buildAttribute(SnomedConcepts.FINDING_SITE, 
				SnomedConcepts.LUNG_STRUCTURE);
		AttributeClause causativeAgentSubstance = TestUtils.buildAttribute(SnomedConcepts.CAUSATIVE_AGENT, 
				SnomedConcepts.SUBSTANCE);
		AttributeClauseList group = TestUtils.buildAttributeClauseList(Arrays.asList(findingSiteLungStructure, causativeAgentSubstance));
		
		AttributeClause severitySevere = TestUtils.buildAttribute(SnomedConcepts.SEVERITY, 
				SnomedConcepts.SEVERE);
		AttributeClause causativeAgentDustAllergen = TestUtils.buildAttribute(SnomedConcepts.CAUSATIVE_AGENT, 
				SnomedConcepts.DUST_ALLERGEN);
		
		// build test concept definitions
		ConceptRef concept2 = TestUtils.buildConcept("CONCEPT_2");
		ConceptDefinition conceptDefinition2 = TestUtils.buildConceptDefinition(severitySevere, causativeAgentDustAllergen);
		conceptDefinitionMap.put(concept2, conceptDefinition2);
		ConceptRef concept1 = TestUtils.buildConcept("CONCEPT_1");
		ConceptDefinition conceptDefinition1 = TestUtils.buildConceptDefinition(group);
		conceptDefinitionMap.put(concept1, conceptDefinition1);
		
		// build expected results
		AttributeClauseList expectedAttributeClauseList = TestUtils.buildAttributeClauseList(Arrays.asList((AttributeClause)EcoreUtil.copy(findingSiteLungStructure), 
				(AttributeClause)EcoreUtil.copy(causativeAgentDustAllergen), (AttributeClause)EcoreUtil.copy(causativeAgentSubstance)));
		ConceptDefinition expectedConceptDefinition = TestUtils.buildConceptDefinition(Collections.singleton(expectedAttributeClauseList), 
				Collections.singleton(EcoreUtil.copy(severitySevere)));
		
		ConceptDefinition mergedConceptDefinition = TestUtils.buildConceptDefinition(group);
		merger.mergeUngroupedAttributes(conceptDefinitionMap, mergedConceptDefinition);
		
		TestUtils.assertConceptDefinitionsEqual(expectedConceptDefinition, mergedConceptDefinition);
	}

	@Test
	public void testGroupedAttributeNameMatch3Reverse() {
		/* 
		 * If the value of the name-matched grouped and ungrouped attributes are disjoint
		 * 		add the ungrouped attribute as an ungrouped attribute in the target expression.
		 */
		
		UngroupedAttributesMerger merger = new UngroupedAttributesMerger(new SubsumptionTester(Branch.MAIN_PATH));
		Map<ConceptRef, ConceptDefinition> conceptDefinitionMap = new HashMap<ConceptRef, ConceptDefinition>();
		
		AttributeClause findingSiteLungStructure = TestUtils.buildAttribute(SnomedConcepts.FINDING_SITE, 
				SnomedConcepts.LUNG_STRUCTURE);
		AttributeClause causativeAgentDustAllergen = TestUtils.buildAttribute(SnomedConcepts.CAUSATIVE_AGENT, 
				SnomedConcepts.DUST_ALLERGEN);
		AttributeClauseList group = TestUtils.buildAttributeClauseList(Arrays.asList(findingSiteLungStructure, causativeAgentDustAllergen));
		
		AttributeClause severitySevere = TestUtils.buildAttribute(SnomedConcepts.SEVERITY, SnomedConcepts.SEVERE);
		AttributeClause causativeAgentContactAllergen = TestUtils.buildAttribute(SnomedConcepts.CAUSATIVE_AGENT, 
				SnomedConcepts.CONTACT_ALLERGEN);
		
		// build test concept definitions
		ConceptRef concept2 = TestUtils.buildConcept("CONCEPT_2");
		ConceptDefinition conceptDefinition2 = TestUtils.buildConceptDefinition(severitySevere, causativeAgentContactAllergen);
		conceptDefinitionMap.put(concept2, conceptDefinition2);
		ConceptRef concept1 = TestUtils.buildConcept("CONCEPT_1");
		ConceptDefinition conceptDefinition1 = TestUtils.buildConceptDefinition(group);
		conceptDefinitionMap.put(concept1, conceptDefinition1);
		
		// build expected results
		AttributeClauseList expectedAttributeClauseList = TestUtils.buildAttributeClauseList(Arrays.asList(new AttributeClause[] { (AttributeClause)EcoreUtil.copy(findingSiteLungStructure), 
				(AttributeClause)EcoreUtil.copy(causativeAgentDustAllergen) }));
		ConceptDefinition expectedConceptDefinition = TestUtils.buildConceptDefinition(Collections.singleton(expectedAttributeClauseList), 
				Arrays.asList(EcoreUtil.copy(severitySevere), EcoreUtil.copy(causativeAgentContactAllergen)));
		
		ConceptDefinition mergedConceptDefinition = TestUtils.buildConceptDefinition(group);
		merger.mergeUngroupedAttributes(conceptDefinitionMap, mergedConceptDefinition);
		
		TestUtils.assertConceptDefinitionsEqual(expectedConceptDefinition, mergedConceptDefinition);
	}
}
