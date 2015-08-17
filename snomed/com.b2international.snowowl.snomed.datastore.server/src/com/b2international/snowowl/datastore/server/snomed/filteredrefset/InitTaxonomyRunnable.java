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
package com.b2international.snowowl.datastore.server.snomed.filteredrefset;

import java.util.concurrent.atomic.AtomicReference;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.BooleanClause.Occur;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.index.CommonIndexConstants;
import com.b2international.snowowl.datastore.index.IndexUtils;
import com.b2international.snowowl.datastore.index.LongDocValuesCollector;
import com.b2international.snowowl.datastore.index.field.ComponentIdLongField;
import com.b2international.snowowl.datastore.server.index.IndexServerService;
import com.b2international.snowowl.datastore.server.snomed.index.StatementCollector;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.datastore.IsAStatement;
import com.b2international.snowowl.snomed.datastore.StatementCollectionMode;
import com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants;
import com.b2international.snowowl.snomed.datastore.index.SnomedHierarchy;

/**
 * 
 */
public final class InitTaxonomyRunnable implements Runnable {
	
	private final int maxDoc;
	private final TermQuery conceptTypeQuery;
	private final AtomicReference<SnomedHierarchy> hierarchyReference;
	private final TermQuery activeComponentQuery;
	private final IndexServerService<?> indexService;
	private final IBranchPath branchPath;

	public InitTaxonomyRunnable(int maxDoc, 
			TermQuery conceptTypeQuery,
			AtomicReference<SnomedHierarchy> hierarchyReference, 
			TermQuery activeComponentQuery,
			IndexServerService<?> indexService,
			IBranchPath branchPath) {
		
		this.maxDoc = maxDoc;
		this.conceptTypeQuery = conceptTypeQuery;
		this.hierarchyReference = hierarchyReference;
		this.activeComponentQuery = activeComponentQuery;
		this.indexService = indexService;
		this.branchPath = branchPath;
	}

	@Override public void run() {
		
		final BooleanQuery allConceptQuery = new BooleanQuery(true);

		allConceptQuery.add(conceptTypeQuery, Occur.MUST);
		allConceptQuery.add(activeComponentQuery, Occur.MUST);

		final BooleanQuery statementQuery = new BooleanQuery(true);
		statementQuery.add(new TermQuery(new Term(CommonIndexConstants.COMPONENT_TYPE, IndexUtils.intToPrefixCoded(SnomedTerminologyComponentConstants.RELATIONSHIP_NUMBER))), Occur.MUST);
		statementQuery.add(new TermQuery(new Term(SnomedIndexBrowserConstants.COMPONENT_ACTIVE, IndexUtils.intToPrefixCoded(1))), Occur.MUST);
		statementQuery.add(new TermQuery(new Term(SnomedIndexBrowserConstants.RELATIONSHIP_ATTRIBUTE_ID, IndexUtils.longToPrefixCoded(Concepts.IS_A))), Occur.MUST);

		final LongDocValuesCollector allConceptIdCollector = new LongDocValuesCollector(ComponentIdLongField.COMPONENT_ID, maxDoc);
		indexService.search(branchPath, allConceptQuery, allConceptIdCollector);

		final StatementCollector statementCollector = new StatementCollector(maxDoc, StatementCollectionMode.NO_IDS);
		indexService.search(branchPath, statementQuery, statementCollector);

		final IsAStatement[] statements = statementCollector.getStatements();
		hierarchyReference.set(new SnomedHierarchy(allConceptIdCollector.getValues().toArray(), statements));
	}
}