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
package com.b2international.snowowl.snomed.core.domain.refset;

import java.util.Objects;

/**
 * @since 4.5
 */
public class MemberChangeImpl implements MemberChange {

	private final MemberChangeKind changeKind;
	private final String referencedComponentId;
	private final String memberId;

	private MemberChangeImpl(MemberChangeKind changeKind, String referencedComponentId, String memberId) {
		this.changeKind = changeKind;
		this.referencedComponentId = referencedComponentId;
		this.memberId = memberId;
	}
	
	@Override
	public MemberChangeKind getChangeKind() {
		return changeKind;
	}

	@Override
	public String getReferencedComponentId() {
		return referencedComponentId;
	}

	@Override
	public String getMemberId() {
		return memberId;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(changeKind, referencedComponentId);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MemberChangeImpl other = (MemberChangeImpl) obj;
		return Objects.equals(changeKind, other.changeKind) && Objects.equals(referencedComponentId, other.referencedComponentId);
	}
	
	@Override
	public int compareTo(MemberChange o) {
		int diff = changeKind.compareTo(o.getChangeKind());

		// TODO alphabetical ordering if label provided???
//		if (diff == 0) {
//			diff = diff1.getLabel().compareTo(diff2.getLabel());
//		}

		if (diff == 0) {
			diff = referencedComponentId.compareTo(o.getReferencedComponentId());
		}
		return diff;
	}
	
	public static MemberChange added(String referencedComponentId) {
		return new MemberChangeImpl(MemberChangeKind.ADD, referencedComponentId, null);
	}
	
	public static MemberChange removed(String referencedComponentId, String memberId) {
		return new MemberChangeImpl(MemberChangeKind.REMOVE, referencedComponentId, memberId);
	}

}
