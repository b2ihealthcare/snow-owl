/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.index.revision;

import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

import com.b2international.index.BulkUpdate;
import com.b2international.index.Hits;
import com.b2international.index.Index;
import com.b2international.index.IndexWrite;
import com.b2international.index.mapping.DocumentMapping;
import com.b2international.index.query.Query;
import com.google.common.collect.ImmutableMap;

/**
 * @since 6.5
 */
public final class RevisionBranches {

	private final Index index;
	private final AtomicInteger segmentIds = new AtomicInteger(0);

	public RevisionBranches(Index index) {
		this.index = index;
	}
	
	/**
	 * Must be called after index initialization.
	 */
	public void init() {
		RevisionBranch mainBranch = getBranch(RevisionBranch.MAIN_PATH);
		if (mainBranch == null) {
			int segmentId = segmentIds.getAndIncrement();
			commit(writer -> {
				// register child with new segment ID
				writer.put(RevisionBranch.MAIN_PATH, RevisionBranch.builder()
						.type("MainBranchImpl")
						.name(RevisionBranch.MAIN_PATH)
						.path(RevisionBranch.MAIN_PATH)
						.deleted(false)
						.parentSegments(Collections.emptySet())
						.segments(Collections.singleton(segmentId))
						.segmentId(segmentId)
						.build());
				writer.commit();
				return null;
			});
		}
	}
	
	/**
	 * Returns the revision branch for the given branchPath.
	 * 
	 * @param branchPath
	 * @return
	 * @since 6.5
	 */
	public RevisionBranch getBranch(String branchPath) {
		return index.read(searcher -> searcher.get(RevisionBranch.class, branchPath));
	}
	
	/**
	 * Search branches with the given query and return the results.
	 * @param query
	 * @return
	 */
	public Hits<RevisionBranch> getBranches(Query<RevisionBranch> query) {
		return index.read(searcher -> searcher.search(query));
	}
	
	public <T> T commit(IndexWrite<T> changes) {
		return index.write(writer -> {
			T result = changes.execute(writer);
			writer.commit();
			return result;
		});
	}
	
	public String create(String parent, String child) {
		RevisionBranch parentBranch = getBranch(parent);
		if (parentBranch == null) {
			throw new IllegalArgumentException("Parent could not be found at path: " + parent);
		}
		final String path = String.format("%s/%s", parent, child);
		
		commit(writer -> {
			// register child with new segment ID
			final int nextChildSegment = nextSegmentId();
			final int nextParentSegment = nextSegmentId();
			writer.put(path, RevisionBranch.builder()
					.type("BranchImpl")
					.parentPath(parent)
					.name(child)
					.path(path)
					.deleted(false)
					.parentSegments(parentBranch.getRevisionBranchSegments().segments())
					.segments(Collections.singleton(nextChildSegment))
					.segmentId(nextChildSegment)
					.build());
			
			// reregister parent branch with updated segment information
			writer.bulkUpdate(new BulkUpdate<>(RevisionBranch.class, DocumentMapping.matchId(parent), DocumentMapping._ID, RevisionBranch.Scripts.WITH_SEGMENTID, ImmutableMap.of("segmentId", nextParentSegment)));
			writer.commit();
			return null;
		});
		
		return path;
	}
	
	private int nextSegmentId() {
		return segmentIds.getAndIncrement();
	}

}
