/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.List;
import java.util.Map;

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.snowowl.core.domain.exceptions.CodeSystemNotFoundException;
import com.b2international.snowowl.core.domain.exceptions.CodeSystemVersionNotFoundException;
import com.b2international.snowowl.core.exceptions.ComponentNotFoundException;
import com.b2international.snowowl.snomed.api.domain.browser.ISnomedBrowserChildConcept;
import com.b2international.snowowl.snomed.api.domain.browser.ISnomedBrowserConcept;
import com.b2international.snowowl.snomed.api.domain.browser.ISnomedBrowserConceptUpdate;
import com.b2international.snowowl.snomed.api.domain.browser.ISnomedBrowserConstant;
import com.b2international.snowowl.snomed.api.domain.browser.ISnomedBrowserDescriptionResult;
import com.b2international.snowowl.snomed.api.domain.browser.ISnomedBrowserParentConcept;

/**
 * The interface for the IHTSDO SNOMED CT Browser service.
 */
public interface ISnomedBrowserService {

	/**
	 * Retrieves information strongly connected to a concept in a single request.
	 * 
	 * @param branchPath - the branch to use
	 * @param componentId - the concept id to use
	 * @param extendedLocales the {@link ExtendedLocale}s to inspect when determining FSN and preferred synonym, in decreasing order of preference
	 * @return the aggregated content for the requested concept
	 * @throws CodeSystemNotFoundException if a code system with the given short name is not registered
	 * @throws CodeSystemVersionNotFoundException if a code system version for the code system with the given identifier is not registered
	 * @throws ComponentNotFoundException if the component identifier does not match any concept on the given task
	 */
	ISnomedBrowserConcept getConceptDetails(String branchPath, String componentId, List<ExtendedLocale> extendedLocales);


	/**
	 * Retrieves a list of parent concepts for a single identifier.
	 * 
	 * @param branchPath - the branch to use
	 * @param componentId - the concept id to use
	 * @param extendedLocales the {@link ExtendedLocale}s to inspect when determining FSN, in decreasing order of preference
	 * @return the parent concept list for the requested concept
	 * @throws CodeSystemNotFoundException if a code system with the given short name is not registered
	 * @throws CodeSystemVersionNotFoundException if a code system version for the code system with the given identifier is not registered
	 * @throws ComponentNotFoundException if the component identifier does not match any concept on the given task
	 */
	List<ISnomedBrowserParentConcept> getConceptParents(String branchPath, String componentId, List<ExtendedLocale> extendedLocales);
	
	/**
	 * Retrieves a list of child concepts for a single identifier.
	 * 
	 * @param branchPath - the branch to use
	 * @param componentId - the parent concept id to use to fetch his children
	 * @param extendedLocales the {@link ExtendedLocale}s to inspect when determining FSN, in decreasing order of preference
	 * @param stated {@code true} if stated children should be returned, {@code false} if inferred
	 * @return the child concept list for the requested concept
	 * @throws CodeSystemNotFoundException if a code system with the given short name is not registered
	 * @throws CodeSystemVersionNotFoundException if a code system version for the code system with the given identifier is not registered
	 * @throws ComponentNotFoundException if the component identifier does not match any concept on the given task
	 */
	List<ISnomedBrowserChildConcept> getConceptChildren(String branchPath, String componentId, List<ExtendedLocale> extendedLocales, boolean stated);
	
	/**
	 * Retrieves a list of descriptions matching the entered query string.
	 * 
	 * @param branchPath - the branch to use
	 * @param query the query text (must be at least 3 characters long)
	 * @param extendedLocales the {@link ExtendedLocale}s to inspect when determining FSN, in decreasing order of preference
	 * @param scrollKeepAlive
	 * @param scrollId
	 * @param limit the maximal number of results to return
	 * @return the search result list of descriptions
	 * @throws CodeSystemNotFoundException if a code system with the given short name is not registered
	 * @throws CodeSystemVersionNotFoundException if a code system version for the code system with the given identifier is not registered
	 * @throws IllegalArgumentException if the query is {@code null} or too short
	 */
	List<ISnomedBrowserDescriptionResult> getDescriptions(String branchPath, String query, List<ExtendedLocale> extendedLocales, ISnomedBrowserDescriptionResult.TermType resultConceptTermType, String scrollKeepAlive, String scrollId, int limit);

	/**
	 * Retrieves a map of enum constants and corresponding concepts.
	 *
	 * @param storageRef the storage reference locating the version and branch to inspect (may not be {@code null})
	 * @param extendedLocales the {@link ExtendedLocale}s to inspect when determining FSN, in decreasing order of preference
	 * @throws CodeSystemNotFoundException if a code system with the given short name is not registered
	 * @throws CodeSystemVersionNotFoundException if a code system version for the code system with the given identifier is not registered
	 * @return a map with keys as constant identifiers, and values as corresponding concept ID-FSN pairs
	 */
	Map<String, ISnomedBrowserConstant> getConstants(String branch, List<ExtendedLocale> extendedLocales);

	ISnomedBrowserConcept create(String branchPath, ISnomedBrowserConcept concept, String userId, List<ExtendedLocale> extendedLocales);

	ISnomedBrowserConcept update(String branchPath, ISnomedBrowserConceptUpdate concept, String userId, List<ExtendedLocale> extendedLocales);
}
