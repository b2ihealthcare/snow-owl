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
package com.b2international.snowowl.snomed.datastore.request;

import static com.google.common.collect.Lists.newArrayList;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.b2international.commons.CompareUtils;
import com.b2international.commons.http.ExtendedLocale;
import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.b2international.snowowl.datastore.request.RevisionSearchRequestBuilder;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.SnomedConstants.LanguageCodeReferenceSetIdentifierMapping;
import com.b2international.snowowl.snomed.datastore.request.SnomedSearchRequest.OptionKey;

/**
 * Abstract class for SNOMED CT search request builders. It collects functionality common to SNOMED CT artefacts.
 * @since 4.5
 */
public abstract class SnomedSearchRequestBuilder<B extends SnomedSearchRequestBuilder<B, R>, R> extends RevisionSearchRequestBuilder<B, R> {

	/**
	 * Filter to return components with the specified module id.
	 * 
	 * @param moduleId
	 * @return SnomedSearchRequestBuilder
	 * @see Concepts
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
	 * TODO: What does this method do?
	 * @param languageRefSetIds
	 * @return
	 */
	public final B filterByLanguageRefSetIds(List<String> languageRefSetIds) {
		return addOption(OptionKey.LANGUAGE_REFSET, languageRefSetIds);
	}
	
	/**
	 * Filter to return components with the specified effective time represented as a string in yyyy-MM-dd format
	 * @param effectiveTime in yyyy-MM-dd format
	 * @return SnomedSearchRequestBuilder
	 * @see DateFormats#DEFAULT
	 */
	public final B filterByEffectiveTime(String effectiveTime) {
		if (CompareUtils.isEmpty(effectiveTime)) {
			return getSelf(); 
		} else {
			return filterByEffectiveTime(EffectiveTimes.parse(effectiveTime, DateFormats.SHORT).getTime());
		}
	}
	
	/**
	 * Filter to return components with the specified effective time represented as a long (ms since epoch) format.
	 * @param effectiveTime in long (ms since epoch) format
	 * @return SnomedSearchRequestBuilder
	 * @see Date#Date(long)
	 * @see Date#getTime()
	 */
	public final B filterByEffectiveTime(long effectiveTime) {
		return filterByEffectiveTime(effectiveTime, effectiveTime);
	}
	
	/**
	 * Filter to return components with the effective times that fall between the start and end dates
	 * represented as longs (ms since epoch).
	 * @param effectiveTime starting effective time in long (ms since epoch) format
	 * @param effectiveTime ending effective time in long (ms since epoch) format
	 * @return SnomedSearchRequestBuilder
	 * @see Date#Date(long)
	 * @see Date#getTime()
	 */
	public final B filterByEffectiveTime(long from, long to) {
		return addOption(OptionKey.EFFECTIVE_TIME_START, from).addOption(OptionKey.EFFECTIVE_TIME_END, to);
	}
	
	/**
	 * TODO:
	 * @param locales
	 * @return
	 */
	public final B filterByExtendedLocales(List<ExtendedLocale> locales) {
		final List<String> languageRefSetIds = newArrayList();
		final List<ExtendedLocale> unconvertableLocales = new ArrayList<ExtendedLocale>();
		for (ExtendedLocale extendedLocale : locales) {
			final String languageRefSetId;
			
			if (!extendedLocale.getLanguageRefSetId().isEmpty()) {
				languageRefSetId = extendedLocale.getLanguageRefSetId();
			} else {
				languageRefSetId = LanguageCodeReferenceSetIdentifierMapping.getReferenceSetIdentifier(extendedLocale.getLanguageTag());
			}
			
			if (languageRefSetId == null) {
				unconvertableLocales.add(extendedLocale);
			} else {
				languageRefSetIds.add(languageRefSetId);
			}
		}
		
		if (languageRefSetIds.isEmpty() && !unconvertableLocales.isEmpty()) {
			throw new BadRequestException("Don't know how to convert extended locale " + unconvertableLocales.get(0).toString() + " to a language reference set identifier.");
		}
		
		return filterByLanguageRefSetIds(languageRefSetIds).setLocales(locales);
	}
}
