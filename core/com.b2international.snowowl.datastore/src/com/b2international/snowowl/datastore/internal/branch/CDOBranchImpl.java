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
import java.util.Set;

import com.b2international.commons.collections.Collections3;
import com.b2international.snowowl.core.Metadata;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableSet;

/**
 * @since 4.1
 */
public class CDOBranchImpl extends BranchImpl implements InternalCDOBasedBranch {

	static final String TYPE = "CDOBranchImpl";
	
	private final int cdoBranchId;
	private final int segmentId;
	private final Collection<Integer> segments;
	private final Collection<Integer> parentSegments;

	protected CDOBranchImpl(String name, String parentPath, long baseTimestamp, long headTimestamp, Metadata metadata, int cdoBranchId, int segmentId, Collection<Integer> segments, Collection<Integer> parentSegments) {
		this(name, parentPath, baseTimestamp, headTimestamp, false, metadata, cdoBranchId, segmentId, segments, parentSegments);
	}

	protected CDOBranchImpl(String name, String parentPath, long baseTimestamp, long headTimestamp, boolean deleted, Metadata metadata, int cdoBranchId, int segmentId, Collection<Integer> segments, Collection<Integer> parentSegments) {
		super(name, parentPath, baseTimestamp, headTimestamp, deleted, metadata);
		this.cdoBranchId = cdoBranchId;
		this.segmentId = segmentId;
		this.segments = Collections3.toImmutableSet(segments);
		this.parentSegments = Collections3.toImmutableSet(parentSegments);
	}
	
	@Override
	protected CDOBranchImpl doCreateBranch(String name, String parentPath, long baseTimestamp, long headTimestamp, boolean deleted, Metadata metadata) {
		return new CDOBranchImpl(name, parentPath, baseTimestamp, headTimestamp, deleted, metadata, cdoBranchId, segmentId, segments, parentSegments);
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
	public InternalCDOBasedBranch withSegmentId(int newSegmentId) {
		final Set<Integer> newSegments = ImmutableSet.<Integer>builder()
				.add(newSegmentId)
				.addAll(segments())
				.build();
		
		CDOBranchImpl branch = new CDOBranchImpl(name(), parentPath(), baseTimestamp(), headTimestamp(), isDeleted(), metadata(), cdoBranchId(), newSegmentId, newSegments, parentSegments());
		branch.setBranchManager(getBranchManager());
		return branch;
	}
	
	@Override
	public String toString() {
		return MoreObjects.toStringHelper(getClass())
				.add("cdoBranchId", cdoBranchId())
				.add("segmentId", segmentId())
				.add("segments", segments())
				.add("parentSegments", parentSegments())
				.toString();
	}
	
	@Override
	BranchDocument.Builder toDocument() {
		return super.toDocument()
				.type(TYPE)
				.cdoBranchId(cdoBranchId)
				.segmentId(segmentId)
				.segments(segments)
				.parentSegments(parentSegments);
	}
	
	static InternalBranch from(BranchDocument doc) {
		return new CDOBranchImpl(
				doc.getName(), 
				doc.getParentPath(), 
				doc.getBaseTimestamp(), 
				doc.getHeadTimestamp(), 
				doc.isDeleted(), 
				doc.getMetadata(), 
				doc.getCdoBranchId(), 
				doc.getSegmentId(), 
				doc.getSegments(), 
				doc.getParentSegments());
	}

}
