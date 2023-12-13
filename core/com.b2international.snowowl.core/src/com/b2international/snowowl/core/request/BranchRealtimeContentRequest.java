/*
 * Copyright 2023 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.snowowl.core.request;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;

import com.b2international.commons.metric.Metrics;
import com.b2international.index.Hits;
import com.b2international.index.Searcher;
import com.b2international.index.aggregations.Aggregation;
import com.b2international.index.aggregations.AggregationBuilder;
import com.b2international.index.query.Knn;
import com.b2international.index.query.Query;
import com.b2international.index.revision.RevisionBranchRef;
import com.b2international.index.revision.RevisionIndex;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.domain.RepositoryBranchContext;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.events.DelegatingRequest;
import com.b2international.snowowl.core.events.Request;

/**
 * @since 8.10.0
 * @param <B>
 */
public final class BranchRealtimeContentRequest<B> extends DelegatingRequest<RepositoryContext, BranchContext, B> {

	private static final long serialVersionUID = 1L;

	private final String branchPath;

	public BranchRealtimeContentRequest(String branchPath, Request<BranchContext, B> next) {
		super(next);
		this.branchPath = checkNotNull(branchPath, "branchPath");
	}

	@Override
	public B execute(RepositoryContext context) {
		RevisionIndex index = context.service(RevisionIndex.class);
		return next(new RepositoryBranchContext(context, branchPath, new RevisionSearcher() {

			private Metrics metrics = Metrics.NOOP;

			@Override
			public <T> Aggregation<T> aggregate(AggregationBuilder<T> aggregation) throws IOException {
				return index.read(branchPath, searcher -> {
					searcher.setMetrics(metrics);
					return searcher.aggregate(aggregation);
				});
			}

			@Override
			public Searcher searcher() {
				return index.read(branchPath, searcher -> {
					searcher.setMetrics(metrics);
					return searcher.searcher();
				});
			}

			@Override
			public <T> Hits<T> search(Query<T> query) throws IOException {
				return index.read(branchPath, searcher -> {
					searcher.setMetrics(metrics);
					return searcher.search(query);
				});
			}

			@Override
			public <T> Iterable<T> get(Class<T> type, Iterable<String> keys) throws IOException {
				return index.read(branchPath, searcher -> {
					searcher.setMetrics(metrics);
					return searcher.get(type, keys);
				});
			}

			@Override
			public <T> T get(Class<T> type, String key) throws IOException {
				return index.read(branchPath, searcher -> {
					searcher.setMetrics(metrics);
					return searcher.get(type, key);
				});
			}

			@Override
			public <T> Hits<T> knn(Knn<T> knn) throws IOException {
				return index.read(branchPath, searcher -> {
					searcher.setMetrics(metrics);
					return searcher.knn(knn);
				});
			}

			@Override
			public RevisionBranchRef ref() {
				return index.read(branchPath, searcher -> {
					searcher.setMetrics(metrics);
					return searcher.ref();
				});
			}
			
			@Override
			public String branch() {
				return branchPath;
			}
			
			@Override
			public void setMetrics(Metrics metrics) {
				this.metrics = metrics;
			}
			
		}));
	}

}
