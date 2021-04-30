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
import java.time.LocalDate;

import com.b2international.snowowl.core.ResourceURI;
import com.fasterxml.jackson.annotation.JsonIgnore;

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
	
	// additional helper methods
	
	@JsonIgnore
	public String getResourceId() {
		return getResource().getResourceId();
	}

}