/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore.reindex;

import org.eclipse.emf.cdo.server.StoreThreadLocal;
import org.eclipse.emf.cdo.spi.server.InternalSession;

import com.b2international.snowowl.core.Repository;
import com.b2international.snowowl.core.RepositoryInfo.Health;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.branch.Branches;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.ft.FeatureToggles;
import com.b2international.snowowl.core.repository.InternalRepository;
import com.b2international.snowowl.datastore.request.RepositoryRequests;

/**
 * @since 4.7
 */
@SuppressWarnings("restriction")
public final class ReindexRequest implements Request<RepositoryContext, ReindexResult> {
	
	private long failedCommitTimestamp = 1;

	ReindexRequest() {}
	
	void setFailedCommitTimestamp(final long failedCommitTimestamp) {
		this.failedCommitTimestamp = failedCommitTimestamp;
	}
	
	@Override
	public ReindexResult execute(RepositoryContext context) {
		final InternalRepository repository = (InternalRepository) context.service(Repository.class);
		final FeatureToggles features = context.service(FeatureToggles.class);
		
		long maxCdoBranchId = -1L;
		final Branches branches = RepositoryRequests.branching().prepareSearch()
				.all()
				.build()
				.execute(context);
		
		for (final Branch branch : branches) {
			if (branch.branchId() > maxCdoBranchId) {
				maxCdoBranchId = branch.branchId();
			}
		}
		
		final org.eclipse.emf.cdo.internal.server.Repository cdoRepository = (org.eclipse.emf.cdo.internal.server.Repository) repository.getCdoRepository().getRepository();
		final InternalSession session = cdoRepository.getSessionManager().openSession(null);
		
		try {
			repository.setHealth(Health.YELLOW, "Reindex is in progress...");
			features.enable(featureFor(context.id()));
			//set the session on the StoreThreadlocal for later access
			StoreThreadLocal.setSession(session);
			//for partial replication get the last branch id and commit time from the index
			//right now index is fully recreated
			final IndexMigrationReplicationContext replicationContext = new IndexMigrationReplicationContext(context, (int) maxCdoBranchId, failedCommitTimestamp - 1, session);
			cdoRepository.replicate(replicationContext);
			// update repository state after the re-indexing
			return new ReindexResult(replicationContext.getFailedCommitTimestamp(),
					replicationContext.getProcessedCommits(), replicationContext.getSkippedCommits(), replicationContext.getException());
		} finally {
			features.disable(featureFor(context.id()));
			StoreThreadLocal.release();
			session.close();
			repository.checkHealth();
		}
	}

	public static ReindexRequestBuilder builder() {
		return new ReindexRequestBuilder();
	}

	public static String featureFor(String repositoryId) {
		return String.format("%s.reindex", repositoryId);
	}
	
}
