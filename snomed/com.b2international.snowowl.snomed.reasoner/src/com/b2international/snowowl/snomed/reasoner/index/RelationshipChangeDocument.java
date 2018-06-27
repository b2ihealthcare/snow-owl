/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.reasoner.index;

import com.b2international.index.Doc;
import com.b2international.index.Keyword;
import com.b2international.snowowl.snomed.reasoner.domain.ChangeNature;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

/**
 * @since 7.0
 */
@Doc(type="relationshipChange")
@JsonDeserialize(builder=RelationshipChangeDocument.Builder.class)
public final class RelationshipChangeDocument {

	public static Builder builder() {
		return new Builder();
	}

	@JsonPOJOBuilder(withPrefix="")
	public static class Builder {

		private String classificationId;
		private ChangeNature nature;
		private String relationshipId;
		private String sourceId;
		private Integer group;
		private Integer unionGroup;

		@JsonCreator
		private Builder() {
			// Disallow instantiation outside static method
		}

		public Builder classificationId(final String classificationId) {
			this.classificationId = classificationId;
			return this;
		}

		public Builder nature(final ChangeNature nature) {
			this.nature = nature;
			return this;
		}

		public Builder relationshipId(final String relationshipId) {
			this.relationshipId = relationshipId;
			return this;
		}

		public Builder sourceId(final String sourceId) {
			this.sourceId = sourceId;
			return this;
		}

		public Builder group(final Integer group) {
			this.group = group;
			return this;
		}

		public Builder unionGroup(final Integer unionGroup) {
			this.unionGroup = unionGroup;
			return this;
		}

		public RelationshipChangeDocument build() {
			return new RelationshipChangeDocument(classificationId, 
					nature, 
					relationshipId, 
					sourceId, 
					group, 
					unionGroup);
		}
	}

	@Keyword private final String classificationId;
	private final ChangeNature nature;
	
	// The origin (stated relationship) SCTID for inferences, or the SCTID of the relationship to remove/inactivate
	private final String relationshipId; 
	
	// Values that should be changed on the origin relationship before saving/presenting it as an inference
	private final String sourceId; 
	private final Integer group;
	private final Integer unionGroup;

	private RelationshipChangeDocument(final String classificationId, 
			final ChangeNature nature, 
			final String relationshipId,
			final String sourceId, 
			final Integer group, 
			final Integer unionGroup) {

		this.classificationId = classificationId;
		this.nature = nature;
		this.relationshipId = relationshipId;
		this.sourceId = sourceId;
		this.group = group;
		this.unionGroup = unionGroup;
	}

	public String getClassificationId() {
		return classificationId;
	}

	public ChangeNature getNature() {
		return nature;
	}

	public String getRelationshipId() {
		return relationshipId;
	}

	public String getSourceId() {
		return sourceId;
	}

	public Integer getGroup() {
		return group;
	}

	public Integer getUnionGroup() {
		return unionGroup;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("RelationshipChangeDocument [classificationId=");
		builder.append(classificationId);
		builder.append(", nature=");
		builder.append(nature);
		builder.append(", relationshipId=");
		builder.append(relationshipId);
		builder.append(", sourceId=");
		builder.append(sourceId);
		builder.append(", group=");
		builder.append(group);
		builder.append(", unionGroup=");
		builder.append(unionGroup);
		builder.append("]");
		return builder.toString();
	}
}
