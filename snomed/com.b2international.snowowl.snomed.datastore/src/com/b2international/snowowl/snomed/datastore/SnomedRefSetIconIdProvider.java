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

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.IBranchPoint;
import com.google.common.base.Preconditions;

/**
 * Icon ID provider for SNOMED&nbsp;CT reference sets.
 * <br>The implementation always falls back to the {@link SnomedConceptIconIdProvider}.
 * @deprecated
 */
public class SnomedRefSetIconIdProvider {

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.core.api.IComponentIconIdProvider#getIconId(com.b2international.snowowl.core.api.IBranchPoint, java.lang.Object)
	 */
	public String getIconId(final IBranchPoint branchPoint, final String componentId) {
		
		Preconditions.checkNotNull(branchPoint, "Branch point argument cannot be null.");
		Preconditions.checkNotNull(componentId, "SNOMED CT reference set identifier concept ID argument cannot be null.");
		
		return new SnomedConceptIconIdProvider().getIconId(branchPoint, componentId);
	}
	
	/* (non-Javadoc)
	 * @see com.b2international.snowowl.core.api.IComponentIconIdProvider#getIconId(com.b2international.snowowl.core.api.IBranchPath, java.lang.Object)
	 */
	public String getIconId(final IBranchPath branchPath, final String componentId) {
		Preconditions.checkNotNull(branchPath, "Branch path argument cannot be null.");
		Preconditions.checkNotNull(componentId, "SNOMED CT reference set identifier concept ID argument cannot be null.");
		return new SnomedConceptIconIdProvider().getIconId(branchPath, componentId);
	}

}