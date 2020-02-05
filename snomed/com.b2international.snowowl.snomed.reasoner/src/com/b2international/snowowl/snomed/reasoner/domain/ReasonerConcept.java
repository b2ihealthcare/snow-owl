/*
 * Copyright 2019-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.reasoner.domain;

import java.io.Serializable;

import com.b2international.snowowl.snomed.core.domain.SnomedDescription;

/**
 * @since 6.14
 */
public final class ReasonerConcept implements Serializable {

	private String originConceptId;
	private Boolean released;

	/*
	 * Values below will match the origin concept ID, as they will only appear as
	 * "removed" changes.
	 */
	private String definitionStatusId;
	private SnomedDescription fsn;
	private SnomedDescription pt;

	// Default constructor is used in JSON de-serialization
	public ReasonerConcept() { }

	public ReasonerConcept(final String originConceptId) {
		setOriginConceptId(originConceptId);
	}

	public String getOriginConceptId() {
		return originConceptId;
	}

	private void setOriginConceptId(final String originConceptId) {
		this.originConceptId = originConceptId;
	}

	public Boolean isReleased() {
		return released;
	}

	public void setReleased(final Boolean released) {
		this.released = released;
	}

	public String getDefinitionStatus() {
		return definitionStatusId;
	}

	public void setDefinitionStatusId(final String definitionStatusId) {
		this.definitionStatusId = definitionStatusId;
	}

	public SnomedDescription getPt() {
		return pt;
	}

	public void setPt(final SnomedDescription pt) {
		this.pt = pt;
	}
	
	public SnomedDescription getFsn() {
		return fsn;
	}
	
	public void setFsn(final SnomedDescription fsn) {
		this.fsn = fsn;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("ReasonerConcept [originConceptId=");
		builder.append(originConceptId);
		builder.append(", released=");
		builder.append(released);
		builder.append(", definitionStatusId=");
		builder.append(definitionStatusId);
		builder.append(", fsn=");
		builder.append(fsn);
		builder.append(", pt=");
		builder.append(pt);
		builder.append("]");
		return builder.toString();
	}
}
