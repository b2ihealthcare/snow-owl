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
package com.b2international.snowowl.snomed.importer.rf2.csv;

/**
 * Represents complex map type reference set release file rows. The class
 * provides storage for the following CSV fields:
 * <ul>
 * <li>{@code mapGroup}
 * <li>{@code mapPriority}
 * <li>{@code mapRule}
 * <li>{@code mapAdvice}
 * <li>{@code correlationId}
 * </ul>
 * 
 */
public class ComplexMapTypeRefSetRow extends AssociatingRefSetRow {

	public static final String PROP_MAP_GROUP = "mapGroup";
	public static final String PROP_MAP_PRIORITY = "mapPriority";
	public static final String PROP_MAP_RULE = "mapRule";
	public static final String PROP_MAP_ADVICE = "mapAdvice";
	public static final String PROP_CORRELATION_ID = "correlationId";
	public static final String PROP_MAP_CATEGORY_ID = "mapCategoryId";
	
	private int mapGroup;
	private int mapPriority;
	private String mapRule;
	private String mapAdvice;
	private String correlationId;
	private String mapCategoryId;
	
	public int getMapGroup() {
		return mapGroup;
	}
	
	public void setMapGroup(final int mapGroup) {
		this.mapGroup = mapGroup;
	}
	
	public int getMapPriority() {
		return mapPriority;
	}
	
	public void setMapPriority(final int mapPriority) {
		this.mapPriority = mapPriority;
	}
	
	public String getMapRule() {
		return mapRule;
	}
	
	public void setMapRule(final String mapRule) {
		this.mapRule = mapRule;
	}
	
	public String getMapAdvice() {
		return mapAdvice;
	}
	
	public void setMapAdvice(final String mapAdvice) {
		this.mapAdvice = mapAdvice;
	}
	
	public String getCorrelationId() {
		return correlationId;
	}
	
	public void setCorrelationId(final String correlationId) {
		this.correlationId = correlationId;
	}
	
	public String getMapCategoryId() {
		return mapCategoryId;
	}

	public void setMapCategoryId(final String mapCategoryId) {
		this.mapCategoryId = mapCategoryId;
	}

	@Override
	public String toString() {
		return String.format("AssociatingRefSetRow [uuid=%s, effectiveTime=%s, active=%s, moduleId=%s, refsetId=%s, " +
				"referencedComponentId=%s, mapGroup=%d, mapPriority=%d, mapRule=%s, mapAdvice=%s, associatedComponentId=%s, correlationId=%s, mapCategoryId=%s",
				getUuid(), getEffectiveTime(), isActive(), getModuleId(), getRefSetId(),
				getReferencedComponentId(), getMapGroup(), getMapPriority(), getMapRule(), getMapAdvice(), getAssociatedComponentId(), 
				getCorrelationId(), getMapCategoryId());
	}
}