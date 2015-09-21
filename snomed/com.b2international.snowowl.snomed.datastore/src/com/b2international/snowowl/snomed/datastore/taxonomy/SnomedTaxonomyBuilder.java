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

import bak.pcj.LongCollection;
import bak.pcj.LongIterator;
import bak.pcj.map.LongKeyMap;

import com.b2international.commons.arrays.Arrays2;
import com.b2international.commons.arrays.LongBidiMapWithInternalId;
import com.b2international.commons.concurrent.equinox.ForkJoinUtils;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
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
		
		final SnomedTaxonomyBuilder $ = new SnomedTaxonomyBuilder();
		$.branchPath = builder.branchPath;
		$.nodes = new LongBidiMapWithInternalId( builder.nodes);
		$.edges = (StatementMap) ((StatementMap) builder.edges).clone();
		$.setDirty(builder.isDirty());
		$.descendants = Arrays2.copy(builder.descendants);
		$.ancestors = Arrays2.copy(builder.ancestors);
		
		return $;
	}

	private IBranchPath branchPath;

	/**
	 * Bi-directional map for storing SNOMED CT concept IDs. 
	 */
	private LongBidiMapWithInternalId nodes;

	/**
	 * Map for storing active IS_A type SNOMED CT relationship representations. Keys are the unique relationship identifiers.
	 * <br>For values see: {@link IsAStatementWithId}.
	 */
	private LongKeyMap edges;

	private SnomedTaxonomyBuilder() {}
	
	public SnomedTaxonomyBuilder(final IBranchPath branchPath, final IsAStatementWithId[] isAStatements, final long[] conceptIds) {
		this.branchPath = Preconditions.checkNotNull(branchPath, "Branch path argument cannot be null.");
		
		final Runnable initStatementsRunnable = new Runnable() {
			@Override public void run() {
				
				edges = isAStatements.length < 1 ? new StatementMap() : new StatementMap(isAStatements.length);
				
				for (final IsAStatementWithId statement : isAStatements) {
					edges.put(statement.getRelationshipId(), new long[] { statement.getDestinationId(), statement.getSourceId() });
				}
				
			}
		};
		
		final Runnable initConceptsRunnable = new Runnable() {
			@Override public void run() {
				nodes = new LongBidiMapWithInternalId(conceptIds.length);
				for (final long ids : conceptIds) {
					nodes.put(ids/*conceptId*/, ids/*conceptId*/);
				}
			}
		};
		
		ForkJoinUtils.runInParallel(initConceptsRunnable, initStatementsRunnable);
		setDirty(true);
	}
	
	public SnomedTaxonomyBuilder(final IBranchPath branchPath, final StatementCollectionMode mode) {
		this.branchPath = Preconditions.checkNotNull(branchPath, "Branch path argument cannot be null.");
		
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

				final LongCollection idsStorageMap = getTerminologyBrowser().getAllConceptIds(branchPath);
				nodes = new LongBidiMapWithInternalId(idsStorageMap.size());
				for (final LongIterator itr = idsStorageMap.iterator(); itr.hasNext(); /**/) {
					final long id = itr.next();
					nodes.put(id/*conceptId*/, id/*storageKey*/);
				}
				
			}

		};
		
		ForkJoinUtils.runInParallel(initConceptsRunnable, initStatementsRunnable);
		setDirty(true);
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