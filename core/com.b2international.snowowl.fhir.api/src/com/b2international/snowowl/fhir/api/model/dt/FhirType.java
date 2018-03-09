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
package com.b2international.snowowl.fhir.api.model.dt;

/**
 * FHIR datatypes
 * 
 * @see <a href="https://www.hl7.org/fhir/datatypes.html">FHIR:Data Types</a>
 * @since 6.3
 */
public enum FhirType {
	
	BOOLEAN("Boolean"),
	INTEGER("Integer"),
	DECIMAL("Decimal"),
	STRING("String"),
	URI("Uri"),
	CODE("Code"),
	DATE("Date"),
	DATETIME("DateTime"),
	OID("Oid"),
	
	//complex types
	CODING("Coding");
	
	private String serializedName;
	
	FhirType(String serializedName) {
		this.serializedName = serializedName;
	}
	
	public String getSerializedName() {
		return serializedName;
	}

	/**
	 * Returns the FHIR datatype enum for a given serialized representation
	 * @param typeString
	 * @return
	 */
	public static FhirType fhirTypeOf(String typeString) {
		
		if (typeString == null) throw new NullPointerException("Type parameter is null.");
		
		for (FhirType fhirType : FhirType.values()) {
			if (fhirType.serializedName.equalsIgnoreCase(typeString)) {
				return fhirType;
			}
		}
		throw new IllegalArgumentException("Could not find FHIR data type for serialized representation '" + typeString + "'");
	}
}