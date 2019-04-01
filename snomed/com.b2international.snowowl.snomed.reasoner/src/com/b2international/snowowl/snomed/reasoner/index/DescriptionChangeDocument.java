/*
 * Copyright 2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
 * Represents a description change; currently only expected to appear with
 * {@link ChangeNature#NEW} or {@link ChangeNature#REDUNDANT} nature to indicate
 * description replacement, when merging equivalent concepts into a single
 * concept.
 * 
 * @since 6.14
 */
@Doc(type="descriptionchange")
@JsonDeserialize(builder=DescriptionChangeDocument.Builder.class)
public final class DescriptionChangeDocument {

	public static class Fields {
		public static final String CLASSIFICATION_ID = "classificationId";
		public static final String CONCEPT_ID = "conceptId";
	}

	public static class Expressions {
		public static Expression classificationId(final String classificationId) {
			return exactMatch(Fields.CLASSIFICATION_ID, classificationId);
		}

		public static Expression classificationId(final Iterable<String> classificationIds) {
			return matchAny(Fields.CLASSIFICATION_ID, classificationIds);
		}

		public static Expression conceptId(final String conceptId) {
			return exactMatch(Fields.CONCEPT_ID, conceptId);
		}

		public static Expression conceptId(final Iterable<String> conceptIds) {
			return matchAny(Fields.CONCEPT_ID, conceptIds);
		}
	}

	public static Builder builder() {
		return new Builder();
	}

	@JsonPOJOBuilder(withPrefix="")
	public static class Builder {

		private String classificationId;
		private ChangeNature nature;
		private String descriptionId;
		private String conceptId;
		private Boolean released;

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

		public Builder descriptionId(final String descriptionId) {
			this.descriptionId = descriptionId;
			return this;
		}

		public Builder conceptId(final String conceptId) {
			this.conceptId = conceptId;
			return this;
		}

		public Builder released(final Boolean released) {
			this.released = released;
			return this;
		}

		public DescriptionChangeDocument build() {
			return new DescriptionChangeDocument(classificationId, 
					nature, 
					descriptionId, 
					conceptId,
					released);
		}
	}

	/** The identifier of the classification run this change belongs to */
	private final String classificationId;
	/** The type of this classification change */
	private final ChangeNature nature;
	/** The SCTID of the "origin" description for inferences, or the SCTID of the description to remove/inactivate */
	private final String descriptionId; 
	/** {@code true} if the description has been released, {@code false} otherwise */
	private final Boolean released;
	
	// Values that should be changed on the original description, before saving/presenting it as an inference
	
	private final String conceptId;

	private DescriptionChangeDocument(final String classificationId, 
			final ChangeNature nature, 
			final String descriptionId,
			final String conceptId, 
			final Boolean released) {

		this.classificationId = classificationId;
		this.nature = nature;
		this.descriptionId = descriptionId;
		this.conceptId = conceptId;
		this.released = released;
	}

	public String getClassificationId() {
		return classificationId;
	}

	public ChangeNature getNature() {
		return nature;
	}

	public String getDescriptionId() {
		return descriptionId;
	}

	public String getConceptId() {
		return conceptId;
	}

	public Boolean isReleased() {
		return released;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("DescriptionChangeDocument [classificationId=");
		builder.append(classificationId);
		builder.append(", nature=");
		builder.append(nature);
		builder.append(", descriptionId=");
		builder.append(descriptionId);
		builder.append(", conceptId=");
		builder.append(conceptId);
		builder.append(", released=");
		builder.append(released);
		builder.append("]");
		return builder.toString();
	}
}
