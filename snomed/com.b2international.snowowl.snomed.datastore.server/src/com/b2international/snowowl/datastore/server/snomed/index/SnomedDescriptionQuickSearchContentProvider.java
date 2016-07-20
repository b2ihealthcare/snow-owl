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

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EPackage;

import com.b2international.index.compat.Highlighting;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.quicksearch.CompactQuickSearchElement;
import com.b2international.snowowl.core.quicksearch.QuickSearchContentResult;
import com.b2international.snowowl.core.quicksearch.QuickSearchElement;
import com.b2international.snowowl.datastore.IBranchPathMap;
import com.b2international.snowowl.datastore.quicksearch.AbstractQuickSearchContentProvider;
import com.b2international.snowowl.datastore.quicksearch.IQuickSearchContentProvider;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.SnomedPackage;
import com.b2international.snowowl.snomed.core.domain.ISnomedDescription;
import com.b2international.snowowl.snomed.core.domain.SnomedDescriptions;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.google.common.base.Function;
import com.google.common.collect.Sets;

/**
 * Server side, Net4j independent service for contributing SNOMED&nbsp;CT descriptions as the underlying elements for the quick search provider.
 * 
 * @see IIndexServerService
 * @see IQuickSearchContentProvider
 */
public class SnomedDescriptionQuickSearchContentProvider extends AbstractQuickSearchContentProvider implements IQuickSearchContentProvider {

	@Override
	public QuickSearchContentResult getComponents(final String queryExpression, final IBranchPathMap branchPathMap, final int limit, final Map<String, Object> configuration) {
		final IBranchPath branchPath = getBranchPath(branchPathMap);
		return SnomedRequests.prepareSearchDescription()
			.filterByActive(true)
			.filterByTerm(queryExpression)
			.setComponentIds(Sets.newHashSet(getComponentIds(configuration)))
			.setLimit(limit)
			.build(branchPath.getPath())
			.execute(ApplicationContext.getServiceForClass(IEventBus.class))
			.then(new Function<SnomedDescriptions, QuickSearchContentResult>() {
				@Override
				public QuickSearchContentResult apply(SnomedDescriptions input) {
					
					final List<QuickSearchElement> hits = newArrayList();
					
					for (ISnomedDescription description : input) {
						final CompactQuickSearchElement hit = new CompactQuickSearchElement(description.getId(), 
								description.getTypeId(), 
								description.getTerm(), 
								false,
								Highlighting.getMatchRegions(queryExpression, description.getTerm()),
								Highlighting.getSuffixes(queryExpression, description.getTerm()));
						hits.add(hit);
					}
					
					return new QuickSearchContentResult(input.getTotal(), hits);
				}
			})
			.getSync();
	}

	@Override
	protected EPackage getEPackage() {
		return SnomedPackage.eINSTANCE;
	}
	
}
