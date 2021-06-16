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
package com.b2international.snowowl.core.version;

import static com.b2international.index.query.Expressions.exactMatch;
import static com.b2international.index.query.Expressions.matchAny;
import static com.b2international.index.query.Expressions.matchRange;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;

import com.b2international.index.Doc;
import com.b2international.index.ID;
import com.b2international.index.query.Expression;
import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.branch.BranchPathUtils;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.common.base.MoreObjects;

/**
 * @since 5.0
 */
@Doc(type = "version")
@JsonDeserialize(builder = VersionDocument.Builder.class)
public final class VersionDocument implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Unique terminology component identifier for versions.
	 */
	public static final short TERMINOLOGY_COMPONENT_ID = 2;
	
	/**
	 * @since 8.0
	 */
	public static class Fields {
		public static final String ID = "id";
		public static final String VERSION = "version";
		public static final String DESCRIPTION = "description";
		public static final String EFFECTIVE_TIME = "effectiveTime";
		public static final String RESOURCE = "resource";
		public static final String BRANCH_PATH = "branchPath";
		public static final String RESOURCE_BRANCH_PATH = "resourceBranchPath";
		
		public static final Set<String> SORT_FIELDS = Set.of(ID, VERSION, DESCRIPTION, EFFECTIVE_TIME, RESOURCE, BRANCH_PATH);
	}

	public static class Expressions {

		public static Expression ids(Iterable<String> ids) {
			return matchAny(Fields.ID, ids);
		}
		
		public static Expression version(String version) {
			return exactMatch(Fields.VERSION, version);
		}
		
		public static Expression versions(Iterable<String> versions) {
			return matchAny(Fields.VERSION, versions);
		}

		public static Expression resource(String resourceUri) {
			return exactMatch(Fields.RESOURCE, resourceUri);
		}
		
		public static Expression resources(Collection<String> resourceUris) {
			return matchAny(Fields.RESOURCE, resourceUris);
		}
		
		public static Expression effectiveTime(long effectiveTime) {
			return exactMatch(Fields.EFFECTIVE_TIME, effectiveTime);
		}
		
		public static Expression effectiveTime(long from, long to) {
			return matchRange(Fields.EFFECTIVE_TIME, from, to);
		}
		
		public static Expression resourceBranchPaths(Iterable<String> resourceBranchPaths) {
			return matchAny(Fields.RESOURCE_BRANCH_PATH, resourceBranchPaths);
		}
		
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	@JsonPOJOBuilder(withPrefix="")
	public static class Builder {
		
		private String id;
		private String version;
		private String description;
		private long effectiveTime;
		private ResourceURI resource;
		private String branchPath;
		
		public Builder id(String id) {
			this.id = id;
			return this;
		}

		public Builder version(String version) {
			this.version = version;
			return this;
		}
		
		public Builder description(String description) {
			this.description = description;
			return this;
		}
		
		public Builder effectiveTime(long effectiveTime) {
			this.effectiveTime = effectiveTime;
			return this;
		}
		
		public Builder resource(ResourceURI resource) {
			this.resource = resource;
			return this;
		}
		
		public Builder branchPath(String branchPath) {
			this.branchPath = branchPath;
			return this;
		}
		
		@JsonSetter
		Builder resourceBranchPath(String resourceBranchPath) {
			return this;
		}
		
		public VersionDocument build() {
			return new VersionDocument(
				id,
				version,
				description,
				effectiveTime, 
				resource,
				branchPath
			);
		}
		
	}
	
	@ID
	private final String id;
	private final String version;
	private final String description;
	private final long effectiveTime;
	private final ResourceURI resource;
	private final String branchPath;
	
	private VersionDocument(
			final String id, 
			final String version,
			final String description,
			final long effectiveTime, 
			final ResourceURI resource,
			final String branchPath) {
		this.id = id;
		this.version = version;
		this.description = description;
		this.effectiveTime = effectiveTime;
		this.resource = resource;
		this.branchPath = branchPath;
	}
	
	public String getId() {
		return id;
	}
	
	public String getVersion() {
		return version;
	}
	
	public String getDescription() {
		return description;
	}

	public long getEffectiveTime() {
		return effectiveTime;
	}
	
	public ResourceURI getResource() {
		return resource;
	}
	
	public String getBranchPath() {
		return branchPath;
	}
	
	public String getResourceBranchPath() {
		return BranchPathUtils.createPath(branchPath).getParentPath();
	}
	
	// additional helpers
	
	/**
	 * @return the effective time as a {@link LocalDate} value.
	 */
	@JsonIgnore
	public LocalDate getEffectiveTimeAsLocalDate() {
		return EffectiveTimes.toDate(effectiveTime);
	}
	
	/**
	 * @return the {@link ResourceURI} that represents this version in the #getSystem, never <code>null</code>
	 */
	@JsonIgnore
	public ResourceURI getVersionResourceURI() {
		return resource.withPath(version);
	}

	@Override
	public int hashCode() {
		return Objects.hash(resource, version);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) { return true; }
		if (obj == null) { return false; }
		if (!(obj instanceof VersionDocument)) { return false; }
		final VersionDocument other = (VersionDocument) obj;
		return Objects.equals(resource, other.resource) && Objects.equals(version, other.version);
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(getClass())
				.add("id", id)
				.add("version", version)
				.add("description", description)
				.add("effectiveTime", effectiveTime)
				.add("resource", resource)
				.add("branchPath", branchPath)
				.toString();
	}

}
