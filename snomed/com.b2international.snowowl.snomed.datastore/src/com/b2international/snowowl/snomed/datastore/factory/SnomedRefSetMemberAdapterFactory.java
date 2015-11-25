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

import org.eclipse.emf.spi.cdo.FSMUtil;

import com.b2international.commons.TypeSafeAdapterFactory;
import com.b2international.snowowl.core.api.IComponent;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.cdo.CDOIDUtils;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetMemberLookupService;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetUtil;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry.Builder;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedMappings;
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
			final SnomedRefSetMemberIndexEntry refSetMemberIndexEntry;

			if (FSMUtil.isClean(refSetMember) && !refSetMember.cdoRevision().isHistorical()) {
				refSetMemberIndexEntry = new SnomedRefSetMemberLookupService().getComponent(BranchPathUtils.createPath(refSetMember), refSetMember.getUuid());
			} else {
				refSetMemberIndexEntry = createIndexEntry(refSetMember);
			}

			return adapterType.cast(refSetMemberIndexEntry);
		}

		return null;
	}

	private SnomedRefSetMemberIndexEntry createIndexEntry(final SnomedRefSetMember refSetMember) {

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
				return builder.additionalField(SnomedMappings.memberTargetComponentId().fieldName(), associationMember.getTargetComponentId());
			}

			@Override
			public Builder caseSnomedAttributeValueRefSetMember(final SnomedAttributeValueRefSetMember attributeValueMember) {
				return builder.additionalField(SnomedMappings.memberValueId().fieldName(), attributeValueMember.getValueId());
			}

			@Override
			public Builder caseSnomedConcreteDataTypeRefSetMember(final SnomedConcreteDataTypeRefSetMember concreteDataTypeMember) {
				builder.additionalField(SnomedMappings.memberDataTypeLabel().fieldName(), concreteDataTypeMember.getLabel())
						.additionalField(SnomedMappings.memberDataTypeOrdinal().fieldName(), concreteDataTypeMember.getDataType().ordinal())
						.additionalField(SnomedMappings.memberSerializedValue().fieldName(), SnomedRefSetUtil.deserializeValue(
							concreteDataTypeMember.getDataType(), 
							concreteDataTypeMember.getSerializedValue()))
						.additionalField(SnomedMappings.memberCharacteristicTypeId().fieldName(), concreteDataTypeMember.getCharacteristicTypeId())
						.additionalField(SnomedMappings.memberOperatorId().fieldName(), concreteDataTypeMember.getOperatorComponentId());

				if (concreteDataTypeMember.getUomComponentId() != null) {
					builder.additionalField(SnomedMappings.memberUomId().fieldName(), concreteDataTypeMember.getUomComponentId());
				}

				return builder;
			}

			@Override
			public Builder caseSnomedDescriptionTypeRefSetMember(final SnomedDescriptionTypeRefSetMember descriptionTypeMember) {
				return builder
						.additionalField(SnomedMappings.memberDescriptionFormatId().fieldName(), descriptionTypeMember.getDescriptionFormat())
						.additionalField(SnomedMappings.memberDescriptionLength().fieldName(), descriptionTypeMember.getDescriptionLength());
			}

			@Override
			public Builder caseSnomedLanguageRefSetMember(final SnomedLanguageRefSetMember languageMember) {
				return builder.additionalField(SnomedMappings.memberAcceptabilityId().fieldName(), languageMember.getAcceptabilityId());
			}

			@Override
			public Builder caseSnomedModuleDependencyRefSetMember(final SnomedModuleDependencyRefSetMember moduleDependencyMember) {
				return builder
						.additionalField(SnomedMappings.memberSourceEffectiveTime().fieldName(), EffectiveTimes.getEffectiveTime(moduleDependencyMember.getSourceEffectiveTime()))
						.additionalField(SnomedMappings.memberTargetEffectiveTime().fieldName(), EffectiveTimes.getEffectiveTime(moduleDependencyMember.getTargetEffectiveTime()));
			}

			@Override
			public Builder caseSnomedQueryRefSetMember(final SnomedQueryRefSetMember queryMember) {
				return builder.additionalField(SnomedMappings.memberQuery().fieldName(), queryMember.getQuery());
			}

			@Override
			public Builder caseSnomedSimpleMapRefSetMember(final SnomedSimpleMapRefSetMember mapRefSetMember) {
				builder.mapTargetComponentType(mapRefSetMember.getMapTargetComponentType());
				builder.additionalField(SnomedMappings.memberMapTargetComponentId().fieldName(), mapRefSetMember.getMapTargetComponentId());

				if (mapRefSetMember.getMapTargetComponentDescription() != null) {
					builder.additionalField(SnomedMappings.memberMapTargetComponentDescription().fieldName(), mapRefSetMember.getMapTargetComponentDescription());
				}

				return builder;
			}

		}.doSwitch(refSetMember);

		return specializedBuilder.build();
	}
}
