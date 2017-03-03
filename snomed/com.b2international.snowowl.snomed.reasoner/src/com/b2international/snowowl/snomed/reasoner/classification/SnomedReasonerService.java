/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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

/**
 * Represents the RPC interface for the SNOMED&nbsp;CT reasoner service.
 * <p>
 * Supported operations are:
 * <ul>
 * <li>{@link #beginClassification(ClassificationSettings, String) starting classification of a branch}
 * <li>{@link #getResult(String) retrieving a classification result for review}
 * <li>{@link #getEquivalentConcepts(String) returning equivalence sets}
 * <li>{@link #persistChanges(String, String) persisting changes from a classification run}
 * <li>{@link #canStartImmediately() checking reasoner availability}
 * <li>{@link #removeResult(String) removing a classification result}
 * </ul>
 */
public interface SnomedReasonerService {

	/**
	 * Performs a classification run for the specified branch of SNOMED CT. Current ontology content is extended with
	 * the given concept definitions; if a definition describes a concept which already exists, the incoming definition
	 * takes precedence.
	 * 
	 * @param settings the object describing parameters for the classification run
	 * @param userId the identifier of the user requesting classification
	 * @return the identifier for the classification run
	 */
	String beginClassification(ClassificationSettings settings, String userId);

	/**
	 * Returns the results of the classification for review. This method blocks until a result becomes available.
	 * 
	 * @param classificationId the unique identifier of the classification run
	 * @return a {@link GetResultResponse} instance
	 */
	GetResultResponse getResult(String classificationId);

	/**
	 * Returns equivalent sets computed from the classification. This method blocks until a result becomes available.
	 * 
	 * @param classificationId the unique identifier of the classification run
	 * @return a {@link GetEquivalentConceptsResponse} instance
	 */
	GetEquivalentConceptsResponse getEquivalentConcepts(String classificationId);

	/**
	 * Instructs the reasoner to persist the results of the classification with the given identifier.
	 * 
	 * @param classificationId the unique identifier of the classification run
	 * @param userId the requesting user's identifier
	 * @return a {@link PersistChangesResponse} instance
	 */
	PersistChangesResponse persistChanges(String classificationId, String userId);

	/**
	 * Checks if any free reasoner instances are available.
	 * 
	 * @return {@code true} if a request for classification is likely to start in a short time, {@code false} otherwise
	 */
	boolean canStartImmediately();

	/**
	 * Removes the result of the classification with the given identifier from memory, it it exists.
	 * 
	 * @param classificationId the unique identifier of the classification run
	 */
	void removeResult(String classificationId);

}
