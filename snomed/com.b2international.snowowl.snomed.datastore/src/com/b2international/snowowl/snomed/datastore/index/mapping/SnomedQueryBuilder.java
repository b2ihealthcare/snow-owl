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

import com.b2international.commons.BooleanUtils;
import com.b2international.snowowl.datastore.index.mapping.QueryBuilderBase;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.datastore.snor.PredicateIndexEntry.PredicateType;
import com.b2international.snowowl.snomed.snomedrefset.DataType;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;

/**
 * @since 4.3
 */
public class SnomedQueryBuilder extends QueryBuilderBase<SnomedQueryBuilder> {

	protected SnomedQueryBuilder() {
		super();
	}

	@Override
	public final SnomedQueryBuilder id(String value) {
		return id(Long.parseLong(value));
	}

	@Override
	public final SnomedQueryBuilder parent(String value) {
		return parent(Long.parseLong(value));
	}

	@Override
	public final SnomedQueryBuilder ancestor(String value) {
		return ancestor(Long.parseLong(value));
	}
	
	public SnomedQueryBuilder id(Long value) {
		return addToQuery(SnomedMappings.id(), value);
	}
	
	public SnomedQueryBuilder parent(Long value) {
		return addToQuery(SnomedMappings.parent(), value);
	}
	
	public SnomedQueryBuilder statedParent(Long id) {
		return addToQuery(SnomedMappings.statedParent(), id);
	}
	
	public SnomedQueryBuilder statedParent(String id) {
		return statedParent(Long.valueOf(id));
	}
	
	public SnomedQueryBuilder statedAncestor(Long id) {
		return addToQuery(SnomedMappings.statedAncestor(), id);
	}
	
	public SnomedQueryBuilder statedAncestor(String id) {
		return statedAncestor(Long.valueOf(id));
	}
	
	public SnomedQueryBuilder ancestor(Long value) {
		return addToQuery(SnomedMappings.ancestor(), value);
	}
	
	public SnomedQueryBuilder memberRefSetType(SnomedRefSetType type) {
		return memberRefSetType(type.getValue());
	}
	
	public SnomedQueryBuilder memberRefSetType(int type) {
		return addToQuery(SnomedMappings.memberRefSetType(), type);
	}
	
	public SnomedQueryBuilder memberRefSetId(String value) {
		return memberRefSetId(Long.parseLong(value));
	}
	
	public SnomedQueryBuilder memberRefSetId(long value) {
		return addToQuery(SnomedMappings.memberRefSetId(), value);
	}
	
	public SnomedQueryBuilder memberReferencedComponentId(String value) {
		return memberReferencedComponentId(Long.valueOf(value));
	}
	
	public SnomedQueryBuilder memberReferencedComponentId(Long value) {
		return addToQuery(SnomedMappings.memberReferencedComponentId(), value);
	}
	
	public SnomedQueryBuilder memberReferencedComponentType(int value) {
		return addToQuery(SnomedMappings.memberReferencedComponentType(), value);
	}

	public SnomedQueryBuilder active() {
		return addToQuery(SnomedMappings.active(), 1);
	}
	
	public SnomedQueryBuilder inactive() {
		return addToQuery(SnomedMappings.active(), 0);
	}

	public SnomedQueryBuilder descriptionTerm(String value) {
		return addToQuery(SnomedMappings.descriptionTerm(), value);
	}
	
	public SnomedQueryBuilder descriptionType(String value) {
		return descriptionType(Long.valueOf(value));
	}

	public SnomedQueryBuilder descriptionType(Long value) {
		return addToQuery(SnomedMappings.descriptionType(), value);
	}
	
	public SnomedQueryBuilder descriptionLanguageCode(String value) {
		return addToQuery(SnomedMappings.descriptionLanguageCode(), value);
	}
	
	public SnomedQueryBuilder descriptionPreferredReferenceSetId(Long value) {
		return addToQuery(SnomedMappings.descriptionPreferredReferenceSetId(), value);
	}
	
	public SnomedQueryBuilder descriptionAcceptableReferenceSetId(Long value) {
		return addToQuery(SnomedMappings.descriptionAcceptableReferenceSetId(), value);
	}

	public SnomedQueryBuilder descriptionConcept(String value) {
		return descriptionConcept(Long.valueOf(value));
	}
	
	public SnomedQueryBuilder descriptionConcept(Long value) {
		return addToQuery(SnomedMappings.descriptionConcept(), value);
	}

	public SnomedQueryBuilder module(String value) {
		return module(Long.valueOf(value));
	}

	public SnomedQueryBuilder module(Long value) {
		return addToQuery(SnomedMappings.module(), value);
	}

	public SnomedQueryBuilder concept() {
		return type(SnomedTerminologyComponentConstants.CONCEPT_NUMBER);
	}
	
	public SnomedQueryBuilder relationship() {
		return type(SnomedTerminologyComponentConstants.RELATIONSHIP_NUMBER);
	}
	
	public SnomedQueryBuilder description() {
		return type(SnomedTerminologyComponentConstants.DESCRIPTION_NUMBER);
	}
	
	public SnomedQueryBuilder refSet() {
		return type(SnomedTerminologyComponentConstants.REFSET_NUMBER);
	}

	public SnomedQueryBuilder predicate() {
		return type(SnomedTerminologyComponentConstants.PREDICATE_TYPE_ID);
	}

	public SnomedQueryBuilder refSetStorageKey(Long value) {
		return addToQuery(SnomedMappings.refSetStorageKey(), value);
	}

	public SnomedQueryBuilder effectiveTime(Long value) {
		return addToQuery(SnomedMappings.effectiveTime(), value);
	}
	
	public SnomedQueryBuilder iconId(final Long iconId) {
		return addToQuery(SnomedMappings.iconId(), iconId);
	}

	public SnomedQueryBuilder parent(final String fieldNameSuffix, final Long parentId) {
		return addToQuery(SnomedMappings.parent(fieldNameSuffix), parentId);
	}

	public SnomedQueryBuilder ancestor(final String fieldNameSuffix, final Long parentId) {
		return addToQuery(SnomedMappings.ancestor(fieldNameSuffix), parentId);
	}

	public SnomedQueryBuilder active(final boolean active) {
		return addToQuery(SnomedMappings.active(), BooleanUtils.toInteger(active));
	}

	public SnomedQueryBuilder released(final boolean released) {
		return addToQuery(SnomedMappings.released(), BooleanUtils.toInteger(released));
	}

	public SnomedQueryBuilder componentReferringPredicate(final String referringPredicateId) {
		return addToQuery(SnomedMappings.componentReferringPredicate(), referringPredicateId);
	}

	public SnomedQueryBuilder primitive(final boolean primitive) {
		return addToQuery(SnomedMappings.primitive(), BooleanUtils.toInteger(primitive));
	}

	public SnomedQueryBuilder exhaustive(final boolean exhaustive) {
		return addToQuery(SnomedMappings.exhaustive(), BooleanUtils.toInteger(exhaustive));
	}

	public SnomedQueryBuilder conceptDegreeOfInterest(final Float conceptDoi) {
		return addToQuery(SnomedMappings.conceptDegreeOfInterest(), conceptDoi);
	}

	public SnomedQueryBuilder conceptReferringRefSetId(final Long refSetId) {
		return addToQuery(SnomedMappings.conceptReferringRefSetId(), refSetId);
	}

	public SnomedQueryBuilder conceptReferringMappingRefSetId(final Long mappingRefSetId) {
		return addToQuery(SnomedMappings.conceptReferringMappingRefSetId(), mappingRefSetId);
	}

	public SnomedQueryBuilder conceptNamespaceId(final Long namespaceId) {
		return addToQuery(SnomedMappings.conceptNamespaceId(), namespaceId);
	}

	public SnomedQueryBuilder descriptionCaseSignificance(final Long caseSignificanceId) {
		return addToQuery(SnomedMappings.descriptionCaseSignificance(), caseSignificanceId);
	}

	public SnomedQueryBuilder relationshipSource(final String sourceId) {
		return relationshipSource(Long.valueOf(sourceId));
	}
	
	public SnomedQueryBuilder relationshipSource(final Long sourceId) {
		return addToQuery(SnomedMappings.relationshipSource(), sourceId);
	}

	public SnomedQueryBuilder relationshipDestination(final String destinationId) {
		return relationshipDestination(Long.valueOf(destinationId));
	}
	
	public SnomedQueryBuilder relationshipDestination(final Long destinationId) {
		return addToQuery(SnomedMappings.relationshipDestination(), destinationId);
	}

	public SnomedQueryBuilder relationshipType(String value) {
		return relationshipType(Long.valueOf(value));
	}

	public SnomedQueryBuilder relationshipType(Long value) {
		return addToQuery(SnomedMappings.relationshipType(), value);
	}

	public SnomedQueryBuilder relationshipCharacteristicType(String value) {
		return relationshipCharacteristicType(Long.valueOf(value));
	}

	public SnomedQueryBuilder relationshipCharacteristicType(Long value) {
		return addToQuery(SnomedMappings.relationshipCharacteristicType(), value);
	}

	public SnomedQueryBuilder relationshipGroup(final Integer group) {
		return addToQuery(SnomedMappings.relationshipGroup(), group);
	}

	public SnomedQueryBuilder relationshipUnionGroup(final Integer unionGroup) {
		return addToQuery(SnomedMappings.relationshipUnionGroup(), unionGroup);
	}

	public SnomedQueryBuilder relationshipInferred(final boolean inferred) {
		return addToQuery(SnomedMappings.relationshipInferred(), BooleanUtils.toInteger(inferred));
	}

	public SnomedQueryBuilder relationshipUniversal(final boolean universal) {
		return addToQuery(SnomedMappings.relationshipUniversal(), BooleanUtils.toInteger(universal));
	}

	public SnomedQueryBuilder relationshipDestinationNegated(final boolean destinationNegated) {
		return addToQuery(SnomedMappings.relationshipDestinationNegated(), BooleanUtils.toInteger(destinationNegated));
	}

	public SnomedQueryBuilder predicateType(final PredicateType type) {
		return addToQuery(SnomedMappings.predicateType(), type.name());
	}

	public SnomedQueryBuilder predicateDescriptionTypeId(final Long typeId) {
		return addToQuery(SnomedMappings.predicateDescriptionTypeId(), typeId);
	}

	public SnomedQueryBuilder predicateDataTypeLabel(final String label) { // RF2 name, probably
		return addToQuery(SnomedMappings.predicateDataTypeLabel(), label);
	}

	public SnomedQueryBuilder predicateDataTypeName(final String name) { // display name, probably
		return addToQuery(SnomedMappings.predicateDataTypeName(), name);
	}

	public SnomedQueryBuilder predicateDataType(final DataType mrcmDataType) {
		return addToQuery(SnomedMappings.predicateDataType(), mrcmDataType.name());
	}

	public SnomedQueryBuilder predicateRelationshipTypeExpression(final String expression) {
		return addToQuery(SnomedMappings.predicateRelationshipTypeExpression(), expression);
	}

	public SnomedQueryBuilder predicateRelationshipValueExpression(final String expression) {
		return addToQuery(SnomedMappings.predicateRelationshipValueExpression(), expression);
	}

	public SnomedQueryBuilder predicateCharacteristicTypeExpression(final String expression) {
		return addToQuery(SnomedMappings.predicateCharacteristicTypeExpression(), expression);
	}

	public SnomedQueryBuilder predicateGroupRule(final String groupRule) {
		return addToQuery(SnomedMappings.predicateGroupRule(), groupRule);
	}

	public SnomedQueryBuilder predicateQueryExpression(final String expression) {
		return addToQuery(SnomedMappings.predicateQueryExpression(), expression);
	}

	public SnomedQueryBuilder predicateRequired(final boolean required) {
		return addToQuery(SnomedMappings.predicateRequired(), BooleanUtils.toInteger(required));
	}

	public SnomedQueryBuilder predicateMultiple(final boolean multiple) {
		return addToQuery(SnomedMappings.predicateMultiple(), BooleanUtils.toInteger(multiple));
	}

	public SnomedQueryBuilder refSetType(final SnomedRefSetType refSetType) {
		return addToQuery(SnomedMappings.refSetType(), refSetType.ordinal());
	}

	public SnomedQueryBuilder refSetReferencedComponentType(final Integer referencedComponentType) {
		return addToQuery(SnomedMappings.refSetReferencedComponentType(), referencedComponentType);
	}

	public SnomedQueryBuilder refSetStructural(final boolean structural) {
		return addToQuery(SnomedMappings.refSetStructural(), BooleanUtils.toInteger(structural));
	}

	public SnomedQueryBuilder memberUuid(final String uuid) {
		return addToQuery(SnomedMappings.memberUuid(), uuid);
	}

	public SnomedQueryBuilder memberRefSetId(final Long refSetId) {
		return addToQuery(SnomedMappings.memberRefSetId(), refSetId);
	}

	public SnomedQueryBuilder memberReferencedComponentType(final Integer referencedComponentType) {
		return addToQuery(SnomedMappings.memberReferencedComponentType(), referencedComponentType);
	}

	public SnomedQueryBuilder memberTargetComponentId(final String targetComponentId) {
		return addToQuery(SnomedMappings.memberTargetComponentId(), targetComponentId);
	}

	public SnomedQueryBuilder memberValueId(final String valueId) {
		return addToQuery(SnomedMappings.memberValueId(), valueId);
	}

	public SnomedQueryBuilder memberQuery(final String query) {
		return addToQuery(SnomedMappings.memberQuery(), query);
	}

	public SnomedQueryBuilder memberMapTargetComponentId(final String mapTargetComponentId) {
		return addToQuery(SnomedMappings.memberMapTargetComponentId(), mapTargetComponentId);
	}

	public SnomedQueryBuilder memberMapTargetComponentType(final Integer mapTargetComponentType) {
		return addToQuery(SnomedMappings.memberMapTargetComponentType(), mapTargetComponentType);
	}

	public SnomedQueryBuilder memberMapTargetComponentDescription(final String mapTargetComponentDescription) {
		return addToQuery(SnomedMappings.memberMapTargetComponentDescription(), mapTargetComponentDescription);
	}

	public SnomedQueryBuilder memberMapGroup(final Integer mapGroup) {
		return addToQuery(SnomedMappings.memberMapGroup(), mapGroup);
	}

	public SnomedQueryBuilder memberMapPriority(final Integer mapPriority) {
		return addToQuery(SnomedMappings.memberMapPriority(), mapPriority);
	}

	public SnomedQueryBuilder memberMapRule(final String mapRule) {
		return addToQuery(SnomedMappings.memberMapRule(), mapRule);
	}

	public SnomedQueryBuilder memberMapAdvice(final String mapAdvice) {
		return addToQuery(SnomedMappings.memberMapAdvice(), mapAdvice);
	}

	public SnomedQueryBuilder memberMapCategoryId(final Long mapCategoryId) {
		return addToQuery(SnomedMappings.memberMapCategoryId(), mapCategoryId);
	}

	public SnomedQueryBuilder memberCorrelationId(final Long mapCorrelationId) {
		return addToQuery(SnomedMappings.memberCorrelationId(), mapCorrelationId);
	}

	public SnomedQueryBuilder memberDescriptionFormatId(final Long descriptionFormatId) {
		return addToQuery(SnomedMappings.memberDescriptionFormatId(), descriptionFormatId);
	}

	public SnomedQueryBuilder memberDescriptionLength(final Integer descriptionFormatLength) {
		return addToQuery(SnomedMappings.memberDescriptionLength(), descriptionFormatLength);
	}

	public SnomedQueryBuilder memberAcceptabilityId(final Long acceptabilityId) {
		return addToQuery(SnomedMappings.memberAcceptabilityId(), acceptabilityId);
	}

	public SnomedQueryBuilder memberOperatorId(final Long operatorId) {
		return addToQuery(SnomedMappings.memberOperatorId(), operatorId);
	}

	public SnomedQueryBuilder memberUomId(final Long uomId) {
		return addToQuery(SnomedMappings.memberUomId(), uomId);
	}

	public SnomedQueryBuilder memberDataTypeLabel(final String label) {
		return addToQuery(SnomedMappings.memberDataTypeLabel(), label);
	}

	public SnomedQueryBuilder memberDataTypeOrdinal(final com.b2international.snowowl.snomed.snomedrefset.DataType refSetDataType) {
		return addToQuery(SnomedMappings.memberDataTypeOrdinal(), refSetDataType.ordinal());
	}

	public SnomedQueryBuilder memberSerializedValue(final String serializedValue) {
		return addToQuery(SnomedMappings.memberSerializedValue(), serializedValue);
	}

	public SnomedQueryBuilder memberCharacteristicTypeId(final Long characteristicTypeId) {
		return addToQuery(SnomedMappings.memberCharacteristicTypeId(), characteristicTypeId);
	}

	public SnomedQueryBuilder memberSourceEffectiveTime(final Long sourceEffectiveTime) {
		return addToQuery(SnomedMappings.memberSourceEffectiveTime(), sourceEffectiveTime);
	}

	public SnomedQueryBuilder memberTargetEffectiveTime(final Long targetEffectiveTime) {
		return addToQuery(SnomedMappings.memberTargetEffectiveTime(), targetEffectiveTime);
	}

}
