/*
 * Copyright 2011-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.index.change;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import com.b2international.commons.collect.LongSets;
import com.b2international.index.revision.ObjectId;
import com.b2international.index.revision.Revision;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.index.revision.StagingArea;
import com.b2international.index.revision.StagingArea.RevisionDiff;
import com.b2international.snowowl.core.repository.ChangeSetProcessorBase;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.domain.Acceptability;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument.Builder;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionFragment;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.refset.RefSetMemberChange;
import com.b2international.snowowl.snomed.datastore.index.update.IconIdUpdater;
import com.b2international.snowowl.snomed.datastore.index.update.ParentageUpdater;
import com.b2international.snowowl.snomed.datastore.index.update.ReferenceSetMembershipUpdater;
import com.b2international.snowowl.snomed.datastore.taxonomy.Taxonomy;
import com.b2international.snowowl.snomed.datastore.taxonomy.TaxonomyGraph;
import com.google.common.collect.*;

/**
 * @since 4.3
 */
public final class ConceptChangeProcessor extends ChangeSetProcessorBase {

	/*
	 * XXX: Case significance and module changes are also allowed, but they are not relevant from 
	 * the concept change processor's point of view.
	 */
	private static final Set<String> ALLOWED_DESCRIPTION_CHANGE_FEATURES = Set.of(
		SnomedDescriptionIndexEntry.Fields.ACTIVE,
		SnomedDescriptionIndexEntry.Fields.TERM,
		SnomedDescriptionIndexEntry.Fields.TYPE_ID
	);
	private static final Set<String> ALLOWED_LANG_MEMBER_CHANGE_FEATURES = Set.of(
		SnomedRefSetMemberIndexEntry.Fields.ACTIVE,
		SnomedRefSetMemberIndexEntry.Fields.ACCEPTABILITY_ID
	);
	
	private static final Ordering<SnomedDescriptionFragment> DESCRIPTION_TYPE_ORDER = Ordering.natural()
			.onResultOf((SnomedDescriptionFragment description) -> {
				if (Concepts.FULLY_SPECIFIED_NAME.equals(description.getTypeId())) {
					if (description.getLanguageRefSetIds().contains(Concepts.REFSET_LANGUAGE_TYPE_US)) {
						return 0;
					} else if (description.getLanguageRefSetIds().contains(Concepts.REFSET_LANGUAGE_TYPE_UK)) {
						return 1;
					}
					return 2;
				} else if (Concepts.SYNONYM.equals(description.getTypeId())) {
					return 3;
				} else {
					return description.getTypeId();
				}
			});
	
	private static final Comparator<SnomedDescriptionFragment> DESCRIPTION_FRAGMENT_ORDER = DESCRIPTION_TYPE_ORDER.thenComparing(
			Comparator.comparingInt(d -> d.getId().length())
		).thenComparing(
			Comparator.comparing(SnomedDescriptionFragment::getId)
		);
	
	private final DoiData doiData;
	private final IconIdUpdater iconId;
	private final ParentageUpdater inferred;
	private final ParentageUpdater stated;
	private final Taxonomy statedTaxonomy;
	private final Taxonomy inferredTaxonomy;
	private final ReferringMemberChangeProcessor memberChangeProcessor;
	
	private Multimap<String, RefSetMemberChange> referringRefSets;
	
	public ConceptChangeProcessor(DoiData doiData, Collection<String> availableImages, Taxonomy statedTaxonomy, Taxonomy inferredTaxonomy) {
		super("concept changes");
		this.doiData = doiData;
		this.iconId = new IconIdUpdater(inferredTaxonomy.getNewTaxonomy(), statedTaxonomy.getNewTaxonomy(), availableImages);
		this.inferred = new ParentageUpdater(inferredTaxonomy.getNewTaxonomy(), false);
		this.stated = new ParentageUpdater(statedTaxonomy.getNewTaxonomy(), true);
		this.statedTaxonomy = statedTaxonomy;
		this.inferredTaxonomy = inferredTaxonomy;
		this.memberChangeProcessor = new ReferringMemberChangeProcessor(SnomedConcept.TYPE);
	}
	
	@Override
	public void process(StagingArea staging, RevisionSearcher searcher) throws IOException {
		// collect member changes
		this.referringRefSets = HashMultimap.create(memberChangeProcessor.process(staging, searcher));

		processNewConcepts(staging);
		
		// collect dirty concepts that require additional properties to be set for index
		final Map<String, RevisionDiff> dirtyConceptDiffsById = Maps.uniqueIndex(staging.getChangedRevisions(SnomedConceptDocument.class).iterator(), diff -> diff.newRevision.getId());
		
		final Set<String> dirtyConceptIds = collectDirtyConceptIds(staging);
		
		// remaining new/dirty/detached descriptions should be properly processed for preferredDescriptions field
		final Map<String, SnomedDescriptionIndexEntry> affectedDescriptionsById = getDescriptionDocuments(staging, searcher);
		final Multimap<String, SnomedDescriptionIndexEntry> affectedDescriptionsByConcept = Multimaps.index(affectedDescriptionsById.values(), SnomedDescriptionIndexEntry::getConceptId);
		dirtyConceptIds.addAll(affectedDescriptionsByConcept.keySet());
		
		// remove all new/detached concept IDs, we've already processed them
		staging.getRemovedObjects(SnomedConceptDocument.class).map(SnomedConceptDocument::getId).forEach(dirtyConceptIds::remove);
		staging.getNewObjects(SnomedConceptDocument.class).map(SnomedConceptDocument::getId).forEach(dirtyConceptIds::remove);
		
		if (!dirtyConceptIds.isEmpty()) {
			final Map<ObjectId, RevisionDiff> changedRevisions = staging.getChangedRevisions();
			// fetch all dirty concept documents by their ID
			final Set<String> missingCurrentConceptIds = dirtyConceptIds.stream()
					.filter(id -> !changedRevisions.containsKey(ObjectId.of(SnomedConcept.TYPE, id)))
					.collect(Collectors.toSet());

			final Map<String, SnomedConceptDocument> currentConceptDocumentsById = newHashMap(Maps.uniqueIndex(searcher.get(SnomedConceptDocument.class, missingCurrentConceptIds), Revision::getId));
			dirtyConceptIds.stream()
				.map(id -> ObjectId.of(SnomedConcept.TYPE, id))
				.filter(changedRevisions::containsKey)
				.map(changedRevisions::get)
				.map(diff -> (SnomedConceptDocument) diff.oldRevision)
				.forEach(doc -> currentConceptDocumentsById.put(doc.getId(), doc));
			
			// update dirty concepts
			for (final String id : dirtyConceptIds) {
				final SnomedConceptDocument concept = dirtyConceptDiffsById.containsKey(id) ? (SnomedConceptDocument) dirtyConceptDiffsById.get(id).newRevision : null;
				final SnomedConceptDocument currentDoc = currentConceptDocumentsById.get(id);
				if (currentDoc == null) {
					throw new IllegalStateException("Current concept revision should not be null for: " + id);
				}
				
				final Builder doc = SnomedConceptDocument.builder(currentDoc);
				
				final Collection<SnomedDescriptionIndexEntry> affectedDescriptions = affectedDescriptionsByConcept.get(id);
				if (!affectedDescriptions.isEmpty()) {
					final Map<String, SnomedDescriptionFragment> updatedPreferredDescriptions = newHashMap(Maps.uniqueIndex(currentDoc.getPreferredDescriptions(), SnomedDescriptionFragment::getId));
					
					// add new/dirty fragments if they are preferred and active terms
					for (SnomedDescriptionIndexEntry affectedDescription : affectedDescriptions) {
						if (staging.isNew(affectedDescription) || staging.isChanged(affectedDescription)) {
							updatedPreferredDescriptions.remove(affectedDescription.getId());
							if (affectedDescription.isActive() && !getPreferredLanguageMembers(affectedDescription).isEmpty()) {
								updatedPreferredDescriptions.put(affectedDescription.getId(), toDescriptionFragment(affectedDescription));
							}
						}
					}
					
					// remove deleted descriptions
					for (SnomedDescriptionIndexEntry affectedDescription : affectedDescriptions) {
						if (staging.isRemoved(affectedDescription)) {
							updatedPreferredDescriptions.remove(affectedDescription.getId());
						}
					}
					
					final List<SnomedDescriptionFragment> preferredDescriptions = updatedPreferredDescriptions.values()
							.stream()
							.sorted(DESCRIPTION_FRAGMENT_ORDER)
							.collect(Collectors.toList());
					
					update(doc, preferredDescriptions, concept, currentDoc);
				} else {
					update(doc, currentDoc.getPreferredDescriptions(), concept, currentDoc);
				}

				stageChange(currentDoc, doc.build());
			}
		}
	}

	private void processNewConcepts(StagingArea staging) {
		final Multimap<String, SnomedDescriptionFragment> newDescriptionFragmentsByConcept = HashMultimap.create();
		
		// changed descriptions are coming from potential merges/rebases
		Streams.concat(staging.getNewObjects(SnomedDescriptionIndexEntry.class), staging.getChangedObjects(SnomedDescriptionIndexEntry.class))
			.filter(SnomedDescriptionIndexEntry::isActive)
			.filter(description -> !Concepts.TEXT_DEFINITION.equals(description.getTypeId()))
			.filter(description -> !getPreferredLanguageMembers(description).isEmpty())
			.forEach(description -> newDescriptionFragmentsByConcept.put(description.getConceptId(), toDescriptionFragment(description)));
		
		// index new concepts
		staging.getNewObjects(SnomedConceptDocument.class).forEach(concept -> {
			final String id = concept.getId();
			final Builder doc = SnomedConceptDocument.builder().id(id);
			
			// in case of a new concept, all of its descriptions should be part of the staging area as well
			final List<SnomedDescriptionFragment> preferredDescriptions = newDescriptionFragmentsByConcept.removeAll(id)
					.stream()
					.sorted(DESCRIPTION_FRAGMENT_ORDER)
					.collect(Collectors.toList());
			
			update(doc, preferredDescriptions, concept, null);
			stageNew(doc.build());
		});
	}
	
	private Map<String, SnomedDescriptionIndexEntry> getDescriptionDocuments(StagingArea staging, RevisionSearcher searcher) throws IOException {
		final Map<String, SnomedDescriptionIndexEntry> descriptions = newHashMap();
		
		// add all new descriptions from tx
		staging
			.getNewObjects(SnomedDescriptionIndexEntry.class)
			.filter(description -> !Concepts.TEXT_DEFINITION.equals(description.getTypeId()))
			.forEach(description -> descriptions.put(description.getId(), description));
		
		// add dirty descriptions with relevant changes from tx
		staging
			// XXX accepts only relevant property changes, lang member detection should take care about acceptability changes
			.getChangedRevisions(SnomedDescriptionIndexEntry.class) 
			.filter(diff -> !Concepts.TEXT_DEFINITION.equals(((SnomedDescriptionIndexEntry) diff.newRevision).getTypeId()))
			.filter(diff -> diff.hasRevisionPropertyChanges(ALLOWED_DESCRIPTION_CHANGE_FEATURES))
			.map(diff -> (SnomedDescriptionIndexEntry) diff.newRevision)
			.forEach(description -> descriptions.put(description.getId(), description));
		
		// add detached descriptions
		staging.getRemovedObjects(SnomedDescriptionIndexEntry.class)
			.filter(description -> !Concepts.TEXT_DEFINITION.equals(description.getTypeId()))
			.forEach(description -> descriptions.put(description.getId(), description));
	
		// gather descriptions for each new, dirty, detached lang. members
		final Set<String> descriptionsToLoad = newHashSet();
		
		staging.getNewObjects(SnomedRefSetMemberIndexEntry.class)
			.map(member -> member.getReferencedComponentId())
			.forEach(descriptionsToLoad::add);
		
		staging.getChangedRevisions(SnomedRefSetMemberIndexEntry.class)
			.filter(diff -> diff.hasRevisionPropertyChanges(ALLOWED_LANG_MEMBER_CHANGE_FEATURES))
			.map(diff -> ((SnomedRefSetMemberIndexEntry) diff.newRevision).getReferencedComponentId())
			.forEach(descriptionsToLoad::add);
		
		staging.getRemovedObjects(SnomedRefSetMemberIndexEntry.class)
			.map(member -> member.getReferencedComponentId())
			.forEach(descriptionsToLoad::add);
		
		descriptionsToLoad.removeAll(descriptions.keySet());
		
		// add all descriptions that are present in the tx because of acceptabilityId prop change
		staging.getChangedRevisions(SnomedDescriptionIndexEntry.class) 
			.map(diff -> (SnomedDescriptionIndexEntry) diff.newRevision)
			.filter(description -> descriptionsToLoad.remove(description.getId())) // XXX removes already dirty description from the toLoad set
			.filter(description -> !Concepts.TEXT_DEFINITION.equals(description.getTypeId()))
			.forEach(description -> descriptions.put(description.getId(), description));
		
		if (!descriptionsToLoad.isEmpty()) {
			searcher.get(SnomedDescriptionIndexEntry.class, descriptionsToLoad)
				.forEach(description -> {
					if (!Concepts.TEXT_DEFINITION.equals(description.getTypeId())) {
						descriptions.put(description.getId(), description);
					}
				});
		}
		
		return descriptions;
	}

	/*
	 * Updates already existing concept document with changes from concept and the current revision.
	 * New concepts does not have currentRevision and dirty concepts may not have a loaded Concept CDOObject, 
	 * therefore both can be null, but not at the same time.
	 * In case of new objects the Concept object should not be null, in case of dirty, the currentVersion should not be null, 
	 * but there can be a dirty concept if a property changed on it.
	 * We will use whatever we actually have locally to compute the new revision.
	 */
	private void update(SnomedConceptDocument.Builder doc, 
			List<SnomedDescriptionFragment> preferredDescriptions, 
			@Nullable SnomedConceptDocument newOrDirtyRevision, 
			SnomedConceptDocument cleanRevision) {
		
		checkArgument(newOrDirtyRevision != null || cleanRevision != null, "Either the newOrDirtyRevision is null or the cleanRevision but not both");

		final String id = newOrDirtyRevision != null ? newOrDirtyRevision.getId() : cleanRevision.getId();
		final long idLong = Long.parseLong(id);
		final boolean active = newOrDirtyRevision != null ? newOrDirtyRevision.isActive() : cleanRevision.isActive();
		
		if (newOrDirtyRevision != null) {
			doc.active(active)
					.released(newOrDirtyRevision.isReleased())
					.effectiveTime(newOrDirtyRevision.getEffectiveTime())
					.moduleId(newOrDirtyRevision.getModuleId())
					.exhaustive(newOrDirtyRevision.isExhaustive())
					.definitionStatusId(newOrDirtyRevision.getDefinitionStatusId())
					.refSetType(newOrDirtyRevision.getRefSetType())
					.referencedComponentType(newOrDirtyRevision.getReferencedComponentType())
					.mapTargetComponentType(newOrDirtyRevision.getMapTargetComponentType())
					.doi(doiData.getDoiScore(idLong));
		} else {
			doc.active(active)
					.released(cleanRevision.isReleased())
					.effectiveTime(cleanRevision.getEffectiveTime())
					.moduleId(cleanRevision.getModuleId())
					.exhaustive(cleanRevision.isExhaustive())
					.definitionStatusId(cleanRevision.getDefinitionStatusId())
					.refSetType(cleanRevision.getRefSetType())
					.referencedComponentType(cleanRevision.getReferencedComponentType())
					.mapTargetComponentType(cleanRevision.getMapTargetComponentType())
					.doi(doiData.getDoiScore(idLong));
		}

		/*
		 * Extract semantic tags from active FSNs received in preferredDescriptions (these are expected to be preferred in at 
		 * least one language reference set).
		 */
		final SortedSet<String> semanticTags = preferredDescriptions.stream()
			.filter(f -> Concepts.FULLY_SPECIFIED_NAME.equals(f.getTypeId()))
			.map(f -> SnomedDescriptionIndexEntry.extractSemanticTag(f.getTerm()))
			.collect(Collectors.toCollection(TreeSet::new));

		final boolean inStated = statedTaxonomy.getNewTaxonomy().containsNode(idLong);
		final boolean inInferred = inferredTaxonomy.getNewTaxonomy().containsNode(idLong);
		
		if (inStated || inInferred) {
			iconId.update(id, Iterables.getFirst(semanticTags, ""), active, doc);
		}
	
		if (inStated) {
			stated.update(id, doc);
		}
	
		if (inInferred) {
			inferred.update(id, doc);
		}
		
		final Collection<String> currentMemberOf = cleanRevision == null ? Collections.emptySet() : cleanRevision.getMemberOf();
		final Collection<String> currentActiveMemberOf = cleanRevision == null ? Collections.emptySet() : cleanRevision.getActiveMemberOf();
		new ReferenceSetMembershipUpdater(referringRefSets.removeAll(id), currentMemberOf, currentActiveMemberOf)
				.update(doc);
		
		doc.semanticTags(semanticTags);
		doc.preferredDescriptions(preferredDescriptions);
	}

	private Set<String> getPreferredLanguageMembers(SnomedDescriptionIndexEntry description) {
		return description.getAcceptabilityMap().entrySet()
			.stream()
			.filter(entry -> Acceptability.PREFERRED == entry.getValue())
			.map(entry -> entry.getKey())
			.collect(Collectors.toSet());
	}
	
	private SnomedDescriptionFragment toDescriptionFragment(SnomedDescriptionIndexEntry description) {
		return new SnomedDescriptionFragment(
			description.getId(), 
			description.getTypeId(), 
			description.getTerm(), 
			List.copyOf(getPreferredLanguageMembers(description))
		);
	}

	private Set<String> collectDirtyConceptIds(final StagingArea staging) throws IOException {
		final Set<String> dirtyConceptIds = newHashSet();
		
		// collect dirty concepts due to change in memberships
		dirtyConceptIds.addAll(referringRefSets.keySet());
		
		// collect inferred taxonomy changes
		dirtyConceptIds.addAll(registerConceptAndDescendants(inferredTaxonomy.getNewEdges(), inferredTaxonomy.getNewTaxonomy()));
		dirtyConceptIds.addAll(registerConceptAndDescendants(inferredTaxonomy.getChangedEdges(), inferredTaxonomy.getNewTaxonomy()));
		dirtyConceptIds.addAll(registerConceptAndDescendants(inferredTaxonomy.getDetachedEdges(), inferredTaxonomy.getOldTaxonomy()));
		// collect stated taxonomy changes
		dirtyConceptIds.addAll(registerConceptAndDescendants(statedTaxonomy.getNewEdges(), statedTaxonomy.getNewTaxonomy()));
		dirtyConceptIds.addAll(registerConceptAndDescendants(statedTaxonomy.getChangedEdges(), statedTaxonomy.getNewTaxonomy()));
		dirtyConceptIds.addAll(registerConceptAndDescendants(statedTaxonomy.getDetachedEdges(), statedTaxonomy.getOldTaxonomy()));

		return dirtyConceptIds;
	}
	
	private Set<String> registerConceptAndDescendants(Set<String> edgeIds, TaxonomyGraph taxonomy) {
		final Set<String> ids = newHashSet();
		for (String edgeId : edgeIds) {
			long conceptId = taxonomy.getSourceNodeId(edgeId);
			ids.add(Long.toString(conceptId));
			ids.addAll(LongSets.toStringSet(taxonomy.getAllDescendantNodeIds(conceptId)));
		}
		return ids;
	}

}
