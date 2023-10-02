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
package com.b2international.snowowl.fhir.core.model.conceptmap;

import com.b2international.snowowl.fhir.core.model.ValidatingBuilder;
import com.b2international.snowowl.fhir.core.model.dt.Coding;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

/**
 * Concept Map translate result match.product parameter
 * 
 * @since 7.1
 */
@JsonDeserialize(builder = Product.Builder.class)
@JsonPropertyOrder({"element", "concept"})
public class Product {
	
	//The element for this product (0..1)
	private final Uri element;
	
	//The value for this product (0..1)
	private final Coding concept;
	
	Product(final Uri element, final Coding concept) {
		this.element = element;
		this.concept = concept;
	}
	
	public Uri getElement() {
		return element;
	}
	
	public Coding getConcept() {
		return concept;
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	@JsonPOJOBuilder(withPrefix="")
	public static class Builder extends ValidatingBuilder<Product>{
		
		private Uri element;
		private Coding concept;

		public Builder element(final String element) {
			this.element = new Uri(element);
			return this;
		}
		
		public Builder element(final Uri element) {
			this.element = element;
			return this;
		}
		
		public Builder concept(final Coding concept) {
			this.concept = concept;
			return this;
		}
		
		public Builder concept(String code, String system, String display) {
			Coding coding = Coding.builder()
				.code(code)
				.system(system)
				.display(display)
				.build();
			
			this.concept = coding;
			return this;
		}
		
		@Override
		protected Product doBuild() {
			return new Product(element, concept);
		}
	}

}
