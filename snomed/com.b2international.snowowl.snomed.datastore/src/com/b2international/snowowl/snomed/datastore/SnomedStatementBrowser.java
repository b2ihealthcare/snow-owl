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

/**
 * Browser service for the SNOMED&nbsp;CT relationships.
 */
public interface SnomedStatementBrowser {

	/**
	 * Returns with all active relationships for a particular branch.
	 * @param branchPath the branch path.
	 * @param mode the collection mode for the array (no IDs, relationship IDs, storage keys).
	 * @return an array of all active relationships.
	 */
	<T extends IsAStatement> T[] getActiveStatements(final IBranchPath branchPath, final StatementCollectionMode mode);
	
	/**
	 * Returns with the unique storage key (CDO ID) of the SNOMED&nbsp;CT relationship specified with it's unique ID.
	 * This method will return with {@code -1} if no relationship can be found on the specified branch with the given
	 * relationship ID.
	 * @param branchPath the branch path.
	 * @param relationshipId the unique ID of the relationship.
	 * @return the storage key of the relationship, or {@code -1} if the relationship does not exist.
	 */
	long getStorageKey(final IBranchPath branchPath, final String relationshipId);

}
