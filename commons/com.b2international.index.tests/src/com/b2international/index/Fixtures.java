/*
 * Copyright 2011-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.index;

import static com.google.common.collect.Sets.newHashSet;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.annotation.*;
import com.google.common.base.MoreObjects;
import com.google.common.collect.Maps;

/**
 * @since 4.7
 */
public class Fixtures {

	@Doc
	@Script(name=Data.Scripts.FIELD_SCORE, script="return doc.floatField.value")
	public static class Data {
		
		public static class Scripts {
			public static final String FIELD_SCORE = "fieldScore";
		}
		
		@ID
		private String id;

		@Text(analyzer=Analyzers.CASE_SENSITIVE)
		@Keyword(alias="exact")
		private String analyzedField;
		
		private String field1;
		private String field2;
		
		private BigDecimal bigDecimalField;
		
		private float floatField;
		private Float floatWrapper;
		private long longField;
		private Long longWrapper;
		private int intField;
		private Integer intWrapper;
		private short shortField;
		private Short shortWrapper;

		@JsonCreator
		public Data(@JsonProperty("id") String id) {
			this.id = id;
		}
		
		public String getId() {
			return id;
		}
		
		public String getAnalyzedField() {
			return analyzedField;
		}

		public void setAnalyzedField(String analyzedField) {
			this.analyzedField = analyzedField;
		}

		public String getField1() {
			return field1;
		}

		public void setField1(String field1) {
			this.field1 = field1;
		}

		public String getField2() {
			return field2;
		}

		public void setField2(String field2) {
			this.field2 = field2;
		}

		public BigDecimal getBigDecimalField() {
			return bigDecimalField;
		}

		public void setBigDecimalField(BigDecimal bigDecimalField) {
			this.bigDecimalField = bigDecimalField;
		}

		public float getFloatField() {
			return floatField;
		}

		public void setFloatField(float floatField) {
			this.floatField = floatField;
		}

		public Float getFloatWrapper() {
			return floatWrapper;
		}

		public void setFloatWrapper(Float floatWrapper) {
			this.floatWrapper = floatWrapper;
		}

		public long getLongField() {
			return longField;
		}

		public void setLongField(long longField) {
			this.longField = longField;
		}

		public Long getLongWrapper() {
			return longWrapper;
		}

		public void setLongWrapper(Long longWrapper) {
			this.longWrapper = longWrapper;
		}

		public int getIntField() {
			return intField;
		}

		public void setIntField(int intField) {
			this.intField = intField;
		}

		public Integer getIntWrapper() {
			return intWrapper;
		}

		public void setIntWrapper(Integer intWrapper) {
			this.intWrapper = intWrapper;
		}

		public short getShortField() {
			return shortField;
		}

		public void setShortField(short shortField) {
			this.shortField = shortField;
		}

		public Short getShortWrapper() {
			return shortWrapper;
		}

		public void setShortWrapper(Short shortWrapper) {
			this.shortWrapper = shortWrapper;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;
			Data other = (Data) obj;
			return true
					&& Objects.equals(id, other.id) 
					&& Objects.equals(analyzedField, other.analyzedField) 
					&& Objects.equals(field1, other.field1) 
					&& Objects.equals(field2, other.field2) 
					&& Objects.equals(bigDecimalField, other.bigDecimalField) 
					&& Objects.equals(floatWrapper, other.floatWrapper) 
					&& Objects.equals(longWrapper, other.longWrapper) 
					&& Objects.equals(intWrapper, other.intWrapper) 
					&& Objects.equals(shortWrapper, other.shortWrapper)
					&& floatField == other.floatField 
					&& longField == other.longField
					&& intField == other.intField 
					&& shortField == other.shortField;
		}
		
		@Override
		public int hashCode() {
			return Objects.hash(
				id,
				analyzedField, 
				field1, 
				field2, 
				bigDecimalField, 
				floatWrapper, 
				longWrapper, 
				intWrapper, 
				shortWrapper
			);
		}
		
		@Override
		public String toString() {
			return MoreObjects.toStringHelper(getClass())
					.add("id", id)
					.add("analyzedField", analyzedField)
					.add("bigDecimalField", bigDecimalField)
					.add("field1", field1)
					.add("field2", field2)
					.add("floatField", floatField)
					.add("floatWrapper", floatWrapper)
					.add("intField", intField)
					.add("intWrapper", intWrapper)
					.add("longField", longField)
					.add("longWrapper", longWrapper)
					.add("shortField", shortField)
					.add("shortWrapper", shortWrapper)
					.toString();
		}
		
	}
	
	public static class PartialData {
		private final String field1;

		@JsonCreator
		public PartialData(@JsonProperty("field1") String field1) {
			this.field1 = field1;
		}
		
		public String getField1() {
			return field1;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;
			PartialData other = (PartialData) obj;
			return Objects.equals(field1, other.field1); 
		}
		
		@Override
		public int hashCode() {
			return Objects.hash(field1);
		}
		
	}
	
	@Doc
	public static class ParentData {
		
		@ID
		private final String id;
		
		private final String field1;
		private final NestedData nestedData;
		
		@JsonCreator
		public ParentData(@JsonProperty("id") String id, @JsonProperty("field1") String field1, @JsonProperty("nestedData") NestedData nestedData) {
			this.id = id;
			this.field1 = field1;
			this.nestedData = nestedData;
		}
		
		public String getField1() {
			return field1;
		}
		
		public String getId() {
			return id;
		}
		
		public NestedData getNestedData() {
			return nestedData;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;
			ParentData other = (ParentData) obj;
			return Objects.equals(id, other.id) 
					&& Objects.equals(field1, other.field1) 
					&& Objects.equals(nestedData, other.nestedData);
		}
		
		@Override
		public int hashCode() {
			return Objects.hash(id, field1, nestedData);
		}
		
	}
	
	@Doc
	public static class MultipleNestedData {
		
		@ID
		String id;
		String field1 = "field1";
		Collection<NestedData> nestedDatas = newHashSet();
		
		@JsonCreator
		public MultipleNestedData(@JsonProperty("id") String id, @JsonProperty("nestedDatas") Collection<NestedData> nestedDatas) {
			this.id = id;
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

		@JsonCreator
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
		
		@ID
		final String id;
		final ParentData parentData;
		
		@JsonCreator
		public DeepData(@JsonProperty("id") String id, @JsonProperty("parentData") ParentData parentData) {
			this.id = id;
			this.parentData = parentData;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;
			DeepData other = (DeepData) obj;
			return Objects.equals(id, other.id) && Objects.equals(parentData, other.parentData);
		}
		
		@Override
		public int hashCode() {
			return Objects.hash(id, parentData);
		}
		
	}
	
	@Doc
	public static class DataWithMap {
		
		@ID
		private final String id;
		
		@JsonIgnore
		Map<String, Object> properties;

		/*JSON deserialization*/
		@JsonCreator
		DataWithMap(@JsonProperty("id") String id) {
			this.id = id;
		}
		
		public DataWithMap(String id, Map<String, Object> properties) {
			this.id = id;
			this.properties = properties;
		}
		
		public String getId() {
			return id;
		}
		
		@JsonAnyGetter
		public Map<String, Object> getProperties() {
			return properties;
		}
		
		@JsonAnySetter
		void setProperties(String key, Object value) {
			if (properties == null) {
				properties = Maps.newHashMap();
			}
			properties.put(key, value);
		}
		
		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;
			DataWithMap other = (DataWithMap) obj;
			return Objects.equals(id, other.id) && Objects.equals(properties, other.properties);
		}
		
		@Override
		public int hashCode() {
			return Objects.hash(id, properties);
		}
		
	}
	
}
