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

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.ecore.EPackage;

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.quicksearch.CompactQuickSearchElement;
import com.b2international.snowowl.core.quicksearch.IQuickSearchProvider;
import com.b2international.snowowl.core.quicksearch.QuickSearchContentResult;
import com.b2international.snowowl.core.quicksearch.QuickSearchElement;
import com.b2international.snowowl.datastore.IBranchPathMap;
import com.b2international.snowowl.datastore.quicksearch.AbstractQuickSearchContentProvider;
import com.b2international.snowowl.datastore.quicksearch.IQuickSearchContentProvider;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.SnomedPackage;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.domain.ISnomedConcept;
import com.b2international.snowowl.snomed.core.domain.ISnomedDescription;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.snomed.core.lang.LanguageSetting;
import com.b2international.snowowl.snomed.datastore.EscgExpressionConstants;
import com.b2international.snowowl.snomed.datastore.quicksearch.SnomedConceptQuickSearchProvider;
import com.b2international.snowowl.snomed.datastore.request.SnomedConceptSearchRequestBuilder;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;

/**
 * Server side, Net4j independent service for providing the SNOMED&nbsp;CT concepts as the content of quick search provider.
 * 
 * @see IQuickSearchContentProvider
 * @see IIndexServerService
 * @see SnomedIndexServerService
 */
public class SnomedConceptQuickSearchContentProvider extends AbstractQuickSearchContentProvider implements IQuickSearchContentProvider {

	private static final class SnomedConceptConverterFunction implements Function<ISnomedConcept, QuickSearchElement> {
		
		private final String queryExpression;
		private final boolean approximate;
		
		private SnomedConceptConverterFunction(final String queryExpression, final boolean approximate) {
			this.queryExpression = queryExpression;
			this.approximate = approximate;
		}

		@Override 
		public QuickSearchElement apply(final ISnomedConcept input) {
			final ISnomedDescription pt = input.getPt();
			final String label = pt != null ? pt.getTerm() : input.getId();
			return new CompactQuickSearchElement(
					input.getId(), 
					input.getIconId(), 
					label, 
					approximate,
					getMatchRegions(queryExpression, label),
					getSuffixes(queryExpression, label));
		}

	}

	@Override
	public QuickSearchContentResult getComponents(final String queryExpression, final IBranchPathMap branchPathMap, final int limit, Map<String, Object> configuration) {
		if (configuration == null) {
			configuration = Collections.emptyMap();
		}
		
		// TODO reintroduce search profiles in concept search
		final String userId = String.valueOf(configuration.get(IQuickSearchProvider.CONFIGURATION_USER_ID));
		// TODO replace server-side LOCALES with client side one via configuration
		final List<ExtendedLocale> locales = ApplicationContext.getInstance().getService(LanguageSetting.class).getLanguagePreference();
		
		final IBranchPath branchPath = getBranchPath(branchPathMap);
		
		final SnomedConceptSearchRequestBuilder req = SnomedRequests
			.prepareSearchConcept()
			.filterByActive(true)
			.filterByTerm(queryExpression)
			.filterByExtendedLocales(locales)
			.filterByDescriptionType("<<" + Concepts.SYNONYM)
			.withDoi()
			.setExpand("pt()")
			.setLimit(limit);
		
		if (configuration.containsKey(SnomedConceptQuickSearchProvider.CONFIGURATION_PARENT_ID)) {
			req.filterByAncestor((String) configuration.get(SnomedConceptQuickSearchProvider.CONFIGURATION_PARENT_ID));
		} else if (configuration.containsKey(IQuickSearchProvider.CONFIGURATION_VALUE_ID_SET)) {
			final Set<String> componentIdFilter = (Set<String>) configuration.get(IQuickSearchProvider.CONFIGURATION_VALUE_ID_SET);
			req.setComponentIds(componentIdFilter);
		} else if (configuration.containsKey(IQuickSearchProvider.CONFIGURATION_VALUE_ID_EXPRESSION)) {
			final String expression = (String) configuration.get(IQuickSearchProvider.CONFIGURATION_VALUE_ID_EXPRESSION);
			if (EscgExpressionConstants.UNRESTRICTED_EXPRESSION.equals(expression)) {
				// no-op
			} else if (EscgExpressionConstants.REJECT_ALL_EXPRESSION.equals(expression)) {
				return new QuickSearchContentResult(); //represents no result
			} else {
				req.filterByEscg(expression);
			}
		}
		
		final SnomedConcepts matches = req.build(branchPath.getPath()).executeSync(ApplicationContext.getInstance().getService(IEventBus.class));
		
		// TODO reintroduce fuzzy search
//		final List<SnomedConceptIndexEntry> approximateResults = newArrayList();
//		
//		if (results.size() < limit) {
//			final SnomedFuzzyQueryAdapter fuzzyAdapter = null == conceptIdSupplier
//					? new SnomedFuzzyQueryAdapter(queryExpression, userId, restrictionQuery)
//					: new SnomedFuzzyQueryAdapter(queryExpression, userId, conceptIdSupplier.get());
//							
//			approximateResults.addAll(searcher.search(branchPath, fuzzyAdapter, limit));
//		}
		
		
		
		return new QuickSearchContentResult(matches.getTotal(), FluentIterable.from(matches).transform(new SnomedConceptConverterFunction(queryExpression, false)).toList());
	}

	@Override
	protected EPackage getEPackage() {
		return SnomedPackage.eINSTANCE;
	}
	
}
