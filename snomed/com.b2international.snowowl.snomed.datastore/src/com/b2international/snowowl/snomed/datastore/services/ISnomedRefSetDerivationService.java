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

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.snomed.datastore.derivation.SnomedRefSetDerivationModel;
import com.b2international.snowowl.snomed.datastore.derivation.SnomedRefSetDerivationModel.SimpleMapTypeDerivation;
import com.b2international.snowowl.snomed.datastore.derivation.SnomedRefSetDerivationModel.SimpleTypeDerivation;

/**
 * Interface for derivating SNOMED&nbsp;CT reference sets on the server side.
 * 
 * @since Snow&nbsp;Owl 3.0.1
 */
public interface ISnomedRefSetDerivationService {

	/**
	 * Derives simple type reference set to simple type reference sets. The derivation is based on the {@link SimpleTypeDerivation}.
	 * 
	 * @param branchPath the branch path.
	 * @param model the {@link SnomedRefSetDerivationModel} for the derivation process.
	 * @return <code>true</code> if the derivations was successful.
	 */
	public boolean deriveSimpleTypeRefSet(final IBranchPath branchPath, final SnomedRefSetDerivationModel model);
	
	/**
	 * Derives simple map type reference set to simple type reference sets. The derivation is based on the {@link SimpleMapTypeDerivation} 
	 * and the source of the reference set members (referenced component or map target).
	 * 
	 * @param branchPath
	 * @param model
	 * @return
	 */
	public boolean deriveSimpleMapTypeRefSet(final IBranchPath branchPath, final SnomedRefSetDerivationModel model);
}