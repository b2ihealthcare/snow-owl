/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.b2international.collections.longs.LongCollection;
import com.b2international.collections.longs.LongIterator;
import com.b2international.commons.collect.LongSets;
import com.b2international.index.query.Query;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.index.revision.StagingArea;
import com.b2international.index.revision.StagingArea.RevisionDiff;
import com.b2international.snowowl.core.api.IComponent;
import com.b2international.snowowl.datastore.index.ChangeSetProcessorBase;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.domain.Acceptability;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument.Builder;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionFragment;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.refset.RefSetMemberChange;
import com.b2international.snowowl.snomed.datastore.index.update.IconIdUpdater;
import com.b2international.snowowl.snomed.datastore.index.update.ParentageUpdater;
import com.b2international.snowowl.snomed.datastore.index.update.ReferenceSetMembershipUpdater;
import com.b2international.snowowl.snomed.datastore.taxonomy.ISnomedTaxonomyBuilder;
import com.b2international.snowowl.snomed.datastore.taxonomy.Taxonomy;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;

/**
 * @since 4.3
 */
public final class ConceptChangeProcessor extends ChangeSetProcessorBase {

	private static final Set<String> ALLOWED_CONCEPT_CHANGE_FEATURES = ImmutableSet.<String>builder()
			.add(SnomedConceptDocument.Fields.ACTIVE)
			.add(SnomedConceptDocument.Fields.EFFECTIVE_TIME)
			.add(SnomedConceptDocument.Fields.RELEASED)
			.add(SnomedConceptDocument.Fields.MODULE_ID)
			.add(SnomedConceptDocument.Fields.PRIMITIVE)
			.add(SnomedConceptDocument.Fields.EXHAUSTIVE)
			.build();
	private static final Set<String> ALLOWED_DESCRIPTION_CHANGE_FEATURES = ImmutableSet.<String>builder()
			.add(SnomedDescriptionIndexEntry.Fields.ACTIVE)
			.add(SnomedDescriptionIndexEntry.Fields.TERM)
			.add(SnomedDescriptionIndexEntry.Fields.TYPE_ID)
			.build();
	private static final Set<String> ALLOWED_LANG_MEMBER_CHANGE_FEATURES = ImmutableSet.<String>builder()
			.add(SnomedRefSetMemberIndexEntry.Fields.ACTIVE)
			.add(SnomedRefSetMemberIndexEntry.Fields.ACCEPTABILITY_ID)
			.build();
	
	private static final Ordering<SnomedDescriptionFragment> DESCRIPTION_FRAGMENT_ORDER = Ordering.natural().onResultOf(SnomedDescriptionFragment::getId);
	
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
		this.memberChangeProcessor = new ReferringMemberChangeProcessor(SnomedTerminologyComponentConstants.CONCEPT_NUMBER);
	}
	
	@Override
	public void process(StagingArea staging, RevisionSearcher searcher) throws IOException {
		// collect member changes
		this.referringRefSets = HashMultimap.create(memberChangeProcessor.process(staging, searcher));

		// collect new and dirty reference sets
//		final Map<String, SnomedRefSet> newAndDirtyRefSetsById = newHashMap(FluentIterable.from(Iterables.concat(commitChangeSet.getNewComponents(), commitChangeSet.getDirtyComponents()))
//				.filter(SnomedRefSet.class)
//				.uniqueIndex(new Function<SnomedRefSet, String>() {
//					@Override
//					public String apply(SnomedRefSet input) {
//						return input.getIdentifierId();
//					}
//				}));
		// collect deleted reference sets
//		final Collection<Long> deletedRefSetStorageKeys = commitChangeSet.getDetachedComponentStorageKeys(SnomedRefSetPackage.Literals.SNOMED_REF_SET);
		
		// index new concepts
		staging.getNewObjects(SnomedConceptDocument.class).forEach(concept -> {
			final String id = concept.getId();
			final Builder doc = SnomedConceptDocument.builder().id(id);
			update(doc, concept, null);
			// in case of a new concept, all of its descriptions should be part of the staging area as well
			
			doc.preferredDescriptions(toDescriptionFragments(staging, id));
			indexNewRevision(doc.build());
		});
		
		// collect dirty concepts that require additional properties to be set for index
		final Map<String, RevisionDiff> dirtyConceptDiffsById = Maps.uniqueIndex(staging.getChangedRevisions(SnomedConceptDocument.class).collect(Collectors.toList()), diff -> diff.newRevision.getId());
		
		final Set<String> dirtyConceptIds = collectDirtyConceptIds(staging);
		
		final Multimap<String, Snomeddesc> dirtyDescriptionsByConcept = Multimaps.index(getDirtyDescriptions(commitChangeSet), d -> d.getConcept().getId());
		
		// remaining new and dirty reference sets should be connected to a non-new concept, so add them here
		dirtyConceptIds.addAll(dirtyDescriptionsByConcept.keySet());
		
		if (!dirtyConceptIds.isEmpty()) {
			// fetch all dirty concept documents by their ID
			final Query<SnomedConceptDocument> query = Query.select(SnomedConceptDocument.class)
					.where(SnomedConceptDocument.Expressions.ids(dirtyConceptIds))
					.limit(dirtyConceptIds.size())
					.build();
			final Map<String, SnomedConceptDocument> currentConceptDocumentsById = Maps.uniqueIndex(searcher.search(query), IComponent::getId);
			
			// update dirty concepts
			for (final String id : dirtyConceptIds) {
				final Concept concept = dirtyConceptsById.get(id);
				final SnomedConceptDocument currentDoc = currentConceptDocumentsById.get(id);
				if (currentDoc == null) {
					throw new IllegalStateException("Current concept revision should not be null for: " + id);
				}
				final Builder doc = SnomedConceptDocument.builder(currentDoc);
				update(doc, concept, currentDoc);
				
				if (concept != null) {
					doc.preferredDescriptions(toDescriptionFragments(concept));
				} else {
					Collection<Description> dirtyDescriptions = dirtyDescriptionsByConcept.get(id);
					if (!dirtyDescriptions.isEmpty()) {
						Map<String, SnomedDescriptionFragment> newDescriptions = newHashMap(Maps.uniqueIndex(currentDoc.getPreferredDescriptions(), SnomedDescriptionFragment::getId));
						for (Description dirtyDescription : dirtyDescriptions) {
							newDescriptions.remove(dirtyDescription.getId());
							if (dirtyDescription.isActive() && !getPreferredLanguageMembers(dirtyDescription).isEmpty()) {
								newDescriptions.put(dirtyDescription.getId(), toDescriptionFragment(dirtyDescription));
							}
						}
						doc.preferredDescriptions(newDescriptions.values().stream().sorted(DESCRIPTION_FRAGMENT_ORDER).collect(Collectors.toList()));
					} else {
						doc.preferredDescriptions(currentDoc.getPreferredDescriptions());
					}
				}
				
				if (concept != null) {
					doc.storageKey(CDOIDUtil.getLong(concept.cdoID()));
				} else {
					doc.storageKey(currentDoc.getStorageKey());
				}
				
				indexChangedRevision(currentDoc, doc.build());
			}
		}
	}
	
	private Iterable<Description> getDirtyDescriptions(ICDOCommitChangeSet commitChangeSet) {
		final Set<Description> dirtyDescriptions = newHashSet();
		// add dirty descriptions from transaction
		FluentIterable
			.from(commitChangeSet.getDirtyComponents(Description.class, ALLOWED_DESCRIPTION_CHANGE_FEATURES))
			.filter(desc -> !Concepts.TEXT_DEFINITION.equals(desc.getType().getId()))
			.copyInto(dirtyDescriptions);
		// register descriptions as dirty for each dirty lang. member
		FluentIterable
			.from(commitChangeSet.getDirtyComponents(SnomedLanguageRefSetMember.class, ALLOWED_LANG_MEMBER_CHANGE_FEATURES))
			.transform(SnomedLanguageRefSetMember::eContainer)
			.filter(Description.class)
			.filter(desc -> !Concepts.TEXT_DEFINITION.equals(desc.getType().getId()))
			.copyInto(dirtyDescriptions);
		
		return dirtyDescriptions;
	}

	/*
	 * Updates already existing concept document with changes from concept and the current revision.
	 * New concepts does not have currentRevision and dirty concepts may not have a loaded Concept CDOObject, 
	 * therefore both can be null, but not at the same time.
	 * In case of new objects the Concept object should not be null, in case of dirty, the currentVersion should not be null, 
	 * but there can be a dirty concept if a property changed on it.
	 * We will use whatever we actually have locally to compute the new revision.
	 */
	private void update(SnomedConceptDocument.Builder doc, SnomedConceptDocument concept, SnomedConceptDocument currentVersion) {
		final String id = concept != null ? concept.getId() : currentVersion.getId();
		final boolean active = concept != null ? concept.isActive() : currentVersion.isActive();
		
		doc.active(active)
			.released(concept != null ? concept.isReleased() : currentVersion.isReleased())
			.effectiveTime(concept != null ? concept.getEffectiveTime() : currentVersion.getEffectiveTime())
			.moduleId(concept != null ? concept.getModuleId() : currentVersion.getModuleId())
			.exhaustive(concept != null ? concept.isExhaustive() : currentVersion.isExhaustive())
			.primitive(concept != null ? concept.isPrimitive() : currentVersion.isPrimitive())
			.refSetType(concept != null ? concept.getRefSetType() : currentVersion.getRefSetType())
			.referencedComponentType(concept != null ? concept.getReferencedComponentType() : currentVersion.getReferencedComponentType())
			.mapTargetComponentType(concept != null ? concept.getMapTargetComponentType() : currentVersion.getMapTargetComponentType())
			.doi(doiData.getDoiScore(id));
		
		final boolean inStated = statedTaxonomy.getNewTaxonomy().containsNode(id);
		final boolean inInferred = inferredTaxonomy.getNewTaxonomy().containsNode(id);
		
		if (inStated || inInferred) {
			iconId.update(id, active, doc);
		}
	
		if (inStated) {
			stated.update(id, doc);
		}
	
		if (inInferred) {
			inferred.update(id, doc);
		}
		
		final Collection<String> currentMemberOf = currentVersion == null ? Collections.<String> emptySet()
				: currentVersion.getMemberOf();
		final Collection<String> currentActiveMemberOf = currentVersion == null ? Collections.<String> emptySet()
				: currentVersion.getActiveMemberOf();
		new ReferenceSetMembershipUpdater(referringRefSets.removeAll(id), currentMemberOf, currentActiveMemberOf)
				.update(doc);
	}

	private List<SnomedDescriptionFragment> toDescriptionFragments(StagingArea staging, String conceptId) {
		return staging.getNewObjects(SnomedDescriptionIndexEntry.class)
				.filter(SnomedDescriptionIndexEntry::isActive)
				.filter(description -> !Concepts.TEXT_DEFINITION.equals(description.getTypeId()))
				.filter(description -> !getPreferredLanguageMembers(description).isEmpty())
				.map(this::toDescriptionFragment)
				.sorted(DESCRIPTION_FRAGMENT_ORDER)
				.collect(Collectors.toList());
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
			ImmutableList.copyOf(getPreferredLanguageMembers(description))
		);
	}

	private Set<String> collectDirtyConceptIds(final StagingArea staging) throws IOException {
		final Set<String> dirtyConceptIds = newHashSet();
		
		// collect relevant concept changes
		FluentIterable
			.from(staging.getDirtyComponents(Concept.class, ALLOWED_CONCEPT_CHANGE_FEATURES))
			.transform(Concept::getId)
			.copyInto(dirtyConceptIds);

		// collect dirty concepts due to change in hierarchy
		dirtyConceptIds.addAll(referringRefSets.keySet());
		
		// collect inferred taxonomy changes
		dirtyConceptIds.addAll(registerConceptAndDescendants(inferredTaxonomy.getNewEdges(), inferredTaxonomy.getNewTaxonomy()));
		dirtyConceptIds.addAll(registerConceptAndDescendants(inferredTaxonomy.getChangedEdges(), inferredTaxonomy.getNewTaxonomy()));
		dirtyConceptIds.addAll(registerConceptAndDescendants(inferredTaxonomy.getDetachedEdges(), inferredTaxonomy.getOldTaxonomy()));
		// collect stated taxonomy changes
		dirtyConceptIds.addAll(registerConceptAndDescendants(statedTaxonomy.getNewEdges(), statedTaxonomy.getNewTaxonomy()));
		dirtyConceptIds.addAll(registerConceptAndDescendants(statedTaxonomy.getChangedEdges(), statedTaxonomy.getNewTaxonomy()));
		dirtyConceptIds.addAll(registerConceptAndDescendants(statedTaxonomy.getDetachedEdges(), statedTaxonomy.getOldTaxonomy()));

		// collect detached reference sets where the concept itself hasn't been detached
		Set<String> detachedRefSets = staging.getDetachedComponents(SnomedRefSetPackage.Literals.SNOMED_REF_SET, SnomedConceptDocument.class, SnomedConceptDocument.Expressions::refSetStorageKeys)
				.stream()
				.map(SnomedConceptDocument::getId)
				.collect(Collectors.toSet());
		Set<String> detachedConcepts = staging.getDetachedComponentIds(SnomedPackage.Literals.CONCEPT, SnomedConceptDocument.class);
		
		dirtyConceptIds.addAll(Sets.difference(detachedRefSets, detachedConcepts));
		
		// remove all new concept IDs
		dirtyConceptIds.removeAll(FluentIterable.from(staging.getNewComponents(Concept.class)).transform(Concept::getId).toSet());
		
		return dirtyConceptIds;
	}
	
	private Set<String> registerConceptAndDescendants(LongCollection relationshipIds, ISnomedTaxonomyBuilder taxonomy) {
		final Set<String> ids = newHashSet();
		final LongIterator relationshipIdIterator = relationshipIds.iterator();
		while (relationshipIdIterator.hasNext()) {
			String relationshipId = Long.toString(relationshipIdIterator.next());
			String conceptId = taxonomy.getSourceNodeId(relationshipId);
			ids.add(conceptId);
			ids.addAll(LongSets.toStringSet(taxonomy.getAllDescendantNodeIds(conceptId)));
		}
		return ids;
	}

}
