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

import static com.b2international.snowowl.semanticengine.simpleast.test.utils.TestUtils.assertConceptDefinitionsEqual;
import static com.b2international.snowowl.semanticengine.simpleast.test.utils.TestUtils.buildAttribute;
import static com.b2international.snowowl.semanticengine.simpleast.test.utils.TestUtils.buildAttributeClauseList;
import static com.b2international.snowowl.semanticengine.simpleast.test.utils.TestUtils.buildConcept;
import static com.b2international.snowowl.semanticengine.simpleast.test.utils.TestUtils.buildConceptDefinition;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.junit.Before;
import org.junit.Test;

import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.semanticengine.simpleast.normalform.AttributeClauseList;
import com.b2international.snowowl.semanticengine.simpleast.normalform.ConceptDefinition;
import com.b2international.snowowl.semanticengine.simpleast.normalform.ConceptDefinitionMerger;
import com.b2international.snowowl.semanticengine.simpleast.subsumption.SubsumptionTester;
import com.b2international.snowowl.semanticengine.simpleast.test.SnomedConcepts;
import com.b2international.snowowl.snomed.dsl.query.queryast.AttributeClause;
import com.b2international.snowowl.snomed.dsl.query.queryast.ConceptRef;

public class ConceptDefinitionMergerTest {

	private ConceptDefinitionMerger conceptDefinitionMerger;

	@Before
	public void beforeTest() {
		conceptDefinitionMerger = new ConceptDefinitionMerger(new SubsumptionTester(Branch.MAIN_PATH));
	}
	
	@Test
	public void test1() {
		/* 
		 * If the value of the grouped attribute is subsumed by the value of the name-matched grouped attribute
		 *		add the ungrouped attribute to the group containing the matching grouped attribute in the target definition
		 *		if this condition is met by multiple groups
		 *			add the ungrouped attribute to all groups that meet this condition
		 */
		Map<ConceptRef, ConceptDefinition> conceptDefinitionMap = new HashMap<ConceptRef, ConceptDefinition>();
		
		AttributeClause findingSiteLungStructure = buildAttribute(SnomedConcepts.FINDING_SITE, 
				SnomedConcepts.LUNG_STRUCTURE);
		AttributeClause causativeAgentSubstance = buildAttribute(SnomedConcepts.CAUSATIVE_AGENT, 
				SnomedConcepts.SUBSTANCE);
		AttributeClauseList group = buildAttributeClauseList(Arrays.asList(findingSiteLungStructure, causativeAgentSubstance));
		
		AttributeClause severitySevere = buildAttribute(SnomedConcepts.SEVERITY, 
				SnomedConcepts.SEVERE);
		AttributeClause causativeAgentDustAllergen = buildAttribute(SnomedConcepts.CAUSATIVE_AGENT, 
				SnomedConcepts.DUST_ALLERGEN);
		
		// build test concept definitions
		ConceptRef concept1 = buildConcept("CONCEPT_1");
		ConceptDefinition conceptDefinition1 = buildConceptDefinition(group);
		conceptDefinitionMap.put(concept1, conceptDefinition1);
		ConceptRef concept2 = buildConcept("CONCEPT_2");
		ConceptDefinition conceptDefinition2 = buildConceptDefinition(severitySevere, causativeAgentDustAllergen);
		conceptDefinitionMap.put(concept2, conceptDefinition2);
		
		// build expected results
		AttributeClauseList expectedAttributeClauseList = buildAttributeClauseList(Arrays.asList(EcoreUtil.copy(findingSiteLungStructure), 
				EcoreUtil.copy(causativeAgentSubstance)));
		ConceptDefinition expectedConceptDefinition = buildConceptDefinition(Collections.singleton(expectedAttributeClauseList), 
				Collections.singleton(EcoreUtil.copy(severitySevere)));
		
		ConceptDefinition mergedConceptDefinition = conceptDefinitionMerger.mergeDefinitions(conceptDefinitionMap);
		
		assertConceptDefinitionsEqual(expectedConceptDefinition, mergedConceptDefinition);
	}
}
