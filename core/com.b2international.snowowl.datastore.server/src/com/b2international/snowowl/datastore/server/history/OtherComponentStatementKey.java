/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.Objects;

import org.eclipse.emf.ecore.EClass;

import com.b2international.snowowl.datastore.cdo.LocalDbUtils;

/**
 * Minimal implementation of {@link PreparedStatementKey} that is identified by the EClass 
 * it is queried against.
 */
public final class OtherComponentStatementKey implements PreparedStatementKey {

	private static final String QUERY_TEMPLATE = "SELECT "
			+ "component.CDO_ID, " // Redundant, only returned to be compatible with all other "other component" queries
			+ "component.CDO_CREATED, "
			+ "component.CDO_REVISED "
			// -------------------------------
			+ "FROM %s component "
			// -------------------------------
			+ "WHERE component.CDO_ID = ? " 
			+ "AND component.CDO_BRANCH = ? "
			+ "AND component.CDO_CREATED <= ? "
			+ "ORDER BY component.CDO_CREATED DESC ";
	
	private final EClass eClass;

	public OtherComponentStatementKey(final EClass eClass) {
		this.eClass = eClass;
	}

	public String getQuery() {
		final String tableName = LocalDbUtils.tableNameFor(eClass);
		return String.format(QUERY_TEMPLATE, tableName);
	}

	@Override
	public int hashCode() {
		return Objects.hash(eClass);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) { return true; }
		if (obj == null) { return false; }
		if (getClass() != obj.getClass()) { return false; }

		final OtherComponentStatementKey other = (OtherComponentStatementKey) obj;
		return Objects.equals(eClass, other.eClass);
	}
}
