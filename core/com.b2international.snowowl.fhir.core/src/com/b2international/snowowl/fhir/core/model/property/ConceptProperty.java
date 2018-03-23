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
package com.b2international.snowowl.fhir.core.model.property;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.b2international.snowowl.fhir.core.codesystems.PropertyType;
import com.b2international.snowowl.fhir.core.model.ValidatingBuilder;
import com.b2international.snowowl.fhir.core.model.dt.Code;
import com.b2international.snowowl.fhir.core.model.serialization.ConceptPropertySerializer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * FHIR Concept return property
 * @since 6.3
 */
@JsonSerialize(using=ConceptPropertySerializer.class)
@JsonInclude(Include.NON_EMPTY) //covers nulls as well
public abstract class ConceptProperty<T> {
	
	//Identifies the property returned (1..1)
	@Valid
	@NotNull
	protected final Code code;
	
	protected final T value;
	
	ConceptProperty(final Code code, final T value) {
		this.code = code;
		this.value = value;
	}
	
	public Code getCode() {
		return code;
	}
	
	public abstract PropertyType getPropertyType();
	
	@JsonIgnore
	public String getCodeValue() {
		return code.getCodeValue();
	}
	
	public T getValue() {
		return value;
	}
	
	public static abstract class Builder<B extends Builder<B, CP, T>, CP extends ConceptProperty<T>, T> extends ValidatingBuilder<CP> {
		
		protected Code code;
		protected T value;

		public B code(final String code) {
			this.code = new Code(code);
			return getSelf();
		}
		
		public B code(final Code code) {
			this.code = code;
			return getSelf();
		}
		
		public B value(final T value) {
			this.value = value;
			return getSelf();
		}
		
		protected abstract B getSelf();
	}

}
