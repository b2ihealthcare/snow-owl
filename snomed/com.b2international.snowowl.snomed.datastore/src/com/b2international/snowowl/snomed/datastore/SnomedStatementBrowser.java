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
import java.util.Map;

import bak.pcj.map.LongKeyMap;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.browser.IStatementBrowser;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;

/**
 * Browser service for the SNOMED&nbsp;CT relationships.
 * @see IStatementBrowser
 */
public interface SnomedStatementBrowser extends IStatementBrowser<SnomedConceptIndexEntry, SnomedRelationshipIndexEntry, String> {

	/**
	 * Returns with all active relationships for a particular branch.
	 * @param branchPath the branch path.
	 * @param mode the collection mode for the array (no IDs, relationship IDs, storage keys).
	 * @return an array of all active relationships.
	 */
	<T extends IsAStatement> T[] getActiveStatements(final IBranchPath branchPath, final StatementCollectionMode mode);
	
	/**
	 * Returns with the highest relationship group number from all the active source relationships of a concept specified by its ID.
	 * @param branchPath the branch path.
	 * @param conceptId the unique ID of the SNOMED&nbsp;CT concept.
	 * @return the highest group number of the active source relationships of the concept.
	 */
	int getHighestGroup(final IBranchPath branchPath, final long conceptId);

	/**
	 * Returns with the highest relationship union group number from all the active source relationships of a concept specified by its ID.
	 * @param branchPath the branch path.
	 * @param conceptId the unique ID of the SNOMED&nbsp;CT concept.
	 * @return the highest union group number of the active source relationships of the concept.
	 */
	int getHighestUnionGroup(final IBranchPath branchPath, final long conceptId);

	/**
	 * Returns with an array of storage keys (CDO IDs) of all active source relationships of a concept specified by its unique ID
	 * from a particular relationship group.
	 * @param branchPath the branch path.
	 * @param conceptId the unique SNOMED&nbsp;CT ID of a concept.
	 * @param group the relationship group.
	 * @return an array of active source relationship storage keys from a given group for a concept.
	 */
	long[] getStatementStorageKeysForGroup(final IBranchPath branchPath, final long conceptId, final int group);

	/**
	 * Returns with an array of storage keys (CDO IDs) of all active source relationships of a concept specified by its unique ID
	 * from a particular relationship union group.
	 * @param branchPath the branch path.
	 * @param conceptId the unique SNOMED&nbsp;CT ID of a concept.
	 * @param unionGroup the relationship union group.
	 * @return an array of active source relationship storage keys from a given union group for a concept.
	 */
	long[] getStatementStorageKeysForUnionGroup(final IBranchPath branchPath, final long conceptId, final int unionGroup);

	/**
	 * Returns with a map of SNOMED CT relationships. Clients can make sure, that all the returning statements are active.
	 * <br>The values are the source concept IDs the values are the associated outbound {@link StatementFragment relationships}.
	 * @param branchPath the branch path.
	 * @return a map of all active statements grouped by the source concept IDs.
	 * @see StatementFragment
	 */
	LongKeyMap getAllActiveStatements(final IBranchPath branchPath);

	/**
	 * Returns the SNOMED CT relationship's source concept identifier for the specified statement storage key.
	 *
	 * @param branchPath the branch path
	 * @param statementStorageKey the storage key of the statement for which the source concept identifier should be returned
	 * @return the source concept ID of the specified relationship
	 */
	long getSourceIdForStatementStorageKey(final IBranchPath branchPath, final long statementStorageKey);

	/**
	 * Returns with the unique storage key (CDO ID) of the SNOMED&nbsp;CT relationship specified with it's unique ID.
	 * This method will return with {@code -1} if no relationship can be found on the specified branch with the given
	 * relationship ID.
	 * @param branchPath the branch path.
	 * @param relationshipId the unique ID of the relationship.
	 * @return the storage key of the relationship, or {@code -1} if the relationship does not exist.
	 */
	long getStorageKey(final IBranchPath branchPath, final String relationshipId);

	/**
	* Returns a collection of all active destination relationships for the specified concept.
	* @param branchPath the branch path reference limiting visibility to a particular branch.
	* @param conceptId the concept ID.
	* @return a collection of active inbound statements for the specified concept
	*/
	Collection<SnomedRelationshipIndexEntry> getActiveInboundStatementsById(final IBranchPath branchPath, final String conceptId);
	
	/**
	* Returns a collection of all destination relationships for the specified concept from a given relationship type.
	* @param branchPath the branch path reference limiting visibility to a particular branch.
	* @param conceptId the concept ID.
	* @param typeId the relationship type ID.
	* @return a collection of active inbound statements for the specified concept
	*/
	Collection<SnomedRelationshipIndexEntry> getInboundStatementsById(final IBranchPath branchPath, final long conceptId, final long typeId);

	/**
	* Returns a collection of all active source relationships for the specified concept.
	* @param branchPath the branch path reference limiting visibility to a particular branch.
	* @param conceptId the concept ID.
	* @return a collection of active outbound statements for the specified concept
	*/
	Collection<SnomedRelationshipIndexEntry> getActiveOutboundStatementsById(final IBranchPath branchPath, final String conceptId);

   /**
	* Returns a collection of all active source relationships for the specified concept and relationship type.
	* @param branchPath the branch path reference limiting visibility to a particular branch.
	* @param conceptId the concept ID.
	* @param relationshipTypeId the id of the type of the relationship
	* @return a collection of active outbound statements for the specified concept
	*/
	Collection<SnomedRelationshipIndexEntry> getActiveOutboundStatementsById(final IBranchPath branchPath, final String id, final String relationshipTypeId);
	
	/**
	* Returns a map of concept IDs and the associated preferred terms for the concepts. The concept IDs are a set of
	* object, value and attribute concept IDs of all source and destination relationships of the concept identifier by
	* the specified unique SNOMED&nbsp;CT ID.
	* @param branchPath the branch path reference limiting visibility to a particular branch.
	* @param conceptId the concept ID.
	* @return a map of concept IDs and concept preferred terms.
	*/
	Map<String, String> getAllStatementLabelsById(final IBranchPath branchPath, final String conceptId);

	/**
	* Returns a map of concept IDs and the associated image concept IDs for the concepts. The concept IDs are a set of
	* object, value and attribute concept IDs of all source and destination relationships of the concept identifier by
	* the specified unique SNOMED&nbsp;CT ID.
	* @param conceptId the concept ID.
	* @return a map of concept IDs and concept image IDs.
	*/
	Map<String, String> getAllStatementImageIdsById(final IBranchPath branchPath, final String conceptId);

	Map<String, String> getAllDestinationLabels(IBranchPath branchPath, Collection<String> sourceIds, String typeId);

}
