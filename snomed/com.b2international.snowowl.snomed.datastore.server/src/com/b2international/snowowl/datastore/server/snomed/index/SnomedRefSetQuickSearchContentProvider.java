/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.ecore.EPackage;

import com.b2international.commons.StringUtils;
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
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.domain.ISnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSet;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSets;
import com.b2international.snowowl.snomed.core.lang.LanguageSetting;
import com.b2international.snowowl.snomed.datastore.quicksearch.SnomedRefSetQuickSearchProvider;
import com.b2international.snowowl.snomed.datastore.request.SnomedConceptSearchRequestBuilder;
import com.b2international.snowowl.snomed.datastore.request.SnomedRefSetSearchRequestBuilder;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

/**
 * Server side, Net4j independent service for contributing SNOMED&nbsp;CT reference sets as the underlying elements for the quick search provider.
 * 
 * 
 * @see IIndexServerService
 * @see SnomedRefSetIndexServerService
 * @see IQuickSearchContentProvider
 */
public class SnomedRefSetQuickSearchContentProvider extends AbstractQuickSearchContentProvider implements IQuickSearchContentProvider {

	private static final class RefSetToQuickSearchElementConverter implements Function<SnomedReferenceSet, QuickSearchElement> {
		
		private final String queryExpression;
		private final Map<String, ISnomedConcept> identifierConceptMap;

		public RefSetToQuickSearchElementConverter(String queryExpression, Map<String, ISnomedConcept> identifierConceptMap) {
			this.identifierConceptMap = identifierConceptMap;
			this.queryExpression = queryExpression;
		}

		@Override 
		public QuickSearchElement apply(final SnomedReferenceSet input) {
			final ISnomedConcept concept = identifierConceptMap.get(input.getId());
			
			if (concept == null) {
				return null;
			} else {
				final String label = concept.getPt() == null ? concept.getId() : concept.getPt().getTerm();
				return new CompactQuickSearchElement(input.getId(), 
						input.getIconId(), 
						label, 
						false,
						getMatchRegions(queryExpression, label),
						getSuffixes(queryExpression, label));
			}
		}
	}
	
	private static final ImmutableSet<SnomedRefSetType> DEFAULT_REFSET_TYPES = ImmutableSet.of(SnomedRefSetType.SIMPLE,
			SnomedRefSetType.SIMPLE_MAP, SnomedRefSetType.QUERY, SnomedRefSetType.EXTENDED_MAP, SnomedRefSetType.DESCRIPTION_TYPE,
			SnomedRefSetType.COMPLEX_MAP, SnomedRefSetType.ATTRIBUTE_VALUE);

	@Override
	public QuickSearchContentResult getComponents(final String filterText, final IBranchPathMap branchPathMap, final int limit,
			final Map<String, Object> configuration) {
		final IBranchPath branchPath = getBranchPath(branchPathMap);

		if (StringUtils.isEmpty(filterText)) {
			return getUnfilteredComponents(limit, configuration, branchPath);
		} else {
			return getFilteredComponents(filterText, limit, configuration, branchPath);
		}
	}

	private QuickSearchContentResult getUnfilteredComponents(final int limit, final Map<String, Object> configuration, final IBranchPath branchPath) {
		
		final SnomedRefSetSearchRequestBuilder refSetRequest = buildRefSetSearchRequest(limit, configuration, 
				getComponentIdsFromConfiguration(configuration));
		final SnomedReferenceSets matchingRefSets = refSetRequest.build(branchPath.getPath())
				.executeSync(getEventBus());

		if (matchingRefSets.getTotal() <= 0) {
			return new QuickSearchContentResult();
		}

		final ImmutableList<String> conceptIds = FluentIterable.from(matchingRefSets.getItems())
				.transform(new Function<SnomedReferenceSet, String>() { @Override public String apply(SnomedReferenceSet input) {
					return input.getId();
				}})
				.toList();

		final SnomedConceptSearchRequestBuilder conceptRequest = buildConceptSearchRequest("", limit, conceptIds);
		final SnomedConcepts matchingConcepts = conceptRequest.build(branchPath.getPath()).executeSync(getEventBus());

		final Map<String, ISnomedConcept> matchingConceptsById = createMatchingConceptsMap(matchingConcepts);
		final RefSetToQuickSearchElementConverter converter = new RefSetToQuickSearchElementConverter("", matchingConceptsById);
		final List<QuickSearchElement> elements = FluentIterable.from(matchingRefSets.getItems())
				.transform(converter)
				.filter(Predicates.notNull())
				.toList();

		// XXX: Total count can be off if inactive identifier concepts are present with active reference sets
		return new QuickSearchContentResult(matchingRefSets.getTotal(), elements);
	}

	private QuickSearchContentResult getFilteredComponents(final String queryExpression, final int limit,
			final Map<String, Object> configuration, final IBranchPath branchPath) {
		final SnomedConceptSearchRequestBuilder conceptRequest = buildConceptSearchRequest(queryExpression, limit,
				getComponentIdsFromConfiguration(configuration));
		final SnomedConcepts matchingConcepts = conceptRequest.build(branchPath.getPath()).executeSync(getEventBus());

		if (matchingConcepts.getTotal() <= 0) {
			return new QuickSearchContentResult();
		}

		final Map<String, ISnomedConcept> matchingConceptsById = createMatchingConceptsMap(matchingConcepts);

		final SnomedRefSetSearchRequestBuilder refSetRequest = buildRefSetSearchRequest(limit, configuration, matchingConceptsById.keySet());
		final SnomedReferenceSets matchingRefSets = refSetRequest.build(branchPath.getPath()).executeSync(getEventBus());

		return new QuickSearchContentResult(matchingRefSets.getTotal(), Lists.transform(matchingRefSets.getItems(),
				new RefSetToQuickSearchElementConverter(queryExpression, matchingConceptsById)));
	}

	private SnomedConceptSearchRequestBuilder buildConceptSearchRequest(final String filterText, final int limit,
			final List<String> componentIds) {
		
		final int searchLimit = componentIds.isEmpty() ? limit : componentIds.size();
		
		return SnomedRequests
				.prepareSearchConcept()
				.filterByActive(true)
				.filterByTerm(filterText)
				.filterByDescriptionType("<<" + Concepts.SYNONYM)
				.filterByAncestor(Concepts.REFSET_ALL)
				.setLimit(searchLimit)
				.setExpand("pt()")
				.setLocales(getLocales())
				.setComponentIds(componentIds);
	}
	
	private SnomedRefSetSearchRequestBuilder buildRefSetSearchRequest(final int limit, 
			final Map<String, Object> configuration, 
			final Collection<String> componentIds) {
		
		final int searchLimit = componentIds.isEmpty() ? limit : componentIds.size();
		
		return SnomedRequests.prepareSearchRefSet()
			.filterByActive(true)
			.filterByTypes(getRefSetTypes(configuration))
			.filterByReferencedComponentType(getReferencedComponentType(configuration))
			.setComponentIds(ImmutableSet.copyOf(componentIds))
			.setLimit(searchLimit);
	}
	
	private Map<String, ISnomedConcept> createMatchingConceptsMap(final SnomedConcepts matchingConcepts) {
		final Map<String, ISnomedConcept> matchingConceptsById = FluentIterable.from(matchingConcepts)
				.uniqueIndex(new Function<ISnomedConcept, String>() {
					@Override
					public String apply(ISnomedConcept input) {
						return input.getId();
					}
				});
		return matchingConceptsById;
	}

	@Override
	protected EPackage getEPackage() {
		return SnomedRefSetPackage.eINSTANCE;
	}
	
	private List<String> getComponentIdsFromConfiguration(final Map<String, Object> configuration) {
		return configuration.containsKey(IQuickSearchProvider.CONFIGURATION_VALUE_ID_SET)
				? ImmutableList.copyOf(getComponentIds(configuration)) : Collections.<String> emptyList();
	}
	
	private Set<SnomedRefSetType> getRefSetTypes(final Map<String, Object> configuration) {
		return configuration.containsKey(SnomedRefSetQuickSearchProvider.REFSET_TYPE_CONFIG_ID)
				? ImmutableSet.copyOf(getRefSetType(configuration))	: DEFAULT_REFSET_TYPES;
	}

	/* extracts the reference set type from the configuration */
	private SnomedRefSetType[] getRefSetType(final Map<String, Object> configuration) {
		return null == configuration ? new SnomedRefSetType[] {} : getTypes(configuration);
	}

	/*
	 * returns with the reference set types given as an array of type ordinal.
	 */
	private SnomedRefSetType[] getTypes(final Map<String, Object> configuration) {

		final int[] typeOrdinals = (int[]) configuration.get(SnomedRefSetQuickSearchProvider.REFSET_TYPE_CONFIG_ID);
		final SnomedRefSetType[] types;

		if (typeOrdinals == null)
			types = new SnomedRefSetType[0];
		else {
			types = new SnomedRefSetType[typeOrdinals.length];
			for (int i = 0; i < typeOrdinals.length; i++) {
				types[i] = SnomedRefSetType.get(typeOrdinals[i]);
			}
		}

		return types;
	}

	/* extracts the reference component type from the configuration */
	private String getReferencedComponentType(final Map<String, Object> configuration) {
		return null == configuration ? null
				: (String) configuration.get(SnomedRefSetQuickSearchProvider.REFERENCED_COMPONENT_TYPE_CONFIG_ID);
	}

	private IEventBus getEventBus() {
		return ApplicationContext.getInstance().getServiceChecked(IEventBus.class);
	}

	private List<ExtendedLocale> getLocales() {
		// TODO provide this via the configuration object from the client side
		return ApplicationContext.getInstance().getService(LanguageSetting.class).getLanguagePreference();
	}

}