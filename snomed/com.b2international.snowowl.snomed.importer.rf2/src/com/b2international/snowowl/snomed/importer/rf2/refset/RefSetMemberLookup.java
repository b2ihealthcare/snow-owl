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
package com.b2international.snowowl.snomed.importer.rf2.refset;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import org.eclipse.emf.cdo.common.id.CDOIDUtil;

import com.b2international.commons.collections.UuidLongMap;
import com.b2international.index.Hits;
import com.b2international.index.query.Query;
import com.b2international.index.revision.RevisionIndex;
import com.b2international.index.revision.RevisionIndexRead;
import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.datastore.CDOEditingContext;
import com.b2international.snowowl.datastore.cdo.CDOUtils;
import com.b2international.snowowl.snomed.datastore.SnomedEditingContext;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.collect.Maps;

public class RefSetMemberLookup {

	private static final int EXPECTED_MEMBERS_SIZE = 50000;

	private final Map<UUID, SnomedRefSetMember> newMembers = Maps.newHashMap();
	private UuidLongMap memberIdMap = new UuidLongMap(EXPECTED_MEMBERS_SIZE);
	private final CDOEditingContext editingContext;
	private final RevisionIndex index;
	
	public RefSetMemberLookup(final RevisionIndex index, final SnomedEditingContext editingContext) {
		this.index = index;
		this.editingContext = editingContext;
	}

	@SuppressWarnings("unchecked")
	public <M extends SnomedRefSetMember> M getMember(final UUID memberId) {
		if (newMembers.containsKey(memberId)) {
			return (M) newMembers.get(memberId);
		} else {
			final long storageKey = getStorageKey(memberId);
			if (storageKey > CDOUtils.NO_STORAGE_KEY) {
				return (M) editingContext.lookup(storageKey);
			} else {
				return null;
			}
		} 
	}

	public long getStorageKey(final UUID uuid) {
		long storageKey = memberIdMap.get(uuid);
		if (storageKey < 0) {
			storageKey = index.read(editingContext.getBranch(), new RevisionIndexRead<Long>() {
				@Override
				public Long execute(RevisionSearcher index) throws IOException {
					final Query<SnomedRefSetMemberIndexEntry> query = Query.select(SnomedRefSetMemberIndexEntry.class)
							.where(SnomedRefSetMemberIndexEntry.Expressions.id(uuid.toString()))
							.limit(2)
							.build();
					final Hits<SnomedRefSetMemberIndexEntry> hits = index.search(query);
					return hits.getTotal() > 0 ? Iterables.getOnlyElement(hits).getStorageKey() : CDOUtils.NO_STORAGE_KEY;
				}
			});
			if (storageKey > CDOUtils.NO_STORAGE_KEY) {
				memberIdMap.put(uuid, storageKey);
			}
		}
		return storageKey;
	}

	public void registerMemberStorageKey(final UUID memberId, final long storageKey) {
		
		final long existingKey = memberIdMap.put(memberId, storageKey);
		
		if (existingKey > 0L && existingKey != storageKey) {
			throw new IllegalStateException(
					MessageFormat.format("Storage key re-registered for member with UUID ''{0}''. Old key: {1}, new key: {2}",
							memberId, existingKey, storageKey));
		}
	}

	public void registerNewMembers() {
		// Consume each element while it is being registered
		for (final Iterator<SnomedRefSetMember> itr = Iterators.consumingIterator(newMembers.values().iterator()); itr.hasNext();) {
			final SnomedRefSetMember newMember = itr.next();
			registerMemberStorageKey(UUID.fromString(newMember.getUuid()), CDOIDUtil.getLong(newMember.cdoID()));
		}
	}

	public void addNewMember(final SnomedRefSetMember member) {
		newMembers.put(UUID.fromString(member.getUuid()), member);
	}
	
	/**
	 * Clears the underlying collections.
	 */
	public void clear() {
		if (null != newMembers) {
			newMembers.clear();
		}
		
		if (null != memberIdMap) {
			memberIdMap = null;
		}
	}
	
}