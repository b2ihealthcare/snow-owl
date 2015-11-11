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
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.junit.Test;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.dsl.scg.Attribute;
import com.b2international.snowowl.dsl.scg.Concept;
import com.b2international.snowowl.dsl.scg.Group;
import com.b2international.snowowl.semanticengine.normalform.ConceptDefinition;
import com.b2international.snowowl.semanticengine.normalform.UngroupedAttributesMerger;
import com.b2international.snowowl.semanticengine.subsumption.SubsumptionTester;
import com.b2international.snowowl.semanticengine.test.SnomedConcepts;
import com.b2international.snowowl.semanticengine.test.utils.TestUtils;
import com.b2international.snowowl.semanticengine.utils.ScgBuilderUtils;
import com.b2international.snowowl.snomed.datastore.RecursiveTerminologyBrowser;
import com.b2international.snowowl.snomed.datastore.SnomedClientTerminologyBrowser;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptIndexEntry;

public class UngroupedAttributesMergerTest {
	
	@Test
	public void testMergeSingleConceptDefinition() {
		SnomedClientTerminologyBrowser terminologyBrowser = ApplicationContext.getInstance().getService(SnomedClientTerminologyBrowser.class);
		RecursiveTerminologyBrowser<SnomedConceptIndexEntry,String> recursiveTerminologyBrowser = RecursiveTerminologyBrowser.create(terminologyBrowser);
		UngroupedAttributesMerger merger = new UngroupedAttributesMerger(new SubsumptionTester(recursiveTerminologyBrowser));
		
		Attribute findingSiteLungStructure = ScgBuilderUtils.buildAttribute(SnomedConcepts.FINDING_SITE, SnomedConcepts.LUNG_STRUCTURE);
		Attribute causativeAgentDustAllergen = ScgBuilderUtils.buildAttribute(SnomedConcepts.CAUSATIVE_AGENT, SnomedConcepts.DUST_ALLERGEN);
		ConceptDefinition conceptDefinition1 = ScgBuilderUtils.buildConceptDefinition(findingSiteLungStructure, causativeAgentDustAllergen);
		
		Map<Concept, ConceptDefinition> conceptDefinitionMap = new HashMap<Concept, ConceptDefinition>();
		Concept concept1 = ScgBuilderUtils.buildConcept("CONCEPT_1");
		conceptDefinitionMap.put(concept1, conceptDefinition1);
		
		ConceptDefinition expectedConceptDefinition = ScgBuilderUtils.buildConceptDefinition(EcoreUtil.copy(findingSiteLungStructure), 
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
		SnomedClientTerminologyBrowser terminologyBrowser = ApplicationContext.getInstance().getService(SnomedClientTerminologyBrowser.class);
		UngroupedAttributesMerger merger = new UngroupedAttributesMerger(new SubsumptionTester(terminologyBrowser));
		Map<Concept, ConceptDefinition> conceptDefinitionMap = new HashMap<Concept, ConceptDefinition>();
		
		Attribute findingSiteLungStructure = ScgBuilderUtils.buildAttribute(SnomedConcepts.FINDING_SITE, SnomedConcepts.LUNG_STRUCTURE);
		Attribute causativeAgentDustAllergen = ScgBuilderUtils.buildAttribute(SnomedConcepts.CAUSATIVE_AGENT, SnomedConcepts.DUST_ALLERGEN);
		Attribute severitySevere = ScgBuilderUtils.buildAttribute(SnomedConcepts.SEVERITY, SnomedConcepts.SEVERE);
		Attribute causativeAgentSubstance = ScgBuilderUtils.buildAttribute(SnomedConcepts.CAUSATIVE_AGENT, SnomedConcepts.SUBSTANCE);

		// build test concept definitions
		Concept concept1 = ScgBuilderUtils.buildConcept("CONCEPT_1");
		ConceptDefinition conceptDefinition1 = ScgBuilderUtils.buildConceptDefinition(findingSiteLungStructure, causativeAgentDustAllergen);
		conceptDefinitionMap.put(concept1, conceptDefinition1);
		Concept concept2 = ScgBuilderUtils.buildConcept("CONCEPT_2");
		ConceptDefinition conceptDefinition2 = ScgBuilderUtils.buildConceptDefinition(severitySevere, causativeAgentSubstance);
		conceptDefinitionMap.put(concept2, conceptDefinition2);
		
		// build expected results
		ConceptDefinition expectedConceptDefinition = ScgBuilderUtils.buildConceptDefinition(findingSiteLungStructure, 
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
		SnomedClientTerminologyBrowser terminologyBrowser = ApplicationContext.getInstance().getService(SnomedClientTerminologyBrowser.class);
		UngroupedAttributesMerger merger = new UngroupedAttributesMerger(new SubsumptionTester(terminologyBrowser));
		Map<Concept, ConceptDefinition> conceptDefinitionMap = new HashMap<Concept, ConceptDefinition>();
		
		Attribute findingSiteLungStructure = ScgBuilderUtils.buildAttribute(SnomedConcepts.FINDING_SITE, SnomedConcepts.LUNG_STRUCTURE);
		Attribute causativeAgentDustAllergen = ScgBuilderUtils.buildAttribute(SnomedConcepts.CAUSATIVE_AGENT, SnomedConcepts.DUST_ALLERGEN);
		Attribute severitySevere = ScgBuilderUtils.buildAttribute(SnomedConcepts.SEVERITY, SnomedConcepts.SEVERE);
		Attribute causativeAgentSubstance = ScgBuilderUtils.buildAttribute(SnomedConcepts.CAUSATIVE_AGENT, SnomedConcepts.SUBSTANCE);

		// build test concept definitions
		Concept concept2 = ScgBuilderUtils.buildConcept("CONCEPT_2");
		ConceptDefinition conceptDefinition2 = ScgBuilderUtils.buildConceptDefinition(severitySevere, causativeAgentSubstance);
		conceptDefinitionMap.put(concept2, conceptDefinition2);
		Concept concept1 = ScgBuilderUtils.buildConcept("CONCEPT_1");
		ConceptDefinition conceptDefinition1 = ScgBuilderUtils.buildConceptDefinition(findingSiteLungStructure, causativeAgentDustAllergen);
		conceptDefinitionMap.put(concept1, conceptDefinition1);
		
		// build expected results
		ConceptDefinition expectedConceptDefinition = ScgBuilderUtils.buildConceptDefinition(findingSiteLungStructure, 
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
		SnomedClientTerminologyBrowser terminologyBrowser = ApplicationContext.getInstance().getService(SnomedClientTerminologyBrowser.class);
		UngroupedAttributesMerger merger = new UngroupedAttributesMerger(new SubsumptionTester(terminologyBrowser));
		Map<Concept, ConceptDefinition> conceptDefinitionMap = new HashMap<Concept, ConceptDefinition>();
		
		Attribute findingSiteLungStructure = ScgBuilderUtils.buildAttribute(SnomedConcepts.FINDING_SITE, SnomedConcepts.LUNG_STRUCTURE);
		Attribute causativeAgentDustAllergen = ScgBuilderUtils.buildAttribute(SnomedConcepts.CAUSATIVE_AGENT, SnomedConcepts.DUST_ALLERGEN);
		Attribute severitySevere = ScgBuilderUtils.buildAttribute(SnomedConcepts.SEVERITY, SnomedConcepts.SEVERE);
		Attribute causativeAgentDustAllergen2 = ScgBuilderUtils.buildAttribute(SnomedConcepts.CAUSATIVE_AGENT, SnomedConcepts.DUST_ALLERGEN);
		
		// build test concept definitions
		Concept concept1 = ScgBuilderUtils.buildConcept("CONCEPT_1");
		ConceptDefinition conceptDefinition1 = ScgBuilderUtils.buildConceptDefinition(findingSiteLungStructure, causativeAgentDustAllergen);
		conceptDefinitionMap.put(concept1, conceptDefinition1);
		Concept concept2 = ScgBuilderUtils.buildConcept("CONCEPT_2");
		ConceptDefinition conceptDefinition2 = ScgBuilderUtils.buildConceptDefinition(severitySevere, causativeAgentDustAllergen2);
		conceptDefinitionMap.put(concept2, conceptDefinition2);
		
		// build expected results
		ConceptDefinition expectedConceptDefinition = ScgBuilderUtils.buildConceptDefinition(findingSiteLungStructure, severitySevere, 
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
		SnomedClientTerminologyBrowser terminologyBrowser = ApplicationContext.getInstance().getService(SnomedClientTerminologyBrowser.class);
		UngroupedAttributesMerger merger = new UngroupedAttributesMerger(new SubsumptionTester(terminologyBrowser));
		Map<Concept, ConceptDefinition> conceptDefinitionMap = new HashMap<Concept, ConceptDefinition>();
		
		Attribute findingSiteLungStructure = ScgBuilderUtils.buildAttribute(SnomedConcepts.FINDING_SITE, SnomedConcepts.LUNG_STRUCTURE);
		Attribute causativeAgentDustAllergen = ScgBuilderUtils.buildAttribute(SnomedConcepts.CAUSATIVE_AGENT, SnomedConcepts.DUST_ALLERGEN);
		
		Attribute severitySevere = ScgBuilderUtils.buildAttribute(SnomedConcepts.SEVERITY, SnomedConcepts.SEVERE);
		Attribute causativeAgentDustAllergen2 = ScgBuilderUtils.buildAttribute(SnomedConcepts.CAUSATIVE_AGENT, SnomedConcepts.DUST_ALLERGEN);
		
		// build test concept definitions
		Concept concept2 = ScgBuilderUtils.buildConcept("CONCEPT_2");
		ConceptDefinition conceptDefinition2 = ScgBuilderUtils.buildConceptDefinition(severitySevere, causativeAgentDustAllergen2);
		conceptDefinitionMap.put(concept2, conceptDefinition2);
		Concept concept1 = ScgBuilderUtils.buildConcept("CONCEPT_1");
		ConceptDefinition conceptDefinition1 = ScgBuilderUtils.buildConceptDefinition(findingSiteLungStructure, causativeAgentDustAllergen);
		conceptDefinitionMap.put(concept1, conceptDefinition1);
		
		// build expected results
		ConceptDefinition expectedConceptDefinition = ScgBuilderUtils.buildConceptDefinition(findingSiteLungStructure, severitySevere, 
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
		SnomedClientTerminologyBrowser terminologyBrowser = ApplicationContext.getInstance().getService(SnomedClientTerminologyBrowser.class);
		UngroupedAttributesMerger merger = new UngroupedAttributesMerger(new SubsumptionTester(terminologyBrowser));
		Map<Concept, ConceptDefinition> conceptDefinitionMap = new HashMap<Concept, ConceptDefinition>();
		
		Attribute findingSiteLungStructure = ScgBuilderUtils.buildAttribute(SnomedConcepts.FINDING_SITE, 
				SnomedConcepts.LUNG_STRUCTURE);
		Attribute severitySevere = ScgBuilderUtils.buildAttribute(SnomedConcepts.SEVERITY, 
				SnomedConcepts.SEVERE);
		Attribute findingSiteHeadStructure = ScgBuilderUtils.buildAttribute(SnomedConcepts.FINDING_SITE, 
				SnomedConcepts.HEAD_STRUCTURE);

		// build test concept definitions
		Concept concept1 = ScgBuilderUtils.buildConcept("CONCEPT_1");
		ConceptDefinition conceptDefinition1 = ScgBuilderUtils.buildConceptDefinition(findingSiteLungStructure);
		conceptDefinitionMap.put(concept1, conceptDefinition1);
		Concept concept2 = ScgBuilderUtils.buildConcept("CONCEPT_2");
		ConceptDefinition conceptDefinition2 = ScgBuilderUtils.buildConceptDefinition(severitySevere, findingSiteHeadStructure);
		conceptDefinitionMap.put(concept2, conceptDefinition2);
		
		// build expected results
		ConceptDefinition expectedConceptDefinition = ScgBuilderUtils.buildConceptDefinition(findingSiteLungStructure, severitySevere, 
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
		SnomedClientTerminologyBrowser terminologyBrowser = ApplicationContext.getInstance().getService(SnomedClientTerminologyBrowser.class);
		UngroupedAttributesMerger merger = new UngroupedAttributesMerger(new SubsumptionTester(terminologyBrowser));
		Map<Concept, ConceptDefinition> conceptDefinitionMap = new HashMap<Concept, ConceptDefinition>();
		
		Attribute findingSiteLungStructure = ScgBuilderUtils.buildAttribute(SnomedConcepts.FINDING_SITE, 
				SnomedConcepts.LUNG_STRUCTURE);
		Attribute severitySevere = ScgBuilderUtils.buildAttribute(SnomedConcepts.SEVERITY, 
				SnomedConcepts.SEVERE);
		Attribute findingSiteHeadStructure = ScgBuilderUtils.buildAttribute(SnomedConcepts.FINDING_SITE, 
				SnomedConcepts.HEAD_STRUCTURE);

		// build test concept definitions
		Concept concept2 = ScgBuilderUtils.buildConcept("CONCEPT_2");
		ConceptDefinition conceptDefinition2 = ScgBuilderUtils.buildConceptDefinition(severitySevere, findingSiteHeadStructure);
		conceptDefinitionMap.put(concept2, conceptDefinition2);
		Concept concept1 = ScgBuilderUtils.buildConcept("CONCEPT_1");
		ConceptDefinition conceptDefinition1 = ScgBuilderUtils.buildConceptDefinition(findingSiteLungStructure);
		conceptDefinitionMap.put(concept1, conceptDefinition1);
		
		// build expected results
		ConceptDefinition expectedConceptDefinition = ScgBuilderUtils.buildConceptDefinition(findingSiteLungStructure, severitySevere, 
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
		SnomedClientTerminologyBrowser terminologyBrowser = ApplicationContext.getInstance().getService(SnomedClientTerminologyBrowser.class);
		UngroupedAttributesMerger merger = new UngroupedAttributesMerger(new SubsumptionTester(terminologyBrowser));
		Map<Concept, ConceptDefinition> conceptDefinitionMap = new HashMap<Concept, ConceptDefinition>();
		
		Attribute findingSiteLungStructure = ScgBuilderUtils.buildAttribute(SnomedConcepts.FINDING_SITE, SnomedConcepts.LUNG_STRUCTURE);
		Attribute causativeAgentDustAllergen = ScgBuilderUtils.buildAttribute(SnomedConcepts.CAUSATIVE_AGENT, SnomedConcepts.DUST_ALLERGEN);
		Group group = ScgBuilderUtils.buildGroup(Arrays.asList(findingSiteLungStructure, causativeAgentDustAllergen));
		
		Attribute severitySevere = ScgBuilderUtils.buildAttribute(SnomedConcepts.SEVERITY, SnomedConcepts.SEVERE);
		Attribute causativeAgentSubstance = ScgBuilderUtils.buildAttribute(SnomedConcepts.CAUSATIVE_AGENT, SnomedConcepts.SUBSTANCE);

		// build test concept definitions
		Concept concept1 = ScgBuilderUtils.buildConcept("CONCEPT_1");
		ConceptDefinition conceptDefinition1 = ScgBuilderUtils.buildConceptDefinition(group);
		conceptDefinitionMap.put(concept1, conceptDefinition1);
		Concept concept2 = ScgBuilderUtils.buildConcept("CONCEPT_2");
		ConceptDefinition conceptDefinition2 = ScgBuilderUtils.buildConceptDefinition(severitySevere, causativeAgentSubstance);
		conceptDefinitionMap.put(concept2, conceptDefinition2);
		
		// build expected results
		Group expectedGroup = ScgBuilderUtils.buildGroup(Arrays.asList((Attribute)EcoreUtil.copy(findingSiteLungStructure), 
				(Attribute)EcoreUtil.copy(causativeAgentDustAllergen)));
		ConceptDefinition expectedConceptDefinition = ScgBuilderUtils.buildConceptDefinition(Collections.singleton(expectedGroup), 
				Arrays.asList(EcoreUtil.copy(severitySevere)));
		
		ConceptDefinition mergedConceptDefinition = ScgBuilderUtils.buildConceptDefinition(EcoreUtil.copy(group));
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
		SnomedClientTerminologyBrowser terminologyBrowser = ApplicationContext.getInstance().getService(SnomedClientTerminologyBrowser.class);
		UngroupedAttributesMerger merger = new UngroupedAttributesMerger(new SubsumptionTester(terminologyBrowser));
		Map<Concept, ConceptDefinition> conceptDefinitionMap = new HashMap<Concept, ConceptDefinition>();
		
		Attribute findingSiteLungStructure = ScgBuilderUtils.buildAttribute(SnomedConcepts.FINDING_SITE, 
				SnomedConcepts.LUNG_STRUCTURE);
		Attribute causativeAgentSubstance = ScgBuilderUtils.buildAttribute(SnomedConcepts.CAUSATIVE_AGENT, 
				SnomedConcepts.SUBSTANCE);
		Group group = ScgBuilderUtils.buildGroup(Arrays.asList(findingSiteLungStructure, causativeAgentSubstance));
		
		Attribute severitySevere = ScgBuilderUtils.buildAttribute(SnomedConcepts.SEVERITY, 
				SnomedConcepts.SEVERE);
		Attribute causativeAgentDustAllergen = ScgBuilderUtils.buildAttribute(SnomedConcepts.CAUSATIVE_AGENT, 
				SnomedConcepts.DUST_ALLERGEN);
		
		// build test concept definitions
		Concept concept1 = ScgBuilderUtils.buildConcept("CONCEPT_1");
		ConceptDefinition conceptDefinition1 = ScgBuilderUtils.buildConceptDefinition(group);
		conceptDefinitionMap.put(concept1, conceptDefinition1);
		Concept concept2 = ScgBuilderUtils.buildConcept("CONCEPT_2");
		ConceptDefinition conceptDefinition2 = ScgBuilderUtils.buildConceptDefinition(severitySevere, causativeAgentDustAllergen);
		conceptDefinitionMap.put(concept2, conceptDefinition2);
		
		// build expected results
		Group expectedGroup = ScgBuilderUtils.buildGroup(Arrays.asList((Attribute)EcoreUtil.copy(findingSiteLungStructure), 
				(Attribute)EcoreUtil.copy(causativeAgentDustAllergen), (Attribute)EcoreUtil.copy(causativeAgentSubstance)));
		ConceptDefinition expectedConceptDefinition = ScgBuilderUtils.buildConceptDefinition(Collections.singleton(expectedGroup), 
				Collections.singleton(EcoreUtil.copy(severitySevere)));
		
		ConceptDefinition mergedConceptDefinition = ScgBuilderUtils.buildConceptDefinition(group);
		merger.mergeUngroupedAttributes(conceptDefinitionMap, mergedConceptDefinition);
		
		TestUtils.assertConceptDefinitionsEqual(expectedConceptDefinition, mergedConceptDefinition);
	}
	
	@Test
	public void testGroupedAttributeNameMatch3() {
		/* 
		 * If the value of the name-matched grouped and ungrouped attributes are disjoint
		 * 		add the ungrouped attribute as an ungrouped attribute in the target expression.
		 */
		SnomedClientTerminologyBrowser terminologyBrowser = ApplicationContext.getInstance().getService(SnomedClientTerminologyBrowser.class);
		UngroupedAttributesMerger merger = new UngroupedAttributesMerger(new SubsumptionTester(terminologyBrowser));
		Map<Concept, ConceptDefinition> conceptDefinitionMap = new HashMap<Concept, ConceptDefinition>();
		
		Attribute findingSiteLungStructure = ScgBuilderUtils.buildAttribute(SnomedConcepts.FINDING_SITE, 
				SnomedConcepts.LUNG_STRUCTURE);
		Attribute causativeAgentDustAllergen = ScgBuilderUtils.buildAttribute(SnomedConcepts.CAUSATIVE_AGENT, 
				SnomedConcepts.DUST_ALLERGEN);
		Group group = ScgBuilderUtils.buildGroup(Arrays.asList(findingSiteLungStructure, causativeAgentDustAllergen));
		
		Attribute severitySevere = ScgBuilderUtils.buildAttribute(SnomedConcepts.SEVERITY, SnomedConcepts.SEVERE);
		Attribute causativeAgentContactAllergen = ScgBuilderUtils.buildAttribute(SnomedConcepts.CAUSATIVE_AGENT, 
				SnomedConcepts.CONTACT_ALLERGEN);
		
		// build test concept definitions
		Concept concept1 = ScgBuilderUtils.buildConcept("CONCEPT_1");
		ConceptDefinition conceptDefinition1 = ScgBuilderUtils.buildConceptDefinition(group);
		conceptDefinitionMap.put(concept1, conceptDefinition1);
		
		Concept concept2 = ScgBuilderUtils.buildConcept("CONCEPT_2");
		ConceptDefinition conceptDefinition2 = ScgBuilderUtils.buildConceptDefinition(severitySevere, causativeAgentContactAllergen);
		conceptDefinitionMap.put(concept2, conceptDefinition2);
		
		// build expected results
		Group expectedGroup = ScgBuilderUtils.buildGroup(Arrays.asList(new Attribute[] { (Attribute)EcoreUtil.copy(findingSiteLungStructure), 
				(Attribute)EcoreUtil.copy(causativeAgentDustAllergen) }));
		ConceptDefinition expectedConceptDefinition = ScgBuilderUtils.buildConceptDefinition(Collections.singleton(expectedGroup), 
				Arrays.asList(EcoreUtil.copy(severitySevere), EcoreUtil.copy(causativeAgentContactAllergen)));
		
		ConceptDefinition mergedConceptDefinition = ScgBuilderUtils.buildConceptDefinition(group);
		merger.mergeUngroupedAttributes(conceptDefinitionMap, mergedConceptDefinition);
		
		TestUtils.assertConceptDefinitionsEqual(expectedConceptDefinition, mergedConceptDefinition);
	}

	@Test
	public void testGroupedAttributeNameMatch1Reverse() {
		/* 
		 * If the value of the ungrouped attribute subsumes value of the name-matched grouped attribute
		 *		omit the ungrouped attribute from the target definition
		 */
		SnomedClientTerminologyBrowser terminologyBrowser = ApplicationContext.getInstance().getService(SnomedClientTerminologyBrowser.class);
		UngroupedAttributesMerger merger = new UngroupedAttributesMerger(new SubsumptionTester(terminologyBrowser));
		Map<Concept, ConceptDefinition> conceptDefinitionMap = new HashMap<Concept, ConceptDefinition>();
		
		Attribute findingSiteLungStructure = ScgBuilderUtils.buildAttribute(SnomedConcepts.FINDING_SITE, SnomedConcepts.LUNG_STRUCTURE);
		Attribute causativeAgentDustAllergen = ScgBuilderUtils.buildAttribute(SnomedConcepts.CAUSATIVE_AGENT, SnomedConcepts.DUST_ALLERGEN);
		Group group = ScgBuilderUtils.buildGroup(Arrays.asList(findingSiteLungStructure, causativeAgentDustAllergen));
		
		Attribute severitySevere = ScgBuilderUtils.buildAttribute(SnomedConcepts.SEVERITY, SnomedConcepts.SEVERE);
		Attribute causativeAgentSubstance = ScgBuilderUtils.buildAttribute(SnomedConcepts.CAUSATIVE_AGENT, SnomedConcepts.SUBSTANCE);

		// build test concept definitions
		Concept concept2 = ScgBuilderUtils.buildConcept("CONCEPT_2");
		ConceptDefinition conceptDefinition2 = ScgBuilderUtils.buildConceptDefinition(severitySevere, causativeAgentSubstance);
		conceptDefinitionMap.put(concept2, conceptDefinition2);
		Concept concept1 = ScgBuilderUtils.buildConcept("CONCEPT_1");
		ConceptDefinition conceptDefinition1 = ScgBuilderUtils.buildConceptDefinition(group);
		conceptDefinitionMap.put(concept1, conceptDefinition1);
		
		// build expected results
		Group expectedGroup = ScgBuilderUtils.buildGroup(Arrays.asList((Attribute)EcoreUtil.copy(findingSiteLungStructure), 
				(Attribute)EcoreUtil.copy(causativeAgentDustAllergen)));
		ConceptDefinition expectedConceptDefinition = ScgBuilderUtils.buildConceptDefinition(Collections.singleton(expectedGroup), 
				Arrays.asList(EcoreUtil.copy(severitySevere)));
		
		ConceptDefinition mergedConceptDefinition = ScgBuilderUtils.buildConceptDefinition(EcoreUtil.copy(group));
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
		SnomedClientTerminologyBrowser terminologyBrowser = ApplicationContext.getInstance().getService(SnomedClientTerminologyBrowser.class);
		UngroupedAttributesMerger merger = new UngroupedAttributesMerger(new SubsumptionTester(terminologyBrowser));
		Map<Concept, ConceptDefinition> conceptDefinitionMap = new HashMap<Concept, ConceptDefinition>();
		
		Attribute findingSiteLungStructure = ScgBuilderUtils.buildAttribute(SnomedConcepts.FINDING_SITE, 
				SnomedConcepts.LUNG_STRUCTURE);
		Attribute causativeAgentSubstance = ScgBuilderUtils.buildAttribute(SnomedConcepts.CAUSATIVE_AGENT, 
				SnomedConcepts.SUBSTANCE);
		Group group = ScgBuilderUtils.buildGroup(Arrays.asList(findingSiteLungStructure, causativeAgentSubstance));
		
		Attribute severitySevere = ScgBuilderUtils.buildAttribute(SnomedConcepts.SEVERITY, 
				SnomedConcepts.SEVERE);
		Attribute causativeAgentDustAllergen = ScgBuilderUtils.buildAttribute(SnomedConcepts.CAUSATIVE_AGENT, 
				SnomedConcepts.DUST_ALLERGEN);
		
		// build test concept definitions
		Concept concept2 = ScgBuilderUtils.buildConcept("CONCEPT_2");
		ConceptDefinition conceptDefinition2 = ScgBuilderUtils.buildConceptDefinition(severitySevere, causativeAgentDustAllergen);
		conceptDefinitionMap.put(concept2, conceptDefinition2);
		Concept concept1 = ScgBuilderUtils.buildConcept("CONCEPT_1");
		ConceptDefinition conceptDefinition1 = ScgBuilderUtils.buildConceptDefinition(group);
		conceptDefinitionMap.put(concept1, conceptDefinition1);
		
		// build expected results
		Group expectedGroup = ScgBuilderUtils.buildGroup(Arrays.asList((Attribute)EcoreUtil.copy(findingSiteLungStructure), 
				(Attribute)EcoreUtil.copy(causativeAgentDustAllergen), (Attribute)EcoreUtil.copy(causativeAgentSubstance)));
		ConceptDefinition expectedConceptDefinition = ScgBuilderUtils.buildConceptDefinition(Collections.singleton(expectedGroup), 
				Collections.singleton(EcoreUtil.copy(severitySevere)));
		
		ConceptDefinition mergedConceptDefinition = ScgBuilderUtils.buildConceptDefinition(group);
		merger.mergeUngroupedAttributes(conceptDefinitionMap, mergedConceptDefinition);
		
		TestUtils.assertConceptDefinitionsEqual(expectedConceptDefinition, mergedConceptDefinition);
	}

	@Test
	public void testGroupedAttributeNameMatch3Reverse() {
		/* 
		 * If the value of the name-matched grouped and ungrouped attributes are disjoint
		 * 		add the ungrouped attribute as an ungrouped attribute in the target expression.
		 */
		SnomedClientTerminologyBrowser terminologyBrowser = ApplicationContext.getInstance().getService(SnomedClientTerminologyBrowser.class);
		UngroupedAttributesMerger merger = new UngroupedAttributesMerger(new SubsumptionTester(terminologyBrowser));
		Map<Concept, ConceptDefinition> conceptDefinitionMap = new HashMap<Concept, ConceptDefinition>();
		
		Attribute findingSiteLungStructure = ScgBuilderUtils.buildAttribute(SnomedConcepts.FINDING_SITE, 
				SnomedConcepts.LUNG_STRUCTURE);
		Attribute causativeAgentDustAllergen = ScgBuilderUtils.buildAttribute(SnomedConcepts.CAUSATIVE_AGENT, 
				SnomedConcepts.DUST_ALLERGEN);
		Group group = ScgBuilderUtils.buildGroup(Arrays.asList(findingSiteLungStructure, causativeAgentDustAllergen));
		
		Attribute severitySevere = ScgBuilderUtils.buildAttribute(SnomedConcepts.SEVERITY, SnomedConcepts.SEVERE);
		Attribute causativeAgentContactAllergen = ScgBuilderUtils.buildAttribute(SnomedConcepts.CAUSATIVE_AGENT, 
				SnomedConcepts.CONTACT_ALLERGEN);
		
		// build test concept definitions
		Concept concept2 = ScgBuilderUtils.buildConcept("CONCEPT_2");
		ConceptDefinition conceptDefinition2 = ScgBuilderUtils.buildConceptDefinition(severitySevere, causativeAgentContactAllergen);
		conceptDefinitionMap.put(concept2, conceptDefinition2);
		Concept concept1 = ScgBuilderUtils.buildConcept("CONCEPT_1");
		ConceptDefinition conceptDefinition1 = ScgBuilderUtils.buildConceptDefinition(group);
		conceptDefinitionMap.put(concept1, conceptDefinition1);
		
		// build expected results
		Group expectedGroup = ScgBuilderUtils.buildGroup(Arrays.asList(new Attribute[] { (Attribute)EcoreUtil.copy(findingSiteLungStructure), 
				(Attribute)EcoreUtil.copy(causativeAgentDustAllergen) }));
		ConceptDefinition expectedConceptDefinition = ScgBuilderUtils.buildConceptDefinition(Collections.singleton(expectedGroup), 
				Arrays.asList(EcoreUtil.copy(severitySevere), EcoreUtil.copy(causativeAgentContactAllergen)));
		
		ConceptDefinition mergedConceptDefinition = ScgBuilderUtils.buildConceptDefinition(group);
		merger.mergeUngroupedAttributes(conceptDefinitionMap, mergedConceptDefinition);
		
		TestUtils.assertConceptDefinitionsEqual(expectedConceptDefinition, mergedConceptDefinition);
	}
}
