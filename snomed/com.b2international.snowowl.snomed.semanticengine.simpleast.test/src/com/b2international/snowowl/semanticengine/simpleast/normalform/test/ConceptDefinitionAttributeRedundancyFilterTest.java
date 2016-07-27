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
import static com.b2international.snowowl.semanticengine.simpleast.test.utils.TestUtils.buildConceptDefinition;

import java.util.Collections;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.junit.Before;
import org.junit.Test;

import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.semanticengine.simpleast.normalform.AttributeClauseList;
import com.b2international.snowowl.semanticengine.simpleast.normalform.ConceptDefinition;
import com.b2international.snowowl.semanticengine.simpleast.normalform.ConceptDefinitionAttributeRedundancyFilter;
import com.b2international.snowowl.semanticengine.simpleast.subsumption.SubsumptionTester;
import com.b2international.snowowl.semanticengine.simpleast.test.SnomedConcepts;
import com.b2international.snowowl.snomed.dsl.query.queryast.AttributeClause;

public class ConceptDefinitionAttributeRedundancyFilterTest {

	private SubsumptionTester subsumptionTester;

	@Before
	public void beforeTest() {
		subsumptionTester = new SubsumptionTester(Branch.MAIN_PATH);
	}
	
	@Test
	public void testEmptyConceptDefinition() {
		ConceptDefinitionAttributeRedundancyFilter filter = new ConceptDefinitionAttributeRedundancyFilter(subsumptionTester);
		ConceptDefinition filteredConceptDefinition = filter.getFilteredConceptDefinition(new ConceptDefinition());
		ConceptDefinition expectedConceptDefinition = buildConceptDefinition(Collections.<AttributeClauseList>emptySet(), 
				Collections.<AttributeClause>emptySet());
		assertConceptDefinitionsEqual(expectedConceptDefinition, filteredConceptDefinition);
	}
	
	@Test
	public void testAttributeGroupRedundancyFilter1() {
		AttributeClause tigerAttributeClause = buildAttribute(SnomedConcepts.ATTRIBUTE, SnomedConcepts.PANTHERA_TIGRIS);
		AttributeClause felidaeAttributeClause = buildAttribute(SnomedConcepts.ATTRIBUTE, SnomedConcepts.FAMILY_FELIDAE);
		ConceptDefinition conceptDefinition = buildConceptDefinition(buildAttributeClauseList(tigerAttributeClause, felidaeAttributeClause));

		ConceptDefinition expectedConceptDefinition = buildConceptDefinition(buildAttributeClauseList(EcoreUtil.copy(felidaeAttributeClause)));
		
		ConceptDefinitionAttributeRedundancyFilter filter = new ConceptDefinitionAttributeRedundancyFilter(subsumptionTester);
		ConceptDefinition filteredConceptDefinition = filter.getFilteredConceptDefinition(conceptDefinition);

		assertConceptDefinitionsEqual(expectedConceptDefinition, filteredConceptDefinition);
	}
	
	@Test
	public void testAttributeGroupRedundancyFilter2() {
		AttributeClause attributeTiger = buildAttribute(SnomedConcepts.ATTRIBUTE, SnomedConcepts.PANTHERA_TIGRIS);
		AttributeClause associatedWithFamilyFelidae = buildAttribute(SnomedConcepts.ASSOCIATED_WITH, SnomedConcepts.FAMILY_FELIDAE);
		AttributeClause attributeSubfamilyPantherinae = buildAttribute(SnomedConcepts.ATTRIBUTE, SnomedConcepts.SUBFAMILY_PANTHERINAE);
		AttributeClause associatedWithSubstance = buildAttribute(SnomedConcepts.ASSOCIATED_WITH, SnomedConcepts.SUBSTANCE);
		ConceptDefinition conceptDefinition = buildConceptDefinition(buildAttributeClauseList(attributeTiger, associatedWithSubstance, 
				associatedWithFamilyFelidae, attributeSubfamilyPantherinae));
		
		ConceptDefinition expectedConceptDefinition = buildConceptDefinition(buildAttributeClauseList(EcoreUtil.copy(associatedWithFamilyFelidae), 
				EcoreUtil.copy(associatedWithSubstance), EcoreUtil.copy(attributeSubfamilyPantherinae)));
		
		ConceptDefinitionAttributeRedundancyFilter filter = new ConceptDefinitionAttributeRedundancyFilter(subsumptionTester);
		ConceptDefinition filteredConceptDefinition = filter.getFilteredConceptDefinition(conceptDefinition);
		
		assertConceptDefinitionsEqual(expectedConceptDefinition, filteredConceptDefinition);
	}
	
	@Test
	public void testUngroupedAttributeRedundancyFilter1() {
		AttributeClause tigerAttributeClause = buildAttribute(SnomedConcepts.ATTRIBUTE, SnomedConcepts.PANTHERA_TIGRIS);
		AttributeClause felidaeAttributeClause = buildAttribute(SnomedConcepts.ATTRIBUTE, SnomedConcepts.FAMILY_FELIDAE);
		ConceptDefinition conceptDefinition = buildConceptDefinition(tigerAttributeClause, felidaeAttributeClause);
		
		ConceptDefinition expectedConceptDefinition = buildConceptDefinition(EcoreUtil.copy(felidaeAttributeClause));
		
		ConceptDefinitionAttributeRedundancyFilter filter = new ConceptDefinitionAttributeRedundancyFilter(subsumptionTester);
		ConceptDefinition filteredConceptDefinition = filter.getFilteredConceptDefinition(conceptDefinition);
		
		assertConceptDefinitionsEqual(expectedConceptDefinition, filteredConceptDefinition);
	}
	
	@Test
	public void testUngroupedAttributeRedundancyFilter2() {
		AttributeClause attributeTiger = buildAttribute(SnomedConcepts.ATTRIBUTE, SnomedConcepts.PANTHERA_TIGRIS);
		AttributeClause associatedWithFamilyFelidae = buildAttribute(SnomedConcepts.ASSOCIATED_WITH, SnomedConcepts.FAMILY_FELIDAE);
		AttributeClause attributeSubfamilyPantherinae = buildAttribute(SnomedConcepts.ATTRIBUTE, SnomedConcepts.SUBFAMILY_PANTHERINAE);
		AttributeClause associatedWithSubstance = buildAttribute(SnomedConcepts.ASSOCIATED_WITH, SnomedConcepts.SUBSTANCE);
		ConceptDefinition conceptDefinition = buildConceptDefinition(attributeTiger, associatedWithSubstance, 
				associatedWithFamilyFelidae, attributeSubfamilyPantherinae);
		
		ConceptDefinition expectedConceptDefinition = buildConceptDefinition(EcoreUtil.copy(associatedWithFamilyFelidae), 
				EcoreUtil.copy(associatedWithSubstance), EcoreUtil.copy(attributeSubfamilyPantherinae));
		
		ConceptDefinitionAttributeRedundancyFilter filter = new ConceptDefinitionAttributeRedundancyFilter(subsumptionTester);
		ConceptDefinition filteredConceptDefinition = filter.getFilteredConceptDefinition(conceptDefinition);
		
		assertConceptDefinitionsEqual(expectedConceptDefinition, filteredConceptDefinition);
	}
}
