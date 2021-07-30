/*
 * Copyright 2020-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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

import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.request.MappingCorrelation;
import com.b2international.snowowl.core.uri.ComponentURI;
import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;

/**
 * @since 7.8
 */
public final class ConceptMapMapping implements Serializable {
	
	private static final long serialVersionUID = 2L;
	
	public static Builder builder() {
		return new Builder();
	}
	
	public static Builder builder(ConceptMapMapping from) {
		return builder()
				.uri(from.getUri())
				.conceptMapUri(from.getConceptMapUri())
				.conceptMapTerm(from.getConceptMapTerm())
				.conceptMapIconId(from.getConceptMapUri())
				.active(from.isActive())
				.mapAdvice(from.getMapAdvice())
				.mapGroup(from.getMapGroup())
				.mappingCorrelation(from.getMappingCorrelation())
				.mapPriority(from.getMapPriority())
				.mapRule(from.getMapRule())
				.sourceComponentURI(from.getSourceComponentURI())
				.sourceIconId(from.getSourceIconId())
				.sourceTerm(from.getSourceTerm())
				.targetIconId(from.getTargetIconId())
				.targetComponentURI(from.getTargetComponentURI())
				.targetTerm(from.getTargetTerm())
				.approximate(from.isApproximate());
	}
	
	public final static class Builder {
		
		private String uri;
		
		private String conceptMapIconId;
		private String conceptMapTerm;
		private String conceptMapUri;
		
		private String sourceIconId;
		private String sourceTerm;
		private ComponentURI sourceComponentURI;
		
		private String targetIconId;
		private String targetTerm;
		private ComponentURI targetComponentURI = ComponentURI.UNSPECIFIED;
		
		private boolean active;
		private MappingCorrelation mappingCorrelation = MappingCorrelation.NOT_SPECIFIED;
		
		private Integer mapGroup = 0;
		private Integer mapPriority = 0;
		private String mapRule = "";
		private String mapAdvice = "";
		
		private boolean approximate;
		
		public Builder uri(ComponentURI uri) {
			return uri(uri.toString());
		}
		
		public Builder uri(String uri) {
			this.uri = uri;
			return this;
		}
		
		public Builder conceptMapTerm(final String conceptMapTerm) {
			this.conceptMapTerm = conceptMapTerm;
			return this;
		}
		
		public Builder conceptMapUri(final ResourceURI conceptMapUri) {
			return conceptMapUri(conceptMapUri.toString());
		}
		
		public Builder conceptMapUri(final ComponentURI conceptMapUri) {
			return conceptMapUri(conceptMapUri.toString());
		}
		
		public Builder conceptMapUri(final String conceptMapUri) {
			this.conceptMapUri = conceptMapUri;
			return this;
		}
		
		public Builder conceptMapIconId(final String conceptMapIconId) {
			this.conceptMapIconId = conceptMapIconId;
			return this;
		}
		
		public Builder sourceTerm(final String sourceTerm) {
			this.sourceTerm = sourceTerm;
			return this;
		}
		
		public Builder sourceIconId(final String sourceIconId) {
			this.sourceIconId = sourceIconId == null ? IComponent.ROOT_ID : sourceIconId;
			return this;
		}
		
		public Builder sourceComponentURI(final ComponentURI sourceComponentURI) {
			this.sourceComponentURI = sourceComponentURI;
			return this;
		}
		
		public Builder targetIconId(final String targetIconId) {
			this.targetIconId = targetIconId == null ? IComponent.ROOT_ID : targetIconId;
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
			this.mapGroup = mapGroup == null ? 0 : mapGroup;
			return this;
		}
		
		public Builder mapPriority(final Integer mapPriority) {
			this.mapPriority = mapPriority == null ? 0 : mapPriority;
			return this;
		}
		
		public Builder mapRule(final String mapRule) {
			this.mapRule = Strings.nullToEmpty(mapRule);
			return this;
		}
		
		public Builder mapAdvice(final String mapAdvice) {
			this.mapAdvice = Strings.nullToEmpty(mapAdvice);
			return this;
		}
		
		public Builder approximate(final boolean approximate) {
			this.approximate = approximate;
			return this;
		}
		
		public ConceptMapMapping build() {
			return new ConceptMapMapping(
				uri,
				conceptMapUri, conceptMapTerm, conceptMapIconId, 
				sourceComponentURI, sourceTerm, sourceIconId, 
				targetComponentURI, targetTerm, targetIconId, 
				active, 
				mappingCorrelation, mapGroup, mapPriority, mapRule, mapAdvice,
				approximate
			);
		}
	
	}

	private final String uri; 
	
	private final String conceptMapIconId;
	private final String conceptMapTerm;
	private final String conceptMapUri;
	
	private final String sourceIconId;
	private final String sourceTerm;
	private final ComponentURI sourceComponentURI;
	
	private final String targetIconId;
	private final String targetTerm;
	private final ComponentURI targetComponentURI;
	
	private final Boolean active;
	private final MappingCorrelation mappingCorrelation;
	
	private final Integer mapGroup;
	private final Integer mapPriority;
	private final String mapRule;
	private final String mapAdvice;
	
	private final boolean approximate;
	
	ConceptMapMapping(
			String uri,
			String conceptMapUri,
			String conceptMapTerm,
			String conceptMapIconId,
			ComponentURI sourceComponentURI, 
			String sourceTerm,
			String sourceIconId, 
			ComponentURI targetComponentURI, 
			String targetTerm, 
			String targetIconId,
			Boolean active,
			MappingCorrelation mappingCorrelation,
			Integer mapGroup,
			Integer mapPriority, 
			String mapRule, 
			String mapAdvice,
			boolean approximate) {
		
		this.uri = uri; 
		this.conceptMapUri = conceptMapUri;
		this.conceptMapTerm = conceptMapTerm;
		this.conceptMapIconId = conceptMapIconId;
		this.sourceComponentURI = sourceComponentURI;
		this.sourceTerm = sourceTerm;
		this.sourceIconId = sourceIconId;
		this.targetComponentURI = targetComponentURI;
		this.targetTerm = targetTerm;
		this.targetIconId = targetIconId;
		this.active = active;
		this.mappingCorrelation = mappingCorrelation;
		this.mapGroup = mapGroup;
		this.mapPriority = mapPriority;
		this.mapRule = mapRule;
		this.mapAdvice = mapAdvice;
		this.approximate = approximate;
	}

	public String getUri() {
		return uri;
	}

	public String getConceptMapUri() {
		return conceptMapUri;
	}
	
	public String getConceptMapTerm() {
		return conceptMapTerm;
	}
	
	public String getConceptMapIconId() {
		return conceptMapIconId;
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
	
	public String getTargetIconId() {
		return targetIconId;
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
	
	public boolean isApproximate() {
		return approximate;
	}
	
	@Override
	public String toString() {
		return MoreObjects.toStringHelper(getClass())
				.add("uri", uri)
				.add("active", active)
				.add("conceptMapUri", conceptMapUri)
				.add("conceptMapTerm", conceptMapTerm)
				.add("conceptMapIconId", conceptMapIconId)
				.add("sourceComponentURI", sourceComponentURI)
				.add("sourceTerm", sourceTerm)
				.add("sourceIconId", sourceIconId)
				.add("targetComponentURI", targetComponentURI)
				.add("targetTerm", targetTerm)
				.add("targetIconId", targetIconId)
				.add("mappingCorrelation", mappingCorrelation)
				.add("mapGroup", mapGroup)
				.add("mapPriority", mapPriority)
				.add("mapRule", mapRule)
				.add("mapAdvice", mapAdvice)
				.add("approximate", approximate)
				.toString();
	}

	@Override
	public int hashCode() {
		return Objects.hash(
			uri,
			conceptMapUri,
			active, 
			sourceComponentURI,
			sourceTerm,
			targetComponentURI, 
			targetTerm, 
			mappingCorrelation, 
			mapGroup, 
			mapPriority, 
			mapRule, 
			mapAdvice,
			approximate
		);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		ConceptMapMapping other = (ConceptMapMapping) obj;
		return Objects.equals(uri, other.uri)
				&& Objects.equals(conceptMapUri, other.conceptMapUri)
				&& Objects.equals(active, other.active)
				&& Objects.equals(sourceComponentURI, other.sourceComponentURI)
				&& Objects.equals(sourceTerm, other.sourceTerm)
				&& Objects.equals(targetComponentURI, other.targetComponentURI)
				&& Objects.equals(targetTerm, other.targetTerm)
				&& Objects.equals(mappingCorrelation, other.mappingCorrelation)
				&& Objects.equals(mapGroup, other.mapGroup)
				&& Objects.equals(mapPriority, other.mapPriority)
				&& Objects.equals(mapRule, other.mapRule)
				&& Objects.equals(mapAdvice, other.mapAdvice)
				&& Objects.equals(approximate, other.approximate);
	}

	public Builder toBuilder() {
		return builder(this);
	}

}
