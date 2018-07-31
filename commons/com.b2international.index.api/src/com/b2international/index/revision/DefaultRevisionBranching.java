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

import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.b2international.commons.options.Metadata;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.ImmutableSortedSet;

/**
 * @since 6.5
 */
public final class DefaultRevisionBranching extends BaseRevisionBranching {

	private final AtomicInteger branchIds = new AtomicInteger(0);
	private final long mainBaseTimestamp;
	private final long mainHeadTimestamp;
	private final TimestampProvider timestampProvider;
	private final long mainBranchId;

	public DefaultRevisionBranching(RevisionIndex index, TimestampProvider timestampProvider, ObjectMapper mapper) {
		super(index, mapper);
		this.timestampProvider = timestampProvider;
		this.mainBaseTimestamp = this.mainHeadTimestamp = currentTime();
		this.mainBranchId = nextBranchId();
	}
	
	public long currentTime() {
		return timestampProvider.getTimestamp();
	}

	public long nextBranchId() {
		return branchIds.getAndIncrement();
	}
	
	@Override
	protected RevisionBranch doReopen(RevisionBranch parentBranch, String child, Metadata metadata) {
		final long currentTime = currentTime();
		final long newBranchId = nextBranchId();
		final RevisionSegment parentLastSegment = parentBranch.getSegments().last();
		final SortedSet<RevisionSegment> parentSegments = ImmutableSortedSet.<RevisionSegment>naturalOrder()
			.addAll(parentBranch.getSegments().headSet(parentLastSegment))
			.add(parentLastSegment.withEnd(currentTime))
			.build();
		
		final SortedSet<RevisionBranchPoint> initialMergeSources = parentSegments.stream()
				.map(RevisionSegment::getEndPoint)
				.collect(Collectors.toCollection(TreeSet::new));
		
		final RevisionBranch branch = RevisionBranch.builder()
				.id(newBranchId)
				.parentPath(parentBranch.getPath())
				.name(child)
				.segments(ImmutableSortedSet.<RevisionSegment>naturalOrder()
						.addAll(parentSegments)
						.add(new RevisionSegment(newBranchId, currentTime, currentTime))
						.build())
				.mergeSources(
					ImmutableSortedMap.of(
						currentTime,
						initialMergeSources
					)
				)
				.metadata(metadata)
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
		return mainBranchId;
	}

	@Override
	protected String applyChangeSet(RevisionBranch from, RevisionBranch to, boolean dryRun, boolean isRebase, String commitMessage) {
		if (!dryRun) {
			final InternalRevisionIndex index = revisionIndex();
			StagingArea staging = index.prepareCommit(to.getPath());
			
			// TODO add conflict processing
			staging.merge(from.ref(), to.ref());
			staging.commit(currentTime(), "TODO", commitMessage);
		}
		return to.getPath();
	}

}
