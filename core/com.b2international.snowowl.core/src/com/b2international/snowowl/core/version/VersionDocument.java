/*
 * Copyright 2011-2023 B2i Healthcare Pte Ltd, http://b2i.sg
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
import java.util.stream.Collectors;

import com.b2international.index.Doc;
import com.b2international.index.ID;
import com.b2international.index.mapping.Field;
import com.b2international.index.query.Expression;
import com.b2international.index.revision.CommitSubject;
import com.b2international.index.revision.RevisionBranch;
import com.b2international.index.revision.RevisionBranchPoint;
import com.b2international.snowowl.core.Dependency;
import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.TerminologyResource;
import com.b2international.snowowl.core.branch.BranchPathUtils;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.core.internal.DependencyDocument;
import com.b2international.snowowl.core.internal.ResourceDocument;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.common.base.MoreObjects;

/**
 * @since 5.0
 */
@Doc(type = VersionDocument.TYPE)
@JsonDeserialize(builder = VersionDocument.Builder.class)
public final class VersionDocument implements CommitSubject, Serializable {

	private static final long serialVersionUID = 2L;

	public static final String TYPE = "version";
	
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
		public static final String DEPENDENCIES = "dependencies";
		public static final String SETTINGS = "settings";
		
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
		
		public static Expression createdAt(long from, long to) {
			return matchRange(Fields.CREATED_AT, from, to);
		}
		
		public static Expression dependency(String queryString) {
			return ResourceDocument.Expressions.dependency(queryString);
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
		private Long updatedAt;
		private String toolingId;
		private String url;
		private String author;

		private String resourceDescription;
		private String title;
		private String status;
		private String contact;
		private String copyright;
		private String language;
		private String purpose;
		private String oid;
		private SortedSet<DependencyDocument> dependencies;
		private Map<String, Object> settings;
		
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
		
		public Builder updatedAt(Long updatedAt) {
			this.updatedAt = updatedAt;
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
		
		public Builder resourceDescription(String resourceDescription) {
			this.resourceDescription = resourceDescription;
			return this;
		}
		
		public Builder title(String title) {
			this.title = title;
			return this;
		}
		
		public Builder status(String status) {
			this.status = status;
			return this;
		}
		
		public Builder contact(String contact) {
			this.contact = contact;
			return this;
		}
		
		public Builder copyright(String copyright) {
			this.copyright = copyright;
			return this;
		}
		
		public Builder language(String language) {
			this.language = language;
			return this;
		}
		
		public Builder purpose(String purpose) {
			this.purpose = purpose;
			return this;
		}
		
		public Builder oid(String oid) {
			this.oid = oid;
			return this;
		}
		
		public Builder dependencies(SortedSet<DependencyDocument> dependencies) {
			this.dependencies = dependencies;
			return this;
		}
		
		public Builder settings(Map<String, Object> settings) {
			this.settings = (settings == null) ? null : Map.copyOf(settings);
			return this;
		}
		
		@JsonIgnore
		public Builder resourceSnapshot(TerminologyResource resourceSnapshot) {
			if (resourceSnapshot != null) {
				return this
					.resourceDescription(resourceSnapshot.getDescription())
					.title(resourceSnapshot.getTitle())
					.status(resourceSnapshot.getStatus())
					.contact(resourceSnapshot.getContact())
					.copyright(resourceSnapshot.getCopyright())
					.language(resourceSnapshot.getLanguage())
					.purpose(resourceSnapshot.getPurpose())
					.oid(resourceSnapshot.getOid())
					.dependencies(resourceSnapshot.getDependencies() == null ? null : resourceSnapshot.getDependencies().stream().map(Dependency::toDocument).collect(Collectors.toCollection(TreeSet::new)))
					.settings(resourceSnapshot.getSettings());
			} else {
				return this
					.resourceDescription(null)
					.title(null)
					.status(null)
					.contact(null)
					.copyright(null)
					.language(null)
					.purpose(null)
					.oid(null)
					.dependencies(null)
					.settings(null);
			}
		}
		
		// index only fields, for searching, sorting, etc.
		
		@JsonSetter
		Builder created(RevisionBranchPoint created) {
			return this;
		}
		
		@JsonSetter
		Builder revised(List<RevisionBranchPoint> revised) {
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
		Builder resourceId(String resourceId) {
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
				updatedAt,
				toolingId,
				url,
				author,
				
				resourceDescription,
				title,
				status,
				contact,
				copyright,
				language,
				purpose,
				oid,
				dependencies,
				settings
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
	// XXX updates are not supported, this value is always the same as createdAt, but in order to be searchable, we need a field
	private final Long updatedAt;
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

	// a snapshot of the corresponding resource document fields at the point of versioning (not indexed)
	private final String resourceDescription;
	private final String title;
	private final String status;
	private final String contact;
	private final String copyright;
	private final String language;
	private final String purpose;
	private final String oid;
	
	@Field(index = false) 
	private final Map<String, Object> settings;
	private final SortedSet<DependencyDocument> dependencies;
	
	// XXX derived field requires a mapping declaration to be present here
	private String resourceBranchPath;

	private VersionDocument(
		final String id, 
		final String version,
		final String description,
		final Long effectiveTime, 
		final ResourceURI resource,
		final String branchPath,
		final Long createdAt,
		final Long updatedAt,
		final String toolingId,
		final String url,
		final String author,
		
		final String resourceDescription,
		final String title,
		final String status,
		final String contact,
		final String copyright,
		final String language,
		final String purpose,
		final String oid,
		final SortedSet<DependencyDocument> dependencies,
		final Map<String, Object> settings) {
		
		this.id = id;
		this.version = version;
		this.description = description;
		this.effectiveTime = effectiveTime;
		this.resource = resource;
		this.resourceId = resource != null ? resource.getResourceId() : null;
		this.resourceType = resource != null ? resource.getResourceType() : null;
		this.branchPath = branchPath;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
		this.toolingId = toolingId;
		this.url = url;
		this.author = author;
		this.created = createdAt != null ? new RevisionBranchPoint(RevisionBranch.MAIN_BRANCH_ID, createdAt) : null;
		
		this.resourceDescription = resourceDescription;
		this.title = title;
		this.status = status;
		this.contact = contact;
		this.copyright = copyright;
		this.language = language;
		this.purpose = purpose;
		this.oid = oid;
		this.dependencies = dependencies;
		this.settings = settings;
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

	public Long getUpdatedAt() {
		return updatedAt;
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
	
	public String getResourceDescription() {
		return resourceDescription;
	}
	
	public String getTitle() {
		return title;
	}
	
	public String getStatus() {
		return status;
	}
	
	public String getContact() {
		return contact;
	}
	
	public String getCopyright() {
		return copyright;
	}
	
	public String getLanguage() {
		return language;
	}
	
	public String getPurpose() {
		return purpose;
	}
	
	public String getOid() {
		return oid;
	}
	
	public Map<String, Object> getSettings() {
		return settings;
	}
	
	public SortedSet<DependencyDocument> getDependencies() {
		return dependencies;
	}
	
	@Override
	public String extractSubjectId() {
		return getResource().toString();
	}
	
	// additional helpers
	
	/**
	 * @return the effective time as a {@link LocalDate} value.
	 */
	@JsonIgnore
	public LocalDate getEffectiveTimeAsLocalDate() {
		return effectiveTime == null ? null : EffectiveTimes.toDate(effectiveTime);
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
			.add("updatedAt", updatedAt)
			.add("url", url)
			.add("author", author)
			.add("resourceDescription", resourceDescription)
			.add("title", title)
			.add("status", status)
			.add("contact", contact)
			.add("copyright", copyright)
			.add("language", language)
			.add("purpose", purpose)
			.add("oid", oid)
			.add("dependencies", dependencies)
			.add("settings", settings)
			.toString();
	}
}
