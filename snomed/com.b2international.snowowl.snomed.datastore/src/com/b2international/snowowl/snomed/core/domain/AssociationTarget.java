/*
 * Copyright 2020-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.io.Serializable;

/**
 * Represents a historical association to another concept in a historical association reference set. 
 * 
 * @since 7.4
 */
public final class AssociationTarget implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private SnomedConcept referenceSet;
	private SnomedCoreComponent targetComponent;

	public static final class Expand {
		public static final String TARGET_COMPONENT = "targetComponent";
	}
	
	public AssociationTarget() {
	}
	
	public AssociationTarget(String referenceSetId, String targetComponentId) {
		setReferenceSetId(referenceSetId);
		setTargetComponentId(targetComponentId);
	}
	
	public SnomedConcept getReferenceSet() {
		return referenceSet;
	}
	
	public String getReferenceSetId() {
		return referenceSet == null ? null : referenceSet.getId();
	}
	
	public SnomedCoreComponent getTargetComponent() {
		return targetComponent;
	}
	
	public String getTargetComponentId() {
		return targetComponent == null ? null : targetComponent.getId();
	}
	
	public void setReferenceSet(SnomedConcept referenceSet) {
		this.referenceSet = referenceSet;
	}
	
	public void setReferenceSetId(String referenceSetId) {
		setReferenceSet(new SnomedConcept(referenceSetId));
	}
	
	public void setTargetComponent(SnomedCoreComponent targetComponent) {
		this.targetComponent = targetComponent;
	}
	
	public void setTargetComponentId(String targetComponentId) {
		setTargetComponent(SnomedCoreComponent.create(targetComponentId));
	}

}
