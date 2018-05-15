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
package com.b2international.index.revision;

import java.util.concurrent.atomic.AtomicInteger;

import com.b2international.commons.options.Metadata;
import com.b2international.index.Index;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedSet;
import com.google.inject.Provider;

/**
 * @since 6.5
 */
public final class DefaultRevisionBranching extends BaseRevisionBranching {

	private final AtomicInteger branchIds = new AtomicInteger(0);
	private final long mainBaseTimestamp;
	private final long mainHeadTimestamp;

	public DefaultRevisionBranching(Provider<Index> index, ObjectMapper mapper) {
		super(index, mapper);
		this.mainBaseTimestamp = this.mainHeadTimestamp = currentTime();
	}
	
	@Override
	protected long currentTime() {
		return System.nanoTime();
	}

	@Override
	protected long nextBranchId() {
		return branchIds.getAndIncrement();
	}
	
	@Override
	protected RevisionBranch doReopen(RevisionBranch parentBranch, String child, Metadata metadata) {
		final long currentTime = currentTime();
		final long newBranchId = nextBranchId();
		final RevisionSegment parentSegment = parentBranch.getSegments().last();
		final RevisionBranch branch = RevisionBranch.builder()
				.id(newBranchId)
				.parentPath(parentBranch.getPath())
				.name(child)
				.segments(ImmutableSortedSet.<RevisionSegment>naturalOrder()
						.addAll(parentBranch.getSegments().headSet(parentSegment))
						.add(parentSegment.withEnd(currentTime))
						.add(new RevisionSegment(newBranchId, currentTime, currentTime))
						.build())
				.build();
		return commit(create(branch));
	}
	
	@Override
	protected long getMainBaseTimestamp() {
		return mainBaseTimestamp;
	}
	
	@Override
	protected long getMainHeadTimestamp() {
		return mainHeadTimestamp;
	}
	
	@Override
	protected long getMainBranchId() {
		return nextBranchId();
	}

	@Override
	protected String applyChangeSet(RevisionBranch from, RevisionBranch to, boolean dryRun, boolean isRebase, String commitMessage) {
		// TODO implement proper apply change set here
		if (!dryRun && from.getHeadTimestamp() > from.getBaseTimestamp()) {
			handleCommit(to.getPath(), currentTime());
		}
		return to.getPath();
	}

}
