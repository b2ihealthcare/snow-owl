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
package com.b2international.snowowl.snomed.datastore.id.assigner;

import java.util.Set;

import com.b2international.snowowl.core.domain.BranchContext;

/**
 * Common interface for a namespace-module allocator which:
 * <ul>
 * <li>For each inferred relationship, returns the expected namespace and module
 * ID, given the source concept ID;
 * <li>For each inferred concrete domain member, returns the expected module ID,
 * given the "container" concept ID.
 * </ul>
 * 
 * @since 5.11.5
 */
public interface SnomedNamespaceAndModuleAssigner {

	/**
	 * Returns an SCTID to be registered to a relationship based on its source
	 * concept ID.
	 * 
	 * @param sourceConceptId the concept ID of the relationship to allocate the
	 *                        SCTID to
	 * @return the namespace for the relationship
	 */
	String getRelationshipNamespace(String sourceConceptId);

	/**
	 * Returns a module concept to be assigned to a relationship based on its source
	 * concept ID.
	 * 
	 * @param sourceConceptId the concept ID of the relationship to determine the
	 *                        module for
	 * @return the module ID for the relationship
	 */
	String getRelationshipModuleId(String sourceConceptId);

	/**
	 * Returns a module concept to be assigned to a concrete domain based on its
	 * referenced component ID.
	 * 
	 * @param referencedConceptId the ID of the referenced concept to determine the
	 *                        module for
	 * @return the module ID for the concrete domain member
	 */
	String getConcreteDomainModuleId(String referencedConceptId);

	/**
	 * @param conceptIds
	 * @param context
	 */
	void collectRelationshipNamespacesAndModules(Set<String> conceptIds, BranchContext context);

	/**
	 * @param conceptIds
	 * @param context
	 */
	void collectConcreteDomainModules(Set<String> conceptIds, BranchContext context);
}
