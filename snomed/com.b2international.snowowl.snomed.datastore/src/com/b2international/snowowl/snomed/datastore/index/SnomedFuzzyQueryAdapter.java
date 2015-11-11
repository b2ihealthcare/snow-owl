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

import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.automaton.LevenshteinAutomata;

import com.b2international.snowowl.core.TextConstants;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.index.IIndexService;
import com.b2international.snowowl.datastore.index.AbstractIndexService;
import com.b2international.snowowl.datastore.index.DocumentWithScore;
import com.b2international.snowowl.datastore.index.IndexQueryBuilder;
import com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedMappings;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;

/**
 * A query adapter that looks for fuzzified search terms in synonyms, expecting at least one match. 
 * 
 */
public class SnomedFuzzyQueryAdapter extends SnomedConceptIndexQueryAdapter implements Serializable {
	
	private static final long serialVersionUID = 7660959539341026254L;
	@Nullable private final Query restrictionQuery;
	private final String userId;
	
	public SnomedFuzzyQueryAdapter(final String searchstring, String userId, final @Nullable String[] conceptIds)  {
		super(Strings.nullToEmpty(searchstring).toLowerCase(), SEARCH_DEFAULT, conceptIds);
		this.userId = userId;
		restrictionQuery = null;
	}
	
	public SnomedFuzzyQueryAdapter(final String searchstring, String userId, @Nullable final Query restrictionQuery)  {
		super(Strings.nullToEmpty(searchstring).toLowerCase(), SEARCH_DEFAULT, null);
		this.userId = userId;
		this.restrictionQuery = restrictionQuery;
	}

	@Override
	public Query createQuery() {
		
		final BooleanQuery query = new BooleanQuery();
		
		// Only find active and not excluded concepts
		query.add(SnomedMappings.newQuery().active().matchAll(), Occur.MUST);
		
		// If the search string is empty, build a query that finds all active concepts
		if (searchString.isEmpty()) {
			
			if (null != restrictionQuery) {
				
				final BooleanQuery booleanquery = new BooleanQuery();
				booleanquery.add(query, Occur.MUST);
				booleanquery.add(restrictionQuery, Occur.MUST);
				
				return booleanquery; 
				
			}
			
			return query;
		}
		
		final Splitter tokenSplitter = Splitter.on(TextConstants.WHITESPACE_OR_DELIMITER_MATCHER)
				.omitEmptyStrings();

		final BooleanQuery fuzzyQueryPart = new BooleanQuery();
		int tokenCount = 0;
		
		for (final String token : tokenSplitter.split(searchString)) {
			final FuzzyQuery fuzzyQuery = new FuzzyQuery(new Term(SnomedIndexBrowserConstants.CONCEPT_SYNONYM, token), 
					LevenshteinAutomata.MAXIMUM_SUPPORTED_DISTANCE, 1);
			fuzzyQueryPart.add(fuzzyQuery, Occur.SHOULD);
			++tokenCount;
		}

		final int minShouldMatch = Math.max(1, tokenCount - 2);
		fuzzyQueryPart.setMinimumNumberShouldMatch(minShouldMatch);
		
		query.add(fuzzyQueryPart, Occur.MUST);
		
		if (null != restrictionQuery) {
			
			final BooleanQuery booleanquery = new BooleanQuery();
			booleanquery.add(query, Occur.MUST);
			booleanquery.add(restrictionQuery, Occur.MUST);
			
			return booleanquery; 
			
		}
		
		return query;
	}

	@Override
	protected List<DocumentWithScore> doSearch(final IIndexService<? super SnomedConceptIndexEntry> indexService, final IBranchPath branchPath, final int limit) {
		final AbstractIndexService<?> abstractIndexService = checkAndCast(indexService, AbstractIndexService.class); 
		final List<DocumentWithScore> documents = abstractIndexService.search(branchPath, createScoreQuery(branchPath), createFilter(), createSort(), limit);
		return documents;
	}
	
	private Query createScoreQuery(IBranchPath branchPath) {
		final Query mainQuery = createQuery();
		@Nullable final BooleanQuery searchProfileExclusionQuery = SearchProfileQueryProvider.provideExclusionQuery(branchPath, userId);
		
		if (null == searchProfileExclusionQuery) {
			return mainQuery;
		} else {
			searchProfileExclusionQuery.add(mainQuery, Occur.MUST); //AND with the main query
			return searchProfileExclusionQuery; 
		}
	}

	@Override
	public int getHitCount(final IIndexService<? super SnomedConceptIndexEntry> indexService, final IBranchPath branchPath) {
		final AbstractIndexService<?> abstractIndexService = checkAndCast(indexService, AbstractIndexService.class); 
		return abstractIndexService.getHitCount(branchPath, createScoreQuery(branchPath), createFilter());
	}
	
	@Override
	protected IndexQueryBuilder createIndexQueryBuilder() {
		throw new IllegalStateException();
	}
}