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

import static com.google.common.base.Preconditions.checkState;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.google.common.base.Strings;

public abstract class SnomedTerminologyComponentConstants {
	
	// suppress constructor
	private SnomedTerminologyComponentConstants() {}

	public static final String TOOLING_ID = "snomed";

	public static final String DEFAULT_NAMESPACE_PATTERN = ".*\\{(\\d{7})\\}.*";
	
	// configuration keys for managing Module and Namespace configuration in CodeSystem entries
	public static final String CODESYSTEM_MODULES_CONFIG_KEY = "moduleIds";
	public static final String CODESYSTEM_NAMESPACE_CONFIG_KEY = "namespace";
	public static final String CODESYSTEM_DESCRIPTION_COPY_POLICY_CONFIG_KEY = "descriptionCopyPolicy";
	public static final String CODESYSTEM_MAINTAINER_TYPE_CONFIG_KEY = "maintainerType";
	public static final String CODESYSTEM_RF2_EXPORT_LAYOUT_CONFIG_KEY = "refSetExportLayout";
	public static final String CODESYSTEM_NRC_COUNTRY_CODE_CONFIG_KEY = "nrcCountryCode";
	public static final String CODESYSTEM_LANGUAGE_CONFIG_KEY = "languages";
	
	// FHIR specific constants
	public static final String SNOMED_URI_BASE = "http://snomed.info";
	public static final String SNOMED_URI_SCT = SNOMED_URI_BASE + "/sct";
	public static final String SNOMED_URI_DEV = SNOMED_URI_BASE + "/xsct";
	public static final String SNOMED_URI_ID = SNOMED_URI_BASE + "/id";
	
	
	public static String getNamespace(String conceptId, String fsn) {
		return getNamespace(DEFAULT_NAMESPACE_PATTERN, conceptId, fsn);
	}
	
	public static String getNamespace(String namespacePattern, String conceptId, String fsn) {
		if (Concepts.CORE_NAMESPACE.equals(conceptId) || Strings.isNullOrEmpty(conceptId) || Strings.isNullOrEmpty(fsn)) {
			return "";
		} else {
			final Matcher matcher = Pattern.compile(namespacePattern).matcher(fsn);
			checkState(matcher.matches(), "Pattern %s does not match on input: %s", namespacePattern, fsn);
			return matcher.group(1);
		}
	}
	
}