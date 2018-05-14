/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.core.domain.constraint;

import com.b2international.snowowl.core.domain.BaseComponent;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;

/**
 * Captures properties required for change tracking on individual components of the MRCM concept model.
 * 
 * @since 6.5
 */
public abstract class SnomedConceptModelComponent extends BaseComponent {

	private boolean active;
	private long effectiveTime;
	private String author;
	
	public boolean isActive() {
		return active;
	}
	
	public void setActive(boolean active) {
		this.active = active;
	}

	public long getEffectiveTime() {
		return effectiveTime;
	}
	
	public void setEffectiveTime(long effectiveTime) {
		this.effectiveTime = effectiveTime;
	}

	public String getAuthor() {
		return author;
	}
	
	public void setAuthor(String author) {
		this.author = author;
	}
	
	@Override
	public short getTerminologyComponentId() {
		// XXX: Returning the same component type ID for all parts
		return SnomedTerminologyComponentConstants.CONSTRAINT_NUMBER;
	}
}
