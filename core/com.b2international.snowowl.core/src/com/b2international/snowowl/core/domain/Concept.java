/*
 * Copyright 2020-2024 B2i Healthcare, https://b2ihealthcare.com
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

import java.util.Collection;
import java.util.List;
import java.util.SortedSet;

import com.b2international.commons.collections.Collections3;
import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.uri.ComponentURI;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

/**
 * @since 7.5
 */
@JsonIgnoreProperties(ignoreUnknown = true) // required to allow backward compatibility
public final class Concept extends BaseComponent {

	private static final long serialVersionUID = 2L;

	private final ResourceURI codeSystemUri;
	private final String componentType;
	
	private Boolean active;
	private String term;
	private String iconId;
	private SortedSet<Description> descriptions;
	
	private List<String> parentIds;
	private List<String> ancestorIds;
	
	private Float score;
	
	private Object internalConcept;
	
	@JsonCreator
	public Concept(
			@JsonProperty("code") ComponentURI code, 
			@JsonProperty("active") Boolean active, 
			@JsonProperty("term") String term, 
			@JsonProperty("descriptions") SortedSet<Description> descriptions, 
			@JsonProperty("iconId") String iconId, 
			@JsonProperty("parentIds") List<String> parentIds, 
			@JsonProperty("ancestorIds") List<String> ancestorIds,
			@JsonProperty("score") Float score) {
		this(code.resourceUri(), code.componentType());
		setId(code.identifier());
		setActive(active);
		setTerm(term);
		setDescriptions(descriptions);
		setIconId(iconId);
		setScore(score);
		setParentIds(parentIds);
		setAncestorIds(ancestorIds);
	}
	
	public Concept(ResourceURI codeSystemUri, String componentType) {
		this.codeSystemUri = codeSystemUri;
		this.componentType = componentType;
	}
	
	public Boolean isActive() {
		return active;
	}
	
	public ResourceURI getCodeSystem() {
		return codeSystemUri;
	}
	
	public String getTerm() {
		return term;
	}
	
	public String getIconId() {
		return iconId;
	}
	
	public void setActive(Boolean active) {
		this.active = active;
	}
	
	public void setTerm(String term) {
		this.term = term;
	}
	
	public void setIconId(String iconId) {
		this.iconId = iconId;
	}
	
	/**
	 * @param alternativeTerms
	 * @deprecated deserialization only: if descriptions property is set via the constructor during deserialization then this ignores the incoming value, use {@link #setDescriptions(Collection)} instead
	 */
	@JsonProperty
	public void setAlternativeTerms(Collection<Description> alternativeTerms) {
		if (descriptions == null) {
			setDescriptions(alternativeTerms);
		}
	}
	
	public void setDescriptions(Collection<Description> descriptions) {
		this.descriptions = descriptions == null ? null : Collections3.toImmutableSortedSet(descriptions);
	}
	
	public void setScore(Float score) {
		this.score = score;
	}
	
	/**
	 * @return optionally set alternative terms for this concept, may be <code>null</code> if there are not alternative terms present for this code
	 * @deprecated serialization only for backward compatibility, use {@link #getDescriptions()} instead  
	 */
	public SortedSet<Description> getAlternativeTerms() {
		return getDescriptions();
	}
	
	public SortedSet<Description> getDescriptions() {
		return descriptions;
	}
	
	public Float getScore() {
		return score;
	}
	
	public List<String> getParentIds() {
		return parentIds;
	}
	
	public void setParentIds(List<String> parentIds) {
		this.parentIds = parentIds;
	}
	
	public List<String> getAncestorIds() {
		return ancestorIds;
	}
	
	public void setAncestorIds(List<String> ancestorIds) {
		this.ancestorIds = ancestorIds;
	}

	@JsonIgnore
	public void setInternalConcept(Object internalConcept) {
		this.internalConcept = internalConcept;
	}
	
	@JsonIgnore
	public Object getInternalConcept() {
		return internalConcept;
	}
	
	@JsonIgnore
	@SuppressWarnings("unchecked")
	public <T> T getInternalConceptAs() {
		return (T) internalConcept;
	}
	
	@JsonIgnore
	@Override
	public String getComponentType() {
		return componentType;
	}

	public ComponentURI getCode() {
		return ComponentURI.of(codeSystemUri, componentType, getId());
	}
	
	@Override
	public String toString() {
		return toConceptString(getId(), getTerm());
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
		if (Strings.isNullOrEmpty(term) || id.equals(term)) {
			return id;
		} else {
			return String.format("%s|%s|", id, term);
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
