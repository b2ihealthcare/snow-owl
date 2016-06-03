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

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;

import com.b2international.snowowl.datastore.CodeSystemEntry;
import com.b2international.snowowl.datastore.ICodeSystem;
import com.b2international.snowowl.datastore.cdo.CDOIDUtils;
import com.b2international.snowowl.datastore.cdo.CDOUtils;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

/**
 * Factory for instantiating {@link ICodeSystem} instances.
 *
 */
public abstract class CodeSystemFactory {

	public static Document createDocument(final com.b2international.snowowl.terminologymetadata.CodeSystem codeSystem) {
		
		CDOUtils.check(codeSystem);
		final Document doc = new Document();
		
		doc.add(new StoredField(TerminologyRegistryIndexConstants.SYSTEM_NAME, Strings.nullToEmpty(codeSystem.getName())));
		doc.add(new StoredField(TerminologyRegistryIndexConstants.SYSTEM_ORG_LINK, Strings.nullToEmpty(codeSystem.getMaintainingOrganizationLink())));
		doc.add(new StoredField(TerminologyRegistryIndexConstants.SYSTEM_LANGUAGE, Strings.nullToEmpty(codeSystem.getLanguage())));
		doc.add(new StoredField(TerminologyRegistryIndexConstants.SYSTEM_CITATION, Strings.nullToEmpty(codeSystem.getCitation())));
		doc.add(new StoredField(TerminologyRegistryIndexConstants.SYSTEM_ICON_PATH, Strings.nullToEmpty(codeSystem.getIconPath())));

		doc.add(new StringField(TerminologyRegistryIndexConstants.SYSTEM_TERMINOLOGY_COMPONENT_ID, Strings.nullToEmpty(codeSystem.getTerminologyComponentId()), Store.YES));
		doc.add(new StringField(TerminologyRegistryIndexConstants.SYSTEM_SHORT_NAME, Strings.nullToEmpty(codeSystem.getShortName()), Store.YES));
		doc.add(new StringField(TerminologyRegistryIndexConstants.SYSTEM_OID, Strings.nullToEmpty(codeSystem.getCodeSystemOID()), Store.YES));
		doc.add(new StringField(TerminologyRegistryIndexConstants.SYSTEM_STORAGE_KEY, Long.toString(CDOIDUtils.asLong(codeSystem.cdoID())), Store.YES));
		doc.add(new StringField(TerminologyRegistryIndexConstants.SYSTEM_REPOSITORY_UUID, codeSystem.getRepositoryUuid(), Store.YES));
		doc.add(new StringField(TerminologyRegistryIndexConstants.SYSTEM_BRANCH_PATH, codeSystem.getBranchPath(), Store.YES));
		
		return doc;
	}
	
	public static ICodeSystem createCodeSystemEntry(final Document doc) {
		return new CodeSystemEntry(
			Preconditions.checkNotNull(doc).get(TerminologyRegistryIndexConstants.SYSTEM_OID),
			doc.get(TerminologyRegistryIndexConstants.SYSTEM_NAME),
			doc.get(TerminologyRegistryIndexConstants.SYSTEM_SHORT_NAME),
			doc.get(TerminologyRegistryIndexConstants.SYSTEM_ORG_LINK),
			doc.get(TerminologyRegistryIndexConstants.SYSTEM_LANGUAGE),
			doc.get(TerminologyRegistryIndexConstants.SYSTEM_CITATION),
			doc.get(TerminologyRegistryIndexConstants.SYSTEM_ICON_PATH),
			doc.get(TerminologyRegistryIndexConstants.SYSTEM_TERMINOLOGY_COMPONENT_ID),
			doc.get(TerminologyRegistryIndexConstants.SYSTEM_STORAGE_KEY),
			doc.get(TerminologyRegistryIndexConstants.SYSTEM_REPOSITORY_UUID),
			doc.get(TerminologyRegistryIndexConstants.SYSTEM_BRANCH_PATH)
			);
	}
	
	public static ICodeSystem createCodeSystemEntry(final com.b2international.snowowl.terminologymetadata.CodeSystem system) {
		return new CodeSystemEntry(
				system.getCodeSystemOID(), 
				system.getName(), 
				system.getShortName(), 
				system.getMaintainingOrganizationLink(), 
				system.getLanguage(), 
				system.getCitation(), 
				system.getIconPath(), 
				system.getTerminologyComponentId(), 
				CDOUtils.isTransient(system) ? CDOUtils.NO_STORAGE_KEY + "" : Long.toString(CDOIDUtils.asLong(system.cdoID())),
				system.getRepositoryUuid(),
				system.getBranchPath());
	}
	
	private CodeSystemFactory() {
		//suppress instantiation
	}
	
}