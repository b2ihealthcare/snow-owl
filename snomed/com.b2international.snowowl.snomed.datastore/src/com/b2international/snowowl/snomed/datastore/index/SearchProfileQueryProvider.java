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
package com.b2international.snowowl.snomed.datastore.index;

import java.util.Collections;
import java.util.Set;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.CompareUtils;
import com.b2international.index.query.Expression;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Expressions.ExpressionBuilder;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.index.interest.ISearchProfileManager;
import com.b2international.snowowl.snomed.datastore.index.interest.SearchProfile;
import com.b2international.snowowl.snomed.datastore.index.interest.SearchProfileInterest;
import com.b2international.snowowl.snomed.datastore.index.interest.SearchProfileRule;

/**
 * Class for providing index queries based on the {@link SearchProfile} associated for a user.
 */
public abstract class SearchProfileQueryProvider {

	private static final Logger LOGGER = LoggerFactory.getLogger(SearchProfileQueryProvider.class);
	
	@Nullable public static Expression provideQuery(final String userId) {
		
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
		
		final ExpressionBuilder searchProfileQuery = Expressions.builder();
		
		for (final SearchProfileRule rule : rules) {
		
		
			final SearchProfileInterest interest = rule.getInterest();
			
			//in case of average not much we can do
			if (!SearchProfileInterest.AVERAGE.equals(interest)) {
				
				final String conceptId = rule.getContextId();
				
				switch (rule.getDomain()) {
				
					case DESCENDANTS_OF_CONCEPT:
						
						final Expression descendantQuery = Expressions.builder()
								.should(SnomedConceptDocument.Expressions.parents(Collections.singleton(conceptId)))
								.should(SnomedConceptDocument.Expressions.ancestors(Collections.singleton(conceptId)))
								.build();
						
						switch (interest) {
	
							case BELOW_AVERAGE: //$FALL-THROUGH$
							case ABOVE_AVERAGE:
								
								searchProfileQuery.should(decorateQuery(descendantQuery, interest));
								break;
								
							case EXCLUDE:
								//exclude
								searchProfileQuery.mustNot(descendantQuery);
								break;
							case AVERAGE:
								throw new IllegalStateException("Interest must not be average.");
								
							default:
								throw new IllegalArgumentException("Unknown interest type: " + interest);
								
						}
						
						break; //break 'descendants of concept' domain
						
					case WITHIN_A_MODULE:
						
						final Expression moduleQuery = SnomedConceptDocument.Expressions.module(conceptId);
	
						switch (interest) {
	
							case BELOW_AVERAGE: //$FALL-THROUGH$
							case ABOVE_AVERAGE:
								
								searchProfileQuery.should(decorateQuery(moduleQuery, interest));
								break;
								
							case EXCLUDE:
								//exclude
								searchProfileQuery.mustNot(moduleQuery);
								break;
								
							case AVERAGE:
								
								throw new IllegalStateException("Interest must not be average.");
								
							default:
								
								throw new IllegalArgumentException("Unknown interest type: " + interest);
								
						}
						
						break; //break 'within a module' domain
						
						
					case MAPPING_SOURCE_CONCEPTS:
						
//						final Expression mappingQuery = SnomedConceptDocument.Expressions.referringMappingRefSet(conceptId);
//						
//						switch (interest) {
//							
//							case BELOW_AVERAGE: //$FALL-THROUGH$
//							case ABOVE_AVERAGE:
//								
//								searchProfileQuery.should(decorateQuery(mappingQuery, interest));
//								break;
//								
//							case EXCLUDE:
//								
//								//exclude
//								searchProfileQuery.mustNot(mappingQuery);
//								break;
//								
//							case AVERAGE:
//								
//								throw new IllegalStateException("Interest must not be average.");
//								
//							default:
//								
//								throw new IllegalArgumentException("Unknown interest type: " + interest);
//								
//						}
						
						break; //break 'mapping source concept' domain
						
						
					case REFERENCE_SET_MEMBERS:
						
//						final Expression refSetQuery = SnomedConceptDocument.Expressions.referringRefSet(conceptId);
//						
//						switch (interest) {
//							
//							case BELOW_AVERAGE: //$FALL-THROUGH$
//							case ABOVE_AVERAGE:
//								
//								searchProfileQuery.should(decorateQuery(refSetQuery, interest));
//								break;
//								
//							case EXCLUDE:
//								
//								//exclude
//								searchProfileQuery.mustNot(refSetQuery);
//								break;
//								
//							case AVERAGE:
//								
//								throw new IllegalStateException("Interest must not be average.");
//								
//							default:
//								
//								throw new IllegalArgumentException("Unknown interest type: " + interest);
//								
//						}
						
						break; //break 'reference set members query' domain
							
						
					case WITHIN_A_NAMESPACE:
						
						final Expression namespaceQuery = SnomedConceptDocument.Expressions.namespace(conceptId);
						
						switch (interest) {
							
							case BELOW_AVERAGE: //$FALL-THROUGH$
							case ABOVE_AVERAGE:
								
								searchProfileQuery.should(decorateQuery(namespaceQuery, interest));
								break;
								
							case EXCLUDE:
								
								//exclude
								searchProfileQuery.mustNot(namespaceQuery);
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
		
		
	
		
		return searchProfileQuery.build();
		
	}
	
//	public static Expression provideExclusionQuery(final IBranchPath branchPath, final String userId) {
//		Preconditions.checkNotNull(branchPath, "Branch path argument cannot be null.");
//
//		if (null == userId) {
//			
//			return null; //user ID is not specified
//			//assuming no search profile 
//		}
//		
//		final SearchProfile profile = getSearchProfile(userId);
//		
//		//search profile does not exist for user
//		if (null == profile) {
//			
//			LOGGER.info("Search profile is not available for user: " + userId);
//			
//			return null;
//			
//		}
//		
//		final Set<SearchProfileRule> rules = profile.getRules();
//		
//		if (CompareUtils.isEmpty(rules)) {
//			
//			LOGGER.info("No search rules were found for user: " + userId);
//			
//			return null; 
//			
//		}
//		
//		BooleanQuery searchProfileQuery = null;
//		
//		for (final SearchProfileRule rule : rules) {
//		
//		
//			final SearchProfileInterest interest = rule.getInterest();
//			
//			//in case of average not much we can do
//			if (SearchProfileInterest.EXCLUDE.equals(interest)) {
//				
//				final String conceptId = rule.getContextId();
//				final Long conceptIdLong = Long.valueOf(conceptId);
//				
//				if (null == searchProfileQuery) {
//					searchProfileQuery = new BooleanQuery(true);
//				}
//				
//				switch (rule.getDomain()) {
//				
//					case DESCENDANTS_OF_CONCEPT:
//						
//						final Query descendantQuery = SnomedMappings.newQuery().parent(conceptId).ancestor(conceptId).matchAny();
//						searchProfileQuery.add(descendantQuery, Occur.MUST_NOT);
//						
//						break; //break 'descendants of concept' domain
//						
//					case WITHIN_A_MODULE:
//	
//						final Query moduleQuery = SnomedMappings.newQuery().module(conceptId).matchAll();
//						searchProfileQuery.add(moduleQuery, Occur.MUST_NOT);
//						
//						break; //break 'within a module' domain
//						
//					case MAPPING_SOURCE_CONCEPTS:
//						
//						final Query mappingQuery = SnomedMappings.conceptReferringMappingRefSetId().toQuery(conceptIdLong);
//						searchProfileQuery.add(mappingQuery, Occur.MUST_NOT);
//						
//						break; //break 'mapping source concept' domain
//						
//					case REFERENCE_SET_MEMBERS:
//						
//						final Query refSetQuery = SnomedMappings.conceptReferringRefSetId().toQuery(conceptIdLong);
//						searchProfileQuery.add(refSetQuery, Occur.MUST_NOT);
//						
//						break; //break 'reference set members query' domain
//						
//					case WITHIN_A_NAMESPACE:
//						
//						final Query namespaceQuery = SnomedMappings.conceptNamespaceId().toQuery(conceptIdLong);
//						searchProfileQuery.add(namespaceQuery, Occur.MUST_NOT);
//						
//						break; //break 'within a namespace' domain
//						
//					default:
//						
//						throw new IllegalArgumentException("Unknown domain: " + rule.getDomain());
//				}
//			}
//		}
//		
//		return searchProfileQuery;
//	}
	
	/*returns with the active search profile associated */
	@Nullable private static SearchProfile getSearchProfile(final String userId) {
		return getSearchProfileManager().getActiveProfile(userId);
	}
	
	/*returns with the search profile manager instance from the application context*/
	private static ISearchProfileManager getSearchProfileManager() {
		return ApplicationContext.getInstance().getService(ISearchProfileManager.class);
	}
	
	/*boosts the queries is the interest is either above or below average.*/
	private static Expression decorateQuery(final Expression query, final SearchProfileInterest interest) {
		
		switch (interest) {
			
			case AVERAGE:
				return query; //nothing to do
				
			case ABOVE_AVERAGE: //$FALL-THROUGH$
			case BELOW_AVERAGE:
				return Expressions.boost(query, interest.getScaleFactor());
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