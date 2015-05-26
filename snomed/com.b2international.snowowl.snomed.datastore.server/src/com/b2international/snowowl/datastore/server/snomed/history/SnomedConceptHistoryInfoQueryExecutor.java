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

import static com.google.common.base.Preconditions.checkNotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.eclipse.emf.cdo.common.id.CDOID;

import com.b2international.commons.collections.CloseableMap;
import com.b2international.snowowl.core.api.IHistoryInfo.IVersion;
import com.b2international.snowowl.datastore.cdo.CDOUtils;
import com.b2international.snowowl.datastore.cdo.LocalDbUtils;
import com.b2international.snowowl.datastore.history.Version;
import com.b2international.snowowl.datastore.server.history.HistoryInfoQueryExecutorImpl;
import com.b2international.snowowl.datastore.server.history.InternalHistoryInfoConfiguration;
import com.b2international.snowowl.datastore.server.history.PreparedStatementKey;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetUtil;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.google.common.collect.ImmutableSet;

/**
 * Historical information query executor implementation for SNOMED CT concepts.
 */
public class SnomedConceptHistoryInfoQueryExecutor extends HistoryInfoQueryExecutorImpl {

	private static final int UNSPECIFIED_CONCEPT_INBOUND_RELATIONSHIP_NUMBER = -1;

	private static final Set<SnomedRefSetType> REFSET_MEMBER_TYPES = ImmutableSet.of(SnomedRefSetType.SIMPLE_MAP, 
			SnomedRefSetType.SIMPLE, 
			SnomedRefSetType.ATTRIBUTE_VALUE, 
			SnomedRefSetType.QUERY, 
			SnomedRefSetType.CONCRETE_DATA_TYPE);

	@Override
	public Map<Long, IVersion<CDOID>> execute(final InternalHistoryInfoConfiguration configuration) throws SQLException {
		checkNotNull(configuration, "configuration");

		final SortedMap<Long, IVersion<CDOID>> modifications = new TreeMap<Long, IVersion<CDOID>>();
		collectPrimaryComponentChanges(configuration, modifications, SnomedPreparedStatementKey.CONCEPT_CHANGES);
		collectConceptPTChanges(configuration, modifications);
		collectOtherComponentChanges(configuration, modifications, SnomedPreparedStatementKey.DESCRIPTION_CHANGES);
		collectOtherComponentChanges(configuration, modifications, SnomedPreparedStatementKey.RELATIONSHIP_CHANGES);
		collectRefSetMemberChanges(configuration, modifications);
		adjustMinorVersions(modifications);
		return modifications;
	}

	@Override
	public short getTerminologyComponentId() {
		return SnomedTerminologyComponentConstants.CONCEPT_NUMBER;
	}

	@Override
	protected Object[] createPrimaryComponentInfo(final ResultSet rs) throws SQLException {
		return new Object[] { 
				rs.getObject(1), // created timestamp 
				rs.getObject(2) // inbound relationships list size
		};
	}

	@Override
	protected void registerPrimaryComponentModifications(final InternalHistoryInfoConfiguration configuration, 
			final SortedMap<Long, IVersion<CDOID>> modifications, 
			final List<Object[]> info) {
		
		int lastInbound = UNSPECIFIED_CONCEPT_INBOUND_RELATIONSHIP_NUMBER;
		int majorVersion = INITIAL_MAJOR_VERSION;
		
		for (final Object[] objectInfo : info) {
			
			final long timeStamp = (Long) objectInfo[0];
			final int inboundListSize = (Integer) objectInfo[1];
			
			/*
			 * Check concept in-bound relationship list size; we don't want to indicate changes related to
			 * inbound relationships.
			 */
			if (lastInbound == UNSPECIFIED_CONCEPT_INBOUND_RELATIONSHIP_NUMBER || lastInbound == inboundListSize) {
				final Version v = new Version(majorVersion++);
				v.addAffectedObjectId(configuration.getCdoId(), timeStamp);
				modifications.put(timeStamp, v);
			}
			
			lastInbound = inboundListSize;
		}
	}

	@Override
	protected CloseableMap<PreparedStatementKey, PreparedStatement> createPreparedStatements(final Connection connection) throws SQLException {
	
		final CloseableMap<PreparedStatementKey, PreparedStatement> statements = new CloseableMap<>();
		statements.put(SnomedPreparedStatementKey.CONCEPT_CHANGES, connection.prepareStatement(SnomedConceptHistoryQueries.CONCEPT_CHANGES.getQuery()));
		statements.put(SnomedPreparedStatementKey.CONCEPT_PT_CHANGES, connection.prepareStatement(SnomedConceptHistoryQueries.CONCEPT_PT_CHANGES.getQuery()));
		statements.put(SnomedPreparedStatementKey.DESCRIPTION_CHANGES, connection.prepareStatement(SnomedConceptHistoryQueries.DESCRIPTION_CHANGES.getQuery()));
		statements.put(SnomedPreparedStatementKey.RELATIONSHIP_CHANGES, connection.prepareStatement(SnomedConceptHistoryQueries.RELATIONSHIP_CHANGES.getQuery()));
		
		for (final SnomedRefSetType type : REFSET_MEMBER_TYPES) {
			final PreparedStatementKey statementKey = SnomedPreparedStatementKey.getMemberStatementKey(type);
			final PreparedStatement statement = getRefSetMemberStatement(connection, type);
			statements.put(statementKey, statement);
		}
		
		return statements;
	}

	private void collectRefSetMemberChanges(final InternalHistoryInfoConfiguration configuration, 
			final SortedMap<Long, IVersion<CDOID>> modifications) throws SQLException {

		for (final SnomedRefSetType type : REFSET_MEMBER_TYPES) {
			final PreparedStatementKey statementKey = SnomedPreparedStatementKey.getMemberStatementKey(type);
			collectOtherComponentChanges(configuration, modifications, statementKey);
		}
	}

	private void collectConceptPTChanges(final InternalHistoryInfoConfiguration configuration, 
			final SortedMap<Long, IVersion<CDOID>> modifications) throws SQLException {
		
		final long fsnCdoId = FsnCdoIdSupplier.INSTANCE.get();
		if (fsnCdoId != CDOUtils.NO_STORAGE_KEY) {
			collectOtherComponentChanges(configuration, modifications, SnomedPreparedStatementKey.CONCEPT_PT_CHANGES);
		}
	}

	private PreparedStatement getRefSetMemberStatement(final Connection connection, final SnomedRefSetType type) throws SQLException {
		final String tableName = LocalDbUtils.tableNameFor(SnomedRefSetUtil.getRefSetMemberClass(type));
		return connection.prepareStatement(SnomedConceptHistoryQueries.RELATED_REFERENCE_SET_MEMBER_CHANGES.getQuery(tableName));
	}
}
