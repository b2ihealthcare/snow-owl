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

import com.b2international.commons.ClassUtils;
import com.b2international.snowowl.core.CoreTerminologyBroker;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.domain.TerminologyAction;
import com.b2international.snowowl.core.domain.IComponentRef;
import com.b2international.snowowl.core.exceptions.AlreadyExistsException;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.datastore.server.components.AbstractComponentServiceImpl;
import com.b2international.snowowl.datastore.server.domain.InternalComponentRef;
import com.b2international.snowowl.snomed.core.domain.SnomedRefSetCreateAction;
import com.b2international.snowowl.snomed.core.domain.UserIdGenerationStrategy;
import com.b2international.snowowl.snomed.core.refset.SnomedReferenceSet;
import com.b2international.snowowl.snomed.core.refset.SnomedReferenceSetService;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.SnomedEditingContext;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetBrowser;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetLookupService;
import com.b2international.snowowl.snomed.datastore.index.refset.SnomedRefSetIndexEntry;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSet;
import com.google.common.collect.ImmutableList;
import com.google.inject.Provider;

/**
 * @since 4.5
 */
public class SnomedReferenceSetServiceImpl extends AbstractComponentServiceImpl<SnomedRefSetCreateAction, SnomedReferenceSet, TerminologyAction, SnomedEditingContext, SnomedRefSet> implements SnomedReferenceSetService {

	private final Provider<SnomedRefSetBrowser> refSetBrowser;
	private final Provider<SnomedRefSetLookupService> refSetLookupService;

	public SnomedReferenceSetServiceImpl(Provider<SnomedRefSetBrowser> refSetBrowser, Provider<SnomedRefSetLookupService> refSetLookupService) {
		super(SnomedDatastoreActivator.REPOSITORY_UUID, ComponentCategory.SET);
		this.refSetBrowser = refSetBrowser;
		this.refSetLookupService = refSetLookupService;
	}

	@Override
	public List<SnomedReferenceSet> getReferenceSets(String path) {
		final IBranchPath branchPath = createStorageRef("SNOMEDCT", path).getBranch().branchPath();
		final ImmutableList.Builder<SnomedReferenceSet> result = ImmutableList.builder();
		final Collection<SnomedRefSetIndexEntry> referenceSets = refSetBrowser.get().getAllReferenceSets(branchPath);
		for (SnomedRefSetIndexEntry entry : referenceSets) {
			result.add(convertToRepresentation(entry));
		}
		return result.build();
	}

	@Override
	protected boolean componentExists(SnomedRefSetCreateAction input) {
		if (input.getIdGenerationStrategy() instanceof UserIdGenerationStrategy) {
			final IBranchPath branchPath = createStorageRef("SNOMEDCT", input.getBranchPath()).getBranch().branchPath();
			return exists(branchPath, input.getIdGenerationStrategy().getId());
		} else {
			return false;
		}
	}

	@Override
	protected boolean componentExists(IComponentRef ref) {
		final InternalComponentRef internalRef = ClassUtils.checkAndCast(ref, InternalComponentRef.class);
		internalRef.checkStorageExists();
		return exists(internalRef.getBranch().branchPath(), internalRef.getComponentId());
	}

	private boolean exists(final IBranchPath branchPath, final String id) {
		return refSetLookupService.get().exists(branchPath, id);
	}

	@Override
	protected AlreadyExistsException createDuplicateComponentException(SnomedRefSetCreateAction input) {
		return null;
	}

	@Override
	protected SnomedEditingContext createEditingContext(IComponentRef ref) {
		return new SnomedEditingContext();
	}

	@Override
	protected String getComponentId(SnomedRefSet component) {
		return component.getIdentifierId();
	}

	@Override
	protected SnomedReferenceSet doRead(IComponentRef ref) {
		final InternalComponentRef internalRef = ClassUtils.checkAndCast(ref, InternalComponentRef.class);
		final SnomedRefSetIndexEntry entry = refSetBrowser.get().getRefSet(internalRef.getBranch().branchPath(), ref.getComponentId());
		return convertToRepresentation(entry);
	}

	@Override
	protected void doUpdate(IComponentRef ref, TerminologyAction update, SnomedEditingContext editingContext) {
		throw new UnsupportedOperationException();
	}

	@Override
	protected void doDelete(IComponentRef ref, SnomedEditingContext editingContext) {
		throw new UnsupportedOperationException();
	}
	
	private SnomedReferenceSet convertToRepresentation(SnomedRefSetIndexEntry entry) {
		final SnomedReferenceSetImpl refset = new SnomedReferenceSetImpl();
		refset.setId(entry.getId());
		refset.setEffectiveTime(new Date(entry.getEffectiveTimeAsLong()));
		refset.setActive(entry.isActive());
		refset.setReleased(entry.isReleased());
		refset.setModuleId(entry.getModuleId());
		refset.setReferencedComponent(CoreTerminologyBroker.getInstance().getComponentInformation(entry.getReferencedComponentType()).getName());
		refset.setType(entry.getType());
		return refset;
	}

}
