/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.request;

import java.util.Collection;

import com.b2international.commons.collections.Collections3;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.request.SearchResourceRequest;
import com.b2international.snowowl.snomed.core.domain.Acceptability;
import com.b2international.snowowl.snomed.core.domain.SnomedDescriptions;
import com.b2international.snowowl.snomed.datastore.request.SnomedDescriptionSearchRequest.OptionKey;

/**
 * <i>Builder</i> class to build requests responsible for searching SNOMED CT descriptions.
 * This class should be instantiated from the corresponding static method on the central {@link SnomedRequests} class.
 * Filter methods restrict the results set returned from the search requests; 
 * what passes the filters will be returned as part of the pageable resultset.
 * 
 * @since 4.5
 */
public final class SnomedDescriptionSearchRequestBuilder extends SnomedComponentSearchRequestBuilder<SnomedDescriptionSearchRequestBuilder, SnomedDescriptions> {

	SnomedDescriptionSearchRequestBuilder() {}
	
	public SnomedDescriptionSearchRequestBuilder withFuzzySearch() {
		return addOption(OptionKey.USE_FUZZY, true);
	}

	public SnomedDescriptionSearchRequestBuilder withParsedTerm() {
		return addOption(OptionKey.PARSED_TERM, true);
	}
	
	/**
	 * Filters results by matching description terms, using different methods for comparison.
	 * <p>
	 * This filter affects the score of each result. If results should be returned in order of 
	 * relevance, specify {@link SearchResourceRequest#SCORE} as one of the sort fields.
	 * 
	 * @param termFilter the expression to match
	 * @return <code>this</code> search request builder, for method chaining
	 */
	public SnomedDescriptionSearchRequestBuilder filterByTerm(String termFilter) {
		return addOption(OptionKey.TERM, termFilter == null ? termFilter : termFilter.trim());
	}

	/**
	 * Filters results by matching description terms, as entered (the comparison is case 
	 * insensitive and folds non-ASCII characters to their closest equivalent).
	 * <p>
	 * This filter affects the score of each result. If results should be returned in order of 
	 * relevance, specify {@link SearchResourceRequest#SCORE} as one of the sort fields.
	 * 
	 * @param termFilter the expression to match
	 * @return <code>this</code> search request builder, for method chaining
	 */
	public SnomedDescriptionSearchRequestBuilder filterByExactTerm(String exactTermFilter) {
		return addOption(OptionKey.EXACT_TERM, exactTermFilter == null ? exactTermFilter : exactTermFilter.trim());
	}

	public SnomedDescriptionSearchRequestBuilder filterByConcept(String conceptFilter) {
		return addOption(OptionKey.CONCEPT, conceptFilter);
	}
	
	public SnomedDescriptionSearchRequestBuilder filterByConceptId(Collection<String> conceptIds) {
		return addOption(OptionKey.CONCEPT, Collections3.toImmutableSet(conceptIds));
	}
	
	public SnomedDescriptionSearchRequestBuilder filterByType(String typeFilter) {
		return addOption(OptionKey.TYPE, typeFilter);
	}

	public SnomedDescriptionSearchRequestBuilder filterByLanguageCodes(Collection<String> languageCodes) {
		return addOption(OptionKey.LANGUAGE, Collections3.toImmutableSet(languageCodes));
	}
	
	public SnomedDescriptionSearchRequestBuilder filterByAcceptability(Acceptability acceptabilityFilter) {
		return addOption(OptionKey.ACCEPTABILITY, acceptabilityFilter);
	}
	
	@Override
	protected SearchResourceRequest<BranchContext, SnomedDescriptions> createSearch() {
		return new SnomedDescriptionSearchRequest();
	}

}
