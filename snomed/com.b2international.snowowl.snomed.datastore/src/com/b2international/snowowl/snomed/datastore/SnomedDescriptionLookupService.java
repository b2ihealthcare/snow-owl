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
package com.b2international.snowowl.snomed.datastore;

import java.util.List;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.view.CDOQuery;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.ecore.EPackage;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.index.IIndexQueryAdapter;
import com.b2international.snowowl.datastore.AbstractLookupService;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.cdo.CDOQueryUtils;
import com.b2international.snowowl.datastore.cdo.CDOUtils;
import com.b2international.snowowl.datastore.utils.ComponentUtils2;
import com.b2international.snowowl.snomed.Description;
import com.b2international.snowowl.snomed.SnomedPackage;
import com.b2international.snowowl.snomed.datastore.index.SnomedDescriptionReducedQueryAdapter;
import com.b2international.snowowl.snomed.datastore.index.SnomedIndexService;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry;
import com.b2international.snowowl.snomed.datastore.services.ISnomedComponentService;
import com.google.common.collect.Iterables;

/**
 * Lookup service implementation for SNOMED CT descriptions. 
 */
public class SnomedDescriptionLookupService extends AbstractLookupService<String, Description, CDOView> {

	@Override
	public Description getComponent(final String descriptionId, final CDOView view) {

		final long descriptionStorageKey = getComponentService().getDescriptionStorageKey(BranchPathUtils.createPath(view), descriptionId);
		CDOObject cdoObject = null;

		notFoundDescriptionLoop: 

			if (CDOUtils.NO_STORAGE_KEY == descriptionStorageKey) {

				//look for new description in the transaction.
				for (final Description newDescription : ComponentUtils2.getNewObjects(view, Description.class)) {
					if (descriptionId.equals(newDescription.getId())) {
						cdoObject = newDescription;
						break notFoundDescriptionLoop;
					}
				}

				//check existence with SQL query (in case of detached components)
				final CDOQuery query = view.createQuery("sql", SnomedTerminologyQueries.SQL_GET_DESCRIPTION_BY_ID);
				query.setParameter("descriptionId", descriptionId);
				return Iterables.getOnlyElement(CDOQueryUtils.getViewResult(query, Description.class), null); 
			}

		if (null == cdoObject) {
			cdoObject = CDOUtils.getObjectIfExists(view, descriptionStorageKey);
		}


		if (null == cdoObject) {
			return null;
		}

		return (Description) cdoObject;
	}

	@Override
	public SnomedDescriptionIndexEntry getComponent(final IBranchPath branchPath, final String id) {
		final IIndexQueryAdapter<SnomedDescriptionIndexEntry> adapter = new SnomedDescriptionReducedQueryAdapter(id, SnomedDescriptionReducedQueryAdapter.SEARCH_DESCRIPTION_ID);
		final List<SnomedDescriptionIndexEntry> search = getIndexService().search(branchPath, adapter, 1);
		return Iterables.getOnlyElement(search, null);
	}

	@Override
	public long getStorageKey(final IBranchPath branchPath, final String id) {
		return getComponentService().getDescriptionStorageKey(branchPath, id);
	}

	private ISnomedComponentService getComponentService() {
		return ApplicationContext.getInstance().getService(ISnomedComponentService.class);
	}

	private SnomedIndexService getIndexService() {
		return ApplicationContext.getInstance().getService(SnomedIndexService.class);
	}
	
	@Override
	protected EPackage getEPackage() {
		return SnomedPackage.eINSTANCE;
	}
}
