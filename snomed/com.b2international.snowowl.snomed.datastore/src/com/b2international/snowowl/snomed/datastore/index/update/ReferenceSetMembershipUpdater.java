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
import com.b2international.snowowl.snomed.datastore.index.refset.RefSetMemberChange;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

/**
 * @since 4.3
 */
public class ReferenceSetMembershipUpdater {

	private final Collection<String> currentReferringRefSets;
	private final Collection<String> currentReferringMappingRefSets;
	private final Collection<RefSetMemberChange> memberChanges;

	public ReferenceSetMembershipUpdater(Collection<RefSetMemberChange> memberChanges, Collection<String> currentReferringRefSets, Collection<String> currentReferringMappingRefSets) {
		this.memberChanges = memberChanges;
		this.currentReferringRefSets = currentReferringRefSets;
		this.currentReferringMappingRefSets = currentReferringMappingRefSets;
	}

	public void update(SnomedConceptDocument.Builder doc) {
		// get reference set membership fields
		final Multiset<String> referencingRefSetIds = HashMultiset.create(currentReferringRefSets);
		// get reference set mapping membership fields
		final Multiset<String> mappingReferencingRefSetIds = HashMultiset.create(currentReferringMappingRefSets);
		
		// merge reference set membership with the changes extracted from the transaction, if any.
		for (final RefSetMemberChange change : memberChanges) {
			switch (change.getChangeKind()) {
				case ADDED:
					if (SnomedRefSetType.SIMPLE.equals(change.getType()) || SnomedRefSetType.ATTRIBUTE_VALUE.equals(change.getType())) {
						referencingRefSetIds.add(change.getRefSetId());
					} else if (SnomedRefSetType.SIMPLE_MAP.equals(change.getType())) {
						mappingReferencingRefSetIds.add(change.getRefSetId());
					}
					break;
				case REMOVED:
					break;
				default:
					throw new IllegalArgumentException("Unknown reference set member change kind: " + change.getChangeKind());
			}
		}

		for (final RefSetMemberChange change : memberChanges) {
			switch (change.getChangeKind()) {
				case ADDED:
					break;
				case REMOVED:
					if (SnomedRefSetType.SIMPLE.equals(change.getType()) || SnomedRefSetType.ATTRIBUTE_VALUE.equals(change.getType())) {
						referencingRefSetIds.remove(change.getRefSetId());
					} else if (SnomedRefSetType.SIMPLE_MAP.equals(change.getType())) {
						mappingReferencingRefSetIds.remove(change.getRefSetId());
					}
					break;
				default:
					throw new IllegalArgumentException("Unknown reference set member change kind: " + change.getChangeKind());
			}
		}
		
		// re-add reference set membership fields
		doc.referringRefSets(referencingRefSetIds);
		// re-add mapping reference set membership fields
		doc.referringMappingRefSets(mappingReferencingRefSetIds);
	}
}
