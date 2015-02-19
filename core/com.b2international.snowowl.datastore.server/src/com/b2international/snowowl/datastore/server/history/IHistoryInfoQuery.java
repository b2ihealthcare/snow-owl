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

import java.sql.SQLException;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.id.CDOID;

import com.b2international.snowowl.core.api.IHistoryInfo.IVersion;

/**
 * Interface for history info queries.
 */
public interface IHistoryInfoQuery {

	/**
	 * Retrieves the history informations by querying the changes from the database according to the passed in CDO ID and CDO branch.
	 * @param cdoId the CDO ID.
	 * @param cdoBranch the passed in CDO specific branch.
	 * @param monitor monitors the query process.
	 * @return the changes where keys are the change time in {@link Long} and the values are fake version numbers.
	 * @throws SQLException database access.
	 */
	Map<Long, ? extends IVersion<CDOID>> query(final CDOID cdoId, final CDOBranch cdoBranch, final IProgressMonitor monitor) throws SQLException;
}