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
package com.b2international.snowowl.snomed.metadata;

import java.util.Collection;

import com.b2international.commons.pcj.LongSets;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.datastore.SnomedTerminologyBrowser;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptIndexEntry;
import com.google.inject.Provider;

/**
 * @since 4.3
 */
public class SnomedMetadataImpl implements SnomedMetadata {

	private Provider<SnomedTerminologyBrowser> browser;

	public SnomedMetadataImpl(Provider<SnomedTerminologyBrowser> browser) {
		this.browser = browser;
	}
	
	@Override
	public Collection<String> getCharacteristicTypeIds(IBranchPath branchPath) {
		return LongSets.toStringSet(getTerminologyBrowser().getAllSubTypeIds(branchPath, Long.valueOf(Concepts.CHARACTERISTIC_TYPE)));
	}

	@Override
	public Collection<SnomedConceptIndexEntry> getCharacteristicTypes(IBranchPath branchPath) {
		return getTerminologyBrowser().getAllSubTypesById(branchPath, Concepts.CHARACTERISTIC_TYPE);
	}

	private SnomedTerminologyBrowser getTerminologyBrowser() {
		return browser.get();
	}

}
