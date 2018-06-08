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
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.datastore.ICDOCommitChangeSet;
import com.b2international.snowowl.datastore.index.ChangeSetProcessorBase;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage;
import com.google.common.collect.Maps;

/**
 * @since 4.3
 */
public class RefSetMemberChangeProcessor extends ChangeSetProcessorBase {

	public RefSetMemberChangeProcessor() {
		super("reference set member changes");
	}

	@Override
	public void process(ICDOCommitChangeSet commitChangeSet, RevisionSearcher searcher) throws IOException {
		deleteRevisions(SnomedRefSetMemberIndexEntry.class, commitChangeSet.getDetachedComponentIds(SnomedRefSetPackage.Literals.SNOMED_REF_SET_MEMBER, SnomedRefSetMemberIndexEntry.class));
		
		for (SnomedRefSetMember member : commitChangeSet.getNewComponents(SnomedRefSetMember.class)) {
			indexNewRevision(SnomedRefSetMemberIndexEntry.builder(member).build());
		}
		
		final Set<SnomedRefSetMember> dirtyMembers = commitChangeSet.getDirtyComponents(SnomedRefSetMember.class);
		final Map<String, SnomedRefSetMemberIndexEntry> currentRevisionsById = Maps.uniqueIndex(
				searcher.get(SnomedRefSetMemberIndexEntry.class, dirtyMembers.stream().map(SnomedRefSetMember::getUuid).collect(Collectors.toSet())), SnomedRefSetMemberIndexEntry::getId);
		
		for (SnomedRefSetMember member : dirtyMembers) {
			SnomedRefSetMemberIndexEntry currentRev = currentRevisionsById.get(member.getUuid());
			if (currentRev == null) {
				throw new IllegalStateException("Current revision cannot be null for member " + member.getUuid());
			}
			indexChangedRevision(currentRev, SnomedRefSetMemberIndexEntry.builder(member).build());
		}
	}
	
}
