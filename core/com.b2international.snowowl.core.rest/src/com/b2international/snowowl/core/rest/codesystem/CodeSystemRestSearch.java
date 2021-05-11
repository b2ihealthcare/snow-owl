/*
 * Copyright 2020-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.List;

import com.b2international.snowowl.core.rest.domain.ResourceRestSearch;

import io.swagger.annotations.ApiParam;

/**
 * @since 7.6.0
 */
public final class CodeSystemRestSearch extends ResourceRestSearch {

	@ApiParam
	private String title;
	
	@ApiParam
	private List<String> oid;
	
	@ApiParam
	private List<String> toolingId;
	
	public List<String> getOid() {
		return oid;
	}
	
	public String getTitle() {
		return title;
	}
	
	public List<String> getToolingId() {
		return toolingId;
	}
	
	public void setOid(List<String> oid) {
		this.oid = oid;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public void setToolingId(List<String> toolingId) {
		this.toolingId = toolingId;
	}
	
}
