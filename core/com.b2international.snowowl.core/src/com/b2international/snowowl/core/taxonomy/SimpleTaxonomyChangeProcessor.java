/*
 * Copyright 2022 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.taxonomy;

import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.index.revision.StagingArea;
import com.b2international.snowowl.core.domain.IComponent;
import com.b2international.snowowl.core.repository.ChangeSetProcessorBase;
import com.b2international.snowowl.core.repository.RevisionDocument;
import com.google.common.collect.Sets;

/**
 * @since 8.0.1
 */
public abstract class SimpleTaxonomyChangeProcessor<D extends RevisionDocument> extends ChangeSetProcessorBase {
	
	private final Class<D> documentClass;
	private final String parentIdsProperty;

	public SimpleTaxonomyChangeProcessor(final String description, final Class<D> documentClass, final String parentIdsProperty) {
		super(description);
		this.documentClass = documentClass;
		this.parentIdsProperty = parentIdsProperty;
	}
	
	@Override
	public void process(final StagingArea staging, final RevisionSearcher searcher) throws IOException {
		final Set<String> conceptsAndAncestorsToLoad = newHashSet();
		final Set<String> conceptsAndDescendantsToLoad = newHashSet();
				
		// We need to update all existing ancestors of new concepts
		final Map<String, D> newConceptsById = newHashMap();
		staging.getNewObjects(documentClass)
			.forEachOrdered(newConcept -> {
				newConceptsById.put(newConcept.getId(), newConcept);
				conceptsAndAncestorsToLoad.addAll(getParentIds(newConcept));	
			});

		// We need to update all concepts in the ancestor and descendant hierarchy of changed concepts
		final Map<String, D> changedConceptsById = newHashMap();
		staging.getChangedRevisions(documentClass, Set.of(parentIdsProperty))
			.forEachOrdered(change -> {
				// We could use the ID from either revision here
				final D oldRevision = documentClass.cast(change.oldRevision);
				final String conceptId = oldRevision.getId();

				// Add concept and ancestors from old revision (ancestor IDs set is reliable)
				conceptsAndAncestorsToLoad.add(conceptId);
				conceptsAndAncestorsToLoad.addAll(getParentIds(oldRevision));
				conceptsAndAncestorsToLoad.addAll(getIndirectAncestorIds(oldRevision));
				
				// Add parents from new revision (we can not rely on the ancestor IDs set here)
				final D newRevision = documentClass.cast(change.newRevision);
				final Set<String> newParentIds = getParentIds(newRevision);
				conceptsAndAncestorsToLoad.addAll(newParentIds);
				
				// Load descendants for concept as well
				conceptsAndDescendantsToLoad.add(conceptId);
				
				// Store the new revision in the map
				changedConceptsById.put(conceptId, newRevision);
			});
		
		// Same for detached concepts
		final Set<String> deletedConceptIds = newHashSet();
		staging.getRemovedObjects(documentClass)
			.forEachOrdered(removedConcept -> {
				final String conceptId = removedConcept.getId();
				deletedConceptIds.add(conceptId);
				conceptsAndAncestorsToLoad.add(conceptId);
				conceptsAndDescendantsToLoad.add(conceptId);
			});

		final Map<String, D> conceptsById = newHashMap();
		
		// First, fetch all ancestors that we could collect from the change set alone
		searcher.get(documentClass, conceptsAndAncestorsToLoad)
			.forEach(existingConcept -> {
				final String conceptId = existingConcept.getId();
				conceptsById.put(conceptId, existingConcept);
				
				// Extend the set with each concept's ancestors (parent and ancestor IDs set is reliable) 
				conceptsAndAncestorsToLoad.remove(conceptId);
				conceptsAndAncestorsToLoad.addAll(getParentIds(existingConcept));
				conceptsAndAncestorsToLoad.addAll(getIndirectAncestorIds(existingConcept));
			});

		// Load remaining ancestors
		conceptsAndAncestorsToLoad.removeAll(conceptsById.keySet());
		// make sure we remove the ROOT_ID from the search
		conceptsAndAncestorsToLoad.remove(IComponent.ROOT_ID);
		
		searcher.get(documentClass, conceptsAndAncestorsToLoad)
			.forEach(existingConcept -> {
				conceptsAndAncestorsToLoad.remove(existingConcept.getId());
				conceptsById.put(existingConcept.getId(), existingConcept);	
			});
		
		if (!conceptsAndAncestorsToLoad.isEmpty()) {
			throw new BadRequestException("Concept(s) are missing from index. '%s'", conceptsAndAncestorsToLoad);
		}
		
		// Load concepts and descendants from the index (these are also the documents which need to be updated)
		final Map<String, D> conceptAndDescendants = getConceptAndDescendants(conceptsAndDescendantsToLoad, searcher);
		conceptsAndDescendantsToLoad.clear();
		conceptsById.putAll(conceptAndDescendants);
		
		// Assume 1:1 ratio between nodes and edges
		final SimpleTaxonomyGraph taxonomy = new SimpleTaxonomyGraph(conceptsById.size(), conceptsById.size());
		taxonomy.addNode(IComponent.ROOT_ID);
		
		// Existing state
		conceptsById.keySet()
			.forEach(taxonomy::addNode);
		conceptsById.values()
			.forEach(concept -> {
				var parents = getParentIdsWithExclusions(concept, deletedConceptIds);
				taxonomy.addEdge(concept.getId(), parents);
				// make sure we register all parents as nodes
				parents.forEach(taxonomy::addNode);
			});

		// Add new concepts
		newConceptsById.keySet()
			.forEach(taxonomy::addNode);
		newConceptsById.values()
			.forEach(concept -> taxonomy.addEdge(concept.getId(), getParentIdsWithExclusions(concept, deletedConceptIds)));

		// Update existing edges (re-register edge with the new set of parent IDs)
		changedConceptsById.values()
			.forEach(concept -> taxonomy.addEdge(concept.getId(), getParentIdsWithExclusions(concept, deletedConceptIds)));
		
		// Delete concepts and edges
		deletedConceptIds.forEach(conceptId -> {
			// this deletes only the edges that are outbound from the current conceptId
			taxonomy.removeEdge(conceptId);
			// XXX this does not automatically delete inbound edges, see getParentIdsWithExclusions for workaround
			taxonomy.removeNode(conceptId);
		});
		
		if (taxonomy.build()) {
			throw new IllegalStateException("Failed to build updated taxonomy.");
		}
		
		// Update new concepts, changed concepts (that were not deleted) and their descendants
		conceptAndDescendants.values()
			.forEach(concept -> updateTaxonomy(staging, taxonomy, concept, changedConceptsById));
		newConceptsById.values()
			.forEach(concept -> updateTaxonomy(staging, taxonomy, concept, changedConceptsById));
	}

	// this methods extract the current set of parentIds and removes any that is in the given set
	private Set<String> getParentIdsWithExclusions(D concept, Set<String> conceptsToRemove) {
		return Sets.difference(getParentIds(concept), conceptsToRemove);
	}

	// direct
	protected abstract Set<String> getParentIds(final D conceptDocument);
	
	// indirect
	protected abstract Set<String> getIndirectAncestorIds(final D conceptDocument);
	
	// direct and indirect
	protected abstract Map<String, D> getConceptAndDescendants(final Set<String> ancestorIds, final RevisionSearcher searcher) throws IOException;

	private void updateTaxonomy(final StagingArea staging, final SimpleTaxonomyGraph taxonomy, final D concept, Map<String, D> changedConceptsById) {
		if (staging.isRemoved(concept)) {
			return;
		}
		
		String id = concept.getId();
		D conceptToUpdate = changedConceptsById.getOrDefault(id, concept); 
		
		Set<String> parentIds = taxonomy.getParentIds(id);
		if (parentIds.isEmpty()) {
			parentIds = Set.of(IComponent.ROOT_ID);
		}
		
		Set<String> ancestorIds = taxonomy.getIndirectAncestorIds(id);
		// register -1 root as ancestor if it is not a direct parent
		if (ancestorIds.isEmpty() && !parentIds.contains(IComponent.ROOT_ID)) {
			ancestorIds = Set.of(IComponent.ROOT_ID);
		}
		
		// stageChange should handle new revisions seamlessly as well
		staging.stageChange(conceptToUpdate, updateTaxonomy(conceptToUpdate, parentIds, ancestorIds));
	}

	protected abstract D updateTaxonomy(final D concept, final Set<String> parentIds, final Set<String> ancestorIds);
}
