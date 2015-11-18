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

import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_ACCEPTABILITY_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_CHARACTERISTIC_TYPE_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_CONTAINER_MODULE_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_CORRELATION_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_DATA_TYPE_VALUE;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_DESCRIPTION_FORMAT_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_DESCRIPTION_LENGTH;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_MAP_ADVICE;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_MAP_CATEGORY_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_MAP_GROUP;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_MAP_PRIORITY;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_MAP_RULE;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_MAP_TARGET_COMPONENT_DESCRIPTION;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_MAP_TARGET_COMPONENT_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_MAP_TARGET_COMPONENT_TYPE_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_OPERATOR_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_QUERY;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_SERIALIZED_VALUE;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_SOURCE_EFFECTIVE_TIME;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_TARGET_COMPONENT_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_TARGET_EFFECTIVE_TIME;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_UOM_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_VALUE_ID;

import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.datastore.index.DocumentUpdaterBase;
import com.b2international.snowowl.datastore.index.mapping.Mappings;
import com.b2international.snowowl.snomed.Component;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetUtil;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedDocumentBuilder;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedMappings;
import com.b2international.snowowl.snomed.mrcm.DataType;
import com.b2international.snowowl.snomed.snomedrefset.SnomedAssociationRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedAttributeValueRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedComplexMapRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedConcreteDataTypeRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedDescriptionTypeRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedLanguageRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedModuleDependencyRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedQueryRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedSimpleMapRefSetMember;
import com.google.common.base.Strings;

/**
 * @since 4.3
 */
public class RefSetMemberMutablePropertyUpdater extends DocumentUpdaterBase<SnomedDocumentBuilder> {

	private SnomedRefSetMember member;

	public RefSetMemberMutablePropertyUpdater(SnomedRefSetMember member) {
		super(member.getUuid());
		this.member = member;
	}

	@Override
	public void doUpdate(SnomedDocumentBuilder doc) {
		doc
			.active(member.isActive())
			.module(member.getModuleId())
			.update(SnomedMappings.effectiveTime(), member.isSetEffectiveTime() ? member.getEffectiveTime().getTime() : EffectiveTimes.UNSET_EFFECTIVE_TIME)
			.released(member.isReleased());
		updateSpecialFields(doc);
	}

	private void updateSpecialFields(SnomedDocumentBuilder doc) {
		switch (member.getRefSet().getType()) {
		case SIMPLE: 
			//nothing else to do
			break;
		case ASSOCIATION:
			//set the target component ID. It's always a SNOMED CT concept
			final SnomedAssociationRefSetMember associationMember = (SnomedAssociationRefSetMember) member;
			doc.update(REFERENCE_SET_MEMBER_TARGET_COMPONENT_ID, associationMember.getTargetComponentId());
			break;
		case ATTRIBUTE_VALUE:
			//set the member value ID. Again, it's always a SNOMED CT concept
			final SnomedAttributeValueRefSetMember attributeValueMember = (SnomedAttributeValueRefSetMember) member;
			doc.update(REFERENCE_SET_MEMBER_VALUE_ID, attributeValueMember.getValueId());
			break;
		case QUERY:
			//set the ESCG query from the member
			final SnomedQueryRefSetMember queryMember = (SnomedQueryRefSetMember) member;
			doc.update(REFERENCE_SET_MEMBER_QUERY, queryMember.getQuery().trim());
			break;
		case EXTENDED_MAP: //$FALL-THROUGH$
		case COMPLEX_MAP:
			//cast member to complex map and set complex map properties to the document
			final SnomedComplexMapRefSetMember complexMember = (SnomedComplexMapRefSetMember) member;
			doc.update(Mappings.storedOnlyIntField(REFERENCE_SET_MEMBER_MAP_GROUP), (int) complexMember.getMapGroup());
			doc.update(Mappings.storedOnlyIntField(REFERENCE_SET_MEMBER_MAP_PRIORITY), (int) complexMember.getMapPriority());
			if (null != complexMember.getMapRule()) {
				doc.update(REFERENCE_SET_MEMBER_MAP_RULE, complexMember.getMapRule());
			}
			if (null != complexMember.getMapAdvice()) {
				doc.update(REFERENCE_SET_MEMBER_MAP_ADVICE, complexMember.getMapAdvice());
			}
			if (null != complexMember.getMapCategoryId()) {
				doc.update(REFERENCE_SET_MEMBER_MAP_CATEGORY_ID, Long.valueOf(complexMember.getMapCategoryId()));
			}
			doc.update(REFERENCE_SET_MEMBER_CORRELATION_ID, Long.valueOf(complexMember.getCorrelationId()));
			
			final String complexMapTargetComponentId = complexMember.getMapTargetComponentId();
			final short complexMapTargetComponentType = complexMember.getMapTargetComponentType();
			
			doc.update(REFERENCE_SET_MEMBER_MAP_TARGET_COMPONENT_ID, complexMapTargetComponentId);
			doc.update(REFERENCE_SET_MEMBER_MAP_TARGET_COMPONENT_TYPE_ID, (int) complexMapTargetComponentType);
			break;
			
		case DESCRIPTION_TYPE:
			//set description type ID, label and description length
			final SnomedDescriptionTypeRefSetMember descriptionMember = (SnomedDescriptionTypeRefSetMember) member;
			doc.update(REFERENCE_SET_MEMBER_DESCRIPTION_FORMAT_ID, Long.valueOf(descriptionMember.getDescriptionFormat()));
			doc.update(Mappings.storedOnlyIntField(REFERENCE_SET_MEMBER_DESCRIPTION_LENGTH), descriptionMember.getDescriptionLength());
			break;
			
		case LANGUAGE:
			//set description acceptability label and ID
			final SnomedLanguageRefSetMember languageMember = (SnomedLanguageRefSetMember) member;
			doc.update(REFERENCE_SET_MEMBER_ACCEPTABILITY_ID, Long.valueOf(languageMember.getAcceptabilityId()));
			break;
			
		case CONCRETE_DATA_TYPE:
			
			//set operator ID, serialized value, UOM ID (if any) and characteristic type ID
			final SnomedConcreteDataTypeRefSetMember dataTypeMember = (SnomedConcreteDataTypeRefSetMember) member;
			doc.update(REFERENCE_SET_MEMBER_OPERATOR_ID, Long.valueOf(dataTypeMember.getOperatorComponentId()));
			if (!Strings.isNullOrEmpty(dataTypeMember.getUomComponentId())) {
				doc.update(Mappings.longDocValuesField(REFERENCE_SET_MEMBER_UOM_ID), Long.valueOf(dataTypeMember.getUomComponentId()));
			}
			
			if (null != dataTypeMember.getCharacteristicTypeId()) {
				doc.update(REFERENCE_SET_MEMBER_CHARACTERISTIC_TYPE_ID, Long.valueOf(dataTypeMember.getCharacteristicTypeId()));
			}
			
			final DataType dataType = SnomedRefSetUtil.getDataType(member.getRefSetIdentifierId());
			doc.update(Mappings.intDocValuesField(REFERENCE_SET_MEMBER_DATA_TYPE_VALUE), dataType.ordinal());
			doc.update(Mappings.stringDocValuesField(REFERENCE_SET_MEMBER_SERIALIZED_VALUE), dataTypeMember.getSerializedValue());
			
			if (member.eContainer() instanceof Component) {
				final String containerModuleId = ((Component) member.eContainer()).getModule().getId();
				doc.update(Mappings.longDocValuesField(REFERENCE_SET_MEMBER_CONTAINER_MODULE_ID), Long.valueOf(containerModuleId));	
			}
			break;
			
		case SIMPLE_MAP:
			//set map target ID, type and label
			final SnomedSimpleMapRefSetMember mapMember = (SnomedSimpleMapRefSetMember) member;
			final String simpleMapTargetComponentId = mapMember.getMapTargetComponentId();
			final short simpleMapTargetComponentType = mapMember.getMapTargetComponentType();
			
			doc.update(REFERENCE_SET_MEMBER_MAP_TARGET_COMPONENT_ID, simpleMapTargetComponentId);
			doc.update(REFERENCE_SET_MEMBER_MAP_TARGET_COMPONENT_TYPE_ID, (int) simpleMapTargetComponentType);
			
			final String componentDescription = mapMember.getMapTargetComponentDescription();
			if (null != componentDescription) {
				doc.update(Mappings.textField(REFERENCE_SET_MEMBER_MAP_TARGET_COMPONENT_DESCRIPTION), componentDescription);
			}
			break;
		case MODULE_DEPENDENCY:
			final SnomedModuleDependencyRefSetMember dependencyMember = (SnomedModuleDependencyRefSetMember) member;
			doc.update(REFERENCE_SET_MEMBER_SOURCE_EFFECTIVE_TIME, EffectiveTimes.getEffectiveTime(dependencyMember.getSourceEffectiveTime()));
			doc.update(REFERENCE_SET_MEMBER_TARGET_EFFECTIVE_TIME, EffectiveTimes.getEffectiveTime(dependencyMember.getTargetEffectiveTime()));
			break;
		default: throw new IllegalArgumentException("Unknown SNOMED CT reference set type: " + member.getRefSet().getType());
		}
	}
}
