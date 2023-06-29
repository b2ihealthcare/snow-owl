/*
 * Copyright 2011-2023 B2i Healthcare Pte Ltd, http://b2i.sg
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

import com.b2international.collections.longs.LongSortedSet;
import com.b2international.index.mapping.Field;
import com.b2international.index.mapping.FieldAlias;
import com.b2international.index.mapping.FieldAlias.FieldAliasType;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

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

		@Field(
			aliases = {
				@FieldAlias(name = "text", type = FieldAliasType.TEXT, analyzer=Analyzers.CASE_SENSITIVE),
				@FieldAlias(name = "exact", type = FieldAliasType.KEYWORD)
			} 
		)
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
		
		@Field(index = false)
		private String unindexedValue;
		
		private LongSortedSet longSortedSet;

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
		
		public LongSortedSet getLongSortedSet() {
			return longSortedSet;
		}
		
		public void setLongSortedSet(LongSortedSet longSortedSet) {
			this.longSortedSet = longSortedSet;
		}
		
		public String getUnindexedValue() {
			return unindexedValue;
		}
		
		public void setUnindexedValue(String unindexedValue) {
			this.unindexedValue = unindexedValue;
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
					&& Objects.equals(longSortedSet, other.longSortedSet)
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
				shortWrapper,
				longSortedSet
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
					.add("longSortedSet", longSortedSet)
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
	public static class MultipleNestedData implements WithScore {
		
		@ID
		String id;
		String field1 = "field1";
		Collection<NestedData> nestedDatas = newHashSet();
		float score;
		
		@JsonCreator
		public MultipleNestedData(@JsonProperty("id") String id, @JsonProperty("nestedDatas") Collection<NestedData> nestedDatas) {
			this.id = id;
			this.nestedDatas.addAll(nestedDatas);
		}
		
		@Override
		public float getScore() {
			return score;
		}
		
		@Override
		public void setScore(float score) {
			this.score = score;
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
		
		@Field(aliases = @FieldAlias(name = "text", analyzer = Analyzers.TOKENIZED, type = FieldAliasType.TEXT))
		String analyzedField;

		public NestedData(String field2) {
			this(field2, null);
		}
		
		@JsonCreator
		public NestedData(@JsonProperty("field2") String field2, @JsonProperty("analyzedField") String analyzedField) {
			this.field2 = field2;
			this.analyzedField = analyzedField;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;
			NestedData other = (NestedData) obj;
			return Objects.equals(field2, other.field2) && Objects.equals(analyzedField, other.analyzedField);
		}
		
		@Override
		public int hashCode() {
			return Objects.hash(field2, analyzedField);
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
		
		private final Map<String, Object> properties;

		@JsonCreator
		public DataWithMap(@JsonProperty("id") String id, @JsonProperty("properties") Map<String, Object> properties) {
			this.id = id;
			this.properties = properties;
		}
		
		public String getId() {
			return id;
		}
		
		public Map<String, Object> getProperties() {
			return properties;
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
	
	@Doc
	@Script(name = "append", script = "ctx._source.value += params.value")
	public static class DataWithUpdateScript {

		@ID
		String id;
		String value;

		@JsonCreator
		public DataWithUpdateScript(
			final @JsonProperty("id") String id,
			final @JsonProperty("field2") String value) {
			
			this.id = id;
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;
		
			final DataWithUpdateScript other = (DataWithUpdateScript) obj;
			return Objects.equals(id, other.id) 
				&& Objects.equals(value, other.value);
		}
		
		@Override
		public int hashCode() {
			return Objects.hash(id, value);
		}
	}
}
