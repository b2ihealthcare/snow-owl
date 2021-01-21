/*
 * Copyright 2011-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.google.common.collect.Lists.newArrayList;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import com.b2international.commons.collections.Collections3;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.FluentIterable;

/**
 * @since 4.7
 */
@JsonDeserialize(builder = MergeConflict.Builder.class)
public final class MergeConflict implements Serializable {

	public static enum ConflictType {
		CONFLICTING_CHANGE,
		DELETED_WHILE_CHANGED,
		CHANGED_WHILE_DELETED,
		HAS_MISSING_REFERENCE,
		CAUSES_MISSING_REFERENCE
	}
	
	private static final String ATTRIBUTE_SEPARATOR = "; ";
	private static final String END_OF_LINE_CHAR = ".";
	private static final String DEFAULT_ATTRIBUTES_MESSAGE = ", conflicting attributes are: [%s].";
	private static final String DEFAULT_CONFLICT_MESSAGE = "%s with ID '%s' has a conflict of type '%s' on target branch%s";

	public static Builder builder() {
		return new Builder();
	}
	
	public static Builder builder(MergeConflict copy) {
		return new Builder()
					.componentId(copy.getComponentId())
					.componentType(copy.getComponentType())
					.conflictingAttributes(copy.getConflictingAttributes())
					.type(copy.getType());
	}

	public static String buildDefaultMessage(String componentId, String componentType, List<ConflictingAttribute> conflictingAttributes, ConflictType type) {
		return String.format(DEFAULT_CONFLICT_MESSAGE, componentType, componentId, type, 
				conflictingAttributes.isEmpty() ? END_OF_LINE_CHAR : String.format(DEFAULT_ATTRIBUTES_MESSAGE, buildAttributes(conflictingAttributes)));
	}
	
	private static String buildAttributes(List<ConflictingAttribute> conflictingAttributes) {
		return Joiner.on(ATTRIBUTE_SEPARATOR).join(FluentIterable.from(conflictingAttributes).transform(new Function<ConflictingAttribute, String>() {
			@Override public String apply(ConflictingAttribute attribute) {
				return attribute.toDisplayName();
			}
		}).toList());
	}
	
	private final String componentId;
	private final String componentType;
	private final List<ConflictingAttribute> conflictingAttributes;
	private final ConflictType type;
	private final String message;
	
	private MergeConflict(String componentId, String componentType, List<ConflictingAttribute> conflictingAttributes, ConflictType type, String message) {
		this.componentId = componentId;
		this.componentType = componentType;
		this.conflictingAttributes = Collections3.toImmutableList(conflictingAttributes);
		this.type = type;
		this.message = message;
	}

	/**
	 * Returns the unique identifier of the component that induced this merge conflict.
	 * 
	 * @return unique identifier of the component
	 */
	public String getComponentId() {
		return componentId;
	}

	/**
	 * Returns the type of the component that induced this merge conflict.
	 * 
	 * @return the type of the component
	 */
	public String getComponentType() {
		return componentType;
	}

	/**
	 * Returns a list of attributes represented as {@link String}s, all of which was involved in causing the conflict.
	 * 
	 * @return the list of attributes involved in causing the conflict
	 */
	public List<ConflictingAttribute> getConflictingAttributes() {
		return conflictingAttributes;
	}

	/**
	 * Returns the type of the conflict, which reflects the nature of problem causing the merge to fail.
	 * 
	 * @return the conflict type
	 */
	public ConflictType getType() {
		return type;
	}

	/**
	 * Returns an interpretable message about the conflict, built using it's properties.
	 * 
	 * @return a summarizing, interpretable message about the conflict
	 */
	public String getMessage() {
		return message;
	}
	
	@Override
	public String toString() {
		return message;
	}

	@JsonPOJOBuilder(withPrefix = "")
	public static class Builder {

		private String componentId;
		private String componentType;
		private List<ConflictingAttribute> conflictingAttributes;
		private ConflictType type;
		private String message;

		@JsonCreator
		public Builder() {
			this.conflictingAttributes = newArrayList();
		}

		public Builder componentId(String id) {
			this.componentId = id;
			return this;
		}

		public Builder componentType(String type) {
			this.componentType = type;
			return this;
		}

		public Builder conflictingAttribute(ConflictingAttribute attribute) {
			this.conflictingAttributes.add(attribute);
			return this;
		}
		
		public Builder conflictingAttributes(List<ConflictingAttribute> attributes) {
			this.conflictingAttributes.addAll(attributes);
			return this;
		}

		public Builder type(ConflictType type) {
			this.type = type;
			return this;
		}

		public Builder message(String message) {
			this.message = message;
			return this;
		}

		public MergeConflict build() {
			
			if (this.conflictingAttributes.size() > 1) {
				Collections.sort(this.conflictingAttributes, ConflictingAttributeImpl.ATTRIBUTE_COMPARATOR);
			}
			
			if (Strings.isNullOrEmpty(message)) {
				this.message = MergeConflict.buildDefaultMessage(this.componentId, this.componentType, this.conflictingAttributes, this.type);
			}
			
			return new MergeConflict(this.componentId, this.componentType, this.conflictingAttributes, this.type, this.message);
		}
	}
	
}
