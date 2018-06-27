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

import static com.b2international.index.query.Expressions.exactMatch;

import com.b2international.index.Doc;
import com.b2international.index.Keyword;
import com.b2international.index.query.Expression;
import com.b2international.snowowl.snomed.reasoner.domain.ChangeNature;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

/**
 * @since 7.0
 */
@Doc(type="concreteDomainChange")
@JsonDeserialize(builder=ConcreteDomainChangeDocument.Builder.class)
public final class ConcreteDomainChangeDocument {

	public static class Fields {
		public static final String CLASSIFICATION_ID = "classificationId";
		public static final String REFERENCED_COMPONENT_ID = "referencedComponentId";
	}

	public static class Expressions {
		public static Expression classificationId(final String classificationId) {
			return exactMatch(Fields.CLASSIFICATION_ID, classificationId);
		}

		public static Expression referencedComponentId(final String referencedComponentId) {
			return exactMatch(Fields.REFERENCED_COMPONENT_ID, referencedComponentId);
		}
	}

	public static Builder builder() {
		return new Builder();
	}

	@JsonPOJOBuilder(withPrefix="")
	public static class Builder {

		private String classificationId;
		private ChangeNature nature;
		private String memberId;
		private String referencedComponentId;

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

		public Builder memberId(final String memberId) {
			this.memberId = memberId;
			return this;
		}

		public Builder referencedComponentId(final String referencedComponentId) {
			this.referencedComponentId = referencedComponentId;
			return this;
		}

		public ConcreteDomainChangeDocument build() {
			return new ConcreteDomainChangeDocument(classificationId, 
					nature, 
					memberId, 
					referencedComponentId);
		}
	}

	@Keyword private final String classificationId;
	private final ChangeNature nature;

	// The origin (stated) UUID of the CD member for inferences, or the UUID of the member to remove/inactivate
	private final String memberId; 

	// Value that should be changed on the origin member before saving/presenting it as an inference
	private final String referencedComponentId; 

	private ConcreteDomainChangeDocument(final String classificationId, 
			final ChangeNature nature, 
			final String memberId,
			final String referencedComponentId) {

		this.classificationId = classificationId;
		this.nature = nature;
		this.memberId = memberId;
		this.referencedComponentId = referencedComponentId;
	}

	public String getClassificationId() {
		return classificationId;
	}

	public ChangeNature getNature() {
		return nature;
	}

	public String getMemberId() {
		return memberId;
	}

	public String getReferencedComponentId() {
		return referencedComponentId;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("ConcreteDomainChangeDocument [classificationId=");
		builder.append(classificationId);
		builder.append(", nature=");
		builder.append(nature);
		builder.append(", memberId=");
		builder.append(memberId);
		builder.append(", referencedComponentId=");
		builder.append(referencedComponentId);
		builder.append("]");
		return builder.toString();
	}
}
