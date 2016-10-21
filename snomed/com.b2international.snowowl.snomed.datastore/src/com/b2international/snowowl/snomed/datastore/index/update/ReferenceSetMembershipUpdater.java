/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.index.update;

import java.util.Collection;

import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.refset.RefSetMemberChange;
import com.b2international.snowowl.snomed.datastore.index.refset.RefSetMemberChange.MemberChangeKind;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

/**
 * @since 4.3
 */
public class ReferenceSetMembershipUpdater {

	private final Collection<String> currentReferringRefSets;
	private final Collection<RefSetMemberChange> memberChanges;
	private final Collection<String> currentReferringMappingRefSets;
	
	public ReferenceSetMembershipUpdater(final Collection<RefSetMemberChange> memberChanges,
			final Collection<String> currentReferringRefSets, final Collection<String> currentReferringMappingRefSets) {
		this.memberChanges = memberChanges;
		this.currentReferringRefSets = currentReferringRefSets;
		this.currentReferringMappingRefSets = currentReferringMappingRefSets;
	}
	
	public void update(SnomedConceptDocument.Builder doc) {
		// get reference set membership fields
		final Multiset<String> referencingRefSetIds = HashMultiset.create(currentReferringRefSets);
		final Multiset<String> referencingMappingRefSetIds = HashMultiset.create(currentReferringMappingRefSets);
		processReferencingRefSetIds(referencingRefSetIds, referencingMappingRefSetIds);
		// re-add reference set membership fields
		doc.referringRefSets(referencingRefSetIds);
		doc.referringMappingRefSets(referencingMappingRefSetIds);
	}
	
	public void update(SnomedDescriptionIndexEntry.Builder doc) {
		final Multiset<String> referencingRefSetIds = HashMultiset.create(currentReferringRefSets);
		final Multiset<String> referencingMappingRefSetIds = HashMultiset.create(currentReferringMappingRefSets);
		processReferencingRefSetIds(referencingRefSetIds, referencingMappingRefSetIds);
		doc.referringRefSets(referencingRefSetIds);
		doc.referringMappingRefSets(referencingMappingRefSetIds);
	}
	
	public void update(SnomedRelationshipIndexEntry.Builder doc) {
		final Multiset<String> referencingRefSetIds = HashMultiset.create(currentReferringRefSets);
		final Multiset<String> referencingMappingRefSetIds = HashMultiset.create(currentReferringMappingRefSets);
		processReferencingRefSetIds(referencingRefSetIds, referencingMappingRefSetIds);
		doc.referringRefSets(referencingRefSetIds);
		doc.referringMappingRefSets(referencingMappingRefSetIds);
	}
	
	private void processReferencingRefSetIds(final Multiset<String> referencingRefSetIds, final Multiset<String> referencingMappingRefSetIds) {
		memberChanges
			.stream()
			.filter(c -> c.getChangeKind() == MemberChangeKind.ADDED)
			.forEach(change -> {
				if (change.isMap()) {
					referencingMappingRefSetIds.add(change.getRefSetId());
				} else {
					referencingRefSetIds.add(change.getRefSetId());
				}
			});
		
		memberChanges
			.stream()
			.filter(c -> c.getChangeKind() == MemberChangeKind.REMOVED)
			.forEach(change -> {
				if (change.isMap()) {
					referencingMappingRefSetIds.remove(change.getRefSetId());
				} else {
					referencingRefSetIds.remove(change.getRefSetId());
				}
			});
	}

}
