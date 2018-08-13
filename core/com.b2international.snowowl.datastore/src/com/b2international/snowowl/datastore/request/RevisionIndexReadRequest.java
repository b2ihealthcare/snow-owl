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
package com.b2international.snowowl.datastore.request;

import java.io.IOException;

import com.b2international.commons.exceptions.IllegalQueryParameterException;
import com.b2international.index.Hits;
import com.b2international.index.Scroll;
import com.b2international.index.Searcher;
import com.b2international.index.aggregations.Aggregation;
import com.b2international.index.aggregations.AggregationBuilder;
import com.b2international.index.query.Query;
import com.b2international.index.query.QueryParseException;
import com.b2international.index.revision.RevisionIndex;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.events.DelegatingRequest;
import com.b2international.snowowl.core.events.Request;

/**
 * A subclass of {@link DelegatingRequest} that:
 * <ul>
 * <li>opens an index read transaction using {@link RevisionIndex};
 * <li>executes the delegate with a {@link BranchContext} that allows access to {@link RevisionSearcher} from the read
 * transaction.
 * </ul>
 * 
 * @since 4.5
 */
public final class RevisionIndexReadRequest<B> extends DelegatingRequest<BranchContext, BranchContext, B> {

	private final boolean snapshot;

	public RevisionIndexReadRequest(Request<BranchContext, B> next) {
		this(next, true);
	}
	
	public RevisionIndexReadRequest(Request<BranchContext, B> next, boolean snapshot) {
		super(next);
		this.snapshot = snapshot;
	}
	
	@Override
	public B execute(final BranchContext context) {
		final String branchPath = context.branchPath();
		RevisionIndex index = context.service(RevisionIndex.class);
		if (snapshot) {
			return index.read(branchPath, searcher -> {
				try {
					return next(context.inject()
							.bind(RevisionSearcher.class, searcher)
							.build());
				} catch (QueryParseException e) {
					throw new IllegalQueryParameterException(e.getMessage());
				}
			});
		} else {
			return next(context.inject()
					.bind(RevisionSearcher.class, new RevisionSearcher() {
						
						@Override
						public <T> Hits<T> scroll(Scroll<T> scroll) throws IOException {
							return index.read(branchPath, searcher -> searcher.scroll(scroll));
						}
						
						@Override
						public void cancelScroll(String scrollId) {
							index.read(branchPath, searcher -> {
								searcher.cancelScroll(scrollId);
								return null;
							});
						}
						
						@Override
						public <T> Aggregation<T> aggregate(AggregationBuilder<T> aggregation) throws IOException {
							return index.read(branchPath, searcher -> searcher.aggregate(aggregation));
						}
						
						@Override
						public Searcher searcher() {
							return index.read(branchPath, searcher -> searcher.searcher());
						}
						
						@Override
						public <T> Hits<T> search(Query<T> query) throws IOException {
							return index.read(branchPath, searcher -> searcher.search(query));
						}
						
						@Override
						public <T> Iterable<T> get(Class<T> type, Iterable<String> keys) throws IOException {
							return index.read(branchPath, searcher -> searcher.get(type, keys));
						}
						
						@Override
						public <T> T get(Class<T> type, String key) throws IOException {
							return index.read(branchPath, searcher -> searcher.get(type, key));
						}
						
						@Override
						public String branch() {
							return branchPath;
						}
					})
					.build());
		}
	}
	
}
