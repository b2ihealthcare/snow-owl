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

import com.b2international.snowowl.core.api.IComponent;
import com.b2international.snowowl.dsl.escg.Expression;
import com.b2international.snowowl.snomed.Concept;

/**
 * This service provides access to Snow Owl's expression engine. The expressions
 * are conform to the Extended SNOMED Compositional Grammar (ESCG). This service
 * is specific to SNOMED CT terminology.
 * 
 * 
 */
public interface IExpressionService {

	
	//List<IComponent<Concept>> getFocusConcepts(final String escgExpression);

	//List<IComponent<Concept>> getFocusConcepts(final Expression escgExpression);

	/**
	 * Returns the Long Normal Form for the passed in concept.
	 * @param conceptId
	 * @return Long Normal Form string
	 */
	String getLongNormalFormExpressionString(final long conceptId);

	/**
	 * Returns the Long Normal Form for the passed in concept.
	 * @param conceptId
	 * @return Long Normal Form {@link Expression}
	 */
	Expression getLongNormalFormExpression(final long conceptId);

	/**
	 * Returns the Short Normal Form for the passed in concept.
	 * @param conceptId
	 * @return Short Normal Form string
	 */
	String getShortNormalFormExpressionString(final long conceptId);

	/**
	 * Returns the Short Normal Form for the passed in concept.
	 * @param conceptId
	 * @return Short Normal Form {@link Expression}
	 */
	Expression getShortNormalFormExpression(long conceptId);

	/**
	 * Evaluates and returns the result for a valid ESCG expression.
	 * 
	 * @param escgExpression as a String
	 * @return list of concept as {@link IComponent}
	 */
	List<IComponent<Concept>> evaluate(final String escgExpression);

	/**
	 * Evaluates and returns the result for a valid ESCG expression.
	 * 
	 * @param escgExpression as an {@link Expression}
	 * @return list of concept as {@link IComponent}
	 */
	List<IComponent<Concept>> evaluate(final Expression escgExpression);
}