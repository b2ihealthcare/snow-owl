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
package com.b2international.snowowl.snomed.api.browser;

import com.b2international.snowowl.api.codesystem.exception.CodeSystemNotFoundException;
import com.b2international.snowowl.api.codesystem.exception.CodeSystemVersionNotFoundException;
import com.b2international.snowowl.api.domain.IComponentRef;
import com.b2international.snowowl.api.domain.IStorageRef;
import com.b2international.snowowl.core.exceptions.ComponentNotFoundException;
import com.b2international.snowowl.snomed.api.domain.browser.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * The interface for the IHTSDO SNOMED CT Browser service.
 */
public interface ISnomedBrowserService {

	/**
	 * Retrieves information strongly connected to a concept in a single request.
	 * 
	 * @param conceptRef the component reference pointing to the concept to retrieve (may not be {@code null})
	 * @param locales the {@link Locale}s to inspect when determining FSN and preferred synonym, in decreasing order of preference
	 * @return the aggregated content for the requested concept
	 * @throws CodeSystemNotFoundException if a code system with the given short name is not registered
	 * @throws CodeSystemVersionNotFoundException if a code system version for the code system with the given identifier is not registered
	 * @throws ComponentNotFoundException if the component identifier does not match any concept on the given task
	 */
	ISnomedBrowserConcept getConceptDetails(IComponentRef conceptRef, List<Locale> locales);


	/**
	 * Retrieves a list of parent concepts for a single identifier.
	 * 
	 * @param conceptRef the component reference pointing to the concept whose parents should be retrieved (may not be {@code null})
	 * @param locales the {@link Locale}s to inspect when determining FSN, in decreasing order of preference
	 * @return the parent concept list for the requested concept
	 * @throws CodeSystemNotFoundException if a code system with the given short name is not registered
	 * @throws CodeSystemVersionNotFoundException if a code system version for the code system with the given identifier is not registered
	 * @throws ComponentNotFoundException if the component identifier does not match any concept on the given task
	 */
	List<ISnomedBrowserParentConcept> getConceptParents(IComponentRef conceptRef, List<Locale> locales);
	
	/**
	 * Retrieves a list of child concepts for a single identifier.
	 * 
	 * @param conceptRef the component reference pointing to the concept whose children should be retrieved (may not be {@code null})
	 * @param locales the {@link Locale}s to inspect when determining FSN, in decreasing order of preference
	 * @return the child concept list for the requested concept
	 * @throws CodeSystemNotFoundException if a code system with the given short name is not registered
	 * @throws CodeSystemVersionNotFoundException if a code system version for the code system with the given identifier is not registered
	 * @throws ComponentNotFoundException if the component identifier does not match any concept on the given task
	 */
	List<ISnomedBrowserChildConcept> getConceptChildren(IComponentRef conceptRef, List<Locale> locales);
	
	/**
	 * Retrieves a list of descriptions matching the entered query string.
	 * 
	 * @param storageRef the storage reference locating the version and branch to search on (may not be {@code null})
	 * @param query the query text (must be at least 3 characters long)
	 * @param locales the {@link Locale}s to inspect when determining FSN, in decreasing order of preference
	 * @param offset the offset in the result set (may not be negative)
	 * @param limit the maximal number of results to return
	 * @return the search result list of descriptions
	 * @throws CodeSystemNotFoundException if a code system with the given short name is not registered
	 * @throws CodeSystemVersionNotFoundException if a code system version for the code system with the given identifier is not registered
	 * @throws IllegalArgumentException if the query is {@code null} or too short
	 */
	List<ISnomedBrowserDescriptionResult> getDescriptions(IStorageRef storageRef, String query, List<Locale> locales, ISnomedBrowserDescriptionResult.TermType resultConceptTermType, int offset, int limit);

	/**
	 * Retrieves a map of enum constants and corresponding concepts.
	 *
	 * @param storageRef the storage reference locating the version and branch to inspect (may not be {@code null})
	 * @param locales the {@link Locale}s to inspect when determining FSN, in decreasing order of preference
	 * @throws CodeSystemNotFoundException if a code system with the given short name is not registered
	 * @throws CodeSystemVersionNotFoundException if a code system version for the code system with the given identifier is not registered
	 * @return a map with keys as constant identifiers, and values as corresponding concept ID-FSN pairs
	 */
	Map<String, ISnomedBrowserConstant> getConstants(IStorageRef storageRef, List<Locale> locales);

	ISnomedBrowserConcept create(String branchPath, ISnomedBrowserConcept concept, String userId, List<Locale> locales);

	ISnomedBrowserConcept update(String branchPath, ISnomedBrowserConcept concept, String userId, ArrayList<Locale> locales);
}
