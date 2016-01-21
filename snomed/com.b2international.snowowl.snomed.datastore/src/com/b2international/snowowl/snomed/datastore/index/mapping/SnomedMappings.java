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

import com.b2international.snowowl.datastore.index.mapping.BinaryDocValuesIndexField;
import com.b2international.snowowl.datastore.index.mapping.DocValuesTextIndexField;
import com.b2international.snowowl.datastore.index.mapping.IndexField;
import com.b2international.snowowl.datastore.index.mapping.IntIndexField;
import com.b2international.snowowl.datastore.index.mapping.LongCollectionIndexField;
import com.b2international.snowowl.datastore.index.mapping.LongIndexField;
import com.b2international.snowowl.datastore.index.mapping.Mappings;
import com.b2international.snowowl.datastore.index.mapping.NumericDocValuesIndexField;
import com.b2international.snowowl.datastore.index.mapping.StoredIndexField;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.google.common.base.Predicates;
import com.google.common.base.Strings;

/**
 * @since 4.3
 */
public class SnomedMappings {

	public static final Long ROOT_ID = -1L;

	// Overridden SNOMED CT specific common field instances
	private static final NumericDocValuesIndexField<Long> COMPONENT_ID = Mappings.longDocValuesField(Mappings.id().fieldName());
	private static final NumericDocValuesIndexField<Long> COMPONENT_ICON_ID = Mappings.longDocValuesField(Mappings.iconId().fieldName());

	// Common SNOMED CT field instances
	private static final NumericDocValuesIndexField<Long> COMPONENT_MODULE = Mappings.longDocValuesField("component_module_id");
	private static final NumericDocValuesIndexField<Long> COMPONENT_EFFECTIVE_TIME = Mappings.longDocValuesField("component_effective_time");
	private static final IndexField<Integer> COMPONENT_ACTIVE = Mappings.intDocValuesField("component_active");
	private static final IndexField<Integer> COMPONENT_RELEASED = Mappings.storedOnlyIntField("component_released");
	private static final IndexField<String> COMPONENT_REFERRING_PREDICATE = Mappings.stringField("component_referring_predicate");

	// Concept field instances
	private static final LongCollectionIndexField CONCEPT_PARENT = parent("");
	private static final LongCollectionIndexField CONCEPT_ANCESTOR = ancestor("");
	private static final LongCollectionIndexField CONCEPT_STATED_PARENT = parent(Concepts.STATED_RELATIONSHIP);
	private static final LongCollectionIndexField CONCEPT_STATED_ANCESTOR = ancestor(Concepts.STATED_RELATIONSHIP);
	
	private static final IndexField<Integer> CONCEPT_PRIMITIVE = Mappings.intDocValuesField("concept_primitive");
	private static final IndexField<Integer> CONCEPT_EXHAUSTIVE = Mappings.intDocValuesField("concept_exhaustive");
	private static final IndexField<Float> CONCEPT_DEGREE_OF_INTEREST = Mappings.floatDocValuesField("concept_degree_of_interest");
	private static final LongIndexField CONCEPT_REFERRING_REFERENCE_SET_ID = Mappings.longField("concept_referring_ref_set_id");
	private static final LongIndexField CONCEPT_REFERRING_MAPPING_REFERENCE_SET_ID = Mappings.longField("concept_referring_mapping_ref_set_id");
	private static final LongIndexField CONCEPT_NAMESPACE_ID = Mappings.longField("concept_namespace_id");

	// Description field instances
	private static final DocValuesTextIndexField DESCRIPTION_TERM = new DocValuesTextIndexField("description_term");
	private static final IndexField<String> DESCRIPTION_LANGUAGE_CODE = Mappings.stringField("description_language_code");
	private static final LongIndexField DESCRIPTION_PREFERRED_REFERENCE_SET_ID = Mappings.longField("description_preferred_ref_set_id");
	private static final LongIndexField DESCRIPTION_ACCEPTABLE_REFERENCE_SET_ID = Mappings.longField("description_acceptable_ref_set_id");
	private static final NumericDocValuesIndexField<Long> DESCRIPTION_TYPE_ID = Mappings.longDocValuesField("description_type_id");
	private static final NumericDocValuesIndexField<Long> DESCRIPTION_CONCEPT_ID = Mappings.longDocValuesField("description_concept_id");
	private static final NumericDocValuesIndexField<Long> DESCRIPTION_CASE_SIGNIFICANCE_ID = Mappings.longDocValuesField("description_case_significance_id");

	// Relationship field instances
	private static final NumericDocValuesIndexField<Long> RELATIONSHIP_SOURCE_ID = Mappings.longDocValuesField("relationship_object_id");
	private static final NumericDocValuesIndexField<Long> RELATIONSHIP_TYPE_ID = Mappings.longDocValuesField("relationship_attribute_id");
	private static final NumericDocValuesIndexField<Long> RELATIONSHIP_DESTINATION_ID = Mappings.longDocValuesField("relationship_value_id");
	private static final NumericDocValuesIndexField<Integer> RELATIONSHIP_GROUP = Mappings.storedOnlyIntFieldWithDocValues("relationship_group");
	private static final NumericDocValuesIndexField<Integer> RELATIONSHIP_UNION_GROUP = Mappings.storedOnlyIntFieldWithDocValues("relationship_union_group");
	private static final IndexField<Integer> RELATIONSHIP_INFERRED = Mappings.storedOnlyIntField("relationship_inferred");
	private static final NumericDocValuesIndexField<Integer> RELATIONSHIP_UNIVERSAL = Mappings.storedOnlyIntFieldWithDocValues("relationship_universal");
	private static final NumericDocValuesIndexField<Integer> RELATIONSHIP_DESTINATION_NEGATED = Mappings.storedOnlyIntFieldWithDocValues("relationship_destination_negated");
	private static final NumericDocValuesIndexField<Long> RELATIONSHIP_CHARACTERISTIC_TYPE_ID = Mappings.longDocValuesField("relationship_characteristic_type_id");

	// Predicate field instances
	private static final StoredIndexField<String> PREDICATE_TYPE = Mappings.storedOnlyStringField("predicate_type");
	private static final StoredIndexField<Long> PREDICATE_DESCRIPTION_TYPE_ID = Mappings.storedOnlyLongField("predicate_description_type_id");
	private static final StoredIndexField<String> PREDICATE_DATA_TYPE_LABEL = Mappings.storedOnlyStringField("predicate_data_type_label");
	private static final StoredIndexField<String> PREDICATE_DATA_TYPE_NAME = Mappings.storedOnlyStringField("predicate_data_type_name");
	private static final StoredIndexField<String> PREDICATE_DATA_TYPE_TYPE = Mappings.storedOnlyStringField("predicate_data_type_type");
	private static final StoredIndexField<String> PREDICATE_RELATIONSHIP_TYPE_EXPRESSION = Mappings.storedOnlyStringField("predicate_relationship_type_expression");
	private static final StoredIndexField<String> PREDICATE_RELATIONSHIP_VALUE_EXPRESSION = Mappings.storedOnlyStringField("predicate_relationship_value_expression");
	private static final StoredIndexField<String> PREDICATE_CHARACTERISTIC_TYPE_EXPRESSION = Mappings.storedOnlyStringField("predicate_characteristic_type_expression");
	private static final StoredIndexField<String> PREDICATE_GROUP_RULE = Mappings.storedOnlyStringField("predicate_group_rule");
	private static final StoredIndexField<String> PREDICATE_QUERY_EXPRESSION = Mappings.storedOnlyStringField("predicate_query_expression");
	private static final StoredIndexField<Integer> PREDICATE_REQUIRED = Mappings.storedOnlyIntField("predicate_required");
	private static final StoredIndexField<Integer> PREDICATE_MULTIPLE = Mappings.storedOnlyIntField("predicate_multiple");

	// Reference set field instances
	private static final IntIndexField REFERENCE_SET_TYPE = Mappings.intField("ref_set_type");
	private static final IntIndexField REFERENCE_SET_REFERENCED_COMPONENT_TYPE = Mappings.intField("ref_set_referenced_component_type");
	private static final IntIndexField REFERENCE_SET_STRUCTURAL = Mappings.intField("ref_set_structural");
	private static final LongIndexField REFERENCE_SET_STORAGE_KEY = Mappings.longField("ref_set_storage_key");

	// Common reference set member field instances
	private static final IndexField<String> REFERENCE_SET_MEMBER_UUID = Mappings.stringField("ref_set_member_uuid");
	private static final NumericDocValuesIndexField<Long> REFERENCE_SET_MEMBER_REFERENCE_SET_ID = Mappings.longDocValuesField("ref_set_member_reference_set_id");
	private static final IntIndexField REFERENCE_SET_MEMBER_REFERENCE_SET_TYPE = Mappings.intField("ref_set_member_reference_set_type");
	private static final NumericDocValuesIndexField<Long> REFERENCE_SET_MEMBER_REFERENCED_COMPONENT_ID = Mappings.longDocValuesField("ref_set_member_referenced_component_id");
	private static final IntIndexField REFERENCE_SET_MEMBER_REFERENCED_COMPONENT_TYPE = Mappings.intField("ref_set_member_referenced_component_type");

	// Association member field instances
	private static final IndexField<String> REFERENCE_SET_MEMBER_TARGET_COMPONENT_ID = Mappings.stringField("ref_set_member_target_component_id");

	// Attribute-value member field instances
	private static final IndexField<String> REFERENCE_SET_MEMBER_VALUE_ID = Mappings.stringField("ref_set_member_value_id");

	// Query member field instances
	private static final IndexField<String> REFERENCE_SET_MEMBER_QUERY = Mappings.stringField("ref_set_member_query");

	// Simple map member field instances
	private static final IndexField<String> REFERENCE_SET_MEMBER_MAP_TARGET_COMPONENT_ID = Mappings.stringField("ref_set_member_map_target_component_id");
	private static final IntIndexField REFERENCE_SET_MEMBER_MAP_TARGET_COMPONENT_TYPE = Mappings.intField("ref_set_member_map_target_component_type");
	private static final IndexField<String> REFERENCE_SET_MEMBER_MAP_TARGET_COMPONENT_DESCRIPTION = Mappings.textField("ref_set_member_map_target_component_description");

	// Complex and extended map member field instances
	private static final StoredIndexField<Integer> REFERENCE_SET_MEMBER_MAP_GROUP = Mappings.storedOnlyIntField("ref_set_member_map_group");
	private static final StoredIndexField<Integer> REFERENCE_SET_MEMBER_MAP_PRIORITY = Mappings.storedOnlyIntField("ref_set_member_map_priority");
	private static final IndexField<String> REFERENCE_SET_MEMBER_MAP_RULE = Mappings.stringField("ref_set_member_map_rule");
	private static final IndexField<String> REFERENCE_SET_MEMBER_MAP_ADVICE = Mappings.stringField("ref_set_member_map_advice");
	private static final LongIndexField REFERENCE_SET_MEMBER_MAP_CATEGORY_ID = Mappings.longField("ref_set_member_category_id");
	private static final LongIndexField REFERENCE_SET_MEMBER_CORRELATION_ID = Mappings.longField("ref_set_member_correlation_id");

	// Description type/format member field instances
	private static final LongIndexField REFERENCE_SET_MEMBER_DESCRIPTION_FORMAT_ID = Mappings.longField("ref_set_member_description_format_id");
	private static final IntIndexField REFERENCE_SET_MEMBER_DESCRIPTION_LENGTH = Mappings.intField("ref_set_member_description_length");

	// Language member field instances
	private static final LongIndexField REFERENCE_SET_MEMBER_ACCEPTABILITY_ID = Mappings.longField("ref_set_member_acceptability_id");

	// Concrete domain member field instances
	private static final LongIndexField REFERENCE_SET_MEMBER_OPERATOR_ID = Mappings.longField("ref_set_member_operator_id");
	private static final NumericDocValuesIndexField<Long> REFERENCE_SET_MEMBER_UOM_ID = Mappings.longDocValuesField("ref_set_member_uom_id");
	private static final BinaryDocValuesIndexField REFERENCE_SET_MEMBER_DATA_TYPE_LABEL = Mappings.stringDocValuesField("ref_set_member_data_type_label"); // data type label ("isVitamin")
	private static final NumericDocValuesIndexField<Integer> REFERENCE_SET_MEMBER_DATA_TYPE_ORDINAL = Mappings.intDocValuesField("ref_set_member_data_type_ordinal");  //data type enumeration ordinal (1 for decimal) 
	private static final BinaryDocValuesIndexField REFERENCE_SET_MEMBER_SERIALIZED_VALUE = Mappings.stringDocValuesField("ref_set_member_serialized_value"); //data type value serialized form ("false")
	private static final LongIndexField REFERENCE_SET_MEMBER_CHARACTERISTIC_TYPE_ID = Mappings.longField("ref_set_member_characteristic_type");

	// Module dependency member field instances
	private static final LongIndexField REFERENCE_SET_MEMBER_SOURCE_EFFECTIVE_TIME = Mappings.longField("ref_set_member_source_effective_time");
	private static final LongIndexField REFERENCE_SET_MEMBER_TARGET_EFFECTIVE_TIME = Mappings.longField("ref_set_member_target_effective_time");

	/* 
	 * Entry points for query, field set and document building
	 */

	public static SnomedQueryBuilder newQuery() {
		return new SnomedQueryBuilder();
	}

	public static SnomedFieldsToLoadBuilder fieldsToLoad() {
		return new SnomedFieldsToLoadBuilder();
	}

	public static SnomedDocumentBuilder doc() {
		return new SnomedDocumentBuilder();
	}

	/* 
	 * Computed fields and utility methods
	 */

	public static boolean isRoot(final long parent) {
		return ROOT_ID == parent;
	}

	public static LongCollectionIndexField parent() {
		return CONCEPT_PARENT;
	}
	
	public static LongCollectionIndexField statedParent() {
		return CONCEPT_STATED_PARENT;
	}

	public static LongCollectionIndexField parent(final String fieldNameSuffix) {
		final LongIndexField field = Mappings.longField(concatIfNotNullOrEmpty(Mappings.parent().fieldName(), fieldNameSuffix));
		return Mappings.filteredLongField(field, Predicates.not(Predicates.equalTo(ROOT_ID)));
	}

	public static LongCollectionIndexField ancestor() {
		return CONCEPT_ANCESTOR;
	}
	
	public static LongCollectionIndexField statedAncestor() {
		return CONCEPT_STATED_ANCESTOR;
	}

	public static LongCollectionIndexField ancestor(final String fieldNameSuffix) {
		final LongIndexField field = Mappings.longField(concatIfNotNullOrEmpty(Mappings.ancestor().fieldName(), fieldNameSuffix));
		return Mappings.filteredLongField(field, Predicates.not(Predicates.equalTo(ROOT_ID)));
	}

	private static String concatIfNotNullOrEmpty(final String firstPart, final String secondPart) {
		final StringBuilder builder = new StringBuilder(firstPart);
		if (!Strings.isNullOrEmpty(secondPart)) {
			builder.append('_');
			builder.append(secondPart);
		}
		return builder.toString();
	}

	/*
	 * Getters
	 */

	public static NumericDocValuesIndexField<Long> id() {
		return COMPONENT_ID;
	}

	public static NumericDocValuesIndexField<Long> iconId() {
		return COMPONENT_ICON_ID;
	}

	public static NumericDocValuesIndexField<Long> module() {
		return COMPONENT_MODULE;
	}

	public static NumericDocValuesIndexField<Long> effectiveTime() {
		return COMPONENT_EFFECTIVE_TIME;
	}

	public static IndexField<Integer> active() {
		return COMPONENT_ACTIVE;
	}

	public static IndexField<Integer> released() {
		return COMPONENT_RELEASED;
	}

	public static IndexField<String> componentReferringPredicate() {
		return COMPONENT_REFERRING_PREDICATE;
	}

	public static IndexField<Integer> primitive() {
		return CONCEPT_PRIMITIVE;
	}

	public static IndexField<Integer> exhaustive() {
		return CONCEPT_EXHAUSTIVE;
	}

	public static IndexField<Float> conceptDegreeOfInterest() {
		return CONCEPT_DEGREE_OF_INTEREST;
	}

	public static LongIndexField conceptReferringRefSetId() {
		return CONCEPT_REFERRING_REFERENCE_SET_ID;
	}

	public static LongIndexField conceptReferringMappingRefSetId() {
		return CONCEPT_REFERRING_MAPPING_REFERENCE_SET_ID;
	}

	public static LongIndexField conceptNamespaceId() {
		return CONCEPT_NAMESPACE_ID;
	}

	public static DocValuesTextIndexField descriptionTerm() {
		return DESCRIPTION_TERM;
	}
	
	public static IndexField<String> descriptionLanguageCode() {
		return DESCRIPTION_LANGUAGE_CODE;
	}
	
	public static LongIndexField descriptionPreferredReferenceSetId() {
		return DESCRIPTION_PREFERRED_REFERENCE_SET_ID;
	}
	
	public static LongIndexField descriptionAcceptableReferenceSetId() {
		return DESCRIPTION_ACCEPTABLE_REFERENCE_SET_ID;
	}

	public static NumericDocValuesIndexField<Long> descriptionType() {
		return DESCRIPTION_TYPE_ID;
	}

	public static NumericDocValuesIndexField<Long> descriptionConcept() {
		return DESCRIPTION_CONCEPT_ID;
	}

	public static NumericDocValuesIndexField<Long> descriptionCaseSignificance() {
		return DESCRIPTION_CASE_SIGNIFICANCE_ID;
	}

	public static NumericDocValuesIndexField<Long> relationshipSource() {
		return RELATIONSHIP_SOURCE_ID;
	}

	public static NumericDocValuesIndexField<Long> relationshipType() {
		return RELATIONSHIP_TYPE_ID;
	}

	public static NumericDocValuesIndexField<Long> relationshipDestination() {
		return RELATIONSHIP_DESTINATION_ID;
	}

	public static NumericDocValuesIndexField<Integer> relationshipGroup() {
		return RELATIONSHIP_GROUP;
	}

	public static NumericDocValuesIndexField<Integer> relationshipUnionGroup() {
		return RELATIONSHIP_UNION_GROUP;
	}

	public static IndexField<Integer> relationshipInferred() {
		return RELATIONSHIP_INFERRED;
	}

	public static NumericDocValuesIndexField<Integer> relationshipUniversal() {
		return RELATIONSHIP_UNIVERSAL;
	}

	public static NumericDocValuesIndexField<Integer> relationshipDestinationNegated() {
		return RELATIONSHIP_DESTINATION_NEGATED;
	}

	public static NumericDocValuesIndexField<Long> relationshipCharacteristicType() {
		return RELATIONSHIP_CHARACTERISTIC_TYPE_ID;
	}

	public static StoredIndexField<String> predicateType() {
		return PREDICATE_TYPE;
	}

	public static StoredIndexField<Long> predicateDescriptionTypeId() {
		return PREDICATE_DESCRIPTION_TYPE_ID;
	}

	public static StoredIndexField<String> predicateDataTypeLabel() {
		return PREDICATE_DATA_TYPE_LABEL;
	}

	public static StoredIndexField<String> predicateDataTypeName() {
		return PREDICATE_DATA_TYPE_NAME;
	}

	public static StoredIndexField<String> predicateDataType() {
		return PREDICATE_DATA_TYPE_TYPE;
	}

	public static StoredIndexField<String> predicateRelationshipTypeExpression() {
		return PREDICATE_RELATIONSHIP_TYPE_EXPRESSION;
	}

	public static StoredIndexField<String> predicateRelationshipValueExpression() {
		return PREDICATE_RELATIONSHIP_VALUE_EXPRESSION;
	}

	public static StoredIndexField<String> predicateCharacteristicTypeExpression() {
		return PREDICATE_CHARACTERISTIC_TYPE_EXPRESSION;
	}

	public static StoredIndexField<String> predicateGroupRule() {
		return PREDICATE_GROUP_RULE;
	}

	public static StoredIndexField<String> predicateQueryExpression() {
		return PREDICATE_QUERY_EXPRESSION;
	}

	public static StoredIndexField<Integer> predicateRequired() {
		return PREDICATE_REQUIRED;
	}

	public static StoredIndexField<Integer> predicateMultiple() {
		return PREDICATE_MULTIPLE;
	}

	public static IntIndexField refSetType() {
		return REFERENCE_SET_TYPE;
	}

	public static IntIndexField refSetReferencedComponentType() {
		return REFERENCE_SET_REFERENCED_COMPONENT_TYPE;
	}

	public static IntIndexField refSetStructural() {
		return REFERENCE_SET_STRUCTURAL;
	}

	public static LongIndexField refSetStorageKey() {
		return REFERENCE_SET_STORAGE_KEY;
	}

	public static IndexField<String> memberUuid() {
		return REFERENCE_SET_MEMBER_UUID;
	}

	public static NumericDocValuesIndexField<Long> memberRefSetId() {
		return REFERENCE_SET_MEMBER_REFERENCE_SET_ID;
	}

	public static IntIndexField memberRefSetType() {
		return REFERENCE_SET_MEMBER_REFERENCE_SET_TYPE;
	}

	public static NumericDocValuesIndexField<Long> memberReferencedComponentId() {
		return REFERENCE_SET_MEMBER_REFERENCED_COMPONENT_ID;
	}

	public static IntIndexField memberReferencedComponentType() {
		return REFERENCE_SET_MEMBER_REFERENCED_COMPONENT_TYPE;
	}

	public static IndexField<String> memberTargetComponentId() {
		return REFERENCE_SET_MEMBER_TARGET_COMPONENT_ID;
	}

	public static IndexField<String> memberValueId() {
		return REFERENCE_SET_MEMBER_VALUE_ID;
	}

	public static IndexField<String> memberQuery() {
		return REFERENCE_SET_MEMBER_QUERY;
	}

	public static IndexField<String> memberMapTargetComponentId() {
		return REFERENCE_SET_MEMBER_MAP_TARGET_COMPONENT_ID;
	}

	public static IntIndexField memberMapTargetComponentType() {
		return REFERENCE_SET_MEMBER_MAP_TARGET_COMPONENT_TYPE;
	}

	public static IndexField<String> memberMapTargetComponentDescription() {
		return REFERENCE_SET_MEMBER_MAP_TARGET_COMPONENT_DESCRIPTION;
	}

	public static StoredIndexField<Integer> memberMapGroup() {
		return REFERENCE_SET_MEMBER_MAP_GROUP;
	}

	public static StoredIndexField<Integer> memberMapPriority() {
		return REFERENCE_SET_MEMBER_MAP_PRIORITY;
	}

	public static IndexField<String> memberMapRule() {
		return REFERENCE_SET_MEMBER_MAP_RULE;
	}

	public static IndexField<String> memberMapAdvice() {
		return REFERENCE_SET_MEMBER_MAP_ADVICE;
	}

	public static LongIndexField memberMapCategoryId() {
		return REFERENCE_SET_MEMBER_MAP_CATEGORY_ID;
	}

	public static LongIndexField memberCorrelationId() {
		return REFERENCE_SET_MEMBER_CORRELATION_ID;
	}

	public static LongIndexField memberDescriptionFormatId() {
		return REFERENCE_SET_MEMBER_DESCRIPTION_FORMAT_ID;
	}

	public static IntIndexField memberDescriptionLength() {
		return REFERENCE_SET_MEMBER_DESCRIPTION_LENGTH;
	}

	public static LongIndexField memberAcceptabilityId() {
		return REFERENCE_SET_MEMBER_ACCEPTABILITY_ID;
	}

	public static LongIndexField memberOperatorId() {
		return REFERENCE_SET_MEMBER_OPERATOR_ID;
	}

	public static NumericDocValuesIndexField<Long> memberUomId() {
		return REFERENCE_SET_MEMBER_UOM_ID;
	}

	public static BinaryDocValuesIndexField memberDataTypeLabel() {
		return REFERENCE_SET_MEMBER_DATA_TYPE_LABEL;
	}

	public static NumericDocValuesIndexField<Integer> memberDataTypeOrdinal() {
		return REFERENCE_SET_MEMBER_DATA_TYPE_ORDINAL;
	}

	public static BinaryDocValuesIndexField memberSerializedValue() {
		return REFERENCE_SET_MEMBER_SERIALIZED_VALUE;
	}

	public static LongIndexField memberCharacteristicTypeId() {
		return REFERENCE_SET_MEMBER_CHARACTERISTIC_TYPE_ID;
	}

	public static LongIndexField memberSourceEffectiveTime() {
		return REFERENCE_SET_MEMBER_SOURCE_EFFECTIVE_TIME;
	}

	public static LongIndexField memberTargetEffectiveTime() {
		return REFERENCE_SET_MEMBER_TARGET_EFFECTIVE_TIME;
	}
}
