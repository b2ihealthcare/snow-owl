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
package com.b2international.snowowl.snomed.core.merge;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import com.b2international.index.revision.AddedInSourceAndDetachedInTargetConflict;
import com.b2international.index.revision.AddedInTargetAndDetachedInSourceConflict;
import com.b2international.index.revision.Conflict;
import com.b2international.index.revision.ObjectId;
import com.b2international.index.revision.RevisionBranchChangeSet;
import com.b2international.index.revision.StagingArea;
import com.b2international.snowowl.core.merge.IMergeConflictRule;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

/**
 * @since 7.0
 */
public class SnomedComponentReferencingDetachedConceptRule implements IMergeConflictRule {

	@Override
	public Collection<Conflict> validate(StagingArea staging, RevisionBranchChangeSet fromChanges, RevisionBranchChangeSet toChanges) {
		ImmutableList.Builder<Conflict> conflicts = ImmutableList.builder();

		conflicts.addAll(check(fromChanges, toChanges, true));
		conflicts.addAll(check(toChanges, fromChanges, false));
		
		return conflicts.build();
	}

	private Iterable<? extends Conflict> check(RevisionBranchChangeSet newAndChangedChangeSet, RevisionBranchChangeSet detachedChangeSet, boolean addedInSource) {
		final Set<String> detachedConceptIds = detachedChangeSet.getRemovedIds(SnomedConceptDocument.class);
		// in case of no detached components, skip
		if (detachedConceptIds.isEmpty()) {
			return Collections.emptySet();
		}
		
		final ImmutableList.Builder<Conflict> conflicts = ImmutableList.builder();
		
		collectDescriptionConflicts(newAndChangedChangeSet, detachedConceptIds, addedInSource, conflicts);
		collectRelationshipConflicts(newAndChangedChangeSet, detachedConceptIds, addedInSource, conflicts);

		return conflicts.build();
	}
	
	private void collectDescriptionConflicts(RevisionBranchChangeSet changeSet, final Set<String> detachedConceptIds,
			boolean addedInSource, final ImmutableList.Builder<Conflict> conflicts) {
		Set<String> newAndChangedDescriptions = ImmutableSet.<String>builder()
			.addAll(changeSet.getAddedIds(SnomedDescriptionIndexEntry.class))
			.addAll(changeSet.getChangedIds(SnomedDescriptionIndexEntry.class))
			.build();

		changeSet
			.read(searcher -> searcher.get(SnomedDescriptionIndexEntry.class, newAndChangedDescriptions))
			.forEach(description -> {
				final String conflictingReference;
				if (detachedConceptIds.contains(description.getTypeId())) {
					conflictingReference = SnomedDescriptionIndexEntry.Fields.TYPE_ID;
				} else if (detachedConceptIds.contains(description.getCaseSignificanceId())) {
					conflictingReference = SnomedDescriptionIndexEntry.Fields.CASE_SIGNIFICANCE_ID;
				} else {
					conflictingReference = "";
				}
				if (!Strings.isNullOrEmpty(conflictingReference)) {
					if (addedInSource) {
						conflicts.add(new AddedInSourceAndDetachedInTargetConflict(ObjectId.of("description", description.getId()), ObjectId.of("concept", description.getTypeId()), conflictingReference));
					} else {
						conflicts.add(new AddedInTargetAndDetachedInSourceConflict(ObjectId.of("concept", description.getTypeId()), ObjectId.of("description", description.getId()), conflictingReference));
					}
				}
			});
	}
	
	private void collectRelationshipConflicts(RevisionBranchChangeSet changeSet, final Set<String> detachedConceptIds,
			boolean addedInSource, final ImmutableList.Builder<Conflict> conflicts) {
		Set<String> newAndChangedRelationships = ImmutableSet.<String>builder()
				.addAll(changeSet.getAddedIds(SnomedRelationshipIndexEntry.class))
				.addAll(changeSet.getChangedIds(SnomedRelationshipIndexEntry.class))
				.build();
		
		changeSet
			.read(searcher -> searcher.get(SnomedRelationshipIndexEntry.class, newAndChangedRelationships))
			.forEach(relationship -> {
				final String conflictingReference;
				if (detachedConceptIds.contains(relationship.getDestinationId())) {
					conflictingReference = SnomedRelationshipIndexEntry.Fields.DESTINATION_ID;
				} else if (detachedConceptIds.contains(relationship.getTypeId())) {
					conflictingReference = SnomedRelationshipIndexEntry.Fields.TYPE_ID;	
				} else if (detachedConceptIds.contains(relationship.getModifierId())) {
					conflictingReference = SnomedRelationshipIndexEntry.Fields.MODIFIER_ID;
				} else if (detachedConceptIds.contains(relationship.getCharacteristicTypeId())) {
					conflictingReference = SnomedRelationshipIndexEntry.Fields.CHARACTERISTIC_TYPE_ID;
				} else {
					conflictingReference = "";
				}
				if (!Strings.isNullOrEmpty(conflictingReference)) {
					if (addedInSource) {
						conflicts.add(new AddedInSourceAndDetachedInTargetConflict(ObjectId.of("relationship", relationship.getId()), ObjectId.of("concept", relationship.getDestinationId()), conflictingReference));
					} else {
						conflicts.add(new AddedInTargetAndDetachedInSourceConflict(ObjectId.of("concept", relationship.getDestinationId()), ObjectId.of("relationship", relationship.getId()), conflictingReference));
					}
				}
			});
	}

}
