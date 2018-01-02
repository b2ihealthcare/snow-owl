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
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;

/**
 * Icon ID provider always returning the 'Defining relationship' relationship characteristic type concept ID.
 * @deprecated
 */
public class SnomedRelationshipIconIdProvider {

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.core.api.IComponentIconIdProvider#getIconId(com.b2international.snowowl.core.api.IBranchPoint, java.lang.Object)
	 */
	public String getIconId(IBranchPoint branchPoint, String componentId) {
		return Concepts.DEFINING_RELATIONSHIP;
	}
	
	/* (non-Javadoc)
	 * @see com.b2international.snowowl.core.api.IComponentIconIdProvider#getIconId(com.b2international.snowowl.core.api.IBranchPath, java.lang.Object)
	 */
	public String getIconId(IBranchPath branchPath, String componentId) {
		return Concepts.DEFINING_RELATIONSHIP;
	}

}