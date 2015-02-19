/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.datastore.oplock.impl.DatastoreLockContextDescriptions;
import com.b2international.snowowl.snomed.reasoner.model.ConceptDefinition;
import com.google.common.base.Objects;
import com.google.common.collect.Lists;

/**
 */
public class ClassificationRequest implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private final UUID classificationId = UUID.randomUUID();
	private final String lockUserId;
	private final IBranchPath snomedBranchPath;
	
	private final List<ConceptDefinition> additionalDefinitions = Lists.newArrayList();
	private String parentContextDescription = DatastoreLockContextDescriptions.CLASSIFY_WITH_REVIEW;
	private String reasonerId;
	
	public ClassificationRequest(final String lockUserId, final IBranchPath snomedBranchPath) {
		checkNotNull(lockUserId, "Lock user identifier may not be null.");
		checkNotNull(snomedBranchPath, "SNOMED CT branch path may not be null.");
		
		this.lockUserId = lockUserId;
		this.snomedBranchPath = snomedBranchPath;
	}
	
	public ClassificationRequest withAdditionalDefinitions(final List<ConceptDefinition> additionalDefinitions) {
		checkNotNull(additionalDefinitions, "Additional concept definition list may not be null.");
		
		this.additionalDefinitions.clear();
		this.additionalDefinitions.addAll(additionalDefinitions);
		return this;
	}

	public ClassificationRequest withParentContextDescription(final String parentContextDescription) {
		checkNotNull(parentContextDescription, "Parent context description may not be null.");
		
		this.parentContextDescription = parentContextDescription;
		return this;
	}

	public ClassificationRequest withReasonerId(final @Nullable String reasonerId) {
		this.reasonerId = reasonerId;
		return this;
	}

	public List<ConceptDefinition> getAdditionalDefinitions() {
		return additionalDefinitions;
	}

	public String getParentContextDescription() {
		return parentContextDescription;
	}

	public UUID getClassificationId() {
		return classificationId;
	}

	public String getLockUserId() {
		return lockUserId;
	}

	public IBranchPath getSnomedBranchPath() {
		return snomedBranchPath;
	}
	
	public String getReasonerId() {
		return reasonerId;
	}
	
	@Override
	public String toString() {
		return Objects.toStringHelper(this)
				.add("classificationId", classificationId)
				.add("lockUserId", lockUserId)
				.add("snomedBranchPath", snomedBranchPath)
				.add("additionalDefinitions", additionalDefinitions)
				.add("parentContextDescription", parentContextDescription)
				.add("reasonerId", reasonerId)
				.toString();
	}
}