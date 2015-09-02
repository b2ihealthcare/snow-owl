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

import java.util.Set;

import javax.annotation.Nullable;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.util.BytesRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.CompareUtils;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.datastore.index.IndexUtils;
import com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants;
import com.b2international.snowowl.snomed.datastore.index.interest.ISearchProfileManager;
import com.b2international.snowowl.snomed.datastore.index.interest.SearchProfile;
import com.b2international.snowowl.snomed.datastore.index.interest.SearchProfileInterest;
import com.b2international.snowowl.snomed.datastore.index.interest.SearchProfileRule;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedMappings;
import com.google.common.base.Preconditions;

/**
 * Class for providing index queries based on the {@link SearchProfile} associated for a user.
 */
public abstract class SearchProfileQueryProvider {

	private static final Logger LOGGER = LoggerFactory.getLogger(SearchProfileQueryProvider.class);
	
	@Nullable public static BooleanQuery provideQuery(final IBranchPath branchPath, final String userId) {
		
		Preconditions.checkNotNull(branchPath, "Branch path argument cannot be null.");

		if (null == userId) {
			
			return null; //user ID is not specified
			//assuming no search profile 
		}
		
		final SearchProfile profile = getSearchProfile(userId);
		
		//search profile does not exist for user
		if (null == profile) {
			
			LOGGER.info("Search profile is not available for user: " + userId);
			
			return null;
			
		}
		
		final Set<SearchProfileRule> rules = profile.getRules();
		
		if (CompareUtils.isEmpty(rules)) {
			
			LOGGER.info("No search rules were found for user: " + userId);
			
			return null; 
			
		}
		
		BooleanQuery searchProfileQuery = null;
		
		for (final SearchProfileRule rule : rules) {
		
		
			final SearchProfileInterest interest = rule.getInterest();
			
			//in case of average not much we can do
			if (!SearchProfileInterest.AVERAGE.equals(interest)) {
				
				final String conceptId = rule.getContextId();
				final BytesRef contextId = IndexUtils.longToPrefixCoded(conceptId);
				
				if (null == searchProfileQuery) {
					
					searchProfileQuery = new BooleanQuery(true);
					
				}
				
				switch (rule.getDomain()) {
				
					case DESCENDANTS_OF_CONCEPT:
						
						final Query descendantQuery = SnomedMappings.newQuery().parent(conceptId).ancestor(conceptId).matchAny();
						
						switch (interest) {
	
							case BELOW_AVERAGE: //$FALL-THROUGH$
							case ABOVE_AVERAGE:
								
								searchProfileQuery.add(decorateQuery(descendantQuery, interest), Occur.SHOULD);
								break;
								
							case EXCLUDE:
								
								//exclude
								searchProfileQuery.add(descendantQuery, Occur.MUST_NOT);
								break;
								
							case AVERAGE:
								
								throw new IllegalStateException("Interest must not be average.");
								
							default:
								
								throw new IllegalArgumentException("Unknown interest type: " + interest);
								
						}
						
						break; //break 'descendants of concept' domain
						
					case WITHIN_A_MODULE:
						
						final Query moduleQuery = SnomedMappings.newQuery().module(conceptId).matchAll();
	
						switch (interest) {
	
							case BELOW_AVERAGE: //$FALL-THROUGH$
							case ABOVE_AVERAGE:
								
								searchProfileQuery.add(decorateQuery(moduleQuery, interest), Occur.SHOULD);
								break;
								
							case EXCLUDE:
								
								//exclude
								searchProfileQuery.add(moduleQuery, Occur.MUST_NOT);
								break;
								
							case AVERAGE:
								
								throw new IllegalStateException("Interest must not be average.");
								
							default:
								
								throw new IllegalArgumentException("Unknown interest type: " + interest);
								
						}
						
						break; //break 'within a module' domain
						
						
					case MAPPING_SOURCE_CONCEPTS:
						
						final Query mappingQuery = new TermQuery(new Term(SnomedIndexBrowserConstants.CONCEPT_REFERRING_MAPPING_REFERENCE_SET_ID, contextId));
						
						switch (interest) {
							
							case BELOW_AVERAGE: //$FALL-THROUGH$
							case ABOVE_AVERAGE:
								
								searchProfileQuery.add(decorateQuery(mappingQuery, interest), Occur.SHOULD);
								break;
								
							case EXCLUDE:
								
								//exclude
								searchProfileQuery.add(mappingQuery, Occur.MUST_NOT);
								break;
								
							case AVERAGE:
								
								throw new IllegalStateException("Interest must not be average.");
								
							default:
								
								throw new IllegalArgumentException("Unknown interest type: " + interest);
								
						}
						
						break; //break 'mapping source concept' domain
						
						
					case REFERENCE_SET_MEMBERS:
						
						final Query refSetQuery = new TermQuery(new Term(SnomedIndexBrowserConstants.CONCEPT_REFERRING_REFERENCE_SET_ID, contextId));
						
						switch (interest) {
							
							case BELOW_AVERAGE: //$FALL-THROUGH$
							case ABOVE_AVERAGE:
								
								searchProfileQuery.add(decorateQuery(refSetQuery, interest), Occur.SHOULD);
								break;
								
							case EXCLUDE:
								
								//exclude
								searchProfileQuery.add(refSetQuery, Occur.MUST_NOT);
								break;
								
							case AVERAGE:
								
								throw new IllegalStateException("Interest must not be average.");
								
							default:
								
								throw new IllegalArgumentException("Unknown interest type: " + interest);
								
						}
						
						break; //break 'reference set members query' domain
							
						
					case WITHIN_A_NAMESPACE:
						
						final Query namespaceQuery = new TermQuery(new Term(SnomedIndexBrowserConstants.CONCEPT_NAMESPACE_ID, contextId));
						
						switch (interest) {
							
							case BELOW_AVERAGE: //$FALL-THROUGH$
							case ABOVE_AVERAGE:
								
								searchProfileQuery.add(decorateQuery(namespaceQuery, interest), Occur.SHOULD);
								break;
								
							case EXCLUDE:
								
								//exclude
								searchProfileQuery.add(namespaceQuery, Occur.MUST_NOT);
								break;
								
							case AVERAGE:
								
								throw new IllegalStateException("Interest must not be average.");
								
							default:
								
								throw new IllegalArgumentException("Unknown interest type: " + interest);
								
						}
						
						break; //break 'within a namespace' domain
						
					default:
						
						throw new IllegalArgumentException("Unknown domain: " + rule.getDomain());
				}
				
				
			}
		}
		
		
	
		
		return searchProfileQuery;
		
	}
	
	public static BooleanQuery provideExclusionQuery(final IBranchPath branchPath, final String userId) {
		Preconditions.checkNotNull(branchPath, "Branch path argument cannot be null.");

		if (null == userId) {
			
			return null; //user ID is not specified
			//assuming no search profile 
		}
		
		final SearchProfile profile = getSearchProfile(userId);
		
		//search profile does not exist for user
		if (null == profile) {
			
			LOGGER.info("Search profile is not available for user: " + userId);
			
			return null;
			
		}
		
		final Set<SearchProfileRule> rules = profile.getRules();
		
		if (CompareUtils.isEmpty(rules)) {
			
			LOGGER.info("No search rules were found for user: " + userId);
			
			return null; 
			
		}
		
		BooleanQuery searchProfileQuery = null;
		
		for (final SearchProfileRule rule : rules) {
		
		
			final SearchProfileInterest interest = rule.getInterest();
			
			//in case of average not much we can do
			if (SearchProfileInterest.EXCLUDE.equals(interest)) {
				
				final BytesRef contextId = IndexUtils.longToPrefixCoded(rule.getContextId());
				
				if (null == searchProfileQuery) {
					
					searchProfileQuery = new BooleanQuery(true);
					
				}
				
				switch (rule.getDomain()) {
				
					case DESCENDANTS_OF_CONCEPT:
						
						final Query descendantQuery = SnomedMappings.newQuery().parent(rule.getContextId()).ancestor(rule.getContextId()).matchAny();
						searchProfileQuery.add(descendantQuery, Occur.MUST_NOT);
						
						break; //break 'descendants of concept' domain
						
					case WITHIN_A_MODULE:
	
						final Query moduleQuery = SnomedMappings.newQuery().module(rule.getContextId()).matchAll();
						searchProfileQuery.add(moduleQuery, Occur.MUST_NOT);
						
						break; //break 'within a module' domain
						
					case MAPPING_SOURCE_CONCEPTS:
						
						final Query mappingQuery = new TermQuery(new Term(SnomedIndexBrowserConstants.CONCEPT_REFERRING_MAPPING_REFERENCE_SET_ID, contextId));
						searchProfileQuery.add(mappingQuery, Occur.MUST_NOT);
						
						break; //break 'mapping source concept' domain
						
					case REFERENCE_SET_MEMBERS:
						
						final Query refSetQuery = new TermQuery(new Term(SnomedIndexBrowserConstants.CONCEPT_REFERRING_REFERENCE_SET_ID, contextId));
						searchProfileQuery.add(refSetQuery, Occur.MUST_NOT);
						
						break; //break 'reference set members query' domain
						
					case WITHIN_A_NAMESPACE:
						
						final String conceptId = rule.getContextId();
						final Query namespaceQuery = new TermQuery(new Term(SnomedIndexBrowserConstants.CONCEPT_NAMESPACE_ID, IndexUtils.longToPrefixCoded(conceptId)));
						searchProfileQuery.add(namespaceQuery, Occur.MUST_NOT);
						
						break; //break 'within a namespace' domain
						
					default:
						
						throw new IllegalArgumentException("Unknown domain: " + rule.getDomain());
				}
			}
		}
		
		return searchProfileQuery;
	}
	
	/*returns with the active search profile associated */
	@Nullable private static SearchProfile getSearchProfile(final String userId) {
		return getSearchProfileManager().getActiveProfile(userId);
	}
	
	/*returns with the search profile manager instance from the application context*/
	private static ISearchProfileManager getSearchProfileManager() {
		return ApplicationContext.getInstance().getService(ISearchProfileManager.class);
	}
	
	/*boosts the queries is the interest is either above or below average.*/
	private static Query decorateQuery(final Query query, final SearchProfileInterest interest) {
		
		switch (interest) {
			
			case AVERAGE:
				return query; //nothing to do
				
			case ABOVE_AVERAGE: //$FALL-THROUGH$
			case BELOW_AVERAGE:
				query.setBoost(interest.getScaleFactor());
				return query;
		
			case EXCLUDE:
				LOGGER.warn("Cannot decorate 'Exclude' interest.");
				return query;
				
			default:
				throw new IllegalArgumentException("Unknown search profile interest: " + interest);
		}
	}
	
	private SearchProfileQueryProvider() {
		//suppress instantiation
	}
	
}