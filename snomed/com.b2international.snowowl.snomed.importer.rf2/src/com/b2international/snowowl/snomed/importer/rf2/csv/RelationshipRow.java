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
 * Represents a relationship release file row. Provides storage for the
 * following CSV fields:
 * <ul>
 * <li>{@code sourceId}
 * <li>{@code destinationId}
 * <li>{@code relationshipGroup}
 * <li>{@code typeId}
 * <li>{@code characteristicTypeId}
 * <li>{@code modifierId}
 * </ul>
 * 
 */
public class RelationshipRow extends AbstractTerminologyComponentRow {

	public static final String PROP_SOURCE_ID = "sourceId";
	public static final String PROP_DESTINATION_ID = "destinationId";
	public static final String PROP_RELATIONSHIP_GROUP = "relationshipGroup";
	public static final String PROP_TYPE_ID = "typeId";
	public static final String PROP_CHARACTERISTIC_TYPE_ID = "characteristicTypeId";
	public static final String PROP_MODIFIER_ID = "modifierId";
	
	private String sourceId;
	private String destinationId;
	private int relationshipGroup;
	private String typeId;
	private String characteristicTypeId;
	private String modifierId;
	
	public String getSourceId() {
		return sourceId;
	}
	
	public void setSourceId(final String sourceId) {
		this.sourceId = sourceId;
	}
	
	public String getDestinationId() {
		return destinationId;
	}
	
	public void setDestinationId(final String destinationId) {
		this.destinationId = destinationId;
	}
	
	public int getRelationshipGroup() {
		return relationshipGroup;
	}
	
	public void setRelationshipGroup(final int relationshipGroup) {
		this.relationshipGroup = relationshipGroup;
	}
	
	public String getTypeId() {
		return typeId;
	}
	
	public void setTypeId(final String typeId) {
		this.typeId = typeId;
	}
	
	public String getCharacteristicTypeId() {
		return characteristicTypeId;
	}
	
	public void setCharacteristicTypeId(final String characteristicTypeId) {
		this.characteristicTypeId = characteristicTypeId;
	}
	
	public String getModifierId() {
		return modifierId;
	}
	
	public void setModifierId(final String modifierId) {
		this.modifierId = modifierId;
	}

	@Override
	public String toString() {
		return String.format("RelationshipRow [id=%s, effectiveTime=%s, active=%s, moduleId=%s, sourceId=%s, destinationId=%s, " +
				"relationshipGroup=%d, typeId=%s, characteristicTypeId=%s, modifierId=%s]",
				getId(), getEffectiveTime(), isActive(), getModuleId(), getSourceId(), getDestinationId(),
				getRelationshipGroup(), getTypeId(), getCharacteristicTypeId(), getModifierId());
	}
}