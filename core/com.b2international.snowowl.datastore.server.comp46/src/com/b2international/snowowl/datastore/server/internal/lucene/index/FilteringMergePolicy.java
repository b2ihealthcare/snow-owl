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
package com.b2international.snowowl.datastore.server.internal.lucene.index;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.MergePolicy;
import org.apache.lucene.index.MergeTrigger;
import org.apache.lucene.index.SegmentCommitInfo;
import org.apache.lucene.index.SegmentInfos;

public class FilteringMergePolicy extends MergePolicy {

	private final MergePolicy delegate;
	private final AtomicReference<String> minSegmentName = new AtomicReference<String>("");

	public FilteringMergePolicy(final MergePolicy delegate) {
		this.delegate = delegate;
	}

	public void setMinSegmentCount(final int counter) {
		minSegmentName.set("_" + Integer.toString(counter, Character.MAX_RADIX));
	}

	@Override
	public MergeSpecification findMerges(final MergeTrigger mergeTrigger, final SegmentInfos segmentInfos, final IndexWriter writer) throws IOException {
		final SegmentInfos filteredInfos = filterSegmentInfos(segmentInfos);
		return filterOnlyDeletionMerges(delegate.findMerges(mergeTrigger, filteredInfos, writer));
	}

	@Override
	public MergeSpecification findForcedMerges(final SegmentInfos segmentInfos, final int maxSegmentCount,
			final Map<SegmentCommitInfo, Boolean> segmentsToMerge, final IndexWriter writer) throws IOException {

		final SegmentInfos filteredInfos = filterSegmentInfos(segmentInfos);
		return filterOnlyDeletionMerges(delegate.findForcedMerges(filteredInfos, maxSegmentCount, segmentsToMerge, writer));
	}

	@Override
	public MergeSpecification findForcedDeletesMerges(final SegmentInfos segmentInfos, final IndexWriter writer) throws IOException {
		final SegmentInfos filteredInfos = filterSegmentInfos(segmentInfos);
		return filterOnlyDeletionMerges(delegate.findForcedDeletesMerges(filteredInfos, writer));
	}

	private SegmentInfos filterSegmentInfos(final SegmentInfos segmentInfos) {

		final SegmentInfos result = segmentInfos.clone();
		result.clear();

		final int minSegmentCounter = isEmpty(minSegmentName.get()) 
				? 0 
				: Integer.parseInt(minSegmentName.get().substring(1), Character.MAX_RADIX); 
		
		for (final SegmentCommitInfo si : segmentInfos) {
			final int segmentCounter = Integer.parseInt(si.info.name.substring(1), Character.MAX_RADIX); 
			if (segmentCounter >= minSegmentCounter) {
				result.add(si);
			}
		}

		return result;
	}
	
	private boolean isEmpty(final String s) {
		
		if (null == s) {
			return true;
		}
		
		for (final char c : s.toCharArray()) {
			if (!Character.isWhitespace(c)) {
				return false;
			}
		}
		
		return true;
		
	}
	
	//filters out all OneMerges which attempt to merge segments containing only deletions.
	//this is to avoid the creation of an empty compound file
	private MergeSpecification filterOnlyDeletionMerges(final MergeSpecification mergeSpecification) {
		
		if (null == mergeSpecification || null == mergeSpecification.merges) {
			return null;
		}
		
		for (final Iterator<OneMerge> itr = mergeSpecification.merges.iterator(); itr.hasNext(); /**/) {

			final OneMerge merge = itr.next();
			boolean keep = false;
			
			for (final SegmentCommitInfo segmentCommitInfo : merge.segments) {
				
				final int delCount = segmentCommitInfo.getDelCount();
				final int docCount = segmentCommitInfo.info.maxDoc() - segmentCommitInfo.getDelCount();
				
				if (delCount != docCount) {
					keep = true;
					break;
				}
			}
			
			if (!keep) {
				itr.remove();
			}
		}
		
		return mergeSpecification.merges.isEmpty() ? null : mergeSpecification;
	}
}