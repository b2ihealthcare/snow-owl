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
import java.text.MessageFormat;

import javax.annotation.concurrent.Immutable;

import com.b2international.snowowl.core.api.IComponent;
import com.b2international.snowowl.core.api.IStatement;
import com.b2international.snowowl.core.api.index.IIndexEntry;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.datastore.cdo.CDOIDUtils;
import com.b2international.snowowl.snomed.Relationship;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.datastore.index.SnomedIndexEntry;
import com.google.common.base.Preconditions;

/**
 * Represents a SNOMED&nbsp;CT relationship.
 * 
 */
@Immutable
public class SnomedRelationshipIndexEntry extends SnomedIndexEntry implements IStatement<String>, IComponent<String>, IIndexEntry, Serializable {
	
	private static final long serialVersionUID = -7873086925532169024L;
	
	
	/**
	 * (non-API)
	 * 
	 * Generates a flag indicating the following relationship properties: status, inferred by a reasoner and universal restriction.
	 * @param released indicates the relationship's publication status.
	 * @param active indicates the relationship status.
	 * @param inferred indicates that the relationship was inferred by a reasoner.
	 * @param universal indicates that the relationship represents a universal restriction.
	 * @param destinationNegated indicates that the relationships's destination concept should be taken in its negated form.
	 * @return the generated relationship flag. 
	 */
	public static byte generateFlags(final boolean released, final boolean active, final boolean inferred, final boolean universal, final boolean destinationNegated) {
		byte flags = 0;
		if (released) flags |= FLAG_IS_RELEASED;
		if (active) flags |= FLAG_IS_ACTIVE;
		if (inferred) flags |= FLAG_IS_INFERRED;
		if (universal) flags |= FLAG_IS_UNIVERSAL;
		if (destinationNegated) flags |= FLAG_IS_DESTINATION_NEGATED;
		return flags;
	}

	/**
	 * Indicates whether the SNOMED&nbsp;CT relationship is published or not.
	 */
	public static final byte FLAG_IS_RELEASED = 1 << 2;
	
	/**
	 * Indicates that the destination concept of this relationship should be taken in its negated form. 
	 */
	public static final byte FLAG_IS_DESTINATION_NEGATED = 1 << 3;
	
	/**
	 * Indicates that the relationship is active.
	 */
	public static final byte FLAG_IS_ACTIVE = 1 << 4;
	
	/**
	 * Indicates that the relationship was inferred by a reasoner.
	 */
	public static final byte FLAG_IS_INFERRED = 1 << 5;
	
	/**
	 * Indicates that the relationship represents a universal restriction.
	 */
	public static final byte FLAG_IS_UNIVERSAL = 1 << 6;

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
	
	private static String getLabel(final String objectId, final String attributeId, final String valueId) {
		return new StringBuilder(objectId).append(" - ").append(attributeId).append(" - ").append(valueId).toString();
	}
	
	private final String objectId;
	private final String attributeId;
	private final String valueId;
	private final String characteristicTypeId;
	private final byte group;
	private final byte unionGroup;
	private final byte flags;

	/**
	 * Creates a RelationshipMini instance with the specified parameters.
	 * 
	 * @param id the unique SCT relationship identifier.
	 * @param objectId the object (source) concept ID.
	 * @param attributeId the attribute (type) concept ID.
	 * @param valueId the value (destination) concept ID.
	 * @param characteristicTypeId the characteristic type concept ID of the relationship.
	 * @param storageKey unique storage key (CDO ID).
	 * @param moduleId ID of the module concept.
	 * @param group the relationship's group identifier.
	 * @param unionGroup the relationship's union group identifier.
	 * @param flags represents the following relationship properties: status, universal, inferred by a reasoner, destination should be negated.
	 * @param effectiveTime the relationship's effective time, or {@link DateUtils#UNSET_EFFECTIVE_TIME} if no effective time has been set.
	 */
	public SnomedRelationshipIndexEntry(final String id, final String objectId, final String attributeId, final String  valueId, final String characteristicTypeId, final long storageKey, 
			final String moduleId, final byte group, final byte unionGroup, final byte flags, final long effectiveTime) {
		super(id, getLabel(objectId, attributeId, valueId), characteristicTypeId, moduleId, unionGroup, storageKey, isAnyFlagSet(flags, FLAG_IS_RELEASED), isAnyFlagSet(flags, FLAG_IS_ACTIVE), effectiveTime);
		this.objectId = Preconditions.checkNotNull(objectId, "Object concept argument ID cannot be null.");
		this.attributeId = Preconditions.checkNotNull(attributeId, "Attribute concept ID argument cannot be null.");
		this.valueId = Preconditions.checkNotNull(valueId, "Value concept ID argument cannot be null.");
		this.characteristicTypeId = Preconditions.checkNotNull(characteristicTypeId, "Characteristic type concept ID argument cannot be null.");
		this.group = group;
		this.unionGroup = unionGroup;
		this.flags = flags;
	}
	
	/**
	 * Creates a {@link SnomedRelationshipIndexEntry} from the specified {@link Relationship}
	 * 
	 * @param relationship
	 */
	public SnomedRelationshipIndexEntry(Relationship relationship) {
		this(
				relationship.getId(), 
				relationship.getSource().getId(), 
				relationship.getType().getId(), 
				relationship.getDestination().getId(), 
				relationship.getCharacteristicType().getId(), 
				CDOIDUtils.asLongSafe(relationship.cdoID()), 
				relationship.getModule().getId(), 
				(byte) relationship.getGroup(), 
				(byte) relationship.getUnionGroup(), 
				generateFlags(
						relationship.isReleased(),
						relationship.isActive(),
						Concepts.INFERRED_RELATIONSHIP.equals(relationship.getCharacteristicType().getId()),
						Concepts.UNIVERSAL_RESTRICTION_MODIFIER.equals(relationship.getModifier().getId()),
						relationship.isDestinationNegated()), 
				EffectiveTimes.getEffectiveTime(relationship.getEffectiveTime()));
	}

	@Override
	public String getObjectId() {
		return objectId;
	}

	@Override
	public String getAttributeId() {
		return attributeId;
	}

	@Override
	public String getValueId() {
		return valueId;
	}
	
	/**
	 * Returns with the relationship characteristic type concept ID.
	 * @return the characteristic type concept ID.
	 */
	public String getCharacteristicTypeId() {
		return characteristicTypeId;
	}

	/**
	 * Returns with the relationship group.
	 * @return the group.
	 */
	public byte getGroup() {
		return group;
	}
	
	/**
	 * Returns with the relationship union group.
	 * @return the union group.
	 */
	public byte getUnionGroup() {
		return unionGroup;
	}

	/**
	 * Returns {@code true} if the relationship is inferred. Otherwise {@code false}.
	 * @return {@code true} if inferred, otherwise {@code false}.
	 */
	public boolean isInferred() {
		return isAnyFlagSet(FLAG_IS_INFERRED);
	}
	
	/**
	 * Returns with {@code true} if the relationship is universal restriction. {@code false} if existential.
	 * @return {@code true} if universal.
	 */
	public boolean isUniversal() {
		return isAnyFlagSet(FLAG_IS_UNIVERSAL);
	}
	
	/**
	 * Returns {@code true} if the destination is negated. Otherwise {@code false}. 
	 * @return {@code true} if the destination is negated. 
	 */
	public boolean isDestinationNegated() {
		return isAnyFlagSet(FLAG_IS_DESTINATION_NEGATED);
	}
	
	/**
	 * (non-API)
	 * 
	 * Returns with all boolean property of the relationship in a compact form.
	 */
	public byte getFlags() {
		return flags;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		final SnomedRelationshipIndexEntry other = (SnomedRelationshipIndexEntry) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return MessageFormat.format("{0}: {1} == {2} ==> {8} {3} [{4},{5}] a:{6} i:{7} u:{8}", 
				id, objectId, attributeId, valueId, group, unionGroup, 
				isActive(), isInferred(), isUniversal(), isDestinationNegated() ? "NOT" : "");
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

	public boolean isDefining() {
		return Concepts.DEFINING_RELATIONSHIP.equals(getCharacteristicTypeId());
	}

	public boolean isAdditional() {
		return Concepts.ADDITIONAL_RELATIONSHIP.equals(getCharacteristicTypeId());
	}
	
}