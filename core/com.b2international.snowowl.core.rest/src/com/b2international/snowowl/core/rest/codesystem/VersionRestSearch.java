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
package com.b2international.snowowl.core.rest.codesystem;

import java.util.List;

import com.b2international.snowowl.core.rest.domain.ObjectRestSearch;

import io.swagger.v3.oas.annotations.Parameter;

/**
 * @since 8.0
 */
public class VersionRestSearch extends ObjectRestSearch {

	@Parameter(description="The corresponding resource identifier(s) to match")
	private List<String> resource;
	
	public List<String> getResource() {
		return resource;
	}
	
	public void setResource(List<String> resource) {
		this.resource = resource;
	}
	
}
