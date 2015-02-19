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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.emf.ecore.util.EcoreUtil;

import com.b2international.snowowl.semanticengine.simpleast.subsumption.SubsumptionTester;
import com.b2international.snowowl.semanticengine.simpleast.utils.QueryAstUtils;
import com.b2international.snowowl.snomed.dsl.query.queryast.AttributeClause;
import com.b2international.snowowl.snomed.dsl.query.queryast.ConceptRef;
import com.b2international.snowowl.snomed.dsl.query.queryast.RValue;

/**
 * <b>5.3.4.3	Merging groups</b><br/>
 * <ul><li>
 * If a group in one definition meets the following criteria in relation to a group in the other 
 * definition then the groups are merged:
 * <ul><li>At least one attribute in one of the groups is name-matched by an attribute in the other group.</li>
 * and
 * <li>For each name-matched pair of attributes, the value of that attribute in one group either subsumes or is 
 * identical to the value of the name-matched attribute in the other group.</li></ul>
 * <li>Groups that meet the criteria for merging are merged by adding all attributes present in both source 
 * groups to the same group in the merged target definition.</li>
 * <li>Groups that cannot be merged are created as separate groups in the target definition.</li>
 * </ul>
 * Note that these conditions allow additional attributes that are not name-matched to be present in either 
 * of the candidate groups. They also allow values of name-matched attributes to be subsumed in different 
 * directions between the two groups (i.e. do not require the entire of one group to be subsumed the other group).<br/><br/>
 * 
 */
public class AttributeClauseListMerger {
	
	private final SubsumptionTester subsumptionTester;
	
	/**
	 * Class constructor.
	 * 
	 * @param subsumptionTester the {@link SubsumptionTester} to use
	 * @param mergedConceptDefinition the merged {@link ConceptDefinition} instance to populate
	 */
	public AttributeClauseListMerger(SubsumptionTester subsumptionTester) {
		this.subsumptionTester = subsumptionTester;
	}

	/**
	 * Because of the interdependencies with {@link UngroupedAttributesMerger}, this method modifies 
	 * the specified {@link ConceptDefinition} as a side effect.
	 * 
	 * @param conceptDefinitions the original concept definitions to be merged
	 * @param mergedConceptDefinition the {@link ConceptDefinition} to modify when merging
	 * @see AttributeClauseListMerger#AttributeClauseGroupMerger(SubsumptionTester, ConceptDefinition)
	 */
	public void mergeAttributeClauseGroups(Map<ConceptRef, ConceptDefinition> conceptDefinitions, ConceptDefinition mergedConceptDefinition) {
		// handle case when there is only one concept
		if (conceptDefinitions.size() == 1) {
			ConceptDefinition conceptDefinition = conceptDefinitions.values().iterator().next();
			mergedConceptDefinition.getAttributeClauseLists().addAll(conceptDefinition.getAttributeClauseLists());
			return;
		}
		
		// use set to avoid duplicates
		Collection<AttributeClauseList> processedAttributeClauseGroups = new HashSet<AttributeClauseList>();

		for (Entry<ConceptRef, ConceptDefinition> conceptMapEntry1 : conceptDefinitions.entrySet()) {
			ConceptDefinition conceptDefinitions1 = conceptMapEntry1.getValue();
			List<AttributeClauseList> attributeGroups1 = conceptDefinitions1.getAttributeClauseLists();
			for (Entry<ConceptRef, ConceptDefinition> conceptMapEntry2 : conceptDefinitions.entrySet()) {
				// don't compare with same concept
				if (conceptMapEntry1.getKey().equals(conceptMapEntry2.getKey()))
					continue;
				
				ConceptDefinition conceptDefinitions2 = conceptMapEntry2.getValue();
				List<AttributeClauseList> attributeGroups2 = conceptDefinitions2.getAttributeClauseLists();
				
				for (AttributeClauseList attributeGroup1 : attributeGroups1) {
					if (processedAttributeClauseGroups.contains(attributeGroup1))
						continue;
					boolean merged = false;
					for (AttributeClauseList attributeGroup2 : attributeGroups2) {
						if (processedAttributeClauseGroups.contains(attributeGroup2))
							continue;
						boolean mergeable = isMergeable(attributeGroup1, attributeGroup2);
						if (mergeable) {
							AttributeClauseList mergedAttributeClauseGroup = new AttributeClauseList();
							Collection<AttributeClause> attributes1 = (EcoreUtil.copyAll(attributeGroup1.getAttributeClauses()));
							Collection<AttributeClause> attributes2 = (EcoreUtil.copyAll(attributeGroup2.getAttributeClauses()));
							mergedAttributeClauseGroup.getAttributeClauses().addAll(attributes1);
							mergedAttributeClauseGroup.getAttributeClauses().addAll(attributes2);
//							resultingAttributeClauseGroups.add(mergedAttributeClauseGroup);
							mergedConceptDefinition.getAttributeClauseLists().add(mergedAttributeClauseGroup);
							merged = true;
							processedAttributeClauseGroups.add(attributeGroup2);
							break;
						} else {
//							resultingAttributeClauseGroups.add(attributeGroup2);
							mergedConceptDefinition.getAttributeClauseLists().add(attributeGroup2);
							processedAttributeClauseGroups.add(attributeGroup2);
						}
					}
					if (!merged) {
//						resultingAttributeClauseGroups.add(attributeGroup1);
						mergedConceptDefinition.getAttributeClauseLists().add(attributeGroup1);
					}
					processedAttributeClauseGroups.add(attributeGroup1);
				}
			}
		}
	}

	/**
	 * <ul><li>
	 * If a group in one definition meets the following criteria in relation to a group in the other 
	 * definition then the groups are merged:
	 * <ul><li>At least one attribute in one of the groups is name-matched by an attribute in the other group.</li>
	 * and
	 * <li>For each name-matched pair of attributes, the value of that attribute in one group either subsumes or is 
	 * identical to the value of the name-matched attribute in the other group.</li></ul></ul>
	 * 
	 * @param group1
	 * @param group2
	 * @return
	 */
	public boolean isMergeable(AttributeClauseList group1, AttributeClauseList group2) {
		boolean mergeable = false;
		for (AttributeClause attribute1 : group1.getAttributeClauses()) {
			ConceptRef attribute1NameConcept = QueryAstUtils.getAttributeNameConcept(attribute1);
			
			for (AttributeClause attribute2 : group2.getAttributeClauses()) {
				ConceptRef attribute2NameConcept = QueryAstUtils.getAttributeNameConcept(attribute2);
				if (attribute1NameConcept.getConceptId().equals(attribute2NameConcept.getConceptId())) {
					RValue attribute1ValueExpression = QueryAstUtils.getAttributeValueExpression(attribute1);
					RValue attribute2ValueExpression = QueryAstUtils.getAttributeValueExpression(attribute2);
					if (subsumptionTester.isSubsumed(attribute1ValueExpression, 
							attribute2ValueExpression)
						|| subsumptionTester.isSubsumed(attribute2ValueExpression, 
								attribute1ValueExpression)) {
						mergeable = true;
						break;
					}
				}
			}
			
		}
		
		return mergeable;
	}
}