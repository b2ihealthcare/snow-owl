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
package com.b2international.snowowl.snomed.datastore;

import static com.google.common.base.Preconditions.checkNotNull;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.IComponentNameProvider;
import com.b2international.snowowl.snomed.datastore.services.SnomedConceptNameProvider;

/**
 * Default label provider implementation for SNOMED&nbsp;CT concepts.
 * <p>This implementation simply delegates into the {@link SnomedConceptNameProvider}.
 *
 */
public class SnomedConceptLabelProviderServiceImpl implements SnomedConceptLabelProviderService {

	@Override
	public String getLabel(final IBranchPath branchPath, final String conceptId) {
		final IComponentNameProvider nameProvider = SnomedConceptNameProvider.INSTANCE;
		if (conceptId != null) {
			return nameProvider.getComponentLabel(checkNotNull(branchPath, "branchPath"), checkNotNull(conceptId, "conceptId"));
		}
		return "N/A";
	}

}