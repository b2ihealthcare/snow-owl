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

import static com.b2international.snowowl.core.CoreTerminologyBroker.UNSPECIFIED_NUMBER_SHORT;
import static java.util.Collections.emptyMap;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import org.eclipse.emf.cdo.common.id.CDOID;

import com.b2international.snowowl.core.api.IHistoryInfo.IVersion;
import com.b2international.snowowl.core.api.component.TerminologyComponentIdProvider;

/**
 * Representation of a historical information query executor implementation.
 */
public interface HistoryInfoQueryExecutor extends TerminologyComponentIdProvider {

	/**
	 * Executes the queries required to retrieve all historical information for a content or terminology independent
	 * component and returns with a map of timestamps and associated {@link IVersion version} entry
	 * pairs representing the changes for a component.
	 * <p><b>NOTE:</b> clients must <b>NOT</b> close the underlying {@link InternalHistoryInfoConfiguration#getConnection() connection}
	 * or any of the {@link InternalHistoryInfoConfiguration#getPreparedStatements() prepared statement}s, but it's the client's
	 * responsibility to properly release any {@link ResultSet result sets} after performing a query.
	 * @param configuration the configuration.
	 * @return a map of timestamps and versions.
	 * @throws SQLException when low level backend exception occurs while preparing or executing the statements.  
	 */
	Map<Long, IVersion<CDOID>> execute(final InternalHistoryInfoConfiguration configuration) throws SQLException;

	/**
	 * No-operation instance. Does nothing.
	 */
	HistoryInfoQueryExecutor NOOP = new HistoryInfoQueryExecutor() {
		
		@Override
		public Map<Long, IVersion<CDOID>> execute(final InternalHistoryInfoConfiguration configuration) {
			return emptyMap();
		}
		
		@Override
		public short getTerminologyComponentId() {
			return UNSPECIFIED_NUMBER_SHORT;
		};
	};
}
