/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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

import com.b2international.snowowl.snomed.common.SnomedRf2Headers;

/**
 * Represents a concrete domain reference set release file row. The class
 * provides storage for the following CSV fields:
 * 
 * <ul>
 * <li>{@code value}
 * <li>{@code relationshipGroup}
 * <li>{@code typeId}
 * <li>{@code characteristicTypeId}
 * </ul>
 */
public class ConcreteDomainRefSetRow extends RefSetRow {

	public static final String PROP_VALUE = SnomedRf2Headers.FIELD_VALUE;
	public static final String PROP_RELATIONSHIP_GROUP = SnomedRf2Headers.FIELD_RELATIONSHIP_GROUP;
	public static final String PROP_TYPE_ID = SnomedRf2Headers.FIELD_TYPE_ID;
	public static final String PROP_CHARACTERISTIC_TYPE_ID = SnomedRf2Headers.FIELD_CHARACTERISTIC_TYPE_ID;

	private String value;
	private int relationshipGroup;
	private String typeId;
	private String characteristicTypeId;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public int getRelationshipGroup() {
		return relationshipGroup;
	}

	public void setRelationshipGroup(int relationshipGroup) {
		this.relationshipGroup = relationshipGroup;
	}

	public String getTypeId() {
		return typeId;
	}

	public void setTypeId(String typeId) {
		this.typeId = typeId;
	}

	public String getCharacteristicTypeId() {
		return characteristicTypeId;
	}

	public void setCharacteristicTypeId(final String characteristicTypeId) {
		this.characteristicTypeId = characteristicTypeId;
	}

	@Override
	public String toString() {
		return String.format("ConcreteDomainRefSetRow [getUuid()=%s, "
				+ "getEffectiveTime()=%s, "
				+ "isActive()=%s, "
				+ "getModuleId()=%s, "
				+ "getRefSetId()=%s, "
				+ "getReferencedComponentId()=%s, "
				+ "getValue()=%s, "
				+ "getRelationshipGroup()=%s, "
				+ "getTypeId()=%s, "
				+ "getCharacteristicTypeId()=%s]",
				getUuid(), 
				getEffectiveTime(), 
				isActive(), 
				getModuleId(), 
				getRefSetId(), 
				getReferencedComponentId(),
				getValue(), 
				getRelationshipGroup(), 
				getTypeId(), 
				getCharacteristicTypeId());
	}
}
