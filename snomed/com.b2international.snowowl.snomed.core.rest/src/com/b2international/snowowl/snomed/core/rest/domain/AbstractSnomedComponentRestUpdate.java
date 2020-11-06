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
package com.b2international.snowowl.snomed.core.rest.domain;

import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.snomed.core.domain.InactivationProperties;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;

/**
 * @since 4.0
 */
public abstract class AbstractSnomedComponentRestUpdate {

	private String moduleId;
	private Boolean active;
	private InactivationProperties inactivationProperties;
	private String effectiveTime;
	
	public Boolean isActive() {
		return active;
	}

	public void setActive(final Boolean active) {
		this.active = active;
	}

	public String getModuleId() {
		return moduleId;
	}

	public void setModuleId(final String moduleId) {
		this.moduleId = moduleId;
	}
	
	public InactivationProperties getInactivationProperties() {
		return inactivationProperties;
	}

	public void setInactivationProperties(InactivationProperties inactivationProperties) {
		this.inactivationProperties = inactivationProperties;
	}
	
	@JsonFormat(shape=Shape.STRING, pattern = DateFormats.SHORT, timezone="UTC")
	public void setEffectiveTime(String effectiveTime) {
		this.effectiveTime = effectiveTime;
	}
	
	public String getEffectiveTime() {
		return effectiveTime;
	}

}