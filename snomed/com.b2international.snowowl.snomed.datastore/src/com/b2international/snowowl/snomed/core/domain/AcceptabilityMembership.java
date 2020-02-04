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
import java.util.Objects;

/**
 * @since 7.4
 */
public final class AcceptabilityMembership implements Comparable<AcceptabilityMembership>, Serializable {

	private SnomedConcept languageRefSet;
	private SnomedConcept acceptability;

	public AcceptabilityMembership() {
	}
	
	public AcceptabilityMembership(String languageRefSetId, String acceptabilityId) {
		setLanguageRefSet(new SnomedConcept(languageRefSetId));
		setAcceptability(new SnomedConcept(acceptabilityId));
	}
	
	public SnomedConcept getAcceptability() {
		return acceptability;
	}
	
	public void setAcceptability(SnomedConcept acceptability) {
		this.acceptability = acceptability;
	}

	public String getAcceptabilityId() {
		return acceptability == null ? null : acceptability.getId();
	}

	public SnomedConcept getLanguageRefSet() {
		return languageRefSet;
	}
	
	public void setLanguageRefSet(SnomedConcept languageRefSet) {
		this.languageRefSet = languageRefSet;
	}

	public String getLanguageRefSetId() {
		return languageRefSet == null ? null : languageRefSet.getId();
	}
	
	@Override
	public int compareTo(AcceptabilityMembership o) {
		return Objects.equals(getAcceptabilityId(), o.getAcceptabilityId()) ? 0 : getAcceptabilityId().compareTo(o.getAcceptabilityId());
	}

}
