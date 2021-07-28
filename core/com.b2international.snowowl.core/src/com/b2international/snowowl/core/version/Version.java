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
package com.b2international.snowowl.core.version;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.branch.BranchPathUtils;
import com.b2international.snowowl.core.date.DateFormats;
import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;

/**
 * @since 8.0
 */
public final class Version implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String id;
	private String version;
	private String description;
	private LocalDate effectiveTime;
	private ResourceURI resource;
	private String branchPath;
	private Long createdAt;
	private String author;
	
	/**
	 * @return the globally unique identifier of this version, resource + version.
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return the day from where this version is effective.
	 */
	public LocalDate getEffectiveTime() {
		return effectiveTime;
	}

	/**
	 * @return the description of the version
	 */
	public String getDescription() {
		return description;
	}
	
	public String getVersion() {
		return version;
	}

	public ResourceURI getResource() {
		return resource;
	}
	
	public String getBranchPath() {
		return branchPath;
	}
	
	public Long getCreatedAt() {
		return createdAt;
	}
	
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	@JsonFormat(shape=Shape.STRING, pattern=DateFormats.SHORT, timezone="UTC")
	public LocalDateTime getCreatedAtDateTime() {
		return createdAt == null ? null : LocalDateTime.ofInstant(Instant.ofEpochMilli(createdAt), ZoneOffset.UTC);
	}
	
	public String getAuthor() {
		return author;
	}

	// hidden setter to allow deserialization of Version JSON strings without errors
	@JsonSetter
	void setResourceBranchPath(String resourceBranchPath) {}
	
	public String getResourceBranchPath() {
		return BranchPathUtils.createPath(branchPath).getParentPath();
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public void setEffectiveTime(LocalDate effectiveTime) {
		this.effectiveTime = effectiveTime;
	}
	
	public void setDescription(final String description) {
		this.description = description;
	}
	
	public void setVersion(String version) {
		this.version = version;
	}
	
	public void setResource(ResourceURI resource) {
		this.resource = resource;
	}
	
	public void setBranchPath(String branchPath) {
		this.branchPath = branchPath;
	}
	
	public void setCreatedAt(Long createdAt) {
		this.createdAt = createdAt;
	}
	
	public void setAuthor(String author) {
		this.author = author;
	}
	
	// additional helper methods
	
	@JsonIgnore
	public String getResourceId() {
		return getResource().getResourceId();
	}
	
	@JsonIgnore
	public ResourceURI getVersionResourceURI() {
		return getResource().withPath(version);
	}

}