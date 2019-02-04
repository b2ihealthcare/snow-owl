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
@Doc(type="concretedomainchange")
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

		public static Expression classificationId(final Iterable<String> classificationIds) {
			return matchAny(Fields.CLASSIFICATION_ID, classificationIds);
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
		private int group;
		private boolean released;

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

		public Builder group(int group) {
			this.group = group;
			return this;
		}
		
		public Builder released(final boolean released) {
			this.released = released;
			return this;
		}
		
		public ConcreteDomainChangeDocument build() {
			return new ConcreteDomainChangeDocument(classificationId, 
					nature, 
					memberId, 
					referencedComponentId,
					group,
					released);
		}
	}

	private final String classificationId;
	private final ChangeNature nature;

	// The origin (stated) UUID of the CD member for inferences, or the UUID of the member to remove/inactivate
	private final String memberId; 

	// Values that should be changed on the original member, before saving/presenting it as an inference
	private final String referencedComponentId;
	private final int group; 
	private final boolean released;

	private ConcreteDomainChangeDocument(final String classificationId, 
			final ChangeNature nature, 
			final String memberId,
			final String referencedComponentId, 
			final int group,
			final boolean released) {

		this.classificationId = classificationId;
		this.nature = nature;
		this.memberId = memberId;
		this.referencedComponentId = referencedComponentId;
		this.group = group;
		this.released = released;
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
	
	public int getGroup() {
		return group;
	}
	
	public boolean isReleased() {
		return released;
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
		builder.append(", group=");
		builder.append(group);
		builder.append(", released=");
		builder.append(released);
		builder.append("]");
		return builder.toString();
	}
}
