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
package com.b2international.snowowl.snomed.datastore.index;

import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.SNOMED_RELEASE_BASE_CODE_SYSTEM_OID;
import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.SNOMED_RELEASE_RELEASE_TYPE;
import static com.b2international.snowowl.terminologyregistry.core.index.TerminologyRegistryIndexConstants.SYSTEM_BRANCH_PATH;
import static com.b2international.snowowl.terminologyregistry.core.index.TerminologyRegistryIndexConstants.SYSTEM_CITATION;
import static com.b2international.snowowl.terminologyregistry.core.index.TerminologyRegistryIndexConstants.SYSTEM_ICON_PATH;
import static com.b2international.snowowl.terminologyregistry.core.index.TerminologyRegistryIndexConstants.SYSTEM_LANGUAGE;
import static com.b2international.snowowl.terminologyregistry.core.index.TerminologyRegistryIndexConstants.SYSTEM_NAME;
import static com.b2international.snowowl.terminologyregistry.core.index.TerminologyRegistryIndexConstants.SYSTEM_OID;
import static com.b2international.snowowl.terminologyregistry.core.index.TerminologyRegistryIndexConstants.SYSTEM_ORG_LINK;
import static com.b2international.snowowl.terminologyregistry.core.index.TerminologyRegistryIndexConstants.SYSTEM_REPOSITORY_UUID;
import static com.b2international.snowowl.terminologyregistry.core.index.TerminologyRegistryIndexConstants.SYSTEM_SHORT_NAME;
import static com.b2international.snowowl.terminologyregistry.core.index.TerminologyRegistryIndexConstants.SYSTEM_STORAGE_KEY;
import static com.b2international.snowowl.terminologyregistry.core.index.TerminologyRegistryIndexConstants.SYSTEM_TERMINOLOGY_COMPONENT_ID;

import org.apache.lucene.document.Document;

import com.b2international.snowowl.snomed.SnomedReleaseType;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.terminologyregistry.core.builder.CodeSystemEntryBuilder;
import com.google.common.base.Preconditions;

/**
 * @since 4.7
 */
public class SnomedReleaseEntryBuilder implements CodeSystemEntryBuilder {

	@Override
	public String getRepositoryUuid() {
		return SnomedDatastoreActivator.REPOSITORY_UUID;
	}

	@Override
	public SnomedReleaseEntry build(final Document doc) {
		return new SnomedReleaseEntry(
				Preconditions.checkNotNull(doc).get(SYSTEM_OID),
				doc.get(SYSTEM_NAME),
				doc.get(SYSTEM_SHORT_NAME),
				doc.get(SYSTEM_ORG_LINK),
				doc.get(SYSTEM_LANGUAGE),
				doc.get(SYSTEM_CITATION),
				doc.get(SYSTEM_ICON_PATH),
				doc.get(SYSTEM_TERMINOLOGY_COMPONENT_ID),
				doc.get(SYSTEM_STORAGE_KEY),
				doc.get(SYSTEM_REPOSITORY_UUID),
				doc.get(SYSTEM_BRANCH_PATH),
				doc.get(SNOMED_RELEASE_BASE_CODE_SYSTEM_OID),
				SnomedReleaseType.get(Integer.valueOf(doc.get(SNOMED_RELEASE_RELEASE_TYPE)))
				);
	}

}
