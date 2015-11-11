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
package com.b2international.snowowl.snomed.datastore.factory;

import com.b2international.commons.TypeSafeAdapterFactory;
import com.b2international.snowowl.core.api.IComponent;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.datastore.cdo.CDOIDUtils;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetUtil;
import com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry.Builder;
import com.b2international.snowowl.snomed.snomedrefset.SnomedAssociationRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedAttributeValueRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedConcreteDataTypeRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedDescriptionTypeRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedLanguageRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedModuleDependencyRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedQueryRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedSimpleMapRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.util.SnomedRefSetSwitch;

/**
 * Adapter factory implementation for SNOMED CT reference set members.
 */
public class SnomedRefSetMemberAdapterFactory extends TypeSafeAdapterFactory {

	public SnomedRefSetMemberAdapterFactory() {
		super(IComponent.class, SnomedRefSetMemberIndexEntry.class);
	}

	@Override
	protected <T> T getAdapterSafe(final Object adaptableObject, final Class<T> adapterType) {

		if (adaptableObject instanceof SnomedRefSetMemberIndexEntry) {
			return adapterType.cast(adaptableObject);
		} 

		if (adaptableObject instanceof SnomedRefSetMember) {
			final SnomedRefSetMember refSetMember = (SnomedRefSetMember) adaptableObject;
			final Builder builder = SnomedRefSetMemberIndexEntry.builder()
					.id(refSetMember.getUuid()) 
					.moduleId(refSetMember.getModuleId())
					.storageKey(CDOIDUtils.asLongSafe(refSetMember.cdoID()))
					.active(refSetMember.isActive())
					.released(refSetMember.isReleased())
					.effectiveTimeLong(refSetMember.isSetEffectiveTime() ? refSetMember.getEffectiveTime().getTime() : EffectiveTimes.UNSET_EFFECTIVE_TIME)
					.referenceSetId(refSetMember.getRefSetIdentifierId())
					.referenceSetType(refSetMember.getRefSet().getType())
					.referencedComponentType(refSetMember.getReferencedComponentType())
					.referencedComponentId(refSetMember.getReferencedComponentId());

			final Builder specializedBuilder = new SnomedRefSetSwitch<Builder>() {

				@Override
				public Builder caseSnomedAssociationRefSetMember(final SnomedAssociationRefSetMember associationMember) {
					return builder.additionalField(SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_TARGET_COMPONENT_ID, associationMember.getTargetComponentId());
				}

				@Override
				public Builder caseSnomedAttributeValueRefSetMember(final SnomedAttributeValueRefSetMember attributeValueMember) {
					return builder.additionalField(SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_VALUE_ID, attributeValueMember.getValueId());
				}

				@Override
				public Builder caseSnomedConcreteDataTypeRefSetMember(final SnomedConcreteDataTypeRefSetMember concreteDataTypeMember) {
					builder.additionalField(SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_DATA_TYPE_LABEL, concreteDataTypeMember.getLabel())
							.additionalField(SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_DATA_TYPE_VALUE, SnomedRefSetUtil.deserializeValue(
									concreteDataTypeMember.getDataType(), 
									concreteDataTypeMember.getSerializedValue()))
							.additionalField(SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_CHARACTERISTIC_TYPE_ID, concreteDataTypeMember.getCharacteristicTypeId())
							.additionalField(SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_OPERATOR_ID, concreteDataTypeMember.getOperatorComponentId());

					if (concreteDataTypeMember.getUomComponentId() != null) {
						builder.additionalField(SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_UOM_ID, concreteDataTypeMember.getUomComponentId());
					}
					
					return builder;
				}
				
				@Override
				public Builder caseSnomedDescriptionTypeRefSetMember(final SnomedDescriptionTypeRefSetMember descriptionTypeMember) {
					return builder.additionalField(SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_DESCRIPTION_FORMAT_ID, descriptionTypeMember.getDescriptionFormat())
							.additionalField(SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_DESCRIPTION_LENGTH, descriptionTypeMember.getDescriptionLength());
				}
				
				@Override
				public Builder caseSnomedLanguageRefSetMember(final SnomedLanguageRefSetMember languageMember) {
					return builder.additionalField(SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_ACCEPTABILITY_ID, languageMember.getAcceptabilityId());
				}
				
				@Override
				public Builder caseSnomedModuleDependencyRefSetMember(final SnomedModuleDependencyRefSetMember moduleDependencyMember) {
					return builder.additionalField(SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_SOURCE_EFFECTIVE_TIME, EffectiveTimes.getEffectiveTime(moduleDependencyMember.getSourceEffectiveTime()))
							.additionalField(SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_TARGET_EFFECTIVE_TIME, EffectiveTimes.getEffectiveTime(moduleDependencyMember.getTargetEffectiveTime()));
				}
				
				@Override
				public Builder caseSnomedQueryRefSetMember(final SnomedQueryRefSetMember queryMember) {
					return builder.additionalField(SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_QUERY, queryMember.getQuery());
				}

				@Override
				public Builder caseSnomedSimpleMapRefSetMember(final SnomedSimpleMapRefSetMember mapRefSetMember) {
					builder.mapTargetComponentType(mapRefSetMember.getMapTargetComponentType());
					builder.additionalField(SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_MAP_TARGET_COMPONENT_ID, mapRefSetMember.getMapTargetComponentId());

					if (mapRefSetMember.getMapTargetComponentDescription() != null) {
						builder.additionalField(SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_MAP_TARGET_COMPONENT_DESCRIPTION, mapRefSetMember.getMapTargetComponentDescription());
					}

					return builder;
				}

			}.doSwitch(refSetMember);

			return adapterType.cast(specializedBuilder.build());
		}

		return null;
	}
}
