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
package com.b2international.snowowl.snomed.datastore.index;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;

import org.apache.lucene.index.AtomicReader;
import org.apache.lucene.index.NumericDocValues;

import bak.pcj.LongCollection;
import bak.pcj.list.LongArrayList;

import com.b2international.snowowl.datastore.index.AbstractDocsOutOfOrderCollector;
import com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants;

/**
 * Collects all SNOMED CT relationship's source concept IDs where the statement's type ID and destination ID fulfills
 * the previously specified condition.
 */
public class StatementObjectIdCollector extends AbstractDocsOutOfOrderCollector {

	/**
	 * Default initial size for the underlying collection.
	 */
	private static final int DEFAULT_EXPECTED_SIZE = 100000;

	private final LongCollection sourceIds;

	private final LongCollection typeIdsToMatch;
	private final LongCollection destinationIdsToMatch;

	private NumericDocValues sourceIdValues;
	private NumericDocValues typeIdValues;
	private NumericDocValues destinationIdsValues;

	/**
	 * Creates a new collector instance with the specified set of type and destination identifiers to match.
	 * 
	 * @param typeIdsToMatch the type identifiers to match
	 * @param destinationIdsToMatch the destination identifiers to match
	 */
	public StatementObjectIdCollector(final LongCollection typeIdsToMatch, final LongCollection destinationIdsToMatch) {
		this(typeIdsToMatch, destinationIdsToMatch, DEFAULT_EXPECTED_SIZE);
	}

	/**
	 * Creates a new collector instance with the specified set of type and destination identifiers to match, and the
	 * expected size of the matching relationships.
	 * 
	 * @param typeIdsToMatch the type identifiers to match
	 * @param destinationIdsToMatch the destination identifiers to match
	 * @param expectedSize expected number of matching relationships, or <= 0 if the built-in default should be used
	 */
	public StatementObjectIdCollector(final LongCollection typeIdsToMatch, final LongCollection destinationIdsToMatch, final int expectedSize) {
		this.typeIdsToMatch = checkNotNull(typeIdsToMatch, "Attribute IDs collection argument cannot be null");
		this.destinationIdsToMatch = checkNotNull(destinationIdsToMatch, "Value IDs collection argument cannot be null");
		this.sourceIds = (0 > expectedSize) ? new LongArrayList(expectedSize) : new LongArrayList();
	}

	@Override
	public void collect(final int docId) throws IOException {
		final long typeId = typeIdValues.get(docId);
		final long destinationId = destinationIdsValues.get(docId);

		if (destinationIdsToMatch.contains(destinationId) && typeIdsToMatch.contains(typeId)) {
			sourceIds.add(sourceIdValues.get(docId));
		}
	}

	@Override
	protected void initDocValues(final AtomicReader leafReader) throws IOException {
		sourceIdValues = leafReader.getNumericDocValues(SnomedIndexBrowserConstants.RELATIONSHIP_OBJECT_ID);
		typeIdValues = leafReader.getNumericDocValues(SnomedIndexBrowserConstants.RELATIONSHIP_ATTRIBUTE_ID);
		destinationIdsValues = leafReader.getNumericDocValues(SnomedIndexBrowserConstants.RELATIONSHIP_VALUE_ID);
	}

	@Override
	protected boolean isLeafCollectible() {
		return sourceIdValues != null && typeIdValues != null && destinationIdsValues != null;
	}

	/**
	 * Returns the collection of matching source concept identifiers. The collection may contain duplicate values.
	 * 
	 * @return the collected source concept identifiers of the relationships where the type and destination identifiers
	 * are part of the specified sets
	 */
	public LongCollection getObjectIds() {
		return sourceIds;
	}
}
