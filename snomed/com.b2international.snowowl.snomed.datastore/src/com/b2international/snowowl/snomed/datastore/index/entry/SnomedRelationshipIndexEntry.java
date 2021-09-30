/*
 * Copyright 2011-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.b2international.index.query.Expressions.*;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.Sets.newHashSet;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.index.Doc;
import com.b2international.index.query.Expression;
import com.b2international.index.query.Expressions.ExpressionBuilder;
import com.b2international.index.revision.ObjectId;
import com.b2international.index.revision.Revision;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.RelationshipValue;
import com.b2international.snowowl.snomed.core.domain.RelationshipValueType;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationship;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.common.base.MoreObjects.ToStringHelper;
import com.google.common.collect.FluentIterable;

/**
 * A transfer object representing a SNOMED CT description.
 */
@Doc(
	type="relationship",
	revisionHash = { 
		SnomedDocument.Fields.ACTIVE, 
		SnomedDocument.Fields.EFFECTIVE_TIME, 
		SnomedDocument.Fields.MODULE_ID, 
		SnomedDocument.Fields.RELEASED, // XXX required for SnomedComponentRevisionConflictProcessor CHANGED vs. DELETED detection
		SnomedRelationshipIndexEntry.Fields.RELATIONSHIP_GROUP,
		SnomedRelationshipIndexEntry.Fields.UNION_GROUP,
		SnomedRelationshipIndexEntry.Fields.CHARACTERISTIC_TYPE_ID,
		SnomedRelationshipIndexEntry.Fields.MODIFIER_ID,
		SnomedRelationshipIndexEntry.Fields.TYPE_ID,
		SnomedRelationshipIndexEntry.Fields.DESTINATION_ID,
		SnomedRelationshipIndexEntry.Fields.DESTINATION_NEGATED,
		SnomedRelationshipIndexEntry.Fields.VALUE_TYPE,
		SnomedRelationshipIndexEntry.Fields.INTEGER_VALUE,
		SnomedRelationshipIndexEntry.Fields.DECIMAL_VALUE,
		SnomedRelationshipIndexEntry.Fields.NUMERIC_VALUE,
		SnomedRelationshipIndexEntry.Fields.STRING_VALUE
	}
)
@JsonDeserialize(builder = SnomedRelationshipIndexEntry.Builder.class)
public final class SnomedRelationshipIndexEntry extends SnomedComponentDocument {

	public static final int DEFAULT_RELATIONSHIP_GROUP = -1;
	public static final int DEFAULT_UNION_GROUP = -1;

	public static Builder builder() {
		return new Builder();
	}

	public static Builder builder(final SnomedRelationship input) {
		return builder()
			.id(input.getId())
			.active(input.isActive())
			.effectiveTime(EffectiveTimes.getEffectiveTime(input.getEffectiveTime()))
			.released(input.isReleased())
			.moduleId(input.getModuleId())
			.sourceId(input.getSourceId())
			.typeId(input.getTypeId())
			.destinationId(input.getDestinationId())
			.destinationNegated(input.isDestinationNegated())
			.value(input.getValueAsObject())
			.relationshipGroup(input.getRelationshipGroup())
			.unionGroup(input.getUnionGroup())
			.characteristicTypeId(input.getCharacteristicTypeId())
			.modifierId(input.getModifierId());
	}

	public static Builder builder(final SnomedRelationshipIndexEntry input) {
		return builder()
			.id(input.getId())
			.active(input.isActive())
			.effectiveTime(input.getEffectiveTime())
			.released(input.isReleased())
			.moduleId(input.getModuleId())
			.sourceId(input.getSourceId())
			.typeId(input.getTypeId())
			.destinationId(input.getDestinationId())
			.destinationNegated(input.isDestinationNegated())
			.value(input.getValueAsObject())
			.relationshipGroup(input.getRelationshipGroup())
			.unionGroup(input.getUnionGroup())
			.characteristicTypeId(input.getCharacteristicTypeId())
			.modifierId(input.getModifierId());
	}

	public static final class Expressions extends SnomedComponentDocument.Expressions {

		private Expressions() {}

		public static Expression sourceId(final String sourceId) {
			return sourceIds(Collections.singleton(sourceId));
		}

		public static Expression sourceIds(final Collection<String> sourceIds) {
			return matchAny(Fields.SOURCE_ID, sourceIds);
		}

		public static Expression typeId(final String typeId) {
			return typeIds(Collections.singleton(typeId));
		}

		public static Expression typeIds(final Collection<String> typeIds) {
			return matchAny(Fields.TYPE_ID, typeIds);
		}

		public static Expression hasDestinationId() {
			return exists(Fields.DESTINATION_ID);
		}

		public static Expression destinationId(final String destinationId) {
			return destinationIds(Collections.singleton(destinationId));
		}

		public static Expression destinationIds(final Collection<String> destinationIds) {
			return matchAny(Fields.DESTINATION_ID, destinationIds);
		}

		public static Expression destinationNegated() {
			return match(Fields.DESTINATION_NEGATED, true);
		}

		public static Expression values(final Collection<RelationshipValue> values) {
			if (values.isEmpty()) {
				return matchNone();
			}
			
			// We are only interested in whether all values are numeric or are strings
			final long types = values.stream()
				.map(v -> RelationshipValueType.STRING.equals(v.type()))
				.distinct()
				.count();
			
			if (types != 1L) {
				throw new BadRequestException("All relationship values should have the same type");
			}
			
			final Set<Integer> integerValues = newHashSet();
			final Set<Double> decimalValues = newHashSet();
			final Set<BigDecimal> numericValues = newHashSet();
			final Set<String> stringValues = newHashSet();
			
			values.forEach(value -> value
				.ifInteger(i -> { integerValues.add(i); numericValues.add(new BigDecimal(i)); })
				.ifDecimal(d -> { decimalValues.add(d.doubleValue()); numericValues.add(d); })
				.ifString(s -> { stringValues.add(s); }));
			
			if (!stringValues.isEmpty()) {
				return matchAny(Fields.STRING_VALUE, stringValues);
			}
			
			final ExpressionBuilder expressionBuilder = com.b2international.index.query.Expressions.builder();
			
			if (!numericValues.isEmpty()) {
				expressionBuilder.should(matchAnyDecimal(Fields.NUMERIC_VALUE, numericValues));
			}
			
			if (!integerValues.isEmpty()) {
				expressionBuilder.should(matchAnyInt(Fields.INTEGER_VALUE, integerValues));
			}
			
			if (!decimalValues.isEmpty()) {
				expressionBuilder.should(matchAnyDouble(Fields.DECIMAL_VALUE, decimalValues));
			}
			
			return expressionBuilder.build(); 
		}

		public static Expression valueLessThan(final RelationshipValue upper, final boolean includeUpper) {
			return upper.map(
				i -> com.b2international.index.query.Expressions.builder()
						.should(matchRange(Fields.NUMERIC_VALUE, null, new BigDecimal(i), true, includeUpper))
						.should(matchRange(Fields.INTEGER_VALUE, null, i, true, includeUpper))
						.build(), 
				d -> com.b2international.index.query.Expressions.builder()
						.should(matchRange(Fields.NUMERIC_VALUE, null, d, true, includeUpper))
						.should(matchRange(Fields.DECIMAL_VALUE, null, d.doubleValue(), true, includeUpper))
						.build(), 
				s -> matchRange(Fields.STRING_VALUE, null, s, true, includeUpper)); 
		}
		
		public static Expression valueGreaterThan(final RelationshipValue lower, final boolean includeLower) {
			return lower.map(
				i -> com.b2international.index.query.Expressions.builder()
						.should(matchRange(Fields.NUMERIC_VALUE, new BigDecimal(i), null, includeLower, true))
						.should(matchRange(Fields.INTEGER_VALUE, i, null, includeLower, true))
						.build(),
				d -> com.b2international.index.query.Expressions.builder()
						.should(matchRange(Fields.NUMERIC_VALUE, d, null, includeLower, true))
						.should(matchRange(Fields.DECIMAL_VALUE, d.doubleValue(), null, includeLower, true))
						.build(),
				s -> matchRange(Fields.STRING_VALUE, s, null, includeLower, true)); 
		}
		
		public static Expression valueType(final RelationshipValueType valueType) {
			return exactMatch(Fields.VALUE_TYPE, valueType.name());
		}
		
		public static Expression valueTypes(final Iterable<RelationshipValueType> valueTypes) {
			return matchAny(Fields.VALUE_TYPE, FluentIterable.from(valueTypes)
				.transform(RelationshipValueType::name)
				.toSet());
		}

		public static Expression relationshipGroup(final int relationshipGroup) {
			return match(Fields.RELATIONSHIP_GROUP, relationshipGroup);
		}

		public static Expression relationshipGroup(final int groupStart, final int groupEnd) {
			checkArgument(groupStart <= groupEnd, "Group end should be greater than or equal to groupStart");
			if (groupStart == groupEnd) {
				return relationshipGroup(groupStart);
			} else {
				return matchRange(Fields.RELATIONSHIP_GROUP, groupStart, groupEnd);
			}
		}

		public static Expression unionGroup(final int unionGroup) {
			return match(Fields.UNION_GROUP, unionGroup);
		}

		public static Expression characteristicTypeId(final String characteristicTypeId) {
			return characteristicTypeIds(Collections.singleton(characteristicTypeId));
		}

		public static Expression characteristicTypeIds(final Collection<String> characteristicTypeIds) {
			return matchAny(Fields.CHARACTERISTIC_TYPE_ID, characteristicTypeIds);
		}

		public static Expression modifierId(final String modifierId) {
			return modifierIds(Collections.singleton(modifierId));
		}

		public static Expression modifierIds(final Collection<String> modifierIds) {
			return matchAny(Fields.MODIFIER_ID, modifierIds);
		}
	}

	public static final class Fields extends SnomedComponentDocument.Fields {
		public static final String SOURCE_ID = SnomedRf2Headers.FIELD_SOURCE_ID;
		public static final String TYPE_ID = SnomedRf2Headers.FIELD_TYPE_ID;
		public static final String DESTINATION_ID = SnomedRf2Headers.FIELD_DESTINATION_ID;
		public static final String RELATIONSHIP_GROUP = SnomedRf2Headers.FIELD_RELATIONSHIP_GROUP;
		public static final String CHARACTERISTIC_TYPE_ID = SnomedRf2Headers.FIELD_CHARACTERISTIC_TYPE_ID;
		public static final String MODIFIER_ID = SnomedRf2Headers.FIELD_MODIFIER_ID;
		
		public static final String DESTINATION_NEGATED = "destinationNegated";
		public static final String VALUE_TYPE = "valueType";
		@Deprecated 
		public static final String INTEGER_VALUE = "integerValue";
		@Deprecated 
		public static final String DECIMAL_VALUE = "decimalValue";
		public static final String NUMERIC_VALUE = "numericValue";
		public static final String STRING_VALUE = "stringValue";
		public static final String UNION_GROUP = "unionGroup";
	}

	@JsonPOJOBuilder(withPrefix="")
	public static class Builder extends SnomedComponentDocument.Builder<Builder, SnomedRelationshipIndexEntry> {

		private String sourceId;
		private String typeId;
		private String destinationId;
		private boolean destinationNegated;
		private RelationshipValueType valueType;
		private BigDecimal numericValue;
		private String stringValue;
		private int relationshipGroup = DEFAULT_RELATIONSHIP_GROUP;
		private int unionGroup = DEFAULT_UNION_GROUP;
		private String characteristicTypeId;
		private String modifierId;

		@JsonCreator
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
			/*
			 * XXX: We need to ignore null values here, as usually both destination ID and
			 * value is passed in to the builder, and the "last" non-null value decides
			 * which form will be built.
			 */
			if (destinationId != null) {
				valueType = null;
				numericValue = null;
				stringValue = null;

				this.destinationId = destinationId;
			}
			
			return getSelf();
		}

		@JsonIgnore
		Builder destinationNegated(final Boolean destinationNegated) {
			return destinationNegated(destinationNegated == null ? false : destinationNegated);
		}

		@JsonProperty
		public Builder destinationNegated(final boolean destinationNegated) {
			this.destinationNegated = destinationNegated;
			return getSelf();
		}

		public Builder value(final RelationshipValue value) {
			/*
			 * XXX: We need to ignore null values here, as usually both destination ID and
			 * value is passed in to the builder, and the "last" non-null value decides
			 * which form will be built.
			 */
			if (value != null) {
				destinationId = null;
				
				value
					.ifInteger(i -> { valueType = RelationshipValueType.INTEGER; numericValue = new BigDecimal(i); })
					.ifDecimal(d -> { valueType = RelationshipValueType.DECIMAL; numericValue = d; })
					.ifString(s -> { valueType = RelationshipValueType.STRING; stringValue = s; });
			}

			return getSelf();
		}

		// Methods below are called by JSON deserialization and tests
		Builder valueType(final RelationshipValueType valueType) {
			this.valueType = valueType;
			return getSelf();
		}

		Builder numericValue(final BigDecimal numericValue) {
			this.numericValue = numericValue;
			return getSelf();
		}
		
		Builder integerValue(final Integer integerValue) {
			this.numericValue = new BigDecimal(integerValue);
			return getSelf();
		}
		
		Builder decimalValue(final Double decimalValue) {
			this.numericValue = BigDecimal.valueOf(decimalValue);
			return getSelf();
		}

		Builder stringValue(final String stringValue) {
			this.stringValue = stringValue;
			return getSelf();
		}

		@JsonIgnore
		Builder relationshipGroup(final Integer relationshipGroup) {
			return relationshipGroup(relationshipGroup == null ? DEFAULT_RELATIONSHIP_GROUP : relationshipGroup);
		}

		@JsonProperty
		public Builder relationshipGroup(final int relationshipGroup) {
			this.relationshipGroup = relationshipGroup;
			return getSelf();
		}

		@JsonIgnore
		Builder unionGroup(final Integer unionGroup) {
			return unionGroup(unionGroup == null ? DEFAULT_UNION_GROUP : unionGroup);
		}

		@JsonProperty
		public Builder unionGroup(final int unionGroup) {
			this.unionGroup = unionGroup;
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

		public SnomedRelationshipIndexEntry build() {
<<<<<<< HEAD
			final SnomedRelationshipIndexEntry doc = new SnomedRelationshipIndexEntry(id,
					moduleId, 
					released, 
					active, 
					effectiveTime, 
					sourceId, 
					typeId, 
					destinationId,
					valueType,
					integerValue,
					decimalValue,
					stringValue,
					characteristicTypeId, 
					modifierId, 
					relationshipGroup, 
					unionGroup, 
					destinationNegated,
					memberOf,
					activeMemberOf);
			
=======
			final SnomedRelationshipIndexEntry doc = new SnomedRelationshipIndexEntry(
				id,
				label,
				moduleId, 
				released, 
				active, 
				effectiveTime, 
				sourceId, 
				typeId, 
				destinationId, 
				valueType,
				numericValue,
				stringValue,
				characteristicTypeId, 
				modifierId, 
				group, 
				unionGroup, 
				destinationNegated,
				memberOf,
				activeMemberOf);

>>>>>>> refs/remotes/origin/7.x
			doc.setScore(score);
			return doc;
		}
	}

	private final String sourceId;
	private final String typeId;
	private final String characteristicTypeId;
	private final String modifierId;
	private final int relationshipGroup;
	
	// extra non-RF2 compatible fields
	private final int unionGroup;
	private final boolean destinationNegated;
	
	// value related fields
	private final String destinationId;
	private final BigDecimal numericValue;
	private final String stringValue;

	private final RelationshipValueType valueType;

	// Fields kept for backwards compatibility with earlier documents
	@JsonIgnore
	private final Integer integerValue = null;
	
	@JsonIgnore
	private final Double decimalValue = null;
	
	private SnomedRelationshipIndexEntry(
		final String id, 
		final String moduleId, 
		final Boolean released,
		final Boolean active, 
		final Long effectiveTimeLong,
		final String sourceId,
		final String typeId,
		final String destinationId,
		final RelationshipValueType valueType,
		final BigDecimal numericValue,
		final String stringValue,
		final String characteristicTypeId,
		final String modifierId,
		final int relationshipGroup,
		final int unionGroup,
		final boolean destinationNegated,
		final List<String> referringRefSets,
		final List<String> referringMappingRefSets) {

		super(
			id, 
			typeId, // XXX: iconId is the same as typeId 
			moduleId, 
			released, 
			active, 
			effectiveTimeLong,
			referringRefSets,
			referringMappingRefSets);

		// XXX -1 is the default value
		checkArgument(relationshipGroup >= -1, "Relationship group number '%s' may not be negative (relationship ID: %s).", relationshipGroup, id);
		checkArgument(unionGroup >= -1, "Union group number '%s' may not be negative (relationship ID: %s).", unionGroup, id);

		this.sourceId = sourceId;
		this.typeId = typeId;
		this.destinationId = destinationId;
		this.valueType = valueType;
		this.numericValue = numericValue;
		this.stringValue = stringValue;
		this.characteristicTypeId = characteristicTypeId;
		this.modifierId = modifierId;
		this.relationshipGroup = relationshipGroup;
		this.unionGroup = unionGroup;
		this.destinationNegated = destinationNegated;
	}

	@Override
	protected Revision.Builder<?, ? extends Revision> toBuilder() {
		return builder(this);
	}

	@Override
	public ObjectId getContainerId() {
		return ObjectId.of(SnomedConceptDocument.class, getSourceId());
	}

	@Override
	@JsonIgnore
	public String getIconId() {
		return super.getIconId();
	}

	public String getSourceId() {
		return sourceId;
	}

	public String getTypeId() {
		return typeId;
	}

	public String getDestinationId() {
		return destinationId;
	}

	public RelationshipValueType getValueType() {
		return valueType;
	}

	@JsonIgnore
	public boolean hasValue() {
		return (valueType != null);
	}

	@JsonIgnore
	public RelationshipValue getValueAsObject() {
		return RelationshipValue.fromTypeAndObjects(valueType, numericValue, stringValue);
	}

	@JsonProperty
	BigDecimal getNumericValue() {
		return numericValue;
	}

	@JsonProperty
	String getStringValue() {
		return stringValue;
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
	@JsonIgnore
	public boolean isDefining() {
		return Concepts.DEFINING_RELATIONSHIP.equals(characteristicTypeId);
	}

	/**
	 * @return {@code true} if the characteristic type id is equal to {@link Concepts#INFERRED_RELATIONSHIP}, {@code false} otherwise
	 */
	@JsonIgnore
	public boolean isInferred() {
		return Concepts.INFERRED_RELATIONSHIP.equals(characteristicTypeId);
	}

	/**
	 * @return {@code true} if the characteristic type id is equal to {@link Concepts#STATED_RELATIONSHIP}, {@code false} otherwise
	 */
	@JsonIgnore
	public boolean isStated() {
		return Concepts.STATED_RELATIONSHIP.equals(characteristicTypeId);
	}

	/**
	 * @return {@code true} if the characteristic type id is equal to {@link Concepts#ADDITIONAL_RELATIONSHIP}, {@code false} otherwise
	 */
	@JsonIgnore
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
	@JsonIgnore
	public boolean isUniversal() {
		return Concepts.UNIVERSAL_RESTRICTION_MODIFIER.equals(modifierId);
	}

	/**
	 * @return {@code true} if the modifier id is equal to {@link Concepts#EXISTENTIAL_RESTRICTION_MODIFIER}, {@code false} otherwise
	 */
	@JsonIgnore
	public boolean isExistential() {
		return Concepts.EXISTENTIAL_RESTRICTION_MODIFIER.equals(modifierId);
	}

	/**
	 * @return the relationship group
	 */
	public Integer getRelationshipGroup() {
		return relationshipGroup == DEFAULT_RELATIONSHIP_GROUP ? null : relationshipGroup;
	}

	/**
	 * @return the relationship union group
	 */
	public Integer getUnionGroup() {
		return unionGroup == DEFAULT_UNION_GROUP ? null : unionGroup;
	}

	/**
	 * @return {@code true} if the destination concept should be negated, {@code false} otherwise
	 */
	public boolean isDestinationNegated() {
		return destinationNegated;
	}

	@Override
	protected ToStringHelper doToString() {
		return super.doToString()
			.add("sourceId", sourceId)
			.add("typeId", typeId)
			.add("destinationId", destinationId)
			.add("value", getValueAsObject())
			.add("characteristicTypeId", characteristicTypeId)
			.add("modifierId", modifierId)
			.add("relationshipGroup", relationshipGroup)
			.add("unionGroup", unionGroup)
			.add("destinationNegated", destinationNegated);
	}
}
