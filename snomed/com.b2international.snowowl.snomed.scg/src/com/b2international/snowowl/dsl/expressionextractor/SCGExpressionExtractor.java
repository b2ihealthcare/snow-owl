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
package com.b2international.snowowl.dsl.expressionextractor;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.EList;

import com.b2international.snowowl.dsl.scg.Attribute;
import com.b2international.snowowl.dsl.scg.AttributeValue;
import com.b2international.snowowl.dsl.scg.Concept;
import com.b2international.snowowl.dsl.scg.Expression;
import com.b2international.snowowl.dsl.scg.Group;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * This class is for extracting concept ids from <b>SCG</b> expression and group them property.
 * Focus concept ids are for creating "is a" relationships.
 * Ungroupped attributes (key is relationship type, value is the destination of the relationship) have the group id of 0.
 * Groupped attributes (key is relationship type, value is the destination of the relationship) group id increased incrementally.
 * 
 *
 */
public class SCGExpressionExtractor {

	private final Expression expression;
	
	public SCGExpressionExtractor(Expression expression) {
		this.expression = expression;
	}
	
	/**
	 * Returns the concept id list of the focus concepts of the expression. Used for "is a" relationships.
	 * 
	 * @return List<String> concept id list.
	 */
	public List<String> getFocusConceptIdList() {
		return FluentIterable.from(expression.getConcepts()).transform(new Function<Concept, String>() {
			@Override public String apply(Concept input) {
				return input.getId();
			}
		}).toList();
	}
	
	private Map<String, String> getUnGroupedAttributePairs() {
		EList<Attribute> unGroupedAttributes = expression.getAttributes();
		
		return extractAttributeConcepts(unGroupedAttributes);
	}
	
	private Map<String, String> extractAttributeConcepts(List<Attribute> attributes) {
		Map<String, String> attributeConceptIdMap = Maps.newHashMap();
		for (Attribute attribute : attributes) {
			Concept typeConcept = attribute.getName();
			
			AttributeValue value = attribute.getValue();
			
			if (!(value instanceof Concept)) {
				// probably because of invalid syntax
				return Collections.emptyMap();
			}
			
			Concept destination = (Concept) value;
			
			attributeConceptIdMap.put(typeConcept.getId(), destination.getId());
		}
		
		return attributeConceptIdMap;
	}
	
	/**
	 * Returns ungroupped (with group id of 0) and grouped attribute concept id pairs (group id increased incrementally).
	 * 
	 * @return List<ExtractedSCGAttributeGroup>
	 */
	public List<ExtractedSCGAttributeGroup> getGroupConcepts() {
		List<ExtractedSCGAttributeGroup> attributeGroupList = Lists.newArrayList();
		EList<Group> groups = expression.getGroups();
		
		Map<String, String> unGroupedAttributePairs = getUnGroupedAttributePairs();
		
		int i = 0;
		
		if (unGroupedAttributePairs.size() != 0) {
			ExtractedSCGAttributeGroup unGrouppedAttributePairs = new ExtractedSCGAttributeGroup();
			
			unGrouppedAttributePairs.setAttributeConceptIdMap(unGroupedAttributePairs);
			unGrouppedAttributePairs.setGroupId(0);
			attributeGroupList.add(unGrouppedAttributePairs);
		}
		
		i++;
		
		for (Group group : groups) {
			EList<Attribute> attributes = group.getAttributes();
			Map<String, String> extractAttributeConcepts = extractAttributeConcepts(attributes);
			
			ExtractedSCGAttributeGroup extractedSCGAttributeGroup = new ExtractedSCGAttributeGroup();
			
			extractedSCGAttributeGroup.setAttributeConceptIdMap(extractAttributeConcepts);
			extractedSCGAttributeGroup.setGroupId(i++);
			
			attributeGroupList.add(extractedSCGAttributeGroup);
		}
		
		return attributeGroupList;
	}
}