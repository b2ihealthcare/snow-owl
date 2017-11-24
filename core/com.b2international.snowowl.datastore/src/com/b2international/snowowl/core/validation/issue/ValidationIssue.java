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
package com.b2international.snowowl.core.validation.issue;

import java.io.Serializable;

import com.b2international.index.Doc;
import com.b2international.snowowl.core.ComponentIdentifier;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

/**
 * @since 6.0
 */
@Doc
public final class ValidationIssue implements Serializable {

	public static class Fields {
		public static final String ID = "id";
		public static final String RULE_ID = "ruleId";
		public static final String BRANCH_PATH = "branchPath";
	}
	
	private final String id;
	private final String ruleId;
	private final String branchPath;
	private final ComponentIdentifier affectedComponent;

	@JsonCreator
	public ValidationIssue(
			@JsonProperty("id") final String id,
			@JsonProperty("ruleId") final String ruleId, 
			@JsonProperty("branchPath") final String branchPath, 
			@JsonProperty("affectedComponent") final ComponentIdentifier affectedComponent) {
		this.id = id;
		this.ruleId = ruleId;
		this.branchPath = branchPath;
		this.affectedComponent = affectedComponent;
	}
	
	public String getId() {
		return id;
	}
	
	public ComponentIdentifier getAffectedComponent() {
		return affectedComponent;
	}
	
	public String getBranchPath() {
		return branchPath;
	}
	
	public String getRuleId() {
		return ruleId;
	}
	
	@Override
	public String toString() {
		return MoreObjects.toStringHelper(getClass())
				.add("id", id)
				.add("ruleId", ruleId)
				.add("branchPath", branchPath)
				.add("affectedComponent", affectedComponent)
				.toString();
	}
	
}
