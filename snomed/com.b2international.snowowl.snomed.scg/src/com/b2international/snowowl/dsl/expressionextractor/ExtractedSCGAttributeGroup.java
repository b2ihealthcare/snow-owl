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

import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.Maps;

/**
 * PoJo class for collecting the extracted attribute groups (concept ids actually) from SCG expression.
 * Concept id map key is used as relationship type concept, value is the destination concept of the relationship.
 * 
 *
 */
public class ExtractedSCGAttributeGroup {
	
	// relationship type concept id, relationship destination concept id
	private Map<String, String> attributeConceptIdMap;
	
	private int groupId;
	
	public ExtractedSCGAttributeGroup() {
		attributeConceptIdMap = Maps.newHashMap();
	}

	public int getGroupId() {
		return groupId;
	}
	
	/**
	 * 
	 * @return Map<String, String> relationship type concept id, relationship destination concept id
	 */
	public Map<String, String> getAttributeConceptIdMap() {
		return attributeConceptIdMap;
	}
	
	/**
	 * Map<String, String> relationship type concept id, relationship destination concept id
	 */
	public void setAttributeConceptIdMap(Map<String, String> attributeConceptIdMap) {
		this.attributeConceptIdMap = attributeConceptIdMap;
	}
	
	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		for (Entry<String, String> entry : attributeConceptIdMap.entrySet()) {
			sb.append(entry.getKey() + ":" + entry.getValue() + " ");
		}
		sb.append("] = " + groupId);
		
		return sb.toString();
	}
}