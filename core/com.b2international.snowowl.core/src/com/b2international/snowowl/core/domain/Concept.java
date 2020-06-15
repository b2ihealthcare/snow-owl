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
package com.b2international.snowowl.core.domain;

import java.util.SortedSet;

import com.b2international.snowowl.core.uri.ComponentURI;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @since 7.5
 */
public final class Concept extends BaseComponent {

	private static final long serialVersionUID = 1L;

	private final String codeSystem;
	private final short terminologyComponentId;
	
	private String term;
	private String iconId;
	private SortedSet<String> alternativeTerms;
	
	public Concept(String codeSystem, short terminologyComponentId) {
		this.codeSystem = codeSystem;
		this.terminologyComponentId = terminologyComponentId;
	}
	
	public String getCodeSystem() {
		return codeSystem;
	}
	
	public String getTerm() {
		return term;
	}
	
	public String getIconId() {
		return iconId;
	}
	
	public void setTerm(String term) {
		this.term = term;
	}
	
	public void setIconId(String iconId) {
		this.iconId = iconId;
	}
	
	public void setAlternativeTerms(SortedSet<String> alternativeTerms) {
		this.alternativeTerms = alternativeTerms;
	}
	
	public SortedSet<String> getAlternativeTerms() {
		return alternativeTerms;
	}
	
	@Override
	public short getTerminologyComponentId() {
		return terminologyComponentId;
	}

	@JsonIgnore
	public ComponentURI getCode() {
		return ComponentURI.of(codeSystem, terminologyComponentId, getId());
	}
	
}
