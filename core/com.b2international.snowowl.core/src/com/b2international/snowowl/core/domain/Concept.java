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

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.b2international.snowowl.core.uri.ComponentURI;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

/**
 * @since 7.5
 */
public final class Concept extends BaseComponent {

	private static final long serialVersionUID = 1L;

	private final String codeSystem;
	private final short terminologyComponentId;
	
	private String preferredTerm;
	private String iconId;
	private List<Description> alternativeDescriptions;
	
	public Concept(String codeSystem, short terminologyComponentId) {
		this.codeSystem = codeSystem;
		this.terminologyComponentId = terminologyComponentId;
	}
	
	public String getCodeSystem() {
		return codeSystem;
	}
	
	public String getPreferredTerm() {
		return preferredTerm;
	}
	
	public String getIconId() {
		return iconId;
	}
	
	public void setPreferredTerm(String preferredTerm) {
		this.preferredTerm = preferredTerm;
	}
	
	public void setIconId(String iconId) {
		this.iconId = iconId;
	}
	
	public void setAlternativeDescriptions(List<Description> alternativeTerms) {
		this.alternativeDescriptions = alternativeTerms;
	}
	
	public List<Description> getAlternativeDescriptions() {
		return alternativeDescriptions;
	}
	
	public Set<String> getAlternativeTerms() {
		return alternativeDescriptions.stream().map(Description::getTerm).collect(Collectors.toSet());
	}
	
	@Override
	public short getTerminologyComponentId() {
		return terminologyComponentId;
	}

	@JsonIgnore
	public ComponentURI getCode() {
		return ComponentURI.of(codeSystem, terminologyComponentId, getId());
	}
	
	@Override
	public String toString() {
		return toConceptString(getId(), getPreferredTerm());
	}
	
	/**
	 * Creates an "ID |TERM|" String representation from the given id and term values. Returns just the ID if the given term is <code>null</code> or empty.
	 * 
	 * @param id - may not be <code>null</code> or empty
	 * @param term - may be <code>null</code>
	 * @return either the given id or a String value in the format of ID |TERM| if the term is not <code>null</code> or empty. 
	 */
	public static final String toConceptString(String id, String term) {
		Preconditions.checkArgument(!Strings.isNullOrEmpty(id), "ID may not be null or empty");
		if (Strings.isNullOrEmpty(term)) {
			return id;
		} else {
			return String.format("%s |%s|", id, term);
		}
	}
	
	/**
	 * Extracts an ID TERM pair from a String formatted like ID|TERM|. If there is no TERM specified then sets the ID for both the ID and TERM positions.
	 * ID is placed on index 0, while TERM is placed on index 1 in the resulting array.
	 *  
	 * @param conceptString
	 * @return never <code>null</code> String array with length of two, 0:ID, 1:TERM array.
	 */
	public static final String[] fromConceptString(String conceptString) {
		if (Strings.isNullOrEmpty(conceptString)) return new String[] {"", ""};
		final int firstPipeIdx = conceptString.indexOf("|");
		final int lastPipeIdx = conceptString.lastIndexOf("|");
		return new String[] {
			conceptString.substring(0, firstPipeIdx == -1 ? conceptString.length() : firstPipeIdx).trim(),
			conceptString.substring(firstPipeIdx + 1, lastPipeIdx == -1 ? conceptString.length() : lastPipeIdx).trim(),
		};
	}
	
}
