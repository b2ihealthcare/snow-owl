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
package com.b2international.snowowl.datastore.server.history;

/**
 * Enumerates commonly used terminology component SQL queries for determining timestamps when the focused component
 * changed.
 */
public enum ComponentHistoryQueries implements PreparedStatementKey {
	
	/**
	 * Query parameters:
	 * <ol>
	 * <li>CDO ID of the concept</li>
	 * <li>Current CDO branch ID</li>
	 * <li>Ending timestamp for the current CDO branch segment ("infinity" or base of child branch)</li>
	 * <ol>
	 */
	COMPONENT_CHANGES("SELECT "
			+ "component.CDO_CREATED "
			// -------------------------------
			+ "FROM %s component "
			// -------------------------------
			+ "WHERE component.CDO_ID = ? " 
			+ "AND component.CDO_BRANCH = ? "
			+ "AND component.CDO_CREATED <= ? "
			+ "ORDER BY component.CDO_CREATED DESC ");
	
	private final String queryTemplate;
	
	private ComponentHistoryQueries(final String queryTemplate) {
		this.queryTemplate = queryTemplate;
	}
	
	public String getQuery(final String tableName) {
		return String.format(queryTemplate, tableName);
	}
}
