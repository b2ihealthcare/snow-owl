/*
 * Copyright 2011-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.identity.domain;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.Objects;

import com.b2international.commons.CompareUtils;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

/**
 * Represents an application specific permission.
 */
public final class Permission implements Serializable {

	private static final long serialVersionUID = 8938726616446337680L;
	
	public static final String SEPARATOR = ":";
	
	public static final String ALL = "*";
	public static final String BROWSE = "browse";
	public static final String EDIT = "edit";
	public static final String IMPORT = "import";
	public static final String EXPORT = "export";
	public static final String VERSION = "version";
	public static final String PROMOTE = "promote";
	public static final String CLASSIFY = "classify";
	
	private final String operation;
	private final String resource;
	private final String name;
	private final String permission;
	private final boolean wildcard;
	private final String rawResource;
	
	public Permission(final String operation, final String resource) {
		this(operation, resource, "");
	}

	public Permission(final String operation, final String resource, final String name) {
		this.operation = checkNotNull(operation, "Operation must be specified");
		this.resource = checkNotNull(resource, "Resource must be specified");
		final int wildcardPosition = this.resource.indexOf(ALL);
		checkArgument(wildcardPosition == -1 /*no wildcard*/ || wildcardPosition == resource.length() - 1 /*at the end*/, "Wildcard character must be at the end of the resource. Got: %s", resource);
		this.wildcard = wildcardPosition != -1;
		this.rawResource = wildcard ? resource.substring(0, wildcardPosition) : resource;
		this.permission = String.join(SEPARATOR, operation, resource);
		this.name = name;
	}

	/**
	 * @return the operation part from the permission string value.
	 */
	@JsonIgnore
	public String getOperation() {
		return operation;
	}
	
	/**
	 * @return the resource part from the permission string value.
	 */
	@JsonIgnore
	public String getResource() {
		return resource;
	}
	
	/**
	 * @return the actual permission string value that represents this {@link Permission} object.
	 */
	public String getPermission() {
		return permission;
	}
	
	/**
	 * @return a descriptive name of this permission
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Returns <code>true</code> if this {@link Permission} implies the incoming permission requirement and <code>false</code> if it does not.  
	 * 
	 * @param permissionRequirement
	 * @return
	 */
	public boolean implies(Permission permissionRequirement) {
		checkArgument(!ALL.equals(permissionRequirement.getOperation()), "Explicit operation is required to check whether this permission '%s' implies '%s'.", this, permissionRequirement);
		checkArgument(!ALL.equals(permissionRequirement.getResource()), "Explicit resource is required to check whether this permission '%s' implies '%s'.", this, permissionRequirement);
		
		// rules
		// * allows all incoming permission requirements (both operation and resource)
		// if not *, then the operation in this permission should match the same operation from the requirement (equals)
		final boolean allowedOperation = ALL.equals(operation) || operation.equals(permissionRequirement.getOperation());
		if (!allowedOperation) {
			return false;
		}
		
		// if operation matches, then proceed based on whether the resource part has a wildcard or not
		if (wildcard) {
			// in case of a wildcard, this permission's resource part should match the beginning of the requirement's resource part
			return permissionRequirement.getResource().startsWith(rawResource);
		} else {
			// otherwise permission resource parts should match
			return permissionRequirement.getResource().equals(resource);
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(getPermission());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		Permission other = (Permission) obj;
		return Objects.equals(getPermission(), other.getPermission());
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(Permission.class).add("permission", getPermission()).add("name", getName()).toString();
	}
	
	public static final Permission valueOf(final String permission) {
		return valueOf(permission, "N/A");
	}
	
	@JsonCreator
	public static final Permission valueOf(
			@JsonProperty("permission") final String permission, 
			@JsonProperty("name") final String name) {
		checkArgument(!CompareUtils.isEmpty(permission), "Permission argument is required");
		final String[] parts = permission.split(SEPARATOR);
		checkArgument(parts.length == 2, "A permission should consist of two String values separated by a ':' character.");
		final String operation = parts[0];
		final String resourceReference = parts[1];
		return new Permission(operation, resourceReference, name);
	}
	
}