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
package com.b2international.snowowl.snomed.datastore.validation;

import org.eclipse.emf.ecore.EPackage;

import com.b2international.snowowl.core.annotations.Client;
import com.b2international.snowowl.datastore.validation.AbstractComponentValidationClientService;
import com.b2international.snowowl.snomed.SnomedPackage;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptIndexEntry;

/**
 * Client side SNOMED&nbsp;CT component validation service implementation.
 * 
 */
@Client
public class SnomedComponentValidationClientService extends AbstractComponentValidationClientService<SnomedConceptIndexEntry> implements IClientSnomedComponentValidationService {

	public SnomedComponentValidationClientService(final ISnomedComponentValidationService wrappedService) {
		super(wrappedService);
	}

	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.BranchPathAwareService#getEPackage()
	 */
	@Override
	protected EPackage getEPackage() {
		return SnomedPackage.eINSTANCE;
	}
}