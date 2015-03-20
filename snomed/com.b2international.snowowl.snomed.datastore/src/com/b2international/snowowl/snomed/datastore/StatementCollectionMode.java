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
package com.b2international.snowowl.snomed.datastore;

import static com.b2international.snowowl.datastore.index.IndexUtils.intToPrefixCoded;
import static com.b2international.snowowl.datastore.index.IndexUtils.longToPrefixCoded;
import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.IS_A;
import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.RELATIONSHIP_NUMBER;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.COMPONENT_ACTIVE;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.COMPONENT_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.COMPONENT_STORAGE_KEY;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.COMPONENT_TYPE;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.RELATIONSHIP_ATTRIBUTE_ID;
import static org.apache.lucene.search.BooleanClause.Occur.MUST;

import java.io.IOException;
import java.text.MessageFormat;

import org.apache.lucene.index.AtomicReader;
import org.apache.lucene.index.NumericDocValues;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;

/**
 * Enumerates the possible collection modes an array of {@link IsAStatement}s can be returned in.
 *
 */
public enum StatementCollectionMode {

	NO_IDS {
		@Override public NumericDocValues getNumericDocValues(final AtomicReader leafReader) throws IOException {
			return null;
		}

		@Override public boolean isLeafCollectible(final NumericDocValues idsValues) {
			return true;
		}

		@Override public long getIdValue(final NumericDocValues idsValues, final int docId) {
			return -1L;
		}

		@Override public IsAStatement createStatement(final long sourceId, final long destinationId, final long idOrKey) {
			return new SnomedIsAStatement(sourceId, destinationId);
		}

		@Override public IsAStatement[] createArray(final int expectedSize) {
			return new SnomedIsAStatement[expectedSize];
		}
	},

	WITH_RELATIONSHIP_IDS {
		@Override public String getIdValuesField() {
			return COMPONENT_ID;
		}

		@Override
		public IsAStatementWithId createStatement(final long sourceId, final long destinationId, final long idOrKey) {
			return new SnomedIsAStatementWithId(sourceId, destinationId, idOrKey);
		}

		@Override public IsAStatement[] createArray(final int expectedSize) {
			return new SnomedIsAStatementWithId[expectedSize];
		}
	},

	WITH_STORAGE_KEYS {
		@Override public String getIdValuesField() {
			return COMPONENT_STORAGE_KEY;
		}

		@Override public IsAStatementWithKey createStatement(final long sourceId, final long destinationId, final long idOrKey) {
			return new SnomedIsAStatementWithKey(sourceId, destinationId, idOrKey);
		}

		@Override public IsAStatement[] createArray(final int expectedSize) {
			return new SnomedIsAStatementWithKey[expectedSize];
		}
	},

	ALL_TYPES_NO_IDS {
		@Override public String getIdValuesField() {
			return RELATIONSHIP_ATTRIBUTE_ID;
		}

		@Override public IsAStatement createStatement(final long sourceId, final long destinationId, final long idOrKey) {
			return new SnomedStatement(sourceId, idOrKey, destinationId);
		}

		@Override public IsAStatement[] createArray(final int expectedSize) {
			return new SnomedStatement[expectedSize];
		}

		@Override public Query getQuery() {
			final BooleanQuery query = new BooleanQuery(true);
			query.add(new TermQuery(new Term(COMPONENT_TYPE, intToPrefixCoded(RELATIONSHIP_NUMBER))), MUST);
			query.add(new TermQuery(new Term(COMPONENT_ACTIVE, intToPrefixCoded(1))), MUST);
			return query;
		}

	};

	/**
	 * @param idsValues the docvalues source to check
	 * 
	 * @return {@code true} if the docvalues source is not {@code null}, {@code false} otherwise
	 */
	public boolean isLeafCollectible(final NumericDocValues idsValues) {
		return idsValues != null;
	}

	public String getIdValuesField() {
		throw new IllegalStateException(MessageFormat.format("No ID field specified for collection mode {0}.", name()));
	}

	public NumericDocValues getNumericDocValues(final AtomicReader leafReader) throws IOException {
		return leafReader.getNumericDocValues(getIdValuesField());
	}

	public long getIdValue(final NumericDocValues idsValues, final int docId) {
		return idsValues.get(docId);
	}

	/**Returns with the index query to collect all relevant relationships.
	 *<p>By default returns with a query that accepts active relationships with IS_A type.*/
	public Query getQuery() {
		final BooleanQuery query = new BooleanQuery(true);
		query.add(new TermQuery(new Term(COMPONENT_ACTIVE, intToPrefixCoded(1))), MUST);
		query.add(new TermQuery(new Term(RELATIONSHIP_ATTRIBUTE_ID, longToPrefixCoded(IS_A))), MUST);
		return query;
	}

	/**
	 * 
	 * @param sourceId
	 * @param destinationId
	 * @param idOrKey
	 * @return
	 */
	public abstract IsAStatement createStatement(long sourceId, long destinationId, long idOrKey);

	public abstract IsAStatement[] createArray(int expectedSize);
}
