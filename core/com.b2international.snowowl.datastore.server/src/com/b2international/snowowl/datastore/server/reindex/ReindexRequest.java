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
package com.b2international.snowowl.datastore.server.reindex;

import org.eclipse.emf.cdo.server.StoreThreadLocal;
import org.eclipse.emf.cdo.spi.server.InternalSession;

import com.b2international.snowowl.core.Repository;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.events.BaseRequest;
import com.b2international.snowowl.datastore.server.internal.InternalRepository;

/**
 * @since 4.7
 */
@SuppressWarnings("restriction")
public class ReindexRequest extends BaseRequest<RepositoryContext, Boolean> {

	ReindexRequest() {}
	
	@Override
	public Boolean execute(RepositoryContext context) {
		final InternalRepository repository = (InternalRepository) context.service(Repository.class);
		
		final org.eclipse.emf.cdo.internal.server.Repository cdoRepository = (org.eclipse.emf.cdo.internal.server.Repository) repository.getCdoRepository().getRepository();
		final InternalSession session = cdoRepository.getSessionManager().openSession(null);
		
		
		try {
			//set the session on the StoreThreadlocal for later access
			StoreThreadLocal.setSession(session);
			//for partial replication get the last branch id and commit time from the index
			//right now index is fully recreated
			cdoRepository.replicate(new IndexMigrationReplicationContext(context, -1, 0, session));
		} finally {
			StoreThreadLocal.release();
			session.close();
		}
		return Boolean.TRUE;
	}

	@Override
	protected Class<Boolean> getReturnType() {
		return Boolean.class;
	}

	public static ReindexRequestBuilder builder(String repositoryId) {
		return new ReindexRequestBuilder(repositoryId);
	}
	
}
