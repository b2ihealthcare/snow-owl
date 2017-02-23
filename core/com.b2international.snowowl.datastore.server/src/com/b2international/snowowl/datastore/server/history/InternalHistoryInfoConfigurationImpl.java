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

import static com.b2international.snowowl.datastore.cdo.CDOUtils.check;
import static com.google.common.base.Preconditions.checkNotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.view.CDOView;

import com.b2international.commons.collections.CloseableMap;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.datastore.history.HistoryInfoConfiguration;
import com.b2international.snowowl.datastore.history.StorageKeyCache;

/**
 * Low-level historical information configuration implementation.
 *
 */
public class InternalHistoryInfoConfigurationImpl implements InternalHistoryInfoConfiguration {

	private final HistoryInfoConfiguration configuration;
	private final Connection connection;
	private final long baseBranchTimestamp;
	private final int branchId;
	private final CloseableMap<PreparedStatementKey, PreparedStatement> statements;
	private final CDOView view;
	private final CDOID cdoId;

	public InternalHistoryInfoConfigurationImpl(final HistoryInfoConfiguration configuration, 
			final Connection connection, final CDOView view) throws SQLException {

		this(
			checkNotNull(configuration, "configuration"), 
			checkNotNull(connection, "connection"), 
			getBaseBranchTimestamp(check(view)), 
			getBranchId(view),
			view
			);
	}

	public InternalHistoryInfoConfigurationImpl(final HistoryInfoConfiguration configuration, 
			final Connection connection, final long baseBranchTimestamp, final int branchId, final CDOView view) throws SQLException {
		
		this.configuration = checkNotNull(configuration, "configuration");
		this.connection = checkNotNull(connection, connection);
		this.baseBranchTimestamp = baseBranchTimestamp;
		this.branchId = branchId;
		this.view = check(view);
		
		final HistoryInfoQueryExecutor executor = getExecutor();
		this.statements = executor instanceof HistoryInfoQueryExecutorImpl 
				? tryGetStatements(executor) 
				: new CloseableMap<PreparedStatementKey, PreparedStatement>();
		cdoId = CDOIDUtil.createLong(this.configuration.getStorageKey());
		
	}

	@Override
	public long getStorageKey() {
		return configuration.getStorageKey();
	}

	@Override
	public String getComponentId() {
		return configuration.getComponentId();
	}

	@Override
	public String getTerminologyComponentId() {
		return configuration.getTerminologyComponentId();
	}

	@Override
	public IBranchPath getBranchPath() {
		return configuration.getBranchPath();
	}

	@Override
	public Connection getConnection() {
		return connection;
	}

	@Override
	public CloseableMap<PreparedStatementKey, PreparedStatement> getPreparedStatements() {
		return statements;
	}

	@Override
	public int getBranchId() {
		return branchId;
	}

	@Override
	public long getBaseBranchTimestamp() {
		return baseBranchTimestamp;
	}
	
	@Override
	public CDOView getView() {
		return view;
	}

	@Override
	public CDOID getCdoId() {
		return cdoId;
	}
	
	@Override
	public void close() throws SQLException {
		try {
			statements.close();
		} catch (final Exception e) {
			if (e instanceof SQLException) {
				throw (SQLException) e;
			} else {
				throw new SnowowlRuntimeException("Unexpected exception when closing prepared statements.", e);
			}
		}
	}

	private HistoryInfoQueryExecutor getExecutor() {
		return HistoryInfoQueryExecutorProvider.INSTANCE.getExecutor(getTerminologyComponentId());
	}
	
	private CloseableMap<PreparedStatementKey, PreparedStatement> tryGetStatements(final HistoryInfoQueryExecutor executor) throws SQLException {
		return ((HistoryInfoQueryExecutorImpl) executor).getPreparedStatements(connection);
	}
	
	private static int getBranchId(final CDOView view) {
		return check(view).getBranch().getID();
	}

	private static long getBaseBranchTimestamp(final CDOView view) {
		return check(view).getBranch().getBase().getTimeStamp();
	}

	@Override
	public StorageKeyCache getStorageKeyCache() {
		return configuration.getStorageKeyCache();
	}

}