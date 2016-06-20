/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore.server.snomed.merge;

import com.b2international.snowowl.core.merge.AbstractMergeConflict;

/**
 * @since 4.7
 */
public class SnomedMergeConflict extends AbstractMergeConflict {

	private static final long serialVersionUID = -180464497938303940L;
	
	private String componentId;
	
	public SnomedMergeConflict(final String componentId, final String message) {
		super(message);
		this.componentId = componentId;
	}
	
	/**
	 * @return the componentId
	 */
	public String getComponentId() {
		return componentId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((componentId == null) ? 0 : componentId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		SnomedMergeConflict other = (SnomedMergeConflict) obj;
		if (componentId == null) {
			if (other.componentId != null) {
				return false;
			}
		} else if (!componentId.equals(other.componentId)) {
			return false;
		}
		return true;
	}
	
}
