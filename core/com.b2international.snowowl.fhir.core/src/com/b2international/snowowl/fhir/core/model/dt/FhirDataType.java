/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.fhir.core.model.dt;

import com.b2international.snowowl.fhir.core.codesystems.OperationOutcomeCode;
import com.b2international.snowowl.fhir.core.exceptions.FhirException;

/**
 * FHIR Data Types.
 * 
 * @see <a href="https://www.hl7.org/fhir/datatypes.html">FHIR:Data Types</a>
 * @since 6.4
 */
public enum FhirDataType {
	
	BOOLEAN("valueBoolean"),
	INTEGER("valueInteger"),
	DECIMAL("valueDecimal"),
	STRING("valueString"),
	URI("valueUri"),
	CODE("valueCode"),
	DATE("valueDate"),
	DATETIME("valueDateTime"),
	OID("valueOid"),
	
	//complex types
	CODING("valueCoding"), 
	
	// references to complex types with specific type field name
	PART("part");
	
	private String serializedName;
	
	FhirDataType(String serializedName) {
		this.serializedName = serializedName;
	}
	
	public String getSerializedName() {
		return serializedName;
	}

	public static FhirDataType getBySerializedName(String serializedName) {
		if (serializedName == null) throw new NullPointerException("SerializedName is null.");
		
		for (FhirDataType fhirType : FhirDataType.values()) {
			if (fhirType.serializedName.equalsIgnoreCase(serializedName)) {
				return fhirType;
			}
		}
		throw FhirException.createFhirError("Could not find FHIR data type for serialized representation '" + serializedName + "'", OperationOutcomeCode.MSG_PARAM_INVALID);
	}
}