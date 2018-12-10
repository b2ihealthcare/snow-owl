/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.common;

import com.google.common.collect.ImmutableSet;

/**
 * Collects all release file field names and headers for SNOMED&nbsp;CT RF2.
 */
public abstract class SnomedRf2Headers {

	// Common field names
	public static final String FIELD_ID = "id";
	public static final String FIELD_EFFECTIVE_TIME = "effectiveTime";
	public static final String FIELD_ACTIVE = "active";
	public static final String FIELD_MODULE_ID = "moduleId";

	// Field names introduced in concept files
	public static final String FIELD_DEFINITION_STATUS_ID = "definitionStatusId";

	// Field names introduced in description files
	public static final String FIELD_CONCEPT_ID = "conceptId";
	public static final String FIELD_LANGUAGE_CODE = "languageCode";
	public static final String FIELD_TYPE_ID = "typeId";
	public static final String FIELD_TERM = "term";
	public static final String FIELD_CASE_SIGNIFICANCE_ID = "caseSignificanceId";

	// Field names introduced in relationship files
	public static final String FIELD_SOURCE_ID = "sourceId";
	public static final String FIELD_DESTINATION_ID = "destinationId";
	public static final String FIELD_RELATIONSHIP_GROUP = "relationshipGroup";
	public static final String FIELD_CHARACTERISTIC_TYPE_ID = "characteristicTypeId";
	public static final String FIELD_MODIFIER_ID = "modifierId";
	
	// Field names introduced in simple type reference set files
	public static final String FIELD_REFSET_ID = "refsetId";
	public static final String FIELD_REFERENCED_COMPONENT_ID = "referencedComponentId";
	
	// Field names introduced in query type reference set files
	public static final String FIELD_QUERY = "query";
	
	// Field names introduced in language type reference set files
	public static final String FIELD_ACCEPTABILITY_ID = "acceptabilityId";

	// Field names introduced in description type reference set files
	public static final String FIELD_DESCRIPTION_FORMAT = "descriptionFormat";
	public static final String FIELD_DESCRIPTION_LENGTH = "descriptionLength";
	
	// Field names introduced in concrete domain reference set files
	public static final String FIELD_VALUE = "value";
	
	// Field names introduced in attribute value type reference set files
	public static final String FIELD_VALUE_ID = "valueId";
	
	/* 
	 * Field names introduced in association type reference set files
	 * (Used throughout Snow Owl's codebase) 
	 */
	public static final String FIELD_TARGET_COMPONENT = "targetComponent";
	
	/*
	 * Alternative column name for association type reference set files 
	 * (Used only in RF2 import/export parts)
	 */
	public static final String FIELD_TARGET_COMPONENT_ID = "targetComponentId"; 
	
	// Field names introduced in simple map type reference set files
	public static final String FIELD_MAP_TARGET = "mapTarget";
	
	//field name for SDD specific simple map reference sets where target description is available 
	public static final String FIELD_MAP_TARGET_DESCRIPTION = "mapTargetDescription";

	// Field names introduced in complex map type reference set files
	public static final String FIELD_MAP_GROUP = "mapGroup";
	public static final String FIELD_MAP_PRIORITY = "mapPriority";
	public static final String FIELD_MAP_RULE = "mapRule";
	public static final String FIELD_MAP_ADVICE = "mapAdvice";
	public static final String FIELD_CORRELATION_ID = "correlationId";
	
	//field for extended map type reference sets
	public static final String FIELD_MAP_CATEGORY_ID = "mapCategoryId";
	
	// Field names introduced in module dependency reference set files
	public static final String FIELD_SOURCE_EFFECTIVE_TIME = "sourceEffectiveTime";
	public static final String FIELD_TARGET_EFFECTIVE_TIME = "targetEffectiveTime";
	
	// Field names introduced in annotation type reference set files
	public static final String FIELD_OWL_EXPRESSION = "owlExpression";
	
	// Field names used in MRCM reference set files
	public static final String FIELD_MRCM_DOMAIN_CONSTRAINT = "domainConstraint";
	public static final String FIELD_MRCM_PARENT_DOMAIN = "parentDomain";
	public static final String FIELD_MRCM_PROXIMAL_PRIMITIVE_CONSTRAINT = "proximalPrimitiveConstraint";
	public static final String FIELD_MRCM_PROXIMAL_PRIMITIVE_REFINEMENT = "proximalPrimitiveRefinement";
	public static final String FIELD_MRCM_DOMAIN_TEMPLATE_FOR_PRECOORDINATION = "domainTemplateForPrecoordination";
	public static final String FIELD_MRCM_DOMAIN_TEMPLATE_FOR_POSTCOORDINATION = "domainTemplateForPostcoordination";
	public static final String FIELD_MRCM_EDITORIAL_GUIDE_REFERENCE = "guideURL";
	
	public static final String FIELD_MRCM_DOMAIN_ID = "domainId";
	public static final String FIELD_MRCM_GROUPED = "grouped";
	public static final String FIELD_MRCM_ATTRIBUTE_CARDINALITY = "attributeCardinality";
	public static final String FIELD_MRCM_ATTRIBUTE_IN_GROUP_CARDINALITY = "attributeInGroupCardinality";
	public static final String FIELD_MRCM_RULE_STRENGTH_ID = "ruleStrengthId";
	public static final String FIELD_MRCM_CONTENT_TYPE_ID = "contentTypeId";
	
	public static final String FIELD_MRCM_RANGE_CONSTRAINT = "rangeConstraint";
	public static final String FIELD_MRCM_ATTRIBUTE_RULE = "attributeRule";
	
	public static final String FIELD_MRCM_RULE_REFSET_ID = "mrcmRuleRefsetId";
	
	public static final ImmutableSet<String> MEMBER_FIELDS_WITH_COMPONENT_ID = ImmutableSet.of(
		// simple map and simple map w/ description
		FIELD_MAP_TARGET,
		// language
		FIELD_ACCEPTABILITY_ID,
		// attribute value
		FIELD_VALUE_ID,
		// complex, ext. map
		FIELD_CORRELATION_ID,
		// description type
		FIELD_DESCRIPTION_FORMAT,
		// concrete domain
		FIELD_CHARACTERISTIC_TYPE_ID,
		FIELD_TYPE_ID,
		// association
		FIELD_TARGET_COMPONENT,
		// ext. map
		FIELD_MAP_CATEGORY_ID,
		// MRCM attribute domain
		FIELD_MRCM_DOMAIN_ID,
		// MRCM attribute domain and attribute range
		FIELD_MRCM_CONTENT_TYPE_ID,
		FIELD_MRCM_RULE_STRENGTH_ID,
		// MRCM module scope
		FIELD_MRCM_RULE_REFSET_ID
	);
	
	public static final String[] CONCEPT_HEADER = new String[] { 
		FIELD_ID, 
		FIELD_EFFECTIVE_TIME, 
		FIELD_ACTIVE, 
		FIELD_MODULE_ID, 
		FIELD_DEFINITION_STATUS_ID };

	public static final String[] DESCRIPTION_HEADER = new String[] { 
		FIELD_ID, 
		FIELD_EFFECTIVE_TIME, 
		FIELD_ACTIVE, 
		FIELD_MODULE_ID, 
		FIELD_CONCEPT_ID, 
		FIELD_LANGUAGE_CODE, 
		FIELD_TYPE_ID, 
		FIELD_TERM, 
		FIELD_CASE_SIGNIFICANCE_ID };
	
	public static final String[] RELATIONSHIP_HEADER = new String[] { 
		FIELD_ID, 
		FIELD_EFFECTIVE_TIME, 
		FIELD_ACTIVE, 
		FIELD_MODULE_ID, 
		FIELD_SOURCE_ID, 
		FIELD_DESTINATION_ID, 
		FIELD_RELATIONSHIP_GROUP, 
		FIELD_TYPE_ID, 
		FIELD_CHARACTERISTIC_TYPE_ID, 
		FIELD_MODIFIER_ID };
	
	public static final String[] SIMPLE_TYPE_HEADER = new String[] {
		FIELD_ID, 
		FIELD_EFFECTIVE_TIME, 
		FIELD_ACTIVE, 
		FIELD_MODULE_ID, 
		FIELD_REFSET_ID, 
		FIELD_REFERENCED_COMPONENT_ID };
	
	public static final String[] QUERY_TYPE_HEADER = new String[] {
		FIELD_ID, 
		FIELD_EFFECTIVE_TIME, 
		FIELD_ACTIVE, 
		FIELD_MODULE_ID, 
		FIELD_REFSET_ID, 
		FIELD_REFERENCED_COMPONENT_ID,
		FIELD_QUERY };
	
	public static final String[] LANGUAGE_TYPE_HEADER = new String[] { 
		FIELD_ID, 
		FIELD_EFFECTIVE_TIME, 
		FIELD_ACTIVE, 
		FIELD_MODULE_ID, 
		FIELD_REFSET_ID, 
		FIELD_REFERENCED_COMPONENT_ID,
		FIELD_ACCEPTABILITY_ID };

	public static final String[] DESCRIPTION_TYPE_HEADER = new String[] {
		FIELD_ID, 
		FIELD_EFFECTIVE_TIME, 
		FIELD_ACTIVE, 
		FIELD_MODULE_ID, 
		FIELD_REFSET_ID, 
		FIELD_REFERENCED_COMPONENT_ID,
		FIELD_DESCRIPTION_FORMAT, 
		FIELD_DESCRIPTION_LENGTH };

	public static final String[] CONCRETE_DATA_TYPE_HEADER = new String[] {
		FIELD_ID, 
		FIELD_EFFECTIVE_TIME, 
		FIELD_ACTIVE, 
		FIELD_MODULE_ID, 
		FIELD_REFSET_ID, 
		FIELD_REFERENCED_COMPONENT_ID, 
		FIELD_VALUE, 
		FIELD_RELATIONSHIP_GROUP, 
		FIELD_TYPE_ID, 
		FIELD_CHARACTERISTIC_TYPE_ID };

	public static final String[] ATTRIBUTE_VALUE_TYPE_HEADER = new String[] { 
		FIELD_ID, 
		FIELD_EFFECTIVE_TIME, 
		FIELD_ACTIVE, 
		FIELD_MODULE_ID, 
		FIELD_REFSET_ID, 
		FIELD_REFERENCED_COMPONENT_ID,
		FIELD_VALUE_ID };
	
	public static final String[] ASSOCIATION_TYPE_HEADER = new String[] {
		FIELD_ID, 
		FIELD_EFFECTIVE_TIME, 
		FIELD_ACTIVE, 
		FIELD_MODULE_ID, 
		FIELD_REFSET_ID, 
		FIELD_REFERENCED_COMPONENT_ID,
		FIELD_TARGET_COMPONENT_ID };
	
	public static final String[] SIMPLE_MAP_TYPE_HEADER = new String[] {
		FIELD_ID, 
		FIELD_EFFECTIVE_TIME, 
		FIELD_ACTIVE, 
		FIELD_MODULE_ID, 
		FIELD_REFSET_ID, 
		FIELD_REFERENCED_COMPONENT_ID,
		FIELD_MAP_TARGET };
	
	public static final String[] SIMPLE_MAP_TYPE_HEADER_WITH_DESCRIPTION = new String[] {
		FIELD_ID, 
		FIELD_EFFECTIVE_TIME, 
		FIELD_ACTIVE, 
		FIELD_MODULE_ID, 
		FIELD_REFSET_ID, 
		FIELD_REFERENCED_COMPONENT_ID,
		FIELD_MAP_TARGET,
		FIELD_MAP_TARGET_DESCRIPTION };

	public static final String[] COMPLEX_MAP_TYPE_HEADER = new String[] {
		FIELD_ID, 
		FIELD_EFFECTIVE_TIME, 
		FIELD_ACTIVE, 
		FIELD_MODULE_ID, 
		FIELD_REFSET_ID, 
		FIELD_REFERENCED_COMPONENT_ID,
		FIELD_MAP_GROUP, 
		FIELD_MAP_PRIORITY, 
		FIELD_MAP_RULE, 
		FIELD_MAP_ADVICE, 
		FIELD_MAP_TARGET, 
		FIELD_CORRELATION_ID };
	
	public static final String[] EXTENDED_MAP_TYPE_HEADER = new String[] {
		FIELD_ID, 
		FIELD_EFFECTIVE_TIME, 
		FIELD_ACTIVE, 
		FIELD_MODULE_ID, 
		FIELD_REFSET_ID, 
		FIELD_REFERENCED_COMPONENT_ID,
		FIELD_MAP_GROUP, 
		FIELD_MAP_PRIORITY, 
		FIELD_MAP_RULE, 
		FIELD_MAP_ADVICE, 
		FIELD_MAP_TARGET, 
		FIELD_CORRELATION_ID,
		FIELD_MAP_CATEGORY_ID };
	
	public static final String[] MODULE_DEPENDENCY_HEADER = new String[] {
		FIELD_ID, 
		FIELD_EFFECTIVE_TIME, 
		FIELD_ACTIVE, 
		FIELD_MODULE_ID, 
		FIELD_REFSET_ID, 
		FIELD_REFERENCED_COMPONENT_ID,
		FIELD_SOURCE_EFFECTIVE_TIME,
		FIELD_TARGET_EFFECTIVE_TIME	};
	
	public static final String[] OWL_EXPRESSION_HEADER = new String[] {
		FIELD_ID, 
		FIELD_EFFECTIVE_TIME, 
		FIELD_ACTIVE, 
		FIELD_MODULE_ID, 
		FIELD_REFSET_ID, 
		FIELD_REFERENCED_COMPONENT_ID,
		FIELD_OWL_EXPRESSION };
	
	public static final String[] MRCM_DOMAIN_HEADER = new String[] {
		FIELD_ID, 
		FIELD_EFFECTIVE_TIME, 
		FIELD_ACTIVE, 
		FIELD_MODULE_ID, 
		FIELD_REFSET_ID, 
		FIELD_REFERENCED_COMPONENT_ID,
		FIELD_MRCM_DOMAIN_CONSTRAINT,
		FIELD_MRCM_PARENT_DOMAIN,
		FIELD_MRCM_PROXIMAL_PRIMITIVE_CONSTRAINT,
		FIELD_MRCM_PROXIMAL_PRIMITIVE_REFINEMENT,
		FIELD_MRCM_DOMAIN_TEMPLATE_FOR_PRECOORDINATION,
		FIELD_MRCM_DOMAIN_TEMPLATE_FOR_POSTCOORDINATION,
		FIELD_MRCM_EDITORIAL_GUIDE_REFERENCE
	};
	
	public static final String[] MRCM_ATTRIBUTE_DOMAIN_HEADER = new String[] {
		FIELD_ID, 
		FIELD_EFFECTIVE_TIME, 
		FIELD_ACTIVE, 
		FIELD_MODULE_ID, 
		FIELD_REFSET_ID, 
		FIELD_REFERENCED_COMPONENT_ID,
		FIELD_MRCM_DOMAIN_ID,
		FIELD_MRCM_GROUPED,
		FIELD_MRCM_ATTRIBUTE_CARDINALITY,
		FIELD_MRCM_ATTRIBUTE_IN_GROUP_CARDINALITY,
		FIELD_MRCM_RULE_STRENGTH_ID,
		FIELD_MRCM_CONTENT_TYPE_ID
	};
	
	public static final String[] MRCM_ATTRIBUTE_RANGE_HEADER = new String[] {
		FIELD_ID, 
		FIELD_EFFECTIVE_TIME, 
		FIELD_ACTIVE, 
		FIELD_MODULE_ID, 
		FIELD_REFSET_ID, 
		FIELD_REFERENCED_COMPONENT_ID,
		FIELD_MRCM_RANGE_CONSTRAINT,
		FIELD_MRCM_ATTRIBUTE_RULE,
		FIELD_MRCM_RULE_STRENGTH_ID,
		FIELD_MRCM_CONTENT_TYPE_ID
	};
	
	public static final String[] MRCM_MODULE_SCOPE_HEADER = new String[] {
		FIELD_ID, 
		FIELD_EFFECTIVE_TIME, 
		FIELD_ACTIVE, 
		FIELD_MODULE_ID, 
		FIELD_REFSET_ID, 
		FIELD_REFERENCED_COMPONENT_ID,
		FIELD_MRCM_RULE_REFSET_ID
	};
	
	private SnomedRf2Headers() {
		// Prevent instantiation
	}
}
