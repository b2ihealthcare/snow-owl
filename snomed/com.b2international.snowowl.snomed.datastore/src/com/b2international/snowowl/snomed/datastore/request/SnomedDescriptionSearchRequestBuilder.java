/*
 * Copyright 2011-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.google.common.collect.Lists.newArrayList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.request.SearchResourceRequest;
import com.b2international.snowowl.snomed.core.domain.Acceptability;
import com.b2international.snowowl.snomed.core.domain.SnomedDescription;
import com.b2international.snowowl.snomed.core.domain.SnomedDescriptions;
import com.b2international.snowowl.snomed.datastore.config.SnomedCoreConfiguration;
import com.b2international.snowowl.snomed.datastore.request.SnomedDescriptionSearchRequest.OptionKey;
import com.google.common.collect.Iterables;

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
	 * @param termFilter - the expression to match
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
	 * @param exactTermFilter - the expression to match
	 * @return <code>this</code> search request builder, for method chaining
	 */
	public SnomedDescriptionSearchRequestBuilder filterByExactTerm(String exactTermFilter) {
		return addOption(OptionKey.EXACT_TERM, exactTermFilter == null ? exactTermFilter : exactTermFilter.trim());
	}

	/**
	 * Filter descriptions by their case significance value.  This method accepts ECL values as caseSignificanceFilter.
	 * 
	 * @param caseSignificanceId
	 * @return <code>this</code> search request builder, for method chaining
	 * @see #filterByCaseSignificances(Iterable)
	 * @see SnomedDescription#getCaseSignificanceId()
	 */
	public SnomedDescriptionSearchRequestBuilder filterByCaseSignificance(String caseSignificanceId) {
		return addOption(OptionKey.CASE_SIGNIFICANCE, caseSignificanceId);
	}
	
	/**
	 * Filter descriptions by their case significance value.
	 * 
	 * @param caseSignificanceIds
	 * @return <code>this</code> search request builder, for method chaining
	 * @see #filterByCaseSignificance(String)
	 * @see SnomedDescription#getCaseSignificanceId()
	 */
	public SnomedDescriptionSearchRequestBuilder filterByCaseSignificances(Iterable<String> caseSignificanceIds) {
		return addOption(OptionKey.CASE_SIGNIFICANCE, caseSignificanceIds);
	}
	
	/**
	 * Filters descriptions by their concept. The filter value accepts ECL expressions (including single ID).
	 * 
	 * @param conceptFilter
	 * @return
	 */
	public SnomedDescriptionSearchRequestBuilder filterByConcept(String conceptFilter) {
		return addOption(OptionKey.CONCEPT, conceptFilter);
	}
	
	/**
	 * Filters descriptions to be descriptions of any of the given concept IDs.
	 * 
	 * @param conceptIds
	 * @return
	 */
	public SnomedDescriptionSearchRequestBuilder filterByConceptId(Collection<String> conceptIds) {
		return addOption(OptionKey.CONCEPT, conceptIds);
	}
	
	/**
	 * Filters descriptions by their Description Type. The filter value accepts ECL expressions (including single ID).
	 * 
	 * @param typeFilter
	 * @return
	 */
	public SnomedDescriptionSearchRequestBuilder filterByType(String typeFilter) {
		return addOption(OptionKey.TYPE, typeFilter);
	}
	
	/**
	 * Filters descriptions by their Description Type.
	 * 
	 * @param typeIds
	 * @return
	 */
	public SnomedDescriptionSearchRequestBuilder filterByType(Iterable<String> typeIds) {
		return addOption(OptionKey.TYPE, typeIds);
	}

	/**
	 * Filters descriptions by their language code value, eg. <code>"en"</code>.
	 * @param languageCodes
	 * @return
	 */
	public SnomedDescriptionSearchRequestBuilder filterByLanguageCodes(Collection<String> languageCodes) {
		return addOption(OptionKey.LANGUAGE, languageCodes);
	}
	
	/**
	 * Filter descriptions by applying the given regular expression on their terms. 
	 * @param regex - the regular expression to apply
	 * @return <code>this</code> search request builder, for method chaining
	 */
	public SnomedDescriptionSearchRequestBuilder filterByTermRegex(String regex) {
		return addOption(OptionKey.TERM_REGEX, regex);
	}
	
	/**
	 * Filter descriptions to have any of the given semantic tag value specified in the end of the Description's term. Empty String value returns
	 * descriptions that does not have semantic tag value specified in their term.
	 * 
	 * @param semanticTag
	 * @return <code>this</code> search request builder, for method chaining
	 * @see SnomedDescription#getSemanticTag()
	 */
	public SnomedDescriptionSearchRequestBuilder filterBySemanticTag(String semanticTag) {
		return addOption(OptionKey.SEMANTIC_TAG, semanticTag);
	}
	
	/**
	 * Filter descriptions to have any of the given semantic tag value specified in the end of the Description's term. Empty {@link String} value
	 * returns descriptions that does not have semantic tag value specified in their term.
	 * 
	 * @param semanticTags
	 * @return <code>this</code> search request builder, for method chaining
	 * @see SnomedDescription#getSemanticTag()
	 */
	public SnomedDescriptionSearchRequestBuilder filterBySemanticTags(Iterable<String> semanticTags) {
		return addOption(OptionKey.SEMANTIC_TAG, semanticTags);
	}
	
	/**
	 * Filters descriptions to have a semantic tag that matches the given regular expression.
	 * 
	 * @param semanticTagRegex
	 * @return <code>this</code> search request builder, for method chaining
	 * @see SnomedDescription#getSemanticTag()
	 */
	public SnomedDescriptionSearchRequestBuilder filterBySemanticTagRegex(String semanticTagRegex) {
		return addOption(OptionKey.SEMANTIC_TAG_REGEX, semanticTagRegex);
	}
	
	/**
	 * Filter to return descriptions based on their language reference set membership.
	 * 
	 * @param languageRefSetId - the language reference set ID where the descriptions are members 
	 * @return <code>this</code> search request builder, for method chaining
	 * @see #filterByAcceptableIn(String)
	 * @see #filterByPreferredIn(String)
	 * @see #filterByLanguageRefSets(Iterable)
	 */
	public SnomedDescriptionSearchRequestBuilder filterByLanguageRefSet(String languageRefSetId) {
		return addOption(OptionKey.LANGUAGE_REFSET, languageRefSetId);
	}
	
	/**
	 * Filter to return descriptions based on their language reference set membership or ECL filter. When the given argument is an ECL expression the
	 * request will resolve the expression and the filter will match all descriptions that has any language reference set member in any of the matched
	 * concept IDs (language reference sets probably).
	 * 
	 * @param languageRefSetIds
	 *            - the language reference set IDs where the descriptions are members
	 * @return <code>this</code> search request builder, for method chaining
	 * @see #filterByAcceptableIn(Iterable)
	 * @see #filterByPreferredIn(Iterable)
	 * @see #filterByLanguageRefSets(String)
	 */
	public SnomedDescriptionSearchRequestBuilder filterByLanguageRefSets(Iterable<String> languageRefSetIds) {
		return addOption(OptionKey.LANGUAGE_REFSET, languageRefSetIds);
	}
	
	/**
	 * Filter to return descriptions based on their language reference set membership using {@link ExtendedLocale}s.
	 * 
	 * @param locales - the locales to use as source of language reference set IDs 
	 * @return <code>this</code> search request builder, for method chaining
	 * @see #filterByLanguageRefSet(Iterable)
	 * @see #getLanguageRefSetIds(List)
	 */
	public SnomedDescriptionSearchRequestBuilder filterByLanguageRefSets(List<ExtendedLocale> locales) {
		return filterByLanguageRefSets(getLanguageRefSetIds(locales));
	}
	
	/**
	 * Filter to return descriptions based on their preferred {@link Acceptability acceptability} membership in the given language refset or ECL
	 * filter. When the given argument is an ECL expression the request will resolve the expression and the filter will match all descriptions that
	 * has preferred language reference set member in any of the matched concept IDs (language reference sets probably).
	 * 
	 * @param languageRefSetId
	 *            - the language reference set ID where the descriptions are members with preferred acceptability
	 * @return <code>this</code> search request builder, for method chaining
	 * @see #filterByAcceptableIn(String)
	 * @see #filterByLanguageRefSet(String)
	 * @see #filterByPreferredIn(Iterable)
	 */
	public SnomedDescriptionSearchRequestBuilder filterByPreferredIn(String languageRefSetId) {
		return addOption(OptionKey.PREFERRED_IN, languageRefSetId);
	}
	
	/**
	 * Filter to return descriptions based on their preferred {@link Acceptability acceptability} membership in any of the given language refsets.
	 * 
	 * @param languageRefSetIds - the language reference set ID where the descriptions are members with preferred acceptability 
	 * @return <code>this</code> search request builder, for method chaining
	 * @see #filterByAcceptableIn(Iterable)
	 * @see #filterByLanguageRefSet(Iterable)
	 * @see #filterByPreferredIn(String)
	 */
	public SnomedDescriptionSearchRequestBuilder filterByPreferredIn(Iterable<String> languageRefSetIds) {
		return addOption(OptionKey.PREFERRED_IN, languageRefSetIds);
	}
	
	/**
	 * Filter to return descriptions based on their preferred {@link Acceptability acceptability} membership in any of the given language refsets
	 * (described by the given locales).
	 * 
	 * @param locales
	 *            - the locales to use as source of language reference set IDs 
	 * @return <code>this</code> search request builder, for method chaining
	 * @see #filterByPreferredIn(Iterable)
	 * @see #getLanguageRefSetIds(List)
	 */
	public SnomedDescriptionSearchRequestBuilder filterByPreferredIn(List<ExtendedLocale> locales) {
		return filterByPreferredIn(getLanguageRefSetIds(locales));
	}
	
	/**
	 * Filter to return descriptions based on their acceptable {@link Acceptability acceptability} membership in the given language refset or ECL
	 * filter. When the given argument is an ECL expression the request will resolve the expression and the filter will match all descriptions that
	 * has acceptable language reference set member in any of the matched concept IDs (language reference sets probably).
	 * 
	 * @param languageRefSetId
	 *            - the language reference set ID where the descriptions are members with preferred acceptability
	 * @return <code>this</code> search request builder, for method chaining
	 * @see #filterByAcceptableIn(String)
	 * @see #filterByLanguageRefSet(String)
	 * @see #filterByPreferredIn(Iterable)
	 */
	public SnomedDescriptionSearchRequestBuilder filterByAcceptableIn(String languageRefSetId) {
		return addOption(OptionKey.ACCEPTABLE_IN, languageRefSetId);
	}
	
	/**
	 * Filter to return descriptions based on their acceptable {@link Acceptability acceptability} membership in any of the given language refsets.
	 * 
	 * @param languageRefSetIds - the language reference set ID where the descriptions are members with preferred acceptability 
	 * @return <code>this</code> search request builder, for method chaining
	 * @see #filterByPreferredIn(Iterable)
	 * @see #filterByLanguageRefSet(Iterable)
	 * @see #filterByAcceptableIn(String)
	 */
	public SnomedDescriptionSearchRequestBuilder filterByAcceptableIn(Iterable<String> languageRefSetIds) {
		return addOption(OptionKey.ACCEPTABLE_IN, languageRefSetIds);
	}

	/**
	 * Filter to return descriptions based on their acceptable {@link Acceptability acceptability} membership in any of the given language refsets
	 * (described by the given locales).
	 * 
	 * @param locales
	 *            - the locales to use as source of language reference set IDs 
	 * @return <code>this</code> search request builder, for method chaining
	 * @see #filterByAcceptableIn(Iterable)
	 * @see #getLanguageRefSetIds(List)
	 */
	public SnomedDescriptionSearchRequestBuilder filterByAcceptableIn(List<ExtendedLocale> locales) {
		return filterByAcceptableIn(getLanguageRefSetIds(locales));
	}
	
	@Override
	protected SearchResourceRequest<BranchContext, SnomedDescriptions> createSearch() {
		return new SnomedDescriptionSearchRequest();
	}
	
	/**
	 * Extracts the language reference set identifier from the specified list of {@link ExtendedLocale}s. 
	 * <p>
	 * The identifiers may come from the value itself, if it includes a reference set ID (eg. {@code en-x-12345678901}),
	 * or from the language tag part, if it is well known (eg. {@code en-US}).
	 * <p>
	 * If no element from the input list can be converted, an {@link IllegalArgumentException} is thrown; no exception occurs
	 * if only some of the {@code ExtendedLocale}s could not be transformed into a language reference set identifier, however. 
	 *  
	 * @param locales  the extended locale list to process (may not be {@code null})
	 * @return the converted language reference set identifiers
	 */
	public static List<String> getLanguageRefSetIds(List<ExtendedLocale> locales) {
		List<String> languageRefSetIds = newArrayList();
		List<ExtendedLocale> unconvertableLocales = new ArrayList<ExtendedLocale>();
	
		for (ExtendedLocale extendedLocale : locales) {
			Collection<String> mappedRefSetIds;
	
			if (!extendedLocale.getLanguageRefSetId().isEmpty()) {
				mappedRefSetIds = Collections.singleton(extendedLocale.getLanguageRefSetId());
			} else {
				mappedRefSetIds = ApplicationContext.getServiceForClass(SnomedCoreConfiguration.class).getMappedLanguageRefSetIds(extendedLocale.getLanguageTag());
			}
	
			if (mappedRefSetIds.isEmpty()) {
				unconvertableLocales.add(extendedLocale);
			} else {
				mappedRefSetIds.forEach(mappedRefSetId -> {
					if (!languageRefSetIds.contains(mappedRefSetId)) {
						languageRefSetIds.add(mappedRefSetId);
					}
				});
			}
		}
	
		if (languageRefSetIds.isEmpty() && !unconvertableLocales.isEmpty()) {
			throw new IllegalArgumentException("Don't know how to convert extended locale " + Iterables.toString(unconvertableLocales) + " to a language reference set identifier.");
		}
		
		return languageRefSetIds;
	}

}
