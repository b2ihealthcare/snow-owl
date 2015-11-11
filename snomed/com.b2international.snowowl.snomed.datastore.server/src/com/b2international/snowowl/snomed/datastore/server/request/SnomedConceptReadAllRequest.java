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
package com.b2international.snowowl.snomed.datastore.server.request;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.index.IIndexQueryAdapter;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.exceptions.IllegalQueryParameterException;
import com.b2international.snowowl.snomed.core.domain.SearchKind;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.snomed.datastore.escg.IEscgQueryEvaluatorService;
import com.b2international.snowowl.snomed.datastore.index.SnomedConceptReducedQueryAdapter;
import com.b2international.snowowl.snomed.datastore.index.SnomedDOIQueryAdapter;
import com.b2international.snowowl.snomed.datastore.index.SnomedIndexService;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptIndexEntry;
import com.b2international.snowowl.snomed.dsl.query.SyntaxErrorException;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

/**
 * @since 4.5
 */
final class SnomedConceptReadAllRequest extends SnomedConceptRequest<BranchContext, SnomedConcepts> {

	private int offset;
	private int limit;
	private Map<SearchKind, String> filters;

	SnomedConceptReadAllRequest(int offset, int limit, Map<SearchKind, String> filters) {
		checkArgument(offset >= 0, "Offset should be greater than or equal to zero");
		checkArgument(limit > 0, "Limit should be greater than zero");
		this.offset = offset;
		this.limit = limit;
		this.filters = filters == null ? Collections.<SearchKind, String>emptyMap() : filters;
	}

	@Override
	public SnomedConcepts execute(BranchContext context) {
		final IBranchPath branchPath = context.branch().branchPath();
		final SnomedIndexService index = context.service(SnomedIndexService.class);
		
		final IIndexQueryAdapter<SnomedConceptIndexEntry> queryAdapter = getQuery(context, branchPath);
		final int total = index.getHitCount(branchPath, queryAdapter);
		final List<SnomedConceptIndexEntry> hits = index.search(branchPath, queryAdapter, offset, limit);
		return new SnomedConcepts(Lists.transform(hits, getConverter(branchPath)), offset, limit, total);
	}

	private IIndexQueryAdapter<SnomedConceptIndexEntry> getQuery(RepositoryContext context, IBranchPath branch) {
		if (filters.isEmpty()) {
			return new SnomedConceptReducedQueryAdapter();
		} else {
			final Query restrictionQuery;
			
			if (filters.containsKey(SearchKind.ESCG)) {
				try {
					restrictionQuery = context.service(IEscgQueryEvaluatorService.class).evaluateBooleanQuery(branch, filters.get(SearchKind.ESCG));
				} catch (final SyntaxErrorException e) {
					throw new IllegalQueryParameterException(e.getMessage());
				}
			} else {
				restrictionQuery = new MatchAllDocsQuery();
			}
			
			final String label = Strings.nullToEmpty(filters.get(SearchKind.LABEL));
			return new SnomedDOIQueryAdapter(label, "", restrictionQuery);
		}
	}

	@Override
	protected Class<SnomedConcepts> getReturnType() {
		return SnomedConcepts.class;
	}
	
}
