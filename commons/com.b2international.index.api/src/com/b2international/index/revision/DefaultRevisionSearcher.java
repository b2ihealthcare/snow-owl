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
package com.b2international.index.revision;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.IOException;

import com.b2international.index.Searcher;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Query;
import com.google.common.collect.Iterables;

/**
 * @since 4.7
 */
public class DefaultRevisionSearcher implements RevisionSearcher {

	private final RevisionBranch branch;
	private final Searcher searcher;

	public DefaultRevisionSearcher(RevisionBranch branch, Searcher searcher) {
		this.branch = branch;
		this.searcher = searcher;
	}
	
	@Override
	public <T extends Revision> T get(Class<T> type, long storageKey) throws IOException {
		final Query<T> query = Query.builder(type).selectAll().where(Revision.matchRevisionOnBranch(branch, storageKey)).build();
		return Iterables.getOnlyElement(search(query), null);
	}

	@Override
	public <T> Iterable<T> search(Query<T> query) throws IOException {
		if (Revision.class.isAssignableFrom(query.getType())) {
			// rewrite query if we are looking for revision, otherwise if we are looking for unversioned nested use it as is
			query = Query.builder(query.getType())
					.select(query.getSelect())
					.where(Expressions.and(query.getWhere(), Revision.branchFilter(branch)))
					.sortBy(query.getSortBy())
					.limit(query.getLimit())
					.offset(query.getOffset()).build();
		} else {
			checkArgument(Revision.class.isAssignableFrom(query.getParentType()), "Searching non-revision documents require a revision parent type: %s", query);
			// run a query on the parent documents with nested match on the children
			query = Query.builder(query.getType(), query.getParentType())
					.select(query.getSelect())
					.where(Expressions.and(query.getWhere(), Expressions.hasParent(query.getParentType(), Revision.branchFilter(branch))))
					.sortBy(query.getSortBy())
					.limit(query.getLimit())
					.offset(query.getOffset())
					.build();
		}
		return searcher.search(query);
	}

}
