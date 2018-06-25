/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
import com.b2international.snowowl.core.api.IComponent;
import com.b2international.snowowl.core.exceptions.NotFoundException;
import com.b2international.snowowl.datastore.AbstractLookupService;
import com.b2international.snowowl.datastore.cdo.CDOUtils;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.mrcm.AttributeConstraint;

/**
 * Lookup service implementation for SNOMED CT MRCM attribute constraints.
 */
public final class SnomedConstraintLookupService extends AbstractLookupService<AttributeConstraint, CDOView> {

	@Override
	public IComponent<String> getComponent(final IBranchPath branchPath, final String constraintId) {
		throw new UnsupportedOperationException("IComponent<String> lookup for attribute constraints is not supported.");
	}

	@Override
	public long getStorageKey(final IBranchPath branchPath, final String id) {
		try {
			return SnomedRequests.prepareGetConstraint(id)
					.build(SnomedDatastoreActivator.REPOSITORY_UUID, branchPath.getPath())
					.execute(ApplicationContext.getServiceForClass(IEventBus.class))
					.then(component -> component.getStorageKey())
					.getSync();
		} catch (NotFoundException e) {
			return CDOUtils.NO_STORAGE_KEY;
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	protected Class<AttributeConstraint> getType() {
		return AttributeConstraint.class;
	}

	@Override
	public String getId(CDOObject component) {
		return ((AttributeConstraint) component).getUuid();
	}
}
