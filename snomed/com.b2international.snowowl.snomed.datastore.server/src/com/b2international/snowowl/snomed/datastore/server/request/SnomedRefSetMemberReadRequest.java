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
package com.b2international.snowowl.snomed.datastore.server.request;

import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.exceptions.ComponentNotFoundException;
import com.b2international.snowowl.snomed.core.domain.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetMemberLookupService;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;

/**
 * @since 4.5
 */
class SnomedRefSetMemberReadRequest extends SnomedRefSetMemberRequest<BranchContext, SnomedReferenceSetMember> {

	private final String componentId;

	SnomedRefSetMemberReadRequest(String id) {
		this.componentId = id;
	}
	
	@Override
	public SnomedReferenceSetMember execute(BranchContext context) {
		final SnomedRefSetMemberIndexEntry member = new SnomedRefSetMemberLookupService().getComponent(context.branch().branchPath(), componentId);
		if (member == null) {
			throw new ComponentNotFoundException("Reference Set Member", componentId);
		} else {
			return new SnomedReferenceSetMemberConverter().apply(member);
		}
	}
	
	@Override
	protected Class<SnomedReferenceSetMember> getReturnType() {
		return SnomedReferenceSetMember.class;
	}
	
}
