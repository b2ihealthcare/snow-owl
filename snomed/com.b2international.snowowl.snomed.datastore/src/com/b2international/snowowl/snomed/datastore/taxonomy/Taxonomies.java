/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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

import com.b2international.collections.longs.LongCollection;
import com.b2international.collections.longs.LongSet;
import com.b2international.commons.Pair;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.datastore.ICDOCommitChangeSet;
import com.b2international.snowowl.snomed.datastore.IsAStatementWithId;
import com.b2international.snowowl.snomed.datastore.SnomedStatementBrowser;
import com.b2international.snowowl.snomed.datastore.StatementCollectionMode;

/**
 * @since 4.7
 */
public final class Taxonomies {

	private Taxonomies() {
	}
	
	public static Taxonomy inferred(IBranchPath branchPath, ICDOCommitChangeSet commitChangeSet, LongCollection conceptIds, SnomedStatementBrowser statementBrowser) {
		return buildTaxonomy(branchPath, commitChangeSet, conceptIds, statementBrowser, StatementCollectionMode.INFERRED_ISA_ONLY);
	}
	
	public static Taxonomy stated(IBranchPath branchPath, ICDOCommitChangeSet commitChangeSet, LongCollection conceptIds, SnomedStatementBrowser statementBrowser) {
		return buildTaxonomy(branchPath, commitChangeSet, conceptIds, statementBrowser, StatementCollectionMode.STATED_ISA_ONLY);
	}

	private static Taxonomy buildTaxonomy(IBranchPath branchPath, ICDOCommitChangeSet commitChangeSet, LongCollection conceptIds,
			SnomedStatementBrowser statementBrowser, final StatementCollectionMode mode) {
		final IsAStatementWithId[] statements = statementBrowser.getActiveStatements(branchPath, mode);
		final ISnomedTaxonomyBuilder oldTaxonomy = new SnomedTaxonomyBuilder(conceptIds, statements);
		final ISnomedTaxonomyBuilder newTaxonomy = new SnomedTaxonomyBuilder(conceptIds, statements);
		oldTaxonomy.build();
		new SnomedTaxonomyUpdateRunnable(branchPath, commitChangeSet, newTaxonomy, mode.getCharacteristicType()).run();
		final Pair<LongSet, LongSet> diff = newTaxonomy.difference(oldTaxonomy);
		return new Taxonomy(newTaxonomy, oldTaxonomy, diff);
	}
	
}
