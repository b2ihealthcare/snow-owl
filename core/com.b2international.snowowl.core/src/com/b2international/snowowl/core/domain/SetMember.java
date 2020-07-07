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

import java.io.Serializable;
import java.util.Objects;

import com.b2international.snowowl.core.uri.ComponentURI;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.base.MoreObjects;

/**
 * @since 7.7
 */
public final class SetMember implements Serializable {

	private static final long serialVersionUID = 1L;

	private final ComponentURI referencedComponentURI;
	
	private final String term;
	private final String iconId;
	
	@JsonCreator
	public SetMember(
			@JsonProperty("referencedComponentURI") ComponentURI referencedComponentURI,
			@JsonProperty("term") String term,
			@JsonProperty("iconId") String iconId) {
		this.referencedComponentURI = referencedComponentURI;
		this.term = term;
		this.iconId = iconId;
	}

	public String getIconId() {
		return iconId;
	}

	public String getTerm() {
		return term;
	}

	public ComponentURI getReferencedComponentURI() {
		return referencedComponentURI;
	}

	@JsonValue
	@Override
	public String toString() {
		return MoreObjects.toStringHelper("ConceptEditorState")
		        .omitNullValues()
				.add("referencedComponentURI", referencedComponentURI)
				.add("term", term)
				.add("iconId", iconId)
		        .toString();
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(referencedComponentURI, term, iconId);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		SetMember other = (SetMember) obj;
		return Objects.equals(referencedComponentURI, other.referencedComponentURI)
				&& term == other.term
				&& Objects.equals(iconId, other.iconId);
	}
}
