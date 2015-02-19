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

import java.io.StringReader;
import java.util.Collection;

import org.junit.Test;

import com.b2international.snowowl.dsl.ESCGEcoreRewriter;
import com.b2international.snowowl.dsl.parser.antlr.ESCGParser;
import com.b2international.snowowl.semanticengine.simpleast.test.utils.TestUtils;
import com.b2international.snowowl.semanticengine.simpleast.utils.QueryAstUtils;
import com.b2international.snowowl.snomed.dsl.query.queryast.AttributeClause;
import com.b2international.snowowl.snomed.dsl.query.queryast.AttributeClauseGroup;
import com.b2international.snowowl.snomed.dsl.query.queryast.ConceptRef;
import com.b2international.snowowl.snomed.dsl.query.queryast.RValue;

public class QueryAstUtilsTest {

	private static final ESCGParser escgParser = TestUtils.createESCGParser();
	
	@Test
	public void testGetFocusConcepts() {
		String expression = "243796009 | situation with explicit context | + 71388002 | Procedure |:"+
				"{ 363589002 | associated procedure | = 71388002 | Procedure | "+
				", 408730004 | procedure context | =398166005 | Performed |"+
				", 408731000 | temporal context | = <<410584005 | Current - specified |"+
				", 408732007 | subject relationship context | =410604004 | Subject of record |"+
				"}";
		ESCGEcoreRewriter rewriter = new ESCGEcoreRewriter(escgParser);
		RValue parsedExpression = rewriter.parse(new StringReader(expression));
		Collection<ConceptRef> focusConcepts = QueryAstUtils.getFocusConcepts(parsedExpression);
		System.out.println(focusConcepts);
	}
	
	@Test
	public void testGetFocusConceptsMultipleGroupsAndUngrouped() {
		String expression = "243796009 | situation with explicit context | + 71388002 | Procedure |:"+
				" 12345678 | test | = 302497006|Haemodialysis|"+
				"{ 363589002 | associated procedure | = 71388002 | Procedure | "+
				", 408730004 | procedure context | =398166005 | Performed |}"+
				"{ 408731000 | temporal context | = <<410584005 | Current - specified |"+
				", 408732007 | subject relationship context | =410604004 | Subject of record |"+
				"}";
		ESCGEcoreRewriter rewriter = new ESCGEcoreRewriter(escgParser);
		RValue parsedExpression = rewriter.parse(new StringReader(expression));
		Collection<ConceptRef> focusConcepts = QueryAstUtils.getFocusConcepts(parsedExpression);
		System.out.println(focusConcepts);
	}
	
	@Test
	public void testGetUngroupedAttributes() {
		String expression = "37931006 | auscultation | :" +
				"260686004 | method | = 129436005 | auscultation - action |";
		ESCGEcoreRewriter rewriter = new ESCGEcoreRewriter(escgParser);
		RValue parsedExpression = rewriter.parse(new StringReader(expression));
		Collection<AttributeClause> ungroupedAttributes = QueryAstUtils.getUngroupedAttributes(parsedExpression);
		System.out.println(ungroupedAttributes);
	}
	
	@Test
	public void testGetAttributeGroups() {
		String expression = "243796009:{246090004=("+
				"14560005:116676008=111214005"+
				",363714003=386053000),"+
				"408729009=410515003"+
				",408731000=410512000"+
				",408732007=125676002}";
		ESCGEcoreRewriter rewriter = new ESCGEcoreRewriter(escgParser);
		RValue parsedExpression = rewriter.parse(new StringReader(expression));
		Collection<AttributeClauseGroup> ungroupedAttributes = QueryAstUtils.getAttributeGroups(parsedExpression);
		System.out.println(ungroupedAttributes);
	}
}
