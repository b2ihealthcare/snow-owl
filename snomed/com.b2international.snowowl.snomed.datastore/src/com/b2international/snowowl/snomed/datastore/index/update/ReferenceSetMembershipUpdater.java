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
package com.b2international.snowowl.snomed.datastore.index.update;

import java.util.Collection;

import com.b2international.snowowl.snomed.datastore.index.entry.SnomedComponentDocument;
import com.b2international.snowowl.snomed.datastore.index.refset.RefSetMemberChange;
import com.b2international.snowowl.snomed.datastore.index.refset.RefSetMemberChange.MemberChangeKind;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

/**
 * @since 4.3
 */
public class ReferenceSetMembershipUpdater {

	private final Collection<RefSetMemberChange> memberChanges;
	private final Collection<String> currentMemberOf;
	private final Collection<String> currentActiveMemberOf;
	
	public ReferenceSetMembershipUpdater(
			final Collection<RefSetMemberChange> memberChanges,
			final Collection<String> currentMemberOf, 
			final Collection<String> currentActiveMemberOf) {
		this.memberChanges = memberChanges;
		this.currentMemberOf = currentMemberOf;
		this.currentActiveMemberOf = currentActiveMemberOf;
	}
	
	public <B extends SnomedComponentDocument.Builder<B, T>, T extends SnomedComponentDocument> void update(B doc) {
		// get reference set membership fields
		final Multiset<String> newMemberOf = HashMultiset.create(currentMemberOf);
		final Multiset<String> newActiveMemberOf = HashMultiset.create(currentActiveMemberOf);
		processReferencingRefSetIds(newMemberOf, newActiveMemberOf);
		// re-add reference set membership fields
		doc.memberOf(newMemberOf);
		doc.activeMemberOf(newActiveMemberOf);
	}
	
	private void processReferencingRefSetIds(final Multiset<String> memberOf, final Multiset<String> activeMemberOf) {
		memberChanges
			.stream()
			.filter(c -> c.getChangeKind() == MemberChangeKind.ADDED)
			.forEach(change -> {
				if (change.isActive()) {
					activeMemberOf.add(change.getRefSetId());
				}
				memberOf.add(change.getRefSetId());
			});
		
		memberChanges
			.stream()
			.filter(c -> c.getChangeKind() == MemberChangeKind.CHANGED)
			.forEach(change -> {
				// if the new state is active, then add it to the activeMemberOf otherwise remove it from that
				// this state transition won't change the memberOf field were all referring refsets are tracked
				if (change.isActive()) {
					activeMemberOf.add(change.getRefSetId());
				} else {
					activeMemberOf.remove(change.getRefSetId());
				}
			});
		
		memberChanges
			.stream()
			.filter(c -> c.getChangeKind() == MemberChangeKind.REMOVED)
			.forEach(change -> {
				if (change.isActive()) {
					activeMemberOf.remove(change.getRefSetId());
				}
				memberOf.remove(change.getRefSetId());
			});
	}

}
