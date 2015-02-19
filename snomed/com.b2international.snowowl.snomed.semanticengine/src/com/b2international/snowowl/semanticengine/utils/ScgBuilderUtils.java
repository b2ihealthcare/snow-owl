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
package com.b2international.snowowl.semanticengine.utils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import com.b2international.snowowl.dsl.scg.Attribute;
import com.b2international.snowowl.dsl.scg.Concept;
import com.b2international.snowowl.dsl.scg.Expression;
import com.b2international.snowowl.dsl.scg.Group;
import com.b2international.snowowl.dsl.scg.ScgFactory;
import com.b2international.snowowl.semanticengine.normalform.ConceptDefinition;

/**
 * Collection of utility methods for building SCG {@link Expression}s, {@link Attribute}s, {@link Group}s and {@link Concept}s.
 * 
 */
public class ScgBuilderUtils {

	public static Group buildGroup(Collection<Attribute> attributes) {
		Group attributeGroup = ScgFactory.eINSTANCE.createGroup();
		attributeGroup.getAttributes().addAll(attributes);
		return attributeGroup;
	}

	public static Group buildGroup(Attribute... attributes) {
		return buildGroup(Arrays.asList(attributes));
	}

	public static Attribute buildAttribute(String nameConceptId, String valueConceptId) {
		Concept nameConcept = buildConcept(nameConceptId);
		Concept valueConcept = buildConcept(valueConceptId);
		Attribute attribute = ScgFactory.eINSTANCE.createAttribute();
		attribute.setName(nameConcept);
		attribute.setValue(valueConcept);
		return attribute;
	}

	public static Attribute buildAttributeWithExpressionValue(String nameConceptId, String valueConceptId) {
		Concept nameConcept = buildConcept(nameConceptId);
		Expression valueExpression = buildExpressionFromSingleConceptId(valueConceptId);
		Attribute attribute = ScgFactory.eINSTANCE.createAttribute();
		attribute.setName(nameConcept);
		attribute.setValue(valueExpression);
		return attribute;
	}

	public static Attribute buildAttribute(String nameConceptId, Expression valueExpression) {
		Concept nameConcept = buildConcept(nameConceptId);
		Attribute attribute = ScgFactory.eINSTANCE.createAttribute();
		attribute.setName(nameConcept);
		attribute.setValue(valueExpression);
		return attribute;
	}

	public static Expression buildExpression(Concept lValue) {
		return buildExpression(Collections.singleton(lValue), Collections.<Group>emptySet(), 
				Collections.<Attribute>emptySet());
	}

	public static Expression buildExpression(Collection<Concept> lValues, Collection<Group> attributeGroups, 
			Collection<Attribute> ungroupedAttributes) {
		ScgFactory factory = ScgFactory.eINSTANCE;
		Expression expression = factory.createExpression();
		
		expression.getConcepts().addAll(lValues);
		expression.getAttributes().addAll(ungroupedAttributes);
		expression.getGroups().addAll(attributeGroups);
		
		return expression;
	}

	public static Expression buildExpressionFromSingleConceptId(String conceptId) {
		Concept conceptGroup = buildConcept(conceptId);
		Expression expression = ScgFactory.eINSTANCE.createExpression();
		expression.getConcepts().add(conceptGroup);
		return expression;
	}

	public static Concept buildConcept(String conceptId) {
		Concept concept = ScgFactory.eINSTANCE.createConcept();
		concept.setId(conceptId);
		return concept;
	}

	public static ConceptDefinition buildConceptDefinition(Collection<Group> attributeGroups, 
			Collection<Attribute> ungroupedAttributes) {
		ConceptDefinition conceptDefinition = new ConceptDefinition();
		conceptDefinition.getGroups().addAll(attributeGroups);
		conceptDefinition.getUngroupedAttributes().addAll(ungroupedAttributes);
		return conceptDefinition;
	}

	public static ConceptDefinition buildConceptDefinition(Group attributeGroup) {
		return buildConceptDefinition(Collections.singleton(attributeGroup), Collections.<Attribute>emptySet());
	}

	public static ConceptDefinition buildConceptDefinition(Group... attributeGroups) {
		return buildConceptDefinition(Arrays.asList(attributeGroups), Collections.<Attribute>emptySet());
	}

	public static ConceptDefinition buildConceptDefinition(Attribute... attributes) {
		return buildConceptDefinition(Collections.<Group>emptySet(), Arrays.asList(attributes));
	}

}