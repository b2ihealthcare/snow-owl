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

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.b2international.collections.longs.LongIterator;
import com.b2international.collections.longs.LongSet;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.request.SearchResourceRequestIterator;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.request.SnomedConceptSearchRequestBuilder;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.datastore.request.rf2.importer.Rf2EffectiveTimeSlice;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Validates the Rf2 import files against the existing graph
 * 
 * @since 7.0
 */
public class Rf2GlobalValidator {

	public void validateTerminologyComponents(Iterable<Rf2EffectiveTimeSlice> slices, Rf2ValidationIssueReporter reporter, BranchContext context) {
		List<Rf2EffectiveTimeSlice> slicesToCheck = Lists.newArrayListWithExpectedSize(Iterables.size(slices)); 
		final Map<String, String> missingDependenciesInEffectiveTime = Maps.newHashMap();
		// check the slices first for missing dependencies
		for (Rf2EffectiveTimeSlice slice : slices) {
			slicesToCheck.add(slice);
			// collect missing dependencies from this slice
			
			// first check all currently registered dependencies in the current effective time slice and collect all missing components
			// then if there is at least one missing component from the current slice, search them in the SNOMED CT repository
			// report any that is missing in the current slice
			
			for (LongSet currentDependencyIds : slice.getDependenciesByComponent().values()) {
				final LongIterator it = currentDependencyIds.iterator();
				depCheck: while (it.hasNext()) {
					final String stringDependencyId = Long.toString(it.next());
					
					// if this component is available in any of the current or previous slices then all is well
					for (Rf2EffectiveTimeSlice sliceToCheck : slicesToCheck) {
						if (sliceToCheck.getContent().containsKey(stringDependencyId)) {
							continue depCheck;
						}
					}
					
					missingDependenciesInEffectiveTime.put(stringDependencyId, slice.getEffectiveTime());
				}
				
			}
		}
		
		if (!missingDependenciesInEffectiveTime.isEmpty()) {
			final Set<String> missingConceptIds = fetchConcepts(context, Sets.newHashSet(missingDependenciesInEffectiveTime.keySet()));
			// the difference between the sets are the ones which don't exist in any of the previous slices, or imported in the system
			if (!missingConceptIds.isEmpty()) {
				missingConceptIds.forEach(id -> reporter.error("%s %s in effective time %s", Rf2ValidationDefects.MISSING_DEPENDANT_ID, id, missingDependenciesInEffectiveTime.get(id)));
			}
		}
	}

	public void validateMembers(Iterable<Rf2EffectiveTimeSlice> orderedEffectiveTimeSlices, Rf2ValidationIssueReporter reporter, BranchContext context) {
//		final int slices = Iterables.size(orderedEffectiveTimeSlices);
//		for (int i = 0; i < slices; i++) {
//			final Rf2EffectiveTimeSlice slice = Iterables.get(orderedEffectiveTimeSlices, i);
//			
//		}
	}

	private Set<String> fetchConcepts(BranchContext context, final Set<String> conceptIdsToFetch) {
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

		while (conceptRequestIterator.hasNext()) {
			conceptRequestIterator.next().forEach(concept -> conceptIdsToFetch.remove(concept.getId()));
		}

		return conceptIdsToFetch;
	}
	
}
