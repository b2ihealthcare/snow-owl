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

import com.b2international.commons.exceptions.NotFoundException;
import com.b2international.commons.http.ExtendedLocale;
import com.b2international.snowowl.snomed.api.domain.browser.ISnomedBrowserConcept;
import com.b2international.snowowl.snomed.api.domain.classification.ClassificationStatus;
import com.b2international.snowowl.snomed.api.domain.classification.IClassificationRun;
import com.b2international.snowowl.snomed.api.domain.classification.IEquivalentConceptSet;
import com.b2international.snowowl.snomed.api.domain.classification.IRelationshipChangeList;

/**
 * Implementations provide access to the SNOMED CT classification functionality of Snow Owl Server.
 */
public interface ISnomedClassificationService {

	/**
	 * Retrieves all classification runs for a particular version, task and requesting user.
	 * 
	 * @param branchPath
	 *            the branch path to match (may not be {@code null})
	 * @param userId
	 *            the requesting user's identifier to match (may not be {@code null})
	 * 
	 * @return a list of {@link IClassificationRun}s corresponding to the input parameters
	 * 
	 * @throws NotFoundException
	 *             - if SNOMED CT as a code system is not registered or the branch associated with the given branchPath is not found
	 */
	List<IClassificationRun> getAllClassificationRuns(String branchPath, String userId);

	/**
	 * Starts a classification run.
	 * <p>
	 * Reasoner identifiers correspond to Equinox fully qualified extension identifiers for the extension point "
	 * {@code org.protege.editor.owl.inference_reasonerfactory}".
	 * 
	 * @param branchPath
	 *            the branch path to run the classification on (may not be {@code null})
	 * @param reasonerId
	 *            the identifier of the reasoner factory to use, eg. "{@code org.semanticweb.elk.elk.reasoner.factory}" for the ELK reasoner (may not
	 *            be {@code null})
	 * @param userId
	 *            the requesting user's identifier to check (may not be {@code null})
	 * 
	 * @return a new {@link IClassificationRun} corresponding to the input parameters, as returned by
	 *         {@link #getClassificationRun(String, String, String, String)}
	 * 
	 * @throws NotFoundException
	 *             - if SNOMED CT as a code system is not registered or the branch associated with the given branchPath is not found
	 */
	IClassificationRun beginClassification(String branchPath, String reasonerId, String userId);

	/**
	 * Retrieves a single classification run with the specified unique identifier.
	 * 
	 * @param branchPath
	 *            the branch path to match (may not be {@code null})
	 * @param classificationId
	 *            the classification identifier to match (may not be {@code null})
	 * @param userId
	 *            the requesting user's identifier to match (may not be {@code null})
	 * 
	 * @return information about an ongoing or finished classification in an {@link IClassificationRun} object
	 *
	 * @throws NotFoundException
	 *             - if SNOMED CT as a code system is not registered or the branch associated with the given branchPath is not found or the
	 *             classification run not found with the specified parameters
	 */
	IClassificationRun getClassificationRun(String branchPath, String classificationId, String userId);

	/**
	 * Retrieves the list of equivalent concept sets found during classification. Concepts in an equivalence set were considered
	 * equivalent by the reasoner based on their defining properties.
	 * <p>
	 * Results are only available if the status of this run is set to {@link ClassificationStatus#COMPLETED COMPLETED}.
	 * 
	 * @param branchPath
	 *            the branch path to match (may not be {@code null})
	 * @param classificationId
	 *            the classification identifier to match (may not be {@code null})
	 * @param locales
	 *            the list of locales to use, in order of preference
	 * @param userId
	 *            the requesting user's identifier to match (may not be {@code null})
	 * 
	 * @return a list of {@link IEquivalentConceptSet equivalent concept sets}, or an empty list if no such concept set exists
	 * 
	 * @throws NotFoundException
	 *             - if SNOMED CT as a code system is not registered or the branch associated with the given branchPath is not
	 *             found or the classification run not found with the specified parameters
	 */
	List<IEquivalentConceptSet> getEquivalentConceptSets(String branchPath, String classificationId, List<ExtendedLocale> locales, String userId);

	/**
	 * Retrieves the pageable list of suggested relationship changes found during classification.
	 * <p>
	 * Suggestions include inactivation of existing relationships as well as creation of new relationships to ensure that the terminology's
	 * {@code IS A} relationships reflect the state inferred by the reasoner, and the entire terminology is kept in distribution normal form at the
	 * same time.
	 * <p>
	 * Changes are only available if the status of this run is set to {@link ClassificationStatus#COMPLETED COMPLETED}.
	 * 
	 * @param branchPath
	 *            the branch path to match (may not be {@code null})
	 * @param classificationId
	 *            the classification identifier to match (may not be {@code null})
	 * @param userId
	 *            the requesting user's identifier to match (may not be {@code null})
	 * @param offset
	 *            the starting offset in the list (may not be negative)
	 * @param limit
	 *            the maximum number of results to return (may not be negative)
	 * 
	 * @return a list of suggested relationship changes, or an empty list if no such suggestions exist
	 * 
	 * @throws NotFoundException
	 *             - if SNOMED CT as a code system is not registered or the branch associated with the given branchPath is not found or the
	 *             classification run not found with the specified parameters
	 */
	IRelationshipChangeList getRelationshipChanges(String branchPath, String classificationId, String userId, int offset, int limit);

	ISnomedBrowserConcept getConceptPreview(String branchPath, String classificationId, String conceptId, List<ExtendedLocale> extendedLocales, String principalName);

	/**
	 * Persists suggested changes for the specified classification run to the terminology store.
	 * <p>
	 * Saving is only possible if the status of this run is set to {@link ClassificationStatus#COMPLETED COMPLETED}.
	 * <p>
	 * This method returns immediately. The process can be followed by polling the {@code ClassificationStatus} of the classification run using
	 * {@link #getClassificationRun(String, String, String, String)}.
	 * 
	 * @param branchPath
	 *            the branch path to match (may not be {@code null})
	 * @param classificationId
	 *            the classification identifier to match (may not be {@code null})
	 * @param userId
	 *            the requesting user's identifier to match (may not be {@code null})
	 * 
	 * @throws NotFoundException
	 *             - if SNOMED CT as a code system is not registered or the branch associated with the given branchPath is not found or the
	 *             classification run not found with the specified parameters
	 */
	void persistChanges(String branchPath, String classificationId, String userId);

	/**
	 * Removes all information related to the specified classification run from the system.
	 * 
	 * @param branchPath
	 *            the branch path to match (may not be {@code null})
	 * @param classificationId
	 *            the classification identifier to match (may not be {@code null})
	 * @param userId
	 *            the requesting user's identifier to match (may not be {@code null})
	 * 
	 * @throws NotFoundException
	 *             - if SNOMED CT as a code system is not registered or the branch associated with the given branchPath is not found or the
	 *             classification run not found with the specified parameters
	 */
	void removeClassificationRun(String branchPath, String classificationId, String userId);
}
