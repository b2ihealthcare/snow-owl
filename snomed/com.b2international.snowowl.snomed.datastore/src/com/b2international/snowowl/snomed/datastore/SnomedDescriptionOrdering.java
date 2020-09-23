/*******************************************************************************
 * Copyright (c) 2020 B2i Healthcare. All rights reserved.
 *******************************************************************************/
package com.b2international.snowowl.snomed.datastore;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import com.b2international.commons.ExplicitFirstOrdering;
import com.b2international.commons.http.ExtendedLocale;
import com.b2international.snowowl.snomed.core.domain.Acceptability;
import com.b2international.snowowl.snomed.core.domain.SnomedDescription;
import com.b2international.snowowl.snomed.datastore.request.SnomedDescriptionSearchRequestBuilder;
import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Ordering;

/**
 * @since 7.10.0
 */
public final class SnomedDescriptionOrdering {

	@SuppressWarnings("deprecation")
	public static Map<String, SnomedDescription> indexBestPreferredByConceptId(final Iterable<SnomedDescription> descriptions, final List<ExtendedLocale> orderedLocales) {

		final List<String> languageRefSetIds = SnomedDescriptionSearchRequestBuilder.getLanguageRefSetIds(orderedLocales);
		final ExplicitFirstOrdering<String> languageRefSetOrdering = ExplicitFirstOrdering.create(languageRefSetIds);

		return extractBest(indexByConceptId(descriptions), languageRefSetIds, description -> {
			final Set<String> preferredLanguageRefSetIds = Maps.filterValues(description.getAcceptabilityMap(), Predicates.equalTo(Acceptability.PREFERRED)).keySet();
			// the explicit first ordering will put the VIP / anticipated / first priority languages codes to the min end.
			return languageRefSetOrdering.min(preferredLanguageRefSetIds);
		});

	}

	private static Multimap<String, SnomedDescription> indexByConceptId(final Iterable<SnomedDescription> descriptions) {
		return Multimaps.index(descriptions, SnomedDescription::getConceptId);
	}

	private static <T> Map<String, SnomedDescription> extractBest(final Multimap<String, SnomedDescription> descriptionsByConceptId,
			final List<T> orderedValues,
			final Function<SnomedDescription, T> predicateFactory) {

		final Map<String, SnomedDescription> uniqueMap = Maps.transformValues(descriptionsByConceptId.asMap(), new ExtractBestFunction<T>(orderedValues, predicateFactory));
		return ImmutableMap.copyOf(Maps.filterValues(uniqueMap, Predicates.notNull()));
	}

	private static class ExtractBestFunction<T> implements Function<Collection<SnomedDescription>, SnomedDescription> {

		private final List<T> orderedValues;
		private final Function<SnomedDescription, T> valuesExtractor;
		private final Ordering<SnomedDescription> ordering;

		private ExtractBestFunction(final List<T> orderedValues, final Function<SnomedDescription, T> valuesExtractor) {
			this.orderedValues = orderedValues;
			this.valuesExtractor = valuesExtractor;
			this.ordering = ExplicitFirstOrdering.create(orderedValues).onResultOf(valuesExtractor);
		}

		@Override
		public SnomedDescription apply(final Collection<SnomedDescription> descriptions) {
			try {

				final SnomedDescription candidate = ordering.min(descriptions);

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

			} catch (final NoSuchElementException e) {
				return null;
			}
		}
	}

	private SnomedDescriptionOrdering() {}

}
