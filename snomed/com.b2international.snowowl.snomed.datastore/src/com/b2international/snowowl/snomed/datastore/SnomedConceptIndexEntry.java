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
package com.b2international.snowowl.snomed.datastore;

import java.io.Serializable;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.b2international.snowowl.core.api.IComponent;
import com.b2international.snowowl.core.api.NullComponent;
import com.b2international.snowowl.core.api.index.IIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.SnomedIndexEntry;

/**
 * Represents a SNOMED&nbsp;CT concept.
 *
 */
@Immutable
public class SnomedConceptIndexEntry extends SnomedIndexEntry implements IComponent<String>, IIndexEntry, Serializable {
	
	private static final long serialVersionUID = -824286402410205210L;

	/**
	 * Returns {@code true} if and if only the specified SNOMED&nbsp;CT {@link SnomedConceptIndexEntry concept} is either {@code null}
	 *  or {@code equals} with the {@link NullComponent#getNullImplementation() null implementation}, otherwise returns {@code false}.
	 * @param concept the concept to check.
	 * @return {@code true} if the specified concept is unset. Otherwise returns with {@code false}.
	 */
	public static boolean isUnset(@Nullable final SnomedConceptIndexEntry concept) {
		return null == concept ? true : NullComponent.getNullImplementation().equals(concept);
	}

	/**
	 * Generates a flag indicating the following concept properties: status,
	 * definition status, subclass enumeration status.
	 * 
	 * @param active
	 *            indicates the concept status.
	 * 
	 * @param primitive
	 *            indicates that the concept is not fully defined.
	 * 
	 * @param exhaustive
	 *            indicates that the concept's direct subclasses are given in
	 *            full, no other category exists, and the subclasses are
	 *            mutually disjoint.
	 * 
	 * @param released flag indicating whether a SNOMED&nbsp;CT concept is published or not.
	 * @return a byte value representing all specified flags
	 */
	public static byte generateFlags(final boolean active, final boolean primitive, final boolean exhaustive, final boolean released) {
		byte flags = 0;
		if (active) flags |= FLAG_IS_ACTIVE;
		if (primitive) flags |= FLAG_IS_PRIMITIVE;
		if (exhaustive) flags |= FLAG_IS_EXHAUSTIVE;
		if (released) flags |= FLAG_IS_RELEASED;
		return flags;
	}
	
	/**
	 * Checks if any of the flags in the specified mask is set. This method has an <tt>int</tt> parameter so flags can be 
	 * bitwise OR-ed together inline without requiring a cast or a local assignment.
	 * 
	 * @param mask the mask to use for flag checking
	 * @return <code>true</code> if the specified flag is set, <code>false</code> otherwise
	 */
	private static boolean isAnyFlagSet(final byte flags, final int mask) {
		return (flags & mask) != 0;
	}

	/**
	 * Indicates that the concept definition is active.
	 */
	public static final byte FLAG_IS_ACTIVE = 1 << 0;
	
	/**
	 * Indicates that the concept's definition has not been fully given.
	 */
	public static final byte FLAG_IS_PRIMITIVE = 1 << 1;
	
	/**
	 * Indicates that direct subclasses from a partition over the set of elements represented by this concept. 
	 */
	public static final byte FLAG_IS_EXHAUSTIVE = 1 << 2;
	
	/**
	 * Indicates that the concept has been published or not. 
	 */
	public static final byte FLAG_IS_RELEASED = 1 << 3;
	
	private byte flags;


	/**
	 * Creates a new ConceptMini instance with the specified parameters.
	 * 
	 * @param conceptId external concept identifier (SNOMED CT ID for example)
	 * @param moduleId the concept's module identifier
	 * @param label the concept's human readable name
	 * @param iconId icon ID of the concept.
	 * @param storageKey internal primary key (CDO key for example)
	 * @param flags concept flags
	 */
	public SnomedConceptIndexEntry(final String conceptId, final String moduleId, final String label, String iconId, final long storageKey, final byte flags, final long effectiveTime) {
		super(conceptId, label, iconId, moduleId, /*storage key*/0f, storageKey, isAnyFlagSet(flags, FLAG_IS_RELEASED), isAnyFlagSet(flags, FLAG_IS_ACTIVE), effectiveTime);
		this.flags = flags;
	}
	
	/**
	 * Creates a SNOMED&nbsp;CT concept.
	 * @param conceptId the concept ID.
	 * @param moduleId ID of the module concept.
	 * @param label preferred term of the concept.
	 * @param iconId icon concept ID.
	 * @param score score from the index.
	 * @param storageKey the unique storage key of the concept. (CDO ID).
	 * @param flags storing additional information for the concept.
	 */
	public SnomedConceptIndexEntry(final String conceptId, final String moduleId, final String label, String iconId, final float score, final long storageKey, final byte flags, final long effectiveTime) {
		super(conceptId, label, iconId, moduleId, score, storageKey, isAnyFlagSet(flags, FLAG_IS_RELEASED), isAnyFlagSet(flags, FLAG_IS_ACTIVE), effectiveTime);
		this.flags = flags;
	}

	/**
	 * Returns {@code true} if the SNOMED&nbsp;CT concept is primitive, otherwise returns {@code false}.
	 * @return {@code true} if primitive, {@code false} if the concept is fully defined.
	 */
	public boolean isPrimitive() {
		return isAnyFlagSet(FLAG_IS_PRIMITIVE);
	}
	
	/**
	 * Returns {@code true} if the SNOMED&nbsp;CT concept is exhaustive, otherwise returns {@code false}.
	 * @return {@code true} if exhaustive, otherwise {@code false}.
	 */
	public boolean isExhaustive() {
		return isAnyFlagSet(FLAG_IS_EXHAUSTIVE);
	}

	@Override
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
		final SnomedConceptIndexEntry other = (SnomedConceptIndexEntry) obj;
		if (getId() == null) {
			if (other.getId() != null)
				return false;
		} else if (!getId().equals(other.getId()))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return getId() + " - " + getLabel() + " - " + this.isActive();
	}

	/**
	 * Checks if any of the flags in the specified mask is set. This method has an <tt>int</tt> parameter so flags can be 
	 * bitwise OR-ed together inline without requiring a cast or a local assignment.
	 * 
	 * @param mask the mask to use for flag checking
	 * @return <code>true</code> if the specified flag is set, <code>false</code> otherwise
	 */
	private boolean isAnyFlagSet(final int flag) {
		return (flags & flag) != 0;
	}

}