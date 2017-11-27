/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore.internal.branch;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import org.eclipse.emf.cdo.common.branch.CDOBranch;

import com.b2international.commons.collections.Collections3;
import com.b2international.snowowl.core.Metadata;
import com.google.common.collect.ImmutableSet;

/**
 * @since 4.1
 */
public class CDOMainBranchImpl extends MainBranchImpl implements InternalCDOBasedBranch {

	static final String TYPE = "CDOMainBranchImpl";
	
	private final int cdoBranchId = CDOBranch.MAIN_BRANCH_ID;
	private final int segmentId;
	private final Collection<Integer> segments;
	
	CDOMainBranchImpl(long baseTimestamp, long headTimestamp, Metadata metadata, int segmentId, Collection<Integer> segments) {
		super(baseTimestamp, headTimestamp, metadata);
		this.segmentId = segmentId;
		this.segments = Collections3.toImmutableSet(segments);
	}
	
	@Override
	protected BranchImpl doCreateBranch(String name, String parentPath, long baseTimestamp, long headTimestamp, boolean deleted, Metadata metadata) {
		return new CDOMainBranchImpl(baseTimestamp, headTimestamp, metadata, segmentId, segments);
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
	public InternalCDOBasedBranch withSegmentId(int newSegmentId) {
		final Set<Integer> newSegments = ImmutableSet.<Integer>builder()
				.add(newSegmentId)
				.addAll(segments())
				.build();
		
		final CDOMainBranchImpl main = new CDOMainBranchImpl(baseTimestamp(), headTimestamp(), metadata(), newSegmentId, newSegments);
		main.setBranchManager(getBranchManager());
		return main;
	}
	
	@Override
	public BranchDocument.Builder toDocument() {
		return super.toDocument()
				.type(TYPE)
				.cdoBranchId(cdoBranchId)
				.segmentId(segmentId)
				.segments(segments);
	}
	
	static InternalBranch from(BranchDocument doc) {
		return new CDOMainBranchImpl(
				doc.getBaseTimestamp(), 
				doc.getHeadTimestamp(), 
				doc.getMetadata(), 
				doc.getSegmentId(), 
				doc.getSegments());
	}
	
}
