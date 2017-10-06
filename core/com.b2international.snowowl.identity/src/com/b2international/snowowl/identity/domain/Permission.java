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

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.Objects;

import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;

/**
 * Represents an application specific permission.
 */
public final class Permission implements Serializable {

	private static final long serialVersionUID = 4297677944468219977L;

	private final String id;
	private final String name;

	public Permission(final String permissionId) {
		this(permissionId, "");
	}
	
	/**
	 * @param permissionId
	 *            application specific permission id.
	 * @param name
	 *            A human readable representation of the application specific
	 *            permission.
	 */
	public Permission(final String permissionId, final String name) {
		this.id = checkNotNull(permissionId, "permissionId");
		this.name = Strings.nullToEmpty(name);
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		Permission other = (Permission) obj;
		return Objects.equals(id, other.id);
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(Permission.class).add("id", id).add("name", name).toString();
	}
}