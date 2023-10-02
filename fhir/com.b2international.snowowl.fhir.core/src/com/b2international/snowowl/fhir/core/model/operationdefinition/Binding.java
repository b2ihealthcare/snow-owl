/*
 * Copyright 2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.fhir.core.model.operationdefinition;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.b2international.snowowl.fhir.core.model.Element;
import com.b2international.snowowl.fhir.core.model.Extension;
import com.b2international.snowowl.fhir.core.model.dt.Code;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.b2international.snowowl.fhir.core.search.Mandatory;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

/**
 * Binds an operation definition parameter to a value set the parameter is coded.
 * @since 8.0.0
 */
@JsonDeserialize(builder = Binding.Builder.class)
public class Binding extends Element {

	@NotNull
	@Valid
	@Mandatory
	@JsonProperty
	private final Code strength;
	
	@NotNull
	@Valid
	@Mandatory
	@JsonProperty
	private Uri valueSetUri;

	Binding(final String id, 
			final List<Extension<?>> extensions,
			final Code strength, 
			final Uri valueSetUri) {
		
		super(id, extensions);
		this.strength = strength;
		this.valueSetUri = valueSetUri;
	}
	
	public Code getStrength() {
		return strength;
	}
	
	@JsonProperty
	public Uri getValueSetUri() {
		return valueSetUri;
	}

	public static Builder builder() {
		return new Builder();
	}
	
	@JsonPOJOBuilder(withPrefix = "")
	public static class Builder extends Element.Builder<Builder, Binding> {
		
		private Code strength;
		private Uri valueSetUri;
		
		@Override
		protected Builder getSelf() {
			return this;
		}
		
		public Builder strength(String strength) {
			this.strength = new Code(strength);
			return getSelf();
		}

		public Builder valueSetUri(String valueSetUri) {
			this.valueSetUri = new Uri(valueSetUri);
			return getSelf();
		}
		
		@Override
		protected Binding doBuild() {
			return new Binding(id, extensions, strength, valueSetUri);
		}
	}

}
