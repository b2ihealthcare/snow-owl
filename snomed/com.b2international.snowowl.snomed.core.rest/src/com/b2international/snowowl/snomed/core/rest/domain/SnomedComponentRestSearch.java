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
package com.b2international.snowowl.snomed.core.rest.domain;

import java.util.List;

import io.swagger.v3.oas.annotations.Parameter;

/**
 * @since 8.0
 */
public class SnomedComponentRestSearch extends SnomedRestSearch {

	@Parameter(description = "The namespace values to match")
	private List<String> namespace;
	
	@Parameter(description = "The namespace concept SCTIDs to match")
	private List<String> namespaceConceptId;

	@Parameter(description = "Matches should be active members of the following reference set(s)")
	private List<String> isActiveMemberOf;

	public List<String> getNamespace() {
		return namespace;
	}

	public void setNamespace(List<String> namespace) {
		this.namespace = namespace;
	}
	
	public List<String> getNamespaceConceptId() {
		return namespaceConceptId;
	}
	
	public void setNamespaceConceptId(List<String> namespaceConceptId) {
		this.namespaceConceptId = namespaceConceptId;
	}

	public List<String> getIsActiveMemberOf() {
		return isActiveMemberOf;
	}
	
	public void setIsActiveMemberOf(List<String> isActiveMemberOf) {
		this.isActiveMemberOf = isActiveMemberOf;
	}
}
