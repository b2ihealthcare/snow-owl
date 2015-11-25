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
import java.util.Set;

import javax.annotation.Nullable;

import org.apache.lucene.search.Query;
import org.eclipse.emf.ecore.EPackage;

import com.b2international.commons.ClassUtils;
import com.b2international.commons.StringUtils;
import com.b2international.commons.pcj.LongSets;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.ComponentUtils;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.browser.ITerminologyBrowser;
import com.b2international.snowowl.core.quicksearch.CompactQuickSearchElement;
import com.b2international.snowowl.core.quicksearch.IQuickSearchProvider;
import com.b2international.snowowl.core.quicksearch.QuickSearchContentResult;
import com.b2international.snowowl.core.quicksearch.QuickSearchElement;
import com.b2international.snowowl.datastore.IBranchPathMap;
import com.b2international.snowowl.datastore.quicksearch.AbstractQuickSearchContentProvider;
import com.b2international.snowowl.datastore.quicksearch.IQuickSearchContentProvider;
import com.b2international.snowowl.datastore.server.snomed.escg.EscgParseFailedException;
import com.b2international.snowowl.datastore.utils.UnrestrictedStringSet;
import com.b2international.snowowl.snomed.SnomedPackage;
import com.b2international.snowowl.snomed.datastore.EscgExpressionConstants;
import com.b2international.snowowl.snomed.datastore.SnomedTerminologyBrowser;
import com.b2international.snowowl.snomed.datastore.escg.IEscgQueryEvaluatorService;
import com.b2international.snowowl.snomed.datastore.index.SnomedDOIQueryAdapter;
import com.b2international.snowowl.snomed.datastore.index.SnomedFuzzyQueryAdapter;
import com.b2international.snowowl.snomed.datastore.index.SnomedIndexService;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptIndexEntry;
import com.b2international.snowowl.snomed.datastore.quicksearch.SnomedConceptQuickSearchProvider;
import com.b2international.snowowl.snomed.datastore.services.ISnomedConceptNameProvider;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.Sets;

import bak.pcj.LongCollection;

/**
 * Server side, Net4j independent service for providing the SNOMED&nbsp;CT concepts as the content of quick search provider.
 * 
 * @see IQuickSearchContentProvider
 * @see IIndexServerService
 * @see SnomedIndexServerService
 */
public class SnomedConceptQuickSearchContentProvider extends AbstractQuickSearchContentProvider implements IQuickSearchContentProvider {

	private static final class SnomedConceptConverterFunction implements Function<SnomedConceptIndexEntry, QuickSearchElement> {
		
		private final IBranchPath branchPath;
		private final String queryExpression;
		private final boolean approximate;
		
		private SnomedConceptConverterFunction(IBranchPath branchPath, String queryExpression, boolean approximate) {
			this.branchPath = branchPath;
			this.queryExpression = queryExpression;
			this.approximate = approximate;
		}

		@Override 
		public QuickSearchElement apply(@Nullable final SnomedConceptIndexEntry input) {
			final String label = getLabel(input.getId());
			
			return new CompactQuickSearchElement(
					input.getId(), 
					input.getIconId(), 
					label, 
					approximate,
					getMatchRegions(queryExpression, label),
					getSuffixes(queryExpression, label));
		}

		private String getLabel(final String componentId) {
			// TODO: this will be driven by the user's language preferences
			return ApplicationContext.getServiceForClass(ISnomedConceptNameProvider.class).getComponentLabel(branchPath, componentId);
		}
	}

	private static class ParentValueRangeSupplier implements Supplier<String[]> {

		private final String parentId;
		private final IBranchPath branchPath;
		
		public ParentValueRangeSupplier(final IBranchPath branchPath, final String parentId) {
			this.branchPath = branchPath;
			this.parentId = parentId;
		}
		
		@Override public String[] get() {

			if (!StringUtils.isEmpty(parentId)) {
				
				final ITerminologyBrowser<SnomedConceptIndexEntry, String> browser = ApplicationContext.getInstance().getService(SnomedTerminologyBrowser.class);
				final SnomedConceptIndexEntry conceptMini = browser.getConcept(branchPath, parentId);
				
				if (null != conceptMini) {
					final Set<String> idSet = ComponentUtils.getIdSet(browser.getAllSubTypes(branchPath, conceptMini));
					return idSet.toArray(new String[idSet.size()]);
				}
			}
			
			return null;
		}
	}

	@Override
	public QuickSearchContentResult getComponents(final String queryExpression, final IBranchPathMap branchPathMap, final int limit, final Map<String, Object> configuration) {

		String userId = null;
		
		if (null != configuration) {
			userId = String.valueOf(configuration.get(IQuickSearchProvider.CONFIGURATION_USER_ID));
		}
		
		final IBranchPath branchPath = getBranchPath(branchPathMap);
		
		Supplier<String[]> conceptIdSupplier = null;
		SnomedDOIQueryAdapter doiAdapter = null;
		Query restrictionQuery = null;

		if (null != configuration && configuration.containsKey(SnomedConceptQuickSearchProvider.CONFIGURATION_PARENT_ID)) {
			conceptIdSupplier = Suppliers.memoize(new ParentValueRangeSupplier(branchPath, (String) configuration.get(SnomedConceptQuickSearchProvider.CONFIGURATION_PARENT_ID)));
			doiAdapter = new SnomedDOIQueryAdapter(queryExpression, userId, conceptIdSupplier.get());
		} else if (null != configuration && configuration.containsKey(IQuickSearchProvider.CONFIGURATION_VALUE_ID_SET)) {
			final Set<String> valueSet = (Set<String>) configuration.get(IQuickSearchProvider.CONFIGURATION_VALUE_ID_SET);
			conceptIdSupplier = (UnrestrictedStringSet.INSTANCE == valueSet) 
					? Suppliers.<String[]>ofInstance(null) 
					: Suppliers.ofInstance(valueSet.toArray(new String[valueSet.size()]));
					
			doiAdapter = new SnomedDOIQueryAdapter(queryExpression, userId, conceptIdSupplier.get());
					
		} else if (null != configuration && configuration.containsKey(IQuickSearchProvider.CONFIGURATION_VALUE_ID_EXPRESSION)) {
			
			final Object object = configuration.get(IQuickSearchProvider.CONFIGURATION_VALUE_ID_EXPRESSION);
			final String expression = ClassUtils.checkAndCast(object, String.class);

			if (EscgExpressionConstants.UNRESTRICTED_EXPRESSION.equals(expression)) {
				doiAdapter = new SnomedDOIQueryAdapter(queryExpression, userId, (String[]) null);
			} else if (EscgExpressionConstants.REJECT_ALL_EXPRESSION.equals(expression)) {
				return new QuickSearchContentResult(); //represents no result
			} else {
				try {
				restrictionQuery = createExpressionQuery(branchPath, expression);
				doiAdapter = new SnomedDOIQueryAdapter(queryExpression, userId, restrictionQuery);
				} catch (final EscgParseFailedException e) {
					//falling back to slower procedure. currently join queries are not supported on numeric fields in Lucene
					final LongCollection conceptIds = ApplicationContext.getInstance().getService(IEscgQueryEvaluatorService.class).evaluateConceptIds(branchPath, expression);
					final String[] ids = LongSets.toStringArray(conceptIds);
					doiAdapter = new SnomedDOIQueryAdapter(queryExpression, userId, ids);
					conceptIdSupplier = Suppliers.ofInstance(ids);
				}
			}
			
		} else {
			conceptIdSupplier = Suppliers.<String[]>ofInstance(null);
			doiAdapter = new SnomedDOIQueryAdapter(queryExpression, userId, conceptIdSupplier.get());
		}
		
		Preconditions.checkNotNull(doiAdapter, "Query adapter was null.");
		
		final SnomedIndexService searcher = ApplicationContext.getInstance().getService(SnomedIndexService.class);
		final int totalHitCount = searcher.getHitCount(branchPath, doiAdapter);
		
		final List<SnomedConceptIndexEntry> results = searcher.search(branchPath, doiAdapter, limit);
		final List<SnomedConceptIndexEntry> approximateResults = newArrayList();
		
		if (results.size() < limit) {
			final SnomedFuzzyQueryAdapter fuzzyAdapter = null == conceptIdSupplier
					? new SnomedFuzzyQueryAdapter(queryExpression, userId, restrictionQuery)
					: new SnomedFuzzyQueryAdapter(queryExpression, userId, conceptIdSupplier.get());
							
			approximateResults.addAll(searcher.search(branchPath, fuzzyAdapter, limit));
		}
		
		return new QuickSearchContentResult(totalHitCount, convertToDTO(branchPath, queryExpression, results, approximateResults));
	}

	@Override
	protected EPackage getEPackage() {
		return SnomedPackage.eINSTANCE;
	}
	
	private List<QuickSearchElement> convertToDTO(final IBranchPath branchPath,
			final String queryExpression,
			final List<SnomedConceptIndexEntry> results,
			final List<SnomedConceptIndexEntry> approximateResults) {

		final List<QuickSearchElement> convertedItems = newArrayList();
		final Set<String> conceptIds = Sets.newHashSetWithExpectedSize(results.size());
		
		final SnomedConceptConverterFunction exactConverter = new SnomedConceptConverterFunction(branchPath, queryExpression, false);
		for (SnomedConceptIndexEntry concept : results) {
			convertedItems.add(exactConverter.apply(concept));
			conceptIds.add(concept.getId());
		}
		
		final SnomedConceptConverterFunction approximateConverter = new SnomedConceptConverterFunction(branchPath, queryExpression, true);
		for (final SnomedConceptIndexEntry concept : approximateResults) {
			if (!conceptIds.contains(concept.getId())) {
				convertedItems.add(approximateConverter.apply(concept));
				conceptIds.add(concept.getId());
			}
		}
		
		return convertedItems;
	}

	private Query createExpressionQuery(IBranchPath branchPath, final String wrapper) {
		return ApplicationContext.getInstance().getService(IEscgQueryEvaluatorService.class).evaluateBooleanQuery(branchPath, wrapper);
	}
}
