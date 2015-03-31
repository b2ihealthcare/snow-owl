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

import static com.google.common.collect.Lists.newArrayList;

import java.io.IOException;
import java.util.List;

import org.apache.lucene.index.AtomicReader;
import org.apache.lucene.index.NumericDocValues;

import bak.pcj.map.LongKeyMap;
import bak.pcj.map.LongKeyOpenHashMap;

import com.b2international.snowowl.datastore.index.AbstractDocsOutOfOrderCollector;
import com.b2international.snowowl.snomed.datastore.StatementFragment;
import com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants;

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
	private final LongKeyOpenHashMap statementMap;

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
		statementMap = (0 > expectedSize) ? new LongKeyOpenHashMap(expectedSize) : new LongKeyOpenHashMap();
	}

	@Override
	public void collect(final int docId) throws IOException {
		final long sourceId = sourceIdValues.get(docId);

		final StatementFragment statement = new StatementFragment(
			idValues.get(docId), //relationship ID
			storageKeyValues.get(docId), // storage key
			destinationIdValues.get(docId), //destination ID
			typeIdValues.get(docId),  //type ID
			getBooleanValue(destinationNegatedValues, docId), //destination negated
			getBooleanValue(universalValues, docId), //universal restriction
			(byte) groupValues.get(docId), //group
			(byte) unionGroupValues.get(docId) //union group
		);

		if (!statementMap.containsKey(sourceId)) {
			statementMap.put(sourceId, newArrayList());
		}

		getStatementsById(sourceId).add(statement);
	}

	private boolean getBooleanValue(final NumericDocValues numericValues, final int docId) {
		return numericValues.get(docId) == 1L;
	}

	@Override
	protected void initDocValues(final AtomicReader leafReader) throws IOException {
		idValues = leafReader.getNumericDocValues(SnomedIndexBrowserConstants.COMPONENT_ID);
		storageKeyValues = leafReader.getNumericDocValues(SnomedIndexBrowserConstants.COMPONENT_STORAGE_KEY);
		sourceIdValues = leafReader.getNumericDocValues(SnomedIndexBrowserConstants.RELATIONSHIP_OBJECT_ID);
		destinationIdValues = leafReader.getNumericDocValues(SnomedIndexBrowserConstants.RELATIONSHIP_VALUE_ID);
		typeIdValues = leafReader.getNumericDocValues(SnomedIndexBrowserConstants.RELATIONSHIP_ATTRIBUTE_ID);
		groupValues = leafReader.getNumericDocValues(SnomedIndexBrowserConstants.RELATIONSHIP_GROUP);
		unionGroupValues = leafReader.getNumericDocValues(SnomedIndexBrowserConstants.RELATIONSHIP_UNION_GROUP);
		universalValues = leafReader.getNumericDocValues(SnomedIndexBrowserConstants.RELATIONSHIP_UNIVERSAL);
		destinationNegatedValues = leafReader.getNumericDocValues(SnomedIndexBrowserConstants.RELATIONSHIP_DESTINATION_NEGATED);
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
	public LongKeyMap getStatementMap() {
		return statementMap;
	}

	@SuppressWarnings("unchecked")
	private List<StatementFragment> getStatementsById(final long sourceId) {
		return (List<StatementFragment>) statementMap.get(sourceId);
	}
}
