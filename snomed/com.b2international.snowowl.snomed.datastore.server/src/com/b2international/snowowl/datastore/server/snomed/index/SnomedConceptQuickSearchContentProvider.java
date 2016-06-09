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

import static com.google.common.collect.Maps.newHashMap;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.ecore.EPackage;

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.index.compat.Highlighting;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.domain.IComponent;
import com.b2international.snowowl.core.quicksearch.CompactQuickSearchElement;
import com.b2international.snowowl.core.quicksearch.IQuickSearchProvider;
import com.b2international.snowowl.core.quicksearch.QuickSearchContentResult;
import com.b2international.snowowl.core.quicksearch.QuickSearchElement;
import com.b2international.snowowl.datastore.IBranchPathMap;
import com.b2international.snowowl.datastore.quicksearch.AbstractQuickSearchContentProvider;
import com.b2international.snowowl.datastore.quicksearch.IQuickSearchContentProvider;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.SnomedPackage;
import com.b2international.snowowl.snomed.core.domain.ISnomedConcept;
import com.b2international.snowowl.snomed.core.domain.ISnomedDescription;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.snomed.core.lang.LanguageSetting;
import com.b2international.snowowl.snomed.datastore.EscgExpressionConstants;
import com.b2international.snowowl.snomed.datastore.quicksearch.SnomedConceptQuickSearchProvider;
import com.b2international.snowowl.snomed.datastore.request.SnomedConceptSearchRequestBuilder;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.primitives.Ints;

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
					Highlighting.getMatchRegions(queryExpression, label),
					Highlighting.getSuffixes(queryExpression, label));
		}

	}

	@Override
	public QuickSearchContentResult getComponents(final String queryExpression, final IBranchPathMap branchPathMap, final int limit, Map<String, Object> configuration) {
		if (configuration == null) {
			configuration = Collections.emptyMap();
		}
		
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
			.withSearchProfile(userId)
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
		
		final List<QuickSearchElement> quickSearchElements = Lists.newArrayList();

		final SnomedConcepts matches = req.build(branchPath.getPath()).executeSync(getEventBus());
		final Map<String, ISnomedConcept> concepts = newHashMap(FluentIterable.from(matches).uniqueIndex(IComponent.ID_FUNCTION));
		// XXX sort only non-fuzzy matches
		final List<QuickSearchElement> results = FluentIterable.from(matches)
				.transform(new SnomedConceptConverterFunction(queryExpression, false))
				.toSortedList(new Comparator<QuickSearchElement>() {
					@Override
					public int compare(QuickSearchElement o1, QuickSearchElement o2) {
						final Float o1Score = concepts.get(o1.getId()).getScore();
						final Float o2Score = concepts.get(o2.getId()).getScore();
						final int sortByScore = o1Score.compareTo(o2Score);
						if (sortByScore == 0) {
							return Ints.compare(o1.getLabel().length(), o2.getLabel().length());
						} else {
							return -sortByScore;
						} 
					}
				});

		quickSearchElements.addAll(results);

		if (matches.getTotal() < limit) {
			req.withFuzzySearch();

			final SnomedConcepts fuzzyMatches = req.build(branchPath.getPath()).executeSync(getEventBus());

			final ImmutableList<QuickSearchElement> approximateResults = FluentIterable.from(fuzzyMatches)
					.filter(new Predicate<ISnomedConcept>() {
						@Override
						public boolean apply(ISnomedConcept input) {
							return !concepts.containsKey(input.getId());
						}
					}).transform(new SnomedConceptConverterFunction(queryExpression, true))
					.toList();

			quickSearchElements.addAll(approximateResults);
		}

		return new QuickSearchContentResult(matches.getTotal(), quickSearchElements);
	}

	private IEventBus getEventBus() {
		return ApplicationContext.getInstance().getService(IEventBus.class);
	}

	@Override
	protected EPackage getEPackage() {
		return SnomedPackage.eINSTANCE;
	}
	
}
