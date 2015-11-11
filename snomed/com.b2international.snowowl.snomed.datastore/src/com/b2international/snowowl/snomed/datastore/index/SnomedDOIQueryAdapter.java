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
package com.b2international.snowowl.snomed.datastore.index;

import static com.b2international.commons.ClassUtils.checkAndCast;

import java.io.Serializable;
import java.util.List;

import javax.annotation.Nullable;

import org.apache.lucene.queries.CustomScoreQuery;
import org.apache.lucene.queries.function.FunctionQuery;
import org.apache.lucene.queries.function.ValueSource;
import org.apache.lucene.queries.function.valuesource.FloatFieldSource;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;

import com.b2international.commons.StringUtils;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.index.IIndexService;
import com.b2international.snowowl.datastore.index.AbstractIndexService;
import com.b2international.snowowl.datastore.index.DocumentWithScore;
import com.b2international.snowowl.datastore.index.IndexQueryBuilder;
import com.b2international.snowowl.datastore.index.IndexUtils;
import com.b2international.snowowl.datastore.index.mapping.Mappings;
import com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedMappings;
import com.google.common.base.Optional;
import com.google.common.base.Strings;

/**
 * The beginning of a new query parser.
 * <p>
 * diabetes mellitis
 * <pre>+conceptStatus:1 label:"diabetes mellitus"^3.0 ((synonym:diabetes synonym:mellitus)^2.0) +(+synonym:diabetes* +synonym:mellitus*)</pre>
 * <p>
 * 313436004 2 48245008 329382 266883004
 * <pre>+conceptStatus:1 +(conceptID:313436004 conceptID:48245008 conceptID:329382 conceptID:266883004) label:"2"^3.0 ((synonym:2)^2.0) +(+synonym:2*)</pre>
 * <p> 
 * bl p st sy  
 * <pre>+conceptStatus:1 label:"bl p st sy"^3.0 ((synonym:bl synonym:p synonym:st synonym:sy)^2.0) +(+synonym:bl* +synonym:p* +synonym:st* +synonym:sy*)</pre>
 * 
 */
public class SnomedDOIQueryAdapter extends SnomedConceptIndexQueryAdapter implements Serializable {
	
	private static final long serialVersionUID = -3044881906076704431L;

	private static final ValueSource DOI_VALUE_SOURCE = new FloatFieldSource(SnomedIndexBrowserConstants.CONCEPT_DEGREE_OF_INTEREST);
	
	private final String userId;
	@Nullable private final Query restrictionQuery;
	
	public SnomedDOIQueryAdapter(final String searchString, final String userId, final @Nullable String[] conceptIds) {
		super(Strings.nullToEmpty(searchString).toLowerCase(), SEARCH_ACTIVE_CONCEPTS, conceptIds);
		this.userId = userId;
		this.restrictionQuery = null;
	}
	
	public SnomedDOIQueryAdapter(final String searchString, final String userId, @Nullable final Query restrictionQuery) {
		super(Strings.nullToEmpty(searchString).toLowerCase(), SEARCH_ACTIVE_CONCEPTS, null);
		this.userId = userId;
		this.restrictionQuery = restrictionQuery;
	}

	@Override
	protected IndexQueryBuilder createIndexQueryBuilder() {
		final Optional<Long> parsedSearchStringOptional = IndexUtils.parseLong(searchString);
		if (parsedSearchStringOptional.isPresent()) {
			return super.createIndexQueryBuilder()
					.finishIf(StringUtils.isEmpty(searchString))
					.require(new IndexQueryBuilder()
					.match(SnomedMappings.newQuery().id(parsedSearchStringOptional.get()).matchAll()).toQuery()).boost(10.0f)
					.matchAllTokenizedTerms(Mappings.label().fieldName(), searchString).boost(5.0f)
					.matchAllTokenizedTerms(SnomedIndexBrowserConstants.CONCEPT_SYNONYM, searchString).boost(4.0f)
					.matchAllTokenizedTermPrefixSequences(Mappings.label().fieldName(), searchString).boost(3.0f)
					.matchTokenizedTermSequence(Mappings.label().fieldName(), searchString).boost(2.0f)
					.matchAllTokenizedTermPrefixes(SnomedIndexBrowserConstants.CONCEPT_SYNONYM, searchString);
		} else {
			return super.createIndexQueryBuilder()
					.finishIf(StringUtils.isEmpty(searchString))
					.require(new IndexQueryBuilder()
					.matchAllTokenizedTerms(Mappings.label().fieldName(), searchString).boost(5.0f)
					.matchAllTokenizedTerms(SnomedIndexBrowserConstants.CONCEPT_SYNONYM, searchString).boost(4.0f)
					.matchAllTokenizedTermPrefixSequences(Mappings.label().fieldName(), searchString).boost(3.0f)
					.matchTokenizedTermSequence(Mappings.label().fieldName(), searchString).boost(2.0f)
					.matchAllTokenizedTermPrefixes(SnomedIndexBrowserConstants.CONCEPT_SYNONYM, searchString));
		}
	}

	private final Query createScoreQuery(final IBranchPath branchPath) {
		
		final Query mainQuery = createQuery();

		@Nullable final BooleanQuery searchProfileQuery = SearchProfileQueryProvider.provideQuery(branchPath, userId);
		
		if (null != restrictionQuery) {
			
			final BooleanQuery query = new BooleanQuery();
			query.add(mainQuery, Occur.MUST);
			query.add(restrictionQuery, Occur.MUST);
			
			if (null == searchProfileQuery) {
				
				return new CustomScoreQuery(query, new FunctionQuery(DOI_VALUE_SOURCE));
				
			} else {
				
				//AND with the ID restriction query
				searchProfileQuery.add(query, Occur.MUST);
				
				return new CustomScoreQuery(searchProfileQuery, new FunctionQuery(DOI_VALUE_SOURCE)); 
				
			}
			
		}
		
		if (null == searchProfileQuery) {
			
			return new CustomScoreQuery(mainQuery, new FunctionQuery(DOI_VALUE_SOURCE));
			
		} else {
			
			//AND with the main query
			searchProfileQuery.add(mainQuery, Occur.MUST);
			
			return new CustomScoreQuery(searchProfileQuery, new FunctionQuery(DOI_VALUE_SOURCE)); 
			
		}
		
	}
	
	@Override
	protected List<DocumentWithScore> doSearch(final IIndexService<? super SnomedConceptIndexEntry> indexService, final IBranchPath branchPath, final int limit) {
		final AbstractIndexService<?> abstractIndexService = checkAndCast(indexService, AbstractIndexService.class); 
		final List<DocumentWithScore> documents = abstractIndexService.search(branchPath, createScoreQuery(branchPath), createFilter(), createSort(), limit);
		return documents;
	}
	
	@Override
	protected List<DocumentWithScore> doSearch(final IIndexService<? super SnomedConceptIndexEntry> indexService, final IBranchPath branchPath, final int offset, final int limit) {
		final AbstractIndexService<?> abstractIndexService = checkAndCast(indexService, AbstractIndexService.class); 
		final List<DocumentWithScore> documents = abstractIndexService.search(branchPath, createScoreQuery(branchPath), createFilter(), createSort(), offset, limit);
		return documents;
	}
	
	@Override
	public int getHitCount(final IIndexService<? super SnomedConceptIndexEntry> indexService, final IBranchPath branchPath) {
		final AbstractIndexService<?> abstractIndexService = checkAndCast(indexService, AbstractIndexService.class); 
		return abstractIndexService.getHitCount(branchPath, createScoreQuery(branchPath), createFilter());
	}
	
	/* (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.index.AbstractIndexQueryAdapter#searchIds(com.b2international.snowowl.core.api.index.IIndexService, com.b2international.snowowl.core.api.IBranchPath, int)
	 */
	@Override
	public List<String> searchIds(final IIndexService<? super SnomedConceptIndexEntry> indexService, final IBranchPath branchPath, final int limit) {
		final AbstractIndexService<?> abstractIndexService = checkAndCast(indexService, AbstractIndexService.class);
		return abstractIndexService.searchIds(branchPath, createScoreQuery(branchPath), createFilter(), createSort(), limit);
	}
	
	
}