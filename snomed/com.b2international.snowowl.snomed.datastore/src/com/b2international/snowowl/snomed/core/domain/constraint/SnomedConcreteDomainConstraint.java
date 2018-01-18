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
package com.b2international.snowowl.snomed.core.domain.constraint;

import com.b2international.snowowl.snomed.snomedrefset.DataType;

/**
 * @since 5.7
 */
public final class SnomedConcreteDomainConstraint extends SnomedConstraint {

	private DataType valueType;
	private String attributeName;
	private String displayName;
	private String characteristicTypeExpresion;

	public DataType getValueType() {
		return valueType;
	}

	public String getAttributeName() {
		return attributeName;
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getCharacteristicTypeExpresion() {
		return characteristicTypeExpresion;
	}

	public void setValueType(DataType valueType) {
		this.valueType = valueType;
	}

	public void setAttributeName(String typeExpression) {
		this.attributeName = typeExpression;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public void setCharacteristicTypeExpresion(String characteristicTypeExpresion) {
		this.characteristicTypeExpresion = characteristicTypeExpresion;
	}
}
