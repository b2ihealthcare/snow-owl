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
package com.b2international.snowowl.semanticengine.simpleast.normalform;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.b2international.snowowl.semanticengine.simpleast.subsumption.SubsumptionTester;
import com.b2international.snowowl.semanticengine.simpleast.utils.QueryAstUtils;
import com.b2international.snowowl.snomed.dsl.query.queryast.AttributeClause;
import com.b2international.snowowl.snomed.dsl.query.queryast.ConceptRef;

/**
 * <b>5.3.4.4	Merging ungrouped attributes</b><br/>
 * <ul><li>
 * If an ungrouped attribute in one definition is name-matched by a grouped attribute in the other definition, 
 * this attribute is merged according to the following rules:</li>
 * <ul><li>
 * If the value of the ungrouped attribute subsumes value of the name-matched grouped attribute
 * <ul><li>omit the ungrouped attribute from the target definition</li></ul>
 * <li>If the value of the grouped attribute subsumes the value of the name-matched grouped attribute
 * <ul><li>
 * add the ungrouped attribute to the group containing the matching grouped attribute in the target definition</li>
 * <li>if this condition is met by multiple groups, add the ungrouped attribute to all groups that meet this condition</li>
 * </ul>
 * <li>If the value of the name-matched grouped and ungrouped attributes are disjoint</li>
 * <ul><li>add the ungrouped attribute as an ungrouped attribute in the target expression.</li></ul></ul>
 * <li>If an ungrouped attribute is name-matched with an ungrouped attribute in the other definition 
 * this attribute is merged according to the following rules:
 * <ul><li>
 * If the value of one of the name-matched attributes subsumes the other value</li>
 * <ul><li>
 * include the attribute with the most specific value (not grouped)</li>
 * <li>omit the attributed with the less specific value</li></ul>
 * <li>If the value of the name-matched attributes are identical</li>
 * <ul><li>Include one and omit the other</li></ul>
 * <li>If neither of the two preceding conditions apply</li>
 * <ul><li>
 * include both attributes (not grouped)</li></ul></ul>
 * <li>If an attribute is ungrouped in one expression and there is no name-matched attribute in the 
 * other definition
 * <ul><li>include the attribute (not grouped).</li></ul></ul>
 * 
 */
public class UngroupedAttributesMerger {
	
	private final SubsumptionTester subsumptionTester;
	
	/**
	 * Class constructor.
	 * 
	 * @param subsumptionTester the {@link SubsumptionTester} to use
	 */
	public UngroupedAttributesMerger(SubsumptionTester subsumptionTester) {
		this.subsumptionTester = subsumptionTester;
	}

	/**
	 * Because of the interdependencies with {@link AttributeClauseListMerger}, this method modifies 
	 * the {@link ConceptDefinition} specified in the constructor as a side effect.
	 * 
	 * @param conceptDefinitions the original concept definitions to be merged
	 * @see UngroupedAttributesMerger#UngroupedAttributesMerger(SubsumptionTester, ConceptDefinition)
	 */
	public void mergeUngroupedAttributes(Map<ConceptRef, ConceptDefinition> conceptDefinitionMap, ConceptDefinition mergedConceptDefinition) {
		// handle case when there is only one concept
		if (conceptDefinitionMap.size() == 1) {
			ConceptDefinition conceptDefinition = conceptDefinitionMap.values().iterator().next();
			mergedConceptDefinition.getUngroupedAttributes().addAll(conceptDefinition.getUngroupedAttributes());
			return;
		}
		
		Set<AttributeClause> ungroupedAttributeSet = new HashSet<AttributeClause>();
		Set<AttributeClause> processedAttributes = new HashSet<AttributeClause>();

		for (Entry<ConceptRef, ConceptDefinition> outerConceptMapEntry : conceptDefinitionMap.entrySet()) {
			ConceptDefinition outerConceptDefinition = outerConceptMapEntry.getValue();
			Set<AttributeClause> nonNameMatchedAttributes = new HashSet<AttributeClause>();
			Set<AttributeClause> attributesToOmitFromMergedUngroupedAttributes = new HashSet<AttributeClause>();
			
			for (AttributeClause outerUngroupedAttribute : outerConceptDefinition.getUngroupedAttributes()) {
				if (processedAttributes.contains(outerUngroupedAttribute))
					continue;
				Collection<AttributeNameMatch> nameMatchedAttributes = QueryAstUtils.findNameMatchedAttributesInConceptDefinitons(
						outerUngroupedAttribute, filterConceptDefinitionMap(conceptDefinitionMap, outerConceptMapEntry.getKey()).values());
				
				// If an ungrouped attribute in one definition is name-matched by a grouped attribute in the other definition
				Collection<AttributeNameMatch> groupedAttributeNameMatches = QueryAstUtils.getGroupedAttributeNameMatches(nameMatchedAttributes);
				for (AttributeNameMatch groupedAttributeNameMatch : groupedAttributeNameMatches) {
					AttributeClause groupedNameMatchedAttribute = groupedAttributeNameMatch.getAttribute();
					if (processedAttributes.contains(groupedNameMatchedAttribute))
						continue;
					
					/* If the value of the ungrouped attribute subsumes value of the name-matched grouped attribute
					 *		omit the ungrouped attribute from the target definition	 */
					if (subsumptionTester.isSubsumed(outerUngroupedAttribute, groupedNameMatchedAttribute)) {
						attributesToOmitFromMergedUngroupedAttributes.add(outerUngroupedAttribute);
						processedAttributes.add(outerUngroupedAttribute);
					}
					/* If the value of the grouped attribute subsumes the value of the name-matched grouped attribute
					 *		add the ungrouped attribute to the group containing the matching grouped attribute in the target definition
					 *		if this condition is met by multiple groups
					 *			add the ungrouped attribute to all groups that meet this condition */
					else if (subsumptionTester.isSubsumed(groupedNameMatchedAttribute, outerUngroupedAttribute)) {
						AttributeClauseList attributeGroup = groupedAttributeNameMatch.getGroup();
						List<AttributeClause> groupedAttributes = attributeGroup.getAttributeClauses();
						
						/*
						 * find attribute group in mergedConceptDefinition
						 * if found
						 * 		add outerUngorupedAttribute to this group
						 * else
						 * 		find groupedNameMatchedAttribute in group
						 * 		if found
						 * 			add outerUngorupedAttribute to this group
						 */
						for (AttributeClauseList mergedAttributeClauseGroup : mergedConceptDefinition.getAttributeClauseLists()) {
							if (mergedAttributeClauseGroup == attributeGroup) {
								groupedAttributes.add(outerUngroupedAttribute);
								break;
							} else {
								// TODO: add test case for this branch
								for (AttributeClause mergedGroupedAttribute : groupedAttributes) {
									if (mergedGroupedAttribute == groupedNameMatchedAttribute) {
										mergedAttributeClauseGroup.getAttributeClauses().add(outerUngroupedAttribute);
										break;
									}
								}
							}
						}
						
						attributesToOmitFromMergedUngroupedAttributes.add(outerUngroupedAttribute);
						processedAttributes.add(outerUngroupedAttribute);
					}
					/* If the value of the name-matched grouped and ungrouped attributes are disjoint
					 * 		add the ungrouped attribute as an ungrouped attribute in the target expression. */
					else {
						ungroupedAttributeSet.add(outerUngroupedAttribute);
						processedAttributes.add(outerUngroupedAttribute);
					}
					
					processedAttributes.add(groupedNameMatchedAttribute);
				}
				
				// If an ungrouped attribute is name-matched with an ungrouped attribute in the other definition
				Collection<AttributeNameMatch> ungroupedAttributeNameMatches = QueryAstUtils.getUngroupedAttributeNameMatches(nameMatchedAttributes);
				for (AttributeNameMatch ungroupedAttributeNameMatch : ungroupedAttributeNameMatches) {
					AttributeClause ungroupedNameMatchedAttribute = ungroupedAttributeNameMatch.getAttribute();
					if (processedAttributes.contains(ungroupedNameMatchedAttribute))
						continue;
					
					/* If the value of one of the name-matched attributes subsumes the other value
					 * 		include the attribute with the most specific value (not grouped)
					 *		omit the attributed with the less specific value
					 * If the value of the name-matched attributes are identical
					 * 		include one and omit the other 
					 * 		TODO: is this really handled by the two ifs? */
					boolean nameMatchedSubsumesUngrouped = subsumptionTester.isSubsumed(ungroupedNameMatchedAttribute, outerUngroupedAttribute);
					boolean ungroupedSubsumesNameMatched = subsumptionTester.isSubsumed(outerUngroupedAttribute, ungroupedNameMatchedAttribute);
					if (nameMatchedSubsumesUngrouped) {
						ungroupedAttributeSet.add(outerUngroupedAttribute);
						attributesToOmitFromMergedUngroupedAttributes.add(ungroupedNameMatchedAttribute);
					} else if (ungroupedSubsumesNameMatched) {
						ungroupedAttributeSet.add(ungroupedNameMatchedAttribute);
						attributesToOmitFromMergedUngroupedAttributes.add(outerUngroupedAttribute);
						processedAttributes.add(ungroupedNameMatchedAttribute);
					}
					/* If neither of the two preceding conditions apply
					 *		include both attributes (not grouped) */
					else {
						ungroupedAttributeSet.add(outerUngroupedAttribute);
						ungroupedAttributeSet.add(ungroupedNameMatchedAttribute);
					}
					
					processedAttributes.add(ungroupedNameMatchedAttribute);
				}
				
				/* If an attribute is ungrouped in one expression and there is no name-matched attribute in	the other definition
				 * 		include the attribute (not grouped).
				 */
				nonNameMatchedAttributes.add(outerUngroupedAttribute);
				processedAttributes.add(outerUngroupedAttribute);	// ???
			}

			// only add those non-matched attributes which we don't explicitly want to omit
			for (AttributeClause nonNameMatchedAttribute : nonNameMatchedAttributes) {
				if (!attributesToOmitFromMergedUngroupedAttributes.contains(nonNameMatchedAttribute))
					ungroupedAttributeSet.add(nonNameMatchedAttribute);
			}
			
		}
		
		mergedConceptDefinition.getUngroupedAttributes().addAll(ungroupedAttributeSet);
	}
	
	private Map<ConceptRef, ConceptDefinition> filterConceptDefinitionMap(Map<ConceptRef, ConceptDefinition> originalMap, ConceptRef concept) {
		Map<ConceptRef, ConceptDefinition> filteredMap = new HashMap<ConceptRef, ConceptDefinition>(originalMap);
		filteredMap.remove(concept);
		return filteredMap;
	}
	
}