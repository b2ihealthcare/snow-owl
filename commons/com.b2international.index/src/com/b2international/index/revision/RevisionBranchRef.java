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
import static com.google.common.collect.Sets.newTreeSet;

import java.util.Iterator;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import com.b2international.index.query.Expression;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Expressions.ExpressionBuilder;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableSortedSet;

/**
 * Reference to a set of revision branch segments to query the contents visible from the branch at a given time.
 * 
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
		if (isEmpty()) {
			return Expressions.matchNone();
		}
		
		final ExpressionBuilder query = Expressions.builder();
		final ExpressionBuilder created = Expressions.builder();
		
		for (RevisionSegment segment : segments) {
			created.should(segment.toRangeExpression(Revision.Fields.CREATED));
			query.mustNot(segment.toRangeExpression(Revision.Fields.REVISED));
		}
		
		return query
				.filter(created.build())
				.build();
	}
	
	public Expression toCreatedInFilter() {
		if (isEmpty()) {
			return Expressions.matchNone();
		}
		
		final ExpressionBuilder createdIn = Expressions.builder();
		for (RevisionSegment segment : segments) {
			createdIn.should(segment.toRangeExpression(Revision.Fields.CREATED));
		}
		
		return createdIn.build(); 
	}

	public Expression toRevisedInFilter() {
		if (isEmpty()) {
			return Expressions.matchNone();
		}
		
		final ExpressionBuilder revisedIn = Expressions.builder();
		for (RevisionSegment segment : segments) {
			revisedIn.should(segment.toRangeExpression(Revision.Fields.REVISED));
		}
		
		return revisedIn.build();
	}

	public RevisionBranchRef difference(RevisionBranchRef other) {
		final TreeSet<RevisionSegment> differenceSegments = newTreeSet();
		
		Iterator<RevisionSegment> segmentsIterator = segments.iterator();
		Iterator<RevisionSegment> otherSegmentsIterator = other.segments.iterator();
		
		while (segmentsIterator.hasNext() && otherSegmentsIterator.hasNext()) {
			RevisionSegment nextSegment = segmentsIterator.next();
			RevisionSegment otherSegment = otherSegmentsIterator.next();
			if (nextSegment.branchId() != otherSegment.branchId()) {
				differenceSegments.add(nextSegment);
				break;
			}
			RevisionSegment difference = nextSegment.difference(otherSegment);
			if (!difference.isEmpty()) {
				differenceSegments.add(difference);
			}
		}
		
		while (segmentsIterator.hasNext()) {
			differenceSegments.add(segmentsIterator.next());
		}
		
		return new RevisionBranchRef(branchId, branchPath, differenceSegments);
	}

	public RevisionBranchRef intersection(RevisionBranchRef other) {
		final TreeSet<RevisionSegment> intersectionSegments = newTreeSet();
		
		Iterator<RevisionSegment> segmentsIterator = segments.iterator();
		Iterator<RevisionSegment> otherSegmentsIterator = other.segments.iterator();
		
		while (segmentsIterator.hasNext() && otherSegmentsIterator.hasNext()) {
			RevisionSegment nextSegment = segmentsIterator.next();
			RevisionSegment otherSegment = otherSegmentsIterator.next();
			if (nextSegment.branchId() != otherSegment.branchId()) {
				break;
			}
			intersectionSegments.add(nextSegment.intersection(otherSegment));
		}
		
		return new RevisionBranchRef(branchId, branchPath, intersectionSegments);
	}

	/**
	 * Returns a {@link RevisionBranchRef} that can query revision from the last segment of this ref.
	 * 
	 * @return
	 * @see Purge#LATEST
	 */
	public RevisionBranchRef lastRef() {
		checkArgument(!isEmpty(), "TODO");
		return new RevisionBranchRef(branchId, branchPath, ImmutableSortedSet.of(segments.last()));
	}
	
	/**
	 * Returns a {@link RevisionBranchRef} that can query the historical segments (everything except the last segment) referenced in this ref. 
	 * @return
	 * @see Purge#HISTORY
	 */
	public RevisionBranchRef historyRef() {
		checkArgument(!isEmpty(), "TODO");
		return new RevisionBranchRef(branchId, branchPath, ImmutableSortedSet.copyOf(segments.headSet(segments.last())));
	}

	public boolean isEmpty() {
		return segments.isEmpty();
	}

	public RevisionBranchRef restrictTo(long timestamp) {
		return new RevisionBranchRef(branchId(), path(), segments()
				.stream()
				.filter(segment -> segment.isBefore(timestamp)) // consider segments that are before the currently desired timestamp
				.map(segment -> segment.withEnd(timestamp))
				.collect(Collectors.toCollection(TreeSet::new)));
	}
	
}
