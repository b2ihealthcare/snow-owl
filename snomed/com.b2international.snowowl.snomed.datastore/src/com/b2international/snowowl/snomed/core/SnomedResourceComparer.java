/*
 * Copyright 2023 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.core;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.b2international.snowowl.core.ComponentIdentifier;
import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.ResourceURIWithQuery;
import com.b2international.snowowl.core.branch.compare.BranchCompareResult;
import com.b2international.snowowl.core.compare.AnalysisCompareChangeKind;
import com.b2international.snowowl.core.compare.AnalysisCompareResult;
import com.b2international.snowowl.core.compare.AnalysisCompareResultItem;
import com.b2international.snowowl.core.compare.DependencyComparer;
import com.b2international.snowowl.core.config.IndexConfiguration;
import com.b2international.snowowl.core.config.RepositoryConfiguration;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.repository.RepositoryRequests;
import com.b2international.snowowl.core.uri.ResourceURIPathResolver;
import com.b2international.snowowl.snomed.core.domain.*;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMembers;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.google.common.collect.*;

/**
 * @since 9.0
 */
public class SnomedResourceComparer implements DependencyComparer {

	@Override
	public AnalysisCompareResult compareResource(
		final RepositoryContext context, 
		final ResourceURIWithQuery fromUri, 
		final ResourceURIWithQuery toUri,
		final boolean summaryOnly
	) {

		final IndexConfiguration indexConfiguration = context.service(RepositoryConfiguration.class).getIndexConfiguration();
		final int partitionSize = indexConfiguration.getTermPartitionSize();
		final int pageSize = indexConfiguration.getPageSize();

		final ResourceURI fromWithoutQuery = fromUri.getResourceUri();
		final ResourceURI toWithoutQuery = toUri.getResourceUri();
		final ResourceURIPathResolver pathResolver = context.service(ResourceURIPathResolver.class);

		final List<String> branchPaths = pathResolver.resolve(context, ImmutableList.of(fromWithoutQuery, toWithoutQuery));
		final String baseBranch = branchPaths.get(0);
		final String compareBranch = branchPaths.get(1);

		final BranchCompareResult compareResult = RepositoryRequests.branching().prepareCompare()
			.setBase(baseBranch)
			.setCompare(compareBranch)
			.setExcludeComponentChanges(true)
			.build()
			.execute(context);

		final Multimap<String, String> addedIdsByType = HashMultimap.create();
		final Multimap<String, String> changedIdsByType = HashMultimap.create();
		final Multimap<String, String> deletedIdsByType = HashMultimap.create();

		indexIdsByType(addedIdsByType, compareResult.getNewComponents());
		indexIdsByType(changedIdsByType, compareResult.getChangedComponents());
		indexIdsByType(deletedIdsByType, compareResult.getDeletedComponents());

		final Collection<String> newConcepts = addedIdsByType.get(SnomedConcept.TYPE);
		final Collection<String> newDescriptions = addedIdsByType.get(SnomedDescription.TYPE);
		final Collection<String> newRelationships = addedIdsByType.get(SnomedRelationship.TYPE);
		final Collection<String> newMembers = addedIdsByType.get(SnomedReferenceSetMember.TYPE);

		final Collection<String> changedConcepts = changedIdsByType.get(SnomedConcept.TYPE);
		final Collection<String> changedDescriptions = changedIdsByType.get(SnomedDescription.TYPE);
		final Collection<String> changedRelationships = changedIdsByType.get(SnomedRelationship.TYPE);
		final Collection<String> changedMembers = changedIdsByType.get(SnomedReferenceSetMember.TYPE);

		final Collection<String> deletedConcepts = deletedIdsByType.get(SnomedConcept.TYPE);
		final Collection<String> deletedDescriptions = deletedIdsByType.get(SnomedDescription.TYPE);
		final Collection<String> deletedRelationships = deletedIdsByType.get(SnomedRelationship.TYPE);
		final Collection<String> deletedMembers = deletedIdsByType.get(SnomedReferenceSetMember.TYPE);

		// Remove members that appear alongside known new/changed concepts and descriptions
		// FIXME: we are skipping relationships as there are no reference sets currently referencing this component type
		removeRedundantMembers(
			context,
			partitionSize,
			pageSize,
			Iterables.concat(newConcepts, newDescriptions, changedConcepts, changedDescriptions),
			toWithoutQuery,
			newMembers,
			changedMembers,
			deletedMembers);

		// Remove deleted members that appear alongside known deleted concepts and descriptions
		removeDeletedMembers(
			context,
			partitionSize,
			pageSize,
			Iterables.concat(deletedConcepts, deletedDescriptions),
			fromWithoutQuery,
			deletedMembers);

		// Remove descriptions and relationships that appear alongside known new/changed concepts
		removeRedundantCoreComponents(
			context,
			partitionSize,
			pageSize,
			Iterables.concat(newConcepts, changedConcepts),
			toWithoutQuery,
			newDescriptions,
			changedDescriptions,
			deletedDescriptions,
			newRelationships,
			changedRelationships,
			deletedRelationships);

		// Remove deleted descriptions and relationships that appear alongside known deleted concepts
		removeDeletedCoreComponents(
			context,
			partitionSize,
			pageSize,
			deletedConcepts,
			fromWithoutQuery,
			deletedDescriptions,
			deletedRelationships);

		/*
		 * Remaining non-concept components contribute to the final compare results in the following fashion:
		 *
		 * - member -> referenced component -> containing concept or self -> changed
		 * - description -> containing concept -> changed
		 * - relationship -> source concept -> changed
		 * 
		 * New and changed components can be retrieved from "toUri", deleted components need to be looked up on "fromUri".
		 */
		final SortedSetMultimap<String, AnalysisCompareChangeKind> changeDetails = TreeMultimap.create();
		
		// Register all changed concepts
		changedConcepts.forEach(id -> changeDetails.put(id, AnalysisCompareChangeKind.COMPONENT_CHANGE));
		
		// OWL expression changes are considered "property changes", the rest is unspecified
		registerMemberChanges(
			context,
			partitionSize,
			pageSize,
			Iterables.concat(newMembers, changedMembers),
			toWithoutQuery,
			changeDetails);

		registerMemberChanges(
			context,
			partitionSize,
			pageSize,
			deletedMembers,
			fromWithoutQuery,
			changeDetails);

		// Description term and status changes are considered "term changes", the rest is unspecified
		registerDescriptionChanges(
			context, 
			partitionSize, 
			pageSize, 
			newDescriptions, 
			toWithoutQuery, 
			changeDetails);
		
		registerDescriptionChanges(
			context, 
			partitionSize, 
			pageSize, 
			deletedDescriptions, 
			fromWithoutQuery,
			changeDetails);

		registerRelationshipChanges(
			context, 
			partitionSize, 
			pageSize, 
			Iterables.concat(newRelationships, changedRelationships), 
			toWithoutQuery, 
			changeDetails);
		
		registerRelationshipChanges(
			context, 
			partitionSize, 
			pageSize, 
			deletedRelationships, 
			fromWithoutQuery, 
			changeDetails);
		
		final AnalysisCompareResult result;
		
		if (summaryOnly) {
			
			// No change detail items needed
			result = new AnalysisCompareResult(fromUri, toUri);
			
		} else {

			final List<AnalysisCompareResultItem> items = changeDetails.entries()
				.stream()
				.map(e -> new AnalysisCompareResultItem(e.getKey(), e.getValue()))
				.collect(Collectors.toList());
			
			result = new AnalysisCompareResult(items, fromUri, toUri);
		}
		
		result.setNewComponents(newConcepts.size());
		result.setChangedComponents(changeDetails.size());
		result.setDeletedComponents(deletedConcepts.size());
		return result;
	}

	private void indexIdsByType(final Multimap<String, String> idsByType, final Collection<ComponentIdentifier> componentIdentifiers) {
		componentIdentifiers.forEach(id -> idsByType.put(id.getComponentType(), id.getComponentId()));
	}

	private void removeRedundantMembers(
		final RepositoryContext context,
		final int partitionSize,
		final int pageSize,
		final Iterable<String> referencedComponentIds,
		final ResourceURI resourceUri,
		final Collection<String> newMembers,
		final Collection<String> changedMembers,
		final Collection<String> deletedMembers
	) {
		for (final List<String> batch : Iterables.partition(referencedComponentIds, partitionSize)) {
			SnomedRequests.prepareSearchMember()
				.filterByReferencedComponent(batch)
				.setLimit(pageSize)
				.setFields(SnomedReferenceSetMember.Fields.ID)
				.stream(context, rb -> rb.build(resourceUri))
				.flatMap(SnomedReferenceSetMembers::stream)
				.forEachOrdered(m -> {
					newMembers.remove(m.getId());
					changedMembers.remove(m.getId());
					deletedMembers.remove(m.getId());
				});
		}
	}

	private void removeDeletedMembers(
		final RepositoryContext context,
		final int partitionSize,
		final int pageSize,
		final Iterable<String> referencedComponentIds,
		final ResourceURI resourceUri,
		final Collection<String> deletedMembers
	) {
		for (final List<String> batch : Iterables.partition(referencedComponentIds, partitionSize)) {
			SnomedRequests.prepareSearchMember()
				.filterByReferencedComponent(batch)
				.setLimit(pageSize)
				.setFields(SnomedReferenceSetMember.Fields.ID)
				.stream(context, rb -> rb.build(resourceUri))
				.flatMap(SnomedReferenceSetMembers::stream)
				.forEachOrdered(m -> {
					deletedMembers.remove(m.getId());
				});
		}
	}

	private void removeRedundantCoreComponents(
		final RepositoryContext context,
		final int partitionSize,
		final int pageSize,
		final Iterable<String> conceptIds,
		final ResourceURI resourceUri,
		final Collection<String> newDescriptions,
		final Collection<String> changedDescriptions,
		final Collection<String> deletedDescriptions,
		final Collection<String> newRelationships,
		final Collection<String> changedRelationships,
		final Collection<String> deletedRelationships
	) {
		for (final List<String> batch : Iterables.partition(conceptIds, partitionSize)) {
			SnomedRequests.prepareSearchDescription()
				.filterByConcepts(batch)
				.setLimit(pageSize)
				.setFields(SnomedDescription.Fields.ID)
				.stream(context, rb -> rb.build(resourceUri))
				.flatMap(SnomedDescriptions::stream)
				.forEachOrdered(d -> {
					newDescriptions.remove(d.getId());
					changedDescriptions.remove(d.getId());
					deletedDescriptions.remove(d.getId());
				});

			SnomedRequests.prepareSearchRelationship()
				.filterBySources(batch)
				.setLimit(pageSize)
				.setFields(SnomedRelationship.Fields.ID)
				.stream(context, rb -> rb.build(resourceUri))
				.flatMap(SnomedRelationships::stream)
				.forEachOrdered(r -> {
					newRelationships.remove(r.getId());
					changedRelationships.remove(r.getId());
					deletedRelationships.remove(r.getId());
				});
		}
	}

	private void removeDeletedCoreComponents(
		final RepositoryContext context,
		final int partitionSize,
		final int pageSize,
		final Iterable<String> conceptIds,
		final ResourceURI resourceUri,
		final Collection<String> deletedDescriptions,
		final Collection<String> deletedRelationships
	) {
		for (final List<String> batch : Iterables.partition(conceptIds, partitionSize)) {
			SnomedRequests.prepareSearchDescription()
				.filterByConcepts(batch)
				.setLimit(pageSize)
				.setFields(SnomedDescription.Fields.ID)
				.stream(context, rb -> rb.build(resourceUri))
				.flatMap(SnomedDescriptions::stream)
				.forEachOrdered(d -> {
					deletedDescriptions.remove(d.getId());
				});

			SnomedRequests.prepareSearchRelationship()
				.filterBySources(batch)
				.setLimit(pageSize)
				.setFields(SnomedRelationship.Fields.ID)
				.stream(context, rb -> rb.build(resourceUri))
				.flatMap(SnomedRelationships::stream)
				.forEachOrdered(r -> {
					deletedRelationships.remove(r.getId());
				});
		}
	}

	private void registerMemberChanges(
		final RepositoryContext context,
		final int partitionSize,
		final int pageSize,
		final Iterable<String> memberIds,
		final ResourceURI resourceUri,
		final SortedSetMultimap<String, AnalysisCompareChangeKind> changeDetails
	) {
		for (final List<String> batch : Iterables.partition(memberIds, partitionSize)) {
			SnomedRequests.prepareSearchMember()
				.filterByIds(batch)
				.filterByReferencedComponentType(SnomedConcept.TYPE)
				.setLimit(pageSize)
				.setFields(SnomedReferenceSetMember.Fields.ID, SnomedReferenceSetMember.Fields.REFERENCED_COMPONENT_ID)
				.stream(context, rb -> rb.build(resourceUri))
				.flatMap(SnomedReferenceSetMembers::stream)
				.forEachOrdered(m -> changeDetails.put(m.getReferencedComponentId(), AnalysisCompareChangeKind.COMPONENT_CHANGE));

			final Set<String> descriptionIds = SnomedRequests.prepareSearchMember()
				.filterByIds(batch)
				.filterByReferencedComponentType(SnomedDescription.TYPE)
				.setLimit(pageSize)
				.setFields(SnomedReferenceSetMember.Fields.ID, SnomedReferenceSetMember.Fields.REFERENCED_COMPONENT_ID)
				.stream(context, rb -> rb.build(resourceUri))
				.flatMap(SnomedReferenceSetMembers::stream)
				.map(SnomedReferenceSetMember::getReferencedComponentId)
				.collect(Collectors.toSet());

			SnomedRequests.prepareSearchDescription()
				.filterByIds(descriptionIds)
				.setLimit(descriptionIds.size())
				.setFields(SnomedDescription.Fields.ID, SnomedDescription.Fields.CONCEPT_ID)
				.build(resourceUri)
				.execute(context)
				.forEach(d -> changeDetails.put(d.getConceptId(), AnalysisCompareChangeKind.COMPONENT_CHANGE));
		}
	}

	private void registerDescriptionChanges(
		final RepositoryContext context,
		final int partitionSize,
		final int pageSize,
		final Iterable<String> descriptionIds,
		final ResourceURI resourceUri,
		final SortedSetMultimap<String, AnalysisCompareChangeKind> changeDetails
	) {
		for (final List<String> batch : Iterables.partition(descriptionIds, partitionSize)) {
			SnomedRequests.prepareSearchDescription()
				.filterByIds(batch)
				.setLimit(pageSize)
				.setFields(SnomedDescription.Fields.ID, SnomedDescription.Fields.CONCEPT_ID)
				.stream(context, rb -> rb.build(resourceUri))
				.flatMap(SnomedDescriptions::stream)
				.forEachOrdered(d -> changeDetails.put(d.getConceptId(), AnalysisCompareChangeKind.COMPONENT_CHANGE));
		}
	}

	private void registerRelationshipChanges(
		final RepositoryContext context,
		final int partitionSize,
		final int pageSize,
		final Iterable<String> relationshipIds,
		final ResourceURI resourceUri,
		final SortedSetMultimap<String, AnalysisCompareChangeKind> changeDetails
	) {
		for (final List<String> batch : Iterables.partition(relationshipIds, partitionSize)) {
			SnomedRequests.prepareSearchRelationship()
				.filterByIds(batch)
				.setLimit(pageSize)
				.setFields(SnomedRelationship.Fields.ID, SnomedRelationship.Fields.SOURCE_ID)
				.stream(context, rb -> rb.build(resourceUri))
				.flatMap(SnomedRelationships::stream)
				.forEachOrdered(r -> changeDetails.put(r.getSourceId(), AnalysisCompareChangeKind.COMPONENT_CHANGE));
		}
	}
}
