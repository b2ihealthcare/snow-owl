/*
 * Copyright 2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
import java.util.List;

/**
 * @since 7.4
 */
public final class InactivationProperties implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private SnomedConcept inactivationIndicator;
	private List<AssociationTarget> associationTargets;
	
	public InactivationProperties() {
	}
	
	public InactivationProperties(String inactivationIndicatorId, List<AssociationTarget> associationTargets) {
		setInactivationIndicatorId(inactivationIndicatorId);
		setAssociationTargets(associationTargets);
	}
	
	/**
	 * Returns the component's corresponding active inactivation indicator member value. In case of multiple values present in the corresponding
	 * inactivation indicator reference set this value will return the first active occurrence from that refset.
	 * 
	 * @return the inactivation indicator value, or {@code null} if the component does not have an inactivation indicator
	 */
	public SnomedConcept getInactivationIndicator() {
		return inactivationIndicator;
	}
	
	public String getInactivationIndicatorId() {
		return inactivationIndicator == null ? null : inactivationIndicator.getId();
	}
	
	public void setInactivationIndicator(SnomedConcept inactivationIndicator) {
		this.inactivationIndicator = inactivationIndicator;
	}
	
	public void setInactivationIndicatorId(String inactivationIndicatorId) {
		setInactivationIndicator(inactivationIndicatorId == null ? null : new SnomedConcept(inactivationIndicatorId));
	}
	
	/**
	 * Returns all active association targets.
	 * 
	 * @return related association targets, or empty {@link List} if there are no association targets present for this component.
	 */
	public List<AssociationTarget> getAssociationTargets() {
		return associationTargets;
	}
	
	public void setAssociationTargets(List<AssociationTarget> associationTargets) {
		this.associationTargets = associationTargets;
	}
	
}
