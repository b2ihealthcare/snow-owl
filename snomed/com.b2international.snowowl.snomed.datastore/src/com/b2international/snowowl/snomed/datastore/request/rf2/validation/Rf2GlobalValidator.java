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

	public void validate(Iterable<Rf2EffectiveTimeSlice> orderedEffectiveTimeSlices, Rf2ValidationIssueReporter reporter, BranchContext context) {
		final int slices = Iterables.size(orderedEffectiveTimeSlices);
		for (int i = 0; i < slices; i++) {
			final Set<String> conceptIdsToFetch = Sets.newHashSet();
			final Rf2EffectiveTimeSlice currentSlice = Iterables.get(orderedEffectiveTimeSlices, i);
			final LongKeyMap<LongSet> currentDependencies = currentSlice.getDependenciesByComponent();
			
			for (LongSet currentDependencyIds : currentDependencies.values()) {
				final LongIterator it = currentDependencyIds.iterator();
				while (it.hasNext()) {
					final long dependantId = it.next();
					boolean foundDependency = false;
					// dependency was not found in the current slice, check previous slices
					if (!currentDependencies.containsKey(dependantId)) {
						for (int j = 0; j < i; j++) {
							final Rf2EffectiveTimeSlice previousEffectiveTimeSlice = Iterables.get(orderedEffectiveTimeSlices, j);
							final boolean foundId = previousEffectiveTimeSlice.getDependenciesByComponent().containsKey(dependantId);
							if (foundId) {
								foundDependency = true;
								break;
							}
						}
					} 
					// dependency was not found in the previous slices collect id-s
					if (foundDependency == false) {
						conceptIdsToFetch.add(Long.toString(dependantId));
					}
				}
			}
			
			// send request of the collected id-s to check
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
			
			final Set<String> issuesToReport = Sets.difference(conceptIdsToFetch, existingConceptIds);
			if (!issuesToReport.isEmpty()) {
				issuesToReport.forEach(id -> reporter.error(String.format("%s %s", Rf2ValidationDefects.MISSING_DEPENDANT_ID.getLabel(), id)));
			}
						
		}
	}

}
