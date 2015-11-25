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

import org.apache.lucene.index.AtomicReader;
import org.apache.lucene.index.NumericDocValues;

import com.b2international.snowowl.datastore.index.AbstractDocsOutOfOrderCollector;
import com.b2international.snowowl.snomed.datastore.IsAStatement;
import com.b2international.snowowl.snomed.datastore.StatementCollectionMode;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedMappings;

/**
 * Collector class for collecting SNOMED CT {@link IsAStatement IS_A relationship}s.
 */
public class StatementCollector extends AbstractDocsOutOfOrderCollector {

	private final IsAStatement[] statements;

	private NumericDocValues idsValues; // can be set to null, relationship id docvalues or storage key docvalues, depending on mode
	private NumericDocValues sourceIdsValues;
	private NumericDocValues destinationIdsValues;

	private final StatementCollectionMode mode;

	private int count;

	/**
	 * Creates a collector with the given expected size and collection mode.
	 * 
	 * @param expectedSize the expected size
	 * @param mode the statement collection mode for this run (collect no IDs, collect relationship IDs, collect storage keys)
	 */
	public StatementCollector(final int expectedSize, final StatementCollectionMode mode) {
		this.mode = checkNotNull(mode, "mode");
		this.statements = mode.createArray(expectedSize);
	}

	@Override
	public void collect(final int docId) throws IOException {
		final long idOrKey = mode.getIdValue(idsValues, docId);
		final long sourceId = sourceIdsValues.get(docId);
		final long destinationId = destinationIdsValues.get(docId);

		statements[count++] = mode.createStatement(sourceId, destinationId, idOrKey);
	}

	@Override
	protected void initDocValues(final AtomicReader leafReader) throws IOException {
		idsValues = mode.getNumericDocValues(leafReader);
		destinationIdsValues = SnomedMappings.relationshipDestination().getDocValues(leafReader);
		sourceIdsValues = SnomedMappings.relationshipSource().getDocValues(leafReader);
	}

	@Override
	protected boolean isLeafCollectible() {
		return mode.isLeafCollectible(idsValues) && sourceIdsValues != null && destinationIdsValues != null;
	}

	/**
	 * Returns collected statements.
	 * 
	 * @return an array copy of the collected statements
	 */
	public IsAStatement[] getStatements() {
		return Arrays.copyOf(statements, count);
	}
}
