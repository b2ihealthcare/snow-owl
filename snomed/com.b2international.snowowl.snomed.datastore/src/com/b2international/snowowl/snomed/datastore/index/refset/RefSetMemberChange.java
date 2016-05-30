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
package com.b2international.snowowl.snomed.datastore.index.refset;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Objects;

import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;

/**
 * Class for representing a reference set member change.
 * Specifies what is the change kind in which reference set.
 */
public class RefSetMemberChange implements Comparable<RefSetMemberChange> {
	
	/**
	 * Enumeration indicating whether a reference set member has been added or detached from its container
	 * reference set. 
	 */
	public static enum MemberChangeKind {
		ADDED,
		REMOVED;
	}
	
	private final String uuid;
	private final String refSetId;
	private final MemberChangeKind changeKind;
	private final SnomedRefSetType type;
	
	public RefSetMemberChange(String uuid, final String refSetId, final MemberChangeKind changeKind, final SnomedRefSetType type) {
		this.uuid = checkNotNull(uuid, "Reference set member UUID may not be null.");
		this.type = checkNotNull(type, "Reference set type may not be null.");
		this.refSetId = checkNotNull(refSetId, "Reference set identifier may not be null.");
		this.changeKind = checkNotNull(changeKind, "Reference set member change kind may not be null.");
	}

	public String getUuid() {
		return uuid;
	}
	
	public MemberChangeKind getChangeKind() {
		return changeKind;
	}
	
	public String getRefSetId() {
		return refSetId;
	}
	
	public SnomedRefSetType getType() {
		return type;
	}

	@Override
	public int hashCode() {
		return Objects.hash(uuid, changeKind, refSetId, type);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		
		final RefSetMemberChange other = (RefSetMemberChange) obj;
		
		if (!Objects.equals(uuid, other.uuid)) return false;
		if (changeKind != other.changeKind) return false;
		if (refSetId != other.refSetId) return false;
		if (type != other.type) return false;
		
		return true;
	}
	
	@Override
	public int compareTo(final RefSetMemberChange o) {
		return changeKind.compareTo(o.changeKind);
	}
}
