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
	
	public static Builder builder() {
		return new Builder();
	}
	
	public static class Builder {
		
		private String sourceIconId;
		private String sourceTerm;
		private ComponentURI sourceComponentURI;
		
		private String targetTerm;
		private ComponentURI targetComponentURI;
		
		private boolean isActive;
		private MappingCorrelation mappingCorrelation = MappingCorrelation.NOT_SPECIFIED;
		
		private int mapGroup = 0;
		private int mapPriority = 0;
		
		public Builder sourceTerm(final String sourceTerm) {
			this.sourceTerm = sourceTerm;
			return this;
		}
		
		public Builder sourceIconId(final String sourceIconId) {
			this.sourceIconId = sourceIconId;
			return this;
		}
		
		public Builder sourceComponentURI(final ComponentURI sourceComponentURI) {
			this.sourceComponentURI = sourceComponentURI;
			return this;
		}
		
		public Builder targetTerm(final String targetTerm) {
			this.targetTerm = targetTerm;
			return this;
		}
		
		public Builder targetComponentURI(final ComponentURI targetComponentURI) {
			this.targetComponentURI = targetComponentURI;
			return this;
		}
		
		public Builder active(final boolean isActive) {
			this.isActive = isActive;
			return this;
		}
		
		public Builder mappingCorrelation(final MappingCorrelation mappingCorrelation) {
			this.mappingCorrelation = mappingCorrelation;
			return this;
		}
		
		public Builder mapGroup(final int mapGroup) {
			this.mapGroup = mapGroup;
			return this;
		}
		
		public Builder mapPriority(final int mapPriority) {
			this.mapPriority = mapPriority;
			return this;
		}
		
		public SetMapping build() {
			return new SetMapping(sourceIconId, sourceTerm, sourceComponentURI, 
					targetTerm, targetComponentURI, isActive, mappingCorrelation, mapGroup, mapPriority);
		}
	
	}
	
	private final String sourceIconId;
	private final String sourceTerm;
	private final ComponentURI sourceComponentURI;
	
	private final String targetTerm;
	private final ComponentURI targetComponentURI;
	
	private final boolean isActive;
	private final MappingCorrelation mappingCorrelation;
	
	private int mapGroup;
	private int mapPriority;
	
	SetMapping(String sourceIconId, 
			String sourceTerm,
			ComponentURI sourceComponentURI, 
			String targetTerm, 
			ComponentURI targetComponentURI, 
			boolean isActive,
			MappingCorrelation mappingCorrelation,
			int mapGroup,
			int mapPriority) {
		
		this.sourceIconId = sourceIconId;
		this.sourceTerm = sourceTerm;
		this.sourceComponentURI = sourceComponentURI;
		this.targetTerm = targetTerm;
		this.targetComponentURI = targetComponentURI;
		this.isActive = isActive;
		this.mappingCorrelation = mappingCorrelation;
		this.mapGroup = mapGroup;
		this.mapPriority = mapPriority;
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
	
	public int getMapGroup() {
		return mapGroup;
	}
	
	public int getMapPriority() {
		return mapPriority;
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
				.add("mapGroup", mapGroup)
				.add("mapPriority", mapPriority)
				.toString();
	}

	@Override
	public int hashCode() {
		return Objects.hash(sourceComponentURI, targetComponentURI, sourceTerm, targetTerm, sourceIconId, 
				isActive, mappingCorrelation, mapGroup, mapPriority);
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
				&& Objects.equals(mappingCorrelation, other.mappingCorrelation)
				&& Objects.equals(mapGroup, other.mapGroup)
				&& Objects.equals(mapPriority, other.mapPriority);
	}
}
