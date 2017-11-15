/*
 * Copyright 2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.validation.rule;

import com.b2international.commons.StringUtils;
import com.b2international.index.Doc;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

/**
 * @since 6.0
 */
@Doc
public final class ValidationRule {

	public enum Type {
		SCRIPT,
		QUERY
	}
	
	/**
	 * @since 6.0
	 */
	public enum Severity {
		ERROR,
		WARNING
	}
	
	/**
	 * @since 6.0
	 */
	public static final class Fields {
		public static final String ID = "id";
		public static final String MESSAGE_TEMPLATE = "messageTemplate";
		public static final String SEVERITY = "severity";
		public static final String TYPE = "type";
	}

	private final String id;
	private final String messageTemplate;
	private final Severity severity;
	private final Type type;
	private final String implementation;
	
	@JsonCreator
	public ValidationRule(
			@JsonProperty("id") final String id,
			@JsonProperty("messageTemplate") final String messageTemplate,
			@JsonProperty("severity") final Severity severity,
			@JsonProperty("type") final Type type,
			@JsonProperty("implementation") final String implementation
			) {
		this.id = id;
		this.messageTemplate = messageTemplate;
		this.severity = severity;
		this.type = type;
		this.implementation = implementation;
	}
	
	public String getId() {
		return id;
	}
	
	public String getMessageTemplate() {
		return messageTemplate;
	}
	
	public Severity getSeverity() {
		return severity;
	}
	
	public Type getType() {
		return type;
	}
	
	public String getImplementation() {
		return implementation;
	}
	
	@Override
	public String toString() {
		return MoreObjects.toStringHelper(getClass())
				.add("id", id)
				.add("messageTemplate", messageTemplate)
				.add("severity", severity)
				.add("type", type)
				.add("implementation", StringUtils.truncate(implementation))
				.toString();
	}
	
}
