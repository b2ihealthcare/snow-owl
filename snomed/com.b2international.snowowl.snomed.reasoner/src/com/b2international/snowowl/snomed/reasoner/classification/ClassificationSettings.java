/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.reasoner.classification;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;

import java.io.Serializable;
import java.util.List;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.datastore.oplock.impl.DatastoreLockContextDescriptions;
import com.b2international.snowowl.snomed.reasoner.model.ConceptDefinition;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.MoreObjects;

/**
 * Carries all parameters required for starting a classification for a branch.
 */
public class ClassificationSettings implements Serializable {

	private static final long serialVersionUID = 1L;

	private final String classificationId;// = UUID.randomUUID().toString();
	private final String userId;
	private final String branchPath;
	private final List<ConceptDefinition> additionalDefinitions = newArrayList();
	
	private String parentContextDescription = DatastoreLockContextDescriptions.CLASSIFY_WITH_REVIEW;
	private String reasonerId;
	
	public ClassificationSettings(String userId, IBranchPath branchPath) {
		checkNotNull(userId, "User identifier may not be null.");
		checkNotNull(branchPath, "SNOMED CT branch path may not be null.");
		this.userId = userId;
		this.branchPath = branchPath.getPath();
		this.classificationId = "Classification_" + branchPath.getPath();
	}
	
	public ClassificationSettings withAdditionalDefinitions(List<ConceptDefinition> additionalDefinitions) {
		checkNotNull(additionalDefinitions, "Additional concept definition list may not be null.");
		
		this.additionalDefinitions.clear();
		this.additionalDefinitions.addAll(additionalDefinitions);
		return this;
	}

	public ClassificationSettings withParentContextDescription(String parentContextDescription) {
		checkNotNull(parentContextDescription, "Parent context description may not be null.");
		
		this.parentContextDescription = parentContextDescription;
		return this;
	}

	public ClassificationSettings withReasonerId(String reasonerId) {
		this.reasonerId = reasonerId;
		return this;
	}

	public String getClassificationId() {
		return classificationId;
	}
	
	@JsonIgnore
	public List<ConceptDefinition> getAdditionalDefinitions() {
		return additionalDefinitions;
	}

	public String getParentContextDescription() {
		return parentContextDescription;
	}

	public String getUserId() {
		return userId;
	}

	public String getBranchPath() {
		return branchPath;
	}
	
	public String getReasonerId() {
		return reasonerId;
	}
	
	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("classificationId", classificationId)
				.add("userId", userId)
				.add("branchPath", branchPath)
				.add("additionalDefinitions", additionalDefinitions)
				.add("parentContextDescription", parentContextDescription)
				.add("reasonerId", reasonerId)
				.toString();
	}
}
