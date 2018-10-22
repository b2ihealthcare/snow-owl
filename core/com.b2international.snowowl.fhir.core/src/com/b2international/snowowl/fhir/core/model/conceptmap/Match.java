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

import java.util.Collection;

import com.b2international.snowowl.fhir.core.codesystems.ConceptMapEquivalence;
import com.b2international.snowowl.fhir.core.model.ValidatingBuilder;
import com.b2international.snowowl.fhir.core.model.dt.Code;
import com.b2international.snowowl.fhir.core.model.dt.Coding;
import com.b2international.snowowl.fhir.core.model.dt.FhirDataType;
import com.b2international.snowowl.fhir.core.model.dt.FhirType;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.common.collect.ImmutableList;

/**
 * Concept Map translate result match parameter
 * 
 * @since 7.1
 */
@JsonDeserialize(builder = Match.Builder.class)
@JsonPropertyOrder({"equivalence", "concept", "product", "source"})
public class Match {
	
	//A code indicating the equivalence of the translation, 
	//using values from [ConceptMapEquivalence]{concept-map-equivalence.html}
	private final Code equivalence;
	
	//The translation outcome. Note that this would never have userSelected = true, 
	//since the process of translations implies that the user is not selecting the code (and only the client could know differently)
	private final Coding concept;
	
	//Another element that is the product of this mapping
	@FhirType(FhirDataType.PART)
	private final Collection<Product> product;
	
	//The canonical URI for the concept map from which this mapping comes from
	private final Uri source;
	
	Match(final Code equivalence, final Coding concept, final Collection<Product> products, final Uri source) {
		this.equivalence = equivalence;
		this.concept = concept;
		this.product = products;
		this.source = source;
	}
	
	public Code getEquivalence() {
		return equivalence;
	}
	
	public Coding getConcept() {
		return concept;
	}
	
	public Collection<Product> getProduct() {
		return product;
	}
	
	public Uri getSource() {
		return source;
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	@JsonPOJOBuilder(withPrefix="")
	public static class Builder extends ValidatingBuilder<Match>{
		
		private Code equivalence;
		private Coding concept;
		private ImmutableList.Builder<Product> products = ImmutableList.builder();
		private Uri source;
		
		Builder() {}

		public Builder equivalence(final ConceptMapEquivalence equivalence) {
			this.equivalence = equivalence.getCode();
			return this;
		}
		
		public Builder concept(final Coding concept) {
			this.concept = concept;
			return this;
		}
		
		/**
		 * Alternative property deserializer to be used when converting FHIR Parameters representation to TranslateRequest. 
		 * Multi-valued property expand.
		 */
		public Builder addProduct(Product match) {
			products.add(match);
			return this;
		}
		
		@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
		public Builder products(Collection<Product> prods) {
			products = ImmutableList.builder();
			products.addAll(prods);
			return this;
		}

		public Builder source(final Uri source) {
			this.source = source;
			return this;
		}
		
		@Override
		protected Match doBuild() {
			return new Match(equivalence, concept, products.build(), source);
		}
	}
}
