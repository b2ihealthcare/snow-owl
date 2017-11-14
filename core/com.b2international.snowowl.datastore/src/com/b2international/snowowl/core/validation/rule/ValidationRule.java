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

import com.b2international.index.Doc;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 6.0
 */
@Doc
public final class ValidationRule {

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
	}

	private final String id;
	private final String messageTemplate;
	private final Severity severity;
	
	@JsonCreator
	public ValidationRule(
			@JsonProperty("id") final String id,
			@JsonProperty("messageTemplate") final String messageTemplate,
			@JsonProperty("severity") final Severity severity
			) {
		this.id = id;
		this.messageTemplate = messageTemplate;
		this.severity = severity;
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
	
}
