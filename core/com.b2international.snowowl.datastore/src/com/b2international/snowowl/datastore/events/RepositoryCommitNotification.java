/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore.events;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.Set;

import com.b2international.commons.collections.Collections3;
import com.b2international.snowowl.core.ComponentIdentifier;
import com.b2international.snowowl.core.events.RepositoryEvent;

/**
 * @since 5.0
 */
public final class RepositoryCommitNotification extends RepositoryEvent {
	
	private static final long serialVersionUID = 1L;
	
	private final String commitId;
	private final String groupId;
	private final String branchPath;
	private final long commitTimestamp;
	private final String userId;
	private final String comment;
	private final Set<ComponentIdentifier> newComponents;
	private final Set<ComponentIdentifier> changedComponents;
	private final Set<ComponentIdentifier> deletedComponents;

	public RepositoryCommitNotification(final String repositoryId,
			final String commitId,
			final String groupId,
			final String branchPath,
			final long commitTimestamp,
			final String userId,
			final String comment,
			final Collection<ComponentIdentifier> newComponents, 
			final Collection<ComponentIdentifier> changedComponents, 
			final Collection<ComponentIdentifier> deletedComponents) {
		super(repositoryId);
		this.commitId = checkNotNull(commitId, "Commit ID argument cannot be null");
		this.groupId = groupId;
		this.branchPath = branchPath;
		this.commitTimestamp = commitTimestamp;
		this.userId = userId;
		this.comment = comment;
		this.newComponents = Collections3.toImmutableSet(newComponents);
		this.changedComponents = Collections3.toImmutableSet(changedComponents);
		this.deletedComponents = Collections3.toImmutableSet(deletedComponents);
	}
	
	public String getCommitId() {
		return commitId;
	}
	
	public String getGroupId() {
		return groupId;
	}

	public String getBranchPath() {
		return branchPath;
	}
	
	public long getCommitTimestamp() {
		return commitTimestamp;
	}
	
	public String getUserId() {
		return userId;
	}
	
	public String getComment() {
		return comment;
	}
	
	public Set<ComponentIdentifier> getNewComponents() {
		return newComponents;
	}
	
	public Set<ComponentIdentifier> getChangedComponents() {
		return changedComponents;
	}
	
	public Set<ComponentIdentifier> getDeletedComponents() {
		return deletedComponents;
	}
	
}
