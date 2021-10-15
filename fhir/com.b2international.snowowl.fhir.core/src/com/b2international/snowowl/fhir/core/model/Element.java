/*
 * Copyright 2011-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.fhir.core.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * FHIR base definition for resources and data types
 *
 * @see <a href="https://www.hl7.org/fhir/element.html">FHIR:Element</a>
 * @since 6.6
 */
public abstract class Element {
	
	@JsonProperty
	private final String id;
	
	@JsonProperty("extension")
	private final List<Extension<?>> extensions;
	
	protected Element(final String id, final List<Extension<?>> extensions) {
		this.id = id;
		this.extensions = extensions;
	}
	
	public final String getId() {
		return id;
	}
	
	public final Collection<Extension<?>> getExtensions() {
		return extensions;
	}
	
	public static abstract class Builder<B extends Builder<B, T>, T extends Element> extends ValidatingBuilder<T> {

		protected String id;

		protected List<Extension<?>> extensions;
		
		protected abstract B getSelf();
		
		public B id(String id) {
			this.id = id;
			return getSelf();
		}
		
		@JsonProperty("extension")
		@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
		public B extensions(List<Extension<?>> extensions) {
			this.extensions = extensions;
			return getSelf();
		}
		
		public B addExtension(final Extension<?> extension) {
			if (extensions == null) {
				extensions = new ArrayList<>();
			}
			extensions.add(extension);
			return getSelf();
		}
	}

}
