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
package com.b2international.snowowl.fhir.core.model.usagecontext;

import java.util.Collection;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.b2international.snowowl.fhir.core.model.Element;
import com.b2international.snowowl.fhir.core.model.Extension;
import com.b2international.snowowl.fhir.core.model.dt.CodeableConcept;
import com.b2international.snowowl.fhir.core.model.dt.Coding;
import com.b2international.snowowl.fhir.core.model.dt.Quantity;
import com.b2international.snowowl.fhir.core.model.dt.Range;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * 
 * FHIR Usage Context
 * 
 * https://www.hl7.org/fhir/metadatatypes.html#UsageContext
 * 
 * It can be {@link CodeableConcept}, {@link Quantity} and {@link Range} 
 * @since 6.6
 */
@JsonSerialize(using=UsageContextSerializer.class)
public abstract class UsageContext<T> extends Element {

	//Type of the context being specified (1..1)
	//UsageContextType (Extensible)
	@Valid
	@NotNull
	protected final Coding code;
	
	//@Valid
	//@NotEmpty
	@NotNull
	protected final T value;
	
	/**
	 * @param id
	 * @param extensions
	 */
	protected UsageContext(final String id, final Collection<Extension> extensions,
			final Coding code, final T value) {
		super(id, extensions);
		this.code = code;
		this.value = value;
	}
	
	public Coding getCode() {
		return code;
	}
	
	public T getValue() {
		return value;
	}
	
	/**
	 * @return the type to append during serialization
	 */
	public abstract String getType();
	
	public static abstract class Builder<B extends Builder<B, UC, T>, UC extends UsageContext<T>, T> extends Element.Builder<B, UC> {
		
		protected Coding code;
		protected T value;

		public B code(final Coding code) {
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
