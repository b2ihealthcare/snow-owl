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
package com.b2international.snowowl.snomed.datastore.request;

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.b2international.snowowl.datastore.request.SearchRequestBuilder;
import com.b2international.snowowl.snomed.SnomedConstants.LanguageCodeReferenceSetIdentifierMapping;
import com.b2international.snowowl.snomed.datastore.request.SnomedSearchRequest.OptionKey;

/**
* Abstract superclass for building request for SNOMED CT component searches.
* Clients should not extend. 
* @since 4.6
*/
public abstract class SnomedSearchRequestBuilder<B extends SnomedSearchRequestBuilder<B, R>, R> extends SearchRequestBuilder<B, R> {

	protected SnomedSearchRequestBuilder(String repositoryId) {
		super(repositoryId);
	}
	
	/**
	 * Filter that returns components that have a matching module id.
	 * Commonly used module IDs are listed in the {@link com.b2international.snowowl.snomed.Concepts} class.
	 * @param moduleId the SNOMED CT module ID to filter by
	 * @return SnomedConceptSearchRequestBuilder
	 */
	public final B filterByModule(String moduleId) {
		return addOption(OptionKey.MODULE, moduleId);
	}

	/**
	 * Filter to return components with the specified state (active/inactive)
	 * @param active
	 * @return SnomedSearchRequestBuilder
	 */
	public final B filterByActive(Boolean active) {
		return addOption(OptionKey.ACTIVE, active);
	}

	/**
	 * Filter to return concepts or descriptions based on its associated 
	 * language refsets.  This filter method is always called along {@link SnomedConceptSearchRequestBuilder#filterByTerm(String)}
	 * or {@link SnomedDescriptionSearchRequestBuilder#filterByTerm(String)} filters.
	 * 
	 * @param list of language refSet ids
	 * @return SnomedSearchRequestBuilder
	 * @see #filterByExtendedLocales(List)
	 */
	public final B filterByLanguageRefSetIds(List<Long> languageRefSetIds) {
		return addOption(OptionKey.LANGUAGE_REFSET, languageRefSetIds);
	}
	
	/**
	 * Filter to return concepts or descriptions based on its associated 
	 * language refsets configured via locales.
	 * This filter method is always called along {@link SnomedConceptSearchRequestBuilder#filterByTerm(String)}
	 * or {@link SnomedDescriptionSearchRequestBuilder#filterByTerm(String)} filters.
	 * 
	 * @param languageRefSetIds
	 * @return SnomedSearchRequestBuilder
	 * @see #filterByLanguageRefSetIds(List)
	 */
	public final B filterByExtendedLocales(List<ExtendedLocale> locales) {
		final List<Long> languageRefSetIds = newArrayList();
		for (ExtendedLocale extendedLocale : locales) {
			final String languageRefSetId;
			
			if (!extendedLocale.getLanguageRefSetId().isEmpty()) {
				languageRefSetId = extendedLocale.getLanguageRefSetId();
			} else {
				languageRefSetId = LanguageCodeReferenceSetIdentifierMapping.getReferenceSetIdentifier(extendedLocale.getLanguageTag());
			}
			
			if (languageRefSetId == null) {
				throw new BadRequestException("Don't know how to convert extended locale " + extendedLocale.toString() + " to a language reference set identifier.");
			} else {
				languageRefSetIds.add(Long.valueOf(languageRefSetId));
			}
		}
		
		return filterByLanguageRefSetIds(languageRefSetIds).setLocales(locales);
	}
}
