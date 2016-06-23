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

import static com.google.common.collect.Lists.newArrayList;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Ordering;

/**
 * @since 4.7
 */
@JsonDeserialize(builder = MergeConflictImpl.Builder.class)
public class MergeConflictImpl implements MergeConflict {

	public static final String ATTRIBUTE_KEY_VALUE_TEMPLATE = "%s -> %s";
	
	private static final String ATTRIBUTE_SEPARATOR = "; ";
	private static final String END_OF_LINE_CHAR = ".";
	private static final String DEFAULT_ATTRIBUTES_MESSAGE = ", conflicting attributes are: [%s].";
	private static final String DEFAULT_CONFLICT_MESSAGE = "%s with ID '%s' has a conflict of type '%s' on target branch%s";

	private String artefactId;
	private String artefactType;
	private List<String> conflictingAttributes;
	private ConflictType type;
	private String message;

	public static Builder builder() {
		return new Builder();
	}

	public static String buildDefaultMessage(String artefactId, String artefactType, List<String> conflictingAttributes, ConflictType type) {
		String attributes = buildAttributesMessage(conflictingAttributes);
		return String.format(DEFAULT_CONFLICT_MESSAGE, artefactType, artefactId, type, Strings.isNullOrEmpty(attributes) ? END_OF_LINE_CHAR : String.format(DEFAULT_ATTRIBUTES_MESSAGE, attributes));
	}
	
	public static List<String> buildAttributeList(Map<String, String> attributes) {
		return FluentIterable.from(attributes.entrySet()).transform(new Function<Entry<String, String>, String>() {
			@Override public String apply(Entry<String, String> input) {
				return String.format(ATTRIBUTE_KEY_VALUE_TEMPLATE, input.getKey(), input.getValue());
			}
		}).toSortedList(Ordering.natural());
	}

	private static String buildAttributesMessage(List<String> conflictingAttributes) {
		return Joiner.on(ATTRIBUTE_SEPARATOR).join(conflictingAttributes);
	}

	private MergeConflictImpl(String artefactId, String artefactType, List<String> conflictingAttributes, ConflictType type, String message) {
		this.artefactId = artefactId;
		this.artefactType = artefactType;
		this.conflictingAttributes = conflictingAttributes;
		this.type = type;
		this.message = message;
	}

	@Override
	public String getArtefactId() {
		return artefactId;
	}

	@Override
	public String getArtefactType() {
		return artefactType;
	}

	@Override
	public List<String> getConflictingAttributes() {
		return conflictingAttributes;
	}

	@Override
	public ConflictType getType() {
		return type;
	}

	@Override
	public String getMessage() {
		return message;
	}

	@JsonPOJOBuilder(buildMethodName="build", withPrefix = "with")
	public static class Builder {

		private String artefactId;
		private String artefactType;
		private List<String> conflictingAttributes;
		private ConflictType type;
		private String message;

		@JsonCreator
		public Builder() {
			this.conflictingAttributes = newArrayList();
		}

		public Builder withArtefactId(String id) {
			this.artefactId = id;
			return this;
		}

		public Builder withArtefactType(String type) {
			this.artefactType = type;
			return this;
		}

		public Builder withConflictingAttribute(String key, String value) {
			this.conflictingAttributes.add(String.format(ATTRIBUTE_KEY_VALUE_TEMPLATE, key, value));
			return this;
		}
		
		public Builder withConflictingAttributes(List<String> attributes) {
			this.conflictingAttributes.addAll(attributes);
			return this;
		}

		public Builder withType(ConflictType type) {
			this.type = type;
			return this;
		}

		public Builder withMessage(String message) {
			this.message = message;
			return this;
		}

		public MergeConflictImpl build() {
			if (Strings.isNullOrEmpty(message)) {
				this.message = MergeConflictImpl.buildDefaultMessage(this.artefactId, this.artefactType, this.conflictingAttributes, this.type);
			}
			Collections.sort(this.conflictingAttributes);
			return new MergeConflictImpl(this.artefactId, this.artefactType, this.conflictingAttributes, this.type, this.message);
		}
	}

}
