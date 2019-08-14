/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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

import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.snomed.api.ISnomedExportService;
import com.b2international.snowowl.snomed.cis.SnomedIdentifiers;
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
			final Branch parent = branch.parent();
			if (parent != null && branch != parent) {
				return getEffectiveBranchMetadataValue(parent, metadataKey);
			}
		}
		return null;
	}
	
}
