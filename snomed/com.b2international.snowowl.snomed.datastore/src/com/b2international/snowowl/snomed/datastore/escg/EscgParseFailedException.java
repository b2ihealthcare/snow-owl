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

import org.apache.lucene.search.BooleanQuery;

/**
 * Runtime exception representing a lack of feature when attempting to parse
 * ESCG expression to the corresponding {@link BooleanQuery}.
 * <p>This exception is thrown by {@link IndexQueryQueryEvaluator}. If occurs, clients
 * should attempt to resolve ESCG evaluation with {@link ConceptIdQueryEvaluator2} service.
 */
public class EscgParseFailedException extends IllegalArgumentException {

	private static final long serialVersionUID = -4816591631985579635L;

	/**Sole constructor.*/
	public EscgParseFailedException() {
		super();
	}
	
	/**Creates a new exception with the message argument.*/
	public EscgParseFailedException(final String message) {
		super(message);
	}
	
}