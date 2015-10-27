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
package com.b2international.snowowl.snomed.datastore.index.refset;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;

import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

import com.b2international.snowowl.core.api.IComponent;
import com.b2international.snowowl.snomed.datastore.IRefSetComponent;
import com.b2international.snowowl.snomed.datastore.index.SnomedIndexEntry;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.google.common.base.Objects;

/**
 * Representation of a SNOMED CT reference set.
 */
@ThreadSafe
@Immutable
public class SnomedRefSetIndexEntry extends SnomedIndexEntry implements IRefSetComponent, IComponent<String>, Serializable {

	private static final long serialVersionUID = 2943070736359287904L;

	private final SnomedRefSetType type;
	private final short referencedComponentType;
	private final boolean structural;

	/**
	 * Creates a new instance of this class.
	 * @param id the unique ID of the identifier concept.
	 * @param label the label of the associated concept.
	 * @param iconId TODO
	 * @param score the score for the index.
	 * @param storageKey unique identifier of the component in the database.
	 * @param type the type of the reference set.
	 * @param referencedComponentType the numeric ID of the referenced component's type
	 */
	public SnomedRefSetIndexEntry(final String id, final String label, String iconId, final String moduleId, final float score, 
			final long storageKey, final SnomedRefSetType type, final short referencedComponentType, final boolean structural) {
		
		super(id, label, iconId, moduleId, score, storageKey, false, false, -1L); //TODO consider associating boolean flags with reference set identifier concept
		this.type = checkNotNull(type, "type");
		this.referencedComponentType = referencedComponentType;
		this.structural = structural;
	}

	/**
	 * Returns with the {@link SnomedRefSetType type} of the reference set.
	 * @return the reference set type.
	 */
	public SnomedRefSetType getType() {
		return type;
	}
	
	/**
	 * <b>NOTE:</b> always throws {@link UnsupportedOperationException}.
	 */
	/* (non-Javadoc)
	 * @see com.b2international.snowowl.snomed.datastore.index.SnomedIndexEntry#isActive()
	 */
	@Override
	public boolean isActive() {
		throw new UnsupportedOperationException("SNOMED CT reference set does not have active property. Operation is unsupported.");
	}
	
	/**
	 * <b>NOTE:</b> always throws {@link UnsupportedOperationException}.
	 */
	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.snomed.datastore.index.SnomedIndexEntry#isReleased()
	 */
	@Override
	public boolean isReleased() {
		throw new UnsupportedOperationException("SNOMED CT reference set cannot be published. Operation is unsupported.");
	}
	
	/**
	 * (non-API)
	 * Returns with the application specific component identifier as a numeric value of the referenced component.
	 * @return the application specific terminology component identifier value for the referenced component.
	 */
	public short getReferencedComponentType() {
		return referencedComponentType;
	}
	
	/**
	 * @return {@code true} if reference set members need to be gathered from lists on the referenced components they
	 * refer to, {@code false} if they are retrievable from the reference set itself (a single list)
	 */
	public boolean isStructural() {
		return structural;
	}
	
	@Override
	public String toString() {
		
		return Objects.toStringHelper(this)
				.add("id", id)
				.add("label", label)
				.add("score", score)
				.add("storageKey", storageKey)
				.add("type", type)
				.add("referencedComponentType", referencedComponentType)
				.add("structural", structural)
				.toString();
	}
	
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((getId() == null) ? 0 : getId().hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final SnomedRefSetIndexEntry other = (SnomedRefSetIndexEntry) obj;
		if (getId() == null) {
			if (other.getId() != null)
				return false;
		} else if (!getId().equals(other.getId()))
			return false;
		return true;
	}
}