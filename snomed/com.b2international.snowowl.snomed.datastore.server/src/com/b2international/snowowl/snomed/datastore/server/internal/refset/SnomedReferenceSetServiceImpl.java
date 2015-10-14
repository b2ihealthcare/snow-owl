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
package com.b2international.snowowl.snomed.datastore.server.internal.refset;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.b2international.snowowl.core.CoreTerminologyBroker;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.domain.IComponentInput;
import com.b2international.snowowl.core.domain.IComponentRef;
import com.b2international.snowowl.core.exceptions.AlreadyExistsException;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.datastore.server.components.AbstractComponentServiceImpl;
import com.b2international.snowowl.snomed.core.refset.SnomedReferenceSet;
import com.b2international.snowowl.snomed.core.refset.SnomedReferenceSetService;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.SnomedEditingContext;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetBrowser;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetEditingContext;
import com.b2international.snowowl.snomed.datastore.index.refset.SnomedRefSetIndexEntry;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSet;
import com.google.common.collect.ImmutableList;
import com.google.inject.Provider;

/**
 * @since 4.5
 */
public class SnomedReferenceSetServiceImpl extends AbstractComponentServiceImpl<IComponentInput, SnomedReferenceSet, IComponentInput, SnomedRefSetEditingContext, SnomedRefSet> implements SnomedReferenceSetService {

	private Provider<SnomedRefSetBrowser> refSetBrowser;

	public SnomedReferenceSetServiceImpl(Provider<SnomedRefSetBrowser> refSetBrowser) {
		super(SnomedDatastoreActivator.REPOSITORY_UUID, ComponentCategory.SET);
		this.refSetBrowser = refSetBrowser;
	}

	@Override
	public List<SnomedReferenceSet> getReferenceSets(String path) {
		final IBranchPath branchPath = createStorageRef("SNOMEDCT", path).getBranch().branchPath();
		final ImmutableList.Builder<SnomedReferenceSet> result = ImmutableList.builder();
		final Collection<SnomedRefSetIndexEntry> referenceSets = refSetBrowser.get().getAllReferenceSets(branchPath);
		for (SnomedRefSetIndexEntry entry : referenceSets) {
			final SnomedReferenceSetImpl refset = new SnomedReferenceSetImpl();
			refset.setId(entry.getId());
			refset.setEffectiveTime(new Date(entry.getEffectiveTimeAsLong()));
			// refset.setActive(entry.isActive()); TODO fix index entry
			// refset.setReleased(entry.isReleased()); TODO fix index entry
			refset.setModuleId(entry.getModuleId());
			refset.setReferencedComponent(CoreTerminologyBroker.getInstance().getComponentInformation(entry.getReferencedComponentType()).getName());
			refset.setType(entry.getType());
			result.add(refset);
		}
		return result.build();
	}

	@Override
	protected boolean componentExists(IComponentInput input) {
		throw new UnsupportedOperationException();
	}

	@Override
	protected boolean componentExists(IComponentRef ref) {
		throw new UnsupportedOperationException();
	}

	@Override
	protected AlreadyExistsException createDuplicateComponentException(IComponentInput input) {
		return null;
	}

	@Override
	protected SnomedRefSetEditingContext createEditingContext(IComponentRef ref) {
		return new SnomedEditingContext().getRefSetEditingContext();
	}

	@Override
	protected SnomedRefSet convertAndRegister(IComponentInput input, SnomedRefSetEditingContext editingContext) {
		throw new UnsupportedOperationException();
	}

	@Override
	protected String getComponentId(SnomedRefSet component) {
		return component.getIdentifierId();
	}

	@Override
	protected SnomedReferenceSet doRead(IComponentRef ref) {
		return null;
	}

	@Override
	protected void doUpdate(IComponentRef ref, IComponentInput update, SnomedRefSetEditingContext editingContext) {
		throw new UnsupportedOperationException();
	}

	@Override
	protected void doDelete(IComponentRef ref, SnomedRefSetEditingContext editingContext) {
		throw new UnsupportedOperationException();
	}

}
