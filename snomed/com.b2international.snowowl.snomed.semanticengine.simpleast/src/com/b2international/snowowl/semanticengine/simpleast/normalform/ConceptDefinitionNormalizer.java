/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationship;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationships;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.dsl.query.queryast.AttributeClause;
import com.b2international.snowowl.snomed.dsl.query.queryast.ConceptRef;
import com.b2international.snowowl.snomed.dsl.query.queryast.RValue;
import com.b2international.snowowl.snomed.dsl.query.queryast.SubExpression;
import com.b2international.snowowl.snomed.dsl.query.queryast.ecoreastFactory;

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

	private final SimpleAstExpressionNormalFormGenerator normalFormGenerator;
	
	public ConceptDefinitionNormalizer(String branch) {
		this.normalFormGenerator = new SimpleAstExpressionNormalFormGenerator(branch);
	}
	
	/**
	 * @param focusConcepts
	 * @return the normalized concept definition of each focus concept
	 */
	public Map<ConceptRef, ConceptDefinition> getNormalizedConceptDefinitions(Collection<ConceptRef> focusConcepts) {
		Map<ConceptRef, Map<Integer, List<AttributeClause>>> conceptDefinitionMap = new HashMap<ConceptRef, Map<Integer,List<AttributeClause>>>();
		for (ConceptRef focusConcept : focusConcepts) {
			Map<Integer, List<AttributeClause>> attributeGroupMap = new HashMap<Integer, List<AttributeClause>>();
			/*
			 * 1. get defining attributes
			 * 2. normalizeAttributes(defining attributes)
			 */
			
			final SnomedRelationships outboundRelationships = SnomedRequests.prepareSearchRelationship()
					.all()
					.filterByActive(true)
					.filterByType(Concepts.IS_A)
					.filterBySource(focusConcept.getConceptId())
					// TODO fix branch path
					.build(SnomedDatastoreActivator.REPOSITORY_UUID, BranchPathUtils.createMainPath().getPath())
					.execute(ApplicationContext.getServiceForClass(IEventBus.class))
					.getSync();
			for (SnomedRelationship relationship : outboundRelationships) {
				int relationshipGroup = relationship.getGroup();
				AttributeClause attribute = ecoreastFactory.eINSTANCE.createAttributeClause();
				ConceptRef name = ecoreastFactory.eINSTANCE.createConceptRef();
				name.setConceptId(relationship.getTypeId());
				attribute.setLeft(name);
				ConceptRef value = ecoreastFactory.eINSTANCE.createConceptRef();
				value.setConceptId(relationship.getDestinationId());
				
				RValue normalizedValueExpression = normalFormGenerator.getLongNormalForm(value);
				if (normalizedValueExpression instanceof ConceptRef) {
					attribute.setRight(normalizedValueExpression);
				} else {
					SubExpression subExpression = ecoreastFactory.eINSTANCE.createSubExpression();
					subExpression.setValue(normalizedValueExpression);
					attribute.setRight(subExpression);
				}
				putAttributeInAttributeClauseGroupMap(attribute, relationshipGroup, attributeGroupMap);
			}
			conceptDefinitionMap.put(focusConcept, attributeGroupMap);
		}
		
		
		Map<ConceptRef, ConceptDefinition> result = new HashMap<ConceptRef, ConceptDefinition>();
		for (Entry<ConceptRef, Map<Integer, List<AttributeClause>>> conceptMapEntry : conceptDefinitionMap.entrySet()) {
			Map<Integer, List<AttributeClause>> conceptMapValue = conceptMapEntry.getValue();
			ConceptDefinition conceptDefinitions = new ConceptDefinition();
			for (Map.Entry<Integer, List<AttributeClause>> relationshipGroupMapEntry : conceptMapValue.entrySet()) {
				int relationshipGroup = relationshipGroupMapEntry.getKey();
				
				if (relationshipGroup == 0) {
					// ungrouped attributes
					conceptDefinitions.getUngroupedAttributes().addAll(relationshipGroupMapEntry.getValue());
				} else {
					// attribute groups
					AttributeClauseList attributeGroup = new AttributeClauseList();
					attributeGroup.getAttributeClauses().addAll(relationshipGroupMapEntry.getValue());
					conceptDefinitions.getAttributeClauseLists().add(attributeGroup);
				}
			}
			
			result.put(conceptMapEntry.getKey(), conceptDefinitions);
		}
		
		return result;
	}

	private void putAttributeInAttributeClauseGroupMap(AttributeClause attribute, int group, Map<Integer, List<AttributeClause>> map) {
		List<AttributeClause> list = map.get(group);
		if (list == null) {
			list = new ArrayList<AttributeClause>();
			map.put(group, list);
		}
		list.add(attribute);
	}
}