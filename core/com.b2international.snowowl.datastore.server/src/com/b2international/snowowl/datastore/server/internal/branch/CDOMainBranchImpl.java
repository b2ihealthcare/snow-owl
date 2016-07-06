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
import java.util.Collections;

import org.eclipse.emf.cdo.common.branch.CDOBranch;

import com.b2international.commons.collections.Collections3;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

/**
 * @since 4.1
 */
public class CDOMainBranchImpl extends MainBranchImpl implements InternalCDOBasedBranch {

	private final int cdoBranchId = CDOBranch.MAIN_BRANCH_ID;
	private final int segmentId;
	private final Collection<Integer> segments;
	
	CDOMainBranchImpl(long baseTimestamp, long headTimestamp, int segmentId, Collection<Integer> segments) {
		super(baseTimestamp, headTimestamp);
		this.segmentId = segmentId;
		this.segments = Collections3.toImmutableSet(segments);
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
		return Collections.emptySet();
	}
	
	@Override
	public int cdoBranchId() {
		return cdoBranchId;
	}
	
	@Override
	public InternalBranch withHeadTimestamp(long newHeadTimestamp) {
		final MainBranchImpl main = new CDOMainBranchImpl(baseTimestamp(), newHeadTimestamp, segmentId, segments);
		main.setBranchManager(branchManager);
		main.metadata(metadata());
		return main;
	}
	
	@Override
	public InternalCDOBasedBranch withSegmentId(int segmentId) {
		final Builder<Integer> builder = ImmutableSet.builder();
		builder.add(segmentId);
		// MAIN branch uses all his previous segments because he never gets reopened
		builder.addAll(segments());
		return new CDOMainBranchImpl(baseTimestamp(), headTimestamp(), segmentId, builder.build());
	}

}
