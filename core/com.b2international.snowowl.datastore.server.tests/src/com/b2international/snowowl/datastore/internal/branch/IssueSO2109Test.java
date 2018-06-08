/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore.internal.branch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.RETURNS_DEFAULTS;
import static org.mockito.Mockito.RETURNS_MOCKS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.util.CDOTimeProvider;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranch;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.b2international.commons.options.MetadataImpl;
import com.b2international.index.Doc;
import com.b2international.index.Index;
import com.b2international.index.Indexes;
import com.b2international.index.mapping.Mappings;
import com.b2international.index.revision.DefaultRevisionIndex;
import com.b2international.index.revision.Revision;
import com.b2international.index.revision.RevisionBranch;
import com.b2international.index.revision.RevisionBranchPoint;
import com.b2international.index.revision.RevisionIndex;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.datastore.cdo.ICDOConflictProcessor;
import com.b2international.snowowl.datastore.internal.InternalRepository;
import com.b2international.snowowl.datastore.server.internal.JsonSupport;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.util.Providers;

/**
 * @since 5.4
 */
public class IssueSO2109Test {

	private static final String STORAGE_KEY = "1";
	private static final String DATA_VALUE = "1";
	private static final String INTERRUPT_MESSAGE = "Interrupting rebase";

	private CDOTimeProvider clock;
	private MockInternalCDOBranchManager cdoBranchManager;

	private CDOBranchManagerImpl manager;

	private InternalRepository repository;
	private RevisionIndex store;

	@Before
	public void givenCDOBranchManager() {
		clock = new AtomicLongTimestampAuthority();
		cdoBranchManager = new MockInternalCDOBranchManager(clock);
		cdoBranchManager.initMainBranch(false, clock.getTimeStamp());

		repository = mock(InternalRepository.class, RETURNS_MOCKS);
		final ICDOConflictProcessor conflictProcessor = mock(ICDOConflictProcessor.class, RETURNS_DEFAULTS);
		final InternalCDOBranch mainBranch = cdoBranchManager.getMainBranch();

		when(repository.getCdoBranchManager()).thenReturn(cdoBranchManager);
		when(repository.getCdoMainBranch()).thenReturn(mainBranch);
		when(repository.getConflictProcessor()).thenReturn(conflictProcessor);
		final ObjectMapper mapper = JsonSupport.getDefaultObjectMapper();
		Index rawIndex = Indexes.createIndex(UUID.randomUUID().toString(), mapper, new Mappings(Data.class));

		when(repository.provider(Mockito.eq(Index.class))).thenReturn(Providers.of(rawIndex));

		manager = new CDOBranchManagerImpl(repository, mapper);
		
		store = new DefaultRevisionIndex(rawIndex, manager, mapper);
		store.admin().create();
	}

	@After
	public void after() {
		store.admin().delete();
	}

	@Test
	public void branchContentMustNotBeNullAfterFailedRebase() throws Exception {

		final String childBranch = manager.createBranch(Branch.MAIN_PATH, "a", new MetadataImpl());

		final long timestamp = clock.getTimeStamp();

		store.prepareCommit(childBranch)
			.stageNew(new Data(STORAGE_KEY, DATA_VALUE))
			.commit(timestamp, UUID.randomUUID().toString(), "Commit");

		manager.handleCommit(childBranch, timestamp);

		final RevisionBranch child = manager.getBranch(childBranch);
		final Data actual = getData(childBranch, STORAGE_KEY);

		assertEquals(new Data(DATA_VALUE, STORAGE_KEY, childBranch, timestamp, new RevisionBranchPoint(child.getId(), timestamp),
				Collections.emptyList()), actual);

		try {
			manager.rebase(childBranch, child.getParentPath(), "commit message", new Runnable() {
				@Override
				public void run() {
					throw new RuntimeException(INTERRUPT_MESSAGE);
				}
			});
		} catch (final RuntimeException e) {
			if (!e.getMessage().equals(INTERRUPT_MESSAGE)) {
				throw e;
			}
		}

		final Data dataAfterRebase = getData(childBranch, STORAGE_KEY);

		assertNotNull("Data most not be null after failed rebase attempt", dataAfterRebase);

		final RevisionBranch currentChild = manager.getBranch(childBranch);

		final CDOBranch currentCDOChild = manager.getCDOBranch(currentChild);

		assertEquals(currentCDOChild.getPathName(), childBranch);
		
		for (RevisionBranch parentChild : manager.getChildren(currentChild.getParentPath())) {
			assertFalse(parentChild.getName().startsWith(RevisionBranch.TEMP_PREFIX));
		}

	}

	private Data getData(final String path, final String storageKey) {
		return store.read(path, index -> index.get(Data.class, storageKey));
	}

	@Doc
	private static class Data extends Revision {

		@JsonProperty
		private final String field;

		public Data(final String id, final String field) {
			super(id);
			this.field = field;
		}

		@JsonCreator
		public Data(
				@JsonProperty("id") final String id,
				@JsonProperty("field") final String field, 
				@JsonProperty("branchPath") final String branchPath, 
				@JsonProperty("commitTimestamp") final long commitTimestamp,
				@JsonProperty("created") final RevisionBranchPoint created, 
				@JsonProperty("revised") final List<RevisionBranchPoint> revised) {
			super(id);
			this.field = field;
			setCreated(created);
			setRevised(revised);
		}

		@Override
		public int hashCode() {
			return Objects.hash(field);
		}

		@Override
		public boolean equals(final Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			final Data other = (Data) obj;
			return Objects.equals(other.field, field);
		}

	}
}
