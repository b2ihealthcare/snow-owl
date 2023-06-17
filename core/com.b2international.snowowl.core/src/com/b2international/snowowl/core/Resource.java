/*
 * Copyright 2021-2023 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core;

import java.io.Serializable;
import java.util.*;

import com.b2international.snowowl.core.commit.CommitInfo;
import com.b2international.snowowl.core.domain.IComponent;
import com.b2international.snowowl.core.internal.ResourceDocument;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.google.common.collect.ImmutableList;

/**
 * @since 8.0
 */
public abstract class Resource implements Serializable {

	private static final long serialVersionUID = 1L;

	// known retired status value from FHIR, TODO make it configurable when needed
	public static final String RETIRED_STATUS = "retired";
	public static final String DRAFT_STATUS = "draft";
	public static final String SNOMED_FIRST = "snomedFirst";
	
	public static final String SETTINGS_DELIMITER = "#";
	
	public static final class Settings {
		
		public static final String PUBLISHER = "publisher";
		public static final String DISTRIBUTABLE = "distributable"; // "true" or "false"
		
	}
	
	/**
	 * @since 8.0
	 */
	public static final class Fields {
		public static final String ID = ResourceDocument.Fields.ID;
		public static final String TITLE = ResourceDocument.Fields.TITLE;
		public static final String URL = ResourceDocument.Fields.URL;
		public static final String OWNER = ResourceDocument.Fields.OWNER;
		public static final String STATUS = ResourceDocument.Fields.STATUS;
		public static final String LANGUAGE = ResourceDocument.Fields.LANGUAGE;
		public static final String RESOURCE_TYPE = ResourceDocument.Fields.RESOURCE_TYPE;
		public static final String TYPE_RANK = ResourceDocument.Fields.TYPE_RANK;
		public static final String CREATED_AT = ResourceDocument.Fields.CREATED_AT;
		public static final String UPDATED_AT = ResourceDocument.Fields.UPDATED_AT;
		
		// TerminologyResource subtype specific fields, but for convenience and single API access, they are defined here
		public static final String OID = ResourceDocument.Fields.OID;
		
		public static final Set<String> ALL = Set.of(
			ID,
			TITLE,
			URL,
			STATUS,
			LANGUAGE,
			OWNER,
			OID,
			CREATED_AT,
			UPDATED_AT,
			RESOURCE_TYPE,
			TYPE_RANK,
			SNOMED_FIRST
		);
	}
	
	/**
	 * @since 8.1.0
	 */
	public static abstract class Expand {
		public static final String RESOURCE_PATH_LABELS = "resourcePathLabels";
		public static final String UPDATED_AT_COMMIT = "updatedAtCommit";
	}
	
	// unique identifier for each resource, can be auto-generated or manually specified
	private String id;

	// URL, eg. http://snomed.info/sct
	private String url;

	// FHIR Property, human-readable name of the resource (formerly CodeSystem.name in 7.x)
	private String title;
	
	// FHIR Property, primary language of this resource, must be a valid two letter ISO-639 language code, if defined
	private String language;
	
	// FHIR Property, supports markdown
	private String description;

	// FHIR Property, publication status of the resource, usually draft|active|retired|unknown (maybe experimental, etc.)
	private String status;

	// FHIR Property, supports markdown
	private String copyright;

	// ('publisher' FHIR property, but owner is way better for our use cases)
	private String owner;

	// FHIR Property, primary e-mail address or contact URL (FHIR property)
	private String contact;

	// useContext from FHIR is just too complex for our initial use cases, using former usage field instead
	private String usage;
	
	// FHIR property, supports markdown
	private String purpose;

	// Hierarchical path from the resource root; contains all indirect ancestor bundle ID(s) of this resource
	private List<String> bundleAncestorIds;
	
	// The ID of the bundle this resource is directly contained by
	private String bundleId;

	// The label of all bundles leading to this resource (expandable property)
	private List<String> resourcePathLabels;
	
	// The timestamp when the resource was created originally 
	private Long createdAt;
	
	// The timestamp when the resource was last modified (either its contents or its properties)
	private Long updatedAt;
	
	// The commit object that holds information about the last update
	private CommitInfo updatedAtCommit;
	
	// Resource metadata
	private Map<String, Object> settings;
	
	// Any additional properties in the JSON representation that are not defined above (usually added through plugins)
	private Map<String, Object> properties;
	
	/**
	 * @return the type of the resource
	 */
	public abstract String getResourceType();
	
	// XXX empty setter to make Jackson happy when deserializing
	@JsonSetter
	/*package*/ final void setResourceType(String resourceType) {}
	
	/**
	 * Logical id of this resource.
	 * 
	 * The logical id on the system that holds the System resource instance - this typically is expected to change as the resource moves from server to server. 
	 * The location URI is constructed by appending the logical id to the server base address where the instance is found and the resource type. 
	 * This URI should be a resolvable URL by which the resource instance may be retrieved, usually from a server, and it may be a relative reference typically to the server base URL.
	 * 
	 * @return
	 */
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public ResourceURI getResourceURI() {
		return ResourceURI.of(getResourceType(), getId());
	}

	// XXX empty setter to make Jackson happy when deserializing 
	@JsonSetter
	/*package*/ final void setResourceURI(ResourceURI resourceUri) {}

	/**
	 * The canonical URL that never changes for this resource - it is the same in every copy. This canonical URL is used to refer to all instances of
	 * this particular code system across all servers and systems. Ideally, this URI should be a URL which resolves to the location of the master
	 * version of the code system, though this is not always possible.
	 * 
	 * @return
	 */
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @return the human-readable name of this resource, eg. "{@code SNOMED Clinical Terms}"
	 */
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	/**
	 * The primary language tag of the resource, eg. "en_US"
	 * @return
	 */
	public String getLanguage() {
		return language;
	}
	
	public void setLanguage(String language) {
		this.language = language;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCopyright() {
		return copyright;
	}

	public void setCopyright(String copyright) {
		this.copyright = copyright;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public String getUsage() {
		return usage;
	}

	public void setUsage(String usage) {
		this.usage = usage;
	}

	public String getPurpose() {
		return purpose;
	}

	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}
	
	public List<String> getBundleAncestorIds() {
		return bundleAncestorIds;
	}
	
	public void setBundleAncestorIds(List<String> bundleAncestorIds) {
		this.bundleAncestorIds = bundleAncestorIds;
	}
	
	public String getBundleId() {
		return bundleId;
	}
	
	public void setBundleId(String bundleId) {
		this.bundleId = bundleId;
	}

	public Long getCreatedAt() {
		return createdAt;
	}
	
	public void setCreatedAt(Long createdAt) {
		this.createdAt = createdAt;
	}
	
	public Long getUpdatedAt() {
		return updatedAt;
	}
	
	public void setUpdatedAt(Long updatedAt) {
		this.updatedAt = updatedAt;
	}
	
	public CommitInfo getUpdatedAtCommit() {
		return updatedAtCommit;
	}
	
	public void setUpdatedAtCommit(CommitInfo updatedAtCommit) {
		this.updatedAtCommit = updatedAtCommit;
	}
	
	/**
	 * A configuration map storing additional key-value pairs specific to this terminology resource (can be {@code null}). Interpretation of values is
	 * implementation-dependent.
	 * 
	 * @return
	 */
	public Map<String, Object> getSettings() {
		return settings;
	}

	public void setSettings(Map<String, Object> settings) {
		this.settings = settings;
	}

	@JsonAnyGetter
	public Map<String, Object> getProperties() {
		return properties;
	}
	
	@JsonIgnore
	public void setProperties(Map<String, Object> properties) {
		this.properties = properties;
	}
	
	@JsonAnySetter
	public void setProperties(String key, Object value) {
		if (this.properties == null) {
			this.properties = new HashMap<>(3);
		}
		this.properties.put(key, value);
	}
	
	/**
	 * @return the ID of all bundles leading to the resource, starting with "-1" (the ID of the resource root), or <code>null</code> if ancestry information is not available
	 */
	public List<String> getResourcePathSegments() {
		final List<String> bundleAncestorIds = getBundleAncestorIds();
		final String bundleParentId = getBundleId();
		
		// if either parentId or ancestorIds list are null then skip calculating the resourcePathSegments (eg. field selection)
		if (bundleParentId == null || bundleAncestorIds == null) {
			return null;
		}
		
		if (IComponent.ROOT_ID.equals(bundleParentId)) {
			return bundleAncestorIds;
		}
		
		// Append our _parent ID_ to our ancestor ID array
		return ImmutableList.<String>builder() 
			.addAll(bundleAncestorIds)
			.add(bundleParentId)
			.build();
	}
	
	// XXX empty setter to make Jackson happy when deserializing
	@JsonSetter
	/*package*/ final void setResourcePathSegments(List<String> resourcePathSegments) {}
	
	public List<String> getResourcePathLabels() {
		return resourcePathLabels;
	}
	
	public void setResourcePathLabels(final List<String> resourcePathLabels) {
		this.resourcePathLabels = resourcePathLabels;
	}

	/**
	 * Converts this {@link Resource} to a commitable {@link ResourceDocument.Builder}
	 * @return
	 */
	public abstract ResourceDocument.Builder toDocumentBuilder();
	
}
