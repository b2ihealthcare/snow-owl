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
package com.b2international.snowowl.snomed.exporter.server;

/**
 * Contains common query suffixes for reference set and core terminology
 * component exporters.
 * 
 */
public abstract class SnomedCommonExportQueries {

	public static final String MEMBER_DELTA_DECORATOR = "AND CDO_SET_EFFECTIVETIME = FALSE ";
	
	public static final String MEMBER_ORDER_DECORATOR = "ORDER BY member.CDO_BRANCH desc, member.CDO_ID, abs(member.CDO_VERSION) DESC "; 
	
	public static final String MEMBER_ON_MAIN_BRANCH_SUFFIX = "AND member.CDO_REVISED = 0 "
			+ "AND member.CDO_BRANCH = 0 ";
	
	public static final String MEMBER_ON_MAIN_BRANCH_FULL_SUFFIX = "AND member.CDO_BRANCH = 0 ";
	
	// %s = component table name
	public static final String MEMBER_ON_TASK_BRANCH_SUFFIX = "AND (member.CDO_BRANCH = 0 OR member.CDO_BRANCH = ?) " // extracted so that the optimizer can use it in indexes
			+ "AND member.CDO_BRANCH = ("
			+ "SELECT MAX(branch.CDO_BRANCH) FROM %s branch "
			+ "WHERE member.CDO_ID = branch.CDO_ID AND (branch.CDO_BRANCH = 0 OR branch.CDO_BRANCH = ?)"
			+ ") AND ("
			+ "(member.CDO_BRANCH = 0 AND member.CDO_CREATED <= ? AND (member.CDO_REVISED = 0 OR member.CDO_REVISED >= ?)) " // on MAIN, before branch point, current or updated after branch point
			+ "OR "
			+ "(member.CDO_BRANCH = ? AND member.CDO_REVISED = 0) " // on task branch, current
			+ ") ";
	
	// %s = component table name
	public static final String MEMBER_ON_TASK_BRANCH_FULL_SUFFIX = "AND (member.CDO_BRANCH = 0 OR member.CDO_BRANCH = ?) " // extracted so that the optimizer can use it in indexes
			+ "AND member.CDO_BRANCH = ("
			+ "SELECT MAX(branch.CDO_BRANCH) FROM %s branch "
			+ "WHERE member.CDO_ID = branch.CDO_ID AND (branch.CDO_BRANCH = 0 OR branch.CDO_BRANCH = ?)"
			+ ") AND ("
			// on MAIN, before branch point, doesn't matter if it was revised or not
			// (we have member.CDO_CREATED <= ? in there twice because of fixed parameter binding)
			+ "(member.CDO_BRANCH = 0 AND member.CDO_CREATED <= ? AND member.CDO_CREATED <= ?) " 
			+ "OR "
			+ "(member.CDO_BRANCH = ?) " // on task branch, doesn't matter if it was revised or not
			+ ") ";

	private SnomedCommonExportQueries() {
		// Prevent instantiation
	}
}