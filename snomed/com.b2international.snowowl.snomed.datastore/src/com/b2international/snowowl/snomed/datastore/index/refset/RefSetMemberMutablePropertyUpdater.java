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

import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.datastore.index.DocumentUpdaterBase;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedDocumentBuilder;
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
			.module(Long.valueOf(member.getModuleId()))
			.effectiveTime(member.isSetEffectiveTime() ? member.getEffectiveTime().getTime() : EffectiveTimes.UNSET_EFFECTIVE_TIME)
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
			doc.memberTargetComponentId(associationMember.getTargetComponentId());
			break;
		case ATTRIBUTE_VALUE:
			//set the member value ID. Again, it's always a SNOMED CT concept
			final SnomedAttributeValueRefSetMember attributeValueMember = (SnomedAttributeValueRefSetMember) member;
			doc.memberValueId(attributeValueMember.getValueId());
			break;
		case QUERY:
			//set the ESCG query from the member
			final SnomedQueryRefSetMember queryMember = (SnomedQueryRefSetMember) member;
			doc.memberQuery(queryMember.getQuery().trim());
			break;
		case EXTENDED_MAP: //$FALL-THROUGH$
		case COMPLEX_MAP:
			//cast member to complex map and set complex map properties to the document
			final SnomedComplexMapRefSetMember complexMember = (SnomedComplexMapRefSetMember) member;
			doc.memberMapGroup(Integer.valueOf(complexMember.getMapGroup()));
			doc.memberMapPriority(Integer.valueOf(complexMember.getMapPriority()));
			if (null != complexMember.getMapRule()) {
				doc.memberMapRule(complexMember.getMapRule());
			}
			if (null != complexMember.getMapAdvice()) {
				doc.memberMapAdvice(complexMember.getMapAdvice());
			}
			if (null != complexMember.getMapCategoryId()) {
				doc.memberMapCategoryId(Long.valueOf(complexMember.getMapCategoryId()));
			}
			doc.memberCorrelationId(Long.valueOf(complexMember.getCorrelationId()));
			
			final String complexMapTargetComponentId = complexMember.getMapTargetComponentId();
			final short complexMapTargetComponentType = complexMember.getMapTargetComponentType();
			
			doc.memberMapTargetComponentId(complexMapTargetComponentId);
			doc.memberMapTargetComponentType(Integer.valueOf(complexMapTargetComponentType));
			break;
			
		case DESCRIPTION_TYPE:
			//set description type ID, label and description length
			final SnomedDescriptionTypeRefSetMember descriptionMember = (SnomedDescriptionTypeRefSetMember) member;
			doc.memberDescriptionFormatId(Long.valueOf(descriptionMember.getDescriptionFormat()));
			doc.memberDescriptionLength(descriptionMember.getDescriptionLength());
			break;
			
		case LANGUAGE:
			//set description acceptability label and ID
			final SnomedLanguageRefSetMember languageMember = (SnomedLanguageRefSetMember) member;
			doc.memberAcceptabilityId(Long.valueOf(languageMember.getAcceptabilityId()));
			break;
			
		case CONCRETE_DATA_TYPE:
			
			//set operator ID, serialized value, UOM ID (if any) and characteristic type ID
			final SnomedConcreteDataTypeRefSetMember dataTypeMember = (SnomedConcreteDataTypeRefSetMember) member;
			doc.memberOperatorId(Long.valueOf(dataTypeMember.getOperatorComponentId()));
			if (!Strings.isNullOrEmpty(dataTypeMember.getUomComponentId())) {
				doc.memberUomId(Long.valueOf(dataTypeMember.getUomComponentId()));
			}
			
			if (null != dataTypeMember.getCharacteristicTypeId()) {
				doc.memberCharacteristicTypeId(Long.valueOf(dataTypeMember.getCharacteristicTypeId()));
			}
			
			// XXX For future reference: the indexed datatype (and it's ordinal) has changed from com.b2international.snowowl.snomed.mrcm.DataType to
			// com.b2international.snowowl.snomed.snomedrefset.DataType in
			// https://github.com/b2ihealthcare/snow-owl/commit/ba20dd14f65eadd537b2b09b4cb30dc5c44eaea8
			
			doc.memberDataType(dataTypeMember.getDataType());
			doc.memberSerializedValue(dataTypeMember.getSerializedValue());
			doc.memberDataTypeLabel(dataTypeMember.getLabel());
			
			break;
			
		case SIMPLE_MAP:
			//set map target ID, type and label
			final SnomedSimpleMapRefSetMember mapMember = (SnomedSimpleMapRefSetMember) member;
			final String simpleMapTargetComponentId = mapMember.getMapTargetComponentId();
			final short simpleMapTargetComponentType = mapMember.getMapTargetComponentType();
			
			doc.memberMapTargetComponentId(simpleMapTargetComponentId);
			doc.memberMapTargetComponentType(Integer.valueOf(simpleMapTargetComponentType));
			
			final String componentDescription = mapMember.getMapTargetComponentDescription();
			if (null != componentDescription) {
				doc.memberMapTargetComponentDescription(componentDescription);
			}
			break;
		case MODULE_DEPENDENCY:
			final SnomedModuleDependencyRefSetMember dependencyMember = (SnomedModuleDependencyRefSetMember) member;
			doc.memberSourceEffectiveTime(EffectiveTimes.getEffectiveTime(dependencyMember.getSourceEffectiveTime()));
			doc.memberTargetEffectiveTime(EffectiveTimes.getEffectiveTime(dependencyMember.getTargetEffectiveTime()));
			break;
		default: throw new IllegalArgumentException("Unknown SNOMED CT reference set type: " + member.getRefSet().getType());
		}
	}
}
