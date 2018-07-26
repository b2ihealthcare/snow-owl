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

import static com.google.common.collect.Sets.newHashSet;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.b2international.commons.options.Metadata;
import com.b2international.index.mapping.DocumentMapping;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Multimap;

/**
 * @since 6.5
 */
public final class DefaultRevisionBranching extends BaseRevisionBranching {

	private final AtomicInteger branchIds = new AtomicInteger(0);
	private final long mainBaseTimestamp;
	private final long mainHeadTimestamp;
	private final TimestampProvider timestampProvider;

	public DefaultRevisionBranching(RevisionIndex index, TimestampProvider timestampProvider, ObjectMapper mapper) {
		super(index, mapper);
		this.timestampProvider = timestampProvider;
		this.mainBaseTimestamp = this.mainHeadTimestamp = currentTime();
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
		return nextBranchId();
	}

	@Override
	protected String applyChangeSet(RevisionBranch from, RevisionBranch to, boolean dryRun, boolean isRebase, String commitMessage) {
		if (!dryRun && from.getHeadTimestamp() > from.getBaseTimestamp()) {
			// TODO add conflict processing
			final InternalRevisionIndex index = revisionIndex();
			final RevisionBranchRef fromRef = from.ref();
			final RevisionBranchRef toRef = to.ref();
			final RevisionCompare fromChanges = index.compare(toRef, fromRef, Integer.MAX_VALUE);
			final List<RevisionCompareDetail> diff = fromChanges.getDetails();
			if (!diff.isEmpty()) {
				StagingArea staging = index.prepareCommit(to.getPath());
				final Multimap<Class<? extends Revision>, String> newRevisionIdsByType = HashMultimap.create();
				final Multimap<Class<? extends Revision>, String> changedRevisionIdsByType = HashMultimap.create();
				final Multimap<Class<? extends Revision>, String> removedRevisionIdsByType = HashMultimap.create();
				
				diff.forEach(detail -> {
					// add all objects to the tx
					if (detail.isAdd()) {
						Class<?> revType = DocumentMapping.getClass(detail.getComponent().type());
						if (Revision.class.isAssignableFrom(revType)) {
							newRevisionIdsByType.put((Class<? extends Revision>) revType, detail.getComponent().id());
						}
					} else if (detail.isChange()) {
						Class<?> revType = DocumentMapping.getClass(detail.getObject().type());
						if (Revision.class.isAssignableFrom(revType)) {
							changedRevisionIdsByType.put((Class<? extends Revision>) revType, detail.getObject().id());
						}
					} else if (detail.isRemove()) {
						Class<?> revType = DocumentMapping.getClass(detail.getComponent().type());
						if (Revision.class.isAssignableFrom(revType)) {
							removedRevisionIdsByType.put((Class<? extends Revision>) revType, detail.getComponent().id());
						}
					} else {
						throw new UnsupportedOperationException("Unsupported diff operation: " + detail.getOp());
					}
				});
				
				// apply new objects
				for (Class<? extends Revision> type : newHashSet(newRevisionIdsByType.keySet())) {
					final Collection<String> newRevisionIds = newRevisionIdsByType.removeAll(type);
					index.read(fromRef, searcher -> searcher.get(type, newRevisionIds)).forEach(staging::stageNew);
				}
				
				// apply changed objects
				for (Class<? extends Revision> type : newHashSet(changedRevisionIdsByType.keySet())) {
					final Collection<String> changedRevisionIds = changedRevisionIdsByType.removeAll(type);
					final Iterable<? extends Revision> oldRevisions = index.read(toRef, searcher -> searcher.get(type, changedRevisionIds));
					final Map<String,? extends Revision> oldRevisionsById = FluentIterable.from(oldRevisions).uniqueIndex(Revision::getId);
					
					final Iterable<? extends Revision> updatedRevisions = index.read(fromRef, searcher -> searcher.get(type, changedRevisionIds));
					final Map<String, ? extends Revision> updatedRevisionsById = FluentIterable.from(updatedRevisions).uniqueIndex(Revision::getId);
					for (String updatedId : updatedRevisionsById.keySet()) {
						if (oldRevisionsById.containsKey(updatedId)) {
							staging.stageChange(oldRevisionsById.get(updatedId), updatedRevisionsById.get(updatedId));
						} else {
							staging.stageNew(updatedRevisionsById.get(updatedId));
						}
					}
				}
				
				// apply deleted objects
				for (Class<? extends Revision> type : newHashSet(removedRevisionIdsByType.keySet())) {
					final Collection<String> removedRevisionIds = removedRevisionIdsByType.removeAll(type);
					index.read(toRef, searcher -> searcher.get(type, removedRevisionIds)).forEach(staging::stageRemove);
				}
				
				staging.commit(currentTime(), "", commitMessage);
			} else {
				handleCommit(to.getPath(), currentTime());
			}
		}
		return to.getPath();
	}

}
