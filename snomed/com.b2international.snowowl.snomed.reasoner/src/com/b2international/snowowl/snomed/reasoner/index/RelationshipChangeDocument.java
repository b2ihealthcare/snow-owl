/*
 * Copyright 2018-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.b2international.index.query.Expressions.exactMatch;
import static com.b2international.index.query.Expressions.matchAny;

import com.b2international.index.Doc;
import com.b2international.index.query.Expression;
import com.b2international.snowowl.snomed.reasoner.domain.ChangeNature;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

/**
 * @since 7.0
 */
@Doc(type="relationshipchange")
@JsonDeserialize(builder=RelationshipChangeDocument.Builder.class)
public final class RelationshipChangeDocument {

	public static class Fields {
		public static final String CLASSIFICATION_ID = "classificationId";
		public static final String SOURCE_ID = "sourceId";
		public static final String DESTINATION_ID = "destinationId";
	}

	public static class Expressions {
		public static Expression classificationId(final String classificationId) {
			return exactMatch(Fields.CLASSIFICATION_ID, classificationId);
		}

		public static Expression classificationId(final Iterable<String> classificationIds) {
			return matchAny(Fields.CLASSIFICATION_ID, classificationIds);
		}

		public static Expression sourceId(final String sourceId) {
			return exactMatch(Fields.SOURCE_ID, sourceId);
		}

		public static Expression sourceId(final Iterable<String> sourceIds) {
			return matchAny(Fields.SOURCE_ID, sourceIds);
		}
		
		public static Expression destinationId(final String destinationId) {
			return exactMatch(Fields.DESTINATION_ID, destinationId);
		}
		
		public static Expression destinationId(final Iterable<String> destinationIds) {
			return matchAny(Fields.DESTINATION_ID, destinationIds);
		}
	}

	public static Builder builder() {
		return new Builder();
	}

	@JsonPOJOBuilder(withPrefix="")
	public static class Builder {

		private String classificationId;
		private ChangeNature nature;
		private String relationshipId;
		private String sourceId;
		private String typeId;
		private String destinationId;
		private Integer group;
		private Integer unionGroup;
		private Boolean released;
		private String characteristicTypeId;

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

		public Builder typeId(final String typeId) {
			this.typeId = typeId;
			return this;
		}

		public Builder destinationId(final String destinationId) {
			this.destinationId = destinationId;
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
		
		public Builder released(final Boolean released) {
			this.released = released;
			return this;
		}

		public Builder characteristicTypeId(final String characteristicTypeId) {
			this.characteristicTypeId = characteristicTypeId;
			return this;
		}

		public RelationshipChangeDocument build() {
			return new RelationshipChangeDocument(classificationId, 
					nature, 
					relationshipId, 
					sourceId, 
					typeId,
					destinationId,
					group, 
					unionGroup,
					released,
					characteristicTypeId);
		}
	}

	/** The identifier of the classification run this change belongs to */
	private final String classificationId;
	/** The type of this classification change */
	private final ChangeNature nature;
	/** The SCTID of the "origin" SCTID for inferences, or the SCTID of the relationship to remove/inactivate */
	private final String relationshipId; 
	/** {@code true} if the description has been released, {@code false} otherwise */
	private final Boolean released;
	
	// Values that should be changed on the "origin" CD member, before saving/presenting it as an inference

	private final String sourceId; 
	private final String typeId;
	private final String destinationId; 
	private final Integer group;
	private final Integer unionGroup;
	private final String characteristicTypeId;

	private RelationshipChangeDocument(final String classificationId, 
			final ChangeNature nature, 
			final String relationshipId,
			final String sourceId, 
			final String typeId, 
			final String destinationId, 
			final Integer group, 
			final Integer unionGroup,
			final Boolean released, 
			final String characteristicTypeId) {

		this.classificationId = classificationId;
		this.nature = nature;
		this.relationshipId = relationshipId;
		this.sourceId = sourceId;
		this.typeId = typeId;
		this.destinationId = destinationId;
		this.group = group;
		this.unionGroup = unionGroup;
		this.released = released;
		this.characteristicTypeId = characteristicTypeId;
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

	public String getTypeId() {
		return typeId;
	}

	public String getDestinationId() {
		return destinationId;
	}

	public Integer getGroup() {
		return group;
	}

	public Integer getUnionGroup() {
		return unionGroup;
	}

	public Boolean isReleased() {
		return released;
	}
	
	public String getCharacteristicTypeId() {
		return characteristicTypeId;
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
		builder.append(", typeId=");
		builder.append(typeId);
		builder.append(", destinationId=");
		builder.append(destinationId);
		builder.append(", group=");
		builder.append(group);
		builder.append(", unionGroup=");
		builder.append(unionGroup);
		builder.append(", released=");
		builder.append(released);
		builder.append(", characteristicTypeId=");
		builder.append(characteristicTypeId);
		builder.append("]");
		return builder.toString();
	}
}
