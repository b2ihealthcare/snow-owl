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

import java.util.Set;

import com.b2international.collections.longs.LongSet;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.snomed.snomedrefset.DataType;

/**
 * Interface for retrieving information about SNOMED&nbsp;CT core components on the server side.
 * 
 */
public interface ISnomedComponentService {
	
	/**
	 * Returns with the available concrete domain data type labels for a specified concrete domain data type.
	 * @param branchPath
	 * @param dataType the data type. E.g.: {@code BOOLEAN} or {@code DECIMAL}.
	 * @return a set of concrete domain data type labels for a specified data type.
	 */
	Set<String> getAvailableDataTypeLabels(IBranchPath branchPath, final DataType dataType);

	/**
	 * Returns with a collection of the reference set member's referenced component storage keys.  
	 * 
	 * @param branchPath the branch path.
	 * @param refSetId the Id of the reference set.
	 * @param referencedComponentType the type of the reference set member's referenced component.
	 * @return a collection of component storage keys.
	 */
	LongSet getComponentByRefSetIdAndReferencedComponent(final IBranchPath branchPath, final String refSetId, final short referencedComponentType);
	
	/**
	 * Returns with a set of concept storage keys that have to be inactivated when retiring the concept concepts
	 * argument.
	 * @param branchPath the branch path for the calculation.
	 * @param focusConceptIds the concept IDs to retire with their descendants.
	 * @return a collection of concept storage keys to inactivate.
	 */
	LongSet getSelfAndAllSubtypeStorageKeysForInactivation(final IBranchPath branchPath, final String... focusConceptIds);
	
}