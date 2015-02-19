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
package com.b2international.snowowl.semanticengine.utils.test;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.b2international.snowowl.dsl.SCGStandaloneSetup;
import com.b2international.snowowl.dsl.scg.Expression;
import com.b2international.snowowl.semanticengine.utils.SemanticUtils;
import com.google.common.collect.Lists;

/**
 *
 */
public class SemanticUtilsTest {

	@Test
	public void testContextAttributeExtractor() {
		doTestContextAttributeExtractor("243796009:363589002=" +
				"17724006" + 
				",405813007=88727008" +
				",408729009=410515003" +
				",408731000=410512000" +
				",408732007=410604004", "410515003", "410512000", "410604004", null);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testContextAttributeExtractorWithExpressionValue() {
		doTestContextAttributeExtractor("243796009:363589002=" +
				"17724006" + 
				",405813007=88727008" +
				",408729009=410515003" +
				",408731000=(410512000)" +
				",408732007=410604004", "410515003", "410512000", "410604004", null);
	}
	
	@Test
	public void testClinicalKernelExtractorWithSimpleKernel() {
		doTestClinicalKernelExtractor("243796009:363589002=" +
				"17724006" + 
				",405813007=88727008" +
				",408729009=410515003" +
				",408731000=410512000" +
				",408732007=410604004", Lists.newArrayList("17724006"));
	}
	
	@Test
	public void testClinicalKernelExtractorWithComplexKernel() {
		doTestClinicalKernelExtractor("243796009:363589002=" +
				"(17724006+73211009:405813007=88727008)" + 
				",405813007=88727008" +
				",408729009=410515003" +
				",408731000=410512000" +
				",408732007=410604004", Lists.newArrayList("17724006", "73211009"));
	}
	
	private void doTestContextAttributeExtractor(String expressionString, String expectedFindingContext, String expectedTemporalContext,
			String expectedSubjectRelationshipContext, String expectedProcedureContext) {
		Expression expression = (Expression) SCGStandaloneSetup.parse(expressionString);
		String findingContext = SemanticUtils.getAttributeValueId(expression, SemanticUtils.FINDING_CONTEXT_ID);
		Assert.assertEquals(expectedFindingContext, findingContext);
		String temporalContext = SemanticUtils.getAttributeValueId(expression, SemanticUtils.TEMPORAL_CONTEXT_ID);
		Assert.assertEquals(expectedTemporalContext, temporalContext);
		String subjectRelationshipContext = SemanticUtils.getAttributeValueId(expression, SemanticUtils.SUBJECT_RELATIONSHIP_CONTEXT_ID);
		Assert.assertEquals(expectedSubjectRelationshipContext, subjectRelationshipContext);
		String procContext = SemanticUtils.getAttributeValueId(expression, SemanticUtils.PROCEDURE_CONTEXT_ID);
		Assert.assertEquals(expectedProcedureContext, procContext);
	}
	
	private void doTestClinicalKernelExtractor(String expressionString, List<String> expectedFocusConceptIds) {
		Expression expression = (Expression) SCGStandaloneSetup.parse(expressionString);
		List<String> clinicalKernelFocusConceptIds = SemanticUtils.getClinicalKernelFocusConceptIds(expression);
		Assert.assertFalse(clinicalKernelFocusConceptIds.isEmpty());
		Assert.assertEquals(expectedFocusConceptIds, clinicalKernelFocusConceptIds);
	}
}
