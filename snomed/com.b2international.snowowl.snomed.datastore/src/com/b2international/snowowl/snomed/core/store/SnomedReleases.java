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
package com.b2international.snowowl.snomed.core.store;

import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.CONCEPT;
import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.SNOMED_INT_CITATION;
import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.SNOMED_INT_ICON_PATH;
import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.SNOMED_INT_LANGUAGE;
import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.SNOMED_INT_LINK;
import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.SNOMED_INT_NAME;
import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.SNOMED_INT_OID;
import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.SNOMED_INT_SHORT_NAME;

import com.b2international.snowowl.snomed.SnomedReleaseType;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;

/**
 * @since 4.7
 */
public class SnomedReleases {

	public static SnomedReleaseBuilder newSnomedRelease() {
		return new SnomedReleaseBuilder();
	}
	
	public static SnomedReleaseBuilder newSnomedInternationalRelease() {
		return new SnomedReleaseBuilder()
				.withName(SNOMED_INT_NAME)
				.withShortName(SNOMED_INT_SHORT_NAME)
				.withCodeSystemOid(SNOMED_INT_OID)
				.withBaseCodeSystemOid(SNOMED_INT_OID) // XXX the same intentionally
				.withLanguage(SNOMED_INT_LANGUAGE)
				.withIconPath(SNOMED_INT_ICON_PATH)
				.withMaintainingOrganizationLink(SNOMED_INT_LINK)
				.withCitation(SNOMED_INT_CITATION)
				.withType(SnomedReleaseType.INTERNATIONAL)
				.withTerminologyComponentId(CONCEPT)
				.withRepositoryUUID(SnomedDatastoreActivator.REPOSITORY_UUID);
	}
	
	public static SnomedVersionBuilder newSnomedVersion() {
		return new SnomedVersionBuilder();
	}
}
