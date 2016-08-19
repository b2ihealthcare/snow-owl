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
package com.b2international.snowowl.datastore;

/**
 * A collection of SQL queries used in Snow Owl's common datastore plug-in. 
 */
public enum DatastoreQueries {

	/**
	 * Query parameters:
	 * <ol>
	 * <li>Commit timestamp</li>
	 * </ol>
	 */
	SQL_GET_COMMIT_INFO_DATA("SELECT "
			+ "USER_ID, "
			+ "COMMIT_COMMENT "
			+ "FROM CDO_COMMIT_INFOS "
			+ "WHERE COMMIT_TIME = ? "
			+ "LIMIT 1 "),

	SQL_GET_INDEX_AND_BRANCH_FOR_VALUE("SELECT "
			+ "r.CDO_IDX, "
			+ "r.CDO_BRANCH "
			+ "FROM %s r "
			+ "WHERE r.CDO_VALUE = :cdoId "
			+ "AND r.CDO_SOURCE = :containerId "
			+ "AND (CDO_VERSION_REMOVED IS NULL OR CDO_VERSION_ADDED <= :versionMaxAdded) ");

	private final String query;

	private DatastoreQueries(final String query) {
		this.query = query;
	}
	
	public String getQuery() {
		return query;
	}
	
	public String getQuery(final String tableName) {
		return String.format(query, tableName);
	}
}
