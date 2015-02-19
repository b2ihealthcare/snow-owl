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
package com.b2international.snowowl.datastore.server.snomed.index.init;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ReferenceManager;
import org.apache.lucene.search.TermQuery;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.view.CDOView;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.cdo.CDOUtils;
import com.b2international.snowowl.datastore.cdo.CDOViewFunction;
import com.b2international.snowowl.datastore.index.DocIdCollector;
import com.b2international.snowowl.datastore.index.DocIdCollector.DocIdsIterator;
import com.b2international.snowowl.datastore.index.IndexUtils;
import com.b2international.snowowl.datastore.server.index.IndexServerService;
import com.b2international.snowowl.datastore.server.snomed.index.init.ImportIndexServerService.IDescriptionTypePredicate;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.datastore.ILanguageConfigurationProvider;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetLookupService;
import com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants;
import com.b2international.snowowl.snomed.datastore.index.SnomedIndexService;
import com.b2international.snowowl.snomed.datastore.services.ISnomedComponentService;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSet;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.Sets;

/**
 * 
 */
public class IndexBasedImportIndexServiceFeeder implements IImportIndexServiceFeeder {

	private Supplier<String> languageRefSetIdSupplier;

	public IndexBasedImportIndexServiceFeeder(final String languageRefSetId, final IBranchPath branchPath) {
		this.languageRefSetIdSupplier = Suppliers.memoize(new Supplier<String>() {

			@Override public String get() {
				
				return CDOUtils.apply(new CDOViewFunction<String, CDOView>(SnomedDatastoreActivator.REPOSITORY_UUID, branchPath) {
					@Override protected String apply(final CDOView view) {
						final SnomedRefSet refSet = new SnomedRefSetLookupService().getComponent(languageRefSetId, view);
						return null == refSet ? getFallbackLanguageRefSetId(view) : languageRefSetId;
					}

					private String getFallbackLanguageRefSetId(final CDOView view) {
						return ApplicationContext.getInstance().getService(ILanguageConfigurationProvider.class).getLanguageConfiguration().getLanguageRefSetId(BranchPathUtils.createPath(view));
					}
				});
				
			}
		});
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.server.snomed.index.init.IImportIndexServiceFeeder#initContent(com.b2international.snowowl.datastore.server.snomed.index.init.ImportIndexServerService, com.b2international.snowowl.core.api.IBranchPath, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void initContent(final ImportIndexServerService service, final IBranchPath branchPath, final IProgressMonitor monitor) {

		final ISnomedComponentService componentService = ApplicationContext.getInstance().getServiceChecked(ISnomedComponentService.class);
		
		registerDescriptionProperties(service, branchPath, componentService);
		service.commit();
		
		registerAcceptability(service, branchPath);
		service.commit();
	}

	private static final Set<String> LANGUAGE_MEMBER_FIELDS_TO_LOAD = Collections.unmodifiableSet(Sets.newHashSet(
			SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_REFERENCED_COMPONENT_ID,
			SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_ACCEPTABILITY_ID,
			SnomedIndexBrowserConstants.COMPONENT_ACTIVE
			));
	
	@SuppressWarnings("unchecked")
	private void registerAcceptability(final ImportIndexServerService service, final IBranchPath branchPath) {
		
		@SuppressWarnings("rawtypes")
		final IndexServerService indexService = (IndexServerService) ApplicationContext.getInstance().getServiceChecked(SnomedIndexService.class);
		final Query memberQuery = new TermQuery(new Term(SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_REFERENCE_SET_ID, IndexUtils.longToPrefixCoded(languageRefSetIdSupplier.get())));
		
		ReferenceManager<IndexSearcher> manager = null;
		IndexSearcher searcher = null;
		
		try {
			
			manager = indexService.getManager(branchPath);
			searcher = manager.acquire();

			final int maxDoc = indexService.maxDoc(branchPath);
			final DocIdCollector collector = DocIdCollector.create(maxDoc);
			
			indexService.search(branchPath, memberQuery, collector);
			final DocIdsIterator itr = collector.getDocIDs().iterator();
			
			while (itr.next()) {
				
				final Document doc = searcher.doc(itr.getDocID(), LANGUAGE_MEMBER_FIELDS_TO_LOAD);
				
				service.registerConcept(
						doc.get(SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_REFERENCED_COMPONENT_ID),
						doc.get(SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_ACCEPTABILITY_ID),
						IndexUtils.getBooleanValue(doc.getField(SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_REFERENCED_COMPONENT_ID)));
			}

			
		} catch (final IOException e) {
			
			throw new SnowowlRuntimeException(e);
			
		} finally {
			
			if (null != manager && null != searcher) {
				
				try {
					
					manager.release(searcher);
					
				} catch (final IOException e) {
					
					throw new SnowowlRuntimeException(e);
					
				}
				
			}
			
		}
		
	}

	private void registerDescriptionProperties(final ImportIndexServerService service, final IBranchPath branchPath, final ISnomedComponentService componentService) {
		
		final String[][] descriptionProperties = componentService.getAllDescriptionProperties(branchPath);
		
		if (null == descriptionProperties) {
			return;
		}
		
		final Set<String> synonymAndDescendantIds = componentService.getSynonymAndDescendantIds(branchPath);
		
		for (final String[] property : descriptionProperties) {
			
			service.registerComponent(property[0], /* descriptionId */ CDOIDUtil.createLong(Long.parseLong(property[4] /*storage key*/)));
			
			// XXX: Assumes that nothing has been added to this import index service yet, as inactive descriptions are not populated at all.
			service.registerDescription(property[0], /* descriptionId */ 
					property[1], /* conceptId */
					property[3], /* term */
					new IDescriptionTypePredicate() { /* typeId */
						@Override public boolean isSynonymOrDescendant() { return synonymAndDescendantIds.contains(property[2]); }
						@Override public boolean isFsn() { return Concepts.FULLY_SPECIFIED_NAME.equals(property[2]); }
					}, 
					true);
		}
	}

}