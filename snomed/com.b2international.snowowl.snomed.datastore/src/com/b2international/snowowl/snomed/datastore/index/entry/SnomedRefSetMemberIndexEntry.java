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

import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.CONCEPT_NUMBER;
import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.DESCRIPTION_NUMBER;
import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.RELATIONSHIP_NUMBER;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Maps.newHashMap;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.b2international.commons.StringUtils;
import com.b2international.commons.functions.UncheckedCastFunction;
import com.b2international.snowowl.core.CoreTerminologyBroker;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.domain.Acceptability;
import com.b2international.snowowl.snomed.core.domain.InactivationIndicator;
import com.b2international.snowowl.snomed.core.domain.RelationshipRefinability;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedCoreComponent;
import com.b2international.snowowl.snomed.core.domain.SnomedDescription;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationship;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetUtil;
import com.b2international.snowowl.snomed.snomedrefset.DataType;
import com.b2international.snowowl.snomed.snomedrefset.SnomedAssociationRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedAttributeValueRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedComplexMapRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedConcreteDataTypeRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedDescriptionTypeRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedLanguageRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedModuleDependencyRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedQueryRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.b2international.snowowl.snomed.snomedrefset.SnomedSimpleMapRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.util.SnomedRefSetSwitch;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

/**
 * Lightweight representation of a SNOMED CT reference set member.
 */
public class SnomedRefSetMemberIndexEntry extends SnomedDocument {

	public static class Fields {
		// known RF2 fields
		public static final String ACCEPTABILITY_ID = SnomedRf2Headers.FIELD_ACCEPTABILITY_ID;
		public static final String VALUE_ID = SnomedRf2Headers.FIELD_VALUE_ID;
		public static final String TARGET_COMPONENT = SnomedRf2Headers.FIELD_TARGET_COMPONENT;
		public static final String MAP_TARGET = SnomedRf2Headers.FIELD_MAP_TARGET;
		public static final String MAP_TARGET_DESCRIPTION = SnomedRf2Headers.FIELD_MAP_TARGET_DESCRIPTION;
		public static final String MAP_GROUP = SnomedRf2Headers.FIELD_MAP_GROUP;
		public static final String MAP_PRIORITY = SnomedRf2Headers.FIELD_MAP_PRIORITY;
		public static final String MAP_RULE = SnomedRf2Headers.FIELD_MAP_RULE;
		public static final String MAP_ADVICE = SnomedRf2Headers.FIELD_MAP_ADVICE;
		public static final String MAP_CATEGORY_ID = SnomedRf2Headers.FIELD_MAP_CATEGORY_ID;
		public static final String CORRELATION_ID = SnomedRf2Headers.FIELD_CORRELATION_ID;
		public static final String DESCRIPTION_FORMAT = SnomedRf2Headers.FIELD_DESCRIPTION_FORMAT;
		public static final String DESCRIPTION_LENGTH = SnomedRf2Headers.FIELD_DESCRIPTION_LENGTH;
		public static final String OPERATOR_ID = SnomedRf2Headers.FIELD_OPERATOR_ID;
		public static final String UNIT_ID = SnomedRf2Headers.FIELD_UNIT_ID;
		public static final String QUERY = SnomedRf2Headers.FIELD_QUERY;
		public static final String CHARACTERISTIC_TYPE_ID = SnomedRf2Headers.FIELD_CHARACTERISTIC_TYPE_ID;
		public static final String SOURCE_EFFECTIVE_TIME = SnomedRf2Headers.FIELD_SOURCE_EFFECTIVE_TIME;
		public static final String TARGET_EFFECTIVE_TIME = SnomedRf2Headers.FIELD_TARGET_EFFECTIVE_TIME;
		public static final String DATA_VALUE = SnomedRf2Headers.FIELD_VALUE;
		public static final String ATTRIBUTE_NAME = SnomedRf2Headers.FIELD_ATTRIBUTE_NAME;
		// extra index fields to store datatype and map target type
		public static final String DATA_TYPE = "dataType";
		public static final String MAP_TARGET_TYPE = "mapTargetType";
	}
	
	private static final Set<String> ADDITIONAL_FIELDS = ImmutableSet.<String>builder()
			.add(Fields.ACCEPTABILITY_ID)
			.add(Fields.VALUE_ID)
			.add(Fields.TARGET_COMPONENT)
			.add(Fields.MAP_TARGET)
			.add(Fields.MAP_GROUP)
			.add(Fields.MAP_TARGET_DESCRIPTION)
			.add(Fields.MAP_PRIORITY)
			.add(Fields.MAP_RULE)
			.add(Fields.MAP_ADVICE)
			.add(Fields.MAP_CATEGORY_ID)
			.add(Fields.CORRELATION_ID)
			.add(Fields.DESCRIPTION_FORMAT)
			.add(Fields.DESCRIPTION_LENGTH)
			.add(Fields.OPERATOR_ID)
			.add(Fields.UNIT_ID)
			.add(Fields.QUERY)
			.add(Fields.CHARACTERISTIC_TYPE_ID)
			.add(Fields.SOURCE_EFFECTIVE_TIME)
			.add(Fields.TARGET_EFFECTIVE_TIME)
			.add(Fields.DATA_VALUE)
			.add(Fields.ATTRIBUTE_NAME)
			.build();

	/**
	 * @param name the field name to check
	 * @return {@code true} if the specified field name is valid as an additional {@code String} or {@link Number} value, {@code false} otherwise
	 */
	public static boolean isAdditionalField(final String name) {
		return ADDITIONAL_FIELDS.contains(name);
	}
	
	private static final long serialVersionUID = 3504576207161692354L;

	public static Builder builder() {
		return new Builder();
	}
	
	public static Builder builder(final SnomedRefSetMemberIndexEntry source) {
		return builder()
				.active(source.isActive())
				.effectiveTime(source.getEffectiveTime())
				.id(source.getId())
				.moduleId(source.getModuleId())
				.referencedComponentId(source.getReferencedComponentId())
				.referencedComponentType(source.getReferencedComponentType())
				.referenceSetId(source.getRefSetIdentifierId())
				.referenceSetType(source.getRefSetType())
				.released(source.isReleased())
				.mapTargetComponentType(source.getMapTargetComponentTypeAsShort())
				.additionalFields(source.additionalFields);
	}
	
	public static final Builder builder(final SnomedReferenceSetMember input) {
		final Object mapTargetComponentType = input.getProperties().get(Fields.MAP_TARGET_TYPE);
		
		final Builder builder = builder()
				.active(input.isActive())
				.effectiveTime(EffectiveTimes.getEffectiveTime(input.getEffectiveTime()))
				.id(input.getId())
				.moduleId(input.getModuleId())
				.referencedComponentId(input.getReferencedComponent().getId())
				.referencedComponentType(input.getReferencedComponent())
				.referenceSetId(input.getReferenceSetId())
				.referenceSetType(input.type())
				.released(input.isReleased())
				.mapTargetComponentType(mapTargetComponentType == null ? -1 : (short) mapTargetComponentType);
		
		for (Entry<String, Object> entry : input.getProperties().entrySet()) {
			final Object value = entry.getValue();
			final String fieldName = entry.getKey();
			if (value instanceof SnomedCoreComponent) {
				builder.additionalField(fieldName, ((SnomedCoreComponent) value).getId());
			} else {
				builder.additionalField(fieldName, convertValue(entry.getKey(), value));
			}
		}
		
		return builder;
	}
	
	public static Builder builder(SnomedRefSetMember refSetMember) {
		final Builder builder = SnomedRefSetMemberIndexEntry.builder()
				.id(refSetMember.getUuid()) 
				.moduleId(refSetMember.getModuleId())
				.active(refSetMember.isActive())
				.released(refSetMember.isReleased())
				.effectiveTime(refSetMember.isSetEffectiveTime() ? refSetMember.getEffectiveTime().getTime() : EffectiveTimes.UNSET_EFFECTIVE_TIME)
				.referenceSetId(refSetMember.getRefSetIdentifierId())
				.referenceSetType(refSetMember.getRefSet().getType())
				.referencedComponentType(refSetMember.getReferencedComponentType())
				.referencedComponentId(refSetMember.getReferencedComponentId());

		return new SnomedRefSetSwitch<Builder>() {

			@Override
			public Builder caseSnomedAssociationRefSetMember(final SnomedAssociationRefSetMember associationMember) {
				return builder.additionalField(Fields.TARGET_COMPONENT, associationMember.getTargetComponentId());
			}

			@Override
			public Builder caseSnomedAttributeValueRefSetMember(final SnomedAttributeValueRefSetMember attributeValueMember) {
				return builder.additionalField(Fields.VALUE_ID, attributeValueMember.getValueId());
			}

			@Override
			public Builder caseSnomedConcreteDataTypeRefSetMember(final SnomedConcreteDataTypeRefSetMember concreteDataTypeMember) {
				builder.additionalField(Fields.ATTRIBUTE_NAME, concreteDataTypeMember.getLabel())
						.additionalField(Fields.DATA_TYPE, concreteDataTypeMember.getDataType().ordinal())
						.additionalField(Fields.DATA_VALUE, concreteDataTypeMember.getSerializedValue())
						.additionalField(Fields.CHARACTERISTIC_TYPE_ID, Long.valueOf(concreteDataTypeMember.getCharacteristicTypeId()))
						.additionalField(Fields.OPERATOR_ID, Long.valueOf(concreteDataTypeMember.getOperatorComponentId()));

				if (concreteDataTypeMember.getUomComponentId() != null) {
					builder.additionalField(Fields.UNIT_ID, concreteDataTypeMember.getUomComponentId());
				}

				return builder;
			}

			@Override
			public Builder caseSnomedDescriptionTypeRefSetMember(final SnomedDescriptionTypeRefSetMember descriptionTypeMember) {
				return builder
						.additionalField(Fields.DESCRIPTION_FORMAT, Long.valueOf(descriptionTypeMember.getDescriptionFormat()))
						.additionalField(Fields.DESCRIPTION_LENGTH, descriptionTypeMember.getDescriptionLength());
			}

			@Override
			public Builder caseSnomedLanguageRefSetMember(final SnomedLanguageRefSetMember languageMember) {
				return builder.additionalField(Fields.ACCEPTABILITY_ID, Long.valueOf(languageMember.getAcceptabilityId()));
			}

			@Override
			public Builder caseSnomedModuleDependencyRefSetMember(final SnomedModuleDependencyRefSetMember moduleDependencyMember) {
				return builder
						.additionalField(Fields.SOURCE_EFFECTIVE_TIME, EffectiveTimes.getEffectiveTime(moduleDependencyMember.getSourceEffectiveTime()))
						.additionalField(Fields.TARGET_EFFECTIVE_TIME, EffectiveTimes.getEffectiveTime(moduleDependencyMember.getTargetEffectiveTime()));
			}

			@Override
			public Builder caseSnomedQueryRefSetMember(final SnomedQueryRefSetMember queryMember) {
				return builder.additionalField(Fields.QUERY, queryMember.getQuery());
			}

			@Override
			public Builder caseSnomedSimpleMapRefSetMember(final SnomedSimpleMapRefSetMember mapRefSetMember) {
				builder.mapTargetComponentType(mapRefSetMember.getMapTargetComponentType());
				builder.additionalField(Fields.MAP_TARGET, mapRefSetMember.getMapTargetComponentId());

				if (mapRefSetMember.getMapTargetComponentDescription() != null) {
					builder.additionalField(Fields.MAP_TARGET_DESCRIPTION, mapRefSetMember.getMapTargetComponentDescription());
				}

				return builder;
			}
			
			@Override
			public Builder caseSnomedComplexMapRefSetMember(final SnomedComplexMapRefSetMember mapRefSetMember) {
				builder.mapTargetComponentType(mapRefSetMember.getMapTargetComponentType());
				builder.additionalField(Fields.MAP_TARGET, mapRefSetMember.getMapTargetComponentId());
				builder.additionalField(Fields.CORRELATION_ID, Long.valueOf(mapRefSetMember.getCorrelationId()));

				addAdditionalFieldIfNotNull(builder, Fields.MAP_GROUP, Integer.valueOf(mapRefSetMember.getMapGroup()));
				addAdditionalFieldIfNotNull(builder, Fields.MAP_ADVICE, mapRefSetMember.getMapAdvice());
				addAdditionalFieldIfNotNull(builder, Fields.MAP_PRIORITY, Integer.valueOf(mapRefSetMember.getMapPriority()));
				addAdditionalFieldIfNotNull(builder, Fields.MAP_RULE, mapRefSetMember.getMapRule());
				
				// extended refset
				if (mapRefSetMember.getMapCategoryId() != null) {
					addAdditionalFieldIfNotNull(builder, Fields.MAP_CATEGORY_ID, Long.valueOf(mapRefSetMember.getMapCategoryId()));
				}

				return builder;
			}
			
			@Override
			public Builder caseSnomedRefSetMember(SnomedRefSetMember object) {
				return builder;
			};

		}.doSwitch(refSetMember);
	}
	
	private static void addAdditionalFieldIfNotNull(final Builder builder, final String fieldName, final Object value) {
		if (value != null) {
			builder.additionalField(fieldName, value);
		}
	}
	
	private static Object convertValue(String rf2Field, Object value) {
		switch (rf2Field) {
		case SnomedRf2Headers.FIELD_ACCEPTABILITY_ID:
		case SnomedRf2Headers.FIELD_CHARACTERISTIC_TYPE_ID:
		case SnomedRf2Headers.FIELD_CORRELATION_ID:
		case SnomedRf2Headers.FIELD_DESCRIPTION_FORMAT:
		case SnomedRf2Headers.FIELD_MAP_CATEGORY_ID:
		case SnomedRf2Headers.FIELD_OPERATOR_ID:
		case SnomedRf2Headers.FIELD_SOURCE_EFFECTIVE_TIME:
		case SnomedRf2Headers.FIELD_TARGET_EFFECTIVE_TIME:
		case SnomedRf2Headers.FIELD_UNIT_ID:
			if (value instanceof String && !StringUtils.isEmpty((String) value)) {
				return Long.valueOf((String) value);
			}
		default: return value;
		}
	}

	public static Collection<SnomedRefSetMemberIndexEntry> from(final Iterable<SnomedReferenceSetMember> refSetMembers) {
		return FluentIterable.from(refSetMembers).transform(new Function<SnomedReferenceSetMember, SnomedRefSetMemberIndexEntry>() {
			@Override
			public SnomedRefSetMemberIndexEntry apply(final SnomedReferenceSetMember refSetMember) {
				return builder(refSetMember).build();
			}
		}).toList();
	}

	public static class Builder extends SnomedDocumentBuilder<Builder> {

		private String referencedComponentId;
		private final Map<String, Object> additionalFields = newHashMap();

		private String referenceSetId;
		private SnomedRefSetType referenceSetType;
		private short referencedComponentType;
		private short mapTargetComponentType = CoreTerminologyBroker.UNSPECIFIED_NUMBER_SHORT;

		private Builder() {
			// Disallow instantiation outside static method
		}

		@Override
		protected Builder getSelf() {
			return this;
		}

		public Builder referencedComponentId(final String referencedComponentId) {
			this.referencedComponentId = referencedComponentId;
			return this;
		}

		public Builder additionalField(final String fieldName, final Object fieldValue) {
			this.additionalFields.put(fieldName, fieldValue);
			return this;
		}

		public Builder additionalFields(final Map<String, Object> additionalFields) {
			this.additionalFields.putAll(additionalFields);
			return this;
		}

		public Builder referenceSetId(final String referenceSetId) {
			this.referenceSetId = referenceSetId;
			return this;
		}

		public Builder referenceSetType(final SnomedRefSetType referenceSetType) {
			this.referenceSetType = referenceSetType;
			return this;
		}

		public Builder referencedComponentType(final String referencedComponentType) {
			this.referencedComponentType = SnomedTerminologyComponentConstants.getValue(referencedComponentType);
			return this;
		}
		
		public Builder referencedComponentType(final short referencedComponentType) {
			this.referencedComponentType = referencedComponentType;
			return this;
		}
		
		public Builder referencedComponentType(final SnomedCoreComponent component) {
			if (component instanceof SnomedConcept) {
				this.referencedComponentType = CONCEPT_NUMBER;
			} else if (component instanceof SnomedDescription) {
				this.referencedComponentType = DESCRIPTION_NUMBER;
			} else if (component instanceof SnomedRelationship) {
				this.referencedComponentType = RELATIONSHIP_NUMBER;
			} else {
				this.referencedComponentType = -1;
			}
			
			return this;
		}

		public Builder mapTargetComponentType(final short mapTargetComponentType) {
			this.mapTargetComponentType = mapTargetComponentType;
			return this;
		}
		
		public SnomedRefSetMemberIndexEntry build() {
			return new SnomedRefSetMemberIndexEntry(id,
					label,
					moduleId, 
					released, 
					active, 
					effectiveTime, 
					referencedComponentId, 
					ImmutableMap.copyOf(additionalFields),
					referenceSetId,
					referenceSetType,
					referencedComponentType,
					mapTargetComponentType);
		}
	}

	private final String referencedComponentId;
	private final ImmutableMap<String, Object> additionalFields;

	private final String referenceSetId;
	private final SnomedRefSetType referenceSetType;
	private final short referencedComponentType;
	private final short mapTargetComponentType;

	private SnomedRefSetMemberIndexEntry(final String id,
			final String label,
			final String moduleId, 
			final boolean released,
			final boolean active, 
			final long effectiveTimeLong, 
			final String referencedComponentId, 
			final ImmutableMap<String, Object> additionalFields,
			final String referenceSetId,
			final SnomedRefSetType referenceSetType,
			final short referencedComponentType,
			final short mapTargetComponentType) {

		super(id, 
				label,
				referencedComponentId, // XXX: iconId is the referenced component identifier
				moduleId, 
				released, 
				active, 
				effectiveTimeLong);

		checkArgument(referencedComponentType >= CoreTerminologyBroker.UNSPECIFIED_NUMBER_SHORT, "Referenced component type '%s' is invalid.", referencedComponentType);
		checkArgument(mapTargetComponentType >= CoreTerminologyBroker.UNSPECIFIED_NUMBER_SHORT, "Map target component type '%s' is invalid.", referencedComponentType);

		this.referencedComponentId = checkNotNull(referencedComponentId, "Reference component identifier may not be null.");
		this.additionalFields = checkNotNull(additionalFields, "Additional field map may not be null.");
		this.referenceSetId = checkNotNull(referenceSetId, "Reference set identifier may not be null.");
		this.referenceSetType = checkNotNull(referenceSetType, "Reference set type may not be null.");
		this.referencedComponentType = referencedComponentType;
		this.mapTargetComponentType = mapTargetComponentType;
	}

	/**
	 * @return the referenced component identifier
	 */
	public String getReferencedComponentId() {
		return referencedComponentId;
	}

	/**
	 * @param fieldName the name of the additional field
	 * @return the {@code String} value stored for the field
	 * @throws IllegalStateException if no value was set for the field
	 * @throws ClassCastException if the value is not of type {@code String}
	 */
	public String getStringField(final String fieldName) {
		return getField(fieldName, String.class);
	}

	/**
	 * @param fieldName the name of the additional field
	 * @return the {@code Integer} value stored for the field
	 * @throws IllegalStateException if no value was set for the field
	 * @throws ClassCastException if the value is not of type {@code Integer}
	 */
	public Integer getIntegerField(final String fieldName) {
		return getField(fieldName, Integer.class);
	}
	/**
	 * @param fieldName the name of the additional field
	 * @return the {@code Long} value stored for the field
	 * @throws IllegalStateException if no value was set for the field
	 * @throws ClassCastException if the value is not of type {@code Long}
	 */	
	public Long getLongField(final String fieldName) {
		return getField(fieldName, Long.class);
	}

	/**
	 * @param fieldName the name of the additional field
	 * @return the {@code BigDecimal} value stored for the field
	 * @throws IllegalStateException if no value was set for the field
	 * @throws ClassCastException if the value is not of type {@code BigDecimal}
	 */
	public BigDecimal getBigDecimalField(final String fieldName) {
		return getField(fieldName, BigDecimal.class);
	}

	/**
	 * @param fieldName the name of the additional field
	 * @return the {@code Date} value stored for the field
	 * @throws IllegalStateException if no value was set for the field
	 * @throws ClassCastException if the value is not of type {@code Date}
	 */
	public Date getDateField(final String fieldName) {
		return getField(fieldName, Date.class);
	}

	/**
	 * @param fieldName the name of the additional field
	 * @return the {@code Boolean} value stored for the field
	 * @throws IllegalStateException if no value was set for the field
	 * @throws ClassCastException if the value is not of type {@code Boolean}
	 */
	public Boolean getBooleanField(final String fieldName) {
		return getField(fieldName, Boolean.class);
	}

	/**
	 * @param fieldName the name of the additional field
	 * @return the {@code Object} value stored for the field
	 * @throws IllegalStateException if no value was set for the field
	 */	
	public Object getField(final String fieldName) {
		return getOptionalField(fieldName).get();
	}

	private Optional<Object> getOptionalField(final String fieldName) {
		return Optional.fromNullable(additionalFields.get(fieldName));
	}

	private <T> T getField(final String fieldName, final Class<T> type) {
		return getField(fieldName, new UncheckedCastFunction<Object, T>(type));
	}

	private <T> T getField(final String fieldName, Function<Object, T> transformFunction) {
		return getOptionalField(fieldName).transform(transformFunction).orNull();
	}

	/**
	 * @return the identifier of the member's reference set
	 */
	public String getRefSetIdentifierId() {
		return referenceSetId;
	}

	/**
	 * @return the type of the member's reference set
	 */
	public SnomedRefSetType getRefSetType() {
		return referenceSetType;
	}

	/**
	 * @return the {@code String} terminology component identifier of the component referenced in this member
	 */
	public String getReferencedComponentType() {
		return CoreTerminologyBroker.getInstance().getTerminologyComponentId(referencedComponentType);
	}

	/**
	 * @return the {@code String} terminology component identifier of the map target in this member, or
	 *         {@link CoreTerminologyBroker#UNSPECIFIED} if not known (or the reference set is not a map)
	 */
	public String getMapTargetComponentType() {
		return CoreTerminologyBroker.getInstance().getTerminologyComponentId(mapTargetComponentType);
	}
	
	/**
	 * @return the {@code String} terminology component identifier of the map target in this member, or
	 *         {@link CoreTerminologyBroker#UNSPECIFIED_NUMBER_SHORT} if not known (or the reference set is not a map)
	 */
	public short getMapTargetComponentTypeAsShort() {
		return mapTargetComponentType;
	}

	@Override
	public String toString() {
		return toStringHelper()
				.add("referencedComponentId", referencedComponentId)
				.add("additionalFields", additionalFields)
				.add("referenceSetType", referenceSetType)
				.add("referencedComponentType", referencedComponentType)
				.add("mapTargetComponentType", mapTargetComponentType)
				.toString();
	}

	@SuppressWarnings("unchecked")
	public <T> T getValue() {
		return (T) SnomedRefSetUtil.deserializeValue(getRefSetPackageDataType(), getStringField(Fields.DATA_VALUE)); 
	}

	public DataType getRefSetPackageDataType() {
		return DataType.get(getIntegerField(Fields.DATA_TYPE));
	}

	public String getUomComponentId() {
		return StringUtils.valueOfOrEmptyString(getOptionalField(Fields.UNIT_ID).orNull());
	}

	public String getAttributeLabel() {
		return getStringField(Fields.ATTRIBUTE_NAME);
	}

	public String getOperatorComponentId() {
		return StringUtils.valueOfOrEmptyString(getLongField(Fields.OPERATOR_ID));
	}

	public String getCharacteristicTypeId() {
		return StringUtils.valueOfOrEmptyString(getLongField(Fields.CHARACTERISTIC_TYPE_ID));
	}	

	public Acceptability getAcceptability() {
		return Acceptability.getByConceptId(getAcceptabilityId());
	}

	public String getAcceptabilityId() {
		return StringUtils.valueOfOrEmptyString(getLongField(Fields.ACCEPTABILITY_ID));
	}

	public Integer getDescriptionLength() {
		return getIntegerField(Fields.DESCRIPTION_LENGTH);
	}
	
	public String getDescriptionFormat() {
		return StringUtils.valueOfOrEmptyString(getLongField(Fields.DESCRIPTION_FORMAT));
	}

	public String getMapTargetComponentId() {
		return getOptionalField(Fields.MAP_TARGET).transform(new UncheckedCastFunction<>(String.class)).orNull();
	}

	public int getMapGroup() {
		return getIntegerField(Fields.MAP_GROUP);
	}

	public int getMapPriority() {
		return getIntegerField(Fields.MAP_PRIORITY);
	}

	public String getMapRule() {
		return getStringField(Fields.MAP_RULE);
	}

	public String getMapAdvice() {
		return getStringField(Fields.MAP_ADVICE);
	}
	
	public String getMapCategoryId() {
		return StringUtils.valueOfOrEmptyString(getLongField(Fields.MAP_CATEGORY_ID));
	}
	
	public String getCorrelationId() {
		return StringUtils.valueOfOrEmptyString(getLongField(Fields.CORRELATION_ID));
	}

	public String getMapTargetDescription() {
		return getStringField(Fields.MAP_TARGET_DESCRIPTION);
	}
	
	public String getQuery() {
		return getStringField(Fields.QUERY);
	}
	
	public String getTargetComponentId() {
		return getStringField(Fields.TARGET_COMPONENT);
	}

	public RelationshipRefinability getRefinability() {
		return RelationshipRefinability.getByConceptId(getValueId());
	}
	
	public InactivationIndicator getInactivationIndicator() {
		return InactivationIndicator.getByConceptId(getValueId());
	}

	public String getValueId() {
		return getStringField(Fields.VALUE_ID);
	}

	@Deprecated
	public String getSpecialFieldLabel() {
		throw new UnsupportedOperationException("Special field label needs to be computed separately.");
	}

}
