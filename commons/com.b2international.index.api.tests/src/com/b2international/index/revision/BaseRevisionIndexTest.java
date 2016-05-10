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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.After;
import org.junit.Before;

import com.b2international.index.DefaultIndex;
import com.b2international.index.IndexClient;
import com.b2international.index.revision.RevisionFixtures.Data;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @since 4.7
 */
public abstract class BaseRevisionIndexTest {
	
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
		index = new DefaultRevisionIndex(new DefaultIndex(createIndexClient(mapper)), branchProvider);
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
	
	protected abstract IndexClient createIndexClient(ObjectMapper mapper);
	
	protected final void indexRevision(final long storageKey, final Data data) {
		final String branchPath = RevisionBranch.MAIN_PATH;
		final long commitTimestamp = clock.incrementAndGet();
		index().write(branchPath, commitTimestamp, new RevisionIndexWrite<Void>() {
			@Override
			public Void execute(RevisionWriter index) throws IOException {
				index.put(storageKey, data);
				index.commit();
				commitBranch(branchPath, commitTimestamp);
				return null;
			}
		});
		
		final Data actual = index().read(branchPath, new RevisionIndexRead<Data>() {
			@Override
			public Data execute(RevisionSearcher index) throws IOException {
				return index.get(Data.class, storageKey);
			}
		});
		
		assertEquals(data, actual);
	}
	
	protected final void deleteRevision(final long storageKey) {
		final String branchPath = RevisionBranch.MAIN_PATH;
		final long commitTimestamp = clock.incrementAndGet();
		index.write(branchPath, commitTimestamp, new RevisionIndexWrite<Void>() {
			@Override
			public Void execute(RevisionWriter index) throws IOException {
				index.remove(Data.class, storageKey);
				index.commit();
				commitBranch(branchPath, commitTimestamp);
				return null;
			}
		});
		
		final Data revision = index.read(RevisionBranch.MAIN_PATH, new RevisionIndexRead<Data>() {
			@Override
			public Data execute(RevisionSearcher index) throws IOException {
				return index.get(Data.class, storageKey);
			}
		});
		
		assertNull(revision);		
	}
	
}
