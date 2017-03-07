/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.ecore.EClass;

import com.b2international.commons.collections.CloseableMap;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.IHistoryInfo.IVersion;
import com.b2international.snowowl.datastore.cdo.LocalDbUtils;
import com.b2international.snowowl.datastore.server.history.HistoryInfoQueryExecutorImpl;
import com.b2international.snowowl.datastore.server.history.InternalHistoryInfoConfiguration;
import com.b2international.snowowl.datastore.server.history.PreparedStatementKey;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSet;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetUtil;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;

/**
 * History information query executor for SNOMED CT reference sets.
 */
public class SnomedRefSetHistoryInfoQueryExecutor extends HistoryInfoQueryExecutorImpl {

	@Override
	public Map<Long, IVersion<CDOID>> execute(final InternalHistoryInfoConfiguration configuration) throws SQLException {

		final SortedMap<Long, IVersion<CDOID>> modifications = new TreeMap<Long, IVersion<CDOID>>();
		final SnomedReferenceSet indexEntry = getRefSet(configuration);
		collectPrimaryComponentChanges(configuration, modifications, getRefSetStatementKey(indexEntry));
		collectOtherComponentChanges(configuration, modifications, SnomedPreparedStatementKey.getMemberStatementKey(indexEntry.getType()));
		adjustMinorVersions(modifications);
		return modifications;
	}

	@Override
	public short getTerminologyComponentId() {
		return SnomedTerminologyComponentConstants.REFSET_NUMBER;
	}

	@Override
	protected CloseableMap<PreparedStatementKey, PreparedStatement> createPreparedStatements(final Connection connection) throws SQLException {
		final CloseableMap<PreparedStatementKey, PreparedStatement> statements = CloseableMap.newCloseableMap();

		for (final EClass refSetClass : SnomedRefSetUtil.REFSET_CLASSES) {
			final String queryString = SnomedRefSetHistoryQueries.REFSET_CHANGES.getQuery(LocalDbUtils.tableNameFor(refSetClass));
			final PreparedStatementKey statementKey = SnomedPreparedStatementKey.getRefSetStatementKey(refSetClass);
			statements.put(statementKey, connection.prepareStatement(queryString));
		}

		for (final SnomedRefSetType type : SnomedRefSetType.VALUES) {
			final EClass memberClass = SnomedRefSetUtil.getRefSetMemberClass(type);
			final String queryString = SnomedRefSetHistoryQueries.REFSET_MEMBER_CHANGES.getQuery(LocalDbUtils.tableNameFor(memberClass));
			final PreparedStatementKey statementKey = SnomedPreparedStatementKey.getMemberStatementKey(type);
			statements.put(statementKey, connection.prepareStatement(queryString));
		}

		return statements;
	}

	private PreparedStatementKey getRefSetStatementKey(final SnomedReferenceSet refSet) {
		if (SnomedRefSetUtil.isMapping(refSet.getType())) {
			return SnomedPreparedStatementKey.MAPPING_REFSET_CHANGES;
		} else if (SnomedRefSetUtil.isStructural(refSet.getId(), refSet.getType())) {
			return SnomedPreparedStatementKey.STRUCTURAL_REFSET_CHANGES;
		} else {
			return SnomedPreparedStatementKey.REGULAR_REFSET_CHANGES;
		}
	}

	private SnomedReferenceSet getRefSet(final InternalHistoryInfoConfiguration configuration) {
		final IBranchPath branchPath = configuration.getBranchPath();
		final String refSetId = configuration.getComponentId();
		return SnomedRequests.prepareGetReferenceSet(refSetId).build(SnomedDatastoreActivator.REPOSITORY_UUID, branchPath.getPath())
				.execute(ApplicationContext.getServiceForClass(IEventBus.class)).getSync();
	}

}
