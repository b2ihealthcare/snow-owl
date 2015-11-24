package com.b2international.snowowl.snomed.api.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.annotation.Resource;

import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.domain.IComponentRef;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.domain.Acceptability;
import com.b2international.snowowl.snomed.core.domain.ISnomedDescription;
import com.b2international.snowowl.snomed.core.domain.SnomedDescriptions;
import com.b2international.snowowl.snomed.datastore.server.request.SnomedRequests;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Maps.EntryTransformer;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

public class FsnService {
	
	@Resource
	protected IEventBus bus;

	public Map<String, String> getConceptIdFsnMap(IComponentRef conceptRef, final Collection<Long> conceptIds, final List<Locale> locales) {
		if (conceptIds.isEmpty()) {
			return Collections.emptyMap();
		}
		
		Request<ServiceProvider, SnomedDescriptions> request = SnomedRequests.prepareDescriptionSearch()
			.all()
			.filterByActive(true)
			.filterByConceptId(conceptIds)
			.filterByType(Concepts.FULLY_SPECIFIED_NAME)
			.filterByAcceptability(Acceptability.PREFERRED)
			.setLocales(locales)
			.build(conceptRef.getBranchPath());

		try {
			return request.execute(bus)
					.then(new Function<SnomedDescriptions, Multimap<String, ISnomedDescription>>() {
						@Override public Multimap<String, ISnomedDescription> apply(SnomedDescriptions descriptions) {
							return indexByConceptId(descriptions);
						}
					})
					.then(new Function<Multimap<String,ISnomedDescription>, Map<String, ISnomedDescription>>() {
						@Override public Map<String, ISnomedDescription> apply(Multimap<String, ISnomedDescription> descriptionMultimap) {
							return extractFirstDescription(descriptionMultimap);
						}
					})
					.then(new Function<Map<String,ISnomedDescription>, Map<String, String>>() {
						@Override public Map<String, String> apply(Map<String, ISnomedDescription> input) {
							return extractTerm(input);
						}
					})
					.get();
		} catch (InterruptedException | ExecutionException e) {
			throw SnowowlRuntimeException.wrap(e);
		}
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
	
	private Map<String, String> extractTerm(Map<String, ISnomedDescription> input) {
		return Maps.transformEntries(input, new EntryTransformer<String, ISnomedDescription, String>() {
			@Override public String transformEntry(String key, ISnomedDescription value) {
				return getTermOrId(key, value);
			}
		});
	}
	
	private String getTermOrId(String key, ISnomedDescription value) {
		return Optional.fromNullable(value)
				.transform(new Function<ISnomedDescription, String>() {
					@Override public String apply(ISnomedDescription input) {
						return input.getTerm();
					}})
				.or(key);
	}
}
