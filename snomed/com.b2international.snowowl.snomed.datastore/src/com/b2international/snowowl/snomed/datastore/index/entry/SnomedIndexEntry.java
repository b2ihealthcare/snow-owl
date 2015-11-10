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
package com.b2international.snowowl.snomed.datastore.index;

import java.io.Serializable;

import com.b2international.snowowl.core.api.IComponent;
import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.datastore.index.AbstractIndexEntry;
import com.google.common.base.Preconditions;

/**
 * Abstract representation of a SNOMED&nbsp;CT component.
 * @see IComponent
 * @see AbstractIndexEntry
 */
public abstract class SnomedIndexEntry extends AbstractIndexEntry implements IComponent<String>, Serializable {

	private static final long serialVersionUID = 1158021444792053062L;

	/**Flag indicating whether the component is published or not.*/
	private final boolean released;
	/**Flag indicating whether the component is active or retired.*/
	private final boolean active;
	/**SNOMED&nbsp;CT concept ID of the module.*/
	private final String moduleId;
	
	/**Effective time is the point in time when the current SNOMED&nbsp;CT component has been modified.<br> 
	 *Could be {@link EffectiveTimes#UNSET_EFFECTIVE_TIME} if not set.*/
	private long effectiveTimeLong;
	/**Human readable representation of the effective time of the component.*/
	private String effectiveTime;
	
	/**
	 * Creates a new index entry representing a SNOMED&nbsp;CT component. 
	 * @param id the unique identifier of the component.
	 * @param label the humane readable name of the component.
	 * @param iconId the concept ID for the icon.
	 * @param score the index scoring.
	 * @param storageKey the primary unique database key of the component.
	 * @param released flag indicating whether the component is published or not.
	 */
	public SnomedIndexEntry(final String id, final String label, String iconId, final String moduleId, final float score, final long storageKey, final boolean released, final boolean active, final long effectiveTimeLong) {
		super(id, label, iconId, score, storageKey);
		this.moduleId = Preconditions.checkNotNull(moduleId, "SNOMED CT module concept ID argument cannot be null");
		this.released = released;
		this.active = active;
		setEffectiveTime(effectiveTimeLong);
	}

	/**
	 * Returns {@code true} if the represented SNOMED&nbsp;CT component is already published. Otherwise returns {@code false}. 
	 * @return {@code true} if the component is released. Otherwise returns with {@code false}.
	 */
	public boolean isReleased() {
		return released;
	}
	
	/**
	 * Returns {@code true} if the SNOMED&nbsp;CT component has active status. It returns with {@code false} if the component has been retired.
	 * @return {@code true} if the component is active, otherwise returns with {@code false}.
	 */
	public boolean isActive() {
		return active;
	}
	
	/**
	 * Returns with the ID of the SNOMED&nbsp;CT module concept.
	 * @return the concept ID of the module.
	 */
	public String getModuleId() {
		return moduleId;
	}
	
	/**Returns with the effective time of the component in {@link DateFormats#DEFAULT} format.
	 * May return with {@link EffectiveTimes#UNSET_EFFECTIVE_TIME_LABEL} if the effective time is not set.
	 * @return the effective time of the current component.*/
	public String getEffectiveTime() {
		return effectiveTime;
	}
	
	/***
	 * Returns with the effective time of the current component. May return with {@link EffectiveTimes#UNSET_EFFECTIVE_TIME}
	 * if the component is unpublished.
	 * @return the effective time as long.
	 */
	public long getEffectiveTimeAsLong() {
		return effectiveTimeLong;
	}

	/**
	 * Sets the effective time of this entry to the specified number of elapsed milliseconds since the Unix epoch in UTC.
	 * <p>
	 * Note that the actual component represented by this index entry will not be changed as a result of this call.
	 * 
	 * @param effectiveTimeLong the effective time in number of milliseconds, or {@link EffectiveTimes#UNSET_EFFECTIVE_TIME}
	 */
	public void setEffectiveTime(final long effectiveTimeLong) {
		this.effectiveTimeLong = effectiveTimeLong;
		this.effectiveTime = EffectiveTimes.format(effectiveTimeLong);
	}
}