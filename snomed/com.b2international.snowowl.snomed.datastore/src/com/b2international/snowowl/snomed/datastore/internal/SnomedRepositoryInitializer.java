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
package com.b2international.snowowl.snomed.datastore.internal;

import com.b2international.snowowl.core.codesystem.CodeSystem;
import com.b2international.snowowl.core.repository.TerminologyRepositoryInitializer;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * Repository initializer for the SNOMED CT tooling.
 */
public final class SnomedRepositoryInitializer extends TerminologyRepositoryInitializer {

	@Override
	protected CodeSystem createPrimaryCodeSystem() {
		
		final ImmutableMap<String,Object> additionalProperties = ImmutableMap.of(SnomedTerminologyComponentConstants.CODESYSTEM_MODULES_CONFIG_KEY, ImmutableList.of(Concepts.MODULE_SCT_CORE));
		
		return CodeSystem.builder()
				.name(SnomedTerminologyComponentConstants.SNOMED_NAME)
				.shortName(SnomedTerminologyComponentConstants.SNOMED_SHORT_NAME)
				.citation(SnomedTerminologyComponentConstants.SNOMED_INT_CITATION)
				.iconPath(SnomedTerminologyComponentConstants.SNOMED_INT_ICON_PATH)
				.primaryLanguage(SnomedTerminologyComponentConstants.SNOMED_INT_LANGUAGE)
				.organizationLink(SnomedTerminologyComponentConstants.SNOMED_INT_LINK)
				.oid(SnomedTerminologyComponentConstants.SNOMED_INT_OID)
				.terminologyId(SnomedTerminologyComponentConstants.TERMINOLOGY_ID)
				.additionalProperties(additionalProperties)
				.build();
	}

}
