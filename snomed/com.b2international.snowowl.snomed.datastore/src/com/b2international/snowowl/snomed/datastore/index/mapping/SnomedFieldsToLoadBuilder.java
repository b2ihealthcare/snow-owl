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
package com.b2international.snowowl.snomed.datastore.index.mapping;

import com.b2international.snowowl.datastore.index.mapping.FieldsToLoadBuilderBase;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;

/**
 * @since 4.3
 */
public class SnomedFieldsToLoadBuilder extends FieldsToLoadBuilderBase<SnomedFieldsToLoadBuilder> {

	protected SnomedFieldsToLoadBuilder() {
		super();
	}
	
	@Override
	@Deprecated
	public SnomedFieldsToLoadBuilder label() {
		throw new UnsupportedOperationException("Label field can not be retrieved for SNOMED CT documents.");
	}

	public SnomedFieldsToLoadBuilder statedParent() {
		return field(SnomedMappings.parent(Concepts.STATED_RELATIONSHIP));
	}

	public SnomedFieldsToLoadBuilder statedAncestor() {
		return field(SnomedMappings.ancestor(Concepts.STATED_RELATIONSHIP));
	}

	public SnomedFieldsToLoadBuilder module() {
		return field(SnomedMappings.module());
	}

	public SnomedFieldsToLoadBuilder effectiveTime() {
		return field(SnomedMappings.effectiveTime());
	}

	public SnomedFieldsToLoadBuilder active() {
		return field(SnomedMappings.active());
	}

	public SnomedFieldsToLoadBuilder released() {
		return field(SnomedMappings.released());
	}

	public SnomedFieldsToLoadBuilder componentReferringPredicate() {
		return field(SnomedMappings.componentReferringPredicate());
	}

	public SnomedFieldsToLoadBuilder primitive() {
		return field(SnomedMappings.primitive());
	}

	public SnomedFieldsToLoadBuilder exhaustive() {
		return field(SnomedMappings.exhaustive());
	}

	public SnomedFieldsToLoadBuilder conceptDegreeOfInterest() {
		return field(SnomedMappings.conceptDegreeOfInterest());
	}

	public SnomedFieldsToLoadBuilder conceptReferringRefSetId() {
		return field(SnomedMappings.conceptReferringRefSetId());
	}

	public SnomedFieldsToLoadBuilder conceptReferringMappingRefSetId() {
		return field(SnomedMappings.conceptReferringMappingRefSetId());
	}

	public SnomedFieldsToLoadBuilder conceptNamespaceId() {
		return field(SnomedMappings.conceptNamespaceId());
	}

	public SnomedFieldsToLoadBuilder descriptionType() {
		return field(SnomedMappings.descriptionType());
	}

	public SnomedFieldsToLoadBuilder descriptionTerm() {
		return field(SnomedMappings.descriptionTerm());
	}
	
	public SnomedFieldsToLoadBuilder descriptionLanguageCode() {
		return field(SnomedMappings.descriptionLanguageCode());
	}
	
	public SnomedFieldsToLoadBuilder descriptionPreferredReferenceSetId() {
		return field(SnomedMappings.descriptionPreferredReferenceSetId());
	}
	
	public SnomedFieldsToLoadBuilder descriptionAcceptableReferenceSetId() {
		return field(SnomedMappings.descriptionAcceptableReferenceSetId());
	}
	
	public SnomedFieldsToLoadBuilder descriptionConcept() {
		return field(SnomedMappings.descriptionConcept());
	}

	public SnomedFieldsToLoadBuilder descriptionCaseSignificance() {
		return field(SnomedMappings.descriptionCaseSignificance());
	}

	public SnomedFieldsToLoadBuilder relationshipSource() {
		return field(SnomedMappings.relationshipSource());
	}

	public SnomedFieldsToLoadBuilder relationshipType() {
		return field(SnomedMappings.relationshipType());
	}

	public SnomedFieldsToLoadBuilder relationshipDestination() {
		return field(SnomedMappings.relationshipDestination());
	}

	public SnomedFieldsToLoadBuilder relationshipGroup() {
		return field(SnomedMappings.relationshipGroup());
	}

	public SnomedFieldsToLoadBuilder relationshipUnionGroup() {
		return field(SnomedMappings.relationshipUnionGroup());
	}

	public SnomedFieldsToLoadBuilder relationshipInferred() {
		return field(SnomedMappings.relationshipInferred());
	}

	public SnomedFieldsToLoadBuilder relationshipUniversal() {
		return field(SnomedMappings.relationshipUniversal());
	}

	public SnomedFieldsToLoadBuilder relationshipDestinationNegated() {
		return field(SnomedMappings.relationshipDestinationNegated());
	}

	public SnomedFieldsToLoadBuilder relationshipCharacteristicType() {
		return field(SnomedMappings.relationshipCharacteristicType());
	}

	public SnomedFieldsToLoadBuilder predicateType() {
		return field(SnomedMappings.predicateType());
	}

	public SnomedFieldsToLoadBuilder predicateDescriptionTypeId() {
		return field(SnomedMappings.predicateDescriptionTypeId());
	}

	public SnomedFieldsToLoadBuilder predicateDataTypeLabel() {
		return field(SnomedMappings.predicateDataTypeLabel());
	}

	public SnomedFieldsToLoadBuilder predicateDataTypeName() {
		return field(SnomedMappings.predicateDataTypeName());
	}

	public SnomedFieldsToLoadBuilder predicateDataType() {
		return field(SnomedMappings.predicateDataType());
	}

	public SnomedFieldsToLoadBuilder predicateRelationshipTypeExpression() {
		return field(SnomedMappings.predicateRelationshipTypeExpression());
	}

	public SnomedFieldsToLoadBuilder predicateRelationshipValueExpression() {
		return field(SnomedMappings.predicateRelationshipValueExpression());
	}

	public SnomedFieldsToLoadBuilder predicateCharacteristicTypeExpression() {
		return field(SnomedMappings.predicateCharacteristicTypeExpression());
	}

	public SnomedFieldsToLoadBuilder predicateGroupRule() {
		return field(SnomedMappings.predicateGroupRule());
	}

	public SnomedFieldsToLoadBuilder predicateQueryExpression() {
		return field(SnomedMappings.predicateQueryExpression());
	}

	public SnomedFieldsToLoadBuilder predicateRequired() {
		return field(SnomedMappings.predicateRequired());
	}

	public SnomedFieldsToLoadBuilder predicateMultiple() {
		return field(SnomedMappings.predicateMultiple());
	}

	public SnomedFieldsToLoadBuilder refSetType() {
		return field(SnomedMappings.refSetType());
	}

	public SnomedFieldsToLoadBuilder refSetReferencedComponentType() {
		return field(SnomedMappings.refSetReferencedComponentType());
	}

	public SnomedFieldsToLoadBuilder refSetStructural() {
		return field(SnomedMappings.refSetStructural());
	}

	public SnomedFieldsToLoadBuilder refSetStorageKey() {
		return field(SnomedMappings.refSetStorageKey());
	}

	public SnomedFieldsToLoadBuilder memberUuid() {
		return field(SnomedMappings.memberUuid());
	}

	public SnomedFieldsToLoadBuilder memberRefSetId() {
		return field(SnomedMappings.memberRefSetId());
	}

	public SnomedFieldsToLoadBuilder memberRefSetType() {
		return field(SnomedMappings.memberRefSetType());
	}

	public SnomedFieldsToLoadBuilder memberReferencedComponentId() {
		return field(SnomedMappings.memberReferencedComponentId());
	}

	public SnomedFieldsToLoadBuilder memberReferencedComponentType() {
		return field(SnomedMappings.memberReferencedComponentType());
	}

	public SnomedFieldsToLoadBuilder memberTargetComponentId() {
		return field(SnomedMappings.memberTargetComponentId());
	}

	public SnomedFieldsToLoadBuilder memberValueId() {
		return field(SnomedMappings.memberValueId());
	}

	public SnomedFieldsToLoadBuilder memberQuery() {
		return field(SnomedMappings.memberQuery());
	}

	public SnomedFieldsToLoadBuilder memberMapTargetComponentId() {
		return field(SnomedMappings.memberMapTargetComponentId());
	}

	public SnomedFieldsToLoadBuilder memberMapTargetComponentType() {
		return field(SnomedMappings.memberMapTargetComponentType());
	}

	public SnomedFieldsToLoadBuilder memberMapTargetComponentDescription() {
		return field(SnomedMappings.memberMapTargetComponentDescription());
	}

	public SnomedFieldsToLoadBuilder memberMapGroup() {
		return field(SnomedMappings.memberMapGroup());
	}

	public SnomedFieldsToLoadBuilder memberMapPriority() {
		return field(SnomedMappings.memberMapPriority());
	}

	public SnomedFieldsToLoadBuilder memberMapRule() {
		return field(SnomedMappings.memberMapRule());
	}

	public SnomedFieldsToLoadBuilder memberMapAdvice() {
		return field(SnomedMappings.memberMapAdvice());
	}

	public SnomedFieldsToLoadBuilder memberMapCategoryId() {
		return field(SnomedMappings.memberMapCategoryId());
	}

	public SnomedFieldsToLoadBuilder memberCorrelationId() {
		return field(SnomedMappings.memberCorrelationId());
	}

	public SnomedFieldsToLoadBuilder memberDescriptionFormatId() {
		return field(SnomedMappings.memberDescriptionFormatId());
	}

	public SnomedFieldsToLoadBuilder memberDescriptionLength() {
		return field(SnomedMappings.memberDescriptionLength());
	}

	public SnomedFieldsToLoadBuilder memberAcceptabilityId() {
		return field(SnomedMappings.memberAcceptabilityId());
	}

	public SnomedFieldsToLoadBuilder memberOperatorId() {
		return field(SnomedMappings.memberOperatorId());
	}

	public SnomedFieldsToLoadBuilder memberUomId() {
		return field(SnomedMappings.memberUomId());
	}

	public SnomedFieldsToLoadBuilder memberDataTypeLabel() {
		return field(SnomedMappings.memberDataTypeLabel());
	}

	public SnomedFieldsToLoadBuilder memberDataTypeOrdinal() {
		return field(SnomedMappings.memberDataTypeOrdinal());
	}

	public SnomedFieldsToLoadBuilder memberSerializedValue() {
		return field(SnomedMappings.memberSerializedValue());
	}

	public SnomedFieldsToLoadBuilder memberCharacteristicTypeId() {
		return field(SnomedMappings.memberCharacteristicTypeId());
	}

	public SnomedFieldsToLoadBuilder memberSourceEffectiveTime() {
		return field(SnomedMappings.memberSourceEffectiveTime());
	}

	public SnomedFieldsToLoadBuilder memberTargetEffectiveTime() {
		return field(SnomedMappings.memberTargetEffectiveTime());
	}
}
