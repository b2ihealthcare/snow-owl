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
package com.b2international.snowowl.snomed.datastore.filteredrefset;

import java.io.Serializable;

import com.b2international.collections.longs.LongCollection;
import com.b2international.snowowl.snomed.datastore.index.SnomedHierarchy;

/**
 * @deprecated - unsupported, will be removed in 4.7
 */
public class RemoveRefSetMemberOperation extends AbstractRefSetMemberOperation implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public RemoveRefSetMemberOperation(final long targetConceptId, final boolean includesSubtypes) {
		super(targetConceptId, includesSubtypes);
	}

	@Override
	public void apply(final LongCollection candidateIds, final LongCollection collectedIds, final SnomedHierarchy hierarchy) {
			
		collectedIds.remove(targetConceptId);
		
		if (includesSubtypes) {
			collectedIds.removeAll(hierarchy.getSubTypeIds(targetConceptId));
		}
	}
	
	@Override
	public boolean isAddition() {
		return false;
	}
}