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
package com.b2international.snowowl.snomed.datastore.browser;

import com.b2international.snowowl.core.api.index.CommonIndexConstants;

/**
 * Collection of constants related to the multi-field terminology browser implementation.
 *  
 */
abstract public class SnomedIndexBrowserConstants {
	
	public static final long ROOT_ID = -1L;
	
	// TODO (apeteri): tear these constants off of this class
	public static final String COMPONENT_ID = CommonIndexConstants.COMPONENT_ID;
	public static final String COMPONENT_LABEL = CommonIndexConstants.COMPONENT_LABEL;
	public static final String COMPONENT_STORAGE_KEY = CommonIndexConstants.COMPONENT_STORAGE_KEY;
	public static final String COMPONENT_COMPARE_UNIQUE_KEY = CommonIndexConstants.COMPONENT_COMPARE_UNIQUE_KEY;
	public static final String COMPONENT_TYPE = CommonIndexConstants.COMPONENT_TYPE;
	public static final String COMPONENT_ICON_ID = CommonIndexConstants.COMPONENT_ICON_ID;
	public static final String COMPONENT_IGNORE_COMPARE_UNIQUE_KEY = CommonIndexConstants.COMPONENT_IGNORE_COMPARE_UNIQUE_KEY;
	
	public static final String COMPONENT_ACTIVE = "component_active";
	public static final String COMPONENT_RELEASED = "component_released";
	public static final String COMPONENT_REFERRING_PREDICATE = "component_referring_predicate";
	public static final String COMPONENT_MODULE_ID = "component_module_id";
	
	public static final String CONCEPT_ANCESTOR = "concept_ancestor_id";
	public static final String CONCEPT_PARENT = CommonIndexConstants.COMPONENT_PARENT;	// TODO: change field to component based
	public static final String CONCEPT_PRIMITIVE = "concept_primitive";
	public static final String CONCEPT_EXHAUSTIVE = "concept_exhaustive";
	public static final String CONCEPT_MODULE_ID = COMPONENT_MODULE_ID;
	public static final String CONCEPT_FULLY_SPECIFIED_NAME = "concept_fully_specified_name";
	public static final String CONCEPT_SYNONYM = "concept_synonym";
	public static final String CONCEPT_OTHER_DESCRIPTION = "concept_other_description";
	public static final String CONCEPT_DEGREE_OF_INTEREST = "concept_degree_of_interest";
	public static final String CONCEPT_REFERRING_REFERENCE_SET_ID = "concept_referring_ref_set_id";
	public static final String CONCEPT_REFERRING_MAPPING_REFERENCE_SET_ID = "concept_referring_mapping_ref_set_id";
	public static final String CONCEPT_NAMESPACE_ID = "concept_namespace_id";
	public static final String CONCEPT_EFFECTIVE_TIME = "concept_effectivetime";
	
	public static final String RELATIONSHIP_OBJECT_ID = "relationship_object_id";
	public static final String RELATIONSHIP_ATTRIBUTE_ID = "relationship_attribute_id";
	public static final String RELATIONSHIP_VALUE_ID = "relationship_value_id";
	public static final String RELATIONSHIP_CHARACTERISTIC_TYPE_ID = "relationship_characteristic_type_id";
	public static final String RELATIONSHIP_GROUP = "relationship_group";
	public static final String RELATIONSHIP_UNION_GROUP = "relationship_union_group";
	public static final String RELATIONSHIP_INFERRED = "relationship_inferred";
	public static final String RELATIONSHIP_UNIVERSAL = "relationship_universal";
	public static final String RELATIONSHIP_DESTINATION_NEGATED = "relationship_destination_negated";
	public static final String RELATIONSHIP_MODULE_ID = COMPONENT_MODULE_ID;
	public static final String RELATIONSHIP_EFFECTIVE_TIME = "relationship_effectivetime";
	
	public static final String REFERENCE_SET_TYPE = "ref_set_type";
	public static final String REFERENCE_SET_REFERENCED_COMPONENT_TYPE = "ref_set_referenced_component_type";
	public static final String REFERENCE_SET_MEMBER_COUNT = "ref_set_member_count";
	public static final String REFERENCE_SET_STRUCTURAL = "ref_set_structural";
	public static final String REFERENCE_SET_MODULE_ID = COMPONENT_MODULE_ID;
	
	public static final String PREDICATE_UUID = "predicate_uuid";
	public static final String PREDICATE_TYPE = "predicate_type";
	public static final String PREDICATE_DESCRIPTION_TYPE_ID = "predicate_description_type_id";
	public static final String PREDICATE_DATA_TYPE_LABEL = "predicate_data_type_label";
	public static final String PREDICATE_DATA_TYPE_NAME = "predicate_data_type_name";
	public static final String PREDICATE_DATA_TYPE_TYPE = "predicate_data_type_type";
	public static final String PREDICATE_RELATIONSHIP_TYPE_EXPRESSION = "predicate_relationship_type_expression";
	public static final String PREDICATE_RELATIONSHIP_VALUE_EXPRESSION = "predicate_relationship_value_expression";
	public static final String PREDICATE_CHARACTERISTIC_TYPE_EXPRESSION = "predicate_characteristic_type_expression";
	public static final String PREDICATE_GROUP_RULE = "predicate_group_rule";
	public static final String PREDICATE_QUERY_EXPRESSION = "predicate_query_expression";
	public static final String PREDICATE_REQUIRED = "predicate_required";
	public static final String PREDICATE_MULTIPLE = "predicate_multiple";

	public static final String DESCRIPTION_TYPE_ID = "description_type_id";
	public static final String DESCRIPTION_CASE_SIGNIFICANCE_ID = "description_case_significance_id";
	public static final String DESCRIPTION_CONCEPT_ID = "description_concept_id";
	public static final String DESCRIPTION_MODULE_ID = COMPONENT_MODULE_ID;
	public static final String DESCRIPTION_EFFECTIVE_TIME = "description_effectivetime";

	public static final String REFERENCE_SET_MEMBER_UUID = "ref_set_member_uuid";
	public static final String REFERENCE_SET_MEMBER_REFERENCED_COMPONENT_ID = "ref_set_member_referenced_component_id";
	public static final String REFERENCE_SET_MEMBER_MODULE_ID = COMPONENT_MODULE_ID;
	public static final String REFERENCE_SET_MEMBER_ACCEPTABILITY_ID = "ref_set_member_acceptability_id";
	public static final String REFERENCE_SET_MEMBER_ACCEPTABILITY_LABEL = "ref_set_member_acceptability_label";
	public static final String REFERENCE_SET_MEMBER_REFERENCE_SET_ID = "ref_set_member_reference_set_id";
	public static final String REFERENCE_SET_MEMBER_VALUE_ID = "ref_set_member_value_id";
	public static final String REFERENCE_SET_MEMBER_VALUE_LABEL = "ref_set_member_value_label";
	public static final String REFERENCE_SET_MEMBER_TARGET_COMPONENT_ID = "ref_set_member_target_component_id";
	public static final String REFERENCE_SET_MEMBER_TARGET_COMPONENT_LABEL = "ref_set_member_target_component_label";
	public static final String REFERENCE_SET_MEMBER_MAP_TARGET_COMPONENT_ID = "ref_set_member_map_target_component_id";
	public static final String REFERENCE_SET_MEMBER_MAP_TARGET_COMPONENT_TYPE_ID = "ref_set_member_map_target_component_type_id";
	public static final String REFERENCE_SET_MEMBER_MAP_TARGET_COMPONENT_LABEL = "ref_set_member_map_target_component_label";
	public static final String REFERENCE_SET_MEMBER_MAP_TARGET_COMPONENT_DESCRIPTION_SORT_KEY = "ref_set_member_map_target_component_description_sort_key";
	public static final String REFERENCE_SET_MEMBER_MAP_TARGET_COMPONENT_DESCRIPTION = "ref_set_member_map_target_component_description";
	public static final String REFERENCE_SET_MEMBER_MAP_GROUP = "ref_set_member_map_group";
	public static final String REFERENCE_SET_MEMBER_MAP_PRIORITY = "ref_set_member_map_priority";
	public static final String REFERENCE_SET_MEMBER_MAP_RULE = "ref_set_member_map_rule";
	public static final String REFERENCE_SET_MEMBER_MAP_ADVICE = "ref_set_member_map_advice";
	public static final String REFERENCE_SET_MEMBER_MAP_CATEGORY_ID = "ref_set_member_category_id";
	public static final String REFERENCE_SET_MEMBER_CORRELATION_ID = "ref_set_member_correlation_id";
	public static final String REFERENCE_SET_MEMBER_DESCRIPTION_FORMAT_ID = "ref_set_member_description_format_id";
	public static final String REFERENCE_SET_MEMBER_DESCRIPTION_FORMAT_LABEL = "ref_set_member_description_format_label";
	public static final String REFERENCE_SET_MEMBER_DESCRIPTION_LENGTH = "ref_set_member_description_length";
	public static final String REFERENCE_SET_MEMBER_OPERATOR_ID = "ref_set_member_operator_id";
	public static final String REFERENCE_SET_MEMBER_SERIALIZED_VALUE = "ref_set_member_serialized_value";
	public static final String REFERENCE_SET_MEMBER_CONTAINER_MODULE_ID = "ref_set_member_container_module_ID";
	public static final String REFERENCE_SET_MEMBER_UOM_ID = "ref_set_member_uom_id";
	public static final String REFERENCE_SET_MEMBER_DATA_TYPE_LABEL = "ref_set_member_data_type_label"; //data type value serialized form: e.g.: false
	public static final String REFERENCE_SET_MEMBER_DATA_TYPE_ID = "ref_set_member_data_type_id";  //data type enumeration literal. e.g.: integer
	public static final String REFERENCE_SET_MEMBER_DATA_TYPE_VALUE = "ref_set_member_data_type_value";  //data type enumeration ordinal . e.g.: 1 for decimal (com.b2international.snowowl.snomed.snomedrefset.DataType) 
	public static final String REFERENCE_SET_MEMBER_CHARACTERISTIC_TYPE_ID = "ref_set_member_characteristic_type";
	public static final String REFERENCE_SET_MEMBER_QUERY = "ref_set_member_query";
	public static final String REFERENCE_SET_MEMBER_REFERENCED_COMPONENT_TYPE = "ref_set_member_referenced_component_type";
	public static final String REFERENCE_SET_MEMBER_EFFECTIVE_TIME = "ref_set_member_effective_time";
	public static final String REFERENCE_SET_MEMBER_REFERENCE_SET_TYPE = "ref_set_member_reference_set_type";
	public static final String REFERENCE_SET_MEMBER_SOURCE_EFFECTIVE_TIME = "ref_set_member_source_effective_time";
	public static final String REFERENCE_SET_MEMBER_TARGET_EFFECTIVE_TIME = "ref_set_member_target_effective_time";

	private SnomedIndexBrowserConstants() {}
}