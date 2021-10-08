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
package com.b2international.snowowl.core.merge;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.common.base.Strings;

/**
 * @since 4.7
 */
@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = ConflictingAttribute.Builder.class)
public final class ConflictingAttribute implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final Comparator<ConflictingAttribute> ATTRIBUTE_COMPARATOR = (o1, o2) -> o1.toDisplayName().compareTo(o2.toDisplayName());
	
	private static final String CONFLICT_TEMPLATE = "%s -> %s vs. %s (old value: %s)";
	private static final String SINGLE_ATTR_TEMPLATE = "%s -> %s (old value: %s)";
	
	private final String property;
	private final String sourceValue;
	private final String targetValue;
	private final String oldValue;

	private ConflictingAttribute(String property, String sourceValue, String targetValue, String oldValue) {
		this.property = property;
		this.sourceValue = sourceValue;
		this.targetValue = targetValue;
		this.oldValue = oldValue;
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	/**
	 * String representation of the attribute that induced - or was involved in - the conflict
	 * 
	 * @return
	 */
	public String getProperty() {
		return property;
	}

	/**
	 * @return the value of the property on the source side of changes
	 */
	public String getSourceValue() {
		return sourceValue;
	}
	
	/**
	 * @return the value of the property on the target side of changes
	 */
	public String getTargetValue() {
		return targetValue;
	}

	/**
	 * If the property was changed and it is possible to extract the old value, then this will return it.
	 * 
	 * @return
	 */
	public String getOldValue() {
		return oldValue;
	}
	
	/**
	 * Converts a {@link ConflictingAttribute} instance to a human readable form. Default conflict message uses this pattern as well.
	 * 
	 * @return
	 */
	public String toDisplayName() {
		if (Strings.isNullOrEmpty(getSourceValue())) {
			return property;
		} else if (Strings.isNullOrEmpty(getTargetValue())) {
			return String.format(SINGLE_ATTR_TEMPLATE, getProperty(), getSourceValue(), Optional.ofNullable(Strings.emptyToNull(oldValue)).orElse("n/a"));
		} else {
			return String.format(CONFLICT_TEMPLATE, getProperty(), getSourceValue(), getTargetValue(), Optional.ofNullable(Strings.emptyToNull(oldValue)).orElse("n/a"));
		}
	}
	
	@JsonPOJOBuilder(withPrefix = "")
	public static class Builder {
		
		private String property;
		private String sourceValue;
		private String targetValue;
		private String oldValue;

		@JsonCreator
		public Builder() {
		}
		
		public Builder property(String property) {
			this.property = property; 
			return this;
		}

		public Builder sourceValue(String sourceValue) {
			this.sourceValue = sourceValue;
			return this;
		}
		
		public Builder targetValue(String targetValue) {
			this.targetValue = targetValue;
			return this;
		}

		public Builder oldValue(String oldValue) {
			this.oldValue = oldValue;
			return this;
		}
		
		public ConflictingAttribute build() {
			return new ConflictingAttribute(this.property, this.sourceValue, this.targetValue, this.oldValue);
		}
		
	}
	
}
