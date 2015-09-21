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
package com.b2international.snowowl.snomed.datastore.taxonomy;

import bak.pcj.map.LongKeyLongMap;
import bak.pcj.map.LongKeyLongMapIterator;
import bak.pcj.map.LongKeyLongOpenHashMap;
import bak.pcj.map.LongKeyMap;

import com.b2international.commons.arrays.Arrays2;
import com.b2international.commons.arrays.LongBidiMapWithInternalId;
import com.b2international.commons.concurrent.equinox.ForkJoinUtils;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.datastore.cdo.CDOUtils;
import com.b2international.snowowl.snomed.datastore.IsAStatementWithId;
import com.b2international.snowowl.snomed.datastore.SnomedStatementBrowser;
import com.b2international.snowowl.snomed.datastore.SnomedTerminologyBrowser;
import com.b2international.snowowl.snomed.datastore.StatementCollectionMode;
import com.b2international.snowowl.snomed.datastore.index.StatementMap;
import com.google.common.base.Preconditions;

/**
 * Builds the taxonomy for the SNOMED&nbsp;CT ontology.
 */
public class SnomedTaxonomyBuilder extends AbstractSnomedTaxonomyBuilder {

	/**
	 * Returns with a new taxonomy builder instance after replicating the specified one.
	 * @param builder the builder to replicate.
	 * @return the new builder instance.
	 */
	public static SnomedTaxonomyBuilder newInstance(final SnomedTaxonomyBuilder builder) {
		Preconditions.checkNotNull(builder, "Builder argument cannot be null.");
		final SnomedTaxonomyBuilder $ = new SnomedTaxonomyBuilder(builder.branchPath);
		$.nodes = new LongBidiMapWithInternalId( builder.nodes);
		$.edges = (StatementMap) ((StatementMap) builder.edges).clone();
		$.setDirty(builder.isDirty());
		$.descendants = Arrays2.copy(builder.descendants);
		$.ancestors = Arrays2.copy(builder.ancestors);
		
		return $;
	}

	private final IBranchPath branchPath;

	/**
	 * Bi-directional map for storing SNOMED CT concept IDs. 
	 */
	private LongBidiMapWithInternalId nodes;

	private LongKeyLongMap storageKeys;
	
	/**
	 * Map for storing active IS_A type SNOMED CT relationship representations. Keys are the unique relationship identifiers.
	 * <br>For values see: {@link IsAStatementWithId}.
	 */
	private LongKeyMap edges;

	private SnomedTaxonomyBuilder(IBranchPath branchPath) {
		this.branchPath = Preconditions.checkNotNull(branchPath, "Branch path argument cannot be null.");
	}
	
	public SnomedTaxonomyBuilder(final IBranchPath branchPath, final StatementCollectionMode mode) {
		this(branchPath);
		
		final Runnable initStatementsRunnable = new Runnable() {
			@Override public void run() {
				final IsAStatementWithId[] isAStatements = getStatementBrowser().getActiveStatements(branchPath, mode);
				edges = 0 < isAStatements.length ? new StatementMap(isAStatements.length) : new StatementMap();
				for (final IsAStatementWithId statement : isAStatements) {
					edges.put(statement.getRelationshipId(), new long[] { statement.getDestinationId(), statement.getSourceId() });
				}
			}
		};

		final Runnable initConceptsRunnable = new Runnable() {
			@Override public void run() {
				final LongKeyLongMap conceptIdToStorageKeyMap = getTerminologyBrowser().getConceptIdToStorageKeyMap(branchPath);
				nodes = new LongBidiMapWithInternalId(conceptIdToStorageKeyMap.size());
				storageKeys = new LongKeyLongOpenHashMap(nodes.size());
				final LongKeyLongMapIterator iterator = conceptIdToStorageKeyMap.entries();
				while (iterator.hasNext()) {
					iterator.next();
					nodes.put(iterator.getKey()/*conceptId*/, iterator.getKey()/*conceptId*/);
					storageKeys.put(iterator.getKey(), iterator.getValue());
				}
			}
		};
		
		ForkJoinUtils.runInParallel(initConceptsRunnable, initStatementsRunnable);
		setDirty(true);
	}

	@Override
	public long getNodeStorageKey(String nodeId) {
		long key = Long.parseLong(nodeId);
		return storageKeys.containsKey(key) ? storageKeys.get(key) : CDOUtils.NO_STORAGE_KEY;
	}
	
	@Override
	public LongBidiMapWithInternalId getNodes() {
		return nodes;
	}

	@Override
	public LongKeyMap getEdges() {
		return edges;
	}

	private SnomedStatementBrowser getStatementBrowser() {
		return ApplicationContext.getInstance().getService(SnomedStatementBrowser.class);
	}

	private SnomedTerminologyBrowser getTerminologyBrowser() {
		return ApplicationContext.getInstance().getService(SnomedTerminologyBrowser.class);
	}
	
}