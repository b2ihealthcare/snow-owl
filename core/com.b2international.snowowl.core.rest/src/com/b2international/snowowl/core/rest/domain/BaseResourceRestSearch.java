/*
 * Copyright 2021-2022 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.rest.domain;

import java.util.List;

import io.swagger.v3.oas.annotations.Parameter;

/**
 * @since 8.0
 */
public abstract class BaseResourceRestSearch extends ObjectRestSearch {

	@Parameter(description = "The title to match (using full-text search capabilities)")
	private String title;
	
	@Parameter(description = "One or more exact titles to match")
	private List<String> titleExact;
	
	@Parameter(description = "One or more container bundles to match")
	private List<String> bundleId;
	
	@Parameter(description = "One or more container ancestor bundles to match")
	private List<String> bundleAncestorId;

	@Parameter(description = "The timestamp to use for historical ('as of') queries")
	private Long timestamp;
	
	@Parameter(description = "One or more owner user ids to match")
	private List<String> owner;
	
	public List<String> getTitleExact() {
		return titleExact;
	}

	public void setTitleExact(List<String> titleExact) {
		this.titleExact = titleExact;
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<String> getBundleId() {
		return bundleId;
	}

	public void setBundleId(List<String> bundleId) {
		this.bundleId = bundleId;
	}
	
	public List<String> getBundleAncestorId() {
		return bundleAncestorId;
	}
	
	public void setBundleAncestorId(List<String> bundleAncestorId) {
		this.bundleAncestorId = bundleAncestorId;
	}
	
	public Long getTimestamp() {
		return timestamp;
	}
	
	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}
	
	public List<String> getOwner() {
		return owner;
	}
	
	public void setOwner(List<String> owner) {
		this.owner = owner;
	}

}
