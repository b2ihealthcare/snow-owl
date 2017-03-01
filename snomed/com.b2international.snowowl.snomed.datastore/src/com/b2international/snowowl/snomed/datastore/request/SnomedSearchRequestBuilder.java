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
 * @since 4.5
 */
public abstract class SnomedSearchRequestBuilder<B extends SnomedSearchRequestBuilder<B, R>, R> extends SearchRequestBuilder<B, R> {

	protected SnomedSearchRequestBuilder(String repositoryId) {
		super(repositoryId);
	}
	
	public final B filterByModule(String moduleId) {
		return addOption(OptionKey.MODULE, moduleId);
	}

	public final B filterByActive(Boolean active) {
		return addOption(OptionKey.ACTIVE, active);
	}

	public final B filterByLanguageRefSetIds(List<Long> languageRefSetIds) {
		return addOption(OptionKey.LANGUAGE_REFSET, languageRefSetIds);
	}
	
	public final B filterByEffectiveTime(String effectiveTime) {
		return addOption(OptionKey.EFFECTIVE_TIME, effectiveTime);
	}
	
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
