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
package com.b2international.snowowl.snomed.refset.maprefsetderivation;
import org.eclipse.core.runtime.SubMonitor;

import com.b2international.snowowl.core.api.SnowowlServiceException;
import com.b2international.snowowl.snomed.refset.derivation.AbstractSnomedRefSetDerivator;

/**
 * This class is for deriving SNOMED CT simple map reference set to simple type reference set.
 * This implementation takes all the <b>active</b> SNOMED CT referenced components or maptargets
 * and use them as referenced component in the newly created simple type reference set.
 * 
 *
 */
public class SnomedSimpleMapRefSetDerivator extends AbstractSnomedRefSetDerivator {

	public SnomedSimpleMapRefSetDerivator(String refSetId, String newRefSetLabel, boolean mapTargetToReferencedComponent) throws SnowowlServiceException {
		super(refSetId, newRefSetLabel, mapTargetToReferencedComponent);
	}

	@Override
	protected int getTotalWork() {
		return 4;
	}

	@Override
	protected void deriveComponents(final SubMonitor monitor) throws SnowowlServiceException {
		deriveConcepts(monitor);
	}
}