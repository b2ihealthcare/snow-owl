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
import java.util.Collection;

import org.apache.lucene.index.AtomicReader;
import org.apache.lucene.index.NumericDocValues;

import com.b2international.collections.PrimitiveMaps;
import com.b2international.collections.longs.LongKeyMap;
import com.b2international.snowowl.datastore.index.AbstractDocsOutOfOrderCollector;
import com.b2international.snowowl.datastore.index.mapping.Mappings;
import com.b2international.snowowl.snomed.datastore.StatementFragment;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedMappings;
import com.google.common.collect.Lists;

/**
 * Collector for gathering SNOMED CT relationship representations after performing an index search.
 */
public class StatementFragmentCollector extends AbstractDocsOutOfOrderCollector {

	/**
	 * Default size for the backing map.
	 */
	private static final int DEFAULT_SIZE = 400000;

	/**
	 * Map for storing statements.
	 * <ul>
	 * <li>Key: source concept identifier</li>
	 * <li>Value: list of outbound statement fragments</li>
	 * </ul>
	 */
	private final LongKeyMap<Collection<StatementFragment>> statementMap;

	private NumericDocValues idValues;
	private NumericDocValues storageKeyValues;
	private NumericDocValues sourceIdValues;
	private NumericDocValues destinationIdValues;
	private NumericDocValues typeIdValues;
	private NumericDocValues groupValues;
	private NumericDocValues unionGroupValues;
	private NumericDocValues universalValues;
	private NumericDocValues destinationNegatedValues;

	/**
	 * Creates a statement fragment collector instance.
	 */
	public StatementFragmentCollector() {
		this(DEFAULT_SIZE);
	}

	/**
	 * Creates a statement fragment collector instance with an initial expected size for the backing map.
	 * 
	 * @param expectedSize the expected number of source concept identifiers, or <= 0 to use the built-in default hash map size
	 */
	public StatementFragmentCollector(final int expectedSize) {
		statementMap = (0 > expectedSize) 
				? PrimitiveMaps.<Collection<StatementFragment>> newLongKeyOpenHashMapWithExpectedSize(expectedSize)
				: PrimitiveMaps.<Collection<StatementFragment>> newLongKeyOpenHashMap();
	}

	@Override
	public void collect(final int docId) throws IOException {
		final long sourceId = sourceIdValues.get(docId);

		final StatementFragment statement = new StatementFragment(
			typeIdValues.get(docId),
			destinationIdValues.get(docId), 
			getBooleanValue(destinationNegatedValues, docId),
			(byte) groupValues.get(docId),
			(byte) unionGroupValues.get(docId),
			getBooleanValue(universalValues, docId),
			idValues.get(docId),
			storageKeyValues.get(docId)
		);

		if (!statementMap.containsKey(sourceId)) {
			statementMap.put(sourceId, Lists.<StatementFragment>newArrayList());
		}

		getStatementsById(sourceId).add(statement);
	}

	private boolean getBooleanValue(final NumericDocValues numericValues, final int docId) {
		return numericValues.get(docId) == 1L;
	}

	@Override
	protected void initDocValues(final AtomicReader reader) throws IOException {
		idValues = SnomedMappings.id().getDocValues(reader);
		storageKeyValues = Mappings.storageKey().getDocValues(reader);
		sourceIdValues = SnomedMappings.relationshipSource().getDocValues(reader);
		destinationIdValues = SnomedMappings.relationshipDestination().getDocValues(reader);
		typeIdValues = SnomedMappings.relationshipType().getDocValues(reader);
		groupValues = SnomedMappings.relationshipGroup().getDocValues(reader);
		unionGroupValues = SnomedMappings.relationshipUnionGroup().getDocValues(reader);
		universalValues = SnomedMappings.relationshipUniversal().getDocValues(reader);
		destinationNegatedValues = SnomedMappings.relationshipDestinationNegated().getDocValues(reader);
	}

	@Override
	protected boolean isLeafCollectible() {
		return idValues != null
				&& storageKeyValues != null
				&& sourceIdValues != null
				&& destinationIdValues != null
				&& typeIdValues != null
				&& groupValues != null
				&& unionGroupValues != null
				&& universalValues != null
				&& destinationNegatedValues != null;
	}

	/**
	 * Returns a map of statement fragments.
	 * <ul>
	 * <li>Key: source concept identifier</li>
	 * <li>Value: list of outbound statement fragments</li>
	 * </ul>
	 * 
	 * @return the collected statement map
	 */
	public LongKeyMap<Collection<StatementFragment>> getStatementMap() {
		return statementMap;
	}

	private Collection<StatementFragment> getStatementsById(final long sourceId) {
		return statementMap.get(sourceId);
	}
}
