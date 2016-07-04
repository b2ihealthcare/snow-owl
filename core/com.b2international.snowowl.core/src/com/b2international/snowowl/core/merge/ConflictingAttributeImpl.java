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
package com.b2international.snowowl.core.merge;

import java.util.Comparator;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.common.base.Strings;

/**
 * @since 4.7
 */
@JsonDeserialize(builder = ConflictingAttributeImpl.Builder.class)
@JsonInclude(Include.NON_NULL)
public class ConflictingAttributeImpl implements ConflictingAttribute {

	public static final Comparator<ConflictingAttribute> ATTRIBUTE_COMPARATOR = new Comparator<ConflictingAttribute>() {
		@Override public int compare(ConflictingAttribute o1, ConflictingAttribute o2) {
			return o1.toDisplayName().compareTo(o2.toDisplayName());
		}
	};
	
	private static final String SINGLE_VALUE_TEMPLATE = "%s -> %s";
	private static final String MULTI_VALUE_TEMPLATE = "%s -> old value: %s, value: %s";
	
	private String property;
	private String value;
	private String oldValue;

	private ConflictingAttributeImpl(String property, String value, String oldValue) {
		this.property = property;
		this.value = value;
		this.oldValue = oldValue;
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	@Override
	public String getProperty() {
		return property;
	}

	@Override
	public String getValue() {
		return value;
	}

	@Override
	public String getOldValue() {
		return oldValue;
	}
	
	@Override
	public String toDisplayName() {
		if (!Strings.isNullOrEmpty(getValue()) && !Strings.isNullOrEmpty(getOldValue())) {
			return String.format(MULTI_VALUE_TEMPLATE, getProperty(), getOldValue(), getValue());
		} else if (!Strings.isNullOrEmpty(getOldValue())) {
			return String.format(SINGLE_VALUE_TEMPLATE, getProperty(), getOldValue());
		} else if (!Strings.isNullOrEmpty(getValue())) {
			return String.format(SINGLE_VALUE_TEMPLATE, getProperty(), getValue());
		}
		return getProperty();
	}
	
	@JsonPOJOBuilder(buildMethodName="build", withPrefix = "")
	public static class Builder {
		
		private String property;
		private String value;
		private String oldValue;

		@JsonCreator
		public Builder() {
		}
		
		public Builder property(String property) {
			this.property = property; 
			return this;
		}

		public Builder value(String value) {
			this.value = value;
			return this;
		}

		public Builder oldValue(String oldValue) {
			this.oldValue = oldValue;
			return this;
		}
		
		public ConflictingAttributeImpl build() {
			return new ConflictingAttributeImpl(this.property, this.value, this.oldValue);
		}
		
	}
}
