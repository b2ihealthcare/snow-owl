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
import com.b2international.snowowl.datastore.AbstractLookupService;
import com.b2international.snowowl.datastore.cdo.CDOUtils;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMembers;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;

/**
 * Lookup service implementation for SNOMED CT reference set members.
 * @deprecated - UNSUPPORTED API, only exist for compatibility reasons, use {@link SnomedRequests} where possible
 */
public final class SnomedRefSetMemberLookupService extends AbstractLookupService<SnomedRefSetMember, CDOView> {

	@Override
	public SnomedRefSetMemberIndexEntry getComponent(final IBranchPath branchPath, final String uuid) {
		return SnomedRequests.prepareSearchMember()
				.setLimit(2)
				.filterById(uuid)
				.build(SnomedDatastoreActivator.REPOSITORY_UUID, branchPath.getPath())
				.execute(ApplicationContext.getServiceForClass(IEventBus.class))
				.then(new Function<SnomedReferenceSetMembers, SnomedRefSetMemberIndexEntry>() {
					@Override
					public SnomedRefSetMemberIndexEntry apply(SnomedReferenceSetMembers input) {
						final SnomedReferenceSetMember member = Iterables.getOnlyElement(input, null);
						return member == null ? null : SnomedRefSetMemberIndexEntry.builder(member).build();
					}
				})
				.getSync();
	}

	@Override
	public long getStorageKey(final IBranchPath branchPath, final String id) {
		final SnomedRefSetMemberIndexEntry component = getComponent(branchPath, id);
		return component != null ? component.getStorageKey() : CDOUtils.NO_STORAGE_KEY;
	}

	@Override
	public String getId(CDOObject component) {
		return ((SnomedRefSetMember) component).getUuid();
	}
	
	@Override
	protected Class<SnomedRefSetMember> getType() {
		return SnomedRefSetMember.class;
	}
}
