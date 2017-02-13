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

import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;

/**
 * Service interface for concrete domains.
 *
 */
public interface IConcreteDomainService {

	/**
	 * Returns {@code true} if the concept given with its unique ID is a vaccine. Otherwise {@code false}.
	 * @param conceptId the concept ID. 
	 * @return {@code true} if vaccine.
	 */
	boolean isVaccine(final String conceptId);
	
	/**
	 * Returns {@code true} if the concept given with its unique ID can be tagged with vaccine. Otherwise {@code false}.
	 * @param conceptId the concept ID. 
	 * @return {@code true} if the boolean concrete domain 'canBeTaggedWithVaccine' is set.
	 */
	boolean canBeTaggedWithVaccine(final String conceptId);
	
	/**
	 * Returns {@code true} if the concept given with its unique ID is a vitamin. Otherwise {@code false}.
	 * @param conceptId the concept ID. 
	 * @return {@code true} if vitamin.
	 */
	boolean isVitamin(final String conceptId);
	
	/**
	 * Returns with all active concrete domain associated with the given concept. Concrete domains tight to any active relationship of
	 * the concept will be returned as well.
	 * @param conceptId the concept ID.
	 * @return all concrete domain of the concept.
	 */
	Collection<SnomedRefSetMemberIndexEntry> getAllDataTypesForConcept(final String conceptId);
	
	/**
	 * Returns with all active concrete domains associated with the concept.
	 * @param conceptId the concept ID.
	 * @return a collection of concrete domain associated with the concept.
	 */
	Collection<SnomedRefSetMemberIndexEntry> getDataTypesForConcept(final String conceptId);
	
	/**
	 * Returns with all active concrete domains associated with the relationship.
	 * @param relationshipId the relationship ID.
	 * @return a collection of concrete domain associated with the relationship.
	 */
	Collection<SnomedRefSetMemberIndexEntry> getDataTypesForRelationship(final String relationshipId);
	
	/**
	 * Returns with the human readable label of the concrete domain. 
	 * @param label the non-human readable label that has to be converted to the human readable one. 
	 * @return the humane readable label of the concrete domain.
	 */
	String getDataTypeLabel(final String label);

	/**
	 * Returns with all manufactured concrete domain values for the associated components.
	 * Keys are the unique concept IDs and the values are the value of the 'Is Manufactures' boolean 
	 * concrete domains.
	 * @return a mapping between concept IDs and 'Is Manufactured' concrete domain values.
	 */
	Map<String, Boolean> getAllManufacturedConcreteDomains();
}