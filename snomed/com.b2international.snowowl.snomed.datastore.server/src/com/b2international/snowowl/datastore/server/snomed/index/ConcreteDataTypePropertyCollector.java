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
package com.b2international.snowowl.datastore.server.snomed.index;

import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_CONTAINER_MODULE_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_MODULE_ID;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;

import org.apache.lucene.index.AtomicReader;
import org.apache.lucene.index.NumericDocValues;

import bak.pcj.LongCollection;

/**
 * Class for collecting CDT properties such as module ID and container (concept or relationship)
 * module ID.
 *
 */
public class ConcreteDataTypePropertyCollector extends ComponentPropertyCollector {

	private NumericDocValues moduleIds;
	private NumericDocValues containerModuleIds;

	public ConcreteDataTypePropertyCollector(final LongCollection acceptedIds) {
		super(checkNotNull(acceptedIds, "acceptedIds"));
	}

	@Override
	protected void setNextReader(final AtomicReader reader) throws IOException {
		super.setNextReader(reader);
		moduleIds = reader.getNumericDocValues(REFERENCE_SET_MEMBER_MODULE_ID);
		containerModuleIds = reader.getNumericDocValues(REFERENCE_SET_MEMBER_CONTAINER_MODULE_ID);
	}
	
	@Override
	protected boolean check() {
		return null != containerModuleIds
			&& null != storageKeys
			&& null != moduleIds;
	}
	
	@Override
	protected Object initProperties(final int doc) {
		return new long[] {
			moduleIds.get(doc),
			containerModuleIds.get(doc)
		};
	}

}