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

import java.util.List;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.snomed.core.domain.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.core.domain.SnomedReferenceSetMembers;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetBrowser;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;

/**
 * @since 4.5
 */
public class SnomedRefSetMemberReadAllRequest extends SnomedRefSetMemberRequest<BranchContext, SnomedReferenceSetMembers> {

	private final int limit;
	private final int offset;

	SnomedRefSetMemberReadAllRequest() {
		this(0, 50);
	}
	
	SnomedRefSetMemberReadAllRequest(int offset, int limit) {
		this.limit = limit;
		this.offset = offset;
	}
	
	@Override
	public SnomedReferenceSetMembers execute(BranchContext context) {
		final IBranchPath branchPath = context.branch().branchPath();
		final SnomedRefSetBrowser browser = context.service(SnomedRefSetBrowser.class);
		final List<SnomedReferenceSetMember> members = FluentIterable
			.from(browser.getAllRefSetIds(branchPath))
			.transformAndConcat(new Function<String, Iterable<? extends SnomedRefSetMemberIndexEntry>>() {
				@Override
				public Iterable<? extends SnomedRefSetMemberIndexEntry> apply(String refSetId) {
					return browser.getMembers(branchPath, refSetId);
				}
			})
			.skip(offset)
			.limit(limit)
			.transform(new SnomedReferenceSetMemberConverter()).toList();
		
		return new SnomedReferenceSetMembers(members);
	}

	@Override
	protected Class<SnomedReferenceSetMembers> getReturnType() {
		return SnomedReferenceSetMembers.class;
	}

}
