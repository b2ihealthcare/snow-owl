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

import static com.b2international.snowowl.datastore.cdo.CDOUtils.NO_STORAGE_KEY;
import static com.b2international.snowowl.datastore.cdo.LocalDbUtils.tableNameFor;
import static com.b2international.snowowl.datastore.server.snomed.history.SnomedHistoryQueries.CONCEPT_CHANGES_FROM_BRANCH;
import static com.b2international.snowowl.datastore.server.snomed.history.SnomedHistoryQueries.CONCEPT_CHANGES_FROM_MAIN;
import static com.b2international.snowowl.datastore.server.snomed.history.SnomedHistoryQueries.CONCEPT_PT_CHANGES_FROM_BRANCH;
import static com.b2international.snowowl.datastore.server.snomed.history.SnomedHistoryQueries.CONCEPT_PT_CHANGES_FROM_MAIN;
import static com.b2international.snowowl.datastore.server.snomed.history.SnomedHistoryQueries.DESCRIPTION_CHANGES_FROM_BRANCH;
import static com.b2international.snowowl.datastore.server.snomed.history.SnomedHistoryQueries.DESCRIPTION_CHANGES_FROM_MAIN;
import static com.b2international.snowowl.datastore.server.snomed.history.SnomedHistoryQueries.RELATED_REFERENCE_SET_MEMBER_CHANGES_FROM_BRANCH_TEMPLATE;
import static com.b2international.snowowl.datastore.server.snomed.history.SnomedHistoryQueries.RELATED_REFERENCE_SET_MEMBER_CHANGES_FROM_MAIN_TEMPLATE;
import static com.b2international.snowowl.datastore.server.snomed.history.SnomedHistoryQueries.RELATIONSHIP_CHANGES_FROM_BRANCH;
import static com.b2international.snowowl.datastore.server.snomed.history.SnomedHistoryQueries.RELATIONSHIP_CHANGES_FROM_MAIN;
import static com.b2international.snowowl.datastore.server.snomed.history.SnomedPreparedStatementKey.CONCEPT_CHANGES;
import static com.b2international.snowowl.datastore.server.snomed.history.SnomedPreparedStatementKey.CONCEPT_PT_CHANGES;
import static com.b2international.snowowl.datastore.server.snomed.history.SnomedPreparedStatementKey.DESCRIPTION_CHANGES;
import static com.b2international.snowowl.datastore.server.snomed.history.SnomedPreparedStatementKey.RELATIONSHIP_CHANGES;
import static com.b2international.snowowl.datastore.server.snomed.history.SnomedPreparedStatementKey.getMemberStatementKey;
import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.CONCEPT_NUMBER;
import static com.b2international.snowowl.snomed.datastore.SnomedRefSetUtil.getRefSetMemberClass;
import static com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType.ATTRIBUTE_VALUE;
import static com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType.CONCRETE_DATA_TYPE;
import static com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType.QUERY;
import static com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType.SIMPLE;
import static com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType.SIMPLE_MAP;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;
import static java.lang.String.format;
import static java.util.Collections.unmodifiableCollection;

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

import com.b2international.commons.collections.CloseableMap;
import com.b2international.snowowl.core.api.IHistoryInfo.IVersion;
import com.b2international.snowowl.datastore.history.Version;
import com.b2international.snowowl.datastore.server.history.HistoryInfoQueryExecutorImpl;
import com.b2international.snowowl.datastore.server.history.InternalHistoryInfoConfiguration;
import com.b2international.snowowl.datastore.server.history.PreparedStatementKey;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;

/**
 * Historical information query executor implementation for SNOMED&nbsp;CT concepts.
 *
 */
public class SnomedConceptHistoryInfoQueryExecutor extends HistoryInfoQueryExecutorImpl {

	private static final int INITIAL_CONCEPT_INBOUND_RELATIONSHIP_NUMBER = -1;

	private static final Collection<SnomedRefSetType> REFSET_MEMBER_TYPES = unmodifiableCollection(newArrayList(
			SIMPLE_MAP,
			SIMPLE,
			ATTRIBUTE_VALUE,
			QUERY,
			CONCRETE_DATA_TYPE));

	@Override
	public Map<Long, IVersion<CDOID>> execute(final InternalHistoryInfoConfiguration configuration) throws SQLException {
		checkNotNull(configuration, "configuration");
		final SortedMap<Long, IVersion<CDOID>> modifications = new TreeMap<Long, IVersion<CDOID>>();

		collectPrimaryComponentChanges(configuration, modifications, CONCEPT_CHANGES);
		collectConceptPTChanges(configuration, modifications);
		collectOtherComponentChanges(configuration, modifications, DESCRIPTION_CHANGES);
		collectOtherComponentChanges(configuration, modifications, RELATIONSHIP_CHANGES);
		collectRefSetMemberChanges(configuration, modifications);

		adjustMinorVersions(modifications);
		
		return modifications;
	}

	@Override
	public short getTerminologyComponentId() {
		return CONCEPT_NUMBER;
	}

	@Override
	protected Object[] createPrimaryComponentInfo(final ResultSet rs) throws SQLException {
		return new Object[] { rs.getObject(1), rs.getObject(2) };
	}

	@Override
	protected void registerPrimaryComponentModifications(final InternalHistoryInfoConfiguration configuration, 
			final SortedMap<Long, IVersion<CDOID>> modifications, final List<Object[]> info) {
		
		int lastInbound = INITIAL_CONCEPT_INBOUND_RELATIONSHIP_NUMBER;
	
		int majorVersion = INITIAL_MAJOR_VERSION;
		for (final Object[] objectInfo : info){
			
			final long timeStamp = (Long) objectInfo[0];
			final int inboundListSize = (Integer) objectInfo[1];
			
			//also check concept in-bound relationship list size
			//since we don't want to indicate a in-bound relationship change we just skip it
			if (-1 == lastInbound || lastInbound == inboundListSize) {
				final Version v = new Version(majorVersion++);
				v.addAffectedObjectId(configuration.getCdoId(), timeStamp);
				modifications.put(timeStamp, v);
			}
			
			lastInbound = inboundListSize;
		}
		
	}

	@Override
	protected CloseableMap<PreparedStatementKey, PreparedStatement> createStatementsForMain(
			final Connection connection) throws SQLException {
		
		final CloseableMap<PreparedStatementKey, PreparedStatement> statements = new CloseableMap<>();
		statements.put(CONCEPT_CHANGES, connection.prepareStatement(CONCEPT_CHANGES_FROM_MAIN));
		statements.put(CONCEPT_PT_CHANGES, connection.prepareStatement(CONCEPT_PT_CHANGES_FROM_MAIN));
		statements.put(DESCRIPTION_CHANGES, connection.prepareStatement(DESCRIPTION_CHANGES_FROM_MAIN));
		statements.put(RELATIONSHIP_CHANGES, connection.prepareStatement(RELATIONSHIP_CHANGES_FROM_MAIN));
		
		for (final SnomedRefSetType type : REFSET_MEMBER_TYPES) {
			final PreparedStatementKey statementKey = getMemberStatementKey(type);
			final PreparedStatement statement = getRefSetMemberStatement(
					connection, 
					RELATED_REFERENCE_SET_MEMBER_CHANGES_FROM_MAIN_TEMPLATE, 
					type);
			
			statements.put(statementKey, statement);
		}
		
		return statements;
	}

	@Override
	protected CloseableMap<PreparedStatementKey, PreparedStatement> createStatementsForBranch(
			final Connection connection) throws SQLException {
	
		final CloseableMap<PreparedStatementKey, PreparedStatement> statements = new CloseableMap<>();
		statements.put(CONCEPT_CHANGES, connection.prepareStatement(CONCEPT_CHANGES_FROM_BRANCH));
		statements.put(CONCEPT_PT_CHANGES, connection.prepareStatement(CONCEPT_PT_CHANGES_FROM_BRANCH));
		statements.put(DESCRIPTION_CHANGES, connection.prepareStatement(DESCRIPTION_CHANGES_FROM_BRANCH));
		statements.put(RELATIONSHIP_CHANGES, connection.prepareStatement(RELATIONSHIP_CHANGES_FROM_BRANCH));
		
		for (final SnomedRefSetType type : REFSET_MEMBER_TYPES) {
			final PreparedStatementKey statementKey = getMemberStatementKey(type);
			final PreparedStatement statement = getRefSetMemberStatement(
					connection, 
					RELATED_REFERENCE_SET_MEMBER_CHANGES_FROM_BRANCH_TEMPLATE, 
					type);
			
			statements.put(statementKey, statement);
		}
		
		return statements;
		
	}

	private void collectRefSetMemberChanges(final InternalHistoryInfoConfiguration configuration, 
			final SortedMap<Long, IVersion<CDOID>> modifications) throws SQLException {

		for (final SnomedRefSetType type : REFSET_MEMBER_TYPES) {
			final PreparedStatementKey statementKey = getMemberStatementKey(type);
			final PreparedStatement statement = configuration.getPreparedStatements().get(statementKey);
			adjustRelatedRefSetMemberChangesStatement(configuration, statement);
			collectOtherComponentChanges(configuration, modifications, statement);
		}
	}

	private void adjustRelatedRefSetMemberChangesStatement(final InternalHistoryInfoConfiguration configuration, 
			final PreparedStatement statement) throws SQLException {
		
		statement.setString(1, configuration.getComponentId());
		if (!isMain(configuration)) {
			statement.setInt(2, configuration.getBranchId());
			statement.setInt(3, configuration.getBranchId());
			statement.setLong(4, configuration.getBaseBranchTimestamp());
		}
	}

	private void collectConceptPTChanges(final InternalHistoryInfoConfiguration configuration, 
			final SortedMap<Long, IVersion<CDOID>> modifications) throws SQLException {
		
		final long fsnCdoId = FsnCdoIdSupplier.INSTANCE.get();
		if (NO_STORAGE_KEY != fsnCdoId) {
			
			final PreparedStatement statement = getAndAdjustConceptPtChangesStatment(configuration, fsnCdoId);
			collectOtherComponentChanges(configuration, modifications, statement);
			
		}
		
	}

	private PreparedStatement getAndAdjustConceptPtChangesStatment(
			final InternalHistoryInfoConfiguration configuration, final long fsnCdoId) throws SQLException {
		
		final PreparedStatement statement = configuration.getPreparedStatements().get(CONCEPT_PT_CHANGES);
		statement.setLong(1, configuration.getStorageKey());
		statement.setLong(2, fsnCdoId);
		if (!isMain(configuration)) {
			statement.setInt(3, configuration.getBranchId());
			statement.setInt(4, configuration.getBranchId());
			statement.setLong(5, configuration.getBaseBranchTimestamp());
			statement.setInt(6, configuration.getBranchId());
			statement.setInt(7, configuration.getBranchId());
			statement.setLong(8, configuration.getBaseBranchTimestamp());
		}
		return statement;
	}

	private PreparedStatement getRefSetMemberStatement(final Connection connection, final String template, 
			final SnomedRefSetType type) throws SQLException {

		final String tableName = tableNameFor(getRefSetMemberClass(type));
		return connection.prepareStatement(format(template, tableName));
	}

}