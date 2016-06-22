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

import com.b2international.collections.longs.LongCollection;
import com.b2international.collections.longs.LongKeyLongMap;
import com.b2international.collections.longs.LongSet;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.browser.ITerminologyBrowser;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;

/**
 * Concept hierarchy browser service for the SNOMED&nbsp;CT ontology. 
 * @see ITerminologyBrowser
 */
public interface SnomedTerminologyBrowser extends ITerminologyBrowser<SnomedConceptDocument, String> {

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
	 * Returns {@code true} if the SNOMED&nbsp;CT concept exists with the given unique ID.
	 * @param branchPath the branch path.
	 * @param conceptId the unique ID of the concept.
	 * @return {@code true} if the component exists, otherwise returns with {@code false}.
	 */
	boolean exists(final IBranchPath branchPath, final String conceptId);
	
}