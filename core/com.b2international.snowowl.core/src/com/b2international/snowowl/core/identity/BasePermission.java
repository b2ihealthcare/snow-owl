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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Objects;

import com.google.common.base.MoreObjects;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;

/**
 * @since 8.0
 */
public abstract class BasePermission implements Permission {

	private static final long serialVersionUID = 1L;
	
	private final String operation;
	
	private final Supplier<String> permission = Suppliers.memoize(() -> {
		return String.join(SEPARATOR, getOperation(), getResource());
	});
	
	public BasePermission(String operation) {
		this.operation = checkNotNull(operation, "Operation must be specified.");
	}
	
	@Override
	public final String getOperation() {
		return operation;
	}
	
	@Override
	public final String getPermission() {
		return permission.get();
	}
	
	@Override
	public final boolean implies(final Permission permissionToAuthenticate) {
		// operation * allows all incoming permission requirements (both operation and resource)
		// if not *, then the operation in this permission should match the same operation from the requirement (equals)
		final boolean allowedOperation = ALL.equals(getOperation()) || getOperation().equals(permissionToAuthenticate.getOperation());
		if (!allowedOperation) {
			return false;
		}
		return doImplies(permissionToAuthenticate);
	}
	
	protected abstract boolean doImplies(Permission permissionToAuthenticate);

	@Override
	public final int hashCode() {
		return Objects.hash(getPermission());
	}

	@Override
	public final boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		Permission other = (Permission) obj;
		return Objects.equals(getPermission(), other.getPermission());
	}

	@Override
	public final String toString() {
		return MoreObjects.toStringHelper(Permission.class).add("permission", getPermission()).toString();
	}

}
