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

import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import com.b2international.index.query.Expression;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableSortedSet;

/**
 * @since 6.5
 */
final class RevisionBranchRef {
	
	private final long branchId;
	private final String branchPath;
	private final SortedSet<RevisionSegment> segments;

	public RevisionBranchRef(long branchId, String branchPath, SortedSet<RevisionSegment> segments) {
		this.branchId = branchId;
		this.branchPath = branchPath;
//		checkArgument(!segments.isEmpty(), "At least one revision segment must be specified");
		this.segments = segments;
	}
	
	public long branchId() {
		return branchId;
	}

	public String path() {
		return branchPath;
	}

	public SortedSet<RevisionSegment> segments() {
		return segments;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(branchId, segments);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		RevisionBranchRef other = (RevisionBranchRef) obj;
		return Objects.equals(branchId, other.branchId)
				&& Objects.equals(segments, other.segments);
	}
	
	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("branchId", branchId)
				.add("segments", segments)
				.toString();
	}

	public Expression toRevisionFilter() {
		return Revision.toRevisionFilter(segments);
	}

	public RevisionBranchRef difference(RevisionBranchRef other) {
		final TreeSet<RevisionSegment> difference = segments.stream()
			.filter(segment -> !other.segments().stream().filter(ex -> ex.branchId() == segment.branchId()).findFirst().isPresent())
			.collect(Collectors.toCollection(TreeSet::new));
		return new RevisionBranchRef(branchId, branchPath, difference);
	}

	public RevisionBranchRef intersection(RevisionBranchRef other) {
		final TreeSet<RevisionSegment> intersection = segments.stream()
			.filter(segment -> other.segments().stream().filter(ex -> ex.branchId() == segment.branchId()).findFirst().isPresent())
			.collect(Collectors.toCollection(TreeSet::new));
		return new RevisionBranchRef(branchId, branchPath, intersection);
	}

	/**
	 * Returns a {@link RevisionBranchRef} that can query revision from the last segment of this ref.
	 * 
	 * @return
	 * @see Purge#LATEST
	 */
	public RevisionBranchRef lastRef() {
		return new RevisionBranchRef(branchId, branchPath, ImmutableSortedSet.of(segments.last()));
	}
	
	/**
	 * Returns a {@link RevisionBranchRef} that can query the historical segments (everything except the last segment) referenced in this ref. 
	 * @return
	 * @see Purge#HISTORY
	 */
	public RevisionBranchRef historyRef() {
		return new RevisionBranchRef(branchId, branchPath, ImmutableSortedSet.copyOf(segments.headSet(segments.last())));
	}
	
}
