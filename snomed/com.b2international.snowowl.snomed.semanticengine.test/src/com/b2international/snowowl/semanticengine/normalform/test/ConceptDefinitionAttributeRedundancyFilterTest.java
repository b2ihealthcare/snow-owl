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
import static com.b2international.snowowl.semanticengine.utils.ScgBuilderUtils.buildConceptDefinition;
import static com.b2international.snowowl.semanticengine.utils.ScgBuilderUtils.buildGroup;

import java.util.Collections;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.junit.Before;
import org.junit.Test;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.dsl.scg.Attribute;
import com.b2international.snowowl.dsl.scg.Group;
import com.b2international.snowowl.semanticengine.normalform.ConceptDefinition;
import com.b2international.snowowl.semanticengine.normalform.ConceptDefinitionAttributeRedundancyFilter;
import com.b2international.snowowl.semanticengine.subsumption.SubsumptionTester;
import com.b2international.snowowl.semanticengine.test.SnomedConcepts;
import com.b2international.snowowl.snomed.datastore.RecursiveTerminologyBrowser;
import com.b2international.snowowl.snomed.datastore.SnomedClientTerminologyBrowser;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptIndexEntry;

public class ConceptDefinitionAttributeRedundancyFilterTest {

	private SubsumptionTester subsumptionTester;

	@Before
	public void beforeTest() {
		SnomedClientTerminologyBrowser terminologyBrowser = ApplicationContext.getInstance().getService(SnomedClientTerminologyBrowser.class);
		RecursiveTerminologyBrowser<SnomedConceptIndexEntry, String> recursiveTerminologyBrowser = 
				new RecursiveTerminologyBrowser<SnomedConceptIndexEntry, String>(terminologyBrowser);
		subsumptionTester = new SubsumptionTester(recursiveTerminologyBrowser);
	}
	
	@Test
	public void testEmptyConceptDefinition() {
		ConceptDefinitionAttributeRedundancyFilter filter = new ConceptDefinitionAttributeRedundancyFilter(subsumptionTester);
		ConceptDefinition filteredConceptDefinition = filter.getFilteredConceptDefinition(new ConceptDefinition());
		ConceptDefinition expectedConceptDefinition = buildConceptDefinition(Collections.<Group>emptySet(), 
				Collections.<Attribute>emptySet());
		assertConceptDefinitionsEqual(expectedConceptDefinition, filteredConceptDefinition);
	}
	
	@Test
	public void testGroupRedundancyFilter1() {
		Attribute tigerAttribute = buildAttribute(SnomedConcepts.ATTRIBUTE, SnomedConcepts.PANTHERA_TIGRIS);
		Attribute felidaeAttribute = buildAttribute(SnomedConcepts.ATTRIBUTE, SnomedConcepts.FAMILY_FELIDAE);
		ConceptDefinition conceptDefinition = buildConceptDefinition(buildGroup(tigerAttribute, felidaeAttribute));

		ConceptDefinition expectedConceptDefinition = buildConceptDefinition(buildGroup(EcoreUtil.copy(felidaeAttribute)));
		
		ConceptDefinitionAttributeRedundancyFilter filter = new ConceptDefinitionAttributeRedundancyFilter(subsumptionTester);
		ConceptDefinition filteredConceptDefinition = filter.getFilteredConceptDefinition(conceptDefinition);

		assertConceptDefinitionsEqual(expectedConceptDefinition, filteredConceptDefinition);
	}
	
	@Test
	public void testGroupRedundancyFilter2() {
		Attribute attributeTiger = buildAttribute(SnomedConcepts.ATTRIBUTE, SnomedConcepts.PANTHERA_TIGRIS);
		Attribute associatedWithFamilyFelidae = buildAttribute(SnomedConcepts.ASSOCIATED_WITH, SnomedConcepts.FAMILY_FELIDAE);
		Attribute attributeSubfamilyPantherinae = buildAttribute(SnomedConcepts.ATTRIBUTE, SnomedConcepts.SUBFAMILY_PANTHERINAE);
		Attribute associatedWithSubstance = buildAttribute(SnomedConcepts.ASSOCIATED_WITH, SnomedConcepts.SUBSTANCE);
		ConceptDefinition conceptDefinition = buildConceptDefinition(buildGroup(attributeTiger, associatedWithSubstance, 
				associatedWithFamilyFelidae, attributeSubfamilyPantherinae));
		
		ConceptDefinition expectedConceptDefinition = buildConceptDefinition(buildGroup(EcoreUtil.copy(associatedWithFamilyFelidae), 
				EcoreUtil.copy(associatedWithSubstance), EcoreUtil.copy(attributeSubfamilyPantherinae)));
		
		ConceptDefinitionAttributeRedundancyFilter filter = new ConceptDefinitionAttributeRedundancyFilter(subsumptionTester);
		ConceptDefinition filteredConceptDefinition = filter.getFilteredConceptDefinition(conceptDefinition);
		
		assertConceptDefinitionsEqual(expectedConceptDefinition, filteredConceptDefinition);
	}
	
	@Test
	public void testUngroupedAttributeRedundancyFilter1() {
		Attribute tigerAttribute = buildAttribute(SnomedConcepts.ATTRIBUTE, SnomedConcepts.PANTHERA_TIGRIS);
		Attribute felidaeAttribute = buildAttribute(SnomedConcepts.ATTRIBUTE, SnomedConcepts.FAMILY_FELIDAE);
		ConceptDefinition conceptDefinition = buildConceptDefinition(tigerAttribute, felidaeAttribute);
		
		ConceptDefinition expectedConceptDefinition = buildConceptDefinition(EcoreUtil.copy(felidaeAttribute));
		
		ConceptDefinitionAttributeRedundancyFilter filter = new ConceptDefinitionAttributeRedundancyFilter(subsumptionTester);
		ConceptDefinition filteredConceptDefinition = filter.getFilteredConceptDefinition(conceptDefinition);
		
		assertConceptDefinitionsEqual(expectedConceptDefinition, filteredConceptDefinition);
	}
	
	@Test
	public void testUngroupedAttributeRedundancyFilter2() {
		Attribute attributeTiger = buildAttribute(SnomedConcepts.ATTRIBUTE, SnomedConcepts.PANTHERA_TIGRIS);
		Attribute associatedWithFamilyFelidae = buildAttribute(SnomedConcepts.ASSOCIATED_WITH, SnomedConcepts.FAMILY_FELIDAE);
		Attribute attributeSubfamilyPantherinae = buildAttribute(SnomedConcepts.ATTRIBUTE, SnomedConcepts.SUBFAMILY_PANTHERINAE);
		Attribute associatedWithSubstance = buildAttribute(SnomedConcepts.ASSOCIATED_WITH, SnomedConcepts.SUBSTANCE);
		ConceptDefinition conceptDefinition = buildConceptDefinition(attributeTiger, associatedWithSubstance, 
				associatedWithFamilyFelidae, attributeSubfamilyPantherinae);
		
		ConceptDefinition expectedConceptDefinition = buildConceptDefinition(EcoreUtil.copy(associatedWithFamilyFelidae), 
				EcoreUtil.copy(associatedWithSubstance), EcoreUtil.copy(attributeSubfamilyPantherinae));
		
		ConceptDefinitionAttributeRedundancyFilter filter = new ConceptDefinitionAttributeRedundancyFilter(subsumptionTester);
		ConceptDefinition filteredConceptDefinition = filter.getFilteredConceptDefinition(conceptDefinition);
		
		assertConceptDefinitionsEqual(expectedConceptDefinition, filteredConceptDefinition);
	}
}
