/*
 * Copyright 2023 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.internal;

import java.util.Comparator;
import java.util.Objects;

import com.b2international.snowowl.core.ResourceURIWithQuery;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 8.12.0
 */
public final class DependencyDocument implements Comparable<DependencyDocument> {

	private static final Comparator<DependencyDocument> COMPARATOR = Comparator
			.comparing(DependencyDocument::getResourceUri)
			.thenComparing(DependencyDocument::getScope);
	
	private final ResourceURIWithQuery resourceUri;
	private final String scope;
	
	// derived fields for easier searching
	private final String resourceType;
	private final String resourceId;
	private final String path;
	private final String query;
	
	@JsonCreator
	public DependencyDocument(@JsonProperty("resourceUri") ResourceURIWithQuery resourceUri, @JsonProperty("scope") String scope) {
		this.resourceUri = Objects.requireNonNull(resourceUri);
		this.scope = scope;
		// configure derived fields
		this.resourceType = resourceUri.getResourceUri().getResourceType();
		this.resourceId = resourceUri.getResourceUri().getResourceId();
		this.path = resourceUri.getResourceUri().getPath();
		this.query = resourceUri.getQuery();
	}
	
	public ResourceURIWithQuery getResourceUri() {
		return resourceUri;
	}
	
	public String getScope() {
		return scope;
	}
	
	// derived fields
	
	public String getResourceType() {
		return resourceType;
	}
	
	public String getResourceId() {
		return resourceId;
	}
	
	public String getPath() {
		return path;
	}
	
	public String getQuery() {
		return query;
	}
	
	@Override
	public int compareTo(DependencyDocument other) {
		return COMPARATOR.compare(this, other);
	}
	
}
