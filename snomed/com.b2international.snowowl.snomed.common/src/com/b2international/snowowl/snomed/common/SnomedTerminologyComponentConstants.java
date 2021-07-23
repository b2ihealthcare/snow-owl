/*
 * Copyright 2011-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.regex.Pattern;

import com.b2international.commons.VerhoeffCheck;
import com.google.common.base.Strings;

public abstract class SnomedTerminologyComponentConstants {
	
	// suppress constructor
	private SnomedTerminologyComponentConstants() {}

	public static final String TOOLING_ID = "snomed";
	

	// configuration keys for managing Module and Namespace configuration in CodeSystem entries
	public static final String CODESYSTEM_MODULES_CONFIG_KEY = "moduleIds";
	public static final String CODESYSTEM_NAMESPACE_CONFIG_KEY = "namespace";
	public static final String CODESYSTEM_DESCRIPTION_COPY_POLICY_CONFIG_KEY = "descriptionCopyPolicy";
	public static final String CODESYSTEM_MAINTAINER_TYPE_CONFIG_KEY = "maintainerType";
	public static final String CODESYSTEM_RF2_EXPORT_LAYOUT_CONFIG_KEY = "refSetExportLayout";
	public static final String CODESYSTEM_NRC_COUNTRY_CODE_CONFIG_KEY = "nrcCountryCode";
	
	// FHIR specific constants
	public static final String SNOMED_URI_BASE = "http://snomed.info";
	public static final String SNOMED_URI_SCT = SNOMED_URI_BASE + "/sct";
	public static final String SNOMED_URI_DEV = SNOMED_URI_BASE + "/xsct";
	public static final String SNOMED_URI_ID = SNOMED_URI_BASE + "/id";
	
//	public static String getTerminologyComponentId(final String referencedComponentId) {
//		switch (getTerminologyComponentIdValue(referencedComponentId)) {
//			case CONCEPT_NUMBER: return CONCEPT;
//			case DESCRIPTION_NUMBER: return DESCRIPTION;
//			case RELATIONSHIP_NUMBER: return RELATIONSHIP;
//			default: throw new IllegalArgumentException("'" + referencedComponentId + "' referenced component type is unknown");
//		}
//	}
//
//	public static boolean isCoreComponentId(String componentId) {
//		return isCoreComponentType(getTerminologyComponentIdValueSafe(componentId));
//	}
//
//	public static boolean isCoreComponentType(short componentType) {
//		return SnomedConcept.TYPE == componentType || 
//				SnomedDescription.TYPE == componentType || 
//				SnomedRelationship.TYPE == componentType;
//	}
	
}