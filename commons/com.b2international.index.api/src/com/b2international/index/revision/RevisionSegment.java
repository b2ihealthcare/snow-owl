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

import com.b2international.index.query.Expression;
import com.b2international.index.query.Expressions;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ComparisonChain;

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
	
	@Override
	public int compareTo(RevisionSegment other) {
		return ComparisonChain.start()
				.compare(branchId, other.branchId)
				.compare(start, other.start)
				.result();
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

	public Expression toRangeExpression(String field) {
		return Expressions.matchRange(field, getStartAddress(), getEndAddress());
	}
	
}
