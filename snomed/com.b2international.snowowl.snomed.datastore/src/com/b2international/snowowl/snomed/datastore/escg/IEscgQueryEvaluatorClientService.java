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
package com.b2international.snowowl.snomed.datastore.escg;

import com.b2international.collections.longs.LongCollection;

/**
 * Service for evaluating ESCG query expressions.
 * 
 * @see IQueryExpressionWrapper
 */
public interface IEscgQueryEvaluatorClientService {
	
	/**
	 * Evaluates the specified ESCG expression specified as a string and returns with a bunch of SNOMED&nbsp;CT concept IDs.
	 * @param queryExpression the ESCG query expression.
	 * @return the unique IDs of the SNOMED&nbsp;CT concepts.
	 */
	LongCollection evaluateConceptIds(final String queryExpression);
	
} 