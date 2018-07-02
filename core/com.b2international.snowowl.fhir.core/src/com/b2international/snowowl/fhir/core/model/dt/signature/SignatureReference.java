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
package com.b2international.snowowl.fhir.core.model.dt.signature;

import javax.validation.constraints.NotNull;

import com.b2international.snowowl.fhir.core.model.ValidatingBuilder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * FHIR Signature complex datatype
 * 
 * A Signature holds an electronic representation of a signature and its supporting context in a FHIR accessible form. 
 *
 * @see <a href="https://www.hl7.org/fhir/datatypes.html#signature">FHIR:Data Types:Signature</a>
 * @since 6.6
 */
@JsonSerialize(using=SignatureReferenceSerializer.class)
public abstract class SignatureReference<T> {
	
	@NotNull
	protected final T value;
	
	protected SignatureReference(final T value) {
		this.value = value;
	}

	public T getValue() {
		return value;
	}
	
	public abstract String getType();
	
	public static abstract class Builder<B extends Builder<B, SR, T>, SR extends SignatureReference<T>, T> extends ValidatingBuilder<SR> {
			
		protected T value;
	
		public B value(final T value) {
			this.value = value;
			return getSelf();
		}
		
		protected abstract B getSelf();
	}

}
