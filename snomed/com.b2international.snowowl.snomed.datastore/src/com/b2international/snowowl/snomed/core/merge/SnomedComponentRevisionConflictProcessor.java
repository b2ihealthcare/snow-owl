/*
 * Copyright 2018-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.core.merge;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import com.b2international.commons.CompareUtils;
import com.b2international.index.mapping.DocumentMapping;
import com.b2international.index.query.Query;
import com.b2international.index.revision.*;
import com.b2international.index.revision.StagingArea.RevisionPropertyDiff;
import com.b2international.snowowl.core.codesystem.CodeSystem;
import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.core.date.Dates;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.merge.ComponentRevisionConflictProcessor;
import com.b2international.snowowl.core.merge.IMergeConflictRule;
import com.b2international.snowowl.core.repository.PathTerminologyResourceResolver;
import com.b2international.snowowl.core.repository.RevisionDocument;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.datastore.index.entry.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.*;

/**
 * @since 7.0
 */
public final class SnomedComponentRevisionConflictProcessor extends ComponentRevisionConflictProcessor {

	private static final String[] CONCEPT_FIELDS_TO_LOAD = {SnomedDocument.Fields.ID, SnomedDocument.Fields.MODULE_ID};
	private static final String[] DESCRIPTION_FIELDS_TO_LOAD = {SnomedDocument.Fields.ID, SnomedDocument.Fields.MODULE_ID, SnomedDescriptionIndexEntry.Fields.CONCEPT_ID};
	private static final String[] RELATIONSHIP_FIELDS_TO_LOAD = {SnomedDocument.Fields.ID, SnomedDocument.Fields.MODULE_ID, SnomedRelationshipIndexEntry.Fields.SOURCE_ID};
	private static final String[] MEMBER_FIELDS_TO_LOAD = {SnomedDocument.Fields.ID, SnomedDocument.Fields.MODULE_ID, SnomedRefSetMemberIndexEntry.Fields.REFERENCED_COMPONENT_ID};

	public SnomedComponentRevisionConflictProcessor() {
		super(ImmutableList.<IMergeConflictRule>builder()
				.add(new SnomedComponentReferencingDetachedConceptRule())
				.build());
	}
	
	@Override
	public RevisionPropertyDiff handleChangedInSourceAndTarget(String revisionId, DocumentMapping mapping, RevisionPropertyDiff sourceChange, RevisionPropertyDiff targetChange, ObjectMapper mapper) {
		if (SnomedDocument.Fields.EFFECTIVE_TIME.equals(sourceChange.getProperty()) && !Objects.equals(sourceChange.getNewValue(), targetChange.getNewValue())) {
			if (EffectiveTimes.isUnset(sourceChange.getNewValue())) {
				return sourceChange;
			} else if (EffectiveTimes.isUnset(targetChange.getNewValue())) {
				return targetChange;
			} else {
				final LocalDate sourceDate = EffectiveTimes.toDate(Long.parseLong(sourceChange.getNewValue()));
				final LocalDate targetDate = EffectiveTimes.toDate(Long.parseLong(targetChange.getNewValue()));
				if (sourceDate.isAfter(targetDate)) {
					return sourceChange;
				}
			}
		}
		return super.handleChangedInSourceAndTarget(revisionId, mapping, sourceChange, targetChange, mapper);
	}
	
	@Override
	public Conflict handleChangedInSourceDetachedInTarget(ObjectId objectId, List<RevisionPropertyDiff> sourceChanges) {
		boolean conflicting = false;
		for (RevisionPropertyDiff sourceChange : sourceChanges) {
			if (SnomedDocument.Fields.RELEASED.equals(sourceChange.getProperty())) {
				conflicting = true;
				break;
			}
		}
		if (conflicting) {
			return new ChangedInSourceAndDetachedInTargetConflict(objectId, sourceChanges.stream().map(diff -> diff.convert(this)).collect(Collectors.toList()));
		} else {
			return super.handleChangedInSourceDetachedInTarget(objectId, sourceChanges);
		}
	}
	
	@Override
	public String convertPropertyValue(String property, String value) {
		if (SnomedDocument.Fields.EFFECTIVE_TIME.equals(property)) {
			if (EffectiveTimes.isUnset(value)) {
				return null;
			} else {
				final long effectiveTime = Long.parseLong(value);
				return Dates.formatByGmt(effectiveTime, DateFormats.SHORT);
			}
		} else {
			return super.convertPropertyValue(property, value);
		}
	}
	
	@Override
	public Conflict convertConflict(Conflict conflict) {
		if (conflict instanceof AddedInSourceAndDetachedInTargetConflict) {
			AddedInSourceAndDetachedInTargetConflict c = (AddedInSourceAndDetachedInTargetConflict) conflict;
			if ("member".equals(c.getAddedOnSource().type())) {
				return c.withFeatureName("referencedComponent");
			}
		} else if (conflict instanceof AddedInTargetAndDetachedInSourceConflict) {
			AddedInTargetAndDetachedInSourceConflict c = (AddedInTargetAndDetachedInSourceConflict) conflict;
			if ("member".equals(c.getDetachedOnSource().type())) {
				return c.withFeatureName("referencedComponent");
			}
		}
		return super.convertConflict(conflict);
	}
	
	/**
	 * Detects SNOMED CT specific donation patterns reported as conflicts during merge/upgrade. This works between any two branches, not just during upgrades so custom branch management is also supported (although not recommended).
	 * <p>
	 * {@inheritDoc}
	 * </p>
	 */
	@Override
	public List<Conflict> filterConflicts(StagingArea staging, List<Conflict> conflicts) {
		// skip if not merging content and if there is no conflicts
		if (!staging.isMerge() || CompareUtils.isEmpty(conflicts)) {
			return conflicts;
		}
		
		RepositoryContext context = (RepositoryContext) staging.getContext();
		// detect if we are merging content between two CodeSystems, if not skip donation check
		// get the two CodeSystems
		String extensionBranch = staging.getMergeFromBranchPath();
		String donationBranch = staging.getBranchPath();
		
		CodeSystem extensionCodeSystem = context.service(PathTerminologyResourceResolver.class).resolve(context, context.info().id(), extensionBranch);
		CodeSystem donationCodeSystem = context.service(PathTerminologyResourceResolver.class).resolve(context, context.info().id(), donationBranch);
		
		// donation Code System should be marked as extension CodeSystem to be able to detect donation changes, otherwise skip donation check and report all conflicts
		// extensionOf is a required property for Code Systems that would like to participate in content donation
		if (extensionCodeSystem.getExtensionOf() == null || !extensionCodeSystem.getExtensionOf().getResourceId().equals(donationCodeSystem.getId())) {
			return conflicts;
		}
		
		final Multimap<Class<?>, String> donatedComponentsByType = HashMultimap.create();
		
		// collect components from known donation conflicts
		for (Conflict conflict : conflicts) {
			ObjectId objectId = conflict.getObjectId();
			// - components that have been added on both paths are potential donation candidates (due to centralized ID management (CIS), ID collision should not happen under normal circumstances, so this is certainly a donated content)
			if (conflict instanceof AddedInSourceAndTargetConflict) {
				donatedComponentsByType.put(staging.mappings().getClass(objectId.type()), objectId.id());
			} else if (conflict instanceof ChangedInSourceAndTargetConflict) {
				// always ignore effective time and module differences
				ChangedInSourceAndTargetConflict changedInSourceAndTarget = (ChangedInSourceAndTargetConflict) conflict;
				if (SnomedRf2Headers.FIELD_EFFECTIVE_TIME.equals(changedInSourceAndTarget.getSourceChange().getProperty()) 
						|| SnomedRf2Headers.FIELD_MODULE_ID.equals(changedInSourceAndTarget.getSourceChange().getProperty())) {
					donatedComponentsByType.put(staging.mappings().getClass(objectId.type()), objectId.id());
				}
			}
		}
		
		// collect donations
		final Set<String> donatedComponentIds = Sets.newHashSet();
		donatedComponentIds.addAll(collectDonatedComponents(staging, donatedComponentsByType, SnomedConceptDocument.class, CONCEPT_FIELDS_TO_LOAD));
		donatedComponentIds.addAll(collectDonatedComponents(staging, donatedComponentsByType, SnomedDescriptionIndexEntry.class, DESCRIPTION_FIELDS_TO_LOAD));
		donatedComponentIds.addAll(collectDonatedComponents(staging, donatedComponentsByType, SnomedRelationshipIndexEntry.class, RELATIONSHIP_FIELDS_TO_LOAD));
		donatedComponentIds.addAll(collectDonatedComponents(staging, donatedComponentsByType, SnomedRefSetMemberIndexEntry.class, MEMBER_FIELDS_TO_LOAD));
		
		return conflicts
				.stream()
				.filter(conflict -> {
					ObjectId objectId = conflict.getObjectId();
					if (donatedComponentIds.contains(objectId.id())) {
						// filter out all conflicts reported around donated content
						// revise all donated content on merge source, so new parent revision will take place instead
						staging.reviseOnMergeSource(staging.mappings().getClass(objectId.type()), objectId.id());
						return false;
					} else {
						return true;
					}
				})
				.collect(Collectors.toList());
	}

	private Set<String> collectDonatedComponents(StagingArea staging, Multimap<Class<?>, String> donatedComponentsByType, Class<? extends SnomedDocument> componentType, String[] fieldsToLoad) {
		Map<String, String[]> donatedComponents = readDonatedComponents(staging, donatedComponentsByType, componentType, fieldsToLoad);
		Map<String, String[]> extensionComponents = readExtensionComponents(staging, donatedComponentsByType, componentType, fieldsToLoad);
		
		return Sets.intersection(donatedComponents.keySet(), extensionComponents.keySet()).stream()
			.filter((donatedComponentId) -> {
				String[] donatedComponent = donatedComponents.get(donatedComponentId);
				String[] extensionComponent = extensionComponents.get(donatedComponentId);
				return isDonatedComponent(donatedComponent, extensionComponent);
			})
			.collect(Collectors.toSet());
	}

	// TODO check if modules are coming from the correct module set defined on CodeSystem, or that is unnecessary?
	private boolean isDonatedComponent(String[] donatedComponent, String[] extensionComponent) {
		// differing modules => donated component
		if (!Objects.equals(donatedComponent[1], extensionComponent[1])) {
			return donatedComponent.length == 2 // concept check requires only module diff 
					|| Objects.equals(donatedComponent[2], extensionComponent[2]); // sub-components require the container component to be the same, otherwise this is not a correctly donated content and requires manual adjustments
		}
		return false;
	}

	private Map<String, String[]> readExtensionComponents(StagingArea staging, final Multimap<Class<?>, String> candidateDonatedComponentsByType, Class<? extends Revision> componentType, String[] fieldsToLoad) {
		final Collection<String> candidateDonatedComponentIds = candidateDonatedComponentsByType.get(componentType);
		return staging.readFromMergeSource((searcher) -> {
			return Maps.uniqueIndex(searcher.search(Query.select(String[].class)
					.from(componentType)
					.fields(fieldsToLoad)
					.where(RevisionDocument.Expressions.ids(candidateDonatedComponentIds))
					.limit(candidateDonatedComponentIds.size())
					.build()), hit -> hit[0]);
		});
	}
	
	private Map<String, String[]> readDonatedComponents(StagingArea staging, final Multimap<Class<?>, String> candidateDonatedComponentsByType, Class<? extends Revision> componentType, String[] fieldsToLoad) {
		final Collection<String> candidateDonatedComponentIds = candidateDonatedComponentsByType.get(componentType);
		return staging.read((searcher) -> {
			return Maps.uniqueIndex(searcher.search(Query.select(String[].class)
					.from(componentType)
					.fields(fieldsToLoad)
					.where(RevisionDocument.Expressions.ids(candidateDonatedComponentIds))
					.limit(candidateDonatedComponentIds.size())
					.build()), hit -> hit[0]);
		});
	}

}
