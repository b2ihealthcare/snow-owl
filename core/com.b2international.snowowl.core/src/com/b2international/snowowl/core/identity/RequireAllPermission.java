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

import java.util.List;

import org.apache.commons.io.FilenameUtils;

import com.b2international.commons.CompareUtils;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

/**
 * @since 8.0
 */
final class RequireAllPermission extends BasePermission {

	private static final long serialVersionUID = 1L;

	private final List<String> resources;
	
	/*package*/ RequireAllPermission(final String operation, final Iterable<String> resources) {
		super(operation);
		checkArgument(!CompareUtils.isEmpty(resources), "At least one resource descriptor is required.");
		for (String resource : resources) {
			checkArgument(!CompareUtils.isEmpty(resource), "Resource descriptor cannot be null or empty.");
		}
		this.resources = ImmutableList.copyOf(resources);
	}
	
	@Override
	public String getResource() {
		return resources.size() == 1 ? Iterables.getFirst(resources, null) : String.format("allOf(%s)", String.join(",", resources));
	}
	
	@Override
	public List<String> getResources() {
		return resources;
	}
	
	@Override
	protected boolean doImplies(Permission permissionToAuthenticate) {
		return getResources().stream().allMatch(resource -> FilenameUtils.wildcardMatch(permissionToAuthenticate.getResource(), resource));
	}
	
	static boolean isRequireAllResource(String resourceReference) {
		return Strings.nullToEmpty(resourceReference).startsWith("allOf(");
	}
	
}