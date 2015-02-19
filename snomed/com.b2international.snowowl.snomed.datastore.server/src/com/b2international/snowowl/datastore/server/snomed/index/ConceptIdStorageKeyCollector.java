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

import java.io.IOException;

import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.NumericDocValues;

import bak.pcj.list.LongArrayList;
import bak.pcj.list.LongList;

import com.b2international.snowowl.datastore.index.AbstractDocsOutOfOrderCollector;
import com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants;
import com.google.common.base.Preconditions;

/**
 * Custom collector for extracting SNOMED&nbsp;CT concept IDs and the unique storage keys (CDO IDs) as 
 * primitive longs.
 *
 */
public class ConceptIdStorageKeyCollector extends AbstractDocsOutOfOrderCollector {

	/**
	 * Default expected size. Value: {@value}.
	 */
	private static final int DEFAULT_SIZE = 600000;
	
	private final LongList conceptIds;
	private final LongList storageKeys;
	
	private NumericDocValues conceptIdsSource;
	private NumericDocValues storageKeySource;
	
	/**
	 * Creates a collector instance with a backing 2D array initialized with the default expected size.
	 */
	public ConceptIdStorageKeyCollector() {
		this(DEFAULT_SIZE);
	}
	
	/**
	 * Creates a collector instance with a backing 2D array initialized with the specified expected size.
	 * @param expectedSize the expected size for the backing 2D array.
	 */
	public ConceptIdStorageKeyCollector(final int expectedSize) {
		conceptIds = 0 > expectedSize ? new LongArrayList(expectedSize) : new LongArrayList();
		storageKeys = 0 > expectedSize ? new LongArrayList(expectedSize) : new LongArrayList();
	}
	
	/* (non-Javadoc)
	 * @see org.apache.lucene.search.Collector#collect(int)
	 */
	@Override
	public void collect(final int doc) throws IOException {

		if (!checkValues()) { //sources cannot be referenced
			return;
		}
		
		final long conceptId = conceptIdsSource.get(doc);
		final long storageKey = storageKeySource.get(doc);
		
		conceptIds.add(conceptId);
		storageKeys.add(storageKey);
		
	}

	/* (non-Javadoc)
	 * @see org.apache.lucene.search.Collector#setNextReader(org.apache.lucene.index.AtomicReaderContext)
	 */
	@Override
	public void setNextReader(final AtomicReaderContext context) throws IOException {

		Preconditions.checkNotNull(context, "Atomic reader context argument cannot be null.");

		conceptIdsSource = context.reader().getNumericDocValues(SnomedIndexBrowserConstants.COMPONENT_ID);
		if (null == conceptIdsSource) {
			resetValues();
			return;
		}
		
		
		storageKeySource = context.reader().getNumericDocValues(SnomedIndexBrowserConstants.COMPONENT_STORAGE_KEY);
		if (null == storageKeySource) {
			resetValues();
			return;
		}
		
	}
	
	/**
	 * Returns with a 2D array of IDs and storage keys of all the active SNOMED&nbsp;CT concepts from the ontology
	 * after performing an index query.
	 * @return a 2D array of concept IDs and storage keys.
	 */
	public long[][] getIds() {
		
		conceptIds.trimToSize();
		storageKeys.trimToSize();
		
		Preconditions.checkState(conceptIds.size() == storageKeys.size(), "The number of collected SNOMED CT concept" +
				" IDs must be equal with the number of storage keys." +
				"Concept ID: " + conceptIds.size() + " Storage keys: " + storageKeys.size());
		
		if (conceptIds.isEmpty()) {
			
			return new long[0][0];
			
		}
		
		
		final long[][] ids = new long[conceptIds.size()][2];
		
		for (int i = 0; i < conceptIds.size(); i++) {
			
			ids[i][0] = conceptIds.get(i);
			ids[i][1] = storageKeys.get(i);
			
		}
		
		
		return ids;
	}
	
	/*sets the reference on the values to null*/
	private void resetValues() {
		conceptIdsSource = null;
		storageKeySource = null;
	}
	
	/*returns true only and if only all the backing values can be referenced*/
	private boolean checkValues() {
		return null != conceptIdsSource
			&& null != storageKeySource;
	}

}