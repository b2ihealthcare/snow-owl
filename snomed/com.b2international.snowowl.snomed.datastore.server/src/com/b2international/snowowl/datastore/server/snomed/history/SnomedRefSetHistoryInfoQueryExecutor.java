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

import static com.b2international.snowowl.core.ApplicationContext.getServiceForClass;
import static com.b2international.snowowl.datastore.cdo.LocalDbUtils.tableNameFor;
import static com.b2international.snowowl.datastore.server.snomed.history.SnomedHistoryQueries.REFSET_CHANGES_TEMPLATE_FROM_BRANCH_TEMPLATE;
import static com.b2international.snowowl.datastore.server.snomed.history.SnomedHistoryQueries.REFSET_CHANGES_TEMPLATE_FROM_MAIN_TEMPLATE;
import static com.b2international.snowowl.datastore.server.snomed.history.SnomedHistoryQueries.REFSET_MEMBER_CHANGES_TEMPLATE_FROM_BRANCH_TEMPLATE;
import static com.b2international.snowowl.datastore.server.snomed.history.SnomedHistoryQueries.REFSET_MEMBER_CHANGES_TEMPLATE_FROM_MAIN_TEMPLATE;
import static com.b2international.snowowl.datastore.server.snomed.history.SnomedPreparedStatementKey.MAPPING_REFSET_CHANGES;
import static com.b2international.snowowl.datastore.server.snomed.history.SnomedPreparedStatementKey.REGULAR_REFSET_CHANGES;
import static com.b2international.snowowl.datastore.server.snomed.history.SnomedPreparedStatementKey.STRUCTURAL_REFSET_CHANGES;
import static com.b2international.snowowl.datastore.server.snomed.history.SnomedPreparedStatementKey.getMemberStatementKey;
import static com.b2international.snowowl.snomed.datastore.SnomedRefSetUtil.REFSET_CLASSES;
import static com.b2international.snowowl.snomed.datastore.SnomedRefSetUtil.getRefSetMemberClass;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Iterables.get;
import static com.google.common.collect.Lists.newArrayList;
import static java.lang.String.format;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.ecore.EClass;

import com.b2international.commons.collections.CloseableMap;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.IHistoryInfo.IVersion;
import com.b2international.snowowl.datastore.history.Version;
import com.b2international.snowowl.datastore.server.history.HistoryInfoQueryExecutorImpl;
import com.b2international.snowowl.datastore.server.history.InternalHistoryInfoConfiguration;
import com.b2international.snowowl.datastore.server.history.PreparedStatementKey;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetBrowser;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetUtil;
import com.b2international.snowowl.snomed.datastore.index.refset.SnomedRefSetIndexEntry;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;

/**
 * History information query executor for SNOMED&nbsp;CT reference sets.
 *
 */
public class SnomedRefSetHistoryInfoQueryExecutor extends HistoryInfoQueryExecutorImpl {

	@Override
	public Map<Long, IVersion<CDOID>> execute(final InternalHistoryInfoConfiguration configuration) throws SQLException {
		final Collection<PreparedStatementKey> statementKeys = getStatementKeys(configuration); 
		checkState(2 == statementKeys.size(), "Expected 2 statement keys, got " + statementKeys.size());
		
		final SortedMap<Long, IVersion<CDOID>> modifications = new TreeMap<Long, IVersion<CDOID>>();
		
		final PreparedStatement refSetStatement = configuration.getPreparedStatements().get(get(statementKeys, 0));
		final PreparedStatement memberStatement = configuration.getPreparedStatements().get(get(statementKeys, 1));
		
		collectRefSetChanges(configuration, refSetStatement, modifications);
		collectMemberChanges(configuration, memberStatement, modifications);
		
		adjustMinorVersions(modifications);
		
		return modifications;
	}

	protected void incrementSubVersions(final SortedMap<Long, IVersion<CDOID>> modifications) {
		int subVersion = 1;
		for (final IVersion<CDOID> version : modifications.values()) { 
			if (((Version) version).representsMajorChange()) {
				subVersion = 1;
			} else {
				((Version) version).setMinorVersion(subVersion++);
			}
		}		
	}
	
	private void collectMemberChanges(final InternalHistoryInfoConfiguration configuration, 
			final PreparedStatement memberStatement, final SortedMap<Long, IVersion<CDOID>> modifications) throws SQLException {
		
		try (
			final ResultSet rs = adjustStatement(configuration, memberStatement, DEFAULT_JOIN_NUMBER).executeQuery();
		) {
			
			final List<Object[]> cdoObjectInfo = newArrayList();
			while (rs.next()) {
				cdoObjectInfo.add(new Object[] {
						rs.getObject(1), 
						rs.getObject(2), 
					});
			}
			registerModifications(modifications, cdoObjectInfo);
			
		}
		
	}

	private void registerModifications(final SortedMap<Long, IVersion<CDOID>> allModification, 
			final List<Object[]> cdoObjectInfo) {

		for (final Object[] result : cdoObjectInfo) {

			final long affectedId = (Long) result[0];
			final long timeStamp = (Long) result[1];

			Version versionForTimeStamp = (Version) allModification.get(timeStamp);

			if (versionForTimeStamp == null) {
				final Version previousVersion = (Version) allModification.get(allModification.headMap(timeStamp).lastKey());
				versionForTimeStamp = new Version(previousVersion.getMajorVersion(), -1);
				allModification.put(timeStamp, versionForTimeStamp);
			}

			versionForTimeStamp.addAffectedObjectId(CDOIDUtil.createLong(affectedId), timeStamp);
		}
	}
	
	private void collectRefSetChanges(final InternalHistoryInfoConfiguration configuration, 
			final PreparedStatement refSetStatement, final SortedMap<Long, IVersion<CDOID>> modifications) throws SQLException {
		try (
				final ResultSet rs = adjustStatement(configuration, refSetStatement, DEFAULT_JOIN_NUMBER).executeQuery();
			) {
				
				final Collection<Long> cdoObjectInfo = newArrayList();
				while (rs.next()) {
					cdoObjectInfo.add(rs.getLong(1));
				}
				int i = 1;
				for (final Long timestamp : cdoObjectInfo){
					
					final Version v = new Version(i++);
					v.addAffectedObjectId(configuration.getCdoId(), timestamp);
					modifications.put(timestamp, v);
				}
				
			}
		
	}
	

	@Override
	public short getTerminologyComponentId() {
		return SnomedTerminologyComponentConstants.REFSET_NUMBER;
	}

	@Override
	protected CloseableMap<PreparedStatementKey, PreparedStatement> createStatementsForMain(
			final Connection connection) throws SQLException {
		
		return createStatements(
				connection, 
				REFSET_CHANGES_TEMPLATE_FROM_MAIN_TEMPLATE, 
				REFSET_MEMBER_CHANGES_TEMPLATE_FROM_MAIN_TEMPLATE);
	}
	
	@Override
	protected CloseableMap<PreparedStatementKey, PreparedStatement> createStatementsForBranch(
			final Connection connection) throws SQLException {
		
		return createStatements(
				connection, 
				REFSET_CHANGES_TEMPLATE_FROM_BRANCH_TEMPLATE, 
				REFSET_MEMBER_CHANGES_TEMPLATE_FROM_BRANCH_TEMPLATE);
	}
	
	private CloseableMap<PreparedStatementKey, PreparedStatement> createStatements(
			final Connection connection, final String refSetQueryTemplate, final String memberQueryTemplate) throws SQLException {
		
		final CloseableMap<PreparedStatementKey, PreparedStatement> statements = new CloseableMap<>();
		for (final EClass refSetClass : REFSET_CLASSES) {
			final String queryString = format(refSetQueryTemplate, tableNameFor(refSetClass));
			final PreparedStatementKey statementKey = SnomedPreparedStatementKey.getRefSetStatementKey(refSetClass);
			statements.put(statementKey, connection.prepareStatement(queryString));
		}
		
		for (final SnomedRefSetType type : SnomedRefSetType.VALUES) {
			final String queryString = format(memberQueryTemplate, tableNameFor(getRefSetMemberClass(type)));
			final PreparedStatementKey statementKey = getMemberStatementKey(type);
			statements.put(statementKey, connection.prepareStatement(queryString));
		}
		
		return statements;
		
	}
	
	private Collection<PreparedStatementKey> getStatementKeys(final InternalHistoryInfoConfiguration configuration) {
		final Collection<PreparedStatementKey> statementKeys = newArrayList();
		final SnomedRefSetIndexEntry refSet = getRefSer(configuration);
		statementKeys.add(getRefSetStatementKey(refSet));
		statementKeys.add(getMemberStatementKey(refSet.getType()));
		return statementKeys;
	}

	private PreparedStatementKey getRefSetStatementKey(final SnomedRefSetIndexEntry refSet) {
		return isMapping(refSet) 
			? MAPPING_REFSET_CHANGES 
			: refSet.isStructural() 
				? STRUCTURAL_REFSET_CHANGES 
				: REGULAR_REFSET_CHANGES;
	}

	private boolean isMapping(final SnomedRefSetIndexEntry refSet) {
		return SnomedRefSetUtil.isMapping(refSet.getType());
	}

	private SnomedRefSetIndexEntry getRefSer(final InternalHistoryInfoConfiguration configuration) {
		final IBranchPath branchPath = configuration.getBranchPath();
		final String refSetId = configuration.getComponentId();
		return getRefSetBrowser().getRefSet(branchPath, refSetId);
	}

	private SnomedRefSetBrowser getRefSetBrowser() {
		return getServiceForClass(SnomedRefSetBrowser.class);
	}
	
}