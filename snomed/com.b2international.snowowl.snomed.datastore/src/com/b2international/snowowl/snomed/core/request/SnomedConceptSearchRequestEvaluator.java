/*
 * Copyright 2020-2024 B2i Healthcare, https://b2ihealthcare.com
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
import java.util.List;
import java.util.SortedSet;
import java.util.stream.Collectors;

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.commons.options.Options;
import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.domain.Concept;
import com.b2international.snowowl.core.domain.Concepts;
import com.b2international.snowowl.core.domain.Description;
import com.b2international.snowowl.core.request.ConceptSearchRequestEvaluator;
import com.b2international.snowowl.core.request.ExpandParser;
import com.b2international.snowowl.core.request.SearchResourceRequest;
import com.b2international.snowowl.snomed.core.SnomedDisplayTermType;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.snomed.core.domain.SnomedDescriptions;
import com.b2international.snowowl.snomed.datastore.request.SnomedConceptSearchRequestBuilder;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSortedSet;

/**
 * @since 7.5
 */
public final class SnomedConceptSearchRequestEvaluator implements ConceptSearchRequestEvaluator {

	private Concept toConcept(ResourceURI codeSystem, SnomedConcept snomedConcept, String pt, boolean requestedExpand) {
		final Concept concept = toConcept(codeSystem, snomedConcept, snomedConcept.getIconId(), pt, snomedConcept.getScore());
		
		SortedSet<Description> alternativeTerms = generateAlternativeTerms(snomedConcept.getPreferredDescriptions());
		
		if (!alternativeTerms.isEmpty()) {
			concept.setAlternativeTerms(alternativeTerms);
		}
		
		concept.setActive(snomedConcept.isActive());
		concept.setParentIds(snomedConcept.getParentIdsAsString());
		concept.setAncestorIds(snomedConcept.getAncestorIdsAsString());
		if (requestedExpand) {
			concept.setInternalConcept(snomedConcept);
		}
		return concept;
	}
	
	public static SortedSet<Description> generateAlternativeTerms(SnomedDescriptions descriptions) {
		return descriptions.stream()
				.flatMap(description -> {
					final String languageCode = description.getLanguageCode();
					return description.getAcceptabilityMap().keySet().stream()
							.map(refsetId -> new ExtendedLocale(languageCode, null, refsetId))
							.map(language -> new Description(description.getTerm(), language.toString()).withInternalDescription(description));
				})
				.collect(ImmutableSortedSet.toImmutableSortedSet(Comparator.naturalOrder()));
	}

	@Override
	public Concepts evaluate(ResourceURI uri, ServiceProvider context, Options search) {
		
		final String preferredDisplay = search.getString(OptionKey.DISPLAY);
		SnomedDisplayTermType displayTermType;
		
		if (preferredDisplay != null) {
			displayTermType = SnomedDisplayTermType.getEnum(preferredDisplay);
		} else {
			displayTermType = SnomedDisplayTermType.PT;
		}
		
		final SnomedConceptSearchRequestBuilder req = SnomedRequests.prepareSearchConcept();
		
		evaluateIdFilterOptions(req, search);
		evaluateTermFilterOptions(req, search);
		evaluateKnnFilterOptions(req, search);
		
		if (search.containsKey(OptionKey.ACTIVE)) {
			req.filterByActive(search.getBoolean(OptionKey.ACTIVE));
		}
		
		if (search.containsKey(OptionKey.PARENT)) {
			req.filterByParents(search.getCollection(OptionKey.PARENT, String.class));
		}
		
		if (search.containsKey(OptionKey.ANCESTOR)) {
			req.filterByAncestors(search.getCollection(OptionKey.ANCESTOR, String.class));
		}
		
		if (search.containsKey(OptionKey.TERM_TYPE)) {
			req.filterByDescriptionType(search.getString(OptionKey.TERM_TYPE));
		}
		
		evaluateQueryOptions(context, req, search);
		
		boolean requestedExpand = search.containsKey(OptionKey.EXPAND);
		// make sure preferredDescriptions() and displayTermType expansion data are always loaded
		Options expand = ExpandParser.parse("preferredDescriptions()")
				.merge(requestedExpand ? search.getOptions(OptionKey.EXPAND) : Options.empty());
		
		if (!Strings.isNullOrEmpty(displayTermType.getExpand())) {
			expand = ExpandParser.parse(displayTermType.getExpand()).merge(expand);
		}
		
		final List<ExtendedLocale> locales = search.getList(OptionKey.LOCALES, ExtendedLocale.class);
		
		SnomedConcepts matches = req
				.filterByDescriptionLanguageRefSet(locales)
				.setLocales(locales)
				.setSearchAfter(search.getString(OptionKey.AFTER))
				.setLimit(search.get(OptionKey.LIMIT, Integer.class))
				.setFields(search.getList(OptionKey.FIELDS, String.class))
				.setExpand(expand)
				.sortBy(search.containsKey(SearchResourceRequest.OptionKey.SORT_BY) ? search.getList(SearchResourceRequest.OptionKey.SORT_BY, SearchResourceRequest.Sort.class) : null)
				.build(uri)
				.execute(context);

		return new Concepts(
			matches
				.stream()
				.map(concept -> toConcept(uri, concept, displayTermType.getLabel(concept), requestedExpand))
				.collect(Collectors.toList()), 
			matches.getSearchAfter(), 
			matches.getLimit(), 
			matches.getTotal()
		);
	}
	
}

