/*
 * Copyright 2011-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
import java.util.List;

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.request.SearchResourceRequest;
import com.b2international.snowowl.core.request.TermFilter;
import com.b2international.snowowl.core.request.TermFilterSupport;
import com.b2international.snowowl.snomed.core.domain.Acceptability;
import com.b2international.snowowl.snomed.core.domain.SnomedDescription;
import com.b2international.snowowl.snomed.core.domain.SnomedDescriptions;
import com.b2international.snowowl.snomed.datastore.SnomedDescriptionUtils;
import com.b2international.snowowl.snomed.datastore.request.SnomedDescriptionSearchRequest.OptionKey;
import com.google.common.collect.ListMultimap;

/**
 * <i>Builder</i> class to build requests responsible for searching SNOMED CT descriptions.
 * This class should be instantiated from the corresponding static method on the central {@link SnomedRequests} class.
 * Filter methods restrict the results set returned from the search requests; 
 * what passes the filters will be returned as part of the pageable resultset.
 * 
 * @since 4.5
 */
public final class SnomedDescriptionSearchRequestBuilder extends SnomedComponentSearchRequestBuilder<SnomedDescriptionSearchRequestBuilder, SnomedDescriptions>
		implements TermFilterSupport<SnomedDescriptionSearchRequestBuilder>{

	SnomedDescriptionSearchRequestBuilder() {}
	
	@Override
	public SnomedDescriptionSearchRequestBuilder filterByTerm(TermFilter termFilter) {
		return addOption(OptionKey.TERM, termFilter);
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
	 * @see #SnomedDescriptionUtils.getLanguageRefSetIds(List)
	 */
	public SnomedDescriptionSearchRequestBuilder filterByLanguageRefSets(List<ExtendedLocale> locales, ListMultimap<String, String> languageMap) {
		return filterByLanguageRefSets(SnomedDescriptionUtils.getLanguageRefSetIds(locales, languageMap));
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
	 * @see #SnomedDescriptionUtils.getLanguageRefSetIds(List)
	 */
	public SnomedDescriptionSearchRequestBuilder filterByPreferredIn(List<ExtendedLocale> locales, ListMultimap<String, String> languageMap) {
		return filterByPreferredIn(SnomedDescriptionUtils.getLanguageRefSetIds(locales, languageMap));
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
	 * @see #SnomedDescriptionUtils.getLanguageRefSetIds(List)
	 */
	public SnomedDescriptionSearchRequestBuilder filterByAcceptableIn(List<ExtendedLocale> locales, ListMultimap<String, String> languageMap) {
		return filterByAcceptableIn(SnomedDescriptionUtils.getLanguageRefSetIds(locales, languageMap));
	}
	
	@Override
	protected SearchResourceRequest<BranchContext, SnomedDescriptions> createSearch() {
		return new SnomedDescriptionSearchRequest();
	}
}
