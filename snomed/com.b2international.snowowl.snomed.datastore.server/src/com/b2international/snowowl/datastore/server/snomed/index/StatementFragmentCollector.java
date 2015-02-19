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
import java.util.List;

import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.NumericDocValues;

import bak.pcj.map.LongKeyMap;
import bak.pcj.map.LongKeyOpenHashMap;

import com.b2international.snowowl.datastore.index.AbstractDocsOutOfOrderCollector;
import com.b2international.snowowl.snomed.datastore.StatementFragment;
import com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

/**
 * Collector for gathering SNOMED&nbsp;CT relationship representations after performing an index search.
 * @see AbstractDocsOutOfOrderCollector
 */
public class StatementFragmentCollector extends AbstractDocsOutOfOrderCollector {

	/**
	 * Default size for the backing map.
	 */
	private static final int DEFAULT_SIZE = 400000;

	/**
	 * Map for storing statements.
	 * <ul>
	 * <li>Keys: source concept IDs.</li>
	 * <li>Values: list of statement fragments. Can be {@code null}.</li>
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
	 * @param expectedSize the expected size.
	 */
	public StatementFragmentCollector(final int expectedSize) {
		statementMap = 0 > expectedSize ? new LongKeyOpenHashMap(expectedSize) : new LongKeyOpenHashMap();
	}

	/* (non-Javadoc)
	 * @see org.apache.lucene.search.Collector#collect(int)
	 */
	@Override
	public void collect(final int doc) throws IOException {

		if (!checkSources()) { //sources cannot be referenced
			return;
		}

		final long sourceId = sourceIdValues.get(doc);

		final StatementFragment statement = new StatementFragment(
				idValues.get(doc), //relationship ID
				storageKeyValues.get(doc), // storage key
				destinationIdValues.get(doc), //destination ID
				typeIdValues.get(doc),  //type ID
				1L == destinationNegatedValues.get(doc), //destination negated
				1L == universalValues.get(doc), //universal restriction
				(byte) groupValues.get(doc), //group
				(byte) unionGroupValues.get(doc) //union group
			);

		if (statementMap.containsKey(sourceId)) {
			getStatementsById(sourceId).add(statement);
		} else {
			statementMap.put(sourceId, Lists.<StatementFragment>newArrayList(statement));
		}

	}

	/* (non-Javadoc)
	 * @see org.apache.lucene.search.Collector#setNextReader(org.apache.lucene.index.AtomicReaderContext)
	 */
	@Override
	public void setNextReader(final AtomicReaderContext context) throws IOException {

		Preconditions.checkNotNull(context, "Atomic reader context argument cannot be null.");

		idValues = context.reader().getNumericDocValues(SnomedIndexBrowserConstants.COMPONENT_ID);
		if (null == idValues) {
			resetSources();
			return;
		}

		storageKeyValues = context.reader().getNumericDocValues(SnomedIndexBrowserConstants.COMPONENT_STORAGE_KEY);
		if (null == storageKeyValues) {
			resetSources();
			return;
		}

		sourceIdValues = context.reader().getNumericDocValues(SnomedIndexBrowserConstants.RELATIONSHIP_OBJECT_ID);
		if (null == sourceIdValues) {
			resetSources();
			return;
		}

		destinationIdValues = context.reader().getNumericDocValues(SnomedIndexBrowserConstants.RELATIONSHIP_VALUE_ID);
		if (null == destinationIdValues) {
			resetSources();
			return;
		}

		typeIdValues = context.reader().getNumericDocValues(SnomedIndexBrowserConstants.RELATIONSHIP_ATTRIBUTE_ID);
		if (null == typeIdValues) {
			resetSources();
			return;
		}

		groupValues = context.reader().getNumericDocValues(SnomedIndexBrowserConstants.RELATIONSHIP_GROUP);
		if (null == groupValues) {
			resetSources();
			return;
		}

		unionGroupValues = context.reader().getNumericDocValues(SnomedIndexBrowserConstants.RELATIONSHIP_UNION_GROUP);
		if (null == unionGroupValues) {
			resetSources();
			return;
		}

		universalValues = context.reader().getNumericDocValues(SnomedIndexBrowserConstants.RELATIONSHIP_UNIVERSAL);
		if (null == universalValues) {
			resetSources();
			return;
		}

		destinationNegatedValues = context.reader().getNumericDocValues(SnomedIndexBrowserConstants.RELATIONSHIP_DESTINATION_NEGATED);
		if (null == destinationNegatedValues) {
			resetSources();
			return;
		}
	}

	/**
	 * Returns with a map of statement fragments.
	 * <ul>
	 * <li>Keys: source concept IDs.</li>
	 * <li>Values: list of statement fragments. Can be {@code null}.</li>
	 * </ul>
	 * @return the statement map.
	 */
	public LongKeyMap getStatementMap() {
		return statementMap;
	}

	/*sets the reference on the sources to null*/
	private void resetSources() {
		idValues = null;
		storageKeyValues = null;
		sourceIdValues = null;
		destinationIdValues = null;
		typeIdValues = null;
		groupValues = null;
		unionGroupValues = null;
		universalValues = null;
		destinationNegatedValues = null;
	}

	/*returns true only and if only all the backing sources can be referenced*/
	private boolean checkSources() {
		return null != idValues
			&& null != storageKeyValues
			&& null != sourceIdValues
			&& null != destinationIdValues
			&& null != typeIdValues
			&& null != groupValues
			&& null != unionGroupValues
			&& null != universalValues
			&& null != destinationNegatedValues;
	}

	/*returns with a list of statements associated with the specified SNOMED CT source concept ID*/
	@SuppressWarnings("unchecked")
	private List<StatementFragment> getStatementsById(final long id) {
		return (List<StatementFragment>) statementMap.get(id);
	}

}