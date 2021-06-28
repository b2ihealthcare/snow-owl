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

import javax.validation.Valid;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.b2international.commons.collections.Collections3;
import com.b2international.snowowl.core.domain.CollectionResource;
import com.b2international.snowowl.fhir.core.codesystems.BundleType;
import com.b2international.snowowl.fhir.core.model.dt.Code;
import com.b2international.snowowl.fhir.core.model.dt.Id;
import com.b2international.snowowl.fhir.core.model.dt.Identifier;
import com.b2international.snowowl.fhir.core.model.dt.Instant;
import com.b2international.snowowl.fhir.core.model.dt.Signature;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.b2international.snowowl.fhir.core.search.Summary;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

/**
 * FHIR Bundle domain model.
 * 
 * @see <a href="https://www.hl7.org/fhir/bundle.html">FHIR:Bundle</a>
 * @since 6.3
 */
@JsonDeserialize(builder = Bundle.Builder.class)
public class Bundle extends FhirResource implements CollectionResource<Entry> {
	
	public static final String RESOURCE_TYPE_BUNDLE = "Bundle";

	private static final long serialVersionUID = 1L;

	//FHIR Json header "resourceType" : "Bundle",
	@JsonProperty
	private String resourceType;

	@Summary
	private Identifier identifier;
	
	@Valid
	@Summary
	private Instant timestamp;
	
	@Summary
	@Valid
	@NotNull
	private Code type;
	
	@Summary
	@Min(value = 0, message = "Total must be equal to or larger than 0")
	private int total;

	@Summary
	private Collection<Link> link;
	
	@Summary
	private List<Entry> entry;
	
	@Summary
	@Valid
	private Signature signature;
	
	private Bundle(Id id, final String resourceType, final Meta meta, final Uri impliciteRules, Code language, Identifier identifier, 
			Instant timestamp, Code type, int total, Collection<Link> links, List<Entry> entries, final Signature signature) {
		super(id, meta, impliciteRules, language);
		this.resourceType = resourceType;
		this.identifier = identifier;
		this.timestamp = timestamp;
		this.type = type;
		this.total = total;
		this.link = links;
		this.entry = entries;
	}
	
	@AssertTrue(message = "Resource type must be 'Bundle'")
	private boolean isResourceTypeValid() {
		return RESOURCE_TYPE_BUNDLE.equals(resourceType);
	}
	
	public static Builder builder(String bundleId) {
		return new Builder(bundleId);
	}
	
	public static Builder builder() {
		return new Builder();
	}

	@JsonPOJOBuilder(withPrefix = "")
	public static class Builder extends FhirResource.Builder<Builder, Bundle> {

		private String resourceType  = RESOURCE_TYPE_BUNDLE;
		
		private Identifier identifier;
		
		private Instant timestamp;
		
		private Code type;
		
		private int total;
		
		private Collection<Link> links;
		
		private List<Entry> entries;
		
		private Signature signature;
		
		Builder() {
		}
		
		public Builder(String bundleId) {
			super(bundleId);
		}

		/*
		 * Ignored in the constructor, only needed for deserialization
		 */
		public Builder resourceType(final String resourceType) {
			this.resourceType = resourceType;
			return getSelf();
		}

		public Builder identifier(final Identifier identifer) {
			this.identifier = identifer;
			return getSelf();
		}
		
		public Builder timestamp(final Instant timestamp) {
			this.timestamp = timestamp;
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
		
		@JsonProperty("link")
		@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
		public Builder links(Collection<Link> links) {
			this.links = links;
			return getSelf();
		}
		
		public Builder addLink(String relation, String url) {
			if (links == null) {
				links = new ArrayList<>();
			}
			links.add(new Link(relation, new Uri(url)));
			return getSelf();
		}

		public Builder addLink(String url) {
			if (links == null) {
				links = new ArrayList<>();
			}
			links.add(new Link(url));
			return getSelf();
		}
		
		public Builder addEntry(Entry entry) {
			if (entries == null) {
				entries = new ArrayList<>();
			}
			entries.add(entry);
			return getSelf();
		}
		
		public Builder entry(Iterable<Entry> entry) {
			this.entries = Collections3.toImmutableList(entry);
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
			return new Bundle(id, resourceType, meta, implicitRules, language, identifier, timestamp, 
					type, total, links, entries, signature);
		}

	}
	
	@JsonIgnore
	@Override
	public List<Entry> getItems() {
		return Collections3.toImmutableList(entry);
	}
	
	public Collection<Entry> getEntry() {
		return entry;
	}
	
	public Identifier getIdentifier() {
		return identifier;
	}
	
	public Collection<Link> getLink() {
		return link;
	}
	
	public Signature getSignature() {
		return signature;
	}
	
	public Instant getTimestamp() {
		return timestamp;
	}

	public Code getType() {
		return type;
	}
	
	public int getTotal() {
		return total;
	}
	
}
