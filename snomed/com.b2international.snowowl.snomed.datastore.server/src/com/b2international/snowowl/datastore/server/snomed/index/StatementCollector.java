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

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.util.Arrays;

import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.NumericDocValues;

import com.b2international.snowowl.datastore.index.AbstractDocsOutOfOrderCollector;
import com.b2international.snowowl.snomed.datastore.IsAStatement;
import com.b2international.snowowl.snomed.datastore.StatementCollectionMode;
import com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants;
import com.google.common.base.Preconditions;

/**
 * Collector class for collecting SNOMED&nbsp;CT {@link IsAStatement IS_A relationship}s.
 *
 */
public class StatementCollector extends AbstractDocsOutOfOrderCollector {

	private static final long NOT_APPLICABLE_ID = -1L;

	/**
	 * Default initial size for the underlying collection. Value: {@value}.
	 */
	private static final int DEFAULT_EXPECTED_SIZE = 1000000;

	private final IsAStatement[] statements;

	private NumericDocValues idsValues; // can be set to null, relationship id docvalues, storage key docvalues depending on mode
	private NumericDocValues sourceIdsValues;
	private NumericDocValues destinationIdsValues;

	private final StatementCollectionMode mode;

	private int count;

	/**
	 * Creates a collector with the the default expected size
	 * @param mode the statement collection mode for this run (collect no IDs, collect relationship IDs, collect storage keys)
	 */
	public StatementCollector(final StatementCollectionMode mode) {
		this(DEFAULT_EXPECTED_SIZE, mode);
	}

	/**
	 * Creates a collector with the given expected size.
	 * @param expectedSize the expected size.
	 * @param mode the statement collection mode for this run (collect no IDs, collect relationship IDs, collect storage keys)
	 */
	public StatementCollector(final int expectedSize, final StatementCollectionMode mode) {
		this.mode = checkNotNull(mode, "mode");
		statements = mode.createArray(expectedSize);
		count = 0;
	}

	/* (non-Javadoc)
	 * @see org.apache.lucene.search.Collector#collect(int)
	 */
	@Override
	public void collect(final int doc) throws IOException {

		if (!checkValues()) { // sources cannot be referenced
			return;
		}

		final long idOrKey = (null != idsValues) ? idsValues.get(doc) : NOT_APPLICABLE_ID;
		final long sourceId = sourceIdsValues.get(doc);
		final long destinationId = destinationIdsValues.get(doc);

		statements[count++] = mode.createStatement(sourceId, destinationId, idOrKey);
	}

	/* (non-Javadoc)
	 * @see org.apache.lucene.search.Collector#setNextReader(org.apache.lucene.index.AtomicReaderContext)
	 */
	@Override
	public void setNextReader(final AtomicReaderContext context) throws IOException {

		Preconditions.checkNotNull(context, "Atomic reader context argument cannot be null.");

		if (mode.collectsIdValues()) {
			idsValues = context.reader().getNumericDocValues(mode.getIdValuesField());
			if (null == idsValues) {
				resetValues();
				return;
			}
		}

		destinationIdsValues = context.reader().getNumericDocValues(SnomedIndexBrowserConstants.RELATIONSHIP_VALUE_ID);
		if (null == destinationIdsValues) {
			resetValues();
			return;
		}

		sourceIdsValues = context.reader().getNumericDocValues(SnomedIndexBrowserConstants.RELATIONSHIP_OBJECT_ID);
		if (null == sourceIdsValues) {
			resetValues();
			return;
		}
	}

	/**
	 * @return an array copy of the collected statements.
	 */
	public IsAStatement[] getStatements() {
		return Arrays.copyOf(statements, count);
	}

	/* sets the reference on the values to null */
	private void resetValues() {
		idsValues = null;
		destinationIdsValues = null;
		sourceIdsValues = null;
	}

	/* returns true if only and only if all the backing values can be referenced */
	private boolean checkValues() {
		final boolean baseValuesPresent = (null != destinationIdsValues) && (null != sourceIdsValues);
		return baseValuesPresent && mode.checkIdsValuesPresent(idsValues);
	}
}