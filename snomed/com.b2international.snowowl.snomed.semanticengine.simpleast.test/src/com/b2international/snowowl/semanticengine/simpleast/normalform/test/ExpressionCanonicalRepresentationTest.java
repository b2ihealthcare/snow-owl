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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.b2international.snowowl.semanticengine.simpleast.test.utils.TestUtils;
import com.b2international.snowowl.snomed.ecl.ecl.ExpressionConstraint;

public class ExpressionCanonicalRepresentationTest {
	
	private static final String EXPRESSION_1 = "64572001:{116676008=72704001,363698007=71341001}";
	private static final String EXPRESSION_1_SHUFFLED = "64572001:{363698007=71341001,116676008=72704001}";
	private static final String EXPRESSION_2 = "71388002:" +
			"{260686004=129304002,363704007=(15497006:272741003=7771000)}" +
			"{260686004=129304002,363704007=(31435000:272741003=7771000)}";
	private static final String EXPRESSION_2_SHUFFLED = "71388002:" +
			"{260686004=129304002,363704007=(31435000:272741003=7771000)}" +
			"{260686004=129304002,363704007=(15497006:272741003=7771000)}";
	private static final String EXPRESSION_3 = "195967001 OR 7771000";
	private static final String EXPRESSION_3_SHUFFLED = "7771000 OR 195967001";
	
	@Test
	public void testGroupedAttributeSorting() {
		ExpressionConstraint expression = TestUtils.parseExpression(EXPRESSION_1);
		ExpressionConstraint expressionShuffled = TestUtils.parseExpression(EXPRESSION_1_SHUFFLED);
		assertEquals(expression.toString(), expressionShuffled.toString());
	}
	
	@Test
	public void testAttributeGroupSorting() {
		ExpressionConstraint expression = TestUtils.parseExpression(EXPRESSION_2);
		ExpressionConstraint expressionShuffled = TestUtils.parseExpression(EXPRESSION_2_SHUFFLED);
		assertEquals(expression.toString(), expressionShuffled.toString());
	}
	
	@Test
	public void testFocusConceptSorting() {
		ExpressionConstraint expression = TestUtils.parseExpression(EXPRESSION_3);
		ExpressionConstraint expressionShuffled = TestUtils.parseExpression(EXPRESSION_3_SHUFFLED);
		assertEquals(expression.toString(), expressionShuffled.toString());
	}
}
