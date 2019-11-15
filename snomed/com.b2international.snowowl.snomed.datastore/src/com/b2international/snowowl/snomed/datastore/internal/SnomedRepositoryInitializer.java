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
package com.b2international.snowowl.snomed.datastore.internal;

import java.util.Collections;

import com.b2international.snowowl.core.repository.TerminologyRepositoryInitializer;
import com.b2international.snowowl.datastore.CodeSystem;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;

/**
 * Repository initializer for the SNOMED CT tooling.
 */
public final class SnomedRepositoryInitializer extends TerminologyRepositoryInitializer {

	@Override
	protected CodeSystem createPrimaryCodeSystem() {
		return CodeSystem.builder()
				.name(SnomedTerminologyComponentConstants.SNOMED_NAME)
				.shortName(SnomedTerminologyComponentConstants.SNOMED_SHORT_NAME)
				.citation(SnomedTerminologyComponentConstants.SNOMED_INT_CITATION)
				.iconPath(SnomedTerminologyComponentConstants.SNOMED_INT_ICON_PATH)
				.language(SnomedTerminologyComponentConstants.SNOMED_INT_LANGUAGE)
				.orgLink(SnomedTerminologyComponentConstants.SNOMED_INT_LINK)
				.oid(SnomedTerminologyComponentConstants.SNOMED_INT_OID)
				.toolingId(SnomedTerminologyComponentConstants.TERMINOLOGY_ID)
				.repositoryId(SnomedDatastoreActivator.REPOSITORY_UUID)
				.uris(Collections.singletonList("http://snomed.info/sct"))
				.build();
	}

}
