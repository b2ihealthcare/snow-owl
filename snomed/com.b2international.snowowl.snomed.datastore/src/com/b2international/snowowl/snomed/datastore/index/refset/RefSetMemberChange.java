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

import java.util.Objects;

import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.google.common.base.Preconditions;

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
	
	private final long refSetId;
	private final MemberChangeKind changeKind;
	private final SnomedRefSetType type;
	
	public RefSetMemberChange(final long refSetId, final MemberChangeKind changeKind, final SnomedRefSetType type) {
		this.type = Preconditions.checkNotNull(type, "SNOMED CT reference set type argument cannot be null.");
		this.refSetId = Preconditions.checkNotNull(refSetId, "SNOMED CT reference set identifier concept ID argument cannot be null.");
		this.changeKind = Preconditions.checkNotNull(changeKind, "Reference set member change king argument cannot be null.");
		Preconditions.checkState(isValidType(type), "Unsupported reference set type: " + type);
	}

	public static boolean isValidType(final SnomedRefSetType type) {
		return SnomedRefSetType.SIMPLE.equals(type)
		|| SnomedRefSetType.ATTRIBUTE_VALUE.equals(type)
		|| SnomedRefSetType.SIMPLE_MAP.equals(type);
	}
	
	public MemberChangeKind getChangeKind() {
		return changeKind;
	}
	
	public long getRefSetId() {
		return refSetId;
	}
	
	public SnomedRefSetType getType() {
		return type;
	}

	@Override
	public int hashCode() {
		return Objects.hash(changeKind, refSetId, type);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		RefSetMemberChange other = (RefSetMemberChange) obj;
		if (changeKind != other.changeKind)
			return false;
		if (refSetId != other.refSetId)
			return false;
		if (type != other.type)
			return false;
		return true;
	}
	
	@Override
	public int compareTo(final RefSetMemberChange o) {
		return changeKind.compareTo(o.changeKind);
	}
	
}