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
package com.b2international.snowowl.snomed.reasoner.classification;

import java.util.List;
import java.util.UUID;

import com.b2international.snowowl.core.api.IBranchPath;

/**
 * Represents the RPC interface for the SNOMED&nbsp;CT reasoner service.
 * <p>
 * Supported operations are:
 * <ul>
 * <li>{@link #beginClassification(UUID, IBranchPath) starting classification of a branch}
 * <li>{@link #beginClassification(UUID, IBranchPath, List) starting classification of a branch with additional definitions}
 * <li>{@link #getResult(UUID) retrieving classification results for review}
 * <li>{@link #getEquivalentConcepts(UUID) returning equivalence sets}
 * <li>{@link #persistChanges(UUID, String) persisting changes from a classification run}
 * <li>{@link #canStartImmediately() checking reasoner availability}
 * </ul>
 * 
 */
public interface SnomedReasonerService {

	String USER_COMMAND_ID = "com.b2international.snowowl.reviewResults";
	String CHANGES_PERSISTED_ADDRESS = "Classification_{uuid}_persisted";

	/**
	 * Triggers a classification run for the specified branch of SNOMED CT. Current ontology content is extended with
	 * the given concept definitions; if a definition describes a concept which already exists, the incoming definition
	 * takes precedence.
	 * 
	 * @param classificationRequest the request object describing parameters
	 */
	void beginClassification(ClassificationRequest classificationRequest);

	/**
	 * Returns the results of the classification for review. This method blocks until a result becomes available.
	 * 
	 * @param classificationId the unique identifier of the classification run
	 * @return a {@link GetResultResponse} instance
	 */
	GetResultResponse getResult(UUID classificationId);

	/**
	 * Returns equivalent sets computed from the classification. This method blocks until a result becomes available.
	 * 
	 * @param classificationId the unique identifier of the classification run
	 * @return a {@link GetEquivalentConceptsResponse} instance
	 */
	GetEquivalentConceptsResponse getEquivalentConcepts(UUID classificationId);

	/**
	 * Instructs the reasoner to persist the results of the classification with the give identifier. This method blocks
	 * until the results are persisted.
	 * 
	 * @param classificationId the unique identifier of the classification run
	 * @param userId the requesting user's identifier
	 * @return a {@link PersistChangesResponse} instance
	 */
	PersistChangesResponse persistChanges(UUID classificationId, String userId);

	/**
	 * Checks if any free reasoner instances are available.
	 * 
	 * @return {@code true} if a request for classification is likely to start in a short time, {@code false} otherwise
	 */
	boolean canStartImmediately();
}