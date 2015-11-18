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
import com.b2international.snowowl.datastore.server.snomed.index.SnomedIndexServerService;
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

	public static final String OPTION_EXPAND = "expand";
	
	public static final String OPTION_LOCALES = "locales";

//	private static class DescriptionClause {
//		
//		private final String term;
//		private final List<Long> typeIds;
//		private final Boolean status;
//		private final String refSetId;
//		private final Acceptability acceptability;
//		
//		public DescriptionClause(String term, List<Long> typeIds, boolean status, String refSetId, Acceptability acceptability) {
//			this.term = term;
//			this.typeIds = typeIds;
//			this.status = status;
//			this.refSetId = refSetId;
//			this.acceptability = acceptability;
//		}
//
//		public Query createQuery() {
//			Query descriptionTermQuery = new DisjunctionMaxQuery(0.0f);
//
//			return descriptionTermQuery;
//		}
//		
//		public Filter createFilter() {
//			BooleanFilter filter = new BooleanFilter();
//			
//			if (status != null) {
//				filter.add(new TermFilter(SnomedMappings.active().toTerm(BooleanUtils.toInteger(status))), Occur.MUST);
//			}
//			
//			if (typeIds != null) {
//				filter.add(SnomedMappings.descriptionType().createTermsFilter(typeIds), Occur.MUST);
//			}
//			
//			if (acceptability != null) {
//				switch (acceptability) {
//					case ACCEPTABLE:
//						filter.add(new TermFilter(SnomedMappings.descriptionAcceptable().toTerm(refSetId)), Occur.MUST);
//						break;
//					case PREFERRED:
//						filter.add(new TermFilter(SnomedMappings.descriptionPreferred().toTerm(refSetId)), Occur.MUST);
//						break;
//					default:
//						throw new IllegalStateException("Unexpected acceptability '" + acceptability + "'.");
//				}
//			}
//			
//			return filter;
//		}
//	}
	
	SnomedConceptSearchRequest() {}

	@Override
	public SnomedConcepts execute(BranchContext context) {
		final IBranchPath branchPath = context.branch().branchPath();
		final SnomedIndexServerService index = (SnomedIndexServerService) context.service(SnomedIndexService.class);

		
		
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
			
			final String label = Strings.nullToEmpty(options().getString(SearchKind.PT.name()));
			return new SnomedDOIQueryAdapter(label, "", restrictionQuery);
		}
	}
	
	private String getSearchKind(SearchKind searchKind) {
		return options().getString(searchKind.name());
	}

	@Override
	protected Class<SnomedConcepts> getReturnType() {
		return SnomedConcepts.class;
	}
}
