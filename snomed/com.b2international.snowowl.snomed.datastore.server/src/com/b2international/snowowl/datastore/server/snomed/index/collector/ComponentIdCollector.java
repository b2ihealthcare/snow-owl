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
package com.b2international.snowowl.datastore.server.snomed.index.collector;

import java.io.IOException;

import org.apache.lucene.index.AtomicReader;
import org.apache.lucene.index.NumericDocValues;

import bak.pcj.list.LongArrayList;
import bak.pcj.list.LongList;

import com.b2international.snowowl.datastore.index.AbstractDocsOutOfOrderCollector;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedMappings;

/**
 * @since 4.3
 */
public class ComponentIdCollector extends AbstractDocsOutOfOrderCollector {

	private static final int DEFAULT_SIZE = 600000;

	private final LongList conceptIds;

	private NumericDocValues conceptIdValues;

	/**
	 * Creates a collector instance with a backing 2D array initialized with the default expected size ({@value #DEFAULT_SIZE} items).
	 */
	public ComponentIdCollector() {
		this(DEFAULT_SIZE);
	}

	/**
	 * Creates a collector instance with a backing 2D array initialized with the specified expected size.
	 * 
	 * @param expectedSize the expected size for the backing 2D array.
	 */
	public ComponentIdCollector(final int expectedSize) {
		conceptIds = 0 > expectedSize ? new LongArrayList(expectedSize) : new LongArrayList();
	}

	@Override
	protected void initDocValues(final AtomicReader leafReader) throws IOException {
		conceptIdValues = SnomedMappings.id().getDocValues(leafReader);
	}

	@Override
	protected boolean isLeafCollectible() {
		return conceptIdValues!= null;
	}

	@Override
	public void collect(final int docId) throws IOException {
		final long conceptId = conceptIdValues.get(docId);
		conceptIds.add(conceptId);
	}

	/**
	 * Returns a 2D array of IDs and storage keys of all the active SNOMED CT concepts from the ontology
	 * after performing an index query.
	 * 
	 * @return a 2D array of concept IDs and storage keys.
	 */
	public LongList getIds() {
		return conceptIds;
	}

}
