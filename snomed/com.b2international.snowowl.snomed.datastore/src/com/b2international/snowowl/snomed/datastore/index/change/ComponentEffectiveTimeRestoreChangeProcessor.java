/*
 * Copyright 2019 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.UnexpectedTypeException;

import org.slf4j.Logger;

import com.b2international.index.revision.RevisionIndex;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.index.revision.StagingArea;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.core.request.SearchResourceRequest;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.CodeSystemEntry;
import com.b2international.snowowl.datastore.CodeSystemVersionEntry;
import com.b2international.snowowl.datastore.CodeSystems;
import com.b2international.snowowl.datastore.index.ChangeSetProcessorBase;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;
import com.b2international.snowowl.terminologyregistry.core.request.CodeSystemRequests;
import com.google.common.base.Strings;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.primitives.Longs;

/**
 * @since 7.1
 */
public final class ComponentEffectiveTimeRestoreChangeProcessor extends ChangeSetProcessorBase {

	private final Logger log;

	protected ComponentEffectiveTimeRestoreChangeProcessor(Logger log) {
		super("effective time restore");
		this.log = log;
	}

	@Override
	public void process(StagingArea staging, RevisionSearcher searcher) throws IOException {
		final Multimap<Class<? extends SnomedDocument>, SnomedDocument> componentsByType = ArrayListMultimap.create();
		staging.getChangedRevisions().values().stream()
			.map(diff -> (SnomedDocument) diff.newRevision)
			.filter(doc -> doc.isReleased() && EffectiveTimes.isUnset(doc.getEffectiveTime()))
			.forEach(doc -> componentsByType.put(doc.getClass(), doc));

		if (componentsByType.isEmpty())	{
			return;
		}
		
		final List<String> branchesForPreviousVersion = getAvailableVersionPaths(staging.getBranchPath());
		if (branchesForPreviousVersion.isEmpty()) {
			return;
		}
		
		for (String branch : branchesForPreviousVersion) {
			for (Class<? extends SnomedDocument> componentType : ImmutableSet.copyOf(componentsByType.keySet())) {
				final Set<String> componentIds = componentsByType.get(componentType).stream().map(SnomedDocument::getId).collect(Collectors.toSet());
				final Map<String, ? extends SnomedDocument> previousVersions = Maps.uniqueIndex(fetchPreviousVersions(staging.getIndex(), branch, componentType, componentIds), SnomedDocument::getId);
				for (SnomedDocument changedRevision : ImmutableList.copyOf(componentsByType.get(componentType))) {
					final SnomedDocument previousVersion = previousVersions.get(changedRevision.getId());
					if (previousVersion != null) {
						if (canRestoreEffectiveTime(changedRevision, previousVersion)) {
							SnomedDocument restoredRevision = toBuilder(changedRevision).effectiveTime(previousVersion.getEffectiveTime()).build();
							stageChange(changedRevision, restoredRevision);
						}
						componentsByType.remove(componentType, changedRevision);
					}
				}
			}
		}
		
		if (!componentsByType.isEmpty()) {
			log.warn("There were components which could not be restored, {}.", 
				componentsByType.values()
					.stream()
					.map(SnomedDocument::getId)
					.collect(Collectors.toSet())
			);
		}
	}
	
	private <B extends SnomedDocument.Builder<B, T>, T extends SnomedDocument> B toBuilder(T doc) {
		if (doc instanceof SnomedConceptDocument) {
			return (B) SnomedConceptDocument.builder((SnomedConceptDocument) doc);
		} else if (doc instanceof SnomedDescriptionIndexEntry) {
			return (B) SnomedDescriptionIndexEntry.builder((SnomedDescriptionIndexEntry) doc);
		} else if (doc instanceof SnomedRelationshipIndexEntry) {
			return (B) SnomedRelationshipIndexEntry.builder((SnomedRelationshipIndexEntry) doc);
		} else if (doc instanceof SnomedRefSetMemberIndexEntry) {
			return (B) SnomedRefSetMemberIndexEntry.builder((SnomedRefSetMemberIndexEntry) doc);
		} else {
			throw new UnsupportedOperationException("Not implemented for: " + doc);
		}
	}

	private boolean canRestoreEffectiveTime(SnomedDocument componentToRestore, SnomedDocument previousVersion) {
		if (componentToRestore.isActive() != previousVersion.isActive()) {
			return false;
		}
		
		if (!Objects.equals(componentToRestore.getModuleId(), previousVersion.getModuleId())) {
			return false;
		} 
		
		if (componentToRestore instanceof SnomedConceptDocument && previousVersion instanceof SnomedConceptDocument) {
			final SnomedConceptDocument conceptToRestore = (SnomedConceptDocument) componentToRestore;
			final SnomedConceptDocument previousConcept = (SnomedConceptDocument) previousVersion;
			return conceptToRestore.isPrimitive() == previousConcept.isPrimitive();
		} else if (componentToRestore instanceof SnomedDescriptionIndexEntry && previousVersion instanceof SnomedDescriptionIndexEntry) {
			final SnomedDescriptionIndexEntry descriptionToRestore = (SnomedDescriptionIndexEntry) componentToRestore;
			final SnomedDescriptionIndexEntry previousDescription = (SnomedDescriptionIndexEntry) previousVersion;
			return descriptionToRestore.getTerm().equals(previousDescription.getTerm()) 
					&& descriptionToRestore.getCaseSignificanceId().equals(previousDescription.getCaseSignificanceId());
		} else if (componentToRestore instanceof SnomedRelationshipIndexEntry && previousVersion instanceof SnomedRelationshipIndexEntry) {
			final SnomedRelationshipIndexEntry relationshipToRestore = (SnomedRelationshipIndexEntry) componentToRestore;
			final SnomedRelationshipIndexEntry previousRelationship = (SnomedRelationshipIndexEntry) previousVersion;
			return Objects.equals(relationshipToRestore.getGroup(), previousRelationship.getGroup()) 
					&& Objects.equals(relationshipToRestore.getUnionGroup(), previousRelationship.getUnionGroup()) 
					&& relationshipToRestore.getCharacteristicTypeId().equals(previousRelationship.getCharacteristicTypeId())
					&& relationshipToRestore.getModifierId().equals(previousRelationship.getModifierId());
		} else if (componentToRestore instanceof SnomedRefSetMemberIndexEntry && previousVersion instanceof SnomedRefSetMemberIndexEntry) {
			final SnomedRefSetMemberIndexEntry memberToRestore = (SnomedRefSetMemberIndexEntry) componentToRestore;
			final SnomedRefSetMemberIndexEntry previousMember = (SnomedRefSetMemberIndexEntry) previousVersion;
			return memberToRestore.getAdditionalFields().entrySet().stream().allMatch(entry -> {
				return Objects.equals(entry.getValue(), previousMember.getAdditionalFields().get(entry.getKey()));
			});
		} else {
			throw new UnexpectedTypeException("Unexpected component type '" + componentToRestore.getClass() + "'.");
		}
	}
	
	private List<String> getAvailableVersionPaths(String branchPath) {
		final IEventBus eventBus = ApplicationContext.getServiceForClass(IEventBus.class);
		final CodeSystems codeSystems = CodeSystemRequests.prepareSearchCodeSystem()
				.all()
				.build(SnomedDatastoreActivator.REPOSITORY_UUID)
				.execute(eventBus)
				.getSync();

		final Map<String, CodeSystemEntry> codeSystemsByMainBranch = Maps.uniqueIndex(codeSystems, CodeSystemEntry::getBranchPath);

		final List<CodeSystemEntry> relativeCodeSystems = Lists.newArrayList();

		final Iterator<IBranchPath> bottomToTop = BranchPathUtils.bottomToTopIterator(BranchPathUtils.createPath(branchPath));

		while (bottomToTop.hasNext()) {
			final IBranchPath candidate = bottomToTop.next();
			if (codeSystemsByMainBranch.containsKey(candidate.getPath())) {
				relativeCodeSystems.add(codeSystemsByMainBranch.get(candidate.getPath()));
			}
		}
		if (relativeCodeSystems.isEmpty()) {
			throw new IllegalStateException("No relative code system has been found for branch '" + branchPath + "'");
		}

		// the first code system in the list is the working codesystem
		final CodeSystemEntry workingCodeSystem = relativeCodeSystems.stream().findFirst().get();

		final Optional<CodeSystemVersionEntry> workingCodeSystemVersion = CodeSystemRequests.prepareSearchCodeSystemVersion()
				.one()
				.filterByCodeSystemShortName(workingCodeSystem.getShortName())
				.sortBy(SearchResourceRequest.SortField.descending(CodeSystemVersionEntry.Fields.EFFECTIVE_DATE))
				.build(SnomedDatastoreActivator.REPOSITORY_UUID)
				.execute(eventBus)
				.getSync()
				.first();

		final List<CodeSystemVersionEntry> relativeCodeSystemVersions = Lists.newArrayList();

		if (workingCodeSystemVersion.isPresent() && !Strings.isNullOrEmpty(workingCodeSystemVersion.get().getPath())) {
			relativeCodeSystemVersions.add(workingCodeSystemVersion.get());
		}

		if (relativeCodeSystems.size() > 1) {

			relativeCodeSystems.stream().skip(1).forEach(codeSystem -> {

				final Map<String, CodeSystemVersionEntry> pathToVersionMap = CodeSystemRequests.prepareSearchCodeSystemVersion()
						.all()
						.filterByCodeSystemShortName(codeSystem.getShortName())
						.build(SnomedDatastoreActivator.REPOSITORY_UUID)
						.execute(eventBus)
						.getSync()
						.stream()
						.collect(Collectors.toMap(version -> version.getPath(), v -> v));

				final Iterator<IBranchPath> branchPathIterator = BranchPathUtils.bottomToTopIterator(BranchPathUtils.createPath(branchPath));

				while (branchPathIterator.hasNext()) {
					final IBranchPath candidate = branchPathIterator.next();
					if (pathToVersionMap.containsKey(candidate.getPath())) {
						relativeCodeSystemVersions.add(pathToVersionMap.get(candidate.getPath()));
						break;
					}
				}

			});

		}

		return relativeCodeSystemVersions.stream()
				// sort versions by effective date in reversed order
				.sorted((v1, v2) -> Longs.compare(v2.getEffectiveDate(), v1.getEffectiveDate()))
				.map(CodeSystemVersionEntry::getPath).collect(Collectors.toList());
	}
	
	private Iterable<? extends SnomedDocument> fetchPreviousVersions(RevisionIndex index, String branch, Class<? extends SnomedDocument> componentType, Set<String> ids) {
		return index.read(branch, searcher -> searcher.get(componentType, ids));
	}

}
