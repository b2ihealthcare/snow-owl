/*
 * Copyright 2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore;

import java.util.List;
import java.util.Map;

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.snowowl.core.TerminologyResource;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.codesystem.CodeSystem;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.test.commons.SnomedContentRule;
import com.b2international.snowowl.test.commons.snomed.TestBranchContext;
import com.google.common.collect.Lists;

/**
 * @since 8.0
 */
public class CodeSystemResource {
	
	public static final ExtendedLocale US_LOCALE = new ExtendedLocale("en", "", Concepts.REFSET_LANGUAGE_TYPE_US);
	public static final ExtendedLocale GB_LOCALE = new ExtendedLocale("en", "", Concepts.REFSET_LANGUAGE_TYPE_UK);
	public static final ExtendedLocale SG_LOCALE = new ExtendedLocale("en", "", Concepts.REFSET_LANGUAGE_TYPE_SG);
	
	public static void configureCodeSystem(final TestBranchContext.Builder context) {
		final List<Map<String, Object>> languageMap = Lists.newArrayList();

		languageMap.add(Map.of(
			"languageTag", US_LOCALE.getLanguageTag(), 
			"languageRefSetIds", List.of(Concepts.REFSET_LANGUAGE_TYPE_US))
		);
		languageMap.add(Map.of(
			"languageTag", GB_LOCALE.getLanguageTag(), 
			"languageRefSetIds", List.of(Concepts.REFSET_LANGUAGE_TYPE_UK))
		);
		languageMap.add(Map.of(
			"languageTag", SG_LOCALE.getLanguageTag(), 
			"languageRefSetIds", List.of(Concepts.REFSET_LANGUAGE_TYPE_SG))
		);

		final CodeSystem cs = new CodeSystem();
		cs.setBranchPath(Branch.MAIN_PATH);
		cs.setId(SnomedContentRule.SNOMEDCT_ID);
		cs.setSettings(Map.of(SnomedTerminologyComponentConstants.CODESYSTEM_LANGUAGE_CONFIG_KEY, languageMap));

		context.with(TerminologyResource.class, cs);
	}
}
