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

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;

import org.elasticsearch.common.Strings;

import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.snowowl.core.branch.Branch;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

/**
 * @since 8.5
 */
public final class ResourceURIWithQuery implements Serializable, Comparable<ResourceURIWithQuery> {

	private static final long serialVersionUID = 1L;

	public static final String QUERY_PART_SEPARATOR = "?";
	private static final String QUERY_KEY_SEPARATOR = "&";
	private static final String QUERY_KEY_VALUE_SEPARATOR = "=";
	
	private final String uri;
	private final ResourceURI resourceUri;
	private final String query;
	
	private Multimap<String, String> queryValues;
	
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
	
	ResourceURIWithQuery(ResourceURI resourceUri, String query) {
		this.resourceUri = resourceUri;
		this.query = query;
		this.uri = String.join(QUERY_PART_SEPARATOR, resourceUri.toString(), query);
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
	
	public Multimap<String, String> getQueryValues() {
		if (queryValues == null) {
			queryValues = HashMultimap.create();
			for (String keyValueRaw : this.query.split(QUERY_KEY_SEPARATOR)) {
				if (!Strings.isNullOrEmpty(keyValueRaw)) {
					String[] keyValue = keyValueRaw.split(QUERY_KEY_VALUE_SEPARATOR);
					if (keyValue.length == 2) {
						queryValues.put(keyValue[0], keyValue[1]);
					}
				}
			}
		}
		return queryValues;
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

	public static ResourceURIWithQuery of(String resourceType, String resourceIdWithQuery) {
		checkNotNull(resourceType, "'resourceType' must be specified");
		checkNotNull(resourceIdWithQuery, "'resourceIdWithQuery' must be specified");
		return new ResourceURIWithQuery(String.join(Branch.SEPARATOR, resourceType, resourceIdWithQuery));
	}
	
	public static ResourceURIWithQuery of(String resourceType, String resourceId, String query) {
		checkNotNull(resourceType, "'resourceType' must be specified");
		checkNotNull(resourceId, "'resourceId' must be specified");
		checkNotNull(query, "'query' must be specified");
		return new ResourceURIWithQuery(String.join(QUERY_PART_SEPARATOR, String.join(Branch.SEPARATOR, resourceType, resourceId), query));
	}
	
}
