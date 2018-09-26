/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.semanticengine.simpleast.test;

import org.junit.ClassRule;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.b2international.snowowl.semanticengine.simpleast.normalform.test.AttributeGroupMergerTest;
import com.b2international.snowowl.semanticengine.simpleast.normalform.test.AttributeNormalizerTest;
import com.b2international.snowowl.semanticengine.simpleast.normalform.test.ConceptDefinitionAttributeRedundancyFilterTest;
import com.b2international.snowowl.semanticengine.simpleast.normalform.test.ConceptDefinitionMergerTest;
import com.b2international.snowowl.semanticengine.simpleast.normalform.test.ExpressionCanonicalRepresentationTest;
import com.b2international.snowowl.semanticengine.simpleast.normalform.test.FocusConceptNormalizerTest;
import com.b2international.snowowl.semanticengine.simpleast.normalform.test.NormalFormGeneratorTestFromSnomedDocument;
import com.b2international.snowowl.semanticengine.simpleast.normalform.test.SimpleAstNormalFormGeneratorTest;
import com.b2international.snowowl.semanticengine.simpleast.normalform.test.UngroupedAttributesMergerTest;
import com.b2international.snowowl.test.commons.SnowOwlAppRule;

/**
 * Test suite for all {@link BasicExpressionNormalFormGenerator} related unit tests.
 * <br/>
 * <i>Note: run with at least 1G heap size.</i>
 * 
 */
@RunWith(Suite.class)
@SuiteClasses({//ConceptDefinitionNormalizerTest.class, 
	AttributeGroupMergerTest.class, 
	UngroupedAttributesMergerTest.class, 
	ConceptDefinitionAttributeRedundancyFilterTest.class, 
	ConceptDefinitionMergerTest.class, 
	FocusConceptNormalizerTest.class, 
	AttributeNormalizerTest.class,
	SimpleAstNormalFormGeneratorTest.class,
	NormalFormGeneratorTestFromSnomedDocument.class,
	ExpressionCanonicalRepresentationTest.class})
public class AllNormalFormGeneratorTests {
	
	@ClassRule
	public static final SnowOwlAppRule appRule = SnowOwlAppRule.snowOwl(AllNormalFormGeneratorTests.class);
	
}
