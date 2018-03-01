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
package com.b2international.snowowl.core.validation.whitelist;

import java.io.Serializable;

import com.b2international.index.Doc;
import com.b2international.snowowl.core.ComponentIdentifier;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

/**
 * @since 6.1
 */
@Doc
public final class ValidationWhiteList implements Serializable {

	/**
	 * @since 6.1
	 */
	public static final class Fields {
		public static final String ID = "id";
		public static final String RULE_ID = "ruleId";
		public static final String COMPONENT_ID = "componentId";
		public static final String TERMINOLOGY_COMPONENT_ID = "terminologyComponentId";
		public static final String REPORTER = "reporter";
		public static final String CREATED_AT = "createdAt";
	}
	
	private final String id;
	private final String ruleId;
	private final String reporter;
	private final long createdAt;
	private final ComponentIdentifier componentIdentifier;
	
	public ValidationWhiteList(
			final String id,
			final String ruleId,
			final String reporter,
			final long createdAt,
			final ComponentIdentifier componentIdentifier) {
		this.id = id;
		this.ruleId = ruleId;
		this.componentIdentifier = componentIdentifier;
		this.reporter = reporter;
		this.createdAt = createdAt;
	}

	@JsonCreator
	public ValidationWhiteList(
			@JsonProperty("id") final String id,
			@JsonProperty("ruleId") final String ruleId,
			@JsonProperty("reporter") final String reporter,
			@JsonProperty("createdAt") final long createdAt,
			@JsonProperty("terminologyComponentId") final short terminologyComponentId,
			@JsonProperty("componentId") final String componentId) {
		this(id, ruleId, reporter, createdAt, ComponentIdentifier.of(terminologyComponentId, componentId));
	}
	
	public String getId() {
		return id;
	}

	public String getRuleId() {
		return ruleId;
	}
	
	@JsonIgnore
	public ComponentIdentifier getComponentIdentifier() {
		return componentIdentifier;
	}
	
	@JsonProperty
	String getComponentId() {
		return componentIdentifier.getComponentId();
	}
	
	@JsonProperty
	short getTerminologyComponentId() {
		return componentIdentifier.getTerminologyComponentId();
	}

	public String getReporter() {
		return reporter;
	}
	
	public long getCreatedAt() {
		return createdAt;
	}
	
	@Override
	public String toString() {
		return MoreObjects.toStringHelper(getClass())
			.add("id", id)
			.add("ruleId", ruleId)
			.add("componentIdentifier", componentIdentifier)
			.add("reporter", reporter)
			.add("createdAt", createdAt)
			.toString();
	}
	
}