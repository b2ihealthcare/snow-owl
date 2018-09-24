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

import com.b2international.index.IP;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.collect.ComparisonChain;

/**
 * @since 6.5
 */
@IP
public final class RevisionBranchPoint implements Comparable<RevisionBranchPoint> {

	private final long branchId;
	private final long timestamp;

	public RevisionBranchPoint(long branchId, long timestamp) {
		this.branchId = branchId;
		this.timestamp = timestamp;
	}
	
	public long getBranchId() {
		return branchId;
	}
	
	public long getTimestamp() {
		return timestamp;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(branchId, timestamp);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		RevisionBranchPoint other = (RevisionBranchPoint) obj;
		return Objects.equals(branchId, other.branchId)
				&& Objects.equals(timestamp, other.timestamp);
	}
	
	@Override
	public String toString() {
		return String.format("%s@%s", branchId, timestamp);
	}
	
	@JsonValue
	public String toIpAddress() {
		return toIpv6(branchId, timestamp);
	}

	public static String toIpv6(long left, long right) {
		StringBuilder value = new StringBuilder(String.format("%016x%016x", left, right));
		for (int i = value.length() - 4; i > 0; i -= 4) {
			value.insert(i, ":");
		}
		return value.toString();
	}

	public RevisionBranchPoint atEpoch() {
		return new RevisionBranchPoint(branchId, 0L);
	}

	public RevisionBranchPoint atFuture() {
		return new RevisionBranchPoint(branchId, Long.MAX_VALUE);
	}
	
	@JsonCreator
	public static RevisionBranchPoint valueOf(String value) {
		String[] addrArray = value.split(":");//a IPv6 adress is of form 2607:f0d0:1002:0051:0000:0000:0000:0004
	    long[] num = new long[addrArray.length];

	    for (int i=0; i<addrArray.length; i++) {
	        num[i] = Long.parseLong(addrArray[i], 16);
	    }
	    long branchId = num[0];
	    for (int i=1;i<4;i++) {
	        branchId = (branchId<<16) + num[i];
	    }
	    long timestamp = num[4];
	    for (int i=5;i<8;i++) {
	        timestamp = (timestamp<<16) + num[i];
	    }

	    return new RevisionBranchPoint(branchId, timestamp); 
	}
	
	@Override
	public int compareTo(RevisionBranchPoint other) {
		return ComparisonChain.start()
				.compare(branchId, other.branchId)
				.compare(timestamp, other.timestamp)
				.result();
	}
	
}
