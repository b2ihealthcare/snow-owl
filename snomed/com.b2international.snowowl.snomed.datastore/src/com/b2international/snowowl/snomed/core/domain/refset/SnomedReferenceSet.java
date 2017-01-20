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

import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.snomed.core.domain.SnomedComponent;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;

/**
 * @since 4.5
 */
public final class SnomedReferenceSet extends SnomedComponent {

	private SnomedRefSetType type;
	private String referencedComponentType;
	private String mapTargetComponentType;
	private SnomedReferenceSetMembers members;

	/**
	 * Returns the type of the reference set.
	 * 
	 * @return
	 */
	public SnomedRefSetType getType() {
		return type;
	}

	/**
	 * Returns the type of the referenced component.
	 * 
	 * @return
	 */
	public String getReferencedComponentType() {
		return referencedComponentType;
	}

	/**
	 * Returns the type of the map target if this reference set is a mapping reference set.
	 * @return
	 */
	public String getMapTargetComponentType() {
		return mapTargetComponentType;
	}

	/**
	 * Returns all members of the reference set.
	 * 
	 * @return
	 */
	public SnomedReferenceSetMembers getMembers() {
		return members;
	}
	
	public void setReferencedComponentType(String referencedComponent) {
		this.referencedComponentType = referencedComponent;
	}
	
	public void setMapTargetComponentType(String mapTargetComponentType) {
		this.mapTargetComponentType = mapTargetComponentType;
	}
	
	public void setType(SnomedRefSetType type) {
		this.type = type;
	}
	
	public void setMembers(SnomedReferenceSetMembers members) {
		this.members = members;
	}
	
	@Override
	public Request<TransactionContext, String> toCreateRequest(String containerId) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public Request<TransactionContext, Boolean> toUpdateRequest() {
		throw new UnsupportedOperationException("Reference sets does not support update operation yet");
	}

}
