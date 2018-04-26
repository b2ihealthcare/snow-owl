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

import com.b2international.snowowl.api.impl.history.AbstractHistoryServiceImpl;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.snomed.api.ISnomedConceptHistoryService;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.datastore.SnomedConceptLookupService;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;

/**
 */
public class SnomedConceptHistoryServiceImpl extends AbstractHistoryServiceImpl implements ISnomedConceptHistoryService {

	public SnomedConceptHistoryServiceImpl() {
		super(SnomedDatastoreActivator.REPOSITORY_UUID, ComponentCategory.CONCEPT, SnomedTerminologyComponentConstants.CONCEPT_NUMBER);
	}

	@Override
	protected long getStorageKey(final IBranchPath branchPath, final String conceptId) {
		return new SnomedConceptLookupService().getStorageKey(branchPath, conceptId);
	}
	
}