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

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EPackage;

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.quicksearch.CompactQuickSearchElement;
import com.b2international.snowowl.core.quicksearch.QuickSearchContentResult;
import com.b2international.snowowl.core.quicksearch.QuickSearchElement;
import com.b2international.snowowl.datastore.IBranchPathMap;
import com.b2international.snowowl.datastore.quicksearch.AbstractQuickSearchContentProvider;
import com.b2international.snowowl.datastore.quicksearch.IQuickSearchContentProvider;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.core.domain.ISnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSet;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSets;
import com.b2international.snowowl.snomed.datastore.SnomedClientTerminologyBrowser;
import com.b2international.snowowl.snomed.datastore.quicksearch.SnomedRefSetQuickSearchProvider;
import com.b2international.snowowl.snomed.datastore.request.SnomedConceptSearchRequestBuilder;
import com.b2international.snowowl.snomed.datastore.request.SnomedRefSetSearchRequestBuilder;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
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
			final String label = concept.getPt() == null ? concept.getId() : concept.getPt().getTerm();
			return new CompactQuickSearchElement(input.getId(), 
					input.getIconId(), 
					label, 
					false,
					getMatchRegions(queryExpression, label),
					getSuffixes(queryExpression, label));
		}
	}

	@Override
	public QuickSearchContentResult getComponents(final String queryExpression, final IBranchPathMap branchPathMap, final int limit, final Map<String, Object> configuration) {
		final IEventBus bus = ApplicationContext.getInstance().getServiceChecked(IEventBus.class);
		final IBranchPath branchPath = getBranchPath(branchPathMap);
		
		// TODO provide this via the configuration object from the client side
		final List<ExtendedLocale> locales = SnomedClientTerminologyBrowser.LOCALES;
		
		final SnomedConceptSearchRequestBuilder req = SnomedRequests
			.prepareSearchConcept()
			.filterByActive(true)
			.filterByTerm(queryExpression)
			.setLimit(limit)
			.setExpand("pt()")
			.setLocales(locales);
		
		// TODO add component ID filter 
		
		final SnomedConcepts matchingConcepts = req.build(branchPath.getPath()).executeSync(bus);
		
		final Map<String, ISnomedConcept> matchingConceptsById = FluentIterable.from(matchingConcepts).uniqueIndex(new Function<ISnomedConcept, String>() {
			@Override
			public String apply(ISnomedConcept input) {
				return input.getId();
			}
		});
		
		final Collection<SnomedRefSetType> refSetTypes = configuration.containsKey(SnomedRefSetQuickSearchProvider.REFSET_TYPE_CONFIG_ID)
				? ImmutableSet.copyOf(getRefSetType(configuration)) : Collections.<SnomedRefSetType> emptySet();
		
		final SnomedRefSetSearchRequestBuilder refSetReq = SnomedRequests
			.prepareSearchRefSet()
			.filterByActive(true)
			.filterByTypes(refSetTypes)
			.filterByReferencedComponentTypes(getReferencedComponentType(configuration))
			.setComponentIds(ImmutableSet.copyOf(matchingConceptsById.keySet()))
			.setLimit(limit);
		
		final SnomedReferenceSets matchingRefSets = refSetReq.build(branchPath.getPath()).executeSync(bus);
		
		// TODO how to compute the total hit count
		return new QuickSearchContentResult(matchingRefSets.getTotal(), Lists.transform(matchingRefSets.getItems(), new RefSetToQuickSearchElementConverter(queryExpression, matchingConceptsById)));
	}

	@Override
	protected EPackage getEPackage() {
		return SnomedRefSetPackage.eINSTANCE;
	}
	
	/*extracts the reference set type from the configuration*/
	private SnomedRefSetType[] getRefSetType(final Map<String, Object> configuration) {
		return null == configuration ? new SnomedRefSetType[] {} : getTypes(configuration);
	}

	/*returns with the reference set types given as an array of type ordinal.*/
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
	
	/*extracts the reference component type from the configuration*/
	private String getReferencedComponentType(final Map<String, Object> configuration) {
		return null == configuration ? null : (String) configuration.get(SnomedRefSetQuickSearchProvider.REFERENCED_COMPONENT_TYPE_CONFIG_ID);
	}

}