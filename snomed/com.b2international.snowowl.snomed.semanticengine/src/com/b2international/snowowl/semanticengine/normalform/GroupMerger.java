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

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.emf.ecore.util.EcoreUtil;

import com.b2international.snowowl.dsl.scg.Attribute;
import com.b2international.snowowl.dsl.scg.Concept;
import com.b2international.snowowl.dsl.scg.Expression;
import com.b2international.snowowl.dsl.scg.Group;
import com.b2international.snowowl.dsl.scg.ScgFactory;
import com.b2international.snowowl.semanticengine.subsumption.SubsumptionTester;
import com.b2international.snowowl.semanticengine.utils.SemanticUtils;

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
public class GroupMerger {
	
	private final SubsumptionTester subsumptionTester;
	
	/**
	 * Class constructor.
	 * 
	 * @param subsumptionTester the {@link SubsumptionTester} to use
	 * @param mergedConceptDefinition the merged {@link ConceptDefinition} instance to populate
	 */
	public GroupMerger(SubsumptionTester subsumptionTester) {
		this.subsumptionTester = subsumptionTester;
	}

	/**
	 * Because of the interdependencies with {@link UngroupedAttributesMerger}, this method modifies 
	 * the specified {@link ConceptDefinition} as a side effect.
	 * 
	 * @param conceptDefinitions the original concept definitions to be merged
	 * @param mergedConceptDefinition the {@link ConceptDefinition} to modify when merging
	 * @see GroupMerger#GroupMerger(SubsumptionTester, ConceptDefinition)
	 */
	public void mergeGroups(Map<Concept, ConceptDefinition> conceptDefinitions, ConceptDefinition mergedConceptDefinition) {
		// handle case when there is only one concept
		if (conceptDefinitions.size() == 1) {
			ConceptDefinition conceptDefinition = conceptDefinitions.values().iterator().next();
			mergedConceptDefinition.getGroups().addAll(conceptDefinition.getGroups());
			return;
		}
		
		// use set to avoid duplicates
		Collection<Group> processedGroups = new HashSet<Group>();

		for (Entry<Concept, ConceptDefinition> conceptMapEntry1 : conceptDefinitions.entrySet()) {
			ConceptDefinition conceptDefinition1 = conceptMapEntry1.getValue();
			List<Group> groups1 = conceptDefinition1.getGroups();
			for (Entry<Concept, ConceptDefinition> conceptMapEntry2 : conceptDefinitions.entrySet()) {
				// don't compare with same concept
				if (conceptMapEntry1.getKey().equals(conceptMapEntry2.getKey()))
					continue;
				
				ConceptDefinition conceptDefinition2 = conceptMapEntry2.getValue();
				List<Group> groups2 = conceptDefinition2.getGroups();
				
				for (Group group1 : groups1) {
					if (processedGroups.contains(group1))
						continue;
					boolean merged = false;
					for (Group group2 : groups2) {
						if (processedGroups.contains(group2))
							continue;
						boolean mergeable = isMergeable(group1, group2);
						if (mergeable) {
							Group mergedGroup = ScgFactory.eINSTANCE.createGroup();
							Collection<Attribute> attributes1 = (EcoreUtil.copyAll(group1.getAttributes()));
							Collection<Attribute> attributes2 = (EcoreUtil.copyAll(group2.getAttributes()));
							mergedGroup.getAttributes().addAll(attributes1);
							mergedGroup.getAttributes().addAll(attributes2);
							mergedConceptDefinition.getGroups().add(mergedGroup);
							merged = true;
							processedGroups.add(group2);
							break;
						}
					}
					if (!merged) {
						mergedConceptDefinition.getGroups().add(group1);
					}
					processedGroups.add(group1);
				}
				
				
				for (Group group2 : groups2) {
					if (processedGroups.contains(group2))
						continue;
					boolean merged = false;
					for (Group group1 : groups1) {
						if (processedGroups.contains(group1))
							continue;
						boolean mergeable = isMergeable(group2, group1);
						if (mergeable) {
							Group mergedGroup = ScgFactory.eINSTANCE.createGroup();
							Collection<Attribute> attributes1 = (EcoreUtil.copyAll(group1.getAttributes()));
							Collection<Attribute> attributes2 = (EcoreUtil.copyAll(group2.getAttributes()));
							mergedGroup.getAttributes().addAll(attributes1);
							mergedGroup.getAttributes().addAll(attributes2);
							mergedConceptDefinition.getGroups().add(mergedGroup);
							merged = true;
							processedGroups.add(group1);
							break;
						}
					}
					if (!merged) {
						mergedConceptDefinition.getGroups().add(group2);
					}
					processedGroups.add(group2);
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
	public boolean isMergeable(Group group1, Group group2) {
		boolean mergeable = false;
		for (Attribute attribute1 : group1.getAttributes()) {
			Concept attribute1NameConcept = attribute1.getName();
			for (Attribute attribute2 : group2.getAttributes()) {
				Concept attribute2NameConcept = attribute2.getName();
				if (attribute1NameConcept.getId().equals(attribute2NameConcept.getId())) {
					Expression attribute1ValueExpression = SemanticUtils.getAttributeValueExpression(attribute1);
					Expression attribute2ValueExpression = SemanticUtils.getAttributeValueExpression(attribute2);
					if (subsumptionTester.isSubsumed(attribute1ValueExpression, 
							attribute2ValueExpression)
						|| subsumptionTester.isSubsumed(attribute2ValueExpression, 
								attribute1ValueExpression)) {
						mergeable = true;
					} else {
						// if there is even one name-matched attribute pair, where the subsumption doesn't hold
						return false;
					}
				}
			}
		}
		return mergeable;
	}
}