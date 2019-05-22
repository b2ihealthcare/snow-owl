/*
 * Copyright 2011-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.core.domain;

import java.util.Date;

import com.b2international.snowowl.core.domain.BaseComponent;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Holds common properties of SNOMED CT components.
 * @since 4.0
 */
public abstract class SnomedComponent extends BaseComponent {

	/**
	 * @since 6.16
	 */
	public static abstract class Fields extends BaseComponent.Fields {
		
		public static final String ACTIVE = SnomedRf2Headers.FIELD_ACTIVE;
		public static final String EFFECTIVE_TIME = SnomedRf2Headers.FIELD_EFFECTIVE_TIME;
		public static final String MODULE_ID = SnomedRf2Headers.FIELD_MODULE_ID;
		public static final String ICON_ID = "iconId";
		
	} 
	
	private Boolean active;
	private Date effectiveTime;
	private String moduleId;
	private String iconId;
	private Float score;

	/**
	 * Returns the component's current status as a boolean value.
	 *  
	 * @return {@code true} if the component is active, {@code false} if it is inactive
	 */
	public Boolean isActive() {
		return active;
	}

	/**
	 * Returns the date at which the current state of the component becomes effective.
	 * 
	 * @return the component's effective time
	 */
	@JsonFormat(shape=Shape.STRING, pattern="yyyyMMdd")
	public Date getEffectiveTime() {
		return effectiveTime;
	}

	/**
	 * Returns the containing module's concept identifier.
	 * 
	 * @return the module identifier for the component
	 */
	public String getModuleId() {
		return moduleId;
	}

	/**
	 * @beta - this method is subject to changes or even removal in future releases.  
	 * @return - the icon ID associated with this component
	 */
	public String getIconId() {
		return iconId;
	}

	/**
	 * @beta - this method is subject to changes or even removal in future releases.
	 * @return - the score associated with this component if it's a match in a query, can be <code>null</code>
	 */
	@JsonIgnore
	public Float getScore() {
		return score;
	}

	public void setActive(final Boolean active) {
		this.active = active;
	}

	@JsonFormat(shape=Shape.STRING, pattern="yyyyMMdd")
	public void setEffectiveTime(final Date effectiveTime) {
		this.effectiveTime = effectiveTime;
	}

	public void setModuleId(final String moduleId) {
		this.moduleId = moduleId;
	}
	
	public void setIconId(String iconId) {
		this.iconId = iconId;
	}
	
	public void setScore(Float score) {
		this.score = score;
	}
	
	/**
	 * Creates an update {@link Request} to update the component to the state represented by this instance.
	 * @return
	 */
	public abstract Request<TransactionContext, Boolean> toUpdateRequest();
	
	/**
	 * Creates a create {@link Request} to create the component represented by this instance.
	 * @return
	 */
	public final Request<TransactionContext, String> toCreateRequest() {
		return toCreateRequest(null);
	}
	
	/**
	 * Creates a create {@link Request} to create the component represented by this instance.
	 * @param containerId the container component identifier to enforce attachment to it, may be <code>null</code> 
	 * @return
	 */
	public abstract Request<TransactionContext, String> toCreateRequest(String containerId);
	
}