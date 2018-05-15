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
package com.b2international.snowowl.datastore.request;

import com.b2international.commons.exceptions.IllegalQueryParameterException;
import com.b2international.index.DocSearcher;
import com.b2international.index.Searcher;
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
 * <li>executes the delegate with a {@link BranchContext} that allows access to {@link Searcher} and {@link RevisionSearcher} from the read
 * transaction.
 * </ul>
 * 
 * @since 4.5
 */
public final class RevisionIndexReadRequest<B> extends DelegatingRequest<BranchContext, BranchContext, B> {

	public RevisionIndexReadRequest(Request<BranchContext, B> next) {
		super(next);
	}
	
	@Override
	public B execute(final BranchContext context) {
		return context.service(RevisionIndex.class)
				.read(context.branchPath(), index -> {
					try {
						return next(context.inject()
								.bind(Searcher.class, index)
								.bind(RevisionSearcher.class, index)
								.bind(DocSearcher.class, index.searcher())
								.build());
					} catch (QueryParseException e) {
						throw new IllegalQueryParameterException(e.getMessage());
					}
				});
	}
	
}
