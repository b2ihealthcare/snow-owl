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
package com.b2international.snowowl.snomed.api;

import java.util.Map;

import com.b2international.snowowl.api.codesystem.exception.CodeSystemNotFoundException;
import com.b2international.snowowl.api.codesystem.exception.CodeSystemVersionNotFoundException;
import com.b2international.snowowl.api.domain.IComponentList;
import com.b2international.snowowl.api.exception.IllegalQueryParameterException;
import com.b2international.snowowl.api.task.exception.TaskNotFoundException;
import com.b2international.snowowl.snomed.api.domain.ISnomedConcept;
import com.b2international.snowowl.snomed.api.domain.ISnomedConceptInput;
import com.b2international.snowowl.snomed.api.domain.ISnomedConceptUpdate;
import com.b2international.snowowl.snomed.api.domain.SearchKind;

/**
 * SNOMED CT concept service implementations provide methods for creating, reading, updating and deleting single
 * concepts, as well as searching for and enumerating pageable subsets of all concepts on a particular version and task.
 */
public interface ISnomedConceptService extends ISnomedComponentService<ISnomedConceptInput, ISnomedConcept, ISnomedConceptUpdate> {

	/**
	 * Returns a subset of all stored concepts.
	 * 
	 * @param version the version to use when enumerating all concepts (may not be {@code null})
	 * @param taskId  the task identifier to use, or {@code null} if the version itself should be inspected
	 * @param offset  the starting offset in the list (may not be negative)
	 * @param limit   the maximum number of results to return (may not be negative)
	 * 
	 * @return the list of concepts
	 * 
	 * @throws CodeSystemNotFoundException        if SNOMED CT as a code system is not registered
	 * @throws CodeSystemVersionNotFoundException if a code system version for SNOMED CT with the given identifier
	 *                                            is not registered
	 * @throws TaskNotFoundException              if the task identifier does not correspond to a task for the given 
	 *                                            code system version
	 */
	IComponentList<ISnomedConcept> getAllConcepts(String version, String taskId, int offset, int limit);

	/**
	 * Searches for concepts matching the specified query parameters.
	 * <p>
	 * Currently only {@link SearchKind#ESCG ESCG} and {@link SearchKind#LABEL LABEL} queries are supported. If multiple key-value pairs are set,
	 * they will be combined with an {@code AND} boolean operator.
	 * 
	 * @param version     the version to use when enumerating all concepts (may not be {@code null})
	 * @param taskId      the task identifier to use, or {@code null} if the version itself should be inspected
	 * @param queryParams the conditions which should be met for matching concepts (may not be {@code null})
	 * @param offset      the starting offset in the list (may not be negative)
	 * @param limit       the maximum number of results to return (may not be negative)
	 * 
	 * @return the list of matching concepts
	 * 
	 * @throws CodeSystemNotFoundException        if SNOMED CT as a code system is not registered
	 * @throws CodeSystemVersionNotFoundException if a code system version for SNOMED CT with the given identifier
	 *                                            is not registered
	 * @throws TaskNotFoundException              if the task identifier does not correspond to a task for the given 
	 *                                            code system version
	 * @throws IllegalQueryParameterException     if an invalid ESCG expression was passed in as a query parameter
	 */
	IComponentList<ISnomedConcept> search(String version, String taskId, Map<SearchKind, String> queryParams, int offset, int limit);
}
