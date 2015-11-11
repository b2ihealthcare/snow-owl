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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.junit.Before;
import org.junit.Test;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.dsl.scg.Attribute;
import com.b2international.snowowl.dsl.scg.Concept;
import com.b2international.snowowl.dsl.scg.Group;
import com.b2international.snowowl.semanticengine.normalform.ConceptDefinition;
import com.b2international.snowowl.semanticengine.normalform.GroupMerger;
import com.b2international.snowowl.semanticengine.subsumption.SubsumptionTester;
import com.b2international.snowowl.semanticengine.test.SnomedConcepts;
import com.b2international.snowowl.semanticengine.test.utils.TestUtils;
import com.b2international.snowowl.semanticengine.utils.ScgBuilderUtils;
import com.b2international.snowowl.snomed.datastore.RecursiveTerminologyBrowser;
import com.b2international.snowowl.snomed.datastore.SnomedClientTerminologyBrowser;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptIndexEntry;

public class GroupMergerTest {
	
	private GroupMerger attributeGroupMerger;

	@Before
	public void beforeTest() {
		SnomedClientTerminologyBrowser terminologyBrowser = ApplicationContext.getInstance().getService(SnomedClientTerminologyBrowser.class);
		RecursiveTerminologyBrowser<SnomedConceptIndexEntry, String> recursiveTerminologyBrowser = new RecursiveTerminologyBrowser<SnomedConceptIndexEntry, String>(terminologyBrowser);
		attributeGroupMerger = new GroupMerger(new SubsumptionTester(recursiveTerminologyBrowser));
	}
	
	@Test
	public void testIsMergeable1() {
		Attribute findingSiteLungStructure = ScgBuilderUtils.buildAttribute(SnomedConcepts.FINDING_SITE, SnomedConcepts.LUNG_STRUCTURE);
		Attribute causativeAgentDustAllergen = ScgBuilderUtils.buildAttribute(SnomedConcepts.CAUSATIVE_AGENT, SnomedConcepts.DUST_ALLERGEN);
		Group group1 = ScgBuilderUtils.buildGroup(Arrays.asList(new Attribute[] { findingSiteLungStructure, causativeAgentDustAllergen }));
		
		Attribute severitySevere = ScgBuilderUtils.buildAttribute(SnomedConcepts.SEVERITY, SnomedConcepts.SEVERE);
		Attribute associatedWithDustAllergen = ScgBuilderUtils.buildAttribute(SnomedConcepts.ASSOCIATED_WITH, SnomedConcepts.DUST_ALLERGEN);
		Group group2 = ScgBuilderUtils.buildGroup(Arrays.asList(new Attribute[] { severitySevere, associatedWithDustAllergen }));
		
		assertFalse(attributeGroupMerger.isMergeable(group1, group2));
		assertFalse(attributeGroupMerger.isMergeable(group2, group1));
	}
	
	@Test
	public void testIsMergeable2() {
		Attribute findingSiteLungStructure = ScgBuilderUtils.buildAttribute(SnomedConcepts.FINDING_SITE, SnomedConcepts.LUNG_STRUCTURE);
		Attribute causativeAgentDustAllergen = ScgBuilderUtils.buildAttribute(SnomedConcepts.CAUSATIVE_AGENT, SnomedConcepts.DUST_ALLERGEN);
		Group group1 = ScgBuilderUtils.buildGroup(Arrays.asList(new Attribute[] { findingSiteLungStructure, causativeAgentDustAllergen }));
		
		Attribute severitySevere = ScgBuilderUtils.buildAttribute(SnomedConcepts.SEVERITY, SnomedConcepts.SEVERE);
		Attribute causativeAgentDustAllergen2 = ScgBuilderUtils.buildAttribute(SnomedConcepts.CAUSATIVE_AGENT, SnomedConcepts.DUST_ALLERGEN);
		Group group2 = ScgBuilderUtils.buildGroup(Arrays.asList(new Attribute[] { severitySevere, causativeAgentDustAllergen2 }));
		
		assertTrue(attributeGroupMerger.isMergeable(group1, group2));
		assertTrue(attributeGroupMerger.isMergeable(group2, group1));
	}
	
	@Test
	public void testIsMergeable3() {
		Attribute findingSiteLungStructure = ScgBuilderUtils.buildAttribute(SnomedConcepts.FINDING_SITE, SnomedConcepts.LUNG_STRUCTURE);
		Attribute causativeAgentDustAllergen = ScgBuilderUtils.buildAttribute(SnomedConcepts.CAUSATIVE_AGENT, SnomedConcepts.DUST_ALLERGEN);
		Group group1 = ScgBuilderUtils.buildGroup(Arrays.asList(new Attribute[] { findingSiteLungStructure, causativeAgentDustAllergen }));
		
		Attribute severitySevere = ScgBuilderUtils.buildAttribute(SnomedConcepts.SEVERITY, SnomedConcepts.SEVERE);
		Attribute causativeAgentSubstance = ScgBuilderUtils.buildAttribute(SnomedConcepts.CAUSATIVE_AGENT, SnomedConcepts.SUBSTANCE);
		Group group2 = ScgBuilderUtils.buildGroup(Arrays.asList(new Attribute[] { causativeAgentSubstance, severitySevere }));
		
		assertTrue(attributeGroupMerger.isMergeable(group1, group2));
		assertTrue(attributeGroupMerger.isMergeable(group2, group1));
	}
	
	@Test
	public void testIsMergeable4() {
		Attribute findingSiteLungStructure = ScgBuilderUtils.buildAttribute(SnomedConcepts.FINDING_SITE, SnomedConcepts.LUNG_STRUCTURE);
		Group group1 = ScgBuilderUtils.buildGroup(Arrays.asList(new Attribute[] { findingSiteLungStructure }));
		
		Attribute severitySevere = ScgBuilderUtils.buildAttribute(SnomedConcepts.SEVERITY, SnomedConcepts.SEVERE);
		Attribute causativeAgentSubstance = ScgBuilderUtils.buildAttribute(SnomedConcepts.CAUSATIVE_AGENT, SnomedConcepts.SUBSTANCE);
		Group group2 = ScgBuilderUtils.buildGroup(Arrays.asList(new Attribute[] { causativeAgentSubstance, severitySevere }));
		
		assertFalse(attributeGroupMerger.isMergeable(group1, group2));
		assertFalse(attributeGroupMerger.isMergeable(group2, group1));
	}
	
	@Test
	public void testIsMergeable5() {
		Attribute causativeAgentDustAllergen = ScgBuilderUtils.buildAttribute(SnomedConcepts.CAUSATIVE_AGENT, SnomedConcepts.DUST_ALLERGEN);
		Group group1 = ScgBuilderUtils.buildGroup(Arrays.asList(new Attribute[] { causativeAgentDustAllergen }));
		
		Attribute severitySevere = ScgBuilderUtils.buildAttribute(SnomedConcepts.SEVERITY, SnomedConcepts.SEVERE);
		Attribute causativeAgentSubstance = ScgBuilderUtils.buildAttribute(SnomedConcepts.CAUSATIVE_AGENT, SnomedConcepts.SUBSTANCE);
		Group group2 = ScgBuilderUtils.buildGroup(Arrays.asList(new Attribute[] { causativeAgentSubstance, severitySevere }));
		
		assertTrue(attributeGroupMerger.isMergeable(group1, group2));
		assertTrue(attributeGroupMerger.isMergeable(group2, group1));
	}
	
	@Test
	public void testMergeSingleConceptDefinition() {
		Attribute findingSiteLungStructure = ScgBuilderUtils.buildAttribute(SnomedConcepts.FINDING_SITE, SnomedConcepts.LUNG_STRUCTURE);
		Attribute causativeAgentDustAllergen = ScgBuilderUtils.buildAttribute(SnomedConcepts.CAUSATIVE_AGENT, SnomedConcepts.DUST_ALLERGEN);
		Group group1 = ScgBuilderUtils.buildGroup(Arrays.asList(findingSiteLungStructure, causativeAgentDustAllergen));
		ConceptDefinition conceptDefinition1 = ScgBuilderUtils.buildConceptDefinition(group1);
		
		Map<Concept, ConceptDefinition> conceptDefinitionMap = new HashMap<Concept, ConceptDefinition>();
		Concept concept1 = ScgBuilderUtils.buildConcept("CONCEPT_1");
		conceptDefinitionMap.put(concept1, conceptDefinition1);
		
		Group expectedMergedGroup = ScgBuilderUtils.buildGroup(Arrays.asList(EcoreUtil.copy(findingSiteLungStructure), 
				EcoreUtil.copy(causativeAgentDustAllergen)));
		ConceptDefinition expectedConceptDefinition = ScgBuilderUtils.buildConceptDefinition(expectedMergedGroup);
		
		ConceptDefinition mergedConceptDefinition = new ConceptDefinition();
		attributeGroupMerger.mergeGroups(conceptDefinitionMap, mergedConceptDefinition);
		
		TestUtils.assertConceptDefinitionsEqual(expectedConceptDefinition, mergedConceptDefinition);
	}
	
	@Test
	public void testMergeGroups1() {
		Attribute findingSiteLungStructure = ScgBuilderUtils.buildAttribute(SnomedConcepts.FINDING_SITE, SnomedConcepts.LUNG_STRUCTURE);
		Attribute causativeAgentDustAllergen = ScgBuilderUtils.buildAttribute(SnomedConcepts.CAUSATIVE_AGENT, SnomedConcepts.DUST_ALLERGEN);
		Group group1 = ScgBuilderUtils.buildGroup(Arrays.asList(findingSiteLungStructure, causativeAgentDustAllergen));
		
		Attribute severitySevere = ScgBuilderUtils.buildAttribute(SnomedConcepts.SEVERITY, SnomedConcepts.SEVERE);
		Attribute causativeAgentSubstance = ScgBuilderUtils.buildAttribute(SnomedConcepts.CAUSATIVE_AGENT, SnomedConcepts.SUBSTANCE);
		Group group2 = ScgBuilderUtils.buildGroup(Arrays.asList(causativeAgentSubstance, severitySevere));
		
		ConceptDefinition conceptDefinition1 = ScgBuilderUtils.buildConceptDefinition(group1);
		ConceptDefinition conceptDefinition2 = ScgBuilderUtils.buildConceptDefinition(group2);
		
		Map<Concept, ConceptDefinition> conceptDefinitionMap = new HashMap<Concept, ConceptDefinition>();
		Concept concept1 = ScgBuilderUtils.buildConcept("CONCEPT_1");
		Concept concept2 = ScgBuilderUtils.buildConcept("CONCEPT_2");
		conceptDefinitionMap.put(concept1, conceptDefinition1);
		conceptDefinitionMap.put(concept2, conceptDefinition2);
		
		Group expectedMergedGroup = ScgBuilderUtils.buildGroup(Arrays.asList(EcoreUtil.copy(findingSiteLungStructure), 
				EcoreUtil.copy(causativeAgentDustAllergen), EcoreUtil.copy(causativeAgentSubstance), EcoreUtil.copy(severitySevere)));
		ConceptDefinition expectedConceptDefinition = ScgBuilderUtils.buildConceptDefinition(expectedMergedGroup);
		
		ConceptDefinition mergedConceptDefinition = new ConceptDefinition();
		attributeGroupMerger.mergeGroups(conceptDefinitionMap, mergedConceptDefinition);

		TestUtils.assertConceptDefinitionsEqual(expectedConceptDefinition, mergedConceptDefinition);
	}
	
	@Test
	public void testMergeGroups2() {
		Attribute findingSiteLungStructure = ScgBuilderUtils.buildAttribute(SnomedConcepts.FINDING_SITE, SnomedConcepts.LUNG_STRUCTURE);
		Group group1 = ScgBuilderUtils.buildGroup(Arrays.asList(findingSiteLungStructure));
		
		Attribute severitySevere = ScgBuilderUtils.buildAttribute(SnomedConcepts.SEVERITY, SnomedConcepts.SEVERE);
		Attribute causativeAgentSubstance = ScgBuilderUtils.buildAttribute(SnomedConcepts.CAUSATIVE_AGENT, SnomedConcepts.SUBSTANCE);
		Group group2 = ScgBuilderUtils.buildGroup(Arrays.asList(causativeAgentSubstance, severitySevere));
		
		ConceptDefinition conceptDefinition1 = ScgBuilderUtils.buildConceptDefinition(group1);
		ConceptDefinition conceptDefinition2 = ScgBuilderUtils.buildConceptDefinition(group2);
		
		Map<Concept, ConceptDefinition> conceptDefinitionMap = new HashMap<Concept, ConceptDefinition>();
		Concept concept1 = ScgBuilderUtils.buildConcept("CONCEPT_1");
		Concept concept2 = ScgBuilderUtils.buildConcept("CONCEPT_2");
		conceptDefinitionMap.put(concept1, conceptDefinition1);
		conceptDefinitionMap.put(concept2, conceptDefinition2);
		
		Group expectedMergedGroup1 = ScgBuilderUtils.buildGroup(Arrays.asList(EcoreUtil.copy(findingSiteLungStructure)));
		Group expectedMergedGroup2 = ScgBuilderUtils.buildGroup(Arrays.asList(EcoreUtil.copy(causativeAgentSubstance), 
				EcoreUtil.copy(severitySevere)));
		ConceptDefinition expectedConceptDefinition = ScgBuilderUtils.buildConceptDefinition(Arrays.asList(expectedMergedGroup1, expectedMergedGroup2), 
				Collections.<Attribute>emptySet());
		
		ConceptDefinition mergedConceptDefinition = new ConceptDefinition();
		attributeGroupMerger.mergeGroups(conceptDefinitionMap, mergedConceptDefinition);
		
		TestUtils.assertConceptDefinitionsEqual(expectedConceptDefinition, mergedConceptDefinition);
	}
	
	@Test
	public void testMergeGroups3() {
		Attribute findingSiteLungStructure = ScgBuilderUtils.buildAttribute(SnomedConcepts.FINDING_SITE, SnomedConcepts.LUNG_STRUCTURE);
		Group group1 = ScgBuilderUtils.buildGroup(Arrays.asList(new Attribute[] { findingSiteLungStructure }));
		
		Attribute severitySevere = ScgBuilderUtils.buildAttribute(SnomedConcepts.SEVERITY, SnomedConcepts.SEVERE);
		Attribute causativeAgentSubstance = ScgBuilderUtils.buildAttribute(SnomedConcepts.CAUSATIVE_AGENT, SnomedConcepts.SUBSTANCE);
		Group group2 = ScgBuilderUtils.buildGroup(Arrays.asList(new Attribute[] { causativeAgentSubstance, severitySevere }));
		
		ConceptDefinition conceptDefinition1 = ScgBuilderUtils.buildConceptDefinition(group1);
		ConceptDefinition conceptDefinition2 = ScgBuilderUtils.buildConceptDefinition(group2);
		
		Map<Concept, ConceptDefinition> conceptDefinitionMap = new HashMap<Concept, ConceptDefinition>();
		Concept concept1 = ScgBuilderUtils.buildConcept("CONCEPT_1");
		Concept concept2 = ScgBuilderUtils.buildConcept("CONCEPT_2");
		conceptDefinitionMap.put(concept1, conceptDefinition1);
		conceptDefinitionMap.put(concept2, conceptDefinition2);
		
		Group expectedMergedGroup1 = ScgBuilderUtils.buildGroup(Arrays.asList(new Attribute[]{ EcoreUtil.copy(findingSiteLungStructure) }));
		Group expectedMergedGroup2 = ScgBuilderUtils.buildGroup(Arrays.asList(new Attribute[]{ EcoreUtil.copy(causativeAgentSubstance), 
				EcoreUtil.copy(severitySevere) }));
		ConceptDefinition expectedConceptDefinition = ScgBuilderUtils.buildConceptDefinition(Arrays.asList(expectedMergedGroup1, expectedMergedGroup2), 
				Collections.<Attribute>emptySet());
		
		ConceptDefinition mergedConceptDefinition = new ConceptDefinition();
		attributeGroupMerger.mergeGroups(conceptDefinitionMap, mergedConceptDefinition);
		
		TestUtils.assertConceptDefinitionsEqual(expectedConceptDefinition, mergedConceptDefinition);
	}
}
