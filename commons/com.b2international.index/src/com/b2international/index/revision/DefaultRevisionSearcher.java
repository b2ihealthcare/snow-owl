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
package com.b2international.index.revision;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.IOException;
import java.util.Collections;

import com.b2international.index.Hits;
import com.b2international.index.Scroll;
import com.b2international.index.Searcher;
import com.b2international.index.aggregations.Aggregation;
import com.b2international.index.aggregations.AggregationBuilder;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Query;
import com.google.common.collect.Iterables;

/**
 * @since 4.7
 */
public class DefaultRevisionSearcher implements RevisionSearcher {

	private final RevisionBranchRef branch;
	private final Searcher searcher;

	public DefaultRevisionSearcher(RevisionBranchRef branch, Searcher searcher) {
		this.branch = branch;
		this.searcher = searcher;
	}
	
	@Override
	public Searcher searcher() {
		return searcher;
	}
	
	@Override
	public <T> T get(Class<T> type, String key) throws IOException {
		if (Revision.class.isAssignableFrom(type)) {
			final Query<T> query = Query.select(type).where(Expressions.exactMatch(Revision.Fields.ID, key)).limit(2).build();
			return Iterables.getOnlyElement(search(query), null);
		} else {
			return searcher.get(type, key);
		}
	}
	
	@Override
	public <T> Iterable<T> get(Class<T> type, Iterable<String> keys) throws IOException {
		if (Iterables.isEmpty(keys)) {
			return Collections.emptySet();
		} else if (Revision.class.isAssignableFrom(type)) {
			return search(Query.select(type).where(Expressions.matchAny(Revision.Fields.ID, keys)).limit(Iterables.size(keys)).build());
		} else {
			return searcher.get(type, keys);
		}
	}

	@Override
	public <T> Hits<T> search(Query<T> query) throws IOException {
		if (query.getParentType() == null && Revision.class.isAssignableFrom(query.getFrom())) {
			// rewrite query if we are looking for revision, otherwise if we are looking for unversioned nested use it as is
			query = Query.select(query.getSelect())
					.from(query.getFrom())
					.fields(query.getFields())
					.where(
						Expressions.builder()
							.must(query.getWhere())
							.filter(branch.toRevisionFilter())
						.build()
					)
					.sortBy(query.getSortBy())
					.limit(query.getLimit())
					.scroll(query.getScrollKeepAlive())
					.searchAfter(query.getSearchAfter())
					.withScores(query.isWithScores())
					.build();
		} else {
			checkArgument(Revision.class.isAssignableFrom(query.getParentType()), "Searching non-revision documents require a revision parent type: %s", query);
			// run a query on the parent documents with nested match on the children
			query = Query.select(query.getSelect())
					.parent(query.getParentType())
					.fields(query.getFields())
					.where(Expressions.builder()
							.must(query.getWhere())
							.filter(Expressions.hasParent(query.getParentType(), branch.toRevisionFilter()))
							.build())
					.sortBy(query.getSortBy())
					.limit(query.getLimit())
					.scroll(query.getScrollKeepAlive())
					.searchAfter(query.getSearchAfter())
					.withScores(query.isWithScores())
					.build();
		}
		return searcher.search(query);
	}
	
	@Override
	public <T> Aggregation<T> aggregate(AggregationBuilder<T> aggregation) throws IOException {
		aggregation.query(Expressions.builder()
				.must(aggregation.getQuery())
				.filter(branch.toRevisionFilter())
			.build());
		return searcher.aggregate(aggregation);
	}
	
	@Override
	public <T> Hits<T> scroll(Scroll<T> scroll) throws IOException {
		return searcher.scroll(scroll);
	}
	
	@Override
	public void cancelScroll(String scrollId) {
		searcher.cancelScroll(scrollId);
	}
	
	@Override
	public String branch() {
		return branch.path();
	}

}
