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

import static com.b2international.commons.collections.CloseableMap.newCloseableMap;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;
import static java.lang.String.format;
import static org.eclipse.emf.cdo.common.id.CDOIDUtil.createLong;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import org.eclipse.emf.cdo.common.id.CDOID;

import com.b2international.commons.collections.CloseableMap;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.IHistoryInfo.IVersion;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.history.Version;
import com.google.common.base.Predicate;


/**
 * Base historical information query executor implementation.
 *
 */
public abstract class HistoryInfoQueryExecutorImpl implements HistoryInfoQueryExecutor {

	protected static final int DEFAULT_JOIN_NUMBER = 0;
	protected static final int FAKE_MINOR_VERSION = -1;
	protected static final int INITIAL_MAJOR_VERSION = 1;
	
	protected static final Predicate<IVersion<CDOID>> OTHER_COMPONENT_REGISTRATION_PREDICATE = //
		new Predicate<IVersion<CDOID>>() {
			public boolean apply(final IVersion<CDOID> version) {
				return 1 < version.getMajorVersion() 
					|| 0 != version.getMinorVersion();
			}
		};

	/**
	 * Returns with a multimap of statements that has to be executed for retrieving all the
	 * historical information for a terminology or content independent component.
	 * @param branchPath the branch path for the visibility when creating the queries.
	 * @param connection the connection for the backend.
	 * @return a map of prepared statements grouped by unique {@link PreparedStatementKey keys}.
	 * @throws SQLException if error occurred while preparing the statements.
	 */
	public CloseableMap<PreparedStatementKey, PreparedStatement> getPreparedStatements(
			final IBranchPath branchPath, final Connection connection) throws SQLException {
		
		checkNotNull(branchPath, "branchPath");
		checkNotNull(connection, "connection");
		
		return BranchPathUtils.isMain(branchPath) 
			? createStatementsForMain(connection) 
			: createStatementsForBranch(connection);
		
	}
	
	/**
	 * Collects and registers the primary component changes.
	 * @param configuration the configuration.
	 * @param modifications the to populate.
	 * @throws SQLException
	 */
	protected void collectPrimaryComponentChanges(final InternalHistoryInfoConfiguration configuration, 
			final SortedMap<Long, IVersion<CDOID>> modifications) throws SQLException {
		collectPrimaryComponentChanges(configuration, modifications, COMPONENT_CHANGES);
		
	}
	
	/**
	 * Collects and registers the primary component changes.
	 * @param configuration the configuration.
	 * @param modifications the to populate.
	 * @param statementKey the prepared statement key for the primary component.
	 * @throws SQLException
	 */
	protected void collectPrimaryComponentChanges(final InternalHistoryInfoConfiguration configuration, 
			final SortedMap<Long, IVersion<CDOID>> modifications, final PreparedStatementKey statementKey) throws SQLException {
		
		PreparedStatement statement = configuration.getPreparedStatements().get(statementKey);
		statement = adjustStatement(configuration, statement, DEFAULT_JOIN_NUMBER);
		
		try (final ResultSet rs = statement.executeQuery()) {
			final List<Object[]> info = newArrayList();
			while (rs.next()) {
				info.add(createPrimaryComponentInfo(rs));
			}
			
			registerPrimaryComponentModifications(configuration, modifications, info);
		}
		
	}
	
	/**
	 * Collects and registers any kind of other component changes as the part of
	 * the primary component changes.
	 * @param configuration the configuration.
	 * @param modifications the map of modifications to populate and update.
	 * @param statementKey the identifier of the statement to execute.
	 * @throws SQLException
	 */
	protected void collectOtherComponentChanges(final InternalHistoryInfoConfiguration configuration, 
			final SortedMap<Long, IVersion<CDOID>> modifications, final PreparedStatementKey statementKey) throws SQLException {
		collectOtherComponentChanges(configuration, modifications, statementKey, DEFAULT_JOIN_NUMBER);
	}
	
	/**
	 * Collects and registers any kind of other component changes as the part of
	 * the primary component changes.
	 * @param configuration the configuration.
	 * @param modifications the map of modifications to populate and update.
	 * @param statementKey the identifier of the statement to execute.
	 * @param numberOfJoins the number of the joins in the SQL query. 
	 * The statement will be adjusted based on this number. Should be {@code 0} or greater.
	 * @throws SQLException
	 */
	protected void collectOtherComponentChanges(final InternalHistoryInfoConfiguration configuration, 
			final SortedMap<Long, IVersion<CDOID>> modifications, final PreparedStatementKey statementKey, 
			final int numberOfJoins) throws SQLException {
		
		checkArgument(DEFAULT_JOIN_NUMBER <= numberOfJoins, "Number of joins argument cannot be a negative integer.");
		
		final PreparedStatement statement = configuration.getPreparedStatements().get(statementKey);
		collectOtherComponentChanges(configuration, modifications, adjustStatement(configuration, statement, numberOfJoins));

	}
	
	/**
	 * Collects and registers any kind of other component changes as the part of
	 * the primary component changes.
	 * @param configuration the configuration.
	 * @param modifications the map of modifications to populate and update.
	 * @param statement the statement to execute.
	 * @throws SQLException
	 */
	protected void collectOtherComponentChanges(final InternalHistoryInfoConfiguration configuration, 
			final SortedMap<Long, IVersion<CDOID>> modifications, final PreparedStatement statement) throws SQLException {
		
		try (final ResultSet rs = statement.executeQuery()) {
			final List<Object[]> info = newArrayList();
			while (rs.next()) {
				info.add(new Object[] {
						rs.getObject(1), //CDO ID 
						rs.getObject(2), //created timestamp
						rs.getObject(3), //revised timestamp
						rs.getObject(4) //branch ID
					});
			}
			
			registerOtherComponentModifications(modifications, info, configuration);
		}

	}
	
	/**
	 * Returns with a multimap of statements that has to be executed for retrieving all the
	 * historical information for a terminology or content independent component when caller is
	 * on the {@link IBranchPath#MAIN_BRANCH MAIN} branch.
	 * @param connection the connection for the backend.
	 * @return a map of prepared statements grouped by unique {@link PreparedStatementKey keys}.
	 * @throws SQLException if error occurred while preparing the statements.
	 */
	protected CloseableMap<PreparedStatementKey, PreparedStatement> createStatementsForMain(
			final Connection connection) throws SQLException {
		
		final CloseableMap<PreparedStatementKey, PreparedStatement> statements = newCloseableMap();
		final String query = format(COMPONENT_CHANGES_ON_MAIN_TEMPLATE, getPrimaryComponentTableName());
		statements.put(COMPONENT_CHANGES, connection.prepareStatement(query));
		
		return statements;
	}
	
	/**
	 * Returns with a multimap of statements that has to be executed for retrieving all the
	 * historical information for a terminology or content independent component when caller is *NOT*
	 * on the {@link IBranchPath#MAIN_BRANCH MAIN} branch.
	 * @param connection the connection for the backend.
	 * @return a map of prepared statements grouped by unique {@link PreparedStatementKey keys}.
	 * @throws SQLException if error occurred while preparing the statements.
	 */
	protected CloseableMap<PreparedStatementKey, PreparedStatement> createStatementsForBranch(
			final Connection connection) throws SQLException {
		
		final CloseableMap<PreparedStatementKey, PreparedStatement> statements = newCloseableMap();
		final String query = format(COMPONENT_CHANGES_ON_BRANCH_TEMPLATE, getPrimaryComponentTableName());
		statements.put(COMPONENT_CHANGES, connection.prepareStatement(query));
		
		return statements;
	}

	/**
	 * Returns with the table name for the primary component.
	 * <p>NOTE: clients must implement this method if 
	 * {@link #createStatementsForMain(Connection)} and/or 
	 * {@link #createStatementsForBranch(Connection)} is not overridden.
	 * @return
	 */
	protected String getPrimaryComponentTableName() {
		throw new UnsupportedOperationException("Implementation error."
				+ "\nClients must implement this method.");
	}
	
	/**
	 * Returns {@code true} if the configuration is adjusted for the {@link IBranchPath#MAIN_BRANCH MAIN}
	 * branch. Otherwise returns with {@code false}.
	 * @param configuration the configuration.
	 * @return {@code true} if MAIN branch. Otherwise {@code false}.
	 */
	protected boolean isMain(final InternalHistoryInfoConfiguration configuration) {
		return BranchPathUtils.isMain(configuration.getBranchPath());
	}
	
	/**
	 * Adjusts the prepared statement based on the configuration and returns with the
	 * adjusted argument.
	 * @param configuration the configuration.
	 * @param statementToAdjust the statement to adjust.
	 * @param numberOfJoins the number of the joins in the SQL query. 
	 * The statement will be adjusted based on this number. Should be {@code 0} or greater.
	 * @return the adjusted argument instance.
	 * @throws SQLException
	 */
	protected PreparedStatement adjustStatement(final InternalHistoryInfoConfiguration configuration, 
			final PreparedStatement statementToAdjust, final int numberOfJoins) throws SQLException {
		
		int parameterIndex = 1;
		
		statementToAdjust.setLong(parameterIndex++, configuration.getStorageKey());
		if (!isMain(configuration)) {
			for (int i = 0; i <= numberOfJoins; i++) {
				statementToAdjust.setInt(parameterIndex++, configuration.getBranchId());
				statementToAdjust.setInt(parameterIndex++, configuration.getBranchId());
				statementToAdjust.setLong(parameterIndex++, configuration.getBaseBranchTimestamp());
			}
		}
		return statementToAdjust;
	}
	
	/**
	 * Adjusts the minor versions. 
	 * @param modifications the map of modifications to adjust.
	 */
	protected void adjustMinorVersions(final Map<Long, ? extends IVersion<CDOID>> modifications) {
		int subVersion = 1;
		for (final IVersion<CDOID> version : modifications.values()) { 
			if (((Version) version).representsMajorChange()) {
				subVersion = 1;
			} else {
				((Version) version).setMinorVersion(subVersion++);
			}
		}		
	}
	
	/**
	 * Registers the primary component modifications by updating the state of the modification argument.
	 * @param configuration the configuration for the query.
	 * @param modifications the map of modifications.
	 * @param info the list of component information to process and register any relevant changes for
	 * the primary component.
	 */
	protected void registerPrimaryComponentModifications(final InternalHistoryInfoConfiguration configuration, 
			final SortedMap<Long, IVersion<CDOID>> modifications, final List<Object[]> info) {
		
		int majorVersion = INITIAL_MAJOR_VERSION; 
		
		for (final Object[] objectInfo : info) {
			final long timestamp = (long) objectInfo[0];
			final Version version = new Version(majorVersion++);
			version.addAffectedObjectId(configuration.getCdoId(), timestamp);
			modifications.put(timestamp, version);
		}
	}

	/**
	 * Creates and returns with an array of primary component information extracted from the result set.
	 * @param rs the result set.
	 * @return an array of component information.
	 * @throws SQLException
	 */
	protected Object[] createPrimaryComponentInfo(final ResultSet rs) throws SQLException {
		return new Object[] { rs.getLong(1) };
	}
	
	/**
	 * Registers additional modifications as the modification of the primary component. 
	 * @param modifications the other modifications that has to be registered for the 
	 * primary component.
	 * @param info a list of CDOIDs, timestamps and CDO branch IDs.
	 * @param configuration the configuration for the historical information query,
	 */
	protected void registerOtherComponentModifications(final SortedMap<Long, IVersion<CDOID>> modifications, 
			final List<Object[]> info, final InternalHistoryInfoConfiguration configuration) {
		
		for (final Object[] result : info) {
			final long affectedId = (Long) result[0];
			final long createdTimestamp = (Long) result[1];
			final long revisedTimestamp = (Long) result[2];
			final int branchId = (Integer) result[3];

			registerOtherComponentModifications(modifications, affectedId, createdTimestamp);
			if (isRevised(revisedTimestamp)
					&& isRelevantChange(configuration, revisedTimestamp, branchId)) {
				registerOtherComponentModifications(modifications, affectedId, revisedTimestamp + 1);
			}
		}
		
	}

	private boolean isRelevantChange(final InternalHistoryInfoConfiguration configuration, 
			final long revisedTimestamp, final int branchId) {
		
		return isSameBranch(configuration, branchId) 
				|| isModifiedOnAncestorBranch(configuration, revisedTimestamp);
	}

	private boolean isModifiedOnAncestorBranch(final InternalHistoryInfoConfiguration 
			configuration, final long revisedTimestamp) {
		
		return configuration.getBaseBranchTimestamp() > revisedTimestamp + 1;
	}

	private void registerOtherComponentModifications(final SortedMap<Long, IVersion<CDOID>> allModification, 
			final long affectedId, final long createdTimestamp) {
		
		Version versionForTimestamp = (Version) allModification.get(createdTimestamp);

		if (versionForTimestamp == null) {
			final Version previousVersion = (Version) allModification.get(allModification.headMap(createdTimestamp).lastKey());
			final int majorVersion = previousVersion.getMajorVersion();
			versionForTimestamp = new Version(majorVersion, FAKE_MINOR_VERSION); // fix minor versions at the end
			allModification.put(createdTimestamp, versionForTimestamp);
		}

		if (canRegisterAffectedObjectId(versionForTimestamp)) {
			versionForTimestamp.addAffectedObjectId(createLong(affectedId), createdTimestamp);
		}
		
	}
	
	protected boolean canRegisterAffectedObjectId(final Version version) {
		return true;
	}

	private boolean isSameBranch(final InternalHistoryInfoConfiguration configuration, final int branchId) {
		return configuration.getBranchId() == branchId;
	}

	private boolean isRevised(final long revisedTimestamp) {
		return revisedTimestamp != 0;
	}
	
}