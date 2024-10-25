/*
 * Copyright 2021-2023 B2i Healthcare, https://b2ihealthcare.com
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

import com.b2international.snowowl.core.rest.domain.BaseResourceRestSearch;

import io.swagger.v3.oas.annotations.Parameter;

/**
 * @since 8.0
 */
public class TerminologyResourceRestSearch extends BaseResourceRestSearch {

	@Parameter
	private List<String> toolingId;
	
	@Parameter
	private List<String> status;

	@Parameter
	private List<String> oid;
	
	@Parameter
	private List<String> url;
	
	@Parameter
	private String dependency;
	
	@Parameter
	private Boolean inUpgrade;
	
	public List<String> getOid() {
		return oid;
	}
	
	public void setOid(List<String> oid) {
		this.oid = oid;
	}
	
	public List<String> getToolingId() {
		return toolingId;
	}

	public void setToolingId(List<String> toolingId) {
		this.toolingId = toolingId;
	}

	public List<String> getStatus() {
		return status;
	}
	
	public void setStatus(List<String> status) {
		this.status = status;
	}

	public List<String> getUrl() {
		return url;
	}
	
	public void setUrl(List<String> url) {
		this.url = url;
	}
	
	public String getDependency() {
		return dependency;
	}
	
	public void setDependency(String dependency) {
		this.dependency = dependency;
	}

	public Boolean getInUpgrade() {
		return inUpgrade;
	}

	public void setInUpgrade(Boolean inUpgrade) {
		this.inUpgrade = inUpgrade;
	}
}
