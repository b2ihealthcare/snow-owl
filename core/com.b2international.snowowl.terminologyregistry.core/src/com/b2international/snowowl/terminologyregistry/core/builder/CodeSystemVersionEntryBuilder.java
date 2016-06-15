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
package com.b2international.snowowl.terminologyregistry.core.builder;

import static com.b2international.snowowl.terminologyregistry.core.index.TerminologyRegistryIndexConstants.VERSION_DESCRIPTION;
import static com.b2international.snowowl.terminologyregistry.core.index.TerminologyRegistryIndexConstants.VERSION_EFFECTIVE_DATE;
import static com.b2international.snowowl.terminologyregistry.core.index.TerminologyRegistryIndexConstants.VERSION_IMPORT_DATE;
import static com.b2international.snowowl.terminologyregistry.core.index.TerminologyRegistryIndexConstants.VERSION_LATEST_UPDATE_DATE;
import static com.b2international.snowowl.terminologyregistry.core.index.TerminologyRegistryIndexConstants.VERSION_REPOSITORY_UUID;
import static com.b2international.snowowl.terminologyregistry.core.index.TerminologyRegistryIndexConstants.VERSION_STORAGE_KEY;
import static com.b2international.snowowl.terminologyregistry.core.index.TerminologyRegistryIndexConstants.VERSION_SYSTEM_SHORT_NAME;
import static com.b2international.snowowl.terminologyregistry.core.index.TerminologyRegistryIndexConstants.VERSION_VERSION_ID;
import static com.b2international.snowowl.terminologyregistry.core.index.TerminologyRegistryIndexConstants.VERSION_PARENT_BRANCH_PATH;

import org.apache.lucene.document.Document;

import com.b2international.snowowl.datastore.CodeSystemVersionEntry;
import com.b2international.snowowl.datastore.index.mapping.Mappings;

/**
 * @since 4.7
 */
public class CodeSystemVersionEntryBuilder {

	public CodeSystemVersionEntry build(final Document doc) {
		return new CodeSystemVersionEntry(
				Mappings.longField(VERSION_IMPORT_DATE).getValue(doc),
				Mappings.longField(VERSION_EFFECTIVE_DATE).getValue(doc),
				Mappings.longField(VERSION_LATEST_UPDATE_DATE).getValue(doc),
				doc.get(VERSION_DESCRIPTION),
				doc.get(VERSION_VERSION_ID), 
				doc.get(VERSION_PARENT_BRANCH_PATH) == null ? "MAIN" : doc.get(VERSION_PARENT_BRANCH_PATH),
				Mappings.longDocValuesField(VERSION_STORAGE_KEY).getValue(doc),
				doc.get(VERSION_REPOSITORY_UUID),
				doc.get(VERSION_SYSTEM_SHORT_NAME));
	}

}
