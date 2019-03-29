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
package com.b2international.snowowl.snomed.datastore;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;

import javax.annotation.Nullable;

import com.google.common.base.Equivalence;
import com.google.common.base.Equivalence.Wrapper;
import com.google.common.base.Function;
import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;

/**
 * Bare minimum representation of a SNOMED&nbsp;CT reference set member.
 *
 */
public class SnomedRefSetMemberFragment implements Serializable {

	private static final long serialVersionUID = -5568808221945622928L;

	/**Shared equivalence for {@link SnomedRefSetMemberFragment reference set member}s.*/
	public static final Equivalence<SnomedRefSetMemberFragment> EQUIVALENCE = new SnomedRefSetMemberFragmentEquivalence();
	
	/**Transforms a wrapper into a {@link SnomedRefSetMemberFragment}.*/
	public static final Function<Wrapper<SnomedRefSetMemberFragment>, SnomedRefSetMemberFragment> UNWRAP_FUNCTION = new WrapperToMemberFunction();
	
	/**Wraps a {@link SnomedRefSetMemberFragment}.*/
	public static final Function<SnomedRefSetMemberFragment, Wrapper<SnomedRefSetMemberFragment>> WRAP_FUNCTION = new MemberToWrapperFunction();
	
	private final String uuid;
	private final String sourceId;
	@Nullable private final String targetId;
	private final boolean active;

	public SnomedRefSetMemberFragment(final String uuid, final String sourceId, @Nullable final String targetId, final boolean active) {
		this.uuid = checkNotNull(uuid, "uuid");
		this.sourceId = checkNotNull(sourceId, "sourceId");
		this.targetId = targetId;
		this.active = active;
	}

	/**
	 * Returns with the UUID of the reference set member.
	 */
	public String getUuid() {
		return uuid;
	}

	/**
	 * Returns with the ID of the source/referenced component.
	 */
	public String getSourceId() {
		return sourceId;
	}

	/**
	 * Returns with the ID of the target component. Can be {@code null}.
	 */
	@Nullable public String getTargetId() {
		return targetId;
	}

	/**
	 * Returns {@code true} if the reference set member is active. Otherwise {@code false}.
	 */
	public boolean isActive() {
		return active;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final SnomedRefSetMemberFragment other = (SnomedRefSetMemberFragment) obj;
		if (uuid == null) {
			if (other.uuid != null)
				return false;
		} else if (!uuid.equals(other.uuid))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		final ToStringHelper toStringHelper = MoreObjects.toStringHelper(this).add("uuid", uuid).add("source ID", sourceId);
		if (null != targetId) {
			toStringHelper.add("target ID", targetId);
		}
		toStringHelper.add("active", active);
		return toStringHelper.toString();
	}
	
	private static final class WrapperToMemberFunction implements Function<Wrapper<SnomedRefSetMemberFragment>, SnomedRefSetMemberFragment>, Serializable {

		private static final long serialVersionUID = 2977258721405123637L;

		@Override
		public SnomedRefSetMemberFragment apply(final Wrapper<SnomedRefSetMemberFragment> wrapper) {
			return wrapper.get();
		}
		
		private WrapperToMemberFunction() { /*suppress instantiation*/ }
		
	}
	
	private static final class MemberToWrapperFunction implements Function<SnomedRefSetMemberFragment, Wrapper<SnomedRefSetMemberFragment>>, Serializable {

		private static final long serialVersionUID = -2424448794015013009L;

		@Override
		public Wrapper<SnomedRefSetMemberFragment> apply(final SnomedRefSetMemberFragment member) {
			return EQUIVALENCE.wrap(member);
		}
		
		private MemberToWrapperFunction() { /*suppress instantiation*/ }
		
	}
	
	/**
	 * Equivalence for {@link SnomedRefSetMemberFragment}s. Used for full depth property equivalence check.
	 *
	 */
	private static final class SnomedRefSetMemberFragmentEquivalence extends Equivalence<SnomedRefSetMemberFragment> implements Serializable {

		private static final long serialVersionUID = -5802175069450917315L;

		@Override
		protected boolean doEquivalent(final SnomedRefSetMemberFragment left, final SnomedRefSetMemberFragment right) {
			if (left.active != right.active)
				return false;
			if (left.sourceId == null) {
				if (right.sourceId != null)
					return false;
			} else if (!left.sourceId.equals(right.sourceId))
				return false;
			if (left.targetId == null) {
				if (right.targetId != null)
					return false;
			} else if (!left.targetId.equals(right.targetId))
				return false;
			if (left.uuid == null) {
				if (right.uuid != null)
					return false;
			} else if (!left.uuid.equals(right.uuid))
				return false;
			return true;
		}

		@Override
		protected int doHash(final SnomedRefSetMemberFragment member) {
			final int prime = 31;
			int result = 1;
			result = prime * result + (member.active ? 1231 : 1237);
			result = prime * result + ((member.sourceId == null) ? 0 : member.sourceId.hashCode());
			result = prime * result + ((member.targetId == null) ? 0 : member.targetId.hashCode());
			result = prime * result + ((member.uuid == null) ? 0 : member.uuid.hashCode());
			return result;
		}
		
		private SnomedRefSetMemberFragmentEquivalence() {
			//suppress instantiation
		}
		
	}
	
}