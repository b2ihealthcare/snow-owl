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

import java.util.List;

import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.index.IIndexQueryAdapter;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.exceptions.IllegalQueryParameterException;
import com.b2international.snowowl.snomed.core.domain.ISnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SearchKind;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.snomed.datastore.escg.IEscgQueryEvaluatorService;
import com.b2international.snowowl.snomed.datastore.index.SnomedConceptReducedQueryAdapter;
import com.b2international.snowowl.snomed.datastore.index.SnomedDOIQueryAdapter;
import com.b2international.snowowl.snomed.datastore.index.SnomedIndexService;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptIndexEntry;
import com.b2international.snowowl.snomed.datastore.services.SnomedBranchRefSetMembershipLookupService;
import com.b2international.snowowl.snomed.dsl.query.SyntaxErrorException;
import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

/**
 * @since 4.5
 */
final class SnomedConceptSearchRequest extends SearchRequest<SnomedConcepts> {

	SnomedConceptSearchRequest() {}

	@Override
	public SnomedConcepts execute(BranchContext context) {
		final IBranchPath branchPath = context.branch().branchPath();
		final SnomedIndexService index = context.service(SnomedIndexService.class);
		
		final IIndexQueryAdapter<SnomedConceptIndexEntry> queryAdapter = getQuery(context, branchPath);
		final int total = index.getHitCount(branchPath, queryAdapter);
		final List<SnomedConceptIndexEntry> hits = index.search(branchPath, queryAdapter, offset(), limit());
		return new SnomedConcepts(Lists.transform(hits, getConverter(branchPath)), offset(), limit(), total);
	}
	
	// TODO move this to SnomedComponentConverters factory class
	protected final Function<SnomedConceptIndexEntry, ISnomedConcept> getConverter(final IBranchPath branchPath) {
		return new SnomedConceptConverter(new SnomedBranchRefSetMembershipLookupService(branchPath));
	}

	private IIndexQueryAdapter<SnomedConceptIndexEntry> getQuery(RepositoryContext context, IBranchPath branch) {
		if (options().isEmpty()) {
			return new SnomedConceptReducedQueryAdapter();
		} else {
			final Query restrictionQuery;
			
			if (options().containsKey(SearchKind.ESCG.name())) {
				try {
					restrictionQuery = context.service(IEscgQueryEvaluatorService.class).evaluateBooleanQuery(branch, options().getString(SearchKind.ESCG.name()));
				} catch (final SyntaxErrorException e) {
					throw new IllegalQueryParameterException(e.getMessage());
				}
			} else {
				restrictionQuery = new MatchAllDocsQuery();
			}
			
			final String label = Strings.nullToEmpty(options().getString(SearchKind.LABEL.name()));
			return new SnomedDOIQueryAdapter(label, "", restrictionQuery);
		}
	}

	@Override
	protected Class<SnomedConcepts> getReturnType() {
		return SnomedConcepts.class;
	}
	
}
