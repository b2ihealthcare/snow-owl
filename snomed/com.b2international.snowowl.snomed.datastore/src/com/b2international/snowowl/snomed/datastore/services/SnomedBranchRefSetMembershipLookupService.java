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
package com.b2international.snowowl.snomed.datastore.services;

import static com.google.common.base.Preconditions.checkNotNull;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.IRefSetMembershipLookupService;
import com.b2international.snowowl.datastore.browser.AbstractClientStatementBrowser;
import com.b2international.snowowl.datastore.browser.AbstractClientTerminologyBrowser;
import com.b2international.snowowl.datastore.browser.BranchSpecificClientStatementBrowser;
import com.b2international.snowowl.datastore.browser.BranchSpecificClientTerminologyBrowser;
import com.b2international.snowowl.datastore.index.AbstractClientIndexService;
import com.b2international.snowowl.datastore.index.BranchSpecificClientIndexService;
import com.b2international.snowowl.snomed.datastore.SnomedStatementBrowser;
import com.b2international.snowowl.snomed.datastore.SnomedTerminologyBrowser;
import com.b2international.snowowl.snomed.datastore.index.SnomedIndexService;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;

/**
 * SNOMED CT terminology specific reference set membership lookup service implementation that uses client services wired for 
 * a particular branch.
 */
public class SnomedBranchRefSetMembershipLookupService 
	extends AbstractSnomedRefSetMembershipLookupService implements IRefSetMembershipLookupService<String> {

	private final AbstractClientIndexService<SnomedIndexEntry> indexService;
	private final AbstractClientTerminologyBrowser<SnomedConceptIndexEntry, String> terminologyBrowser;
	private final AbstractClientStatementBrowser<SnomedConceptIndexEntry, SnomedRelationshipIndexEntry, String> statementBrowser;

	private static SnomedIndexService getSnomedIndexService() {
		return ApplicationContext.getServiceForClass(SnomedIndexService.class);
	}

	private static SnomedTerminologyBrowser getSnomedTerminologyBrowser() {
		return ApplicationContext.getServiceForClass(SnomedTerminologyBrowser.class);
	}

	private static SnomedStatementBrowser getSnomedStatementBrowser() {
		return ApplicationContext.getServiceForClass(SnomedStatementBrowser.class);
	}

	/**
	 * Creates a new instance with the specified branch path
	 * @param branchPath the branch path to use (may not be {@code null})
	 */
	public SnomedBranchRefSetMembershipLookupService(final IBranchPath branchPath) {
		checkNotNull(branchPath, "Branch path to use may not be null.");
		indexService = new BranchSpecificClientIndexService<SnomedIndexEntry>(getSnomedIndexService(), branchPath);
		terminologyBrowser = new BranchSpecificClientTerminologyBrowser<SnomedConceptIndexEntry, String>(getSnomedTerminologyBrowser(), branchPath);
		statementBrowser = new BranchSpecificClientStatementBrowser<SnomedConceptIndexEntry, SnomedRelationshipIndexEntry, String>(getSnomedStatementBrowser(), branchPath);
	}

	@Override
	protected AbstractClientIndexService<SnomedIndexEntry> getIndexService() {
		return indexService;
	}

	@Override
	protected AbstractClientTerminologyBrowser<SnomedConceptIndexEntry, String> getTerminologyBrowser() {
		return terminologyBrowser;
	}

	@Override
	protected AbstractClientStatementBrowser<SnomedConceptIndexEntry, SnomedRelationshipIndexEntry, String> getStatementBrowser() {
		return statementBrowser;
	}
}