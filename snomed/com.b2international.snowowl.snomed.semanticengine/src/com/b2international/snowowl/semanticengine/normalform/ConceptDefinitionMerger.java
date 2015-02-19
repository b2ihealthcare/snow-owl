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

import java.util.Map;

import com.b2international.snowowl.dsl.scg.Concept;
import com.b2international.snowowl.semanticengine.subsumption.SubsumptionTester;

/**
 * <b>5.3.4	Merge definitions</b><br/><br/>
 * <b>5.3.4.1 Overview</b><br/>
 * The set of normalized definitions derived from the "Normalize focus concepts" 
 * process (5.3.3) are merged with one another to remove redundancy. Then the 
 * normalized refinement is merged with the pre-merged definition to create a single 
 * refinement which expresses the full set of definitions and refinements without 
 * unnecessary redundancy.<br/>
 * The rules applied to the merger are described below for grouped and ungrouped attributes.</br>
 * Group merging is completed before applying any ungrouped relationships. This ensures that, 
 * where appropriate, ungrouped attributes are applied to the correct groups in the output.</br>
 * Redundant attributes are not removed until the merger process is complete. This ensures that 
 * the full set of attributes is available to allow matching throughout the process of merging.<br/><br/>
 * <b>5.3.4.2	Attribute names and attribute hierarchies</b><br/>
 * The following sections on merging groups and attributes refer to "name-matched" attributes. 
 * Two or more attributes in a definition or expression are "name-matched" if they have the 
 * same attribute name.<br/>
 * For example, the attribute "procedure site"="appendix structure" is name-matched by the 
 * attribute "procedure site"="entire femur".<br/>
 * However, consideration also needs to be given to hierarchical relationships between 
 * different "attribute names". For example, "procedure site � direct" and "procedure site � indirect" 
 * are subtypes of "procedure site".<br/>
 * The simplest approach that can be consistently applied is to treat attributes that have 
 * subsumed names as name-matched for the purposes of group and value merging. The more specific 
 * attribute name is then applied to the merged attribute in the target definition. This means 
 * that the same rules apply for merging the values of "procedure site" and 
 * "procedure site � direct" as apply to mergers of attributes with identical names and that 
 * the name "procedure site - direct" would then be applied to any values that were merged in this way.<br><br>
 * 
 * <em>Progress note:<br>
 * Review of a number of practical examples suggests that there may be some unexpected consequences 
 * of this approach. For this reason, while the issues that arise are studied further, implementers 
 * are recommended only to merge literal name-matched attributes.</em><br/><br/>
 * 
 */
public class ConceptDefinitionMerger {
 
	
	private final SubsumptionTester subsumptionTester;
	
	public ConceptDefinitionMerger(SubsumptionTester subsumptionTester) {
		this.subsumptionTester = subsumptionTester;
	}

	/**
	 * <b>5.3.4.3	Merging groups</b><br/>
	 * See {@link GroupMerger}</br></br>
	 * 
	 * <b>5.3.4.4	Merging ungrouped attributes</b><br/>
	 * See {@link UngroupedAttributesMerger}</br></br>
	 * 
	 * <b>5.3.4.5	Remove redundant elements from the merged definition</b><br/>
	 * See {@link ConceptDefinitionAttributeRedundancyFilter}<br/><br/>
	 * 
	 * <b>5.3.4.6	Completion of the definition merging</b><br/>
	 * Once the focus concept definitions have been merged, the target definition is passed to the "Merge refinement" process (5.3.5).
	 * 
	 * @param conceptDefinitions
	 * @param ungroupedAttributes
	 * @return the merged concept definition
	 */
	public ConceptDefinition mergeDefinitions(Map<Concept, ConceptDefinition> conceptDefinitions) {
		ConceptDefinition mergedConceptDefinition = new ConceptDefinition();
		GroupMerger GroupMerger = new GroupMerger(subsumptionTester);
		GroupMerger.mergeGroups(conceptDefinitions, mergedConceptDefinition);
		UngroupedAttributesMerger ungroupedAttributeSetMerger = new UngroupedAttributesMerger(subsumptionTester);
		ungroupedAttributeSetMerger.mergeUngroupedAttributes(conceptDefinitions, mergedConceptDefinition);
		ConceptDefinitionAttributeRedundancyFilter conceptDefinitionRedundancyFilter = new ConceptDefinitionAttributeRedundancyFilter(subsumptionTester);
		return conceptDefinitionRedundancyFilter.getFilteredConceptDefinition(mergedConceptDefinition);
	}
}