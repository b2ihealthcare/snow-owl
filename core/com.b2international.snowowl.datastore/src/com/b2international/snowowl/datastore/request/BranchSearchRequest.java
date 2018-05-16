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

import static com.google.common.collect.Maps.newHashMap;

import java.util.Collections;
import java.util.Map;

import com.b2international.index.Hits;
import com.b2international.index.query.Expression;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Expressions.ExpressionBuilder;
import com.b2international.index.revision.BaseRevisionBranching;
import com.b2international.index.revision.RevisionBranch;
import com.b2international.index.revision.RevisionBranch.BranchState;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.branch.BranchData;
import com.b2international.snowowl.core.branch.Branches;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.google.common.collect.ImmutableList;

/**
 * @since 4.1
 */
final class BranchSearchRequest extends SearchIndexResourceRequest<RepositoryContext, Branches, RevisionBranch> {

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
		return new Branches(Collections.emptyList(), null, null, limit, 0);
	}

	@Override
	protected Expression prepareQuery(RepositoryContext context) {
		ExpressionBuilder queryBuilder = Expressions.builder();
				
		addIdFilter(queryBuilder, ids -> Expressions.matchAny("path", ids));
		
		if (containsKey(OptionKey.PARENT)) {
			queryBuilder.filter(Expressions.matchAny("parentPath", getCollection(OptionKey.PARENT, String.class)));
		}
		
		if (containsKey(OptionKey.NAME)) {
			queryBuilder.filter(Expressions.matchAny("name", getCollection(OptionKey.NAME, String.class)));
		}
		
		return queryBuilder.build();
	}
	
	@Override
	protected Class<RevisionBranch> getDocumentType() {
		return RevisionBranch.class;
	}

	@Override
	protected Branches toCollectionResource(RepositoryContext context, Hits<RevisionBranch> hits) {
		final ImmutableList.Builder<Branch> branches = ImmutableList.builder();
		final Map<String, RevisionBranch> branchesById = newHashMap();
		final BaseRevisionBranching branching = context.service(BaseRevisionBranching.class);
		for (RevisionBranch doc : hits) {
			final BranchState state; 
			if (doc.isMain()) {
				// XXX MAIN branch is always up to date
				state = BranchState.UP_TO_DATE;
			} else {
				final String parentPath = doc.getParentPath();
				if (!branchesById.containsKey(parentPath)) {
					branchesById.put(parentPath, branching.getBranch(parentPath));
				}
				state = doc.state(branchesById.get(parentPath));
			}
			branches.add(new BranchData(doc, state, BranchPathUtils.createPath(doc.getPath())));
		}
		return new Branches(branches.build(), hits.getScrollId(), hits.getSearchAfter(), limit(), hits.getTotal());
	}

}
