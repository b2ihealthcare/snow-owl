/*
 * Copyright 2018-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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

import com.b2international.collections.longs.LongIterator;
import com.b2international.collections.longs.LongSet;
import com.b2international.index.query.Query;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.repository.RevisionDocument;
import com.b2international.snowowl.core.request.io.ImportDefectAcceptor;
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
	
	public void validateTerminologyComponents(
			final List<Rf2EffectiveTimeSlice> slices, 
			final ImportDefectAcceptor globalDefectAcceptor, 
			final BranchContext context) {
		
		// Keys are component IDs waiting to be resolved, values are the earliest effective time "mention" of the component
		final Map<String, String> dependenciesByEffectiveTime = Maps.newHashMap();
		
		// Check the slices in reverse order for missing dependencies
		for (Rf2EffectiveTimeSlice slice : Lists.reverse(slices)) {
			final String effectiveTimeLabel = Rf2EffectiveTimeSlice.SNAPSHOT_SLICE.equals(slice.getEffectiveTime()) 
					? "..."
					: String.format(" in effective time '%s'", slice.getEffectiveTime());
			
			log.info("Validating component consistency{}", effectiveTimeLabel);

			// Resolve pending dependencies with the current slice content
			final Set<String> contentInSlice = Set.copyOf(slice.getContent().keySet());
			dependenciesByEffectiveTime.keySet().removeAll(contentInSlice);
			
			// Core component dependencies
			for (final LongSet componentDependencies : slice.getDependenciesByComponent().values()) {
				final LongIterator it = componentDependencies.iterator();
				
				while (it.hasNext()) {
					final long dependencyId = it.next();
					final String stringDependencyId = Long.toString(dependencyId);

					// Insert or update any entry with the current slice key, unless the current slice has the component
					if (!contentInSlice.contains(stringDependencyId)) {
						dependenciesByEffectiveTime.put(stringDependencyId, slice.getEffectiveTime());
					}
				}
			}
			
			// Dependencies of reference set members includes the reference component ID, check them separately
			final LongIterator referencedComponentIds = slice.getMembersByReferencedComponent().keySet().iterator();
			while (referencedComponentIds.hasNext()) {
				final long referencedComponentId = referencedComponentIds.next();
				final String stringReferencedComponentId = Long.toString(referencedComponentId);
				
				if (!contentInSlice.contains(stringReferencedComponentId)) {
					dependenciesByEffectiveTime.put(stringReferencedComponentId, slice.getEffectiveTime());
				}
			}
		}

		// Anything that remains is not resolved by the imported data; check if it is in Snow Owl
		if (!dependenciesByEffectiveTime.isEmpty()) {
			
			Set<String> conceptIds = newHashSet();
			Set<String> descriptionIds = newHashSet();
			Set<String> relationshipIds = newHashSet();
			
			dependenciesByEffectiveTime.keySet().stream()
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
					reportMissingComponent(globalDefectAcceptor, id, dependenciesByEffectiveTime.get(id), "concept");
				});
			}
			
			log.trace("Fetch existing description IDs...");
			
			final Set<String> missingDescriptionIds = fetchComponentIds(context, descriptionIds, SnomedDescriptionIndexEntry.class);

			if (!missingDescriptionIds.isEmpty()) {
				missingDescriptionIds.forEach(id -> {
					reportMissingComponent(globalDefectAcceptor, id, dependenciesByEffectiveTime.get(id), "description");
				});
			}
			
			log.trace("Fetch existing relationship IDs...");

			final Set<String> missingRelationshipIds = fetchComponentIds(context, relationshipIds, SnomedRelationshipIndexEntry.class);

			if (!missingRelationshipIds.isEmpty()) {
				missingRelationshipIds.forEach(id -> {
					reportMissingComponent(globalDefectAcceptor, id, dependenciesByEffectiveTime.get(id), "relationship");
				});
			}
			
		}
	}

	private void reportMissingComponent(ImportDefectAcceptor globalDefectAcceptor, String id, String effectiveTime, String componentTypeLabel) {
		String effectiveTimeLabel = Rf2EffectiveTimeSlice.SNAPSHOT_SLICE.equals(effectiveTime) ? "" : String.format(" in effective time '%s'", effectiveTime);
		globalDefectAcceptor.error(String.format("%s %s with id '%s'%s", Rf2ValidationDefects.MISSING_DEPENDANT_ID, componentTypeLabel, id, effectiveTimeLabel));
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
