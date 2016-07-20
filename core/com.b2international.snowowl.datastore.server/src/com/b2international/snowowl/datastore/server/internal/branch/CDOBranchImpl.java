/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore.server.internal.branch;

import java.util.Collection;

import com.b2international.commons.collections.Collections3;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

/**
 * @since 4.1
 */
public class CDOBranchImpl extends BranchImpl implements InternalCDOBasedBranch {

	private final int cdoBranchId;
	private final int segmentId;
	private final Collection<Integer> segments;
	private final Collection<Integer> parentSegments;

	protected CDOBranchImpl(String name, String parentPath, long baseTimestamp, int cdoBranchId, int segmentId, Collection<Integer> segments, Collection<Integer> parentSegments) {
		this(name, parentPath, baseTimestamp, baseTimestamp, cdoBranchId, segmentId, segments, parentSegments);
	}

	protected CDOBranchImpl(String name, String parentPath, long baseTimestamp, long headTimestamp, int cdoBranchId, int segmentId, Collection<Integer> segments, Collection<Integer> parentSegments) {
		this(name, parentPath, baseTimestamp, headTimestamp, false, cdoBranchId, segmentId, segments, parentSegments);
	}

	protected CDOBranchImpl(String name, String parentPath, long baseTimestamp, long headTimestamp, boolean deleted, int cdoBranchId, int segmentId, Collection<Integer> segments, Collection<Integer> parentSegments) {
		super(name, parentPath, baseTimestamp, headTimestamp, deleted);
		this.cdoBranchId = cdoBranchId;
		this.segmentId = segmentId;
		this.segments = Collections3.toImmutableSet(segments);
		this.parentSegments = Collections3.toImmutableSet(parentSegments);
	}
	
	@Override
	protected CDOBranchImpl doCreateBranch(String name, String parentPath, long baseTimestamp, long headTimestamp, boolean deleted) {
		return new CDOBranchImpl(name, parentPath, baseTimestamp, headTimestamp, deleted, cdoBranchId, segmentId, segments, parentSegments);
	}
	
	@Override
	public int segmentId() {
		return segmentId;
	}
	
	@Override
	public Collection<Integer> segments() {
		return segments;
	}
	
	@Override
	public Collection<Integer> parentSegments() {
		return parentSegments;
	}

	@Override
	public int cdoBranchId() {
		return cdoBranchId;
	}
	
	@Override
	public InternalCDOBasedBranch withSegmentId(int segmentId) {
		final Builder<Integer> builder = ImmutableSet.builder();
		builder.add(segmentId);
		// use previous segments here, the branch got a new segment because a new child branch got opened
		builder.addAll(segments());
		return new CDOBranchImpl(name(), parentPath(), baseTimestamp(), headTimestamp(), isDeleted(), cdoBranchId(), segmentId, builder.build(), parentSegments);
	}

}
