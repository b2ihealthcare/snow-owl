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
import com.b2international.snowowl.snomed.Relationship;
import com.b2international.snowowl.snomed.SnomedPackage;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;
import com.google.common.collect.Iterables;

/**
 * Lookup service implementation for SNOMED CT relationships. 
 */
public class SnomedRelationshipLookupService extends AbstractLookupService<String, Relationship, CDOView> {

	@Override
	public Relationship getComponent(final String relationshipId, final CDOView view) {

		final long relationshipStorageKey = getStatementBrowser().getStorageKey(BranchPathUtils.createPath(view), relationshipId);
		CDOObject cdoObject = null;

		notFoundRelationshipLoop: 

			if (CDOUtils.NO_STORAGE_KEY == relationshipStorageKey) {

				//get new relationship from the underlying audit CDO view by its unique SNOMED CT ID
				for (final Relationship newRelationship : ComponentUtils2.getNewObjects(view, Relationship.class)) {
					if (relationshipId.equals(newRelationship.getId())) {
						cdoObject = newRelationship;
						break notFoundRelationshipLoop;
					}
				}

				//check relationship existence with SQL query (in case of detached components for historical views)
				final CDOQuery query = view.createQuery("sql", SnomedTerminologyQueries.SQL_GET_RELATIONSHIP_BY_ID);
				query.setParameter("relationshipId", relationshipId);
				return Iterables.getOnlyElement(CDOQueryUtils.getViewResult(query, Relationship.class), null);
			}

		if (null == cdoObject) {
			cdoObject = CDOUtils.getObjectIfExists(view, relationshipStorageKey);
		}

		if (null == cdoObject) {
			return null;
		}

		return (Relationship) cdoObject;
	}

	@Override
	public SnomedRelationshipIndexEntry getComponent(final IBranchPath branchPath, final String id) {
		return getStatementBrowser().getStatement(branchPath, id);
	}

	@Override
	public long getStorageKey(final IBranchPath branchPath, final String id) {
		return getStatementBrowser().getStorageKey(branchPath, id);
	}

	private SnomedStatementBrowser getStatementBrowser() {
		return ApplicationContext.getInstance().getService(SnomedStatementBrowser.class);
	}
	
	@Override
	protected EPackage getEPackage() {
		return SnomedPackage.eINSTANCE;
	}
}
