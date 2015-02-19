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
 *
 */
public abstract class DatastoreQueries {

	public static final String SQL_GET_TIME_STAMP_BY_BRANCH_ID = "SELECT MAX(COMMIT_TIME) "
			+ "FROM CDO_COMMIT_INFOS "
			+ "WHERE BRANCH_ID = :branchId ";
	
	public static final String SQL_TABLE_EXISTS = "SELECT TABLE_NAME "
			+ "FROM INFORMATION_SCHEMA.TABLES "
			+ "WHERE TABLE_NAME = :tableName "
			+ "LIMIT 1 ";
	
	/**
	 * <ol>
	 * <li>Commit timestamp.</li>
	 * <li>Branch ID.</li>
	 * </ol>
	 */
	public static final String SQL_GET_COMMIT_INFO_DATA = "SELECT USER_ID, COMMIT_COMMENT "
			+ "FROM CDO_COMMIT_INFOS "
			+ "WHERE COMMIT_TIME = ? "
			+ "AND (BRANCH_ID = 0 OR BRANCH_ID = ?) "
			+ "LIMIT 1 "; // COMMIT_TIME has a unique index, so we're only interested in at most one result

	public static final String SQL_GET_INDEX_AND_BRANCH_FOR_VALUE = "SELECT r.CDO_IDX, r.CDO_BRANCH "
			+ "FROM {0} r "
			+ "WHERE r.CDO_VALUE = :cdoId "
			+ "AND (CDO_VERSION_REMOVED IS NULL OR CDO_VERSION_ADDED <= :versionMaxAdded) ";

	private DatastoreQueries() {
		// Prevent instantiation
	}
}