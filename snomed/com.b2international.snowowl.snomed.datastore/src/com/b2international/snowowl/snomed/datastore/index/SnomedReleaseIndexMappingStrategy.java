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

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.IntField;

import com.b2international.snowowl.snomed.SnomedRelease;
import com.b2international.snowowl.terminologyregistry.core.index.CodeSystemIndexMappingStrategy;

/**
 * @since 4.7
 */
public class SnomedReleaseIndexMappingStrategy extends CodeSystemIndexMappingStrategy {

	public SnomedReleaseIndexMappingStrategy(final SnomedRelease snomedRelease) {
		super(snomedRelease);
	}

	@Override
	public Document createDocument() {
		final Document document = super.createDocument();

		addStringFieldIfExists(document, SNOMED_RELEASE_BASE_CODE_SYSTEM_OID, getCodeSystem().getBaseCodeSystemOID());
		document.add(new IntField(SNOMED_RELEASE_RELEASE_TYPE, getCodeSystem().getReleaseType().getValue(), Store.YES));

		return document;
	}

	@Override
	public SnomedRelease getCodeSystem() {
		return (SnomedRelease) super.getCodeSystem();
	}

}
