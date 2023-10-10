/*
 * Copyright 2021-2023 B2i Healthcare Pte Ltd, http://b2i.sg
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

import com.b2international.commons.CompareUtils;
import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.snowowl.core.branch.Branch;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

/**
 * @since 8.0
 */
public final class ResourceURI implements Serializable, Comparable<ResourceURI> {

	private static final long serialVersionUID = 1L;

	@JsonIgnore
	private static final Pattern URI_PATTERN = Pattern.compile("^([^\\/]+)[\\/]{1}([^\\/@]+)(([\\/]{1}[^\\/\\s@]+)*)(@[^\\s]+)?$");
	
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
	public static final String NEXT = "NEXT";

	/**
	 * Special resource URI character that denotes an altered version of a resource, be it a version, a special in progress content branch that needs to be addressed or any other special path.
	 */
	public static final String TILDE = "~";
	
	// the original value
	private final String uri;
	
	// the type of the resource, concrete class of Resource
	private final String resourceType;
	
	// the actual ID of the resource, see Resource.id
	private final String resourceId;

	// relative location or version of the resource
	private final String path;
	
	// timestamp part as String, for point-in-time resource URIs
	private final String timestampPart;
	
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
		this.uri = uri.replaceFirst("/HEAD", "").replaceFirst("~HEAD", ""); 
		this.resourceType = matcher.group(1);
		this.resourceId = matcher.group(2);
		
		
		if (hasSpecialResourceIdPart()) {
			if (!CompareUtils.isEmpty(matcher.group(3))) {
				throw new BadRequestException("Resource URIs cannot use both the special tilde ('~') character and a branch path. Got: %s", uri)
					.withDeveloperMessage("For child paths use either the '~' or the '/path' alternatives. For nested deeper branch paths always use the forward slash separator.");
			}
			this.path = null; // when using special ID part paths cannot get any value
		} else {
			// remove leading slash from match
			this.path = CompareUtils.isEmpty(matcher.group(3)) ? HEAD : matcher.group(3).substring(1);
		}
		
		
		try {

			if (CompareUtils.isEmpty(matcher.group(5))) {
				this.timestampPart = "";
			} else {
				// test that a valid numeric value was given after the 'at' symbol
				Long.parseLong(matcher.group(5).substring(1));
				this.timestampPart = matcher.group(5);
			}
			
		} catch (NumberFormatException e) {
			throw new BadRequestException("Malformed Resource URI value: timestamp part of '%s' must be in format '@<numeric value>", uri);
		}
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
	
	public String getTimestampPart() {
		return timestampPart;
	}
	
	public boolean isLatest() {
		return hasPath(LATEST) || LATEST.equals(getSpecialIdPart());
	}
	
	public boolean isHead() {
		return hasPath(HEAD) || HEAD.equals(getSpecialIdPart());
	}
	
	public boolean isNext() {
		return hasPath(NEXT) || NEXT.equals(getSpecialIdPart());
	}
	
	public boolean hasPath(String path) {
		Preconditions.checkArgument(!Strings.isNullOrEmpty(path), "Path must not be empty or null");
		return path.equals(getPath());
	}
	
	public ResourceURI withPath(String path) {
		if (Objects.equals(getPath(), path)) {
			return this;
		}
		if (Strings.isNullOrEmpty(path)) {
			return ResourceURI.of(resourceType, resourceId.concat(Strings.nullToEmpty(getTimestampPart())));
		} else {
			return ResourceURI.branch(resourceType, resourceId, path.concat(Strings.nullToEmpty(getTimestampPart())));
		}
	}

	@JsonIgnore
	public ResourceURI withTimestampPart(String timestampPart) {
		if (Objects.equals(getTimestampPart(), timestampPart)) {
			return this;
		}
		// depending on whether we have a path segment or not append the new timestamp part
		if (path == null) {
			return ResourceURI.of(resourceType, resourceId.concat(timestampPart));
		} else {
			return ResourceURI.branch(resourceType, resourceId, Strings.nullToEmpty(path).concat(timestampPart));
		}
	}
	
	@JsonIgnore
	public ResourceURIWithQuery withQueryPart(String query) {
		return ResourceURIWithQuery.of(resourceType, Branch.BRANCH_PATH_JOINER.join(resourceId, path), query);
	}

	@JsonIgnore
	public ResourceURI withoutPath() {
		return new ResourceURI(Branch.BRANCH_PATH_JOINER.join(resourceType, resourceId));
	}
	
	@JsonIgnore
	public String withoutResourceType() {
		return isHead() ? resourceId : Branch.BRANCH_PATH_JOINER.join(resourceId, path);
	}
	
	// SPECIAL TILDE ID handling
	
	public String getSpecialIdPart() {
		return hasSpecialResourceIdPart() ? extractSpecialResourceIdPart(resourceId) : null;
	}
	
	@JsonIgnore
	public boolean hasSpecialResourceIdPart() {
		return this.resourceId.contains(TILDE);
	}
	
	@JsonIgnore
	public ResourceURI withoutSpecialResourceIdPart() {
		final String resourceId = getResourceId();
		final int separatorIdx = resourceId.lastIndexOf(TILDE);
		
		if (separatorIdx > 0) {
			return ResourceURI.of(getResourceType(), resourceId.substring(0, separatorIdx).concat(Strings.nullToEmpty(getTimestampPart())));
		} else {
			return this;
		}
	}
	
	public ResourceURI withSpecialResourceIdPart(String specialResourceIdPart) {
		return specialResourceIdPart == null ? this : new ResourceURI(String.join(Branch.SEPARATOR, resourceType, String.join(TILDE, withoutSpecialResourceIdPart(this.resourceId), specialResourceIdPart)));
	}
	
	@JsonIgnore
	public ResourceURI toRegularURI() {
		if (hasSpecialResourceIdPart()) {
			return withoutSpecialResourceIdPart().withPath(getSpecialIdPart());
		} else {
			return this;
		}
	}

	@JsonIgnore
	public ResourceURI asLatest() {
		return ResourceURI.latest(resourceType, resourceId);
	}
	
	@JsonIgnore
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
		return new ResourceURI(Branch.BRANCH_PATH_JOINER.join(resourceType, resourceId, path));
	}

	public static ResourceURI latest(String resourceType, String resourceId) {
		Preconditions.checkArgument(!resourceId.contains(Branch.SEPARATOR), "Resource ID should not be an URI already. Got: %s", resourceId);
		return new ResourceURI(Branch.BRANCH_PATH_JOINER.join(resourceType, resourceId, LATEST));
	}
	
	public static ResourceURI next(String resourceType, String resourceId) {
		Preconditions.checkArgument(!resourceId.contains(Branch.SEPARATOR), "Resource ID should not be an URI already. Got: %s", resourceId);
		return new ResourceURI(Branch.BRANCH_PATH_JOINER.join(resourceType, resourceId, NEXT));
	}

	public static ResourceURI of(String resourceType, String resourceId) {
		checkNotNull(resourceType, "'resourceType' must be specified");
		checkNotNull(resourceId, "'resourceId' must be specified");
		return new ResourceURI(Branch.BRANCH_PATH_JOINER.join(resourceType, resourceId));
	}

	public ResourceURIWithQuery withQuery(String query) {
		return new ResourceURIWithQuery(this, query);
	}
	
	public static String withoutSpecialResourceIdPart(String resourceIdWithPotentialTildePath) {
		return resourceIdWithPotentialTildePath.split(TILDE)[0];
	}
	
	public static String extractSpecialResourceIdPart(String resourceIdWithTilde) {
		Preconditions.checkArgument(isSpecialResourceId(resourceIdWithTilde), "Argument '%s' does not look like a resource ID with the special tilde ('~') character.", resourceIdWithTilde);
		return resourceIdWithTilde.split(TILDE)[1];
	}
	
	public static boolean isSpecialResourceId(String resourceIdWithTilde) {
		return resourceIdWithTilde != null && resourceIdWithTilde.contains(TILDE);
	}

}
