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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides utility methods for issuing database queries and updates using the
 * JDBC API.
 * <p>
 * Where not otherwise specified, caught {@link SQLException} instances will be
 * wrapped into {@link RuntimeSQLException}s and then re-thrown.
 * 
 */
public class JdbcUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger(JdbcUtils.class);
	
    /** The class name of the H2 JDBC driver */ 
    public static final String JDBC_DRIVER_H2 = "org.h2.Driver";
	
	/**
	 * Opens and initializes a connection to a database with the specified
	 * parameters.
	 * <p>
	 * The method triggers JDBC driver class loading by invoking the given
	 * class' parameterless constructor reflectively, then uses
	 * {@link DriverManager} to return a database connection. A
	 * {@link RuntimeException} will be thrown in case the driver class is not
	 * available, or if its constructor is not accessible or throws an exception
	 * upon invocation.
	 * 
	 * @param driverClassName
	 *            the name of the JDBC driver to use
	 * 
	 * @param url
	 *            the JDBC database URL, eg. <code>jdbc:h2:mem:test</code>
	 * 
	 * @param username
	 *            the database user name for the connection
	 * 
	 * @param password
	 *            the connection password
	 * 
	 * @return an initialized {@link Connection} instance
	 */
	public static Connection createConnection(final String driverClassName, final String url, final String username, final String password) {
		
		try {
			
			Class.forName(driverClassName)
				.getDeclaredConstructor()
				.newInstance();
			
		} catch (final Exception e) {
			/*
			 * XXX: newInstance() propagates all exception (also the checked
			 * ones) from the invoked constructor, we have to catch and wrap all
			 * exceptions here
			 */
			throw new RuntimeException("An error occurred while initializing JDBC driver '"
							+ driverClassName + "'.", e);
		}

		Connection connection = null;

		try {
			
			connection = DriverManager.getConnection(url, username, password);
			connection.setAutoCommit(false);
			connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
			
		} catch (final SQLException e) {
			
			close(connection); // In case the actual connection succeeded but the initialization didn't
			throw new RuntimeSQLException("Couldn't initialize connection to database '" + url + "'.", e);
		}

		LOGGER.info("Connected to database '" + url + "'.");
		return connection;
	}

	/**
	 * Closes a database connection silently; issues a warning for any SQL
	 * exception that occurs during closing.
	 * 
	 * @param connection
	 *            the connection to close (may be <code>null</code> or an
	 *            already closed connection)
	 */
	public static void close(final Connection connection) {
		
		if (connection == null) {
			return;
		}
		
		try {
			connection.close();
		} catch (final SQLException e) {
			LOGGER.warn("Caught exception while closing connection '" + connection + "', ignoring.", e);
		}
	}	

	/**
	 * Closes the specified statement silently; issues a warning for any SQL
	 * exception that occurs during closing.
	 * <p>
	 * If the given statement has currently a {@link ResultSet} open, it will be
	 * closed as well.
	 * 
	 * @param statement
	 *            the statement to close (may be <code>null</code> or an already
	 *            closed statement)
	 */
	public static void close(final Statement statement) {

		if (statement == null) {
			return;
		}
		
		try {
			statement.close();
		} catch (final SQLException e) {
			LOGGER.warn("Caught exception while closing statement '" + statement + "', ignoring.", e);
		}
	}

	/**
	 * Closes the given result set silently; issues a warning for any SQL
	 * exception that occurs during closing.
	 * 
	 * @param resultSet
	 *            the result set to close (may be <code>null</code> or a result
	 *            set which is already closed)
	 */
	public static void close(final ResultSet resultSet) {
		
		if (resultSet == null) {
			return;
		}
		
		try {
			resultSet.close();
		} catch (final SQLException e) {
			LOGGER.warn("Caught exception while closing result set '" + resultSet + "', ignoring.", e);
		}
	}

	/**
	 * Commits the current transaction represented by the specified connection
	 * instance. If the connection is set to auto-commit mode, this operation
	 * does nothing. 
	 * 
	 * @param connection
	 *            the connection linked to the transaction to commit
	 */
	public static void commit(final Connection connection) {
		
		try {
			
			if (!connection.getAutoCommit()) {
				connection.commit();
			}
			
		} catch (final SQLException e) {
			
			throw new RuntimeSQLException("Couldn't commit transaction on connection '" 
					+ connection + "'.", e);
		}
	}

	/**
	 * Prepares a (possibly parameterized) SQL statement and populates it with
	 * the given parameters, using the JDBC standard mapping of Java objects.
	 * 
	 * @param connection
	 *            the database connection to use
	 *            
	 * @param sql
	 *            the SQL statement text, containing zero or more '?'
	 *            placeholders
	 *            
	 * @param parameters
	 *            the variable number of statement parameters
	 * 
	 * @return an initialized {@link PreparedStatement} instance
	 */
	public static PreparedStatement prepareStatement(final Connection connection, final String sql, final Object... parameters) {
		
		PreparedStatement statement;
		
		try {
			statement = connection.prepareStatement(sql);
		} catch (final SQLException e) {
			throw new RuntimeSQLException("Couldn't prepare statement for query '" + sql + "'.", e);
		}
		
		try {
			
			for (int i = 0; i < parameters.length; i++) {
				statement.setObject(i + 1, parameters[i]);
			}
		
		} catch (final SQLException e) {
			close(statement);
			throw new RuntimeSQLException("Couldn't set parameter for query '" + sql + "'.", e);
		}
		
		return statement;
	}
	
	/**
	 * Initializes and executes the specified statement (typically an INSERT,
	 * UPDATE, or DELETE statement, or a DDL statement that modifies database
	 * structure).
	 * 
	 * @param connection
	 *            the connection to use
	 *            
	 * @param sql
	 *            the SQL statement text
	 *            
	 * @param parameters
	 *            the variable number of statement parameters
	 *            
	 * @return the affected row count
	 */
	public static int executeUpdate(final Connection connection, final String sql, final Object... parameters) {
		
		PreparedStatement statement = null;
		
		try {
			
			statement = prepareStatement(connection, sql, parameters);
			final int updateCount = statement.executeUpdate();
			return updateCount;
			
		} catch (final SQLException e) {
			throw new RuntimeSQLException("Couldn't execute update for statement '" + sql + "'.", e);
		} finally {
			JdbcUtils.close(statement);
		}
	}
	
	/**
	 * Executes a query that is expected to return a single integer value.
	 * 
	 * @param connection
	 *            the connection to use
	 *            
	 * @param sql
	 *            the SQL statement text
	 *            
	 * @param parameters
	 *            the variable number of statement parameters
	 *            
	 * @return the integer value of the first column of the first result set row,
	 *         or <code>null</code> if an empty result set is returned from the
	 *         query
	 */
	public static Integer executeIntQuery(final Connection connection, final String sql, final Object... parameters) {
		
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		
		try {
			
			statement = prepareStatement(connection, sql, parameters);
			resultSet = statement.executeQuery();
			
			if (resultSet.next()) {
				final int value = resultSet.getInt(1);
				return value;
			} else {
				return null;
			}
			
		} catch (final SQLException e) {
			throw new RuntimeSQLException("Couldn't execute Integer query for statement '" + sql + "'.", e);
		} finally {
			JdbcUtils.close(resultSet);
			JdbcUtils.close(statement);
		}			
	}
	
	/**
	 * Executes a query that is expected to return a single long value.
	 * 
	 * @param connection
	 *            the connection to use
	 *            
	 * @param sql
	 *            the SQL statement text
	 *            
	 * @param parameters
	 *            the variable number of statement parameters
	 *            
	 * @return the long value of the first column of the first result set row,
	 *         or <code>null</code> if an empty result set is returned from the
	 *         query
	 */	
	public static Long executeLongQuery(final Connection connection, final String sql, final Object... parameters) {
		
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		
		try {
			
			statement = prepareStatement(connection, sql, parameters);
			resultSet = statement.executeQuery();
			
			if (resultSet.next()) {
				final long value = resultSet.getLong(1);
				return value;
			} else {
				return null;
			}
			
		} catch (final SQLException e) {
			throw new RuntimeSQLException("Couldn't execute Long query for statement '" + sql + "'.", e);
		} finally {
			JdbcUtils.close(resultSet);
			JdbcUtils.close(statement);
		}			
	}
	
	/**
	 * Executes a query that is expected to return a single string.
	 * 
	 * @param connection
	 *            the connection to use
	 *            
	 * @param sql
	 *            the SQL statement text
	 *            
	 * @param parameters
	 *            the variable number of statement parameters
	 *            
	 * @return the string value of the first column of the first result set row,
	 *         or <code>null</code> if an empty result set is returned from the
	 *         query
	 */
	public static String executeStringQuery(final Connection connection, final String sql, final Object... parameters) {
		
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		
		try {
			
			statement = prepareStatement(connection, sql, parameters);
			resultSet = statement.executeQuery();
			
			if (resultSet.next()) {
				final String value = resultSet.getString(1);
				return value;
			} else {
				return null;
			}
			
		} catch (final SQLException e) {
			throw new RuntimeSQLException("Couldn't execute String query for statement '" + sql + "'.", e);
		} finally {
			JdbcUtils.close(resultSet);
			JdbcUtils.close(statement);
		}			
	}
	
	/**
	 * Executes a query that is expected to return a single boolean value.
	 * 
	 * @param connection
	 *            the connection to use
	 *            
	 * @param sql
	 *            the SQL statement text
	 *            
	 * @param parameters
	 *            the variable number of statement parameters
	 *            
	 * @return the boolean value of the first column of the first result set row,
	 *         or <code>null</code> if an empty result set is returned from the
	 *         query
	 */
	public static Boolean executeBooleanQuery(final Connection connection, final String sql, final Object... parameters) {
		
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		
		try {
			
			statement = prepareStatement(connection, sql, parameters);
			resultSet = statement.executeQuery();
			
			if (resultSet.next()) {
				final boolean value = resultSet.getBoolean(1);
				return value;
			} else {
				return null;
			}
			
		} catch (final SQLException e) {
			throw new RuntimeSQLException("Couldn't execute Boolean query for statement '" + sql + "'.", e);
		} finally {
			JdbcUtils.close(resultSet);
			JdbcUtils.close(statement);
		}			
	}
}
