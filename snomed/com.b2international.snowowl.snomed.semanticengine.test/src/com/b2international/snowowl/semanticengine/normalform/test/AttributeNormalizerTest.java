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
import static com.b2international.snowowl.semanticengine.utils.ScgBuilderUtils.buildExpression;
import static com.b2international.snowowl.semanticengine.utils.ScgBuilderUtils.buildGroup;
import static org.junit.Assert.assertEquals;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.dsl.scg.Attribute;
import com.b2international.snowowl.dsl.scg.Expression;
import com.b2international.snowowl.dsl.scg.Group;
import com.b2international.snowowl.semanticengine.normalform.AttributeNormalizer;
import com.b2international.snowowl.semanticengine.normalform.ConceptDefinition;
import com.b2international.snowowl.semanticengine.test.SnomedConcepts;
import com.b2international.snowowl.snomed.datastore.RecursiveTerminologyBrowser;
import com.b2international.snowowl.snomed.datastore.SnomedClientStatementBrowser;
import com.b2international.snowowl.snomed.datastore.SnomedClientTerminologyBrowser;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptIndexEntry;

public class AttributeNormalizerTest {

	private AttributeNormalizer attributeNormalizer;

	@Before
	public void beforeTest() {
		SnomedClientTerminologyBrowser terminologyBrowser = ApplicationContext.getInstance().getService(SnomedClientTerminologyBrowser.class);
		RecursiveTerminologyBrowser<SnomedConceptIndexEntry, String> recursiveTerminologyBrowser = 
				new RecursiveTerminologyBrowser<SnomedConceptIndexEntry, String>(terminologyBrowser);
		SnomedClientStatementBrowser statementBrowser = ApplicationContext.getInstance().getService(SnomedClientStatementBrowser.class);
		attributeNormalizer = new AttributeNormalizer(recursiveTerminologyBrowser, statementBrowser);
	}
	
	@Test
	public void testEmptyInput() {
		ConceptDefinition conceptDefinition = attributeNormalizer.normalizeAttributes(Collections.<Group>emptyList(), 
				Collections.<Attribute>emptyList());
		assertEquals(0, conceptDefinition.getGroups().size());
		assertEquals(0, conceptDefinition.getUngroupedAttributes().size());
	}
	
	@Test
	public void testUngroupedAttributeNormalization() {
		Attribute associatedFindingPain = buildAttribute(SnomedConcepts.ASSOCIATED_FINDING, SnomedConcepts.FOOT_PAIN);
		ConceptDefinition normalizedConceptDefinition = attributeNormalizer.normalizeAttributes(Collections.<Group>emptyList(), 
				Collections.singletonList(associatedFindingPain));

		Expression expectedValueExpression = buildExpression(Collections.singleton(buildConcept(SnomedConcepts.PAIN)), Collections.<Group>emptySet(),
				Collections.singleton(buildAttribute(SnomedConcepts.FINDING_SITE, SnomedConcepts.FOOT_STRUCTURE)));
		ConceptDefinition expectedConceptDefinition = buildConceptDefinition(buildAttribute(SnomedConcepts.ASSOCIATED_FINDING, expectedValueExpression));
		assertConceptDefinitionsEqual(expectedConceptDefinition, normalizedConceptDefinition);
	}
	
	@Test
	public void testGroupedAttributeNormalization() {
		Attribute associatedFindingPain = buildAttribute(SnomedConcepts.ASSOCIATED_FINDING, SnomedConcepts.FOOT_PAIN);
		ConceptDefinition normalizedConceptDefinition = attributeNormalizer.normalizeAttributes(Collections.singletonList(
				buildGroup(associatedFindingPain)), 
				Collections.<Attribute>emptyList());

		Expression expectedValueExpression = buildExpression(Collections.singleton(buildConcept(SnomedConcepts.PAIN)), Collections.<Group>emptySet(),
				Collections.singleton(buildAttribute(SnomedConcepts.FINDING_SITE, SnomedConcepts.FOOT_STRUCTURE)));
		ConceptDefinition expectedConceptDefinition = buildConceptDefinition(buildGroup(buildAttribute(SnomedConcepts.ASSOCIATED_FINDING, expectedValueExpression)));
		assertConceptDefinitionsEqual(expectedConceptDefinition, normalizedConceptDefinition);
	}
}
