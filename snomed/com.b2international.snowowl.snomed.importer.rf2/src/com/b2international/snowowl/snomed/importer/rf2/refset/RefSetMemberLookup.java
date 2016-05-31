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

import java.text.MessageFormat;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import org.eclipse.emf.cdo.common.id.CDOIDUtil;

import com.b2international.commons.collections.UuidLongMap;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.CDOEditingContext;
import com.b2international.snowowl.snomed.datastore.SnomedEditingContext;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;
import com.google.common.collect.Iterators;
import com.google.common.collect.Maps;

/**
 * Maps UUIDs to CDO storage keys in a compact in-memory map.
 * 
 * 
 * @param <M> the reference set member type (must be a subtype of
 * {@link SnomedRefSetMember})
 */
public class RefSetMemberLookup<M extends SnomedRefSetMember> {

	private static final int EXPECTED_MEMBERS_SIZE = 50000;

	private final Map<UUID, M> newMembers = Maps.newHashMap();
	private UuidLongMap memberIdMap = new UuidLongMap(EXPECTED_MEMBERS_SIZE);
	private final CDOEditingContext editingContext;
	private final IBranchPath branchPath;
	
	public RefSetMemberLookup(final SnomedEditingContext editingContext) {
		this.editingContext = editingContext;
		branchPath = BranchPathUtils.createPath(editingContext.getTransaction());
	}

	@SuppressWarnings("unchecked")
	public M getMember(final UUID memberId) {
		
		final long storageKey = getMemberStorageKey(memberId);
		
		if (storageKey > 0L) {
			return (M) editingContext.lookup(storageKey);
		} else {
			final M m = newMembers.get(memberId);
			
			if (null == m) {
				
				final long _storageKey = getMemberStorageKey(branchPath, memberId.toString());
				
				if (_storageKey > 0L) {
				
					registerMemberStorageKey(memberId, _storageKey);
					return getMember(memberId); //should be fine for the second attempt
					
				} else {
					
					return null;
					
				}
				
			}
			
			return m;
		}
	}

	private long getMemberStorageKey(final UUID memberId) {
		return memberIdMap.get(memberId);
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
		for (final Iterator<M> itr = Iterators.consumingIterator(newMembers.values().iterator()); itr.hasNext();) {
			final M newMember = itr.next();
			registerMemberStorageKey(UUID.fromString(newMember.getUuid()), CDOIDUtil.getLong(newMember.cdoID()));
		}
	}

	public void addNewMember(final M member) {
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