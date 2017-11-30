/*
 * Copyright 2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.reasoner.server;

import java.util.Set;

import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.datastore.SnomedEditingContext;
import com.google.common.collect.Multiset;

/**
 * Common interface for a namespace-module allocator which:
 * <ul>
 * <li>Pre-loads module concepts used for saving inferred relationship and CD member changes;
 * <li>Collects the required number of SCTIDs for inferred relationships for each relevant namespace, and reserves them in bulk;
 * <li>For each inferred relationship, returns the expected SCTID and module concept, given the source concept ID;
 * <li>For each inferred CD member, returns the expected module concept, given the "container" concept ID.
 * </ul>
 * 
 * @since 5.11.5
 */
public interface NamespaceAndModuleAssigner {

	/**
	 * Reserves SCTIDs and loads module concepts for the new relationships of each source concept passed in as a {@link Multiset} of concept IDs.
	 * 
	 * The allocated values and modules can be later be retrieved by {@link #getRelationshipId(String)} and {@link #getRelationshipModule(String)},
	 * respectively.
	 * 
	 * @param conceptIds
	 *            the number of relationships to allocate values for, keyed by source concept ID
	 * @param context
	 *            the active SNOMED CT editing context
	 */
	void allocateRelationshipIdsAndModules(final Multiset<String> conceptIds, final SnomedEditingContext context);

	/**
	 * Returns an SCTID to be registered to a relationship based on its source concept ID.
	 * 
	 * @param sourceConceptId
	 *            the concept ID of the relationship to allocate the SCTID to
	 * @return SCTID for the relationship
	 */
	String getRelationshipId(final String sourceConceptId);

	/**
	 * Returns a module concept to be assigned to a relationship based on its source concept ID.
	 * 
	 * @param sourceConceptId
	 *            the concept ID of the relationship to determine the module for
	 * @return the module concept for the relationship
	 */
	Concept getRelationshipModule(final String sourceConceptId);

	/**
	 * Loads modules for the new concrete domains of each concept passed in as a set of concept IDs.
	 * 
	 * The allocated modules can be later retrieved via the {@link #getConcreteDomainModule(String)} method.
	 * 
	 * @param conceptIds
	 *            the set of source concept IDs to look up modules for
	 * @param context
	 *            the active SNOMED CT editing context
	 */
	void allocateConcreteDomainModules(final Set<String> conceptIds, final SnomedEditingContext context);

	/**
	 * Returns a module concept to be assigned to a concrete domain based on its source concept ID.
	 * 
	 * @param sourceConceptId
	 *            the ID of the referenced concept to determine the module for
	 * @return module concept id for the concrete domain
	 */
	Concept getConcreteDomainModule(final String sourceConceptId);

	/**
	 * After successfully allocating and using the IDs the clients are responsible of calling this method to ensure that the allocated IDs will be
	 * registered in the underlying ID service.
	 */
	void registerAllocatedIds();
}
