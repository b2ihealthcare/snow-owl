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

import java.util.Collection;

import com.b2international.collections.longs.LongCollection;
import com.b2international.collections.longs.LongKeyLongMap;
import com.b2international.collections.longs.LongSet;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.IComponentWithChildFlag;
import com.b2international.snowowl.core.api.browser.ITerminologyBrowser;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;

/**
 * Concept hierarchy browser service for the SNOMED&nbsp;CT ontology. 
 * @see ITerminologyBrowser
 */
public interface SnomedTerminologyBrowser extends ITerminologyBrowser<SnomedConceptDocument, String> {

	/**
	 * Returns with a set of the active direct descendant concept IDs of a SNOMED&nbsp;CT concept given by its unique ID.
	 * @param branchPath the branch path.
	 * @param conceptId the unique ID of the concept.
	 * @return a set of concept IDs of the active direct descendant concepts.
	 */
	LongSet getSubTypeIds(final IBranchPath branchPath, final long conceptId);

	/**
	 * Returns with a set of all active descendant concept IDs of a SNOMED&nbsp;CT concept given by its unique ID.
	 * @param branchPath the branch path.
	 * @param conceptId the unique ID of the concept.
	 * @return a set of concept IDs of all active descendant concepts.
	 */
	LongSet getAllSubTypeIds(final IBranchPath branchPath, final long conceptId);
	
	/**
	 * Returns with a set of the active direct ancestor concept IDs of a SNOMED&nbsp;CT concept given by its unique ID.
	 * @param branchPath the branch path.
	 * @param conceptId the unique ID of the concept.
	 * @return a set of concept IDs of the active direct ancestor concepts.
	 */
	LongSet getSuperTypeIds(final IBranchPath branchPath, final long conceptId);

	/**
	 * Returns with a set of all active ancestor concept IDs of a SNOMED&nbsp;CT concept given by its unique ID.
	 * @param branchPath the branch path.
	 * @param conceptId the unique ID of the concept.
	 * @return a set of concept IDs of all active ancestor concepts.
	 */
	LongSet getAllSuperTypeIds(final IBranchPath branchPath, final long conceptId);
	
	/**
	 * Returns with a 2D array of IDs and storage keys of all the active SNOMED&nbsp;CT concepts from the ontology.
	 * <p>The first dimension is the concept ID the second dimension is the storage key (CDO ID).
	 * @param branchPath the branch path.
	 * @return a 2D array of concept IDs and storage keys.
	 */
	long[][] getAllActiveConceptIdsStorageKeys(final IBranchPath branchPath);
	
	/**
	 * Returns with a 2D array of IDs and storage keys of all SNOMED&nbsp;CT concepts from the ontology.
	 * <p>The first dimension is the concept ID the second dimension is the storage key (CDO ID).
	 * @param branchPath the branch path.
	 * @return a 2D array of concept IDs and storage keys.
	 */
	long[][] getAllConceptIdsStorageKeys(final IBranchPath branchPath);
	
	/**
	 * Maps all active SNOMED&nbsp;CT concept identifiers to their corresponding storage keys in the ontology.
	 * <p>Map keys are concept IDs, values are concept storage keys (CDO ID).
	 * @param branchPath the branch path.
	 * @return a map of concept IDs and storage keys.
	 */
	LongKeyLongMap getConceptIdToStorageKeyMap(final IBranchPath branchPath);
	
	/**
	 * Returns all active SNOMED&nbsp;CT concept identifiers from the ontology.
	 * @param branchPath the branch path.
	 * @return a collection of concept IDs for all active concepts.;
	 */
	LongCollection getAllActiveConceptIds(final IBranchPath branchPath);

	/**
	 * Returns all  SNOMED&nbsp;CT concept identifiers from the ontology. 
	 * The result set contains retired concept IDs as well.
	 * @param branchPath the branch path.
	 * @return a collection of concept IDs for all concepts including the retired ones as well.;
	 */
	LongCollection getAllConceptIds(final IBranchPath branchPath);
	
	/**
	 * Returns {@code true} only and if only the specified SNOMED&nbsp;CT <b>core</b> component ID does not
	 * exist in the store. Otherwise it returns with {@code false}.
	 * <p><b>NOTE:&nbsp;</b>this method is not aware of checking reference set and reference set members IDs.
	 * In case of checking *NON* core component IDs, this method returns {@code false}.
	 * @param branchPath the branch path.
	 * @param componentId the SNOMED&nbsp;CT core component ID to check.
	 * @return {@code true} if the ID is unique, otherwise returns with {@code false}.
	 */
	boolean isUniqueId(final IBranchPath branchPath, final String componentId);
	
	/**
	 * Returns {@code true} if the SNOMED&nbsp;CT concept exists with the given unique ID.
	 * @param branchPath the branch path.
	 * @param conceptId the unique ID of the concept.
	 * @return {@code true} if the component exists, otherwise returns with {@code false}.
	 */
	boolean exists(final IBranchPath branchPath, final String conceptId);
	
	/**
	 * Returns with the unique storage key (CDO ID) of the SNOMED&nbsp;CT concept specified with it's unique ID.
	 * This method will return with {@code -1} if no concept can be found on the specified branch with the given
	 * concept ID. 
	 * @param branchPath the branch path.
	 * @param conceptId the unique ID of the concept.
	 * @return the storage key of the concept, or {@code -1} if the concept does not exist.
	 */
	long getStorageKey(final IBranchPath branchPath, final String conceptId);
	
	/**
	 * Returns the sub types of the specified concept, with an additional boolean flag to indicate whether the concept has children or not.
	 * 
	 * @param branchPath the branch path
	 * @param concept the concept
	 * @return the sub types with additional child flag
	 * @deprecated - unused, will be removed in 4.6
	 */
	public Collection<IComponentWithChildFlag<String>> getSubTypesWithChildFlag(final IBranchPath branchPath, final SnomedConceptDocument concept);
	
}