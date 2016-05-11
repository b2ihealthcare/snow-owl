/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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

import com.b2international.index.Doc;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 4.7
 */
public class RevisionFixtures {

	public static final long STORAGE_KEY1 = 1L;
	public static final long STORAGE_KEY2 = 2L;
	
	private RevisionFixtures() {
	}
	
	@Doc
	public static class Data extends Revision {
		
		private String field1;
		private String field2;

		@JsonCreator
		public Data(@JsonProperty("field1") final String field1, @JsonProperty("field2") final String field2) {
			this.field1 = field1;
			this.field2 = field2;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;
			Data other = (Data) obj;
			return Objects.equals(field1, other.field1) && Objects.equals(field2, other.field2); 
		}
		
		@Override
		public int hashCode() {
			return Objects.hash(field1, field2);
		}
		
	}
	
	@Doc
	public static class NestedData extends Revision {
		
		private String field1;
		// using unversioned data not the revision based one here
		private com.b2international.index.Fixtures.Data data;
		
		@JsonCreator
		public NestedData(@JsonProperty("field1") String field1, @JsonProperty("data") com.b2international.index.Fixtures.Data data) {
			this.field1 = field1;
			this.data = data;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;
			NestedData other = (NestedData) obj;
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
		public DeeplyNestedData(@JsonProperty("parentData") com.b2international.index.Fixtures.ParentData parentData) {
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
