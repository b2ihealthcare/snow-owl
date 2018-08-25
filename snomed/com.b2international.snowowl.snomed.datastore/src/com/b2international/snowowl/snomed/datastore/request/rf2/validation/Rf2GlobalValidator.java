/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.request.rf2.validation;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.b2international.collections.longs.LongIterator;
import com.b2international.collections.longs.LongKeyMap;
import com.b2international.collections.longs.LongSet;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.domain.IComponent;
import com.b2international.snowowl.core.request.SearchResourceRequestIterator;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.request.SnomedConceptSearchRequestBuilder;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.datastore.request.rf2.importer.Rf2EffectiveTimeSlice;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

/**
 * Validates the Rf2 import files against the existing graph
 * 
 * @since 7.0
 */
public class Rf2GlobalValidator {

	public void validateTerminologyComponents(Iterable<Rf2EffectiveTimeSlice> orderedEffectiveTimeSlices, Rf2ValidationIssueReporter reporter, BranchContext context) {
		final int slices = Iterables.size(orderedEffectiveTimeSlices);
		for (int i = 0; i < slices ; i++) {
			final Rf2EffectiveTimeSlice currentSlice = Iterables.get(orderedEffectiveTimeSlices, i);
			final Map<String, String[]> currentRowsByComponentId = currentSlice.getContent();
			final Set<String> conceptIdsToFetch = Sets.newHashSet();
			
			final LongKeyMap<LongSet> dependenciesByComponent = currentSlice.getDependenciesByComponent();
			for (LongSet currentDependencyIds : dependenciesByComponent.values()) {
				final LongIterator it = currentDependencyIds.iterator();
				while (it.hasNext()) {
					final long dependencyId = it.next();
					final String stringDependencyId = Long.toString(dependencyId);
					boolean foundDependency = false;
					
					// current effectiveTimeSlice did not contain a required dependency check previous effectiveTimeSlices
					if (!currentRowsByComponentId.containsKey(stringDependencyId)) {
						for (int j = 0; j < i; j++) {
							final Rf2EffectiveTimeSlice previousEffectiveTimeSlice = Iterables.get(orderedEffectiveTimeSlices, j);
							final Map<String, String[]> previousRowsByComponentId = previousEffectiveTimeSlice.getContent();
							if (previousRowsByComponentId.containsKey(stringDependencyId)) {
								foundDependency = true;
								if (conceptIdsToFetch.contains(stringDependencyId)) {
									conceptIdsToFetch.remove(stringDependencyId);
								}
								break;
							}
							
						}
					} else {
						foundDependency = true;
						if (conceptIdsToFetch.contains(stringDependencyId)) {
							conceptIdsToFetch.remove(stringDependencyId);
						}
					}
					
					if (foundDependency == false) {
						conceptIdsToFetch.add(stringDependencyId);
					}
				}
				
				if (!conceptIdsToFetch.isEmpty()) {
					final Set<String> existingConceptIds = fetchConcepts(reporter, context, currentSlice, conceptIdsToFetch);
					final Set<String> issuesToReport = Sets.difference(conceptIdsToFetch, existingConceptIds);
					if (!issuesToReport.isEmpty()) {
						issuesToReport.forEach(id -> reporter.error(String.format("%s %s in effective time %s", Rf2ValidationDefects.MISSING_DEPENDANT_ID.getLabel(), id, currentSlice.getEffectiveTime())));
					}
				}
			}
		}
	}

	public void validateMembers(Iterable<Rf2EffectiveTimeSlice> orderedEffectiveTimeSlices, Rf2ValidationIssueReporter reporter, BranchContext context) {
		final int slices = Iterables.size(orderedEffectiveTimeSlices);
		for (int i = 0; i < slices; i++) {
			final Rf2EffectiveTimeSlice slice = Iterables.get(orderedEffectiveTimeSlices, i);
		}
	
	}

	private Set<String> fetchConcepts(Rf2ValidationIssueReporter reporter, BranchContext context, final Rf2EffectiveTimeSlice currentSlice, final Set<String> conceptIdsToFetch) {
		SnomedConceptSearchRequestBuilder conceptRequestBuilder = SnomedRequests.prepareSearchConcept()
				.all()
				.filterByIds(conceptIdsToFetch)
				.setFields(SnomedConceptDocument.Fields.ID)
				.setScroll("2m")
				.setLimit(10_000);

		SearchResourceRequestIterator<SnomedConceptSearchRequestBuilder, SnomedConcepts> conceptRequestIterator = new SearchResourceRequestIterator<SnomedConceptSearchRequestBuilder, SnomedConcepts>(
				conceptRequestBuilder, scrolledBuilder -> {
					return scrolledBuilder.build().execute(context);
				});

		final Set<String> existingConceptIds = Sets.newHashSet();
		while (conceptRequestIterator.hasNext()) {
			final SnomedConcepts existingConcepts = conceptRequestIterator.next();
			existingConceptIds.addAll(existingConcepts.stream().map(IComponent::getId).collect(Collectors.toSet()));
		}

		return existingConceptIds;
	}
	
}
