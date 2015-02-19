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

import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.DESCRIPTION_CASE_SIGNIFICANCE_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.DESCRIPTION_CONCEPT_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.DESCRIPTION_MODULE_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.DESCRIPTION_TYPE_ID;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;

import org.apache.lucene.index.AtomicReader;
import org.apache.lucene.index.NumericDocValues;

import bak.pcj.LongCollection;

/**
 * Collector for gathering the container concept ID, the description type ID and the 
 * case significance concept IDs of a description.
 * <p>This collector supplies a mapping between description storage keys and 
 * properties. Properties are represented as an array of primitive longs.
 * The first item is the owner concept ID, then the description module ID, description type concept ID, 
 * finally the case significance concept ID.   
 *
 */
public class DescriptionPropertyCollector extends ComponentPropertyCollector {

	private NumericDocValues concpetIds;
	private NumericDocValues typeIds;
	private NumericDocValues caseSignificanceIds;
	private NumericDocValues moduleIds;

	public DescriptionPropertyCollector(final LongCollection acceptedIds) {
		super(checkNotNull(acceptedIds, "acceptedIds"));
	}

	@Override
	protected void setNextReader(final AtomicReader reader) throws IOException {
		super.setNextReader(reader);
		concpetIds = reader.getNumericDocValues(DESCRIPTION_CONCEPT_ID);
		moduleIds = reader.getNumericDocValues(DESCRIPTION_MODULE_ID);
		typeIds = reader.getNumericDocValues(DESCRIPTION_TYPE_ID);
		caseSignificanceIds = reader.getNumericDocValues(DESCRIPTION_CASE_SIGNIFICANCE_ID);
	}
	
	@Override
	protected boolean check() {
		return null != caseSignificanceIds 
			&& null != typeIds 
			&& null != concpetIds 
			&& null != storageKeys
			&& null != moduleIds;
	}
	
	@Override
	protected long[] initProperties(final int doc) {
		return new long[] { 
			concpetIds.get(doc),
			moduleIds.get(doc),
			typeIds.get(doc), 
			caseSignificanceIds.get(doc) 
		};
	}

}