/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.google.common.base.Preconditions.checkArgument;

import java.util.List;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.view.CDOQuery;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.ecore.EPackage;

import com.b2international.commons.CompareUtils;
import com.b2international.commons.StringUtils;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.exceptions.NotFoundException;
import com.b2international.snowowl.datastore.AbstractLookupService;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.cdo.CDOQueryUtils;
import com.b2international.snowowl.datastore.cdo.CDOUtils;
import com.b2international.snowowl.datastore.utils.ComponentUtils2;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.SnomedPackage;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSet;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSet;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

/**
 * Lookup service implementation for SNOMED CT reference sets.
 * @deprecated - UNSUPPORTED API, only exist for compatibility reasons, use {@link SnomedRequests} where possible
 */
public class SnomedRefSetLookupService extends AbstractLookupService<String, SnomedRefSet, CDOView> {

	/**
	 * Table names for all available reference sets, ordered by frequency of use (it is likely that most of the reference sets in
	 * a system will be simple, for example).
	 */
	private static final List<String> REFSET_TABLE_NAMES = ImmutableList.of("SNOMEDREFSET_SNOMEDREGULARREFSET",
			"SNOMEDREFSET_SNOMEDMAPPINGREFSET",
			"SNOMEDREFSET_SNOMEDCONCRETEDATATYPEREFSET",
			"SNOMEDREFSET_SNOMEDSTRUCTURALREFSET");

	@Override
	public SnomedRefSet getComponent(final String identifierConceptId, final CDOView view) {
		checkArgument(!StringUtils.isEmpty(identifierConceptId), "Identifier SNOMED CT concept ID cannot be null or empty.");

		CDOObject cdoObject = null;

		// try to get reference set from the underlying transaction first
		for (final SnomedRefSet newRefSet : ComponentUtils2.getNewObjects(view, SnomedRefSet.class)) {
			if (identifierConceptId.equals(newRefSet.getIdentifierId())) {
				cdoObject = newRefSet;
				break;
			}
		}
		
		if (cdoObject == null) {
			final long refSetStorageKey = getStorageKey(BranchPathUtils.createPath(view), identifierConceptId);
			
			if (CDOUtils.NO_STORAGE_KEY == refSetStorageKey) {
				for (final String tableName : REFSET_TABLE_NAMES) {
					
					final String sqlGetRefsetByIdentifierConceptId = String.format(SnomedTerminologyQueries.SQL_GET_REFSET_BY_IDENTIFIER_CONCEPT_ID, tableName);
					final CDOQuery cdoQuery = view.createQuery("sql", sqlGetRefsetByIdentifierConceptId);
					cdoQuery.setParameter("identifierConceptId", identifierConceptId);
					
					final List<SnomedRefSet> result = CDOQueryUtils.getViewResult(cdoQuery, SnomedRefSet.class);
					
					if (!CompareUtils.isEmpty(result)) {
						return Iterables.getOnlyElement(result);
					}
				}
				
				return null;
			}
			
			cdoObject = CDOUtils.getObjectIfExists(view, refSetStorageKey);
		}
		
		return (SnomedRefSet) cdoObject;
	}

	@Override
	public SnomedConceptDocument getComponent(final IBranchPath branchPath, final String id) {
		try {
			final IEventBus bus = ApplicationContext.getServiceForClass(IEventBus.class);
			final SnomedReferenceSet refSet = SnomedRequests
					.prepareGetReferenceSet(id)
					.build(SnomedDatastoreActivator.REPOSITORY_UUID, branchPath.getPath())
					.execute(bus)
					.getSync();
			
			final SnomedConceptDocument concept = new SnomedConceptLookupService().getComponent(branchPath, id);
			
			return SnomedConceptDocument
					.builder(concept)
					.refSet(refSet)
					.build();
		} catch (NotFoundException e) {
			return null;
		}
	}

	@Override
	public long getStorageKey(final IBranchPath branchPath, final String id) {
		final SnomedConceptDocument component = getComponent(branchPath, id);
		return component != null ? component.getRefSetStorageKey() : CDOUtils.NO_STORAGE_KEY;
	}

	@Override
	protected EPackage getEPackage() {
		return SnomedPackage.eINSTANCE;
	}
}
