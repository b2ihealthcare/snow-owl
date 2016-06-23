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

import static com.google.common.collect.Maps.newHashMap;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.snowowl.core.domain.IComponentRef;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.domain.Acceptability;
import com.b2international.snowowl.snomed.core.domain.ISnomedDescription;
import com.b2international.snowowl.snomed.core.domain.SnomedDescriptions;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Sets;

/**
 * @since 4.5
 */
public abstract class DescriptionRequestHelper {

	/**
	 * Retrieves the preferred term for the concept identified by the given {@link IComponentRef component reference}, if it exists. 
	 * <p>
	 * The first active description with "synonym" or descendant as the type will be returned, where all of the following conditions apply:
	 * <ul>
	 * <li>a matching well-known language reference set exists for the given {@code Locale} (eg. {@code "en-GB"});
	 * <li>the description has a language reference set member in the reference set identified above with preferred acceptability.
	 * </ul>
	 * <p>
	 * If no such description can be found, the process is repeated with the next {@code Locale} in the list.
	 * 
	 * @param conceptRef 	the reference to the concept for which the preferred term should be returned (may not be {@code null})
	 * @param locales		a list of {@link Locale}s to use, in order of preference
	 * @return 				the preferred term for the concept, or {@code null} if no results could be retrieved
	 */
	public ISnomedDescription getPreferredTerm(final String conceptId, final List<ExtendedLocale> locales) {
		final SnomedDescriptionSearchRequestBuilder req = preparePtSearch(conceptId, locales);
		return Iterables.getOnlyElement(execute(req).getItems(), null);
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
	public ISnomedDescription getFullySpecifiedName(final String conceptId, final List<ExtendedLocale> locales) {
		final SnomedDescriptionSearchRequestBuilder req = prepareFsnSearchByAcceptability(conceptId, locales);
		ISnomedDescription fsn = Iterables.getOnlyElement(execute(req).getItems(), null);
		
		if (fsn != null) {
			return fsn;
		}
		
		final ImmutableSet.Builder<String> languageCodes = ImmutableSet.builder();
		for (ExtendedLocale locale : locales) {
			languageCodes.add(locale.getLanguage());
		}
		
		fsn = Iterables.getOnlyElement(execute(prepareFsnSearchByLanguageCodes(conceptId, languageCodes)).getItems(), null);
		
		if (fsn != null) {
			return fsn;
		}

		return Iterables.getOnlyElement(execute(prepareFsnSearchDefault(conceptId)).getItems(), null);
	}

	public Map<String, ISnomedDescription> getFullySpecifiedNames(Set<String> conceptIds, List<ExtendedLocale> locales) {
		if (conceptIds.isEmpty()) {
			return Collections.emptyMap();
		}
		
		final Map<String, ISnomedDescription> fsnMap = newHashMap();
		
		fsnMap.putAll(convertToMap(execute(prepareFsnSearchByAcceptability(conceptIds, locales))));
		
		Set<String> conceptIdsNotInMap = Sets.difference(conceptIds, fsnMap.keySet());
		if (conceptIdsNotInMap.isEmpty()) {
			return fsnMap;
		}
		
		final ImmutableSet.Builder<String> languageCodes = ImmutableSet.builder();
		for (ExtendedLocale locale : locales) {
			languageCodes.add(locale.getLanguage());
		}


		fsnMap.putAll(convertToMap(execute(prepareFsnSearchByLanguageCodes(conceptIdsNotInMap, languageCodes))));
		
		conceptIdsNotInMap = Sets.difference(conceptIds, fsnMap.keySet());
		if (conceptIdsNotInMap.isEmpty()) {
			return fsnMap;
		}

		fsnMap.putAll(convertToMap(execute(prepareFsnSearchDefault(conceptIdsNotInMap))));

		return fsnMap;
	}


	// FSN requests
	
	private SnomedDescriptionSearchRequestBuilder prepareFsnSearchByAcceptability(final String conceptId, final List<ExtendedLocale> locales) {
		return prepareFsnSearchDefault(conceptId)
				.filterByAcceptability(Acceptability.PREFERRED)
				.filterByExtendedLocales(locales);
	}
	
	private SnomedDescriptionSearchRequestBuilder prepareFsnSearchByAcceptability(final Collection<String> conceptIds, final List<ExtendedLocale> locales) {
		return prepareFsnSearchDefault(conceptIds)
				.filterByAcceptability(Acceptability.PREFERRED)
				.filterByExtendedLocales(locales);
	}
	
	private SnomedDescriptionSearchRequestBuilder prepareFsnSearchByLanguageCodes(final String conceptId, final ImmutableSet.Builder<String> languageCodes) {
		return prepareFsnSearchDefault(conceptId)
				.filterByLanguageCodes(languageCodes.build());
	}
	
	private SnomedDescriptionSearchRequestBuilder prepareFsnSearchByLanguageCodes(final Collection<String> conceptIds, final ImmutableSet.Builder<String> languageCodes) {
		return prepareFsnSearchDefault(conceptIds)
				.filterByLanguageCodes(languageCodes.build());
	}

	private SnomedDescriptionSearchRequestBuilder prepareFsnSearchDefault(final String conceptId) {
		return SnomedRequests.prepareSearchDescription()
				.one()
				.filterByActive(true)
				.filterByConceptId(conceptId)
				.filterByType(Concepts.FULLY_SPECIFIED_NAME);
	}
	
	private SnomedDescriptionSearchRequestBuilder prepareFsnSearchDefault(final Collection<String> conceptIds) {
		return SnomedRequests.prepareSearchDescription()
				.filterByActive(true)
				.filterByConceptId(conceptIds)
				.filterByType(Concepts.FULLY_SPECIFIED_NAME)
				.setLimit(conceptIds.size());
	}

	// PT requests
	
	private SnomedDescriptionSearchRequestBuilder preparePtSearch(final String conceptId, final List<ExtendedLocale> locales) {
		return SnomedRequests.prepareSearchDescription()
				.one()
				.filterByActive(true)
				.filterByConceptId(conceptId)
				.filterByType("<<" + Concepts.SYNONYM)
				.filterByAcceptability(Acceptability.PREFERRED)
				.filterByExtendedLocales(locales);
	}
	
	private SnomedDescriptionSearchRequestBuilder preparePtSearch(final Collection<String> conceptIds, final List<ExtendedLocale> locales) {
		return SnomedRequests.prepareSearchDescription()
				.filterByActive(true)
				.filterByConceptId(conceptIds)
				.filterByType("<<" + Concepts.SYNONYM)
				.filterByAcceptability(Acceptability.PREFERRED)
				.filterByExtendedLocales(locales)
				.setLimit(conceptIds.size());
	}
	

	private Map<String, ISnomedDescription> convertToMap(SnomedDescriptions descriptions) {
		return extractFirstDescription(indexByConceptId(descriptions));
	}
	
	private Multimap<String, ISnomedDescription> indexByConceptId(SnomedDescriptions descriptions) {
		return Multimaps.index(descriptions.getItems(), new Function<ISnomedDescription, String>() {
			@Override public String apply(ISnomedDescription description) {
				return description.getConceptId();
			}
		});
	}
	
	private Map<String, ISnomedDescription> extractFirstDescription(Multimap<String, ISnomedDescription> activeFsnsById) {
		return Maps.transformValues(activeFsnsById.asMap(), new Function<Collection<ISnomedDescription>, ISnomedDescription>() {
			@Override public ISnomedDescription apply(Collection<ISnomedDescription> descriptions) {
				return Iterables.getFirst(descriptions, null);
			}
		});
	}
	
	protected abstract SnomedDescriptions execute(SnomedDescriptionSearchRequestBuilder req);

	public Map<String, ISnomedDescription> getPreferredTerms(Set<String> conceptIds, List<ExtendedLocale> locales) {
		if (conceptIds.isEmpty()) {
			return Collections.emptyMap();
		}
		return convertToMap(execute(preparePtSearch(conceptIds, locales)));
	}
	
}
