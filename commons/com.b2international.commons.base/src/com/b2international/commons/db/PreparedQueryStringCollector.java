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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

/**
 * Executes a SQL that is expected to return a single column of strings, and
 * places the results in a string collection. Operations (including the
 * constructor) may throw {@link RuntimeSQLException} to indicate failure
 * related to the database.
 * 
 */
public class PreparedQueryStringCollector extends PreparedQueryCollector<String> {

	/**
	 * Creates a new prepared query string collector instance.
	 * 
	 * @param connection
	 *            the database connection to use
	 *            
	 * @param sql
	 *            the SQL statement, with zero or more '?' parameter
	 *            placeholders
	 */
	public PreparedQueryStringCollector(Connection connection, String sql) {
		super(connection, sql);
	}

	@Override
	protected boolean forEachResult(Collection<String> resultCollection, ResultSet resultSet) throws SQLException {
		resultCollection.add(resultSet.getString(1));
		return true;
	}
}