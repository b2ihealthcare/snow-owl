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

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.After;
import org.junit.Before;

import com.b2international.index.DefaultIndex;
import com.b2international.index.IndexClient;
import com.b2international.index.Indexes;
import com.b2international.index.mapping.Mappings;
import com.b2international.index.query.Query;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @since 4.7
 */
public abstract class BaseRevisionIndexTest {
	
	private Mappings mappings;
	private RevisionIndex index;
	private Map<String, RevisionBranch> branches = newHashMap();
	private AtomicLong clock = new AtomicLong(0L);
	private RevisionBranchProvider branchProvider = new RevisionBranchProvider() {
		@Override
		public RevisionBranch getBranch(String branchPath) {
			return branches.get(branchPath);
		}
	};
	
	@Before
	public void setup() {
		// initialize MAIN branch with 0,0 timestamps
		branches.put(RevisionBranch.MAIN_PATH, new RevisionBranch(null, RevisionBranch.MAIN_PATH, 0L, 0L));
		
		final ObjectMapper mapper = new ObjectMapper();
		mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
		mappings = new Mappings(getTypes());
		index = new DefaultRevisionIndex(new DefaultIndex(createIndexClient(mapper, mappings)), branchProvider);
		index.admin().create();
	}

	@After
	public void teardown() {
		index.admin().delete();
	}
	
	protected final void commitBranch(final String branchPath, final long commitTimestamp) {
		final RevisionBranch branch = branches.get(branchPath);
		branches.put(branchPath, new RevisionBranch(branch.parent(), branch.path(), branch.baseTimestamp(), commitTimestamp));
	}
	
	protected final long nextCommitTimestamp() {
		return clock.incrementAndGet();
	}

	protected final RevisionIndex index() {
		return index;
	}
	
	/**
	 * Returns the document types used by this test case.
	 * @return
	 */
	protected abstract Collection<Class<?>> getTypes();
	
	private final IndexClient createIndexClient(ObjectMapper mapper, Mappings mappings) {
		return Indexes.createIndexClient(UUID.randomUUID().toString(), mapper, mappings);
	}
	
	protected final <T extends Revision> T getDocument(final String branch, final Class<T> type, final long storageKey) {
		return index().read(branch, new RevisionIndexRead<T>() {
			@Override
			public T execute(RevisionSearcher index) throws IOException {
				return index.get(type, storageKey);
			}
		});
	}
	
	protected final void indexRevision(final String branchPath, final long storageKey, final Revision data) {
		final long commitTimestamp = nextCommitTimestamp();
		index().write(branchPath, commitTimestamp, new RevisionIndexWrite<Void>() {
			@Override
			public Void execute(RevisionWriter index) throws IOException {
				index.put(storageKey, data);
				index.commit();
				commitBranch(branchPath, commitTimestamp);
				return null;
			}
		});
	}
	
	protected final void deleteRevision(final String branchPath, final Class<? extends Revision> type, final long storageKey) {
		final long commitTimestamp = nextCommitTimestamp();
		index().write(branchPath, commitTimestamp, new RevisionIndexWrite<Void>() {
			@Override
			public Void execute(RevisionWriter index) throws IOException {
				index.remove(type, storageKey);
				index.commit();
				commitBranch(branchPath, commitTimestamp);
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
	
}
