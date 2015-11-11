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
import com.b2international.snowowl.core.events.BaseRequest;
import com.b2international.snowowl.core.exceptions.ComponentNotFoundException;
import com.b2international.snowowl.snomed.core.domain.SnomedReferenceSet;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetLookupService;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetIndexEntry;

/**
 * @since 4.5
 */
final class SnomedRefSetReadRequest extends BaseRequest<BranchContext, SnomedReferenceSet> {

	private final String referenceSetId;
	private final List<String> expansions;

	protected SnomedRefSetReadRequest(String referenceSetId, List<String> expansions) {
		this.referenceSetId = referenceSetId;
		this.expansions = expansions;
	}

	@Override
	public SnomedReferenceSet execute(BranchContext context) {
		final IBranchPath branch = context.branch().branchPath();
		final SnomedRefSetLookupService lookupService = new SnomedRefSetLookupService();
		final SnomedRefSetIndexEntry entry = lookupService.getComponent(branch, referenceSetId);
		if (entry == null) {
			throw new ComponentNotFoundException("Reference Set", referenceSetId);
		} else {
			return new SnomedReferenceSetConverter(context, expansions).apply(entry);
		}
	}
	
	@Override
	protected Class<SnomedReferenceSet> getReturnType() {
		return SnomedReferenceSet.class;
	}

}
