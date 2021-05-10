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
package com.b2international.snowowl.core.rest.codesystem;

import com.b2international.snowowl.core.ResourceURI;

/**
 * @since 1.0
 */
public class VersionRestInput {

	private ResourceURI resource;
	private String version;
	private String description = "";
	private String effectiveTime;
	private Boolean force = Boolean.FALSE;
	
	public ResourceURI getResource() {
		return resource;
	}
	
	public String getDescription() {
		return description;
	}
	
	public String getEffectiveTime() {
		return effectiveTime;
	}
	
	public String getVersion() {
		return version;
	}
	
	public Boolean isForce() {
		return force;
	}

	public void setResource(ResourceURI resource) {
		this.resource = resource;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public void setEffectiveTime(String effectiveTime) {
		this.effectiveTime = effectiveTime;
	}
	
	public void setVersion(String version) {
		this.version = version;
	}
	
	public void setForce(Boolean force) {
		this.force = force;
	}
	
}
