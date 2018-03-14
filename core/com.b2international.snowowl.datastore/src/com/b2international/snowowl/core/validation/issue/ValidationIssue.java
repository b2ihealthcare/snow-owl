/*
 * Copyright 2017-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.validation.issue;

import static com.google.common.collect.Maps.newHashMap;

import java.io.Serializable;
import java.util.Map;

import com.b2international.index.Doc;
import com.b2international.index.Script;
import com.b2international.snowowl.core.ComponentIdentifier;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

/**
 * @since 6.0
 */
@Doc
@Script(name = ValidationIssue.Scripts.WHITELIST, script="ctx._source.whitelisted = params.whitelisted")
public final class ValidationIssue implements Serializable {

	public static class Fields {
		public static final String ID = "id";
		public static final String RULE_ID = "ruleId";
		public static final String BRANCH_PATH = "branchPath";
		public static final String AFFECTED_COMPONENT_ID = "affectedComponentId";
		public static final String AFFECTED_COMPONENT_TYPE = "affectedComponentType";
		public static final String WHITELISTED = "whitelisted";
		public static final String DETAILS = "details";
	}

	public static class Scripts {
		public static final String WHITELIST = "whitelist";
	}
	
	private final String id;
	private final String ruleId;
	private final String branchPath;
	private final String affectedComponentId;
	private final short affectedComponentType;
	private final boolean whitelisted;
	
	private Map<String, Object> details = newHashMap();
	
	private transient ComponentIdentifier affectedComponent;

	public ValidationIssue(
			final String id,
			final String ruleId, 
			final String branchPath, 
			final ComponentIdentifier affectedComponent,
			final boolean whitelisted) {
		this(id, ruleId, branchPath, affectedComponent.getTerminologyComponentId(), affectedComponent.getComponentId(), whitelisted);
	}
	
	@JsonCreator
	public ValidationIssue(
			@JsonProperty("id") final String id,
			@JsonProperty("ruleId") final String ruleId, 
			@JsonProperty("branchPath") final String branchPath, 
			@JsonProperty("affectedComponentType") final short affectedComponentType,
			@JsonProperty("affectedComponentId") final String affectedComponentId,
			@JsonProperty("whitelisted") final boolean whitelisted) {
		this.id = id;
		this.ruleId = ruleId;
		this.branchPath = branchPath;
		this.affectedComponentId = affectedComponentId;
		this.affectedComponentType = affectedComponentType;
		this.whitelisted = whitelisted;
	}
	
	public String getId() {
		return id;
	}
	
	@JsonIgnore
	public ComponentIdentifier getAffectedComponent() {
		if (affectedComponent == null) {
			affectedComponent = ComponentIdentifier.of(affectedComponentType, affectedComponentId);
		}
		return affectedComponent;
	}
	
	@JsonProperty
	String getAffectedComponentId() {
		return affectedComponentId;
	}
	
	@JsonProperty
	short getAffectedComponentType() {
		return affectedComponentType;
	}
	
	public String getBranchPath() {
		return branchPath;
	}
	
	public String getRuleId() {
		return ruleId;
	}
	
	public boolean isWhitelisted() {
		return whitelisted;
	}
	
	@JsonAnyGetter
	public Map<String, Object> getDetails() {
		return details;
	}
	
	@JsonAnySetter
	public void setDetails(String key, Object value) {
		this.details.put(key, value);
	}
	
	public void setDetails(Map<String, Object> details) {
		this.details.putAll(details);
	}
	
	@Override
	public String toString() {
		return MoreObjects.toStringHelper(getClass())
				.add("id", id)
				.add("ruleId", ruleId)
				.add("branchPath", branchPath)
				.add("affectedComponent", getAffectedComponent())
				.add("details", getDetails())
				.toString();
	}
	
}
