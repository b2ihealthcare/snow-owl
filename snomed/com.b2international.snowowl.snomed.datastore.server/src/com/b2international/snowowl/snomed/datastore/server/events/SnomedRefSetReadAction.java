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
package com.b2international.snowowl.snomed.datastore.server.events;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.snomed.core.refset.SnomedReferenceSet;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetBrowser;
import com.b2international.snowowl.snomed.datastore.index.refset.SnomedRefSetIndexEntry;

/**
 * @since 4.5
 */
class SnomedRefSetReadAction extends SnomedRefSetAction<SnomedReferenceSet> {

	private String referenceSetId;

	protected SnomedRefSetReadAction(String branch, String referenceSetId) {
		super(branch);
		this.referenceSetId = referenceSetId;
	}

	@Override
	public SnomedReferenceSet execute(RepositoryContext context) {
		final IBranchPath branch = context.branch();
		final SnomedRefSetBrowser browser = context.service(SnomedRefSetBrowser.class);
		final SnomedRefSetIndexEntry entry = browser.getRefSet(branch, referenceSetId);
		return new SnomedReferenceSetConverter().apply(entry);
	}

}
