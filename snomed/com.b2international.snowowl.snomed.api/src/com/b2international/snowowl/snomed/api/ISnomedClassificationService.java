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

import java.util.List;

import com.b2international.snowowl.api.codesystem.exception.CodeSystemNotFoundException;
import com.b2international.snowowl.api.codesystem.exception.CodeSystemVersionNotFoundException;
import com.b2international.snowowl.api.task.exception.TaskNotFoundException;
import com.b2international.snowowl.snomed.api.domain.classification.ClassificationStatus;
import com.b2international.snowowl.snomed.api.domain.classification.IClassificationRun;
import com.b2international.snowowl.snomed.api.domain.classification.IEquivalentConceptSet;
import com.b2international.snowowl.snomed.api.domain.classification.IRelationshipChangeList;
import com.b2international.snowowl.snomed.api.exception.ClassificationRunNotFoundException;

/**
 * Implementations provide access to the SNOMED CT classification functionality of Snow Owl Server.
 */
public interface ISnomedClassificationService {

	/**
	 * Retrieves all classification runs for a particular version, task and requesting user.
	 *  
	 * @param version the version to match (may not be {@code null})
	 * @param taskId  the task identifier to match, or {@code null} if runs for a particular version should be retrieved
	 * @param userId  the requesting user's identifier to match (may not be {@code null})
	 * 
	 * @return a list of {@link IClassificationRun}s corresponding to the input parameters
	 * 
	 * @throws CodeSystemNotFoundException        if SNOMED CT as a code system is not registered
	 * @throws CodeSystemVersionNotFoundException if a code system version for SNOMED CT with the given identifier
	 *                                            is not registered
	 * @throws TaskNotFoundException              if the task identifier does not correspond to a task for the given 
	 *                                            code system version
	 */
	List<IClassificationRun> getAllClassificationRuns(String version, String taskId, String userId);

	/**
	 * Starts a classification run.
	 * <p>
	 * Reasoner identifiers correspond to Equinox fully qualified extension identifiers for the extension point 
	 * "{@code org.protege.editor.owl.inference_reasonerfactory}". 
	 * 
	 * @param version    the version to run the classification on (may not be {@code null})
	 * @param taskId     the task to run the classification on, or {@code null} if the classification should be performed 
	 *                   on a particular version
	 * @param reasonerId the identifier of the reasoner factory to use, eg. "{@code org.semanticweb.elk.elk.reasoner.factory}" 
	 *                   for the ELK reasoner (may not be {@code null})
	 * @param userId     the requesting user's identifier to check (may not be {@code null})
	 * 
	 * @return a new {@link IClassificationRun} corresponding to the input parameters, as returned 
	 * by {@link #getClassificationRun(String, String, String, String)}
	 * 
	 * @throws CodeSystemNotFoundException        if SNOMED CT as a code system is not registered
	 * @throws CodeSystemVersionNotFoundException if a code system version for SNOMED CT with the given identifier
	 *                                            is not registered
	 * @throws TaskNotFoundException              if the task identifier does not correspond to a task for the given 
	 *                                            code system version
	 */
	IClassificationRun beginClassification(String version, String taskId, String reasonerId, String userId);

	/**
	 * Retrieves a single classification run with the specified unique identifier.
	 * 
	 * @param version          the version to match (may not be {@code null})
	 * @param taskId           the task identifier to match, or {@code null} if runs for a particular version should be retrieved
	 * @param classificationId the classification identifier to match (may not be {@code null})
	 * @param userId           the requesting user's identifier to match (may not be {@code null})
	 * 
	 * @return information about an ongoing or finished classification in an {@link IClassificationRun} object
	 *
	 * @throws CodeSystemNotFoundException        if SNOMED CT as a code system is not registered
	 * @throws CodeSystemVersionNotFoundException if a code system version for SNOMED CT with the given identifier
	 *                                            is not registered
	 * @throws TaskNotFoundException              if the task identifier does not correspond to a task for the given 
	 *                                            code system version
	 * @throws ClassificationRunNotFoundException if the classification with the specified parameters can not be found
	 */
	IClassificationRun getClassificationRun(String version, String taskId, String classificationId, String userId);

	/**
	 * Retrieves the list of equivalent concept sets found during classification. Concepts in an equivalence set were considered
	 * equivalent by the reasoner based on their defining properties.
	 * <p>
	 * Results are only available if the status of this run is set to {@link ClassificationStatus#COMPLETED COMPLETED}.
	 * 
	 * @param version          the version to match (may not be {@code null})
	 * @param taskId           the task identifier to match, or {@code null} if runs for a particular version should be retrieved
	 * @param classificationId the classification identifier to match (may not be {@code null})
	 * @param userId           the requesting user's identifier to match (may not be {@code null})
	 * 
	 * @return a list of {@link IEquivalentConceptSet equivalent concept sets}, or an empty list if no such concept set exists
	 * 
	 * @throws CodeSystemNotFoundException        if SNOMED CT as a code system is not registered
	 * @throws CodeSystemVersionNotFoundException if a code system version for SNOMED CT with the given identifier
	 *                                            is not registered
	 * @throws TaskNotFoundException              if the task identifier does not correspond to a task for the given 
	 *                                            code system version
	 * @throws ClassificationRunNotFoundException if the classification with the specified parameters can not be found
	 */
	List<IEquivalentConceptSet> getEquivalentConceptSets(String version, String taskId, String classificationId, String userId);

	/**
	 * Retrieves the pageable list of suggested relationship changes found during classification.
	 * <p>
	 * Suggestions include inactivation of existing relationships as well as creation of new relationships to ensure that 
	 * the terminology's {@code IS A} relationships reflect the state inferred by the reasoner, and the entire terminology 
	 * is kept in distribution normal form at the same time.
	 * <p>
	 * Changes are only available if the status of this run is set to {@link ClassificationStatus#COMPLETED COMPLETED}.
	 * 
	 * @param version          the version to match (may not be {@code null})
	 * @param taskId           the task identifier to match, or {@code null} if runs for a particular version should be retrieved
	 * @param classificationId the classification identifier to match (may not be {@code null})
	 * @param userId           the requesting user's identifier to match (may not be {@code null})
	 * @param offset           the starting offset in the list (may not be negative)
	 * @param limit            the maximum number of results to return (may not be negative)
	 * 
	 * @return a list of suggested relationship changes, or an empty list if no such suggestions exist
	 * 
	 * @throws CodeSystemNotFoundException        if SNOMED CT as a code system is not registered
	 * @throws CodeSystemVersionNotFoundException if a code system version for SNOMED CT with the given identifier
	 *                                            is not registered
	 * @throws TaskNotFoundException              if the task identifier does not correspond to a task for the given 
	 *                                            code system version
	 * @throws ClassificationRunNotFoundException if the classification with the specified parameters can not be found
	 */
	IRelationshipChangeList getRelationshipChanges(String version, String taskId, String classificationId, String userId, int offset, int limit);

	/**
	 * Persists suggested changes for the specified classification run to the terminology store.
	 * <p>
	 * Saving is only possible if the status of this run is set to {@link ClassificationStatus#COMPLETED COMPLETED}.
	 * <p>
	 * This method returns immediately. The process can be followed by polling the {@code ClassificationStatus} of the classification run
	 * using {@link #getClassificationRun(String, String, String, String)}.
	 * 
	 * @param version          the version to match (may not be {@code null})
	 * @param taskId           the task identifier to match, or {@code null} if runs for a particular version should be retrieved
	 * @param classificationId the classification identifier to match (may not be {@code null})
	 * @param userId           the requesting user's identifier to match (may not be {@code null})
	 * 
	 * @throws CodeSystemNotFoundException        if SNOMED CT as a code system is not registered
	 * @throws CodeSystemVersionNotFoundException if a code system version for SNOMED CT with the given identifier
	 *                                            is not registered
	 * @throws TaskNotFoundException              if the task identifier does not correspond to a task for the given 
	 *                                            code system version
	 * @throws ClassificationRunNotFoundException if the classification with the specified parameters can not be found
	 */
	void persistChanges(String version, String taskId, String classificationId, String userId);

	/**
	 * Removes all information related to the specified classification run from the system.
	 * 
	 * @param version          the version to match (may not be {@code null})
	 * @param taskId           the task identifier to match, or {@code null} if runs for a particular version should be retrieved
	 * @param classificationId the classification identifier to match (may not be {@code null})
	 * @param userId           the requesting user's identifier to match (may not be {@code null})
	 * 
	 * @throws CodeSystemNotFoundException        if SNOMED CT as a code system is not registered
	 * @throws CodeSystemVersionNotFoundException if a code system version for SNOMED CT with the given identifier
	 *                                            is not registered
	 * @throws TaskNotFoundException              if the task identifier does not correspond to a task for the given 
	 *                                            code system version
	 * @throws ClassificationRunNotFoundException if the classification with the specified parameters can not be found
	 */
	void removeClassificationRun(String version, String taskId, String classificationId, String userId);
}
