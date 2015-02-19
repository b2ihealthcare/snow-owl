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

import org.eclipse.emf.ecore.EPackage;

import com.b2international.snowowl.core.annotations.Client;
import com.b2international.snowowl.datastore.ActiveBranchPathAwareService;
import com.b2international.snowowl.snomed.SnomedPackage;
import com.b2international.snowowl.snomed.datastore.derivation.SnomedRefSetDerivationModel;

/**
 * Client side SNOMED&nbsp;CT reference set derivation service implementation.
 * @since Snow&nbsp;Owl 3.0.1
 */
@Client
public class ClientSnomedRefSetDerivationService extends ActiveBranchPathAwareService implements IClientSnomedRefSetDerivationService {
	
	private final ISnomedRefSetDerivationService wrappedService;
	
	public ClientSnomedRefSetDerivationService(final ISnomedRefSetDerivationService wrappedSerice) {
		wrappedService = wrappedSerice;
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.snomed.datastore.services.IClientSnomedRefSetDerivationService#deriveSimpleTypeRefSet()
	 */
	@Override
	public boolean deriveSimpleTypeRefSet(final SnomedRefSetDerivationModel model) {
		return wrappedService.deriveSimpleTypeRefSet(getBranchPath(), model);
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.snomed.datastore.services.IClientSnomedRefSetDerivationService#deriveSimpleMapTypeRefSet()
	 */
	@Override
	public boolean deriveSimpleMapTypeRefSet(final SnomedRefSetDerivationModel model) {
		return wrappedService.deriveSimpleMapTypeRefSet(getBranchPath(), model);
	}

	@Override
	protected EPackage getEPackage() {
		return SnomedPackage.eINSTANCE;
	}
}