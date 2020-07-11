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

import com.b2international.snowowl.core.request.MappingCorrelation;
import com.b2international.snowowl.core.uri.ComponentURI;
import com.google.common.base.MoreObjects;

/**
 * @since 7.8
 */
public final class SetMapping implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private final String sourceTerm;
	private final String sourceIconId;
	private final ComponentURI sourceComponentURI;
	
	private final String targetTerm;
	private final ComponentURI targetComponentURI;
	
	private final boolean isActive;
	private final MappingCorrelation mappingCorrelation;
	
	public SetMapping(ComponentURI sourceComponentURI, ComponentURI targetComponentURI, 
			String sourceTerm, String sourceIconId,
			String targetTerm, 
			boolean isActive,
			MappingCorrelation mappingCorrelation) {
		this.sourceComponentURI = sourceComponentURI;
		this.targetComponentURI = targetComponentURI;
		this.sourceTerm = sourceTerm;
		this.sourceIconId = sourceIconId;
		this.targetTerm = targetTerm;
		this.isActive = isActive;
		this.mappingCorrelation = mappingCorrelation;
	}

	public String getSourceIconId() {
		return sourceIconId;
	}

	public String getSourceTerm() {
		return sourceTerm;
	}

	public ComponentURI getSourceComponentURI() {
		return sourceComponentURI;
	}
	
	public ComponentURI getTargetComponentURI() {
		return targetComponentURI;
	}
	
	public String getTargetTerm() {
		return targetTerm;
	}
	
	public boolean isActive() {
		return isActive;
	}
	
	public MappingCorrelation getMappingCorrelation() {
		return mappingCorrelation;
	}
	
	@Override
	public String toString() {
		return MoreObjects.toStringHelper("SetMember")
				.add("sourceComponentURI", sourceComponentURI)
				.add("term", sourceTerm)
				.add("iconId", sourceIconId)
				.add("targetComponentURI", targetComponentURI)
				.add("targetTerm", targetTerm)
				.add("isActive", isActive)
				.add("mappingCorrelation", mappingCorrelation)
				.toString();
	}

	@Override
	public int hashCode() {
		return Objects.hash(sourceComponentURI, targetComponentURI, sourceTerm, targetTerm, sourceIconId, 
				isActive, mappingCorrelation);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		SetMapping other = (SetMapping) obj;
		return Objects.equals(sourceComponentURI, other.sourceComponentURI)
				&& Objects.equals(targetComponentURI, other.targetComponentURI)
				&& Objects.equals(sourceTerm, other.sourceTerm)
				&& Objects.equals(targetTerm, other.targetTerm)
				&& Objects.equals(sourceIconId, other.sourceIconId)
				&& Objects.equals(isActive, other.isActive)
				&& Objects.equals(mappingCorrelation, other.mappingCorrelation);
	}
}
