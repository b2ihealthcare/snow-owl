/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.api;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;

import com.b2international.snowowl.core.ComponentTypeNameCache;

/**
 * A terminology specific ID and an application specific terminology component type ID for uniquely
 * identifying a component in the application.  
 *
 */
public class IdAndTerminologyComponentIdProviderImpl implements IdAndTerminologyComponentIdProvider, Serializable {

	private static final long serialVersionUID = -6588854279025587211L;

	private final String id;
	private final short terminologyComponentId;

	/**
	 * Creates a new {@link IdAndTerminologyComponentIdProvider} instance with the given component ID and the
	 * terminology component ID arguments.
	 * @param id the component ID.
	 * @param terminologyComponentId application specific terminology component ID.
	 * @return the new {@link IdAndTerminologyComponentIdProvider} instance.
	 */
	public static IdAndTerminologyComponentIdProvider create(final String id, final short terminologyComponentId) {
		return new IdAndTerminologyComponentIdProviderImpl(checkNotNull(id, "id"), terminologyComponentId);
	}
	
	public IdAndTerminologyComponentIdProviderImpl(final String id, final short terminologyComponentId) {
		this.id = checkNotNull(id, "id");
		this.terminologyComponentId = terminologyComponentId;
	}
	
	@Override
	public String getId() {
		return id;
	}

	@Override
	public short getTerminologyComponentId() {
		return terminologyComponentId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + terminologyComponentId;
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof IdAndTerminologyComponentIdProviderImpl))
			return false;
		final IdAndTerminologyComponentIdProviderImpl other = (IdAndTerminologyComponentIdProviderImpl) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (terminologyComponentId != other.terminologyComponentId)
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append(ComponentTypeNameCache.INSTANCE.getComponentName(this));
		sb.append(" ID: ");
		sb.append(id);
		return sb.toString();
	}

}