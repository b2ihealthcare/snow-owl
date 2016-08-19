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
package com.b2international.snowowl.semanticengine.normalform;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.common.util.ECollections;
import org.eclipse.emf.common.util.EList;

import com.b2international.snowowl.core.api.browser.IClientTerminologyBrowser;
import com.b2international.snowowl.dsl.scg.Attribute;
import com.b2international.snowowl.dsl.scg.AttributeValue;
import com.b2international.snowowl.dsl.scg.Concept;
import com.b2international.snowowl.dsl.scg.Expression;
import com.b2international.snowowl.dsl.scg.Group;
import com.b2international.snowowl.dsl.scg.ScgFactory;
import com.b2international.snowowl.semanticengine.utils.AttributeCollectionComparator;
import com.b2international.snowowl.semanticengine.utils.SemanticUtils;
import com.b2international.snowowl.snomed.datastore.SnomedClientStatementBrowser;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptIndexEntry;
import com.google.common.collect.Ordering;

/**
 * Generates the normal form of an arbitrary SCG expression.
 * 
 */
public class ScgExpressionNormalFormGenerator implements ExpressionNormalFormGenerator {
	
	private final IClientTerminologyBrowser<SnomedConceptIndexEntry, String> terminologyBrowser;
	private final SnomedClientStatementBrowser statementBrowser;

	public ScgExpressionNormalFormGenerator(IClientTerminologyBrowser<SnomedConceptIndexEntry, String> terminologyBrowser, SnomedClientStatementBrowser statementBrowser) {
		this.terminologyBrowser = terminologyBrowser;
		this.statementBrowser = statementBrowser;
	}

	/**
	 * @return the original expression in long normal form
	 */
	public Expression getLongNormalForm(Expression originalExpression) {
		// expression focus concepts	
		Collection<Concept> focusConcepts = originalExpression.getConcepts();
		FocusConceptNormalizer focusConceptNormalizer = new FocusConceptNormalizer(terminologyBrowser, statementBrowser);
		FocusConceptNormalizationResult normalizedFocusConcepts = focusConceptNormalizer.normalizeFocusConcepts(focusConcepts);
		
		// expression refinements
		List<Group> expressionAttributeGroups = originalExpression.getGroups();
		List<Attribute> expressionUngroupedAttributes = originalExpression.getAttributes();
		AttributeNormalizer attributeNormalizer = new AttributeNormalizer(terminologyBrowser, statementBrowser);
		ConceptDefinition normalizedExpressionRefinements = attributeNormalizer.normalizeAttributes(expressionAttributeGroups, 
				expressionUngroupedAttributes);
		
		// merge refinements
		RefinementsMerger refinementsMerger = new RefinementsMerger(terminologyBrowser);
		ConceptDefinition mergedRefinements = refinementsMerger.mergeRefinements(normalizedFocusConcepts, normalizedExpressionRefinements);
		
		// create expression
		Expression normalizedExpression = ScgFactory.eINSTANCE.createExpression();
//		normalizedExpression.getRefinements().addAll(mergedRefinements);
		normalizedExpression.getGroups().addAll(mergedRefinements.getGroups());
		normalizedExpression.getAttributes().addAll(mergedRefinements.getUngroupedAttributes());
		normalizedExpression.getConcepts().addAll(wrapConceptMinis(normalizedFocusConcepts.filteredPrimitiveSuperTypes));

		sortExpressionContents(normalizedExpression);
		return normalizedExpression;
	}
	
	private void sortExpressionContents(Expression expression) {
		
		/*
		 * 5.5.2 Canonical representations
		 * 
		 * The idea of a canonical representation is that it generates a
		 * predictable string rendering. The missing element to deliver this in
		 * the description of the "long normal form", is a specified sort order
		 * within the collections elements in an expression. A standard sort
		 * order is not essential for general purpose use but it is very useful
		 * to enable fast matching of logically identical expressions (which
		 * might otherwise be obscured by differences in order that have no
		 * semantic relevance). The canonical form for a SNOMED CT expression is
		 * regarded as being the long normal form ordered according to the
		 * following sorting rules.
		 */
		sortConcepts(expression.getConcepts());
		sortAttributes(expression.getAttributes());
		sortGroups(expression.getGroups());
	}

	private void sortConcepts(EList<Concept> concepts) {
		
		/* 
		 * ConceptIds are sorted alphabetically based on their normal string rendering (i.e.
		 * digits with no leading zeros)
		 */
		ECollections.sort(concepts, Ordering.usingToString());
	}
	
	private void sortAttributes(EList<Attribute> attributes) {
		
		/*
		 * Attributes are sorted alphabetically based on the string concatenation of the
		 * name and value conceptIds separated by an "=" sign.
		 * 
		 * If a value contains nested refinements, the value is enclosed in
		 * round brackets (which may influence the sort order) and the elements
		 * of the nested expression are sorted by applying the general canonical
		 * sorting rules.
		 */
		for (Attribute attribute : attributes) {
			
			AttributeValue rValue = attribute.getValue();

			if (rValue instanceof Expression) {
				sortExpressionContents((Expression) rValue);
			}
		}
		
		ECollections.sort(attributes, Ordering.usingToString());
	}

	private void sortGroups(EList<Group> groups) {

		/*
		 * Groups are sorted by alphabetical order of the combined set of previously sorted
		 * attributes.
		 */
		for (Group group : groups) {
			sortAttributes(group.getAttributes());
		}
		
		ECollections.sort(groups, Ordering.usingToString());
	}
	
	/**
	 * @return the original expression in short normal form
	 */
	public Expression getShortNormalForm(Expression originalExpression) {
		Expression longNormalFormExpression = getLongNormalForm(originalExpression);
		deriveShortNormalForm(longNormalFormExpression);
		return longNormalFormExpression;
	}

	private void deriveShortNormalForm(Expression expression) {
		/* 5.5.1.4	Recursive removal of redundancy
		 * The process described in this section is recursively applied to any nested expressions that remain 
		 * after the top-level process to remove redundant attributes and groups. Unlike the process of normalization, 
		 * this process is done breadth first at each level in the hierarchy. If long normalized forms at nested 
		 * levels are shortened before checking for redundancy, the expression will not match those in the merged 
		 * definition even if they are semantically identical. */
		deriveShortNormalFormNoRecursion(expression);
		Collection<Attribute> attributesWithExpressionValue = collectAttributesWithExpressionValue(expression);
		for (Attribute attributeWithExpressionValue : attributesWithExpressionValue) {
			deriveShortNormalFormNoRecursion(SemanticUtils.getAttributeValueExpression(attributeWithExpressionValue));
		}
	}
	
	private void deriveShortNormalFormNoRecursion(Expression expression) {
		Collection<Concept> primitiveFocusConcepts = expression.getConcepts();
		FocusConceptNormalizer focusConceptNormalizer = new FocusConceptNormalizer(terminologyBrowser, statementBrowser);
		FocusConceptNormalizationResult normalizationResult = focusConceptNormalizer.normalizeFocusConcepts(primitiveFocusConcepts);
		
		/* 5.5.1.3	Removed redundant attributes and groups
		 * Attributes and groups shared with the merged definition are removed from the refinement. 
		 * Only groups and ungrouped attributes that are identical can be removed from the refinement. 
		 * If a group is not identical the parts that are similar cannot be removed. */
		Collection<Group> groupsToRemove = new HashSet<Group>();
		List<Group> longNormalFormGroups = expression.getGroups();
		for (Group refinementGroup : longNormalFormGroups) {
			for (Group mergedDefinitionGroup : normalizationResult.mergedConceptDefinition.getGroups()) {
				AttributeCollectionComparator attributeCollectionComparator = new AttributeCollectionComparator();
				boolean equal = attributeCollectionComparator.equal(mergedDefinitionGroup.getAttributes(), 
						refinementGroup.getAttributes());
				if (equal)
					groupsToRemove.add(refinementGroup);
			}
		}
		
		Collection<Attribute> ungroupedAttributesToRemove = new HashSet<Attribute>();
		List<Attribute> longNormalFormUngroupedAttributes = expression.getAttributes();
		for (Attribute refinementAttribute : longNormalFormUngroupedAttributes) {
			for (Attribute mergedDefinitionAttribute : normalizationResult.mergedConceptDefinition.getUngroupedAttributes()) {
				if (refinementAttribute.toString().equals(mergedDefinitionAttribute.toString()))
					ungroupedAttributesToRemove.add(refinementAttribute);
			}
		}
		longNormalFormGroups.removeAll(groupsToRemove);
		longNormalFormUngroupedAttributes.removeAll(ungroupedAttributesToRemove);
	}

	private Collection<Attribute> collectAttributesWithExpressionValue(Expression expression) {
		Set<Attribute> attributes = new HashSet<Attribute>();
		attributes.addAll(SemanticUtils.getAttributes(expression.getGroups()));
		attributes.addAll(expression.getAttributes());
		for (Iterator<Attribute> attributeIterator = attributes.iterator(); attributeIterator.hasNext();) {
			Attribute attribute = (Attribute) attributeIterator.next();
			if (!(attribute.getValue() instanceof Expression))
				attributeIterator.remove();
		}
		return attributes;
	}
	
	private Collection<Concept> wrapConceptMinis(Collection<SnomedConceptIndexEntry> filteredPrimitiveSuperTypes) {
		List<Concept> concepts = new ArrayList<Concept>();
		for (SnomedConceptIndexEntry conceptMini : filteredPrimitiveSuperTypes) {
			Concept concept = ScgFactory.eINSTANCE.createConcept();
			concept.setId(conceptMini.getId());
			concepts.add(concept);
		}
		return concepts;
	}
}