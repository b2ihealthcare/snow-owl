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
package com.b2international.snowowl.core;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.elasticsearch.common.Strings;

import com.b2international.commons.CompareUtils;
import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.snowowl.core.branch.Branch;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.base.Preconditions;

/**
 * @since 8.0
 */
public final class ResourceURI implements Serializable, Comparable<ResourceURI> {

	private static final long serialVersionUID = 1L;

	@JsonIgnore
	private static final Pattern URI_PATTERN = Pattern.compile("^([^\\/]+)[\\/]{1}([^\\/]+)(([\\/]{1}[^\\/\\s]+)*)$");
	
	/**
	 * Special path key, that represents the latest released version of a code system.
	 */
	public static final String LATEST = "LATEST";
	
	/**
	 * Special path key, that represents the latest development version of a code system (often referenced as MAIN).
	 */
	public static final String HEAD = "HEAD"; 
	
	/**
	 * Considering the usage of next as a special path key (tag) as it is getting popularity in other systems
	 * HEAD or NEXT???
	 */
	public static final String NEXT = "";
	
	// the original value
	private final String uri;
	
	// the type of the resource, concrete class of Resource
	private final String resourceType;
	
	// the actual ID of the resource, see Resource.id
	private final String resourceId;

	// relative location or version of the resource
	private final String path;
	
	@JsonCreator
	public ResourceURI(String uri) throws BadRequestException {
		if (Strings.isNullOrEmpty(uri)) {
			throw new BadRequestException("Malformed Resource URI value: '%s' is empty.", uri);
		}

		if (uri.startsWith(Branch.MAIN_PATH)) {
			throw new BadRequestException("Malformed Resource URI value: '%s' cannot start with MAIN.", uri);
		}
		
		final Matcher matcher = URI_PATTERN.matcher(uri);
		if (!matcher.matches()) {
			throw new BadRequestException("Malformed Resource URI value: '%s' must be in format '<resourceType>/<resourceId>/<path>'.", uri);
		}
		// ignore HEAD in path part by automatically removing it from the uri
		this.uri = uri.replaceAll("/HEAD", ""); 
		this.resourceType = matcher.group(1);
		this.resourceId = matcher.group(2);
		this.path = CompareUtils.isEmpty(matcher.group(3)) ? HEAD : matcher.group(3).substring(1); // removes the leading slash character
	}
	
	public String getUri() {
		return uri;
	}
	
	public String getResourceType() {
		return resourceType;
	}
	
	public String getResourceId() {
		return resourceId;
	}
	
	public String getPath() {
		return path;
	}
	
	public boolean isLatest() {
		return LATEST.equals(getPath());
	}
	
	public boolean isHead() {
		return HEAD.equals(getPath());
	}
	
	public boolean isNext() {
		return NEXT.equals(getPath());
	}
	
	public ResourceURI withPath(String path) {
		return Strings.isNullOrEmpty(path) ? ResourceURI.of(resourceType, resourceId) : ResourceURI.branch(resourceType, resourceId, path);
	}
	
	@JsonIgnore
	public ResourceURI withoutPath() {
		return new ResourceURI(String.join(Branch.SEPARATOR, resourceType, resourceId));
	}
	
	@JsonIgnore
	public String withoutResourceType() {
		return isHead() ? resourceId : String.join(Branch.SEPARATOR, resourceId, path);
	}
	
	public ResourceURI asLatest() {
		return ResourceURI.latest(resourceType, resourceId);
	}
	
	public ResourceURI asNext() {
		return ResourceURI.next(resourceType, resourceId);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(uri);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		ResourceURI other = (ResourceURI) obj;
		return Objects.equals(uri, other.uri);
	}

	@JsonValue
	@Override
	public String toString() {
		return getUri();
	}
	
	@Override
	public int compareTo(ResourceURI o) {
		return toString().compareTo(o.toString());
	}

	public static ResourceURI branch(String resourceType, String resourceId, String path) {
		Preconditions.checkArgument(!resourceId.contains(Branch.SEPARATOR), "Resource ID should not be an URI already. Got: %s", resourceId);
		return new ResourceURI(String.join(Branch.SEPARATOR, resourceType, resourceId, path));
	}

	public static ResourceURI latest(String resourceType, String resourceId) {
		Preconditions.checkArgument(!resourceId.contains(Branch.SEPARATOR), "Resource ID should not be an URI already. Got: %s", resourceId);
		return new ResourceURI(String.join(Branch.SEPARATOR, resourceType, resourceId, LATEST));
	}
	
	public static ResourceURI next(String resourceType, String resourceId) {
		Preconditions.checkArgument(!resourceId.contains(Branch.SEPARATOR), "Resource ID should not be an URI already. Got: %s", resourceId);
		return new ResourceURI(String.join(Branch.SEPARATOR, resourceType, resourceId, NEXT));
	}

	public static ResourceURI of(String resourceType, String resourceId) {
		checkNotNull(resourceType, "'resourceType' must be specified");
		checkNotNull(resourceId, "'resourceId' must be specified");
		return new ResourceURI(String.join(Branch.SEPARATOR, resourceType, resourceId));
	}

}
