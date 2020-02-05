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

import java.util.Collection;

import javax.validation.Valid;

import com.b2international.snowowl.fhir.core.codesystems.NarrativeStatus;
import com.b2international.snowowl.fhir.core.model.dt.Code;
import com.b2international.snowowl.fhir.core.model.dt.Id;
import com.b2international.snowowl.fhir.core.model.dt.Narrative;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Sets;

/**
 * 
 * FHIR Domain resource domain model.
 * 
 * <pre>
 * "resourceType" : "[name]",
 * // from Resource: id, meta, implicitRules, and language
 * "text" : { Narrative }, // C? Text summary of the resource, for human interpretation 0..1
 * "contained" : [{ Resource }], // Contained, inline Resources 0..*
 * (Extensions - see JSON page) 0..*
 * (Modifier Extensions - see JSON page)
 * </pre>
 * 
 * @see <a href="https://www.hl7.org/fhir/domainresource.html">FHIR:DomainResource</a>
 * @since 6.3
 */
public abstract class DomainResource extends FhirResource {
	
	//Text summary of the resource, for human interpretation 0..1
	private Narrative text;
	
	@Valid
	@JsonProperty("extension")
	private Collection<Extension<?>> extensions;

	public DomainResource(final Id id, final Meta meta, final Uri impliciteRules, final Code language, final Narrative text) {
		super(id, meta, impliciteRules, language);
		this.text = text;
	}

	public Narrative getText() {
		return text;
	}
	
	public static abstract class Builder<B extends Builder<B, T>, T extends FhirResource> extends FhirResource.Builder<B, T> {

		protected Narrative text;
		
		protected Collection<Extension<?>> extensions = Sets.newHashSet();
		
		/**
		 * Use this constructor when a new resource is sent to the server to be created.
		 */
		public Builder() {
		}
		
		public Builder(String resourceId) {
			super(resourceId);
		}

		public B narrative(NarrativeStatus narrativeStatus, String div) {
			Narrative narrative = Narrative.builder()
					.status(narrativeStatus)
					.div(div)
					.build();
			
			this.text = narrative;
			return getSelf();
		}
		
		public B text(Narrative text) {
			this.text = text;
			return getSelf();
		}
		
		public B addExtension(Extension<?> extension) {
			this.extensions.add(extension);
			return getSelf();
		}
	}

}
