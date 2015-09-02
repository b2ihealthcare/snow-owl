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

import static com.google.common.base.Preconditions.checkState;

import java.io.IOException;

import org.apache.lucene.index.AtomicReader;
import org.apache.lucene.index.NumericDocValues;

import bak.pcj.list.LongArrayList;
import bak.pcj.list.LongList;

import com.b2international.snowowl.datastore.index.AbstractDocsOutOfOrderCollector;
import com.b2international.snowowl.datastore.index.mapping.Mappings;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedMappings;

/**
 * Custom collector for extracting SNOMED CT concept identifiers and the unique storage keys as primitive longs.
 */
public class ConceptIdStorageKeyCollector extends AbstractDocsOutOfOrderCollector {

	private static final int DEFAULT_SIZE = 600000;

	private final LongList conceptIds;
	private final LongList storageKeys;

	private NumericDocValues conceptIdValues;
	private NumericDocValues storageKeyValues;

	/**
	 * Creates a collector instance with a backing 2D array initialized with the default expected size ({@value #DEFAULT_SIZE} items).
	 */
	public ConceptIdStorageKeyCollector() {
		this(DEFAULT_SIZE);
	}

	/**
	 * Creates a collector instance with a backing 2D array initialized with the specified expected size.
	 * 
	 * @param expectedSize the expected size for the backing 2D array.
	 */
	public ConceptIdStorageKeyCollector(final int expectedSize) {
		conceptIds = 0 > expectedSize ? new LongArrayList(expectedSize) : new LongArrayList();
		storageKeys = 0 > expectedSize ? new LongArrayList(expectedSize) : new LongArrayList();
	}

	@Override
	protected void initDocValues(final AtomicReader leafReader) throws IOException {
		conceptIdValues = SnomedMappings.id().getDocValues(leafReader);
		storageKeyValues = Mappings.storageKey().getDocValues(leafReader);
	}

	@Override
	protected boolean isLeafCollectible() {
		return conceptIdValues!= null && storageKeyValues != null;
	}

	@Override
	public void collect(final int docId) throws IOException {
		final long conceptId = conceptIdValues.get(docId);
		final long storageKey = storageKeyValues.get(docId);

		conceptIds.add(conceptId);
		storageKeys.add(storageKey);
	}

	/**
	 * Returns a 2D array of IDs and storage keys of all the active SNOMED CT concepts from the ontology
	 * after performing an index query.
	 * 
	 * @return a 2D array of concept IDs and storage keys.
	 */
	public long[][] getIds() {
		checkState(conceptIds.size() == storageKeys.size(), "The number of collected SNOMED CT concept IDs must be equal with the number of storage keys. "
				+ "Concept IDs: " + conceptIds.size() 
				+ ", Storage keys: " + storageKeys.size());

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
}
