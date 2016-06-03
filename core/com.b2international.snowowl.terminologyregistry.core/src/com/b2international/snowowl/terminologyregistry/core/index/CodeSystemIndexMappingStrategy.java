/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.terminologyregistry.core.index;

import static com.b2international.snowowl.datastore.cdo.CDOUtils.check;
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
import static com.b2international.snowowl.terminologyregistry.core.index.TerminologyRegistryIndexConstants.SYSTEM_BRANCH_PATH;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StringField;

import com.b2international.snowowl.datastore.cdo.CDOIDUtils;
import com.b2international.snowowl.datastore.index.AbstractIndexMappingStrategy;
import com.b2international.snowowl.datastore.index.mapping.Mappings;
import com.b2international.snowowl.terminologymetadata.CodeSystem;

/**
 * Index mapping strategy for {@link CodeSystem}.
 *
 */
public class CodeSystemIndexMappingStrategy extends AbstractIndexMappingStrategy {
	
	private final CodeSystem codeSystem;

	public CodeSystemIndexMappingStrategy(final CodeSystem codeSystem) {
		this.codeSystem = check(codeSystem);
	}
	
	@Override
	public Document createDocument() {
		final Document doc = new Document();
		addStringFieldIfExists(doc, SYSTEM_OID, codeSystem.getCodeSystemOID());
		addStringFieldIfExists(doc, SYSTEM_NAME, codeSystem.getName());
		addStringFieldIfExists(doc, SYSTEM_SHORT_NAME, codeSystem.getShortName());
		addStringFieldIfExists(doc, SYSTEM_ORG_LINK, codeSystem.getMaintainingOrganizationLink());
		addStringFieldIfExists(doc, SYSTEM_LANGUAGE, codeSystem.getLanguage());
		addStringFieldIfExists(doc, SYSTEM_CITATION, codeSystem.getCitation());
		addStringFieldIfExists(doc, SYSTEM_ICON_PATH, codeSystem.getIconPath());
		addStringFieldIfExists(doc, SYSTEM_TERMINOLOGY_COMPONENT_ID, codeSystem.getTerminologyComponentId());
		doc.add(new LongField(SYSTEM_STORAGE_KEY, getStorageKey(), Store.YES));
		Mappings.storageKey().addTo(doc, getStorageKey());
		addStringFieldIfExists(doc, SYSTEM_REPOSITORY_UUID, codeSystem.getRepositoryUuid());
		addStringFieldIfExists(doc, SYSTEM_BRANCH_PATH, codeSystem.getBranchPath());
		return doc;
	}

	@Override
	protected long getStorageKey() {
		return CDOIDUtils.asLong(codeSystem.cdoID());
	}

	protected void addStringFieldIfExists(final Document doc, final String fieldName, final String value) {
		if (null != value) {
			doc.add(new StringField(fieldName, value, Store.YES));
		}
	}
	
	public CodeSystem getCodeSystem() {
		return codeSystem;
	}
	
}