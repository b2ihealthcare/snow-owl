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
public final class ConceptMapMapping implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public static Builder builder() {
		return new Builder();
	}
	
	public static Builder builder(ConceptMapMapping from) {
		return builder()
				.active(from.isActive())
				.mapAdvice(from.getMapAdvice())
				.mapGroup(from.getMapGroup())
				.mappingCorrelation(from.getMappingCorrelation())
				.mapPriority(from.getMapPriority())
				.mapRule(from.getMapRule())
				.sourceComponentURI(from.getSourceComponentURI())
				.sourceIconId(from.getSourceIconId())
				.sourceTerm(from.getSourceTerm())
				.targetComponentURI(from.getTargetComponentURI())
				.targetTerm(from.getTargetTerm());
	}
	
	public final static class Builder {
		
		private String sourceIconId;
		private String sourceTerm;
		private ComponentURI sourceComponentURI;
		
		private String targetTerm;
		private ComponentURI targetComponentURI = ComponentURI.UNSPECIFIED;
		
		private boolean active;
		private MappingCorrelation mappingCorrelation = MappingCorrelation.NOT_SPECIFIED;
		
		private Integer mapGroup = 0;
		private Integer mapPriority = 0;
		private String mapRule = "";
		private String mapAdvice = "";
		
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
		
		public Builder active(final boolean active) {
			this.active = active;
			return this;
		}
		
		public Builder mappingCorrelation(final MappingCorrelation mappingCorrelation) {
			this.mappingCorrelation = mappingCorrelation;
			return this;
		}
		
		public Builder mapGroup(final Integer mapGroup) {
			this.mapGroup = mapGroup;
			return this;
		}
		
		public Builder mapPriority(final Integer mapPriority) {
			this.mapPriority = mapPriority;
			return this;
		}
		
		public Builder mapRule(final String mapRule) {
			this.mapRule = mapRule;
			return this;
		}
		
		public Builder mapAdvice(final String mapAdvice) {
			this.mapAdvice = mapAdvice;
			return this;
		}
		
		public ConceptMapMapping build() {
			return new ConceptMapMapping(sourceIconId, sourceTerm, sourceComponentURI, 
					targetTerm, targetComponentURI, active, mappingCorrelation, mapGroup, mapPriority, mapRule, mapAdvice);
		}
	
	}
	
	private final String sourceIconId;
	private final String sourceTerm;
	private final ComponentURI sourceComponentURI;
	
	private final String targetTerm;
	private final ComponentURI targetComponentURI;
	
	private final Boolean active;
	private final MappingCorrelation mappingCorrelation;
	
	private final Integer mapGroup;
	private final Integer mapPriority;
	private final String mapRule;
	private final String mapAdvice;
	
	ConceptMapMapping(String sourceIconId, 
			String sourceTerm,
			ComponentURI sourceComponentURI, 
			String targetTerm, 
			ComponentURI targetComponentURI, 
			boolean active,
			MappingCorrelation mappingCorrelation,
			Integer mapGroup,
			Integer mapPriority, 
			String mapRule, 
			String mapAdvice) {
		
		this.sourceIconId = sourceIconId;
		this.sourceTerm = sourceTerm;
		this.sourceComponentURI = sourceComponentURI;
		this.targetTerm = targetTerm;
		this.targetComponentURI = targetComponentURI;
		this.active = active;
		this.mappingCorrelation = mappingCorrelation;
		this.mapGroup = mapGroup;
		this.mapPriority = mapPriority;
		this.mapRule = mapRule;
		this.mapAdvice = mapAdvice;
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
	
	public Boolean isActive() {
		return active;
	}
	
	public MappingCorrelation getMappingCorrelation() {
		return mappingCorrelation;
	}
	
	public Integer getMapGroup() {
		return mapGroup;
	}
	
	public Integer getMapPriority() {
		return mapPriority;
	}
	
	public String getMapAdvice() {
		return mapAdvice;
	}
	
	public String getMapRule() {
		return mapRule;
	}
	
	@Override
	public String toString() {
		return MoreObjects.toStringHelper("SetMember")
				.add("sourceComponentURI", sourceComponentURI)
				.add("term", sourceTerm)
				.add("iconId", sourceIconId)
				.add("targetComponentURI", targetComponentURI)
				.add("targetTerm", targetTerm)
				.add("active", active)
				.add("mappingCorrelation", mappingCorrelation)
				.add("mapGroup", mapGroup)
				.add("mapPriority", mapPriority)
				.toString();
	}

	@Override
	public int hashCode() {
		return Objects.hash(sourceComponentURI, targetComponentURI, sourceTerm, targetTerm, sourceIconId, 
				active, mappingCorrelation, mapGroup, mapPriority, mapRule, mapAdvice);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		ConceptMapMapping other = (ConceptMapMapping) obj;
		return Objects.equals(sourceComponentURI, other.sourceComponentURI)
				&& Objects.equals(targetComponentURI, other.targetComponentURI)
				&& Objects.equals(sourceTerm, other.sourceTerm)
				&& Objects.equals(targetTerm, other.targetTerm)
				&& Objects.equals(sourceIconId, other.sourceIconId)
				&& Objects.equals(active, other.active)
				&& Objects.equals(mappingCorrelation, other.mappingCorrelation)
				&& Objects.equals(mapGroup, other.mapGroup)
				&& Objects.equals(mapPriority, other.mapPriority)
				&& Objects.equals(mapRule, other.mapRule)
				&& Objects.equals(mapAdvice, other.mapAdvice);
	}

	public Builder toBuilder() {
		return builder(this);
	}

}
