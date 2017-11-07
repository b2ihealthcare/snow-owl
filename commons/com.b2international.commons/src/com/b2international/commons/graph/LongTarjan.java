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
package com.b2international.commons.graph;

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;
import java.util.function.LongFunction;

import com.b2international.collections.PrimitiveLists;
import com.b2international.collections.PrimitiveMaps;
import com.b2international.collections.PrimitiveSets;
import com.b2international.collections.longs.LongCollection;
import com.b2international.collections.longs.LongIterator;
import com.b2international.collections.longs.LongKeyIntMap;
import com.b2international.collections.longs.LongList;
import com.b2international.collections.longs.LongSet;

/**
 * Tarjan's algorithm that works with primitive longs for performance.
 * <p>
 * Estimated memory cost is ~14MB for 350,000 long items.
 */
public final class LongTarjan {

	private final int batchSize;
	private final LongFunction<LongCollection> getFollowers;

	private final LongList idStack = PrimitiveLists.newLongArrayList();
	private final LongKeyIntMap indexMap = PrimitiveMaps.newLongKeyIntOpenHashMap();
	private final LongKeyIntMap lowLinkMap = PrimitiveMaps.newLongKeyIntOpenHashMap();
	private final List<LongSet> components = newArrayList();

	private int index;
	private LongSet current;

	public LongTarjan(final int batchSize, final LongFunction<LongCollection> getFollowers) {
		this.batchSize = batchSize;
		this.getFollowers = getFollowers;
	}

	public List<LongSet> run(final LongCollection ids) {

		for (final LongIterator itr = ids.iterator(); itr.hasNext(); /* empty */) {
			final long currentId = itr.next();
			indexMap.put(currentId, -1);
		}

		for (final LongIterator itr = ids.iterator(); itr.hasNext(); /* empty */) {
			final long currentId = itr.next();
			if (indexMap.get(currentId) == -1) {
				visit(currentId, ids);
			}
		}

		if (currentSize() > 0) {
			flushBatch();
		}

		return components;
	}

	private void visit(final long currentId, final LongCollection ids) {

		indexMap.put(currentId, index);
		lowLinkMap.put(currentId, index);
		index++;

		idStack.add(currentId);

		final LongCollection followers = getFollowers.apply(currentId);
		for (final LongIterator itr = followers.iterator(); itr.hasNext(); /* empty */) {
			final long currentFollowerId = itr.next();

			if (!indexMap.containsKey(currentFollowerId)) {
				continue;
			}

			if (indexMap.get(currentFollowerId) == -1) {
				visit(currentFollowerId, ids);
				final int newLowLink = Math.min(lowLinkMap.get(currentId), lowLinkMap.get(currentFollowerId));
				lowLinkMap.put(currentId, newLowLink);
			} else if (idStack.contains(currentFollowerId)) {
				final int newLowLink = Math.min(lowLinkMap.get(currentId), indexMap.get(currentFollowerId));
				lowLinkMap.put(currentId, newLowLink);
			}
		}

		if (lowLinkMap.get(currentId) == indexMap.get(currentId)) {
			long sccMember = removeLast();

			if (currentId == sccMember) {
				addToCurrent(sccMember);

				if (currentSize() >= batchSize) {
					flushBatch();
				}

			} else {

				addToCurrent(sccMember);

				do {
					sccMember = removeLast();
					addToCurrent(sccMember);
				} while (currentId != sccMember);

				if (currentSize() >= batchSize) {
					flushBatch();
				}
			}
		}
	}
	
	private int currentSize() {
		if (current == null) return 0;
		return current.size();
	}

	private void addToCurrent(final long sccMember) {
		if (current == null) {
			current = PrimitiveSets.newLongOpenHashSet();
		}
		current.add(sccMember);
	}

	private long removeLast() {
		return idStack.removeLong(idStack.size() - 1);
	}

	private void flushBatch() {
		components.add(current);
		current = null;
	}

}
