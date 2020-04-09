/*
 * Copyright 2011-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.branch;

import static com.google.common.collect.Maps.newHashMap;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.b2international.index.Hits;
import com.b2international.index.query.Expression;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Expressions.ExpressionBuilder;
import com.b2international.index.revision.BaseRevisionBranching;
import com.b2international.index.revision.RevisionBranch;
import com.b2international.index.revision.RevisionBranch.BranchState;
import com.b2international.snowowl.core.authorization.RepositoryAccessControl;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.identity.Permission;
import com.b2international.snowowl.core.request.SearchIndexResourceRequest;
import com.google.common.collect.ImmutableList;

/**
 * @since 4.1
 */
final class BranchSearchRequest extends SearchIndexResourceRequest<RepositoryContext, Branches, RevisionBranch> implements RepositoryAccessControl {

	enum OptionKey {
		
		/**
		 * Filter branches by their parent path
		 */
		PARENT,
		
		/**
		 * Filter branches by their name
		 */
		NAME, 
		
		/**
		 * Filter branches by numeric identifier
		 */
		BRANCH_ID,
	}
	
	BranchSearchRequest() {}
	
	@Override
	protected Branches createEmptyResult(int limit) {
		return new Branches(Collections.emptyList(), null, limit, 0);
	}

	@Override
	protected Expression prepareQuery(RepositoryContext context) {
		ExpressionBuilder queryBuilder = Expressions.builder();
				
		addIdFilter(queryBuilder, ids -> Expressions.matchAny(RevisionBranch.Fields.PATH, ids));
		
		if (containsKey(OptionKey.PARENT)) {
			queryBuilder.filter(Expressions.matchAny(RevisionBranch.Fields.PARENT_PATH, getCollection(OptionKey.PARENT, String.class)));
		}
		
		if (containsKey(OptionKey.NAME)) {
			queryBuilder.filter(Expressions.matchAny(RevisionBranch.Fields.NAME, getCollection(OptionKey.NAME, String.class)));
		}
		
		if (containsKey(OptionKey.BRANCH_ID)) {
			queryBuilder.filter(Expressions.matchAnyLong(RevisionBranch.Fields.ID, getCollection(OptionKey.BRANCH_ID, Long.class)));
		}
		
		return queryBuilder.build();
	}
	
	@Override
	protected Class<RevisionBranch> getDocumentType() {
		return RevisionBranch.class;
	}

	@Override
	protected Branches toCollectionResource(RepositoryContext context, Hits<RevisionBranch> hits) {
		final BaseRevisionBranching branching = context.service(BaseRevisionBranching.class);
		final List<Branch> branchHits = toBranchData(branching, hits);
		
		expand(context, branchHits);
		
		return new Branches(branchHits, hits.getSearchAfter(), limit(), hits.getTotal());
	}

	private List<Branch> toBranchData(final BaseRevisionBranching branching, final Iterable<RevisionBranch> hits) {
		final Map<String, RevisionBranch> branchesById = newHashMap();
		final ImmutableList.Builder<Branch> branches = ImmutableList.builder();
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
			branches.add(new Branch(doc, state, BranchPathUtils.createPath(doc.getPath()), doc.getMergeSources()));
		}
		return branches.build();
	}

	private void expand(RepositoryContext context, List<Branch> branchHits) {
		if (branchHits.isEmpty()) return;
		
		if (expand().containsKey(Branch.Expand.CHILDREN)) {
			final BaseRevisionBranching branching = context.service(BaseRevisionBranching.class);
			for (Branch branchHit : branchHits) {
				final List<Branch> children = toBranchData(branching, branching.getChildren(branchHit.path()));
				branchHit.setChildren(new Branches(children, null, children.size(), children.size()));
			}
		}
	}

	@Override
	public String getOperation() {
		return Permission.BROWSE;
	}

}
