/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.semanticengine.simpleast.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.ecore.util.EcoreUtil;

import com.b2international.snowowl.semanticengine.simpleast.normalform.AttributeClauseList;
import com.b2international.snowowl.semanticengine.simpleast.normalform.AttributeNameMatch;
import com.b2international.snowowl.semanticengine.simpleast.normalform.ConceptDefinition;
import com.b2international.snowowl.semanticengine.simpleast.subsumption.SubsumptionTester;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.dsl.query.queryast.AndClause;
import com.b2international.snowowl.snomed.dsl.query.queryast.AttributeClause;
import com.b2international.snowowl.snomed.dsl.query.queryast.AttributeClauseGroup;
import com.b2international.snowowl.snomed.dsl.query.queryast.ConceptRef;
import com.b2international.snowowl.snomed.dsl.query.queryast.RValue;
import com.b2international.snowowl.snomed.dsl.query.queryast.SubExpression;
import com.b2international.snowowl.snomed.dsl.query.queryast.ecoreastFactory;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;

/**
 * Collection of utility methods used by the {@link SubsumptionTester} and the {@link BasicExpressionNormalFormGenerator}.
 * 
 */
public final class QueryAstUtils {
	
	 /* The only valid context attributes are:<br/>
	  * "associated finding" (246090004), "associated procedure" (363589002), "finding context" (2470590016), 
	  * "procedure context" (2470591017), "temporal context" (2470592012) and "subject relationship context" (2470593019).*/
	private static final List<String> VALID_CONTEXT_ATTRIBUTE_NAMES = Arrays.asList("246090004", "363589002", 
			"2470590016", "2470591017", "2470592012", "2470593019"); 
	
	private QueryAstUtils() { } // suppress default constructor

	public static List<ConceptRef> getFocusConcepts(RValue expressionRoot) {
		List<ConceptRef> focusConcepts = Lists.newArrayList();
		collectFocusConcepts(expressionRoot, focusConcepts);
		return focusConcepts;
	}
	
	public static List<AttributeClauseGroup> getAttributeGroups(RValue expressionRoot) {
		List<AttributeClauseGroup> attributeGroups = Lists.newArrayList();
		collectAttributeGroups(expressionRoot, attributeGroups);
		return attributeGroups;
	}
	
	private static void collectAttributeGroups(RValue root, List<AttributeClauseGroup> attributeGroups) {
		if (root instanceof AttributeClauseGroup) {
			AttributeClauseGroup attributeGroup = (AttributeClauseGroup) root;
			attributeGroups.add(attributeGroup);
		} else if (root instanceof AndClause) {
			AndClause rootAndClause = (AndClause) root;
			collectAttributeGroups(rootAndClause.getLeft(), attributeGroups);
			collectAttributeGroups(rootAndClause.getRight(), attributeGroups);
		} else if (root instanceof SubExpression) {
			SubExpression subExpression = (SubExpression) root;
			collectAttributeGroups(subExpression.getValue(), attributeGroups);
		}
	}
	
	public static List<AttributeClause> getUngroupedAttributes(RValue expressionRoot) {
		List<AttributeClause> ungroupedAttributes = Lists.newArrayList();
		collectNonIsAAttributes(expressionRoot, ungroupedAttributes);
		return ungroupedAttributes;
	}
	
	private static void collectNonIsAAttributes(RValue root, List<AttributeClause> attributes) {
		if (root instanceof AttributeClause) {
			AttributeClause attribute = (AttributeClause) root;
			if (!(getAttributeNameConcept(attribute).getConceptId().equals(Concepts.IS_A))) {
				attributes.add(attribute);
			}
		} else if (root instanceof AndClause) {
			AndClause rootAndClause = (AndClause) root;
			collectNonIsAAttributes(rootAndClause.getLeft(), attributes);
			collectNonIsAAttributes(rootAndClause.getRight(), attributes);
		} else if (root instanceof SubExpression) {
			SubExpression subExpression = (SubExpression) root;
			collectNonIsAAttributes(subExpression.getValue(), attributes);
		}
	}
	
	private static void collectFocusConcepts(RValue root, Collection<ConceptRef> focusConcepts) {
		if (root instanceof AndClause) {
			AndClause rootAndClause = (AndClause) root;
			collectFocusConcepts(rootAndClause.getLeft(), focusConcepts);
			collectFocusConcepts(rootAndClause.getRight(), focusConcepts);
		} else if (root instanceof ConceptRef) {
			focusConcepts.add((ConceptRef) root);
		} else if (root instanceof SubExpression) {
			SubExpression subExpression = (SubExpression) root;
			collectFocusConcepts(subExpression.getValue(), focusConcepts);
		}
	}
	
	/**
	 * @param attribute
	 * @return	the name concept of the specified {@link AttributeClause}
	 * @throws UnsupportedOperationException if the attribute name is not of the type {@link ConceptRef}
	 */
	public static ConceptRef getAttributeNameConcept(AttributeClause attribute) {
		RValue nameLValue = attribute.getLeft();
		if (!(nameLValue instanceof ConceptRef))
			throw new UnsupportedOperationException("Attribute name '" + nameLValue + "' is not supported.");
		ConceptRef nameConceptGroup = (ConceptRef) nameLValue;
		return nameConceptGroup;
	}

	/**
	 * @param attribute
	 * @return	the value of the specified {@link AttributeClause}
	 */
	public static RValue getAttributeValueExpression(AttributeClause predicate) {
		RValue right = predicate.getRight();
		if (right instanceof SubExpression) {
			return EcoreUtil.copy(right);
		}
		RValue expressionValue = EcoreUtil.copy(right);
		if (right instanceof ConceptRef) {
			return expressionValue;
//			AttributeClause attributeClause = ecoreastFactory.eINSTANCE.createAttributeClause();
//			ConceptRef isA = ecoreastFactory.eINSTANCE.createConceptRef();
//			isA.setConceptId(IS_A);
//			attributeClause.setLeft(isA);
//			attributeClause.setRight(EcoreUtil.copy(right));
//			expressionValue = attributeClause;
		}
		SubExpression subExpression = ecoreastFactory.eINSTANCE.createSubExpression();
		subExpression.setValue(expressionValue);
		
		return subExpression;
	}

	/**
	 * @param attributeToMatch
	 * @param attributes
	 * @return the attributes from the specified collection, which are name-matched with the specified attribute
	 */
	public static Collection<AttributeNameMatch> findNameMatchedAttributes(AttributeClause attributeToMatch, Collection<AttributeClause> attributes) {
		return findNameMatchedAttributes(getAttributeNameConcept(attributeToMatch), attributes);
	}
	
	/**
	 * @param attributeNameConceptToMatch
	 * @param attributes
	 * @return the attributes from the specified collection, which are name-matched with the specified concept
	 */
	public static Collection<AttributeNameMatch> findNameMatchedAttributes(ConceptRef attributeNameConceptToMatch, Collection<AttributeClause> attributes) {
		// use set to avoid duplicates
		Collection<AttributeNameMatch> nameMatchedAttributes = new HashSet<AttributeNameMatch>();
		for (AttributeClause attribute : attributes) {
			ConceptRef attributeNameConceptRef = getAttributeNameConcept(attribute);
			ConceptRef attributeToMatchNameConcept = attributeNameConceptToMatch;
			if (attributeNameConceptRef.getConceptId().equals(attributeToMatchNameConcept.getConceptId())) {
				nameMatchedAttributes.add(new AttributeNameMatch(attribute));
			}
		}
		
		return Collections.unmodifiableCollection(nameMatchedAttributes);
	}
	
	/**
	 * @param attributeToMatch
	 * @param attributeGroup
	 * @return the attributes and their containing groups from the specified group, which are name-matched with the specified attribute
	 */
	public static Collection<AttributeNameMatch> findNameMatchedAttributesInGroups(ConceptRef attributeNameConceptToMatch, Collection<AttributeClauseList> attributeGroups) {
		Collection<AttributeNameMatch> nameMatchedAttributes = new HashSet<AttributeNameMatch>();
		for (AttributeClauseList attributeGroup : attributeGroups) {
			nameMatchedAttributes.addAll(findNameMatchedAttributes(attributeNameConceptToMatch, attributeGroup));
		}
		return Collections.unmodifiableCollection(nameMatchedAttributes);
	}
	
	/**
	 * @param attributeToMatch
	 * @param attributeGroup
	 * @return the attributes and their containing groups from the specified group, which are name-matched with the specified attribute
	 */
	public static Collection<AttributeNameMatch> findNameMatchedAttributes(ConceptRef attributeNameConceptToMatch, AttributeClauseList attributeGroup) {
		Collection<AttributeClause> groupedAttributes = attributeGroup.getAttributeClauses();
		Collection<AttributeNameMatch> attributeNameMatches = findNameMatchedAttributes(attributeNameConceptToMatch, groupedAttributes);
		for (AttributeNameMatch attributeNameMatch : attributeNameMatches) {
			attributeNameMatch.setGroup(attributeGroup);
		}

		return Collections.unmodifiableCollection(attributeNameMatches);
	}
	
	/**
	 * @param attributeToMatch
	 * @param attributeGroup
	 * @return the attributes and their containing groups from the specified group, which are name-matched with the specified attribute
	 */
	public static Collection<AttributeNameMatch> findNameMatchedAttributes(AttributeClause attributeToMatch, AttributeClauseList attributeGroup) {
		return findNameMatchedAttributes(getAttributeNameConcept(attributeToMatch), attributeGroup);
	}
	
	public static Collection<AttributeNameMatch> findNameMatchedAttributes(AttributeClause attributeToMatch, ConceptDefinition conceptDefinition) {
		// use set to avoid duplicates
		Collection<AttributeNameMatch> allNameMatchedAttributes = new HashSet<AttributeNameMatch>();
		Collection<AttributeNameMatch> ungroupedNameMatchedAttributes = findNameMatchedAttributes(attributeToMatch, 
				conceptDefinition.getUngroupedAttributes());
		allNameMatchedAttributes.addAll(ungroupedNameMatchedAttributes);
		
		for (AttributeClauseList attributeGroup : conceptDefinition.getAttributeClauseLists()) {
			allNameMatchedAttributes.addAll(findNameMatchedAttributes(attributeToMatch, attributeGroup));
		}
		
		return Collections.unmodifiableCollection(allNameMatchedAttributes);
	}
	
	public static Collection<AttributeNameMatch> findNameMatchedAttributes(String attributeNameConceptIdToMatch, ConceptDefinition conceptDefinition) {
		// use set to avoid duplicates
		Collection<AttributeNameMatch> allNameMatchedAttributes = new HashSet<AttributeNameMatch>();
		ConceptRef attributeNameConceptToMatch = ecoreastFactory.eINSTANCE.createConceptRef();
		attributeNameConceptToMatch.setConceptId(attributeNameConceptIdToMatch);
		Collection<AttributeNameMatch> ungroupedNameMatchedAttributes = findNameMatchedAttributes(attributeNameConceptToMatch, 
				conceptDefinition.getUngroupedAttributes());
		allNameMatchedAttributes.addAll(ungroupedNameMatchedAttributes);
		
		for (AttributeClauseList attributeGroup : conceptDefinition.getAttributeClauseLists()) {
			allNameMatchedAttributes.addAll(findNameMatchedAttributes(attributeNameConceptToMatch, attributeGroup));
		}
		
		return Collections.unmodifiableCollection(allNameMatchedAttributes);
	}
	
	public static Collection<AttributeNameMatch> findNameMatchedAttributesInConceptDefinitons(AttributeClause attributeToMatch, 
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
	
	public static boolean isLateralizable(ConceptRef concept) {
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
	public static boolean isValidContextAttribute(AttributeClause attribute) {
		ConceptRef attributeNameConcept = getAttributeNameConcept(attribute);
		return VALID_CONTEXT_ATTRIBUTE_NAMES.contains(attributeNameConcept.getConceptId());
	}
	
	public static boolean isNullOrEmpty(Collection<?> collection) {
		return collection == null || collection.isEmpty();
	}
	
	public static Collection<AttributeClause> getAttributes(Collection<AttributeClauseList> attributeGroups) {
		Collection<AttributeClause> attributes = new HashSet<AttributeClause>();
		for (AttributeClauseList attributeGroup : attributeGroups) {
			attributes.addAll(attributeGroup.getAttributeClauses());
		}
		return attributes;
	}
	
	public static RValue buildExpression(Collection<String> superTypes, List<AttributeClauseList> attributeClauseLists, 
			List<AttributeClause> ungroupedAttributes) {
		Preconditions.checkArgument(!superTypes.isEmpty(), "There should be at least one super type.");
		
		// sort super type concepts alphabetically by ID
		ArrayList<String> sortedSuperTypes = Lists.newArrayList(superTypes);
		Collections.sort(sortedSuperTypes, Ordering.natural());
		
		ConceptRef firstSuperTypeConceptRef = ecoreastFactory.eINSTANCE.createConceptRef();
		Iterator<String> superTypeIterator = superTypes.iterator();
		firstSuperTypeConceptRef.setConceptId(superTypeIterator.next());
		
		RValue root = firstSuperTypeConceptRef;

		// focus concepts
		while (superTypeIterator.hasNext()) {
			String conceptId = superTypeIterator.next();
			root = createAndClause(root, createConceptRef(conceptId));
		}
		
		// refinements
		Collections.sort(ungroupedAttributes, Ordering.usingToString());
		for (AttributeClause attribute : ungroupedAttributes) {
			root = createAndClause(root, attribute);
		}
		sortAttributeGroups(attributeClauseLists);
		for (AttributeClauseList group : attributeClauseLists) {
			root = handleAttributeClauseGroup(root, group);
		}
		
		return root;
	}
	
	private static void sortAttributeGroups(List<AttributeClauseList> attributeClauseLists) {
		for (AttributeClauseList attributeClauseList : attributeClauseLists) {
			Collections.sort(attributeClauseList.getAttributeClauses(), Ordering.usingToString());
		}
		Collections.sort(attributeClauseLists, Ordering.usingToString());
	}

	private static RValue handleAttributeClauseGroup(RValue root, AttributeClauseList attributeClauseList) {
		Preconditions.checkNotNull(root, "Root must not be null.");
		Preconditions.checkNotNull(attributeClauseList, "Attribute clause llist must not be null.");
		AttributeClauseGroup attributeClauseGroup = ecoreastFactory.eINSTANCE.createAttributeClauseGroup();
		List<AttributeClause> attributeClauses = attributeClauseList.getAttributeClauses();
		if(attributeClauses != null && !attributeClauses.isEmpty()) {
			// no need to sort here, attributes were already sorted while sorting their containing groups
			AttributeClause firstAttribute = attributeClauses.get(0);
			RValue localRootClause = firstAttribute;
			
			for(int i = 1; i < attributeClauses.size(); i++) {
				AttributeClause attribute = attributeClauses.get(i);
				
				// TODO: handle RefSet
				if (attribute.getLeft() instanceof ConceptRef) {
					RValue rValue = attribute;
					localRootClause = createAndClause(localRootClause, rValue);
				}
			}
//			SubExpression valueSubExpression = ecoreastFactory.eINSTANCE.createSubExpression();
//			valueSubExpression.setValue(localRootClause);
			attributeClauseGroup.setValue(localRootClause);
			return createAndClause(root, attributeClauseGroup);
		}
		return root;
	}
	
	private static AndClause createAndClause(RValue left, RValue right) {
		AndClause clause = ecoreastFactory.eINSTANCE.createAndClause();
		clause.setLeft(left);
		clause.setRight(right);
		return clause;
	}
	
	protected static RValue createConceptRef(String conceptId) {
		return createConceptRef(conceptId, "");
	}
	
	protected static RValue createConceptRef(String conceptId, String label) {
		ConceptRef conceptRef = ecoreastFactory.eINSTANCE.createConceptRef();
		conceptRef.setConceptId(conceptId);
		conceptRef.setLabel(label);
		return conceptRef;
	}
	
	protected static AttributeClause createAttributeClause(RValue left, RValue right) {
		AttributeClause attributeClause = ecoreastFactory.eINSTANCE.createAttributeClause();
		attributeClause.setLeft(left);
		attributeClause.setRight(right);
		return attributeClause;
	}

}