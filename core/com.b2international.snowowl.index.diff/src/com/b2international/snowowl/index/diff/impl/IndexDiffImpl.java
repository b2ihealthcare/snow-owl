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

import static com.b2international.commons.collect.LongSets.in;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;

import com.b2international.collections.longs.LongCollection;
import com.b2international.collections.longs.LongCollections;
import com.b2international.collections.longs.LongIterator;
import com.b2international.collections.longs.LongSet;
import com.b2international.commons.Change;
import com.b2international.commons.ChangeKind;
import com.b2international.commons.collect.LongSets;
import com.b2international.commons.collect.LongSets.LongPredicate;
import com.b2international.snowowl.index.diff.IndexDiff;
import com.google.common.base.Objects;

/**
 * Represents an {@link IndexDiff index difference}.
 *
 */
public class IndexDiffImpl implements IndexDiff, Serializable {

	private static final long serialVersionUID = 4897399764715923530L;

	private final LongSet newIds;
	private final LongSet changedIds;
	private final LongSet detachedIds;
	private final LongPredicate predicate;
	
	public IndexDiffImpl(final LongSet newIds, final LongSet changedIds, final LongSet detachedIds) {
		this.newIds = checkNotNull(newIds, "newIds");
		this.changedIds = checkNotNull(changedIds, "changedIds");
		this.detachedIds = checkNotNull(detachedIds, "detachedIds");
		predicate = in(this.newIds, this.changedIds, this.detachedIds);
	}

	@Override
	public LongSet getNewIds() {
		return LongCollections.unmodifiableSet(newIds);
	}

	@Override
	public LongSet getChangedIds() {
		return LongCollections.unmodifiableSet(changedIds);
	}

	@Override
	public LongSet getDetachedIds() {
		return LongCollections.unmodifiableSet(detachedIds);
	}
	
	@Override
	public LongIterator iterator() {
		return LongSets.iterator(newIds, changedIds, detachedIds);
	}

	@Override
	public boolean contains(final long key) {
		return predicate.apply(key);
	}
	
	@Override
	public Change getChange(final long key) {
		if (newIds.contains(key)) {
			return ChangeKind.ADDED;
		} else if (changedIds.contains(key)) {
			return ChangeKind.UPDATED;
		} else if (detachedIds.contains(key)) {
			return ChangeKind.DELETED;
		}
		throw new IllegalArgumentException("Cannot find entry with key: " + key); 
	}
	
	@Override
	public String toString() {
		return Objects.toStringHelper(this)
				.add("\nNew IDs [count: " + newIds.size() + "]:", toString(newIds))
				.add("\nChanged IDs [count: " + changedIds.size() + "]:", toString(changedIds))
				.add("\nDetached IDs [count: " + detachedIds.size() + "]:", toString(detachedIds))
				.toString();
	}
	
	private String toString(final LongCollection collection) {
		final StringBuilder sb = new StringBuilder();
		for (final LongIterator itr = collection.iterator(); itr.hasNext(); /**/) {
			sb.append(0 < sb.length() ? ",\n" : "\n");
			sb.append("\t");
			sb.append(itr.next());
		}
		if (sb.length() > 0) {
			sb.append("\n");
		}
		return sb.toString();
	}

}