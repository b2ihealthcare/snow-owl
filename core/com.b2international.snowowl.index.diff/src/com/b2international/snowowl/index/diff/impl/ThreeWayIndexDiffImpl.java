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
package com.b2international.snowowl.index.diff.impl;

import static com.google.common.base.Preconditions.checkNotNull;

import com.b2international.collections.longs.LongIterator;
import com.b2international.collections.longs.LongSet;
import com.b2international.commons.Change;
import com.b2international.snowowl.index.diff.IndexDiff;
import com.b2international.snowowl.index.diff.IndexDiffMerger;
import com.b2international.snowowl.index.diff.ThreeWayIndexDiff;

/**
 * Three way index difference implementation.
 *
 */
public class ThreeWayIndexDiffImpl implements ThreeWayIndexDiff {

	private static final long serialVersionUID = -4061684045842035330L;

	private final IndexDiff sourceDiff;
	private final IndexDiff targetDiff;
	private final IndexDiff mergedDiff;

	public ThreeWayIndexDiffImpl(final IndexDiff sourceDiff, final IndexDiff targetDiff) {
		this.sourceDiff = checkNotNull(sourceDiff, "sourceDiff");
		this.targetDiff = checkNotNull(targetDiff, "targetDiff");
		this.mergedDiff = IndexDiffMerger.merge(this.sourceDiff, this.targetDiff);
	}

	@Override
	public LongSet getNewIds() {
		return mergedDiff.getNewIds();
	}

	@Override
	public LongSet getChangedIds() {
		return mergedDiff.getChangedIds();
	}

	@Override
	public LongSet getDetachedIds() {
		return mergedDiff.getDetachedIds();
	}

	@Override
	public LongIterator iterator() {
		return mergedDiff.iterator();
	}

	@Override
	public boolean contains(final long key) {
		return mergedDiff.contains(key);
	}

	@Override
	public IndexDiff getSourceDiff() {
		return sourceDiff;
	}

	@Override
	public IndexDiff getTargetDiff() {
		return targetDiff;
	}
	
	@Override
	public Change getChange(final long key) {
		return mergedDiff.getChange(key);
	}

}