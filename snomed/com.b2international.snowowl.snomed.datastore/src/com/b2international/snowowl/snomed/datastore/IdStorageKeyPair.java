/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore;

import com.b2international.commons.Pair;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;

/**
 * Class for wrapping a terminology specific ID and a primary unique key.
 * <p><b>NOTE:&nbsp;</b>{@code equals} and {@code hashCode} is based on the terminology specific ID.<br>
 * Primary key can be {@code -1L} if it is temporary. 
 * @see Pair
 */
public final class IdStorageKeyPair extends Pair<String, Long> {
	
	public IdStorageKeyPair(final String componentId, final Long storageKey) {
		super(
			Preconditions.checkNotNull(componentId, "Component ID argument cannot be null."), 
			Preconditions.checkNotNull(storageKey, "Storage key argument cannot be null."));
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getA() == null) ? 0 : getA().hashCode());
		return result;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final IdStorageKeyPair other = (IdStorageKeyPair) obj;
		if (getA() == null) {
			if (other.getA() != null)
				return false;
		} else if (!getA().equals(other.getA()))
			return false;
		return true;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).add("ID", getA()).add("CDO ID", getB()).toString();
	}
	
}