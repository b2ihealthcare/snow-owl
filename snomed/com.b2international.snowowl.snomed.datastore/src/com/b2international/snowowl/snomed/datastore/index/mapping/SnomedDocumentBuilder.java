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
				.add(SnomedMappings.memberDataTypeLabel())
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
				.add(Mappings.compareUniqueKey())
				.add(Mappings.compareIgnoreUniqueKey())
				.add(SnomedMappings.refSetStorageKey())
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
				.add(SnomedMappings.descriptionType())
				.add(SnomedMappings.descriptionLanguageCode())
				.add(SnomedMappings.descriptionPreferredReferenceSetId())
				.add(SnomedMappings.descriptionAcceptableReferenceSetId());
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
	
	// Deprecated document builder methods
	
	@Override
	@Deprecated
	public SnomedDocumentBuilder label(String value) {
		throw new UnsupportedOperationException("Labels in SNOMED CT are not supported.");
	}
	
	@Override
	@Deprecated
	public SnomedDocumentBuilder labelWithSort(String value) {
		throw new UnsupportedOperationException("Sort key fields in SNOMED CT are not supported.");
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
		return update(SnomedMappings.iconId(), iconId);
	}

	public SnomedDocumentBuilder parent(final String fieldNameSuffix, final Long parentId) {
		return addToDoc(SnomedMappings.parent(fieldNameSuffix), parentId);
	}

	public SnomedDocumentBuilder ancestor(final String fieldNameSuffix, final Long parentId) {
		return addToDoc(SnomedMappings.ancestor(fieldNameSuffix), parentId);
	}

	public SnomedDocumentBuilder module(final Long moduleId) {
		return update(SnomedMappings.module(), moduleId);
	}

	public SnomedDocumentBuilder effectiveTime(final Long effectiveTime) {
		return update(SnomedMappings.effectiveTime(), effectiveTime);
	}

	public SnomedDocumentBuilder active(final boolean active) {
		return update(SnomedMappings.active(), BooleanUtils.toInteger(active));
	}

	public SnomedDocumentBuilder released(final boolean released) {
		return update(SnomedMappings.released(), BooleanUtils.toInteger(released));
	}

	public SnomedDocumentBuilder componentReferringPredicate(final String referringPredicateId) {
		return addToDoc(SnomedMappings.componentReferringPredicate(), referringPredicateId);
	}

	public SnomedDocumentBuilder primitive(final boolean primitive) {
		return update(SnomedMappings.primitive(), BooleanUtils.toInteger(primitive));
	}

	public SnomedDocumentBuilder exhaustive(final boolean exhaustive) {
		return update(SnomedMappings.exhaustive(), BooleanUtils.toInteger(exhaustive));
	}

	public SnomedDocumentBuilder conceptDegreeOfInterest(final Float conceptDoi) {
		return update(SnomedMappings.conceptDegreeOfInterest(), conceptDoi);
	}

	public SnomedDocumentBuilder conceptReferringRefSetId(final Long refSetId) {
		return addToDoc(SnomedMappings.conceptReferringRefSetId(), refSetId);
	}

	public SnomedDocumentBuilder conceptReferringMappingRefSetId(final Long mappingRefSetId) {
		return addToDoc(SnomedMappings.conceptReferringMappingRefSetId(), mappingRefSetId);
	}

	public SnomedDocumentBuilder conceptNamespaceId(final Long namespaceId) {
		return update(SnomedMappings.conceptNamespaceId(), namespaceId);
	}

	public SnomedDocumentBuilder descriptionType(final Long typeId) {
		return addToDoc(SnomedMappings.descriptionType(), typeId);
	}

	public SnomedDocumentBuilder descriptionTerm(final String term) {
		return update(SnomedMappings.descriptionTerm(), term);
	}
	
	public SnomedDocumentBuilder descriptionLanguageCode(final String languageCode) {
		return addToDoc(SnomedMappings.descriptionLanguageCode(), languageCode);
	}
	
	public SnomedDocumentBuilder descriptionConcept(final Long conceptId) {
		return addToDoc(SnomedMappings.descriptionConcept(), conceptId);
	}

	public SnomedDocumentBuilder descriptionCaseSignificance(final Long caseSignificanceId) {
		return update(SnomedMappings.descriptionCaseSignificance(), caseSignificanceId);
	}

	public SnomedDocumentBuilder descriptionPreferredReferenceSetId(final Long refSetId) {
		return addToDoc(SnomedMappings.descriptionPreferredReferenceSetId(), refSetId);
	}
	
	public SnomedDocumentBuilder descriptionAcceptableReferenceSetId(final Long refSetId) {
		return addToDoc(SnomedMappings.descriptionAcceptableReferenceSetId(), refSetId);
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
		return update(SnomedMappings.relationshipGroup(), group);
	}

	public SnomedDocumentBuilder relationshipUnionGroup(final Integer unionGroup) {
		return update(SnomedMappings.relationshipUnionGroup(), unionGroup);
	}

	public SnomedDocumentBuilder relationshipInferred(final boolean inferred) {
		return update(SnomedMappings.relationshipInferred(), BooleanUtils.toInteger(inferred));
	}

	public SnomedDocumentBuilder relationshipUniversal(final boolean universal) {
		return update(SnomedMappings.relationshipUniversal(), BooleanUtils.toInteger(universal));
	}

	public SnomedDocumentBuilder relationshipDestinationNegated(final boolean destinationNegated) {
		return update(SnomedMappings.relationshipDestinationNegated(), BooleanUtils.toInteger(destinationNegated));
	}

	public SnomedDocumentBuilder relationshipCharacteristicType(final Long characteristicTypeId) {
		return update(SnomedMappings.relationshipCharacteristicType(), characteristicTypeId);
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

	public SnomedDocumentBuilder memberTargetComponentId(final String targetComponentId) {
		return update(SnomedMappings.memberTargetComponentId(), targetComponentId);
	}

	public SnomedDocumentBuilder memberValueId(final String valueId) {
		return update(SnomedMappings.memberValueId(), valueId);
	}

	public SnomedDocumentBuilder memberQuery(final String query) {
		return update(SnomedMappings.memberQuery(), query);
	}

	public SnomedDocumentBuilder memberMapTargetComponentId(final String mapTargetComponentId) {
		return update(SnomedMappings.memberMapTargetComponentId(), mapTargetComponentId);
	}

	public SnomedDocumentBuilder memberMapTargetComponentType(final Integer mapTargetComponentType) {
		return update(SnomedMappings.memberMapTargetComponentType(), mapTargetComponentType);
	}

	public SnomedDocumentBuilder memberMapTargetComponentDescription(final String mapTargetComponentDescription) {
		return update(SnomedMappings.memberMapTargetComponentDescription(), mapTargetComponentDescription);
	}

	public SnomedDocumentBuilder memberMapGroup(final Integer mapGroup) {
		return update(SnomedMappings.memberMapGroup(), mapGroup);
	}

	public SnomedDocumentBuilder memberMapPriority(final Integer mapPriority) {
		return update(SnomedMappings.memberMapPriority(), mapPriority);
	}

	public SnomedDocumentBuilder memberMapRule(final String mapRule) {
		return update(SnomedMappings.memberMapRule(), mapRule);
	}

	public SnomedDocumentBuilder memberMapAdvice(final String mapAdvice) {
		return update(SnomedMappings.memberMapAdvice(), mapAdvice);
	}

	public SnomedDocumentBuilder memberMapCategoryId(final Long mapCategoryId) {
		return update(SnomedMappings.memberMapCategoryId(), mapCategoryId);
	}

	public SnomedDocumentBuilder memberCorrelationId(final Long mapCorrelationId) {
		return update(SnomedMappings.memberCorrelationId(), mapCorrelationId);
	}

	public SnomedDocumentBuilder memberDescriptionFormatId(final Long descriptionFormatId) {
		return update(SnomedMappings.memberDescriptionFormatId(), descriptionFormatId);
	}

	public SnomedDocumentBuilder memberDescriptionLength(final Integer descriptionFormatLength) {
		return update(SnomedMappings.memberDescriptionLength(), descriptionFormatLength);
	}

	public SnomedDocumentBuilder memberAcceptabilityId(final Long acceptabilityId) {
		return update(SnomedMappings.memberAcceptabilityId(), acceptabilityId);
	}

	public SnomedDocumentBuilder memberOperatorId(final Long operatorId) {
		return update(SnomedMappings.memberOperatorId(), operatorId);
	}

	public SnomedDocumentBuilder memberContainerModuleId(final Long containerModuleId) {
		return update(SnomedMappings.memberContainerModuleId(), containerModuleId);
	}

	public SnomedDocumentBuilder memberUomId(final Long uomId) {
		return update(SnomedMappings.memberUomId(), uomId);
	}

	public SnomedDocumentBuilder memberDataTypeLabel(final String label) {
		return update(SnomedMappings.memberDataTypeLabel(), label);
	}

	public SnomedDocumentBuilder memberDataType(final com.b2international.snowowl.snomed.snomedrefset.DataType refSetDataType) {
		return update(SnomedMappings.memberDataTypeOrdinal(), refSetDataType.ordinal());
	}

	public SnomedDocumentBuilder memberSerializedValue(final String serializedValue) {
		return update(SnomedMappings.memberSerializedValue(), serializedValue);
	}

	public SnomedDocumentBuilder memberCharacteristicTypeId(final Long characteristicTypeId) {
		return update(SnomedMappings.memberCharacteristicTypeId(), characteristicTypeId);
	}

	public SnomedDocumentBuilder memberSourceEffectiveTime(final Long sourceEffectiveTime) {
		return update(SnomedMappings.memberSourceEffectiveTime(), sourceEffectiveTime);
	}

	public SnomedDocumentBuilder memberTargetEffectiveTime(final Long targetEffectiveTime) {
		return update(SnomedMappings.memberTargetEffectiveTime(), targetEffectiveTime);
	}
}
