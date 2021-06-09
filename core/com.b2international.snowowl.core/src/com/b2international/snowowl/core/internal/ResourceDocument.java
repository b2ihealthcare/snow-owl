/*
 * Copyright 2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.internal;

import static com.b2international.index.query.Expressions.exactMatch;
import static com.b2international.index.query.Expressions.matchAny;
import static com.b2international.index.query.Expressions.matchTextAll;
import static com.b2international.index.query.Expressions.regexp;

import java.util.Map;
import java.util.stream.Collectors;

import com.b2international.commons.collections.Collections3;
import com.b2international.index.Analyzers;
import com.b2international.index.Doc;
import com.b2international.index.Normalizers;
import com.b2international.index.mapping.Field;
import com.b2international.index.mapping.FieldAlias;
import com.b2international.index.mapping.FieldAlias.FieldAliasType;
import com.b2international.index.query.Expression;
import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.repository.RevisionDocument;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

/**
 * @since 8.0
 */
@Doc(
	type = "resource",
	revisionHash = {
		ResourceDocument.Fields.URL,
		ResourceDocument.Fields.TITLE,
		ResourceDocument.Fields.LANGUAGE,
		ResourceDocument.Fields.DESCRIPTION,
		ResourceDocument.Fields.STATUS,
		ResourceDocument.Fields.COPYRIGHT,
		ResourceDocument.Fields.OWNER,
		ResourceDocument.Fields.CONTACT,
		ResourceDocument.Fields.USAGE,
		ResourceDocument.Fields.PURPOSE,
	}
)
@JsonDeserialize(builder = ResourceDocument.Builder.class)
public final class ResourceDocument extends RevisionDocument {

	/**
	 * @since 8.0
	 */
	public static final class Fields extends RevisionDocument.Fields {

		// common resource fields
		public static final String RESOURCE_TYPE = "resourceType";
		public static final String URL = "url";
		public static final String TITLE = "title";
		public static final String LANGUAGE = "language";
		public static final String DESCRIPTION = "description";
		public static final String STATUS = "status";
		public static final String COPYRIGHT = "copyright";
		public static final String OWNER = "owner";
		public static final String CONTACT = "contact";
		public static final String USAGE = "usage";
		public static final String PURPOSE = "purpose";
		public static final String BUNDLE_ID = "bundleId";
		
		// specialized resource fields
		public static final String OID = "oid";
		public static final String BRANCH_PATH = "branchPath";
		public static final String TOOLING_ID = "toolingId";
		public static final String EXTENSION_OF = "extensionOf";
		public static final String UPGRADE_OF = "upgradeOf";
		public static final String SETTINGS = "settings";
		
		// analyzed fields
		private static final String TITLE_PREFIX   = TITLE + ".prefix";
		private static final String TITLE_EXACT    = TITLE + ".exact";
		private static final String TITLE_TEXT     = TITLE + ".text";
		
	}
	
	/**
	 * @since 8.0
	 */
	public static final class Expressions extends RevisionDocument.Expressions {
		
		public static Expression resourceType(String resourceType) {
			return exactMatch(Fields.RESOURCE_TYPE, resourceType);
		}
		
		public static Expression resourceTypes(Iterable<String> resourceTypes) {
			return matchAny(Fields.RESOURCE_TYPE, resourceTypes);
		}
		
		public static Expression matchTitleExact(String term) {
			return matchTextAll(Fields.TITLE_EXACT, term);
		}
		
		public static Expression title(String title) {
			return exactMatch(Fields.TITLE, title);
		}
		
		public static Expression titles(Iterable<String> titles) {
			return matchAny(Fields.TITLE, titles);
		}
		
		public static Expression matchTitleRegex(String regex) {
			return regexp(Fields.TITLE, regex);
		}
		
		public static Expression matchTitleAllPrefixesPresent(String term) {
			return matchTextAll(Fields.TITLE_PREFIX, term);
		}
		
		public static Expression matchTitleAllTermsPresent(String term) {
			return matchTextAll(Fields.TITLE_TEXT, term);
		}
		
		public static Expression toolingIds(Iterable<String> toolingIds) {
			return matchAny(Fields.TOOLING_ID, toolingIds);
		}
		
		public static Expression branchPaths(Iterable<String> branchPaths) {
			return matchAny(Fields.BRANCH_PATH, branchPaths);
		}
		
		public static Expression oid(String oid) {
			return exactMatch(Fields.OID, oid);
		}
		
		public static Expression oids(Iterable<String> oids) {
			return matchAny(Fields.OID, oids);
		}
		
		public static Expression extensionOf(Iterable<ResourceURI> extensionOfs) {
			return matchAny(Fields.EXTENSION_OF, Collections3.toImmutableSet(extensionOfs).stream().map(ResourceURI::toString).collect(Collectors.toSet()));
		}
		
		public static Expression upgradeOfs(Iterable<ResourceURI> upgradeOfs) {
			return matchAny(Fields.UPGRADE_OF, Collections3.toImmutableSet(upgradeOfs).stream().map(ResourceURI::toString).collect(Collectors.toSet()));
		}
		
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	public static Builder builder(ResourceDocument from) {
		return builder()
				.id(from.getId())
				.iconId(from.getIconId())
				.resourceType(from.getResourceType())
				.url(from.getUrl())
				.title(from.getTitle())
				.language(from.getLanguage())
				.description(from.getDescription())
				.status(from.getStatus())
				.copyright(from.getCopyright())
				.owner(from.getOwner())
				.contact(from.getContact())
				.usage(from.getUsage())
				.purpose(from.getPurpose())
				.bundleId(from.getBundleId())
				.oid(from.getOid())
				.branchPath(from.getBranchPath())
				.toolingId(from.getToolingId())
				.extensionOf(from.getExtensionOf())
				.upgradeOf(from.getUpgradeOf())
				.settings(from.getSettings());
	}
	
	@JsonPOJOBuilder(withPrefix = "")
	public static final class Builder extends RevisionDocument.RevisionDocumentBuilder<Builder, RevisionDocument> {
		
		// common resource fields
		private String resourceType;
		private String url;
		private String title;
		private String language;
		private String description;
		private String status;
		private String copyright;
		private String owner;
		private String contact;
		private String usage;
		private String purpose;
		private String bundleId;
		
		// specialized resource fields
		private String oid;
		private String branchPath;
		private String toolingId;
		private ResourceURI extensionOf;
		private ResourceURI upgradeOf;
		private Map<String, Object> settings;
		
		@JsonCreator
		private Builder() {
		}
		
		public Builder resourceType(String resourceType) {
			this.resourceType = resourceType;
			return getSelf();
		}
		
		public Builder url(String url) {
			this.url = url;
			return getSelf();
		}
		
		public Builder title(String title) {
			this.title = title;
			return getSelf();
		}
		
		public Builder language(String language) {
			this.language = language;
			return getSelf();
		}
		
		public Builder description(String description) {
			this.description = description;
			return getSelf();
		}
		
		public Builder status(String status) {
			this.status = status;
			return getSelf();
		}
		
		public Builder copyright(String copyright) {
			this.copyright = copyright;
			return getSelf();
		}
		
		public Builder owner(String owner) {
			this.owner = owner;
			return getSelf();
		}
		
		public Builder contact(String contact) {
			this.contact = contact;
			return getSelf();
		}
		
		public Builder usage(String usage) {
			this.usage = usage;
			return getSelf();
		}
		
		public Builder purpose(String purpose) {
			this.purpose = purpose;
			return getSelf();
		}

		public Builder bundleId(String bundleId) {
			this.bundleId = bundleId;
			return getSelf();
		}
		
		public Builder oid(String oid) {
			this.oid = oid;
			return getSelf();
		}
		
		public Builder branchPath(String branchPath) {
			this.branchPath = branchPath;
			return getSelf();
		}
		
		public Builder toolingId(String toolingId) {
			this.toolingId = toolingId;
			return getSelf();
		}
		
		public Builder extensionOf(ResourceURI extensionOf) {
			this.extensionOf = extensionOf;
			return getSelf();
		}
		
		public Builder upgradeOf(ResourceURI upgradeOf) {
			this.upgradeOf = upgradeOf;
			return getSelf();
		}
		
		public Builder settings(Map<String, Object> settings) {
			this.settings = settings;
			return getSelf();
		}
		
		@Override
		protected Builder getSelf() {
			return this;
		}

		@Override
		public ResourceDocument build() {
			return new ResourceDocument(
				id, 
				iconId, 
				resourceType, 
				url, 
				title, 
				language, 
				description, 
				status, 
				copyright, 
				owner, 
				contact, 
				usage, 
				purpose,
				bundleId,
				oid,
				branchPath,
				toolingId,
				extensionOf,
				upgradeOf,
				settings
			);
		}
		
	}

	// common resource fields
	private final String resourceType;
	private final String url;
	@Field(
		aliases = {
			@FieldAlias(name = "prefix", type = FieldAliasType.TEXT, analyzer=Analyzers.PREFIX, searchAnalyzer=Analyzers.TOKENIZED),
			@FieldAlias(name = "text", type = FieldAliasType.TEXT, analyzer=Analyzers.TOKENIZED),
			@FieldAlias(name = "exact", type = FieldAliasType.KEYWORD, normalizer = Normalizers.LOWER_ASCII)
		}
	)
	private final String title;
	private final String language;
	private final String description;
	private final String status;
	private final String copyright;
	private final String owner;
	private final String contact;
	private final String usage;
	private final String purpose;
	private final String bundleId;
	
	// specialized resource fields
	private final String oid;
	private final String branchPath;
	private final String toolingId;
	private final ResourceURI extensionOf;
	private final ResourceURI upgradeOf;
	private final Map<String, Object> settings;
	
	public ResourceDocument(
			final String id, 
			final String iconId, 
			final String resourceType,
			final String url,
			final String title,
			final String language,
			final String description,
			final String status,
			final String copyright,
			final String owner,
			final String contact,
			final String usage,
			final String purpose,
			final String bundleId,
			final String oid,
			final String branchPath,
			final String toolingId,
			final ResourceURI extensionOf,
			final ResourceURI upgradeOf,
			final Map<String, Object> settings) {
		super(id, iconId);
		this.resourceType = resourceType;
		this.url = url;
		this.title = title;
		this.language = language;
		this.description = description;
		this.status = status;
		this.copyright = copyright;
		this.owner = owner;
		this.contact = contact;
		this.usage = usage;
		this.purpose = purpose;
		this.bundleId = bundleId;
		this.oid = oid;
		this.branchPath = branchPath;
		this.toolingId = toolingId;
		this.extensionOf = extensionOf;
		this.upgradeOf = upgradeOf;
		this.settings = settings;
	}

	@JsonIgnore
	public ResourceURI getResourceURI() {
		return ResourceURI.of(resourceType, getId());
	}
	
	public String getResourceType() {
		return resourceType;
	}
	
	public String getUrl() {
		return url;
	}
	
	public String getTitle() {
		return title;
	}
	
	public String getLanguage() {
		return language;
	}
	
	public String getDescription() {
		return description;
	}
	
	public String getStatus() {
		return status;
	}
	
	public String getCopyright() {
		return copyright;
	}
	
	public String getOwner() {
		return owner;
	}
	
	public String getContact() {
		return contact;
	}
	
	public String getUsage() {
		return usage;
	}
	
	public String getPurpose() {
		return purpose;
	}
	
	public String getBundleId() {
		return bundleId;
	}
	
	public String getOid() {
		return oid;
	}
	
	public String getBranchPath() {
		return branchPath;
	}
	
	public String getToolingId() {
		return toolingId;
	}
	
	public ResourceURI getExtensionOf() {
		return extensionOf;
	}
	
	public ResourceURI getUpgradeOf() {
		return upgradeOf;
	}
	
	public Map<String, Object> getSettings() {
		return settings;
	}

}
