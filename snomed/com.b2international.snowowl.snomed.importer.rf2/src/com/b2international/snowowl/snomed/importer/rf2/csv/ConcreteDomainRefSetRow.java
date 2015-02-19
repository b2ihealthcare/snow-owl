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

import com.b2international.commons.StringUtils;

/**
 * Represents a concrete domain reference set release file row. The class
 * provides storage for the following CSV fields:
 * <ul>
 * <li>{@code uomId}
 * <li>{@code operatorId}
 * <li>{@code attributeName} (optional)
 * <li>{@code dataValue}
 * <li>{@code characteristicTypeId}
 * </ul>
 * 
 */
public class ConcreteDomainRefSetRow extends RefSetRow {

	public static final String PROP_UOM_ID = "uomId";
	public static final String PROP_OPERATOR_ID = "operatorId";
	public static final String PROP_ATTRIBUTE_NAME = "attributeName";
	public static final String PROP_DATA_VALUE = "dataValue";
	public static final String PROP_CHARACTERISTIC_TYPE_ID = "characteristicTypeId";
	
	private String uomId;
	private String operatorId;
	private String attributeName = "";
	private String dataValue;
	private String characteristicTypeId;

	public String getUomId() {
		return uomId;
	}

	public void setUomId(final String uomId) {
		if (StringUtils.isEmpty(uomId)) {
			this.uomId = null;
		} else {
			this.uomId = uomId;
		}
	}

	public String getOperatorId() {
		return operatorId;
	}

	public void setOperatorId(final String operatorId) {
		this.operatorId = operatorId;
	}

	public String getAttributeName() {
		return attributeName;
	}

	public void setAttributeName(final String attributeName) {
		this.attributeName = attributeName;
	}

	public String getDataValue() {
		return dataValue;
	}

	public void setDataValue(final String dataValue) {
		this.dataValue = dataValue;
	}

	public String getCharacteristicTypeId() {
		return characteristicTypeId;
	}
	
	public void setCharacteristicTypeId(final String characteristicTypeId) {
		this.characteristicTypeId = characteristicTypeId;
	}

	@Override
	public String toString() {
		return String.format("ConcreteDomainRefSetRow [uuid=%s, effectiveTime=%s, active=%s, moduleId=%s, refsetId=%s, " +
				"referencedComponentId=%s, uomId=%s, operatorId=%s, attributeName=%s, dataValue=%s, characteristicTypeId=%s",
				getUuid(), getEffectiveTime(), isActive(), getModuleId(), getRefSetId(),
				getReferencedComponentId(), getUomId(), getOperatorId(), getAttributeName(), getDataValue(), getCharacteristicTypeId());
	}

}