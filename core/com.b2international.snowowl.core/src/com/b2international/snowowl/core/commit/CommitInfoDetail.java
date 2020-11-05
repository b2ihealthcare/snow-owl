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
package com.b2international.snowowl.core.commit;

import java.io.Serializable;

import com.b2international.commons.ChangeKind;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

/**
 * @since 6.6
 */
@JsonDeserialize(builder=CommitInfoDetail.Builder.class)
public final class CommitInfoDetail implements Serializable {
	
	public static Builder builder() {
		return new Builder();
	}
	
	@JsonPOJOBuilder(withPrefix="")
	public static class Builder {
		
		private ChangeKind changeKind;
		private String objectType;
		private String object;
		private String property;
		private String fromValue;
		private String valueType;
		private String value;
		
		Builder() {
		}
		
		public Builder changeKind(ChangeKind changeKind) {
			this.changeKind = changeKind;
			return this;
		}

		public Builder objectType(String objectType) {
			this.objectType = objectType;
			return this;
		}
		
		public Builder object(String object) {
			this.object = object;
			return this;
		}
		
		public Builder property(String property) {
			this.property = property;
			return this;
		}
		
		public Builder fromValue(String fromValue) {
			this.fromValue = fromValue;
			return this;
		}
		
		public Builder valueType(String valueType) {
			this.valueType = valueType;
			return this;
		}
		
		public Builder value(String value) {
			this.value = value;
			return this;
		}
		
		public CommitInfoDetail build() {
			return new CommitInfoDetail(changeKind, objectType, object, property, fromValue, valueType, value);
		}
		
	}
	
	private final ChangeKind changeKind;
	private final String objectType;
	private final String object;
	private final String property;
	private final String fromValue;
	private final String valueType;
	private final String value;
	
	private CommitInfoDetail(ChangeKind changeKind, 
			String objectType, 
			String object, 
			String property, 
			String fromValue,
			String valueType, 
			String value) {
		
		this.changeKind = changeKind;
		this.objectType = objectType;
		this.object = object;
		this.property = property;
		this.fromValue = fromValue;
		this.valueType = valueType;
		this.value = value;
	}
	
	public ChangeKind getChangeKind() {
		return changeKind;
	}
	
	public String getObjectType() {
		return objectType;
	}
	
	public String getObject() {
		return object;
	}
	
	public String getProperty() {
		return property;
	}
	
	public String getFromValue() {
		return fromValue;
	}
	
	public String getValueType() {
		return valueType;
	}
	
	public String getValue() {
		return value;
	}

	@Override
	public String toString() {
		return "CommitInfoDetail [changeKind=" + changeKind + ", objectType=" + objectType + ", object=" + object
				+ ", property=" + property + ", fromValue=" + fromValue + ", valueType=" + valueType + ", value="
				+ value + "]";
	}
	
}
