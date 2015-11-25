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

import static com.google.common.base.Preconditions.checkNotNull;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.view.CDOQuery;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.ecore.EPackage;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.datastore.AbstractLookupService;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.cdo.CDOQueryUtils;
import com.b2international.snowowl.datastore.cdo.CDOUtils;
import com.b2international.snowowl.datastore.utils.ComponentUtils2;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.SnomedPackage;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptIndexEntry;
import com.google.common.collect.Iterables;

/**
 * Lookup service implementation for SNOMED CT concepts.
 */
public class SnomedConceptLookupService extends AbstractLookupService<String, Concept, CDOView> {

	@Override
	public Concept getComponent(final String conceptId, final CDOView view) {

		checkNotNull(conceptId, "SNOMED CT concept ID argument cannot be null.");
		CDOUtils.check(view);

		final long conceptStorageKey = getTerminologyBrowser().getStorageKey(BranchPathUtils.createPath(view), conceptId);
		CDOObject cdoObject = null;

		//make an other attempt to lookup concept in transaction with ID
		notFoundConceptLoop: 

			if (CDOUtils.NO_STORAGE_KEY == conceptStorageKey) {

				for (final Concept newConcept : ComponentUtils2.getNewObjects(view, Concept.class)) {
					if (conceptId.equals(newConcept.getId())) {
						cdoObject = newConcept;
						break notFoundConceptLoop;
					}
				}

				//if concept still not available, run a SQL query to see if it is available
				final CDOQuery query = view.createQuery("sql", SnomedTerminologyQueries.SQL_GET_CONCEPT_BY_ID);
				query.setParameter("conceptId", conceptId);
				return Iterables.getOnlyElement(CDOQueryUtils.getViewResult(query, Concept.class), null); 
			}

		if (null == cdoObject) { 
			cdoObject = CDOUtils.getObjectIfExists(view, conceptStorageKey);
		}

		if (null == cdoObject) {
			return null;
		}

		return (Concept) cdoObject;
	}

	@Override
	public SnomedConceptIndexEntry getComponent(final IBranchPath branchPath, final String conceptId) {
		final SnomedTerminologyBrowser terminologyBrowser = getTerminologyBrowser();
		return (null == terminologyBrowser) ? null : terminologyBrowser.getConcept(branchPath, conceptId);
	}

	@Override
	public long getStorageKey(final IBranchPath branchPath, final String id) {
		return getTerminologyBrowser().getStorageKey(branchPath, id);
	}

	private SnomedTerminologyBrowser getTerminologyBrowser() {
		return ApplicationContext.getInstance().getService(SnomedTerminologyBrowser.class);
	}
	
	@Override
	protected EPackage getEPackage() {
		return SnomedPackage.eINSTANCE;
	}
}
