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
import static com.b2international.snowowl.semanticengine.utils.ScgBuilderUtils.buildGroup;

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
import com.b2international.snowowl.semanticengine.normalform.ConceptDefinitionMerger;
import com.b2international.snowowl.semanticengine.subsumption.SubsumptionTester;
import com.b2international.snowowl.semanticengine.test.SnomedConcepts;
import com.b2international.snowowl.snomed.datastore.RecursiveTerminologyBrowser;
import com.b2international.snowowl.snomed.datastore.SnomedClientTerminologyBrowser;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptIndexEntry;

public class ConceptDefinitionMergerTest {

	private ConceptDefinitionMerger conceptDefinitionMerger;

	@Before
	public void beforeTest() {
		SnomedClientTerminologyBrowser terminologyBrowser = ApplicationContext.getInstance().getService(SnomedClientTerminologyBrowser.class);
		RecursiveTerminologyBrowser<SnomedConceptIndexEntry, String> recursiveTerminologyBrowser = 
				new RecursiveTerminologyBrowser<SnomedConceptIndexEntry, String>(terminologyBrowser);
		conceptDefinitionMerger = new ConceptDefinitionMerger(new SubsumptionTester(recursiveTerminologyBrowser));
	}
	
	@Test
	public void test1() {
		/* 
		 * If the value of the grouped attribute is subsumed by the value of the name-matched grouped attribute
		 *		add the ungrouped attribute to the group containing the matching grouped attribute in the target definition
		 *		if this condition is met by multiple groups
		 *			add the ungrouped attribute to all groups that meet this condition
		 */
		Map<Concept, ConceptDefinition> conceptDefinitionMap = new HashMap<Concept, ConceptDefinition>();
		
		Attribute findingSiteLungStructure = buildAttribute(SnomedConcepts.FINDING_SITE, 
				SnomedConcepts.LUNG_STRUCTURE);
		Attribute causativeAgentSubstance = buildAttribute(SnomedConcepts.CAUSATIVE_AGENT, 
				SnomedConcepts.SUBSTANCE);
		Group group = buildGroup(Arrays.asList(findingSiteLungStructure, causativeAgentSubstance));
		
		Attribute severitySevere = buildAttribute(SnomedConcepts.SEVERITY, 
				SnomedConcepts.SEVERE);
		Attribute causativeAgentDustAllergen = buildAttribute(SnomedConcepts.CAUSATIVE_AGENT, 
				SnomedConcepts.DUST_ALLERGEN);
		
		// build test concept definitions
		Concept concept1 = buildConcept("CONCEPT_1");
		ConceptDefinition conceptDefinition1 = buildConceptDefinition(group);
		conceptDefinitionMap.put(concept1, conceptDefinition1);
		Concept concept2 = buildConcept("CONCEPT_2");
		ConceptDefinition conceptDefinition2 = buildConceptDefinition(severitySevere, causativeAgentDustAllergen);
		conceptDefinitionMap.put(concept2, conceptDefinition2);
		
		// build expected results
		Group expectedGroup = buildGroup(Arrays.asList((Attribute)EcoreUtil.copy(findingSiteLungStructure), 
				(Attribute)EcoreUtil.copy(causativeAgentSubstance)));
		ConceptDefinition expectedConceptDefinition = buildConceptDefinition(Collections.singleton(expectedGroup), 
				Collections.singleton(EcoreUtil.copy(severitySevere)));
		
		ConceptDefinition mergedConceptDefinition = conceptDefinitionMerger.mergeDefinitions(conceptDefinitionMap);
		
		assertConceptDefinitionsEqual(expectedConceptDefinition, mergedConceptDefinition);
	}
}
