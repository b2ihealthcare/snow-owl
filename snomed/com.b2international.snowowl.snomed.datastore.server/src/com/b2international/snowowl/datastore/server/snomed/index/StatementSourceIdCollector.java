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

import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.NumericDocValues;

import com.b2international.snowowl.datastore.index.AbstractDocsOutOfOrderCollector;
import com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants;
import com.google.common.base.Preconditions;

/**
 * Collector class for collecting a single source ID from SNOMED&nbsp;CT relationships.
 *
 */
public class StatementSourceIdCollector extends AbstractDocsOutOfOrderCollector {

	private NumericDocValues objectIdsValue;

	private final long statementStorageKey;
	
	private long sourceId = -1L;

	/**
	 * 
	 * @param statementStorageKey
	 */
	public StatementSourceIdCollector(final long statementStorageKey) {
		this.statementStorageKey = statementStorageKey;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.apache.lucene.search.Collector#collect(int)
	 */
	@Override
	public void collect(final int doc) throws IOException {

		if (!checkValues()) { //sources cannot be referenced
			return;
		}
		
		final long objectId = objectIdsValue.get(doc);
		
		if (-1L == sourceId) {
			sourceId = objectId;
		} else {
			throw new IllegalStateException(MessageFormat.format("Found more than one matching statement for storage key {0}.", statementStorageKey));
		}
	}

	/* 
	 * (non-Javadoc)
	 * @see org.apache.lucene.search.Collector#setNextReader(org.apache.lucene.index.AtomicReaderContext)
	 */
	@Override
	public void setNextReader(final AtomicReaderContext context) throws IOException {

		Preconditions.checkNotNull(context, "Atomic reader context argument cannot be null.");

		objectIdsValue = context.reader().getNumericDocValues(SnomedIndexBrowserConstants.RELATIONSHIP_OBJECT_ID);
	}
	
	/*
	 * returns true only and if only all the backing values can be referenced
	 */
	private boolean checkValues() {
		return null != objectIdsValue;
	}

	/**
	 * @return the collected source concept ID
	 */
	public long getSourceId() {
		if (-1L == sourceId) {
			throw new IllegalStateException(MessageFormat.format("No matching statement found for storage key {0}.", statementStorageKey));
		} else {
			return sourceId;
		}
	}
}