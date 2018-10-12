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
package com.b2international.snowowl.fhir.core.model.valueset.expansion;

import javax.validation.Valid;

import org.hibernate.validator.constraints.NotEmpty;

import com.b2international.snowowl.fhir.core.model.ValidatingBuilder;
import com.b2international.snowowl.fhir.core.model.dt.FhirDataType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Value set expansion parameter
 * 
 * @since 6.7
 */
@JsonSerialize(using=ExpansionParameterSerializer.class)
@JsonInclude(Include.NON_EMPTY) //covers nulls as well
public abstract class Parameter<T> {

	// Name as assigned by the server
	@NotEmpty
	protected final String name;

	// value of the named parameter
	@Valid
	protected final T value;

	Parameter(final String name, final T value) {
		this.name = name;
		this.value = value;
	}
	
	public String getName() {
		return name;
	}

	public T getValue() {
		return value;
	}
	
	@JsonIgnore
	public abstract FhirDataType getType();

	public static abstract class Builder<B extends Builder<B, P, T>, P extends Parameter<T>, T> extends ValidatingBuilder<P> {

		protected String name;
		protected T value;

		public B name(final String name) {
			this.name = name;
			return getSelf();
		}

		public B value(final T value) {
			this.value = value;
			return getSelf();
		}

		protected abstract B getSelf();
	}

}
