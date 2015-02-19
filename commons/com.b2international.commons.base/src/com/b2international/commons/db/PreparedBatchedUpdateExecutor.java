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
package com.b2international.commons.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Allows the client to issue multiple updates to the database with the same
 * prepared statement instance in batches. Operations (including the
 * constructor) may throw {@link RuntimeSQLException} to indicate failure
 * related to the database.
 * 
 */
public class PreparedBatchedUpdateExecutor extends PreparedUpdateExecutor {

	/**
	 * Creates a new batched prepared statement executor instance.
	 * 
	 * @param connection
	 *            the database connection to use
	 *            
	 * @param sql
	 *            the SQL statement, with zero or more '?' placeholders
	 */
	public PreparedBatchedUpdateExecutor(Connection connection, String sql) {
		super(connection, sql);
	}

	@Override
	protected void doExecute(PreparedStatement statement) throws SQLException {
		statement.addBatch();
	}
	
	@Override
	protected void preCommit(PreparedStatement statement) throws SQLException {
		statement.executeBatch();
	}
}