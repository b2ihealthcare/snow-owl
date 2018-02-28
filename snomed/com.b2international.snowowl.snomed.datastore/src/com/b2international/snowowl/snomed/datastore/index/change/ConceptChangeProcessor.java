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

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.ecore.EStructuralFeature;

import com.b2international.collections.longs.LongCollection;
import com.b2international.collections.longs.LongIterator;
import com.b2international.commons.collect.LongSets;
import com.b2international.index.Hits;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Query;
import com.b2international.index.revision.Revision;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.core.api.ComponentUtils;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.datastore.ICDOCommitChangeSet;
import com.b2international.snowowl.datastore.cdo.CDOIDUtils;
import com.b2international.snowowl.datastore.index.ChangeSetProcessorBase;
import com.b2international.snowowl.datastore.index.RevisionDocument;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.Description;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.SnomedPackage;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.domain.Acceptability;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument.Builder;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionFragment;
import com.b2international.snowowl.snomed.datastore.index.refset.RefSetMemberChange;
import com.b2international.snowowl.snomed.datastore.index.update.IconIdUpdater;
import com.b2international.snowowl.snomed.datastore.index.update.ParentageUpdater;
import com.b2international.snowowl.snomed.datastore.index.update.ReferenceSetMembershipUpdater;
import com.b2international.snowowl.snomed.datastore.taxonomy.ISnomedTaxonomyBuilder;
import com.b2international.snowowl.snomed.datastore.taxonomy.Taxonomy;
import com.b2international.snowowl.snomed.snomedrefset.SnomedLanguageRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Ordering;

/**
 * @since 4.3
 */
public final class ConceptChangeProcessor extends ChangeSetProcessorBase {

	private static final Set<EStructuralFeature> ALLOWED_CONCEPT_CHANGE_FEATURES = ImmutableSet.<EStructuralFeature>builder()
			.add(SnomedPackage.Literals.COMPONENT__ACTIVE)
			.add(SnomedPackage.Literals.COMPONENT__EFFECTIVE_TIME)
			.add(SnomedPackage.Literals.COMPONENT__RELEASED)
			.add(SnomedPackage.Literals.COMPONENT__MODULE)
			.add(SnomedPackage.Literals.CONCEPT__DEFINITION_STATUS)
			.add(SnomedPackage.Literals.CONCEPT__EXHAUSTIVE)
			.add(SnomedPackage.Literals.CONCEPT__DESCRIPTIONS)
			.build();
	private static final Set<EStructuralFeature> ALLOWED_DESCRIPTION_CHANGE_FEATURES = ImmutableSet.<EStructuralFeature>builder()
			.add(SnomedPackage.Literals.COMPONENT__ACTIVE)
			.add(SnomedPackage.Literals.DESCRIPTION__TERM)
			.add(SnomedPackage.Literals.DESCRIPTION__TYPE)
			.add(SnomedPackage.Literals.DESCRIPTION__LANGUAGE_REF_SET_MEMBERS)
			.build();
	private static final Set<EStructuralFeature> ALLOWED_LANG_MEMBER_CHANGE_FEATURES = ImmutableSet.<EStructuralFeature>builder()
			.add(SnomedRefSetPackage.Literals.SNOMED_REF_SET_MEMBER__ACTIVE)
			.add(SnomedRefSetPackage.Literals.SNOMED_LANGUAGE_REF_SET_MEMBER__ACCEPTABILITY_ID)
			.build();
	
	private static final Ordering<SnomedDescriptionFragment> DESCRIPTION_FRAGMENT_ORDER = Ordering.natural().onResultOf(SnomedDescriptionFragment::getStorageKey);
	
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
	public void process(ICDOCommitChangeSet commitChangeSet, RevisionSearcher searcher) throws IOException {
		// process concept deletions first
		deleteRevisions(SnomedConceptDocument.class, commitChangeSet.getDetachedComponents(SnomedPackage.Literals.CONCEPT));
		// collect member changes
		this.referringRefSets = HashMultimap.create(memberChangeProcessor.process(commitChangeSet, searcher));

		// collect new and dirty reference sets
		final Map<String, SnomedRefSet> newAndDirtyRefSetsById = newHashMap(FluentIterable.from(Iterables.concat(commitChangeSet.getNewComponents(), commitChangeSet.getDirtyComponents()))
				.filter(SnomedRefSet.class)
				.uniqueIndex(new Function<SnomedRefSet, String>() {
					@Override
					public String apply(SnomedRefSet input) {
						return input.getIdentifierId();
					}
				}));
		// collect deleted reference sets
		final Set<Long> deletedRefSets = newHashSet(CDOIDUtils.createCdoIdToLong(commitChangeSet.getDetachedComponents(SnomedRefSetPackage.Literals.SNOMED_REF_SET)));
		
		// index new concepts
		for (final Concept concept : commitChangeSet.getNewComponents(Concept.class)) {
			final String id = concept.getId();
			final Builder doc = SnomedConceptDocument.builder().id(id);
			update(doc, concept, null);
			SnomedRefSet refSet = newAndDirtyRefSetsById.remove(id);
			if (refSet != null) {
				doc.refSet(refSet);
			}
			doc.preferredDescriptions(toDescriptionFragments(concept));
			indexNewRevision(concept.cdoID(), doc.build());
		}
		
		// collect dirty concepts for reindex
		final Map<String, Concept> dirtyConceptsById = Maps.uniqueIndex(commitChangeSet.getDirtyComponents(Concept.class), Concept::getId);
		
		final Set<String> dirtyConceptIds = collectDirtyConceptIds(searcher, commitChangeSet);
		
		final Multimap<String, Description> dirtyDescriptionsByConcept = Multimaps.index(getDirtyDescriptions(commitChangeSet), d -> d.getConcept().getId());
		
		// remaining new and dirty reference sets should be connected to a non-new concept, so add them here
		dirtyConceptIds.addAll(newAndDirtyRefSetsById.keySet());
		dirtyConceptIds.addAll(dirtyDescriptionsByConcept.keySet());
		
		if (!dirtyConceptIds.isEmpty()) {
			// fetch all dirty concept documents by their ID
			final Query<SnomedConceptDocument> query = Query.select(SnomedConceptDocument.class)
					.where(SnomedConceptDocument.Expressions.ids(dirtyConceptIds))
					.limit(dirtyConceptIds.size())
					.build();
			final Map<String, SnomedConceptDocument> currentConceptDocumentsById = Maps.uniqueIndex(searcher.search(query), ComponentUtils.<String>getIdFunction());
			
			// update dirty concepts
			for (final String id : dirtyConceptIds) {
				final Concept concept = dirtyConceptsById.get(id);
				final SnomedConceptDocument currentDoc = currentConceptDocumentsById.get(id);
				if (currentDoc == null) {
					throw new IllegalStateException("Current concept revision should not be null for: " + id);
				}
				final Builder doc = SnomedConceptDocument.builder(currentDoc);
				update(doc, concept, currentDoc);
				SnomedRefSet refSet = newAndDirtyRefSetsById.remove(id);
				if (refSet != null) {
					doc.refSet(refSet);
				}
				// clear refset props when deleting refset
				if (deletedRefSets.contains(currentDoc.getRefSetStorageKey())) {
					doc.clearRefSet();
				}
				
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
					indexChangedRevision(concept.cdoID(), doc.build());				
				} else {
					indexChangedRevision(currentDoc.getStorageKey(), doc.build());
				}
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
	private void update(SnomedConceptDocument.Builder doc, Concept concept, SnomedConceptDocument currentVersion) {
		final String id = concept != null ? concept.getId() : currentVersion.getId();
		final boolean active = concept != null ? concept.isActive() : currentVersion.isActive();
		
		doc.active(active)
			.released(concept != null ? concept.isReleased() : currentVersion.isReleased())
			.effectiveTime(concept != null ? getEffectiveTime(concept) : currentVersion.getEffectiveTime())
			.moduleId(concept != null ? concept.getModule().getId() : currentVersion.getModuleId())
			.exhaustive(concept != null ? concept.isExhaustive() : currentVersion.isExhaustive())
			.primitive(concept != null ? concept.isPrimitive() : currentVersion.isPrimitive())
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

	private List<SnomedDescriptionFragment> toDescriptionFragments(Concept concept) {
		return concept.getDescriptions()
				.stream()
				.filter(Description::isActive)
				.filter(description -> !Concepts.TEXT_DEFINITION.equals(description.getType().getId()))
				.filter(description -> !getPreferredLanguageMembers(description).isEmpty())
				.map(this::toDescriptionFragment)
				.sorted(DESCRIPTION_FRAGMENT_ORDER)
				.collect(Collectors.toList());
	}
	
	private Set<String> getPreferredLanguageMembers(Description description) {
		return description.getLanguageRefSetMembers()
			.stream()
			.filter(SnomedLanguageRefSetMember::isActive)
			.filter(member -> Acceptability.PREFERRED.getConceptId().equals(member.getAcceptabilityId()))
			.map(SnomedRefSetMember::getRefSetIdentifierId)
			.collect(Collectors.toSet());
	}
	
	private SnomedDescriptionFragment toDescriptionFragment(Description description) {
		return new SnomedDescriptionFragment(
			description.getId(), 
			CDOIDUtil.getLong(description.cdoID()),
			description.getType().getId(), 
			description.getTerm(), 
			ImmutableList.copyOf(getPreferredLanguageMembers(description))
		);
	}

	private long getEffectiveTime(Concept concept) {
		return concept.isSetEffectiveTime() ? concept.getEffectiveTime().getTime() : EffectiveTimes.UNSET_EFFECTIVE_TIME;
	}

	private Set<String> collectDirtyConceptIds(final RevisionSearcher searcher, final ICDOCommitChangeSet commitChangeSet) throws IOException {
		final Set<String> dirtyConceptIds = newHashSet();
		
		// collect relevant concept changes
		FluentIterable
			.from(commitChangeSet.getDirtyComponents(Concept.class, ALLOWED_CONCEPT_CHANGE_FEATURES))
			.transform(Concept::getId)
			.copyInto(dirtyConceptIds);

		// collect dirty concepts due to change in hierarchy
		dirtyConceptIds.addAll(referringRefSets.keySet());
//		dirtyConceptIds.addAll(getAffectedConcepts(searcher, commitChangeSet, inferredTaxonomy));
//		dirtyConceptIds.addAll(getAffectedConcepts(searcher, commitChangeSet, statedTaxonomy));
		
		// collect inferred taxonomy changes
		dirtyConceptIds.addAll(registerConceptAndDescendants(inferredTaxonomy.getNewEdges(), inferredTaxonomy.getNewTaxonomy()));
		dirtyConceptIds.addAll(registerConceptAndDescendants(inferredTaxonomy.getChangedEdges(), inferredTaxonomy.getNewTaxonomy()));
		dirtyConceptIds.addAll(registerConceptAndDescendants(inferredTaxonomy.getDetachedEdges(), inferredTaxonomy.getOldTaxonomy()));
		// collect stated taxonomy changes
		dirtyConceptIds.addAll(registerConceptAndDescendants(statedTaxonomy.getNewEdges(), statedTaxonomy.getNewTaxonomy()));
		dirtyConceptIds.addAll(registerConceptAndDescendants(statedTaxonomy.getChangedEdges(), statedTaxonomy.getNewTaxonomy()));
		dirtyConceptIds.addAll(registerConceptAndDescendants(statedTaxonomy.getDetachedEdges(), statedTaxonomy.getOldTaxonomy()));

		// collect detached reference sets where the concept itself hasn't been detached
		Collection<CDOID> detachedRefSets = commitChangeSet.getDetachedComponents(SnomedRefSetPackage.Literals.SNOMED_REF_SET);
		Set<Long> detachedRefSetStorageKeys = ImmutableSet.copyOf(CDOIDUtils.createCdoIdToLong(detachedRefSets));
		Collection<CDOID> detachedConcepts = commitChangeSet.getDetachedComponents(SnomedPackage.Literals.CONCEPT);
		Set<Long> detachedConceptStorageKeys = ImmutableSet.copyOf(CDOIDUtils.createCdoIdToLong(detachedConcepts));
		
		final Query<String> query = Query.select(String.class)
				.from(SnomedConceptDocument.class)
				.fields(RevisionDocument.Fields.ID)
				.where(Expressions.builder()
						.filter(SnomedConceptDocument.Expressions.refSetStorageKeys(detachedRefSetStorageKeys))
						.mustNot(Expressions.matchAnyLong(Revision.STORAGE_KEY, detachedConceptStorageKeys))
						.build())
				.limit(detachedRefSets.size())
				.build();

		final Hits<String> hits = searcher.search(query);
		dirtyConceptIds.addAll(hits.getHits());
		
		// remove all new concept IDs
		dirtyConceptIds.removeAll(FluentIterable.from(commitChangeSet.getNewComponents(Concept.class)).transform(Concept::getId).toSet());
		
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

//	private Collection<String> getAffectedConcepts(RevisionSearcher searcher, ICDOCommitChangeSet commitChangeSet, Taxonomy taxonomy) throws IOException {
//		final Set<String> affectedConceptIds = newHashSet();
//		final ISnomedTaxonomyBuilder newTaxonomy = taxonomy.getNewTaxonomy();
//		final ISnomedTaxonomyBuilder oldTaxonomy = taxonomy.getOldTaxonomy();
//		// process new/reactivated relationships
//		final LongIterator it = taxonomy.getNewEdges().iterator();
//		while (it.hasNext()) {
//			final String relationshipId = Long.toString(it.next());
//			final String sourceNodeId = newTaxonomy.getSourceNodeId(relationshipId);
//			affectedConceptIds.add(sourceNodeId);
//			// add all descendants
//			affectedConceptIds.addAll(LongSets.toStringSet(newTaxonomy.getAllDescendantNodeIds(sourceNodeId)));
//		}
//		
//		// process detached/inactivated relationships
//		final LongIterator detachedIt = taxonomy.getDetachedEdges().iterator();
//		final Map<String, String> oldSourceConceptIconIds = getSourceConceptIconIds(searcher, oldTaxonomy, taxonomy.getDetachedEdges());
//		while (detachedIt.hasNext()) {
//			final String relationshipId = Long.toString(detachedIt.next());
//			final String sourceNodeId = oldTaxonomy.getSourceNodeId(relationshipId);
//			// if concept still exists a relationship became inactive or deleted
//			if (newTaxonomy.containsNode(sourceNodeId)) {
//				final LongSet allAncestorNodeIds = newTaxonomy.getAllAncestorNodeIds(sourceNodeId);
//				final String oldIconId = oldSourceConceptIconIds.get(sourceNodeId);
//				if (!allAncestorNodeIds.contains(Long.parseLong(oldIconId))) {
//					affectedConceptIds.add(sourceNodeId);
//					// add all descendants
//					affectedConceptIds.addAll(LongSets.toStringSet(newTaxonomy.getAllDescendantNodeIds(sourceNodeId)));
//				}
//			} else {
//				affectedConceptIds.add(sourceNodeId);
//				affectedConceptIds.addAll(LongSets.toStringSet(oldTaxonomy.getAllDescendantNodeIds(sourceNodeId)));
//			}
//		}
//		
//		return affectedConceptIds;
//	}
//	
//	private Map<String, String> getSourceConceptIconIds(RevisionSearcher searcher, ISnomedTaxonomyBuilder oldTaxonomy, LongSet detachedRelationshipIds) throws IOException {
//		final LongIterator it = detachedRelationshipIds.iterator();
//		final Collection<String> sourceNodeIds = newHashSetWithExpectedSize(detachedRelationshipIds.size());
//		while (it.hasNext()) {
//			final String relationshipId = Long.toString(it.next());
//			sourceNodeIds.add(oldTaxonomy.getSourceNodeId(relationshipId)); 
//		}
//		
//		if (sourceNodeIds.isEmpty()) {
//			return Collections.emptyMap();
//		} else {
//			final Query<SnomedConceptDocument> query = Query.select(SnomedConceptDocument.class)
//					.where(SnomedDocument.Expressions.ids(sourceNodeIds))
//					.limit(sourceNodeIds.size())
//					.build();
//			final Hits<SnomedConceptDocument> hits = searcher.search(query);
//			final Map<String, String> iconsByIds = newHashMapWithExpectedSize(hits.getTotal());
//			for (SnomedConceptDocument hit : hits) {
//				iconsByIds.put(hit.getId(), hit.getIconId());
//			}
//			return iconsByIds;
//		}
// 	}

}
