/*
 * Copyright 2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.fhir.core.model.structuredefinition;

import java.util.Collection;

import javax.validation.Valid;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;

import com.b2international.snowowl.fhir.core.model.Element;
import com.b2international.snowowl.fhir.core.model.Extension;
import com.b2international.snowowl.fhir.core.model.dt.Code;
import com.b2international.snowowl.fhir.core.model.dt.Reference;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.b2international.snowowl.fhir.core.search.Mandatory;
import com.b2international.snowowl.fhir.core.search.Summary;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Binds to a value set if this element is coded.
 * @since 7.1
 */
public class Binding extends Element {

	@NotNull
	@Valid
	@Mandatory
	@JsonProperty
	private final Code strength;
	
	@Summary
	@JsonProperty
	private final String description;
	
	@Valid
	@Summary
	private Uri valueSetUri;

	@Valid
	@Summary
	private Reference valueSetReference;
	
	@JsonProperty
	public Uri getValueSetUri() {
		return valueSetUri;
	}

	@JsonProperty
	public Reference getValueSetReference() {
		return valueSetReference;
	}

	@AssertTrue(message = "Both URI and Reference cannot be set for the 'valueSet' property")
	private boolean isValid() {

		if (valueSetUri != null && valueSetReference != null) {
			return false;
		}
		return true;
	}
	
	protected Binding(final String id, 
			@SuppressWarnings("rawtypes") final Collection<Extension> extensions,
			final Code strength, 
			final String description,
			final Uri valueSetUri,
			final Reference valueSetReference) {
		
		super(id, extensions);
		this.strength = strength;
		this.description = description;
		this.valueSetUri = valueSetUri;
		this.valueSetReference = valueSetReference;
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	public static class Builder extends Element.Builder<Builder, Binding> {
		
		private Code strength;
		private String description;
		private Uri valueSetUri;
		private Reference valueSetReference;
		
		@Override
		protected Builder getSelf() {
			return this;
		}
		
		public Builder strength(String strength) {
			this.strength = new Code(strength);
			return getSelf();
		}

		public Builder description(String description) {
			this.description = description;
			return getSelf();
		}
		
		public Builder valueSetUri(String valueSetUri) {
			this.valueSetUri = new Uri(valueSetUri);
			return getSelf();
		}
		
		public Builder valueSetReference(Reference valueSetReference) {
			this.valueSetReference = valueSetReference;
			return getSelf();
		}
		
		@Override
		protected Binding doBuild() {
			return new Binding(id, extensions, strength, description, valueSetUri, valueSetReference);
		}
	}

}
