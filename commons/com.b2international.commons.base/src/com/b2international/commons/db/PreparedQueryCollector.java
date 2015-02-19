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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

/**
 * Executes a SQL query and places the results in a collection of objects of
 * type T after conversion. Operations (including the constructor) may throw
 * {@link RuntimeSQLException} to indicate failure related to the database.
 * 
 * 
 * @param <T>
 *            the type of the objects expected from the query result
 */
public abstract class PreparedQueryCollector<T> {
	
	/**
	 * Closes the specified prepared query collector.
	 * 
	 * @param collector
	 *            the query collector to close (may be <code>null</code> or a
	 *            query collector instance which has already been closed)
	 */
	public static void close(PreparedQueryCollector<?> collector) {

		if (collector == null) {
			return;
		}
		
		collector.close();
	}

	private final PreparedStatement statement;
	
	/**
	 * Creates a new prepared query collector instance.
	 * 
	 * @param connection
	 *            the database connection to use
	 *            
	 * @param sql
	 *            the SQL statement, with zero or more '?' parameter
	 *            placeholders
	 */
	public PreparedQueryCollector(Connection connection, String sql) {
		this.statement = JdbcUtils.prepareStatement(connection, sql);
	}

	/**
	 * Executes the query with the specified parameters, and places the results
	 * into the given collection.
	 * <p>
	 * When the object conversion and addition to the given collection is
	 * finished, {@link #afterProcessed()} is called; if no row from the result
	 * set has been processed, or the result set is empty,
	 * {@link #onZeroResultsProcessed()} is also invoked.
	 * 
	 * @param resultCollection
	 *            the collection to add elements to
	 * 
	 * @param parameters
	 *            the variable number of query parameters
	 */
	public void collect(Collection<T> resultCollection, Object... parameters) {
		
		ResultSet resultSet = null;
		
		try {
			
			for (int i = 0; i < parameters.length; i++) {
				statement.setObject(i + 1, parameters[i]);
			}
			
			resultSet = statement.executeQuery();
			boolean oneResultProcessed = handleResultSet(resultCollection, resultSet);
			afterProcessed();

			if (!oneResultProcessed) {
				onZeroResultsProcessed();
			}
		
		} catch (SQLException e) {
			throw new RuntimeSQLException("Couldn't execute prepared query '" + statement + "'.", e);
		} finally {
			JdbcUtils.close(resultSet);
		}
	}
	
	/**
	 * Handles the conversion and addition of new objects to the collection
	 * based on the result set returned by the query. The default implementation
	 * calls {@link #forEachResult(Collection, ResultSet)} for each resulting
	 * row, and stops processing if <code>false</code> is returned from that
	 * method.
	 * 
	 * @param resultCollection
	 *            the collection to add query result objects to
	 * 
	 * @param resultSet
	 *            the result set returned by the query
	 * 
	 * @return <code>true</code> if at least one result set row has been
	 *         processed, <code>false</code> otherwise
	 * 
	 * @throws SQLException
	 *             if processing the result set fails for some reason
	 */
	protected boolean handleResultSet(Collection<T> resultCollection, ResultSet resultSet) throws SQLException {
		
		boolean oneElementProcessed = false;
		
		while (resultSet.next()) {
			
			if (!forEachResult(resultCollection, resultSet)) {
				break;
			}
			
			oneElementProcessed = true;
		}
		
		return oneElementProcessed;
	}
	
	/**
	 * Template method for extracting an object of type T from a single result
	 * set row and adding it to the collection; subclasses should override.
	 * 
	 * @param resultCollection
	 *            the collection to add the result object to
	 *            
	 * @param resultSet
	 *            the result set returned by the query
	 *            
	 * @return <code>true</code> if processing of result set rows should
	 *         continue, <code>false</code> otherwise
	 *         
	 * @throws SQLException
	 *             if processing the result set row fails for some reason
	 */
	protected abstract boolean forEachResult(Collection<T> resultCollection, ResultSet resultSet) throws SQLException;
	
	/**
	 * Callback method, invoked after collection of query result objects is finished.
	 */
	protected void afterProcessed() {
		// Empty implementation
	}
	
	/**
	 * Callback method, invoked in case none of the result set rows has been
	 * converted to an object, or the returned result set was empty.
	 */
	protected void onZeroResultsProcessed() {
		// Empty implementation
	}
	
	/**
	 * Releases resources associated with the query collector's prepared
	 * statement.
	 */
	public void close() {
		JdbcUtils.close(statement);
	}
}