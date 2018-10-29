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

import javax.validation.Valid;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.b2international.snowowl.fhir.core.model.Element;
import com.b2international.snowowl.fhir.core.model.Extension;
import com.b2international.snowowl.fhir.core.search.Summary;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;

/**
 * FHIR Signature complex datatype
 * 
 * A Signature holds an electronic representation of a signature and its supporting context in a FHIR accessible form. 
 *
 * @see <a href="https://www.hl7.org/fhir/datatypes.html#signature">FHIR:Data Types:Signature</a>
 * @since 6.6
 */
public class Signature extends Element {
	
	@SuppressWarnings("rawtypes")
	Signature(String id, Collection<Extension> extensions, Collection<Coding> types, final Instant when, final Uri whoUri, final Reference whoReference, 
			final Uri onBehalfOfUri, final Reference onBehalfOfReference, final Code contentType, final Byte[] blob) {
		
		super(id, extensions);
		
		this.types = types;
		this.when = when;
		this.whoUri = whoUri;
		this.whoReference = whoReference;
		this.whoUri = whoUri;
		this.onBehalfOfUri = onBehalfOfUri;
		this.onBehalfOfReference = onBehalfOfReference;
		this.contentType = contentType;
		this.blob = blob;
	}

	@Summary
	@Valid
	@NotEmpty
	@JsonProperty("type")
	private Collection<Coding> types;
	
	@Summary
	@Valid
	@NotNull
	@JsonProperty
	private Instant when;
	
	@Summary
	@Valid
	private Uri whoUri;
	
	@Summary
	@Valid
	private Reference whoReference;
	
	@Summary
	@Valid
	private Uri onBehalfOfUri;
	
	@Summary
	@Valid
	private Reference onBehalfOfReference;
	
	@Summary
	@Valid
	@JsonProperty
	private Code contentType;
	
	@Valid
	@JsonProperty
	private Byte[] blob;
	
	@JsonProperty
	public Uri getWhoUri() {
		return whoUri;
	}
	
	@JsonProperty
	public Reference getWhoReference() {
		return whoReference;
	}
	
	@JsonProperty
	public Uri getOnBehalfOfUri() {
		return onBehalfOfUri;
	}
	
	@JsonProperty
	public Reference getOnBehalfReference() {
		return onBehalfOfReference;
	}
	
	@AssertTrue(message = "Either URI or Reference should be set for the 'who' and 'onBehalfOf' fields")
	private boolean isValid() {

		if (whoUri != null && whoReference != null) {
			return false;
		}
	
		if (onBehalfOfUri != null && onBehalfOfReference != null) {
			return false;
		}
		return true;
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	public static class Builder extends Element.Builder<Builder, Signature> {
		
		private Collection<Coding> types = Lists.newArrayList();
		private Instant when;
		private Uri whoUri;
		private Reference whoReference;
		private Uri onBehalfOfUri;
		private Reference onBehalfOfReference;
		private Code contentType;
		private Byte[] blob;
		
		@Override
		protected Builder getSelf() {
			return this;
		}
		
		public Builder type(Collection<Coding> types) {
			this.types = types;
			return getSelf();
		}
		
		public Builder addType(Coding typeCoding) {
			types.add(typeCoding);
			return getSelf();
		}
		
		public Builder when(Instant when) {
			this.when = when;
			return getSelf();
		}
		
		public Builder whoUri(Uri whoUri) {
			this.whoUri = whoUri;
			return getSelf();
		}
		
		public Builder whoReference(Reference reference) {
			this.whoReference = reference;
			return getSelf();
		}
		
		public Builder onBehalfOfUri(Uri onBehalfOfUri) {
			this.onBehalfOfUri = onBehalfOfUri;
			return getSelf();
		}
		
		public Builder onBehalfOfReference(Reference reference) {
			this.onBehalfOfReference = reference;
			return getSelf();
		}
		
		public Builder contentType(Code contentType) {
			this.contentType = contentType;
			return getSelf();
		}
		
		public Builder blob(Byte[] blob) {
			this.blob = blob;
			return getSelf();
		}
		
		public Builder blob(byte[] blob) {
			
			this.blob = new Byte[blob.length];
			
			for (int i = 0; i < blob.length; i++) {
		        this.blob[i] = Byte.valueOf(blob[i]);
		    }
			return getSelf();
		}

		@Override
		protected Signature doBuild() {
			return new Signature(id, extensions, types, when, whoUri, whoReference, onBehalfOfUri, onBehalfOfReference, contentType, blob);
		}
	}

}
