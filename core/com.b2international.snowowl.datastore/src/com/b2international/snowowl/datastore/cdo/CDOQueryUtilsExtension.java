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
package com.b2international.snowowl.datastore.cdo;

import org.eclipse.emf.cdo.common.util.CDOQueryQueue;
import org.eclipse.emf.cdo.internal.server.ServerCDOView;
import org.eclipse.emf.cdo.internal.server.ServerCDOView.ServerCDOSession;
import org.eclipse.emf.cdo.spi.server.InternalQueryManager;
import org.eclipse.emf.cdo.spi.server.InternalQueryResult;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.spi.server.InternalSession;
import org.eclipse.emf.cdo.spi.server.InternalView;
import org.eclipse.emf.cdo.view.CDOQuery;
import org.eclipse.emf.spi.cdo.AbstractQueryIterator;

/**
 * CDO query utility class extension to support missing CDO query execution feature with CDO server views
 * on the server-side.
 */
public class CDOQueryUtilsExtension {

	@SuppressWarnings("restriction")
	public static void getResults(final CDOQuery query, final AbstractQueryIterator<?> queryIterator) {
		
		final ServerCDOView view = (ServerCDOView) query.getView();
		final ServerCDOSession session = (ServerCDOSession) view.getSession();
		final InternalRepository repository = session.getRepository();
		final InternalQueryManager queryManager = repository.getQueryManager();

		final InternalSession serverSession = session.getInternalSession();
		final InternalView serverView = serverSession.getView(view.getViewID());
		final InternalQueryResult result = queryManager.execute(serverView, queryIterator.getQueryInfo());
		final int queryId = result.getQueryID();
		
		queryIterator.setQueryID(queryId);
		
		final CDOQueryQueue<Object> resultQueue = queryIterator.getQueue();
		
		try {
			
			while (result.hasNext()) {
				final Object object = result.next();
				if (null != object) {
					resultQueue.add(object);
				}
			}
			
		} catch (final Throwable t) {
			resultQueue.setException(new RuntimeException(t.getMessage(), t));
		} finally {
			resultQueue.close();
		}
	}
}
