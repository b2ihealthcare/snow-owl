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
import java.util.HashSet;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.util.EcoreUtil;

import com.b2international.commons.tree.emf.EObjectWalker;
import com.b2international.snowowl.dsl.scg.Attribute;
import com.b2international.snowowl.dsl.scg.AttributeValue;
import com.b2international.snowowl.dsl.scg.Concept;
import com.b2international.snowowl.dsl.scg.Expression;
import com.b2international.snowowl.dsl.scg.Group;
import com.b2international.snowowl.dsl.scg.ScgFactory;
import com.b2international.snowowl.semanticengine.normalform.AttributeNameMatch;
import com.b2international.snowowl.semanticengine.normalform.ConceptDefinition;
import com.b2international.snowowl.semanticengine.normalform.ScgExpressionNormalFormGenerator;
import com.b2international.snowowl.semanticengine.subsumption.SubsumptionTester;

/**
 * Collection of utility methods used by the {@link SubsumptionTester} and the {@link ScgExpressionNormalFormGenerator}.
 * 
 */
public final class SemanticUtils {
	
	public static final String KNOWN_PRESENT_ID = "410515003";
	public static final String CURRENT_ID = "15240007";
	public static final String PERFORMED_ID = "398166005";
	public static final String SUBJECT_OF_RECORD_ID = "410604004";
	public static final String ASSOCIATED_FINDING_ID = "246090004";
	public static final String ASSOCIATED_PROCEDURE_ID = "363589002";
	public static final String FINDING_CONTEXT_ID = "408729009";
	public static final String PROCEDURE_CONTEXT_ID = "408730004";
	public static final String TEMPORAL_CONTEXT_ID = "408731000";
	public static final String SUBJECT_RELATIONSHIP_CONTEXT_ID = "408732007";
	public static final String SITUATION_WITH_EXPLICIT_CONTEXT_ID = "243796009";

	public static final String ASSOCIATED_MORPHOLOGY_ID = "116676008";
	public static final String FINDING_SITE_ID = "363698007";
	public static final String CAUSATIVE_AGENT_ID = "246075003";
	public static final String HAS_ACTIVE_INGREDIENT_ID = "127489000";
	public static final String HAS_DOSE_FORM_ID = "411116001";
	
	private static final List<String> VALID_CONTEXT_ATTRIBUTE_NAMES = Arrays.asList(ASSOCIATED_FINDING_ID, ASSOCIATED_PROCEDURE_ID,
			FINDING_CONTEXT_ID, PROCEDURE_CONTEXT_ID, TEMPORAL_CONTEXT_ID, SUBJECT_RELATIONSHIP_CONTEXT_ID);
	
	private SemanticUtils() { } // suppress default constructor

	/**
	 * Returns the value of the first occurrence of an attribute with the specified name.
	 * @param expression
	 * @param attributeNameId
	 * @return the value of the first occurrence of an attribute with the specified name
	 */
	public static String getAttributeValueId(Expression expression, String attributeNameId) {
		AttributeValueExtractingVisitor attributeValueExtractor = new AttributeValueExtractingVisitor(attributeNameId);
		EObjectWalker walker = EObjectWalker.createContainmentWalker(attributeValueExtractor);
		walker.walk(expression);
		String extractedAttributeValueId = attributeValueExtractor.getExtractedAttributeValueId();
		return extractedAttributeValueId;
	}
	
	/**
	 * Returns the value of the first occurrence of an attribute with the specified name, traversing 
	 * the SCG expression only to a limited depth.
	 * @param expression
	 * @param attributeNameId
	 * @return the value of the first occurrence of an attribute with the specified name
	 */
	public static Long getAttributeValueIdFromLimitedDepth(Expression expression, String attributeNameId, int maxScgExpressionDepth) {
		LimitedDepthAttributeValueExtractingVisitor attributeValueExtractor = new LimitedDepthAttributeValueExtractingVisitor(
				attributeNameId, maxScgExpressionDepth);
		EObjectWalker walker = EObjectWalker.createContainmentWalker(attributeValueExtractor);
		walker.walk(expression);
		Long extractedAttributeValueId = attributeValueExtractor.getAttributeValueId();
		return extractedAttributeValueId;
	}
	
	/**
	 * Returns the focus concepts of the specified {@link Expression}'s clinical kernel.
	 * @param expression
	 * @return the focus concepts of the specified {@link Expression}'s clinical kernel
	 */
	public static List<String> getClinicalKernelFocusConceptIds(Expression expression) {
		ClinicalKernelFocusConceptExtractingVisitor focusConceptExtractor = new ClinicalKernelFocusConceptExtractingVisitor();
		EObjectWalker walker = EObjectWalker.createContainmentWalker(focusConceptExtractor);
		walker.walk(expression);
		return focusConceptExtractor.getExtractedFocusConceptIds();
	}
	
	public static boolean isClinicalKernelPrimitive(Expression expression) {
		PrimitiveClinicalKernelDetectingVisitor primitiveDetector = new PrimitiveClinicalKernelDetectingVisitor();
		EObjectWalker walker = EObjectWalker.createContainmentWalker(primitiveDetector);
		walker.walk(expression);
		return primitiveDetector.isClinicalKernelPrimitive();
	}
	
	public static boolean isSingleConceptExpression(Expression expression) {
		return expression.getAttributes().isEmpty() && 
				expression.getGroups().isEmpty() && 
				expression.getConcepts().size() == 1;
	}
	
	/**
	 * @param attribute
	 * @return	the attribute {@link Expression} of the specified {@link Attribute}
	 */
	public static Expression getAttributeValueExpression(Attribute attribute) {
		Expression expression = ScgFactory.eINSTANCE.createExpression();
		AttributeValue rValue = attribute.getValue();
		if (rValue instanceof Concept)  {
			Concept valueConcept = (Concept) rValue;
			expression.getConcepts().add(EcoreUtil.copy(valueConcept));
		} else if (rValue instanceof Expression) {
			Expression negatableSubExpression = (Expression) rValue;
			expression.getGroups().addAll(EcoreUtil.copyAll(negatableSubExpression.getGroups()));
			expression.getAttributes().addAll(EcoreUtil.copyAll(negatableSubExpression.getAttributes()));
			EList<Concept> concepts = negatableSubExpression.getConcepts();
			for (Concept lValue : concepts) {
				expression.getConcepts().add(EcoreUtil.copy(lValue));
			}
		}
		
		return expression;
	}

	/**
	 * @param attributeToMatch
	 * @param attributes
	 * @return the attributes from the specified collection, which are name-matched with the specified attribute
	 */
	public static Collection<AttributeNameMatch> findNameMatchedAttributes(Attribute attributeToMatch, Collection<Attribute> attributes) {
		return findNameMatchedAttributes(attributeToMatch.getName(), attributes);
	}
	
	/**
	 * @param attributeNameConceptToMatch
	 * @param attributes
	 * @return the attributes from the specified collection, which are name-matched with the specified concept
	 */
	public static Collection<AttributeNameMatch> findNameMatchedAttributes(Concept attributeNameConceptToMatch, Collection<Attribute> attributes) {
		// use set to avoid duplicates
		Collection<AttributeNameMatch> nameMatchedAttributes = new HashSet<AttributeNameMatch>();
		for (Attribute attribute : attributes) {
			Concept attributeNameConcept = attribute.getName();
			Concept attributeToMatchNameConcept = attributeNameConceptToMatch;
			if (attributeNameConcept.getId().equals(attributeToMatchNameConcept.getId())) {
				nameMatchedAttributes.add(new AttributeNameMatch(attribute));
			}
		}
		
		return Collections.unmodifiableCollection(nameMatchedAttributes);
	}
	
	/**
	 * @param attributeToMatch
	 * @param Group
	 * @return the attributes and their containing groups from the specified group, which are name-matched with the specified attribute
	 */
	public static Collection<AttributeNameMatch> findNameMatchedAttributesInGroups(Concept attributeNameConceptToMatch, Collection<Group> Groups) {
		Collection<AttributeNameMatch> nameMatchedAttributes = new HashSet<AttributeNameMatch>();
		for (Group Group : Groups) {
			nameMatchedAttributes.addAll(findNameMatchedAttributes(attributeNameConceptToMatch, Group));
		}
		return Collections.unmodifiableCollection(nameMatchedAttributes);
	}
	
	/**
	 * @param attributeToMatch
	 * @param Group
	 * @return the attributes and their containing groups from the specified group, which are name-matched with the specified attribute
	 */
	public static Collection<AttributeNameMatch> findNameMatchedAttributes(Concept attributeNameConceptToMatch, Group group) {
		Collection<Attribute> groupedAttributes = group.getAttributes();
		Collection<AttributeNameMatch> attributeNameMatches = findNameMatchedAttributes(attributeNameConceptToMatch, groupedAttributes);
		for (AttributeNameMatch attributeNameMatch : attributeNameMatches) {
			attributeNameMatch.setGroup(group);
		}

		return Collections.unmodifiableCollection(attributeNameMatches);
	}
	
	/**
	 * @param attributeToMatch
	 * @param Group
	 * @return the attributes and their containing groups from the specified group, which are name-matched with the specified attribute
	 */
	public static Collection<AttributeNameMatch> findNameMatchedAttributes(Attribute attributeToMatch, Group Group) {
		return findNameMatchedAttributes(attributeToMatch.getName(), Group);
	}
	
	public static Collection<AttributeNameMatch> findNameMatchedAttributes(Attribute attributeToMatch, ConceptDefinition conceptDefinition) {
		// use set to avoid duplicates
		Collection<AttributeNameMatch> allNameMatchedAttributes = new HashSet<AttributeNameMatch>();
		Collection<AttributeNameMatch> ungroupedNameMatchedAttributes = findNameMatchedAttributes(attributeToMatch, 
				conceptDefinition.getUngroupedAttributes());
		allNameMatchedAttributes.addAll(ungroupedNameMatchedAttributes);
		
		for (Group Group : conceptDefinition.getGroups()) {
			allNameMatchedAttributes.addAll(findNameMatchedAttributes(attributeToMatch, Group));
		}
		
		return Collections.unmodifiableCollection(allNameMatchedAttributes);
	}
	
	public static Collection<AttributeNameMatch> findNameMatchedAttributes(String attributeNameConceptIdToMatch, ConceptDefinition conceptDefinition) {
		// use set to avoid duplicates
		Collection<AttributeNameMatch> allNameMatchedAttributes = new HashSet<AttributeNameMatch>();
		Concept attributeNameConceptToMatch = ScgFactory.eINSTANCE.createConcept();
		attributeNameConceptToMatch.setId(attributeNameConceptIdToMatch);
		Collection<AttributeNameMatch> ungroupedNameMatchedAttributes = findNameMatchedAttributes(attributeNameConceptToMatch, 
				conceptDefinition.getUngroupedAttributes());
		allNameMatchedAttributes.addAll(ungroupedNameMatchedAttributes);
		
		for (Group Group : conceptDefinition.getGroups()) {
			allNameMatchedAttributes.addAll(findNameMatchedAttributes(attributeNameConceptToMatch, Group));
		}
		
		return Collections.unmodifiableCollection(allNameMatchedAttributes);
	}
	
	public static Collection<AttributeNameMatch> findNameMatchedAttributesInConceptDefinitons(Attribute attributeToMatch, 
			Collection<ConceptDefinition> conceptDefinitions) {
		// use set to avoid duplicates
		Collection<AttributeNameMatch> nameMatchedAttributes = new HashSet<AttributeNameMatch>();
		for (ConceptDefinition conceptDefinition : conceptDefinitions) {
			nameMatchedAttributes.addAll(findNameMatchedAttributes(attributeToMatch, conceptDefinition));
		}
		
		return Collections.unmodifiableCollection(nameMatchedAttributes);
	}
	
	public static Collection<AttributeNameMatch> getGroupedAttributeNameMatches(Collection<AttributeNameMatch> matches) {
		Collection<AttributeNameMatch> groupedAttributeNameMatches = new HashSet<AttributeNameMatch>();
		for (AttributeNameMatch attributeNameMatch : matches) {
			if (attributeNameMatch.getGroup() != AttributeNameMatch.NO_GROUP)
				groupedAttributeNameMatches.add(attributeNameMatch);
		}
		
		return Collections.unmodifiableCollection(groupedAttributeNameMatches);
	}
	
	public static Collection<AttributeNameMatch> getUngroupedAttributeNameMatches(Collection<AttributeNameMatch> matches) {
		Collection<AttributeNameMatch> ungroupedAttributeNameMatches = new HashSet<AttributeNameMatch>();
		for (AttributeNameMatch attributeNameMatch : matches) {
			if (attributeNameMatch.getGroup() == AttributeNameMatch.NO_GROUP)
				ungroupedAttributeNameMatches.add(attributeNameMatch);
		}
		
		return Collections.unmodifiableCollection(ungroupedAttributeNameMatches);
	}
	
	public static boolean isLateralizable(Concept concept) {
		throw new UnsupportedOperationException("Not implemented yet.");
	}
	
	/**
	 * The only valid context attributes are:<br/>
	 * "associated finding" (246090004), "associated procedure" (363589002), "finding context" (2470590016), 
	 * "procedure context" (2470591017), "temporal context" (2470592012) and "subject relationship context" (2470593019).
	 * 
	 * @param attribute
	 * @return true if the attribute name is one of the valid concepts, false otherwise
	 */
	public static boolean isValidContextAttribute(Attribute attribute) {
		Concept attributeNameConcept = attribute.getName();
		return VALID_CONTEXT_ATTRIBUTE_NAMES.contains(attributeNameConcept.getId());
	}
	
	public static boolean isNullOrEmpty(Collection<?> collection) {
		return collection == null || collection.isEmpty();
	}
	
	/**
	 * @param valueNormalFormExpression
	 * @return {@link ConceptGroup} if the specified expression only has a single focus concept, {@link NegatableSubExpression} otherwise
	 */
	public static AttributeValue buildRValue(Expression valueNormalFormExpression) {
		if (valueNormalFormExpression.getGroups().isEmpty() && valueNormalFormExpression.getAttributes().isEmpty() 
				&& valueNormalFormExpression.getConcepts().size() == 1) {
			Concept valueNormalFormExpressionFocusConcept = valueNormalFormExpression.getConcepts().iterator().next();
			Concept concept = ScgFactory.eINSTANCE.createConcept();
			concept.setId(valueNormalFormExpressionFocusConcept.getId());
			return concept;
		} else {
			return EcoreUtil.copy(valueNormalFormExpression);
		}
	}
	
	public static Collection<Attribute> getAttributes(Collection<Group> Groups) {
		Collection<Attribute> attributes = new HashSet<Attribute>();
		for (Group group : Groups) {
			attributes.addAll(group.getAttributes());
		}
		return attributes;
	}
}