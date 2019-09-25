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
package com.b2international.snowowl.fhir.core.model.dt;

import java.util.Collection;

import com.b2international.snowowl.fhir.core.codesystems.QuantityComparator;
import com.b2international.snowowl.fhir.core.model.Element;
import com.b2international.snowowl.fhir.core.model.Extension;
import com.b2international.snowowl.fhir.core.search.Summary;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * FHIR Base Quantity abstract complex datatype
 * 
 * @see <a href="https://www.hl7.org/fhir/datatypes.html#quantity">FHIR:Data Types:Quantity</a>
 * @since 6.6
 */
public abstract class BaseQuantity extends Element {
	
	@Summary
	@JsonProperty
	private Double value;
	
	@Summary
	protected Code comparator;
	
	public abstract Code getComparator();
	
	@Summary
	@JsonProperty
	private String unit;
	
	@Summary
	@JsonProperty
	private Uri system;
	
	@Summary
	@JsonProperty
	private Code code;
	
	BaseQuantity(String id, Collection<Extension> extensions,
			final Double value, final Code comparator, final String unit, final Uri system, final Code code) {
		super(id, extensions);
		
		this.value = value;
		this.comparator = comparator;
		this.unit = unit;
		this.system = system;
		this.code = code;
	}
	
	public static abstract class Builder<B extends Builder<B, T>, T extends BaseQuantity> extends Element.Builder<B, T> {
		
		protected Double value;
		protected Code comparator;
		protected String unit;
		protected Uri system;
		protected Code code;
		
		public B value(Double value) {
			this.value = value;
			return getSelf();
		}
		
		public B comparator(QuantityComparator comparator) {
			this.comparator = comparator.getCode();
			return getSelf();
		}
		
		public B unit(String unit) {
			this.unit = unit;
			return getSelf();
		}
		
		public B system(String system) {
			this.system = new Uri(system);
			return getSelf();
		}

		public B system(Uri systemUri) {
			this.system = systemUri;
			return getSelf();
		}

		public B code(Code code) {
			this.code = code;
			return getSelf();
		}
		
		public B code(String codeValue) {
			this.code = new Code(codeValue);
			return getSelf();
		}
	}

}
