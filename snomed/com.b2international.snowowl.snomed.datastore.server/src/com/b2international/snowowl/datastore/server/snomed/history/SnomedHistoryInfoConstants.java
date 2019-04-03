/*
 * Copyright 2011-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore.server.snomed.history;

/**
 * Provides constants for SNOMED history information generation.
 */
public abstract class SnomedHistoryInfoConstants {
	
	public static final String CASE_SIGNIFICANCE_FEATURE_NAME = "caseSignificance";
	public static final String STATUS_FEATURE_NAME = "active";
	public static final String MODULE_FEATURE_NAME = "module";
	public static final String MODULE_ID_FEATURE_NAME = "moduleId";
	public static final String OPERATOR_TYPE_FEATURE_NAME = "operatorType";
	public static final String UNIT_TYPE_FEATURE_NAME = "unitType";
	public static final String VALUE_FEATURE_NAME = "value";
	public static final String DEFINITION_STATUS_FEATURE_NAME = "definitionStatus";
	public static final String EFFECTIVE_TIME_FEATURE_NAME = "effectiveTime";
	public static final String EXHAUSTIVE_FEATURE_NAME = "exhaustive";
	public static final String MAP_TARGET_TYPE_FEATURE_NAME = "mapTargetComponentType";
	public static final String DESCRIPTION_TYPE_FEATURE_NAME = "type";
	public static final String RELEASED_FEATURE_NAME = "released";
	public static final String RELATIONSHIP_TYPE_FEATURE_NAME = "type";
	public static final String DESCRIPTION_TERM_FEATURE_NAME = "term";
	public static final String GROUP_FEATURE_NAME = "group";
	public static final String UNION_GROUP_FEATURE_NAME = "unionGroup";
	public static final String CHARACTERISTIC_TYPE_FEATURE_NAME = "characteristicType";
	public static final String MODIFIER_FEATURE_NAME = "modifier";
	public static final String CORRELATION_ID_FEATURE_NAME = "correlationId";
	public static final String MAP_GROUP_FEATURE_NAME = "mapGroup";
	public static final String VALUE_ID_FEATURE_NAME = "valueId";
	public static final String DESCRIPTION_LENGTH_FEATURE_NAME = "descriptionLength";
	public static final String DESCRIPTION_FORMAT_FEATURE_NAME = "descriptionFormat";
	public static final String SOURCE_EFFECTIVE_TIME_FEATURE_NAME = "sourceEffectiveTime";
	public static final String TARGET_EFFECTIVE_TIME_FEATURE_NAME = "targetEffectiveTime";
	public static final String ACCEPTABILITY_ID_FEATURE_NAME = "acceptabilityId";
	public static final String SERIALIZED_VALUE_FEATURE_NAME = "serializedValue";
	
	private SnomedHistoryInfoConstants() {
		throw new UnsupportedOperationException("This class is not supposed to be instantiated.");
	}
}
