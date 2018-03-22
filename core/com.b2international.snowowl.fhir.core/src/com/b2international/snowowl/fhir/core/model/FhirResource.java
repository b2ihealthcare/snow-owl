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
package com.b2international.snowowl.fhir.core.model;

import java.util.Base64;

import com.b2international.snowowl.fhir.core.model.dt.Code;
import com.b2international.snowowl.fhir.core.model.dt.Id;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Top-level FHIR resource
 * 
 * <pre>
 * 0..1 for every property
 * "id" : "<id>", // Logical id of this artifact
 * "meta" : { Meta }, // Metadata about the resource
 * "implicitRules" : "<uri>", // A set of rules under which this content was created
 * "language" : "<code>" // Language of the resource content
 * </pre>
 * 
 * @see <a href="https://www.hl7.org/fhir/resource.html">FHIR:Resource</a>
 * @since 6.3
 */
@JsonPropertyOrder({ "resourceType", "id" })
public abstract class FhirResource {
	
	private Id id;

	private Code language;
	
	FhirResource(final Id id, final Code language) {
		this.id = id;
		this.language = language;
	}
	
	public Code getLanguage() {
		return language;
	}
	
	public Id getId() {
		return id;
	}
	
	public static abstract class Builder<B extends Builder<B, T>, T extends FhirResource> extends ValidatingBuilder<T> {

		protected Id id;

		protected Code language = new Code("en");
		
		/**
		 * Encode our internal component Id to hide it from the outside world.
		 * @param cdoId
		 */
		public Builder(String resourceId) {
			this.id = new Id(resourceId);
		}

		protected abstract B getSelf();
		
		public B language(final Code language) {
			this.language = language;
			return getSelf();
		}
		
		public B language(final String language) {
			this.language = new Code(language);
			return getSelf();
		}
	}

}
