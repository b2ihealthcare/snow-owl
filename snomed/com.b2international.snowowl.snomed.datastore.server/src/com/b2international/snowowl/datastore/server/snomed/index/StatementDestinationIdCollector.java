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

import com.b2international.collections.PrimitiveMaps;
import com.b2international.collections.longs.LongKeyLongMap;
import com.b2international.snowowl.datastore.index.AbstractDocsOutOfOrderCollector;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedMappings;

/**
 * Collects destination concept identifiers keyed by source concept identifiers for relationships of a particular type.
 * <p>
 * Note that only single outbound relationships are allowed per source concept; multiple results for the same source
 * concept will overwrite each other.
 */
public class StatementDestinationIdCollector extends AbstractDocsOutOfOrderCollector {

	/**
	 * Default initial size for the underlying collection. Value: {@value} .
	 */
	private static final int DEFAULT_EXPECTED_SIZE = 30000;

	private final LongKeyLongMap sourceToDestinationIds;

	private NumericDocValues sourceIdsValues;
	private NumericDocValues destinationIdsValues;

	/**
	 * Creates a new collector instance with the default expected size.
	 */
	public StatementDestinationIdCollector() {
		this(DEFAULT_EXPECTED_SIZE);
	}

	/**
	 * Creates a collector instance with the given expected size.
	 * 
	 * @param expectedSize the expected number of source-destination pairs, or <= 0 to use the built-in defaults
	 */
	public StatementDestinationIdCollector(final int expectedSize) {
		this.sourceToDestinationIds = (0 > expectedSize) ? PrimitiveMaps.newLongKeyLongOpenHashMapWithExpectedSize(expectedSize) : PrimitiveMaps.newLongKeyLongOpenHashMap();
	}

	@Override
	public void collect(final int docId) throws IOException {
		final long sourceId = sourceIdsValues.get(docId);
		final long destinationId = destinationIdsValues.get(docId);
		sourceToDestinationIds.put(sourceId, destinationId);
	}

	@Override
	protected void initDocValues(final AtomicReader leafReader) throws IOException {
		sourceIdsValues = SnomedMappings.relationshipSource().getDocValues(leafReader);
		destinationIdsValues = SnomedMappings.relationshipDestination().getDocValues(leafReader);
	}

	@Override
	protected boolean isLeafCollectible() {
		return sourceIdsValues != null && destinationIdsValues != null;
	}

	/**
	 * Returns the mapped source-destination concept identifier pairs.
	 * 
	 * @return a map of destination identifiers, keyed by source identifiers
	 */
	public LongKeyLongMap getIds() {
		return sourceToDestinationIds;
	}
}
