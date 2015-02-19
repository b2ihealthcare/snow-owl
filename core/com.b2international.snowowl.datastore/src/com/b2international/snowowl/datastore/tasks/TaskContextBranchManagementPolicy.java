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
package com.b2international.snowowl.datastore.tasks;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * {@link ITaskContextBranchManagementPolicy} implementation.
 * 
 */
public class TaskContextBranchManagementPolicy implements ITaskContextBranchManagementPolicy {

	private final Iterable<String> repositoryUuids;

	public TaskContextBranchManagementPolicy(final String repositoryUuid, final String... others) {
		this(Lists.asList(Preconditions.checkNotNull(repositoryUuid, "Repository UUIDs argument cannot be null."), others));
	}
	
	public TaskContextBranchManagementPolicy(final Iterable<String> repositoryUuids) {
		this.repositoryUuids = Sets.newHashSet(Preconditions.checkNotNull(repositoryUuids, "Repository UUIDs argument cannot be null."));
	}
	
	/* (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.tasks.ITaskContextBranchManagementPolicy#getRepositoryUuids()
	 */
	@Override
	public Iterable<String> getRepositoryUuids() {
		return Iterables.unmodifiableIterable(repositoryUuids);
	}

}