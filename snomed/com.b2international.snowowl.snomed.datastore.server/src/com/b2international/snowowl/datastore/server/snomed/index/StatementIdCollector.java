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

import org.apache.lucene.index.AtomicReader;
import org.apache.lucene.index.NumericDocValues;

import com.b2international.collections.PrimitiveSets;
import com.b2international.collections.longs.LongSet;
import com.b2international.snowowl.datastore.index.AbstractDocsOutOfOrderCollector;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedMappings;

/**
 * Collector for gathering all source, type and destination concept IDs referenced in matching SNOMED CT relationships.
 */
public class StatementIdCollector extends AbstractDocsOutOfOrderCollector {

	/**
	 * Default initial size for the underlying collection. Value: {@value}.
	 */
	private static final int DEFAULT_EXPECTED_SIZE = 30000;

	private final LongSet ids;

	private NumericDocValues sourceIdsValues;
	private NumericDocValues typeIdsValues;
	private NumericDocValues destinationIdsValues;

	/**
	 * Creates a new collector instance with the default expected size. 
	 */
	public StatementIdCollector() {
		this(DEFAULT_EXPECTED_SIZE);
	}

	/**
	 * Creates a new collector instance with the specified expected size.
	 * 
	 * @param expectedSize the expected number of collected identifiers, or <= 0 if the built-in default should be used
	 */
	public StatementIdCollector(final int expectedSize) {
		this.ids = (0 > expectedSize) 
				? PrimitiveSets.newLongOpenHashSetWithExpectedSize(expectedSize) 
				: PrimitiveSets.newLongOpenHashSet();
	}

	@Override
	public void collect(final int docId) throws IOException {
		ids.add(sourceIdsValues.get(docId));
		ids.add(typeIdsValues.get(docId));
		ids.add(destinationIdsValues.get(docId));
	}

	@Override
	protected void initDocValues(final AtomicReader leafReader) throws IOException {
		sourceIdsValues = SnomedMappings.relationshipSource().getDocValues(leafReader);
		typeIdsValues = SnomedMappings.relationshipType().getDocValues(leafReader);
		destinationIdsValues = SnomedMappings.relationshipDestination().getDocValues(leafReader);
	}

	@Override
	protected boolean isLeafCollectible() {
		return sourceIdsValues != null
				&& typeIdsValues != null
				&& destinationIdsValues != null;
	}

	/**
	 * Returns a set of object, value and attribute concept identifiers.
	 * 
	 * @return the collected concept identifier set
	 */
	public LongSet getIds() {
		return ids;
	}
}
