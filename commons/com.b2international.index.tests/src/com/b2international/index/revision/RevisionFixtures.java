/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.index.revision;

import java.util.Objects;

import com.b2international.index.Analyzers;
import com.b2international.index.Doc;
import com.b2international.index.RevisionHash;
import com.b2international.index.Script;
import com.b2international.index.Text;
import com.b2international.index.WithScore;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects.ToStringHelper;

/**
 * @since 4.7
 */
public class RevisionFixtures {

	private RevisionFixtures() {
	}
	
	@Doc
	@RevisionHash({ "field1", "field2" })
	public static class RevisionData extends Revision {
		
		@Text(analyzer=Analyzers.TOKENIZED)
		private String field1;
		private String field2;

		@JsonCreator
		public RevisionData(
				@JsonProperty(Revision.Fields.ID) final String id, 
				@JsonProperty("field1") final String field1, 
				@JsonProperty("field2") final String field2) {
			super(id);
			this.field1 = field1;
			this.field2 = field2;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;
			RevisionData other = (RevisionData) obj;
			return Objects.equals(field1, other.field1) && Objects.equals(field2, other.field2); 
		}
		
		@Override
		public int hashCode() {
			return Objects.hash(field1, field2);
		}
		
	}
	
	@Doc
	public static class AnalyzedData extends Revision {
		
		@Text
		private final String field;
		
		@JsonCreator
		public AnalyzedData(
				@JsonProperty(Revision.Fields.ID) final String id, 
				@JsonProperty("field") final String field) {
			super(id);
			this.field = field;
		}
		
		public String getField() {
			return field;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;
			AnalyzedData other = (AnalyzedData) obj;
			return Objects.equals(field, other.field); 
		}
		
		@Override
		public int hashCode() {
			return Objects.hash(field);
		}
		
	}
	
	@Doc
	@Script(name="doi", script="return doc.doi.value")
	@Script(name="doiFactor", script="return doc.doi.value * params.factor")
	public static class ScoredData extends RevisionData implements WithScore {
		
		private float score = 0.0f;
		private final float doi;

		@JsonCreator
		public ScoredData(
				@JsonProperty(Revision.Fields.ID) final String id,
				@JsonProperty("field1") final String field1, 
				@JsonProperty("field2") final String field2,
				@JsonProperty("doi") final float doi) {
			super(id, field1, field2);
			this.doi = doi;
		}
		
		@Override
		public float getScore() {
			return score;
		}

		@Override
		public void setScore(float score) {
			this.score = score;
		}
		
		public float getDoi() {
			return doi;
		}

		@Override
		protected ToStringHelper doToString() {
			return super.doToString()
					.add("doi", doi)
					.add("score", score);
		}
		
	}
	
	@Doc
	public static class BooleanData extends RevisionData {

		private final boolean active;

		@JsonCreator
		public BooleanData(
				@JsonProperty(Revision.Fields.ID) final String id,
				@JsonProperty("field1") final String field1, 
				@JsonProperty("field2") final String field2,
				@JsonProperty("value") final boolean active) {
			super(id, field1, field2);
			this.active = active;
		}
		
		public boolean isActive() {
			return active;
		}
		
	}
	
	@Doc
	public static class RangeData extends RevisionData {
		
		private final int from;
		private final int to;

		@JsonCreator
		public RangeData(
				@JsonProperty(Revision.Fields.ID) final String id,
				@JsonProperty("field1") final String field1, 
				@JsonProperty("field2") final String field2, 
				@JsonProperty("from") final int from,
				@JsonProperty("to") final int to) {
			super(id, field1, field2);
			this.from = from;
			this.to = to;
		}
		
		public int getFrom() {
			return from;
		}
		
		public int getTo() {
			return to;
		}
		
	}
	
	@Doc
	public static class NestedRevisionData extends Revision {
		
		private String field1;
		// using unversioned data not the revision based one here
		private com.b2international.index.Fixtures.Data data;
		
		@JsonCreator
		public NestedRevisionData(
				@JsonProperty(Revision.Fields.ID) final String id,
				@JsonProperty("field1") String field1, 
				@JsonProperty("data") com.b2international.index.Fixtures.Data data) {
			super(id);
			this.field1 = field1;
			this.data = data;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;
			NestedRevisionData other = (NestedRevisionData) obj;
			return Objects.equals(field1, other.field1) && Objects.equals(data, other.data); 
		}
		
		@Override
		public int hashCode() {
			return Objects.hash(field1, data);
		}
		
	}
	
	@Doc
	public static class DeeplyNestedData extends Revision {
		
		private com.b2international.index.Fixtures.ParentData parentData;
		
		@JsonCreator
		public DeeplyNestedData(
				@JsonProperty(Revision.Fields.ID) final String id,
				@JsonProperty("parentData") com.b2international.index.Fixtures.ParentData parentData) {
			super(id);
			this.parentData = parentData;
		}
		
		@Override
		public int hashCode() {
			return Objects.hash(parentData);
		}
		
		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;
			DeeplyNestedData other = (DeeplyNestedData) obj;
			return Objects.equals(parentData, other.parentData); 
		}
		
	}
	
}
