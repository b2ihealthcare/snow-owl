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

import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EPackage;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.quicksearch.CompactQuickSearchElement;
import com.b2international.snowowl.core.quicksearch.QuickSearchContentResult;
import com.b2international.snowowl.core.quicksearch.QuickSearchElement;
import com.b2international.snowowl.datastore.IBranchPathMap;
import com.b2international.snowowl.datastore.quicksearch.AbstractQuickSearchContentProvider;
import com.b2international.snowowl.datastore.quicksearch.IQuickSearchContentProvider;
import com.b2international.snowowl.snomed.SnomedPackage;
import com.b2international.snowowl.snomed.datastore.index.SnomedDescriptionIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.SnomedDescriptionIndexQueryAdapter;
import com.b2international.snowowl.snomed.datastore.index.SnomedDescriptionReducedQueryAdapter;
import com.b2international.snowowl.snomed.datastore.index.SnomedIndexService;
import com.google.common.base.Function;
import com.google.common.collect.Lists;

/**
 * Server side, Net4j independent service for contributing SNOMED&nbsp;CT descriptions as the underlying elements for the quick search provider.
 * 
 * @see IIndexServerService
 * @see IQuickSearchContentProvider
 */
public class SnomedDescriptionQuickSearchContentProvider extends AbstractQuickSearchContentProvider implements IQuickSearchContentProvider {

	private static final class SnomedDescriptionConverterFunction implements Function<SnomedDescriptionIndexEntry, QuickSearchElement> {
		
		private final String queryExpression;
		
		public SnomedDescriptionConverterFunction(String queryExpression) {
			this.queryExpression = queryExpression;
		}

		@Override 
		public QuickSearchElement apply(final SnomedDescriptionIndexEntry input) {
			return new CompactQuickSearchElement(input.getId(), 
					input.getTypeId(), 
					input.getLabel(), 
					false,
					getMatchRegions(queryExpression, input.getLabel()),
					getSuffixes(queryExpression, input.getLabel()));
		}
	}

	@Override
	public QuickSearchContentResult getComponents(final String queryExpression, final IBranchPathMap branchPathMap, final int limit, final Map<String, Object> configuration) {

		final SnomedDescriptionIndexQueryAdapter queryAdapter = new SnomedDescriptionReducedQueryAdapter(queryExpression, 
				SnomedDescriptionReducedQueryAdapter.SEARCH_DESCRIPTION_ACTIVE_ONLY |
				SnomedDescriptionReducedQueryAdapter.SEARCH_DESCRIPTION_ID |
				SnomedDescriptionReducedQueryAdapter.SEARCH_DESCRIPTION_TERM,
				null,
				getComponentIds(configuration));
		
		final SnomedIndexService searcher = ApplicationContext.getInstance().getService(SnomedIndexService.class);
		final IBranchPath branchPath = getBranchPath(branchPathMap);
		final int totalHitCount = searcher.getHitCount(branchPath, queryAdapter);
		
		if (totalHitCount < 1) {
			return new QuickSearchContentResult();
		}
		
		return new QuickSearchContentResult(totalHitCount, convertToDTO(queryExpression, searcher.search(branchPath, queryAdapter, limit)));
	}

	@Override
	protected EPackage getEPackage() {
		return SnomedPackage.eINSTANCE;
	}
	
	private List<QuickSearchElement> convertToDTO(final String queryExpression, final List<SnomedDescriptionIndexEntry> searchResults) {
		return Lists.transform(searchResults, new SnomedDescriptionConverterFunction(queryExpression));
	}
}
