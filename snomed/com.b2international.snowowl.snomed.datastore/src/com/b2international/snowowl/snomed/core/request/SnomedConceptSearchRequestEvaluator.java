/*
 * Copyright 2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.core.request;

import java.util.Comparator;
import java.util.stream.Collectors;

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.commons.options.Options;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.domain.Concept;
import com.b2international.snowowl.core.domain.Concepts;
import com.b2international.snowowl.core.request.ConceptSearchRequestEvaluator;
import com.b2international.snowowl.core.request.SearchResourceRequest;
import com.b2international.snowowl.core.uri.CodeSystemURI;
import com.b2international.snowowl.snomed.core.SnomedDisplayTermType;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.snomed.datastore.request.SnomedConceptSearchRequestBuilder;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.ecl.Ecl;
import com.google.common.collect.FluentIterable;

/**
 * @since 7.5
 */
public final class SnomedConceptSearchRequestEvaluator implements ConceptSearchRequestEvaluator {

	private Concept toConcept(CodeSystemURI codeSystem, SnomedConcept snomedConcept, String term) {
		final Concept concept = toConcept(codeSystem, snomedConcept, snomedConcept.getIconId(), term, snomedConcept.getScore());
		concept.setAlternativeTerms(FluentIterable.from(snomedConcept.getPreferredDescriptions())
				.transform(pd -> pd.getTerm())
				.toSortedSet(Comparator.naturalOrder()));
		return concept;
	}
	
	@Override
	public Concepts evaluate(CodeSystemURI uri, BranchContext context, Options search) {
		
		final String preferredDisplay = search.getString(OptionKey.DISPLAY);
		SnomedDisplayTermType displayTermType;
		
		if (preferredDisplay != null) {
			displayTermType = SnomedDisplayTermType.getEnum(preferredDisplay);
		} else {
			displayTermType = SnomedDisplayTermType.PT;
		}
		
		final SnomedConceptSearchRequestBuilder req = SnomedRequests.prepareSearchConcept();
		evaluateTermFilterOptions(req, search);
		
		if (search.containsKey(OptionKey.ID)) {
			req.filterByIds(search.getCollection(OptionKey.ID, String.class));
		}
		
		if (search.containsKey(OptionKey.ACTIVE)) {
			req.filterByActive(search.getBoolean(OptionKey.ACTIVE));
		}
		
		if (search.containsKey(OptionKey.TERM_TYPE)) {
			req.filterByDescriptionType(search.getString(OptionKey.TERM_TYPE));
		}
		
		if (search.containsKey(OptionKey.QUERY) || search.containsKey(OptionKey.MUST_NOT_QUERY)) {
			StringBuilder query = new StringBuilder();
			
			if (search.containsKey(OptionKey.QUERY)) {
				query
					.append("(")
					.append(Ecl.or(search.getCollection(OptionKey.QUERY, String.class)))
					.append(")");
			} else {
				query.append(Ecl.ANY);
			}
			
			if (search.containsKey(OptionKey.MUST_NOT_QUERY)) {
				query
					.append(" MINUS (")
					.append(Ecl.or(search.getCollection(OptionKey.MUST_NOT_QUERY, String.class)))
					.append(")");
			}
			
			req.filterByQuery(query.toString());
		}
		
		SnomedConcepts matches = req
				.setLocales(search.getList(OptionKey.LOCALES, ExtendedLocale.class))
				.setSearchAfter(search.getString(OptionKey.AFTER))
				.setLimit(search.get(OptionKey.LIMIT, Integer.class))
				.setExpand(String.join(",", "preferredDescriptions()", displayTermType.getExpand()))
				.sortBy(search.containsKey(SearchResourceRequest.OptionKey.SORT_BY) ? search.getList(SearchResourceRequest.OptionKey.SORT_BY, SearchResourceRequest.Sort.class) : null)
				.build()
				.execute(context);

		return new Concepts(
			matches
				.stream()
				.map(concept -> toConcept(uri, concept, displayTermType.getLabel(concept)))
				.collect(Collectors.toList()), 
			matches.getSearchAfter(), 
			matches.getLimit(), 
			matches.getTotal()
		);
	}
	
}

