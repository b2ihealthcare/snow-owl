/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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

import com.b2international.index.Hits;
import com.b2international.index.Index;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Query;
import com.b2international.index.query.SortBy;
import com.b2international.index.query.SortBy.Order;
import com.b2international.snowowl.core.Repository;
import com.b2international.snowowl.core.RepositoryInfo.Health;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.ft.FeatureToggles;
import com.b2international.snowowl.core.ft.Features;
import com.b2international.snowowl.datastore.internal.InternalRepository;
import com.b2international.snowowl.datastore.internal.branch.BranchDocument;
import com.google.common.collect.Iterables;

/**
 * @since 4.7
 */
@SuppressWarnings("restriction")
public final class ReindexRequest implements Request<RepositoryContext, ReindexResult> {
	
	private static final String CDO_BRANCH_ID = "cdoBranchId";
	
	private long failedCommitTimestamp = 1;

	ReindexRequest() {}
	
	void setFailedCommitTimestamp(final long failedCommitTimestamp) {
		this.failedCommitTimestamp = failedCommitTimestamp;
	}
	
	@Override
	public ReindexResult execute(RepositoryContext context) {
		final InternalRepository repository = (InternalRepository) context.service(Repository.class);
		final FeatureToggles features = context.service(FeatureToggles.class);
		final String reindexToggle = Features.getReindexFeatureToggle(context.id());
		
		// XXX: We are deliberately side-stepping health checks here
		final Index index = repository.getIndex();
		final Hits<Integer> maxCdoBranchIdHits = index.read(searcher -> {
			return searcher.search(Query.select(Integer.class)
					.from(BranchDocument.class)
					.fields(CDO_BRANCH_ID)
					.where(Expressions.matchAll())
					.sortBy(SortBy.field(CDO_BRANCH_ID, Order.DESC))
					.limit(1)
					.build());
		});
			
		final int maxCdoBranchId = maxCdoBranchIdHits.isEmpty() ? -1 : Iterables.getOnlyElement(maxCdoBranchIdHits);
		final org.eclipse.emf.cdo.internal.server.Repository cdoRepository = (org.eclipse.emf.cdo.internal.server.Repository) repository.getCdoRepository().getRepository();
		final InternalSession session = cdoRepository.getSessionManager().openSession(null);
		
		try {
			repository.setHealth(Health.YELLOW, "Reindex is in progress...");
			features.enable(reindexToggle);
			//set the session on the StoreThreadlocal for later access
			StoreThreadLocal.setSession(session);
			//for partial replication get the last branch id and commit time from the index
			//right now index is fully recreated
			final IndexMigrationReplicationContext replicationContext = new IndexMigrationReplicationContext(context, maxCdoBranchId, failedCommitTimestamp - 1, session);
			cdoRepository.replicate(replicationContext);
			// update repository state after the re-indexing
			return new ReindexResult(replicationContext.getFailedCommitTimestamp(),
					replicationContext.getProcessedCommits(), replicationContext.getSkippedCommits(), replicationContext.getException());
		} finally {
			features.disable(reindexToggle);
			StoreThreadLocal.release();
			session.close();
			repository.checkHealth();
		}
	}

	public static ReindexRequestBuilder builder() {
		return new ReindexRequestBuilder();
	}

}
