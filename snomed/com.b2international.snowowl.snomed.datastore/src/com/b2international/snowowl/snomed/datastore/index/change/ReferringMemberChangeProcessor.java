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

import java.io.IOException;

import com.b2international.index.revision.RevisionSearcher;
import com.b2international.index.revision.StagingArea;
import com.b2international.index.revision.StagingArea.RevisionPropertyDiff;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.refset.RefSetMemberChange;
import com.b2international.snowowl.snomed.datastore.index.refset.RefSetMemberChange.MemberChangeKind;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

/**
 * @since 5.2
 */
final class ReferringMemberChangeProcessor {
	
	private final short referencedComponentType;

	public ReferringMemberChangeProcessor(final short referencedComponentType) {
		this.referencedComponentType = referencedComponentType;
	}
	
	public Multimap<String, RefSetMemberChange> process(StagingArea staging, RevisionSearcher searcher) throws IOException {
		final Multimap<String, RefSetMemberChange> memberChanges = HashMultimap.create();
		
		// process new members
		staging.getNewObjects(SnomedRefSetMemberIndexEntry.class)
			.filter(this::byReferencedComponentType)
			.forEach((newMember) -> {
				addChange(memberChanges, newMember, MemberChangeKind.ADDED);
			});
		
		// process dirty members
		staging.getChangedRevisions(SnomedRefSetMemberIndexEntry.class)
			.filter(diff -> byReferencedComponentType((SnomedRefSetMemberIndexEntry) diff.newRevision))
			.forEach((diff) -> {
				RevisionPropertyDiff propChange = diff.getRevisionPropertyDiff(SnomedRefSetMemberIndexEntry.Fields.ACTIVE);
				if (propChange != null) {
					addChange(memberChanges, (SnomedRefSetMemberIndexEntry) diff.newRevision, MemberChangeKind.CHANGED);
				}
			});
		
		// process detached members
		staging.getRemovedObjects(SnomedRefSetMemberIndexEntry.class)
			.filter(this::byReferencedComponentType)
			.forEach(doc -> {
				final String uuid = doc.getId();
				final String referencedComponentId = doc.getReferencedComponentId();
				final String refSetId = doc.getReferenceSetId();
				memberChanges.put(referencedComponentId, new RefSetMemberChange(uuid, refSetId, MemberChangeKind.REMOVED, doc.isActive()));
			});
		
		return memberChanges;
	}
	
	private boolean byReferencedComponentType(SnomedRefSetMemberIndexEntry member) {
		return referencedComponentType == SnomedTerminologyComponentConstants.getTerminologyComponentIdValue(member.getReferencedComponentId());
	}

	private void addChange(final Multimap<String, RefSetMemberChange> memberChanges, SnomedRefSetMemberIndexEntry member, MemberChangeKind changeKind) {
		final String uuid = member.getId();
		final String refSetId = member.getReferenceSetId();
		memberChanges.put(member.getReferencedComponentId(), new RefSetMemberChange(uuid, refSetId, changeKind, member.isActive()));
	}

}
