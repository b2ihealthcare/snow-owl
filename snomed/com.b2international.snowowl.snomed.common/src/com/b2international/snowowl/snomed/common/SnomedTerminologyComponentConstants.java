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

import java.util.Map;
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
	
	// known language dialect aliases, see more information at: https://confluence.ihtsdotools.org/display/DOCECL/Appendix+C+-+Dialect+Aliases
	public static final Map<String, String> LANG_REFSET_DIALECT_ALIASES = Map.ofEntries(
		Map.entry("554461000005103", "da-dk"),
		Map.entry("32570271000036106", "en-au"),
		Map.entry("19491000087109", "en-ca"),
		Map.entry("900000000000508004", "en-gb"),
		Map.entry("21000220103", "en-ie"),
		Map.entry("271000210107", "en-nz"),
		Map.entry("900000000000509007", "en-us"),
		Map.entry("608771002", "en-int-gmdn"),
		Map.entry("999001261000000100", "en-nhs-clinical"),
		Map.entry("999000671000001103", "en-nhs-dmd"),
		Map.entry("999000691000001104", "en-nhs-pharmacy"),
		Map.entry("999000681000001101", "en-uk-drug"),
		Map.entry("999001251000000103", "en-uk-ext"),
		Map.entry("448879004", "es"),
		Map.entry("450828004", "es-ar"),
		Map.entry("5641000179103", "es-uy"),
		Map.entry("71000181105", "et-ee"),
		Map.entry("722130004", "dr"),
		Map.entry("722131000", "fr"),
		Map.entry("21000172104", "fr-be"),
		Map.entry("20581000087109", "fr-ca"),
		Map.entry("722129009", "ja"),
		Map.entry("31000172101", "nl-be"),
		Map.entry("31000146106", "nl-nl"),
		Map.entry("61000202103", "nb-no"),
		Map.entry("91000202106", "nn-no"),
		Map.entry("46011000052107", "sv-se"),
		Map.entry("722128001", "zh")	
	);
	
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