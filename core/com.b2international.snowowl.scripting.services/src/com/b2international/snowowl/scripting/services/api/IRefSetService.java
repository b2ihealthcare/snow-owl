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
package com.b2international.snowowl.scripting.services.api;

import java.util.Collection;
import java.util.Map;

import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;

/**
 * Reference set service interface for SNOMED&nbsp;CT.
 *
 */
public interface IRefSetService {

	/**
	 * Returns with all the members of the given reference set. 
	 * @param referenceSetId the reference set identifier concept ID.
	 * @return the reference set members for the reference set.
	 */
	Collection<SnomedRefSetMemberIndexEntry> getMembers(final String referenceSetId);

	/**
	 * Returns with all reference sets.
	 * @return a collection of all existing reference sets.
	 */
	Collection<SnomedRefSetIndexEntry> getAllReferenceSets();

	/**
	 * Returns with all active reference set members referring the given SNOMED&nbsp;CT concept.
	 * @param conceptId the unique concept ID.
	 * @return a collection of all active members referring to the given concept.
	 */
	Collection<SnomedRefSetMemberIndexEntry> getReferringMembers(final String conceptId);
	
	/**
	 * Returns with all active simple type reference set members referring the given SNOMED&nbsp;CT concept.
	 * @param conceptId the unique concept ID.
	 * @return a collection of all active simple type members referring to the given concept.
	 */
	Collection<SnomedRefSetMemberIndexEntry> getReferringSimpleTypeMembers(final String conceptId);
	
	/**
	 * Returns with all active reference set members referring the given SNOMED&nbsp;CT concept for the given reference set.
	 * @param conceptId the unique concept ID.
	 * @param refSetId the reference set ID.
	 * @return a collection of all active members referring to the given concept from the given reference set.
	 */
	Collection<SnomedRefSetMemberIndexEntry> getReferringMembers(final String conceptId, final String refSetId);

	/**
	 * Returns with all mapping type reference set where the map source is the given concept.
	 * @param conceptId the concept ID as the map source. (Referenced component.)
	 * @return a collection of active reference set member where the map source is the given concept. 
	 */
	Collection<SnomedRefSetMemberIndexEntry> getMappingMembers(final String conceptId);

	/**
	 * Returns with all mapping type reference set where the map source is the given concept for the given reference set.
	 * @param conceptId the concept ID as the map source. (Referenced component.)
	 * @return a collection of active reference set member where the map source is the given concept for the given reference set.
	 */
	Collection<SnomedRefSetMemberIndexEntry> getMappingMembers(final String conceptId, final String refSetId);

	/**
	 * Returns with all active mapping type reference set members from a reference set where the map target is the given component.
	 * @param mapTarget the map target component ID.
	 * @param mappingRefSetId the identifier concept ID of the mapping reference set.
	 * @return a collection of active reference set members from a particular reference set where the map target is the given component.
	 */
	Collection<SnomedRefSetMemberIndexEntry> getMembersForMapTarget(final String mapTarget, final String mappingRefSetId);

	/**
	 * Returns with a map of active mappings from a give mapping type reference set. The keys are the map source component IDs
	 * and the values are a collection of map target component IDs.
	 * @param refSetId the mapping type reference set ID.
	 * @return a map of mapping between sources and targets. Sources are unique component IDs and the values are a collection 
	 * of map target component IDs.
	 */
	Map<String, Collection<String>> getMapppings(final String refSetId);

}