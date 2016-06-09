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
import static com.b2international.snowowl.semanticengine.simpleast.test.utils.TestUtils.buildExpression;
import static com.b2international.snowowl.semanticengine.simpleast.test.utils.TestUtils.buildUnconstrainedConceptGroup;
import static org.junit.Assert.assertEquals;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.semanticengine.simpleast.normalform.AttributeClauseList;
import com.b2international.snowowl.semanticengine.simpleast.normalform.AttributeNormalizer;
import com.b2international.snowowl.semanticengine.simpleast.normalform.ConceptDefinition;
import com.b2international.snowowl.semanticengine.simpleast.test.SnomedConcepts;
import com.b2international.snowowl.snomed.datastore.SnomedClientTerminologyBrowser;
import com.b2international.snowowl.snomed.dsl.query.queryast.AttributeClause;
import com.b2international.snowowl.snomed.dsl.query.queryast.RValue;

public class AttributeNormalizerTest {

	private AttributeNormalizer attributeNormalizer;

	@Before
	public void beforeTest() {
		SnomedClientTerminologyBrowser terminologyBrowser = ApplicationContext.getInstance().getService(SnomedClientTerminologyBrowser.class);
		attributeNormalizer = new AttributeNormalizer(terminologyBrowser);
	}
	
	@Test
	public void testEmptyInput() {
		ConceptDefinition conceptDefinition = attributeNormalizer.normalizeAttributes(Collections.<AttributeClauseList>emptyList(), 
				Collections.<AttributeClause>emptyList());
		assertEquals(0, conceptDefinition.getAttributeClauseLists().size());
		assertEquals(0, conceptDefinition.getUngroupedAttributes().size());
	}
	
	@Test
	public void testUngroupedAttributeNormalization() {
		AttributeClause associatedFindingPain = buildAttribute(SnomedConcepts.ASSOCIATED_FINDING, SnomedConcepts.FOOT_PAIN);
		ConceptDefinition normalizedConceptDefinition = attributeNormalizer.normalizeAttributes(Collections.<AttributeClauseList>emptyList(), 
				Collections.singletonList(associatedFindingPain));

		RValue expectedValueExpression = buildExpression(Collections.singletonList(buildUnconstrainedConceptGroup(SnomedConcepts.PAIN)), Collections.<AttributeClauseList>emptyList(),
				Collections.singletonList(buildAttribute(SnomedConcepts.FINDING_SITE, SnomedConcepts.FOOT_STRUCTURE)));
		ConceptDefinition expectedConceptDefinition = buildConceptDefinition(buildAttribute(SnomedConcepts.ASSOCIATED_FINDING, expectedValueExpression));
		assertConceptDefinitionsEqual(expectedConceptDefinition, normalizedConceptDefinition);
	}
	
	@Test
	public void testGroupedAttributeNormalization() {
		AttributeClause associatedFindingPain = buildAttribute(SnomedConcepts.ASSOCIATED_FINDING, SnomedConcepts.FOOT_PAIN);
		ConceptDefinition normalizedConceptDefinition = attributeNormalizer.normalizeAttributes(Collections.singletonList(
				buildAttributeClauseList(associatedFindingPain)), 
				Collections.<AttributeClause>emptyList());

		RValue expectedValueExpression = buildExpression(Collections.singletonList(buildUnconstrainedConceptGroup(SnomedConcepts.PAIN)), Collections.<AttributeClauseList>emptyList(),
				Collections.singletonList(buildAttribute(SnomedConcepts.FINDING_SITE, SnomedConcepts.FOOT_STRUCTURE)));
		ConceptDefinition expectedConceptDefinition = buildConceptDefinition(buildAttributeClauseList(buildAttribute(SnomedConcepts.ASSOCIATED_FINDING, expectedValueExpression)));
		assertConceptDefinitionsEqual(expectedConceptDefinition, normalizedConceptDefinition);
	}
}
