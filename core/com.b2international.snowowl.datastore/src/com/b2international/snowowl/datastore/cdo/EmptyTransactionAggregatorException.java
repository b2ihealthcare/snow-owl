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
package com.b2international.snowowl.datastore.cdo;

import javax.annotation.Nullable;

/**
 * Exception has to be thrown to indicate empty {@link ICDOTransactionAggregator}.
 */
public class EmptyTransactionAggregatorException extends RuntimeException {

	/**
	 * Default message. {@value}
	 */
	private static final String DEFAULT_MESSAGE = "Transactions were empty.";
	private static final long serialVersionUID = -7097599935832143360L;

	/**Creates a new exception with the {@link #DEFAULT_MESSAGE} message.*/
	public EmptyTransactionAggregatorException() {
		super(DEFAULT_MESSAGE);
	}
	
	public EmptyTransactionAggregatorException(@Nullable final String message) {
		super(message);
	}
	
}