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
package com.b2international.snowowl.datastore.index;

import java.io.IOException;
import java.util.Map;

import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.MergePolicy;
import org.apache.lucene.index.MergeTrigger;
import org.apache.lucene.index.NoMergePolicy;
import org.apache.lucene.index.SegmentCommitInfo;
import org.apache.lucene.index.SegmentInfos;

import com.b2international.snowowl.core.api.IMutable;
import com.google.common.base.Preconditions;

/**
 * Mutable {@link MergePolicy} implementation.
 * @see IMutable
 */
public class MutableMergePolicyWrapper extends MergePolicy implements IMutable {

	private final MergePolicy delegate;
	private volatile boolean enabled = true;

	public MutableMergePolicyWrapper(final MergePolicy policy) {
		this.delegate = Preconditions.checkNotNull(policy, "Merge policy argument cannot be null.");
	}

	@Override
	public MergeSpecification findMerges(final MergeTrigger mergeTrigger, final SegmentInfos segmentInfos, final IndexWriter writer) throws IOException {
		return enabled
				? delegate.findMerges(mergeTrigger, segmentInfos, writer)
				: NoMergePolicy.INSTANCE.findMerges(mergeTrigger, segmentInfos, writer);
	}

	@Override
	public MergeSpecification findForcedMerges(final SegmentInfos segmentInfos, final int maxSegmentCount, final Map<SegmentCommitInfo, Boolean> segmentsToMerge, final IndexWriter writer) throws IOException {
		return enabled
				? delegate.findForcedMerges(segmentInfos, maxSegmentCount, segmentsToMerge, writer)
				: NoMergePolicy.INSTANCE.findForcedMerges(segmentInfos, maxSegmentCount, segmentsToMerge, writer);
	}

	@Override
	public MergeSpecification findForcedDeletesMerges(final SegmentInfos segmentInfos, final IndexWriter writer) throws IOException {
		return enabled 
				? delegate.findForcedDeletesMerges(segmentInfos, writer) 
				: NoMergePolicy.INSTANCE.findForcedDeletesMerges(segmentInfos, writer);
	}

	@Override
	public void close() {
		delegate.close();
	}

	@Override
	public void enable() {
		enabled = true;
	}

	@Override
	public void disable() {
		enabled = false;
	}

}