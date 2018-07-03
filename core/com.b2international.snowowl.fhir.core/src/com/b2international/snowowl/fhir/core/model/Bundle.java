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
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.b2international.snowowl.fhir.core.codesystems.BundleType;
import com.b2international.snowowl.fhir.core.model.dt.Code;
import com.b2international.snowowl.fhir.core.model.dt.Id;
import com.b2international.snowowl.fhir.core.model.dt.Identifier;
import com.b2international.snowowl.fhir.core.model.dt.Signature;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.b2international.snowowl.fhir.core.search.Summary;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;

/**
 * FHIR Bundle domain model.
 * 
 * @see <a href="https://www.hl7.org/fhir/bundle.html">FHIR:Bundle</a>
 * @since 6.3
 */
public class Bundle extends FhirResource {
	
	//FHIR header "resourceType" : "Bundle",
	@JsonProperty
	private String resourceType = "Bundle";

	@Summary
	@JsonProperty
	private Identifier identifier;
	
	@Summary
	@Valid
	@NotNull
	@JsonProperty
	private Code type;
	
	@Summary
	@Min(value = 0, message = "Total must be equal to or larger than 0")
	@JsonProperty
	private int total;

	@Summary
	@JsonProperty("link")
	private Collection<Link> links;
	
	@Summary
	@JsonProperty("entry")
	private Collection<Entry> entries;
	
	@Summary
	@Valid
	@JsonProperty
	private Signature signature;
	
	public Bundle(Id id, Code language, Identifier identifier, Code type, int total, Collection<Link> links, Collection<Entry> entries, final Signature signature) {
		super(id, language);
		
		this.identifier = identifier;
		this.type = type;
		this.total = total;
		this.links = links;
		this.entries = entries;
	}
	
	public Bundle(final Id id, final Code language) {
		super(id, language);
	}
	
	public static Builder builder(String bundleId) {
		return new Builder(bundleId);
	}

	public static class Builder extends FhirResource.Builder<Builder, Bundle> {

		private Identifier identifier;
		
		private Code type;
		
		private int total;
		
		private Collection<Link> links = Lists.newArrayList();
		
		private Collection<Entry> entries = Lists.newArrayList();
		
		private Signature signature;
		
		public Builder(String cdoId) {
			super(cdoId);
		}

		public Builder identifier(final Identifier identifer) {
			this.identifier = identifer;
			return getSelf();
		}
		
		public Builder type(BundleType type) {
			this.type = type.getCode();
			return getSelf();
		}

		public Builder total(int total) {
			this.total = total;
			return getSelf();
		}
		
		public Builder addLink(String relation, String url) {
			links.add(new Link(relation, new Uri(url)));
			return getSelf();
		}

		public Builder addLink(String url) {
			links.add(new Link(url));
			return getSelf();
		}
		
		public Builder addEntry(Entry entry) {
			entries.add(entry);
			return getSelf();
		}
		
		public Builder signature(Signature signature) {
			this.signature = signature;
			return getSelf();
		}

		@Override
		protected Builder getSelf() {
			return this;
		}

		@Override
		protected Bundle doBuild() {
			return new Bundle(id, language, identifier, type, total, links, entries, signature);
		}
	}

}
