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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.junit.Before;
import org.junit.Test;

import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.semanticengine.simpleast.normalform.AttributeClauseList;
import com.b2international.snowowl.semanticengine.simpleast.normalform.AttributeClauseListMerger;
import com.b2international.snowowl.semanticengine.simpleast.normalform.ConceptDefinition;
import com.b2international.snowowl.semanticengine.simpleast.subsumption.SubsumptionTester;
import com.b2international.snowowl.semanticengine.simpleast.test.SnomedConcepts;
import com.b2international.snowowl.semanticengine.simpleast.test.utils.TestUtils;
import com.b2international.snowowl.snomed.dsl.query.queryast.AttributeClause;
import com.b2international.snowowl.snomed.dsl.query.queryast.ConceptRef;

public class AttributeGroupMergerTest {
	
	private AttributeClauseListMerger attributeGroupMerger;

	@Before
	public void beforeTest() {
		attributeGroupMerger = new AttributeClauseListMerger(new SubsumptionTester(Branch.MAIN_PATH));
	}
	
	@Test
	public void testIsMergeable1() {
		AttributeClause findingSiteLungStructure = TestUtils.buildAttribute(SnomedConcepts.FINDING_SITE, SnomedConcepts.LUNG_STRUCTURE);
		AttributeClause causativeAgentDustAllergen = TestUtils.buildAttribute(SnomedConcepts.CAUSATIVE_AGENT, SnomedConcepts.DUST_ALLERGEN);
		AttributeClauseList group1 = TestUtils.buildAttributeClauseList(Arrays.asList(new AttributeClause[] { findingSiteLungStructure, causativeAgentDustAllergen }));
		
		AttributeClause severitySevere = TestUtils.buildAttribute(SnomedConcepts.SEVERITY, SnomedConcepts.SEVERE);
		AttributeClause associatedWithDustAllergen = TestUtils.buildAttribute(SnomedConcepts.ASSOCIATED_WITH, SnomedConcepts.DUST_ALLERGEN);
		AttributeClauseList group2 = TestUtils.buildAttributeClauseList(Arrays.asList(new AttributeClause[] { severitySevere, associatedWithDustAllergen }));
		
		assertFalse(attributeGroupMerger.isMergeable(group1, group2));
		assertFalse(attributeGroupMerger.isMergeable(group2, group1));
	}
	
	@Test
	public void testIsMergeable2() {
		AttributeClause findingSiteLungStructure = TestUtils.buildAttribute(SnomedConcepts.FINDING_SITE, SnomedConcepts.LUNG_STRUCTURE);
		AttributeClause causativeAgentDustAllergen = TestUtils.buildAttribute(SnomedConcepts.CAUSATIVE_AGENT, SnomedConcepts.DUST_ALLERGEN);
		AttributeClauseList group1 = TestUtils.buildAttributeClauseList(Arrays.asList(new AttributeClause[] { findingSiteLungStructure, causativeAgentDustAllergen }));
		
		AttributeClause severitySevere = TestUtils.buildAttribute(SnomedConcepts.SEVERITY, SnomedConcepts.SEVERE);
		AttributeClause causativeAgentDustAllergen2 = TestUtils.buildAttribute(SnomedConcepts.CAUSATIVE_AGENT, SnomedConcepts.DUST_ALLERGEN);
		AttributeClauseList group2 = TestUtils.buildAttributeClauseList(Arrays.asList(new AttributeClause[] { severitySevere, causativeAgentDustAllergen2 }));
		
		assertTrue(attributeGroupMerger.isMergeable(group1, group2));
		assertTrue(attributeGroupMerger.isMergeable(group2, group1));
	}
	
	@Test
	public void testIsMergeable3() {
		AttributeClause findingSiteLungStructure = TestUtils.buildAttribute(SnomedConcepts.FINDING_SITE, SnomedConcepts.LUNG_STRUCTURE);
		AttributeClause causativeAgentDustAllergen = TestUtils.buildAttribute(SnomedConcepts.CAUSATIVE_AGENT, SnomedConcepts.DUST_ALLERGEN);
		AttributeClauseList group1 = TestUtils.buildAttributeClauseList(Arrays.asList(new AttributeClause[] { findingSiteLungStructure, causativeAgentDustAllergen }));
		
		AttributeClause severitySevere = TestUtils.buildAttribute(SnomedConcepts.SEVERITY, SnomedConcepts.SEVERE);
		AttributeClause causativeAgentSubstance = TestUtils.buildAttribute(SnomedConcepts.CAUSATIVE_AGENT, SnomedConcepts.SUBSTANCE);
		AttributeClauseList group2 = TestUtils.buildAttributeClauseList(Arrays.asList(new AttributeClause[] { causativeAgentSubstance, severitySevere }));
		
		assertTrue(attributeGroupMerger.isMergeable(group1, group2));
		assertTrue(attributeGroupMerger.isMergeable(group2, group1));
	}
	
	@Test
	public void testIsMergeable4() {
		AttributeClause findingSiteLungStructure = TestUtils.buildAttribute(SnomedConcepts.FINDING_SITE, SnomedConcepts.LUNG_STRUCTURE);
		AttributeClauseList group1 = TestUtils.buildAttributeClauseList(Arrays.asList(new AttributeClause[] { findingSiteLungStructure }));
		
		AttributeClause severitySevere = TestUtils.buildAttribute(SnomedConcepts.SEVERITY, SnomedConcepts.SEVERE);
		AttributeClause causativeAgentSubstance = TestUtils.buildAttribute(SnomedConcepts.CAUSATIVE_AGENT, SnomedConcepts.SUBSTANCE);
		AttributeClauseList group2 = TestUtils.buildAttributeClauseList(Arrays.asList(new AttributeClause[] { causativeAgentSubstance, severitySevere }));
		
		assertFalse(attributeGroupMerger.isMergeable(group1, group2));
		assertFalse(attributeGroupMerger.isMergeable(group2, group1));
	}
	
	@Test
	public void testIsMergeable5() {
		AttributeClause causativeAgentDustAllergen = TestUtils.buildAttribute(SnomedConcepts.CAUSATIVE_AGENT, SnomedConcepts.DUST_ALLERGEN);
		AttributeClauseList group1 = TestUtils.buildAttributeClauseList(Arrays.asList(new AttributeClause[] { causativeAgentDustAllergen }));
		
		AttributeClause severitySevere = TestUtils.buildAttribute(SnomedConcepts.SEVERITY, SnomedConcepts.SEVERE);
		AttributeClause causativeAgentSubstance = TestUtils.buildAttribute(SnomedConcepts.CAUSATIVE_AGENT, SnomedConcepts.SUBSTANCE);
		AttributeClauseList group2 = TestUtils.buildAttributeClauseList(Arrays.asList(new AttributeClause[] { causativeAgentSubstance, severitySevere }));
		
		assertTrue(attributeGroupMerger.isMergeable(group1, group2));
		assertTrue(attributeGroupMerger.isMergeable(group2, group1));
	}
	
	@Test
	public void testMergeSingleConceptDefinition() {
		AttributeClause findingSiteLungStructure = TestUtils.buildAttribute(SnomedConcepts.FINDING_SITE, SnomedConcepts.LUNG_STRUCTURE);
		AttributeClause causativeAgentDustAllergen = TestUtils.buildAttribute(SnomedConcepts.CAUSATIVE_AGENT, SnomedConcepts.DUST_ALLERGEN);
		AttributeClauseList group1 = TestUtils.buildAttributeClauseList(Arrays.asList(findingSiteLungStructure, causativeAgentDustAllergen));
		ConceptDefinition conceptDefinition1 = TestUtils.buildConceptDefinition(group1);
		
		Map<ConceptRef, ConceptDefinition> conceptDefinitionMap = new HashMap<ConceptRef, ConceptDefinition>();
		ConceptRef concept1 = TestUtils.buildConcept("CONCEPT_1");
		conceptDefinitionMap.put(concept1, conceptDefinition1);
		
		AttributeClauseList expectedMergedAttributeClauseList = TestUtils.buildAttributeClauseList(Arrays.asList(EcoreUtil.copy(findingSiteLungStructure), 
				EcoreUtil.copy(causativeAgentDustAllergen)));
		ConceptDefinition expectedConceptDefinition = TestUtils.buildConceptDefinition(expectedMergedAttributeClauseList);
		
		ConceptDefinition mergedConceptDefinition = new ConceptDefinition();
		attributeGroupMerger.mergeAttributeClauseGroups(conceptDefinitionMap, mergedConceptDefinition);
		
		TestUtils.assertConceptDefinitionsEqual(expectedConceptDefinition, mergedConceptDefinition);
	}
	
	@Test
	public void testMergeAttributeGroups1() {
		AttributeClause findingSiteLungStructure = TestUtils.buildAttribute(SnomedConcepts.FINDING_SITE, SnomedConcepts.LUNG_STRUCTURE);
		AttributeClause causativeAgentDustAllergen = TestUtils.buildAttribute(SnomedConcepts.CAUSATIVE_AGENT, SnomedConcepts.DUST_ALLERGEN);
		AttributeClauseList group1 = TestUtils.buildAttributeClauseList(Arrays.asList(findingSiteLungStructure, causativeAgentDustAllergen));
		
		AttributeClause severitySevere = TestUtils.buildAttribute(SnomedConcepts.SEVERITY, SnomedConcepts.SEVERE);
		AttributeClause causativeAgentSubstance = TestUtils.buildAttribute(SnomedConcepts.CAUSATIVE_AGENT, SnomedConcepts.SUBSTANCE);
		AttributeClauseList group2 = TestUtils.buildAttributeClauseList(Arrays.asList(causativeAgentSubstance, severitySevere));
		
		ConceptDefinition conceptDefinition1 = TestUtils.buildConceptDefinition(group1);
		ConceptDefinition conceptDefinition2 = TestUtils.buildConceptDefinition(group2);
		
		Map<ConceptRef, ConceptDefinition> conceptDefinitionMap = new HashMap<ConceptRef, ConceptDefinition>();
		ConceptRef concept1 = TestUtils.buildConcept("CONCEPT_1");
		ConceptRef concept2 = TestUtils.buildConcept("CONCEPT_2");
		conceptDefinitionMap.put(concept1, conceptDefinition1);
		conceptDefinitionMap.put(concept2, conceptDefinition2);
		
		AttributeClauseList expectedMergedAttributeClauseList = TestUtils.buildAttributeClauseList(Arrays.asList(EcoreUtil.copy(findingSiteLungStructure), 
				EcoreUtil.copy(causativeAgentDustAllergen), EcoreUtil.copy(causativeAgentSubstance), EcoreUtil.copy(severitySevere)));
		ConceptDefinition expectedConceptDefinition = TestUtils.buildConceptDefinition(expectedMergedAttributeClauseList);
		
		ConceptDefinition mergedConceptDefinition = new ConceptDefinition();
		attributeGroupMerger.mergeAttributeClauseGroups(conceptDefinitionMap, mergedConceptDefinition);

		TestUtils.assertConceptDefinitionsEqual(expectedConceptDefinition, mergedConceptDefinition);
	}
	
	@Test
	public void testMergeAttributeGroups2() {
		AttributeClause findingSiteLungStructure = TestUtils.buildAttribute(SnomedConcepts.FINDING_SITE, SnomedConcepts.LUNG_STRUCTURE);
		AttributeClauseList group1 = TestUtils.buildAttributeClauseList(Arrays.asList(findingSiteLungStructure));
		
		AttributeClause severitySevere = TestUtils.buildAttribute(SnomedConcepts.SEVERITY, SnomedConcepts.SEVERE);
		AttributeClause causativeAgentSubstance = TestUtils.buildAttribute(SnomedConcepts.CAUSATIVE_AGENT, SnomedConcepts.SUBSTANCE);
		AttributeClauseList group2 = TestUtils.buildAttributeClauseList(Arrays.asList(causativeAgentSubstance, severitySevere));
		
		ConceptDefinition conceptDefinition1 = TestUtils.buildConceptDefinition(group1);
		ConceptDefinition conceptDefinition2 = TestUtils.buildConceptDefinition(group2);
		
		Map<ConceptRef, ConceptDefinition> conceptDefinitionMap = new HashMap<ConceptRef, ConceptDefinition>();
		ConceptRef concept1 = TestUtils.buildConcept("CONCEPT_1");
		ConceptRef concept2 = TestUtils.buildConcept("CONCEPT_2");
		conceptDefinitionMap.put(concept1, conceptDefinition1);
		conceptDefinitionMap.put(concept2, conceptDefinition2);
		
		AttributeClauseList expectedMergedAttributeGroup1 = TestUtils.buildAttributeClauseList(Arrays.asList(EcoreUtil.copy(findingSiteLungStructure)));
		AttributeClauseList expectedMergedAttributeGroup2 = TestUtils.buildAttributeClauseList(Arrays.asList(EcoreUtil.copy(causativeAgentSubstance), 
				EcoreUtil.copy(severitySevere)));
		ConceptDefinition expectedConceptDefinition = TestUtils.buildConceptDefinition(Arrays.asList(expectedMergedAttributeGroup1, expectedMergedAttributeGroup2), 
				Collections.<AttributeClause>emptySet());
		
		ConceptDefinition mergedConceptDefinition = new ConceptDefinition();
		attributeGroupMerger.mergeAttributeClauseGroups(conceptDefinitionMap, mergedConceptDefinition);
		
		TestUtils.assertConceptDefinitionsEqual(expectedConceptDefinition, mergedConceptDefinition);
	}
	
	@Test
	public void testMergeAttributeGroups3() {
		AttributeClause findingSiteLungStructure = TestUtils.buildAttribute(SnomedConcepts.FINDING_SITE, SnomedConcepts.LUNG_STRUCTURE);
		AttributeClauseList group1 = TestUtils.buildAttributeClauseList(Arrays.asList(new AttributeClause[] { findingSiteLungStructure }));
		
		AttributeClause severitySevere = TestUtils.buildAttribute(SnomedConcepts.SEVERITY, SnomedConcepts.SEVERE);
		AttributeClause causativeAgentSubstance = TestUtils.buildAttribute(SnomedConcepts.CAUSATIVE_AGENT, SnomedConcepts.SUBSTANCE);
		AttributeClauseList group2 = TestUtils.buildAttributeClauseList(Arrays.asList(new AttributeClause[] { causativeAgentSubstance, severitySevere }));
		
		ConceptDefinition conceptDefinition1 = TestUtils.buildConceptDefinition(group1);
		ConceptDefinition conceptDefinition2 = TestUtils.buildConceptDefinition(group2);
		
		Map<ConceptRef, ConceptDefinition> conceptDefinitionMap = new HashMap<ConceptRef, ConceptDefinition>();
		ConceptRef concept1 = TestUtils.buildConcept("CONCEPT_1");
		ConceptRef concept2 = TestUtils.buildConcept("CONCEPT_2");
		conceptDefinitionMap.put(concept1, conceptDefinition1);
		conceptDefinitionMap.put(concept2, conceptDefinition2);
		
		AttributeClauseList expectedMergedAttributeGroup1 = TestUtils.buildAttributeClauseList(Arrays.asList(new AttributeClause[]{ EcoreUtil.copy(findingSiteLungStructure) }));
		AttributeClauseList expectedMergedAttributeGroup2 = TestUtils.buildAttributeClauseList(Arrays.asList(new AttributeClause[]{ EcoreUtil.copy(causativeAgentSubstance), 
				EcoreUtil.copy(severitySevere) }));
		ConceptDefinition expectedConceptDefinition = TestUtils.buildConceptDefinition(Arrays.asList(expectedMergedAttributeGroup1, expectedMergedAttributeGroup2), 
				Collections.<AttributeClause>emptySet());
		
		ConceptDefinition mergedConceptDefinition = new ConceptDefinition();
		attributeGroupMerger.mergeAttributeClauseGroups(conceptDefinitionMap, mergedConceptDefinition);
		
		TestUtils.assertConceptDefinitionsEqual(expectedConceptDefinition, mergedConceptDefinition);
	}
}
