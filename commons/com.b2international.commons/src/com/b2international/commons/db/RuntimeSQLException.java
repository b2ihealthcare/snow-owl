/*
 * Copyright 2011-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.commons.db;

import static com.google.common.base.Preconditions.checkArgument;

import java.sql.SQLException;

/**
 * Wraps a checked {@link SQLException} into a runtime exception, so SQL
 * dependent operations don't need to specify a <code>throws</code> clause in
 * their method signature.
 * 
 */
public class RuntimeSQLException extends RuntimeException {

	private static final long serialVersionUID = 3008437115208630482L;

	public RuntimeSQLException() {
		super();
	}

	public RuntimeSQLException(String message, SQLException cause) {
		super(message, cause);
	}

	public RuntimeSQLException(String message) {
		super(message);
	}

	public RuntimeSQLException(SQLException cause) {
		super(cause);
	}

	@Override
	public synchronized Throwable initCause(Throwable cause) {
		checkArgument((cause == null || cause instanceof SQLException),
				"cause must be null or an instance of SQLException.");
		return super.initCause(cause);
	}
	
	@Override
	public SQLException getCause() {
		return (SQLException) super.getCause();
	}
}