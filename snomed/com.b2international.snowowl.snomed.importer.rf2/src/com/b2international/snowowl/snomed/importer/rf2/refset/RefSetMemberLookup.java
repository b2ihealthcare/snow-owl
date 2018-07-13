/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.importer.rf2.refset;

import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.cdo.common.id.CDOIDUtil;

import com.b2international.collections.PrimitiveMaps;
import com.b2international.collections.PrimitiveSets;
import com.b2international.collections.longs.LongIterator;
import com.b2international.collections.longs.LongSet;
import com.b2international.collections.longs.LongValueMap;
import com.b2international.index.Hits;
import com.b2international.index.query.Query;
import com.b2international.index.revision.RevisionIndex;
import com.b2international.index.revision.RevisionIndexRead;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.datastore.CDOEditingContext;
import com.b2international.snowowl.snomed.datastore.SnomedEditingContext;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.importer.rf2.terminology.ComponentLookup;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

public class RefSetMemberLookup {

	private final CDOEditingContext editingContext;
	private final RevisionIndex index;
	
	private Map<String, SnomedRefSetMember> newMembers;
	private LongValueMap<String> storageKeysByMemberId;
	
	public RefSetMemberLookup(final RevisionIndex index, final SnomedEditingContext editingContext) {
		this.index = index;
		this.editingContext = editingContext;
	}

	public <M> Collection<M> getMembers(Collection<String> memberIds) {
		final Collection<M> members = Sets.newHashSetWithExpectedSize(memberIds.size());
		final Set<String> missingMemberIds = Sets.newHashSet();
		
		for (String memberId : memberIds) {
			final M component = getNewMember(memberId);
			if (component != null) {
				members.add(component);
			} else {
				missingMemberIds.add(memberId);
			}
		}
		
		if (missingMemberIds.isEmpty()) {
			return members;
		}
		
		LongIterator storageKeys = getComponentStorageKeys(missingMemberIds).iterator();
		
		while (storageKeys.hasNext()) {
			final long storageKey = storageKeys.next();
			members.add((M) editingContext.lookup(storageKey));
		}
		
		return members;
	}

	public <M> M getNewMember(String memberId) {
		return newMembers == null ? null : (M) newMembers.get(memberId);
	}

	public LongSet getComponentStorageKeys(final Collection<String> memberIds) {
		final LongSet storageKeys = PrimitiveSets.newLongOpenHashSetWithExpectedSize(memberIds.size());
		final Set<String> missingStorageKeyComponentIds = newHashSet();
		for (String memberId : memberIds) {
			if (storageKeysByMemberId != null && storageKeysByMemberId.containsKey(memberId)) {
				storageKeys.add(storageKeysByMemberId.get(memberId));
			} else {
				missingStorageKeyComponentIds.add(memberId);
			}
		}
		
		if (!missingStorageKeyComponentIds.isEmpty()) {
			try {
				LongValueMap<String> missingStorageKeys = getStorageKeys(missingStorageKeyComponentIds);
				for (String missingStorageKeyComponentId : missingStorageKeyComponentIds) {
					if (missingStorageKeys.containsKey(missingStorageKeyComponentId)) {
						final long missingStorageKey = missingStorageKeys.get(missingStorageKeyComponentId);
						storageKeys.add(missingStorageKey);
						registerMemberStorageKey(missingStorageKeyComponentId, missingStorageKey);
					}
				} 
			} catch (IOException e) {
				throw new SnowowlRuntimeException(e);
			}
		}
		
		return storageKeys;
	}
	
	private LongValueMap<String> getStorageKeys(final Collection<String> componentIds) throws IOException {
		return index.read(editingContext.getBranch(), new RevisionIndexRead<LongValueMap<String>>() {
			@Override
			public LongValueMap<String> execute(RevisionSearcher index) throws IOException {
				final LongValueMap<String> map = PrimitiveMaps.newObjectKeyLongOpenHashMapWithExpectedSize(componentIds.size());
				final Query<SnomedRefSetMemberIndexEntry> query = Query.select(SnomedRefSetMemberIndexEntry.class)
						.where(SnomedRefSetMemberIndexEntry.Expressions.ids(componentIds))
						.limit(componentIds.size())
						.build();
				final Hits<SnomedRefSetMemberIndexEntry> hits = index.search(query);
				for (SnomedDocument doc : hits) {
					map.put(doc.getId(), doc.getStorageKey());
				}
				return map;
			}
		});
	}
	
	public void registerMemberStorageKey(final String memberId, final long storageKey) {
		if (storageKeysByMemberId == null) {
			storageKeysByMemberId = PrimitiveMaps.newObjectKeyLongOpenHashMapWithExpectedSize(ComponentLookup.EXPECTED_COMPONENT_SIZE);
		}
		final long existingKey = storageKeysByMemberId.put(memberId, storageKey);
		if (existingKey > 0L && existingKey != storageKey) {
			throw new IllegalStateException(
					MessageFormat.format("Storage key re-registered for member with UUID ''{0}''. Old key: {1}, new key: {2}",
							memberId, existingKey, storageKey));
		}
	}

	public void registerNewMemberStorageKeys() {
		// Consume each element while it is being registered
		if (newMembers != null) {
			for (final SnomedRefSetMember newMember : Iterables.consumingIterable(newMembers.values())) {
				registerMemberStorageKey(newMember.getUuid(), CDOIDUtil.getLong(newMember.cdoID()));
			}
			newMembers = null;
		}
	}

	public void addNewMember(final SnomedRefSetMember member) {
		if (newMembers == null) {
			newMembers = newHashMap();
		}
		newMembers.put(member.getUuid(), member);
	}
	
	/**
	 * Clears the underlying collections.
	 */
	public void clear() {
		if (null != newMembers) {
			newMembers = null;
		}
		
		if (null != storageKeysByMemberId) {
			storageKeysByMemberId = null;
		}
	}
	
}