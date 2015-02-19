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
 * prepared statement instance. Operations (including the constructor) may throw
 * {@link RuntimeSQLException} to indicate failure related to the database.
 * 
 */
public class PreparedUpdateExecutor {

	/**
	 * Closes the specified query executor.
	 * 
	 * @param executor
	 *            the executor to close (may be <code>null</code> or an already
	 *            closed instance)
	 */
	public static void close(PreparedUpdateExecutor executor) {
		
		if (executor == null) {
			return;
		}
		
		executor.close();
	}
	
	private final Connection connection;
	private final PreparedStatement statement;
	private int executeCount;
	
	/**
	 * Creates a new prepared statement executor instance.
	 * 
	 * @param connection
	 *            the database connection to use
	 *            
	 * @param sql
	 *            the SQL statement, with zero or more '?' placeholders
	 */
	public PreparedUpdateExecutor(Connection connection, String sql) {
		this.connection = connection;
		this.statement = JdbcUtils.prepareStatement(connection, sql);
	}

	/**
	 * Executes an update with the specified parameters, using the Java object
	 * to database column type conversion defined by the JDBC API. Depending on
	 * the implementation used, the results of an update may not be visible even
	 * in the same transaction until {@link #commit()} is called.
	 * 
	 * @param parameters
	 *            the variable number of parameters for the prepared statement
	 */
	public void execute(Object... parameters) {
		
		try {
			
			for (int i = 0; i < parameters.length; i++) {
				statement.setObject(i + 1, parameters[i]);
			}
			
			doExecute(statement);
			executeCount++;
		
		} catch (SQLException e) {
			throw new RuntimeSQLException("Couldn't execute prepared statement '" + statement + "'.", e);
		}
	}

	/**
	 * Hook method for carrying out the actual update on the statement populated
	 * with actual parameters. The default implementation calls
	 * {@link PreparedStatement#executeUpdate()} on the given statement;
	 * subclasses may override.
	 * 
	 * @param statement
	 *            the prepared statement associated with this executor
	 * 
	 * @throws SQLException
	 *             if the statement is closed or a database error occurs
	 */
	protected void doExecute(PreparedStatement statement) throws SQLException {
		statement.executeUpdate();
	}
	
	/**
	 * Makes the changes carried out by this executor permanent by committing the
	 * active transaction of the database connection.
	 */
	public void commit() {
		
		if (executeCount == 0) {
			return;
		}
		
		try {
			preCommit(statement);
		} catch (SQLException e) {
			throw new RuntimeSQLException("Couldn't execute pre-commit operation for prepared statement '" 
					+ statement + "'.", e);
		}
		
		JdbcUtils.commit(connection);
		executeCount = 0;
	}
	
	/**
	 * Hook method for performing pre-commit operations on the prepared
	 * statement before committing the connection's active transaction. The
	 * default implementation is empty; subclasses should override.
	 * 
	 * @param statement
	 *            the prepared statement associated with this executor
	 * 
	 * @throws SQLException
	 *             if the statement is closed or a database error occurs
	 */
	protected void preCommit(PreparedStatement statement) throws SQLException {
		// Empty implementation
	}

	/**
	 * Releases resources associated with the contained prepared statement.
	 */
	public void close() {
		JdbcUtils.close(statement);
	}
	
	/**
	 * Returns the number of {@link #execute(Object...)} calls since the last commit. 
	 * 
	 * @return the execution count
	 */
	public int getExecuteCount() {
		return executeCount;
	}
}