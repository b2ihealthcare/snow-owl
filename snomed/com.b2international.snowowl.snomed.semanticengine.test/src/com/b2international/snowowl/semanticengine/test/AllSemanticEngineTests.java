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
package com.b2international.snowowl.semanticengine.test;

import org.junit.ClassRule;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.b2international.snowowl.semanticengine.normalform.test.AttributeNormalizerTest;
import com.b2international.snowowl.semanticengine.normalform.test.ConceptDefinitionAttributeRedundancyFilterTest;
import com.b2international.snowowl.semanticengine.normalform.test.ConceptDefinitionMergerTest;
import com.b2international.snowowl.semanticengine.normalform.test.ConceptDefinitionNormalizerTest;
import com.b2international.snowowl.semanticengine.normalform.test.ExpressionCanonicalRepresentationTest;
import com.b2international.snowowl.semanticengine.normalform.test.FocusConceptNormalizerTest;
import com.b2international.snowowl.semanticengine.normalform.test.GroupMergerTest;
import com.b2international.snowowl.semanticengine.normalform.test.NormalFormGeneratorTest;
import com.b2international.snowowl.semanticengine.normalform.test.NormalFormGeneratorTestFromSnomedDocument;
import com.b2international.snowowl.semanticengine.normalform.test.UngroupedAttributesMergerTest;
import com.b2international.snowowl.semanticengine.utils.test.ContextWrapperBuilderTest;
import com.b2international.snowowl.semanticengine.utils.test.SemanticUtilsTest;
import com.b2international.snowowl.test.commons.SnowOwlAppRule;

/**
 * Test suite for all {@link NormalFormGenerator} related unit tests.
 * <br/>
 * <i>Note: run with at least 1G heap size.</i>
 * 
 * @since 3.3
 */
@RunWith(Suite.class)
@SuiteClasses({
	AttributeNormalizerTest.class,
	ConceptDefinitionAttributeRedundancyFilterTest.class, 
	ConceptDefinitionMergerTest.class,
	ConceptDefinitionNormalizerTest.class,
	ExpressionCanonicalRepresentationTest.class,
	FocusConceptNormalizerTest.class,
	GroupMergerTest.class,
	NormalFormGeneratorTest.class,
	NormalFormGeneratorTestFromSnomedDocument.class,
	UngroupedAttributesMergerTest.class,
	ContextWrapperBuilderTest.class,
	SemanticUtilsTest.class
})
public class AllSemanticEngineTests {

	@ClassRule
	public static final SnowOwlAppRule appRule = SnowOwlAppRule.snowOwl(AllSemanticEngineTests.class);
	
}
