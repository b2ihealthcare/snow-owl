/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.index.entry;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A minimum representation of a SNOMED CT Description indexed and added to
 * {@link SnomedConceptDocument concept documents} to allow efficient term based
 * sorting and expand of PTs/FSNs for a given language reference set and acceptability. 
 */
public final class SnomedDescriptionFragment implements Serializable {

	private static final long serialVersionUID = 109221755912417375L;
	
	private final String id;
	private final String typeId;
	private final String term;
	private final String languageRefSetId;
	private final String acceptabilityId;
	
	@JsonCreator
	public SnomedDescriptionFragment(
			@JsonProperty("id") final String id, 
			@JsonProperty("typeId") final String typeId, 
			@JsonProperty("term") final String term, 
			@JsonProperty("languageRefSetId") final String languageRefSetId, 
			@JsonProperty("acceptabilityId") final String acceptabilityId) {
		this.id = id;
		this.typeId = typeId;
		this.languageRefSetId = languageRefSetId;
		this.acceptabilityId = acceptabilityId;
		this.term = term;
	}

	public String getId() {
		return id;
	}

	public String getTypeId() {
		return typeId;
	}

	public String getTerm() {
		return term;
	}

	public String getLanguageRefSetId() {
		return languageRefSetId;
	}

	public String getAcceptabilityId() {
		return acceptabilityId;
	}
	
}