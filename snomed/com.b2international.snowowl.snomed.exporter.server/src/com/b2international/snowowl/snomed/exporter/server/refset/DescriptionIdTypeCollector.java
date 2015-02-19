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

import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.search.FieldCache;
import org.apache.lucene.search.FieldCache.Longs;

import bak.pcj.map.LongKeyLongMap;
import bak.pcj.map.LongKeyLongOpenHashMap;

import com.b2international.snowowl.datastore.index.AbstractDocsOutOfOrderCollector;
import com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants;

/**
 * Collector for gathering the description ID and the description type concept ID
 * of a subset of SNOMED&nbsp;CT descriptions.
 */
public class DescriptionIdTypeCollector extends AbstractDocsOutOfOrderCollector {

	private Longs descriptionIds;
	private Longs typeIds;

	private final LongKeyLongMap map;
	
	public DescriptionIdTypeCollector(final int expectedSize) {
		map = expectedSize > 0 ? new LongKeyLongOpenHashMap(expectedSize) : new LongKeyLongOpenHashMap();
	}
	
	/* (non-Javadoc)
	 * @see org.apache.lucene.search.Collector#collect(int)
	 */
	@Override
	public void collect(int doc) throws IOException {
		map.put(descriptionIds.get(doc), typeIds.get(doc));
	}

	/* (non-Javadoc)
	 * @see org.apache.lucene.search.Collector#setNextReader(org.apache.lucene.index.AtomicReaderContext)
	 */
	@Override
	public void setNextReader(AtomicReaderContext context) throws IOException {
		descriptionIds = FieldCache.DEFAULT.getLongs(context.reader(), SnomedIndexBrowserConstants.COMPONENT_ID, false);
		typeIds = FieldCache.DEFAULT.getLongs(context.reader(), SnomedIndexBrowserConstants.DESCRIPTION_TYPE_ID, false);
	}

	/**Returns with a mapping between description IDs and the description type concept IDs.*/
	public LongKeyLongMap getIdMap() {
		return map;
	}
	
}