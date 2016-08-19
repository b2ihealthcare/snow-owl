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
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;
import static org.eclipse.emf.cdo.common.id.CDOIDUtil.createLong;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.collections.CloseableMap;
import com.b2international.snowowl.core.api.IHistoryInfo.IVersion;
import com.b2international.snowowl.datastore.history.Version;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;


/**
 * Base historical information query executor implementation.
 *
 */
public abstract class HistoryInfoQueryExecutorImpl implements HistoryInfoQueryExecutor {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(HistoryInfoQueryExecutorImpl.class);

	/** Minor version value, set when the actual minor version is not yet known. */
	protected static final int UNSPECIFIED_MINOR_VERSION = -1;
	
	/** The starting value for major versions. */
	protected static final int INITIAL_MAJOR_VERSION = 1;
	
	/**
	 * Returns a map of JDBC statements that have to be executed for retrieving all the historical information for a
	 * terminology component.
	 * 
	 * @param connection the database connection
	 * @return a map of prepared statements grouped by unique {@link PreparedStatementKey keys}
	 * 
	 * @throws SQLException if an error occurs while preparing statements
	 */
	public CloseableMap<PreparedStatementKey, PreparedStatement> getPreparedStatements(final Connection connection) throws SQLException {
		checkNotNull(connection, "Database connection may not be null.");
		return createPreparedStatements(connection);
	}
	
	protected CloseableMap<PreparedStatementKey, PreparedStatement> createPreparedStatements(final Connection connection) throws SQLException {
		final CloseableMap<PreparedStatementKey, PreparedStatement> statements = newCloseableMap();
		final String query = ComponentHistoryQueries.COMPONENT_CHANGES.getQuery(getPrimaryComponentTableName());
		statements.put(ComponentHistoryQueries.COMPONENT_CHANGES, connection.prepareStatement(query));
		return statements;
	}
	
	/**
	 * Returns the SQL table name for the primary component.
	 * <p>
	 * NOTE: clients must implement this method if {@link #createPreparedStatements(Connection)} is not overridden.
	 */
	protected String getPrimaryComponentTableName() {
		throw new AssertionError("Clients must implement this method if createPreparedStatements is not overridden.");
	}

	/**
	 * Collects and registers primary component changes.
	 * 
	 * @param configuration the history configuration
	 * @param modifications the modification map to populate
	 * 
	 * @throws SQLException
	 */
	protected void collectPrimaryComponentChanges(final InternalHistoryInfoConfiguration configuration, 
			final SortedMap<Long, IVersion<CDOID>> modifications) throws SQLException {
		
		collectPrimaryComponentChanges(configuration, modifications, ComponentHistoryQueries.COMPONENT_CHANGES);
	}
	
	/**
	 * Collects and registers primary component changes using the specified statement key.
	 * <p>
	 * The statement key is used to get the JDBC prepared statement, which will be executed after updating its parameters.
	 * 
	 * @param configuration the history configuration
	 * @param modifications the modification map to populate
	 * @param statementKey the prepared statement key for the primary component
	 * 
	 * @throws SQLException
	 */
	protected void collectPrimaryComponentChanges(final InternalHistoryInfoConfiguration configuration, 
			final SortedMap<Long, IVersion<CDOID>> modifications, 
			final PreparedStatementKey statementKey) throws SQLException {
		
		final PreparedStatement statement = configuration.getPreparedStatements().get(statementKey);
		final List<CDOBranchPoint> branchPoints = getBranchPoints(configuration);
		
		// Primary components are collected in a single list
		final List<Object[]> primaryComponentInfos = newArrayList();

		for (final CDOBranchPoint branchPoint : branchPoints) {
			setStorageKeyQueryParameters(statement, configuration.getStorageKey(), branchPoint.getBranch().getID(), branchPoint.getTimeStamp());
		
			try (final ResultSet rs = statement.executeQuery()) {
				while (rs.next()) {
					primaryComponentInfos.add(createPrimaryComponentInfo(rs));
				}
			}
		}
		
		registerPrimaryComponentModifications(configuration, modifications, primaryComponentInfos);
	}
	
	/**
	 * Collects and registers other component changes using the specified statement key.
	 * 
	 * @param configuration the history configuration
	 * @param modifications the modification map to populate
	 * @param statementKey the prepared statement key for the other components
	 * 
	 * @throws SQLException
	 */
	protected void collectOtherComponentChanges(final InternalHistoryInfoConfiguration configuration, 
			final SortedMap<Long, IVersion<CDOID>> modifications, 
			final PreparedStatementKey statementKey) throws SQLException {
		
		final PreparedStatement statement = configuration.getPreparedStatements().get(statementKey);
		final List<CDOBranchPoint> branchPoints = getBranchPoints(configuration);

		for (final CDOBranchPoint branchPoint : branchPoints) {

			// Other components are processed in segments
			final List<Object[]> otherComponentInfos = newArrayList();
			setStorageKeyQueryParameters(statement, configuration.getStorageKey(), branchPoint.getBranch().getID(), branchPoint.getTimeStamp());
		
			try (final ResultSet rs = statement.executeQuery()) {
				while (rs.next()) {
					otherComponentInfos.add(createOtherComponentInfo(rs));
				}
			}
			
			registerOtherComponentModifications(modifications, otherComponentInfos, branchPoint);
		}
	}

	protected List<CDOBranchPoint> getBranchPoints(final InternalHistoryInfoConfiguration configuration) {
		
		final CDOBranch currentBranch = configuration.getView().getBranch();
		final ImmutableList.Builder<CDOBranchPoint> branchPointsBuilder = ImmutableList.builder();
		
		final CDOBranchPoint[] basePath = currentBranch.getBasePath();
		for (int i = 1; i < basePath.length; i++) {
			branchPointsBuilder.add(basePath[i]);
		}
		
		branchPointsBuilder.add(currentBranch.getPoint(Long.MAX_VALUE));
		
		// Start with the branch in question, then work our way back
		return ImmutableList.copyOf(Lists.reverse(branchPointsBuilder.build()));
	}

	/**
	 * Populates the specified JDBC prepared statement's parameters from the given history configuration.
	 * 
	 * @param statement the statement to adjust
	 * @param storageKey the focused CDO object's identifier
	 * @param branchId the branch to run the query on
	 * @param maxCommitTimestamp the maximum permissible timestamp which should be considered on this branch segment
	 * 
	 * @throws SQLException
	 */
	protected void setStorageKeyQueryParameters(final PreparedStatement statement, 
			final long storageKey, 
			final int branchId, 
			final long maxCommitTimestamp) throws SQLException {
		
		statement.setLong(1, storageKey);
		statement.setInt(2, branchId);
		statement.setLong(3, maxCommitTimestamp);
	}
	
	/**
	 * Populates the specified JDBC prepared statement's parameters from the given history configuration.
	 * 
	 * @param statement the statement to adjust
	 * @param componentId the focused components's identifier
	 * @param branchId the branch to run the query on
	 * @param maxCommitTimestamp the maximum permissible timestamp which should be considered on this branch segment
	 * 
	 * @throws SQLException
	 */
	protected void setComponentIdQueryParameters(final PreparedStatement statement, 
			final String componentId, 
			final int branchId, 
			final long maxCommitTimestamp) throws SQLException {
		
		statement.setString(1, componentId);
		statement.setInt(2, branchId);
		statement.setLong(3, maxCommitTimestamp);
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
		
		int majorVersion = info.size(); 
		
		for (final Object[] objectInfo : info) {
			final long timestamp = (long) objectInfo[0];
			final Version version = new Version(majorVersion--);
			version.addAffectedObjectId(configuration.getCdoId(), timestamp);
			modifications.put(timestamp, version);
		}
	}

	protected Object[] createPrimaryComponentInfo(final ResultSet rs) throws SQLException {
		return new Object[] { 
				rs.getLong(1) // created timestamp 
		};
	}
	
	/**
	 * Registers additional modifications as the modification of the primary component.
	 * 
	 * @param modifications the map of modifications that has to be registered for the component
	 * @param info a list of CDOIDs, timestamps and CDO branch IDs
	 * @param branchPoint the branch point for the currently processed branch segment
	 */
	protected void registerOtherComponentModifications(final SortedMap<Long, IVersion<CDOID>> modifications, 
			final List<Object[]> info, 
			final CDOBranchPoint branchPoint) {
		
		for (final Object[] result : info) {
			final long affectedId = (Long) result[0];
			final long createdTimestamp = (Long) result[1];
			final long revisedTimestamp = (Long) result[2];

			registerOtherComponentModifications(modifications, affectedId, createdTimestamp);
			
			/*
			 * Since some of the queries don't return deleted revisions, set (revised timestamp + 1) as a relevant
			 * commit timestamp, if the update happened before the branch point.
			 */
			if (isRevised(revisedTimestamp) && isRelevantChange(revisedTimestamp, branchPoint)) {
				registerOtherComponentModifications(modifications, affectedId, revisedTimestamp + 1);
			}
		}
	}

	protected Object[] createOtherComponentInfo(final ResultSet rs) throws SQLException {
		return new Object[] {
				rs.getObject(1), //CDO ID 
				rs.getObject(2), //created timestamp
				rs.getObject(3), //revised timestamp
		};
	}

	private void registerOtherComponentModifications(final SortedMap<Long, IVersion<CDOID>> allModification, 
			final long affectedId, 
			final long createdTimestamp) {
		
		Version versionForTimestamp = (Version) allModification.get(createdTimestamp);

		if (versionForTimestamp == null) {
			final SortedMap<Long, IVersion<CDOID>> headMap = allModification.headMap(createdTimestamp);
			if (headMap.isEmpty()) {
				LOGGER.error("Created timestamp {} is smaller than first versions timestamp {}.", createdTimestamp, allModification.firstKey());
				return;
			}
			final Version previousVersion = (Version) allModification.get(headMap.lastKey());
			final int majorVersion = previousVersion.getMajorVersion();
			versionForTimestamp = new Version(majorVersion, UNSPECIFIED_MINOR_VERSION); // fix minor versions at the end
			allModification.put(createdTimestamp, versionForTimestamp);
		}

		if (canRegisterAffectedObjectId(versionForTimestamp)) {
			versionForTimestamp.addAffectedObjectId(createLong(affectedId), createdTimestamp);
		}
	}
	
	protected boolean canRegisterAffectedObjectId(final Version version) {
		return true;
	}

	private boolean isRevised(final long revisedTimestamp) {
		return revisedTimestamp != CDOBranchPoint.UNSPECIFIED_DATE;
	}
	
	private boolean isRelevantChange(final long revisedTimestamp, final CDOBranchPoint branchPoint) {
		return revisedTimestamp + 1 < branchPoint.getTimeStamp();
	}
}
