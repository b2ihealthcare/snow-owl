/*
 * Copyright 2020-2023 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.snowowl.snomed.datastore;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.b2international.commons.CompareUtils;
import com.b2international.commons.ExplicitFirstOrdering;
import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.commons.http.ExtendedLocale;
import com.b2international.snowowl.core.TerminologyResource;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.domain.Acceptability;
import com.b2international.snowowl.snomed.core.domain.SnomedDescription;
import com.b2international.snowowl.snomed.datastore.config.SnomedLanguageConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.*;

/**
 * @since 7.10.0
 */
public final class SnomedDescriptionUtils {

	public static Map<String, SnomedDescription> indexBestPreferredByConceptId(final BranchContext context, final Iterable<SnomedDescription> descriptions, final List<ExtendedLocale> extendedLocales) {

		if (extendedLocales.isEmpty()) {
			return Map.of();
		}
		
		final List<String> languageRefSetIds = getLanguageRefSetIds(context, extendedLocales);
		final ExplicitFirstOrdering<String> languageRefSetOrdering = ExplicitFirstOrdering.create(languageRefSetIds);
		final Multimap<String, SnomedDescription> conceptIdToDescriptionsMap = Multimaps.index(descriptions, SnomedDescription::getConceptId);

		final Map<String, SnomedDescription> conceptToBestPreferredDescriptionMap = newHashMap();

		for (final Entry<String, Collection<SnomedDescription>> entry : conceptIdToDescriptionsMap.asMap().entrySet()) {

			final String conceptId = entry.getKey();
			final Collection<SnomedDescription> conceptDescriptions = entry.getValue();

			final Optional<SnomedDescription> bestPreferredDescription = conceptDescriptions.stream()
					.sorted((d1,d2) -> {

						final Set<String> preferredLanguageRefsetIds1 = getPreferredLanguageRefsetIds(languageRefSetIds, d1);
						final Set<String> preferredLanguageRefsetIds2 = getPreferredLanguageRefsetIds(languageRefSetIds, d2);

						if (preferredLanguageRefsetIds1.isEmpty() && preferredLanguageRefsetIds2.isEmpty()) {
							return 0;
						} else if (preferredLanguageRefsetIds1.isEmpty()) {
							return 1;
						} else if (preferredLanguageRefsetIds2.isEmpty()) {
							return -1;
						}

						final String preferredLanguageRefsetId1 = languageRefSetOrdering.min(preferredLanguageRefsetIds1);
						final String preferredLanguageRefsetId2 = languageRefSetOrdering.min(preferredLanguageRefsetIds2);
						
						return languageRefSetOrdering.compare(preferredLanguageRefsetId1, preferredLanguageRefsetId2);

					})
					.findFirst();
			
			if (bestPreferredDescription.isPresent()) {
				
				if (getPreferredLanguageRefsetIds(languageRefSetIds, bestPreferredDescription.get()).isEmpty()) {
					// if the best description is not preferred in any of the provided 
					// extended locales then do not return anything for the concept 
					continue;
				} else {
					conceptToBestPreferredDescriptionMap.put(conceptId, bestPreferredDescription.get());
				}
				
			}

		}

		return conceptToBestPreferredDescriptionMap;
	}

	@SuppressWarnings("deprecation")
	private static Set<String> getPreferredLanguageRefsetIds(final List<String> languageRefSetIds, final SnomedDescription description) {
		return Maps.filterEntries(description.getAcceptabilityMap(), 
				acceptabilityEntry -> languageRefSetIds.contains(acceptabilityEntry.getKey()) && Acceptability.PREFERRED == acceptabilityEntry.getValue()).keySet();
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
	 * @param context - the context to use to retrieve language map configuration
	 * @param locales  the extended locale list to process (may not be {@code null})
	 * @return the converted language reference set identifiers or an empty {@link List}, never <code>null</code>
	 */
	public static List<String> getLanguageRefSetIds(final BranchContext context, final List<ExtendedLocale> locales) {
		return getLanguageRefSetIds(getLanguageMapping(context), locales);
	}

	public static List<String> getLanguageRefSetIds(final ListMultimap<String, String> languageMap, final List<ExtendedLocale> locales) {
		if (CompareUtils.isEmpty(locales)) {
			return Collections.emptyList();
		}
		
		final List<String> languageRefSetIds = newArrayList();
		final List<ExtendedLocale> unconvertableLocales = new ArrayList<ExtendedLocale>();

		for (final ExtendedLocale extendedLocale : locales) {
			Collection<String> mappedRefSetIds;

			if (!extendedLocale.getLanguageRefSetId().isEmpty()) {
				mappedRefSetIds = Collections.singleton(extendedLocale.getLanguageRefSetId());
			} else {
				mappedRefSetIds = languageMap.get(extendedLocale.getLanguageTag());
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
			throw new BadRequestException("Don't know how to convert extended locale %s to a language reference set identifier.", Iterables.toString(unconvertableLocales));
		}

		return languageRefSetIds;
	}
	
	/**
	 * Returns the currently configured dialect alias configuration from the given context. If there is no such setting configured it returns an empty {@link ListMultimap}.
	 * @param context
	 * @return a {@link ListMultimap} that has all the configured dialect aliases, never <code>null</code>
	 */
	public static ListMultimap<String,String> getLanguageMapping(final BranchContext context) {
		return getLanguageMapping(context.service(ObjectMapper.class), context.service(TerminologyResource.class));
	}

	/**
	 * Returns the currently configured dialect alias configuration from the given {@link TerminologyResource}'s settings. If there is no such setting configured it returns an empty {@link ListMultimap}.
	 * @param mapper
	 * @param resource
	 * @return a {@link ListMultimap} that has all the configured dialect aliases, never <code>null</code>
	 */
	public static ListMultimap<String, String> getLanguageMapping(final ObjectMapper mapper, final TerminologyResource resource) {
		ListMultimap<String, String> languageMap = ArrayListMultimap.create();
		getLanguagesConfiguration(mapper, resource).forEach(mapping -> languageMap.putAll(mapping.getLanguageTag(), mapping.getLanguageRefSetIds()));
		return languageMap;
	}

	/**
	 * Returns the currently configured language settings from the given {@link TerminologyResource}'s settings. If there is no such setting configured it returns an empty {@link List}.
	 * @param mapper
	 * @param resource
	 * @return a {@link List} that has all the configured language settings, never <code>null</code>
	 */
	public static List<SnomedLanguageConfig> getLanguagesConfiguration(final ObjectMapper mapper, final TerminologyResource resource) {
		return ((List<Map<String, Object>>) resource.getSettings().getOrDefault(SnomedTerminologyComponentConstants.CODESYSTEM_LANGUAGE_CONFIG_KEY, List.of()))
				.stream()
				.map(config -> mapper.convertValue(config, SnomedLanguageConfig.class))
				.collect(Collectors.toList());
	}
	
	private SnomedDescriptionUtils() {}

}
