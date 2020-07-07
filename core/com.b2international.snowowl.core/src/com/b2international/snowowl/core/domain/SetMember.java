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

import java.util.Objects;

import com.b2international.snowowl.core.uri.ComponentURI;

/**
 * @since 7.7
 */
public final class SetMember {

	private final ComponentURI referencedComponentURI;
	
	private final String term;
	private final String iconId;
	
	public SetMember(ComponentURI referencedComponentURI, String term, String iconId) {
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
