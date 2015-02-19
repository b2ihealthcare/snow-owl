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
import java.util.Set;

import com.b2international.snowowl.semanticengine.simpleast.subsumption.SubsumptionTester;
import com.b2international.snowowl.snomed.dsl.query.queryast.AttributeClause;

/**
 * <b>5.3.4.5	Remove redundant elements from the merged definition</b><br/>
 * Check each group in the target definition and, within that group, compare the values of any name-matched attributes.
 * <ul><li>If an attribute in the group has a value that subsumes the value of another name- matched attribute in the same group, 
 * remove that attribute from this group in the target definition.</li>
 * Check the ungrouped set of attributes.
 * <li>If any ungrouped attribute has a value that subsumes the value of a name-matched attribute, remove this ungrouped 
 * attribute from the target definition.</li>
 * </ul>
 * 
 * <em>Note:<br/>
 * The removal of redundancies described only applies to name-matched pairs of attributes. It does not affect attributes 
 * that are redundant only because they are present in the definitions of the primitive focus concepts. Supertype (�is a�) 
 * relationships are ignored during this stage of processing.</em><br/><br/>
 * 
 */
public class ConceptDefinitionAttributeRedundancyFilter {

	private final SubsumptionTester subsumptionTester;

	public ConceptDefinitionAttributeRedundancyFilter(SubsumptionTester subsumptionTester) {
		this.subsumptionTester = subsumptionTester;
	}
	
	/**
	 * @param conceptDefinition	the {@link ConceptDefinition} to filter for redundant attributes
	 * @return the filtered {@link ConceptDefinition}
	 */
	public ConceptDefinition getFilteredConceptDefinition(ConceptDefinition conceptDefinition) {
		ConceptDefinition filteredConceptDefinition = new ConceptDefinition();

		// attribute groups
		for (AttributeClauseList attributeGroup : conceptDefinition.getAttributeClauseLists()) {
			filteredConceptDefinition.getAttributeClauseLists().add(filterRedundantAttributes(attributeGroup));
		}
		
		// ungrouped attributes
		filteredConceptDefinition.getUngroupedAttributes().addAll(filterRedundantAttributes(conceptDefinition.getUngroupedAttributes()));
		
		return filteredConceptDefinition;
	}
	
	private AttributeClauseList filterRedundantAttributes(AttributeClauseList attributeGroup) {
		AttributeClauseList filteredAttributeClauseGroup = new AttributeClauseList();
		filteredAttributeClauseGroup.getAttributeClauses().addAll(filterRedundantAttributes(attributeGroup.getAttributeClauses()));
		return filteredAttributeClauseGroup;
	}
	
	private Set<AttributeClause> filterRedundantAttributes(Collection<AttributeClause> attributes) {
		Set<AttributeClause> filteredAttributes = new HashSet<AttributeClause>();
		
		/*
		 * for each attribute
		 * 		for each filtered attribute
		 * 			if attribute subsumes filtered attribute
		 * 				remove filtered
		 * 				add attribute
		 */
		
		for (AttributeClause outerAttribute : attributes) {
			AttributeClause subsumedFilteredAttribute = subsumesFilteredAttribute(filteredAttributes, outerAttribute);
			if (subsumedFilteredAttribute != null) {
				filteredAttributes.remove(subsumedFilteredAttribute);
				filteredAttributes.add(outerAttribute);
			} else if (subsumedByFilteredAttribute(filteredAttributes, outerAttribute) == null){
				filteredAttributes.add(outerAttribute);
			}
		}
		
		return filteredAttributes;
	}

	private AttributeClause subsumesFilteredAttribute(Set<AttributeClause> filteredAttributes, AttributeClause attribute) {
		for (AttributeClause filteredAttribute : filteredAttributes) {
			if (subsumptionTester.isSubsumed(attribute, filteredAttribute)) {
				return filteredAttribute;
			}
		}
		return null;
	}
	
	private AttributeClause subsumedByFilteredAttribute(Set<AttributeClause> filteredAttributes, AttributeClause attribute) {
		for (AttributeClause filteredAttribute : filteredAttributes) {
			if (subsumptionTester.isSubsumed(filteredAttribute, attribute)) {
				return filteredAttribute;
			}
		}
		return null;
	}
}