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
package com.b2international.snowowl.snomed.core.refset.automap;

import java.util.List;

import com.b2international.commons.StringUtils;

/**
 * Wrapper class for the automapped values (resolved component id, parsed values from flat file)
 * 
 */
public class AutoMapEntry {

	private static final String ALWAYS_PREFIX = "ALWAYS ";
	private static final String EXACT_MATCH_CONCEPT_ID = "447557004";
	private static final String TRUE = "TRUE";
	
	private final List<String> parsedValues;
	private MappingMode mappingMode = MappingMode.EMPTY;
	private MappingState mappingState = MappingState.NOT_ACCEPTED;
	private String autoMappedId;
	private int mapGroup;
	private int mapPriority;
	private String mapRule;
	private String mapAdvice;
	private String correlationId;

	public AutoMapEntry(final List<String> parsedValues) {
		this.parsedValues = parsedValues;
		autoMappedId = "";
		mapGroup = 1;
		mapPriority = 1;
		//Exact match map from SNOMED CT source code to target code
		correlationId = EXACT_MATCH_CONCEPT_ID;
		mapRule = TRUE;
		mapAdvice = ALWAYS_PREFIX + parsedValues.get(0); // ALWAYS\\w${SIN_NUMBER}
	}

	/**
	 * @return the mapping state (e.g. accepted or not accepted)
	 */
	public MappingState getMappingState() {
		return mappingState;
	}
	
	/**
	 * @param mappingState the mapping state to set
	 */
	public void setMappingState(final MappingState mappingState) {
		this.mappingState = mappingState;
	}
	
	/**
	 * @return the mapping mode (e.g. manual, auto or empty)
	 */
	public MappingMode getMappingMode() {
		return mappingMode;
	}

	/**
	 * @param mappingMode the mapping mode to set
	 */
	public void setMappingMode(final MappingMode mappingMode) {
		this.mappingMode = mappingMode;
	}

	/**
	 * @return the SNOMED CT identifier of the mapped concept
	 */
	public String getAutoMappedId() {
		return autoMappedId;
	}

	/**
	 * @param autoMappedId the mapped concept's SNOMED CT identifier to set
	 */
	public void setAutoMappedId(final String autoMappedId) {
		if (StringUtils.isEmpty(autoMappedId)) {
			setMappingMode(MappingMode.EMPTY);
		}
		
		this.autoMappedId = autoMappedId;
	}

	/**
	 * @return the list of values parsed from the original input file
	 */
	public List<String> getParsedValues() {
		return parsedValues;
	}

	/**
	 * Returns with the map group.
	 * @return map group.
	 */
	public int getMapGroup() {
		return mapGroup;
	}

	/**
	 * Sets the map group to a specified value.
	 * @param mapGroup the new map group value. 
	 */
	public void setMapGroup(final int mapGroup) {
		this.mapGroup = mapGroup;
	}

	/**
	 * Returns with the map priority value.
	 * @return the map priority.
	 */
	public int getMapPriority() {
		return mapPriority;
	}

	/**
	 * Sets the map priority to a specified value.
	 * @param mapPriority the new map priority value.
	 */
	public void setMapPriority(final int mapPriority) {
		this.mapPriority = mapPriority;
	}

	/**
	 * Returns with the map rule.
	 * @return the map rule.
	 */
	public String getMapRule() {
		return mapRule;
	}

	/**
	 * Sets the map rule to a specified value.
	 * @param mapRule the new map rule value.
	 */
	public void setMapRule(final String mapRule) {
		this.mapRule = mapRule;
	}

	/**
	 * Returns with the map advice.
	 * @return the map advice.
	 */
	public String getMapAdvice() {
		return mapAdvice;
	}

	/**
	 * Sets the map advice to a specified value.
	 * @param mapAdvice the new map advice value.
	 */
	public void setMapAdvice(final String mapAdvice) {
		this.mapAdvice = mapAdvice;
	}

	/**
	 * Returns with the SNOMED CT concept ID representing the correlation.
	 * @return the SNOMED CT concept identifier of the correlation.
	 */
	public String getCorrelationId() {
		return correlationId;
	}

	/**
	 * Sets the SNOMED CT concept ID of the correlation represented as a concept.
	 * @param correlationId the new concept ID representing the correlation.
	 */
	public void setCorrelationId(final String correlationId) {
		this.correlationId = correlationId;
	}
	
	
}