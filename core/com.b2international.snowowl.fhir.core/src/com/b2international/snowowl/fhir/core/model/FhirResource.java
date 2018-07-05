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

import com.b2international.snowowl.fhir.core.model.dt.Code;
import com.b2international.snowowl.fhir.core.model.dt.Coding;
import com.b2international.snowowl.fhir.core.model.dt.Id;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.b2international.snowowl.fhir.core.search.Mandatory;
import com.b2international.snowowl.fhir.core.search.Summary;
import com.fasterxml.jackson.annotation.JsonProperty;
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
	
	/**
	 * Logical id of this artifact
	 * Set the ID to -1 when submitting for creation.
	 */
	@Mandatory
	private final Id id;
	
	/**
	 * Metadata about the resource
	 */
	/*
	 * Not final as this field can mark the resource as SUBSETTED
	 * bbanfai: this is great as according to https://www.hl7.org/fhir/search.html#summary only mandatory elements should be returned
	 * and meta (carrying the subsetted tag) is not mandatory based on the spec.
	 * Changed field to mandatory (to be serialized if present)
	 */
	@Mandatory
	@Summary
	private Meta meta; 
	
	/**
	 * A set of rules under which this content was created
	 */
	@Summary
	private final Uri implicitRules;
	
	/**
	 * Language of the resource content
     * Common Languages (Extensible but limited to All Languages)
	 */
	private final Code language;
	
	FhirResource(final Id id, final Meta meta, final Uri implicitRules, final Code language) {
		this.id = id;
		this.meta = meta;
		this.implicitRules = implicitRules;
		this.language = language;
	}
	
	public Id getId() {
		return id;
	}
	
	public Meta getMeta() {
		return meta;
	}
	public Uri getImplicitRules() {
		return implicitRules;
	}

	public Code getLanguage() {
		return language;
	}
	
	/**
	 * Marks the resource to be SUBSETTED
	 * to indicate that the resource is not fully detailed (e.g. summary mode)
	 */
	public void setSubsetted() {
		if (meta == null) {
			meta = Meta.builder()
				.addTag(Coding.CODING_SUBSETTED)
				.build();
		} else {
			meta.getTags().add(Coding.CODING_SUBSETTED);
		}
		
	}

	public static abstract class Builder<B extends Builder<B, T>, T extends FhirResource> extends ValidatingBuilder<T> {

		protected Id id;

		protected Meta meta;
		
		protected Uri implicitRules;
		
		protected Code language;
		
		/**
		 * Use this constructor when a new resource is sent to the server to be created.
		 */
		public Builder() {
		}
		
		/**
		 * Encode our internal component Id to hide it from the outside world.
		 * This can be null when a new resource is sent to the server to be saved
		 * @see FhirResource.Builder:Builder()
		 * @param cdoId
		 */
		public Builder(String resourceId) {
			this.id = new Id(resourceId);
		}
		
		protected abstract B getSelf();
		
		/**
		 * Each resource has an "id" element which contains the logical identity of the resource assigned by the server responsible for storing it. 
		 * Resources always have a known identity except for the special case when a new resource is being sent to a server to assign an identity (create interaction). 
		 * The logical identity is unique within the space of all resources of the same type on the same server. Once assigned, the identity is never changed. 
		 * Note that if the resource is copied to another server, the copy might not be able to retain the same logical identity.
		 * @param resourceId
		 * @return builder
		 */
		public B id(String resourceId) {
			this.id = new Id(resourceId);
			return getSelf();
		}
		
		public B meta(final Meta meta) {
			this.meta = meta;
			return getSelf();
		}
		
		public B implicitRules(final Uri implicitRules) {
			this.implicitRules = implicitRules;
			return getSelf();
		}
		
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
