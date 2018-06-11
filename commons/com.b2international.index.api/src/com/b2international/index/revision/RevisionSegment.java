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

import java.util.Objects;

import com.b2international.index.query.Expression;
import com.b2international.index.query.Expressions;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Range;

/**
 * @since 6.5
 */
public final class RevisionSegment implements Comparable<RevisionSegment> {

	private final long branchId;
	private final long start;
	private final long end;
	
	@JsonCreator
	public RevisionSegment(
			@JsonProperty("branchId") long branchId, 
			@JsonProperty("start") long start, 
			@JsonProperty("end") long end) {
		this.branchId = branchId;
		checkArgument(start <= end, "Start and end values should represent a proper interval in time. Got: [%s, %s]", start, end);
		this.start = start;
		this.end = end;
	}
	
	@JsonIgnore
	public boolean isEmpty() {
		return start == end;
	}
	
	public RevisionSegment difference(RevisionSegment other) {
		if (branchId != other.branchId) {
			System.err.println();
		}
		checkArgument(branchId == other.branchId, "Cannot compute difference for a segment from another branch");
		RevisionSegment intersection = intersection(other);
		if (end > intersection.end) {
			return new RevisionSegment(branchId, intersection.end + 1L, end);
		} else {
			return new RevisionSegment(branchId, end, end);
		}
	} 
	
	public RevisionSegment intersection(RevisionSegment other) {
		checkArgument(branchId == other.branchId, "Cannot compute intersection for a segment from another branch");
		Range<Long> thisRange = Range.closed(start, end);
		Range<Long> otherRange = Range.closed(other.start, other.end);
		Range<Long> intersection = thisRange.intersection(otherRange);
		return new RevisionSegment(branchId, intersection.lowerEndpoint(), intersection.upperEndpoint());
	}
	
	@Override
	public int compareTo(RevisionSegment other) {
		return ComparisonChain.start()
				.compare(branchId, other.branchId)
				.compare(start, other.start)
				.result();
	}
	
	public boolean isBefore(long timestamp) {
		return start <= timestamp;
	}

	@JsonProperty
	public long branchId() {
		return branchId;
	}
	
	@JsonProperty
	public long start() {
		return start;
	}

	@JsonIgnore
	public String getStartAddress() {
		return RevisionBranchPoint.toIpv6(branchId, start);
	}

	@JsonProperty
	public long end() {
		return end;
	}
	
	@JsonIgnore
	public String getEndAddress() {
		return RevisionBranchPoint.toIpv6(branchId, end);
	}

	public RevisionSegment withEnd(long newEnd) {
		return new RevisionSegment(branchId, start, newEnd);
	}

	@Override
	public int hashCode() {
		return Objects.hash(branchId, start, end);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		RevisionSegment other = (RevisionSegment) obj;
		return Objects.equals(branchId, other.branchId)
				&& Objects.equals(start, other.start)
				&& Objects.equals(end, other.end);
	}
	
	public Expression toRangeExpression(String field) {
		return toRangeExpression(field, true);
	}
	
	public Expression toRangeExpression(String field, boolean includeEnd) {
		return Expressions.matchRange(field, getStartAddress(), getEndAddress(), true, includeEnd);
	}
	
	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("branchId", branchId)
				.add("start", start)
				.add("end", end)
				.toString();
	}

}
