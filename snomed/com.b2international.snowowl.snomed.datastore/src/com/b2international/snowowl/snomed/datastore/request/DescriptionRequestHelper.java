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

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import com.b2international.commons.ExplicitFirstOrdering;
import com.b2international.commons.http.ExtendedLocale;
import com.b2international.snowowl.core.domain.IComponentRef;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.SnomedConstants.LanguageCodeReferenceSetIdentifierMapping;
import com.b2international.snowowl.snomed.core.domain.Acceptability;
import com.b2international.snowowl.snomed.core.domain.SnomedDescription;
import com.b2international.snowowl.snomed.core.domain.SnomedDescriptions;
import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;

/**
 * @since 4.5
 */
public abstract class DescriptionRequestHelper {

	private static class ExtractBestFunction<T> implements Function<Collection<SnomedDescription>, SnomedDescription> {
		
		private final List<T> orderedValues;
		private final Function<SnomedDescription, T> valuesExtractor;
		private final Ordering<SnomedDescription> ordering;

		private ExtractBestFunction(List<T> orderedValues, Function<SnomedDescription, T> valuesExtractor) {
			this.orderedValues = orderedValues;
			this.valuesExtractor = valuesExtractor;
			this.ordering = ExplicitFirstOrdering.create(orderedValues).onResultOf(valuesExtractor);
		}

		@Override 
		public SnomedDescription apply(Collection<SnomedDescription> descriptions) {
			try {
				
				SnomedDescription candidate = ordering.max(descriptions);
				
				/*
				 * We're using ExplicitFirstOrdering so that it doesn't break in the middle of processing 
				 * the collection, but this means that we have to test the final SnomedDescription again 
				 * to see if it is suitable for our needs.
				 */
				if (orderedValues.contains(valuesExtractor.apply(candidate))) {
					return candidate;
				} else {
					return null;
				}
				
			} catch (NoSuchElementException e) {
				return null;
			}
		}
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
			String languageRefSetId;
	
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
			throw new IllegalArgumentException("Don't know how to convert extended locale " + unconvertableLocales.get(0).toString() + " to a language reference set identifier.");
		}
		
		return languageRefSetIds;
	}

	/**
	 * Retrieves the preferred term for the concept identified by the given {@link IComponentRef component reference}, if it exists. 
	 * <p>
	 * The first active description with "synonym" or descendant as the type will be returned, where all of the following conditions apply:
	 * <ul>
	 * <li>a matching well-known language reference set exists for the given {@code ExtendedLocale} (eg. {@code "en-GB"});
	 * <li>the description has a language reference set member in the reference set identified above with preferred acceptability.
	 * </ul>
	 * <p>
	 * If no such description can be found, the process is repeated with the next {@code Locale} in the list.
	 * 
	 * @param conceptId 	the identifier of the concept for which the preferred term should be returned (may not be {@code null})
	 * @param locales		a list of {@link Locale}s to use, in order of preference
	 * @return 				the preferred term for the concept, or {@code null} if no results could be retrieved
	 */
	public SnomedDescription getPreferredTerm(String conceptId, List<ExtendedLocale> locales) {
		SnomedDescriptionSearchRequestBuilder req = preparePtSearch(conceptId, locales);
		SnomedDescriptions descriptions = execute(req);
		Map<String, SnomedDescription> bestMatchByConceptId = indexBestPreferredByConceptId(descriptions, locales);
		return bestMatchByConceptId.get(conceptId);
	}
	
	public Map<String, SnomedDescription> getPreferredTerms(Set<String> conceptIds, List<ExtendedLocale> locales) {
		if (conceptIds.isEmpty()) {
			return Collections.emptyMap();
		}
		
		SnomedDescriptionSearchRequestBuilder req = preparePtSearch(conceptIds, locales);
		SnomedDescriptions descriptions = execute(req);
		Map<String, SnomedDescription> bestMatchByConceptId = indexBestPreferredByConceptId(descriptions, locales);
		return bestMatchByConceptId;
	}

	/**
	 * Retrieves the fully specified name for the concept identified by the given {@link IComponentRef component reference}, if it exists. 
	 * <p>
	 * The first active description with "fully specified name" as the type will be returned, where all of the following conditions apply:
	 * <ul>
	 * <li>a matching well-known language reference set exists for the given {@code Locale} (eg. {@code "en-GB"});
	 * <li>the description has a language reference set member in the reference set identified above with preferred acceptability.
	 * </ul>
	 * <p>
	 * If no such description can be found, the search is repeated with the following conditions:
	 * <ul>
	 * <li>the description's language code matches the supplied {@code Locale}'s language (eg. {@code "en"} on description, {@code "en-US"} on {@code Locale});
	 * </ul>
	 * <p>
	 * Failing that, the whole check starts from the beginning with the next {@link Locale} in the list.
	 * The method falls back to the first active fully specified name if the language code does not match any of the specified {@code Locale}s.
	 * 
	 * @param conceptRef the reference to the concept for which the preferred term should be returned (may not be {@code null})
	 * @param locales    a list of {@link Locale}s to use, in order of preference
	 * @return the preferred term for the concept
	 */
	public SnomedDescription getFullySpecifiedName(String conceptId, List<ExtendedLocale> locales) {
		SnomedDescriptionSearchRequestBuilder acceptabilityReq = prepareFsnSearchByAcceptability(conceptId, locales);
		SnomedDescriptions preferredDescriptions = execute(acceptabilityReq);
		Map<String, SnomedDescription> bestPreferredByConceptId = indexBestPreferredByConceptId(preferredDescriptions, locales);
		
		if (bestPreferredByConceptId.containsKey(conceptId)) {
			return bestPreferredByConceptId.get(conceptId);
		}
		
		List<String> languageCodes = getLanguageCodes(locales);
		
		SnomedDescriptionSearchRequestBuilder languageCodeReq = prepareFsnSearchByLanguageCodes(conceptId, languageCodes);
		SnomedDescriptions languageCodeDescriptions = execute(languageCodeReq);
		Map<String, SnomedDescription> bestLanguageByConceptId = indexBestLanguageByConceptId(languageCodeDescriptions, languageCodes);

		if (bestLanguageByConceptId.containsKey(conceptId)) {
			return bestLanguageByConceptId.get(conceptId);
		}

		SnomedDescriptionSearchRequestBuilder defaultReq = prepareFsnSearchDefault(conceptId);
		SnomedDescriptions activeFsnDescriptions = execute(defaultReq);

		/* 
		 * XXX: we usually expect to see just one active FSN at this point, but depending on the given ExtendedLocale combinations,
		 * there might be more candidates remaining. 
		 */
		return Iterables.getFirst(activeFsnDescriptions, null); 
	}

	public Map<String, SnomedDescription> getFullySpecifiedNames(Set<String> conceptIds, List<ExtendedLocale> locales) {
		if (conceptIds.isEmpty()) {
			return Collections.emptyMap();
		}
		
		Map<String, SnomedDescription> fsnMap = newHashMap();
		Set<String> conceptIdsNotInMap;
		
		SnomedDescriptionSearchRequestBuilder acceptabilityReq = prepareFsnSearchByAcceptability(conceptIds, locales);
		SnomedDescriptions preferredDescriptions = execute(acceptabilityReq);
		fsnMap.putAll(indexBestPreferredByConceptId(preferredDescriptions, locales));
		
		conceptIdsNotInMap = Sets.difference(conceptIds, fsnMap.keySet());
		if (conceptIdsNotInMap.isEmpty()) {
			return fsnMap;
		}
		
		List<String> languageCodes = getLanguageCodes(locales);
		SnomedDescriptionSearchRequestBuilder languageCodeReq = prepareFsnSearchByLanguageCodes(conceptIdsNotInMap, languageCodes);
		SnomedDescriptions languageCodeDescriptions = execute(languageCodeReq);
		fsnMap.putAll(indexBestLanguageByConceptId(languageCodeDescriptions, languageCodes));
		
		conceptIdsNotInMap = Sets.difference(conceptIds, fsnMap.keySet());
		if (conceptIdsNotInMap.isEmpty()) {
			return fsnMap;
		}

		SnomedDescriptionSearchRequestBuilder defaultReq = prepareFsnSearchDefault(conceptIdsNotInMap);
		SnomedDescriptions activeFsnDescriptions = execute(defaultReq);
		fsnMap.putAll(indexFirstByConceptId(activeFsnDescriptions));

		return fsnMap;
	}

	private List<String> getLanguageCodes(List<ExtendedLocale> locales) {
		return FluentIterable.from(locales)
				.transform(new Function<ExtendedLocale, String>() { @Override public String apply(ExtendedLocale input) { return input.getLanguage(); } })
				.toSet() // preserves iteration order, but makes elements unique
				.asList();
	}

	// FSN requests
	
	private SnomedDescriptionSearchRequestBuilder prepareFsnSearchByAcceptability(String conceptId, List<ExtendedLocale> locales) {
		return prepareFsnSearchDefault(conceptId)
				.filterByAcceptability(Acceptability.PREFERRED)
				.filterByExtendedLocales(locales);
	}
	
	private SnomedDescriptionSearchRequestBuilder prepareFsnSearchByAcceptability(Collection<String> conceptIds, List<ExtendedLocale> locales) {
		return prepareFsnSearchDefault(conceptIds)
				.filterByAcceptability(Acceptability.PREFERRED)
				.filterByExtendedLocales(locales);
	}
	
	private SnomedDescriptionSearchRequestBuilder prepareFsnSearchByLanguageCodes(String conceptId, List<String> languageCodes) {
		return prepareFsnSearchDefault(conceptId)
				.filterByLanguageCodes(languageCodes);
	}
	
	private SnomedDescriptionSearchRequestBuilder prepareFsnSearchByLanguageCodes(Collection<String> conceptIds, List<String> languageCodes) {
		return prepareFsnSearchDefault(conceptIds)
				.filterByLanguageCodes(languageCodes);
	}

	private SnomedDescriptionSearchRequestBuilder prepareFsnSearchDefault(String conceptId) {
		return SnomedRequests.prepareSearchDescription()
				.all()
				.filterByActive(true)
				.filterByConcept(conceptId)
				.filterByType(Concepts.FULLY_SPECIFIED_NAME);
	}
	
	private SnomedDescriptionSearchRequestBuilder prepareFsnSearchDefault(Collection<String> conceptIds) {
		return SnomedRequests.prepareSearchDescription()
				.all()
				.filterByActive(true)
				.filterByConceptId(conceptIds)
				.filterByType(Concepts.FULLY_SPECIFIED_NAME);
	}

	// PT requests
	
	private SnomedDescriptionSearchRequestBuilder preparePtSearch(String conceptId, List<ExtendedLocale> locales) {
		return SnomedRequests.prepareSearchDescription()
				.all()
				.filterByActive(true)
				.filterByConcept(conceptId)
				.filterByType("<<" + Concepts.SYNONYM)
				.filterByAcceptability(Acceptability.PREFERRED)
				.filterByExtendedLocales(locales);
	}
	
	private SnomedDescriptionSearchRequestBuilder preparePtSearch(Collection<String> conceptIds, List<ExtendedLocale> locales) {
		return SnomedRequests.prepareSearchDescription()
				.all()
				.filterByActive(true)
				.filterByConceptId(conceptIds)
				.filterByType("<<" + Concepts.SYNONYM)
				.filterByAcceptability(Acceptability.PREFERRED)
				.filterByExtendedLocales(locales);
	}

	private Map<String, SnomedDescription> indexBestPreferredByConceptId(SnomedDescriptions descriptions, List<ExtendedLocale> orderedLocales) {
		List<String> languageRefSetIds = getLanguageRefSetIds(orderedLocales);
		ExplicitFirstOrdering<String> languageRefSetOrdering = ExplicitFirstOrdering.create(languageRefSetIds);
		
		return extractBest(indexByConceptId(descriptions), languageRefSetIds, description -> {
			Set<String> preferredLanguageRefSetIds = Maps.filterValues(description.getAcceptabilityMap(), Predicates.equalTo(Acceptability.PREFERRED)).keySet();
			return languageRefSetOrdering.max(preferredLanguageRefSetIds);
		});
	}
	
	private Map<String, SnomedDescription> indexBestLanguageByConceptId(SnomedDescriptions descriptions, List<String> orderedLanguages) {
		return extractBest(indexByConceptId(descriptions), orderedLanguages, description -> description.getLanguageCode());
	}
	
	private Map<String, SnomedDescription> indexFirstByConceptId(SnomedDescriptions descriptions) {
		return extractFirst(indexByConceptId(descriptions));
	}

	private Multimap<String, SnomedDescription> indexByConceptId(SnomedDescriptions descriptions) {
		return Multimaps.index(descriptions.getItems(), description -> description.getConceptId());
	}
	
	private Map<String, SnomedDescription> extractFirst(Multimap<String, SnomedDescription> descriptionsByConceptId) {
		Map<String, SnomedDescription> uniqueMap = Maps.transformValues(descriptionsByConceptId.asMap(), values -> Iterables.getFirst(values, null));
		return ImmutableMap.copyOf(Maps.filterValues(uniqueMap, Predicates.notNull()));
	}
	
	private <T> Map<String, SnomedDescription> extractBest(Multimap<String, SnomedDescription> descriptionsByConceptId, 
			List<T> orderedValues, 
			Function<SnomedDescription, T> predicateFactory) {
		
		Map<String, SnomedDescription> uniqueMap = Maps.transformValues(descriptionsByConceptId.asMap(), new ExtractBestFunction<T>(orderedValues, predicateFactory));
		return ImmutableMap.copyOf(Maps.filterValues(uniqueMap, Predicates.notNull()));
	}
	
	protected abstract SnomedDescriptions execute(SnomedDescriptionSearchRequestBuilder req);
}
