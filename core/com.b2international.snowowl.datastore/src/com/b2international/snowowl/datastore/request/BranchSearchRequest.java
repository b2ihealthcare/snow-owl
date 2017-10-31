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
package com.b2international.snowowl.datastore.request;

import java.io.IOException;

import com.b2international.index.Hits;
import com.b2international.index.Searcher;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Expressions.ExpressionBuilder;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.branch.BranchManager;
import com.b2international.snowowl.core.branch.Branches;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.datastore.internal.branch.InternalBranch;
import com.google.common.collect.ImmutableList;

/**
 * @since 4.1
 */
final class BranchSearchRequest extends SearchIndexResourceRequest<RepositoryContext, Branches> {

	enum OptionKey {
		
		/**
		 * Filter branches by their parent path
		 */
		PARENT,
		
		/**
		 * Filter branches by their name
		 */
		NAME,
		
	}
	
	BranchSearchRequest() {}
	
	@Override
	protected Branches createEmptyResult(int limit) {
		return new Branches(limit, 0);
	}

	@Override
	protected Branches doExecute(RepositoryContext context) throws IOException {
		final Searcher searcher = context.service(Searcher.class);
		final ExpressionBuilder queryBuilder = Expressions.builder();

		addIdFilter(queryBuilder, ids -> Expressions.matchAny("path", ids));
		
		if (containsKey(OptionKey.PARENT)) {
			queryBuilder.filter(Expressions.matchAny("parentPath", getCollection(OptionKey.PARENT, String.class)));
		}
		
		if (containsKey(OptionKey.NAME)) {
			queryBuilder.filter(Expressions.matchAny("name", getCollection(OptionKey.NAME, String.class)));
		}
		
		final Hits<InternalBranch> matches = searcher.search(select(InternalBranch.class)
				.where(queryBuilder.build())
				.scroll(scrollKeepAlive())
				.limit(limit())
				.sortBy(sortBy())
				.build());
		
		final BranchManager branchManager = context.service(BranchManager.class);
		for (InternalBranch branch : matches) {
			branch.setBranchManager(branchManager);
		}
		
		return new Branches(ImmutableList.<Branch>copyOf(matches), matches.getScrollId(), limit(), matches.getTotal());
	}

}
