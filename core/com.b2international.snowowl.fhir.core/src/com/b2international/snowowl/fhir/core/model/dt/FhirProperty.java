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

import java.util.Date;

import com.b2international.snowowl.fhir.core.model.ValidatingBuilder;
import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.annotations.ApiModelProperty;

/**
 * @since 6.4
 * @param <T>
 */
public abstract class FhirProperty {
	
	private final FhirDataType type;
	private final Object value;
	
	public FhirProperty(FhirDataType type, Object value) {
		this.type = type;
		this.value = value;
	}

	@JsonIgnore
	public FhirDataType getType() {
		return type;
	}
	
	@ApiModelProperty(notes = "Code|String|Boolean|Coding|Integer|Datetime")
	public Object getValue() {
		return value;
	}
	
	public static abstract class Builder<T extends FhirProperty, B extends Builder<T, B>> extends ValidatingBuilder<T> {
		
		private Object value;
		private FhirDataType type;

		protected Builder() {}
		
		public final B valueString(String value) {
			return setValue(FhirDataType.STRING, value);
		}

		public final B valueBoolean(Boolean value) {
			return setValue(FhirDataType.BOOLEAN, value);
		}
		
		public final B valueUri(String value) {
			return setValue(FhirDataType.URI, value);
		}
		
		public final B valueCode(String value) {
			return setValue(FhirDataType.CODE, value);
		}
		
		public final B valueCoding(Coding value) {
			return setValue(FhirDataType.CODING, value);
		}
		
		public final B valueInteger(Integer value) {
			return setValue(FhirDataType.INTEGER, value);
		}
		
		public final B valueDecimal(Double value) {
			return setValue(FhirDataType.DECIMAL, value);
		}
		
		public final B valueDateTime(Date value) {
			return setValue(FhirDataType.DATETIME, value);
		}
		
		protected final B setValue(FhirDataType type, Object value) {
			this.type = type;
			this.value = value;
			return getSelf();
		}
		
		protected abstract B getSelf();
		
		protected final Object value() {
			return value;
		}
		
		protected final FhirDataType type() {
			return type;
		}
		
	}
	
}
