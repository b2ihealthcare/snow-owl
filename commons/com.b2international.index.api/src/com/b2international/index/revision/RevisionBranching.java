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

import static com.google.common.base.Preconditions.checkArgument;

import java.util.concurrent.atomic.AtomicInteger;

import com.b2international.index.Hits;
import com.b2international.index.Index;
import com.b2international.index.IndexWrite;
import com.b2international.index.query.Query;
import com.b2international.index.revision.RevisionBranch.BranchState;
import com.google.common.collect.ImmutableSortedSet;

/**
 * @since 6.5
 */
public final class RevisionBranching {

	private final Index index;
	private final AtomicInteger branchIds = new AtomicInteger(0);

	public RevisionBranching(Index index) {
		this.index = index;
	}
	
	/**
	 * Must be called after index initialization.
	 */
	public void init() {
		RevisionBranch mainBranch = getBranch(RevisionBranch.MAIN_PATH);
		if (mainBranch == null) {
			commit(writer -> {
				final int branchId = nextBranchId();
				final long currentTime = currentTime();
				writer.put(RevisionBranch.MAIN_PATH, RevisionBranch.builder()
						.id(branchId)
						.parentPath("")
						.name(RevisionBranch.MAIN_PATH)
						.segments(ImmutableSortedSet.<RevisionSegment>naturalOrder()
								.add(new RevisionSegment(branchId, currentTime, currentTime))
								.build())
						.build());
				writer.commit();
				return null;
			});
		}
	}

	private long currentTime() {
		return System.nanoTime();
	}

	private int nextBranchId() {
		return branchIds.getAndIncrement();
	}
	
	/**
	 * Returns the MAIN branch of the repository.
	 * @return
	 */
	public RevisionBranch getMainBranch() {
		return getBranch(RevisionBranch.MAIN_PATH);
	}
	
	/**
	 * Returns the revision branch for the given branchPath.
	 * 
	 * @param branchPath
	 * @return
	 */
	public RevisionBranch getBranch(String branchPath) {
		return index.read(searcher -> searcher.get(RevisionBranch.class, branchPath));
	}
	
	/**
	 * Search branches with the given query and return the results.
	 * @param query
	 * @return
	 */
	public Hits<RevisionBranch> getBranches(Query<RevisionBranch> query) {
		return index.read(searcher -> searcher.search(query));
	}
	
	public <T> T commit(IndexWrite<T> changes) {
		return index.write(writer -> {
			T result = changes.execute(writer);
			writer.commit();
			return result;
		});
	}
	
	public String create(String parent, String child) {
		RevisionBranch parentBranch = getBranch(parent);
		if (parentBranch == null) {
			throw new IllegalArgumentException("Parent could not be found at path: " + parent);
		}
		return commit(writer -> {
			final long currentTime = currentTime();
			final int newBranchId = nextBranchId();
			final RevisionSegment parentSegment = parentBranch.getSegments().last();
			final RevisionBranch branch = RevisionBranch.builder()
				.id(newBranchId)
				.parentPath(parent)
				.name(child)
				.segments(ImmutableSortedSet.<RevisionSegment>naturalOrder()
						.addAll(parentBranch.getSegments().headSet(parentSegment))
						.add(parentSegment.withEnd(currentTime))
						.add(new RevisionSegment(newBranchId, currentTime, currentTime))
						.build())
				.build();
			writer.put(branch.getPath(), branch);
			writer.commit();
			return branch.getPath();
		});
	}

	public void delete(String branchPath) {
		if (RevisionBranch.MAIN_PATH.equals(branchPath)) {
			throw new IllegalArgumentException("MAIN cannot be deleted");
		}
	}

	public BranchState getBranchState(String branchPath) {
		if (RevisionBranch.MAIN_PATH.equals(branchPath)) {
			return BranchState.UP_TO_DATE;
		} else {
			final RevisionBranch revisionBranch = getBranch(branchPath);
			return revisionBranch.state(getBranch(revisionBranch.getParentPath()));
		}
	}

	public void rebase(String branchPath) {
		if (RevisionBranch.MAIN_PATH.equals(branchPath)) {
			throw new IllegalArgumentException("MAIN cannot be rebased");
		}
	}
	
	public void merge(String branchPath, String changesFromPath) {
		if (branchPath.equals(changesFromPath)) {
			throw new IllegalArgumentException(String.format("Can't merge branch '%s' onto itself.", branchPath));
		}
		RevisionBranch branch = getBranch(branchPath);
		RevisionBranch changesFrom = getBranch(changesFromPath);
		BranchState changesFromState = changesFrom.state(branch);
		checkArgument(changesFromState == BranchState.FORWARD, "Branch %s should be in FORWARD state to be merged into %s. It's currently %s", changesFromPath, branchPath, changesFromState);
		throw new UnsupportedOperationException("TODO implement me");
	}

}
