/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
import org.eclipse.emf.cdo.view.CDOView;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.exceptions.NotFoundException;
import com.b2international.snowowl.datastore.AbstractLookupService;
import com.b2international.snowowl.datastore.cdo.CDOUtils;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.core.domain.SnomedDescription;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSet;

/**
 * Lookup service implementation for SNOMED CT reference sets.
 * @deprecated - UNSUPPORTED API, only exist for compatibility reasons, use {@link SnomedRequests} where possible
 */
public final class SnomedRefSetLookupService extends AbstractLookupService<SnomedRefSet, CDOView> {

	@Override
	public SnomedConceptDocument getComponent(final IBranchPath branchPath, final String id) {
		try {
			return SnomedRequests.prepareGetConcept(id)
					.setExpand("referenceSet()")
					.build(SnomedDatastoreActivator.REPOSITORY_UUID, branchPath.getPath())
					.execute(ApplicationContext.getServiceForClass(IEventBus.class))
					.then(concept -> {
						if (concept.getReferenceSet() == null) {
							return null;
						}
						final SnomedDescription pt = concept.getPt();
						final String preferredTerm = pt == null ? concept.getId() : pt.getTerm();
						return SnomedConceptDocument.builder(concept)
								.label(preferredTerm)
								.build();
					})
					.getSync();
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
	public String getId(CDOObject component) {
		return ((SnomedRefSet) component).getIdentifierId();
	}
	
	@Override
	protected Class<SnomedRefSet> getType() {
		return SnomedRefSet.class;
	}
}
