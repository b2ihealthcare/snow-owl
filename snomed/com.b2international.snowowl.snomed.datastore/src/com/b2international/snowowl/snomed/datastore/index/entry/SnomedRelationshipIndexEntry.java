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
package com.b2international.snowowl.snomed.datastore.index.entry;

import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.RELATIONSHIP_DESTINATION_NEGATED;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.RELATIONSHIP_GROUP;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.RELATIONSHIP_OBJECT_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.RELATIONSHIP_UNION_GROUP;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.RELATIONSHIP_UNIVERSAL;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.RELATIONSHIP_VALUE_ID;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;

import org.apache.lucene.document.Document;

import com.b2international.commons.BooleanUtils;
import com.b2international.snowowl.core.api.IComponent;
import com.b2international.snowowl.core.api.IStatement;
import com.b2international.snowowl.core.api.index.IIndexEntry;
import com.b2international.snowowl.datastore.index.mapping.Mappings;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.domain.CharacteristicType;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedMappings;

/**
 * A transfer object representing a SNOMED CT description.
 */
public class SnomedRelationshipIndexEntry extends SnomedIndexEntry implements IStatement<String>, IComponent<String>, IIndexEntry, Serializable {

	private static final long serialVersionUID = -7873086925532169024L;

	public static Builder builder() {
		return new Builder();
	}
	
	public static Builder builder(final Document doc) {
		return builder()
				.id(SnomedMappings.id().getValueAsString(doc))
				.sourceId(doc.get(RELATIONSHIP_OBJECT_ID))
				.typeId(SnomedMappings.relationshipType().getValueAsString(doc))
				.destinationId(doc.get(RELATIONSHIP_VALUE_ID))
				.characteristicTypeId(SnomedMappings.relationshipCharacteristicType().getValueAsString(doc))
				.group((byte) doc.getField(RELATIONSHIP_GROUP).numericValue().intValue())
				.unionGroup((byte) doc.getField(RELATIONSHIP_UNION_GROUP).numericValue().intValue())
				.active(BooleanUtils.valueOf(SnomedMappings.active().getValue(doc)))
				.released(BooleanUtils.valueOf(SnomedMappings.released().getValue(doc)))
				.modifierId(BooleanUtils.valueOf(Mappings.intField(RELATIONSHIP_UNIVERSAL).getValue(doc)) ? Concepts.UNIVERSAL_RESTRICTION_MODIFIER : Concepts.EXISTENTIAL_RESTRICTION_MODIFIER)
				.destinationNegated(BooleanUtils.valueOf(Mappings.intField(RELATIONSHIP_DESTINATION_NEGATED).getValue(doc)))
				.moduleId(SnomedMappings.module().getValueAsString(doc))
				.storageKey(Mappings.storageKey().getValue(doc))
				.effectiveTimeLong(SnomedMappings.effectiveTime().getValue(doc));
	}

	public static class Builder extends AbstractBuilder<Builder> {

		private String sourceId;
		private String typeId;
		private String destinationId;
		private String characteristicTypeId;
		private String modifierId;

		private byte group;
		private byte unionGroup;

		private boolean destinationNegated;
		
		private Builder() {
			// Disallow instantiation outside static method
		}
		
		@Override
		protected Builder getSelf() {
			return this;
		}

		public Builder sourceId(final String sourceId) {
			this.sourceId = sourceId;
			return getSelf();
		}

		public Builder typeId(final String typeId) {
			this.typeId = typeId;
			return getSelf();
		}

		public Builder destinationId(final String destinationId) {
			this.destinationId = destinationId;
			return getSelf();
		}

		public Builder characteristicTypeId(final String characteristicTypeId) {
			this.characteristicTypeId = characteristicTypeId;
			return getSelf();
		}

		public Builder modifierId(final String modifierId) {
			this.modifierId = modifierId;
			return getSelf();
		}

		public Builder group(final byte group) {
			this.group = group;
			return getSelf();
		}

		public Builder unionGroup(final byte unionGroup) {
			this.unionGroup = unionGroup;
			return getSelf();
		}

		public Builder destinationNegated(final boolean destinationNegated) {
			this.destinationNegated = destinationNegated;
			return getSelf();
		}

		public SnomedRelationshipIndexEntry build() {
			return new SnomedRelationshipIndexEntry(id, 
					score, 
					storageKey, 
					moduleId, 
					released, 
					active, 
					effectiveTimeLong, 
					sourceId, 
					typeId, 
					destinationId, 
					characteristicTypeId, 
					modifierId, 
					group, 
					unionGroup, 
					destinationNegated);
		}
	}

	private final String sourceId;
	private final String destinationId;
	private final String characteristicTypeId;
	private final String modifierId;
	private final byte group;
	private final byte unionGroup;
	private final boolean destinationNegated;

	private SnomedRelationshipIndexEntry(final String id, 
			final float score, 
			final long storageKey, 
			final String moduleId, 
			final boolean released,
			final boolean active, 
			final long effectiveTimeLong,
			final String sourceId,
			final String typeId,
			final String destinationId,
			final String characteristicTypeId,
			final String modifierId,
			final byte group,
			final byte unionGroup,
			final boolean destinationNegated) {

		super(id, 
				typeId, // XXX: iconId is the same as typeId 
				score, 
				storageKey, 
				moduleId, 
				released, 
				active, 
				effectiveTimeLong);

		checkArgument(group >= 0, "Group number '%s' may not be negative.");
		checkArgument(unionGroup >= 0, "Union group number '%s' may not be negative.");

		this.sourceId = checkNotNull(sourceId, "Relationship source identifier may not be null.");
		this.destinationId = checkNotNull(destinationId, "Relationship destination identifier may not be null.");
		this.characteristicTypeId = checkNotNull(characteristicTypeId, "Relationship characteristic type identifier may not be null.");
		this.modifierId = checkNotNull(modifierId, "Relationship modifier identifier may not be null.");
		this.group = group;
		this.unionGroup = unionGroup;
		this.destinationNegated = destinationNegated;
	}

	@Override
	public String getObjectId() {
		return sourceId;
	}

	@Override
	public String getAttributeId() {
		return getIconId(); // XXX: aliased to icon identifier in constructor
	}

	@Override
	public String getValueId() {
		return destinationId;
	}

	/**
	 * @return the characteristic type identifier of this relationship
	 */
	public String getCharacteristicTypeId() {
		return characteristicTypeId;
	}
	
	/**
	 * @return {@code true} if the characteristic type id is equal to {@link Concepts#DEFINING_RELATIONSHIP}, {@code false} otherwise
	 */
	public boolean isDefining() {
		return Concepts.DEFINING_RELATIONSHIP.equals(characteristicTypeId);
	}

	/**
	 * @return {@code true} if the characteristic type id is equal to {@link Concepts#INFERRED_RELATIONSHIP}, {@code false} otherwise
	 */
	public boolean isInferred() {
		return Concepts.INFERRED_RELATIONSHIP.equals(characteristicTypeId);
	}
	
	/**
	 * @return {@code true} if the characteristic type id is equal to {@link Concepts#STATED_RELATIONSHIP}, {@code false} otherwise
	 */
	public boolean isStated() {
		return Concepts.STATED_RELATIONSHIP.equals(characteristicTypeId);
	}

	/**
	 * @return {@code true} if the characteristic type id is equal to {@link Concepts#ADDITIONAL_RELATIONSHIP}, {@code false} otherwise
	 */
	public boolean isAdditional() {
		return Concepts.ADDITIONAL_RELATIONSHIP.equals(characteristicTypeId);
	}

	/**
	 * @return the modifier identifier of this relationship
	 */
	public String getModifierId() {
		return modifierId;
	}

	/**
	 * @return {@code true} if the modifier id is equal to {@link Concepts#UNIVERSAL_RESTRICTION_MODIFIER}, {@code false} otherwise
	 */
	public boolean isUniversal() {
		return Concepts.UNIVERSAL_RESTRICTION_MODIFIER.equals(modifierId);
	}
	
	/**
	 * @return {@code true} if the modifier id is equal to {@link Concepts#EXISTENTIAL_RESTRICTION_MODIFIER}, {@code false} otherwise
	 */
	public boolean isExistential() {
		return Concepts.EXISTENTIAL_RESTRICTION_MODIFIER.equals(modifierId);
	}
	
	/**
	 * @return the {@link CharacteristicType} value for this relationship, based on the stored characteristic type identifier
	 */
	public CharacteristicType getCharacteristicType() {
		return CharacteristicType.getByConceptId(characteristicTypeId);
	}

	/**
	 * @return the relationship group
	 */
	public byte getGroup() {
		return group;
	}

	/**
	 * @return the relationship union group
	 */
	public byte getUnionGroup() {
		return unionGroup;
	}

	/**
	 * @return {@code true} if the destination concept should be negated, {@code false} otherwise
	 */
	public boolean isDestinationNegated() {
		return destinationNegated;
	}

	@Override
	public String toString() {
		return toStringHelper()
				.add("sourceId", sourceId)
				.add("typeId", getAttributeId())
				.add("destinationId", destinationId)
				.add("characteristicTypeId", characteristicTypeId)
				.add("modifierId", modifierId)
				.add("group", group)
				.add("unionGroup", unionGroup)
				.add("destinationNegated", destinationNegated)
				.toString();
	}
}
