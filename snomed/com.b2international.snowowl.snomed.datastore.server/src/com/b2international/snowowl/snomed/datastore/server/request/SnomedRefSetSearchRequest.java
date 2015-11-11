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

import java.util.Collection;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.snomed.core.domain.SnomedReferenceSet;
import com.b2international.snowowl.snomed.core.domain.SnomedReferenceSets;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetBrowser;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetIndexEntry;
import com.google.common.collect.ImmutableList;

/**
 * @since 4.5
 */
final class SnomedRefSetSearchRequest extends SearchRequest<SnomedReferenceSets> {

	@Override
	public SnomedReferenceSets execute(BranchContext context) {
		final IBranchPath branchPath = context.branch().branchPath();
		final SnomedRefSetBrowser browser = context.service(SnomedRefSetBrowser.class);

		final ImmutableList.Builder<SnomedReferenceSet> result = ImmutableList.builder();

		final Collection<SnomedRefSetIndexEntry> referenceSets = browser.getAllReferenceSets(branchPath);
		final SnomedReferenceSetConverter converter = new SnomedReferenceSetConverter(context);
		for (SnomedRefSetIndexEntry entry : referenceSets) {
			result.add(converter.apply(entry));
		}

		return new SnomedReferenceSets(result.build());
	}
	
	@Override
	protected Class<SnomedReferenceSets> getReturnType() {
		return SnomedReferenceSets.class;
	}

}
