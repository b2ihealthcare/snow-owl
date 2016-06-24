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
package com.b2international.snowowl.snomed.datastore.index.change;

import java.io.IOException;

import org.eclipse.emf.cdo.common.id.CDOID;

import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.datastore.ICDOCommitChangeSet;
import com.b2international.snowowl.datastore.cdo.CDOIDUtils;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.refset.RefSetMemberChange;
import com.b2international.snowowl.snomed.datastore.index.refset.RefSetMemberChange.MemberChangeKind;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

/**
 * @since 4.3
 */
final class ConceptReferringMemberChangeProcessor {

	private static final Predicate<SnomedRefSetMember> REFERRING_CONCEPT_MEMBER = new Predicate<SnomedRefSetMember>() {
		@Override
		public boolean apply(SnomedRefSetMember input) {
			return SnomedTerminologyComponentConstants.CONCEPT_NUMBER == input.getReferencedComponentType() && isValidType(input.getRefSet().getType());
		}
	};
	
	private static boolean isValidType(final SnomedRefSetType type) {
		return SnomedRefSetType.SIMPLE.equals(type) || SnomedRefSetType.ATTRIBUTE_VALUE.equals(type) || SnomedRefSetType.SIMPLE_MAP.equals(type);
	}
	
	public Multimap<String, RefSetMemberChange> process(ICDOCommitChangeSet commitChangeSet, RevisionSearcher searcher) throws IOException {
		final Multimap<String, RefSetMemberChange> memberChanges = HashMultimap.create();
		
		// process active new and dirty
		final Iterable<SnomedRefSetMember> newAndDirtyReferringMembers = FluentIterable.from(commitChangeSet.getNewComponents()).filter(SnomedRefSetMember.class)
				.filter(REFERRING_CONCEPT_MEMBER);
		
		for (SnomedRefSetMember member : newAndDirtyReferringMembers) {
			if (member.isActive()) {
				addChange(memberChanges, member, MemberChangeKind.ADDED);
			}
		}
		
		// process dirty inactive members
		final Iterable<SnomedRefSetMember> dirtyReferringMembers = FluentIterable
				.from(commitChangeSet.getDirtyComponents(SnomedRefSetMember.class))
				.filter(REFERRING_CONCEPT_MEMBER);
		
		for (SnomedRefSetMember member : dirtyReferringMembers) {
			if (member.isActive()) {
				addChange(memberChanges, member, MemberChangeKind.ADDED);
			} else {
				addChange(memberChanges, member, MemberChangeKind.REMOVED);
			}
		}
		
		// process detached
		final Iterable<CDOID> detachedComponents = commitChangeSet.getDetachedComponents(SnomedRefSetPackage.Literals.SNOMED_REF_SET_MEMBER);
		final Iterable<Long> detachedMemberStorageKeys = CDOIDUtils.createCdoIdToLong(detachedComponents);
		final Iterable<SnomedRefSetMemberIndexEntry> detachedMembers = searcher.get(SnomedRefSetMemberIndexEntry.class, detachedMemberStorageKeys);
		for (SnomedRefSetMemberIndexEntry doc : detachedMembers) {
			final SnomedRefSetType type = doc.getReferenceSetType(); 
			if (doc.isActive() && isValidType(type) && doc.getReferencedComponentType() == SnomedTerminologyComponentConstants.CONCEPT_NUMBER) {
				final String uuid = doc.getId();
				final String referencedComponentId = doc.getReferencedComponentId();
				final String refSetId = doc.getReferenceSetId();
				memberChanges.put(referencedComponentId, new RefSetMemberChange(uuid, refSetId, MemberChangeKind.REMOVED, type));
			}
		}
		
		return memberChanges;
	}
	
	private void addChange(final Multimap<String, RefSetMemberChange> memberChanges, SnomedRefSetMember member, MemberChangeKind changeKind) {
		final String uuid = member.getUuid();
		final String refSetId = member.getRefSetIdentifierId();
		final SnomedRefSetType refSetType = member.getRefSet().getType();
		memberChanges.put(member.getReferencedComponentId(), new RefSetMemberChange(uuid, refSetId, changeKind, refSetType));
	}
}
