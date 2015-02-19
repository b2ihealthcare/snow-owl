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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.view.CDOView;

import com.b2international.commons.collections.CloseableMap;
import com.b2international.snowowl.datastore.history.HistoryInfoConfiguration;

/**
 * Low-level representation of a historical information configuration used for
 * running a query.
 *
 */
public interface InternalHistoryInfoConfiguration extends HistoryInfoConfiguration, AutoCloseable {

	/**
	 * Returns with the connection for the underlying backend.
	 * @return the SQL connection for the underlying backend.
	 */
	Connection getConnection();
	
	/**
	 * Returns with a closeable map of statements that has to be executed for retrieving all the
	 * historical information for a terminology or content independent component.
	 * @return a map of prepared statements grouped by unique {@link PreparedStatementKey keys}.
	 */
	CloseableMap<PreparedStatementKey, PreparedStatement> getPreparedStatements();
	
	/**
	 * Returns with the repository-wise unique ID of the branch where the current
	 * query is being performed.
	 * @return the unique branch ID.
	 */
	int getBranchId();
	
	/**
	 * The base timestamp of the branch where the current query is being performed.
	 * The base of the {@link CDOBranch#MAIN_BRANCH_NAME MAIN} branch equals with
	 * the repository initialization time.
	 * @return the base timestamp of the MAIN branch.
	 */
	long getBaseBranchTimestamp();
	
	/**
	 * Returns with the CDO view for the query.
	 * @return the CDO view.
	 */
	CDOView getView();
	
	/**
	 * Sugar for getting {@link HistoryInfoConfiguration#getStorageKey()}.
	 * @return the storage key as a {@link CDOID} instance.
	 */
	CDOID getCdoId();
	
	@Override
	public void close() throws SQLException;
	
}