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
package com.b2international.snowowl.core.identity;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.Serializable;
import java.util.List;

import com.b2international.commons.CompareUtils;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * Represents an application specific permission.
 * Permissions have two parts: operation and resource
 * Resources can be expressed using file-style wildcards such as '*' and '?'
 * 
 * @since 8.0
 */
public interface Permission extends Serializable {

	public static final String SEPARATOR = ":";
	public static final String RESOURCE_SEPARATOR = "/";
	
	public static final String ALL = "*"; //$NON-NLS-N$
	public static final String OPERATION_BROWSE = "browse";  //$NON-NLS-N$
	public static final String OPERATION_EDIT = "edit";  //$NON-NLS-N$
	public static final String OPERATION_IMPORT = "import";  //$NON-NLS-N$
	public static final String OPERATION_EXPORT = "export";  //$NON-NLS-N$
	public static final String OPERATION_VERSION = "version";  //$NON-NLS-N$
	public static final String OPERATION_PROMOTE = "promote";  //$NON-NLS-N$
	public static final String OPERATION_CLASSIFY = "classify";  //$NON-NLS-N$
	
//	public static Permission toAll(String...resources) {
//		return new RequireAllPermission(OPERATION_ALL, asResource(resources));
//	}
//
//	public static Permission toImport(String...resources) {
//		return new RequireAllPermission(OPERATION_IMPORT, asResource(resources));
//	}
//	
//	public static Permission toExport(String...resources) {
//		return new RequireAllPermission(OPERATION_EXPORT, asResource(resources));
//	}
//	
//	public static Permission toBrowse(String...resources) {
//		return new RequireAllPermission(OPERATION_BROWSE, asResource(resources));
//	}
//	
//	public static Permission toEdit(String...resources) {
//		return new RequireAllPermission(OPERATION_EDIT, asResource(resources));
//	}
//	
//	public static Permission toVersion(String...resources) {
//		return new RequireAllPermission(OPERATION_VERSION, asResource(resources));
//	}
//	
//	public static Permission toPromote(String...resources) {
//		return new RequireAllPermission(OPERATION_PROMOTE, asResource(resources));
//	}
//	
//	public static Permission toClassify(String...resources) {
//		return new RequireAllPermission(OPERATION_CLASSIFY, asResource(resources));
//	}
	
	public static Permission requireAll(String operation, String...resources) {
		return requireAll(operation, List.of(resources));
	}
	
	public static Permission requireAll(String operation, Iterable<String> resources) {
		return new RequireAllPermission(operation, resources);
	}
	
	public static Permission requireAny(String operation, String...resources) {
		return requireAny(operation, List.of(resources));
	}
	
	public static Permission requireAny(String operation, Iterable<String> resources) {
		return new RequireAnyPermission(operation, resources);
	}
	
	public static String asResource(String...resources) {
		return String.join(RESOURCE_SEPARATOR, resources);
	}
	
	public static String asResource(Iterable<String> resources) {
		return String.join(RESOURCE_SEPARATOR, resources);
	}

	/**
	 * @return the operation part from the permission string value.
	 */
	@JsonIgnore
	String getOperation();

	/**
	 * @return the resource part from the permission string value.
	 */
	@JsonIgnore
	String getResource();

	/**
	 * @return the actual permission string value that represents this {@link Permission} object.
	 */
	String getPermission();

	/**
	 * @param permissionToAuthenticate
	 * @return <code>true</code> if this permission implies the given permission, meaning it satisfies at as a requirement and basically represent the same access rules
	 */
	boolean implies(Permission permissionToAuthenticate);
	
//	/**
//	 * Convert the {@link String} representation of a permission into a {@link Permission} object.
//	 * The input string must be in the form of "&lt;operation&gt;:&lt;resource&gt;"
//	 * 
//	 * @param permission as String
//	 * @return a {@link Permission} with the appropriate operation and resources set
//	*/
//	@JsonCreator
//	public static final Permission valueOf(@JsonProperty("permission") final String permission) {
//		checkArgument(!CompareUtils.isEmpty(permission), "Permission argument is required");
//		final String[] parts = permission.split(SEPARATOR);
//		checkArgument(parts.length == 2, "A permission should consist of two String values separated by a ':' character. Got: %s", permission);
//		final String operation = parts[0];
//		final String resourceReference = parts[1];
//		return new RequireAllPermission(operation, resourceReference);
//	}
	
}
