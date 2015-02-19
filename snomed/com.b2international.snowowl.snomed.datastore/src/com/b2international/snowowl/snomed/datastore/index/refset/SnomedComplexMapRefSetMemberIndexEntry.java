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

import com.b2international.snowowl.snomed.SnomedConstants;
import com.b2international.snowowl.snomed.snomedrefset.SnomedComplexMapRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedMappingRefSet;
import com.google.common.base.Preconditions;

/**
 * Lightweight representation of the SNOMED CT complex map type reference set member.
 */
public class SnomedComplexMapRefSetMemberIndexEntry extends SnomedRefSetMemberIndexEntry implements Serializable {

	private static final long serialVersionUID = 7146609289640197325L;

	private int mapGroup;
	private int mapPriority;
	private String mapRule;
	private String mapAdvice;
	private String correlationId;
	private String mapCategoryId;

	/**
	 * Creates a new instance of this class based on the passed in complex map type reference set member.
	 * @param member the complex map type reference set member.
	 * @return the new lightweight representation of the SNOMED CT complex map type reference set member.
	 */
	public static SnomedComplexMapRefSetMemberIndexEntry create(final SnomedComplexMapRefSetMember member) {
		return create(member, null);
	}
	
	public static SnomedComplexMapRefSetMemberIndexEntry create(final SnomedComplexMapRefSetMember member, @Nullable final String label) {
		Preconditions.checkNotNull(member, "Reference set member argument cannot be null.");
		Preconditions.checkArgument(member.eContainer() instanceof SnomedMappingRefSet, "Container of passed in reference set member was not a " +
				"SNOMED CT mapping type reference set but " + member.eContainer().getClass().getName());
		Preconditions.checkArgument(!CDOState.NEW.equals(member.cdoState()), "Reference set member CDO state should not be NEW. " +
				"Use " + SnomedComplexMapRefSetMemberIndexEntry.class + ".createForNewMember(SnomedComplexMapRefSetMember) instead.");
		Preconditions.checkArgument(!CDOState.TRANSIENT.equals(member.cdoState()), "Reference set member CDO state should not be TRANSIENT. " +
				"Use " + SnomedComplexMapRefSetMemberIndexEntry.class + ".createForDetachedMember(SnomedComplexMapRefSetMember, SnomedMappingRefSet) instead");
		return new SnomedComplexMapRefSetMemberIndexEntry(member, label, tryGetIconId(member), CDOIDUtil.getLong(member.cdoID()), (SnomedMappingRefSet) member.eContainer());
	}
	
	/**
	 * Creates a new instance of this class based on the passed in unpersisted complex map type reference set member.
	 * <br><br><b>Note:</b> the member should be an unpersisted one. The CDO state should be org.eclipse.emf.cdo.CDOState.NEW.
	 * @param member the complex map type reference set member.
	 * @return the new lightweight representation of the SNOMED CT complex map type reference set member.
	 */
	public static SnomedComplexMapRefSetMemberIndexEntry createForNewMember(final SnomedComplexMapRefSetMember member) {
		return createForNewMember(member, null);
	}
	
	public static SnomedComplexMapRefSetMemberIndexEntry createForNewMember(final SnomedComplexMapRefSetMember member, @Nullable final String label) {
		
		Preconditions.checkNotNull(member, "Reference set member argument cannot be null.");
		Preconditions.checkArgument(member.eContainer() instanceof SnomedMappingRefSet, "Container of passed in reference set member was not a " +
				"SNOMED CT mapping type reference set but " + member.eContainer().getClass().getName());
		Preconditions.checkArgument(CDOState.NEW.equals(member.cdoState()), "Reference set member CDO state must be NEW.");
		return new SnomedComplexMapRefSetMemberIndexEntry(member, label, tryGetIconId(member), 0L, (SnomedMappingRefSet) member.eContainer());
		
	}
	
	public static SnomedComplexMapRefSetMemberIndexEntry createForDetachedMember(final SnomedComplexMapRefSetMember member, final SnomedMappingRefSet refSet) {
		return createForDetachedMember(member, refSet, null);
	}
	
	/**
	 * Creates a new instance of this class based on the passed in detached complex map type reference set member.
	 * <br><br><b>Note:</b> the member should be a detached one. The CDO state should be org.eclipse.emf.cdo.CDOState.TRANSIENT.
	 * @param member the complex map type reference set member. 
	 * @param refSet the reference set from where the member has been detached.
	 * @return the new lightweight representation of the SNOMED CT complex map type reference set member.
	 */
	public static SnomedComplexMapRefSetMemberIndexEntry createForDetachedMember(final SnomedComplexMapRefSetMember member, final SnomedMappingRefSet refSet, @Nullable final String label) {
		Preconditions.checkNotNull(member, "Reference set member argument cannot be null.");
		Preconditions.checkNotNull(refSet, "Container reference set argument cannot be null.");
		Preconditions.checkArgument(CDOState.TRANSIENT.equals(member.cdoState()), "Reference set member CDO state must be TRANSIENT.");
		return new SnomedComplexMapRefSetMemberIndexEntry(member, label, SnomedConstants.Concepts.ROOT_CONCEPT, 0L, refSet);
	}
	
	/**
	 * Creates an uninitialized instance.
	 */
	public SnomedComplexMapRefSetMemberIndexEntry(final SnomedRefSetMemberIndexEntry entry) { 
		super(entry);
	}

	/**
	 * Initialize the current instance of this lightweight representation of the SNOMED CT complex map type reference set.
	 * @param member the reference set member.
	 * @param iconId TODO
	 * @param cdoid the unique CDO ID if the object.
	 * @param refSet the container reference set.
	 */
	protected SnomedComplexMapRefSetMemberIndexEntry(final SnomedComplexMapRefSetMember member, final @Nullable String label, final String iconId, final long cdoid, final SnomedMappingRefSet refSet) {
		super(member, label, iconId, cdoid, refSet);
		mapGroup = member.getMapGroup();
		mapPriority = member.getMapPriority();
		mapRule = member.getMapRule();
		mapAdvice = member.getMapAdvice();
		correlationId = member.getCorrelationId();
		mapCategoryId = member.getMapCategoryId();
	}

	public int getMapGroup() {
		return mapGroup;
	}

	public void setMapGroup(final int mapGroup) {
		this.mapGroup = mapGroup;
	}

	public int getMapPriority() {
		return mapPriority;
	}

	public void setMapPriority(final int mapPriority) {
		this.mapPriority = mapPriority;
	}

	public String getMapRule() {
		return mapRule;
	}

	public void setMapRule(final String mapRule) {
		this.mapRule = mapRule;
	}

	public String getMapAdvice() {
		return mapAdvice;
	}

	public void setMapAdvice(final String mapAdvice) {
		this.mapAdvice = mapAdvice;
	}

	public String getCorrelationId() {
		return correlationId;
	}

	public void setCorrelationId(final String correlationId) {
		this.correlationId = correlationId;
	}
	
	public String getMapCategoryId() {
		return mapCategoryId;
	}

	public void setMapCategoryId(final String mapCategoryId) {
		this.mapCategoryId = mapCategoryId;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SnomedComplexMapRefSetMemberIndexEntry [uuid=" + getId()
				+ ", refComponentLabel=" + getLabel() + "]";
	}
	
}