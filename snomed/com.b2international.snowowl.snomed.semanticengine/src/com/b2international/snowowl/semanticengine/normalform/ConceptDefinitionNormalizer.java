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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.b2international.snowowl.core.api.browser.IClientTerminologyBrowser;
import com.b2international.snowowl.dsl.scg.Attribute;
import com.b2international.snowowl.dsl.scg.AttributeValue;
import com.b2international.snowowl.dsl.scg.Concept;
import com.b2international.snowowl.dsl.scg.Expression;
import com.b2international.snowowl.dsl.scg.Group;
import com.b2international.snowowl.dsl.scg.ScgFactory;
import com.b2international.snowowl.semanticengine.utils.SemanticUtils;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.datastore.SnomedClientStatementBrowser;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;

/**
 * <b>5.3.3.1	The set of normalized definitions of each focus concept</b><br/>
 * The set of normalized definitions includes a separate normalized definition for each focus concept,
 * <ul><li>
 * The normalized definition includes</li>
 * <ul><li>All ungrouped relationships</li>
 * <li>All relationship groups complete with contained relationships</li></ul>
 * <li>All relationship values are normalized by recursively following the full set of rules 
 * described in section 5.3.</li>
 * </ul>
 * Note: Storage of pre-computed normalized form of concept definitions simplifies this process as 
 * it removes the requirement for recursive processing of definitions at run time.
 * The set of normalized definitions is passed to the "Merge definitions" process (5.3.4).
 * 
 */
public class ConceptDefinitionNormalizer {

	private final IClientTerminologyBrowser<SnomedConceptIndexEntry, String> terminologyBrowser;
	private final ScgExpressionNormalFormGenerator normalFormGenerator;
	private final SnomedClientStatementBrowser statementBrowser;
	
	public ConceptDefinitionNormalizer(IClientTerminologyBrowser<SnomedConceptIndexEntry, String> terminologyBrowser, SnomedClientStatementBrowser statementBrowser) {
		this.terminologyBrowser = terminologyBrowser;
		this.statementBrowser = statementBrowser;
		this.normalFormGenerator = new ScgExpressionNormalFormGenerator(terminologyBrowser, statementBrowser);
		
	}
	
	/**
	 * @param focusConcepts
	 * @return the normalized concept definition of each focus concept
	 */
	public Map<Concept, ConceptDefinition> getNormalizedConceptDefinitions(Collection<Concept> focusConcepts) {
		Map<Concept, Map<Integer, List<Attribute>>> conceptDefinitionMap = new HashMap<Concept, Map<Integer,List<Attribute>>>();
		for (Concept focusConcept : focusConcepts) {
			Map<Integer, List<Attribute>> GroupMap = new HashMap<Integer, List<Attribute>>();
			/*
			 * 1. get defining attributes
			 * 2. normalizeAttributes(defining attributes)
			 */
			//int internalID = terminologyBrowser.resolve(focusConcept.getId());
			//int[] outgoingRelationships = terminologyBrowser.getData().outgoingRelationships[internalID];
			final Collection<SnomedRelationshipIndexEntry> outboundRelationships = statementBrowser.getActiveOutboundStatementsById(focusConcept.getId());
			//for (int i = 0; i < outgoingRelationships.length; i++) {
			for (SnomedRelationshipIndexEntry relationship : outboundRelationships) {
				if (!relationship.getAttributeId().equals(Concepts.IS_A) 
						&& !relationship.isAdditional()) {
					
					int relationshipGroup = relationship.getGroup();
					Attribute attribute = ScgFactory.eINSTANCE.createAttribute();
					Concept nameConcept = ScgFactory.eINSTANCE.createConcept();
					nameConcept.setId(relationship.getAttributeId());
					attribute.setName(nameConcept);
					Expression valueExpression = ScgFactory.eINSTANCE.createExpression();
					Concept valueConcept = ScgFactory.eINSTANCE.createConcept();
					valueConcept.setId(relationship.getValueId());
					valueExpression.getConcepts().add(valueConcept);
					Expression valueNormalFormExpression = normalFormGenerator.getLongNormalForm(valueExpression);
					// only build subexpression, if necessary
					AttributeValue value = SemanticUtils.buildRValue(valueNormalFormExpression);
					attribute.setValue(value);
					putAttributeInGroupMap(attribute, relationshipGroup, GroupMap);
				}
			}
			conceptDefinitionMap.put(focusConcept, GroupMap);
		}
		
		
		Map<Concept, ConceptDefinition> result = new HashMap<Concept, ConceptDefinition>();
		for (Entry<Concept, Map<Integer, List<Attribute>>> conceptMapEntry : conceptDefinitionMap.entrySet()) {
			Map<Integer, List<Attribute>> conceptMapValue = conceptMapEntry.getValue();
			ConceptDefinition conceptDefinitions = new ConceptDefinition();
			for (Map.Entry<Integer, List<Attribute>> relationshipGroupMapEntry : conceptMapValue.entrySet()) {
				int relationshipGroup = relationshipGroupMapEntry.getKey();
				
				if (relationshipGroup == 0) {
					// ungrouped attributes
					conceptDefinitions.getUngroupedAttributes().addAll(relationshipGroupMapEntry.getValue());
				} else {
					// attribute groups
					Group group = ScgFactory.eINSTANCE.createGroup();
					group.getAttributes().addAll(relationshipGroupMapEntry.getValue());
					conceptDefinitions.getGroups().add(group);
				}
			}
			
			result.put(conceptMapEntry.getKey(), conceptDefinitions);
		}
		
		return result;
	}

	private void putAttributeInGroupMap(Attribute attribute, Integer group, Map<Integer, List<Attribute>> map) {
		List<Attribute> list = map.get(group);
		if (list == null) {
			list = new ArrayList<Attribute>();
			map.put(group, list);
		}
		list.add(attribute);
	}
}