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

import java.util.List;

import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;

/**
 * Representation of a query evaluation service.
 * <p>Available features:
 * <ul>
 * <li>{@link #evaluate(String) <em>Evaluates a query expression</em>}</li>
 * <li>{@link #evaluateForIds(String) <em>Evaluates a query expression for component IDs</em>}</li>
 * </ul>
 * </p>
 * @param <T> type of the results.
 * @deprecated - use {@link SnomedRequests#prepareSearchConcept()} instead
 */
public interface IQueryEvaluatorService<T> {

	/**
	 * Evaluates a query described with a domain specific language and returns with the results as the outcome of the query evaluation.
	 * @param queryExpression the query expression to evaluate.
	 * @return a collection of results as the outcome of the query evaluation. The results are ordered.
	 * @see IQueryEvaluatorService
	 */
	List<T> evaluate(final String queryExpression);
	
	/**
	 * Evaluates a query described with a domain specific language and returns with the component IDs as the outcome of the query evaluation.
	 * @param queryExpression the query expression to evaluate.
	 * @return a collection of component unique IDs as the outcome of the query evaluation. The results are ordered.
	 * @see IQueryEvaluatorService
	 */
	List<String> evaluateForIds(final String queryExpression);
	
}