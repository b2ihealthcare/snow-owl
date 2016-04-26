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
package com.b2international.snowowl.index.diff;

import static com.b2international.commons.collect.LongSets.filter;
import static com.b2international.commons.collect.LongSets.in;
import static com.b2international.commons.collect.LongSets.intersection;
import static com.b2international.commons.collect.LongSets.not;
import static com.google.common.base.Preconditions.checkNotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.collections.PrimitiveSets;
import com.b2international.collections.longs.LongSet;
import com.b2international.snowowl.index.diff.impl.IndexDiffImpl;
import com.google.common.base.Stopwatch;
import com.google.common.hash.Hashing;

/**
 * Merges {@link IndexDiff} instances used for 3 way diff calculation.
 *
 */
public abstract class IndexDiffMerger {

	private static final Logger LOGGER = LoggerFactory.getLogger(IndexDiffMerger.class);
	
	private IndexDiffMerger() { /*suppress instantiation*/ }
	
	/**
	 * Merges the two given index differences and return with the merged difference.
	 * @param sourceDiff the source difference.
	 * @param targetDiff the target difference.
	 * @return the merged index difference.
	 */
	public static IndexDiff merge(final IndexDiff sourceDiff, final IndexDiff targetDiff) {
		
		checkNotNull(sourceDiff, "sourceDiff");
		checkNotNull(targetDiff, "targetDiff");
		
		final Stopwatch stopwatch = Stopwatch.createStarted();
		
		LOGGER.info("Merging index differences...");
		
		trace("Source difference: " + toString(sourceDiff));
		trace("Target difference: " + toString(targetDiff));
		
		final LongSet newIds = PrimitiveSets.newLongOpenHashSet(Hashing.murmur3_32());
		final LongSet changedIds = PrimitiveSets.newLongOpenHashSet(Hashing.murmur3_32());
		final LongSet detachedIds = PrimitiveSets.newLongOpenHashSet(Hashing.murmur3_32());
		
		//NEW IDs
		//target new is always new
		newIds.addAll(targetDiff.getNewIds());
		//source detached is always new if not detached on target
		newIds.addAll(filter(sourceDiff.getDetachedIds(), not(in(targetDiff.getDetachedIds()))));
		
		//DETACHED IDs
		//source new is always detached
		detachedIds.addAll(sourceDiff.getNewIds());
		//target detached is always detached
		detachedIds.addAll(targetDiff.getDetachedIds());
		//remove the detached intersection of source and target
		detachedIds.removeAll(intersection(sourceDiff.getDetachedIds(), targetDiff.getDetachedIds()));
		
		//CHANGED IDs
		//changed is the union of the source and target changed which are members of neither new nor detached
		//XXX no need to create union as we do require a custom hash function for such large longs
		final LongSet changedIntersectionIds = PrimitiveSets.newLongOpenHashSet(Hashing.murmur3_32());
		changedIntersectionIds.addAll(sourceDiff.getChangedIds());
		changedIntersectionIds.addAll(targetDiff.getChangedIds());
		
		changedIds.addAll(filter(changedIntersectionIds, not(in(newIds, detachedIds))));
		
		final IndexDiffImpl diff = new IndexDiffImpl(newIds, changedIds, detachedIds);
		
		trace("Merged difference: " + toString(diff));
		LOGGER.info("Index difference merge successfully finished. [" + stopwatch + "]");
		
		return diff; 
		
	}
	
	private static String toString(final IndexDiff diff) {
		return new StringBuilder("New: ")
		.append(diff.getNewIds().size())
		.append(" changed: ")
		.append(diff.getChangedIds().size())
		.append(" detached: ")
		.append(diff.getDetachedIds().size())
		.append(".").toString();
	}
	
	private static void trace(final String msg) {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace(msg);
		}
	}
	
}