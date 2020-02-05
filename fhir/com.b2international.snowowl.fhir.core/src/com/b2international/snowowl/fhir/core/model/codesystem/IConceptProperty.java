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
package com.b2international.snowowl.fhir.core.model.codesystem;

import java.util.function.Supplier;

import com.b2international.snowowl.fhir.core.codesystems.ConceptPropertyType;
import com.b2international.snowowl.fhir.core.codesystems.FhirCodeSystem;
import com.b2international.snowowl.fhir.core.model.codesystem.Property.Builder;
import com.b2international.snowowl.fhir.core.model.dt.Code;

/**
 * @since 6.4
 */
public interface IConceptProperty extends FhirCodeSystem {

	/**
	 * Returns the FHIR type of the property.
	 * @return
	 */
	ConceptPropertyType getConceptPropertyType();
	
	/**
	 * Returns the ID of the property. 
	 * @return
	 */
	default Code getType() {
		return getConceptPropertyType().getCode();
	}
	
	default Property propertyOf(Object value, String description) {
		Builder prop = Property.builder()
				.code(getCodeValue())
				.description(description);
		
		switch (getConceptPropertyType()) {
		case CODE:
			prop.valueCode((String) value);
			break;
		case BOOLEAN:
			prop.valueBoolean((Boolean) value);
			break;
		case STRING:
			prop.valueString((String) value);
			break;
		default: 
			throw new UnsupportedOperationException("Unsupported property type " + getConceptPropertyType());
		}
		
		return prop.build();
	}
	
	/**
	 * Creates a property for the value without description
	 * @param value
	 * @return
	 */
	default Property propertyOf(Object value) {
		Builder prop = Property.builder()
				.code(getCodeValue());
		
		switch (getConceptPropertyType()) {
		case CODE:
			prop.valueCode((String) value);
			break;
		case BOOLEAN:
			prop.valueBoolean((Boolean) value);
			break;
		case STRING:
			prop.valueString((String) value);
			break;
		default: 
			throw new UnsupportedOperationException("Unsupported property type " + getConceptPropertyType());
		}
		return prop.build();
	}
	
	/**
	 * Creates a property for the supplier's value without description
	 * @param value
	 * @return
	 */
	default Property propertyOf(Supplier<?> function) {
		
		Builder prop = Property.builder()
				.code(getCodeValue());
		
		switch (getConceptPropertyType()) {
		case CODE:
			prop.valueCode((String) function.get());
			break;
		case BOOLEAN:
			prop.valueBoolean((Boolean) function.get());
			break;
		case STRING:
			prop.valueString((String) function.get());
			break;
		default: 
			throw new UnsupportedOperationException("Unsupported property type " + getConceptPropertyType());
		}
		return prop.build();
	}
	
	/**
	 * Class that represents {@link IConceptProperty} computed dynamically from certain data sources, code systems, etc..
	 * 
	 * @since 6.4
	 */
	final class Dynamic implements IConceptProperty {

		private final String codeSystemUri;
		private final String displayName;
		private final String code;
		private final ConceptPropertyType propertyType;

		private Dynamic(String codeSystemUri, String displayName, String code, ConceptPropertyType propertyType) {
			this.codeSystemUri = codeSystemUri;
			this.displayName = displayName;
			this.code = code;
			this.propertyType = propertyType;
		}
		
		@Override
		public String getCodeSystemUri() {
			return codeSystemUri;
		}

		@Override
		public String getDisplayName() {
			return displayName;
		}

		@Override
		public String getCodeValue() {
			return code;
		}
		
		@Override
		public ConceptPropertyType getConceptPropertyType() {
			return propertyType;
		}
		
		public static IConceptProperty valueCode(String codeSystemUri, String displayName, String code) {
			return new Dynamic(codeSystemUri, displayName, code, ConceptPropertyType.CODE);
		}
		
		public static IConceptProperty valueBoolean(String codeSystemUri, String displayName, String code) {
			return new Dynamic(codeSystemUri, displayName, code, ConceptPropertyType.BOOLEAN);
		}
		
		public static IConceptProperty valueString(String codeSystemUri, String displayName, String code) {
			return new Dynamic(codeSystemUri, displayName, code, ConceptPropertyType.STRING);
		}
		
	}
	
}
