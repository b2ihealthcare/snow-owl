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
package com.b2international.snowowl.core.rest.resource;

import java.util.List;

import com.b2international.snowowl.core.rest.domain.ObjectRestSearch;

import io.swagger.annotations.ApiParam;

/**
 * @since 8.0
 */
public class ResourceRestSearch extends ObjectRestSearch {

	@ApiParam
	private List<String> branch;
	
	@ApiParam
	private List<String> resourceType;
	
	@ApiParam
	private List<String> titleExact;
	
	@ApiParam
	private String title;
	
	@ApiParam
	private List<String> toolingId;

	public List<String> getBranch() {
		return branch;
	}

	public void setBranch(List<String> branchPath) {
		this.branch = branchPath;
	}

	public List<String> getResourceType() {
		return resourceType;
	}

	public void setResourceType(List<String> resourceType) {
		this.resourceType = resourceType;
	}

	public List<String> getTitleExact() {
		return titleExact;
	}

	public void setTitleExact(List<String> titleExact) {
		this.titleExact = titleExact;
	}

	public List<String> getToolingId() {
		return toolingId;
	}

	public void setToolingId(List<String> toolingId) {
		this.toolingId = toolingId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
}
