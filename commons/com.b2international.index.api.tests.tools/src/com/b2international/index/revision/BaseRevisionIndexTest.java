/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.After;
import org.junit.Before;

import com.b2international.index.DefaultIndex;
import com.b2international.index.Index;
import com.b2international.index.IndexClient;
import com.b2international.index.IndexRead;
import com.b2international.index.Indexes;
import com.b2international.index.Searcher;
import com.b2international.index.mapping.DocumentMapping;
import com.b2international.index.mapping.Mappings;
import com.b2international.index.query.Query;
import com.b2international.index.util.Reflections;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Ordering;

/**
 * @since 4.7
 */
public abstract class BaseRevisionIndexTest {
	
	protected static final String MAIN = RevisionBranch.MAIN_PATH;
	protected static final long STORAGE_KEY1 = 1L;
	protected static final long STORAGE_KEY2 = 2L;
	
	// XXX start from 3 to take the two constant values above into account
	private AtomicLong storageKeys = new AtomicLong(3);
	private ObjectMapper mapper;
	private Mappings mappings;
	private Index rawIndex;
	private RevisionIndex index;
	private Map<String, RevisionBranch> branches = newHashMap();
	private AtomicLong clock = new AtomicLong(0L);
	private AtomicInteger segmentIds = new AtomicInteger(0);
	private RevisionBranchProvider branchProvider = new RevisionBranchProvider() {
		@Override
		public RevisionBranch getBranch(String branchPath) {
			return branches.get(branchPath);
		}
		
		@Override
		public RevisionBranch getParentBranch(String branchPath) {
			final RevisionBranch branch = branches.get(branchPath);
			final Set<Integer> segments = newHashSet(branch.segments());
			segments.remove(branch.segmentId());
			return new RevisionBranch(branchPath.substring(0, branchPath.lastIndexOf(RevisionBranch.SEPARATOR)), Ordering.natural().max(segments), segments);
		}
	};
	
	private int nextSegmentId() {
		return segmentIds.getAndIncrement();
	}
	
	protected final long nextStorageKey() {
		return storageKeys.getAndIncrement();
	}
	
	@Before
	public void setup() {
		// initially the MAIN is only one segment long
		final Set<Integer> segments = newHashSet();
		final int initialSegment = nextSegmentId();
		segments.add(initialSegment);
		branches.put(MAIN, new RevisionBranch(MAIN, initialSegment, segments));
		
		mapper = new ObjectMapper();
		mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
		configureMapper(mapper);
		mappings = new Mappings(getTypes());
		rawIndex = new DefaultIndex(createIndexClient(mapper, mappings));
		index = new DefaultRevisionIndex(rawIndex, branchProvider);
		index.admin().create();
	}

	@After
	public void teardown() {
		if (index != null) {
			index.admin().delete();
		}
	}
	
	protected final ObjectMapper getMapper() {
		return mapper;
	}
	
	protected void configureMapper(ObjectMapper mapper) {
	}
	
	protected String createBranch(String parent, String child) {
		RevisionBranch parentBranch = branches.get(parent);
		if (parentBranch == null) {
			throw new IllegalArgumentException("Parent could not be found at path: " + parent);
		}
		final String path = String.format("%s/%s", parent, child);
		// register child with new segment ID
		final int initialSegment = nextSegmentId();
		final Set<Integer> segments = newHashSet();
		// add parent segments
		segments.add(initialSegment);
		segments.addAll(parentBranch.segments());
		branches.put(path, new RevisionBranch(path, initialSegment, segments));
		// reregister parent branch with updated segment information
		final int newParentSegment = nextSegmentId();
		final Set<Integer> newParentSegments = newHashSet();
		newParentSegments.add(newParentSegment);
		newParentSegments.addAll(parentBranch.segments());
		branches.put(parent, new RevisionBranch(parent, newParentSegment, newParentSegments));
		return path;
	}
	
	protected final long currentTime() {
		return clock.incrementAndGet();
	}

	protected final RevisionIndex index() {
		return index;
	}
	
	protected final Index rawIndex() {
		return rawIndex;
	}
	
	/**
	 * Returns the document types used by this test case.
	 * @return
	 */
	protected abstract Collection<Class<?>> getTypes();
	
	private final IndexClient createIndexClient(ObjectMapper mapper, Mappings mappings) {
		return Indexes.createIndexClient(UUID.randomUUID().toString(), mapper, mappings);
	}
	
	protected final <T extends Revision> T getRevision(final String branch, final Class<T> type, final long storageKey) {
		return index().read(branch, new RevisionIndexRead<T>() {
			@Override
			public T execute(RevisionSearcher index) throws IOException {
				return index.get(type, storageKey);
			}
		});
	}
	
	protected final void indexRevision(final String branchPath, final long storageKey, final Revision data) {
		final long commitTimestamp = currentTime();
		index().write(branchPath, commitTimestamp, new RevisionIndexWrite<Void>() {
			@Override
			public Void execute(RevisionWriter index) throws IOException {
				index.put(storageKey, data);
				index.commit();
				return null;
			}
		});
	}
	
	protected final void deleteRevision(final String branchPath, final Class<? extends Revision> type, final long storageKey) {
		final long commitTimestamp = currentTime();
		index().write(branchPath, commitTimestamp, new RevisionIndexWrite<Void>() {
			@Override
			public Void execute(RevisionWriter index) throws IOException {
				index.remove(type, storageKey);
				index.commit();
				return null;
			}
		});
	}
	
	protected final <T> Iterable<T> search(final String branchPath, final Query<T> query) {
		return index().read(branchPath, new RevisionIndexRead<Iterable<T>>() {
			@Override
			public Iterable<T> execute(RevisionSearcher index) throws IOException {
				return index.search(query);
			}
		});
	}
	
	protected final <T> Iterable<T> searchRaw(final Query<T> query) {
		return rawIndex().read(new IndexRead<Iterable<T>>() {
			@Override
			public Iterable<T> execute(Searcher index) throws IOException {
				return index.search(query);
			}
		});
	}
	
	protected void assertDocEquals(Object expected, Object actual) {
		assertNotNull("Actual document is missing from index", actual);
		for (Field f : mappings.getMapping(expected.getClass()).getFields()) {
			if (Revision.REPLACED_INS.equals(f.getName()) 
					|| Revision.SEGMENT_ID.equals(f.getName())
					|| Revision.COMMIT_TIMESTAMP.equals(f.getName()) 
					|| Revision.BRANCH_PATH.equals(f.getName()) 
					|| Revision.STORAGE_KEY.equals(f.getName()) 
					|| DocumentMapping._ID.equals(f.getName())) {
				// skip revision fields from equality check
				continue;
			}
			assertEquals(String.format("Field '%s' should be equal", f.getName()), Reflections.getValue(expected, f), Reflections.getValue(actual, f));
		}
	}
	
}
