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

import javax.annotation.Nullable;

import bak.pcj.map.LongKeyMap;
import bak.pcj.set.LongSet;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.browser.IClientRefSetBrowser;
import com.b2international.snowowl.core.api.browser.ITerminologyAndRefSetBrowser;
import com.b2international.snowowl.core.api.browser.ITerminologyBrowser;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;

/**
 * Service for browsing SNOMED&nbsp;CT concept and reference set hierarchy.
 * @see ITerminologyBrowser
 * @see IClientRefSetBrowser
 */
public interface SnomedRefSetBrowser extends ITerminologyAndRefSetBrowser<SnomedRefSetIndexEntry, SnomedConceptIndexEntry, String> {

	/**
	 * Returns with the reference set identifier concept IDs of all {@link SnomedRefSetType#SIMPLE simple} and 
	 * {@link SnomedRefSetType#ATTRIBUTE_VALUE attribute value} reference sets where the SNOMED&nbsp;CT concept 
	 * given by its unique ID is referenced by an active reference set member.
	 * <p>The following method will return with an empty collection if:
	 * <ul>
	 * <li>the concept ID is not a valid SNOMED&nbsp;CT concept ID,</li>
	 * <li>or the concept is referenced with a retired reference set member,</li>
	 * <li>or the referenced SNOMED&nbsp;CT concept is retired,</li>
	 * <li>or the container reference set is neither {@link SnomedRefSetType#SIMPLE simple} nor 
	 * {@link SnomedRefSetType#ATTRIBUTE_VALUE attribute value} reference set type.</li>
	 * </ul>
	 * @param branchPath the branch path.
	 * @param conceptId the unique ID of the SNOMED&nbsp;CT concept.
	 * @return a collection of container reference set IDs.
	 */
	Collection<String> getContainerRefSetIds(final IBranchPath branchPath, final String conceptId);
	
	/**
	 * Returns with the reference set identifier concept IDs of all {@link SnomedRefSetType#SIMPLE_MAP simple map} 
	 * reference sets where the SNOMED&nbsp;CT concept  given by its unique ID is referenced by an active reference set member.
	 * <p>The following method will return with an empty collection if:
	 * <ul>
	 * <li>the concept ID is not a valid SNOMED&nbsp;CT concept ID,</li>
	 * <li>or the concept is referenced with a retired reference set member,</li>
	 * <li>or the referenced SNOMED&nbsp;CT concept is retired,</li>
	 * <li>or the container reference set is *NOT* {@link SnomedRefSetType#SIMPLE_MAP simple map} reference set type.</li>
	 * </ul>
	 * @param branchPath the branch path.
	 * @param conceptId the unique ID of the SNOMED&nbsp;CT concept.
	 * @return a collection of container simple map reference set IDs.
	 */
	Collection<String> getContainerMappingRefSetIds(final IBranchPath branchPath, final String conceptId);
	
	/**
	 * Returns with the {@link SnomedRefSetType reference set type} ordinal of the reference set identified by its unique 
	 * reference set concept ID or {@code -1} if the reference set does not exist in store with the given ID.
	 * @param branchPath the branch path.
	 * @param refSetId the reference set identifier concept ID.
	 * @return the {@link SnomedRefSetType reference set type} ordinal.
	 */
	int getTypeOrdinal(final IBranchPath branchPath, final String refSetId);
	
	/**
	 * Returns with the unique storage key (CDO ID) of the SNOMED&nbsp;CT reference set member specified with it's unique UUID.
	 * This method will return with {@code -1} if no reference set member can be found on the specified branch with the given UUID. 
	 * @param branchPath the branch path.
	 * @param uuid the UUID of the reference set members.
	 * @return the storage key of the reference set member, or {@code -1} if reference set member does not exist.
	 */
	long getMemberStorageKey(final IBranchPath branchPath, final String uuid);
	
	/**
	 * Returns with the reference set concept identifier ID for a reference set specified by its unique storage key (CDO ID).
	 * <br>Returns with {@code null} if the reference set cannot be found.
	 * @param branchPath the branch path.
	 * @param storageKey the unique CDO ID of a reference set.
	 * @return
	 */
	@Nullable String getIdentifierId(final IBranchPath branchPath, final long storageKey);
	
	/**
	 * Returns {@code true} only and if only a SNOMED CT reference set, identified by it unique reference set identifier concept ID,
	 * has at least one active reference set member referencing to a SNOMED&nbsp;CT concept given by its unique concept ID. Otherwise,
	 * returns with {@code false}.
	 * @param branchPath the branch path.
	 * @param identifierConceptId the reference set identifier concept ID.
	 * @param conceptId the unique concept ID. 
	 * @return {@code true} if the reference set has at least one active member referencing to the specified concept, otherwise returns with {@code false}.
	 */
	boolean isActiveMemberOf(final IBranchPath branchPath, final long identifierConceptId, final long conceptId);
	
	/**
	 * Returns with the unique storage keys (CDO ID) of the published SNOMED&nbsp;CT module dependency reference set members specified
	 * with their module or referenced component ID.
	 * 
	 * @param branchPath 
	 * 		the branch path.
	 * @param id 
	 * 		the module or referenced component ID of the module dependency member. 
	 * @return 
	 * 		a collection of storage keys where the module dependency refset member is published and the module or referenced component
	 * 		ID equals to the ID in the parameter. 
	 */
	LongSet getPublishedModuleDependencyMembers(final IBranchPath branchPath, final String id);
	
	/**
	 * Returns with a collection of active mapping reference set members which are establish mapping between the given source and target and contained by the given reference set.
	 */
	Collection<SnomedRefSetMemberIndexEntry> getMappings(final IBranchPath branchPath, final String mappingRefSetId, final String sourceId, final String targetId);
	
	/**
	 * Returns with {@code true} if the given mapping reference set has at least one active member referencing from the given source to the given target.
	 * <br>Otherwise {@code false}.  
	 */
	boolean hasMapping(final IBranchPath branchPath, final String mappingRefSetId, final String sourceId, final String targetId);
	
	/**
	 * Returns with all reference sets.
	 * @param branchPath the branch path.
	 * @return a collection of all existing reference sets.
	 */
	Collection<SnomedRefSetIndexEntry> getAllReferenceSets(final IBranchPath branchPath);
	
	/**
	 * Returns with all active reference set members referring the given SNOMED&nbsp;CT concept.
	 * @param branchPath the branch path.
	 * @param conceptId the unique concept ID.
	 * @return a collection of active members referring to the given concept.
	 */
	Collection<SnomedRefSetMemberIndexEntry> getActiveReferringMembers(final IBranchPath branchPath, final String conceptId);

	/**
	 * Returns with all (including the inactive ones) reference set members referring the given SNOMED&nbsp;CT concept.
	 * @param branchPath the branch path.
	 * @param conceptId the unique concept ID.
	 * @return a collection of all members referring to the given concept.
	 */
	Collection<SnomedRefSetMemberIndexEntry> getReferringMembers(final IBranchPath branchPath, final String conceptId);
	
	/**
	 * Returns with all (including the inactive ones) reference set members of a given reference set.
	 * @param branchPath the branch path.
	 * @param referenceSetId the reference set identifier concept ID.
	 * @return a collection of all reference set members of a reference set.
	 */
	Collection<SnomedRefSetMemberIndexEntry> getMembers(final IBranchPath branchPath, final String referenceSetId);
	
	/**
	 * Returns with all active reference set members of a given reference set.
	 * @param branchPath the branch path.
	 * @param referenceSetId the reference set identifier concept ID.
	 * @return a collection of active reference set members of a reference set.
	 */
	Collection<SnomedRefSetMemberIndexEntry> getActiveMembers(final IBranchPath branchPath, final String referenceSetId);
	
	/**
	 * Returns with all mapping type reference set where the map source is the given concept.
	 * @param branchPath the branch path.
	 * @param conceptId the concept ID as the map source. (Referenced component.)
	 * @return a collection of active reference set member where the map source is the given concept. 
	 */
	Collection<SnomedRefSetMemberIndexEntry> getMappingMembers(final IBranchPath branchPath, final String conceptId);
	
	/**
	 * Returns with a map of active mappings from a give mapping type reference set. The keys are the map source component IDs
	 * and the values are a collection of map target component IDs.
	 * @param branchPath the branch path.
	 * @param refSetId the mapping type reference set ID.
	 * @return a map of mapping between sources and targets. Sources are unique component IDs and the values are a collection 
	 * of map target component IDs.
	 */
	Map<String, Collection<String>> getMapppings(final IBranchPath branchPath, final String refSetId);
	
	/**
	 * Returns with all active mapping type reference set members from a reference set where the map target is the given component.
	 * @param branchPath the branch path.
	 * @param mapTarget the map target component ID.
	 * @param mappingRefSetId the identifier concept ID of the mapping reference set.
	 * @return a collection of active reference set members from a particular reference set where the map target is the given component.
	 */
	Collection<SnomedRefSetMemberIndexEntry> getMembersForMapTarget(final IBranchPath branchPath, final String mapTarget, final String mappingRefSetId);
	
	/**
	 * Returns wit {@code true} only and if only the reference set exists on the given branch with the specified reference set storage key
	 * argument and the reference set is NOT a structural reference set but a regular one.
	 * @param branchPath the branch path.
	 * @param storageKey the storage key of the reference set.
	 * @return {@code true} if the reference set exists and a regular one. {@code false} if
	 * the reference set is either missing or a structural one.
	 */
	boolean isRegularRefSet(final IBranchPath branchPath, final long storageKey);
	
	/**
	 * Returns with a collection of ESCG queries associated with the query type reference set given with 
	 * its reference set identifier concept ID argument.
	 * <p>This method returns with an empty collection, if the reference set does not exist with the given ID on the specified 
	 * branch, or the reference set is not a query type or the reference set does not have any members.
	 * @param branchPath the branch path.
	 * @param refSetId the query type reference set ID.
	 * @return a collection of queries associated with the query type reference set.
	 */
	Collection<String> getAllQueries(final IBranchPath branchPath, final String refSetId);
	
	/**
	 * Returns with a map of reference set identifier concept IDs and a set of referenced 
	 * concept IDs for all regular simple, simple map and attribute value type reference sets.
	 * Basically a mapping that can fulfill an ESCG query with '&#94;' (<i>Caret</i>). 
	 * @param branchPath the branch path.
	 * @return a mapping between regular reference set IDs and the contained referenced concept IDs.
	 */
	LongKeyMap getReferencedConceptIds(final IBranchPath branchPath);
	
}