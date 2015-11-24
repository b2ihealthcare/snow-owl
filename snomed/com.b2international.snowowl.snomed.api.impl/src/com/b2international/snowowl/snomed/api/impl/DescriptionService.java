package com.b2international.snowowl.snomed.api.impl;

import static com.google.common.collect.Maps.newHashMap;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import javax.annotation.Resource;

import com.b2international.commons.functions.StringToLongFunction;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.domain.IComponentRef;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.SnomedConstants.LanguageCodeReferenceSetIdentifierMapping;
import com.b2international.snowowl.snomed.core.domain.Acceptability;
import com.b2international.snowowl.snomed.core.domain.ISnomedDescription;
import com.b2international.snowowl.snomed.core.domain.SnomedDescriptions;
import com.b2international.snowowl.snomed.datastore.server.request.SnomedRequests;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Sets;

public class DescriptionService {
	
	@Resource
	protected IEventBus bus;

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
	public ISnomedDescription getPreferredTerm(final String branch, final String conceptId, final List<Locale> locales) {
		return Iterables.getOnlyElement(SnomedRequests.prepareDescriptionSearch()
				.one()
				.filterByActive(true)
				.filterByConceptId(conceptId)
				.filterByType("<<" + Concepts.SYNONYM)
				.filterByAcceptability(Acceptability.PREFERRED)
				.filterByLanguageRefSetIds(StringToLongFunction.copyOf(LanguageCodeReferenceSetIdentifierMapping.getReferenceSetIdentifiers(locales)))
				.build(branch)
				.executeSync(bus)
				.getItems(), null);
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
	public ISnomedDescription getFullySpecifiedName(final String branch, final String conceptId, final List<Locale> locales) {
		ISnomedDescription fsn = Iterables.getOnlyElement(SnomedRequests.prepareDescriptionSearch()
				.one()
				.filterByActive(true)
				.filterByConceptId(conceptId)
				.filterByType(Concepts.FULLY_SPECIFIED_NAME)
				.filterByAcceptability(Acceptability.PREFERRED)
				.filterByLanguageRefSetIds(StringToLongFunction.copyOf(LanguageCodeReferenceSetIdentifierMapping.getReferenceSetIdentifiers(locales)))
				.build(branch)
				.executeSync(bus)
				.getItems(), null);
		
		if (fsn != null) {
			return fsn;
		}
		
		final ImmutableSet.Builder<String> languageCodes = ImmutableSet.builder();
		for (Locale locale : locales) {
			languageCodes.add(locale.getLanguage().toLowerCase(Locale.ENGLISH));
		}
		
		fsn = Iterables.getOnlyElement(SnomedRequests.prepareDescriptionSearch()
				.one()
				.filterByActive(true)
				.filterByConceptId(conceptId)
				.filterByType(Concepts.FULLY_SPECIFIED_NAME)
				.filterByLanguageCodes(languageCodes.build())
				.build(branch)
				.executeSync(bus)
				.getItems(), null);
		
		if (fsn != null) {
			return fsn;
		}

		return Iterables.getOnlyElement(SnomedRequests.prepareDescriptionSearch()
				.one()
				.filterByActive(true)
				.filterByConceptId(conceptId)
				.filterByType(Concepts.FULLY_SPECIFIED_NAME)
				.build(branch)
				.executeSync(bus)
				.getItems(), null);
	}

	public Map<String, ISnomedDescription> getFullySpecifiedNames(String branch, Set<String> conceptIds, List<Locale> locales) {
		if (conceptIds.isEmpty()) {
			return Collections.emptyMap();
		}
		
		Request<ServiceProvider, SnomedDescriptions> request = SnomedRequests.prepareDescriptionSearch()
			.all()
			.filterByActive(true)
			.filterByConceptId(Collections2.transform(conceptIds, new StringToLongFunction()))
			.filterByType(Concepts.FULLY_SPECIFIED_NAME)
			.filterByAcceptability(Acceptability.PREFERRED)
			.filterByLanguageRefSetIds(StringToLongFunction.copyOf(LanguageCodeReferenceSetIdentifierMapping.getReferenceSetIdentifiers(locales)))
			.build(branch);

		final Map<String, ISnomedDescription> fsnMap = newHashMap();
		
		try {
			Map<String, ISnomedDescription> preferredFsnMap = convertToMap(request.execute(bus)).get();
			fsnMap.putAll(preferredFsnMap);
		} catch (InterruptedException | ExecutionException e) {
			throw SnowowlRuntimeException.wrap(e);
		}
		
		Set<String> conceptIdsNotInMap = Sets.difference(conceptIds, fsnMap.keySet());
		if (conceptIdsNotInMap.isEmpty()) {
			return fsnMap;
		}
		
		final ImmutableSet.Builder<String> languageCodes = ImmutableSet.builder();
		for (Locale locale : locales) {
			languageCodes.add(locale.getLanguage().toLowerCase(Locale.ENGLISH));
		}

		request = SnomedRequests.prepareDescriptionSearch()
				.all()
				.filterByActive(true)
				.filterByConceptId(Collections2.transform(conceptIdsNotInMap, new StringToLongFunction()))
				.filterByType(Concepts.FULLY_SPECIFIED_NAME)
				.filterByLanguageCodes(languageCodes.build())
				.build(branch);

		try {
			Map<String, ISnomedDescription> preferredFsnMap = convertToMap(request.execute(bus)).get();
			fsnMap.putAll(preferredFsnMap);
		} catch (InterruptedException | ExecutionException e) {
			throw SnowowlRuntimeException.wrap(e);
		}
		
		conceptIdsNotInMap = Sets.difference(conceptIds, fsnMap.keySet());
		if (conceptIdsNotInMap.isEmpty()) {
			return fsnMap;
		}

		request = SnomedRequests.prepareDescriptionSearch()
				.all()
				.filterByActive(true)
				.filterByConceptId(Collections2.transform(conceptIdsNotInMap, new StringToLongFunction()))
				.filterByType(Concepts.FULLY_SPECIFIED_NAME)
				.build(branch);

		try {
			Map<String, ISnomedDescription> preferredFsnMap = convertToMap(request.execute(bus)).get();
			fsnMap.putAll(preferredFsnMap);
		} catch (InterruptedException | ExecutionException e) {
			throw SnowowlRuntimeException.wrap(e);
		}

		return fsnMap;
	}

	private Promise<Map<String, ISnomedDescription>> convertToMap(Promise<SnomedDescriptions> descriptionsPromise) {
		return descriptionsPromise.then(new Function<SnomedDescriptions, Multimap<String, ISnomedDescription>>() {
			@Override public Multimap<String, ISnomedDescription> apply(SnomedDescriptions descriptions) {
				return indexByConceptId(descriptions);
			}
		})
		.then(new Function<Multimap<String,ISnomedDescription>, Map<String, ISnomedDescription>>() {
			@Override public Map<String, ISnomedDescription> apply(Multimap<String, ISnomedDescription> descriptionMultimap) {
				return extractFirstDescription(descriptionMultimap);
			}
		});
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
}
