/*
 * Copyright 2011-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Objects;

import com.b2international.snowowl.snomed.core.domain.SnomedConcept;

/**
 * @since 4.5
 */
public class MemberChangeImpl implements MemberChange {

	private final MemberChangeKind changeKind;
	private final SnomedConcept referencedComponent;
	private final String memberId;

	private MemberChangeImpl(MemberChangeKind changeKind, SnomedConcept referencedComponent, String memberId) {
		this.changeKind = changeKind;
		this.referencedComponent = checkNotNull(referencedComponent);
		this.memberId = memberId;
	}
	
	@Override
	public MemberChangeKind getChangeKind() {
		return changeKind;
	}

	@Override
	public SnomedConcept getReferencedComponent() {
		return referencedComponent;
	}

	@Override
	public String getMemberId() {
		return memberId;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(changeKind, referencedComponent.getId());
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
		return Objects.equals(changeKind, other.changeKind) && Objects.equals(referencedComponent.getId(), other.referencedComponent.getId());
	}
	
	@Override
	public int compareTo(MemberChange o) {
		int diff = changeKind.compareTo(o.getChangeKind());

		if (diff == 0 && referencedComponent.getPt() != null && o.getReferencedComponent().getPt() != null) {
			diff = referencedComponent.getPt().getTerm().compareTo(o.getReferencedComponent().getPt().getTerm());
		}

		if (diff == 0) {
			diff = referencedComponent.getId().compareTo(o.getReferencedComponent().getId());
		}
		return diff;
	}
	
	public static MemberChange added(SnomedConcept referencedComponent) {
		return new MemberChangeImpl(MemberChangeKind.ADD, referencedComponent, null);
	}
	
	public static MemberChange removed(SnomedConcept referencedComponent, String memberId) {
		return new MemberChangeImpl(MemberChangeKind.REMOVE, referencedComponent, memberId);
	}
	
	public static MemberChange changed(SnomedConcept referencedComponent, String memberId) {
		return new MemberChangeImpl(MemberChangeKind.CHANGE, referencedComponent, memberId);
	}
	
}
