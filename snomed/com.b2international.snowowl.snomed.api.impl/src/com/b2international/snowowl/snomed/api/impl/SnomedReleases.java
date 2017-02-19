/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.api.impl;

import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.SNOMED_INT_CITATION;
import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.SNOMED_INT_ICON_PATH;
import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.SNOMED_INT_LANGUAGE;
import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.SNOMED_INT_LINK;
import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.SNOMED_NAME;
import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.SNOMED_INT_OID;
import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.SNOMED_SHORT_NAME;
import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.TERMINOLOGY_ID;
import static com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator.REPOSITORY_UUID;

import com.b2international.snowowl.api.impl.codesystem.domain.CodeSystem;
import com.b2international.snowowl.core.api.IBranchPath;

/**
 * @since 4.7
 */
public class SnomedReleases {

	private static final CodeSystem INT_CODESYSTEM = CodeSystem.builder()
			.name(SNOMED_NAME)
			.shortName(SNOMED_SHORT_NAME)
			.oid(SNOMED_INT_OID)
			.primaryLanguage(SNOMED_INT_LANGUAGE)
			.organizationLink(SNOMED_INT_LINK)
			.citation(SNOMED_INT_CITATION)
			.branchPath(IBranchPath.MAIN_BRANCH)
			.iconPath(SNOMED_INT_ICON_PATH)
			.repositoryUuid(REPOSITORY_UUID)
			.terminologyId(TERMINOLOGY_ID)
			.build();

	/**
	 * @return the code system representing SNOMED CT's International release
	 */
	public static CodeSystem internationalRelease() {
		return INT_CODESYSTEM;
	}

	/**
	 * @return the code system representing the B2i extension of SNOMED CT
	 */
	public static CodeSystem b2iExtension(String extensionPath) {
		return CodeSystem.builder()
				.name("Systematized Nomenclature of Medicine Clinical Terms, B2i extension")
				.shortName(SNOMED_SHORT_NAME + "-B2I")
				.oid(SNOMED_INT_OID + ".1000154")
				.primaryLanguage(SNOMED_INT_LANGUAGE)
				.organizationLink("https://b2i.sg/")
				.citation(SNOMED_INT_CITATION)
				.branchPath(extensionPath)
				.iconPath(SNOMED_INT_ICON_PATH)
				.repositoryUuid(REPOSITORY_UUID)
				.terminologyId(TERMINOLOGY_ID)
				.extensionOf(SNOMED_SHORT_NAME)
				.build();
	}

}
