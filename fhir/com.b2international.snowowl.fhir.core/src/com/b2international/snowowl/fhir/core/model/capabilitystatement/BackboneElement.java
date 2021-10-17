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
package com.b2international.snowowl.fhir.core.model.capabilitystatement;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import com.b2international.snowowl.fhir.core.model.Element;
import com.b2international.snowowl.fhir.core.model.Extension;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * FHIR Backbone element
 * @see <a href="https://www.hl7.org/fhir/backboneelement.html">FHIR:BackboneElement</a
 * @since 8.0.0
 */
public abstract class BackboneElement extends Element {
	
	@Valid
	@JsonProperty("modifierExtension")
	private List<Extension<?>> modifierExtensions;

	protected BackboneElement(final String id, final List<Extension<?>> extensions, final List<Extension<?>> modifierExtensions) {
		super(id, extensions);
		this.modifierExtensions = modifierExtensions;
	}
	
	public List<Extension<?>> getModifierExtensions() {
		return modifierExtensions;
	}
	
	public static abstract class Builder<B extends Builder<B, BBE>, BBE extends BackboneElement> extends Element.Builder<B, BBE> {
		
		protected List<Extension<?>> modifierExtensions;
		
		@JsonProperty("modifierExtension")
		@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
		public B modifierExtensions(final List<Extension<?>> modifierExtensions) {
			this.modifierExtensions = modifierExtensions;
			return getSelf();
		}
		
		public B addModifierExtension(final Extension<?> modifierExtension) {
			if (modifierExtensions == null) {
				modifierExtensions = new ArrayList<>();
			}
			modifierExtensions.add(modifierExtension);
			return getSelf();
		}
		
		protected abstract B getSelf();
		
	}

}
