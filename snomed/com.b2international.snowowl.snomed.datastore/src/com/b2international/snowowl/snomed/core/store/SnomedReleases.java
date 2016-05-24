/*******************************************************************************
 * Copyright (c) 2016 B2i Healthcare. All rights reserved.
 *******************************************************************************/
package com.b2international.snowowl.snomed.core.store;

import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.CONCEPT;
import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.SNOMED_INT_CITATION;
import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.SNOMED_INT_ICON_PATH;
import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.SNOMED_INT_LANGUAGE;
import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.SNOMED_INT_LINK;
import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.SNOMED_INT_NAME;
import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.SNOMED_INT_OID;
import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.SNOMED_INT_SHORT_NAME;
import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.TERMINOLOGY_ID;

import com.b2international.snowowl.datastore.CodeSystemUtils;
import com.b2international.snowowl.snomed.SnomedReleaseType;

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
				.withRepositoryUUID(CodeSystemUtils.getRepositoryUuid(TERMINOLOGY_ID));
	}
	
}
