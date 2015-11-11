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

import org.eclipse.emf.ecore.EPackage;

import bak.pcj.map.LongKeyMap;

import com.b2international.snowowl.core.annotations.Client;
import com.b2international.snowowl.datastore.browser.AbstractClientStatementBrowser;
import com.b2international.snowowl.datastore.browser.ActiveBranchClientStatementBrowser;
import com.b2international.snowowl.snomed.SnomedPackage;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;

/**
 * Relationship hierarchy browser service for the SNOMED&nbsp;CT ontology.
 * @see AbstractClientStatementBrowser
 */
@Client
public class SnomedClientStatementBrowser extends ActiveBranchClientStatementBrowser<SnomedConceptIndexEntry, SnomedRelationshipIndexEntry, String> {

	/**
	 * Creates a new service instance for the client side
	 * @param delegateBrowser the server side service delegate.
	 */
	public SnomedClientStatementBrowser(final SnomedStatementBrowser delegateBrowser) {
		super(delegateBrowser);
	}

	/**
	 * Returns with all active IS_A relationships for a the currently active branch.
	 * @param mode the collection mode for the array (no IDs, relationship IDs, storage keys).
	 * @return an array of all active IS_A source relationships.
	 */
	public IsAStatement[] getAllActiveIsAStatement(final StatementCollectionMode mode) {
		return ((SnomedStatementBrowser) getDelegateBrowser()).getActiveStatements(getBranchPath(), mode);
	}

	/**
	 * Returns with the highest relationship group number from all the active source relationships of a concept specified by its ID.
	 * @param conceptId the unique ID of the SNOMED&nbsp;CT concept.
	 * @return the highest group number of the active source relationships of the concept.
	 */
	public int getHighestGroup(final long conceptId) {
		return ((SnomedStatementBrowser) getDelegateBrowser()).getHighestGroup(getBranchPath(), conceptId);
	}

	/**
	 * Returns with the highest relationship union group number from all the active source relationships of a concept specified by its ID.
	 * @param conceptId the unique ID of the SNOMED&nbsp;CT concept.
	 * @return the highest union group number of the active source relationships of the concept.
	 */
	public int getHighestUnionGroup(final long conceptId) {
		return ((SnomedStatementBrowser) getDelegateBrowser()).getHighestUnionGroup(getBranchPath(), conceptId);
	}

	/**
	 * Returns with an array of storage keys (CDO IDs) of all active source relationships of a concept specified by its unique ID
	 * from a particular relationship group.
	 * @param conceptId the unique SNOMED&nbsp;CT ID of a concept.
	 * @param group the relationship group.
	 * @return an array of active source relationship storage keys from a given group for a concept.
	 */
	public long[] getStatementStorageKeysForGroup(final long conceptId, final int group) {
		return ((SnomedStatementBrowser) getDelegateBrowser()).getStatementStorageKeysForGroup(getBranchPath(), conceptId, group);
	}

	/**
	 * Returns with a map of SNOMED CT relationships. Clients can make sure, that all the returning statements are active.
	 * <br>The values are the source concept IDs the values are the associated outbound {@link StatementFragment relationships}.
	 * @return a map of all active statements grouped by the source concept IDs.
	 * @see StatementFragment
	 */
	public LongKeyMap getAllActiveStatements() {
		return ((SnomedStatementBrowser)getDelegateBrowser()).getAllActiveStatements(getBranchPath());
	}

	/**
	 * Returns a collection of all active source relationships for the specified concept.
	 * @param conceptId the concept ID.
	 * @return a collection of active inbound statements for the specified concept
	 */
	public Collection<SnomedRelationshipIndexEntry> getActiveInboundStatementsById(final String conceptId) {
		return ((SnomedStatementBrowser) getDelegateBrowser()).getActiveInboundStatementsById(getBranchPath(), conceptId);
	}

	/**
	 * Returns a collection of all active destination relationships for the specified concept.
	 * @param conceptId the concept ID.
	 * @return a collection of active outbound statements for the specified concept
	 */
	public Collection<SnomedRelationshipIndexEntry> getActiveOutboundStatementsById(final String conceptId) {
		return ((SnomedStatementBrowser) getDelegateBrowser()).getActiveOutboundStatementsById(getBranchPath(), conceptId);
	}

	/**
	 * Returns a map of concept IDs and the associated preferred terms for the concepts. The concept IDs are a set of
	 * object, value and attribute concept IDs of all source and destination relationships of the concept identifier by
	 * the specified unique SNOMED&nbsp;CT ID.
	 * @param conceptId the concept ID.
	 * @return a map of concept IDs and concept preferred terms.
	 */
	public Map<String, String> getAllStatementLabelsById(final String conceptId) {
		return ((SnomedStatementBrowser) getDelegateBrowser()).getAllStatementLabelsById(getBranchPath(), conceptId);
	}

	/**
	 * Returns a map of concept IDs and the associated image concept IDs for the concepts. The concept IDs are a set of
	 * object, value and attribute concept IDs of all source and destination relationships of the concept identifier by
	 * the specified unique SNOMED&nbsp;CT ID.
	 * @param conceptId the concept ID.
	 * @return a map of concept IDs and concept image IDs.
	 */
	public Map<String, String> getAllStatementImageIdsById(final String conceptId) {
		return ((SnomedStatementBrowser) getDelegateBrowser()).getAllStatementImageIdsById(getBranchPath(), conceptId);
	}

	public Map<String, String> getAllDestinationLabels(final Collection<String> sourceIds, final String typeId) {
		return ((SnomedStatementBrowser) getDelegateBrowser()).getAllDestinationLabels(getBranchPath(), sourceIds, typeId);
	}

	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.BranchPathAwareService#getEPackage()
	 */
	@Override
	protected EPackage getEPackage() {
		return SnomedPackage.eINSTANCE;
	}
}