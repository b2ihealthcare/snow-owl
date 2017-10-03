/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

import com.google.common.collect.ImmutableSet;


/**
 * Represent a custom role Users assigned to.
 */
public final class Role implements Serializable {
	
	public static final Role UNSPECIFIED = new Role("Unspecified", Collections.emptySet());
	public static final Role ADMINISTRATOR = new Role("Administrator", ImmutableSet.of(
		new Permission(PermissionIdConstant.BROWSE),
		new Permission(PermissionIdConstant.EDIT),
		new Permission(PermissionIdConstant.EXPORT),
		new Permission(PermissionIdConstant.IMPORT),
		new Permission(PermissionIdConstant.VERSION),
		new Permission(PermissionIdConstant.PROMOTE)
	));
	
	private static final long serialVersionUID = 1601508745318826995L;

	private final String name;
	
	private final Collection<Permission> permissions;
	
	public Role(String name, Collection<Permission> permissions) {
		this.name = name;
		this.permissions = permissions;
	}
	
	public String getName() {
		return name;
	}
	
	public Collection<Permission> getPermissions() {
		return permissions;
	}
	
	@Override
	public String toString() {
		return name;
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		Role other = (Role) obj;
		return Objects.equals(name, other.name);
	}
	
}