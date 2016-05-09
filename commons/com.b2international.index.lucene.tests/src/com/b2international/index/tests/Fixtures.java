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
package com.b2international.index.tests;

import static com.google.common.collect.Sets.newHashSet;

import java.util.Collection;
import java.util.Objects;

import com.b2international.index.Doc;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 4.7
 */
public class Fixtures {

	@Doc
	public static class Data {
		String field1 = "field1";
		String field2 = "field2";

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
	public static class ParentData {
		
		String field1 = "field1";
		NestedData nestedData;
		
		@JsonCreator
		public ParentData(@JsonProperty("nestedData") NestedData nestedData) {
			this.nestedData = nestedData;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;
			ParentData other = (ParentData) obj;
			return Objects.equals(field1, other.field1) && Objects.equals(nestedData, other.nestedData);
		}
		
		@Override
		public int hashCode() {
			return Objects.hash(field1, nestedData);
		}
		
	}
	
	@Doc
	public static class MultipleNestedData {
		
		String field1 = "field1";
		Collection<NestedData> nestedDatas = newHashSet();
		
		@JsonCreator
		public MultipleNestedData(@JsonProperty("nestedDatas") Collection<NestedData> nestedDatas) {
			this.nestedDatas.addAll(nestedDatas);
		}
		
		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;
			MultipleNestedData other = (MultipleNestedData) obj;
			return Objects.equals(field1, other.field1) && Objects.equals(nestedDatas, other.nestedDatas);
		}
		
		@Override
		public int hashCode() {
			return Objects.hash(field1, nestedDatas);
		}
		
	}
	
	@Doc
	public static class NestedData {
		
		String field2;

		@JsonCreator()
		public NestedData(@JsonProperty("field2") String field2) {
			this.field2 = field2;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;
			NestedData other = (NestedData) obj;
			return Objects.equals(field2, other.field2);
		}
		
		@Override
		public int hashCode() {
			return Objects.hash(field2);
		}
		
	}
	
	@Doc
	public static class DeepData {
		
		final ParentData parentData;
		
		@JsonCreator
		public DeepData(@JsonProperty("parentData") ParentData parentData) {
			this.parentData = parentData;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;
			DeepData other = (DeepData) obj;
			return Objects.equals(parentData, other.parentData);
		}
		
		@Override
		public int hashCode() {
			return Objects.hash(parentData);
		}
		
	}
	
}
