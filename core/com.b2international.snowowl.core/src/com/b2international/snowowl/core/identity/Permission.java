/*
 * Copyright 2011-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.identity;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.Objects;

import org.apache.commons.io.FilenameUtils;

import com.b2international.commons.CompareUtils;
import com.b2international.commons.StringUtils;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

/**
 * Represents an application specific permission.
 * Permissions have two parts: operation and resource
 * Resources can be expressed using file-style wildcards such as '*' and '?'
 */
public final class Permission implements Serializable {

	private static final long serialVersionUID = 8938726616446337680L;
	
	private static final String SEPARATOR = ":";
	private static final String RESOURCE_SEPARATOR = "/";
	
	private final String operation;
	private final String resource;
	
	private final String permission;
	
	public static final String OPERATION_ALL = "*"; //$NON-NLS-N$
	public static final String OPERATION_BROWSE = "browse";  //$NON-NLS-N$
	public static final String OPERATION_EDIT = "edit";  //$NON-NLS-N$
	public static final String OPERATION_IMPORT = "import";  //$NON-NLS-N$
	public static final String OPERATION_EXPORT = "export";  //$NON-NLS-N$
	public static final String OPERATION_VERSION = "version";  //$NON-NLS-N$
	public static final String OPERATION_PROMOTE = "promote";  //$NON-NLS-N$
	public static final String OPERATION_CLASSIFY = "classify";  //$NON-NLS-N$
	
	public static Permission toAll(String...resources) {
		return new Permission(OPERATION_ALL, asResource(resources));
	}

	public static Permission toImport(String...resources) {
		return new Permission(OPERATION_IMPORT, asResource(resources));
	}
	
	public static Permission toExport(String...resources) {
		return new Permission(OPERATION_EXPORT, asResource(resources));
	}
	
	public static Permission toBrowse(String...resources) {
		return new Permission(OPERATION_BROWSE, asResource(resources));
	}
	
	public static Permission toEdit(String...resources) {
		return new Permission(OPERATION_EDIT, asResource(resources));
	}
	
	public static Permission toVersion(String...resources) {
		return new Permission(OPERATION_VERSION, asResource(resources));
	}
	
	public static Permission toPromote(String...resources) {
		return new Permission(OPERATION_PROMOTE, asResource(resources));
	}
	
	public static Permission toClassify(String...resources) {
		return new Permission(OPERATION_CLASSIFY, asResource(resources));
	}
	
	public static Permission of(String operation, String...resources) {
		return new Permission(operation, asResource(resources));
	}
	
	/**
	 * Convert the {@link String} representation of a permission into a {@link Permission} object.
	 * The input string must be in the form of "&lt;operation&gt;:&lt;resource&gt;"
	 * 
	 * @param permission as String
	 * @return a {@link Permission} with the appropriate operation and resources set
	*/
	@JsonCreator
	public static final Permission valueOf(@JsonProperty("permission") final String permission) {
		checkArgument(!CompareUtils.isEmpty(permission), "Permission argument is required");
		final String[] parts = permission.split(SEPARATOR);
		checkArgument(parts.length == 2, "A permission should consist of two String values separated by a ':' character. Got: %s", permission);
		final String operation = parts[0];
		final String resourceReference = parts[1];
		return new Permission(operation, resourceReference);
	}

	private Permission(final String operation, final String resource) {
		this.operation = checkNotNull(operation, "Operation must be specified.");
		
		if (StringUtils.isEmpty(resource)) {
			throw new IllegalArgumentException("Resource must be specified.");
		}
		this.resource = resource;
		this.permission = String.join(SEPARATOR, operation, resource);
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
	
	public boolean implies(final Permission permissionToAuthenticate) {
		
		// operation
		
		// * allows all incoming permission requirements (both operation and resource)
		// if not *, then the operation in this permission should match the same operation from the requirement (equals)
		final boolean allowedOperation = OPERATION_ALL.equals(operation) || operation.equals(permissionToAuthenticate.getOperation());
		if (!allowedOperation) {
			return false;
		}
		return FilenameUtils.wildcardMatch(permissionToAuthenticate.getResource(), resource);
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
		return MoreObjects.toStringHelper(Permission.class).add("permission", getPermission()).toString();
	}
	
	public static String asResource(String...resources) {
		return String.join(RESOURCE_SEPARATOR, resources);
	}

}