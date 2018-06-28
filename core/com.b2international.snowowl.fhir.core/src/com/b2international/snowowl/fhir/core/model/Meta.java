/*******************************************************************************
 * Copyright (c) 2018 B2i Healthcare. All rights reserved.
 *******************************************************************************/
package com.b2international.snowowl.fhir.core.model;

import java.util.Collection;

import com.b2international.snowowl.fhir.core.model.dt.Coding;
import com.b2international.snowowl.fhir.core.model.dt.Id;
import com.b2international.snowowl.fhir.core.model.dt.Instant;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.b2international.snowowl.fhir.core.search.Summary;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Sets;

/**
 * FHIR Resource Metadata
 * 
 * @see <a href="https://www.hl7.org/fhir/resource.html#Meta">FHIR:Resource:Meta</a>
 * @since 6.6
 */
public class Meta extends Element {
	
	@Summary
	@JsonProperty
	private Id versionId;
	
	@Summary
	@JsonProperty
	private Instant lastUpdated;
	
	@Summary
	@JsonProperty("profile")
	private Collection<Uri> profiles;
	
	@Summary
	@JsonProperty("security")
	private Collection<Coding> securities;
	
	@Summary
	@JsonProperty("tag")
	private Collection<Coding> tags;
	
	/**
	 * @param id
	 * @param extensions
	 */
	protected Meta(String id, Collection<Extension> extensions,
			final Id versionId, final Instant lastUpdated, final Collection<Uri> profiles, final Collection<Coding> securities, final Collection<Coding> tags) {
		
		super(id, extensions);
		this.versionId = versionId;
		this.lastUpdated = lastUpdated;
		this.profiles = profiles;
		this.securities = securities;
		this.tags = tags;
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	public static class Builder extends Element.Builder<Builder, Meta> {
		
		private Id versionId;
		private Instant lastUpdated;
		private Collection<Uri> profiles = Sets.newHashSet();
		private Collection<Coding> securities = Sets.newHashSet();
		private Collection<Coding> tags = Sets.newHashSet();
		
		@Override
		protected Builder getSelf() {
			return this;
		}
		
		public Builder versionId(Id versionId) {
			this.versionId = versionId;
			return getSelf();
		}
		
		public Builder versionId(String versionId) {
			this.versionId = new Id(versionId);
			return getSelf();
		}
		
		public Builder lastUpdated(Instant lastUpdated) {
			this.lastUpdated = lastUpdated;
			return getSelf();
		}
		
		public Builder addProfile(Uri profileUri) {
			profiles.add(profileUri);
			return getSelf();
		}

		public Builder addProfile(String profile) {
			profiles.add(new Uri(profile));
			return getSelf();
		}
		
		public Builder addSecurity(Coding security) {
			securities.add(security);
			return getSelf();
		}

		public Builder addTag(Coding tag) {
			tags.add(tag);
			return getSelf();
		}
		
		@Override
		protected Meta doBuild() {
			return new Meta(id, extensions, versionId, lastUpdated, profiles, securities, tags);
		}
	}
}
