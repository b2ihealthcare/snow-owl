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

import java.io.Serializable;

import javax.annotation.Nullable;

import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;

import com.b2international.snowowl.snomed.snomedrefset.SnomedDescriptionTypeRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSet;
import com.google.common.base.Preconditions;

/**
 * Lightweight representation of a SNOMED CT description type reference set member.
 * 
 */
public class SnomedDescriptionTypeRefSetMemberIndexEntry extends SnomedRefSetMemberIndexEntry implements Serializable {

	private static final long serialVersionUID = 8358682019541941129L;
	
	private int descriptionLength;
	
	public SnomedDescriptionTypeRefSetMemberIndexEntry(final SnomedRefSetMemberIndexEntry entry) {
		super(entry);
	}


	/**
	 * Initialize the current instance of this lightweight representation of the SNOMED CT complex map type reference set.
	 * @param member the reference set member.
	 * @param iconId TODO
	 * @param cdoid the unique CDO ID if the object.
	 * @param refSet the container reference set.
	 */
	protected SnomedDescriptionTypeRefSetMemberIndexEntry(final SnomedDescriptionTypeRefSetMember member, final @Nullable String label, String iconId, final long cdoid, final SnomedRefSet refSet) {
		super(member, label, iconId, cdoid, refSet);
		
		descriptionLength = member.getDescriptionLength();
	}
	
	public static SnomedDescriptionTypeRefSetMemberIndexEntry createForNewMember(final SnomedDescriptionTypeRefSetMember member) {
		return createForNewMember(member, null);
	}
	
	public static SnomedDescriptionTypeRefSetMemberIndexEntry createForNewMember(final SnomedDescriptionTypeRefSetMember member, @Nullable final String label) {
		Preconditions.checkNotNull(member, "Reference set member argument cannot be null.");
		Preconditions.checkArgument(member.eContainer() instanceof SnomedRefSet, "Container of passed in reference set member was not a " +
				"SNOMED CT reference set but " + member.eContainer().getClass().getName());
		Preconditions.checkArgument(CDOState.NEW.equals(member.cdoState()), "Reference set member CDO state must be NEW.");
		
		return new SnomedDescriptionTypeRefSetMemberIndexEntry(member, label, null, 0L, (SnomedRefSet) member.eContainer());
	}
	
	public static SnomedDescriptionTypeRefSetMemberIndexEntry createForDetachedMember(SnomedDescriptionTypeRefSetMember member, SnomedRefSet refSet) {
		return createForDetachedMember(member, refSet, null);
	}
	
	public static SnomedDescriptionTypeRefSetMemberIndexEntry createForDetachedMember(SnomedDescriptionTypeRefSetMember member, SnomedRefSet refSet, @Nullable final String label) {
		Preconditions.checkNotNull(member, "Reference set member argument cannot be null.");
		Preconditions.checkNotNull(refSet, "Container reference set argument cannot be null.");
		Preconditions.checkArgument(CDOState.TRANSIENT.equals(member.cdoState()), "Reference set member CDO state must be TRANSIENT.");
		
		return new SnomedDescriptionTypeRefSetMemberIndexEntry(member, label, null, 0L, refSet);
	}
	public static SnomedDescriptionTypeRefSetMemberIndexEntry create(final SnomedDescriptionTypeRefSetMember member) {
		return create(member, null);
	}
	
	public static SnomedDescriptionTypeRefSetMemberIndexEntry create(final SnomedDescriptionTypeRefSetMember member, @Nullable final String label) {
		Preconditions.checkNotNull(member, "Reference set member argument cannot be null.");
		Preconditions.checkArgument(member.eContainer() instanceof SnomedRefSet, "Container of passed in reference set member was not a " +
				"SNOMED CT reference set but " + member.eContainer().getClass().getName());
		Preconditions.checkArgument(!CDOState.NEW.equals(member.cdoState()), "Reference set member CDO state should not be NEW. " +
				"Use com.b2international.snowowl.snomed.datastore.index.refset.SnomedRefSetLuceneIndexDTO.createForNewMember(SnomedRefSetMember<?>) instead.");
		Preconditions.checkArgument(!CDOState.TRANSIENT.equals(member.cdoState()), "Reference set member CDO state should not be TRANSIENT. " +
				"Use com.b2international.snowowl.snomed.datastore.index.refset.SnomedRefSetLuceneIndexDTO.createForDetachedMember(SnomedRefSetMember<?>, SnomedRefSet<?>) insead");
		return new SnomedDescriptionTypeRefSetMemberIndexEntry(member, label, null, CDOIDUtil.getLong(member.cdoID()), (SnomedRefSet) member.eContainer());
	}
	
	public int getDescriptionLength() {
		return descriptionLength;
	}
	
	public void setDescriptionLength(int desccriptionLength) {
		this.descriptionLength = desccriptionLength;
	}
	
	@Override
	public String toString() {
		return "SnomedDescriptionTypeRefSetMemberIndexEntry [uuid=" + getId()
				+ ", refComponentLabel=" + getLabel() + "]";
	}
}