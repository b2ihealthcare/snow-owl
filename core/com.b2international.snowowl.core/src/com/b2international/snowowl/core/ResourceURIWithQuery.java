/*
 * Copyright 2022 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core;

import java.io.Serializable;

import org.elasticsearch.common.Strings;

import com.b2international.commons.exceptions.BadRequestException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @since 8.5
 */
public final class ResourceURIWithQuery implements Serializable, Comparable<ResourceURIWithQuery> {

	private static final long serialVersionUID = 1L;

	private static final String QUERY_PART_SEPARATOR = "?";
	
	private final String uri;
	private final ResourceURI resourceUri;
	private final String query;
	
	@JsonCreator
	public ResourceURIWithQuery(String uri) {
		if (Strings.isNullOrEmpty(uri)) {
			throw new BadRequestException("Malformed Resource URI value: '%s' is empty.", uri);
		}
		
		int firstQueryCharAt = uri.indexOf(QUERY_PART_SEPARATOR);
		// if no query part specified, then set end of text
		if (firstQueryCharAt == -1) {
			firstQueryCharAt = uri.length();
		}
		this.uri = uri;
		this.resourceUri = new ResourceURI(uri.substring(0, firstQueryCharAt));
		this.query = firstQueryCharAt == uri.length() ? "" : uri.substring(firstQueryCharAt + 1, uri.length()); 
	}
	
	public String getUri() {
		return uri;
	}
	
	public ResourceURI getResourceUri() {
		return resourceUri;
	}
	
	public String getQuery() {
		return query;
	}
	
	@Override
	public int compareTo(ResourceURIWithQuery o) {
		return toString().compareTo(o.toString());
	}
	
	@JsonValue
	@Override
	public String toString() {
		return getUri();
	}
	
}
