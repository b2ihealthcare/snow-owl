/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.google.common.collect.Maps.newHashMap;

import java.io.IOException;
import java.util.Map;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;

import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.datastore.ICDOCommitChangeSet;
import com.b2international.snowowl.datastore.cdo.CDOIDUtils;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.domain.Acceptability;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.refset.RefSetMemberChange;
import com.b2international.snowowl.snomed.datastore.index.refset.RefSetMemberChange.MemberChangeKind;
import com.b2international.snowowl.snomed.snomedrefset.SnomedLanguageRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage;
import com.google.common.base.Function;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

/**
 * @since 4.3
 */
// TODO: The process is very similar to ConceptReferringMemberChangeProcessor, maybe the two can be merged by generalizing
public class DescriptionAcceptabilityChangeProcessor {

	public Map<String, Multimap<Acceptability, RefSetMemberChange>> process(ICDOCommitChangeSet commitChangeSet, RevisionSearcher searcher) throws IOException {
		final Multimap<String, RefSetMemberChange> preferredMemberChanges = HashMultimap.create();
		final Multimap<String, RefSetMemberChange> acceptableMemberChanges = HashMultimap.create();
		
		// add active new and active dirty members
		final Iterable<SnomedLanguageRefSetMember> newAndDirtyMembers = Iterables.concat(
				commitChangeSet.getNewComponents(SnomedLanguageRefSetMember.class),
				commitChangeSet.getDirtyComponents(SnomedLanguageRefSetMember.class));

		for (SnomedLanguageRefSetMember member : newAndDirtyMembers) {
			if (member.isActive()) {
				final String uuid = member.getUuid();
				final String refSetId = member.getRefSetIdentifierId();
				final RefSetMemberChange change = new RefSetMemberChange(uuid, refSetId, MemberChangeKind.ADDED, member.isActive());
				registerChange(preferredMemberChanges, acceptableMemberChanges, member.getAcceptabilityId(), member.getReferencedComponentId(), change);
			}
		}
		
		// remove dirty inactive (and/or changed in acceptability) members
		final Iterable<SnomedLanguageRefSetMember> dirtyMembers = commitChangeSet.getDirtyComponents(SnomedLanguageRefSetMember.class);
		
		final Iterable<Long> dirtyMemberStorageKeys = CDOIDUtils.createCdoIdToLong(CDOIDUtils.getIds(dirtyMembers));
		final Iterable<Long> detachedMemberStorageKeys = CDOIDUtils.createCdoIdToLong(commitChangeSet.getDetachedComponents(SnomedRefSetPackage.Literals.SNOMED_LANGUAGE_REF_SET_MEMBER));
		final Map<Long, SnomedRefSetMemberIndexEntry> currentRevisionsByStorageKey = Maps.uniqueIndex(searcher.get(SnomedRefSetMemberIndexEntry.class, Iterables.concat(dirtyMemberStorageKeys, detachedMemberStorageKeys)), new Function<SnomedRefSetMemberIndexEntry, Long>() {
			@Override
			public Long apply(SnomedRefSetMemberIndexEntry input) {
				return input.getStorageKey();
			}
		});
		
		for (SnomedLanguageRefSetMember member : dirtyMembers) {
			final String uuid = member.getUuid();
			final String refSetId = member.getRefSetIdentifierId();
			final RefSetMemberChange change = new RefSetMemberChange(uuid, refSetId, MemberChangeKind.REMOVED, member.isActive());
			final SnomedRefSetMemberIndexEntry before = currentRevisionsByStorageKey.get(CDOIDUtil.getLong(member.cdoID()));
			if (before != null) {
				final String beforeAcceptabilityId = before.getAcceptabilityId();
				final boolean beforeActive = before.isActive();
				final boolean acceptabilityChanged = !member.getAcceptabilityId().equals(beforeAcceptabilityId);
				
				if (beforeActive && acceptabilityChanged) {
					registerChange(preferredMemberChanges, acceptableMemberChanges, beforeAcceptabilityId, member.getReferencedComponentId(), change);
				}
			}
			
			if (!member.isActive()) {
				registerChange(preferredMemberChanges, acceptableMemberChanges, member.getAcceptabilityId(), member.getReferencedComponentId(), change);				
			}
		}
		
		// remove earlier active detached members
		
		for (CDOID cdoid : commitChangeSet.getDetachedComponents(SnomedRefSetPackage.Literals.SNOMED_LANGUAGE_REF_SET_MEMBER)) {
			final SnomedRefSetMemberIndexEntry before = currentRevisionsByStorageKey.get(CDOIDUtil.getLong(cdoid));
			if (before.isActive()) {
				final String uuid = before.getId();
				final String refSetId = before.getReferenceSetId();
				final String referencedComponentId = before.getReferencedComponentId();
				final String beforeAcceptabilityId = before.getAcceptabilityId();
				final RefSetMemberChange change = new RefSetMemberChange(uuid, refSetId, MemberChangeKind.REMOVED, before.isActive());
				
				registerChange(preferredMemberChanges, acceptableMemberChanges, beforeAcceptabilityId, referencedComponentId, change);
			}
		}
		
		final Map<String, Multimap<Acceptability, RefSetMemberChange>> changes = newHashMap();
		
		for (String descriptionId : Iterables.concat(preferredMemberChanges.keySet(), acceptableMemberChanges.keySet())) {
			if (!changes.containsKey(descriptionId)) {
				changes.put(descriptionId, HashMultimap.<Acceptability, RefSetMemberChange>create());
			}
			final Multimap<Acceptability, RefSetMemberChange> memberChanges = changes.get(descriptionId);
			
			memberChanges.putAll(Acceptability.PREFERRED, preferredMemberChanges.get(descriptionId));
			memberChanges.putAll(Acceptability.ACCEPTABLE, acceptableMemberChanges.get(descriptionId));
		}
		return changes;
	}

	private void registerChange(final Multimap<String, RefSetMemberChange> preferredMemberChanges,
			final Multimap<String, RefSetMemberChange> acceptableMemberChanges, 
			final String acceptabilityId,
			final String referencedComponentId,
			final RefSetMemberChange change) {
		
		if (acceptabilityId.equals(Concepts.REFSET_DESCRIPTION_ACCEPTABILITY_PREFERRED)) {
			preferredMemberChanges.put(referencedComponentId, change);
		} else {
			acceptableMemberChanges.put(referencedComponentId, change);
		}
	}
}
