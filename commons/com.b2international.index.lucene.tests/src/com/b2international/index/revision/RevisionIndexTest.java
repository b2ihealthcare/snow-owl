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
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.b2international.index.DefaultIndex;
import com.b2international.index.FSIndexAdmin;
import com.b2international.index.IndexClient;
import com.b2international.index.LuceneIndexClient;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Query;
import com.b2international.index.revision.RevisionFixtures.Data;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @since 4.7
 */
public class RevisionIndexTest {

	protected static final long STORAGE_KEY1 = 1L;
	protected static final long STORAGE_KEY2 = 2L;

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();
	
	private IndexClient client;
	private RevisionIndex index;
	private Map<String, RevisionBranch> branches = newHashMap();
	private AtomicLong clock = new AtomicLong(0L);

	@Before
	public void givenClient() {
		// initialize MAIN branch with 0,0 timestamps
		branches.put(RevisionBranch.MAIN_PATH, new RevisionBranch(null, RevisionBranch.MAIN_PATH, 0L, 0L));
		
		final ObjectMapper mapper = new ObjectMapper();
		mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
		client = new LuceneIndexClient(new FSIndexAdmin(folder.getRoot(), UUID.randomUUID().toString()), mapper);
		index = new DefaultRevisionIndex(new DefaultIndex(client), new RevisionBranchProvider() {
			@Override
			public RevisionBranch getBranch(String branchPath) {
				return branches.get(branchPath);
			}
		});
		client.admin().create();
	}
	
	@After
	public void after() {
		client.close();
	}
	
	@Test
	public void searchEmptyIndexShouldReturnNullRevision() throws Exception {
		final Data revision = index.read(RevisionBranch.MAIN_PATH, new RevisionIndexRead<Data>() {
			@Override
			public Data execute(RevisionSearcher index) throws IOException {
				return index.get(Data.class, STORAGE_KEY1);
			}
		});
		
		assertNull(revision);
	}
	
	@Test
	public void indexRevision() throws Exception {
		indexRevision(STORAGE_KEY1, new Data("field1", "field2"));
	}

	@Test
	public void indexTwoRevisions() throws Exception {
		indexRevision();
		indexRevision(STORAGE_KEY1, new Data("field1Changed", "field2Changed"));
	}

	@Test
	public void deleteRevision() throws Exception {
		indexRevision();
		deleteRevision(STORAGE_KEY1);
	}
	
	@Test
	public void updateThenDeleteRevision() throws Exception {
		indexTwoRevisions();
		deleteRevision(STORAGE_KEY1);
	}
	
	@Test
	public void searchDifferentRevisions() throws Exception {
		final Data first = new Data("field1", "field2");
		final Data second = new Data("field1Changed", "field2");
		
		indexRevision(STORAGE_KEY1, first);
		indexRevision(STORAGE_KEY2, second);
		
		final Iterable<Data> matches = index.read(RevisionBranch.MAIN_PATH, new RevisionIndexRead<Iterable<Data>>() {
			@Override
			public Iterable<Data> execute(RevisionSearcher index) throws IOException {
				final Query<Data> query = Query.builder(Data.class).selectAll().where(Expressions.exactMatch("field1", "field1")).build();
				return index.search(query);
			}
		});
		assertThat(matches).hasSize(1);
		assertThat(matches).containsOnly(first);
	}

	@Test
	public void searchMultipleRevisions() throws Exception {
		final Data first = new Data("field1", "field2");
		final Data second = new Data("field1", "field2Changed");
		
		indexRevision(STORAGE_KEY1, first);
		indexRevision(STORAGE_KEY1, second);
		
		final Iterable<Data> matches = index.read(RevisionBranch.MAIN_PATH, new RevisionIndexRead<Iterable<Data>>() {
			@Override
			public Iterable<Data> execute(RevisionSearcher index) throws IOException {
				final Query<Data> query = Query.builder(Data.class).selectAll().where(Expressions.exactMatch("field1", "field1")).build();
				return index.search(query);
			}
		});
		// only second version should match, the first revision should be unaccessible without timestamp
		assertThat(matches).hasSize(1);
		assertThat(matches).containsOnly(second);
	}
	private void commitBranch(String branchPath, long commitTimestamp) {
		final RevisionBranch branch = branches.get(branchPath);
		branches.put(branchPath, new RevisionBranch(branch.parent(), branch.path(), branch.baseTimestamp(), commitTimestamp));
	}
	
	private void indexRevision(final long storageKey, final Data data) {
		final String branchPath = RevisionBranch.MAIN_PATH;
		final long commitTimestamp = clock.incrementAndGet();
		index.write(branchPath, commitTimestamp, new RevisionIndexWrite<Void>() {
			@Override
			public Void execute(RevisionWriter index) throws IOException {
				index.put(storageKey, data);
				index.commit();
				commitBranch(branchPath, commitTimestamp);
				return null;
			}
		});
		
		final Data actual = index.read(branchPath, new RevisionIndexRead<Data>() {
			@Override
			public Data execute(RevisionSearcher index) throws IOException {
				return index.get(Data.class, storageKey);
			}
		});
		
		assertEquals(data, actual);
	}
	
	private void deleteRevision(final long storageKey) {
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
