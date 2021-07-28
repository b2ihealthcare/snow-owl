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
import java.util.*;

import com.b2international.index.Doc;
import com.b2international.index.ID;
import com.b2international.index.query.Expression;
import com.b2international.index.revision.RevisionBranch;
import com.b2international.index.revision.RevisionBranchPoint;
import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.branch.BranchPathUtils;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
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

	private static final long serialVersionUID = 2L;

	/**
	 * @since 8.0
	 */
	public static class Fields {
		// same fields as in the Revision document to allow queryin' versions along with ResourceDocuments
		public static final String CREATED = "created";
		public static final String REVISED = "revised";
		
		public static final String ID = "id";
		public static final String VERSION = "version";
		public static final String DESCRIPTION = "description";
		public static final String EFFECTIVE_TIME = "effectiveTime";
		public static final String RESOURCE = "resource";
		public static final String BRANCH_PATH = "branchPath";
		public static final String CREATED_AT = "createdAt";
		public static final String TOOLING_ID = "toolingId";
		public static final String URL = "url";
		public static final String AUTHOR = "author";
		
		// derived fields
		public static final String RESOURCE_BRANCH_PATH = "resourceBranchPath";
		public static final String RESOURCE_TYPE = "resourceType";
		public static final String RESOURCE_ID = "resourceId";
		
		public static final Set<String> SORT_FIELDS = Set.of(ID, VERSION, DESCRIPTION, EFFECTIVE_TIME, RESOURCE, BRANCH_PATH, AUTHOR, CREATED_AT, TOOLING_ID, URL);
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
		
		public static Expression resources(Iterable<String> resourceUris) {
			return matchAny(Fields.RESOURCE, resourceUris);
		}
		
		public static Expression resourceTypes(Iterable<String> resourceTypes) {
			return matchAny(Fields.RESOURCE_TYPE, resourceTypes);
		}
		
		public static Expression resourceIds(Iterable<String> resourceIds) {
			return matchAny(Fields.RESOURCE_ID, resourceIds);
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
		
		public static Expression authors(Iterable<String> authors) {
			return matchAny(Fields.AUTHOR, authors);
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
		private Long effectiveTime;
		private ResourceURI resource;
		private String branchPath;
		private Long createdAt;
		private String toolingId;
		private String url;
		private String author;
		
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
		
		public Builder effectiveTime(Long effectiveTime) {
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
		
		public Builder createdAt(Long createdAt) {
			this.createdAt = createdAt;
			return this;
		}
		
		public Builder author(String author) {
			this.author = author;
			return this;
		}
		
		public Builder toolingId(String toolingId) {
			this.toolingId = toolingId;
			return this;
		}
		
		public Builder url(String url) {
			this.url = url;
			return this;
		}
		
		// index only fields, for searching, sorting, etc.
		
		@JsonSetter
		Builder created(RevisionBranchPoint created) {
			return this;
		}
		
		@JsonSetter
		Builder resourceBranchPath(String resourceBranchPath) {
			return this;
		}
		
		@JsonSetter
		Builder resourceType(String resourceType) {
			return this;
		}
		
		@JsonSetter
		Builder revised(List<RevisionBranchPoint> revised) {
			return this;
		}
		
		public VersionDocument build() {
			return new VersionDocument(
				id,
				version,
				description,
				effectiveTime, 
				resource,
				branchPath,
				createdAt,
				toolingId,
				url,
				author
			);
		}

	}
	
	@ID
	private final String id;
	private final String version;
	private final String description;
	private final Long effectiveTime;
	private final ResourceURI resource;
	private final String branchPath;
	private final Long createdAt;
	private final String toolingId;
	private final String url;
	private final String author;
	
	/**
	 * Same as Revision.created and revised to allow running queries against both Resource and Version documents. 
	 * NOTE: VersionDocument only uses the timestamp portion of the branchpoint model for createdAt property.
	 */
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private RevisionBranchPoint created;
	
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private List<RevisionBranchPoint> revised = Collections.emptyList();
	
	// search only fields
	private String resourceId;
	private String resourceType;

	private VersionDocument(
			final String id, 
			final String version,
			final String description,
			final Long effectiveTime, 
			final ResourceURI resource,
			final String branchPath,
			final Long createdAt,
			final String toolingId,
			final String url,
			final String author) {
		this.id = id;
		this.version = version;
		this.description = description;
		this.effectiveTime = effectiveTime;
		this.resource = resource;
		this.resourceId = resource != null ? resource.getResourceId() : null;
		this.resourceType = resource != null ? resource.getResourceType() : null;
		this.branchPath = branchPath;
		this.createdAt = createdAt;
		this.toolingId = toolingId;
		this.url = url;
		this.author = author;
		this.created = createdAt != null ? new RevisionBranchPoint(RevisionBranch.MAIN_BRANCH_ID, createdAt) : null;
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
	
	public String getResourceId() {
		return resourceId;
	}
	
	public String getResourceType() {
		return resourceType;
	}
	
	public String getBranchPath() {
		return branchPath;
	}
	
	public String getResourceBranchPath() {
		return BranchPathUtils.createPath(branchPath).getParentPath();
	}
	
	public Long getCreatedAt() {
		return createdAt;
	}
	
	public String getToolingId() {
		return toolingId;
	}
	
	public String getUrl() {
		return url;
	}
	
	public String getAuthor() {
		return author;
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
				.add("toolingId", toolingId)
				.add("createdAt", createdAt)
				.add("url", url)
				.add("author", author)
				.toString();
	}

}
