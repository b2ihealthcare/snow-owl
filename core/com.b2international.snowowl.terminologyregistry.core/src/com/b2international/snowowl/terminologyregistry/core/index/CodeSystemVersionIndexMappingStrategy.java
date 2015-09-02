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

import static com.b2international.snowowl.core.date.Dates.getTime;
import static com.b2international.snowowl.datastore.cdo.CDOUtils.check;
import static com.b2international.snowowl.terminologyregistry.core.index.TerminologyRegistryIndexConstants.VERSION_DESCRIPTION;
import static com.b2international.snowowl.terminologyregistry.core.index.TerminologyRegistryIndexConstants.VERSION_EFFECTIVE_DATE;
import static com.b2international.snowowl.terminologyregistry.core.index.TerminologyRegistryIndexConstants.VERSION_IMPORT_DATE;
import static com.b2international.snowowl.terminologyregistry.core.index.TerminologyRegistryIndexConstants.VERSION_LATEST_UPDATE_DATE;
import static com.b2international.snowowl.terminologyregistry.core.index.TerminologyRegistryIndexConstants.VERSION_REPOSITORY_UUID;
import static com.b2international.snowowl.terminologyregistry.core.index.TerminologyRegistryIndexConstants.VERSION_STORAGE_KEY;
import static com.b2international.snowowl.terminologyregistry.core.index.TerminologyRegistryIndexConstants.VERSION_VERSION_ID;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StringField;

import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.datastore.cdo.CDOIDUtils;
import com.b2international.snowowl.datastore.index.AbstractIndexMappingStrategy;
import com.b2international.snowowl.datastore.index.mapping.Mappings;
import com.b2international.snowowl.terminologymetadata.CodeSystemVersion;

/**
 * Index mapping strategy for {@link CodeSystemVersion}.
 *
 */
public class CodeSystemVersionIndexMappingStrategy extends AbstractIndexMappingStrategy {

	private final CodeSystemVersion version;
	
	public CodeSystemVersionIndexMappingStrategy(final CodeSystemVersion version) {
		this.version = check(version);
	}
	
	@Override
	public Document createDocument() {
		final Document doc = new Document();
		doc.add(new LongField(VERSION_IMPORT_DATE, getTime(version.getImportDate()), Store.YES));
		doc.add(new LongField(VERSION_EFFECTIVE_DATE, EffectiveTimes.getEffectiveTime(version.getEffectiveDate()), Store.YES));
		addStringFieldIfExists(doc, VERSION_DESCRIPTION, version.getDescription());
		addStringFieldIfExists(doc, VERSION_VERSION_ID, version.getVersionId());
		// XXX using EffectiveTimes here to handle possible null lastUpdateDate values 
		doc.add(new LongField(VERSION_LATEST_UPDATE_DATE, EffectiveTimes.getEffectiveTime(version.getLastUpdateDate()), Store.YES));
		doc.add(new LongField(VERSION_STORAGE_KEY, getStorageKey(), Store.YES));
		Mappings.storageKey().addTo(doc, getStorageKey());
		addStringFieldIfExists(doc, VERSION_REPOSITORY_UUID, version.getCodeSystemVersionGroup().getRepositoryUuid());
		return doc;
	}

	@Override
	protected long getStorageKey() {
		return CDOIDUtils.asLong(version.cdoID());
	}
	
	private void addStringFieldIfExists(final Document doc, final String fieldName, final String value) {
		if (null != value) {
			doc.add(new StringField(fieldName, value, Store.YES));
		}
	}

}