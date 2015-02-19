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

import com.b2international.snowowl.snomed.datastore.derivation.SnomedRefSetDerivationModel;

/**
 * Interface for derivating SNOMED&nbsp;CT reference sets on the client side.
 * 
 * @since Snow&nbsp;Owl 3.0.1
 */
public interface IClientSnomedRefSetDerivationService {

	/**
	 * Derives simple type reference set to simple type reference set based on the derivation model.
	 * 
	 * @param model the model for the derivation.
	 * @return
	 */
	public boolean deriveSimpleTypeRefSet(final SnomedRefSetDerivationModel model);

	/**
	 * Derives simple map type reference set to simple type reference set based on the derivation model.
	 * 
	 * @param model the model for the derivation.
	 * @return
	 */
	public boolean deriveSimpleMapTypeRefSet(final SnomedRefSetDerivationModel model);
}