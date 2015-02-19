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

import java.util.Collections;

import org.junit.Assert;
import org.junit.Test;

import com.b2international.snowowl.dsl.scg.Attribute;
import com.b2international.snowowl.dsl.scg.Concept;
import com.b2international.snowowl.dsl.scg.Expression;
import com.b2international.snowowl.dsl.scg.Group;
import com.b2international.snowowl.semanticengine.utils.ContextWrapperBuilder;
import com.b2international.snowowl.semanticengine.utils.ScgBuilderUtils;
import com.b2international.snowowl.semanticengine.utils.SemanticUtils;

public class ContextWrapperBuilderTest {
	
	private static final String BODY_WEIGHT_ID = "27113001";
	private static final String DEFAULT_SUBJECT_RELATIONSHIP_VALUE_ID = "33333333";
	private static final String DEFAULT_TEMPORAL_CONTEXT_VALUE_ID = "22222222";
	private static final String DEFAULT_FINDING_CONTEXT_VALUE_ID = "11111111";

	@Test
	public void testCompleteWrapper() {
		ContextWrapperBuilder contextWrapperBuilder = new ContextWrapperBuilder();
		Concept concept = ScgBuilderUtils.buildConcept(BODY_WEIGHT_ID); // body weight
		Expression clinicalKernelExpression = ScgBuilderUtils.buildExpression(concept);
		Concept situationWithExplicitContextConcept = ScgBuilderUtils.buildConcept(SemanticUtils.SITUATION_WITH_EXPLICIT_CONTEXT_ID);
		Attribute findingContextAttribute = ScgBuilderUtils.buildAttribute(SemanticUtils.FINDING_CONTEXT_ID, DEFAULT_FINDING_CONTEXT_VALUE_ID);
		Attribute temporalContextAttribute = ScgBuilderUtils.buildAttribute(SemanticUtils.TEMPORAL_CONTEXT_ID, DEFAULT_TEMPORAL_CONTEXT_VALUE_ID);
		Attribute subjectRelationshipContextAttribute = ScgBuilderUtils.buildAttribute(SemanticUtils.SUBJECT_RELATIONSHIP_CONTEXT_ID, DEFAULT_SUBJECT_RELATIONSHIP_VALUE_ID);
		Group group = ScgBuilderUtils.buildGroup(contextWrapperBuilder.buildAssociatedFindingAttribute(clinicalKernelExpression),
				findingContextAttribute, temporalContextAttribute, subjectRelationshipContextAttribute); 
		Expression incompleteContextWrapperExpression = ScgBuilderUtils.buildExpression(
				Collections.singletonList(situationWithExplicitContextConcept),	
				Collections.singletonList(group), Collections.<Attribute>emptyList());
		
		Assert.assertTrue(ContextWrapperBuilder.hasSomeContextWrapper(incompleteContextWrapperExpression));
		Assert.assertTrue(ContextWrapperBuilder.hasCompleteContextWrapper(incompleteContextWrapperExpression));
		Expression completeContextWrapperExpression = contextWrapperBuilder.ensureCompleteContextWrapper(incompleteContextWrapperExpression);
		Assert.assertTrue(ContextWrapperBuilder.hasCompleteContextWrapper(completeContextWrapperExpression));
		String findingContextValueId = SemanticUtils.getAttributeValueId(completeContextWrapperExpression, SemanticUtils.FINDING_CONTEXT_ID);
		String temporalContextValueId = SemanticUtils.getAttributeValueId(completeContextWrapperExpression, SemanticUtils.TEMPORAL_CONTEXT_ID);
		String subjectRelContextValueId = SemanticUtils.getAttributeValueId(completeContextWrapperExpression, SemanticUtils.SUBJECT_RELATIONSHIP_CONTEXT_ID);
		Assert.assertEquals(DEFAULT_FINDING_CONTEXT_VALUE_ID, findingContextValueId);
		Assert.assertEquals(DEFAULT_TEMPORAL_CONTEXT_VALUE_ID, temporalContextValueId);
		Assert.assertEquals(DEFAULT_SUBJECT_RELATIONSHIP_VALUE_ID, subjectRelContextValueId);
	}

	@Test
	public void testLessIncompleteWrapper() {
		ContextWrapperBuilder contextWrapperBuilder = new ContextWrapperBuilder();
		Concept concept = ScgBuilderUtils.buildConcept(BODY_WEIGHT_ID); // body weight
		Expression clinicalKernelExpression = ScgBuilderUtils.buildExpression(concept);
		Concept situationWithExplicitContextConcept = ScgBuilderUtils.buildConcept(SemanticUtils.SITUATION_WITH_EXPLICIT_CONTEXT_ID);
		Attribute findingContextAttribute = contextWrapperBuilder.buildDefaultFindingContextAttribute();
		String testConceptId = "123456789";
		findingContextAttribute.setValue(ScgBuilderUtils.buildConcept(testConceptId));
		Group group = ScgBuilderUtils.buildGroup(contextWrapperBuilder.buildAssociatedFindingAttribute(clinicalKernelExpression),
				findingContextAttribute); 
		Expression incompleteContextWrapperExpression = ScgBuilderUtils.buildExpression(
				Collections.singletonList(situationWithExplicitContextConcept),	
				Collections.singletonList(group), Collections.<Attribute>emptyList());
		
		Assert.assertTrue(ContextWrapperBuilder.hasSomeContextWrapper(incompleteContextWrapperExpression));
		Assert.assertFalse(ContextWrapperBuilder.hasCompleteContextWrapper(incompleteContextWrapperExpression));
		Expression completeContextWrapperExpression = contextWrapperBuilder.ensureCompleteContextWrapper(incompleteContextWrapperExpression);
		Assert.assertTrue(ContextWrapperBuilder.hasCompleteContextWrapper(completeContextWrapperExpression));
		String findingContextValueId = SemanticUtils.getAttributeValueId(completeContextWrapperExpression, SemanticUtils.FINDING_CONTEXT_ID);
		Assert.assertEquals(testConceptId, findingContextValueId);
	}
	
	@Test
	public void testIncompleteWrapper() {
		ContextWrapperBuilder contextWrapperBuilder = new ContextWrapperBuilder();
		Concept concept = ScgBuilderUtils.buildConcept(BODY_WEIGHT_ID); // body weight
		Expression clinicalKernelExpression = ScgBuilderUtils.buildExpression(concept);
		Concept situationWithExplicitContextConcept = ScgBuilderUtils.buildConcept(SemanticUtils.SITUATION_WITH_EXPLICIT_CONTEXT_ID);
		Group group = ScgBuilderUtils.buildGroup(contextWrapperBuilder.buildAssociatedFindingAttribute(clinicalKernelExpression)); 
		Expression incompleteContextWrapperExpression = ScgBuilderUtils.buildExpression(
				Collections.singletonList(situationWithExplicitContextConcept),	
				Collections.singletonList(group), Collections.<Attribute>emptyList());
		
		Assert.assertTrue(ContextWrapperBuilder.hasSomeContextWrapper(incompleteContextWrapperExpression));
		Assert.assertFalse(ContextWrapperBuilder.hasCompleteContextWrapper(incompleteContextWrapperExpression));
		Expression completeContextWrapperExpression = contextWrapperBuilder.ensureCompleteContextWrapper(incompleteContextWrapperExpression);
		Assert.assertTrue(ContextWrapperBuilder.hasCompleteContextWrapper(completeContextWrapperExpression));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testMismatchedWrapper1() {
		ContextWrapperBuilder contextWrapperBuilder = new ContextWrapperBuilder();
		Concept concept = ScgBuilderUtils.buildConcept(BODY_WEIGHT_ID); // body weight
		Expression clinicalKernelExpression = ScgBuilderUtils.buildExpression(concept);
		Concept situationWithExplicitContextConcept = ScgBuilderUtils.buildConcept(SemanticUtils.SITUATION_WITH_EXPLICIT_CONTEXT_ID);
		Group group = ScgBuilderUtils.buildGroup(contextWrapperBuilder.buildAssociatedFindingAttribute(clinicalKernelExpression),
				contextWrapperBuilder.buildDefaultProcedureContextAttribute()); 
		Expression incompleteContextWrapperExpression = ScgBuilderUtils.buildExpression(
				Collections.singletonList(situationWithExplicitContextConcept),	
				Collections.singletonList(group), Collections.<Attribute>emptyList());
		
		Assert.assertTrue(ContextWrapperBuilder.hasSomeContextWrapper(incompleteContextWrapperExpression));
		Assert.assertFalse(ContextWrapperBuilder.hasCompleteContextWrapper(incompleteContextWrapperExpression));
		contextWrapperBuilder.ensureCompleteContextWrapper(incompleteContextWrapperExpression);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testMismatchedWrapper2() {
		ContextWrapperBuilder contextWrapperBuilder = new ContextWrapperBuilder();
		Concept concept = ScgBuilderUtils.buildConcept(BODY_WEIGHT_ID); // body weight
		Expression clinicalKernelExpression = ScgBuilderUtils.buildExpression(concept);
		Concept situationWithExplicitContextConcept = ScgBuilderUtils.buildConcept(SemanticUtils.SITUATION_WITH_EXPLICIT_CONTEXT_ID);
		Group group = ScgBuilderUtils.buildGroup(contextWrapperBuilder.buildAssociatedProcedureAttribute(clinicalKernelExpression),
				contextWrapperBuilder.buildDefaultFindingContextAttribute()); 
		Expression incompleteContextWrapperExpression = ScgBuilderUtils.buildExpression(
				Collections.singletonList(situationWithExplicitContextConcept),	
				Collections.singletonList(group), Collections.<Attribute>emptyList());
		
		Assert.assertTrue(ContextWrapperBuilder.hasSomeContextWrapper(incompleteContextWrapperExpression));
		Assert.assertFalse(ContextWrapperBuilder.hasCompleteContextWrapper(incompleteContextWrapperExpression));
		contextWrapperBuilder.ensureCompleteContextWrapper(incompleteContextWrapperExpression);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testWithClinicalKernelOnly() {
		ContextWrapperBuilder contextWrapperBuilder = new ContextWrapperBuilder();
		Concept concept = ScgBuilderUtils.buildConcept(BODY_WEIGHT_ID); // body weight
		Expression clinicalKernelExpression = ScgBuilderUtils.buildExpression(concept);
		Assert.assertFalse(ContextWrapperBuilder.hasSomeContextWrapper(clinicalKernelExpression));
		Assert.assertFalse(ContextWrapperBuilder.hasCompleteContextWrapper(clinicalKernelExpression));
		contextWrapperBuilder.ensureCompleteContextWrapper(clinicalKernelExpression);
	}
	
}
