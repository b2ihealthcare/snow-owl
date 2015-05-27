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
package com.b2international.snowowl.datastore.server.snomed.history;

/**
 * Contains SQL queries used for populating the history page of SNOMED CT reference sets. 
 */
public enum SnomedRefSetHistoryQueries {

	/**
	 * Query parameters:
	 * <ol>
	 * <li>CDO ID of the reference set</li>
	 * <li>Current CDO branch ID</li>
	 * <li>Ending timestamp for the current CDO branch segment ("infinity" or base of child branch)</li>
	 * <ol>
	 */
	REFSET_CHANGES("SELECT "
			+ "refset.CDO_CREATED "
			// -------------------------------
			+ "FROM %s refset "
			// -------------------------------
			+ "WHERE refset.CDO_ID = ? "
			+ "AND refset.CDO_BRANCH = ? "
			+ "AND refset.CDO_CREATED <= ? "
			+ "ORDER BY refset.CDO_CREATED "),
	
	/**
	 * Query parameters:
	 * <ol>
	 * <li>CDO ID of the reference set</li>
	 * <li>Current CDO branch ID</li>
	 * <li>Ending timestamp for the current CDO branch segment ("infinity" or base of child branch)</li>
	 * <ol>
	 */
	REFSET_MEMBER_CHANGES("SELECT "
			+ "member.CDO_ID, "
			+ "member.CDO_CREATED, "
			+ "member.CDO_REVISED "
			// -------------------------------
			+ "FROM %s member "
			// -------------------------------
			+ "WHERE member.REFSET = ? "
			+ "AND member.CDO_BRANCH = ? "
			+ "AND member.CDO_CREATED <= ? ");

	private final String queryTemplate;
	
	private SnomedRefSetHistoryQueries(final String queryTemplate) {
		this.queryTemplate = queryTemplate;
	}
	
	public String getQuery(final String tableName) {
		return String.format(queryTemplate, tableName);
	}
}
