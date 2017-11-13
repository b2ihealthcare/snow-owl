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
package com.b2international.snowowl.semanticengine.simpleast.test.utils;

import java.io.StringReader;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.ComparisonFailure;

import com.b2international.snowowl.semanticengine.simpleast.normalform.AttributeClauseList;
import com.b2international.snowowl.semanticengine.simpleast.normalform.ConceptDefinition;
import com.b2international.snowowl.semanticengine.simpleast.utils.ConceptDefinitionComparator;
import com.b2international.snowowl.semanticengine.simpleast.utils.ExpressionComparator;
import com.b2international.snowowl.semanticengine.simpleast.utils.QueryAstUtils;
import com.b2international.snowowl.snomed.dsl.query.queryast.AttributeClause;
import com.b2international.snowowl.snomed.dsl.query.queryast.ConceptRef;
import com.b2international.snowowl.snomed.dsl.query.queryast.RValue;
import com.b2international.snowowl.snomed.dsl.query.queryast.SubExpression;
import com.b2international.snowowl.snomed.dsl.query.queryast.ecoreastFactory;
import com.b2international.snowowl.snomed.ecl.EclStandaloneSetup;
import com.b2international.snowowl.snomed.ecl.ecl.ExpressionConstraint;
import com.b2international.snowowl.snomed.ecl.parser.antlr.EclParser;
import com.google.common.collect.Lists;
import com.google.inject.Injector;

/**
 * Various utility functions for testing.
 * 
 */
public class TestUtils {

	private static EclParser parser;

	public static ExpressionConstraint parseExpression(String expression) {
		if (expression == null)
			throw new NullPointerException("Expression string cannot be null.");
		return (ExpressionConstraint) parser.parse(new StringReader(expression)).getRootASTElement();
	}
	
	public static EclParser createESCGParser() {
		if (parser == null) {
			final Injector injector = new EclStandaloneSetup().createInjectorAndDoEMFRegistration();
			parser = injector.getInstance(EclParser.class);
		}
		return parser;
	}
	public static void assertConceptDefinitionsEqual(ConceptDefinition expectedConceptDefinition,
			ConceptDefinition actualConceptDefinition) {
		ConceptDefinitionComparator conceptDefinitionComparator = new ConceptDefinitionComparator();
		boolean equal = conceptDefinitionComparator.equal(expectedConceptDefinition, actualConceptDefinition);
		
		if (!equal) {
			throw new ComparisonFailure("Concept definitions are not equal.", expectedConceptDefinition.toString(), actualConceptDefinition.toString());
		}
	}
	
//	public static void assertExpressionsEqual(String message, Expression expected, Expression actual) {
//		ExpressionComparator expressionComparator = new ExpressionComparator();
//		boolean equal = expressionComparator.equal(expected, actual);
//		if (!equal)
//			throw new ComparisonFailure(message, expected.toString(), actual.toString());
//	}


	public static AttributeClauseList buildAttributeClauseList(Collection<AttributeClause> attributes) {
		AttributeClauseList attributeClauseList = new AttributeClauseList();
		attributeClauseList.getAttributeClauses().addAll(attributes);
		return attributeClauseList;
	}
	
	public static AttributeClauseList buildAttributeClauseList(AttributeClause... attributes) {
		return buildAttributeClauseList(Arrays.asList(attributes));
	}
	
	public static AttributeClause buildAttribute(String nameConceptId, String valueConceptId) {
		ConceptRef nameConceptGroup = buildUnconstrainedConceptGroup(nameConceptId);
		ConceptRef valueConceptGroup = buildUnconstrainedConceptGroup(valueConceptId);
		AttributeClause attribute = ecoreastFactory.eINSTANCE.createAttributeClause();
		attribute.setLeft(nameConceptGroup);
		attribute.setRight(valueConceptGroup);
		return attribute;
	}
	
	public static AttributeClause buildAttributeWithExpressionValue(String nameConceptId, String valueConceptId) {
		ConceptRef nameConceptGroup = buildUnconstrainedConceptGroup(nameConceptId);
		SubExpression valueNegatableSubExpression = buildSubExpressionFromSingleConceptId(valueConceptId);
		AttributeClause attribute = ecoreastFactory.eINSTANCE.createAttributeClause();
		attribute.setLeft(nameConceptGroup);
		attribute.setRight(valueNegatableSubExpression);
		return attribute;
	}
	
	public static AttributeClause buildAttribute(String nameConceptId, RValue valueExpression) {
		ConceptRef nameConceptGroup = buildUnconstrainedConceptGroup(nameConceptId);
		SubExpression valueNegatableSubExpression = ecoreastFactory.eINSTANCE.createSubExpression();
		valueNegatableSubExpression.setValue(valueExpression);
		AttributeClause attribute = ecoreastFactory.eINSTANCE.createAttributeClause();
		attribute.setLeft(nameConceptGroup);
		attribute.setRight(valueNegatableSubExpression);
		return attribute;
	}
	
	public static RValue buildExpression(ConceptRef focusConcept) {
		return buildExpression(Collections.singletonList(focusConcept), Collections.<AttributeClauseList>emptyList(), 
				Collections.<AttributeClause>emptyList());
	}
	
	public static RValue buildExpression(List<ConceptRef> focusConcepts, List<AttributeClauseList> attributeGroups, 
			List<AttributeClause> ungroupedAttributes) {
		List<String> focusConceptIds = Lists.newArrayList();
		for (ConceptRef focusConcept : focusConcepts) {
			focusConceptIds.add(focusConcept.getConceptId());
		}
		return QueryAstUtils.buildExpression(focusConceptIds, attributeGroups, ungroupedAttributes);
	}
	
	public static SubExpression buildSubExpressionFromSingleConceptId(String conceptId) {
		ConceptRef conceptGroup = buildUnconstrainedConceptGroup(conceptId);
		SubExpression negatableSubExpression = ecoreastFactory.eINSTANCE.createSubExpression();
//		negatableSubExpression.setValue(expression);
		// TODO: is this needed?
		return negatableSubExpression;
	}
	
	public static ConceptRef buildConcept(String conceptId) {
		ConceptRef concept = ecoreastFactory.eINSTANCE.createConceptRef();
		concept.setConceptId(conceptId);
		return concept;
	}
	
	public static ConceptRef buildUnconstrainedConceptGroup(String conceptId) {
		ConceptRef concept = ecoreastFactory.eINSTANCE.createConceptRef();
		concept.setConceptId(conceptId);
		return concept;
	}
	
	public static ConceptDefinition buildConceptDefinition(Collection<AttributeClauseList> attributeGroups, 
			Collection<AttributeClause> ungroupedAttributes) {
		ConceptDefinition conceptDefinition = new ConceptDefinition();
		conceptDefinition.getAttributeClauseLists().addAll(attributeGroups);
		conceptDefinition.getUngroupedAttributes().addAll(ungroupedAttributes);
		return conceptDefinition;
	}

	public static ConceptDefinition buildConceptDefinition(AttributeClauseList attributeGroup) {
		return buildConceptDefinition(Collections.singleton(attributeGroup), Collections.<AttributeClause>emptySet());
	}
	
	public static ConceptDefinition buildConceptDefinition(AttributeClauseList... attributeGroups) {
		return buildConceptDefinition(Arrays.asList(attributeGroups), Collections.<AttributeClause>emptySet());
	}
	
	public static ConceptDefinition buildConceptDefinition(AttributeClause... attributes) {
		return buildConceptDefinition(Collections.<AttributeClauseList>emptySet(), Arrays.asList(attributes));
	}

	public static void assertExpressionsEqual(String string, RValue expected,
			RValue actual) {
		ExpressionComparator conceptDefinitionComparator = new ExpressionComparator();
		boolean equal = conceptDefinitionComparator.equal(expected, actual);
		
		if (!equal) {
			throw new ComparisonFailure("Expressions are not equal.", expected.toString(), actual.toString());
		}
	}
}
