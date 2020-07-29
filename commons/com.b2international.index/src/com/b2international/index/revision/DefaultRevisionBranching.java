/*
 * Copyright 2018-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import com.b2international.commons.options.Metadata;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Query;
import com.b2international.index.query.SortBy;
import com.b2international.index.query.SortBy.Order;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedSet;

/**
 * @since 6.5
 */
public final class DefaultRevisionBranching extends BaseRevisionBranching {

	private final AtomicLong branchIds = new AtomicLong(0L);
	private final long mainBaseTimestamp;
	private final long mainHeadTimestamp;
	private final long mainBranchId;

	public DefaultRevisionBranching(RevisionIndex index, TimestampProvider timestampProvider) {
		super(index, timestampProvider);
		this.mainBaseTimestamp = this.mainHeadTimestamp = currentTime();
		this.mainBranchId = nextBranchId();
	}
	
	public long nextBranchId() {
		return branchIds.getAndIncrement();
	}
	
	@Override
	protected void init() {
		super.init();
		branchIds.set(getMaxBranchId() + 1);
	}
	
	private long getMaxBranchId() {
		return search(Query.select(RevisionBranch.class)
				.where(Expressions.matchAll())
				.sortBy(SortBy.field(RevisionBranch.Fields.ID, Order.DESC))
				.limit(1)
				.build())
				.stream()
				.findFirst()
				.map(RevisionBranch::getId)
				.orElse(0L);
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
					ImmutableList.of(
						new RevisionBranchMergeSource(currentTime, initialMergeSources, false)
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

}
