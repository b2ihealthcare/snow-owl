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
import static com.b2international.index.query.Expressions.matchAny;
import static com.b2international.index.query.Expressions.matchAnyLong;

import com.b2international.collections.longs.LongList;
import com.b2international.commons.functions.StringToLongFunction;
import com.b2international.index.Doc;
import com.b2international.index.query.Expression;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

/**
 * @since 7.0
 */
@Doc(type="equivalentconceptset")
@JsonDeserialize(builder=EquivalentConceptSetDocument.Builder.class)
public final class EquivalentConceptSetDocument {

	public static class Fields {
		public static final String CLASSIFICATION_ID = "classificationId";
		public static final String CONCEPT_IDS = "conceptIds";
	}

	public static class Expressions {
		public static Expression classificationId(final String classificationId) {
			return exactMatch(Fields.CLASSIFICATION_ID, classificationId);
		}

		public static Expression classificationId(final Iterable<String> classificationIds) {
			return matchAny(Fields.CLASSIFICATION_ID, classificationIds);
		}

		public static Expression conceptIds(final Iterable<String> conceptIds) {
			return matchAnyLong(Fields.CONCEPT_IDS, StringToLongFunction.copyOf(conceptIds));
		}
	}

	public static Builder builder() {
		return new Builder();
	}

	@JsonPOJOBuilder(withPrefix="")
	public static class Builder {

		private String classificationId;
		private boolean unsatisfiable;
		private LongList conceptIds;

		@JsonCreator
		private Builder() {
			// Disallow instantiation outside static method
		}

		public Builder classificationId(final String classificationId) {
			this.classificationId = classificationId;
			return this;
		}

		public Builder unsatisfiable(final boolean unsatisfiable) {
			this.unsatisfiable = unsatisfiable;
			return this;
		}

		public Builder conceptIds(final LongList conceptIds) {
			this.conceptIds = conceptIds;
			return this;
		}

		public EquivalentConceptSetDocument build() {
			return new EquivalentConceptSetDocument(classificationId, 
					unsatisfiable, 
					conceptIds);
		}
	}

	private final String classificationId;
	private final boolean unsatisfiable;
	private final LongList conceptIds;

	private EquivalentConceptSetDocument(final String classificationId, 
			final boolean unsatisfiable, 
			final LongList conceptIds) {
		this.classificationId = classificationId;
		this.unsatisfiable = unsatisfiable;
		this.conceptIds = conceptIds;
	}

	public String getClassificationId() {
		return classificationId;
	}

	public boolean isUnsatisfiable() {
		return unsatisfiable;
	}

	public LongList getConceptIds() {
		return conceptIds;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("EquivalentConceptSetDocument [classificationId=");
		builder.append(classificationId);
		builder.append(", unsatisfiable=");
		builder.append(unsatisfiable);
		builder.append(", conceptIds=");
		builder.append(conceptIds);
		builder.append("]");
		return builder.toString();
	}
}
