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
import java.text.MessageFormat;

import org.apache.lucene.index.AtomicReader;
import org.apache.lucene.index.NumericDocValues;

import com.b2international.snowowl.datastore.index.AbstractDocsOutOfOrderCollector;
import com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants;

/**
 * Collector class for collecting a single source ID from SNOMED CT relationships.
 */
public class StatementSourceIdCollector extends AbstractDocsOutOfOrderCollector {

	private static final long NOT_INITIALIZED = -1L;

	private final long statementStorageKey;

	private long sourceId = NOT_INITIALIZED;
	private NumericDocValues sourceIdsValue;

	/**
	 * Creates a new collector instance for the relationship with the specified storage key.
	 *  
	 * @param statementStorageKey the relationship's storage key, for error message reporting purposes
	 */
	public StatementSourceIdCollector(final long statementStorageKey) {
		this.statementStorageKey = statementStorageKey;
	}

	@Override
	public void collect(final int docId) throws IOException {
		final long objectId = sourceIdsValue.get(docId);

		if (isSourceIdInitialized()) {
			throw new IllegalStateException(MessageFormat.format("Found more than one matching statement for storage key {0}.", statementStorageKey));
		} else {
			sourceId = objectId;
		}
	}

	@Override
	protected void initDocValues(final AtomicReader leafReader) throws IOException {
		sourceIdsValue = leafReader.getNumericDocValues(SnomedIndexBrowserConstants.RELATIONSHIP_OBJECT_ID);
	}

	@Override
	protected boolean isLeafCollectible() {
		return sourceIdsValue != null;
	}

	/**
	 * @return the collected source concept ID
	 */
	public long getSourceId() {

		if (isSourceIdInitialized()) {
			return sourceId;
		} else {
			throw new IllegalStateException(MessageFormat.format("No matching statement found for storage key {0}.", statementStorageKey));
		}
	}

	private boolean isSourceIdInitialized() {
		return sourceId != NOT_INITIALIZED;
	}
}
