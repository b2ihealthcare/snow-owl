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
package com.b2international.snowowl.snomed.exporter.server.refset;

import java.io.IOException;

import org.apache.lucene.index.AtomicReader;
import org.apache.lucene.index.NumericDocValues;

import bak.pcj.map.LongKeyLongMap;
import bak.pcj.map.LongKeyLongOpenHashMap;

import com.b2international.snowowl.datastore.index.AbstractDocsOutOfOrderCollector;
import com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants;

/**
 * Collector for gathering the description ID and the description type concept ID of a subset of SNOMED CT
 * descriptions.
 */
public class DescriptionIdTypeCollector extends AbstractDocsOutOfOrderCollector {

	private NumericDocValues descriptionIds;
	private NumericDocValues typeIds;

	private final LongKeyLongMap map;

	/**
	 * Creates a new collector with a backing map of the specified initial size.
	 * 
	 * @param expectedSize the expected number of entries in the collector's backing map, or <= 0 to use the default size
	 */
	public DescriptionIdTypeCollector(final int expectedSize) {
		map = expectedSize > 0 ? new LongKeyLongOpenHashMap(expectedSize) : new LongKeyLongOpenHashMap();
	}

	@Override
	public void collect(final int docId) throws IOException {
		map.put(descriptionIds.get(docId), typeIds.get(docId));
	}

	@Override
	protected void initDocValues(final AtomicReader leafReader) throws IOException {
		descriptionIds = leafReader.getNumericDocValues(SnomedIndexBrowserConstants.COMPONENT_ID);
		typeIds = leafReader.getNumericDocValues(SnomedIndexBrowserConstants.DESCRIPTION_TYPE_ID);
	}

	@Override
	protected boolean isLeafCollectible() {
		return descriptionIds != null && typeIds != null;
	}

	/**
	 * Returns a mapping between description IDs and the description type concept IDs.
	 * 
	 * @return the collected description ID-type ID mapping
	 */
	public LongKeyLongMap getIdMap() {
		return map;
	}
}
