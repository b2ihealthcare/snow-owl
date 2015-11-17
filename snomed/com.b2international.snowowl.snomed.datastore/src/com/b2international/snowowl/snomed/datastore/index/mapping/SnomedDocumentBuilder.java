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

import org.apache.lucene.document.Document;

import com.b2international.commons.BooleanUtils;
import com.b2international.snowowl.datastore.index.mapping.DocumentBuilderBase;
import com.b2international.snowowl.datastore.index.mapping.DocumentBuilderFactory;
import com.b2international.snowowl.datastore.index.mapping.IndexField;
import com.b2international.snowowl.datastore.index.mapping.IntIndexField;
import com.b2international.snowowl.datastore.index.mapping.Mappings;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.datastore.snor.PredicateIndexEntry.PredicateType;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

/**
 * @since 4.3
 */
public final class SnomedDocumentBuilder extends DocumentBuilderBase<SnomedDocumentBuilder> {

	public static class Factory implements DocumentBuilderFactory<SnomedDocumentBuilder> {

		@Override
		public SnomedDocumentBuilder createBuilder() {
			return new SnomedDocumentBuilder();
		}

		@Override
		public SnomedDocumentBuilder createBuilder(Document oldDoc) {
			final IntIndexField typeField = Mappings.type();
			final boolean refSetMember = oldDoc.getField(typeField.fieldName()) == null;

			final Document newDoc = new Document();
			final SnomedDocumentBuilder newDocBuilder = new SnomedDocumentBuilder(newDoc);

			final Builder<IndexField<?>> fieldsToCopy = ImmutableSet.builder();
			fieldsToCopy.add(Mappings.storageKey());
			fieldsToCopy.add(SnomedMappings.module());

			if (refSetMember) {
				addRefSetMemberFields(oldDoc, fieldsToCopy);
			} else {
				addRegularComponentFields(oldDoc, fieldsToCopy, typeField);
			}

			for (IndexField<?> field : fieldsToCopy.build()) {
				field.copyTo(oldDoc, newDoc);
			}

			return newDocBuilder;
		}

		private void addRefSetMemberFields(Document oldDoc, final Builder<IndexField<?>> fieldsToCopy) {

			fieldsToCopy
			.add(SnomedMappings.active())
			.add(SnomedMappings.memberRefSetType())
			.add(SnomedMappings.memberRefSetId())
			.add(SnomedMappings.memberReferencedComponentId())
			.add(SnomedMappings.memberReferencedComponentType())
			.add(SnomedMappings.released())
			.add(SnomedMappings.memberUuid())
			.add(SnomedMappings.effectiveTime());

			final SnomedRefSetType refSetType = SnomedRefSetType.get(SnomedMappings.memberRefSetType().getValue(oldDoc));

			switch (refSetType) {
			case SIMPLE: 
				break;
			case ASSOCIATION:
				fieldsToCopy.add(SnomedMappings.memberTargetComponentId());
				break;
			case ATTRIBUTE_VALUE:
				fieldsToCopy.add(SnomedMappings.memberValueId());
				break;
			case QUERY:
				fieldsToCopy.add(SnomedMappings.memberQuery());
				break;
			case EXTENDED_MAP:
			case COMPLEX_MAP:
				fieldsToCopy
				.add(SnomedMappings.memberMapTargetComponentId())
				.add(SnomedMappings.memberMapTargetComponentType())
				.add(SnomedMappings.memberMapGroup())
				.add(SnomedMappings.memberMapPriority())
				.add(SnomedMappings.memberMapRule())
				.add(SnomedMappings.memberMapAdvice())
				.add(SnomedMappings.memberMapCategoryId())
				.add(SnomedMappings.memberCorrelationId());
				break;
			case DESCRIPTION_TYPE:
				fieldsToCopy
				.add(SnomedMappings.memberDescriptionFormatId())
				.add(SnomedMappings.memberDescriptionLength());
				break;
			case LANGUAGE:
				fieldsToCopy
				.add(SnomedMappings.memberAcceptabilityId());
				break;
			case CONCRETE_DATA_TYPE:
				fieldsToCopy
				.add(SnomedMappings.memberOperatorId())
				.add(SnomedMappings.memberSerializedValue())
				.add(SnomedMappings.memberUomId())
				.add(SnomedMappings.memberCharacteristicTypeId())
				.add(SnomedMappings.memberDataTypeOrdinal())
				.add(SnomedMappings.memberContainerModuleId());
				break;
			case SIMPLE_MAP:
				fieldsToCopy
				.add(SnomedMappings.memberMapTargetComponentId())
				.add(SnomedMappings.memberMapTargetComponentType())
				.add(SnomedMappings.memberMapTargetComponentDescription());
				break;
			case MODULE_DEPENDENCY:
				fieldsToCopy
				.add(SnomedMappings.memberSourceEffectiveTime())
				.add(SnomedMappings.memberTargetEffectiveTime());
				break;
			default: 
				throw new IllegalArgumentException("Unhandled refset type '" + refSetType + "', cannot extract fields from source document.");
			}
		}

		private void addRegularComponentFields(Document oldDoc, final Builder<IndexField<?>> fieldsToCopy, IntIndexField typeField) {

			fieldsToCopy
			.add(SnomedMappings.id())
			.add(SnomedMappings.effectiveTime())
			.add(SnomedMappings.released())
			.add(SnomedMappings.active())
			.add(typeField);

			final short type = typeField.getShortValue(oldDoc);

			switch (type) {
			case SnomedTerminologyComponentConstants.PREDICATE_TYPE_ID:
				// ignored, no other fields are reconstructed from the document
				break;
			case SnomedTerminologyComponentConstants.CONCEPT_NUMBER:
			case SnomedTerminologyComponentConstants.REFSET_NUMBER:
				fieldsToCopy
				.add(SnomedMappings.refSetStorageKey())
				.add(SnomedMappings.ancestor())
				.add(SnomedMappings.parent())
				.add(SnomedMappings.iconId())
				.add(SnomedMappings.ancestor(Concepts.STATED_RELATIONSHIP))
				.add(SnomedMappings.parent(Concepts.STATED_RELATIONSHIP))
				.add(SnomedMappings.conceptReferringRefSetId())
				.add(SnomedMappings.conceptReferringMappingRefSetId())
				.add(SnomedMappings.primitive())
				.add(SnomedMappings.exhaustive())
				.add(SnomedMappings.conceptDegreeOfInterest())
				.add(SnomedMappings.componentReferringPredicate())
				.add(SnomedMappings.refSetType())
				.add(SnomedMappings.refSetReferencedComponentType())
				.add(SnomedMappings.refSetStructural())
				.add(SnomedMappings.conceptNamespaceId()); // FIXME: How dynamic is this field after a concept has been created? See below

				// final ISnomedComponentService componentService = ApplicationContext.getInstance().getService(ISnomedComponentService.class);
				// final long namespaceId = componentService.getExtensionConceptId(BranchPathUtils.createMainPath(), SnomedMappings.id().getValueAsString(oldDoc));
				// Mappings.searchOnlyLongField(SnomedMappings.CONCEPT_NAMESPACE_ID).addTo(newDoc, namespaceId);

				break;
			case SnomedTerminologyComponentConstants.DESCRIPTION_NUMBER:
				fieldsToCopy
				.add(SnomedMappings.descriptionTerm())
				.add(SnomedMappings.descriptionCaseSignificance())
				.add(SnomedMappings.descriptionConcept())
				.add(SnomedMappings.descriptionType());
				break;
			case SnomedTerminologyComponentConstants.RELATIONSHIP_NUMBER:
				fieldsToCopy
				.add(SnomedMappings.relationshipSource())
				.add(SnomedMappings.relationshipType())
				.add(SnomedMappings.relationshipDestination())
				.add(SnomedMappings.relationshipCharacteristicType())
				.add(SnomedMappings.relationshipGroup())
				.add(SnomedMappings.relationshipUnionGroup())
				.add(SnomedMappings.relationshipDestinationNegated())
				.add(SnomedMappings.relationshipInferred())
				.add(SnomedMappings.relationshipUniversal());
				break;
			default:
				throw new IllegalArgumentException("Unhandled component type number '" + type + "', cannot extract fields from source document.");
			}
		}
	}

	protected SnomedDocumentBuilder() {
		super();
	}

	protected SnomedDocumentBuilder(Document doc) {
		super(doc);
	}

	@Override
	protected SnomedDocumentBuilder getSelf() {
		return this;
	}

	// Overridden document builder methods (they use long values in SNOMED CT)

	@Override
	public SnomedDocumentBuilder id(String id) {
		return id(Long.valueOf(id));
	}

	@Override
	public SnomedDocumentBuilder parent(String parentId) {
		return parent(Long.valueOf(parentId));
	}

	@Override
	public SnomedDocumentBuilder ancestor(String ancestorId) {
		return ancestor(Long.valueOf(ancestorId));
	}

	@Override
	public SnomedDocumentBuilder iconId(String iconId) {
		return iconId(Long.valueOf(iconId));
	}

	// Fields that should be added once, when creating the document 

	public SnomedDocumentBuilder id(final Long id) {
		return addToDoc(SnomedMappings.id(), id);
	}

	public SnomedDocumentBuilder parent(final Long parentId) {
		return addToDoc(SnomedMappings.parent(), parentId);
	}

	public SnomedDocumentBuilder ancestor(final Long parentId) {
		return addToDoc(SnomedMappings.ancestor(), parentId);
	}

	public SnomedDocumentBuilder iconId(final Long iconId) {
		return addToDoc(SnomedMappings.iconId(), iconId);
	}

	public SnomedDocumentBuilder parent(final String fieldNameSuffix, final Long parentId) {
		return addToDoc(SnomedMappings.parent(fieldNameSuffix), parentId);
	}

	public SnomedDocumentBuilder ancestor(final String fieldNameSuffix, final Long parentId) {
		return addToDoc(SnomedMappings.ancestor(fieldNameSuffix), parentId);
	}

	public SnomedDocumentBuilder module(final Long moduleId) {
		return addToDoc(SnomedMappings.module(), moduleId);
	}

	public SnomedDocumentBuilder effectiveTime(final Long effectiveTime) {
		return addToDoc(SnomedMappings.effectiveTime(), effectiveTime);
	}

	public SnomedDocumentBuilder active(final boolean active) {
		return addToDoc(SnomedMappings.active(), BooleanUtils.toInteger(active));
	}

	public SnomedDocumentBuilder released(final boolean released) {
		return addToDoc(SnomedMappings.released(), BooleanUtils.toInteger(released));
	}

	public SnomedDocumentBuilder componentReferringPredicate(final String referringPredicateId) {
		return addToDoc(SnomedMappings.componentReferringPredicate(), referringPredicateId);
	}

	public SnomedDocumentBuilder primitive(final boolean primitive) {
		return addToDoc(SnomedMappings.primitive(), BooleanUtils.toInteger(primitive));
	}

	public SnomedDocumentBuilder exhaustive(final boolean exhaustive) {
		return addToDoc(SnomedMappings.exhaustive(), BooleanUtils.toInteger(exhaustive));
	}

	public SnomedDocumentBuilder conceptDegreeOfInterest(final Float conceptDoi) {
		return addToDoc(SnomedMappings.conceptDegreeOfInterest(), conceptDoi);
	}

	public SnomedDocumentBuilder conceptReferringRefSetId(final Long refSetId) {
		return addToDoc(SnomedMappings.conceptReferringRefSetId(), refSetId);
	}

	public SnomedDocumentBuilder conceptReferringMappingRefSetId(final Long mappingRefSetId) {
		return addToDoc(SnomedMappings.conceptReferringMappingRefSetId(), mappingRefSetId);
	}

	public SnomedDocumentBuilder conceptNamespaceId(final Long namespaceId) {
		return addToDoc(SnomedMappings.conceptNamespaceId(), namespaceId);
	}

	public SnomedDocumentBuilder descriptionType(final Long typeId) {
		return addToDoc(SnomedMappings.descriptionType(), typeId);
	}

	public SnomedDocumentBuilder descriptionConcept(final Long conceptId) {
		return addToDoc(SnomedMappings.descriptionConcept(), conceptId);
	}

	public SnomedDocumentBuilder descriptionCaseSignificance(final Long caseSignificanceId) {
		return addToDoc(SnomedMappings.descriptionCaseSignificance(), caseSignificanceId);
	}

	public SnomedDocumentBuilder relationshipSource(final Long sourceId) {
		return addToDoc(SnomedMappings.relationshipSource(), sourceId);
	}

	public SnomedDocumentBuilder relationshipType(final Long typeId) {
		return addToDoc(SnomedMappings.relationshipType(), typeId);
	}

	public SnomedDocumentBuilder relationshipDestination(final Long destinationId) {
		return addToDoc(SnomedMappings.relationshipDestination(), destinationId);
	}

	public SnomedDocumentBuilder relationshipGroup(final Integer group) {
		return addToDoc(SnomedMappings.relationshipGroup(), group);
	}

	public SnomedDocumentBuilder relationshipUnionGroup(final Integer unionGroup) {
		return addToDoc(SnomedMappings.relationshipUnionGroup(), unionGroup);
	}

	public SnomedDocumentBuilder relationshipInferred(final boolean inferred) {
		return addToDoc(SnomedMappings.relationshipInferred(), BooleanUtils.toInteger(inferred));
	}

	public SnomedDocumentBuilder relationshipUniversal(final boolean universal) {
		return addToDoc(SnomedMappings.relationshipUniversal(), BooleanUtils.toInteger(universal));
	}

	public SnomedDocumentBuilder relationshipDestinationNegated(final boolean destinationNegated) {
		return addToDoc(SnomedMappings.relationshipDestinationNegated(), BooleanUtils.toInteger(destinationNegated));
	}

	public SnomedDocumentBuilder relationshipCharacteristicType(final Long characteristicTypeId) {
		return addToDoc(SnomedMappings.relationshipCharacteristicType(), characteristicTypeId);
	}

	public SnomedDocumentBuilder predicateType(final PredicateType type) {
		return addToDoc(SnomedMappings.predicateType(), type.name());
	}

	public SnomedDocumentBuilder predicateDescriptionTypeId(final Long typeId) {
		return addToDoc(SnomedMappings.predicateDescriptionTypeId(), typeId);
	}

	public SnomedDocumentBuilder predicateDataTypeLabel(final String label) { // RF2 name, probably
		return addToDoc(SnomedMappings.predicateDataTypeLabel(), label);
	}

	public SnomedDocumentBuilder predicateDataTypeName(final String name) { // display name, probably
		return addToDoc(SnomedMappings.predicateDataTypeName(), name);
	}

	public SnomedDocumentBuilder predicateDataType(final com.b2international.snowowl.snomed.mrcm.DataType mrcmDataType) {
		return addToDoc(SnomedMappings.predicateDataType(), mrcmDataType.name());
	}

	public SnomedDocumentBuilder predicateRelationshipTypeExpression(final String expression) {
		return addToDoc(SnomedMappings.predicateRelationshipTypeExpression(), expression);
	}

	public SnomedDocumentBuilder predicateRelationshipValueExpression(final String expression) {
		return addToDoc(SnomedMappings.predicateRelationshipValueExpression(), expression);
	}

	public SnomedDocumentBuilder predicateCharacteristicTypeExpression(final String expression) {
		return addToDoc(SnomedMappings.predicateCharacteristicTypeExpression(), expression);
	}

	public SnomedDocumentBuilder predicateGroupRule(final String groupRule) {
		return addToDoc(SnomedMappings.predicateGroupRule(), groupRule);
	}

	public SnomedDocumentBuilder predicateQueryExpression(final String expression) {
		return addToDoc(SnomedMappings.predicateQueryExpression(), expression);
	}

	public SnomedDocumentBuilder predicateRequired(final boolean required) {
		return addToDoc(SnomedMappings.predicateRequired(), BooleanUtils.toInteger(required));
	}

	public SnomedDocumentBuilder predicateMultiple(final boolean multiple) {
		return addToDoc(SnomedMappings.predicateMultiple(), BooleanUtils.toInteger(multiple));
	}

	public SnomedDocumentBuilder refSetType(final SnomedRefSetType refSetType) {
		return addToDoc(SnomedMappings.refSetType(), refSetType.ordinal());
	}

	public SnomedDocumentBuilder refSetReferencedComponentType(final Integer referencedComponentType) {
		return addToDoc(SnomedMappings.refSetReferencedComponentType(), referencedComponentType);
	}

	public SnomedDocumentBuilder refSetStructural(final boolean structural) {
		return addToDoc(SnomedMappings.refSetStructural(), BooleanUtils.toInteger(structural));
	}

	public SnomedDocumentBuilder refSetStorageKey(final Long refSetStorageKey) {
		return addToDoc(SnomedMappings.refSetStorageKey(), refSetStorageKey);
	}

	public SnomedDocumentBuilder memberUuid(final String uuid) {
		return addToDoc(SnomedMappings.memberUuid(), uuid);
	}

	public SnomedDocumentBuilder memberRefSetId(final Long refSetId) {
		return addToDoc(SnomedMappings.memberRefSetId(), refSetId);
	}

	public SnomedDocumentBuilder memberRefSetType(final SnomedRefSetType refSetType) {
		return addToDoc(SnomedMappings.memberRefSetType(), refSetType.ordinal());
	}

	public SnomedDocumentBuilder memberReferencedComponentId(final Long referencedComponentId) {
		return addToDoc(SnomedMappings.memberReferencedComponentId(), referencedComponentId);
	}

	public SnomedDocumentBuilder memberReferencedComponentType(final Integer referencedComponentType) {
		return addToDoc(SnomedMappings.memberReferencedComponentType(), referencedComponentType);
	}

	public SnomedDocumentBuilder memberTargetComponentId(final String mapTargetComponentId) {
		return addToDoc(SnomedMappings.memberTargetComponentId(), mapTargetComponentId);
	}

	public SnomedDocumentBuilder memberValueId(final String valueId) {
		return addToDoc(SnomedMappings.memberValueId(), valueId);
	}

	public SnomedDocumentBuilder memberQuery(final String query) {
		return addToDoc(SnomedMappings.memberQuery(), query);
	}

	public SnomedDocumentBuilder memberMapTargetComponentId(final String mapTargetComponentId) {
		return addToDoc(SnomedMappings.memberMapTargetComponentId(), mapTargetComponentId);
	}

	public SnomedDocumentBuilder memberMapTargetComponentType(final Integer mapTargetComponentType) {
		return addToDoc(SnomedMappings.memberMapTargetComponentType(), mapTargetComponentType);
	}

	public SnomedDocumentBuilder memberMapTargetComponentDescription(final String mapTargetComponentDescription) {
		return addToDoc(SnomedMappings.memberMapTargetComponentDescription(), mapTargetComponentDescription);
	}

	public SnomedDocumentBuilder memberMapGroup(final Integer mapGroup) {
		return addToDoc(SnomedMappings.memberMapGroup(), mapGroup);
	}

	public SnomedDocumentBuilder memberMapPriority(final Integer mapPriority) {
		return addToDoc(SnomedMappings.memberMapPriority(), mapPriority);
	}

	public SnomedDocumentBuilder memberMapRule(final String mapRule) {
		return addToDoc(SnomedMappings.memberMapRule(), mapRule);
	}

	public SnomedDocumentBuilder memberMapAdvice(final String mapAdvice) {
		return addToDoc(SnomedMappings.memberMapAdvice(), mapAdvice);
	}

	public SnomedDocumentBuilder memberMapCategoryId(final Long mapCategoryId) {
		return addToDoc(SnomedMappings.memberMapCategoryId(), mapCategoryId);
	}

	public SnomedDocumentBuilder memberCorrelationId(final Long mapCorrelationId) {
		return addToDoc(SnomedMappings.memberCorrelationId(), mapCorrelationId);
	}

	public SnomedDocumentBuilder memberDescriptionFormatId(final Long descriptionFormatId) {
		return addToDoc(SnomedMappings.memberDescriptionFormatId(), descriptionFormatId);
	}

	public SnomedDocumentBuilder memberDescriptionLength(final Integer descriptionFormatLength) {
		return addToDoc(SnomedMappings.memberDescriptionLength(), descriptionFormatLength);
	}

	public SnomedDocumentBuilder memberAcceptabilityId(final Long acceptabilityId) {
		return addToDoc(SnomedMappings.memberAcceptabilityId(), acceptabilityId);
	}

	public SnomedDocumentBuilder memberOperatorId(final Long operatorId) {
		return addToDoc(SnomedMappings.memberOperatorId(), operatorId);
	}

	public SnomedDocumentBuilder memberContainerModuleId(final Long containerModuleId) {
		return addToDoc(SnomedMappings.memberContainerModuleId(), containerModuleId);
	}

	public SnomedDocumentBuilder memberUomId(final Long uomId) {
		return addToDoc(SnomedMappings.memberUomId(), uomId);
	}

	public SnomedDocumentBuilder memberDataTypeLabel(final String label) {
		return addToDoc(SnomedMappings.memberDataTypeLabel(), label);
	}

	public SnomedDocumentBuilder memberDataTypeOrdinal(final com.b2international.snowowl.snomed.snomedrefset.DataType refSetDataType) {
		return addToDoc(SnomedMappings.memberDataTypeOrdinal(), refSetDataType.ordinal());
	}

	public SnomedDocumentBuilder memberSerializedValue(final String serializedValue) {
		return addToDoc(SnomedMappings.memberSerializedValue(), serializedValue);
	}

	public SnomedDocumentBuilder memberCharacteristicTypeId(final Long characteristicTypeId) {
		return addToDoc(SnomedMappings.memberCharacteristicTypeId(), characteristicTypeId);
	}

	public SnomedDocumentBuilder memberSourceEffectiveTime(final Long sourceEffectiveTime) {
		return addToDoc(SnomedMappings.memberSourceEffectiveTime(), sourceEffectiveTime);
	}

	public SnomedDocumentBuilder memberTargetEffectiveTime(final Long targetEffectiveTime) {
		return addToDoc(SnomedMappings.memberTargetEffectiveTime(), targetEffectiveTime);
	}
}
