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

import static com.google.common.collect.Sets.newHashSet;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;

import com.b2international.collections.PrimitiveSets;
import com.b2international.collections.longs.LongIterator;
import com.b2international.collections.longs.LongSet;
import com.b2international.index.query.Query;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.datastore.index.RevisionDocument;
import com.b2international.snowowl.snomed.cis.SnomedIdentifiers;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedComponentDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;
import com.b2international.snowowl.snomed.datastore.request.rf2.importer.Rf2EffectiveTimeSlice;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Validates the Rf2 import files against the existing graph
 * 
 * @since 7.0
 */
public class Rf2GlobalValidator {

	private static final int RAW_QUERY_PAGE_SIZE = 500_000;
	private final Logger log;

	public Rf2GlobalValidator(Logger log) {
		this.log = log;
	}
	
	public void validateTerminologyComponents(Iterable<Rf2EffectiveTimeSlice> slices, Rf2ValidationIssueReporter reporter, BranchContext context) {
		
		List<Rf2EffectiveTimeSlice> slicesToCheck = Lists.newArrayListWithExpectedSize(Iterables.size(slices)); 
		
		final Map<String, String> missingDependenciesInEffectiveTime = Maps.newHashMap();
		final LongSet checkedDependencies = PrimitiveSets.newLongOpenHashSet();
		
		// check the slices first for missing dependencies
		for (Rf2EffectiveTimeSlice slice : slices) {
			
			String effectiveTimeLabel = Rf2EffectiveTimeSlice.SNAPSHOT_SLICE.equals(slice.getEffectiveTime()) ? "..."
					: String.format(" in effective time '%s'", slice.getEffectiveTime());
			
			log.info("Validating component consistency{}", effectiveTimeLabel);
			
			slicesToCheck.add(slice);
			
			// first check all currently registered dependencies in the current effective time slice and collect all missing components
			// then if there is at least one missing component from the current slice, search them in the SNOMED CT repository
			// report any that is missing in the current slice
			
			for (LongSet componentDependencies : slice.getDependenciesByComponent().values()) {
				
				final LongIterator it = componentDependencies.iterator();
				
				depCheck: while (it.hasNext()) {
					
					final long dependencyId = it.next();
					
					if (checkedDependencies.contains(dependencyId)) {
						continue depCheck;
					}
					
					final String stringDependencyId = Long.toString(dependencyId);
					
					// if this component is available in any of the current or previous slices then all is well
					for (Rf2EffectiveTimeSlice sliceToCheck : slicesToCheck) {
						if (sliceToCheck.getContent().containsKey(stringDependencyId)) {
							checkedDependencies.add(dependencyId);
							continue depCheck;
						}
					}
					
					missingDependenciesInEffectiveTime.put(stringDependencyId, slice.getEffectiveTime());
				}
				
			}
			
			// validate existence of referenced component IDs
			
			LongIterator referencedComponentIds = slice.getMembersByReferencedComponent().keySet().iterator();
			
			refCheck: while (referencedComponentIds.hasNext()) {
				
				long referencedComponentId = referencedComponentIds.next();
				
				if (checkedDependencies.contains(referencedComponentId)) {
					continue refCheck;
				}
				
				final String stringReferencedComponentId = Long.toString(referencedComponentId);
				
				// if this component is available in any of the current or previous slices then all is well
				for (Rf2EffectiveTimeSlice sliceToCheck : slicesToCheck) {
					if (sliceToCheck.getContent().containsKey(stringReferencedComponentId)) {
						checkedDependencies.add(referencedComponentId);
						continue refCheck;
					}
				}
				
				missingDependenciesInEffectiveTime.put(stringReferencedComponentId, slice.getEffectiveTime());
				
			}
			
		}
		
		if (!missingDependenciesInEffectiveTime.isEmpty()) {
			
			Set<String> conceptIds = newHashSet();
			Set<String> descriptionIds = newHashSet();
			Set<String> relationshipIds = newHashSet();
			
			missingDependenciesInEffectiveTime.keySet().stream()
				.forEach(id -> {
					switch (SnomedIdentifiers.getComponentCategory(id)) {
						case CONCEPT: conceptIds.add(id);
							break;
						case DESCRIPTION: descriptionIds.add(id);
							break;
						case RELATIONSHIP: relationshipIds.add(id);
							break;
						default: log.error("Unknown component type for identifier: {}", id);
							break;
					}
				});
			
			log.trace("Fetch existing concept IDs...");
			
			final Set<String> missingConceptIds = fetchComponentIds(context, conceptIds, SnomedConceptDocument.class);
			
			if (!missingConceptIds.isEmpty()) {
				missingConceptIds.forEach(id -> {
					reportMissingComponent(reporter, id, missingDependenciesInEffectiveTime.get(id), "concept");
				});
			}
			
			log.trace("Fetch existing description IDs...");
			
			final Set<String> missingDescriptionIds = fetchComponentIds(context, descriptionIds, SnomedDescriptionIndexEntry.class);

			if (!missingDescriptionIds.isEmpty()) {
				missingDescriptionIds.forEach(id -> {
					reportMissingComponent(reporter, id, missingDependenciesInEffectiveTime.get(id), "description");
				});
			}
			
			log.trace("Fetch existing relationship IDs...");

			final Set<String> missingRelationshipIds = fetchComponentIds(context, relationshipIds, SnomedRelationshipIndexEntry.class);

			if (!missingRelationshipIds.isEmpty()) {
				missingRelationshipIds.forEach(id -> {
					reportMissingComponent(reporter, id, missingDependenciesInEffectiveTime.get(id), "relationship");
				});
			}
			
		}
	}

	private void reportMissingComponent(Rf2ValidationIssueReporter reporter, String id, String effectiveTime, String componentTypeLabel) {
		String effectiveTimeLabel = Rf2EffectiveTimeSlice.SNAPSHOT_SLICE.equals(effectiveTime) ? "" : String.format(" in effective time '%s'", effectiveTime);
		reporter.error("%s %s with id '%s'%s", Rf2ValidationDefects.MISSING_DEPENDANT_ID, componentTypeLabel, id, effectiveTimeLabel);
	}

	private <T extends SnomedComponentDocument> Set<String> fetchComponentIds(BranchContext context, final Set<String> componentIdsToFetch, Class<T> clazz) {
		
		if (!componentIdsToFetch.isEmpty()) {
			
			Set<String> existingComponentIds = newHashSet();
			
			for (List<String> ids : Iterables.partition(componentIdsToFetch, RAW_QUERY_PAGE_SIZE)) {
				
				try {
					
					Query<String> query = Query.select(String.class)
							.from(clazz)
							.fields(RevisionDocument.Fields.ID)
							.where(RevisionDocument.Expressions.ids(ids))
							.limit(ids.size())
							.build();
					
					existingComponentIds.addAll(context.service(RevisionSearcher.class).search(query).getHits());
					
				} catch (IOException e) {
					throw new SnowowlRuntimeException(e);
				}
				
			}
			
			componentIdsToFetch.removeAll(existingComponentIds);
			
		}
		
		return componentIdsToFetch;
	}
	
}
