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
package com.b2international.snowowl.snomed.api.impl;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.datastore.request.RepositoryRequests;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.api.ISnomedExportService;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.id.SnomedIdentifiers;
import com.google.common.base.Strings;

/**
 * {@link ISnomedExportService export service} implementation for the SNOMED CT ontology.
 * @deprecated
 */
public class SnomedExportService implements ISnomedExportService {

	@Override
	public String resolveNamespaceId(Branch branch) {
		
		String branchMetaShortname = getEffectiveBranchMetadataValue(branch, "shortname");
		String branchMetaDefaultNamespace = getEffectiveBranchMetadataValue(branch, "defaultNamespace");
		
		if (!Strings.isNullOrEmpty(branchMetaShortname) && !Strings.isNullOrEmpty(branchMetaDefaultNamespace)) {
			return String.format("%s%s", branchMetaShortname.toUpperCase(), branchMetaDefaultNamespace);
		}
		
		return SnomedIdentifiers.INT_NAMESPACE;
	}
	
	// FIXME This should not be here, see IHTSDO/com.b2international.snowowl.snomed.core.domain.BranchMetadataResolver
	private String getEffectiveBranchMetadataValue(Branch branch, String metadataKey) {
		final String metadataValue = branch.metadata().getString(metadataKey);
		if (metadataValue != null) {
			return metadataValue;
		} else {
			if (!Branch.MAIN_PATH.equals(branch.parentPath())) {
				final Branch parent = RepositoryRequests.branching()
					.prepareGet(branch.parentPath())
					.build(SnomedDatastoreActivator.REPOSITORY_UUID)
					.execute(ApplicationContext.getServiceForClass(IEventBus.class))
					.getSync();
				return getEffectiveBranchMetadataValue(parent, metadataKey);
			}
		}
		return null;
	}
	
}
