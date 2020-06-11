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

import com.b2international.snowowl.core.uri.ComponentURI;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 7.7
 */
public final class SetMember extends BaseComponent {

	private static final long serialVersionUID = 1L;

	private ComponentURI referencedComponentURI;
	
	private final String sourceTerm;
	private final String iconId;
	private final short terminologyComponentId;
	
    @JsonCreator
	public SetMember(
			@JsonProperty("referencedComponentURI") ComponentURI referencedComponentURI,
			@JsonProperty("terminologyComponentId") short terminologyComponentId,
			@JsonProperty("term") String term,
			@JsonProperty("iconId") String iconId) {
		this.referencedComponentURI = referencedComponentURI;
		this.terminologyComponentId = terminologyComponentId;
		this.sourceTerm = term;
		this.iconId = iconId;
	}
	
	@Override
	public short getTerminologyComponentId() {
		return terminologyComponentId;
	}

	public String getIconId() {
		return iconId;
	}

	public String getSourceTerm() {
		return sourceTerm;
	}

	public ComponentURI getReferencedComponentURI() {
		return referencedComponentURI;
	}

}
